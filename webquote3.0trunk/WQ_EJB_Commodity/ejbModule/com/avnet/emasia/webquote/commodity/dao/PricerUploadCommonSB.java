package com.avnet.emasia.webquote.commodity.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;

import org.jboss.ejb3.annotation.TransactionTimeout;

import com.avnet.emasia.webquote.commodity.bean.PricerUploadParametersBean;
import com.avnet.emasia.webquote.commodity.bean.PricerUploadTemplateBean;
import com.avnet.emasia.webquote.commodity.bean.ProgramItemUploadCounterBean;
import com.avnet.emasia.webquote.commodity.bean.VerifyEffectiveDateResult;
import com.avnet.emasia.webquote.commodity.constant.PRICER_TYPE;
import com.avnet.emasia.webquote.commodity.constant.PricerConstant;
import com.avnet.emasia.webquote.commodity.ejb.MaterialRegionalUploadSB;
import com.avnet.emasia.webquote.commodity.util.PricerUtils;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.CostIndicator;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.NormalPricer;
import com.avnet.emasia.webquote.entity.PgMasterMapping;
import com.avnet.emasia.webquote.entity.PricerUploadSummary;
import com.avnet.emasia.webquote.entity.ProductCategory;
import com.avnet.emasia.webquote.entity.ProductGroup;
import com.avnet.emasia.webquote.entity.ProgramType;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.ejb.MaterialRegionalSB;
import com.avnet.emasia.webquote.utilities.util.QuoteUtil;

@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
//@Transactional
public class PricerUploadCommonSB {
	private static final Logger LOG = Logger.getLogger(PricerUploadCommonSB.class.getName());

	private  static final int DELTED_INIT_SATUS = 0;
	@Resource
	protected UserTransaction ut;

	@PersistenceContext(unitName = "Server_Source")
	protected EntityManager em;

	@EJB
	protected MaterialRegionalSB materialRegionalSB;
	@EJB
	protected MaterialRegionalUploadSB materialRegionalUploadSB;

	/**
	 * Insert pricer data and quantity break price data to db.
	 * 
	 * @param items
	 * @return
	 */
	public ProgramItemUploadCounterBean insertUploadPricer(String materialTypeStr, List<PricerUploadTemplateBean> items,
			User user, List<Manufacturer> manufacturerLst, PricerUploadParametersBean puBean) {
		LOG.info("insert pricer data db begin...");

		int itemCountOneLoop = 500;
		Function<List<PricerUploadTemplateBean>, int[] > function = 
				(List<PricerUploadTemplateBean> subItemLst) -> {
					int[] countArr = new int[12];
					insertUploadOperater(subItemLst, materialTypeStr, user, manufacturerLst, countArr, puBean);
					return countArr;
				};
		int[] countArr = splitBatchToDo(items, itemCountOneLoop, user, materialTypeStr, function);
		ProgramItemUploadCounterBean countBean = new ProgramItemUploadCounterBean();
		countBean.setAddedPartCount(countArr[0]);
		countBean.setAddedNormalCount(countArr[1]);
		countBean.setAddedProgramCount(countArr[2]);
		countBean.setUpdatedPartCount(countArr[3]);
		countBean.setUpdatedNormalCount(countArr[4]);
		countBean.setUpdatedProgramCount(countArr[5]);
		countBean.setAddedContractPriceCount(countArr[6]);
		countBean.setUpdatedContractPriceCount(countArr[7]);
		countBean.setAddedSimplePricerCount(countArr[8]);
		countBean.setUpdatedSimplePricerCount(countArr[9]);
		countBean.setAddedSalesCostPricerCount(countArr[10]);
		countBean.setUpdatedSalesCostPricerCount(countArr[11]);
		LOG.info("insert pricer data to db ended");
		return countBean;
	}

	/**
	 * Insert pricer data and quantity break price data to db.
	 * 
	 * @param items
	 * @return
	 */
	public ProgramItemUploadCounterBean removeOnlyPricer(String materialTypeStr, List<PricerUploadTemplateBean> items,
			User user) {
		LOG.info("remove only pricer data from db begin...");

		int itemCountOneLoop = 500;
		Function<List<PricerUploadTemplateBean>, int[] > function = 
				(List<PricerUploadTemplateBean> subItemLst) -> {
					int[] countArr = new int[5];
					removeOnlyPricerOperater(subItemLst, materialTypeStr, user, countArr);
					return countArr;
				};
		int[] countArr = splitBatchToDo(items, itemCountOneLoop, user, materialTypeStr, function);

		ProgramItemUploadCounterBean countBean = new ProgramItemUploadCounterBean();

		countBean.setRemovedNormalCount(countArr[0]);
		countBean.setRemovedProgramCount(countArr[1]);
		countBean.setRemovedContractPriceCount(countArr[2]);
		countBean.setRemovedSimplePricerCount(countArr[3]);
		countBean.setRemovedSalesCostPricerCount(countArr[4]);
		LOG.info("remove only pricer data from db ended");
		return countBean;
	}
	
	private int[] splitBatchToDo(List<PricerUploadTemplateBean> items, int itemCountOneLoop, User user,
			String materialTypeStr, 
			Function<List<PricerUploadTemplateBean>, int[] > function){
		int[] countArr = null;
		int loop = (int) Math.ceil(items.size()/(double)itemCountOneLoop);
		try
		{
			for(int i=0; i< loop; i++) {
				int fromIndex = i*itemCountOneLoop;
				//no include toIndex;
				//int toIndex = loop > i+1 ? (i+1)*itemCountOneLoop : i*itemCountOneLoop + items.size() % itemCountOneLoop;
				int toIndex = loop-1 == i ? items.size() : fromIndex + itemCountOneLoop;
				List<PricerUploadTemplateBean> subItemLst = items.subList(fromIndex, toIndex);
				ut.setTransactionTimeout(100000000);
				ut.begin();
				int[] countSubArr = function.apply(subItemLst);
				ut.commit();
				if (countArr == null) countArr = new int[countSubArr.length];
				for(int index=0; index<countSubArr.length; index++) 
					countArr[index] = countArr[index] + countSubArr[index];
				
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE,
					"pricer upload is error, will roll back for materialTypeStr: " + materialTypeStr
							+ " :: user Id and name " + user.getId() + " & " + user.getName() +";" 
							+ e.getMessage(), e);
			try {
				ut.rollback();
			} catch (Exception e1) {
				LOG.log(Level.SEVERE," invoke roll back error:: ", e1);
			}
		}
		return countArr;
	}
		
