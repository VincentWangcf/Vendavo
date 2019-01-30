package com.avnet.emasia.webquote.commodity.ejb; 

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.Column;
import javax.persistence.Query;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.avnet.emasia.webquote.commodity.bean.PricerUploadParametersBean;
import com.avnet.emasia.webquote.commodity.bean.PricerUploadTemplateBean;
import com.avnet.emasia.webquote.commodity.bean.VerifyEffectiveDateResult;
import com.avnet.emasia.webquote.commodity.constant.PRICER_TYPE;
import com.avnet.emasia.webquote.commodity.constant.PricerConstant;
import com.avnet.emasia.webquote.commodity.constant.QED_CHECK_STATE;
import com.avnet.emasia.webquote.commodity.dao.PricerUploadCommonSB;
import com.avnet.emasia.webquote.commodity.util.CommodityUtil;
import com.avnet.emasia.webquote.commodity.util.PricerUtils;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.ContractPricer;
import com.avnet.emasia.webquote.entity.CostIndicator;
import com.avnet.emasia.webquote.entity.Currency;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.MaterialRegional;
import com.avnet.emasia.webquote.entity.NormalPricer;
import com.avnet.emasia.webquote.entity.Pricer;
import com.avnet.emasia.webquote.entity.ProductCategory;
import com.avnet.emasia.webquote.entity.ProgramPricer;
import com.avnet.emasia.webquote.entity.QuantityBreakPrice;
import com.avnet.emasia.webquote.entity.SalesCostPricer;
import com.avnet.emasia.webquote.entity.SalesCostType;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.exception.WebQuoteException;
import com.avnet.emasia.webquote.utilities.util.QuoteUtil;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class SalesCostPricerUploadSB  extends PricerUploadCommonSB {
	
	private static final Logger LOG =Logger.getLogger(SalesCostPricerUploadSB.class.getName());
	/**
	 *mfr + p/n + pricer type + region + salescost type((ZIM/(ZBMP/ZBMD)) ZBMP ARE THE SAME AS ZBMP) + QED
	 *
	 *
	 *@Temporal(TemporalType.TIMESTAMP)
	*@Column(name = "QUOTATION_EFFECTIVE_DATE")
	*private Date quotationEffectiveDate;
	
	*@Temporal(TemporalType.TIMESTAMP)
	 *@Column(name = "QUOTATION_EFFECTIVE_TO")
	 *private Date quotationEffectiveTo;

	 **/
	//TODO
	public SalesCostPricer getSCPricerByComponentKey(PricerUploadTemplateBean bean) {
		Material material = bean.getMaterial();
		if(material == null ||bean == null ){
			return null;
		}
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT p FROM SalesCostPricer p WHERE p.material = :material ");//AND p.pricerType =:pricerType
		sql.append(" AND p.currency = :currency ");
		sql.append(" AND p.bizUnitBean.name = :bizUnit ");
		sql.append(" AND  ");
		
		String sqlSalesTypeParam = null;
		if(SalesCostType.ZBMD.toString().equals(bean.getSalesCostType()) 
				|| SalesCostType.ZBMP.toString().equals(bean.getSalesCostType())) {
			sqlSalesTypeParam = " (p.salesCostType =:ZBMD  or  p.salesCostType =:ZBMP) " ;
		} else if (SalesCostType.ZINM.toString().equals(bean.getSalesCostType())) {
			sqlSalesTypeParam = " p.salesCostType =:ZINM ";
		}
		
		sql.append(sqlSalesTypeParam);
		
		String qedStringInFile = bean.getQuotationEffectiveDate();
		//displayQuoteEffDate quotationEffectiveTo quotationEffectiveDate
		/**
		 * qedStringInFile not empty
		 * must match all as below:
		 * first : displayQuoteEffDate must equals QED in excel
		 **/
		if(!QuoteUtil.isEmpty(qedStringInFile)){
			/*sql.append(" AND (p.displayQuoteEffDate =:effectiveForm AND (p.quotationEffectiveDate =:effectiveForm "
					+" OR p.quotationEffectiveDate IS NUL OR p.quotationEffectiveTo IS NUL "
					+" OR NOT (p.quotationEffectiveDate <=:effectiveTo AND p.quotationEffectiveTo >=:effectiveForm)"
					+ "))");*/
			sql.append(" AND p.displayQuoteEffDate =:effectiveForm ");
		}else {
			//qedStringInFile is empty only when displayQuoteEffDate is null ,this situation can be matched
			sql.append(" AND p.displayQuoteEffDate IS NULL");
		}
			
		Query query = em.createQuery(sql.toString());
		query.setParameter("material", material);
		query.setParameter("currency", Currency.valueOf(bean.getCurrency()));
		
		///query.setParameter("pricerType", PRICER_TYPE.SALESCOST.getName());
		query.setParameter("bizUnit", bean.getRegion());
		
		if(SalesCostType.ZBMD.toString().equals(bean.getSalesCostType()) 
				|| SalesCostType.ZBMP.toString().equals(bean.getSalesCostType())) {
			query.setParameter("ZBMD", SalesCostType.ZBMD);
			query.setParameter("ZBMP", SalesCostType.ZBMP);
		}else if (SalesCostType.ZINM.toString().equals(bean.getSalesCostType())) {
			query.setParameter("ZINM", SalesCostType.ZINM);
		}
		/*// FOR PARAMETER :salesCostType
		if(SalesCostType.ZBMD.toString().equals(bean.getSalesCostType()) 
				|| SalesCostType.ZBMP.toString().equals(bean.getSalesCostType())) {
			query.setParameter("salesCostType", 
					"('" + SalesCostType.ZBMD.toString() +"','" + SalesCostType.ZBMP.toString() + "')");
		} else {
			query.setParameter("salesCostType", "('" + bean.getSalesCostType() + "')");
		}*/
		// FOR displayQuoteEffDate quotationEffectiveTo quotationEffectiveDate
		if(!QuoteUtil.isEmpty(qedStringInFile)) {
			Date effectiveForm = PricerUtils.getEffectiveFrom(qedStringInFile);
			//Date effectiveTo = PricerUtils.getEffectiveTo(effectiveForm, bean.getValidity());
			query.setParameter("effectiveForm", effectiveForm);
			//query.setParameter("effectiveTo", effectiveTo);
		}
		List<SalesCostPricer> list = query.getResultList();
		if(list.isEmpty()) {
			return null;
		}
		//Log
		return (SalesCostPricer)list.get(0);			
	}
	//TODO
	public List<Pricer> getAllOverLapSCPricers(PricerUploadTemplateBean bean, Long pricerId) {
		Material material = bean.getMaterial();
		if(material == null ||bean == null ){
			
			return new ArrayList<Pricer>();
		}
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT p FROM SalesCostPricer p WHERE p.material = :material ");//AND p.pricerType =:pricerType 
		sql.append(" AND p.currency = :currency ");
		sql.append(" AND p.bizUnitBean.name = :bizUnit ");
		sql.append(" AND  ");
		
		String sqlSalesTypeParam = null;
		if(SalesCostType.ZBMD.toString().equals(bean.getSalesCostType()) 
				|| SalesCostType.ZBMP.toString().equals(bean.getSalesCostType())) {
			sqlSalesTypeParam = " (p.salesCostType =:ZBMD  or  p.salesCostType =:ZBMP) " ;
		} else if (SalesCostType.ZINM.toString().equals(bean.getSalesCostType())) {
			sqlSalesTypeParam = " p.salesCostType =:ZINM ";
		}
		
		sql.append(sqlSalesTypeParam);
		//we use displayEffectiveDate to match ,but use the quotationEffectiveDate to check overlap.
		sql.append(" AND (p.quotationEffectiveDate <=:effectiveTo AND p.quotationEffectiveTo >=:effectiveForm) ");
		// need to no include matched prcier.
		if(pricerId!=null && pricerId>0) {
			sql.append(" AND  p.id <> :id ");
		}
		String qedStringInFile = bean.getQuotationEffectiveDate();
		Date effectiveForm = PricerUtils.getEffectiveFrom(qedStringInFile);
		Date effectiveTo = PricerUtils.getEffectiveTo(effectiveForm, bean.getValidity());
		Query query = em.createQuery(sql.toString());
		query.setParameter("material", material);
		query.setParameter("currency", Currency.valueOf(bean.getCurrency()));
		query.setParameter("bizUnit", bean.getRegion());
		// FOR PARAMETER :salesCostType
		if(SalesCostType.ZBMD.toString().equals(bean.getSalesCostType()) 
				|| SalesCostType.ZBMP.toString().equals(bean.getSalesCostType())) {
			query.setParameter("ZBMD", SalesCostType.ZBMD);
			query.setParameter("ZBMP", SalesCostType.ZBMP);
		}else if (SalesCostType.ZINM.toString().equals(bean.getSalesCostType())) {
			query.setParameter("ZINM", SalesCostType.ZINM);
		}
		if(pricerId!=null && pricerId>0) {
			query.setParameter("id", pricerId);
		}
		query.setParameter("effectiveForm", effectiveForm);
		query.setParameter("effectiveTo", effectiveTo);
		return query.getResultList();
	}
	
	
	@Override
	public void insertUploadOperater(List<PricerUploadTemplateBean> items, String materialTypeStr,User user, List<Manufacturer> manufacturerLst, int[] countArr, PricerUploadParametersBean puBean)  {
		long start = System.currentTimeMillis();
		//need some   persist  obj to fill .
		initPricerUploadParametersBean(items, user, manufacturerLst, puBean);
		//for next item update last item when has this situation.
		items.sort(new Comparator<PricerUploadTemplateBean> (){

			@Override
			public int compare(PricerUploadTemplateBean arg0, PricerUploadTemplateBean arg1) {
				return arg0.getLineSeq() - arg1.getLineSeq();
			}
			
		});
		for (PricerUploadTemplateBean bean : items) {
			if (QED_CHECK_STATE.INSERT.getName().equals(bean.getQedCheckState())) {
				//need TO TRY TO get pricer again from DB.
				bean.setPricer(this.getSCPricerByComponentKey(bean));
				if(bean.getPricer() !=null) {
					bean.setQedCheckState(QED_CHECK_STATE.UPDATE.getName());
				}else {
					try {
						bean.setPricer(new SalesCostPricer());
						SalesCostPricer pricer = PricerUtils.convert((SalesCostPricer)bean.getPricer(), bean, user, puBean);
						//insert init status is 0;
						pricer.setBatchStatus(0);
						em.persist(pricer);
						insertQuantityBreakPrice(pricer);
						em.flush();
						
						countArr[10]++;// addedNormalCount++;
					} catch (Exception e) {
						LOG.log(Level.SEVERE, "insertSCPricer error : " + materialTypeStr
								+ " :: user Id and name " + user.getId() + " & " + user.getName() + e.getMessage(), e);
					}
				}
			}
			//
			if (QED_CHECK_STATE.UPDATE.getName().equals(bean.getQedCheckState())) {
				try {
					removeQuantityBreakPrice(bean);
					
					//reload to avoid Optimistic Lock
					bean.setPricer(em.find(SalesCostPricer.class, bean.getPricer().getId()));
					SalesCostPricer pricer = (SalesCostPricer)bean.getPricer();
					this.InsertIntoPricerDeletedByPricerId(pricer.getId(), user);
					pricer.setBatchStatus(0);
					//if the qed or qedto has any changes , the pricer should be save into pricer_delted
					//and set  BatchStatus =0
					/*Date effectiveForm = PricerUtils.getEffectiveFrom( bean.getQuotationEffectiveDate());
					Date effectiveTo = PricerUtils.getEffectiveTo(effectiveForm, bean.getValidity());*/
					/*if(isQedOrQedToChange(effectiveForm,effectiveTo,
							pricer.getQuotationEffectiveDate(),pricer.getQuotationEffectiveTo())){
						this.InsertIntoPricerDeletedByPricerId(pricer.getId(), user);
						pricer.setBatchStatus(0);
					}else{
						//update  status is 1;
						pricer.setBatchStatus(1);
					}*/
					pricer = PricerUtils.convert(pricer, bean, user, puBean);
					insertQuantityBreakPrice(pricer);
					em.merge(pricer);
					countArr[11]++; // updatedNormalCount++;
				} catch (Exception e) {
					LOG.log(Level.SEVERE, "updateSCPricer error : " + materialTypeStr
							+ " :: user Id and name " + user.getId() + " & " + user.getName() + e.getMessage(), e);
				}
			} 
		}
		long end = System.currentTimeMillis();
		LOG.info("insertUploadOperater-SalesCost,takes " + (end - start) + " ms  processed line:"+items.get(items.size()-1).getLineSeq());
	}
	
	public boolean isQedOrQedToChange(Date qedInFile, Date qedToInFile, Date qedInDB, Date qedToInDB){
		if(qedInFile==null||qedToInFile==null||qedInDB==null||qedToInDB==null){
			return true;
		}
		return !(qedInFile.equals(qedInDB) && qedToInFile.equals(qedToInDB));
	}
	
	private void removeQuantityBreakPrice(PricerUploadTemplateBean pricr){
		long materialDetailId = pricr.getPricer().getId();
		//TODO a.materialDetail.id
		Query query = em.createQuery("delete FROM QuantityBreakPrice a where a.materialDetail.id = :matDtlId");
		query.setParameter("matDtlId", materialDetailId);
		int rowCount = query.executeUpdate();
		//LOG.info("delete QuantityBreakPrice size :"+ rowCount);
	}
	private void insertQuantityBreakPrice(SalesCostPricer pricer){
		for (QuantityBreakPrice abp : pricer.getQuantityBreakPrices()) {
			abp.setMaterialDetail(pricer);
			em.persist(abp);
		}
	}
	
	@Override
	public void removeOnlyPricerOperater(List<PricerUploadTemplateBean> items, String pricerType, User user,  int[] countArr) {
		 this.removeOnlyMaterialDetail(items, user,countArr,pricerType);		
	}
		

	private void removeOnlyMaterialDetail( List<PricerUploadTemplateBean> beans, User user,int[] countArr,String pricerType) {
		
		StringBuffer sb = new StringBuffer("(");
		for(PricerUploadTemplateBean bean:beans){
			sb.append(bean.getPricer().getId()).append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append(")");
		this.insertIntoPricerDeletedByPricerIds(sb.toString(), user);
		String deleteQuantityBreakPrice = "DELETE FROM QuantityBreakPrice e WHERE e.materialDetail.id in " +sb.toString();
		Query deleteQbpQuery = em.createQuery(deleteQuantityBreakPrice);
		deleteQbpQuery.executeUpdate();
		String hql = "DELETE FROM Pricer e WHERE e.id in " +sb.toString();
		Query query = em.createQuery(hql);
		int rowCount = query.executeUpdate();
		countArr[4] = countArr[4]+rowCount;
		LOG.info("remove only pricer - salesCost count :"+ countArr[4]);

	}
}