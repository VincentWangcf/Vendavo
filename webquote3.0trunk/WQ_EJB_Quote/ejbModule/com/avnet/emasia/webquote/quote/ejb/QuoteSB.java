package com.avnet.emasia.webquote.quote.ejb;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.beanutils.BeanUtils;

import com.avnet.emasia.webquote.cache.ICacheUtil;
import com.avnet.emasia.webquote.constants.ActionEnum;
import com.avnet.emasia.webquote.constants.BmtFlagEnum;
import com.avnet.emasia.webquote.dp.xml.requestquote.DropDownFilters;
import com.avnet.emasia.webquote.ejb.interceptor.EntityManagerInjector;
import com.avnet.emasia.webquote.entity.Attachment;
import com.avnet.emasia.webquote.entity.AuditExchangeRate;
import com.avnet.emasia.webquote.entity.AuditQuoteItem;
import com.avnet.emasia.webquote.entity.AuditQuoteItemPK;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.BmtFlag;
import com.avnet.emasia.webquote.entity.ContractPricer;
import com.avnet.emasia.webquote.entity.CostIndicator;
import com.avnet.emasia.webquote.entity.Currency;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.DataAccess;
import com.avnet.emasia.webquote.entity.DrmsAgpRea;
import com.avnet.emasia.webquote.entity.ExchangeRate;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.Money;
import com.avnet.emasia.webquote.entity.ProductGroup;
import com.avnet.emasia.webquote.entity.QuotationEmail;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.QuoteItemDesign;
import com.avnet.emasia.webquote.entity.QuoteItemDesignHis;
import com.avnet.emasia.webquote.entity.QuoteItemHis;
import com.avnet.emasia.webquote.entity.QuoteItemHisPK;
import com.avnet.emasia.webquote.entity.QuoteNumber;
import com.avnet.emasia.webquote.entity.QuoteToSo;
import com.avnet.emasia.webquote.entity.ReferenceMarginSetting;
import com.avnet.emasia.webquote.entity.SalesCostType;
import com.avnet.emasia.webquote.entity.Team;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.entity.util.StringUtils;
import com.avnet.emasia.webquote.exception.AppException;
import com.avnet.emasia.webquote.exception.WebQuoteException;
import com.avnet.emasia.webquote.exception.WebQuoteRuntimeException;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.quote.strategy.factory.DrmsValidationUpdateStrategyFactory;
import com.avnet.emasia.webquote.quote.strategy.interfaces.DrmsValidationUpdateStrategy;
import com.avnet.emasia.webquote.quote.vo.AuditExchangeReportSearchCriteria;
import com.avnet.emasia.webquote.quote.vo.AuditLogReportSearchCriteria;
import com.avnet.emasia.webquote.quote.vo.AuditReportSearchCriteria;
import com.avnet.emasia.webquote.quote.vo.MyQuoteSearchCriteria;
import com.avnet.emasia.webquote.quote.vo.QuoteItemVo;
import com.avnet.emasia.webquote.quote.vo.WorkingPlatformItemVO;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilities.DateUtils;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;
import com.avnet.emasia.webquote.utilities.util.BeanUtilsExtends;
import com.avnet.emasia.webquote.utilities.util.QuoteUtil;
import com.avnet.emasia.webquote.vo.PricerInfo;
import com.avnet.emasia.webquote.vo.RfqItemVO;
import com.avnet.webquote.quote.ejb.utility.EJBQuoteComparator;
import com.avnet.webquote.quote.ejb.utility.EJBQuoteUtility;
import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

/**
 * Session Bean implementation class QuoteSB
 */
@Stateless
@LocalBean
@Interceptors(EntityManagerInjector.class)
public class QuoteSB {
	private static final Logger LOGGER = Logger.getLogger(QuoteSB.class.getName());
	@PersistenceContext(unitName="Server_Source")
	EntityManager em;

	@EJB
	QuoteNumberSB quoteNumberSB;
	
	@EJB
	ExchangeRateSB exchangeRateSB;

	@EJB
	UserSB userSB;
	
	@EJB
	MaterialSB materialSB;

	@EJB
	ManufacturerSB manufacturerSB;
	
	@EJB
	SysConfigSB sysConfigSB;
	
	@EJB
	CostIndicatorSB costIndicatorSB;
	
	@EJB
	SAPWebServiceSB sapWebServiceSB;
	
	@EJB
	CopyRefreshQuoteSB copyQuoteSB;
	

	@EJB
	MyQuoteSearchSB myQuoteSearchSB;
	
//	@EJB
//	ICacheUtil cacheUtil;

	
	private static final Logger LOG =Logger.getLogger(QuoteSB.class.getName());
	private static final long DEFAULT_LONG = 99999l;
	//private static final String SQL_QUERY_PROG_DRAFT_RFQ = "select b from Quote b where b.stage=:stage and b.bizUnit =:bizUnit and b.lastUpdatedBy=:lastUpdatedBy and b.lastUpdatedOn >= :expireDate";
	private static final String SQL_QUERY_PROG_DRAFT_RFQ = "select b from Quote b where b.stage=:stage and b.bizUnit =:bizUnit and b.lastUpdatedBy=:lastUpdatedBy ";
	private static final String SQL_QUERY_PROG_DRAFT_RFQ_WITH_EXPIRE = "select b from Quote b where b.stage=:stage and b.bizUnit =:bizUnit and b.lastUpdatedBy=:lastUpdatedBy ";
	//private static final String SQL_QUERY_PROG_DRAFT_RFQ_QITEM_COUNT = "select count(q) from QuoteItem b where b.stage=:stage and b.bizUnit =:bizUnit and b.lastUpdatedBy=:lastUpdatedBy";
	public static final String OPTION_YES = "Yes";
	public static final String OPTION_NO = "No";
	/**
	 * Default constructor. 
	 */
	public QuoteSB() {
	}

	public QuoteItem findByPK(long id){
		return em.find(QuoteItem.class, id);
	}

	public Quote findQuoteByPK(long id){
		return em.find(Quote.class, id);
	}

	public Quote findQuoteByFormNumber(String formNumber, BizUnit bizUnit){
		String sql = "select q from Quote q where q.formNumber = :formNumber and q.bizUnit = :bizUnit";
		Query query = em.createQuery(sql);
		query.setParameter("bizUnit", bizUnit);		
		query.setParameter("formNumber", formNumber);		
		return (Quote)query.getSingleResult();       			
	}

	public List<Quote> findQuoteByFormNumber(String formNumber){
		String sql = "select q from Quote q where UPPER(q.formNumber) like :formNumber";
		Query query = em.createQuery(sql, Quote.class);
		query.setParameter("formNumber", "%" + formNumber.toUpperCase() + "%");
		return query.setFirstResult(0).setMaxResults(10).getResultList();
	}

	public List<Quote> findQuoteByEmployeeID(String employeeId){
		String sql = "select q from Quote q where q.sales.employeeId = :employeeId"; 
		Query query = em.createQuery(sql, Quote.class);
		query.setParameter("employeeId", employeeId);
		return query.getResultList();
	}

	public QuoteItem findQuoteItemByQuoteNumber(String quoteNumber){
		String sql = "select q from QuoteItem q where q.quoteNumber = :quoteNumber";
		Query query = em.createQuery(sql);
		query.setParameter("quoteNumber", quoteNumber);
		try {
			return (QuoteItem)query.getSingleResult();
		} catch (Exception ex){
			LOG.log(Level.WARNING, "Exception in getting Quote Item for quote Number : "+quoteNumber);
			return null;
		}
	}
	
	public List<QuoteItem> findQuoteItemsByPKs(List<Long> quoteItemIds){
		String sql = "select q from QuoteItem q where q.id in :quoteItemIds";
		Query query = em.createQuery(sql);
		query.setParameter("quoteItemIds", quoteItemIds);
		try {
			return query.getResultList();
		} catch (Exception ex){
			LOG.log(Level.WARNING, "Exception in getting Quote Item list");
			return null;
		}
	}	

	public List<QuoteToSo> findQuoteToSoList(){
		Date date = new Date();
		Date endDate = QuoteUtil.getEndOfPostQuotation(date);
		long diff = 1000 * 60 * 5;
		Date startDate = new Date(endDate.getTime() - diff);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		
		String sql = "select q from QuoteToSo q where q.status = 1 ";
		Query query = em.createQuery(sql);
		try {
			return query.getResultList();
		} catch (Exception ex){
			LOG.log(Level.WARNING, "Exception in getting QuoteToSo list");
			return null;
		}
	}

	public void removeQuoteToSoList(List<QuoteToSo> quoteToSos){

		LOG.log(Level.INFO, "removeQuoteToSoList");  

		for(QuoteToSo quoteToSo : quoteToSos){
			quoteToSo.setStatus(false);
			em.merge(quoteToSo);
		}
		em.flush();

	}

	public void removeQuoteToSoList(String cutOffTime){
		Query query = em.createNativeQuery("BEGIN UPDATE_POST_QUOTATION_Q_TO_S('"+cutOffTime+"'); END;");
	    query.executeUpdate();
	}	
	
	public List<String> findProjectNameBySoldToInQuoteHistory(String soldToCustomerNumber){

		List<String> projects = em.createQuery("select distinct q.projectName from QuoteItem q where q.projectName is not null and q.soldToCustomer.customerNumber = :soldToCustomerNumber")
				.setParameter("soldToCustomerNumber", soldToCustomerNumber)
				.getResultList();

		return projects;

	}

	public List<String> findProjectNameInQuoteHistory(String key){

		List<String> projects = em.createQuery("select distinct q.projectName from QuoteItem q where q.projectName is not null and upper(q.projectName) like :projectName")
				.setParameter("projectName", "%"+key.toUpperCase()+"%")
				.getResultList();

		return projects;

	}

	public List<String> findApplicationNameBySoldToInQuoteHistory(String soldToCustomerNumber){

		List<String> applications = em.createQuery("select distinct q.application from QuoteItem q where q.application is not null and q.soldToCustomer.customerNumber = :soldToCustomerNumber")
				.setParameter("soldToCustomerNumber", soldToCustomerNumber)
				.getResultList();

		return applications;
	}


	public Quote saveQuote(Quote quote){
		
		Quote savedQuote = null;

		quote = populateFormNumberForDraft(quote);
		
		try{
			savedQuote = em.merge(quote);
			em.flush();	
			//em.refresh(quote);
			//LOG.info("refresh=records==>>> "+quote.getQuoteItems().size());
			//em.clear();					
		} catch (Exception ex){
			
			LOG.log(Level.WARNING, "Error saving Quote : "+quote.getId()+" , Exeception message : "+ex.getMessage());
		}
		return savedQuote;			
	}

	public QuoteItem createAlternativeQuoteItem(QuoteItem quoteItem){
		try{
			em.persist(quoteItem);
			em.flush();				
			//em.clear();					
		} catch (Exception ex){
			LOG.log(Level.WARNING, "EXception in create Alternative quote item, Quote ID : "+quoteItem.getId()+" , message : "+ex.getMessage());
		}
		return quoteItem;			
	}
	
	public List<QuoteItem> saveQuoteItems(List<QuoteItemVo> quoteItemVos){
		LOG.info("<<<<== Update Old Item ===>>> ");
		List<QuoteItem> qiReturn = new ArrayList<QuoteItem>();
		
		for(QuoteItemVo vo : quoteItemVos){
			QuoteItem quoteItem = em.merge(vo.getQuoteItem());
			qiReturn.add(quoteItem);
			LOG.info("Current QuoteItem's Quote Number ===>>> " + quoteItem.getQuoteNumber() + ",Current Version===>>>" + quoteItem.getVersion());
		}
		em.flush();	
		
		return qiReturn;
	}
	
	/** @author Damon??Chen
	 * @date Created Date??2016??9??14?? ????12:00:06
	   @Description: 
	   @param List<QuoteItems>
	 * @return  void
	 * @throws  
	 */
	
	public void saveQuoteItemsWithParamItem(List<QuoteItem> quoteItems){
		LOG.info("<<<<== Update Old Item ===>>> ");
		for(QuoteItem q : quoteItems){
			QuoteItem quoteItem = em.merge(q);	
			LOG.info("Current QuoteItem's Quote Number ===>>> " + quoteItem.getQuoteNumber() + ",Current Version===>>>" + quoteItem.getVersion());
		}
		em.flush();		
	}
	
	public List<QuoteItem> updateQuoteItems(List<QuoteItem> quoteItemVos){
		List<QuoteItem> outcome = new ArrayList<QuoteItem>(quoteItemVos.size());
			for(QuoteItem vo : quoteItemVos){
				outcome.add(em.merge(vo));						
			}
		em.flush();
		return outcome;
	}
	
	public List<QuoteItem> updateQuoteItems(List<QuoteItem> quoteItemVos, ActionEnum action){
		List<QuoteItem> outcome = new ArrayList<QuoteItem>(quoteItemVos.size());
		for(QuoteItem vo : quoteItemVos){
			vo.setAction(action.name());
			outcome.add(em.merge(vo));						
		}
		em.flush();
		return outcome;
	}
	
	/**
	 * save quotes to DB
	 * @param quotes
	 */
	public List<Quote> saveQuote(List<Quote> quotes){
		List<String> optimisticLockItems = new ArrayList<String>();
		List<Quote> qReturn = new ArrayList<Quote>();
		
		for(Quote vo : quotes){
			try{
				Quote q = em.merge(vo);
				qReturn.add(q);
				em.flush();	
			}catch(OptimisticLockException e){
				LOG.log(Level.WARNING, "EXception in saveQuote for quote item, Form number : "+vo.getFormNumber()+" , message : "+e.getMessage());
				optimisticLockItems.add(vo.getFormNumber());
			}
		}
		if(optimisticLockItems.size() != 0 ){
			String s = "RFQ " + optimisticLockItems.toString() + " has/have been modified by other users. Please refresh and submit again.";
			throw new RuntimeException(s);
		}
		
		return qReturn;
	}


	public QuoteItem saveQuoteItem(QuoteItem quoteItem) {
		QuoteItem savedQuoteItem = null;
		savedQuoteItem = em.merge(quoteItem);
		em.flush();
		return savedQuoteItem;
	}

	public void submitRFQs(List<Quote> quotes){
		for(Quote quote:quotes){
			submitRFQ(quote);
		}
	}
	
	
	public String submitRFQ(Quote quote){


		String formNumber = populateQuoteNumber(quote);
		for(QuoteItem item:quote.getQuoteItems()){
			for(QuoteItem item2:quote.getQuoteItems()){
				if(item.getDupMatchCode() !=null&& item.getDupMatchCode().trim().substring(0, 6).equals("childr")){
					if(item2.getDupMatchCode() !=null && item2.getDupMatchCode().trim().substring(0, 6).equals("parent") && item2.getDupMatchCode().trim().substring(6).equals(item.getDupMatchCode().trim().substring(6)) ){
						item.setFirstRfqCode(item2.getQuoteNumber());
						break;
					}
				}
			}
			
		}
		//fixed the first quote is null issue .
		if(quote!=null && quote.getQuoteItems()!=null && quote.getQuoteItems().size()>0)
		{
			for(QuoteItem qi : quote.getQuoteItems())
			{
				//fixed the issue 1327
				if((QuoteSBConstant.QUOTE_STAGE_FINISH.equalsIgnoreCase(qi.getStage()) || QuoteSBConstant.QUOTE_STAGE_PENDING.equalsIgnoreCase(qi.getStage())) && qi.getFirstRfqCode()==null && qi.getQuoteNumber()!=null)
				{
					qi.setFirstRfqCode(qi.getQuoteNumber());
				}
			}
		}
		if (quote != null)
		{
			if(quote.getId() > 0) {
				em.merge(quote);
			}
			else
			{
				em.persist(quote);
			}
		}
		em.flush();				
		//em.clear();			
		return formNumber;
		/*
    	try {
    		em.merge(quote);
    	} catch (Exception ex){
    		LOG.log(Level.SEVERE, ex.getMessage());
    		em.persist(quote);    		
    	}

    	return formNumber;
		 */    	
	}

	public String postQuotation(List<QuoteItem> quoteItems, long userId){
		String quoteItemIds = "";
		for(QuoteItem quoteItem : quoteItems){
			if(quoteItemIds.length() > 0)
				quoteItemIds += ",";
			quoteItemIds += quoteItem.getId();
		}
		Query query = em.createNativeQuery("BEGIN POST_QUOTATION_PROC('"+quoteItemIds+"', "+String.valueOf(userId)+"); END;");
	    Object obj = query.executeUpdate();
	    return obj.toString();
	}
   
	public QuoteItem updateQuoteItem(QuoteItem quoteItem){
		QuoteItem item = em.merge(quoteItem);
		em.flush();
		em.clear();	    	
		return item;
	}
	
	public QuoteItemDesign updateQuoteItemDesign(QuoteItemDesign itemDesign){
		QuoteItemDesign design = em.merge(itemDesign);
		em.flush();
		em.clear();	    	
		return design;
	}
	
	public Attachment addAttachment(Attachment att) {
		Attachment attachment = em.merge(att);
		em.flush();
		em.clear();	    	
		return attachment;
	}
	
	public List<Attachment> addAttachment(List<Attachment> atts) {
		List<Attachment> attachments = new ArrayList<Attachment>();
		for(Attachment att: atts) {
			Attachment attachment = em.merge(att);
			em.flush();
			attachments.add(attachment);
			em.clear();
		}
		   
		return attachments;
		
	}

	/*
	public List<QuoteItem> updateQuoteItem(List<QuoteItem> quoteItems){
		for(QuoteItem item : quoteItems){
			item = em.merge(item);
		}
		em.flush();		
		em.clear();	
		return quoteItems;
	}
	*/
	
	/**
	 * 
	 * @param quote
	 * @return
	 */
	public Quote populateFormNumber(Quote quote){
		QuoteNumber formNumber = null;
		if(isNotAllDraft(quote))
		{
			formNumber = quoteNumberSB.lockNumber("FRM",quote.getBizUnit());
		}
		else
		{
			formNumber = quoteNumberSB.lockNumber("DRF",quote.getBizUnit());
		}
		//Added by Tonmy on 12 Sep 2013 . for issue 978
		if(!quote.isContinueSubmit())
		{
			quote.setFormNumber(quoteNumberSB.nextNumber(formNumber));
		}
		else
		{
			//fix issue: 1279, 1319
			if(quote.getFormNumber()!=null && quote.getFormNumber().startsWith("DF") && isNotAllDraft(quote))
			{
				quote.setFormNumber(quoteNumberSB.nextNumber(formNumber));
			}
		}
		return quote;
	}
	
	/**
	 * calculate quoteNumber according to a quote
	 * @author 916138
	 * @param quote
	 */
	public void calculateQuoteNumber(Quote quote, QuoteItem item) {
		QuoteNumber quoteNumber = quoteNumberSB.lockNumber("QUO",quote.getBizUnit());
		quote = calculateQuoteByQuoteNumber(quoteNumber,  quote,  item);
	}

	public Quote calculateQuoteByQuoteNumber(QuoteNumber quoteNumber, Quote quote, QuoteItem item){
		if(quote.isContinueSubmit())
		{
			if(item.getQuoteNumber()==null || item.getQuoteNumber().isEmpty())
			{
				String quoteNo = quoteNumberSB.nextNumber(quoteNumber);
				item.setQuoteNumber(quoteNo);
				item.setAlternativeQuoteNumber(quoteNo);
				if(item.getFirstRfqCode() == null){
					item.setFirstRfqCode(quoteNo);
				}
			} else {
				item.setAlternativeQuoteNumber(item.getQuoteNumber());					
			}
		}
		else
		{
			String quoteNo = quoteNumberSB.nextNumber(quoteNumber);
			item.setQuoteNumber(quoteNo);
			item.setAlternativeQuoteNumber(quoteNo);
			if(item.getFirstRfqCode() == null){
				item.setFirstRfqCode(quoteNo);
			}
		}
		
		return quote;
	}
	
	public String populateQuoteNumber(Quote quote){
		QuoteNumber quoteNumber = quoteNumberSB.lockNumber("QUO",quote.getBizUnit());
		quote=populateFormNumber(quote);
		for(QuoteItem item : quote.getQuoteItems()){
			quote = calculateQuoteByQuoteNumber(quoteNumber,  quote,  item);
		}    	
		return quote.getFormNumber();
	}
	
	

