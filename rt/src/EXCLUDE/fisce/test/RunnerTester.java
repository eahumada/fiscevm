package EXCLUDE.fisce.test;

public class RunnerTester extends Thread {

	/**
	 * @param args
	 */
	public static final Object lock = new Object();
	public static final Object lock0 = new Object();
	private boolean started = false;

	public static void main(String[] args) {
		RunnerTester t = new RunnerTester();
		// t.setPriority(10);
		t.start();
		synchronized (lock0) {
			try {
				while (!t.started) {
					System.out.println("[WAIT]");
					lock0.wait();
					System.out.println("[WAIT OVER]");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		for (int i = 0; i < 4; i++) {
			synchronized (lock) {
				System.out.println("[********]");
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		t.interrupt();
		return;
	}

	public void run() {
		Thread.yield();
		synchronized (lock0) {
			started = true;
			System.out.println("[NOTIFY]");
			lock0.notifyAll();
			System.out.println("[NOTIFY OVER]");
		}
		int j = 0;
		while (true) {
			synchronized (lock) {
				System.out.println("[########]");
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				return;
			}
		}
	}

}
