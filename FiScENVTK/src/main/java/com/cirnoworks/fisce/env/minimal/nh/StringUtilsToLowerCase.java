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
package com.cirnoworks.fisce.env.minimal.nh;

import com.cirnoworks.fisce.vm.IHeap;
import com.cirnoworks.fisce.vm.IThread;
import com.cirnoworks.fisce.vm.NativeHandlerTemplate;
import com.cirnoworks.fisce.vm.VMCriticalException;
import com.cirnoworks.fisce.vm.VMException;

/**
 * @author Cloudee
 * 
 */
public class StringUtilsToLowerCase extends NativeHandlerTemplate {

	private IHeap heap;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cirnoworks.fisce.vm.INativeHandler#dealNative(int[],
	 * com.cirnoworks.fisce.vm.IThread)
	 */
	@Override
	public void doInit() throws VMException, VMCriticalException {
		heap = context.getHeap();
	}

	public void dealNative(int[] args, IThread thread) throws VMException,
			VMCriticalException {
		String in = heap.getString(args[0]);
		thread.pushHandle(heap.putString(in.toUpperCase()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cirnoworks.fisce.vm.INativeHandler#getUniqueName()
	 */
	public String getUniqueName() {
		return "com/cirnoworks/fisce/privat/StringUtils.toUpperCase.(Ljava/lang/String;)Ljava/lang/String;";
	}

}
