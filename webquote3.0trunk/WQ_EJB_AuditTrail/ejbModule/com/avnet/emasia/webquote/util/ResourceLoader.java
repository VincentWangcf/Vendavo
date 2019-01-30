package com.avnet.emasia.webquote.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2012-12-25
 * 
 */

public final class ResourceLoader {

	//create singleton
	private static ResourceLoader loader = new ResourceLoader();

	private ResourceLoader() {
	}

	public static ResourceLoader getInstance() {
		return loader;
	}

	public Properties getProp(String fileName) throws IOException {
		Properties prop = new Properties();
		prop.load(getInputStream(fileName));
		return prop;
	}
	
	public ByteArrayInputStream getXLSFile(String fileName) throws IOException {
		ByteArrayInputStream afile =(ByteArrayInputStream) getInputStream(fileName); 
		return afile;
	}
	public InputStream getInputStream(String fileName) {
		Class theClass = this.getClass();
		ClassLoader theLoader = theClass.getClassLoader();
		return theLoader.getResourceAsStream(fileName);
		
	}

	public Object getClassInstance(String className)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		Class theClass = this.getClass();
		ClassLoader theLoader = theClass.getClassLoader();
		return theLoader.loadClass(className).newInstance();
	}

	public Object getClassInstance(String className, Object[] args)
			throws ClassNotFoundException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, SecurityException, NoSuchMethodException {

		Class theClass = this.getClass();
		ClassLoader theLoader = theClass.getClassLoader();
		Class[] type = new Class[args.length];
		for (int i = 0; i < args.length; i++) {
			type[i] = args[i].getClass();
		}
		Class cls = theLoader.loadClass(className);
		Constructor construct = cls.getConstructor(type);
		Object o = construct.newInstance(args);
		return o;
	}

	public static void main(String[] args) {
		
	}
}