	public ProgramItemUploadCounterBean removeAllPricer(List<PricerUploadTemplateBean> items, User user) {
		LOG.info("remove all pricer data from db begin...");

		int[] countArr = new int[] { 0, 0, 0, 0, 0 };// removeNormal removeProgram
												// removeContract
		int[] countArrBeforeAction = countArr.clone();
		try {
			// Sql column isn't more than 1000.
			if (items.size() > 500) {
				List<PricerUploadTemplateBean> subItemLst = new ArrayList<PricerUploadTemplateBean>();
				while (items.size() > 500) {
					subItemLst = new LinkedList<>(items.subList(0, 499));
					ut.setTransactionTimeout(100000000);
					ut.begin();

					countArrBeforeAction = countArr.clone();
					removeAllPricerOperater(subItemLst, user, countArr);
					ut.commit();
					items.removeAll(subItemLst);
				}
				if (items.size() > 0) {
					ut.setTransactionTimeout(100000000);
					ut.begin();
					countArrBeforeAction = countArr.clone();
					removeAllPricerOperater(items, user, countArr);
					ut.commit();
				}
			} else {
				ut.setTransactionTimeout(100000000);
				ut.begin();
				countArrBeforeAction = countArr.clone();
				removeAllPricerOperater(items, user, countArr);
				ut.commit();
			}

		} catch (Exception e) {
			try {
				LOG.log(Level.SEVERE, "transaction rolled back in pricer upload for user " + user.getEmployeeId());
				ut.rollback();
				throw e;
			} catch (Exception e1) {
				LOG.log(Level.SEVERE, "Insert pricer upload is error role back for user Id and name " + user.getId()
						+ " & " + user.getName() + " : Exception ::" + e1.getMessage(), e1);
			}
			/**
			 * If failed to perform the transaction, the use value before the
			 * action
			 */
			countArr = countArrBeforeAction;
		}

		ProgramItemUploadCounterBean countBean = new ProgramItemUploadCounterBean();

		countBean.setRemovedNormalCount(countArr[0]);
		countBean.setRemovedProgramCount(countArr[1]);
		countBean.setRemovedContractPriceCount(countArr[2]);
		countBean.setRemovedSimplePricerCount(countArr[3]);
		countBean.setRemovedSalesCostPricerCount(countArr[4]);
		LOG.info("remove all pricer data for selected mfr from db ended");
		return countBean;
	}

	public void removeOnlyPricerOperater(List<PricerUploadTemplateBean> items, String materialTypeStr, User user,
			int[] countArr) {
		// By a subclass implementation

	}

	public void insertUploadOperater(List<PricerUploadTemplateBean> subItemLst, String materialTypeStr, User user,
			List<Manufacturer> manufacturerLst, int[] countArr, PricerUploadParametersBean puBean) {
		// By a subclass implementation
	}

	public void removeAllPricerOperater(List<PricerUploadTemplateBean> beans, User user, int[] countArr) {

		if (beans == null || beans.size() == 0) {
			return;
		}

		int count = beans.size();

		String bizUnit = user.getDefaultBizUnit().getName();

		String employeeId = user.getEmployeeId();

		StringBuffer sb = new StringBuffer(
				"DELETE FROM QuantityBreakPrice d where d.materialDetail.bizUnitBean.name = '" + bizUnit + "' ");
		// David
		if (QuoteUtil.isEqual(true, user.isSalsCostAccessFlag())) {
			sb = new StringBuffer("DELETE FROM QuantityBreakPrice d where d.materialDetail.pricerType='"
					+ PRICER_TYPE.SALESCOST.getName() + "' AND d.materialDetail.bizUnitBean.name = '" + bizUnit + "' ");
		} else if (QuoteUtil.isEqual(false, user.isSalsCostAccessFlag())) {
			sb = new StringBuffer("DELETE FROM QuantityBreakPrice d where d.materialDetail.pricerType!='"
					+ PRICER_TYPE.SALESCOST.getName() + "' AND d.materialDetail.bizUnitBean.name = '" + bizUnit + "' ");
		}
		// Added by Punit for CPD Exercise
		// sb = deleteMaterialDetailAndContractPriceQuery(sb, count);
		for (int j = 1; j < (count * 2 + 1);) {
			if (j == 1) {
				sb.append(" and ( ");
			}
			sb.append(" (d.materialDetail.material.manufacturer.name = ?").append(j++);
			sb.append(" and d.materialDetail.material.fullMfrPartNumber = ?").append(j++).append(")");
			if (j <= count * 2) {
				sb.append(" or ");
			} else {
				sb.append(")");
			}
		}

		Query query = em.createQuery(sb.toString());

		int i = 1;
		for (PricerUploadTemplateBean bean : beans) {
			query.setParameter(i++, bean.getMfr());
			query.setParameter(i++, bean.getFullMfrPart());
		}

		i = query.executeUpdate();
		LOG.info(employeeId + " removed " + i + " record in QUANTITY_BREAK_PRICE");
		 
		int removedNormalCount = 0;
		int removedProgramCount = 0;
		int removedContractCount = 0;
		int removedSimpleCount = 0;
		int removedSalesCostCount = 0;
		if (!QuoteUtil.isEqual(true, user.isSalsCostAccessFlag())) {
			sb.setLength(0);

			sb.append("DELETE FROM ProgramPricer d where d.bizUnitBean.name = '" + bizUnit + "' ");

			// Added by Punit for CPD Exercise
			sb = deleteMaterialDetailAndContractPriceQuery(sb, count);

			query = em.createQuery(sb.toString());

			i = 1;
			for (PricerUploadTemplateBean bean : beans) {
				query.setParameter(i++, bean.getMfr());
				query.setParameter(i++, bean.getFullMfrPart());
			}

			removedProgramCount = query.executeUpdate();

			LOG.info(employeeId + " removed " + removedProgramCount + " ProgramPricer record in Material_Detail");

			sb.setLength(0);

			sb.append("DELETE FROM NormalPricer d where d.bizUnitBean.name = '" + bizUnit + "' ");

			// Added by Punit for CPD Exercise
			sb = deleteMaterialDetailAndContractPriceQuery(sb, count);

			query = em.createQuery(sb.toString());

			i = 1;
			for (PricerUploadTemplateBean bean : beans) {
				query.setParameter(i++, bean.getMfr());
				query.setParameter(i++, bean.getFullMfrPart());
			}

			removedNormalCount = query.executeUpdate();

			LOG.info(employeeId + " removed " + removedNormalCount + " NormalPricer record in Material_Detail");

			sb.setLength(0);

			sb.append("DELETE FROM ContractPricer d where d.bizUnitBean.name = '" + bizUnit + "' ");

			// Added by Punit for CPD Exercise
			sb = deleteMaterialDetailAndContractPriceQuery(sb, count);

			query = em.createQuery(sb.toString());

			i = 1;
			for (PricerUploadTemplateBean bean : beans) {
				query.setParameter(i++, bean.getMfr());
				query.setParameter(i++, bean.getFullMfrPart());
			}

			removedContractCount = query.executeUpdate();

			LOG.info(employeeId + " removed " + removedContractCount + " record in Contract_Price ");
			// SIMPLE
			sb.setLength(0);
			sb.append("DELETE FROM SimplePricer d where d.bizUnitBean.name = '" + bizUnit + "' ");
			// Added by Punit for CPD Exercise
			sb = deleteMaterialDetailAndContractPriceQuery(sb, count);
			query = em.createQuery(sb.toString());
			i = 1;
			for (PricerUploadTemplateBean bean : beans) {
				query.setParameter(i++, bean.getMfr());
				query.setParameter(i++, bean.getFullMfrPart());
			}
			removedSimpleCount = query.executeUpdate();
			LOG.info(employeeId + " removed " + removedContractCount + " record in Simple_Price ");
		}
		
		if (!QuoteUtil.isEqual(false, user.isSalsCostAccessFlag())) {
			//Sales cost pricer should be save into pricer_deleted table before delete for sap exchange
			batchInsertIntoPricerDeleted( bizUnit, count, beans, user);
			sb.setLength(0);
			sb.append("DELETE FROM SalesCostPricer d where d.bizUnitBean.name = '" + bizUnit + "' ");
			// Added by Punit for CPD Exercise
			sb = deleteMaterialDetailAndContractPriceQuery(sb, count);
			sb.append(" AND d.batchStatus < 0 ");
			query = em.createQuery(sb.toString());
			i = 1;
			for (PricerUploadTemplateBean bean : beans) {
				query.setParameter(i++, bean.getMfr());
				query.setParameter(i++, bean.getFullMfrPart());
			}
			removedSalesCostCount = query.executeUpdate();
			LOG.info(employeeId + " removed " + removedContractCount + " record in SalesCost_Price ");
		}
		countArr[0] = countArr[0] + removedNormalCount;
		countArr[1] = countArr[1] + removedProgramCount;
		countArr[2] = countArr[2] + removedContractCount;
		countArr[3] = countArr[3] + removedSimpleCount;
		countArr[4] = countArr[4] + removedSalesCostCount;
	}

