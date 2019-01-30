package com.avnet.emasia.webquote.masterData.ejb.job;



import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.Timer;

import org.jboss.ejb3.annotation.TransactionTimeout;

import com.avnet.emasia.webquote.masterData.dto.Config;
import com.avnet.emasia.webquote.masterData.dto.FileContext;
import com.avnet.emasia.webquote.masterData.ejb.MasterDataSB;
import com.avnet.emasia.webquote.masterData.ejb.ScheduleConfigSB;
import com.avnet.emasia.webquote.masterData.ejb.StandardJob;
import com.avnet.emasia.webquote.masterData.ejb.remote.MaterialRemote;
import com.avnet.emasia.webquote.masterData.exception.CheckedException;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-01-23
 * Upload sequence:  3.
 */
@Stateless
@Remote(MaterialRemote.class)
public class MaterialLoadingJob extends StandardJob implements MaterialRemote
{
	private static Logger logger = Logger.getLogger(MaterialLoadingJob.class.getName());
    //private String[] tables = {"MATERIAL"};
    private FileContext fileCon = null;
    
    @EJB
    private MasterDataSB masterSB;
    @EJB
    private ScheduleConfigSB schedConfSB;
  //For schedule job
    /* (non-Javadoc)
     * @see com.avnet.emasia.webquote.masterData.ejb.StandardJob#executeTask(javax.ejb.Timer)
     */
	@Asynchronous
	@TransactionTimeout(value = 36000, unit = TimeUnit.SECONDS)
	public void executeTask(Timer timer) {

		try {
			setType("MATERIAL");
			run();
		} catch (Exception e) {
			String msg = MessageFormatorUtil.getParameterizedStringFromException(e);
			logger.log(Level.SEVERE, "Exception occured for Timer: " + timer.getInfo() + ", Task: " + this.getType()
					+ ", Reason for failure: " + msg, e);
		}
	}
    
    //For control-M  
    /* (non-Javadoc)
     * @see com.avnet.emasia.webquote.masterData.ejb.remote.MaterialRemote#executeTask()
     */
	@TransactionTimeout(value = 36000, unit = TimeUnit.SECONDS)
	@Override
	public int executeTask() {
		int returnInt = 1;
		try {
			setType("MATERIAL");
			run();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Exception occured in executing task: " + this.getType() + ", Reason for failure: "
					+ e.getMessage(), e);
			returnInt = 0;
		}
		return returnInt;
	}
    
    
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.avnet.emasia.webquote.masterData.ejb.StandardJob#init()
	 */
	public void init() {
		String[] tables = schedConfSB.getTables("MATERIAL");
		fileCon = new FileContext(tables);
		fileCon.setConfig(initCommonLoadingAndSchedule(tables));
	}
    /*
     * loading actions
     */
    /* (non-Javadoc)
     * @see com.avnet.emasia.webquote.masterData.ejb.StandardJob#loadingAction()
     */
	@Override
	public void loadingAction() throws CheckedException {
		logger.log(Level.FINE, "calling MaterialLoadingJob#loadingAction method...");
		init();
		loadingActionLoadingAndScheduleFileContext(fileCon);
		boolean switcher = schedConfSB.getSwitch("MATERIAL");
		if (switcher) {
			logger.info("run additional job");
		}

	}
    
    @Override
    public boolean isRanOnThisServer(String serviceName) {
    	return true;
    }
	
    /* (non-Javadoc)
     * @see com.avnet.emasia.webquote.masterData.ejb.StandardJob#transfer(java.util.List, com.avnet.emasia.webquote.masterData.dto.Config, java.lang.String, java.lang.String)
     */
    @Override
	public List<Object> transfer(List<String> dataList , Config config, String functionName, String fileName) throws CheckedException
	{
		return transferMasterAndMaterial(dataList, config, functionName, fileName);
	}


	/* (non-Javadoc)
	 * @see com.avnet.emasia.webquote.masterData.ejb.StandardJob#merge(java.util.List[], java.lang.String[], java.lang.String[])
	 */
	@Override
	public void merge(List<Object>[] objList, String[] fileNameArray, String[] functionNameArray) throws CheckedException
	{
		masterSB.mergeMaterial(objList, fileNameArray,functionNameArray);
	}
    
	
}
