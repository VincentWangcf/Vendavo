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
import com.avnet.emasia.webquote.masterData.ejb.StandardJob;
import com.avnet.emasia.webquote.masterData.ejb.remote.SalesOrderRemote;
import com.avnet.emasia.webquote.masterData.exception.CheckedException;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-03-15
 * Upload sequence:  1.
 */

@Stateless
@Remote(SalesOrderRemote.class)
public class SalesOrderLoadingJob extends StandardJob implements SalesOrderRemote
{
	private static Logger logger = Logger.getLogger(SalesOrderLoadingJob.class.getName());
    private String[] tables = {"SALES_ORDER"};
    private FileContext fileCon = null;
    
    @EJB
    private MasterDataSB masterSB;
  //For schedule job
    /* (non-Javadoc)
     * @see com.avnet.emasia.webquote.masterData.ejb.job.SalesOrderLoadingJob#executeTask(javax.ejb.Timer)
     */
    @Asynchronous
    @TransactionTimeout(value = 30000, unit = TimeUnit.SECONDS) 
    public void executeTask(Timer timer)
    {
        try
        {
        	setType("SALES_ORDER");
            run();
        }
        catch(Exception e)
        {
        	String msg = MessageFormatorUtil.getParameterizedStringFromException(e);
        	logger.log(Level.SEVERE, "Exception occured in SalesOrderLoadingJob#executeTask for Timer: "+timer.getInfo()+", Task: "+this.getType()+", Reason for failure: "+msg, e);
        }
    }
    //For control-M   
    /* (non-Javadoc)
     * @see com.avnet.emasia.webquote.masterData.ejb.remote.SalesOrderRemote#executeTask()
     */
    @TransactionTimeout(value = 30000, unit = TimeUnit.SECONDS) 
    @Override
	public int executeTask() {
		int returnInt = 1;
		try {
			setType("SALES_ORDER");
			run();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Exception occured in executing task: " + this.getType() + ", Reason for failure: "
					+ e.getMessage(), e);
			returnInt = 0;
		}
		return returnInt;

	}
    
    /* (non-Javadoc)
     * @see com.avnet.emasia.webquote.masterData.ejb.StandardJob#loadingAction()
     */
    @Override
	public void loadingAction() throws CheckedException
	{
    	fileCon = new FileContext(tables);  		
		fileCon.setConfig(initCommonLoadingAndSchedule(tables));;
		loadingActionLoadingAndScheduleFileContext(fileCon);
	

	}
	
	/* (non-Javadoc)
	 * @see com.avnet.emasia.webquote.masterData.ejb.StandardJob#transfer(java.util.List, com.avnet.emasia.webquote.masterData.dto.Config, java.lang.String, java.lang.String)
	 */
	@Override
	public List<Object> transfer(List<String> dataList , Config config, String functionName, String fileName) throws CheckedException
		{
			return transferSalesOrder(dataList, config, functionName, fileName);
		}
	

	
	/* (non-Javadoc)
	 * @see com.avnet.emasia.webquote.masterData.ejb.StandardJob#merge(java.util.List[], java.lang.String[], java.lang.String[])
	 */
	@Override
	public void merge(List<Object>[] objList, String[] fileNameArray, String[] functionNameArray) throws CheckedException
	{
		masterSB.mergeSalesOrder(objList, fileNameArray,functionNameArray);
	}
	
	@Override
	public boolean isRanOnThisServer(String serviceName) {
		return true;
	}


}