	/*****
	 * 
	 * @param bizUnit
	 * @param count
	 * @param beans
	 * @param user
	 * @return
	 * 
	 * Sales cost pricer should be save into pricer_deleted table before delete for sap .
 	 */
	@Transactional
	public int batchInsertIntoPricerDeleted(String bizUnit, int count, List<PricerUploadTemplateBean> beans, User user) {
		StringBuffer sb = new StringBuffer();
		Query query = null;
		// batch update before batch insert into pricer_deleted
		// this avoid the phantom read between the batch insert and then batch delete 
		//batchStatus - 100 IN batchInsertIntoPricerDeleted function
		sb.setLength(0);
		sb.append("UPDATE  SalesCostPricer d SET d.batchStatus = d.batchStatus-100 where d.bizUnitBean.name = '" + bizUnit + "' ");
		sb = deleteMaterialDetailAndContractPriceQuery(sb, count);
		query = em.createQuery(sb.toString());
		int i = 1;
		for (PricerUploadTemplateBean bean : beans) {
			query.setParameter(i++, bean.getMfr());
			query.setParameter(i++, bean.getFullMfrPart());
		}
		int updateCount = query.executeUpdate();
		LOG.config(() -> " UPDATE " + updateCount + " record FOR REMOVE SalesCost_Price.");
		
		int offsetIndex = 4;
		sb.setLength(0);
		sb.append(getInsertPDeletedSql()).append(
				 " INNER JOIN MATERIAL M ON M.ID=P.MATERIAL_ID INNER JOIN MANUFACTURER MFR ON M.MANUFACTURER_ID= MFR.ID  ").append(
				 " WHERE P.BIZ_UNIT=?4 ");
			sb = this.deleteForNativeSql(sb, count, offsetIndex);
			sb.append(" AND P.BATCH_STATUS < 0 ");
		query  = em.createNativeQuery(sb.toString());
		query.setParameter(1, user.getEmployeeId());
		query.setParameter(2, new Date(), TemporalType.TIMESTAMP);
		query.setParameter(3, DELTED_INIT_SATUS);
		query.setParameter(4, bizUnit);
		i = 1 + offsetIndex ;
		for (PricerUploadTemplateBean bean : beans) {
			query.setParameter(i++, bean.getMfr());
			query.setParameter(i++, bean.getFullMfrPart());
		}
		int insertCount = query.executeUpdate();
		LOG.config(() -> user.getEmployeeId() + " insert into  " + insertCount + " record Pricer_deleted.");
		return insertCount;
	}
	
	 
	@Transactional
	public int batchInsertIntoPricerDeleted(String bizUnit, String mfr, User user) {
		StringBuffer sb = new StringBuffer();
		Query query = null;
		// batch update before batch insert into pricer_deleted
		// this avoid the phantom read between the batch insert and then batch delete 
		//batchStatus - 100
		sb.setLength(0);
		sb.append("UPDATE  SalesCostPricer d SET d.batchStatus = d.batchStatus-100 where d.bizUnitBean.name = ?1 and d.material.manufacturer.name = ?2 ");
		//sb = deleteMaterialDetailAndContractPriceQuery(sb, count);
		query = em.createQuery(sb.toString());
		query.setParameter(1, bizUnit);
		query.setParameter(2, mfr);
		int updateCount = query.executeUpdate();
		LOG.info(" UPDATE " + updateCount + " record FOR REMOVE SalesCost_Price.");
		
		int offsetIndex = 4;
		sb.setLength(0);
		sb.append(getInsertPDeletedSql()).append(
				 " INNER JOIN MATERIAL M ON M.ID=P.MATERIAL_ID INNER JOIN MANUFACTURER MFR ON M.MANUFACTURER_ID= MFR.ID  ").append(
				 " WHERE P.BIZ_UNIT=?4 ");
		sb.append(" AND MFR.NAME = ?5 ");
		sb.append(" AND P.BATCH_STATUS < 0 ");
		query  = em.createNativeQuery(sb.toString());
		query.setParameter(1, user.getEmployeeId());
		query.setParameter(2, new Date(), TemporalType.TIMESTAMP);
		query.setParameter(3, DELTED_INIT_SATUS);
		query.setParameter(4, bizUnit);
		query.setParameter(5, mfr);
		int insertCount = query.executeUpdate();
		LOG.info(user.getEmployeeId() + " insert into  " + insertCount + " record Pricer_deleted.");
		return insertCount;
	}
		
	@Transactional
	public int InsertIntoPricerDeletedByPricerId(long id, User user) {
		if(id<1) {
			return 0;
		}
		StringBuffer sb = new StringBuffer();
		sb.append(getInsertPDeletedSql()).append(
				" WHERE P.ID=").append(id);
		Query query  = em.createNativeQuery(sb.toString());
		//query.setParameter("ID", id);
		query.setParameter(1, user.getEmployeeId());
		query.setParameter(2, new Date(), TemporalType.TIMESTAMP);
		query.setParameter(3, DELTED_INIT_SATUS);
		int insertCount = query.executeUpdate();
		LOG.info(user.getEmployeeId() + " insert into  " + insertCount + " record Pricer_deleted.");
		return insertCount;
	}
	
