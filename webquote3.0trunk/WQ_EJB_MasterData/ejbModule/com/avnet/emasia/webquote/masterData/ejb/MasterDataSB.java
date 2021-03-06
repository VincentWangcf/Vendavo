package com.avnet.emasia.webquote.masterData.ejb;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.UserTransaction;

import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.CustomerAddress;
import com.avnet.emasia.webquote.entity.CustomerPartner;
import com.avnet.emasia.webquote.entity.CustomerSale;
import com.avnet.emasia.webquote.entity.DataSyncError;
import com.avnet.emasia.webquote.entity.DrNedaHeader;
import com.avnet.emasia.webquote.entity.DrNedaItem;
import com.avnet.emasia.webquote.entity.DrProjectCustomer;
import com.avnet.emasia.webquote.entity.DrProjectHeader;
import com.avnet.emasia.webquote.entity.FreeStock;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.Pos;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.SalesOrder;
import com.avnet.emasia.webquote.entity.SalesOrg;
import com.avnet.emasia.webquote.entity.SapMaterial;
import com.avnet.emasia.webquote.entity.SapMaterialDetail;
import com.avnet.emasia.webquote.entity.SapPcnMaterial;
import com.avnet.emasia.webquote.entity.util.StringUtils;
import com.avnet.emasia.webquote.exception.CommonConstants;
import com.avnet.emasia.webquote.masterData.exception.CheckedException;
import com.avnet.emasia.webquote.masterData.util.Constants;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.utilities.DateUtils;
import com.avnet.emasia.webquote.utilities.bean.MailInfoBean;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;
import com.avnet.emasia.webquote.utilities.ejb.MailUtilsSB;
import com.avnet.emasia.webquote.utilities.schedule.HATimerService;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class MasterDataSB {

	@Resource
	UserTransaction ut;

	@PersistenceContext(unitName = "Server_Source")
	private EntityManager em;
	@EJB
	private MailUtilsSB mailUtilsSB;
	@EJB
	private ScheduleConfigSB schedConfSB;

	@EJB
	private DataSyncErrorSB errorSB;

	@EJB
	SysConfigSB sysConfigSB;

	private static Logger logger = Logger.getLogger(MasterDataSB.class.getName());
	private final static int BATCH_NUMBER = 1000;
	private final static String FIELD_NAME = "name";
	private final static int SESSION_TIME_OUT = 36000;
	private final static String MATERIAL_TYPE_NORMAL = "NORMAL";

	private final static String MFR_VIS = "VIS";
	private final static String MFR_VIS_IPE = "VIS-IPE";
	private final static String MFR_VIS_SEMI = "VIS-SEMI";
	private final static String SAP_CATEGORY_IPE = "IP&E";
	private final static String BLANK_SPACE = "  ";
	private final static String NEXT_LINE = "\n";

	/*
	 * Sales Organization batch merge action.
	 */
	// For issue 1149, Updated by Tonmy on 12, Nov.
	// it required to update the sales org and bizUnit mapping. only add
	// operation . Not removed operation.
	public void mergeSalesOrg(List<Object>[] list, String[] fileNameAry, String[] funcNameAry) throws CheckedException {
		logger.info("call mergeSalesOrg");
		Query q = em.createQuery("SELECT a FROM SalesOrg a where a.name=:name");
		Query q1 = em.createQuery("SELECT a FROM SalesOrg a ");
		List<DataSyncError> errorList = new ArrayList<DataSyncError>();
		int errorPosition = 0;
		String errorFileName = null;
		String errorFunction = null;
		try {
			ut.setTransactionTimeout(SESSION_TIME_OUT);
			ut.begin();
			List<SalesOrg> fullList = (List<SalesOrg>) q1.getResultList();
			if (fullList != null && fullList.size() > 0) {
				for (int i = 0; i < fullList.size(); i++) {
					SalesOrg saleOrg = (SalesOrg) fullList.get(i);
					saleOrg.setBizUnits(null);
					em.merge(saleOrg);
					em.flush();
					em.clear();
				}
			}
			for (int i = 0; i < list.length; i++) {
				errorPosition = 0;
				List subList = list[i];
				errorFileName = fileNameAry[i];
				errorFunction = funcNameAry[i];
				for (int j = 0; j < subList.size(); j++) {
					Object obj = subList.get(j);
					List<BizUnit> bList = ((SalesOrg) obj).getBizUnits();
					q.setParameter(FIELD_NAME, ((SalesOrg) obj).getName());
					List<SalesOrg> resultList = (List<SalesOrg>) q.getResultList();
					// Check sales organization whether exist in DB.
					if (resultList != null && resultList.size() > 0) {
						SalesOrg tempSalesOrg = (SalesOrg) resultList.get(0);
						((SalesOrg) obj).setCreatedOn(tempSalesOrg.getCreatedOn());
						((SalesOrg) obj).setBizUnits(
								joinBizUnitList(((SalesOrg) obj).getBizUnits(), tempSalesOrg.getBizUnits()));
						errorPosition++;
						em.merge(obj);
						em.flush();
						em.clear();
					} else {
						errorPosition++;
						em.persist(obj);
						em.flush();
						em.clear();

					}

					if (resultList != null)
						resultList.clear();

				}
			}
			em.flush();
			em.clear();
			ut.commit();
		}
    	catch(Exception e)
    	{
    		try {
				errorList = addError(errorList, e.getMessage(), errorFileName, errorFunction, errorPosition);
				ut.rollback();
			}
    	    catch(Exception syex) 
    	    {
				logger.log(Level.SEVERE, "Exception occured in method mergeSalesOrg() on rollback transaction for error file name: "+errorFileName+", Error Function: "+errorFunction+", "
						+ "error position: "+errorPosition+", Reason for failure: "+syex.getMessage(), syex);
			}

			CheckedException cheEx = new CheckedException("[errorFunction:" + errorFunction + "] [errorFileName:"
					+ errorFileName + "] [errorPosition:" + errorPosition + "] [errorMsg: " + e.getMessage() + "]");
			throw cheEx;

		} finally {
			try {
				ut.begin();
				errorSB.persistError(errorList);
				ut.commit();
			} 
    		catch(Exception e)
    		{
				/*logger.log(Level.SEVERE, e.getMessage(), e);*/
				CheckedException cheEx = new CheckedException("[ error message insert failed ]");
				throw cheEx;
			}
		}
	}

	/*
	 * Sales Order batch merge action.
	 */
	public void mergeSalesOrder(List<Object>[] list, String[] fileNameAry, String[] funcNameAry)
			throws CheckedException {
		logger.info("call mergeSalesOrder");
		Query q = em.createQuery("SELECT a FROM QuoteItem a where a.quoteNumber=:quoteNumber");
		List<DataSyncError> errorList = new ArrayList<DataSyncError>();
		int errorPosition = 0;
		String errorFileName = null;
		String errorFunction = null;
		try {
			ut.setTransactionTimeout(SESSION_TIME_OUT);
			ut.begin();
			for (int i = 0; i < list.length; i++) {
				errorPosition = 0;
				List subList = list[i];
				errorFileName = fileNameAry[i];
				errorFunction = funcNameAry[i];
				for (int j = 0; j < subList.size(); j++) {
					Object obj = subList.get(j);
					errorPosition++;
					if (obj instanceof SalesOrder) {
						SalesOrder tempObj = (SalesOrder) obj;
						q.setParameter("quoteNumber", ((SalesOrder) obj).getId().getQuoteNumber());
						List<QuoteItem> listQuoteItem = (List<QuoteItem>) q.getResultList();
						if (listQuoteItem != null && listQuoteItem.size() > 0) {
							((SalesOrder) obj).setQuoteItem(listQuoteItem.get(0));
							if (Constants.OPERATION_INDICATOR_UPDATE.equals(tempObj.getIndicator())) {
								em.merge(tempObj);
								em.flush();
								em.clear();
								// em.merge(tempObj);
								// if(j%BATCH_NUMBER==0)
								// {
								// em.flush();
								// em.clear();
								// }
							} else if (Constants.OPERATION_INDICATOR_DELETE.equals(tempObj.getIndicator())) {
								logger.info("delete action" + ((SalesOrder) obj).getId().getQuoteNumber() + "|"
										+ ((SalesOrder) obj).getId().getSalesOrderItemNumber() + "|"
										+ ((SalesOrder) obj).getId().getSalesOrderNumber());
								SalesOrder tempSalesOrder = (SalesOrder) em.find(SalesOrder.class,
										((SalesOrder) obj).getId());
								// Fixed the sales order removed failed issue .
								if (tempSalesOrder != null) {
									em.remove(tempSalesOrder);
									em.flush();
									em.clear();
									// em.remove(tempSalesOrder);
									// if(j%BATCH_NUMBER==0)
									// {
									// em.flush();
									// em.clear();
									// }
								}

							}
						} else {
							logger.info("Can't find the QuoteItem :" + ((SalesOrder) obj).getId().getQuoteNumber());
							// According Timothy 's request . Will skip the
							// record if can't find the quote number. 20130701.
							// throw new CheckedException("Can't find the
							// QuoteItem :"+
							// ((SalesOrder)obj).getId().getQuoteNumber());
						}

						tempObj = null;

					}

				}
				if (subList != null)
					subList.clear();
			}
			em.flush();
			em.clear();
			ut.commit();
		}
    	catch(Exception e)
    	{
    		try {
				errorList = addError(errorList, e.getMessage(), errorFileName, errorFunction, errorPosition);
				ut.rollback();
			} catch (Exception syex) {
				logger.log(Level.SEVERE, "Exception occured on rollback for error file name: "+errorFileName+", Error Function: "+errorFunction+", "
						+ "error position: "+errorPosition+", Reason for failure: "+syex.getMessage(), syex);
			}

			CheckedException cheEx = new CheckedException("[errorFunction:" + errorFunction + "] [errorFileName:"
					+ errorFileName + "] [errorPosition:" + errorPosition + "] [errorMsg: " + e.getMessage() + "]");
			throw cheEx;

		} finally {
			try {
				ut.begin();
				errorSB.persistError(errorList);
				ut.commit();
			}
    		catch(Exception e)
    		{
				/*logger.log(Level.SEVERE, e.getMessage(), e);*/
				CheckedException cheEx = new CheckedException("[ error message insert failed ]");
				throw cheEx;
			}
		}
	}

	/*
	 * Manufacturer data merge action
	 */
	public void mergeMfr(List<Object>[] list, String[] fileNameAry, String[] funcNameAry) throws CheckedException {
		logger.info("call mergeMfr");
		List<DataSyncError> errorList = new ArrayList<DataSyncError>();
		int errorPosition = 0;
		Query q = em.createQuery("SELECT a FROM Manufacturer a where a.name=:name");
		String errorFileName = null;
		String errorFunction = null;
		try {
			ut.setTransactionTimeout(SESSION_TIME_OUT);
			ut.begin();
			for (int i = 0; i < list.length; i++) {
				errorPosition = 0;
				List subList = list[i];
				errorFileName = fileNameAry[i];
				errorFunction = funcNameAry[i];
				for (int j = 0; j < subList.size(); j++) {
					Object obj = subList.get(j);
					errorPosition++;
					if (obj instanceof Manufacturer) {
						Manufacturer tempObj = (Manufacturer) obj;
						q.setParameter(FIELD_NAME, tempObj.getName());
						List<Manufacturer> resultListObj = (List<Manufacturer>) q.getResultList();
						if (resultListObj != null && resultListObj.size() > 0) {
							Manufacturer tempM = (Manufacturer) resultListObj.get(0);
							tempM.setDescription(((Manufacturer) tempObj).getDescription());
							em.merge(tempM);
							if (j % BATCH_NUMBER == 0) {
								em.flush();
								em.clear();
							}
						} else {
							em.persist(tempObj);
							if (j % BATCH_NUMBER == 0) {
								em.flush();
								em.clear();
							}

						}
						if (resultListObj != null)
							resultListObj.clear();

					}

				}

			}
			em.flush();
			em.clear();
			ut.commit();
		} 
    	catch(Exception e)
    	{
    		try {
				errorList = addError(errorList, e.getMessage(), errorFileName, errorFunction, errorPosition);
				ut.rollback();
			}
    	    catch(Exception syex) 
    	    {
    	    	logger.log(Level.SEVERE, "Exception occured in method mergeMfr() on rollback transaction for error file name: "+errorFileName+", Error Function: "+errorFunction+", "
						+ "error position: "+errorPosition+", Reason for failure: "+syex.getMessage(), syex);
			}

			CheckedException cheEx = new CheckedException("[errorFunction:" + errorFunction + "] [errorFileName:"
					+ errorFileName + "] [errorPosition:" + errorPosition + "] [errorMsg: " + e.getMessage() + "]");
			throw cheEx;

		} finally {
			try {
				ut.begin();
				errorSB.persistError(errorList);
				ut.commit();
			}
    		catch(Exception e)
    		{
				/*logger.log(Level.SEVERE, e.getMessage(), e);*/
				CheckedException cheEx = new CheckedException("[ error message insert failed ]");
				throw cheEx;
			}
		}
	}

	/*
	 * Customer batch merge action.
	 */
	public void mergeCustomer(List<Object>[] list, String[] fileNameAry, String[] funcNameAry) throws CheckedException {
		logger.info("call mergeCustomer");
		List<DataSyncError> errorList = new ArrayList<DataSyncError>();
		Query q = em.createQuery("SELECT a FROM Customer a where a.addressNumber=:addressNumber");
		int errorPosition = 0;
		String errorFileName = null;
		String errorFunction = null;
		boolean isFirstCustomerAddress = false;
		boolean isFirstCustomerPartner = false;
		boolean isFirstCustomerSale = false;
		try {
			ut.setTransactionTimeout(36000);
			ut.begin();
			for (int i = 0; i < list.length; i++) {

				errorPosition = 0;
				List subList = list[i];
				errorFileName = fileNameAry[i];
				errorFunction = funcNameAry[i];
				for (int j = 0; j < subList.size(); j++) {
					Object obj = subList.get(j);
					errorPosition++;
					if (obj instanceof Customer) {
						String customerNum = ((Customer) obj).getCustomerNumber();

						Customer tempCus = em.find(Customer.class, customerNum);
						if (tempCus == null) {
							((Customer) obj).setNewCustomerFlag(true);
						} else {
							((Customer) obj).setNewCustomerFlag(false);
							((Customer) obj).setCreatedOn(tempCus.getCreatedOn());
						}

					} else if (obj instanceof CustomerAddress) {

						if (isFirstCustomerAddress == false) {
							em.flush();
							em.clear();
							isFirstCustomerAddress = true;
						}

						CustomerAddress tempObj = (CustomerAddress) obj;
						q.setParameter("addressNumber", tempObj.getCustomerAddress());
						List<Customer> cusList = (List<Customer>) q.getResultList();
						if (cusList != null && cusList.size() > 0) {
							((CustomerAddress) obj).getId().setCustomerNumber(cusList.get(0).getCustomerNumber());
						} else {
							logger.info("Not found address Number: " + tempObj.getCustomerAddress());
							throw new CheckedException(CommonConstants.WQ_EJB_MASTER_DATA_CANT_FIND_ADDRESS_NUMBER
									+ tempObj.getCustomerAddress());
						}
					} else if (obj instanceof CustomerSale) {
						if (isFirstCustomerSale == false) {
							em.flush();
							em.clear();
							isFirstCustomerSale = true;
						}

					} else if (obj instanceof CustomerPartner) {
						if (isFirstCustomerPartner == false) {
							em.flush();
							em.clear();
							isFirstCustomerPartner = true;
						}

					}

					em.merge(obj);
					if (j % BATCH_NUMBER == 0) {
						em.flush();
						em.clear();
					}
				}

				// Flush
				em.flush();
				em.clear();

			}
			em.flush();
			em.clear();
			ut.commit();
		}
    	catch(Exception e)
    	{
    		  //added by damonchen@20181129
    		e.printStackTrace();
    		try {
				errorList = addError(errorList, e.getMessage(), errorFileName, errorFunction, errorPosition);
				ut.rollback();
			}
    	    catch(Exception syex) 
    	    {
    	    	  //added by damonchen@20181129
    	    	syex.printStackTrace();
    	    	logger.log(Level.SEVERE, "Exception occured in method MasterDataSB#mergeCustomer() for error file name: "+errorFileName+", Error Function: "+errorFunction+", "
						+ "error position: "+errorPosition+", Reason for failure: "+syex.getMessage(), syex);
			}

			CheckedException cheEx = new CheckedException("[errorFunction:" + errorFunction + "] [errorFileName:"
					+ errorFileName + "] [errorPosition:" + errorPosition + "] [errorMsg: " + e.getMessage() + "]");
			throw cheEx;

		} finally {
			try {
				ut.begin();
				errorSB.persistError(errorList);
				ut.commit();
			}
    		catch(Exception e)
    		{
    			e.printStackTrace();
				CheckedException cheEx = new CheckedException("[ error message insert failed ]");
				throw cheEx;
			}
		}
	}

	/*
	 * Free Stock batch merge action. 1, Removed all the data first. and then
	 * insert data.
	 */
	public void mergeFreeStock(List<Object>[] list, String[] fileNameAry, String[] funcNameAry,
			List<DataSyncError> errorList) throws CheckedException {

    	logger.info("call mergeFreeStock");
    	
    	//List<DataSyncError> errorList = new ArrayList<DataSyncError>();
    	int errorPosition = 0 ; 
    	String errorFileName = null;
    	String errorFunction = null;
    	try
    	{
    		ut.setTransactionTimeout(SESSION_TIME_OUT);
    		ut.begin();
    		Query q1 = em.createQuery("DELETE FROM FreeStock");
        	Query q2 = em.createQuery("SELECT a FROM SapMaterial a where a.sapPartNumber=:sapPartNumber");
    		q1.executeUpdate();
			for (int i = 0; i < list.length; i++) {
				errorPosition = 0;
				List subList = list[i];
				errorFileName = fileNameAry[i];
				errorFunction = funcNameAry[i];
				for (int j = 0; j < subList.size(); j++) {
					Object obj = subList.get(j);
					errorPosition++;
					q2.setParameter("sapPartNumber", ((FreeStock) obj).getId().getSapPartNumber());
					// for UAT only. skip the record if can't find the related
					// material,
					List<SapMaterial> resultObjList = (List<SapMaterial>) q2.getResultList();
					if (resultObjList != null && resultObjList.size() > 0) {
						SapMaterial resultObj = (SapMaterial) resultObjList.get(0);
						((FreeStock) obj).setMaterial(resultObj.getMaterial());
						em.persist(obj);
						if (j % BATCH_NUMBER == 0) {
							em.flush();
						}

					} else {
						errorList = addError(errorList,
								"|Can't find the material|" + ((FreeStock) obj).getId().getSapPartNumber() + "|"
										+ ((FreeStock) obj).getOriginalSapData(),
								errorFileName, errorFunction, errorPosition);
						logger.info("|Can't find the material|" + ((FreeStock) obj).getId().getSapPartNumber());
					}
					
				}
			}
			em.flush();
			em.clear();
			ut.commit();
			logger.log(Level.FINE, "Deleted records from FreeStock");
		}
    	catch(Exception e)
    	{
    		try {
				errorList = addError(errorList, e.getMessage(), errorFileName, errorFunction, errorPosition);
				ut.rollback();
			}
    	    catch(Exception syex) 
    	    {
    	    	logger.log(Level.SEVERE, "Exception occured in method mergeFreeStock() for error file name: "+errorFileName+", Error Function: "+errorFunction+", "
						+ "error position: "+errorPosition+", Reason for failure: "+syex.getMessage(), syex);
			}

			CheckedException cheEx = new CheckedException("[errorFunction:" + errorFunction + "] [errorFileName:"
					+ errorFileName + "] [errorPosition:" + errorPosition + "] [errorMsg: " + e.getMessage() + "]");
			throw cheEx;

		} finally {
			try {
				ut.begin();
				errorSB.persistError(errorList);
				ut.commit();
				sendErrorNotificationEmail(errorList);
			}
    		catch(Exception e)
    		{
    			/*logger.log(Level.SEVERE, e.getMessage(), e);*/
				CheckedException cheEx = new CheckedException("[ error message insert failed ]");
				throw cheEx;
			}
		}
	}

	/*
	 * Material batch merge action.
	 */
	public void mergeMaterial(List<Object>[] aList, String[] fileNameAry, String[] funcNameAry)
			throws CheckedException {
		logger.info("call mergeMaterial");
		Query q = em.createQuery("SELECT a FROM Manufacturer a where a.name=:name");
		Query q2 = em.createQuery("SELECT a FROM SapMaterial a where a.sapPartNumber=:sapPartNumber");
		Query q3 = em
				.createQuery("SELECT a FROM SapMaterialDetail a where a.material=:material and a.bizUnitBean=:bizUnit");
		Query q4 = em.createQuery(
				"SELECT a FROM Material a where a.manufacturer.name=:mfrName and a.fullMfrPartNumber=:fullMfrPartNumber");
		Query q5 = em.createQuery("SELECT a FROM SapPcnMaterial a where a.sapPartNumber=:paramMATNR");
		List<DataSyncError> errorList = new ArrayList<DataSyncError>();
		int errorPosition = 0;
		String errorFileName = null;
		String errorFunction = null;
		boolean isFirstMaterialDetialObj = false;
		try {
			ut.setTransactionTimeout(SESSION_TIME_OUT);
			ut.begin();

			// q.setParameter(FIELD_NAME, "BLANK");
			// Manufacturer blankMfr = (Manufacturer)q.getSingleResult();
			for (int i = 0; i < aList.length; i++) {
				errorPosition = 0;
				List subList = aList[i];
				errorFileName = fileNameAry[i];
				errorFunction = funcNameAry[i];
				for (int j = 0; j < subList.size(); j++) {
					Object obj = subList.get(j);
					errorPosition++;
					if (obj instanceof SapMaterial) {
						boolean isGo = true;

						/*
						 * =================================added for
						 * sap_pcn_maaterial by damon@20161219 begin
						 * ================================
						 */

						q5.setParameter("paramMATNR", ((SapMaterial) obj).getSapPartNumber());
						List<SapPcnMaterial> resultSapPcnMaterialList = (List<SapPcnMaterial>) q5.getResultList();
						if (resultSapPcnMaterialList != null && resultSapPcnMaterialList.size() > 0) {
							SapPcnMaterial resultPcnMaterial = resultSapPcnMaterialList.get(0);
							resultPcnMaterial.setMFRPN(((SapMaterial) obj).getPartNumber());
							if (((SapMaterial) obj).getDeletionFlag()) {
								resultPcnMaterial.setLVORM("X");
							} else {
								resultPcnMaterial.setLVORM(null);
							}
							resultPcnMaterial.setMAKTX(((SapMaterial) obj).getDescription());
							resultPcnMaterial.setMSTAV(((SapMaterial) obj).getMstav());
							resultPcnMaterial.setMTART(((SapMaterial) obj).getMtart());
							resultPcnMaterial.setPRDHA(((SapMaterial) obj).getPraha());
							resultPcnMaterial.setZZCOMM(((SapMaterial) obj).getCommissionGroup());
							resultPcnMaterial.setZZCMCKEY(((SapMaterial) obj).getSpecialMaterial());
							resultPcnMaterial.setZZPRODCAT(((SapMaterial) obj).getSapProductCategory());
							resultPcnMaterial.setMFRNR(((SapMaterial) obj).getMfr());
							Date cal = DateUtils.getCurrentAsiaDateObj();
							resultPcnMaterial.setUpdatedDate(cal);
							resultPcnMaterial.setUpdatedBy("999999");
							em.merge(resultPcnMaterial);
						} else {

							SapPcnMaterial resultPcnMaterial = new SapPcnMaterial();
							resultPcnMaterial.setSapPartNumber(((SapMaterial) obj).getSapPartNumber());
							resultPcnMaterial.setMFRPN(((SapMaterial) obj).getPartNumber());
							if (((SapMaterial) obj).getDeletionFlag()) {
								resultPcnMaterial.setLVORM("X");
							}

							resultPcnMaterial.setMAKTX(((SapMaterial) obj).getDescription());
							resultPcnMaterial.setMSTAV(((SapMaterial) obj).getMstav());
							resultPcnMaterial.setMTART(((SapMaterial) obj).getMtart());
							resultPcnMaterial.setPRDHA(((SapMaterial) obj).getPraha());
							resultPcnMaterial.setZZCOMM(((SapMaterial) obj).getCommissionGroup());
							resultPcnMaterial.setZZCMCKEY(((SapMaterial) obj).getSpecialMaterial());
							resultPcnMaterial.setZZPRODCAT(((SapMaterial) obj).getSapProductCategory());
							resultPcnMaterial.setMFRNR(((SapMaterial) obj).getMfr());
							Date cal = DateUtils.getCurrentAsiaDateObj();
							resultPcnMaterial.setCreatedDate(cal);
							resultPcnMaterial.setCreatedBy("999999");
							resultPcnMaterial.setUpdatedDate(cal);
							resultPcnMaterial.setUpdatedBy("999999");
							em.persist(resultPcnMaterial);
						}

						/*
						 * =================================add for
						 * sap_pcn_maaterial by damon@20161219 end
						 * ================================
						 */

						// No need mfr checking . import the mfr directly ,
						// which confirmed with Timothy.
						String tempMfr = ((SapMaterial) obj).getMfr();
						tempMfr = mfrChange(tempMfr, ((SapMaterial) obj).getSapProductCategory());
						if (tempMfr == null || StringUtils.isEmpty(tempMfr)) {
							logger.info("Null MFR sap part:" + ((SapMaterial) obj).getSapPartNumber());
							isGo = false;
						}

						if (isGo) {

							q2.setParameter("sapPartNumber", ((SapMaterial) obj).getSapPartNumber());
							List<SapMaterial> resultMaterialList = (List<SapMaterial>) q2.getResultList();
							if (resultMaterialList != null && resultMaterialList.size() > 0) {
								SapMaterial resultMaterial = resultMaterialList.get(0);
								if (!((SapMaterial) obj).getDeletionFlag()) {
									resultMaterial.setDeletionFlag(((SapMaterial) obj).getDeletionFlag());
									resultMaterial.setSapProductCategory(((SapMaterial) obj).getSapProductCategory());
									// resultMaterial.setCreateDate(((SapMaterial)obj).getCreateDate());
									resultMaterial.setUpdateDate(((SapMaterial) obj).getUpdateDate());

								} else {
									resultMaterial.setDeletionFlag(((SapMaterial) obj).getDeletionFlag());
									resultMaterial.setUpdateDate(((SapMaterial) obj).getUpdateDate());
									// resultMaterial.setCreateDate(((SapMaterial)obj).getCreateDate());
								}

								// added by damon@20161224
								resultMaterial.setDescription(((SapMaterial) obj).getDescription());

								//Material tempMaterial = resultMaterial.getMaterial();
/*								if (tempMaterial != null) {
									tempMaterial.setSpecialMaterial(((SapMaterial) obj).getSpecialMaterial());
									// INC0177869
									tempMaterial.setSapPartNumber(((SapMaterial) obj).getSapPartNumber());
									tempMaterial.setLastUpdatedOn(new Date());
									em.merge(tempMaterial);
								} else {*/
									// fixed ticket INC0177869
								//Fixed the issue that SAP P/N  is not linked to correct P/N bu
									q4.setParameter("mfrName", ((SapMaterial) obj).getMfr());
									q4.setParameter("fullMfrPartNumber", ((SapMaterial) obj).getPartNumber());
									List<Material> tempMaterials = (List<Material>) q4.getResultList();
									Material tempMaterial = null;
									if (tempMaterials != null && tempMaterials.size() > 0) {
										tempMaterial = tempMaterials.get(0);
									}

									if (tempMaterial != null) {
										tempMaterial.setHasSapFlag(true);
										if (StringUtils.isEmpty(tempMaterial.getSapPartNumber())) {
											tempMaterial.setSapPartNumber(((SapMaterial) obj).getSapPartNumber());
										}
										tempMaterial.setSpecialMaterial(((SapMaterial) obj).getSpecialMaterial());
										em.merge(tempMaterial);
										//need set the mapping by damon
										resultMaterial.setMaterial(tempMaterial);
									} else {
										q.setParameter(FIELD_NAME, ((SapMaterial) obj).getMfr());
										
										// Added by Punit for CPD Exercise
										tempMaterial = mergeManufaturer(obj, tempMaterial, em, q);
										
										tempMaterial.setSapPartNumber(resultMaterial.getSapPartNumber());
										tempMaterial.setSpecialMaterial(resultMaterial.getSpecialMaterial());
										tempMaterial.setHasSapFlag(true);
										tempMaterial.setValid(true);
										em.persist(tempMaterial);
										em.flush();
										resultMaterial.setMaterial(tempMaterial);
									}
								//}
								em.merge(resultMaterial);
								if (j % BATCH_NUMBER == 0) {
									em.flush();
									em.clear();
								}
							} else {

								q4.setParameter("mfrName", ((SapMaterial) obj).getMfr());
								q4.setParameter("fullMfrPartNumber", ((SapMaterial) obj).getPartNumber());
								List<Material> tempMaterials = (List<Material>) q4.getResultList();
								Material tempMaterial = null;
								if (tempMaterials != null && tempMaterials.size() > 0) {
									tempMaterial = tempMaterials.get(0);
								}

								if (tempMaterial != null) {
									tempMaterial.setHasSapFlag(true);
									if (StringUtils.isEmpty(tempMaterial.getSapPartNumber())) {
										tempMaterial.setSapPartNumber(((SapMaterial) obj).getSapPartNumber());
									}
									tempMaterial.setSpecialMaterial(((SapMaterial) obj).getSpecialMaterial());
									em.merge(tempMaterial);
								} else {
									q.setParameter(FIELD_NAME, ((SapMaterial) obj).getMfr());
									
									// Added by Punit for CPD Exercise
									tempMaterial = mergeManufaturer(obj, tempMaterial, em, q);
									tempMaterial.setSapPartNumber(((SapMaterial) obj).getSapPartNumber());
									tempMaterial.setSpecialMaterial(((SapMaterial) obj).getSpecialMaterial());
									tempMaterial.setHasSapFlag(true);
									tempMaterial.setValid(true);
									em.persist(tempMaterial);
									em.flush();
									((SapMaterial) obj).setMaterial(tempMaterial);
								}

								((SapMaterial) obj).setMaterial(tempMaterial);

								em.persist(obj);
								if (j % BATCH_NUMBER == 0) {
									em.flush();
									em.clear();
								}
							}

							if (null != resultMaterialList)
								resultMaterialList.clear();
						}
					} else {
						if (isFirstMaterialDetialObj == false) {
							em.flush();
							em.clear();
							isFirstMaterialDetialObj = true;
						}
						SapMaterialDetail tempMd = (SapMaterialDetail) obj;
						q2.setParameter("sapPartNumber", tempMd.getMaterial().getSapPartNumber());
						List<Material> resultObjList = (List<Material>) q2.getResultList();
						if (resultObjList != null && resultObjList.size() > 0) {
							Material resultObj = resultObjList.get(0);
							((SapMaterialDetail) obj).setMaterial(resultObj);

							if ((((SapMaterialDetail) obj).getSalesOrgBean().getName() == null
									|| ((SapMaterialDetail) obj).getSalesOrgBean().getName().isEmpty())
									&& (((SapMaterialDetail) obj).getPlant() == null
											|| ((SapMaterialDetail) obj).getPlant().isEmpty())) {
								// No need to save
							} else {

								if (((SapMaterialDetail) obj).getSalesOrgBean().getName() == null
										|| ((SapMaterialDetail) obj).getSalesOrgBean().getName().isEmpty()) {
									((SapMaterialDetail) obj).setSalesOrgBean(null);
								}
								q3.setParameter("material", ((SapMaterialDetail) obj).getMaterial());
								q3.setParameter("bizUnit", ((SapMaterialDetail) obj).getBizUnitBean());
								List<SapMaterialDetail> resultList = (List<SapMaterialDetail>) q3.getResultList();
								if (null != resultList && resultList.size() > 0) {
									SapMaterialDetail md = resultList.get(0);
									((SapMaterialDetail) obj).setId(md.getId());
									((SapMaterialDetail) obj).setCreatedOn(md.getCreatedOn());
									em.merge(obj);
									if (j % BATCH_NUMBER == 0) {
										em.flush();
										em.clear();
									}
								} else {
									em.persist(obj);
									if (j % BATCH_NUMBER == 0) {
										em.flush();
										em.clear();
									}
								}

								// clear list.
								if (null != resultList)
									resultList.clear();
							}
							if (null != resultObjList)
								resultObjList.clear();
						} else {
							logger.info("[Material] Can't find sap part : " + tempMd.getMaterial().getSapPartNumber());
						}

					}

				}
			}
			em.flush();
			em.clear();
			ut.commit();
		}
    	catch(Exception e)
    	{
    		try {
				errorList = addError(errorList, e.getMessage(), errorFileName, errorFunction, errorPosition);
				ut.rollback();
			}
    	    catch(Exception syex) 
    	    {
    	    	logger.log(Level.SEVERE, "Exception occured in method mergeMaterial() on rollback transaction for error file name: "+errorFileName+", Error Function: "+errorFunction+", "
						+ "error position: "+errorPosition+", Reason for failure: "+syex.getMessage(), syex);
			}

			CheckedException cheEx = new CheckedException("[errorFunction:" + errorFunction + "] [errorFileName:"
					+ errorFileName + "] [errorPosition:" + errorPosition + "] [errorMsg: " + e.getMessage() + "]");
			throw cheEx;

		} finally {
			try {
				ut.begin();
				errorSB.persistError(errorList);
				ut.commit();
			}
    		catch(Exception e)
    		{
				CheckedException cheEx = new CheckedException("[ error message insert failed ]");
				throw cheEx;
			}
		}
	}

	/*
	 * Project Head batch merge action. Restructure the DRMS loading logic,
	 * Changed by Tonmy on 6 Aug. 1,Caught the foreign key miss issue records.
	 * 2,Caught the numeric format issue records
	 */
	public void mergeDrms(List<Object>[] list, String[] fileNameAry, String[] funcNameAry,
			List<DataSyncError> errorList) throws CheckedException {
		logger.info("call mergeDrms");
		// List<DataSyncError> errorList = new ArrayList<DataSyncError>();
		Query q = em.createQuery("SELECT a FROM Customer a where a.customerNumber=:customerNumber");
		Query q2 = em.createQuery("SELECT a FROM SalesOrg a where a.name=:name");
		Query q3 = em.createQuery("SELECT a FROM DrProjectHeader a where a.projectId=:projectId");
		Query q4 = em
				.createQuery("SELECT a FROM DrNedaHeader a where a.id.projectId=:projectId and a.id.nedaId=:nedaId");
		int errorPosition = 0;
		String errorFileName = null;
		String errorFunction = null;
		final int BATCH_NUMBER_FOR_DRMS = 100;
		boolean isFirstProjectCustomer = false;
		boolean isFirstNedaHeader = false;
		boolean isFirstNedaItem = false;

		try {
			ut.setTransactionTimeout(SESSION_TIME_OUT);
			ut.begin();
			for (int i = 0; i < list.length; i++) {
				errorPosition = 0;
				List subList = list[i];
				errorFileName = fileNameAry[i];
				errorFunction = funcNameAry[i];
				for (int j = 0; j < subList.size(); j++) {
					Object obj = subList.get(j);
					errorPosition++;
					if (obj instanceof DrProjectHeader) {
						DrProjectHeader tempObj = (DrProjectHeader) obj;
						if (tempObj.getSalesOrgBean() != null
								&& !StringUtils.isEmpty(tempObj.getSalesOrgBean().getName())) {
							q2.setParameter("name", tempObj.getSalesOrgBean().getName());
							List<SalesOrg> salesOrgResultList = (List<SalesOrg>) q2.getResultList();
							if (salesOrgResultList.isEmpty()) {
								em.merge(tempObj.getSalesOrgBean());
							}
						}

						em.merge(obj);
						if (j % BATCH_NUMBER_FOR_DRMS == 0) {
							em.flush();
							em.clear();
						}

					} else if (obj instanceof DrProjectCustomer) {

						if (isFirstProjectCustomer == false) {
							em.flush();
							em.clear();
							isFirstProjectCustomer = true;
						}
						DrProjectCustomer tempObj = (DrProjectCustomer) obj;
						// Walter required to skip the project customer , which
						// 's sales org don't exist in DB.
						boolean salesOrgExist = true;
						if (tempObj.getId() != null) {
							if (!StringUtils.isEmpty(tempObj.getId().getSalesOrg())) {
								q2.setParameter("name", tempObj.getId().getSalesOrg());
								List<SalesOrg> salesOrgResultList = (List<SalesOrg>) q2.getResultList();
								if (salesOrgResultList != null) {
									if (salesOrgResultList.size() < 1) {
										salesOrgExist = false;
									}
								} else {
									salesOrgExist = false;
								}

								if (salesOrgResultList != null)
									salesOrgResultList.clear();
							}

						}

						if (salesOrgExist) {
							// logger.info("salesOrgExist is true");

							q.setParameter("customerNumber", tempObj.getId().getCustomerNumber());
							List<Customer> resultList = (List<Customer>) q.getResultList();
							if (resultList != null && resultList.size() > 0) {
								((DrProjectCustomer) obj).setCustomer(resultList.get(0));
								if (resultList != null)
									resultList.clear();

								q3.setParameter("projectId", tempObj.getId().getProjectId());
								List<DrProjectHeader> drResultList = (List<DrProjectHeader>) q3.getResultList();
								if (drResultList != null && drResultList.size() > 0) {
									em.merge(obj);
									if (j % BATCH_NUMBER_FOR_DRMS == 0) {
										em.flush();
										em.clear();
									}

									if (drResultList != null)
										drResultList.clear();
								} else {
									logger.info("Can't find the project :" + tempObj.getId().getProjectId());
									errorList = addError(errorList,
											"|Can't find the project|" + tempObj.getId().getProjectId() + "|"
													+ tempObj.getOriginalSapData(),
											errorFileName, errorFunction, errorPosition);
								}

							} else {
								logger.info("Can't find the customer :" + tempObj.getId().getCustomerNumber());
								// throw new CheckedException("Can't find the
								// customer :"+
								// tempObj.getId().getCustomerNumber());
								errorList = addError(errorList,
										"|Can't find the customer|" + tempObj.getId().getCustomerNumber() + "|"
												+ tempObj.getOriginalSapData(),
										errorFileName, errorFunction, errorPosition);
							}

						} else {
							logger.info("Can't find the related salse org : " + tempObj.getId().getSalesOrg());
							errorList = addError(errorList,
									"|Can't find the sales Org|" + tempObj.getId().getSalesOrg() + "|"
											+ tempObj.getOriginalSapData(),
									errorFileName, errorFunction, errorPosition);
						}

					} else if (obj instanceof DrNedaHeader) {

						if (isFirstNedaHeader == false) {
							em.flush();
							em.clear();
							isFirstNedaHeader = true;
						}

						DrNedaHeader tempObj = (DrNedaHeader) obj;
						q3.setParameter("projectId", tempObj.getId().getProjectId());
						List<DrProjectHeader> drResultList = (List<DrProjectHeader>) q3.getResultList();
						if (drResultList != null && drResultList.size() > 0) {
							em.merge(obj);
							if (j % BATCH_NUMBER_FOR_DRMS == 0) {
								em.flush();
								em.clear();
							}

							if (drResultList != null)
								drResultList.clear();
						} else {
							logger.info("Can't find the project :" + tempObj.getId().getProjectId());
							errorList = addError(errorList,
									"|Can't find the project|" + tempObj.getId().getProjectId() + "|"
											+ tempObj.getOriginalSapData(),
									errorFileName, errorFunction, errorPosition);
						}

					} else if (obj instanceof DrNedaItem) {
						if (isFirstNedaItem == false) {
							em.flush();
							em.clear();
							isFirstNedaItem = true;
						}
						DrNedaItem tempObj = (DrNedaItem) obj;
						q4.setParameter("projectId", tempObj.getId().getProjectId());
						q4.setParameter("nedaId", tempObj.getId().getNedaId());
						List<DrNedaHeader> drNedaResultList = (List<DrNedaHeader>) q4.getResultList();
						if (drNedaResultList != null && drNedaResultList.size() > 0) {
							em.merge(obj);
							if (j % BATCH_NUMBER_FOR_DRMS == 0) {
								em.flush();
								em.clear();
							}

							if (drNedaResultList != null)
								drNedaResultList.clear();
						} else {
							logger.info("Can't find the Neda Header :" + tempObj.getId().getProjectId() + " "
									+ tempObj.getId().getNedaId());
							// throw new CheckedException("Can't find the
							// customer :"+
							// tempObj.getId().getCustomerNumber());
							errorList = addError(errorList,
									"|Can't find the Neda Header|" + tempObj.getId().getProjectId() + " "
											+ tempObj.getId().getNedaId() + "|" + tempObj.getOriginalSapData(),
									errorFileName, errorFunction, errorPosition);
						}
					}

				}
				// Start other type of object.
				em.flush();
				em.clear();
			}
			em.flush();
			em.clear();
			ut.commit();
		}
    	catch(Exception e)
    	{
    		String errorMsg = "";
			try {
				errorMsg = e.getMessage();
				if (errorMsg == null || "".equalsIgnoreCase(errorMsg))
					errorMsg = "exception";
				errorList = addError(errorList, errorMsg, errorFileName, errorFunction, errorPosition);
				ut.rollback();
			}
    	    catch(Exception syex) 
    	    {
    	    	logger.log(Level.SEVERE, "Exception occured for error file name: "+errorFileName+", Error Function: "+errorFunction+", "
						+ "error position: "+errorPosition+", Reason for failure: "+syex.getMessage(), syex);
			}

			CheckedException cheEx = new CheckedException("[errorFunction:" + errorFunction + "] [errorFileName:"
					+ errorFileName + "] [errorPosition:" + errorPosition + "] [errorMsg: " + errorMsg + "]");
			throw cheEx;

		} finally {
			try {
				ut.begin();
				errorSB.persistError(errorList);
				ut.commit();
				sendErrorNotificationEmail(errorList);
				//Fixed by Damonchen@20180511
				errorList.clear();
			}
    		catch(Exception e)
    		{
    			CheckedException cheEx = new CheckedException("[ error message insert failed ]");
				throw cheEx;
			}
		}
	}

	/*
	 * Pos batch merge action.
	 */
	public void mergePos(List<Object>[] list, String[] fileNameAry, String[] funcNameAry, List<DataSyncError> errorList)
			throws CheckedException {
		logger.info("call mergePos");
		// List<DataSyncError> errorList = new ArrayList<DataSyncError>();
		Query q = em.createQuery("SELECT a FROM Material a where a.sapPartNumber =:sapPartNumber");
		Query q2 = em.createQuery("SELECT a FROM Customer a where a.customerNumber=:customerNumber");
		int errorPosition = 0;
		String errorFileName = null;
		String errorFunction = null;
		try {
			ut.setTransactionTimeout(SESSION_TIME_OUT);
			ut.begin();
			for (int i = 0; i < list.length; i++) {
				errorPosition = 0;
				List subList = list[i];
				errorFileName = fileNameAry[i];
				errorFunction = funcNameAry[i];
				for (int j = 0; j < subList.size(); j++) {
					Object obj = subList.get(j);
					errorPosition++;
					if (obj instanceof Pos) {
						Pos tempObj = (Pos) obj;
						q.setParameter("sapPartNumber", tempObj.getSapPartNumber());
						// Production
						// List<Material> resultObjList =
						// (List<Material>)q.getResultList();
						// //Since some material don't imported into db. if
						// cann't find the related material. will skip the pos
						// records.
						// if(resultObjList!=null && resultObjList.size()>0)
						// {
						// Material resultObj = resultObjList.get(0);
						// tempObj.setMaterial(resultObj);
						// //For UAT Only , if not found custoemr . will skip
						// it.
						// //Base on Timothy 's request.
						// q2.setParameter("customerNumber",
						// tempObj.getCustomer().getCustomerNumber());
						// List<Customer> custResultList = (List<Customer>)
						// q2.getResultList();
						// if(custResultList!=null && custResultList.size()>0)
						// {
						// em.merge(obj);
						// if(j%BATCH_NUMBER==0)
						// {
						// em.flush();
						// em.clear();
						// }
						// }
						// else
						// {
						// //throw new CheckedException("Can't find the customer
						// : "+tempObj.getCustomer().getCustomerNumber());
						// errorList = addError(errorList, "|Can't find the
						// customer|"+tempObj.getCustomer().getCustomerNumber()+"|"+tempObj.getOriginalSapData(),errorFileName,errorFunction,errorPosition
						// );
						// logger.info("|Can't find the
						// customer|"+tempObj.getCustomer().getCustomerNumber());
						// }
						//
						// }
						// else
						// {
						// errorList = addError(errorList, "|Can't find the
						// material|"+tempObj.getSapPartNumber()+"|"+tempObj.getOriginalSapData(),errorFileName,errorFunction,errorPosition
						// );
						// logger.info("|Can't find the
						// material|"+tempObj.getSapPartNumber());
						// }

						// Based on Timothy 's request . disable all the foreign
						// key and load all the data into DB .
						// Feb 21 , 2014
						em.merge(obj);
						if (j % BATCH_NUMBER == 0) {
							em.flush();
							em.clear();
						}

					}
				}
			}
			em.flush();
			em.clear();
			ut.commit();
		} catch (Exception e) {
			try {
				errorList = addError(errorList, e.getMessage(), errorFileName, errorFunction, errorPosition);
				ut.rollback();
			}
    	    catch(Exception syex) 
    	    {
    	    	logger.log(Level.SEVERE, "Exception occured in method MasterDataSB#mergePos() for error file name: "+errorFileName+", Error Function: "+errorFunction+", "
						+ "error position: "+errorPosition+", Reason for failure: "+syex.getMessage(), syex);
			}

			CheckedException cheEx = new CheckedException("[errorFunction:" + errorFunction + "] [errorFileName:"
					+ errorFileName + "] [errorPosition:" + errorPosition + "] [errorMsg: " + e.getMessage() + "]");
			throw cheEx;

		} finally {
			try {
				ut.begin();
				errorSB.persistError(errorList);
				ut.commit();
				sendErrorNotificationEmail(errorList);
			}
    		catch(Exception e)
    		{
    			CheckedException cheEx = new CheckedException("[ error message insert failed ]");
				throw cheEx;
			}
		}
	}

	/*
	 * DRMS AGP REA merge action. 1, Removed all the data first. and then insert
	 * data.
	 */
	public void mergeDrmsAgpRea(List<Object>[] list, String[] fileNameAry, String[] funcNameAry)
			throws CheckedException {
    	logger.info("call mergeDRMSAGPREA");
    	List<DataSyncError> errorList = new ArrayList<DataSyncError>();
    	int errorPosition = 0 ; 
    	String errorFileName = null;
    	String errorFunction = null;
    	try
    	{
    		ut.setTransactionTimeout(SESSION_TIME_OUT);
    		ut.begin();
    		Query q1 = em.createQuery("DELETE FROM DrmsAgpRea");
    		q1.executeUpdate();
    		logger.info("delete  DrmsAgpRea finished");
	    	for(int i =0; i < list.length ; i ++) {
				errorPosition = 0;
				List subList = list[i];
				errorFileName = fileNameAry[i];
				errorFunction = funcNameAry[i];
				for (int j = 0; j < subList.size(); j++) {
					Object obj = subList.get(j);
					errorPosition++;
					em.persist(obj);
					em.flush();
				}
			}
			logger.log(Level.FINE, "Error List persisted into DB.");
			em.clear();
			ut.commit();

		}
    	catch(Exception e)
    	{
    		try {
				errorList = addError(errorList, e.getMessage(), errorFileName, errorFunction, errorPosition);
				ut.rollback();
			}
    	    catch(Exception syex) 
    	    {
    	    	logger.log(Level.SEVERE, "Exception occured in method mergeDRMSAGPREA() for error file name: "+errorFileName+", Error Function: "+errorFunction+", "
						+ "error position: "+errorPosition+", Reason for failure: "+syex.getMessage(), syex);
			}

			CheckedException cheEx = new CheckedException("[errorFunction:" + errorFunction + "] [errorFileName:"
					+ errorFileName + "] [errorPosition:" + errorPosition + "] [errorMsg: " + e.getMessage() + "]");
			throw cheEx;

		} finally {
			try {
				ut.begin();
				errorSB.persistError(errorList);
				ut.commit();
			} 
    		catch(Exception e)
    		{
    			/*logger.log(Level.SEVERE, e.getMessage(), e);*/
				CheckedException cheEx = new CheckedException("[ error message insert failed ]");
				throw cheEx;
			}
		}
	}

	/*
	 * special customer merge action. 1, Removed all the data first. and then
	 * insert data.
	 */
	/**
	 * @param list
	 * @param fileNameAry
	 * @param funcNameAry
	 * @throws CheckedException
	 */
	public void mergeSpecialCustomer(List<Object>[] list, String[] fileNameAry, String[] funcNameAry)
			throws CheckedException {
    	logger.info("call mergeSpecialCustomer");
    	List<DataSyncError> errorList = new ArrayList<DataSyncError>();
    	int errorPosition = 0 ; 
    	String errorFileName = null;
    	String errorFunction = null;
    	try
    	{
    		ut.setTransactionTimeout(SESSION_TIME_OUT);
    		ut.begin();
    		Query q1 = em.createQuery("DELETE FROM SpecialCustomer");
    		q1.executeUpdate();
    		logger.info("delete SpecialCustomer finished");
	    	for(int i =0; i < list.length ; i ++) {
				errorPosition = 0;
				List subList = list[i];
				errorFileName = fileNameAry[i];
				errorFunction = funcNameAry[i];
				for (int j = 0; j < subList.size(); j++) {
					Object obj = subList.get(j);
					errorPosition++;
					em.persist(obj);
					em.flush();
				}
			}
			em.clear();
			ut.commit();

		}
    	catch(Exception e)
    	{
    		try {
				errorList = addError(errorList, e.getMessage(), errorFileName, errorFunction, errorPosition);
				ut.rollback();
			}
    	    catch(Exception syex) 
    	    {
    	    	logger.log(Level.SEVERE, "Exception occured in mergeSpecialCustomer() for error file name: "+errorFileName+", Error Function: "+errorFunction+", "
						+ "error position: "+errorPosition+", Reason for failure: "+syex.getMessage(), syex);
			}

			CheckedException cheEx = new CheckedException("[errorFunction:" + errorFunction + "] [errorFileName:"
					+ errorFileName + "] [errorPosition:" + errorPosition + "] [errorMsg: " + e.getMessage() + "]");
			throw cheEx;

		} finally {
			try {
				ut.begin();
				errorSB.persistError(errorList);
				ut.commit();
			}
    		catch(Exception e)
    		{
    			/*logger.log(Level.SEVERE, e.getMessage(), e);*/
				CheckedException cheEx = new CheckedException("[ error message insert failed ]");
				throw cheEx;
			}
		}
	}

	/*
	 * Add error object to List
	 */
	public List<DataSyncError> addError(List<DataSyncError> errorList, String errorMsg, String fileName,
			String funcCode, int record) {

		logger.info("call addError");
		DataSyncError dse = new DataSyncError();
		dse.setCreatingDate(DateUtils.getCurrentAsiaDateObj());
		dse.setErrorMessage(errorMsg);
		dse.setFileName(fileName);
		dse.setFunctionCode(funcCode);
		dse.setErrorRecord(String.valueOf(record));
		if(errorList == null) errorList = new ArrayList<DataSyncError>();
		errorList.add(dse);
		return errorList;
	}

	/*
	 * When SAP Part Master data of ��VIS�� is loaded, system will detect
	 * whether Product Category is ��IP&E��. If yes, this part will be put under
	 * MFR ��VIS-IPE�� in WebQuote; otherwise this part will be put under MFR
	 * ��VIS-SEMI��.
	 * 
	 */
	public String mfrChange(String mfr, String category) {
		if (mfr != null && MFR_VIS.equalsIgnoreCase(mfr)) {
			if (category != null && SAP_CATEGORY_IPE.equalsIgnoreCase(category)) {
				mfr = MFR_VIS_IPE;
			} else {
				mfr = MFR_VIS_SEMI;
			}
		}
		return mfr;
	}

	public void sendErrorNotificationEmail(List<DataSyncError> errorList) {
		if (errorList != null && errorList.size() > 0) {

			MailInfoBean mib = new MailInfoBean();
			String subject = Constants.MASTER_LOADING_ERROR_EMAIL_SUBJECT;
			// add by Lenon.Yang(043044) 2016/05/23
			String jbossNodeName = System.getProperty(HATimerService.JBOSS_NODE_NAME);
			if (org.apache.commons.lang.StringUtils.isNotBlank(jbossNodeName)) {
				subject += "(Jboss Node:" + jbossNodeName + ")";
			}
			mib.setMailSubject(subject);
			String sender = sysConfigSB.getProperyValue(QuoteSBConstant.ERROR_EMAIL_FROM_ADDRESS);

			mib.setMailFrom(sender);
			StringBuffer contentSb = new StringBuffer();
			contentSb.append("Issue records:").append(NEXT_LINE);
			for (DataSyncError dse : errorList) {
				contentSb.append(dse.getFileName()).append(BLANK_SPACE).append(dse.getErrorMessage()).append(NEXT_LINE);
			}
			mib.setMailContent(contentSb.toString());
			List<String> toList = new ArrayList<String>();
			String recipients = sysConfigSB.getProperyValue(QuoteSBConstant.ERROR_EMAIL_TO_ADDRESS);
			if (null != recipients && !recipients.isEmpty()) {
				String[] tos = recipients.split(";");
				for (String recipient : tos) {
					if (null != recipient && !recipient.trim().isEmpty())
						toList.add(recipient);
				}
			}
			toList.clear();toList.add("Vincent.Wang@AVNET.COM");
			mib.setMailTo(toList);
			try {
				mailUtilsSB.sendTextMail(mib);
			}
			catch (Exception e)
			{
				logger.log(Level.SEVERE, "Exception occured in sending email from "+mib.getMailFrom()+", to "+mib.getMailTo().toString()+", "
						+ "subject: "+mib.getMailSubject()+", Reason for failure: "+e.getMessage(), e);
				
				logger.info("Send email failed!" + e.getMessage());
			}
		}
	}

	public List<BizUnit> joinBizUnitList(List<BizUnit> newBuList, List<BizUnit> oldBuList) {
		List<BizUnit> returnBuList = new ArrayList<BizUnit>();
		Set<String> storedBu = new HashSet<String>();
		if (oldBuList != null && oldBuList.size() > 0) {
			for (BizUnit bu : oldBuList) {
				returnBuList.add(bu);
				storedBu.add(bu.getName());
			}
		}
		if (newBuList != null && newBuList.size() > 0) {
			for (BizUnit bu : newBuList) {
				if (!storedBu.contains(bu.getName())) {
					returnBuList.add(bu);
				}

			}
		}

		return returnBuList;

	}
	
	/**
	 * @param obj
	 * @param tempMaterial
	 * @param resultMaterial
	 * @param em
	 * @param q
	 */
	private Material mergeManufaturer(Object obj,Material tempMaterial, EntityManager em,Query q){
		List<Manufacturer> tempMfrList = (List<Manufacturer>) q.getResultList();
		// if MFR is not exists,create a new one
		// added by Lenon 2016-08-17
		Manufacturer newMfr = null;
		if (!(tempMfrList != null && tempMfrList.size() > 0)) {
			newMfr = new Manufacturer();
			newMfr.setName(((SapMaterial) obj).getMfr());
			newMfr.setDescription(((SapMaterial) obj).getMfr());
			newMfr = em.merge(newMfr);
		} else {
			newMfr = tempMfrList.get(0);
		}
		tempMaterial = new Material();
		tempMaterial.setManufacturer(newMfr);
		tempMaterial.setFullMfrPartNumber(((SapMaterial) obj).getPartNumber());
		Date cal = DateUtils.getCurrentAsiaDateObj();
		tempMaterial.setCreatedOn(cal);
		tempMaterial.setLastUpdatedOn(cal);
		tempMaterial.setLastUpdatedBy("999999");
		
		return tempMaterial;
	}
	
	//Bryan Start
	/*
	 * SAP DPA Customer batch merge action.
	 */
	public void mergeSapDpaCustomer(List<Object>[] list, String[] fileNameAry, String[] funcNameAry) throws CheckedException {
		logger.info("call mergeSapDpaCustomer");
		

		List<DataSyncError> errorList = new ArrayList<DataSyncError>();
		int errorPosition = 0;
		String errorFileName = null;
		String errorFunction = null;
		try {
			ut.setTransactionTimeout(SESSION_TIME_OUT);
			ut.begin();

			//delete all record in SAP_DPA_CUST before insert new records.
			Query q = em.createQuery("DELETE FROM SapDpaCust");
			q.executeUpdate();

			for (int i = 0; i < list.length; i++) {
				errorPosition = 0;
				List subList = list[i];
				errorFileName = fileNameAry[i];
				errorFunction = funcNameAry[i];
				for (int j = 0; j < subList.size(); j++) {
					Object obj = subList.get(j);

					errorPosition++;
					em.persist(obj);
					//em.flush();
					//em.clear();

				}
			}
			em.flush();
			em.clear();
			ut.commit();
			System.out.println("commited..");
		}
    	catch(Exception e)
    	{
    		try {
				errorList = addError(errorList, e.getMessage(), errorFileName, errorFunction, errorPosition);
				ut.rollback();
			}
    	    catch(Exception syex) 
    	    {
				logger.log(Level.SEVERE, "Exception occured in method mergeSalesOrg() on rollback transaction for error file name: "+errorFileName+", Error Function: "+errorFunction+", "
						+ "error position: "+errorPosition+", Reason for failure: "+syex.getMessage(), syex);
			}

			CheckedException cheEx = new CheckedException("[errorFunction:" + errorFunction + "] [errorFileName:"
					+ errorFileName + "] [errorPosition:" + errorPosition + "] [errorMsg: " + e.getMessage() + "]");
			throw cheEx;

		} finally {
			try {
				ut.begin();
				errorSB.persistError(errorList);
				ut.commit();
			} 
    		catch(Exception e)
    		{
				/*logger.log(Level.SEVERE, e.getMessage(), e);*/
				CheckedException cheEx = new CheckedException("[ error message insert failed ]");
				throw cheEx;
			}
		}
	}
	//Bryan End
}
