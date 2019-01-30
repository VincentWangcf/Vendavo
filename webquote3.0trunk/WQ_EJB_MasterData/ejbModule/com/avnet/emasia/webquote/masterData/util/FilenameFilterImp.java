package com.avnet.emasia.webquote.masterData.util;
import java.io.*;
/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-01-22
 * 
 */

public class FilenameFilterImp implements FilenameFilter {
	
	public FilenameFilterImp( String startFileName)
	{
		this.startFileName = startFileName; 
	}
	private String startFileName;
	
	public String getStartFileName() {
		return startFileName;
	}

	public void setStartFileName(String startFileName) {
		this.startFileName = startFileName;
	}

	public boolean accept(File dir, String name) {

		if ( name.startsWith(startFileName) && name.endsWith("DAT") ){
			return true;
		}
		return false;
	}

}
