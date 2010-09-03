/**
 *  Copyright 2010 Yuxuan Huang
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the Lesser GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.cirnoworks.fisce.jvm13;

/**
 * 
 * @author cloudee
 */
public class VMException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6933484337768111121L;
	private String innerThrowableName;
	private String innerMessage;

	public VMException(String innerThrowableName, String message) {
		super(innerThrowableName + ":" + message);
		this.innerThrowableName = innerThrowableName;
		this.innerMessage = message;
	}

	public String getInnerThrowableName() {
		return innerThrowableName;
	}

	public String getInnerMessage() {
		return innerMessage;
	}

}