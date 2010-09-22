package com.cirnoworks.fisce.env.minimal;

import junit.framework.TestCase;

import com.cirnoworks.fisce.vm.VMContext;
import com.cirnoworks.fisce.vm.data.ClassBase;
import com.cirnoworks.fisce.vm.data.ClassMethod;

public class ClassMethodTest extends TestCase {
	public void testGetParamCount() throws Exception {
		VMContext context = TestInitializer.getContext();
		context.bootFromClass("com/cirnoworks/fisce/test/Dummy");
		
		ClassBase it = (ClassBase) context
				.getClass("com/cirnoworks/fisce/test/ITester");
		ClassBase t = (ClassBase) context.getClass("com/cirnoworks/fisce/test/Tester");
		ClassBase tc = (ClassBase) context
				.getClass("com/cirnoworks/fisce/test/TesterChild");
		ClassMethod[] methods = t.getMethods();
		ClassMethod target = null;
		for (int i = 0, max = methods.length; i < max; i++) {
			ClassMethod method = methods[i];
			if (method.getName().equals("complex")) {
				target = method;
				break;
			}
		}
		assert target != null;
		assert target.getParamCount() == 8 : target.getParamCount();

	}
}