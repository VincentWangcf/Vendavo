package com.avnet.emasia.webquote.masterData.ejb.scheduleJob;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timer;

import org.jboss.ejb3.annotation.TransactionTimeout;

import com.avnet.emasia.webquote.entity.DataSyncError;
import com.avnet.emasia.webquote.masterData.dto.Config;
import com.avnet.emasia.webquote.masterData.dto.FileContext;
import com.avnet.emasia.webquote.masterData.ejb.MasterDataSB;
import com.avnet.emasia.webquote.masterData.ejb.StandardJob;
import com.avnet.emasia.webquote.masterData.exception.CheckedException;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;


/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-01-23
 * Upload sequence:  5.
 */
@Stateless
public class DrmsScheduleJob extends StandardJob
{
	private static Logger logger = Logger.getLogger(DrmsScheduleJob.class.getName());
    private String[] tables = {"PROJECT_HEADER","PROJECT_CUSTOMER","NEDA_HEADER","NEDA_ITEM"};
    private FileContext fileCon = null;
    
    @EJB
    private MasterDataSB masterSB;

  //For schedule job
    /* (non-Javadoc)
     * @see com.avnet.emasia.webquote.masterData.ejb.scheduleJob.DrmsScheduleJob#executeTask(javax.ejb.Timer)
     */
	@Asynchronous
	@TransactionTimeout(value = 30000, unit = TimeUnit.SECONDS)
	public void executeTask(Timer timer) {

		try {
			setType("DRMS");
			run();
		} catch (Exception e) {
			String msg = MessageFormatorUtil.getParameterizedStringFromException(e);
			logger.log(Level.SEVERE, "Exception occured in DrmsScheduleJob#executeTask for Timer: " + timer.getInfo() + ", Task: " + this.getType()
					+ ", Reason for failure: " + msg, e);
		}
	}

   
	@Override
	public void loadingAction() throws CheckedException {
		logger.log(Level.FINE, "Method call DrmsScheduleJob#loadingAction started... ");
		fileCon = new FileContext(tables);
		fileCon.setConfig(initCommonLoadingAndSchedule(tables));
		loadingActionLoadingAndScheduleFileContext(fileCon);

		// re-initial the error record list
		if (errorList != null)
			errorList.clear();
		errorList = new ArrayList<DataSyncError>();

	}

	@Override
	public List<Object> transfer(List<String> dataList , Config config, String functionName, String fileName) throws CheckedException
		{
			return transferDrms(dataList, config, functionName, fileName, errorList);
		}
	

	@Override
	public void merge(List<Object>[] objList, String[] fileNameArray, String[] functionNameArray) throws CheckedException
	{
		masterSB.mergeDrms(objList, fileNameArray,functionNameArray, errorList);
	}
	
}
