package com.avnet.emasia.webquote.masterData.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-01-22
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

	public Object getClassInstance(String className)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		Class theClass = this.getClass();
		ClassLoader theLoader = theClass.getClassLoader();
		return theLoader.loadClass(className).newInstance();
	}
	
	public Object getClass(String className)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		Class theClass = this.getClass();
		ClassLoader theLoader = theClass.getClassLoader();
		return theLoader.loadClass(className);
	}
	
//	public HSSFWorkbook getHSSFWorkbook(String fileName) throws IOException {
//		POIFSFileSystem fs = new POIFSFileSystem(getInputStream(fileName));
//		HSSFWorkbook afile = new HSSFWorkbook(fs); 
//		return afile;
//	}
	
	public InputStream getInputStream(String fileName) {
		Class theClass = this.getClass();
		ClassLoader theLoader = theClass.getClassLoader();
		return theLoader.getResourceAsStream(fileName);
		
	}
}
