package com.avnet.emasia.webquote.masterData.ejb.scheduleJob;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timer;

import org.jboss.ejb3.annotation.TransactionTimeout;

import com.avnet.emasia.webquote.masterData.dto.Config;
import com.avnet.emasia.webquote.masterData.dto.FileContext;
import com.avnet.emasia.webquote.masterData.ejb.MasterDataSB;
import com.avnet.emasia.webquote.masterData.ejb.StandardJob;
import com.avnet.emasia.webquote.masterData.exception.CheckedException;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;


/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-01-23
 * 
 */
@Stateless
public class MfrScheduleJob extends StandardJob
{
	private static Logger logger = Logger.getLogger(MfrScheduleJob.class.getName());
    private static String[] tables= {"MANUFACTURER"};
    private FileContext fileCon = null;
    
    @EJB
    private MasterDataSB masterSB;
  //For schedule job
    /* (non-Javadoc)
     * @see com.avnet.emasia.webquote.masterData.ejb.StandardJob#executeTask(javax.ejb.Timer)
     */
    @Asynchronous
    @TransactionTimeout(value = 36000, unit = TimeUnit.SECONDS) 
	public void executeTask(Timer timer) {

		try {
			setType("MANUFACTURER");
			run();
		} catch (Exception e) {
			String msg = MessageFormatorUtil.getParameterizedStringFromException(e);
			logger.log(Level.SEVERE, "Exception occured in MfrScheduleJob#executeTask  for Timer: " + timer.getInfo() + ", Task: " + this.getType()
					+ ", Reason for failure: " + msg, e);
		}
	}

    
    /* (non-Javadoc)
     * @see com.avnet.emasia.webquote.masterData.ejb.StandardJob#loadingAction()
     */
    @Override
	public void loadingAction() throws CheckedException
	{
    	logger.info("The class MfrScheduleJob of  method loadingAction() call...");
		fileCon = new FileContext(tables);
		fileCon.setConfig(initCommonLoadingAndSchedule(tables));
		loadingActionDrmsAgpAndMFRFileContext(fileCon);
	}
	
	/* (non-Javadoc)
	 * @see com.avnet.emasia.webquote.masterData.ejb.StandardJob#transfer(java.util.List, com.avnet.emasia.webquote.masterData.dto.Config, java.lang.String, java.lang.String)
	 */
	@Override
	public List<Object> transfer(List<String> dataList, Config config, String functionName, String fileName)
			throws CheckedException {
		return transferDrmsAgpAndMfr(dataList, config, functionName, fileName);
	}
	
	/* (non-Javadoc)
	 * @see com.avnet.emasia.webquote.masterData.ejb.StandardJob#merge(java.util.List[], java.lang.String[], java.lang.String[])
	 */
	@Override
	public void merge(List<Object>[] objList, String[] fileNameArray, String[] functionNameArray) throws CheckedException
	{
		masterSB.mergeMfr(objList, fileNameArray,functionNameArray);
	}

}