	public String populateQuoteNumberOnly(BizUnit bizunit){

		QuoteNumber quoteNumber = quoteNumberSB.lockNumber("QUO",bizunit);
		String quoteNo = quoteNumberSB.nextNumber(quoteNumber);
		return quoteNo;
	}

    public List<QuoteItem> findQuotesByMfrAndBizUnit(List<String> mfrs, BizUnit bizUnit, List<String> materialTypes, String quoteStage, List<String> quoteNumbers){
    	
    	String sql = "select i from QuoteItem i where i.quote.bizUnit = :bizUnit and i.stage = :quoteStage and i.requestedMaterial.manufacturer.name in :mfrs and i.quoteNumber is not null ";
    	if(quoteNumbers != null){
    		sql += "and i.quoteNumber in :quoteNumbers ";
    	}
    	sql += "order by i.alternativeQuoteNumber";
    	TypedQuery<QuoteItem> query = em.createQuery(sql, QuoteItem.class);
    	
    	query.setParameter("mfrs", mfrs)
    	.setParameter("bizUnit", bizUnit)
    	.setParameter("quoteStage", quoteStage);
    	if(quoteNumbers != null){
        	query.setParameter("quoteNumbers", quoteNumbers);
    	}
    	List<QuoteItem> qis = query.getResultList();
		LOG.log(Level.INFO, "result size = " + qis.size());    	
    	return qis;
    	
    }

	public boolean removeRFQ(String rfqCode){
		Quote quote = em.find(Quote.class, rfqCode);
		if(quote == null){
			return false;
		}
		em.remove(quote);
		return true;
	}

	public Attachment getAttachment(long id){

		Attachment attachment = em.find(Attachment.class, id);
		attachment.getFileImage();
		return attachment;
	}

	public List<Attachment> findAttachments(long id, String type){

		if(type.equalsIgnoreCase(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE)){
			return em.createQuery("select a from Attachment a where a.quote.id = ?1 and a.type= ?2 ", Attachment.class)
					.setParameter(1, id)
					.setParameter(2, type)
					.getResultList();

		}else{
			return em.createQuery("select a from Attachment a where a.quoteItem.id = ?1 and a.type= ?2 ", Attachment.class)
					.setParameter(1, id)
					.setParameter(2, type)
					.getResultList();

		}

	}
	
	
	public List<Attachment> findAttachments(QuoteItem item,List<String> types) {

		List<Attachment> attachments = new ArrayList<Attachment>();

		if(item != null && item.getId()!=0){

			for(Attachment attachment : item.getAttachments()){
				if(types.contains(attachment.getType())){
					attachments.add(attachment);
				}
			}

			for(Attachment attachment : item.getQuote().getAttachments()){
				if(types.contains(attachment.getType())){
					attachments.add(attachment);
				}
			}

			return attachments;
		}

		return attachments;
	}

	public List<Attachment> findAttachments(long quoteItemId, long quoteId, List<String> types){

		List<Attachment> attachments = new ArrayList<Attachment>();

		if(quoteItemId != 0){
			QuoteItem quoteItem = em.find(QuoteItem.class, quoteItemId);

			for(Attachment attachment : quoteItem.getAttachments()){
				if(types.contains(attachment.getType())){
					attachments.add(attachment);
				}
			}

			for(Attachment attachment : quoteItem.getQuote().getAttachments()){
				if(types.contains(attachment.getType())){
					attachments.add(attachment);
				}
			}

			return attachments;
		}

		if(quoteId != 0){
			Quote quote = em.find(Quote.class, quoteId);

			for(Attachment attachment : quote.getAttachments()){
				if(types.contains(attachment.getType())){
					attachments.add(attachment);
				}
			}
		}    	

		return attachments;
	}    


	public List<String> findDistinctProjectName(BizUnit bizUnit){
		TypedQuery<String> query = em.createQuery("select distinct i.projectName from QuoteItem i where i.projectName is not null and i.quote.bizUnit = :bizUnit", String.class);
		query.setParameter("bizUnit", bizUnit);
		return query.getResultList();
	}

	public List<String> findDistinctProjectNameBySoldToAndEndCustomer(String soldToCode, String endCustomer){
		TypedQuery<String> query = em.createQuery("select distinct i.projectName from QuoteItem i " +
				"where i.projectName is not null and i.soldToCustomer.customerNumber = :soldToCode " +
				"and i.endCustomer.customerNumber = :endCustomer", String.class);

		query.setParameter("soldToCode", soldToCode).
		setParameter("endCustomer", endCustomer);

		return query.getResultList();

	}

	public List<QuoteItem> findQuotesByMfrAndBizUnit(List<String> mfrs, BizUnit bizUnit, List<String> materialTypes, String quoteStage){
		return findQuotesByMfrAndBizUnit(mfrs, bizUnit, materialTypes, quoteStage, null);
	}

	public List<QuoteItem> findQuotesByMaterialAndBizUnit(Material material, BizUnit bizUnit){
		return findQuotesByMaterialAndBizUnitAndStageAndStatus(material, bizUnit, null, null);    
	}

	public List<QuoteItem> findQuotesByMaterialAndBizUnitByStage(Material material, BizUnit bizUnit, List<String> stage){
		return findQuotesByMaterialAndBizUnitAndStageAndStatus(material, bizUnit, stage, null);    
	}

	public List<QuoteItem> findQuotesByMaterialAndBizUnitByStatus(Material material, BizUnit bizUnit, List<String> status){
		return findQuotesByMaterialAndBizUnitAndStageAndStatus(material, bizUnit, null, status);    
	}

	public List<QuoteItem> findQuotesByMaterialAndBizUnitAndStageAndStatus(Material material, BizUnit bizUnit, List<String> stage, List<String> status){
		String sql = "select i from QuoteItem i where i.quote.bizUnit = :bizUnit and i.requestedMaterial = :material";
		if(stage != null)
			sql += " and i.stage in :stage";
		if(status != null)
			sql += " and i.status in :status";
		TypedQuery<QuoteItem> query = em.createQuery(sql, QuoteItem.class);

		query.setParameter("material", material);
		query.setParameter("bizUnit", bizUnit);
		if(stage != null)
			query.setParameter("stage", stage);
		if(status != null)
			query.setParameter("status", status);

		return query.getResultList();

	}    

	public List<QuoteItem> findQuotesByMaterialsAndBizUnitAndStageAndStatusAndCustomer(String partNumber, String mfr, BizUnit bizUnit, List<String> stage, List<String> status, String customerName){
		String sql = "select i from QuoteItem i join i.quotedMfr mfr where i.quote.bizUnit = :bizUnit ";
		if(partNumber != null){
			sql += " and i.quotedPartNumber like :partNumber";
		}
		if(mfr != null){
			sql += " and i.quotedMfr.name like :mfr";
		}
		if(stage != null)
			sql += " and i.stage in :stage";
		if(status != null)
			sql += " and i.status in :status";
		if(customerName != null){
			customerName = "%"+customerName.toUpperCase()+"%";
			sql += " and UPPER(i.quote.soldToCustomerName) like :customerName";
			//"or i.soldToCustomer.customerName1 + i.soldToCustomer.customerName2 like :customerName";
		}
		sql += " order by i.submissionDate desc";

		TypedQuery<QuoteItem> query = em.createQuery(sql, QuoteItem.class);

		query.setParameter("bizUnit", bizUnit);
		if(stage != null)
			query.setParameter("stage", stage);
		if(status != null)
			query.setParameter("status", status);
		if(customerName != null)
			query.setParameter("customerName", customerName.toUpperCase());
		if(partNumber != null)
			query.setParameter("partNumber", "%" + partNumber.toUpperCase() + "%");
		if(mfr != null)
			query.setParameter("mfr", "%" + mfr.toUpperCase() + "%");

		query.setFirstResult(0);
		query.setMaxResults(200);

		return query.getResultList();

	} 
	
	public String createQuotationEmail(String request){
		Query query = em.createNativeQuery("BEGIN POST_QUOTATION_EMAIL_PROC('"+request+"'); END;");
	    Object obj = query.executeUpdate();
	    return obj.toString();
	}
	
	public List<QuotationEmail> findQuotationEmail(){
		Date date = new Date();
		Date endDate = QuoteUtil.getEndOfPostQuotation(date);
		long diff = 1000 * 60 * 5;
		Date startDate = new Date(endDate.getTime() - diff);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		
		String sql = "select q from QuotationEmail q where q.status = 1 ";
		TypedQuery<QuotationEmail> query = em.createQuery(sql, QuotationEmail.class);
		return query.getResultList();
		
	}
	
	public void removeQuotationEmail(List<QuotationEmail> emails){
		
		LOG.log(Level.INFO, "removeQuotationEmail");  
		for(QuotationEmail email : emails){
			email.setStatus(false);
			em.merge(email);
		}
		em.flush();
	
	}
	
	public void removeQuotationEmail(String cutOffTime){
		Query query = em.createNativeQuery("BEGIN UPDATE_POST_QUOTATION_EMAIL('"+cutOffTime+"'); END;");
	    query.executeUpdate();
	}	
	/*
	 *  Below are for program submit
	 */
	/*
	 * Get program draft Rfq 
	 */
	public Quote getProgDraftRfq(User user, BizUnit bizUnit)
	{
		Query query = em.createQuery(SQL_QUERY_PROG_DRAFT_RFQ);
		query.setParameter("bizUnit", bizUnit);
		query.setParameter("stage", QuoteSBConstant.QUOTE_STATE_PROG_DRAFT);
		query.setParameter("lastUpdatedBy", user.getEmployeeId());
		//query.setParameter("expireDate", getExpireDate());
		
		List<Quote> queryList = query.getResultList();
		if(queryList!=null && queryList.size()>0)
			return queryList.get(0);
		else
			return null;
	}
	
	private Quote getProgDraftRfqWithExpire(User user, BizUnit bizUnit)
	{
		Query query = em.createQuery(SQL_QUERY_PROG_DRAFT_RFQ_WITH_EXPIRE);
		query.setParameter("bizUnit", bizUnit);
		query.setParameter("stage", QuoteSBConstant.QUOTE_STATE_PROG_DRAFT);
		query.setParameter("lastUpdatedBy", user.getEmployeeId());
		
		List<Quote> queryList = query.getResultList();
		if(queryList!=null && queryList.size()>0)
			return queryList.get(0);
		else
			return null;
	}

