package com.avnet.emasia.webquote.masterData.dto;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import com.avnet.emasia.webquote.exception.CommonConstants;
import com.avnet.emasia.webquote.masterData.exception.CheckedException;
import com.avnet.emasia.webquote.masterData.exception.IOInteractException;
import com.avnet.emasia.webquote.masterData.kit.TxtLoadingUnit;
/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-01-25
 * 
 */
public class DataFile {
	
    private static Logger logger = Logger.getLogger("DataFile");
	public DataFile (File file){
		this.file = file;
	}
	private File file ;

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
	public boolean isFile(){
		return file.isFile();
	}
	
	public List<String> loadData() throws CheckedException 
	{
		logger.fine(" call loadData ");
		TxtLoadingUnit unit = TxtLoadingUnit.getInstance();
		List<String> rtnList = null;
		try 
		{
			rtnList = (List<String>)unit.convertTxt2Java(file.getAbsolutePath());
		} 
		catch (IOInteractException e) 
		{
			throw new CheckedException("load data error",e);
		}
		catch (IOException e) {
			throw new CheckedException("load data error", e);
		}
		return rtnList;
	}



}
