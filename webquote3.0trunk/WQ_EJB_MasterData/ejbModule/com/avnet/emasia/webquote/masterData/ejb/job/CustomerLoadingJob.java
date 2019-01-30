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
import com.avnet.emasia.webquote.masterData.ejb.remote.CustomerRemote;
import com.avnet.emasia.webquote.masterData.exception.CheckedException;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;


/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-01-23
 * Upload sequence:  2.
 */
@Stateless
@Remote(CustomerRemote.class)
public class CustomerLoadingJob extends StandardJob implements CustomerRemote
{
	private static Logger logger = Logger.getLogger(CustomerLoadingJob.class.getName());
    private String[] tables = {"CUSTOMER","CUSTOMER_ADDRESS","CUSTOMER_SALE","CUSTOMER_PARTNER"};
    private FileContext fileCon = null;
    
    @EJB
    private MasterDataSB masterSB;
    @EJB
    private ScheduleConfigSB schedConfSB;
  //For schedule job
    /* (non-Javadoc)
     * @see com.avnet.emasia.webquote.masterData.ejb.job.CustomerLoadingJob#executeTask(javax.ejb.Timer)
     */
    @Asynchronous
    @TransactionTimeout(value = 30000, unit = TimeUnit.SECONDS) 
    public void executeTask(Timer timer)
    {
        try
        {
        	setType("CUSTOMER");
            run();	
        }
        catch(Exception e)
        {
        	String msg = MessageFormatorUtil.getParameterizedStringFromException(e);
        	logger.log(Level.SEVERE, "Exception occured in CustomerLoadingJob#executeTask for Timer: "+timer.getInfo()+", Task: "+this.getType()+", Reason for failure: "+msg, e);
        }
    }
    
    //For control-M
    @TransactionTimeout(value = 30000, unit = TimeUnit.SECONDS) 
    @Override
    public int executeTask()
    {
        int returnInt = 1; 
    	try
        {
	    	setType("CUSTOMER");
	        run();
        }
    	catch(Exception e)
        {
    		logger.log(Level.SEVERE, "Exception occured in executing task: "+this.getType()+", Reason for failure: "+e.getMessage(), e);
        	returnInt = 0 ;
        }
        return returnInt;
    }
   
	@Override
	public void loadingAction() throws CheckedException {
		fileCon = new FileContext(tables);
		fileCon.setConfig(initCommonLoadingAndSchedule(tables));
		loadingActionLoadingAndScheduleFileContext(fileCon);
		boolean switcher = schedConfSB.getSwitch("MATERIAL");
		if (switcher) {
			logger.info("run additional job");
		}
	}

	@Override
	public List<Object> transfer(List<String> dataList, Config config, String functionName, String fileName)
			throws CheckedException {
		return transferCustomer(dataList, config, functionName, fileName, "");
	}
	
	
	@Override
	public void merge(List<Object>[] objList, String[] fileNameArray, String[] functionNameArray) throws CheckedException
	{
		masterSB.mergeCustomer(objList, fileNameArray,functionNameArray);
	}
	
	@Override
	public boolean isRanOnThisServer(String serviceName) {
		return true;
	}
	
	
}