	@Transactional
	public int insertIntoPricerDeletedByPricerIds(String ids, User user) {
		if(ids ==null||ids.isEmpty()) {
			return 0;
		}
		StringBuffer sb = new StringBuffer();
		sb.append(getInsertPDeletedSql()).append(
				" WHERE P.ID in ").append(ids);
		Query query  = em.createNativeQuery(sb.toString());
		query.setParameter(1, user.getEmployeeId());
		query.setParameter(2, new Date(), TemporalType.TIMESTAMP);
		query.setParameter(3, DELTED_INIT_SATUS);
		int insertCount = query.executeUpdate();
		LOG.info(user.getEmployeeId() + " insert into  " + insertCount + " record Pricer_deleted.");
		return insertCount;
	}
	//Common sql string
	private String getInsertPDeletedSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("Insert into PRICER_DELETED (DELETE_ID,DELETED_BY,DELETED_ON,DELETED_BATCH_STATUS,ID,MATERIAL_ID,VERSION,COST,COST_INDICATOR,").append(
				"QTY_INDICATOR,PRICE_VALIDITY,LEAD_TIME,BOTTOM_PRICE,MOQ,MOV,MPQ,MIN_SELL_PRICE,FULL_MFR_PART_NUMBER,PRICE_LIST_REMARKS1,").append(
				 "PRICE_LIST_REMARKS2,PRICE_LIST_REMARKS3,PRICE_LIST_REMARKS4,SHIPMENT_VALIDITY,TERMS_AND_CONDITIONS,ALLOCATION_FLAG,AVNET_QC_COMMENTS,").append(
				 "RESCHEDULED_WINDOW,CANCELLATION_WINDOW,SALES_ORG,").append(
				 "CREATED_ON,LAST_UPDATED_ON,BIZ_UNIT,PROGRAM_AVAILABLE_TO_SELL_QTY,PROGRAM_MINIMUM_COST,PROGRAM_TYPE_ID,PROGRAM_EFFECTIVE_DATE,").append(
				 "PROGRAM_CLOSING_DATE,PROGRAM_CALL_FOR_QUOTE,PROGRAM_AQ_FLAG,CREATED_BY,LAST_UPDATED_BY,MATERIAL_CATEGORY,").append(
				 "PROGRAM_SPECIAL_ITEM_FLAG,RESALE_INDICATOR,").append(
				 "NEW_PROGRAM_MATERIAL_INDICATOR,PART_DESCRIPTION,AVAILABLE_TO_SELL_MORE_FLAG,DISPLAY_QUOTE_EFF_DATE,QUOTATION_EFFECTIVE_DATE,").append(
				 "QUOTATION_EFFECTIVE_TO,CURRENCY,NORM_SELL,START_DATE,VENDOR_QUOTE_NUMBER,SOLD_CUSTOMER_ID,END_CUSTOMER_ID,VENDOR_QUOTE_QUANTITY,").append(
				 "END_CUSTOMER_NAME,OVERRIDE_FLAG,PRICER_TYPE,SALES_COST_TYPE,QTY_CONTROL,DISPLAY_ON_TOP,BATCH_STATUS) ").append(
				//SELECT FROM PRICER 
				// " SELECT PRICER_DELETED_SEQ.NEXTVAL,:DELETED_BY,:DELETED_ON,:DELETED_BATCH_STATUS,P.ID,P.MATERIAL_ID,P.VERSION,P.COST,P.COST_INDICATOR,").append(
						 " SELECT PRICER_DELETED_SEQ.NEXTVAL,?1,?2,?3,P.ID,P.MATERIAL_ID,P.VERSION,P.COST,P.COST_INDICATOR,").append(
				 "P.QTY_INDICATOR,P.PRICE_VALIDITY,P.LEAD_TIME,P.BOTTOM_PRICE,P.MOQ,P.MOV,P.MPQ,P.MIN_SELL_PRICE,P.FULL_MFR_PART_NUMBER,P.PRICE_LIST_REMARKS1,").append(
				 "P.PRICE_LIST_REMARKS2,P.PRICE_LIST_REMARKS3,P.PRICE_LIST_REMARKS4,P.SHIPMENT_VALIDITY,P.TERMS_AND_CONDITIONS,P.ALLOCATION_FLAG,P.AVNET_QC_COMMENTS,").append(
				 "P.RESCHEDULED_WINDOW,P.CANCELLATION_WINDOW,P.SALES_ORG,").append(
				 "P.CREATED_ON,P.LAST_UPDATED_ON,P.BIZ_UNIT,P.PROGRAM_AVAILABLE_TO_SELL_QTY,P.PROGRAM_MINIMUM_COST,P.PROGRAM_TYPE_ID,P.PROGRAM_EFFECTIVE_DATE,").append(
				 "P.PROGRAM_CLOSING_DATE,P.PROGRAM_CALL_FOR_QUOTE,P.PROGRAM_AQ_FLAG,P.CREATED_BY,P.LAST_UPDATED_BY,P.MATERIAL_CATEGORY,").append(
				 "P.PROGRAM_SPECIAL_ITEM_FLAG,P.RESALE_INDICATOR,").append(
				 "P.NEW_PROGRAM_MATERIAL_INDICATOR,P.PART_DESCRIPTION,P.AVAILABLE_TO_SELL_MORE_FLAG,P.DISPLAY_QUOTE_EFF_DATE,P.QUOTATION_EFFECTIVE_DATE,").append(
				 "P.QUOTATION_EFFECTIVE_TO,P.CURRENCY,P.NORM_SELL,P.START_DATE,P.VENDOR_QUOTE_NUMBER,P.SOLD_CUSTOMER_ID,P.END_CUSTOMER_ID,P.VENDOR_QUOTE_QUANTITY,").append(
				// batch update before batch insert into pricer_deleted
				// this avoid the phantom read between the batch insert and then batch delete 
				//batchStatus - 100 IN batchInsertIntoPricerDeleted function
				//so here RESTORE THE BATCH_STATUS..
				 "P.END_CUSTOMER_NAME,P.OVERRIDE_FLAG,P.PRICER_TYPE,P.SALES_COST_TYPE,P.QTY_CONTROL,P.DISPLAY_ON_TOP,CASE WHEN P.BATCH_STATUS < 0 THEN P.BATCH_STATUS + 100 ELSE P.BATCH_STATUS END BATCH_STATUS FROM PRICER P ");
			return sb.toString();
	}
	/**
	 * @param sb
	 * @param count
	 */
	private StringBuffer deleteMaterialDetailAndContractPriceQuery(StringBuffer sb, int count) {
		for (int j = 1; j < (count * 2 + 1);) {
			if (j == 1) {
				sb.append(" and ( ");
			}
			sb.append(" (d.material.manufacturer.name = ?").append(j++);
			sb.append(" and d.material.fullMfrPartNumber = ?").append(j++).append(")");
			if (j <= count * 2) {
				sb.append(" or ");
			} else {
				sb.append(")");
			}
		}

		return sb;
	}

	/**
	 * @param sb
	 * @param count
	 */
	private StringBuffer deleteForNativeSql(StringBuffer sb, int count, int offsetIndex) {
		for (int j = 1; j < (count * 2 + 1);) {
			if (j == 1) {
				sb.append(" and ( ");
			}
			sb.append(" (MFR.name = ?").append(offsetIndex+j++);
			sb.append(" and M.FULL_MFR_PART_NUMBER = ?").append(offsetIndex+j++).append(")");
			if (j <= count * 2) {
				sb.append(" or ");
			} else {
				sb.append(")");
			}
		}
		return sb;
	}
	/*
	 * private StringBuffer deleteQueryForRegional(StringBuffer sb, int count) {
	 * for(int j= 1; j < (count * 3 + 1); ){
	 * 
	 * sb.append(" (d.materialDetail.bizUnitBean.name = ? ").append(j++);
	 * sb.append(" AND d.materialDetail.material.manufacturer.name = ?").append(
	 * j++);
	 * sb.append(" AND d.materialDetail.material.fullMfrPartNumber = ?").append(
	 * j++).append(")"); if(j <= count * 3){ sb.append(" or "); } }
	 * 
	 * return sb; }
	 */

	public List<Manufacturer> findMFR(List<PricerUploadTemplateBean> beans, User user) {
		Set<String> mfrNameSet = new HashSet<String>();
		for (PricerUploadTemplateBean bean : beans) {
			mfrNameSet.add(bean.getMfr());
		}

		TypedQuery<Manufacturer> queryMfr = em.createQuery("SELECT a FROM Manufacturer a where a.name in :mfrNames",
				Manufacturer.class);
		queryMfr.setParameter("mfrNames", mfrNameSet);
		List<Manufacturer> mLst = queryMfr.getResultList();
		return mLst;
	}

	public List<CostIndicator> findCstIdct(List<PricerUploadTemplateBean> beans, User user,
			PricerUploadParametersBean puBean) {
		Set<String> costIndicatorSet = new HashSet<String>();
		for (PricerUploadTemplateBean bean : beans) {
			costIndicatorSet.add(bean.getCostIndicator());
		}

		TypedQuery<CostIndicator> queryCst = em
				.createQuery("SELECT a FROM CostIndicator a where a.name in :costIndicators", CostIndicator.class);
		queryCst.setParameter("costIndicators", costIndicatorSet);
		List<CostIndicator> cLst = queryCst.getResultList();
		Map<Object, CostIndicator> costIdtMap = puBean.getCostIdtMap();
		for (CostIndicator cstIdt : cLst) {
			costIdtMap.put(cstIdt.getName(), cstIdt);
		}
		return cLst;
	}

	/**
	 * If there is no record with the same [MFR ]+ [Full MFR Part Number] +
	 * [Region] and with [Normal][Material Category] found in Material_Detail
	 * table, display the error message Please upload Normal Pricing information
	 * for highlighted lines before uploading the Program Pricing information!
	 * Row: [XXXX], the records without matching Normal Pricer are highlighted
	 * in red.
	 * 
	 * @param pr
	 * @return
	 */
	public List<Integer> findCompKeyInMtlDtl(List<PricerUploadTemplateBean> prLst, User user) {
		BizUnit bz = user.getDefaultBizUnit();
		List<Integer> errRows = new ArrayList<Integer>();
		List<List<PricerUploadTemplateBean>> pLsts = new ArrayList<List<PricerUploadTemplateBean>>();
		int size = prLst.size();
		int batchCount = size / PricerConstant.BATCH_SIZE;
		int mod = size % PricerConstant.BATCH_SIZE;
		if (mod != 0) {
			batchCount++;
		}

		for (int i = 0; i < batchCount; i++) {
			List<PricerUploadTemplateBean> pLst = new ArrayList<PricerUploadTemplateBean>();

			for (int j = 0; j < PricerConstant.BATCH_SIZE; j++) {
				int s = i * PricerConstant.BATCH_SIZE + j;
				if (s >= size) {
					break;
				}
				pLst.add(prLst.get(s));
			}
			pLsts.add(pLst);
		}

		// batch find
		for (List<PricerUploadTemplateBean> pLst : pLsts) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<NormalPricer> c = cb.createQuery(NormalPricer.class);
			Root<NormalPricer> mt = c.from(NormalPricer.class);
			mt.fetch("material", JoinType.LEFT);
			Join<NormalPricer, BizUnit> bizUnit = mt.join("bizUnitBean");
			Join<NormalPricer, Material> material = mt.join("material");
			Join<Material, Manufacturer> manufacturer = material.join("manufacturer");

			List<Predicate> predicates = new ArrayList<Predicate>();
			for (PricerUploadTemplateBean bean : pLst) {
				Predicate p1 = cb.equal(manufacturer.<String>get("name"), bean.getMfr().toUpperCase());
				Predicate p2 = cb.equal(material.<String>get("fullMfrPartNumber"), bean.getFullMfrPart().toUpperCase());
				Predicate p = cb.and(p1, p2);
				predicates.add(p);
			}
			Predicate predicateBizUnit = cb.like(bizUnit.<String>get("name"), bz.getName());

			c.where(cb.and(predicateBizUnit, cb.or(predicates.toArray(new Predicate[0]))));

			TypedQuery<NormalPricer> q = em.createQuery(c);
			List<NormalPricer> materialDetails = q.getResultList();

			for (PricerUploadTemplateBean bean : pLst) {
				boolean found = false;
				for (NormalPricer mtd : materialDetails) {
					if (mtd.getMaterial().getManufacturer().getName().equalsIgnoreCase(bean.getMfr())
							&& mtd.getMaterial().getFullMfrPartNumber().equalsIgnoreCase(bean.getFullMfrPart())
							&& mtd.getBizUnitBean().getName().equals(bz.getName())) {
						bean.setMetarialDetailId(mtd.getId());
						// get the material_detail ID, convenient to save
						bean.setMaterial(mtd.getMaterial());
						found = true;
						break;
					}
				}
				if (found == false) {
					bean.setErr(true);
					errRows.add(bean.getLineSeq());
				}
			}
		}
		Collections.sort(errRows);
		return errRows;
	}

	public List<Integer> findCostIndicator(List<PricerUploadTemplateBean> pricer, User user, List<Manufacturer> mfrLst,
			PricerUploadParametersBean puBean) {
		List<Integer> errRows = new ArrayList<Integer>();

		try {
			// Sql column isn't more than 1000.
			List<PricerUploadTemplateBean> pricerForCheck = new ArrayList<PricerUploadTemplateBean>();
			pricerForCheck.addAll(pricer);
			if (pricerForCheck.size() > 500) {
				List<PricerUploadTemplateBean> subItemLst = new ArrayList<PricerUploadTemplateBean>();
				while (pricerForCheck.size() > 500) {
					subItemLst = pricerForCheck.subList(0, 499);
					ut.setTransactionTimeout(100000000);
					ut.begin();
					errRows.addAll(findACostIdct(subItemLst, user, mfrLst, puBean));
					ut.commit();
					pricerForCheck.removeAll(subItemLst);
				}
				if (pricerForCheck.size() > 0) {
					ut.setTransactionTimeout(100000000);
					ut.begin();
					errRows.addAll(findACostIdct(pricerForCheck, user, mfrLst, puBean));
					ut.commit();
				}
			} else {
				ut.setTransactionTimeout(100000000);
				ut.begin();
				errRows.addAll(findACostIdct(pricerForCheck, user, mfrLst, puBean));
				ut.commit();
			}

		} catch (Exception e) {
			LOG.log(Level.SEVERE, "check normal pricer upload cost indicator is error " + e.toString());
			try {
				throw e;
			} catch (Exception e1) {
				LOG.log(Level.SEVERE, "check normal pricer upload cost indicator is error for user Id and name "
						+ user.getId() + " & " + user.getName() + e1.getMessage(), e1);
			}
		}
		Collections.sort(errRows);
		return errRows;
	}

	public void saveProgramWhenNotExist(Set<String> pgmTypeSet, Map<Object, Object> pgmTypeMap) {
		String gpStr = "";
		String groupStr = "SELECT A FROM ProgramType A WHERE ";
		int i = 0;

		for (String gp : pgmTypeSet) {
			i++;
			gpStr += "UPPER(A.name) = '" + gp + "'";
			if (i < pgmTypeSet.size()) {
				gpStr += " or ";
			}
		}

		List<ProgramType> grpLst = null;
		if (!gpStr.equals("")) {
			groupStr += gpStr;
			TypedQuery<ProgramType> query = em.createQuery(groupStr, ProgramType.class);
			grpLst = query.getResultList();
		}

		for (ProgramType pg : grpLst) {
			pgmTypeMap.put(pg.getName().toUpperCase(), pg);
		}
		for (String pgName : pgmTypeSet) {
			if (pgmTypeMap.size() > 0) {
				if (!pgmTypeMap.containsKey(pgName)) {
					ProgramType pg = new ProgramType();
					pg.setName(pgName);
					em.persist(pg);
					pgmTypeMap.put(pgName, pg);
				}
			} else {
				ProgramType pg = new ProgramType();
				pg.setName(pgName);
				em.persist(pg);
				pgmTypeMap.put(pgName, pg);
			}
		}
	}

	public List<Customer> findCustomerNumbers(Set<String> customerNumbers) {
		TypedQuery<Customer> query = em.createQuery(
				"select distinct c from Customer c where c.customerNumber in :customerNumbers", Customer.class);
		query.setParameter("customerNumbers", customerNumbers);
		List<Customer> results = query.getResultList();
		return results;
	}

	/**
	 * Search all material that suitable for condition.
	 * 
	 * @param pricrLst
	 * @return
	 */
	public List<Material> findAllMaterial(List<PricerUploadTemplateBean> pricrLst, User user, List<Manufacturer> mfrLst,
			PricerUploadParametersBean puBean) {
		long start = System.currentTimeMillis();
		List<Material> mtlLst = null;

		Map<Object, Object> manfterNMap = puBean.getManfterNMap();
		Map<Object, Object> groupMap1 = puBean.getGroupMap1();
		Map<Object, Object> groupMap2 = puBean.getGroupMap2();
		Map<Object, Object> categoryMap = puBean.getCategoryMap();
		Map<Object, Object> pgmTypeMap = puBean.getPgmTypeMap();
		Map<Object, Object> regionMap = puBean.getRegionMap();

		Set<String> fullPartNumSet = new HashSet<String>();
		Set<Long> mftIdSet = new HashSet<Long>();
		Set<String> categorySet = new HashSet<String>();
		Set<String> pgmTypeSet = new HashSet<String>();
		Set<String> group1Set = new HashSet<String>();
		Set<String> group2Set = new HashSet<String>();
		Set<String> group3Set = new HashSet<String>();
		Set<String> group4Set = new HashSet<String>();
		// Set<String> regionSet = new HashSet<String>();
		for (PricerUploadTemplateBean pBean : pricrLst) {

			fullPartNumSet.add(pBean.getFullMfrPart());
			if (pBean.getProductCat() != null) {
				categorySet.add(pBean.getProductCat().toUpperCase());
			}
			if (!QuoteUtil.isEmpty(pBean.getProgram())) {
				pgmTypeSet.add(pBean.getProgram().toUpperCase());
			}

			if (pBean.getProductGroup1() != null && !pBean.getProductGroup1().equals("")) {
				group1Set.add(pBean.getProductGroup1().toUpperCase());
			}

			if (pBean.getProductGroup2() != null && !pBean.getProductGroup2().equals("")) {
				group2Set.add(pBean.getProductGroup2().toUpperCase());
			}

			if (pBean.getProductGroup3() != null && !pBean.getProductGroup3().equals("")) {
				group3Set.add(pBean.getProductGroup3());
			}

			if (pBean.getProductGroup4() != null && !pBean.getProductGroup4().equals("")) {
				group4Set.add(pBean.getProductGroup4());
			}
		}

		if (mfrLst.size() > 0) {

			for (Manufacturer mfr : mfrLst) {
				manfterNMap.put(mfr.getName().toUpperCase(), mfr.getId());
				mftIdSet.add(mfr.getId());
			}

			// If record with the same [MFR ]+ [Full MFR Part Number]+[Material
			// Type] is found in Material table, a new record will be inserted
			// into Material_Detail table only, with referring to the existing
			// one in Material table as foreign key.
			// if materialDetails is lazy
			String qryStr = "SELECT distinct A FROM Material A  WHERE A.manufacturer.id in :mftIds and A.fullMfrPartNumber in :fullPartNums";
			// if materialDetails is eager
			// String qryStr = "SELECT distinct A FROM Material A join fetch
			// A.materialDetails b WHERE 1=1 and A.manufacturer.id in :mftIds
			// and A.fullMfrPartNumber in :fullPartNums";
			TypedQuery<Material> matrQuery = em.createQuery(qryStr, Material.class);
			matrQuery.setParameter("mftIds", mftIdSet);
			matrQuery.setParameter("fullPartNums", fullPartNumSet);
			mtlLst = matrQuery.getResultList();

			// if product group'name can't find in db,it need to add(QC have the
			// authority to do this operation).
			if (group1Set.size() > 0) {
				findProductGroup(group1Set, groupMap1, "1");
			}

			if (group2Set.size() > 0) {
				findProductGroup(group2Set, groupMap2, "2");
			}

			if (pgmTypeSet.size() > 0) {
				saveProgramWhenNotExist(pgmTypeSet, pgmTypeMap);
			}

			// directly find the all region info from biz_unit so only need init
			// once
			if (regionMap.isEmpty()) {
				findAllBizUnit(regionMap);
			}

			if (categorySet != null && categorySet.size() > 0) {
				String catStr = "";
				String categoryStr = "SELECT a FROM ProductCategory a where ";
				int i = 0;
				for (String ct : categorySet) {
					i++;
					catStr += "UPPER(a.categoryCode) = '" + ct + "'";
					if (i < categorySet.size()) {
						catStr += " or ";
					}
				}
				categoryStr += catStr;
				TypedQuery<ProductCategory> categoryQuery = em.createQuery(categoryStr, ProductCategory.class);
				List<ProductCategory> ctPLst = categoryQuery.getResultList();
				for (ProductCategory ct : ctPLst) {
					categoryMap.put(ct.getCategoryCode(), ct);
				}
			}
		}

		// long end = System.currentTimeMillis();
		// LOG.info("findAllMaterial take " +(end -start)+"ms");
		return mtlLst;
	}

	@Transactional
	@TransactionTimeout(value=1, unit=TimeUnit.HOURS)
	public void initPricerUploadParametersBean(List<PricerUploadTemplateBean> pricrLst, User user,
			List<Manufacturer> mfrLst, PricerUploadParametersBean puBean) {
		long start = System.currentTimeMillis();
		// List<Material> mtlLst = null;
		// Map<Object, Object> manfterNMap = puBean.getManfterNMap();
		Map<Object, Object> groupMap1 = puBean.getGroupMap1();
		Map<Object, Object> groupMap2 = puBean.getGroupMap2();
		Map<Object, Object> categoryMap = puBean.getCategoryMap();
		Map<Object, Object> pgmTypeMap = puBean.getPgmTypeMap();
		Map<Object, Object> regionMap = puBean.getRegionMap();

		Set<String> fullPartNumSet = new HashSet<String>();
		/// Set<Long> mftIdSet = new HashSet<Long>();
		Set<String> categorySet = new HashSet<String>();
		Set<String> pgmTypeSet = new HashSet<String>();
		Set<String> group1Set = new HashSet<String>();
		Set<String> group2Set = new HashSet<String>();
		Set<String> group3Set = new HashSet<String>();
		Set<String> group4Set = new HashSet<String>();
		// Set<String> regionSet = new HashSet<String>();
		for (PricerUploadTemplateBean pBean : pricrLst) {

			fullPartNumSet.add(pBean.getFullMfrPart());
			if (pBean.getProductCat() != null) {
				categorySet.add(pBean.getProductCat().toUpperCase());
			}
			if (!QuoteUtil.isEmpty(pBean.getProgram())) {
				pgmTypeSet.add(pBean.getProgram().toUpperCase());
			}

			if (pBean.getProductGroup1() != null && !pBean.getProductGroup1().equals("")) {
				group1Set.add(pBean.getProductGroup1().toUpperCase());
			}

			if (pBean.getProductGroup2() != null && !pBean.getProductGroup2().equals("")) {
				group2Set.add(pBean.getProductGroup2().toUpperCase());
			}

			if (pBean.getProductGroup3() != null && !pBean.getProductGroup3().equals("")) {
				group3Set.add(pBean.getProductGroup3());
			}

			if (pBean.getProductGroup4() != null && !pBean.getProductGroup4().equals("")) {
				group4Set.add(pBean.getProductGroup4());
			}
		}

		if (mfrLst.size() > 0) {
			// if product group'name can't find in db,it need to add(QC have the
			// authority to do this operation).
			if (group1Set.size() > 0) {
				findProductGroup(group1Set, groupMap1, "1");
			}

			if (group2Set.size() > 0) {
				findProductGroup(group2Set, groupMap2, "2");
			}

			if (pgmTypeSet.size() > 0) {
				saveProgramWhenNotExist(pgmTypeSet, pgmTypeMap);
			}

			// directly find the all region info from biz_unit so only need init
			// once
			if (regionMap.isEmpty()) {
				findAllBizUnit(regionMap);
			}

			if (categorySet != null && categorySet.size() > 0) {
				String catStr = "";
				String categoryStr = "SELECT a FROM ProductCategory a where ";
				int i = 0;
				for (String ct : categorySet) {
					i++;
					catStr += "UPPER(a.categoryCode) = '" + ct + "'";
					if (i < categorySet.size()) {
						catStr += " or ";
					}
				}
				categoryStr += catStr;
				TypedQuery<ProductCategory> categoryQuery = em.createQuery(categoryStr, ProductCategory.class);
				List<ProductCategory> ctPLst = categoryQuery.getResultList();
				for (ProductCategory ct : ctPLst) {
					categoryMap.put(ct.getCategoryCode(), ct);
				}
			}
		}

		// long end = System.currentTimeMillis();
		// LOG.info("findAllMaterial take " +(end -start)+"ms");
	}

	/**
	 * [Product Group1] in Normal Pricer will be inserted into [Product Group 1]
	 * field in Material table. If the value is not found in Product_Group1
	 * table, system will insert the value in Product_Group1 table first, and
	 * then insert the new uploaded record into Material table, with referring
	 * to the record created in Product_Group1 as foreign key. [Product Group2]
	 * in Normal Pricer will be inserted into [Product Group 2] field in
	 * Material table. If the value is not found in Product_Group2 table, system
	 * will insert the value in Product_Group2 table first, and then insert the
	 * new uploaded record into Material table, with referring to the record
	 * created in Product_Group2 table as foreign key. For [Product Group 3] and
	 * [Product Group 4] fields, directly update the values in their
	 * corresponding fields in DB.
	 * 
	 * @param groupStrbf
	 * @param groupMap
	 */
	private void findProductGroup(Set<String> groupSet, Map<Object, Object> groupMap, String groupType) {
		String gpStr = "";
		String groupStr = "SELECT A FROM ProductGroup A WHERE ";
		int i = 0;

		for (String gp : groupSet) {
			i++;
			gpStr += "UPPER(A.name) = '" + gp + "'";
			if (i < groupSet.size()) {
				gpStr += " or ";
			}
		}

		List<ProductGroup> grpLst = null;
		if (!gpStr.equals("")) {
			groupStr += gpStr;
			TypedQuery<ProductGroup> groupQuery = em.createQuery(groupStr, ProductGroup.class);
			grpLst = groupQuery.getResultList();
		}

		// if product group'name can't find in db,it need to add(QC have the
		// authority to do this operation).
		for (ProductGroup pg : grpLst) {
			groupMap.put(pg.getName().toUpperCase(), pg);
		}

		// Can find a part of product group,the other need to be created in db.
		for (String pgName : groupSet) {
			if (groupMap.size() > 0) {
				if (!groupMap.containsKey(pgName)) {
					ProductGroup pg = new ProductGroup();
					pg.setName(pgName);
					em.persist(pg);
					groupMap.put(pgName, pg);
				}
			} else {
				ProductGroup pg = new ProductGroup();
				pg.setName(pgName);
				em.persist(pg);
				groupMap.put(pgName, pg);
			}
		}
	}

	private List<Integer> findACostIdct(List<PricerUploadTemplateBean> items, User user, List<Manufacturer> mfrLst,
			PricerUploadParametersBean puBean) {
		/** In order to increase performance and use native query */
		// long start = System.currentTimeMillis();
		BizUnit bz = user.getDefaultBizUnit();
		Set<String> fullPartNumSet = new HashSet<String>();
		List<Integer> errRows = new ArrayList<Integer>();
		Set<Long> mftIdSet = new HashSet<Long>();
		for (PricerUploadTemplateBean pBean : items) {
			if (pBean.getCostIndicator() != null
					&& !pBean.getCostIndicator().equalsIgnoreCase(PricerConstant.A_COST_INDICATOR)) {
				String fullMfrPart = PricerUtils.escapeSQLIllegalCharacters(pBean.getFullMfrPart());
				fullPartNumSet.add(fullMfrPart);
			}

			for (Manufacturer mfr : mfrLst) {
				if (mfr.getName().equalsIgnoreCase(pBean.getMfr())) {
					mftIdSet.add(mfr.getId());
					break;
				}
			}
		}
		if (mftIdSet.size() == 0 || fullPartNumSet.size() == 0) {
			// long end = System.currentTimeMillis();
			// LOG.info("verifyCstIdctInDB,takes " + (end - start) + " ms
			// fullPartNumSet.size():"+fullPartNumSet.size());
			return errRows;
		}
		String inMfr = PricerUtils.buildInSql(mftIdSet);
		String inFullPartNum = PricerUtils.buildInSql(fullPartNumSet);

		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT distinct t1.FULL_MFR_PART_NUMBER,t1.MANUFACTURER_ID  ");
		sb.append(" FROM PRICER t2, MATERIAL t1  where t1.ID = t2.MATERIAL_ID  and t2.BIZ_UNIT='" + bz.getName() + "'");
		sb.append(
				" and t2.PRICER_TYPE='NormalPricer' and t2.COST_INDICATOR = 'A-Book Cost'  and t1.FULL_MFR_PART_NUMBER in "
						+ inFullPartNum + " and t1.MANUFACTURER_ID in " + inMfr + " ");

		Query query = em.createNativeQuery(sb.toString());
		List<?> nlist = query.getResultList();

		Object[] obj = null;
		List<VerifyEffectiveDateResult> results = new ArrayList<VerifyEffectiveDateResult>();
		VerifyEffectiveDateResult result = null;
		for (int i = 0; i < nlist.size(); i++) {
			obj = (Object[]) nlist.get(i);
			result = new VerifyEffectiveDateResult();
			result.setFullMfrPartNumber((String) obj[0]);
			for (Manufacturer mfr : mfrLst) {
				if (mfr.getId() == Long.parseLong(obj[1].toString())) {
					result.setMfr(mfr.getName());
					break;
				}
			}

			results.add(result);
		}
		Map<String, String> dtMap = new HashMap<String, String>();
		for (VerifyEffectiveDateResult dt : results) {
			dtMap.put(dt.getMfr() + " " + dt.getFullMfrPartNumber(), null);
		}

		for (PricerUploadTemplateBean pricr : items) {
			if (!dtMap.containsKey(pricr.getMfr() + " " + pricr.getFullMfrPart())) {
				if (pricr.getCostIndicator() != null
						&& !pricr.getCostIndicator().equalsIgnoreCase(PricerConstant.A_COST_INDICATOR)) {
					if (!PricerUtils.isHaveABookCost(pricr, items)) {
						errRows.add(pricr.getLineSeq());
						pricr.setErr(true);
					}
				}

			}
		}
		// long end = System.currentTimeMillis();
		// LOG.info("verifyCstIdctInDB,takes " + (end - start) + " ms
		// fullPartNumSet.size():"+fullPartNumSet.size());
		return errRows;
	}

	/*****/
	private void findAllBizUnit(Map<Object, Object> regionMap) {
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT b from BizUnit b");
		Query query = em.createQuery(sb.toString());
		List<BizUnit> bizs = query.getResultList();
		for (BizUnit b : bizs) {
			regionMap.put(b.getName().toUpperCase(), b);
		}
	}

	public void savePricerUploadSummary2Db(ProgramItemUploadCounterBean countBean, String fileName, User user) {
		StringBuffer sb = new StringBuffer();
		sb.append(" Part Created:" + countBean.getAddedPartCount() + " ");
		sb.append(" Part Updated:" + countBean.getUpdatedPartCount() + " ");
		sb.append(" Part Removed:" + countBean.getRemovedPartCount() + " ");
		sb.append(" Normal Pricer Added:" + countBean.getAddedNormalCount() + " ");
		sb.append(" Normal Pricer Updated:" + countBean.getUpdatedNormalCount() + " ");
		sb.append(" Normal Pricer Removed:" + countBean.getRemovedNormalCount() + " ");
		sb.append(" Program Pricer Added:" + countBean.getAddedProgramCount() + " ");
		sb.append(" Program Pricer Updated:" + countBean.getUpdatedProgramCount() + " ");
		sb.append("	Program Pricer Removed:" + countBean.getRemovedProgramCount() + " ");
		sb.append("	Contract Pricer Added:" + countBean.getAddedContractPriceCount() + " ");
		sb.append("	Contract Pricer Updated:" + countBean.getUpdatedContractPriceCount() + " ");
		sb.append("	Contract Pricer Removed:" + countBean.getRemovedContractPriceCount() + "");
		sb.append("	Simple Pricer Added:" + countBean.getAddedSimplePricerCount() + " ");
		sb.append("	Simple Pricer Updated:" + countBean.getUpdatedSimplePricerCount() + " ");
		sb.append("	Simple Pricer Removed:" + countBean.getRemovedSimplePricerCount() + "");
		sb.append("	SalesCost Pricer Added:" + countBean.getAddedSalesCostPricerCount() + " ");
		sb.append("	SalesCost Pricer Updated:" + countBean.getUpdatedSalesCostPricerCount() + " ");
		sb.append("	SalesCost Pricer Removed:" + countBean.getRemovedSalesCostPricerCount() + "");
		PricerUploadSummary summary = new PricerUploadSummary();
		summary.setContent(sb.toString());
		summary.setCreatedOn(new Date());
		summary.setEmployeeId(user.getEmployeeId());
		summary.setFileName(fileName);
		summary.setBizUnit(user.getDefaultBizUnit().getName());
		try {
			ut.setTransactionTimeout(10000);
			ut.begin();
			em.persist(summary);
			ut.commit();
		} catch (SystemException | NotSupportedException | SecurityException | IllegalStateException | RollbackException
				| HeuristicMixedException | HeuristicRollbackException e) {
			LOG.log(Level.SEVERE,
					"Exception occurs in method savePricerUploadSummary2Db for " + fileName + " :: Biz unit :"
							+ user.getDefaultBizUnit().getName() + "Employee ID:: " + user.getEmployeeId() + ""
							+ e.getMessage(),
					e);
		}

	}

	/**
	 * merger from branch 2.1.12 for the missed function
	 * 
	 * @param manufacturerLst
	 * @return
	 */
	public List<PgMasterMapping> getPgMasterMapping(List<Manufacturer> manufacturerLst) {

		String qryStr = "SELECT A FROM PgMasterMapping A WHERE A.manufacturer.id in :manufacturer_ids";
		TypedQuery<PgMasterMapping> matrQuery = em.createQuery(qryStr, PgMasterMapping.class);
		Set<Long> set = new TreeSet<Long>();
		for (Manufacturer mfr : manufacturerLst) {
			set.add(mfr.getId());
		}
		matrQuery.setParameter("manufacturer_ids", set);
		List<PgMasterMapping> list = matrQuery.getResultList();
		return list;

	}

}