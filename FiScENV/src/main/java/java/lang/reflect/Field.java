/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/**
 * @author Evgueni Brevnov, Serguei S. Zapreyev, Alexey V. Varlamov
 */

package java.lang.reflect;

import java.lang.annotation.Annotation;

/**
 * @com.intel.drl.spec_ref
 */
public final class Field extends AccessibleObject implements Member {

	/**
	 * @com.intel.drl.spec_ref
	 */
	@Override
	public Annotation[] getDeclaredAnnotations() {
		throw new RuntimeException(
				"Generic and annotations in reflection is not currently supported.");
	}

	/**
	 * @com.intel.drl.spec_ref
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		throw new RuntimeException(
				"Generic and annotations in reflection is not currently supported.");
	}

	/**
	 * @com.intel.drl.spec_ref
	 */
	public Type getGenericType() throws GenericSignatureFormatError,
			TypeNotPresentException, MalformedParameterizedTypeException {
		throw new RuntimeException(
				"Generic and annotations in reflection is not currently supported.");
	}

	/**
	 * @com.intel.drl.spec_ref
	 */
	public String toGenericString() {
		return getUniqueName();
	}

	/**
	 * @com.intel.drl.spec_ref
	 */
	public native boolean isSynthetic();

	/**
	 * @com.intel.drl.spec_ref
	 */
	public native boolean isEnumConstant();

	/**
	 * @com.intel.drl.spec_ref
	 */
	public native Object get(Object obj) throws IllegalArgumentException,
			IllegalAccessException;

	/**
	 * @com.intel.drl.spec_ref
	 */
	public native boolean getBoolean(Object obj)
			throws IllegalArgumentException, IllegalAccessException;

	/**
	 * @com.intel.drl.spec_ref
	 */
	public native byte getByte(Object obj) throws IllegalArgumentException,
			IllegalAccessException;

	/**
	 * @com.intel.drl.spec_ref
	 */
	public native char getChar(Object obj) throws IllegalArgumentException,
			IllegalAccessException;

	/**
	 * @com.intel.drl.spec_ref
	 */
	public native Class<?> getDeclaringClass();

	/**
	 * @com.intel.drl.spec_ref
	 */
	public native double getDouble(Object obj) throws IllegalArgumentException,
			IllegalAccessException;

	/**
	 * @com.intel.drl.spec_ref
	 */
	public native float getFloat(Object obj) throws IllegalArgumentException,
			IllegalAccessException;

	/**
	 * @com.intel.drl.spec_ref
	 */
	public native int getInt(Object obj) throws IllegalArgumentException,
			IllegalAccessException;

	/**
	 * @com.intel.drl.spec_ref
	 */
	public native long getLong(Object obj) throws IllegalArgumentException,
			IllegalAccessException;

	/**
	 * @com.intel.drl.spec_ref
	 */
	public native int getModifiers();

	/**
	 * @com.intel.drl.spec_ref
	 */
	public native String getName();

	/**
	 * @com.intel.drl.spec_ref
	 */
	public native short getShort(Object obj) throws IllegalArgumentException,
			IllegalAccessException;

	/**
	 * @com.intel.drl.spec_ref
	 */
	public native Class<?> getType();

	/**
	 * @com.intel.drl.spec_ref
	 */
	@Override
	public int hashCode() {
		return getDeclaringClass().getName().hashCode() ^ getName().hashCode();
	}

	/**
	 * @com.intel.drl.spec_ref
	 */
	public native void set(Object obj, Object value)
			throws IllegalArgumentException, IllegalAccessException;

	/**
	 * @com.intel.drl.spec_ref
	 */
	public native void setBoolean(Object obj, boolean value)
			throws IllegalArgumentException, IllegalAccessException;

	/**
	 * @com.intel.drl.spec_ref
	 */
	public native void setByte(Object obj, byte value)
			throws IllegalArgumentException, IllegalAccessException;

	/**
	 * @com.intel.drl.spec_ref
	 */
	public native void setChar(Object obj, char value)
			throws IllegalArgumentException, IllegalAccessException;

	/**
	 * @com.intel.drl.spec_ref
	 */
	public native void setDouble(Object obj, double value)
			throws IllegalArgumentException, IllegalAccessException;

	/**
	 * @com.intel.drl.spec_ref
	 */
	public native void setFloat(Object obj, float value)
			throws IllegalArgumentException, IllegalAccessException;

	/**
	 * @com.intel.drl.spec_ref
	 */
	public native void setInt(Object obj, int value)
			throws IllegalArgumentException, IllegalAccessException;

	/**
	 * @com.intel.drl.spec_ref
	 */
	public native void setLong(Object obj, long value)
			throws IllegalArgumentException, IllegalAccessException;

	/**
	 * @com.intel.drl.spec_ref
	 */
	public native void setShort(Object obj, short value)
			throws IllegalArgumentException, IllegalAccessException;

	/**
	 * @com.intel.drl.spec_ref
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(80);
		// append modifiers if any
		int modifier = getModifiers();
		if (modifier != 0) {
			sb.append(Modifier.toString(modifier)).append(' ');
		}
		// append return type
		appendArrayType(sb, getType());
		sb.append(' ');
		// append full field name
		sb.append(getDeclaringClass().getName()).append('.').append(getName());
		return sb.toString();
	}

	/* NON API SECTION */

	native String getUniqueName();

}
