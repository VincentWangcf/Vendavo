package com.avnet.emasia.webquote.masterData.kit;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.avnet.emasia.webquote.exception.CommonConstants;
import com.avnet.emasia.webquote.exception.WebQuoteRuntimeException;
import com.avnet.emasia.webquote.masterData.exception.IOInteractException;
import com.avnet.emasia.webquote.masterData.util.Constants;
import com.avnet.emasia.webquote.masterData.util.FileUtils;


/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-01-22
 * 
 */

public class TxtLoadingUnit {

	private static Logger logger = Logger.getLogger(TxtLoadingUnit.class.getName());

	static class TxtLoadingUnitHolder {
		static TxtLoadingUnit unit = new TxtLoadingUnit();
	}

	public static TxtLoadingUnit getInstance() {
		return TxtLoadingUnitHolder.unit;
	}

	private TxtLoadingUnit() {
	}

	public List<String> convertTxt2Java(String fullPath) throws IOInteractException, IOException
	{
		if (FileUtils.isFile(fullPath) == false) 
		{
			throw new IOInteractException(CommonConstants.WQ_EJB_MASTER_DATA_PATH_IS_NOT_RIGHT, new WebQuoteRuntimeException());
		}
		InputStreamReader isr = FileUtils.readFile(fullPath,
				Constants.IN_ECODING);
		BufferedReader br = new BufferedReader(isr);
		String data = null;
		List<String> holdingList = new ArrayList<String>();
		try 
		{
			while ((data = br.readLine()) != null) 
			{
				holdingList.add(data);
			}
		} 
		catch (IOException e) 
		{
			throw new IOInteractException(CommonConstants.WQ_EJB_MASTER_DATA_TXT_FILE_READ_ERROR+" on file path: "+fullPath,e);
		} 
		finally 
		{
			try 
			{
				if (null != br)
					br.close();
				if (null != isr)
					isr.close();
			} 
			catch (IOException e) 
			{
				 /*logger.log(Level.SEVERE, e.getMessage(), e);*/
			    throw e;
			}
		}
		return holdingList;
	}
}
