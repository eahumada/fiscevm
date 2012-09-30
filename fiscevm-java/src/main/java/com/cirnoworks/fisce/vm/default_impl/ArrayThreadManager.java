/**
 *  Copyright 2010 Yuxuan Huang. All rights reserved.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.cirnoworks.fisce.vm.default_impl;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.cirnoworks.fisce.intf.IThread;
import com.cirnoworks.fisce.intf.VMCriticalException;
import com.cirnoworks.fisce.intf.VMException;
import com.cirnoworks.fisce.util.Base64;
import com.cirnoworks.fisce.util.DOMHelper;
import com.cirnoworks.fisce.vm.JHeap;
import com.cirnoworks.fisce.vm.JThreadManager;
import com.cirnoworks.fisce.vm.VMContext;
import com.cirnoworks.fisce.vm.data.ClassArray;
import com.cirnoworks.fisce.vm.data.ClassBase;
import com.cirnoworks.fisce.vm.data.ClassField;
import com.cirnoworks.fisce.vm.data.ClassMethod;

public class ArrayThreadManager implements Runnable, JThreadManager {
	// constant
	private static final int[] pricmds = new int[] { 1, 100, // 1
			200, // 2
			500, // 3
			700, // 4
			1000, // 5
			1400, // 6
			1800, // 7
			2500, // 8
			3500, // 9
			5000, // 10
	};

	// *********************
	// persist
	private final ArrayList<ArrayThread> runningThreads = new ArrayList<ArrayThread>();

	// thread id --> monitor id waiting for monitorentry
	private int[] waitForLockId = new int[MAX_THREADS];
	// thread id --> monitor id waiting for notify
	private int[] waitForNotifyId = new int[MAX_THREADS];
	// thread id --> how much time the thread orignally had
	private int[] pendingLockCount = new int[MAX_THREADS];
	// thread id --> next wakeup time
	private long[] nextWakeUpTime = new long[MAX_THREADS];
	// thread id --> interrupted
	private boolean[] interrupted = new boolean[MAX_THREADS];
	// thread id --> destroyPending
	private boolean[] destroyPending = new boolean[MAX_THREADS];
	// thread id --> daemon
	private boolean[] daemon = new boolean[MAX_THREADS];
	// handle --> monitor owner id
	private int[] monitorOwnerId = new int[JHeap.MAX_OBJECTS];
	// handle --> monitor owner times
	private int[] monitorOwnerTimes = new int[JHeap.MAX_OBJECTS];
	//
	// **************************
	// no persist but need resume
	// threadId --> is used
	private boolean[] threadIds = new boolean[MAX_THREADS];
	// **************************
	// no persist
	private VMContext context;
	private JHeap heap;
	private int state;
	private final Object threadsLock = new Object();
	private final Object stateLock = new Object();
	private Thread workingThread;
	private final ArrayList<ArrayThread> pendingThreads = new ArrayList<ArrayThread>();
	private long nextWakeUpTimeTotal;
	private int nextThreadId;
	private Throwable exitException;
	private int exitCode;

	public void monitorEnter(ArrayThread dt, int monitorId) {
		monitorEnter(dt, monitorId, 1);
	}

	public void monitorExit(ArrayThread dt, int monitorId) throws VMException,
			VMCriticalException {
		monitorExit(dt, monitorId, 1);
	}

	public void sleep(IThread dt, long time) {
		int threadId = ((ArrayThread) dt).getThreadId();
		nextWakeUpTime[threadId] = System.currentTimeMillis() + time;
		((ArrayThread) dt).setYield(true);
	}

	public void interrupt(int targetHandle) throws VMException,
			VMCriticalException {
		ArrayThread target = getThread(targetHandle);
		if (target == null) {
			return;
		}
		int threadId = target.getThreadId();
		// System.out
		// .println("nwut[" + threadId + "]=" + nextWakeUpTime[threadId]);
		if (nextWakeUpTime[threadId] > 0) {
			assert target.getCurrentThrowable() == 0;
			int exceptionHandle = context.getThrower().prepareThrowable(
					new VMException("java/lang/InterruptedException", ""),
					target);
			target.setCurrentThrowable(exceptionHandle);
			nextWakeUpTime[threadId] = 0;
			interrupted[threadId] = true;
		}
	}

	public boolean isInterrupted(int targetHandle, boolean clear) {
		ArrayThread target = getThread(targetHandle);
		if (target == null) {
			return false;
		}
		boolean ret = interrupted[target.getThreadId()];
		if (clear) {
			interrupted[target.getThreadId()] = false;
		}
		return ret;
	}

	public void wait(IThread thread, int monitorId, long time)
			throws VMException, VMCriticalException {
		ArrayThread dt = (ArrayThread) thread;
		int tid = dt.getThreadId();
		assert waitForNotifyId[tid] == 0;
		if (monitorOwnerId[monitorId] != tid) {
			throw new VMException("java/lang/IllegalMonitorStateException", "");
		}
		waitForNotifyId[tid] = monitorId;
		pendingLockCount[tid] = releaseMonitor(dt, monitorId);
		// System.out.println("*****" + tid + " " + pendingLockCount[tid]);
		if (time <= 0) {
			// wait till the end of the time ;)
			nextWakeUpTime[tid] = Long.MAX_VALUE;
		} else {
			nextWakeUpTime[tid] = System.currentTimeMillis() + time;
		}
		((ArrayThread) thread).setYield(true);
	}

	public void notify(IThread thread, int monitorId, boolean all)
			throws VMException {
		ArrayThread dt = (ArrayThread) thread;
		int tid = dt.getThreadId();
		if (monitorOwnerId[monitorId] != tid) {
			throw new VMException("java/lang/IllegalMonitorStateException", "");
		}
		for (int i = 0; i < MAX_THREADS; i++) {
			if (waitForNotifyId[i] == monitorId) {
				waitForNotifyId[i] = 0;
				assert waitForLockId[i] == 0;
				waitForLockId[i] = monitorId;
				nextWakeUpTime[i] = 0;
				if (!all) {
					// only notify one
					break;
				}
			}
		}
	}

	public boolean isAlive(int threadHandle) {
		ArrayThread t = getThread(threadHandle);
		return t != null;
	}

	public void destroyThread(ArrayThread dt) {
		synchronized (threadsLock) {
			if (runningThreads.contains(dt)) {
				destroyPending[dt.getThreadId()] = true;
			}
		}
	}

	public ArrayThreadManager() {
		for (int i = 0; i < JHeap.MAX_OBJECTS; i++) {
			monitorOwnerId[i] = -1;
		}
	}

	public void setContext(VMContext context) {
		this.context = context;
		heap = (JHeap) context.getHeap();
	}

	private void monitorEnter(ArrayThread dt, int monitorId, int times) {
		int threadId = dt.getThreadId();
		int owner = monitorOwnerId[monitorId];
		if (owner == threadId) {
			monitorOwnerTimes[monitorId] += times;
		} else if (owner < 0) {
			monitorOwnerId[monitorId] = threadId;
			monitorOwnerTimes[monitorId] = times;
		} else {
			assert waitForLockId[threadId] == 0;
			waitForLockId[threadId] = monitorId;
			pendingLockCount[threadId] = 1;
			dt.setYield(true);
		}
	}

	private void monitorExit(ArrayThread dt, int monitorId, int times)
			throws VMException, VMCriticalException {
		int threadId = dt.getThreadId();
		if (monitorOwnerId[monitorId] != threadId) {
			throw new VMException("java/lang/IllegalMonitorStateException", "");
		}
		monitorOwnerTimes[monitorId] -= times;
		if (monitorOwnerTimes[monitorId] == 0) {
			monitorOwnerId[monitorId] = -1;
			dt.setYield(true);
		} else if (monitorOwnerTimes[monitorId] < 0) {
			context.getConsole().error("Too much monitors released!");
			throw new VMCriticalException(
					"java/lang/IllegalMonitorStateException");
		}
	}

	private int releaseMonitor(ArrayThread dt, int monitorId)
			throws VMException, VMCriticalException {
		int threadId = dt.getThreadId();
		if (monitorOwnerId[monitorId] != threadId) {
			throw new VMException("java/lang/IllegalMonitorStateException", "");
		}
		int times = monitorOwnerTimes[monitorId];
		monitorExit(dt, monitorId, times);
		return times;
	}

	private int fetchNextThreadId() throws VMException {
		int h = nextThreadId;
		while (threadIds[h]) {
			// used
			h++;
			if (h == MAX_THREADS) {
				h = 0;
			}
			if (h == nextThreadId) {
				throw new VMException("java/lang/VirtualMachineError",
						"Threads used up!");
			}
		}
		nextThreadId = (h + 1) % MAX_THREADS;
		return h;
	}

	private ArrayThread getThread(int threadHandle) {
		synchronized (threadsLock) {
			Iterator<ArrayThread> idt = runningThreads.iterator();
			while (idt.hasNext()) {
				ArrayThread dt = idt.next();
				if (threadHandle == dt.getThreadHandle()) {
					return dt;
				}
			}
			idt = pendingThreads.iterator();
			while (idt.hasNext()) {
				ArrayThread dt = idt.next();
				if (threadHandle == dt.getThreadHandle()) {
					return dt;
				}
			}
		}
		return null;
	}

	private void onThreadAdd(ArrayThread dt) {

	}

	private void onThreadRemove(ArrayThread dt) {
		int tid = dt.getThreadId();
		// release all locks
		waitForLockId[tid] = 0;
		waitForNotifyId[tid] = 0;
		nextWakeUpTime[tid] = 0;
		pendingLockCount[tid] = 0;
		threadIds[tid] = false;
		destroyPending[tid] = false;
		interrupted[tid] = false;
		for (int i = 0; i < JHeap.MAX_OBJECTS; i++) {
			if (monitorOwnerId[i] == tid) {
				monitorOwnerId[i] = -1;
				monitorOwnerTimes[i] = 0;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cirnoworks.fisce.vm.IThreadManager#bootFromMain(com.cirnoworks
	 * .fisce.vm.data.ClassBase)
	 */
	public void bootFromMain(ClassBase clazz) throws VMException,
			VMCriticalException {
		synchronized (stateLock) {
			if (state != STATE_NEW) {
				throw new IllegalStateException();
			}

			setState(STATE_BOOT_PENDING);
		}
		ClassMethod method = context.getMethod(clazz.getName()
				+ ".main.([Ljava/lang/String;)V");
		if (method == null) {
			throw new VMException("java/lang/NoSuchMethodError",
					clazz.getName() + ".main(String[] args)");
		}
		ArrayThread dt = new ArrayThread(context, this);
		ClassBase threadClass = (ClassBase) context
				.getClass("java/lang/Thread");
		if (threadClass == null) {
			throw new VMException("java/lang/ClassNotFoundException",
					"java/lang/Thread");
		}
		int threadHandle = heap.allocate(threadClass);
		ClassField nameField = context.getField("java/lang/Thread.name.[C");
		ClassField priorityField = context
				.getField("java/lang/Thread.priority.I");
		if (nameField == null || priorityField == null) {
			throw new VMException("java/lang/NoSuchFieldError",
					"Cannot found fields in Thread");
		}
		char[] name = "Thread-0".toCharArray();
		int nameHandle = heap.allocate((ClassArray) context.getClass("[C"),
				name.length);
		for (int i = 0, max = name.length; i < max; i++) {
			heap.putArrayChar(nameHandle, i, name[i]);
		}
		heap.putFieldHandle(threadHandle, nameField, nameHandle);
		heap.putFieldInt(threadHandle, priorityField, 5);
		dt.create(threadHandle, method);
		dt.setPriority(5);
		dt.setThreadId(fetchNextThreadId());
		onThreadAdd(dt);
		runningThreads.add(dt);
		synchronized (stateLock) {
			setState(STATE_STOP);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cirnoworks.fisce.vm.IThreadManager#pushThread(int)
	 */
	public void pushThread(int threadHandle) throws VMException,
			VMCriticalException {
		synchronized (stateLock) {
			if (state != STATE_RUNNING && state != STATE_STOP_PENDING) {
				throw new IllegalStateException();
			}
		}
		ArrayThread dt = new ArrayThread(context, this);
		ClassField priorityField = context
				.getField("java/lang/Thread.priority.I");
		int priority = heap.getFieldInt(threadHandle, priorityField);
		if (priority == 0) {
			priority = 5;
		}

		// so we need to set the priority here...
		dt.setPriority(priority);
		dt.create(threadHandle);
		dt.setThreadId(fetchNextThreadId());
		ClassField daemonField = context.getField("java/lang/Thread.daemon.Z");
		boolean daemon = heap.getFieldBoolean(threadHandle, daemonField);
		this.daemon[dt.getThreadId()] = daemon;
		onThreadAdd(dt);
		// System.out.println("XXXX+"+dt.getThreadId());
		pendingThreads.add(dt);
	}

	public void run() {
		synchronized (stateLock) {
			if (state != STATE_RUN_PENDING && state != STATE_STOP_PENDING) {
				throw new IllegalStateException("" + state);
			}
			setState(STATE_RUNNING);
		}
		long nextGC = 0;
		long nextGCForce = 0;
		Iterator<ArrayThread> idt = runningThreads.iterator();
		boolean run = false;
		int stateLocal;
		try {
			while (true) {
				synchronized (stateLock) {
					stateLocal = state;
				}
				switch (stateLocal) {
				case STATE_RUNNING:
					if (idt.hasNext()) {
						ArrayThread dt = idt.next();
						if (!daemon[dt.getThreadId()]) {
							run = true;
						}
						int threadId = dt.getThreadId();
						if (destroyPending[threadId]) {
							onThreadRemove(dt);
							idt.remove();

						}

						long nwut = nextWakeUpTime[threadId];
						if (nwut > System.currentTimeMillis()) {
							if (nextWakeUpTimeTotal > nwut) {
								nextWakeUpTimeTotal = nwut;
							}
							break;
						}
						nextWakeUpTime[threadId] = 0;
						nextWakeUpTimeTotal = 0;

						int lockId = waitForLockId[threadId];
						if (lockId > 0) {

							// waiting for lock
							if (monitorOwnerId[lockId] < 0) {
								// context.getConsole().info(
								// threadId + ">+++Exit lock " + lockId);
								// lock already released
								monitorOwnerId[lockId] = threadId;
								monitorOwnerTimes[lockId] = pendingLockCount[threadId];
								waitForLockId[threadId] = 0;
							} else {
								// still locked
								break;
							}
						}
						boolean dead = dt.run(pricmds[dt.getPriority()]);
						if (dead) {
							synchronized (runningThreads) {
								onThreadRemove(dt);
								idt.remove();
							}
						}
					} else {
						synchronized (threadsLock) {
							if (pendingThreads.size() > 0) {
								runningThreads.addAll(pendingThreads);
								pendingThreads.clear();
								nextWakeUpTimeTotal = 0;
								run = true;
							}
						}

						if (!run) {
							synchronized (stateLock) {
								setState(STATE_DEAD);
								workingThread = null;
								return;
							}
						}
						long now = System.currentTimeMillis();
						long sleepTime = nextWakeUpTimeTotal - now;
						if ((sleepTime > 10 && now > nextGC)
								|| now > nextGCForce) {
							nextGC = now + 2000;
							nextGCForce = nextGC + 10000;
							heap.gc();
							now = System.currentTimeMillis();
							sleepTime = nextWakeUpTimeTotal - now;
						}
						if (sleepTime > 0) {
							try {
								Thread.sleep(sleepTime);
							} catch (InterruptedException e) {
							}
						}
						nextWakeUpTimeTotal = Long.MAX_VALUE;
						idt = runningThreads.iterator();
						run = false;
					}
					break;
				case STATE_STOP_PENDING:
					synchronized (stateLock) {
						synchronized (threadsLock) {
							runningThreads.addAll(pendingThreads);
							pendingThreads.clear();
						}
						idt = runningThreads.iterator();
						setState(STATE_STOP);
						workingThread = null;
						return;
					}
				case STATE_DEAD_PENDING:
					synchronized (stateLock) {
						setState(STATE_DEAD);
						workingThread = null;
						return;
					}
				default:
					throw new IllegalThreadStateException();
				}
			}
		} catch (VMCriticalException e) {
			context.getConsole().error("Critical Exception! Shutdown VM", e);
			context.onException(e);
			this.exitException = e;
			synchronized (stateLock) {
				setState(STATE_DEAD);
				workingThread = null;
			}
		} catch (Throwable e) {
			context.getConsole().error("Uncatched Exception! Shutdown VM", e);
			context.onException(e);
			this.exitException = e;
			synchronized (stateLock) {
				setState(STATE_DEAD);
				workingThread = null;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cirnoworks.fisce.vm.IThreadManager#start()
	 */
	public void start() throws VMException {
		synchronized (stateLock) {
			if (workingThread != null) {
				throw new IllegalStateException();
			}
			if (state != STATE_STOP) {
				throw new IllegalStateException();
			}
			setState(STATE_RUN_PENDING);
			workingThread = new Thread(this);
			workingThread.start();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cirnoworks.fisce.vm.IThreadManager#requestStop()
	 */
	public void requestStop() {
		synchronized (stateLock) {
			switch (state) {
			case STATE_RUN_PENDING:
			case STATE_RUNNING:
				setState(STATE_STOP_PENDING);
			case STATE_STOP_PENDING:
			case STATE_STOP:
			case STATE_DEAD:
			case STATE_DEAD_PENDING:
				break;
			default:
				throw new IllegalStateException("" + state);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cirnoworks.fisce.vm.IThreadManager#waitTillStopped(long)
	 */
	public int waitTillStopped(long waitTime) throws InterruptedException {
		synchronized (stateLock) {
			switch (state) {
			case STATE_RUN_PENDING:
			case STATE_RUNNING:
			case STATE_STOP_PENDING:
			case STATE_DEAD_PENDING:
				break;
			case STATE_STOP:
			case STATE_DEAD:
				return exitCode;
			default:
				throw new IllegalThreadStateException();
			}
		}
		long time = waitTime > 0 ? System.currentTimeMillis() + waitTime
				: Long.MAX_VALUE;
		while (System.currentTimeMillis() < time) {
			synchronized (stateLock) {
				if (state == STATE_STOP || state == STATE_DEAD) {
					break;
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				break;
			}
		}
		synchronized (stateLock) {
			if (state == STATE_STOP || state == STATE_DEAD) {
				if (exitException != null) {
					throw new RuntimeException("VMError before finish!",
							exitException);
				}
				return exitCode;
			} else {
				throw new InterruptedException();
			}
		}
	}

	public void setPriority(int threadHandle, int priority) throws VMException {
		ArrayThread dt = getThread(threadHandle);
		if (dt == null) {
			// The sun cldc impl will call this when Thread object first
			// instanced
			// In this time we don't have a thread here.
			// How can I know when user create a Thread object without some
			// hacking?
			// WTF!
			return;
		}
		dt.setPriority(priority);
	}

	public IThread[] getThreads() throws VMException {
		synchronized (threadsLock) {
			IThread[] rt = runningThreads.toArray(new IThread[runningThreads
					.size() + pendingThreads.size()]);
			IThread[] pt = pendingThreads.toArray(new IThread[pendingThreads
					.size()]);
			System.arraycopy(pt, 0, rt, runningThreads.size(),
					pendingThreads.size());
			return rt;
		}
	}

	public void loadData(Element data) throws VMCriticalException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			setState(STATE_STOP);
			NodeList threadsElement = data.getElementsByTagName("threads");
			NodeList threadElements = ((Element) threadsElement.item(0))
					.getElementsByTagName("thread");
			for (int i = 0, max = threadElements.getLength(); i < max; i++) {
				Element te = (Element) threadElements.item(i);
				int tid = Integer.parseInt(te.getAttribute("tid"));
				int waitForLockId1 = Integer.parseInt(te
						.getAttribute("waitForLockId"));
				int waitForNotifyId1 = Integer.parseInt(te
						.getAttribute("waitForNotifyId"));
				int pendingLockCount1 = Integer.parseInt(te
						.getAttribute("pendingLockCount"));
				long nextWakeUpTime1 = Long.parseLong(te
						.getAttribute("nextWakeUpTime"));
				boolean interrupted1 = Boolean.parseBoolean(te
						.getAttribute("interrupted"));
				boolean destroyPending1 = Boolean.parseBoolean(te
						.getAttribute("destroyPending"));
				boolean daemon1 = Boolean.parseBoolean(te
						.getAttribute("daemon"));
				baos.reset();
				Base64.decode(DOMHelper.getTextContent(te), baos);
				ArrayThread dt = new ArrayThread(context, this);
				dt.createFromData(baos.toByteArray());
				runningThreads.add(dt);
				threadIds[tid] = true;
				waitForLockId[tid] = waitForLockId1;
				waitForNotifyId[tid] = waitForNotifyId1;
				pendingLockCount[tid] = pendingLockCount1;
				nextWakeUpTime[tid] = nextWakeUpTime1;
				interrupted[tid] = interrupted1;
				destroyPending[tid] = destroyPending1;
				daemon[tid] = daemon1;
			}

			Element monitorsElement = (Element) data.getElementsByTagName(
					"monitors").item(0);
			NodeList monitorElements = monitorsElement
					.getElementsByTagName("monitor");
			for (int i = 0, max = monitorElements.getLength(); i < max; i++) {
				Element me = (Element) monitorElements.item(i);
				int handle = Integer.parseInt(me.getAttribute("handle"));
				int owner = Integer.parseInt(me.getAttribute("owner"));
				int times = Integer.parseInt(me.getAttribute("times"));
				monitorOwnerId[handle] = owner;
				monitorOwnerTimes[handle] = times;
			}
		} catch (Exception e) {
			throw new VMCriticalException(e);
		}
	}

	public void saveData(Element data) {
		// private final ArrayList<ArrayThread> runningThreads = new
		// ArrayList<ArrayThread>();
		// thread id --> monitor id waiting for monitorentry
		// private int[] waitForLockId = new int[MAX_THREADS];
		// thread id --> monitor id waiting for notify
		// private int[] waitForNotifyId = new int[MAX_THREADS];
		// thread id --> how much time the thread orignally had
		// private int[] pendingLockCount = new int[MAX_THREADS];
		// thread id --> next wakeup time
		// private long[] nextWakeUpTime = new long[MAX_THREADS];
		// thread id --> interrupted
		// private boolean[] interrupted = new boolean[MAX_THREADS];
		// thread id --> destroyPending
		// private boolean[] destroyPending = new boolean[MAX_THREADS];
		// handle --> monitor owner id
		Document document = data.getOwnerDocument();
		Element threads = document.createElement("threads");
		data.appendChild(threads);
		for (ArrayThread dt : runningThreads) {
			Element thread = document.createElement("thread");
			DOMHelper.setTextContent(thread, Base64.encode(dt.getFullStack()));
			int tid = dt.getThreadId();
			thread.setAttribute("tid", String.valueOf(tid));
			thread.setAttribute("waitForLockId",
					String.valueOf(waitForLockId[tid]));
			thread.setAttribute("waitForNotifyId",
					String.valueOf(waitForNotifyId[tid]));
			thread.setAttribute("pendingLockCount",
					String.valueOf(pendingLockCount[tid]));
			thread.setAttribute("nextWakeUpTime",
					String.valueOf(nextWakeUpTime[tid]));
			thread.setAttribute("interrupted", String.valueOf(interrupted[tid]));
			thread.setAttribute("destroyPending",
					String.valueOf(destroyPending[tid]));
			thread.setAttribute("daemon", String.valueOf(daemon[tid]));
			threads.appendChild(thread);
		}

		// private int[] monitorOwnerId = new int[IHeap.MAX_OBJECTS];
		// handle --> monitor owner times
		// private int[] monitorOwnerTimes = new int[IHeap.MAX_OBJECTS];
		Element monitors = document.createElement("monitors");
		for (int i = 0; i < JHeap.MAX_OBJECTS; i++) {
			if (monitorOwnerId[i] >= 0) {
				Element monitor = document.createElement("monitor");
				monitor.setAttribute("handle", String.valueOf(i));
				monitor.setAttribute("owner", String.valueOf(monitorOwnerId[i]));
				monitor.setAttribute("times",
						String.valueOf(monitorOwnerTimes[i]));
				monitors.appendChild(monitor);

			}
		}
		data.appendChild(monitors);
	}

	public void exit(int exitCode) {
		synchronized (context) {
			setState(STATE_DEAD_PENDING);
			this.exitCode = exitCode;
		}
	}

	private void setState(int state) {
		synchronized (stateLock) {
			this.state = state;
			context.onStateChange(state);
		}
	}

}