	private Date getExpireDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_MONTH, -7);
		return calendar.getTime();
	}
	/*
	 * Remove program draft Rfq 
	 */
	public void removeProgDraftRfq(User user,BizUnit bizUnit)
	{
		LOG.info("call | removeProgDraftRfq");
		Quote quote = getProgDraftRfqWithExpire(user, bizUnit);
		if(quote !=null)
		{
			if(quote.getQuoteItems()!=null && quote.getQuoteItems().size()>0)
				for(QuoteItem item: quote.getQuoteItems())
				{
					em.remove(item);
				}
			em.remove(quote);
		}	

	}

	public void saveProgDraftQuote(Quote quote, User user, BizUnit bizUnit)
	{
		LOG.info("call | saveProgDraftQuote");
		Quote draftQuote = getProgDraftRfqWithExpire(user, bizUnit);
		if(draftQuote !=null)
		{
			em.remove(draftQuote);
			em.flush();
		}


		em.persist(quote);
		em.flush();

	}

	public void submitProgramRfq(Quote quote){

		LOG.info("call | submitProgramRfq");
		populateQuoteNumber(quote);
		em.persist(quote);
		em.flush();
	}

	public List<QuoteItem> findQuotesByStage(String stage){
		String sql = "select i from QuoteItem i where i.stage = :stage";
		TypedQuery<QuoteItem> query = em.createQuery(sql, QuoteItem.class);    	
		query.setParameter("stage", stage);

		return query.getResultList();
	}

	public List<Quote> findQcName(String formNumber){
		String sql = "select q from QuoteItem q where UPPER(q.lastUpdatedQc.name) like :qcName";
		Query query = em.createQuery(sql, Quote.class);
		query.setParameter("qcName", "%" + formNumber.toUpperCase() + "%");
		return query.setFirstResult(0).setMaxResults(10).getResultList();
	}

	public String findMaxReverVersion(String quoteNumber, String revertVersion){
		
		if(quoteNumber ==null || quoteNumber.equals("") || revertVersion==null || revertVersion.equals("") || revertVersion.length() < 2 ){
			return "0A";
		}
		
		String firstPart = revertVersion.substring(0, revertVersion.length()-1);
		
		String sql = "select q from QuoteItem q where q.firstRfqCode = :quoteNumber AND q.revertVersion like :firstPart";
		TypedQuery<QuoteItem> query = em.createQuery(sql, QuoteItem.class);
		query.setParameter("quoteNumber", quoteNumber);
		query.setParameter("firstPart", firstPart + "%");
		List<QuoteItem> items = query.getResultList();
		char maxRevertVersion = 'A';
		for(QuoteItem item : items){
			String s = item.getRevertVersion();
			try{
				String s1 = s.substring(revertVersion.length()-1);
				if(maxRevertVersion < s1.charAt(0)){
					maxRevertVersion = s1.charAt(0);
				}
			}catch(Exception e){
				LOGGER.log(Level.SEVERE, "Exception in find max revert version number : "+item.getId()+" , Revert version : "+s+" , Exception Message : " +e.getMessage(), e);
			}
		}
		return firstPart + maxRevertVersion; 
		
	}
	public static final String ATTACHMENT_ROOT_PATH = "ATTACHMENT_ROOT_PATH";
	
	public byte[] zipFile(List<Attachment> attachments) throws IOException {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);
		
		for(int i = 0 ; i < attachments.size() ; i++)
		{
			Attachment attachment = attachments.get(i);
			byte[] fileByteArray = attachment.getFileImage();
			if(fileByteArray == null || !(fileByteArray.length > 0)) {
				// modified by Yang,Lenon for read attachments from host server(2015.12.31)
				String fileRootPath = sysConfigSB.getProperyValue(ATTACHMENT_ROOT_PATH);
				String realFilePath = fileRootPath + File.separator + attachment.getFilePath() + File.separator + attachment.getFileNameActual();
				fileByteArray = file2Byte(realFilePath);
			} 
			
			ZipEntry entry = new ZipEntry(attachment.getFileName());
			entry.setSize(fileByteArray.length);
			zos.putNextEntry(entry);
			zos.write(fileByteArray);
		}
		
		zos.closeEntry();
		zos.close();
		return baos.toByteArray();
	}

	private byte[] file2Byte(String filePath) throws IOException  {
		File file = new File(filePath);
		if (file.exists()) {
			byte[] buffer = null;
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
			return buffer;
		}
		return new byte[] {};
	}
	
	public List<QuoteItem> search(MyQuoteSearchCriteria criteria) {
		
		//LOG.info(criteria.toString());
		
		List<QuoteItem> searchResult = null;		

		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<QuoteItem> c = cb.createQuery(QuoteItem.class);

		Root<QuoteItem> quoteItem = c.from(QuoteItem.class);
		
		//Join<QuoteItem, MaterialType> materialType = quoteItem.join("materialType", JoinType.LEFT);
		Join<QuoteItem, ProductGroup> productGroup = quoteItem.join("productGroup2", JoinType.LEFT);
		//Join<QuoteItem, ProgramType> programType = quoteItem.join("programType", JoinType.LEFT);
		Join<QuoteItem, Quote> quote = quoteItem.join("quote");
		Join<Quote, User> sales = quote.join("sales", JoinType.LEFT);
		Join<Quote, Team> team = quote.join("team", JoinType.LEFT);
		Join<Quote, BizUnit> bizUnit  = quote.join("bizUnit", JoinType.LEFT);
		//Join<Quote, Customer> soldToCustomer = quote.join("soldToCustomer", JoinType.LEFT);
		//Join<QuoteItem, Customer> endCustomer = quoteItem.join("endCustomer", JoinType.LEFT);
		//Join<QuoteItem, Material> material = quoteItem.join("requestedMaterial", JoinType.LEFT);
		//Join<QuoteItem, Material> quotedMaterial = quoteItem.join("quotedMaterial", JoinType.LEFT);
		Join<QuoteItem, Manufacturer> manufacturer = quoteItem.join("requestedMfr", JoinType.LEFT);

		c.orderBy(cb.asc(quoteItem.get("submissionDate")));
		
		populateCriteria(c, criteria, quoteItem, productGroup, quote, sales, team, manufacturer, bizUnit, null, null, null, null);
		
		TypedQuery<QuoteItem> q = em.createQuery(c);
		//Added By Lenon.Yang(043044) 2016-11-07
		if(criteria.getWorkingPlatFormSearchCriteria() != null) {
			String recordNumber = criteria.getWorkingPlatFormSearchCriteria().getRecordNumber();
			if(!org.apache.commons.lang.StringUtils.equalsIgnoreCase("ALL", recordNumber)) {
				q.setFirstResult(0);
				q.setMaxResults(Integer.parseInt(recordNumber));
			}
			
		}
		searchResult = q.getResultList();		
		
		return searchResult;
		
	}
	
	public List<QuoteItem> search(MyQuoteSearchCriteria criteria, int first, int pageSize,String orderBy, String field, Map<String, Object> filter) {
		
		//LOG.info(criteria.toString());
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<QuoteItem> c = cb.createQuery(QuoteItem.class);

		Root<QuoteItem> quoteItem = c.from(QuoteItem.class);
		c.distinct(true);
		//Join<QuoteItem, MaterialType> materialType = quoteItem.join("materialType", JoinType.LEFT);
		Join<QuoteItem, ProductGroup> productGroup = quoteItem.join("productGroup2", JoinType.LEFT);
		//Join<QuoteItem, ProgramType> programType = quoteItem.join("programType", JoinType.LEFT);
		Join<QuoteItem, Quote> quote = quoteItem.join("quote");
		Join<Quote, User> sales = quote.join("sales", JoinType.LEFT);
		Join<Quote, Team> team = quote.join("team", JoinType.LEFT);
		Join<Quote, BizUnit> bizUnit  = quote.join("bizUnit", JoinType.LEFT);
		
		//Join<QuoteItem, Material> material = quoteItem.join("requestedMaterial", JoinType.LEFT);
		//Join<QuoteItem, Material> quotedMaterial = quoteItem.join("quotedMaterial", JoinType.LEFT);
		Join<QuoteItem, Manufacturer> manufacturer = quoteItem.join("requestedMfr", JoinType.LEFT);

		c.orderBy(cb.asc(quoteItem.get("submissionDate")));
		
		populateCriteria(c, criteria, quoteItem, productGroup, quote, sales, team, manufacturer, bizUnit, orderBy, field, filter,null);

//		populateCriteria(c, criteria, quoteItem, productGroup, quote, sales, team, manufacturer, bizUnit, null, null, null, null);
//		populateCriteria(c, criteria, quoteItem, productGroup, quote, sales, team, manufacturer, bizUnit, orderBy, field, filter, null);
		
		TypedQuery<QuoteItem> q = em.createQuery(c);
		
		//Added By Lenon.Yang(043044) 2016-11-07
		//		if(criteria.getWorkingPlatFormSearchCriteria() != null) {
		//			String recordNumber = criteria.getWorkingPlatFormSearchCriteria().getRecordNumber();
		//			if(!org.apache.commons.lang.StringUtils.equalsIgnoreCase("ALL", recordNumber)) {
		//				q.setFirstResult(0);
		//				q.setMaxResults(Integer.parseInt(recordNumber));
		//			}
		//			
		//		}
		
		q.setFirstResult(first);
		q.setMaxResults(pageSize);
		
		List<QuoteItem> searchResult = q.getResultList();		
		//Add to solve Send Off Line Report java.lang.OutOfMemoryError: Java heap space
	   	em.clear();
		return searchResult;
		
	}
	

	public boolean isNotAllDraft(Quote quote)
	{
		boolean isAllDraftQuote = false;
		for(QuoteItem qi : quote.getQuoteItems())
		{
			if(!QuoteSBConstant.QUOTE_STAGE_DRAFT.equalsIgnoreCase(qi.getStage()))
			{
				isAllDraftQuote = true;
				break;
			}
		}
		return isAllDraftQuote;
	}
	
	public Quote populateFormNumberForDraft(Quote quote)
	{
		if(quote.getFormNumber()==null)
		{
			if(isNotAllDraft(quote))
			{
					QuoteNumber formNumber = quoteNumberSB.lockNumber("FRM",quote.getBizUnit());
					if(!quote.isContinueSubmit())
					{
						quote.setFormNumber(quoteNumberSB.nextNumber(formNumber));
					}
			}
			else
			{
					QuoteNumber formNumber = quoteNumberSB.lockNumber("DRF",quote.getBizUnit());
					if(!quote.isContinueSubmit())
					{
						quote.setFormNumber(quoteNumberSB.nextNumber(formNumber));
					}
			}
		}
		else
		{
			if(quote.getFormNumber().startsWith("DF"))
			{
				if(isNotAllDraft(quote))
				{
					QuoteNumber formNumber = quoteNumberSB.lockNumber("FRM",quote.getBizUnit());
					if(!quote.isContinueSubmit())
					{
						quote.setFormNumber(quoteNumberSB.nextNumber(formNumber));
					}
				}
			}
		}
		return quote;
	}
	
	/*
	 * Default Cost Indicator Logic
	1.	Skip Pricers under Restricted Customer Mapping, Future Pricers and Expired Pricers in the logics
		a.	Check the Quotation Effective Date
			i.	If Quotation Effective Date > Today , ignore it
			ii.	If Price Validity < Today, ignore it.
	2.	Find the Valid Contract Pricer (Refer to the Appendix A4) according to the Salesman (at RFQ Item Level), Sold To Customer (at RFQ Item Level) and End Customer (at RFQ Item Level).
	3.	If Valid Contract Pricer is found and it is not allowed to override (Overridden Flag is false), take the Contract Pricer as Default Cost Indicator and fill in the T&C of A-Book Cost to Quote Item, go to <8>.
	4.	If Valid Contract Pricer is found and it is allowed to override, go to <6> to check whether there is any Program/Normal Pricer in which the cost is lower. If Valid Contract Pricer is the lowest cost, take the Contract Pricer as Default Cost Indicator and fill in the T&C of A-Book Cost to Quote Item, go to <8>.
	5.	If Valid Contract Pricer is not found, go to <6> to check Program/Normal Pricer.
	6.	Find Valid Program/Normal Pricer (Refer to the Appendix A2 & A3) which has the lowest cost. If no valid pricer is not found, go to <7>. If valid pricer is found, go to <8>
	7.	If there is no valid Contract Pricer, Program Pricer and Normal Pricer in the system, assign cost indicator as null and the pricer information as blank. <end>.
	8.	If the valid pricer is found, filling in the pricer information. And refer to the following table to fill in missing information. <end>. (remark : If there is no valid A-Book Cost in the system, use the Expired A-Book Cost data to fill in the missing information.)

	 */
	
    
	public PricerInfo applyDefaultCostIndicatorLogic(PricerInfo pricerInfo, boolean needRefillAccessRelatedFields) {
		
		Material material = materialSB.find(pricerInfo.getMfr(), pricerInfo.getFullMfrPartNumber());
		if(material != null) {
			material.applyDefaultCostIndicatorLogic(pricerInfo, needRefillAccessRelatedFields);
		} else {
			pricerInfo.setBuyCurr(null);
		}
		pricerInfo.adjustAfterNoFoundBuyCurr();
		return pricerInfo;
	}
	
    public void applyDefaultCostIndicatorLogic(QuoteItem quoteItem, boolean needRefillAccessRelatedFields) {

    	try {
    		PricerInfo pricerInfo = quoteItem.createPricerInfo();
        	
            Material material = materialSB.findExactMaterialByMfrPartNumber(quoteItem.getRequestedMfr().getName(), 
            		quoteItem.getQuotedPartNumber(), quoteItem.getQuote().getBizUnit());
    		
    		if(material == null){
    			if(needRefillAccessRelatedFields){
    				quoteItem.setMaterialTypeId("NORMAL");
    				quoteItem.setProgramType(null);
    			}
    			pricerInfo.clearPricingInfo();
    		} else {
    			User sales = em.find(User.class, pricerInfo.getSalesId());
    			pricerInfo.setAllowedCustomers(sales.getCustomers());
    			material.applyDefaultCostIndicatorLogic(pricerInfo, needRefillAccessRelatedFields);
    		}
    		pricerInfo.adjustAfterNoFoundBuyCurr();
        	pricerInfo.fillPriceInfoToQuoteItem(quoteItem);
    	}catch (Exception e) {
    		LOG.severe("applyDefaultCostIndicatorLogic(QuoteItem quoteItem, boolean needRefillAccessRelatedFields) ::"
    				+"QuoteItem ::" +quoteItem.toString()
    				+ e.getMessage() + e.getStackTrace() );
    		throw e;
    	}
    	
    	
    }
	
    
    public void refreshingNewPricing(QuoteItem quoteItem) {
    	
    	PricerInfo pricerInfo = createPricerInfoFromQuoteItem(quoteItem);
        Material material = materialSB.findExactMaterialByMfrPartNumber(quoteItem.getQuotedMfr().getName(), 
        		quoteItem.getQuotedPartNumber(), quoteItem.getQuote().getBizUnit());
		
        if(material != null){
        	material.applyDefaultCostIndicatorLogic(pricerInfo, false);
        } 
        
		pricerInfo.fillPriceInfoToQuoteItem(quoteItem);
    	
    }
	
    private PricerInfo createPricerInfoFromQuoteItem(QuoteItem quoteItem) {
        PricerInfo pricerInfo = quoteItem.createPricerInfo();
        User sales = em.find(User.class, pricerInfo.getSalesId());
        if(sales !=null) {
        	pricerInfo.setAllowedCustomers(sales.getCustomers());
        }
        return pricerInfo;
    }
    
	
	

	public void selectPricer(QuoteItem quoteItem, long pricerId) {
		PricerInfo pricerInfo = quoteItem.createPricerInfo();
		pricerInfo.clearPricingInfo();
		Material material = materialSB.findMaterialByPricerId(pricerId);

		if (material != null) {
			material.selectPricer(pricerId, pricerInfo);
		}
		pricerInfo.adjustAfterNoFoundBuyCurr();
		pricerInfo.fillPriceInfoToQuoteItem(quoteItem);

	}
	
	public void switchCostIndicator(QuoteItem quoteItem, CostIndicator costIndicator, Currency buyCurr) {

		PricerInfo pricerInfo = createPricerInfoFromQuoteItem(quoteItem);

		Material material = materialSB.find(quoteItem.getQuotedMfr().getName(), quoteItem.getQuotedPartNumber());
		
		if (material != null) {
			material.switchCostIndicator(pricerInfo, costIndicator, buyCurr);
		} else {
			pricerInfo.setCostIndicator(costIndicator==null? null:costIndicator.getName());
			pricerInfo.setBuyCurr(buyCurr.toString());
			pricerInfo.calExRate();
		}
		pricerInfo.fillPriceInfoToQuoteItem(quoteItem);
		
	}
	
	//upload excel buycurr changed or costIndicator changed ,call this function.
	public void updateByBuyCurrChanged(final QuoteItem quoteItem) {
		QuoteItem quoteItemCopy = quoteItem.clone();
		switchCostIndicator(quoteItemCopy, costIndicatorSB.findCostIndicator(quoteItem.getCostIndicator()),
				quoteItem.getBuyCurr());
		quoteItem.setPricerId(quoteItemCopy.getPricerId());
		quoteItem.setMinSellPrice(quoteItemCopy.getMinSellPrice());
		quoteItem.setBottomPrice(quoteItemCopy.getBottomPrice());
		quoteItem.reCalExchRateForSubmitted();
		quoteItem.reCalMargin();
	}
	
	public QuoteItem changeQuotedPartNumber(QuoteItem quoteItem, String mfr, String fullMfrPartNumber){
		
    	PricerInfo pricerInfo = quoteItem.createPricerInfo();
    	pricerInfo.setQuotedMfr(manufacturerSB.findManufacturerByName(mfr));
    	pricerInfo.setFullMfrPartNumber(fullMfrPartNumber);
    	
        Material material = materialSB.findExactMaterialByMfrPartNumber(mfr, fullMfrPartNumber, quoteItem.getQuote().getBizUnit());
		
		if(material == null){
			pricerInfo.clearPricingInfo();
//			pricerInfo.setSalesCostFlag(false);
			pricerInfo.setSalesCostType(SalesCostType.NonSC);
		} else {
			quoteItem.setQuotedMaterial(material);
			User sales = em.find(User.class, pricerInfo.getSalesId());
			pricerInfo.setAllowedCustomers(sales.getCustomers());
			material.applyDefaultCostIndicatorLogic(pricerInfo, true);
		}
		pricerInfo.adjustAfterNoFoundBuyCurr();
    	pricerInfo.fillPriceInfoToQuoteItem(quoteItem);

		return quoteItem;
	}
	

	

	public List<AuditQuoteItem> findAuditQuoteItem(AuditReportSearchCriteria criteria){
		
		//LOG.info(criteria.toString());
		
		List<AuditQuoteItem> searchResult = new ArrayList<AuditQuoteItem>();		
		
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<AuditQuoteItem> c = cb.createQuery(AuditQuoteItem.class);
		
		Root<AuditQuoteItem> auditQuoteItem = c.from(AuditQuoteItem.class);
		
		populateCriteria(c, criteria, auditQuoteItem);
				
		TypedQuery<AuditQuoteItem> q = em.createQuery(c);
		
		q.setFirstResult(0);
		q.setMaxResults(200);		

		
		List<AuditQuoteItem> items = q.getResultList();
				
		return items;

	}			

	private void populateCriteria(CriteriaQuery c, AuditReportSearchCriteria criteria, Root<AuditQuoteItem> auditQuoteItem) {

		CriteriaBuilder cb = em.getCriteriaBuilder();

		List<Predicate> criterias = new ArrayList<Predicate>();
		
		if(criteria.getFormNumber() != null){
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			String formNumber = criteria.getFormNumber();
			Predicate predicate = cb.like(cb.upper(auditQuoteItem.<String>get("formNumber")), "%" + formNumber.toUpperCase() + "%" );
			predicates.add(predicate);
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));
		}
				
		if(criteria.getQuoteNumber() != null) {
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			String quoteNumber = criteria.getQuoteNumber();
			Predicate predicate = cb.like(cb.upper(auditQuoteItem.<String>get("quoteNumber")), "%" + quoteNumber.toUpperCase() + "%" );
			predicates.add(predicate);
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));
			
		}	

		if(criteria.getMfr() != null){
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			String quotedMfr = criteria.getMfr();
			Predicate predicate = cb.like(cb.upper(auditQuoteItem.<String>get("quotedMfrNameNew")), "%" + quotedMfr.toUpperCase() + "%" );
			predicates.add(predicate);					
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));			

		}
		
		if(criteria.getQuotedPartNumber() != null){
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			String quotedPartNumber = criteria.getQuotedPartNumber();
			Predicate predicate = cb.like(cb.upper(auditQuoteItem.<String>get("quotedPartNumberNew")), "%" + quotedPartNumber.toUpperCase() + "%" );
			predicates.add(predicate);					
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));			

		}
		
		if (criteria.getUpdateDateFrom() != null ) {
			criterias.add(cb.greaterThanOrEqualTo(auditQuoteItem.<AuditQuoteItemPK>get("id").<Date>get("updateDate"), criteria.getUpdateDateFrom()));
		}
		
		if (criteria.getUpdateDateTo() != null ) {
			criterias.add(cb.lessThanOrEqualTo(auditQuoteItem.<AuditQuoteItemPK>get("id").<Date>get("updateDate"), criteria.getUpdateDateTo()));
		}

		if(criteria.getBizUnit() != null){
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			BizUnit bizUnit = criteria.getBizUnit();
			Predicate predicate = cb.like(cb.upper(auditQuoteItem.<String>get("bizUnit")), "%" + bizUnit.getName() + "%" );
			predicates.add(predicate);					
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));			

		}
		
		

		if (criterias.size() == 0) {
			
		} else if (criterias.size() == 1) {
			c.where(criterias.get(0));
		} else {
			c.where(cb.and(criterias.toArray(new Predicate[0])));
		}
		
		c.orderBy(cb.desc(auditQuoteItem.<AuditQuoteItemPK>get("id").<Date>get("updateDate")));
	}
	
	public List<AuditExchangeRate> findAuditExchangeRate(AuditExchangeReportSearchCriteria criteria){
		
		//LOG.info(criteria.toString());
		
		List<AuditExchangeRate> searchResult = new ArrayList<AuditExchangeRate>();		
		
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<AuditExchangeRate> c = cb.createQuery(AuditExchangeRate.class);
		
		Root<AuditExchangeRate> auditExchangeRate = c.from(AuditExchangeRate.class);
		
		populateExchangeRateCriteria(c, criteria, auditExchangeRate);
				
		TypedQuery<AuditExchangeRate> q = em.createQuery(c);
		
		q.setFirstResult(0);
		q.setMaxResults(200);		

		
		List<AuditExchangeRate> items = q.getResultList();
				
		return items;

	}

	private void populateExchangeRateCriteria(CriteriaQuery c, AuditExchangeReportSearchCriteria criteria, Root<AuditExchangeRate> auditExchangeRate) {

		CriteriaBuilder cb = em.getCriteriaBuilder();

		List<Predicate> criterias = new ArrayList<Predicate>();
		
		if(!StringUtils.isEmpty(criteria.getCurrencyFrom())){
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			String currencyFrom = criteria.getCurrencyFrom();
			Predicate predicate = cb.like(cb.upper(auditExchangeRate.<String>get("currFromNew")), "%" + currencyFrom.toUpperCase() + "%" );
			predicates.add(predicate);
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));
		}
				
		if(!StringUtils.isEmpty(criteria.getCurrencyTo())) {
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			String currencyTo = criteria.getCurrencyTo();
			Predicate predicate = cb.like(cb.upper(auditExchangeRate.<String>get("currToNew")), "%" + currencyTo.toUpperCase() + "%" );
			predicates.add(predicate);
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));
			
		}	
	
		if(!StringUtils.isEmpty(criteria.getSoldToCode())){
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			String soldToCode = criteria.getSoldToCode();
			Predicate predicate = cb.like(cb.upper(auditExchangeRate.<String>get("soldToCodeNew")), "%" + soldToCode.toUpperCase() + "%" );
			predicates.add(predicate);					
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));			

		}
		
		if (criteria.getUpdateDateFrom() != null ) {
			criterias.add(cb.greaterThanOrEqualTo(auditExchangeRate.<Date>get("updateDate"), criteria.getUpdateDateFrom()));
		}
		
		if (criteria.getUpdateDateTo() != null ) {
			criterias.add(cb.lessThanOrEqualTo(auditExchangeRate.<Date>get("updateDate"), criteria.getUpdateDateTo()));
		}

		if(criteria.getBizUnit() != null){
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			BizUnit bizUnit = criteria.getBizUnit();
			Predicate predicate = cb.like(cb.upper(auditExchangeRate.<String>get("bizUnitNew")), "%" + bizUnit.getName() + "%" );
			predicates.add(predicate);					
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));			

		}
		
		

		if (criterias.size() == 0) {
			
		} else if (criterias.size() == 1) {
			c.where(criterias.get(0));
		} else {
			c.where(cb.and(criterias.toArray(new Predicate[0])));
		}
		
		c.orderBy(cb.desc(auditExchangeRate.<Date>get("updateDate")));
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void updateReferenceMarginForSalesAndCs(QuoteItem quoteItem)
	{
		List<ReferenceMarginSetting> referenceMarginSettings = manufacturerSB.findAllReferenceMarginSetting();
		Collections.sort(referenceMarginSettings, new EJBQuoteComparator()); 
		Collections.reverse(referenceMarginSettings);
			if(quoteItem.getResaleMargin()!=null && quoteItem.getResaleMargin().doubleValue()!=0)
			{
				boolean found = false;
				for (int k = 0 ; k < referenceMarginSettings.size() ; k++)
				{
					try{
						ReferenceMarginSetting referenceMarginSetting = referenceMarginSettings.get(k);
						Material quotedMaterial = quoteItem.getQuotedMaterial();
						
						long quoteItemManufacturerId = 0l;
						long quoteItemMaterialId =0l;
						if(quotedMaterial!=null){
							if(quotedMaterial.getManufacturer()!=null){
								quoteItemManufacturerId = quotedMaterial.getManufacturer().getId();
							}				
							quoteItemMaterialId = quotedMaterial.getId();
						}
		
						String quoteBizUnit = quoteItem.getQuote().getBizUnit().getName();
			
						long quoteItemProductGroup = DEFAULT_LONG;
						if(quoteItem.getProductGroup2()!=null)
						{
						   quoteItemProductGroup = quoteItem.getProductGroup2().getId();
						}
						
						long referenceMarginSettingManufacturerId = referenceMarginSetting.getManufacturerID();
						String referenceMarginSettingBizUnit = referenceMarginSetting.getBizUnitID();
						long referenceMarginSettingMaterialId = referenceMarginSetting.getMaterialID();
						long referenceMarginProductGroup = referenceMarginSetting.getProductGroupID();

						/*System.out.println(quoteItemManufacturerId + " " + referenceMarginSettingManufacturerId);
						System.out.println(quoteBizUnit + " " + referenceMarginSettingBizUnit);
						System.out.println(quoteItemMaterialId + " " + referenceMarginSettingMaterialId);
						System.out.println(quoteItemProductGroup + " " + referenceMarginProductGroup);*/
						
						//All 4 items matched
						if(quoteItemManufacturerId==referenceMarginSettingManufacturerId  && 
								quoteBizUnit.equals(referenceMarginSettingBizUnit) &&
								quoteItemMaterialId == referenceMarginSettingMaterialId &&
								quoteItemProductGroup == referenceMarginProductGroup)
						{
							//System.out.println("####1####");
							applyReferenceMarginLogic(referenceMarginSetting, quoteItem);
							found = true;
							break;
						}
						//3 Items matched and  materialId is 0
						else if(quoteItemManufacturerId==referenceMarginSettingManufacturerId && 
								quoteBizUnit.equals(referenceMarginSettingBizUnit) &&
								referenceMarginSettingMaterialId == 0 &&
								quoteItemProductGroup == referenceMarginProductGroup)
						{
							//System.out.println("####2####");
							applyReferenceMarginLogic(referenceMarginSetting, quoteItem);
							found = true;
							break;
						}
						//2 Items matached and materialID, productGroup == 0
						else if(quoteItemManufacturerId==referenceMarginSettingManufacturerId && 
								quoteBizUnit.equals(referenceMarginSettingBizUnit) &&
								referenceMarginSettingMaterialId == 0 &&
								referenceMarginProductGroup == 0)
						{
							//System.out.println("####3####");
							applyReferenceMarginLogic(referenceMarginSetting, quoteItem);
							found = true;
							break;
						}
						
					}
					catch(Exception e)
					{	
						LOG.log(Level.SEVERE, "Exception in update ReferenceMargin For Sales And Cs for quote item id : "+quoteItem.getId()+" , Exception Message : "+e.getMessage(), e);
					}
				}
				if (found == false){
					quoteItem.setReferenceMargin(quoteItem.getResaleMargin());
				}
				
			}else{
				quoteItem.setReferenceMargin(quoteItem.getResaleMargin());
			}
	}

	private void applyReferenceMarginLogic(ReferenceMarginSetting referenceMarginSetting, QuoteItem quoteItem)
	{
		double resaleMargin = quoteItem.getResaleMargin();
		double factor = referenceMarginSetting.getFactor().doubleValue()/100;
		double floor = referenceMarginSetting.getFloor().doubleValue();
		double cap = referenceMarginSetting.getCap().doubleValue();
		double referenceMargin = factor*resaleMargin;

		//System.out.println("resale margin " + resaleMargin + " factor " + factor + " floor " + floor + " cap " + cap + " reference margin " + referenceMargin);

		if(referenceMargin!=0)
		{
			if(referenceMargin < resaleMargin && resaleMargin < floor)
			{
				quoteItem.setReferenceMargin(Double.valueOf(resaleMargin));
			}
			else if(floor!=0 && referenceMargin < floor && floor <= resaleMargin)
			{
				quoteItem.setReferenceMargin(Double.valueOf(floor));
			}
			else if(cap!=0 && referenceMargin > cap)
			{
				quoteItem.setReferenceMargin(Double.valueOf(cap));
			}
			else
			{
				quoteItem.setReferenceMargin(Double.valueOf(referenceMargin));
			}
		}
	}
	
	public List<DrmsAgpRea> findAllDrmsAgpReasons(){
		TypedQuery<DrmsAgpRea> query = em.createQuery("select r from DrmsAgpRea r order by r.authGpChgRea", DrmsAgpRea.class);
		return query.getResultList();
		
	}
	//added parameter ActionEnum for WorkingPlatform by damon@20160818
	public void saveQuoteItemAndUpdateDRMS(List<QuoteItem> quoteItems, String employeeId,ActionEnum action,User user){
		StringBuffer sb = new StringBuffer();
		
		List<String> optimisticLockItems = new ArrayList<String>();
		int itemIndex=0;
		for(QuoteItem item : quoteItems){
			try{
				//fixed OptimisticLockException issue by DamonChen@20180409 
				itemIndex=quoteItems.indexOf(item);
				item.setAction(action.name());
				item.postProcessAfterFinish();
				item = em.merge(item);
				quoteItems.set(itemIndex, item);
				
				
				
			}catch(OptimisticLockException e){
				LOGGER.log(Level.WARNING, "Error occured for employee Id: "+employeeId+", Reason for failure: "+e.getMessage(),e);
				optimisticLockItems.add(item.getQuoteNumber());
			}
		}
		
		if(optimisticLockItems.size() != 0 ){
			String s = "RFQ " + optimisticLockItems.toString() + " has/have been modified by other users. Please refresh and submit again.";
			throw new WebQuoteRuntimeException(s,new RuntimeException());
		}
		
		List<QuoteItem> drmsUpdateQuoteItems = new ArrayList<QuoteItem>();

		for(QuoteItem item : quoteItems){
			if(QuoteUtil.getDrmsKey(item) == null){
				continue;
			}
			//upded for INC0970255  byDamonchen@20180822
			if(item.isSalesCostFlag() && !SalesCostType.NonSC.equals(item.getSalesCostType())){
				continue;
			}
			boolean drmsUpdate = false;
			
			if(item.getAuthGp() == null){
				drmsUpdate = true;
			}else{
				if(QuoteUtil.compareQGPAndAGP(item.getResaleMargin(), item.getAuthGp()) > 0){
					drmsUpdate = true;
				}

			}

			//Added by Lenon(043044) 2016/11/30
			//Bryan Start
			DrmsValidationUpdateStrategy drmsValidationUpdateStrategy = 
					DrmsValidationUpdateStrategyFactory.getInstance()
					.getDrmsValidationUpdateStrategy(user.getDefaultBizUnit().getName());
//			DrmsValidationUpdateStrategy drmsValidationUpdateStrategy = 
//					cacheUtil.getDrmsValidationUpdateStrategy(user.getDefaultBizUnit().getName());
			//Bryan End
			boolean isValidateAGP = drmsValidationUpdateStrategy.isValidationAGP();
			if(!isValidateAGP) {
				drmsUpdate = true;
			}

			if (drmsUpdate){
				item.setAuthGp(item.getResaleMargin());
				item = drmsValidationUpdateStrategy.getUpdateSapReasonCodeItem(item);
				drmsUpdateQuoteItems.add(item);
				item = em.merge(item);
			}			
		}
		
		try {
			sapWebServiceSB.updateSAPAGP(drmsUpdateQuoteItems, employeeId);
		} catch (AppException e) {
			throw new WebQuoteRuntimeException(e.getMessage(),new RuntimeException());
			//throw new RuntimeException(e.getMessage());
		}
		
		sb = new StringBuffer();
		for(QuoteItem item : drmsUpdateQuoteItems){
			if(item.getDrmsUpdated() == false){
				sb.append("Update SAP A.GP falied for: " + item.getQuoteNumber() + " " + QuoteUtil.getDrmsKey(item) + " " + (item.getDrmsUpdateFailedDesc() == null ? "" : item.getDrmsUpdateFailedDesc()) + "<br/>") ;
			}
		}
		
		if (sb.length() != 0){
			sb.append("<br>/");
			sb.append("Cannot proceed. Please unselect the failed RFQ and retry.");
			LOG.log(Level.INFO, sb.toString());
			throw new RuntimeException(sb.toString());
		}
		
	}
	
	/**
	 * Priority	Search Criteria				
	 * 1	Currency From	Currency To	Sold To Code  Region
	 * 2	Currency From	Currency To				  Region
	 * @param qi
	 * @return
	 */
	public ExchangeRate findLatestExchangeRate(QuoteItem qi) {
		ExchangeRate latestRate = null;
		
		latestRate = exchangeRateSB.findExchangeRate(qi.getBuyCurr().name(),qi.getRfqCurr().name(),qi.getSoldToCustomer().getCustomerNumber(), qi.getQuote().getBizUnit().getName());

		if(null==latestRate) {
			latestRate = exchangeRateSB.findExchangeRate(qi.getBuyCurr().name(),qi.getRfqCurr().name(),null, qi.getQuote().getBizUnit().getName());
			
		}
		
		return latestRate;

	}

	//=======================added by Damon begin====================
	//return null,exception;NO-DUP, no duplicate;DUP, duplicate
	public String  copyAndStageQuoteItem(QuoteItem origQuoteItem,QuoteItem dupQuoteItem, String isAqStr,User user) {

		try {
			String returnStr="NO-DUP";
			
			if (origQuoteItem.getDesignRegion() == null || origQuoteItem.getDesignRegion().trim().equals("")|| origQuoteItem.getDesignRegion().trim().equals("ASIA")) {
				if(isAqStr!=null && isAqStr.trim().equals("1")){
					origQuoteItem.setStatus(QuoteSBConstant.RFQ_STATUS_AQ);
					origQuoteItem.setStage(QuoteSBConstant.QUOTE_STAGE_FINISH);
				}else{
					origQuoteItem.setStatus(QuoteSBConstant.RFQ_STATUS_QC);
					origQuoteItem.setStage(QuoteSBConstant.QUOTE_STAGE_PENDING);
				}
				
			} else {
				String nseconds=""+origQuoteItem.getSoldToCustomer().getCustomerNumber()+System.nanoTime();
				 LOG.info("nseconds:"+nseconds);
				if (origQuoteItem.getBmtCheckedFlag()) {
					BeanUtilsExtends.copyProperties(dupQuoteItem, origQuoteItem);
					if (isAqStr != null && isAqStr.trim().equals("0")) {
						dupQuoteItem.setStatus(QuoteSBConstant.RFQ_STATUS_AQ);
						dupQuoteItem.setStage(QuoteSBConstant.QUOTE_STAGE_INVALID);
						dupQuoteItem.setBmtDescCode("0");
						dupQuoteItem.setDupMatchCode("childr"+nseconds);
					} else if (isAqStr != null && isAqStr.trim().equals("1")) {
						dupQuoteItem.setStatus(QuoteSBConstant.RFQ_STATUS_AQ);
						dupQuoteItem.setStage(QuoteSBConstant.QUOTE_STAGE_FINISH);
						dupQuoteItem.setBmtDescCode("1");
						dupQuoteItem.setDupMatchCode("childr"+nseconds);
					}
					
					origQuoteItem.setStatus(QuoteSBConstant.RFQ_STATUS_BQ);
					origQuoteItem.setStage(QuoteSBConstant.QUOTE_STAGE_PENDING);
					origQuoteItem.setBmtDescCode("99");
					origQuoteItem.setDupMatchCode("parent"+nseconds);
					setQuoteItemDesignMapping(origQuoteItem,user);
					returnStr="DUP";
				}else{
					origQuoteItem.setStatus(QuoteSBConstant.RFQ_STATUS_BQ);
					origQuoteItem.setStage(QuoteSBConstant.QUOTE_STAGE_PENDING);
					origQuoteItem.setBmtDescCode("99");
					setQuoteItemDesignMapping(origQuoteItem,user);
					returnStr="BMTFLAGCHECK-NO-DUP";
				}
			}
			
			return returnStr;
		} catch (Exception e) {
			LOG.log(Level.SEVERE,"Error occured when copying "+origQuoteItem+" to "+dupQuoteItem+", User "+user+"String "+isAqStr+", Reason to failed : "+MessageFormatorUtil.getParameterizedStringFromException(e),e);
			return null;
		}

	}
	
	public void setQuoteItemDesignMapping(QuoteItem quoteItem, User user) {
		try {
			LOG.log(Level.INFO, "Function setQuoteItemDesignMapping,the quote item id is:" + quoteItem.getId());
			QuoteItemDesign quoteItemDesignExist = null;
			QuoteItemDesign quoteItemDesign = new QuoteItemDesign();
			if (quoteItem.getId() != 0L) {
				quoteItemDesignExist = this.findQuoteItemDesignByQuoteId(quoteItem.getId());
				if (quoteItemDesignExist != null && quoteItemDesignExist.getId() != 0L) {
					quoteItemDesign.setId(quoteItemDesignExist.getId());
					quoteItemDesign.setVersion(quoteItemDesignExist.getVersion());
				}

			}
			BmtFlag b = new BmtFlag();
			b.setBmtFlagCode(quoteItem.getBmtDescCode());
			quoteItemDesign.setQuoteItem(quoteItem);
			quoteItemDesign.setBmtFlag(b);
			quoteItemDesign.setCreatedBy(user.getEmployeeId());
			quoteItemDesign.setCreatedTime(new Date());
			quoteItemDesign.setLastUpdatedBy(user.getEmployeeId());
			quoteItemDesign.setLastUpdatedTime(new Date());
			quoteItem.setQuoteItemDesign(quoteItemDesign);
			quoteItem.setQuotedQty(null);
			quoteItem.setSalesCost(null);
			quoteItem.setQuotedPrice(null);
			quoteItem.setSuggestedResale(null);
			
		} catch (Exception e) {
			LOG.log(Level.INFO, "setQuoteItemDesignMapping error:" + e.getMessage());
		}
	}

	public QuoteItemDesign findQuoteItemDesignByQuoteId(long id){
		String sql = "select q from QuoteItemDesign q where q.quoteItem.id = :quoteId";
		Query query = em.createQuery(sql);
		query.setParameter("quoteId", id);
		try {
			return (QuoteItemDesign)query.getSingleResult();
		} catch (Exception ex){
			LOG.log(Level.INFO,"Exception in finding quote item : "+id+", Exception message"+ex.getMessage());
			return null;
		}
	}
	

	//=======================added by Damon end====================	

	public List<QuoteItem> findQuoteItemByFormNumberAndStage(long quoteId, String stage,String designRegion) {
		String jpql = "select o from QuoteItem o where o.quote.id = :quoteId and o.stage = :stage ";
		if(org.apache.commons.lang.StringUtils.isEmpty(designRegion)){
			jpql += " and o.designRegion is null";
		}else{
			jpql += " and o.designRegion = '"+designRegion+"'";			
		}
		TypedQuery<QuoteItem> query =  em.createQuery(jpql, QuoteItem.class);
		query.setParameter("quoteId", quoteId);
		query.setParameter("stage", stage);
		return query.getResultList();
	}
	
	
	public List<QuoteItemHis> findQuoteItemHis(AuditLogReportSearchCriteria criteria) {

		//LOG.info(criteria.toString());

		List<QuoteItemHis> searchResult = new ArrayList<QuoteItemHis>();

		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<QuoteItemHis> c = cb.createQuery(QuoteItemHis.class);

		Root<QuoteItemHis> quoteItemHis = c.from(QuoteItemHis.class);

		populateQuoteItemHisCriteria(c, criteria, quoteItemHis);

		TypedQuery<QuoteItemHis> q = em.createQuery(c);

		q.setFirstResult(0);
		q.setMaxResults(50);

		List<QuoteItemHis> items = q.getResultList();

		return items;

	}
	
	private void populateQuoteItemHisCriteria(CriteriaQuery c, AuditLogReportSearchCriteria criteria, Root<QuoteItemHis> quoteItemHis) {

		CriteriaBuilder cb = em.getCriteriaBuilder();

		List<Predicate> criterias = new ArrayList<Predicate>();
		
			
		if(criteria.getQuoteNumber() != null) {
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			for(String quoteNumber : criteria.getQuoteNumber()){
				Predicate predicate = cb.like(cb.upper(quoteItemHis.<String>get("quoteNumber")), "%" + quoteNumber.toUpperCase() + "%" );
				predicates.add(predicate);
			}
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));
			
		}	



		if (criterias.size() == 0) {
			
		} else if (criterias.size() == 1) {
			c.where(criterias.get(0));
		} else {
			c.where(cb.and(criterias.toArray(new Predicate[0])));
		}
		c.orderBy(cb.asc(quoteItemHis.<QuoteItemHisPK>get("id").<String>get("quoteItemId")));
		c.orderBy(cb.asc(quoteItemHis.<Integer>get("version")));
		
	}
	
	public List<QuoteItemDesignHis> findQuoteItemDesignHis(List<Long> quoteItemIds) {
		String sql = "select q from QuoteItemDesignHis q where q.id.quoteItemId in :quoteItemIds order by q.id.quoteItemId asc, q.version asc";
		Query query = em.createQuery(sql, QuoteItemDesignHis.class);
		query.setParameter("quoteItemIds", quoteItemIds);
		return query.setFirstResult(0).setMaxResults(50).getResultList();
	}
	

	public List<QuoteItem> quoteRateRequest(List<QuoteItemVo> proceedQuoteItemVos,User currentUser) throws AppException  {
		List<QuoteItem> sapQuoteItems = new ArrayList<QuoteItem>();
		List<QuoteItem> emailQuoteItems = new ArrayList<QuoteItem>();
		Map<String, Quote> newQuotes = new HashMap<String, Quote>();
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		cal.setTime(date);
		date = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");	

		for (QuoteItemVo vo : proceedQuoteItemVos) {
			QuoteItem oldItem = vo.getQuoteItem();
			Quote oldQuote = oldItem.getQuote();
			// modified by Lenon.Yang(043044) 2016/05/18 17:56
			//detach oldItem: not need to update QuoteItem When change oldItem property,avoid OptimisticLockException
			em.detach(oldItem);
			LOG.info("old item quote Form Number ===>>>"
					+ oldQuote.getFormNumber());
			Quote newQuote = null;
			if (newQuotes.containsKey(oldQuote.getFormNumber())) {
				newQuote = newQuotes.get(oldQuote.getFormNumber());
			} else {
				newQuote = copyQuoteSB.copyQuoteSpecific(oldQuote, currentUser);
				newQuotes.put(oldQuote.getFormNumber(), newQuote);

			}

			QuoteItem newItem = copyQuoteSB.copyQuoteItem(oldItem, newQuote,
					currentUser);
			boolean isExpiry = this.myQuoteSearchSB.isQuoteExpired(vo
					.getQuoteItem());
			//modified by lenon 2016/03/06 
			newItem.setQuotationEffectiveDate(date);
//			newItem.setQuotationEffectiveDateStr(sdf.format(date));
			final Quote quote=populateFormNumber(newQuote);
			newQuote.setFormNumber(quote.getFormNumber());
			LOG.info("New Form Number ===>>>" + newQuote.getFormNumber());
			// Fix INC0206096 June Guo 2015/08/25
			//newItem.setQuotationEffectiveDate(oldItem.getQuotationEffectiveDate());
			//newItem.setQuotationEffectiveDateStr(oldItem.getQuotationEffectiveDateStr());
			
			newItem.setAction(ActionEnum.MYQUOTE_QUOTE_RATE_REQUEST_CREATE.name()); // add audit action 
			oldItem.setAction(ActionEnum.MYQUOTE_QUOTE_RATE_REQUEST_UPDATE.name()); // add audit action 
			
			oldItem.setLastUpdatedBy(currentUser.getEmployeeId());
			oldItem.setLastUpdatedName(currentUser.getName());
			oldItem.setLastUpdatedOn(date);
			//Multi Currency Project 201810 (whwong) Defect ID 57
			newItem.setExRateFnl(null);
			
			LOG.info("New item isQuoteExpired ===>>>" + isExpiry);
			if (isExpiry) {
				newItem.setBuyCurr(Currency.getByCurrencyName(vo.getExChangeRate().getCurrFrom()));
				newItem.setRfqCurr(Currency.getByCurrencyName(vo.getExChangeRate().getCurrTo()));
				newItem.setExRateBuy(vo.getExChangeRateBuy().getExRateTo());
				newItem.setExRateRfq(vo.getExChangeRate().getExRateTo());
				newItem.setVat(vo.getExChangeRate().getVat());
				newItem.setHandling(vo.getExChangeRate().getHandling());
/*update by whwong (20190115): SB request should update the EXRate for expired quote also
				newItem.setExRateRfq(null);
				newItem.setExRateBuy(null);
*/
				// if quoted expiry date is expired, the stage of new quote is
				// PENDING , status = QC
				newItem.setStage(QuoteSBConstant.QUOTE_STAGE_PENDING);
				newItem.setStatus(QuoteSBConstant.RFQ_STATUS_QC);
			} else {
				/*
				 * 1. If the Quote Expiry Date is not expired, system willcopy
				 * the quote and assign the new quote number and latest
				 * ??Currency To??, ??Ex Rate To??,??Handling?? to the
				 * copiedquote. And the quote expired date of the original quote
				 * is updated to the one day before the requesting date. Both
				 * old and new quotes are sent to SAP. And the quotation form is
				 * sent out to sales again.
				 */
				// if quoted expiry date is not expired, the stage of new quote
				// is FINISH, status of new quote = status of old quote
				// the quote expired date of the original quote is updatedto the
				// one day before the requesting date.

				// the new quote number and latest ??Currency To??, ??Ex Rate
				// To??, ??Handling?? to the copied quote.
				//newItem.setExRateBuy(vo.getExChangeRate().getExRateFrom());
				newItem.setExRateBuy(vo.getExChangeRateBuy().getExRateTo());
				newItem.setExRateRfq(vo.getExChangeRate().getExRateTo());
				newItem.setBuyCurr(Currency.USD);
				newItem.setRfqCurr(Currency.getByCurrencyName(vo.getExChangeRate().getCurrTo()));

				//Multi Currency Project 201810 (whwong) FDD 5.6.2.6: update latest exRate
				//ExchangeRate exRates[];

				//exRates = Money.getExchangeRate(newItem.getBuyCurr(), newItem.getRfqCurr(), newItem.getQuote().getBizUnit(), newItem.getSoldToCustomer(), new Date());
/*				if (vo.getExChangeRate()!=null) {
					newItem.setExRateBuy(exRates[0].getExRateTo());
					newItem.setExRateRfq(exRates[1].getExRateTo());
				}
				newItem.setVat(exRates[1].getVat());
				newItem.setHandling(exRates[1].getHandling());
*/				
				newItem.setVat(vo.getExChangeRate().getVat());
				newItem.setHandling(vo.getExChangeRate().getHandling());
				newItem.setStage(QuoteSBConstant.QUOTE_STAGE_FINISH);
				newItem.setHightlighted(true);

				oldItem.setQuoteExpiryDate(addDay(date, -1));

				// Fix INC0206096 June Guo 2015/08/25
				// if the new quote expiry date is prior to quotation effective
				// date, update the quotation effective date as the 2 days
				// before the requesting date.
				if (oldItem.getQuotationEffectiveDate() != null
						&& oldItem.getQuotationEffectiveDate().after(
								oldItem.getQuoteExpiryDate())) {
					oldItem.setQuotationEffectiveDate(addDay(date, -2));
				}

				//newItem.setExRateFnl(null);
				oldItem.setHightlighted(true);

				LOG.info("old item " + oldItem.getQuoteNumber()
						+ " effective date "
						+ (oldItem.getQuotationEffectiveDate() == null ? "" : sdf.format(oldItem.getQuotationEffectiveDate()))
						+ " \r\n expiry date is ===>>>"
						+ oldItem.getQuoteExpiryDate());

				// create sap object,
				sapQuoteItems.add(oldItem);
				sapQuoteItems.add(newItem);

				// only email the old quote to sales
				//emailQuoteItems.add(newItem);			//20181127 (whwong) moved to after Quote save
				LOG.info("NEW Rate is ===>>> " + newItem.getExRateRfq());

			}
			// quoteSB.saveQuoteItem(oldItem);
			LOG.info("OldItem Quote Number ===>>> " + oldItem.getQuoteNumber() + ",Current Version===>>>" + oldItem.getVersion());
		}
		// set form number and quote number to the new quote
		Iterator<Entry<String, Quote>> it = newQuotes.entrySet().iterator();
		List<Quote> quoteList = new ArrayList<Quote>();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Quote newQuote = (Quote) entry.getValue();
			//saveQuote(newQuote); // save new Item to DB
			quoteList.add(newQuote);
		}
		if(quoteList.size() > 0) {
			LOG.info("<<===== Save New Quote ===>>> ");
			List<Quote> qSaved = saveQuote(quoteList);
			for(Quote q : qSaved){
				for (QuoteItem qi : q.getQuoteItems()){
					emailQuoteItems.add(qi);
				}
			}
			
		}

		LOG.info("<<===== Update Old QuoteItem ===>>> ");
		// save old item's change to DB
		saveQuoteItems(proceedQuoteItemVos);

		// send to sap
		if (null != sapQuoteItems && sapQuoteItems.size() > 0) {
			sapWebServiceSB.createSAPQuote(sapQuoteItems);
		}

		return emailQuoteItems;
	}

	private Date addDay(Date date, int rd) {
		if (date == null)
			return null;
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(GregorianCalendar.DATE, rd);
		date = calendar.getTime();
		return date;
	}
	
	public void calculateDPQuoteNumber(QuoteItem item) {
		QuoteNumber quoteNumber = quoteNumberSB.lockDPNumber("DPQ");
		String quoteNo = quoteNumberSB.nextNumber(quoteNumber);
		item.setQuoteNumber(quoteNo);
		LOG.info("DP Quote Number ====>>>>" + quoteNo);
		item.setAlternativeQuoteNumber(quoteNo);
		if (item.getFirstRfqCode() == null) {
			item.setFirstRfqCode(quoteNo);
		}
	}

	public String populateDPFormNumber(Quote quote) {
		QuoteNumber formNumber = quoteNumberSB.lockDPNumber("DPF");
		quote.setFormNumber(quoteNumberSB.nextNumber(formNumber));
		LOG.info("DP From Number ====>>>>" + quote.getFormNumber());
		return quote.getFormNumber();
	}
	
	
	public Quote saveDPQuote(Quote quote) {
		Quote savedQuote = null;
		savedQuote = em.merge(quote);
		em.flush();
		return savedQuote;
	}
	
	public void detach(Object entity){
		em.detach(entity);
	}
	
	
	
	/**
	 * Search mfr list.
	 *
	 * @param criteria the criteria
	 * @return the list
	 */
	public List<String> searchMfrNameList(MyQuoteSearchCriteria criteria) {

		//LOG.info(criteria.toString());

		List<String> searchResult = null;
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> c = cb.createQuery(String.class);
		
		Root<QuoteItem> quoteItem = c.from(QuoteItem.class);
		Join<QuoteItem, ProductGroup> productGroup = quoteItem.join("productGroup2", JoinType.LEFT);
		Join<QuoteItem, Quote> quote = quoteItem.join("quote");
		Join<Quote, User> sales = quote.join("sales", JoinType.LEFT);
		Join<Quote, Team> team = quote.join("team", JoinType.LEFT);
		Join<Quote, BizUnit> bizUnit = quote.join("bizUnit", JoinType.LEFT);
		Join<QuoteItem, Manufacturer> manufacturer = quoteItem.join("requestedMfr", JoinType.LEFT);
		c.select(quoteItem.<Manufacturer>get("requestedMfr").get("name"));
		c.distinct(true);
		c.orderBy(cb.asc(manufacturer.<String>get("name")));
		populateCriteria(c, criteria, quoteItem, productGroup, quote, sales, team, manufacturer, bizUnit,null,null,null,null);
		TypedQuery<String> q = em.createQuery(c);
		searchResult = q.getResultList();
		System.out.println("searchResult size:"+searchResult.size());
		return searchResult;

	}
	
	public List<Customer> searchSoldToPartyList(MyQuoteSearchCriteria criteria) {

		//LOG.info(criteria.toString());

		List<Customer> searchResult = null;
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Customer> c = cb.createQuery(Customer.class);
		
		Root<QuoteItem> quoteItem = c.from(QuoteItem.class);
		Join<QuoteItem, ProductGroup> productGroup = quoteItem.join("productGroup2", JoinType.LEFT);
		Join<QuoteItem, Quote> quote = quoteItem.join("quote");
		Join<Quote, User> sales = quote.join("sales", JoinType.LEFT);
		Join<Quote, Team> team = quote.join("team", JoinType.LEFT);
		Join<Quote, BizUnit> bizUnit = quote.join("bizUnit", JoinType.LEFT);
		Join<QuoteItem, Manufacturer> manufacturer = quoteItem.join("requestedMfr", JoinType.LEFT);
		Join<Quote, Customer> soldToCustomer = quote.join("soldToCustomer", JoinType.LEFT);
		c.select(quote.<Customer>get("soldToCustomer"));
		c.distinct(true);

		populateCriteria(c, criteria, quoteItem, productGroup, quote, sales, team, manufacturer, bizUnit,null,null,null,null);
		TypedQuery<Customer> q = em.createQuery(c);
		searchResult = q.getResultList();
		//System.out.println(searchResult.size());
		return searchResult;

	}
	
	public List<Team> searchTeamList(MyQuoteSearchCriteria criteria) {

		//LOG.info(criteria.toString());

		List<Team> searchResult = null;
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Team> c = cb.createQuery(Team.class);
		
		Root<QuoteItem> quoteItem = c.from(QuoteItem.class);
		Join<QuoteItem, ProductGroup> productGroup = quoteItem.join("productGroup2", JoinType.LEFT);
		Join<QuoteItem, Quote> quote = quoteItem.join("quote");
		Join<Quote, User> sales = quote.join("sales", JoinType.LEFT);
		Join<Quote, Team> team = quote.join("team", JoinType.LEFT);
		Join<Quote, BizUnit> bizUnit = quote.join("bizUnit", JoinType.LEFT);
		Join<QuoteItem, Manufacturer> manufacturer = quoteItem.join("requestedMfr", JoinType.LEFT);
		c.select(quote.<Team>get("team"));
		c.distinct(true);
		populateCriteria(c, criteria, quoteItem, productGroup, quote, sales, team, manufacturer, bizUnit,null,null,null,null);
		TypedQuery<Team> q = em.createQuery(c);
		searchResult = q.getResultList();
		//System.out.println(searchResult.size());
		return searchResult;

	}
	
