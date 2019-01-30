package com.avnet.emasia.webquote.masterData.ejb.scheduleJob;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.Timer;

import org.jboss.ejb3.annotation.TransactionTimeout;

import com.avnet.emasia.webquote.exception.CommonConstants;
import com.avnet.emasia.webquote.masterData.dto.Config;
import com.avnet.emasia.webquote.masterData.dto.FileContext;
import com.avnet.emasia.webquote.masterData.ejb.MasterDataSB;
import com.avnet.emasia.webquote.masterData.ejb.StandardJob;
import com.avnet.emasia.webquote.masterData.ejb.remote.MfrRemote;
import com.avnet.emasia.webquote.masterData.exception.CheckedException;
import com.avnet.emasia.webquote.masterData.util.Constants;
import com.avnet.emasia.webquote.masterData.util.FunctionUtils;
import com.avnet.emasia.webquote.masterData.util.ResourceLoader;
import com.avnet.emasia.webquote.masterData.util.StringUtils;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;


/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-01-23
 * 
 */
@Stateless
public class SpecialCustomerScheduleJob extends StandardJob
{
	private static Logger logger = Logger.getLogger(SpecialCustomerScheduleJob.class.getName());
    private static String[] tables= {"SPECIAL_CUSTOMER"};
    private FileContext fileCon = null;
    
    @EJB
    private MasterDataSB masterSB;
  //For schedule job
    /* (non-Javadoc)
     * @see com.avnet.emasia.webquote.masterData.ejb.StandardJob#executeTask(javax.ejb.Timer)
     */
    @Asynchronous
    @TransactionTimeout(value = 36000, unit = TimeUnit.SECONDS) 
    public void executeTask(Timer timer)
    {
        
        try
        {
        	setType("SPECIAL_CUSTOMER");
            run();  	
        }
        catch(Exception e)
        {
        	String msg = MessageFormatorUtil.getParameterizedStringFromException(e);
        	logger.log(Level.SEVERE, "Exception occured for Timer: "+timer.getInfo()+", Task: "+this.getType()+", Reason for failure: "+msg, e);
        }
    }

    
    @Override
	public void loadingAction() throws CheckedException
	{
    	logger.log(Level.FINE, "SpecialCustomerScheduleJob loadingAction Method Call......");
    	fileCon = new FileContext(tables);  		
		fileCon.setConfig(initCommonLoadingAndSchedule(tables));
		loadingActionLoadingAndScheduleFileContext(fileCon);

	}
	
	@Override
	public List<Object> transfer(List<String> dataList , Config config, String functionName, String fileName) throws CheckedException
		{
			return transferSpecialCustomer(dataList, config, functionName, fileName);
		}
	

	
	@Override
	public void merge(List<Object>[] objList, String[] fileNameArray, String[] functionNameArray) throws CheckedException
	{
		masterSB.mergeSpecialCustomer(objList, fileNameArray,functionNameArray);
	}

}
