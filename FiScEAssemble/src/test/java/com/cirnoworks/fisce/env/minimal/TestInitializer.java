/**
 *  Copyright 2010-2011 Yuxuan Huang. All rights reserved.
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
package com.cirnoworks.fisce.env.minimal;

import java.io.InputStream;

import org.w3c.dom.Element;

import com.cirnoworks.fisce.intf.FiScEVM;
import com.cirnoworks.fisce.intf.IThread;
import com.cirnoworks.fisce.intf.IToolkit;
import com.cirnoworks.fisce.intf.NativeHandlerTemplate;
import com.cirnoworks.fisce.intf.NullConsole;
import com.cirnoworks.fisce.intf.VMCriticalException;
import com.cirnoworks.fisce.intf.VMException;
import com.cirnoworks.fisce.vm.VMContext;
import com.cirnoworks.fisce.vm.default_impl.ArrayHeap;
import com.cirnoworks.fisce.vm.default_impl.ArrayThreadManager;
import com.cirnoworks.fisce.vm.default_impl.DefaultClassLoader;
import com.cirnoworks.fisce.vm.default_impl.DefaultHeap;
import com.cirnoworks.fisce.vm.default_impl.DefaultThreadManager;
import com.cirnoworks.fisce.vm.default_impl.FastThreadManager;

public class TestInitializer {

	private static class TestToolkit implements IToolkit {

		private class FailSupport extends NativeHandlerTemplate {

			public void dealNative(int[] args, IThread thread)
					throws VMException, VMCriticalException {
				String str = args[0] == 0 ? "null" : context.getHeap()
						.getString(args[0]);
				throw new VMCriticalException("Test failed: " + str);
			}

			public String getUniqueName() {
				return "EXCLUDE/fisce/test/TestService.fail.(Ljava/lang/String;)V";
			}

		}

		private VMContext context;

		public void setContext(FiScEVM context) {
			this.context = (VMContext) context;
		}

		public void setupContext() {
			context.registerNativeHandler(new FailSupport());
		}

		public InputStream getResourceByClassName(String className) {
			return null;
		}

		public void saveData(Element data) throws VMCriticalException {

		}

		public void loadData(Element data) throws VMCriticalException {

		}

	}

	public static VMContext getContext() {
		VMContext vm = new VMContext();
		vm.setClassLoader(new DefaultClassLoader());
		vm.setHeap(new DefaultHeap());
		vm.setThreadManager(new DefaultThreadManager());
		vm.setConsole(new NullConsole());
		// vm.setConsole(Log4JConsole.getConsole());
		vm.addToolkit(new BaseToolkit());
		vm.addToolkit(new TestToolkit());
		return vm;
	}

	public static VMContext getFastContext() {
		VMContext vm = new VMContext();
		vm.setClassLoader(new DefaultClassLoader());
		vm.setHeap(new DefaultHeap());
		vm.setThreadManager(new FastThreadManager());
		vm.setConsole(new NullConsole());
		// vm.setConsole(Log4JConsole.getConsole());
		vm.addToolkit(new BaseToolkit());
		vm.addToolkit(new TestToolkit());
		return vm;
	}

	public static VMContext getArrayContext() {
		VMContext vm = new VMContext();
		vm.setClassLoader(new DefaultClassLoader());
		vm.setHeap(new ArrayHeap());
		vm.setThreadManager(new ArrayThreadManager());
		vm.setConsole(new NullConsole());
		// vm.setConsole(Log4JConsole.getConsole());
		vm.addToolkit(new BaseToolkit());
		vm.addToolkit(new TestToolkit());
		return vm;
	}
}
