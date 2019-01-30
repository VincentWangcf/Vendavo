package com.avnet.emasia.webquote.masterData.ejb.scheduleJob;


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

import com.avnet.emasia.webquote.entity.DataSyncError;
import com.avnet.emasia.webquote.exception.CommonConstants;
import com.avnet.emasia.webquote.masterData.util.StringUtils;


import com.avnet.emasia.webquote.masterData.dto.Config;
import com.avnet.emasia.webquote.masterData.dto.FileContext;
import com.avnet.emasia.webquote.masterData.ejb.MasterDataSB;
import com.avnet.emasia.webquote.masterData.ejb.ScheduleConfigSB;
import com.avnet.emasia.webquote.masterData.ejb.StandardJob;
import com.avnet.emasia.webquote.masterData.ejb.remote.PosRemote;
import com.avnet.emasia.webquote.masterData.exception.CheckedException;
import com.avnet.emasia.webquote.masterData.util.Constants;
import com.avnet.emasia.webquote.masterData.util.FunctionUtils;
import com.avnet.emasia.webquote.masterData.util.ResourceLoader;
import com.avnet.emasia.webquote.quote.ejb.PosSB;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-02-06
 * Upload sequence:  6.
 */

@Stateless
public class PosScheduleJob extends StandardJob
{
	private static Logger logger = Logger.getLogger(PosScheduleJob.class.getName());
    private String[] tables = {"POS"};
    private FileContext fileCon = null;
    private List<DataSyncError> errorList;
    @EJB
    private MasterDataSB masterSB;
    @EJB
    private PosSB posSB;
    @EJB
    private ScheduleConfigSB schedConfSB;
  //For schedule job
    @Asynchronous
    @TransactionTimeout(value = 30000, unit = TimeUnit.SECONDS) 
    public void executeTask(Timer timer)
    {
        
        try
        {
        	setType("POS");
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

		logger.log(Level.FINE, "PosScheduleJob loadingAction Method Call......");
		fileCon = new FileContext(tables);
		fileCon.setConfig(initCommonLoadingAndSchedule(tables));
		loadingActionLoadingAndScheduleFileContext(fileCon);

		// re-initial the error record list
		if (errorList != null)
			errorList.clear();
		errorList = new ArrayList<DataSyncError>();

		boolean switcher = schedConfSB.getSwitch("POS");

		if (switcher) {
			try {
				posSB.summarizePos();
			} catch (Exception ex) {
				logger.log(Level.SEVERE,
						"Exception Occoured while summarizing the POS in loadingAction Method :" + ex.getMessage(), ex);
			}
		}

	}
	
	@Override
	public List<Object> transfer(List<String> dataList, Config config, String functionName, String fileName)
			throws CheckedException {
		return transferPosLoadingAndPosSchedule(dataList, config, functionName, fileName, masterSB, errorList);
	}

	
	
	@Override
	public void merge(List<Object>[] objList, String[] fileNameArray, String[] functionNameArray) throws CheckedException
	{
		masterSB.mergePos(objList, fileNameArray,functionNameArray, errorList);
	}


}