//	public List<QuoteItemDesign> searchBMTFlagList(MyQuoteSearchCriteria criteria) {
//		LOG.info(criteria.toString());
//		List<QuoteItemDesign> searchResult = null;
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<QuoteItemDesign> c = cb.createQuery(QuoteItemDesign.class);
//		Root<QuoteItem> quoteItem = c.from(QuoteItem.class);
//		Join<QuoteItem, ProductGroup> productGroup = quoteItem.join("productGroup2", JoinType.LEFT);
//		Join<QuoteItem, Quote> quote = quoteItem.join("quote");
//		Join<Quote, User> sales = quote.join("sales", JoinType.LEFT);
//		Join<Quote, Team> team = quote.join("team", JoinType.LEFT);
//		Join<Quote, BizUnit> bizUnit = quote.join("bizUnit", JoinType.LEFT);
//		Join<QuoteItem, Manufacturer> manufacturer = quoteItem.join("requestedMfr", JoinType.LEFT);
//		c.select(quoteItem.get("quoteItemDesign"));
//		c.distinct(true);
//		populateCriteria(c, criteria, quoteItem, productGroup, quote, sales, team, manufacturer, bizUnit,null,null,null,null);
//		TypedQuery<QuoteItemDesign> q = em.createQuery(c);
//		searchResult = q.getResultList();
//		System.out.println(searchResult.size());
//		return searchResult;
//	}
	
	
	
	public List<String> searchBMTFlagList(MyQuoteSearchCriteria criteria) {
		//LOG.info(criteria.toString());
		List<String> searchResult = null;
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> c = cb.createQuery(String.class);
		Root<QuoteItem> quoteItem = c.from(QuoteItem.class);
		Join<QuoteItem, ProductGroup> productGroup = quoteItem.join("productGroup2", JoinType.LEFT);
		Join<QuoteItem, Quote> quote = quoteItem.join("quote");
		Join<Quote, User> sales = quote.join("sales", JoinType.LEFT);
		Join<Quote, Team> team = quote.join("team", JoinType.LEFT);
		Join<Quote, BizUnit> bizUnit = quote.join("bizUnit", JoinType.LEFT);
		Join<QuoteItem, Manufacturer> manufacturer = quoteItem.join("requestedMfr", JoinType.LEFT);
		c.select(quoteItem.get("quoteItemDesign").get("bmtFlag").get("bmtFlagCode"));
		c.distinct(true);
		populateCriteria(c, criteria, quoteItem, productGroup, quote, sales, team, manufacturer, bizUnit,null,null,null,null);
		TypedQuery<String> q = em.createQuery(c);
		searchResult = q.getResultList();
		System.out.println(searchResult.size());
		return searchResult;
	}
	/**
	 * Populate criteria.
	 *
	 * @param c the c
	 * @param criteria the criteria
	 * @param quoteItem the quote item
	 * @param productGroup the product group
	 * @param quote the quote
	 * @param sales the sales
	 * @param team the team
	 * @param manufacturer the manufacturer
	 * @param bizUnit the biz unit
	 */
	private void populateCriteria(CriteriaQuery c, MyQuoteSearchCriteria criteria, Root<QuoteItem> quoteItem, 
			Join<QuoteItem, ProductGroup> productGroup,
			Join<QuoteItem, Quote> quote, Join<Quote, User> sales, Join<Quote, Team> team, 
			Join<QuoteItem, Manufacturer> manufacturer, Join<Quote, BizUnit> bizUnit,
			String orderBy, String field,Map<String, Object> filter,String wPStatus, String...statisStatus) {

		Join<Quote, Customer> soldToCustomer = quote.join("soldToCustomer", JoinType.LEFT);
		Join<QuoteItem, Customer> endCustomer = quoteItem.join("endCustomer", JoinType.LEFT);
		Join<QuoteItem, QuoteItemDesign> itemDesign = quoteItem.join("quoteItemDesign", JoinType.LEFT);
		CriteriaBuilder cb = em.getCriteriaBuilder();

		List<Predicate> criterias = new ArrayList<Predicate>();
						
		if (criteria.getStage() != null && criteria.getStage().size() != 0) {
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			for(String stage : criteria.getStage()){
				Predicate predicate = cb.like(quoteItem.<String>get("stage"), stage );
				predicates.add(predicate);
			}
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));			

		}	
		if(criteria.getQuoteNumber() != null && criteria.getQuoteNumber().size() !=0 ) {
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			for(String quoteNumber : criteria.getQuoteNumber()){
				Predicate predicate = cb.like(cb.upper(quoteItem.<String>get("quoteNumber")), "%" + quoteNumber.toUpperCase() + "%" );
				predicates.add(predicate);
			}
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));
			
		}		
		if( criteria.getDataAccesses() != null && criteria.getDataAccesses().size() != 0){
			
			List<Predicate> p0 = new ArrayList<Predicate>();
			for(DataAccess dataAccess : criteria.getDataAccesses()){
				List<Predicate> p1 = new ArrayList<Predicate>();
				final Path<String> materialTypeId=quoteItem.<String>get("materialTypeId");
				final Path<String> programType=quoteItem.<String>get("programType");
				p1 = EJBQuoteUtility.addPredicates(cb, materialTypeId,programType, manufacturer, productGroup, dataAccess, p1);
				if(dataAccess.getTeam() !=	null){
					Predicate predicate = cb.equal(team.<String>get("name"), dataAccess.getTeam().getName());
					Predicate predicate2 = cb.isNull(team);
					p1.add(cb.or(predicate, predicate2));
				}
				//in case p1.size() is 0 (all dataAccess member is null), add below fake expression to make
				//p1 has one element, otherwise p0 will not have p1 element
				if(p1.size() ==0){
					Predicate predicate = cb.equal(quoteItem.<Long>get("id"), quoteItem.<Long>get("id"));
					p1.add(predicate);
				}
				
				p0.add(cb.and(p1.toArray(new Predicate[0])));

			}
			criterias.add(cb.or(p0.toArray(new Predicate[0])));
		}else{
			Predicate predicate = cb.notEqual(manufacturer.<Long>get("id"), manufacturer.<Long>get("id"));
			criterias.add(predicate);
		}
		
		// added for sales cost project by DamonChen@20170914
		if (criteria.isSalsCostAccessFlag() == null) {
			if (criteria.getWorkingPlatFormSearchCriteria() != null) {
				String selectedSalesCostPart = criteria.getWorkingPlatFormSearchCriteria().getSelectedSalesCostPart();
				if (org.apache.commons.lang.StringUtils.isNotBlank(selectedSalesCostPart)) {
					if (org.apache.commons.lang.StringUtils.equalsIgnoreCase(OPTION_YES, selectedSalesCostPart)) {
						Predicate predicate = cb.equal(quoteItem.<Boolean>get("salesCostFlag"), true);
						criterias.add(predicate);
					}
					if (org.apache.commons.lang.StringUtils.equalsIgnoreCase(OPTION_NO, selectedSalesCostPart)) {
						//Predicate predicate = cb.equal(quoteItem.<Boolean>get("salesCostFlag"), false);
						//criterias.add(predicate);
						
						List<Predicate> predicates = new ArrayList<Predicate>();
						Predicate predicate = cb.equal(quoteItem.<Boolean>get("salesCostFlag"), false);
						predicates.add(predicate);
						Predicate predicate2 = cb.isNull(quoteItem.<Boolean>get("salesCostFlag"));
						predicates.add(predicate2);
						criterias.add(cb.or(predicates.toArray(new Predicate[0])));
					}

				}
			}
		} else if (criteria.isSalsCostAccessFlag() == true) {
			// Predicate predicate =
			// cb.equal(quoteItem.<Boolean>get("salesCostFlag"), true);
			// criterias.add(predicate);

			if (criteria.getWorkingPlatFormSearchCriteria() != null) {
				String selectedSalesCostPart = criteria.getWorkingPlatFormSearchCriteria().getSelectedSalesCostPart();
				if (org.apache.commons.lang.StringUtils.isNotBlank(selectedSalesCostPart)) {
					if (org.apache.commons.lang.StringUtils.equalsIgnoreCase(OPTION_NO, selectedSalesCostPart)) {
						Predicate predicate = cb.equal(quoteItem.<String>get("quoteNumber"), "DM");
						criterias.add(predicate);
					} else {
						Predicate predicate = cb.equal(quoteItem.<Boolean>get("salesCostFlag"), true);
						criterias.add(predicate);
					}

				}
			} else {
				Predicate predicate = cb.equal(quoteItem.<Boolean>get("salesCostFlag"), true);
				criterias.add(predicate);
			}
		} else if (criteria.isSalsCostAccessFlag() == false) {
			//Predicate predicate = cb.equal(quoteItem.<Boolean>get("salesCostFlag"), false);
			//criterias.add(predicate);
			if (criteria.getWorkingPlatFormSearchCriteria() != null) {
				String selectedSalesCostPart = criteria.getWorkingPlatFormSearchCriteria().getSelectedSalesCostPart();
				if (org.apache.commons.lang.StringUtils.isNotBlank(selectedSalesCostPart)) {
					if (org.apache.commons.lang.StringUtils.equalsIgnoreCase(OPTION_YES, selectedSalesCostPart)) {
						Predicate predicate = cb.equal(quoteItem.<String>get("quoteNumber"), "DM");
						criterias.add(predicate);
					}else{

						List<Predicate> predicates = new ArrayList<Predicate>();
						Predicate predicate = cb.equal(quoteItem.<Boolean>get("salesCostFlag"), false);
						predicates.add(predicate);
						Predicate predicate2 = cb.isNull(quoteItem.<Boolean>get("salesCostFlag"));
						predicates.add(predicate2);
						criterias.add(cb.or(predicates.toArray(new Predicate[0])));
					}

				}
			}else{

				List<Predicate> predicates = new ArrayList<Predicate>();
				Predicate predicate = cb.equal(quoteItem.<Boolean>get("salesCostFlag"), false);
				predicates.add(predicate);
				Predicate predicate2 = cb.isNull(quoteItem.<Boolean>get("salesCostFlag"));
				predicates.add(predicate2);
				criterias.add(cb.or(predicates.toArray(new Predicate[0])));
			}
		}
		
		
		if (criteria.getBizUnits() != null && criteria.getBizUnits().size()!=0) {
			
			CriteriaBuilder.In<String> in = cb.in(bizUnit.<String>get("name"));
			
			for(BizUnit bu : criteria.getBizUnits()){
				in.value(bu.getName());
			}
			criterias.add(in);			
		}else{
			Predicate predicate = cb.like(bizUnit.<String>get("name"), "" );
			criterias.add(predicate);
		}
		
		//Added by Lenon.Yang(040344) 2016-11-07
		if(criteria.getWorkingPlatFormSearchCriteria() != null) {
			if(criteria.getWorkingPlatFormSearchCriteria().getStatus() != null 
					&&  criteria.getWorkingPlatFormSearchCriteria().getStatus().size() > 0) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				for (String status : criteria.getWorkingPlatFormSearchCriteria().getStatus()) {
					Predicate predicate = cb.like(quoteItem.<String>get("status"), status);
					predicates.add(predicate);
				}
				criterias.add(cb.or(predicates.toArray(new Predicate[0])));		
			}
			String selectedMfr = criteria.getWorkingPlatFormSearchCriteria().getSelectedMfr();
			if(org.apache.commons.lang.StringUtils.isNotBlank(selectedMfr) 
					&& !org.apache.commons.lang.StringUtils.equalsIgnoreCase("*ALL",selectedMfr)) {
				Predicate predicate = cb.like(cb.upper(manufacturer.<String>get("name")), "%" + selectedMfr.toUpperCase() + "%" );
				criterias.add(predicate);
			}
			
			
		}
		
		if(filter!=null&&filter.size()>0||(orderBy!=null&&orderBy.length()>0&&field!=null&&field.length()>0)){
			List<Predicate> filterCriteria = createFilterCriteria(c, criteria, quoteItem, productGroup, quote, sales, team, manufacturer, bizUnit,itemDesign,filter,orderBy,field,soldToCustomer,endCustomer);
			
			criterias.addAll(filterCriteria);
			
		}
		
		if(wPStatus !=null){
			Predicate predicate = cb.like(quoteItem.<String>get("status"), wPStatus );
			criterias.add(predicate);
		}
		
		if(statisStatus !=null && statisStatus.length > 0) {
			Predicate predicate = quoteItem.<String>get("status").in(Arrays.asList(statisStatus));
			criterias.add(predicate);
		}
		
		if (criterias.size() == 0) {
			
		} else if (criterias.size() == 1) {
			c.where(criterias.get(0));
		} else {
			c.where(cb.and(criterias.toArray(new Predicate[0])));
		}
		

	}

	/**
	 * NEC Pagination Changes :
	 * Creates the filter criteria.
	 *
	 * @param c the c
	 * @param criteria the criteria
	 * @param quoteItem the quote item
	 * @param productGroup2 the product group
	 * @param quote the quote
	 * @param sales the sales
	 * @param team the team
	 * @param manufacturer the manufacturer
	 * @param bizUnit the biz unit
	 * @param itemDesign the item design
	 * @param filters the filters
	 * @param orderBy the order by
	 * @param field the field
	 * @param soldToCustomer the sold to customer
	 * @param endCustomer the end customer
	 */
	//NEC Pagination Changes: Added for filter in pagination
	private List<Predicate> createFilterCriteria(CriteriaQuery c, MyQuoteSearchCriteria criteria,
			Root<QuoteItem> quoteItem, Join<QuoteItem, ProductGroup> productGroup2, Join<QuoteItem, Quote> quote,
			Join<Quote, User> sales, Join<Quote, Team> team, Join<QuoteItem, Manufacturer> manufacturer,
			Join<Quote, BizUnit> bizUnit, Join<QuoteItem, QuoteItemDesign> itemDesign, Map<String, Object> filters,
			String orderBy, String field, Join<Quote, Customer> soldToCustomer, Join<QuoteItem, Customer> endCustomer) {
		

		CriteriaBuilder cb = em.getCriteriaBuilder();
		Join<QuoteItem, ProductGroup> productGroup1 = quoteItem.join("productGroup1", JoinType.LEFT);

		List<Predicate> criterias = new ArrayList<Predicate>();

		if(filters==null){
			filters = new HashMap<>();
		}
		Set<String> keySet = filters.keySet();
		for (Iterator iterator = keySet.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			Object object = filters.get(key);
			String tableName = getTableName(key);
			String fieldName = getFieldName(key);
			
			if(tableName == null || "".equalsIgnoreCase(tableName)){
				if(key != null && key.contains("requestedMfr")){
					tableName ="manufacturer";
					fieldName = "name";
				}
				
			}

			Predicate predicate =null;
			switch (tableName) {
			case "quoteItem":
	            if("pendingDay".equals(fieldName)){
	                  
	                  Integer days = null;
	                  try{
	                        days = Integer.parseInt((String)object);
	                        Calendar calendarFrom = Calendar.getInstance();
	                        
	                        while(days > 0){
	                             // Pagination : changes done for defect WQ-988 
	                        	calendarFrom.add(Calendar.DAY_OF_MONTH, -1);
	                            if(!(calendarFrom.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendarFrom.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)){
	                                    days--;
	                              }
	                         // Pagination : changes ends
	                        }
	                    calendarFrom.set(Calendar.AM_PM, Calendar.AM );
	                    calendarFrom.set(Calendar.HOUR, 0);
	                    calendarFrom.set(Calendar.MINUTE, 0);
	                    calendarFrom.set(Calendar.SECOND, 0);
	                    Calendar calendarTo = (Calendar) calendarFrom.clone();
	                    calendarTo.set(Calendar.HOUR, 23);
	                    calendarTo.set(Calendar.MINUTE, 59);
	                    calendarTo.set(Calendar.SECOND, 59);
	                  
	                  predicate = cb.between(quoteItem.<Date>get("submissionDate"), calendarFrom.getTime(), calendarTo.getTime());
	                  criterias.add(cb.or(predicate));
	                        
					} catch (NumberFormatException ne){
						predicate = cb.equal(quoteItem.<Long>get("id"), 0);
						criterias.add(cb.or(predicate));
	                }
	                  
				}// Pagination : changes done for  WQ -979
	            else if ("priceValidity".equals(fieldName)) {
						predicate = cb.equal(quoteItem.<Double>get(fieldName), (String) object);
						criterias.add(cb.or(predicate));
				}
	         // Pagination : changes ends
	            //added getPropertyType(QuoteItem.class, fieldName).equals(boolean.class) by Damon
	            else if(getPropertyType(QuoteItem.class, fieldName).equals(Boolean.class) || getPropertyType(QuoteItem.class, fieldName).equals(boolean.class)){
					Boolean value = false;
					String data=((String)object).toUpperCase();
					if(data.contains("Y")||data.contains("1")||data.contains("E")||data.contains("S")){
						value = true;
						predicate = cb.equal(quoteItem.<Boolean>get(fieldName), value );
						criterias.add(cb.or(predicate));
					}else if(data.contains("N")||data.contains("O")||data.contains("0")){
						value = true;
						predicate = cb.notEqual(quoteItem.<Boolean>get(fieldName), value );
						criterias.add(cb.or(predicate));
					}
				}else if(getPropertyType(QuoteItem.class, fieldName).equals(Long.class)){
					try{
						Long value = Long.parseLong((String)object);
//						String data = format(value);
						predicate = cb.like(quoteItem.<String>get(fieldName), "%" + ((String)object).trim() + "%" );
						criterias.add(cb.or(predicate));
					}catch (NumberFormatException e) {
						predicate = cb.equal(quoteItem.<Long>get("id"), 0);
						criterias.add(cb.or(predicate));
					}
				}else if(getPropertyType(QuoteItem.class, fieldName).equals(Integer.class)){
					try {
						Integer value = Integer.parseInt((String)object);
//						String data = format(value);
						predicate = cb.like(quoteItem.<String>get(fieldName), "%" + ((String)object).trim() + "%" );
						criterias.add(cb.or(predicate));
					} catch (NumberFormatException e) {
						predicate = cb.equal(quoteItem.<Long>get("id"), 0);
						criterias.add(cb.or(predicate));
					}
				}else if(getPropertyType(QuoteItem.class, fieldName).equals(Double.class)){
					try {
						Double value = Double.parseDouble((String)object);
						String filterWord = ((String)object).trim();
						filterWord = formatDecimal(filterWord);
						if(filterWord.startsWith(".")){
							predicate = cb.like(quoteItem.<String>get(fieldName),  filterWord + "%" );
							criterias.add(cb.or(predicate));
						}else{
							predicate = cb.like(quoteItem.<String>get(fieldName), "%" + filterWord + "%" );
							criterias.add(cb.or(predicate));
						}
					} catch (NumberFormatException e) {
						predicate = cb.equal(quoteItem.<Long>get("id"), 0);
						criterias.add(cb.or(predicate));
					}
				}else if(getPropertyType(QuoteItem.class, fieldName).equals(BigDecimal.class)){
					BigDecimal  value = new BigDecimal(0);
				    try {
			            if(object instanceof BigDecimal) {
			            	value = (BigDecimal) object;
			            	String filterWord = ((String)object).trim();
							filterWord = formatDecimal(filterWord);
							if(filterWord.startsWith(".")){
								predicate = cb.like(quoteItem.<String>get(fieldName),  filterWord + "%" );
								criterias.add(cb.or(predicate));
							}else{
								predicate = cb.like(quoteItem.<String>get(fieldName), "%" + filterWord + "%" );
								criterias.add(cb.or(predicate));
							}
			            } else if(object instanceof String) {
			            	value = new BigDecimal((String) object);
			            	String filterWord = ((String)object).trim();
							filterWord = formatDecimal(filterWord);
							if(filterWord.startsWith(".")){
								predicate = cb.like(quoteItem.<String>get(fieldName),  filterWord + "%" );
								criterias.add(cb.or(predicate));
							}else{
								predicate = cb.like(quoteItem.<String>get(fieldName), "%" + filterWord + "%" );
								criterias.add(cb.or(predicate));
							}
			            } else if(object instanceof BigInteger) {
			            	value = new BigDecimal((BigInteger) object);
			            	String filterWord = ((String)object).trim();
							filterWord = formatDecimal(filterWord);
							if(filterWord.startsWith(".")){
								predicate = cb.like(quoteItem.<String>get(fieldName),  filterWord + "%" );
								criterias.add(cb.or(predicate));
							}else{
								predicate = cb.like(quoteItem.<String>get(fieldName), "%" + filterWord + "%" );
								criterias.add(cb.or(predicate));
							}
			            } else if(object instanceof Number) {
			            	value = new BigDecimal(((Number) object).doubleValue());
			            	String filterWord = ((String)object).trim();
							filterWord = formatDecimal(filterWord);
							if(filterWord.startsWith(".")){
								predicate = cb.like(quoteItem.<String>get(fieldName),  filterWord + "%" );
								criterias.add(cb.or(predicate));
							}else{
								predicate = cb.like(quoteItem.<String>get(fieldName), "%" + filterWord + "%" );
								criterias.add(cb.or(predicate));
							}
			            } else {
			            	LOGGER.log(Level.SEVERE, "Not possible to coerce [" + object + "] from class " + object.getClass() + " into a BigDecimal.");
			            }
			        } catch (NumberFormatException e) {
			        	LOGGER.log(Level.SEVERE, "Not possible to coerce [" + object + "] from class " + object.getClass() + " into a BigDecimal.");
						predicate = cb.equal(quoteItem.<Long>get("id"), 0);
						criterias.add(cb.or(predicate));
			        } catch (ClassCastException e) {
			        	LOGGER.log(Level.SEVERE, "Not possible to coerce [" + object + "] from class " + object.getClass() + " into a BigDecimal.");
						predicate = cb.equal(quoteItem.<Long>get("id"), 0);
						criterias.add(cb.or(predicate));
			        } catch (Exception e) {
			            LOGGER.log(Level.SEVERE, "Exception caught " + e);
						predicate = cb.equal(quoteItem.<Long>get("id"), 0);
						criterias.add(cb.or(predicate));
			        }
				    try{
//				    	String data = format(value);
				    	predicate = cb.like(quoteItem.<String>get(fieldName), "%" + ((String)object).trim() + "%" );
				    	criterias.add(cb.or(predicate));
				    }catch (Exception e) {
						predicate = cb.equal(quoteItem.<Long>get("id"), 0);
						criterias.add(cb.or(predicate));
					}
				}else if(getPropertyType(QuoteItem.class, fieldName).equals(Date.class)){
//					Calendar calendar = Calendar.getInstance();
//					calendar.add(Calendar.YEAR, -2);
//
//					calendar.set(Calendar.HOUR_OF_DAY, 0);
//					calendar.set(Calendar.MINUTE, 0);
//					calendar.set(Calendar.SECOND, 0);
//					calendar.set(Calendar.MILLISECOND, 0);

					Calendar calendar = new GregorianCalendar(9999, 11, 31);
					// Pagination : changes done for WQ-977
//					calendar.set(9998, 12, 31);
					// Pagination : changes ends
					Date dateFrom = calendar.getTime();
					try {
						dateFrom = QuoteUtil.convertStringToDate((String)object,"dd/MM/yyyy");
					} catch (ParseException e) {
						LOG.warning("Unable to parse date");
					}
					Calendar calendarFrom =  new GregorianCalendar(9999, 11, 31);
					calendarFrom.setTime(dateFrom);
					calendarFrom.set(Calendar.AM_PM, Calendar.AM);
					calendarFrom.set(Calendar.HOUR_OF_DAY, 0);
					calendarFrom.set(Calendar.MINUTE, 0);
					calendarFrom.set(Calendar.SECOND, 0);
					calendarFrom.set(Calendar.MILLISECOND, 0);
					Date dateTo = (Date) dateFrom.clone();
					Calendar calendarTo =  Calendar.getInstance();
					calendarTo.setTime(dateTo);
					calendarTo.set(Calendar.HOUR, 23);
					calendarTo.set(Calendar.MINUTE, 59);
					calendarTo.set(Calendar.SECOND, 59);
					predicate = cb.between(quoteItem.<Date>get(fieldName), calendarFrom.getTime(), calendarTo.getTime());
					criterias.add(cb.or(predicate));
				} else{
					predicate = cb.like(cb.upper(quoteItem.<String>get(fieldName)), "%" + ((String)object).toUpperCase().trim() + "%" );
					criterias.add(cb.or(predicate));
				}
				break;
			case "quote":
				 if(getPropertyType(Quote.class, fieldName).equals(Boolean.class) || 
						 getPropertyType(Quote.class, fieldName).equals(boolean.class)) {
					Boolean value = false;
					String data=((String)object).toUpperCase();
					if(data.contains("Y")||data.contains("1")||data.contains("E")||data.contains("S")){
						value = true;
						predicate = cb.equal(quote.<Boolean>get(fieldName), value );
						criterias.add(cb.or(predicate));
					}else if(data.contains("N")||data.contains("O")||data.contains("0")){
						value = true;
						predicate = cb.notEqual(quote.<Boolean>get(fieldName), value );
						criterias.add(cb.or(predicate));
					}
				} else {
					predicate = cb.like(cb.upper(quote.<String>get(fieldName)), "%" + ((String)object).toUpperCase().trim() + "%" );
					criterias.add(cb.or(predicate));
				}
				
				break;
			case "sales":
				predicate = cb.like(cb.upper(sales.<String>get(fieldName)), "%" + ((String)object).toUpperCase().trim() + "%" );
				criterias.add(cb.or(predicate));
				break;
			case "team":
				predicate = cb.like(cb.upper(team.<String>get(fieldName)), "%" + ((String)object).toUpperCase().trim() + "%" );
				criterias.add(cb.or(predicate));
				break;
			case "quoteItemDesign":
				if(getPropertyType(QuoteItemDesign.class, fieldName).equals(Boolean.class)){
					Boolean value = false;
					String data=((String)object).toUpperCase();
					if(data.contains("Y")||data.contains("1")||data.contains("E")||data.contains("S")){
						value = true;
						predicate = cb.equal(itemDesign.<Boolean>get(fieldName), value );
						criterias.add(cb.or(predicate));
					}else if(data.contains("N")||data.contains("O")||data.contains("0")){
						value = false;
						predicate = cb.equal(itemDesign.<Boolean>get(fieldName), value );
						criterias.add(cb.or(predicate));
					}
				}else if(getPropertyType(QuoteItemDesign.class, fieldName).equals(Long.class)){
					try{
						Long value = Long.parseLong((String)object);
//						String data = format(value);
						predicate = cb.like(itemDesign.<String>get(fieldName), "%" + ((String)object).trim() + "%" );
						criterias.add(cb.or(predicate));
					}catch (NumberFormatException e) {
						predicate = cb.equal(quoteItem.<Long>get("id"), 0);
						criterias.add(cb.or(predicate));
					}
				}else if(getPropertyType(QuoteItemDesign.class, fieldName).equals(Integer.class)){
					try{
						Integer value = Integer.parseInt((String)object);
//						String data = format(value);
						predicate = cb.like(itemDesign.<String>get(fieldName), "%" + ((String)object).trim() + "%" );
						criterias.add(cb.or(predicate));
					}catch (NumberFormatException e) {
						predicate = cb.equal(quoteItem.<Long>get("id"), 0);
						criterias.add(cb.or(predicate));
					}
				}else if(getPropertyType(QuoteItemDesign.class, fieldName).equals(Double.class)){
					try{
						Double value = Double.parseDouble((String)object);
//						String data = format(value);
						predicate = cb.like(itemDesign.<String>get(fieldName), "%" + ((String)object).trim() + "%" );
						criterias.add(cb.or(predicate));
					}catch (NumberFormatException e) {
						predicate = cb.equal(quoteItem.<Long>get("id"), 0);
						criterias.add(cb.or(predicate));
					}
				}else if(getPropertyType(QuoteItemDesign.class, fieldName).equals(BigDecimal.class)){
					BigDecimal  value = new BigDecimal(0);
				    try {
			            if(object instanceof BigDecimal) {
			            	value = (BigDecimal) object;
			            } else if(object instanceof String) {
			            	value = new BigDecimal((String) object);
			            } else if(object instanceof BigInteger) {
			            	value = new BigDecimal((BigInteger) object);
			            } else if(object instanceof Number) {
			            	value = new BigDecimal(((Number) object).doubleValue());
			            } else {
			            	LOGGER.log(Level.SEVERE, "Not possible to coerce [" + object + "] from class " + object.getClass() + " into a BigDecimal.");
			            }
			        } catch (NumberFormatException e) {
			        	LOGGER.log(Level.SEVERE, "Not possible to coerce [" + object + "] from class " + object.getClass() + " into a BigDecimal.");
						predicate = cb.equal(quoteItem.<Long>get("id"), 0);
						criterias.add(cb.or(predicate));
			        } catch (ClassCastException e) {
			        	LOGGER.log(Level.SEVERE, "Not possible to coerce [" + object + "] from class " + object.getClass() + " into a BigDecimal.");
						predicate = cb.equal(quoteItem.<Long>get("id"), 0);
						criterias.add(cb.or(predicate));
			        } catch (Exception e) {
			            LOGGER.log(Level.SEVERE, "Exception caught " + e);
						predicate = cb.equal(quoteItem.<Long>get("id"), 0);
						criterias.add(cb.or(predicate));
			        }
				    try{
//				    	String data = format(value);
				    	predicate = cb.like(itemDesign.<String>get(fieldName), "%" + ((String)object).trim() + "%" );
				    	criterias.add(cb.or(predicate));
				    }catch (NumberFormatException e) {
						predicate = cb.equal(quoteItem.<Long>get("id"), 0);
						criterias.add(cb.or(predicate));
					}
				}else if(getPropertyType(QuoteItemDesign.class, fieldName).equals(Date.class)){
					// Pagination : changes done for WQ-977
					Calendar calendar = new GregorianCalendar(9999, 11, 31);
//					calendar.set(9998, 12, 31);
					// Pagination : changes ends
					Date dateFrom = calendar.getTime();
					try {
						dateFrom = QuoteUtil.convertStringToDate((String)object,"dd/MM/yyyy");
					} catch (ParseException e) {
						LOG.warning("Unable to parse date");
					}
					Calendar calendarFrom = new GregorianCalendar(9999, 11, 31);
					calendarFrom.set(Calendar.AM_PM, Calendar.AM);
					calendarFrom.setTime(dateFrom);
					calendarFrom.set(Calendar.HOUR_OF_DAY, 0);
					calendarFrom.set(Calendar.MINUTE, 0);
					calendarFrom.set(Calendar.SECOND, 0);
					calendarFrom.set(Calendar.MILLISECOND, 0);
					Date dateTo = (Date) dateFrom.clone();
					Calendar calendarTo = Calendar.getInstance();
					calendarTo.setTime(dateTo);
					calendarTo.set(Calendar.HOUR, 23);
					calendarTo.set(Calendar.MINUTE, 59);
					calendarTo.set(Calendar.SECOND, 59);
					if(fieldName.equalsIgnoreCase("drEffectiveDate")||fieldName.equalsIgnoreCase("drExpiryDate")){
						predicate = cb.equal(itemDesign.<Date>get(fieldName), calendarTo.getTime());
					}else{
						predicate = cb.between(itemDesign.<Date>get(fieldName), calendarFrom.getTime(), calendarTo.getTime());
					}
					criterias.add(cb.or(predicate));

				}else{
					predicate = cb.like(cb.upper(itemDesign.<String>get(fieldName)), "%" + ((String)object).toUpperCase().trim() + "%" );
					criterias.add(cb.or(predicate));
				}
				break;
			case "soldToCustomer":
				if(!"".equalsIgnoreCase(fieldName) && "customerNumber".equalsIgnoreCase(fieldName)){
					try{
						Integer value = Integer.parseInt((String)object);
//					String data = format(value);
						predicate = cb.like(soldToCustomer.<String>get(fieldName), "%" + ((String)object).trim() + "%" );
						criterias.add(cb.or(predicate));
					}catch (NumberFormatException e) {
						predicate = cb.equal(quoteItem.<Long>get("id"), 0);
						criterias.add(cb.or(predicate));
					}
				}else{
					//for solve full name combined by name1 and name2...
					//Expression<String> emptySpace = cb.parameter(String.class, "");
					String emptySpace = "";
					String space = " ";
					Expression<String> e1= soldToCustomer.<String>get("customerName1");
					Expression<String> e2= soldToCustomer.<String>get("customerName2");
					Expression<String> e3= soldToCustomer.<String>get("customerName3");
					Expression<String> e4= soldToCustomer.<String>get("customerName4");
					Expression<String> r1 = cb.<String>selectCase().when(cb.isNull(e1), emptySpace).
							when(cb.lessThan(cb.length(cb.trim(e1)), 1), emptySpace).otherwise(e1);
					Expression<String> r2 = cb.<String>selectCase().when(cb.isNull(e2), emptySpace).
							when(cb.lessThan(cb.length(cb.trim(e2)), 1), emptySpace).otherwise(cb.concat(space, e2));
					Expression<String> r3 = cb.<String>selectCase().when(cb.isNull(e3), emptySpace).
							when(cb.lessThan(cb.length(cb.trim(e3)), 1), emptySpace).otherwise(cb.concat(space, e3));
					Expression<String> r4 = cb.<String>selectCase().when(cb.isNull(e4), emptySpace).
							when(cb.lessThan(cb.length(cb.trim(e4)), 1), emptySpace).otherwise(cb.concat(space, e4));
					Expression<String> fullNameExp = cb.concat(cb.concat(r1, r2), cb.concat(r3, r4)) ;
					String pram = ("%" + ((String)object).trim() + "%").toUpperCase();
					criterias.add(cb.like(cb.upper(fullNameExp), pram));
					/*criterias.add(cb.or(
						cb.like(cb.upper(e1), pram),
						cb.like(cb.upper(e2), pram),
						cb.like(cb.upper(e3), pram),
						cb.like(cb.upper(e4), pram),
						cb.like(cb.upper(fullNameExp), pram)
						));*/
				
				}
				break;
			case "shipToCustomer":
				Integer value = Integer.parseInt((String)object);
//				String data = format(value);
			    predicate = cb.like(endCustomer.<String>get(fieldName), "%" + ((String)object).trim() + "%" );
				criterias.add(cb.or(predicate));
				break;
			case "endCustomer":
				if(!"".equalsIgnoreCase(fieldName) && "customerNumber".equalsIgnoreCase(fieldName)){
					Integer value1 = Integer.parseInt((String)object);
//					String datas = format(value1);
				    predicate = cb.like(endCustomer.<String>get(fieldName), "%" + ((String)object).trim() + "%" );
					criterias.add(cb.or(predicate));
				}else{
				// Pagination: changes done for defect WQ-978
				String upperCase=(String)object;
				upperCase= upperCase.trim().toUpperCase();
				
				criterias.add(cb.or(
						cb.like( cb.upper(endCustomer.<String>get("customerName1")), "%" + upperCase + "%" ),
						cb.like(cb.upper(endCustomer.<String>get("customerName2")), "%" + upperCase + "%" ),
						cb.like(cb.upper(endCustomer.<String>get("customerName3")), "%" + upperCase + "%" ),
						cb.like(cb.upper(endCustomer.<String>get("customerName4")), "%" + upperCase + "%" ))
						);
				}
				// Pagination: WQ-978 changes ends

				break;
			case "productGroup2":
				predicate = cb.like(cb.upper(productGroup2.<String>get(fieldName)), "%" + ((String)object).toUpperCase().trim() + "%" );
				criterias.add(cb.or(predicate));
				break;
			case "productGroup1":
				predicate = cb.like(cb.upper(productGroup1.<String>get(fieldName)), "%" + ((String)object).toUpperCase().trim() + "%" );
				criterias.add(cb.or(predicate));
				break;
			case "manufacturer":
				predicate = cb.like(cb.upper(manufacturer.<String>get(fieldName)), "%" + ((String)object).toUpperCase().trim() + "%" );
				criterias.add(cb.or(predicate));
				break;
			case "bizUnit":
				predicate = cb.like(cb.upper(bizUnit.<String>get(fieldName)), "%" + ((String)object).toUpperCase().trim() + "%" );
				criterias.add(cb.or(predicate));
				break;
			case "bmtFlag":	
				 predicate = cb.like(itemDesign.get("bmtFlag").<String>get("bmtFlagCode"), "%" + ((String)object).toUpperCase().trim() + "%" );
				 criterias.add(cb.or(predicate));
				break;
			default:
				break;
			}

		}
		if(orderBy!=null&&orderBy.length()>0&&field!=null&&field.length()>0){
			String tableName = getTableName(field);
			String fieldName = getFieldName(field);

			if(tableName == null || "".equalsIgnoreCase(tableName)){
				if(field != null && field.contains("requestedMfr")){
					tableName ="manufacturer";
					fieldName = "name";
				}
				
			}
			
			switch (tableName) {
			case "quoteItem":
				 if("pendingDay".equals(fieldName) ){
					 fieldName="submissionDate";
						// fixed for defect#277 that In working platform pending list, the arrow setting of pending days is wrong, when I sorted "ascending", but pending days data is shown "descending
					if ("DESC".equals(orderBy)) {
						c.orderBy(cb.asc(quoteItem.get(fieldName)));
					} else if ("ASC".equals(orderBy)) {
						c.orderBy(cb.desc(quoteItem.get(fieldName)));
					}
				} else {
					if ("ASC".equals(orderBy)) {
						c.orderBy(cb.asc(quoteItem.get(fieldName)));
					} else if ("DESC".equals(orderBy)) {
						c.orderBy(cb.desc(quoteItem.get(fieldName)));
					}
				}
				break;
			case "productGroup2":
				 if("ASC".equals(orderBy)){
						c.orderBy(cb.asc(productGroup2.get(fieldName)));
					}else if("DESC".equals(orderBy)){
						c.orderBy(cb.desc(productGroup2.get(fieldName)));
					}
					break;
			case "productGroup1":
				 if("ASC".equals(orderBy)){
						c.orderBy(cb.asc(productGroup1.get(fieldName)));
					}else if("DESC".equals(orderBy)){
						c.orderBy(cb.desc(productGroup1.get(fieldName)));
					}
					break;
			case "quote":
				if("ASC".equals(orderBy)){
					c.orderBy(cb.asc(quote.get(fieldName)));
				}else if("DESC".equals(orderBy)){
					c.orderBy(cb.desc(quote.get(fieldName)));
				}
				break;
			case "sales":
				if("ASC".equals(orderBy)){
					c.orderBy(cb.asc(sales.get(fieldName)));
				}else if("DESC".equals(orderBy)){
					c.orderBy(cb.desc(sales.get(fieldName)));
				}
				break;
			case "team":
				if("ASC".equals(orderBy)){
					c.orderBy(cb.asc(team.get(fieldName)));
				}else if("DESC".equals(orderBy)){
					c.orderBy(cb.desc(team.get(fieldName)));
				}
				break;
			case "soldToCustomer":
				if(fieldName.equalsIgnoreCase("customerNumber")){
					if("ASC".equals(orderBy)){
						c.orderBy(cb.asc(soldToCustomer.get(fieldName)));
					}else if("DESC".equals(orderBy)){
						c.orderBy(cb.desc(soldToCustomer.get(fieldName)));
					}	
				}else if("ASC".equals(orderBy)){
		              List<Order> orderList = new ArrayList<Order>();
		              orderList.add(cb.asc(soldToCustomer.get("customerName1")));
		              orderList.add(cb.asc(soldToCustomer.get("customerName2")));
		              orderList.add(cb.asc(soldToCustomer.get("customerName3")));
		              orderList.add(cb.asc(soldToCustomer.get("customerName4")));
		              c.orderBy(orderList);
				}else if("DESC".equals(orderBy)){
					  List<Order> orderList = new ArrayList<Order>();
		              orderList.add(cb.desc(soldToCustomer.get("customerName1")));
		              orderList.add(cb.desc(soldToCustomer.get("customerName2")));
		              orderList.add(cb.desc(soldToCustomer.get("customerName3")));
		              orderList.add(cb.desc(soldToCustomer.get("customerName4")));
		              c.orderBy(orderList);
				}
				break;
			case "endCustomer":
				if(fieldName.equalsIgnoreCase("customerNumber")){
					if("ASC".equals(orderBy)){
						c.orderBy(cb.asc(endCustomer.get(fieldName)));
					}else if("DESC".equals(orderBy)){
						c.orderBy(cb.desc(endCustomer.get(fieldName)));
					}	
				}else if("ASC".equals(orderBy)){
					 List<Order> orderList = new ArrayList<Order>();
		              orderList.add(cb.asc(endCustomer.get("customerName1")));
		              orderList.add(cb.asc(endCustomer.get("customerName2")));
		              orderList.add(cb.asc(endCustomer.get("customerName3")));
		              orderList.add(cb.asc(endCustomer.get("customerName4")));
		              c.orderBy(orderList);
				}else if("DESC".equals(orderBy)){
					 List<Order> orderList = new ArrayList<Order>();
		              orderList.add(cb.asc(endCustomer.get("customerName1")));
		              orderList.add(cb.asc(endCustomer.get("customerName2")));
		              orderList.add(cb.asc(endCustomer.get("customerName3")));
		              orderList.add(cb.asc(endCustomer.get("customerName4")));
		              c.orderBy(orderList);
				}
				break;
			case "quoteItemDesign":
				if("ASC".equals(orderBy)){
					c.orderBy(cb.asc(itemDesign.get(fieldName)));
				}else if("DESC".equals(orderBy)){
					c.orderBy(cb.desc(itemDesign.get(fieldName)));
				}
				break;				
			case "manufacturer":
				if("ASC".equals(orderBy)){
					c.orderBy(cb.asc(manufacturer.get(fieldName)));
				}else if("DESC".equals(orderBy)){
					c.orderBy(cb.desc(manufacturer.get(fieldName)));
				}
				break;
			default:
				break;
			}

		}
	
		return criterias;
	}

	

	/**
	 * NEC Pagination Changes :
	 * Gets the table name from input data that is passed from xhtml file for filtering and sorting.
	 *
	 * @param data the data
	 * @return the table name
	 */
	private String getTableName(String data){
		String tableName ="";
		String []array=data.split("\\.");
		if(array.length>=2){
			tableName = array[array.length-2];
		}
		return tableName;
	}

	/**
	 * NEC Pagination Changes :
	 * Gets the field name from input data that is passed from xhtml file for filtering and sorting.
	 *
	 * @param data the data
	 * @return the field name
	 */
	private String getFieldName(String data){
		String fieldName ="";
		String []array=data.split("\\.");
		if(array.length>=1){
			fieldName = array[array.length-1];
		}else{
			fieldName = data;
		}
		return fieldName;
	}
	
	//removes leading zero in case of decimal
		public String formatDecimal(String value) {
			if(value.contains(".")){
				value = org.apache.commons.lang.StringUtils.stripStart(value, "0");
				
			}
			   return value;
		}
	
	/**
	 * NEC Pagination Changes :
	 * Gets the property type from entity classes.
	 *
	 * @param cls the cls
	 * @param propertyName the property name
	 * @return the property type
	 */
	private Object getPropertyType(Class<?> cls, String propertyName) {
		Object retvalue = null;
		try {
			java.lang.reflect.Field field = cls.getDeclaredField(propertyName);
			// field.setAccessible(true);
			retvalue = field.getType();
		} catch (Exception e) {
			try {
				LOGGER.log(Level.SEVERE, "Exception in getting property type for property name : "+propertyName+", Exception message: "+e.getMessage(), e);
				java.lang.reflect.Field field = cls.getSuperclass().getDeclaredField(propertyName);
				retvalue = field.getType();
			} catch (Exception ex) {
				LOGGER.log(Level.SEVERE, "Exception in getting property type for property name : "+propertyName+", Exception message: "+ex.getMessage(), ex);
			}
		}
		return retvalue;
	}
	
	

