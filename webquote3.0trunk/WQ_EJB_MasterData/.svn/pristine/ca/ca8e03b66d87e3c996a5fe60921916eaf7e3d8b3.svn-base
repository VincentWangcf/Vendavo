package com.avnet.emasia.webquote.masterData.ejb.scheduleJob;


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
import javax.transaction.SystemException;

import com.avnet.emasia.webquote.entity.SalesOrg;
import com.avnet.emasia.webquote.exception.CommonConstants;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.DataSyncError;
import com.avnet.emasia.webquote.masterData.dao.SalesCostPricerDAO;
import com.avnet.emasia.webquote.masterData.dto.Config;
import com.avnet.emasia.webquote.masterData.dto.FileContext;
import com.avnet.emasia.webquote.masterData.ejb.DataSyncErrorSB;
import com.avnet.emasia.webquote.masterData.ejb.MasterDataSB;
import com.avnet.emasia.webquote.masterData.ejb.StandardJob;
import com.avnet.emasia.webquote.masterData.ejb.remote.SalesOrgRemote;
import com.avnet.emasia.webquote.masterData.exception.CheckedException;
import com.avnet.emasia.webquote.masterData.util.Constants;
import com.avnet.emasia.webquote.masterData.util.FunctionUtils;
import com.avnet.emasia.webquote.masterData.util.ResourceLoader;
import com.avnet.emasia.webquote.utilities.DateUtils;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;

/**
 * @author WingHong, Wong (P02810)
 * @Created on 2017-11-24
 * Upload sequence:  1.
 */

@Stateless
public class SalesCostPricerBalQtyUpdateScheduleJob extends StandardJob
{
	private static Logger logger = Logger.getLogger(SalesCostPricerBalQtyUpdateScheduleJob.class.getName());
	private String[] tables = { "SCP_BAL_QTY" };
	private FileContext fileCon = null;

	@EJB
	private MasterDataSB masterSB;
	
	@EJB
	private DataSyncErrorSB errorSB;

    @EJB
    private SalesCostPricerDAO salesCostPricerDAO;

    // For schedule job
	/* (non-Javadoc)
	 * @see com.avnet.emasia.webquote.masterData.ejb.StandardJob#executeTask(javax.ejb.Timer)
	 */
	@Asynchronous
	public void executeTask(Timer timer) {
		try {
			setType("SCP_BAL_QTY");
			run();
		} catch (Exception e) {
			String msg = MessageFormatorUtil.getParameterizedStringFromException(e);
			logger.log(Level.SEVERE, "Exception occured for SalesOrgScheduleJob#Timer: " + timer.getInfo() + ", Task: " + this.getType()
					+ ", Reason for failure: " + msg, e);
		}
	}

	/* (non-Javadoc)
	 * @see com.avnet.emasia.webquote.masterData.ejb.StandardJob#loadingAction()
	 */
	@Override
	public void loadingAction() throws CheckedException {
		logger.log(Level.FINE, "SalesOrgScheduleJob loadingAction Method Call......");
		fileCon = new FileContext(tables);
		fileCon.setConfig(initCommonLoadingAndSchedule(tables));
		loadingActionLoadingAndScheduleFileContext(fileCon);
	}

	@Override
	public void saveToDB(FileContext fileCon) throws CheckedException {

		logger.info("SalesCostPricerBalQtyUpdateScheduleJob.saveToDB : BEGIN");
		List<String>[][] dataFileList = fileCon.getDataFileList();

		for (int i = 0; i < dataFileList[0].length; i++) {

			List<Object>[] objectList = new List[dataFileList.length];
			String[] fileNameArray = new String[dataFileList.length];
			String[] functionNameArray = new String[dataFileList.length];

			for (int j = 0; j < dataFileList.length; j++) {
				try {
					Config config = fileCon.getConfig()[j];
					
					transferToDB(dataFileList[j][i], config, fileCon.getConfig()[j].getFileName(),
							fileCon.getDataFiles()[j][i].getFile().getName());
//					objectList[j] = transfer(dataFileList[j][i], config, fileCon.getConfig()[j].getFileName(),
//							fileCon.getDataFiles()[j][i].getFile().getName());
					fileNameArray[j] = fileCon.getDataFiles()[j][i].getFile().getName();
					functionNameArray[j] = fileCon.getConfig()[j].getFileName();
				} catch (CheckedException e) {
					DataSyncError error = new DataSyncError();
					error.setFileName(fileCon.getDataFiles()[j][i].getFile().getName());
					error.setErrorRecord("-1");
					error.setFunctionCode(fileCon.getConfig()[j].getFileName());
					error.setCreatingDate(DateUtils.getCurrentAsiaDateObj());
					error.setErrorMessage("[function] : " + fileCon.getConfig()[j].getFileName() + " [fileName] : "
							+ fileCon.getDataFiles()[j][i].getFile().getName()
							+ " The error occurred durring data transferred to object : [detail] : " + e.getMessage());
					errorSB.createErrorMsg(error);
					throw new CheckedException(CommonConstants.WQ_WEB_TRANSFER_EXCPTION + error.getErrorMessage());
				}
			}

			//merge(objectList, fileNameArray, functionNameArray);
		}

		logger.info("SalesCostPricerBalQtyUpdateScheduleJob.saveToDB : END");
	}
	
