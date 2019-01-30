package com.avnet.emasia.webquote.masterData.ejb.job;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.Timer;

import com.avnet.emasia.webquote.entity.SalesOrg;
import com.avnet.emasia.webquote.exception.CommonConstants;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.masterData.dto.Config;
import com.avnet.emasia.webquote.masterData.dto.FileContext;
import com.avnet.emasia.webquote.masterData.ejb.MasterDataSB;
import com.avnet.emasia.webquote.masterData.ejb.StandardJob;
import com.avnet.emasia.webquote.masterData.ejb.remote.SalesOrgRemote;
import com.avnet.emasia.webquote.masterData.exception.CheckedException;
import com.avnet.emasia.webquote.masterData.util.Constants;
import com.avnet.emasia.webquote.masterData.util.FunctionUtils;
import com.avnet.emasia.webquote.masterData.util.ResourceLoader;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-01-23
 * Upload sequence:  1.
 */

@Stateless
@Remote(SalesOrgRemote.class)
public class SalesOrgLoadingJob extends StandardJob implements SalesOrgRemote
{
	private static Logger logger = Logger.getLogger(SalesOrgLoadingJob.class.getName());
	private String[] tables = { "SALES_ORG" };
	private FileContext fileCon = null;
	@EJB
	private MasterDataSB masterSB;

	// For schedule job
	/* (non-Javadoc)
	 * @see com.avnet.emasia.webquote.masterData.ejb.StandardJob#executeTask(javax.ejb.Timer)
	 */
	@Asynchronous
	public void executeTask(Timer timer) {
		try {
			setType("SALES_ORG");
			run();
		} catch (Exception e) {
			String msg = MessageFormatorUtil.getParameterizedStringFromException(e);
			logger.log(Level.SEVERE, "Exception occured for SalesOrgLoadingJob#Timer: " + timer.getInfo() + ", Task: " + this.getType()
					+ ", Reason for failure: " + msg, e);
		}
	}

	// For control-M
	/* (non-Javadoc)
	 * @see com.avnet.emasia.webquote.masterData.ejb.remote.SalesOrgRemote#executeTask()
	 */
	@Override
	public int executeTask() {
		int returnInt = 1;
		try {
			setType("SALES_ORG");
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
	public void loadingAction() throws CheckedException {
		fileCon = new FileContext(tables);
		fileCon.setConfig(initCommonLoadingAndSchedule(tables));
		loadingActionLoadingAndScheduleFileContext(fileCon);
	}

	/* (non-Javadoc)
	 * @see com.avnet.emasia.webquote.masterData.ejb.StandardJob#transfer(java.util.List, com.avnet.emasia.webquote.masterData.dto.Config, java.lang.String, java.lang.String)
	 */
	@Override
	public List<Object> transfer(List<String> dataList, Config config, String functionName, String fileName)
			throws CheckedException {
		return transferSalesOrg(dataList, config, functionName, fileName);
	}

	/* (non-Javadoc)
	 * @see com.avnet.emasia.webquote.masterData.ejb.StandardJob#merge(java.util.List[], java.lang.String[], java.lang.String[])
	 */
	@Override
	public void merge(List<Object>[] objList, String[] fileNameArray, String[] functionNameArray)
			throws CheckedException {
		masterSB.mergeSalesOrg(objList, fileNameArray, functionNameArray);
	}
	
	@Override
	public boolean isRanOnThisServer(String serviceName) {
		return true;
	}


}