/**
 * NEC Pagination Changes :
 * Gets the count criteria used in datatable with lazy loading.
 *
 * @param criteria the criteria
 * @param orderBy the order by
 * @param field the field
 * @param b the b
 * @param filter the filter
 * @return the count criteria
 */
	public int searchCount(MyQuoteSearchCriteria criteria, String orderBy, String field, Map<String, Object> filter, String wPStatus){
		
		//LOG.info(criteria.toString());
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<Long> c = cb.createQuery(Long.class);

		Root<QuoteItem> quoteItem = c.from(QuoteItem.class);
		
		//Join<QuoteItem, MaterialType> materialType = quoteItem.join("materialType", JoinType.LEFT);
		Join<QuoteItem, ProductGroup> productGroup = quoteItem.join("productGroup2", JoinType.LEFT);
		//Join<QuoteItem, ProgramType> programType = quoteItem.join("programType", JoinType.LEFT);
		Join<QuoteItem, Quote> quote = quoteItem.join("quote");
		Join<Quote, User> sales = quote.join("sales", JoinType.LEFT);
		Join<Quote, Team> team = quote.join("team", JoinType.LEFT);
		Join<Quote, BizUnit> bizUnit  = quote.join("bizUnit", JoinType.LEFT);
		Join<Quote, Customer> soldToCustomer = quote.join("soldToCustomer", JoinType.LEFT);
		Join<QuoteItem, Customer> endCustomer = quoteItem.join("endCustomer", JoinType.LEFT);
		//Join<QuoteItem, Material> material = quoteItem.join("requestedMaterial", JoinType.LEFT);
		//Join<QuoteItem, Material> quotedMaterial = quoteItem.join("quotedMaterial", JoinType.LEFT);
		Join<QuoteItem, Manufacturer> manufacturer = quoteItem.join("requestedMfr", JoinType.LEFT);
		Join<QuoteItem, QuoteItemDesign> itemDesign = quoteItem.join("quoteItemDesign", JoinType.LEFT);

		c.orderBy(cb.asc(quoteItem.get("submissionDate")));
		
		populateCriteria(c, criteria, quoteItem, productGroup, quote, sales, team, manufacturer, bizUnit, orderBy, field, filter,wPStatus);
		
		c.select(cb.count(quoteItem));
		
		int count =  em.createQuery(c).getSingleResult().intValue();
		
		return count;
	}
	
	public List<DropDownFilters> searchFilterDropDownList(MyQuoteSearchCriteria criteria, String orderBy, String field, Map<String, Object> filter, String wPStatus){
		
		LOG.info(criteria.toString());
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<DropDownFilters> c = cb.createQuery(DropDownFilters.class);

		Root<QuoteItem> quoteItem = c.from(QuoteItem.class);
		
		Join<QuoteItem, ProductGroup> productGroup = quoteItem.join("productGroup2", JoinType.LEFT);
		Join<QuoteItem, Quote> quote = quoteItem.join("quote");
		Join<Quote, User> sales = quote.join("sales", JoinType.LEFT);
		Join<Quote, Team> team = quote.join("team", JoinType.LEFT);
		Join<Quote, BizUnit> bizUnit  = quote.join("bizUnit", JoinType.LEFT);
		Join<Quote, Customer> soldToCustomer = quote.join("soldToCustomer", JoinType.LEFT);
		 
		Join<QuoteItem, Manufacturer> manufacturer = quoteItem.join("requestedMfr", JoinType.LEFT);
		Join<QuoteItem, QuoteItemDesign> itemDesign = quoteItem.join("quoteItemDesign", JoinType.LEFT);
		Expression<?>[] targetExpressions = {
				soldToCustomer.get("customerName1"),
				soldToCustomer.get("customerName2"),
				soldToCustomer.get("customerName3"),
				soldToCustomer.get("customerName4"),
				team.get("name"),
				quoteItem.get("designRegion"),
				manufacturer.get("name"),
				itemDesign.get("bmtFlag").get("bmtFlagCode")};
		
		populateCriteria(c, criteria, quoteItem, productGroup, quote, sales, team, manufacturer, bizUnit, orderBy, field, filter,wPStatus);
		c.multiselect(targetExpressions).groupBy(targetExpressions);
		return  em.createQuery(c).getResultList();
		
	}
	/***
	 * @param criteria
	 * @param statusArray
	 * @return
	 */
	public Map<String, Integer> searchStaticsCount(MyQuoteSearchCriteria criteria, Map<String, Object> filters, String[] statusArray){
		Map<String, Integer> statisMap = new HashMap<String, Integer> ();
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<Object[]> c = cb.createQuery(Object[].class);

		Root<QuoteItem> quoteItem = c.from(QuoteItem.class);
		
		//Join<QuoteItem, MaterialType> materialType = quoteItem.join("materialType", JoinType.LEFT);
		Join<QuoteItem, ProductGroup> productGroup = quoteItem.join("productGroup2", JoinType.LEFT);
		//Join<QuoteItem, ProgramType> programType = quoteItem.join("programType", JoinType.LEFT);
		Join<QuoteItem, Quote> quote = quoteItem.join("quote");
		Join<Quote, User> sales = quote.join("sales", JoinType.LEFT);
		Join<Quote, Team> team = quote.join("team", JoinType.LEFT);
		Join<Quote, BizUnit> bizUnit  = quote.join("bizUnit", JoinType.LEFT);
		Join<Quote, Customer> soldToCustomer = quote.join("soldToCustomer", JoinType.LEFT);
		Join<QuoteItem, Customer> endCustomer = quoteItem.join("endCustomer", JoinType.LEFT);
		//Join<QuoteItem, Material> material = quoteItem.join("requestedMaterial", JoinType.LEFT);
		//Join<QuoteItem, Material> quotedMaterial = quoteItem.join("quotedMaterial", JoinType.LEFT);
		Join<QuoteItem, Manufacturer> manufacturer = quoteItem.join("requestedMfr", JoinType.LEFT);
		Join<QuoteItem, QuoteItemDesign> itemDesign = quoteItem.join("quoteItemDesign", JoinType.LEFT);

		populateCriteria(c, criteria, quoteItem, productGroup, quote, sales, team, manufacturer, bizUnit, null, null, filters, null, statusArray);
		c.multiselect(quoteItem.<String>get("status"),
				cb.count(quoteItem.<String>get("status")).alias("cnt")).groupBy(quoteItem.<String>get("status"));
		List<Object[]> resultList = em.createQuery(c).getResultList();
		for(Object[] r :resultList) {
			statisMap.put((String)r[0],
					((Long)r[1]).intValue());
		}
		return statisMap;
	}

	//NEC Pagination Changes: Method created to fetch records for current page
	public List findLazyData(MyQuoteSearchCriteria criteria, int first, int pageSize,
			String field, String orderBy, Map<String, Object> filter) {
		return search(criteria, first, pageSize, orderBy, field, filter);
	}

	//NEC Pagination Changes: Method created to find count for records in database
	public int findLazyDataCount(MyQuoteSearchCriteria criteria, String rfqStatusQc, String field, String orderBy,
			Map<String, Object> filters) {
		return searchCount(criteria,orderBy,field,filters,rfqStatusQc);
	}

	//NEC Pagination Changes: Method created to fetch records through asynchronous call to database
	@Asynchronous
	public void findLazyNextPageDataAsynchronously(MyQuoteSearchCriteria criteria, int first, int pageSize, int pageNumber,
			String sortField, String sort, Map<String, Object> filters,
			ConcurrentHashMap<Integer, List<WorkingPlatformItemVO>> map, Queue<Integer> queue, int cachePageSize) {
		List workingPlatformItemVOList = findLazyData(criteria, first, pageSize, sortField, sort, filters);
		if(workingPlatformItemVOList.size()>0){
			map.put(pageNumber, workingPlatformItemVOList);
		}
		queue.add(pageNumber);
		if(cachePageSize < map.size()){
			map.remove(queue.remove());
		}
	}
	
	public String createQuoteBuilderFormNumber(BizUnit bizUnit) {
		QuoteNumber formNumber = quoteNumberSB.lockNumber("FRM",bizUnit);
		return quoteNumberSB.nextNumber(formNumber);
	}
	
	public String createQuoteBuilderQuoteNumber(BizUnit bizUnit) {
		return populateQuoteNumberOnly(bizUnit);
	}

	/**
	 * @throws WebQuoteException    
	* @Description: TODO
	* @author 042659
	* @param quoteItem
	* @param dupQuoteItem
	* @param user
	* @return      
	* @return String    
	* @throws  
	*/  
	public String generateDuplicatedQuoteItem(QuoteItem origQuoteItem,QuoteItem dupQuoteItem, User user) throws WebQuoteException {
		// TODO Auto-generated method stub
		
		try {
			String returnStr="NO-DUP";
			
			if (origQuoteItem.getDesignRegion() == null || origQuoteItem.getDesignRegion().trim().equals("")|| origQuoteItem.getDesignRegion().trim().equals("ASIA")) {
				
			} else {
				String nseconds=""+origQuoteItem.getSoldToCustomer().getCustomerNumber()+System.nanoTime();
				 LOG.info("nseconds:"+nseconds);
				if (origQuoteItem.getBmtCheckedFlag() !=null && origQuoteItem.getBmtCheckedFlag()==true) {
					BeanUtilsExtends.copyProperties(dupQuoteItem, origQuoteItem);
					dupQuoteItem.setId(0);
					dupQuoteItem.setQuoteNumber(null);
					dupQuoteItem.setVersion(0);
					
					if(origQuoteItem.getAttachments()!=null && origQuoteItem.getAttachments().size()>0){
						List<Attachment> newAttachments = new ArrayList<>();
						for (Attachment attachment : origQuoteItem.getAttachments()) {
								Attachment newAttachment = new Attachment();
								BeanUtils.copyProperties(newAttachment, attachment);
								newAttachment.setId(0L);
								newAttachment.setQuoteItem(dupQuoteItem);
								newAttachments.add(newAttachment);
						}
						dupQuoteItem.setAttachments(newAttachments);
					}
				
					
					if (QuoteSBConstant.RFQ_STATUS_AQ.equals(origQuoteItem.getStatus())) {
						dupQuoteItem.setStatus(QuoteSBConstant.RFQ_STATUS_AQ);
						dupQuoteItem.setStage(QuoteSBConstant.QUOTE_STAGE_FINISH);
						// refer to  the isShiftFlag in method calAfterFillin() in RfqItemVO  DamonChen@20180223,
						// AS don't need to change the sales cost type  if the quote is AQ, as need to turn back when generate duplicated AQ Quote
						if (dupQuoteItem.getSalesCostType() != null) {
							if (SalesCostType.ZINC.equals(dupQuoteItem.getSalesCostType())) {
								dupQuoteItem.setSalesCostType(SalesCostType.ZBMP);
							} else if (SalesCostType.ZIND.equals(dupQuoteItem.getSalesCostType())) {
								dupQuoteItem.setSalesCostType(SalesCostType.ZBMD);
							}
						}
						dupQuoteItem.setBmtDescCode("1");
						dupQuoteItem.setDupMatchCode("childr" + nseconds);
					} else {
					
						
						dupQuoteItem.setStatus(QuoteSBConstant.RFQ_STATUS_AQ);
						dupQuoteItem.setStage(QuoteSBConstant.QUOTE_STAGE_INVALID);
						dupQuoteItem.setBmtDescCode("0");
						dupQuoteItem.setDupMatchCode("childr"+nseconds);
					}
					if (QuoteSBConstant.RFQ_STATUS_AQ.equals(origQuoteItem.getStatus())) {
						this.applyDefaultCostIndicatorLogic(origQuoteItem, false);
					}
					origQuoteItem.setStatus(QuoteSBConstant.RFQ_STATUS_BQ);
					origQuoteItem.setStage(QuoteSBConstant.QUOTE_STAGE_PENDING);
					origQuoteItem.setBmtDescCode("99");
					origQuoteItem.setDupMatchCode("parent"+nseconds);
					setQuoteItemDesignMapping(origQuoteItem,user);
					returnStr="DUP";
				}else{
					//need to refresh price ,as be clean on RfqItemVo.calAfterFillin function,by DamonChen@20180420
					if (QuoteSBConstant.RFQ_STATUS_AQ.equals(origQuoteItem.getStatus())) {
						this.applyDefaultCostIndicatorLogic(origQuoteItem, false);
					}
					//need to reset BQ and Panding ,as they are from rfqItem 
					origQuoteItem.setStatus(QuoteSBConstant.RFQ_STATUS_BQ);
					origQuoteItem.setStage(QuoteSBConstant.QUOTE_STAGE_PENDING);
					origQuoteItem.setBmtDescCode("99");
					setQuoteItemDesignMapping(origQuoteItem,user);
					returnStr="BMTFLAGCHECK-NO-DUP";
				}
			}
			
			return returnStr;
		} catch (Exception e) {
			LOG.log(Level.SEVERE,"Error occured when copying "+origQuoteItem+" to "+dupQuoteItem+", User "+user+", Reason to failed : "+MessageFormatorUtil.getParameterizedStringFromException(e),e);
			throw new WebQuoteException("","Error occured when copying "+origQuoteItem+" to "+dupQuoteItem+", User "+user.getEmployeeId(),MessageFormatorUtil.getParameterizedStringFromException(e));
		}
		
	}

	/**   
	* @Description: TODO
	* @author 042659
	* @param rfqItem      
	* @return void    
	* @throws  
	*/  
	public boolean isDuplicatedFromDB(RfqItemVO rfqItem) {
		// TODO Auto-generated method stub

		List<QuoteItem> searchResult = extractQuoteItemByRfqItem(rfqItem);
		boolean isHas = false;
		if (searchResult.size() > 0) {
			//StringBuilder sb = new StringBuilder();
			StringBuilder dateSb = new StringBuilder();
			StringBuilder sb = new StringBuilder(""); 
			sb.append("<div style='width:100%'>");
			dateSb.append("<div style='width:100%'>");
			
			
			for (QuoteItem item : searchResult) {
				if(org.apache.cxf.common.util.StringUtils.isEmpty(rfqItem.getEndCustomerNumber())){
					
					if(item.getEndCustomer() !=null && !org.apache.cxf.common.util.StringUtils.isEmpty(item.getEndCustomer().getCustomerNumber())){
						
						continue;
					}
					
				}
				if (!StringUtils.isEmpty(rfqItem.getTargetResale())) {

					if (item.getQuotedPrice() !=null && item.getQuotedPrice().compareTo(Double.parseDouble(rfqItem.getTargetResale())) > 0) {
						continue;
					}

					if (item.getSalesCost() !=null && item.getSalesCost().compareTo(new BigDecimal(rfqItem.getTargetResale())) > 0) {
						continue;
					}
				} else {
					if (item.getQuotedPrice()== null ||item.getQuotedPrice() <= 0) {
						if (item.getSalesCost() == null ||item.getSalesCost().compareTo(BigDecimal.ZERO) <= 0) {
							continue;
						}
					}

					
				}
				isHas = true;
				sb.append("<div>").append(item.getQuoteNumber()).append("</div>");
				dateSb.append("<div>").append(DateUtils.getFormatDate(item.getPoExpiryDate(),"dd/MM/yyyy")).append("</div>");

			}
			
			sb.append("</div>");
			dateSb.append("</div>");
			if (!org.apache.commons.lang.StringUtils.isBlank(sb.toString())) {
				rfqItem.setMatchedQuoteNumber(sb.toString());
				
			}
			if (!org.apache.commons.lang.StringUtils.isBlank(dateSb.toString())) {
				rfqItem.setDupQuoteItemPoExpiryDateStr(dateSb.toString());
			}
		}

		return isHas;

	}

	/**   
	* @Description: TODO
	* @author 042659
	* @param rfqItem
	* @return      
	* @return List<QuoteItem>    
	* @throws  
	*/  
	private List<QuoteItem> extractQuoteItemByRfqItem(RfqItemVO rfqItem) {
		// TODO Auto-generated method stub
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<QuoteItem> c = cb.createQuery(QuoteItem.class);

		Root<QuoteItem> quoteItem = c.from(QuoteItem.class);
		c.distinct(true);
		Join<QuoteItem, Quote> quote = quoteItem.join("quote");
		Join<Quote, BizUnit> bizUnit = quote.join("bizUnit");
		Join<QuoteItem, Manufacturer> manufacturer = quoteItem.join("requestedMfr");
		Join<Quote, Customer> soldToCustomer = quoteItem.join("soldToCustomer");
		Join<QuoteItem, Customer> endCustomer = quoteItem.join("endCustomer",JoinType.LEFT);
		List<Predicate> criterias = new ArrayList<Predicate>();
		if (rfqItem.getRfqHeaderVO() != null && !StringUtils.isEmpty(rfqItem.getRfqHeaderVO().getBizUnit())) {

			Predicate predicate = cb.equal(cb.upper(bizUnit.<String>get("name")), rfqItem.getRfqHeaderVO().getBizUnit().toUpperCase().trim());
			criterias.add(predicate);

		}
		if (!StringUtils.isEmpty(rfqItem.getMfr())) {
			Predicate predicate = cb.equal(cb.upper(manufacturer.<String>get("name")), (rfqItem.getMfr()).toUpperCase().trim());
			criterias.add(predicate);
		}

		if (!StringUtils.isEmpty(rfqItem.getRequiredPartNumber())) {
			Predicate predicate = cb.equal(cb.upper(quoteItem.<String>get("quotedPartNumber")), rfqItem.getRequiredPartNumber().toUpperCase());
			criterias.add(predicate);
		}

		if (!StringUtils.isEmpty(rfqItem.getSoldToCustomerNumber())) {
			Predicate predicate = cb.equal(cb.upper(soldToCustomer.<String>get("customerNumber")), rfqItem.getSoldToCustomerNumber().toUpperCase());
			criterias.add(predicate);

		}

		if (!StringUtils.isEmpty(rfqItem.getEndCustomerNumber())) {
			Predicate predicate = cb.equal(cb.upper(endCustomer.<String>get("customerNumber")), rfqItem.getEndCustomerNumber().toUpperCase());
			criterias.add(predicate);
		}

		criterias.add(cb.equal(cb.upper(quoteItem.<String>get("stage")), QuoteSBConstant.QUOTE_STAGE_FINISH));

		Calendar poExpiryDatePlus14days = Calendar.getInstance();

		poExpiryDatePlus14days.add(Calendar.YEAR, 0);
		poExpiryDatePlus14days.add(Calendar.DAY_OF_MONTH, 14);
		poExpiryDatePlus14days.set(Calendar.HOUR_OF_DAY, 0);
		poExpiryDatePlus14days.set(Calendar.MINUTE, 0);
		poExpiryDatePlus14days.set(Calendar.SECOND, 0);
		poExpiryDatePlus14days.set(Calendar.MILLISECOND, 0);
		criterias.add(cb.greaterThanOrEqualTo(quoteItem.<Date>get("poExpiryDate"), poExpiryDatePlus14days.getTime()));
		c.where(cb.and(criterias.toArray(new Predicate[0])));
		TypedQuery<QuoteItem> q = em.createQuery(c);

		List<QuoteItem> searchResult = q.getResultList();
		return searchResult;
	}

	public int getRfqDraftCount(User user, BizUnit bizUnit)
	{
		Quote quote = this.getProgDraftRfq(user, bizUnit);
		if(quote != null){
			return quote.getQuoteItems().size();
		}
		return 0;
	}

	
	
	public void persistQuoteItem(QuoteItem quoteItem) {
		em.persist(quoteItem);
	}

	/**   
	* @Description: TODO
	* @author 042659
	* @param quoteItems      
	* @return void    
	* @throws  
	*/  
	public void postProcessAfterRfqSubmitFinish(List<QuoteItem> quoteItems) {
		for (QuoteItem quoteItem : quoteItems) {
			if(QuoteSBConstant.QUOTE_STAGE_FINISH.equalsIgnoreCase(quoteItem.getStage()) &&
						QuoteSBConstant.RFQ_STATUS_AQ.equalsIgnoreCase(quoteItem.getStatus())){
			quoteItem.postProcessAfterFinish();
			}
		}
		
	}
	
	public void postProcessAfterRfqSubmitFinish(QuoteItem quoteItem) {
		if (QuoteSBConstant.QUOTE_STAGE_FINISH.equalsIgnoreCase(quoteItem.getStage())
				&& QuoteSBConstant.RFQ_STATUS_AQ.equalsIgnoreCase(quoteItem.getStatus())) {
			quoteItem.postProcessAfterFinish();
		}
	}

	/**   
	* @Description: TODO
	* @author 042659
	* @param rfqItem
	* @param quoteItem      
	* @return void    
	* @throws  
	*/  
	public void fillPriceInfoToQuoteItem(RfqItemVO rfqItem, QuoteItem quoteItem) {
		// TODO Auto-generated method stub
		rfqItem.fillPriceInfoToQuoteItem(quoteItem);
		
	}
	

	public int[] calPricerNumber(QuoteItem quoteItem) {
		if (quoteItem.getQuotedPartNumber() == null) {
			return new int[]{0,0,0};
		}
		PricerInfo pricerInfo = quoteItem.createPricerInfo();
		Material material = materialSB.findExactMaterialByMfrPartNumber(quoteItem.getRequestedMfr().getName(), 
        		quoteItem.getQuotedPartNumber(), quoteItem.getQuote().getBizUnit());
		if (material != null) {
			return material.calPricerNumber(pricerInfo);
		} 
		return new int[]{0,0,0};
	}
	
	public void patch() {
		String ids= "10144779189,10294591914,10297635763,10297635765,10297635852,10297635854,10297635826,10297635756,10297635827,10297635820,10297635832,10297635787,10297635857,10297635858,10297635795,10297635789,10297635751,10297610700,10297635807,10297635865,10297636813,10297679908,10297635866,10297635834,10297635768,10297635764,10297635825,10297776159,10297806600,10297834726,10297834737,10297834736,10297835553,10297668179,10297772770,10297772771,10297772767,10297772769,10297855136,10297773879,10297806581,10297835017,10297801030,10297806593,10297806599,10297834234,10297801028,10297801029,10297635754,10297635753,10297635801,10297635822,10297635862,10297635861,10297635809,10298199978,10297801618,10298300903,10297666762,10298169930,10298169927,10298169925,10298169931,10298169929,10298169932,10298169928,10298241421,10298241420,10298242935,10298244101,10298136757,10297914003,10297610769,10297806634,10298139711,10298202588,10298200306,10298202621,10298303087,10298300776,10298300777,10298300778,10297911444,10297914746,10297914743,10297914744,10298139862,10298241530,10298220438,10298220440,10298220439,10298220442,10298220443,10298220441,10298220444,10298220445,10298220446,10298242121,10298241738,10298241736,10297837201,10297637608,10297637631,10297637630,10297637633,10297637632,10297637634,10298274392,10298274391,10298274390,10297914747,10297914748,10297914745,10297914742,10297913892,10297913895,10297914006,10298241737,10298241713,10298241723,10297665403,10297666573,10297665464,10297666038,10297666026,10297807872,10298139877,10298139841,10297950261,10297950259,10297950273,10297665399,10297665400,10298095044,10298095748,10298094313,10298094452,10297834299,10297637031,10298270235,10297834935,10297834938,10297834937,10297834990,10297834989,10297666034,10297666043,10298136637,10298136638,10297775977,10298169886,10298171290,10298171289,10297989151,10298245078,10298246278,10297886322,10297886323,10298138767,10297664863,10297664862,10297912378,10297951187,10298298737,10297886399,10297664658,10297664661,10297664655,10297913979,10298274450,10298140144,10298140140,10297682683,10297728161,10297728160,10298137560,10298169985,10297837017,10297837014,10298301119,10298301115,10298095838,10298095988,10298264375,10298171391,10298298457,10298298455,10298298456,10298137061,10297981187,10297981188,10297637117,10297637116,10297637115,10297773190,10298171394,10298171393,10298171392,10297777081,10297988264,10297774879,10298137041,10298171264,10298136866,10298136865,10298273595,10298273596,10298273603,10298273592,10297777076,10297988261,10298013750,10298013749,10298013751,10298013753,10298199888,10298199873,10298199886,10298300951,10298302202,10297925643,10298273676,10298273675,10298273673,10298273690,10298273688,10298199273,10298199346,10298199347,10298199349,10298199350,10298199351,10297988262,10297806786,10298138752,10298199884,10298302180,10298169924,10298169926,10298202867,10298006616,10297887634,10297953660,10297982603,10298328275,10298169657,10298169655,10298169658,10298241546,10298328650,10298328767,10298328773,10298328766,10297672988,10297982934,10298093838,10298093837,10298138384,10297982662,10297982679,10297772120,10298242445,10297774547,10297774548,10298243597,10298244964,10298329914,10298329697,10298329696,10297636734,10297636733,10298139761,10298245463,10298245455,10298245464,10298245447,10298245454,10298272240,10298272238,10298272239,10298263989,10298300747,10298300755,10298300754,10298300756,10297834516,10297951566,10297951567,10297951569,10297951568,10297951570,10297952189,10298272241,10298329655,10298301988,10298168793,10297682215,10297728074,10297834517,10298263857,10298263858,10298263856,10297952220,10297918814,10298169674,10298245449,10298245456,10298245457,10297905465,10297905462,10297905507,10298329114,10298202302,10298202296,10298202300,10298202301,10298202349,10297919510,10297919511,10298271565,10297952216,10297952240,10297952258,10298246517,10298246519,10298246521,10298246518,10298246520,10298246522,10298273528,10298273539,10298273775,10298202303,10297953616,10297914668,10297914667,10297952243,10297952184,10298093311,10297905459,10297952215,10297979550,10297982940,10298297776,10297681728,10297681731,10297952182,10297952183,10297952218,10297954193,10297807806,10298095648,10298171686,10298171683,10297952744,10297954187,10297954202,10297954190,10297809055,10297809057,10297809056,10297667094,10298171688,10298199223,10297954196,10297988423,10297988418,10297988424,10297988415,10297988419,10297988420,10297988416,10298137847,10298243411,10297988417,10297988426,10298137848,10297834154,10297834873,10297834868,10297912817,10297915032,10298094125,10298095539,10297772940,10297911682,10297911674,10297911684,10297911678,10297911683,10297913669,10297915033,10298095542,10298139470,10298139482,10298139472,10298139471,10297672574,10297911675,10297911680,10297950849,10298095546,10297672526,10297672527,10297672544,10297774681,10297666905,10297666879,10297666896,10297666872,10297949471,10297952557,10297666867,10297666871,10297988730,10297988729,10297988728,10297988738,10297988739,10297988733,10297988736,10297988737,10297988734,10298201234,10298201233,10298273294,10298273295,10298273300,10298273296,10298271248,10298271240,10298271233,10298271214,10297666888,10298006576,10298298228,10298271223,10298271217,10298271208,10298271230,10298271222,10298271224,10298271241,10298271220,10298271236,10298271253,10298271221,10298271219,10298298241,10298298242,10298298239,10298298240,10298298439,10298298440,10298273413,10298273796,10298096044,10298096073,10298096072,10298013798,10297801084,10297923590,10297666901,10298245035,10298271213,10298271216,10298271228,10298271237,10298271231,10298271247,10298271242,10298271246,10298271210,10298271252,10298298936,10298298923,10298298922,10298298945,10298298925,10297666880,10298202592,10298096071,10298096075,10298096074,10298137416,10297801522,10297864611,10297886156,10297886158,10297672861,10297663845,10297923830,10297982088,10298273387,10298195945,10297668184,10297952048,10297983274,10298300863,10298300862,10298301375,10298301377,10298301378,10298301376,10297665616,10297952051,10297952060,10297952063,10298271745,10298271746,10298271738,10298271741,10298271737,10298271736,10298271748,10298200255,10298200256,10298200927,10297663610,10297666180,10297672573,10297672528,10297807820,10297988625,10297988786,10297988787,10297988782,10297988783,10297988785,10298326776,10298202638,10298301144,10298329314,10298272217,10298095092,10297950717,10297808034,10297988784,10297988789,10298243147,10298301147,10298241276,10298298370,10298298462,10298298463,10297680386,10297731380,10298243480,10298244446,10298301168,10298271243,10298271250,10298271255,10298271234,10298271211,10298271244,10298271215,10298271239,10298271238,10298271245,10298271249,10298271218,10298271235,10298298927,10298298942,10298298924,10298298920,10298298938,10298329889,10297953049,10297731368,10297731381,10298093687,10298093688,10298301165,10298301920,10298301921,10298245040,10298271229,10298271251,10298271212,10298271209,10298271232,10298271225,10298271226,10298271227,10298271254,10298298238,10297953052,10297953053";
		TypedQuery<QuoteItem> query = em.createQuery("select q from QuoteItem q where q.id in (" + ids + ")", QuoteItem.class);
		for (QuoteItem quoteItem : query.getResultList()) {
			quoteItem.getQuotedMfr().applyReferenceMarginLogic(quoteItem);
			System.out.println(quoteItem.getQuoteNumber());
			
		}
	}
	
	public String findRFQCurrByQuoteNumber(String quoteNumber){
		String sql = "select q from QuoteItem q where q.quoteNumber = :quoteNumber";
		Query query = em.createQuery(sql);
		query.setParameter("quoteNumber", quoteNumber);
		try {
			QuoteItem quoteItem = (QuoteItem) query.getSingleResult();
			String rfqCurr = quoteItem.getRfqCurr().name();
			return rfqCurr;
		} catch (Exception ex){
			LOG.log(Level.WARNING, "Exception in getting Quote Item rfqCurr");
			return null;
		}
	}	
}