	private int getFirstRowIndex(List<String> dataList, int nCol, boolean skipHeader) {
		int iOut =0, i=0;
		List<String> rowData = null;
		
		for(i = 0; i<dataList.size(); i++){
			rowData = FunctionUtils.splitLineData((String) dataList.get(i), Constants.SEPARATOR, true);
			if (rowData.size()==nCol) break;
		}
		
		if (skipHeader) iOut = i + 1; else iOut = i;
		
		return iOut;
	}
	
	/* (non-Javadoc)
	 * @see com.avnet.emasia.webquote.masterData.ejb.StandardJob#transfer(java.util.List, com.avnet.emasia.webquote.masterData.dto.Config, java.lang.String, java.lang.String)
	 */
	//@Override
	public void transferToDB(List<String> dataList, Config config, String functionName, String fileName)
			throws CheckedException {
		logger.info("SalesCostPricerBalQtyUpdateScheduleJob.transferToDB : BEGIN {" + functionName + "," + fileName + "}");
		//List<Object> returnList = new ArrayList<Object>();
		//List<String> fields = FunctionUtils.splitLineData((String) dataList.remove(0), Constants.SEPARATOR, true);
		int size = dataList.size();
		List<String> rowData = null;
		int iStartIndex = 0;
		
		iStartIndex = getFirstRowIndex(dataList, 11, true);	//skip invalid row data
		
		for (int i = iStartIndex; i < size; i++) {
			try {
				//0				 |1				|2	   |3				|4				|5					|6			   |7				 |8			  |9					   |10
				//Sales cost type|Sales cost key|Region|Web Quote Number|Customer Number|End Customer Number|Customer Group|Avnet Part Number|Manufacturer|Manufacturer part number|Balanced qty for ZINM pricing type
				//0,1,8,9,10
				//20171128: New format
				//0		 |1		|2  |3  |4		|5	   |6	  |7		  |8	  |9	   |10
				//SC_TYPE|REGION|MFR|MPN|BAL_QTY|SC_KEY|WQ_NUM|SOLDTO_CODE|EC_CODE|CUST_GRP|APN
				//0,1,2,3,4

				rowData = FunctionUtils.splitLineData((String) dataList.get(i), Constants.SEPARATOR, true);
//				Object obj = null;
//				Object fkObj = null;
//				logger.info("SalesCostPricerBalQtyUpdateLoadingJob.transferToDB : DataLine {" + dataList.get(i) + "} " + rowData.get(10));

				String salesCostType = rowData.get(0);
				String bizUnit = rowData.get(1);
				String manufacturerName = rowData.get(2);
				String fullMfrPartNumber = rowData.get(3);
				long qtyControl = Long.parseLong(rowData.get(4), 10);
				
				salesCostPricerDAO.updateQtyControl(salesCostType, bizUnit, manufacturerName, fullMfrPartNumber, qtyControl);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new CheckedException("Method=transfer param=[className={"+config.getClassName()+"}] IllegalStateException[{"+e.getMessage()+"}]");
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new CheckedException("Method=transfer param=[className={"+config.getClassName()+"}] SecurityException[{"+e.getMessage()+"}]");
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new CheckedException("Method=transfer param=[className={"+config.getClassName()+"}] SystemException[{"+e.getMessage()+"}]");
			}
			
		}
		logger.info("SalesCostPricerBalQtyUpdateScheduleJob.transferToDB : END");
		//return returnList;
	}

	/* (non-Javadoc)
	 * @see com.avnet.emasia.webquote.masterData.ejb.StandardJob#merge(java.util.List[], java.lang.String[], java.lang.String[])
	 */
//	@Override
//	public void merge(List<Object>[] objList, String[] fileNameArray, String[] functionNameArray)
//			throws CheckedException {
//		masterSB.mergeSalesOrg(objList, fileNameArray, functionNameArray);
//	}


}
