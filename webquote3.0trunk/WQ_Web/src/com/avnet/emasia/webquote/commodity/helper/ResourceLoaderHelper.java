package com.avnet.emasia.webquote.commodity.helper;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-01-22
 * 
 */
public final class ResourceLoaderHelper {

	//create singleton
	private static ResourceLoaderHelper loader = new ResourceLoaderHelper();

	private ResourceLoaderHelper() {
	}

	public static ResourceLoaderHelper getInstance() {
		return loader;
	}
	
	public HSSFWorkbook getHSSFWorkbook(String fileName) throws IOException {
		POIFSFileSystem fs = new POIFSFileSystem(getInputStream(fileName));
		HSSFWorkbook afile = new HSSFWorkbook(fs); 
		return afile;
	}
	
	public InputStream getInputStream(String fileName) {
		Class theClass = this.getClass();
		ClassLoader theLoader = theClass.getClassLoader();
		return theLoader.getResourceAsStream(fileName);
		
	}
}
