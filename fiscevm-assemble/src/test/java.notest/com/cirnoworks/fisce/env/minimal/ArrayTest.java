/**
 *  Copyright 2010 Yuxuan Huang
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the Lesser GNU Lesser General Public License as published by
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

import com.cirnoworks.fisce.intf.FiScEVM;

/**
 * @author cloudee
 * 
 */
public class ArrayTest {
	public void testArray() throws Exception {
		FiScEVM context = TestInitializer.getContext();
		context.bootFromClass("EXCLUDE/fisce/test/ArrayTest");
		// context.requestStop();
		context.start();
		context.waitTillStopped(0);
	}

	public void testArrayFast() throws Exception {
		FiScEVM context = TestInitializer.getFastContext();
		context.bootFromClass("EXCLUDE/fisce/test/ArrayTest");
		// context.requestStop();
		context.start();
		context.waitTillStopped(0);
	}

	public void testArrayArray() throws Exception {
		FiScEVM context = TestInitializer.getArrayContext();
		// context.setConsole(Log4JConsole.getConsole());
		context.bootFromClass("EXCLUDE/fisce/test/ArrayTest");
		// context.requestStop();
		context.start();
		context.waitTillStopped(0);
	}
}