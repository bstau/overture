package org.overture.typechecker.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.typechecker.tests.framework.BaseTestSuite;
import org.overture.typechecker.tests.framework.ClassTestCase;
import org.overturetool.test.framework.Properties;
import org.overturetool.vdmj.lex.LexLocation;

public class ClassVdmPpTypeChecker extends BaseTestSuite {
	
	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		LexLocation.absoluteToStringLocation = false;
		Properties.recordTestResults = true;
		String name = "Type Check Class TestSuite";
		String root = "src\\test\\resources\\classesPP";
		//String root = "src\\test\\resources\\test";
		TestSuite test = createTestCompleteFile(name, root, ClassTestCase.class);
		return test;
	}
}
