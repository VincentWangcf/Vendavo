package com.avnet.emasia.webquote.quote.ejb;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.eclipse.persistence.config.QueryHints;

import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.BmtFlag;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.CustomerAddress;
import com.avnet.emasia.webquote.entity.DataAccess;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.ProductGroup;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.QuoteItemDesign;
import com.avnet.emasia.webquote.entity.ReferenceMarginSetting;
import com.avnet.emasia.webquote.entity.Team;
import com.avnet.emasia.webquote.entity.User;
import static com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant.*;

import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.quote.vo.MyQuoteSearchCriteria;
import com.avnet.emasia.webquote.quote.vo.QuoteItemVo;
import com.avnet.emasia.webquote.quote.vo.QuoteVo;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilities.DBResourceBundle;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.utilities.util.QuoteUtil;
import com.avnet.webquote.quote.ejb.utility.EJBQuoteUtility;

@Stateless
@LocalBean
public class MyQuoteSearchSB {

	@PersistenceContext(unitName = "Server_Source")
	EntityManager em;

	@EJB
	QuoteNumberSB quoteNumberSB;
	
	@EJB
	SalesOrderSB salesOrderSB;
	
	@EJB
	ManufacturerSB manufacturerSB;
	
	@EJB
	UserSB userSB;

	private static final Logger LOG = Logger.getLogger(MyQuoteSearchSB.class.getName());
	
	private static final int MAX_RESULT_IN_MYQUOTE = 501;

	//NEC Pagination Changes: Asynchronous method to fetch data for next/previous page
	@Asynchronous
	public void asyncSearch(MyQuoteSearchCriteria criteria, Locale locale, int first, int pageSize,int pageNumber,
			String sortField, String sortOrder, Map<String, Object> filters,ConcurrentHashMap<Integer, List<QuoteItemVo>> map, Queue<Integer> queue, int cachePageSize, boolean responseInternalTransfer) {
		/*return search(criteria, locale, first, pageSize, pageNumber,
				sortField, sortOrder, filters, map, queue, cachePageSize, responseInternalTransfer);*/
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<QuoteItem> c = cb.createQuery(QuoteItem.class);
		
		c.distinct(true);

		Root<QuoteItem> quoteItem = c.from(QuoteItem.class);

		Join<QuoteItem, ProductGroup> productGroup = quoteItem.join("productGroup2", JoinType.LEFT);

		Join<QuoteItem, Quote> quote = quoteItem.join("quote");
		Join<Quote, User> sales = quote.join("sales", JoinType.LEFT);
		Join<Quote, Team> team = quote.join("team", JoinType.LEFT);
		Join<Quote, BizUnit> bizUnit = quote.join("bizUnit", JoinType.LEFT);
		Join<Quote, Customer> soldToCustomer = quote.join("soldToCustomer", JoinType.LEFT);
		Join<QuoteItem, Customer> endCustomer = quoteItem.join("endCustomer", JoinType.LEFT);
		Join<QuoteItem, Customer> shipToCustomer = quoteItem.join("shipToCustomer", JoinType.LEFT);
		Join<QuoteItem, Manufacturer> manufacturer = quoteItem.join("requestedMfr", JoinType.LEFT);
		Join<QuoteItem, QuoteItemDesign> itemDesign = quoteItem.join("quoteItemDesign", JoinType.LEFT);

		//c.orderBy(cb.desc(quoteItem.get("quoteNumber")));
		
		if(responseInternalTransfer){
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(quoteItem.get("submissionDate")));
			orderList.add(cb.asc(quoteItem.get("id")));

			c.orderBy(orderList);
		}else{
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(quoteItem.get("submissionDate")));
			orderList.add(cb.desc(quoteItem.get("id")));

			c.orderBy(orderList);
		}
		

		populateCriteria(c, criteria, quoteItem, productGroup, quote, sales, team, soldToCustomer, endCustomer,shipToCustomer,
				manufacturer, bizUnit, itemDesign,sortField,sortOrder,filters);

		TypedQuery<QuoteItem> q = em.createQuery(c);

		q.setFirstResult(first);
		q.setMaxResults(pageSize);

		List<QuoteItem> searchResult = q.getResultList();

		List<QuoteItemVo> vos = calculateReadyForOrder(searchResult, locale);
		calculateCopyToCSName(vos);
		populateChineseCustoemrName(vos);
		searchBmtName(vos);
		
		
		if(vos != null && vos.size() > 0){
			
			first++;
			for (QuoteItemVo quoteItemVo : vos) {
				quoteItemVo.setSeq(first++);
			}
			
			map.put(pageNumber, vos);
			queue.add(pageNumber);
			
			if(cachePageSize < map.size()){
				map.remove(queue.remove());
			}
		}
		

	}
	
	//NEC Pagination Changes: Search for lazy loading pagination
	public List<QuoteItemVo> search(MyQuoteSearchCriteria criteria, Locale locale, int first, int pageSize,
			String sortField, String sortOrder, Map<String, Object> filters, boolean responseInternalTransfer) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<QuoteItem> c = cb.createQuery(QuoteItem.class);
		c.distinct(true);

		Root<QuoteItem> quoteItem = c.from(QuoteItem.class);

		Join<QuoteItem, ProductGroup> productGroup = quoteItem.join("productGroup2", JoinType.LEFT);

		Join<QuoteItem, Quote> quote = quoteItem.join("quote");
		Join<Quote, User> sales = quote.join("sales", JoinType.LEFT);
		Join<Quote, Team> team = quote.join("team", JoinType.LEFT);
		Join<Quote, BizUnit> bizUnit = quote.join("bizUnit", JoinType.LEFT);
		Join<Quote, Customer> soldToCustomer = quote.join("soldToCustomer", JoinType.LEFT);
		Join<QuoteItem, Customer> endCustomer = quoteItem.join("endCustomer", JoinType.LEFT);
		Join<QuoteItem, Customer> shipToCustomer = quoteItem.join("shipToCustomer", JoinType.LEFT);
		Join<QuoteItem, Manufacturer> manufacturer = quoteItem.join("requestedMfr", JoinType.LEFT);
		Join<QuoteItem, QuoteItemDesign> itemDesign = quoteItem.join("quoteItemDesign", JoinType.LEFT);

		//c.orderBy(cb.desc(quoteItem.get("quoteNumber")));
		if(responseInternalTransfer){
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(quoteItem.get("submissionDate")));
			orderList.add(cb.asc(quoteItem.get("id")));

			c.orderBy(orderList);
		}else{
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(quoteItem.get("submissionDate")));
			orderList.add(cb.desc(quoteItem.get("id")));

			c.orderBy(orderList);
		}

		populateCriteria(c, criteria, quoteItem, productGroup, quote, sales, team, soldToCustomer, endCustomer,shipToCustomer,
				manufacturer, bizUnit, itemDesign,sortField,sortOrder,filters);
		//em.create
		TypedQuery<QuoteItem> q = em.createQuery(c);
		q.setFirstResult(first);
		q.setMaxResults(pageSize);
		//q.get
	
		//q.setHint(QueryHints., " /*+parallel(default)*/ ");
		List<QuoteItem> searchResult = q.getResultList();

		List<QuoteItemVo> vos = calculateReadyForOrder(searchResult, locale);
		calculateCopyToCSName(vos);
		populateChineseCustoemrName(vos);
		searchBmtName(vos);
		
		first++;
		for (QuoteItemVo quoteItemVo : vos) {
			quoteItemVo.setSeq(first++);
		}
		
		return vos;

	}

	//NEC Pagination Changes: To get count of records in database
	public int count(MyQuoteSearchCriteria criteria, Locale locale, String sortField,
			String sortOrder, Map<String, Object> filters) {

		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<Long> c = cb.createQuery(Long.class);

		Root<QuoteItem> quoteItem = c.from(QuoteItem.class);

		Join<QuoteItem, ProductGroup> productGroup = quoteItem.join("productGroup2", JoinType.LEFT);

		Join<QuoteItem, Quote> quote = quoteItem.join("quote");
		Join<Quote, User> sales = quote.join("sales", JoinType.LEFT);
		Join<Quote, Team> team = quote.join("team", JoinType.LEFT);
		Join<Quote, BizUnit> bizUnit = quote.join("bizUnit", JoinType.LEFT);
		Join<Quote, Customer> soldToCustomer = quote.join("soldToCustomer", JoinType.LEFT);
		Join<QuoteItem, Customer> endCustomer = quoteItem.join("endCustomer", JoinType.LEFT);
		Join<QuoteItem, Customer> shipToCustomer = quoteItem.join("shipToCustomer", JoinType.LEFT);
		Join<QuoteItem, Manufacturer> manufacturer = quoteItem.join("requestedMfr", JoinType.LEFT);
		Join<QuoteItem, QuoteItemDesign> itemDesign = quoteItem.join("quoteItemDesign", JoinType.LEFT);

		/*//c.orderBy(cb.desc(quoteItem.get("quoteNumber")));
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.desc(quoteItem.get("submissionDate")));
		orderList.add(cb.desc(quoteItem.get("id")));

		c.orderBy(orderList);*/

		populateCriteria(c, criteria, quoteItem, productGroup, quote, sales, team, soldToCustomer, endCustomer,shipToCustomer,
				manufacturer, bizUnit, itemDesign,sortField,sortOrder,filters);

		c.select(cb.count(quoteItem));
		//.setHint(QueryHints.HINT, " /* +parallel(default)*/ ")
		//long t = System.currentTimeMillis();
		//Get the count for the data to be fetched
		//int count = getBaseDao().findLazyDataCountCriteriaBased(updatedCountCriteria);
		//Long cnt= em.createQuery(c).getSingleResult();
		//LOG.info("em.createQuery(c).getSingleResult() take time " +(System.currentTimeMillis()-t));
		int count = em.createQuery(c).getSingleResult().intValue();
		return count;

	}
	public List<QuoteItemVo> search(MyQuoteSearchCriteria criteria,Locale locale) {
		
		List<QuoteItem> searchResult = new ArrayList<QuoteItem>();		
		
		if(criteria.getStage() != null && criteria.getStage().size() !=0){
			if(criteria.getStage().contains(QuoteSBConstant.QUOTE_STAGE_DRAFT)){
				searchResult = searchQuoteWithoutItem(criteria);
			}
		}		

		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<QuoteItem> c = cb.createQuery(QuoteItem.class);

		Root<QuoteItem> quoteItem = c.from(QuoteItem.class);

		Join<QuoteItem, ProductGroup> productGroup = quoteItem.join("productGroup2", JoinType.LEFT);

		Join<QuoteItem, Quote> quote = quoteItem.join("quote");
		Join<Quote, User> sales = quote.join("sales", JoinType.LEFT);
		Join<Quote, Team> team = quote.join("team", JoinType.LEFT);
		Join<Quote, BizUnit> bizUnit  = quote.join("bizUnit", JoinType.LEFT);
		Join<Quote, Customer> soldToCustomer = quote.join("soldToCustomer", JoinType.LEFT);
		Join<QuoteItem, Customer> endCustomer = quoteItem.join("endCustomer", JoinType.LEFT);
		Join<QuoteItem, Customer> shipToCustomer = quoteItem.join("shipToCustomer", JoinType.LEFT);
		Join<QuoteItem, Manufacturer> manufacturer = quoteItem.join("requestedMfr", JoinType.LEFT);
		Join<QuoteItem, QuoteItemDesign> itemDesign = quoteItem.join("quoteItemDesign", JoinType.LEFT);

		c.orderBy(cb.desc(quoteItem.get("quoteNumber")));
		
		populateCriteria(c, criteria, quoteItem, productGroup, quote, sales, team, soldToCustomer, endCustomer, shipToCustomer, manufacturer, bizUnit,itemDesign,null,null,criteria.getFiltersMap()!= null?criteria.getFiltersMap():null);
		
		TypedQuery<QuoteItem> q = em.createQuery(c);
		
		//only page search need set max result. offline search need to search all result
		if(null!=criteria.getAction() && (criteria.getAction().equalsIgnoreCase(QuoteSBConstant.MYQUOTE_SEARCH_ACTION_OFFLINE)||criteria.getAction().equalsIgnoreCase(QuoteSBConstant.RESPONSEIT_DOWNLOAD_SEARCH_ACTION))) {
			q.setFirstResult(criteria.getStart());
			q.setMaxResults(criteria.getEnd());
		}else {
			q.setFirstResult(0);
			q.setMaxResults(MAX_RESULT_IN_MYQUOTE);
		}
		
		List<QuoteItem> searchResult2 = q.getResultList();
		
		searchResult.addAll(searchResult2);
		
		Collections.sort(searchResult, new Comparator<QuoteItem>(){
			public int compare(QuoteItem arg0, QuoteItem arg1) {
				int i = 0;
				if(null!=arg1.getSubmissionDate() &&  null!=arg0.getSubmissionDate()) {
					i =  arg1.getSubmissionDate().compareTo(arg0.getSubmissionDate());
				}
				
				if(i !=0 ){
					return i;
				}else{
					return (int)(arg1.getId() - arg0.getId());
				}
			}
		});
		
		List<QuoteItemVo> vos = calculateReadyForOrder(searchResult,locale);
		calculateCopyToCSName(vos);		
		populateChineseCustoemrName(vos);	
		searchBmtName(vos);
		//Add to solve Send Off Line Report java.lang.OutOfMemoryError: Java heap space
	   	em.clear();
		return vos;		
	}
		
	private void searchBmtName(List<QuoteItemVo> vos) {
		for(QuoteItemVo vo : vos){
			if(null!=vo.getQuoteItem()) {
				String bmtEmployeeId = vo.getQuoteItem().getLastUpdatedBmt();
				if(isNotEmpty(bmtEmployeeId)){
					User bmtUser = userSB.findByEmployeeIdLazily(bmtEmployeeId);
					if (null!=bmtUser)
						vo.setLastUpdateBmtName(bmtUser.getName());
				}
			}
		}
		
	}

	public Long searchCount(MyQuoteSearchCriteria criteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<Long> c = cb.createQuery(Long.class);

		Root<QuoteItem> quoteItem = c.from(QuoteItem.class);
		
		Join<QuoteItem, ProductGroup> productGroup = quoteItem.join("productGroup2", JoinType.LEFT);

		Join<QuoteItem, Quote> quote = quoteItem.join("quote");
		Join<Quote, User> sales = quote.join("sales", JoinType.LEFT);
		Join<Quote, Team> team = quote.join("team", JoinType.LEFT);
		Join<Quote, BizUnit> bizUnit  = quote.join("bizUnit", JoinType.LEFT);
		Join<Quote, Customer> soldToCustomer = quote.join("soldToCustomer", JoinType.LEFT);
		Join<QuoteItem, Customer> endCustomer = quoteItem.join("endCustomer", JoinType.LEFT);
		Join<QuoteItem, Customer> shipToCustomer = quoteItem.join("shipToCustomer", JoinType.LEFT);
		Join<QuoteItem, Manufacturer> manufacturer = quoteItem.join("requestedMfr", JoinType.LEFT);
		Join<QuoteItem, QuoteItemDesign> itemDesign = quoteItem.join("quoteItemDesign", JoinType.LEFT);
		
		populateCriteria(c, criteria, quoteItem, productGroup, quote, sales, team, soldToCustomer, endCustomer, shipToCustomer,manufacturer, bizUnit,itemDesign,null,null,criteria.getFiltersMap()!= null?criteria.getFiltersMap():null);
		
		c.select(cb.count(quoteItem));
		
		TypedQuery<Long> q = em.createQuery(c);
		
		Long count = q.getSingleResult();

		LOG.info("size:" + count);

		return count;
	}

	public List<QuoteVo> formSearch(MyQuoteSearchCriteria criteria) {
		
		//LOG.info(criteria.toString());
		
		List<QuoteItem> searchResult = new ArrayList<QuoteItem>();		
			
		if(criteria.getStage() != null && criteria.getStage().size() !=0){
			if(criteria.getStage().contains(QuoteSBConstant.QUOTE_STAGE_DRAFT)){
				searchResult = searchQuoteWithoutItem(criteria);
			}
		}		

		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<QuoteItem> c = cb.createQuery(QuoteItem.class);
		
		Root<QuoteItem> quoteItem = c.from(QuoteItem.class);
			
		Join<QuoteItem, ProductGroup> productGroup = quoteItem.join("productGroup2", JoinType.LEFT);
	
		Join<QuoteItem, Quote> quote = quoteItem.join("quote");
		Join<Quote, User> sales = quote.join("sales", JoinType.LEFT);
		Join<Quote, Team> team = quote.join("team", JoinType.LEFT);
		Join<Quote, BizUnit> bizUnit  = quote.join("bizUnit", JoinType.LEFT);
		Join<Quote, Customer> soldToCustomer = quote.join("soldToCustomer", JoinType.LEFT);
		Join<QuoteItem, Customer> endCustomer = quoteItem.join("endCustomer", JoinType.LEFT);

		Join<QuoteItem, Manufacturer> manufacturer = quoteItem.join("requestedMfr", JoinType.LEFT);
		Join<QuoteItem, QuoteItemDesign> itemDesign = quoteItem.join("quoteItemDesign", JoinType.LEFT);
		

		populateCriteria(c, criteria, quoteItem,  productGroup, quote, sales, team, soldToCustomer, endCustomer,null, manufacturer, bizUnit,itemDesign,null,null,null);
		
		c.orderBy(cb.asc(quote.<String>get("id")));
		
		TypedQuery<QuoteItem> q = em.createQuery(c);
		
		q.setFirstResult(0);
		q.setMaxResults(MAX_RESULT_IN_MYQUOTE);
		
		long fromDate = System.currentTimeMillis();
		List<QuoteItem> items = q.getResultList();
		long toDate = System.currentTimeMillis();
		
		long diff = toDate - fromDate ;

		long days = diff / (1000 * 60 * 60 * 24);
		long hour=diff/(1000*60*60);

		long m=(diff-hour*1000*60*60)/(60*1000);

		long s=(diff-hour*1000*60*60-m*60*1000)/1000;
		
		LOG.info("fromDate ==>> "+ fromDate);
		LOG.info("toDate ==>> "+ toDate);
		LOG.info("detail connection sql query difference  ==>>"+days+" days,"+ hour +" hour " + m+" minutes "+ s +" seconds ");
		
		fromDate = System.currentTimeMillis();
		
		searchResult.addAll(items);
		
		List<QuoteVo> results = new ArrayList<QuoteVo>();
		
		for(QuoteItem item : searchResult){
			long formId = item.getQuote().getId();
			boolean found = false;
			for(QuoteVo vo : results){
				if(vo.getQuote().getId() == formId ){
					if(item.getStage()!= null && item.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)){
						vo.setCompleted(vo.getCompleted() + 1);
					}
					vo.setTotal(vo.getTotal() + 1);
					found = true;
					break;
				}
			}
			
			if(found == false){
				QuoteVo vo = new QuoteVo(item.getQuote());
				if(item.getId() == 0){
					vo.setCompleted(0L);
					vo.setTotal(0L);
				}else{
					if(item.getStage()!= null && item.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)){
						vo.setCompleted(1L);
					}else{
						vo.setCompleted(0L);
					}
					vo.setTotal(1L);
				}
				results.add(vo);
			}
		}
		
		//modified by Lenon,Yang(043044) 2016.03.24
		//calculateAttachCountForQuote(results, criteria);
		if(results.size() > 0) {
			for(QuoteVo qvo : results) {
				Quote quoteTemp = qvo.getQuote();
				if(quoteTemp != null && quoteTemp.getFormAttachmentFlag() != null && quoteTemp.getFormAttachmentFlag()) {
					qvo.setAttachCount(1L);
				}
			}
		}
		
		 toDate = System.currentTimeMillis();
		
		 diff = toDate - fromDate ;

		 days = diff / (1000 * 60 * 60 * 24);
		 hour=diff/(1000*60*60);

		 m=(diff-hour*1000*60*60)/(60*1000);

		 s=(diff-hour*1000*60*60-m*60*1000)/1000;
		
		 LOG.info("fromDate ==>> "+ fromDate);
		 LOG.info("toDate ==>> "+ toDate);
		 LOG.info("detail local construct difference  ==>>"+days+" days,"+ hour +" hour " + m+" minutes "+ s +" seconds ");
		
		return results;
	}	
	
	//andy modify 2.2 
	private void populateCriteria(CriteriaQuery c, MyQuoteSearchCriteria criteria, Root<QuoteItem> quoteItem, 
			Join<QuoteItem, ProductGroup> productGroup,
			Join<QuoteItem, Quote> quote, Join<Quote, User> sales, Join<Quote, Team> team, Join<Quote, Customer> soldToCustomer,
			Join<QuoteItem, Customer> endCustomer, 
			Join<QuoteItem, Customer> shipToCustomer, Join<QuoteItem, Manufacturer> manufacturer, Join<Quote, BizUnit> bizUnit,
			Join<QuoteItem, QuoteItemDesign> itemDesign,String sortField, String sortOrder, Map<String, Object> filters) {

		CriteriaBuilder cb = em.getCriteriaBuilder();

		List<Predicate> criterias = new ArrayList<Predicate>();
		
		Expression<String> startM = cb.literal(" ");
		//Sold To Party(Quote Data Maintenance)
		if (criteria.getSoleToCustomerNameLst() != null && criteria.getSoleToCustomerNameLst().size() > 0) {
			List<Predicate> predicates = new ArrayList<Predicate>();

			for (String sldToCstrNam : criteria.getSoleToCustomerNameLst()) {
				Predicate predicate = cb.like(cb.concat(cb.concat(cb.concat(cb.concat(soldToCustomer.<String>get("customerName1"),startM), cb.concat(soldToCustomer.<String>get("customerName2"),startM)),cb.concat(soldToCustomer.<String>get("customerName3"),startM)),soldToCustomer.<String>get("customerName4")), "%" + sldToCstrNam + "%" );
				predicates.add(predicate);
			}
			
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));
		}
		
		if(criteria.getQuoteId() != null && criteria.getQuoteId().size() != 0){
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			for(Long quoteId : criteria.getQuoteId()){
				Predicate predicate = cb.equal(quote.<Long>get("id"), quoteId);
				predicates.add(predicate);
			}
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));
			//criteria.setQuoteId(null);
		}

		//dLinkFlag
		if (criteria.isDLinkFlag()!=null){
			if (criteria.isDLinkFlag()) {
				Predicate predicate = cb.equal(quote.<Boolean>get("dLinkFlag"), true);
				criterias.add(predicate);
			}
			else {
				Predicate predicate = cb.equal(quote.<Boolean>get("dLinkFlag"), false);
				criterias.add(predicate);
			}
		}
/*		if (org.apache.commons.lang.StringUtils.equalsIgnoreCase("Yes", selectedSalesCostPart)) {
			Predicate predicate = cb.equal(quoteItem.<Boolean>get("salesCostFlag"), true);
			criterias.add(predicate);
		}
*/	
		
		if(criteria.getFormNumber() != null && criteria.getFormNumber().size() != 0){
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			for(String formNumber : criteria.getFormNumber()){
				Predicate predicate = cb.like(cb.upper(quote.<String>get("formNumber")), "%" + formNumber.toUpperCase() + "%" );
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
		
		if (criteria.getMfr() != null && criteria.getMfr().size() !=0) {
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			for(String mfr : criteria.getMfr()){
				Predicate predicate = cb.like(cb.upper(manufacturer.<String>get("name")), "%" + mfr.toUpperCase() + "%" );
				predicates.add(predicate);
			}
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));
			
		}				
		
		if(criteria.getQuotedPartNumber() != null && criteria.getQuotedPartNumber().size() != 0){
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			for(String quotedPartNumber : criteria.getQuotedPartNumber()){
				Predicate predicate = cb.like(cb.upper(quoteItem.<String>get("quotedPartNumber")), "%" + quotedPartNumber.toUpperCase() + "%" );
				//Predicate predicate = cb.like(cb.upper(quotedMaterial.<String>get("fullMfrPartNumber")), "%" + quotedPartNumber.toUpperCase() + "%" );
				predicates.add(predicate);
					
			}
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));			

		}
		
		//added for Datamaintenance page by DamonChen
		if(criteria.getFirstRFQCodeLst() != null && criteria.getFirstRFQCodeLst().size() != 0){
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			for(String firstRFQcode : criteria.getFirstRFQCodeLst()){
				Predicate predicate = cb.like(cb.upper(quoteItem.<String>get("firstRfqCode")), "%" + firstRFQcode.toUpperCase() + "%" );
				//Predicate predicate = cb.like(cb.upper(quotedMaterial.<String>get("fullMfrPartNumber")), "%" + quotedPartNumber.toUpperCase() + "%" );
				predicates.add(predicate);
					
			}
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));			

		}
		
		
		if(criteria.getRequestedPartNumber() != null && criteria.getRequestedPartNumber().size() != 0){
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			for(String quotedPartNumber : criteria.getRequestedPartNumber()){
				Predicate predicate = cb.like(cb.upper(quoteItem.<String>get("requestedPartNumber")), "%" + quotedPartNumber.toUpperCase() + "%" );
				predicates.add(predicate);
					
			}
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));			

		}		
		if(criteria.getTeam() != null && criteria.getTeam().size() != 0){
			List<Predicate> predicates = new ArrayList<Predicate>();

			for(String teamName : criteria.getTeam()){
				Predicate predicate = cb.like(cb.upper(team.<String>get("name")), "%" + teamName + "%" );
				predicates.add(predicate);
			}
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));
		}
		if(criteria.getCustomerName() != null && criteria.getCustomerName().size() != 0){
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			for(String customerName : criteria.getCustomerName()){
				//Predicate predicate = cb.like(cb.upper(soldToCustomer.<String>get("customerName1")), "%" + customerName.toUpperCase() + "%" );
				//fix ticket INC0123390  june guo 20150323
				Predicate predicate = cb.like(cb.upper(quote.<String>get("soldToCustomerName")), "%" + customerName.toUpperCase() + "%" );
				predicates.add(predicate);
			}
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));
			
		}		
		
		if (criteria.getSalesName() != null && criteria.getSalesName().size()!=0) {
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			for(String name : criteria.getSalesName()){
				Predicate predicate = cb.like(cb.upper(sales.<String>get("name")), "%" + name.toUpperCase() + "%" );
				predicates.add(predicate);
			}
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));			
			
		}
		
		if (criteria.getYourReference() != null && criteria.getYourReference().size() !=0) {
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			for(String yourReference : criteria.getYourReference()){
				Predicate predicate = cb.like(cb.upper(quote.<String>get("yourReference")), "%" + yourReference.toUpperCase() + "%" );
				predicates.add(predicate);
			}
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));
			
		}
		
		if (criteria.getSoldToCode() != null && criteria.getSoldToCode().size() != 0) {
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			for(String soldToCode : criteria.getSoldToCode()){
				Predicate predicate = cb.like(soldToCustomer.<String>get("customerNumber"), "%" + soldToCode + "%" );
				predicates.add(predicate);
			}
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));									
		}
		
		if (criteria.getEndCustomeName() != null && criteria.getEndCustomeName().size() != 0) {

			List<Predicate> predicates = new ArrayList<Predicate>();
			
			for(String name : criteria.getEndCustomeName()){
				Predicate predicate1 = cb.like(cb.upper(endCustomer.<String>get("customerName1")), "%" + name.toUpperCase() + "%" );
				Predicate predicate2 = cb.like(cb.upper(quoteItem.<String>get("endCustomerName")), "%" + name.toUpperCase() + "%" );
				predicates.add(cb.or(predicate1, predicate2));
			}
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));					
		}
	
		if (criteria.getStatus() != null && criteria.getStatus().size() != 0) {
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			for(String status : criteria.getStatus()){
				Predicate predicate = cb.like(quoteItem.<String>get("status"), status );
				predicates.add(predicate);
			}
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));			
		}else{
			Predicate predicate = cb.like(quoteItem.<String>get("status"), "" );
			criterias.add(predicate);
		}
		
		//for BMT Project
		if (criteria.getBmtFlag() != null && criteria.getBmtFlag().size() != 0) {
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			for(String bmtFlag : criteria.getBmtFlag()){
				Predicate predicate = cb.like(itemDesign.get("bmtFlag").<String>get("bmtFlagCode"), bmtFlag );
				predicates.add(predicate);
			}
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));			
		}
		/*else{
			Predicate predicate = cb.like(quoteItem.<String>get("status"), "" );
			criterias.add(predicate);
		}*/

		
		// added for sales cost project by DamonChen@20170914
		if ((!StringUtils.isEmpty(criteria.getSearchPage())) && QuoteSBConstant.RESPONSEINTERNALTRANSFER_PAGE.equals(criteria.getSearchPage())) {

			if (criteria.isSalsCostAccessFlag() == null) {
				if (criteria.getSelectedSalesCostPart() != null) {
					String selectedSalesCostPart = criteria.getSelectedSalesCostPart();
					if (org.apache.commons.lang.StringUtils.isNotBlank(selectedSalesCostPart)) {
						if (org.apache.commons.lang.StringUtils.equalsIgnoreCase("Yes", selectedSalesCostPart)) {
							Predicate predicate = cb.equal(quoteItem.<Boolean>get("salesCostFlag"), true);
							criterias.add(predicate);
						}
						if (org.apache.commons.lang.StringUtils.equalsIgnoreCase("No", selectedSalesCostPart)) {
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

				if (criteria.getSelectedSalesCostPart() != null) {
					String selectedSalesCostPart = criteria.getSelectedSalesCostPart();
					if (org.apache.commons.lang.StringUtils.isNotBlank(selectedSalesCostPart)) {
						if (org.apache.commons.lang.StringUtils.equalsIgnoreCase("No", selectedSalesCostPart)) {
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

				if (criteria.getSelectedSalesCostPart() != null) {
					String selectedSalesCostPart = criteria.getSelectedSalesCostPart();
					if (org.apache.commons.lang.StringUtils.isNotBlank(selectedSalesCostPart)) {
						if (org.apache.commons.lang.StringUtils.equalsIgnoreCase("Yes", selectedSalesCostPart)) {

							Predicate predicate = cb.equal(quoteItem.<String>get("quoteNumber"), "DM");
							criterias.add(predicate);
						} else {
							List<Predicate> predicates = new ArrayList<Predicate>();
							Predicate predicate = cb.equal(quoteItem.<Boolean>get("salesCostFlag"), false);
							predicates.add(predicate);
							Predicate predicate2 = cb.isNull(quoteItem.<Boolean>get("salesCostFlag"));
							predicates.add(predicate2);
							criterias.add(cb.or(predicates.toArray(new Predicate[0])));
						}

					}

				} else {
					List<Predicate> predicates = new ArrayList<Predicate>();
					Predicate predicate = cb.equal(quoteItem.<Boolean>get("salesCostFlag"), false);
					predicates.add(predicate);
					Predicate predicate2 = cb.isNull(quoteItem.<Boolean>get("salesCostFlag"));
					predicates.add(predicate2);
					criterias.add(cb.or(predicates.toArray(new Predicate[0])));
				}
			}
		} else {
			if (criteria.getSelectedSalesCostPart() != null) {
				String selectedSalesCostPart = criteria.getSelectedSalesCostPart();
				if (org.apache.commons.lang.StringUtils.isNotBlank(selectedSalesCostPart)) {
					if (org.apache.commons.lang.StringUtils.equalsIgnoreCase("Yes", selectedSalesCostPart)) {
						Predicate predicate = cb.equal(quoteItem.<Boolean>get("salesCostFlag"), true);
						criterias.add(predicate);
					}
					if (org.apache.commons.lang.StringUtils.equalsIgnoreCase("No", selectedSalesCostPart)) {
						List<Predicate> predicates = new ArrayList<Predicate>();
						Predicate predicate = cb.equal(quoteItem.<Boolean>get("salesCostFlag"), false);
						predicates.add(predicate);
						Predicate predicate2 = cb.isNull(quoteItem.<Boolean>get("salesCostFlag"));
						predicates.add(predicate2);
						criterias.add(cb.or(predicates.toArray(new Predicate[0])));
					}

				}
			}
		}

		if (criteria.getCustomerGroupIdList() != null && criteria.getCustomerGroupIdList().size() != 0) {

			List<Predicate> predicates = new ArrayList<Predicate>();

			for (String customerGroupId : criteria.getCustomerGroupIdList()) {
				Predicate predicate = cb.like(cb.upper(quoteItem.<String>get("customerGroupId")),"%" + customerGroupId + "%");
				predicates.add(predicate);
			}
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));

		}
	
		//added for sales cost project end 
		if (criteria.getStage() != null && criteria.getStage().size() != 0) {
			
			List<String> stages = new ArrayList<String>();

			for(String stage : criteria.getStage()){
				stages.add(stage);
			}
			
			Predicate predicate1 = null;
			Predicate predicate2 = null;
			Predicate predicate3 = null;
			
			boolean draft = false;
			if(stages.contains(QuoteSBConstant.QUOTE_STAGE_DRAFT)){
				if(criteria.getLastUpdatedBy()!= null && criteria.getLastUpdatedBy().size() !=0){
					draft = true;

					Predicate predicate = cb.like(quoteItem.<String>get("stage"), QuoteSBConstant.QUOTE_STAGE_DRAFT);
					//andy modify 2.2
					CriteriaBuilder.In<String> in = cb.in(quoteItem.<String>get("lastUpdatedBy"));
					
					for(String employeeId : criteria.getLastUpdatedBy()){
						in.value(employeeId);
					}
					
					predicate1 = cb.and(predicate, in);
					
					stages.remove(QuoteSBConstant.QUOTE_STAGE_DRAFT);
				}
			}
			
			List<Predicate> predicates2 = new ArrayList<Predicate>();
			boolean others = false;
			for(String stage : stages){
				Predicate predicate = cb.like(quoteItem.<String>get("stage"), stage );
				predicates2.add(predicate);
				others = true;
			}
			
			if(others == true){
				predicate2 = cb.or(predicates2.toArray(new Predicate[0]));
			}
			
			if(draft && others){
				predicate3 = cb.or(predicate1, predicate2);
				criterias.add(predicate3);
			}else{
				if(draft){
					criterias.add(predicate1);
				}
				if(others){
					criterias.add(predicate2);
				}
			}

		}else{
			Predicate predicate = cb.like(quoteItem.<String>get("stage"), "" );
			criterias.add(predicate);
		}
		

		if (criteria.getUploadDateFrom() != null ) {
			criterias.add(cb.greaterThanOrEqualTo(quoteItem.<Date>get("submissionDate"), criteria.getUploadDateFrom()));
		}
		
		if (criteria.getUploadDateTo() != null ) {
			criterias.add(cb.lessThanOrEqualTo(quoteItem.<Date>get("submissionDate"), criteria.getUploadDateTo()));
		}

		if (criteria.getSentOutDateFrom() != null ) {
			criterias.add(cb.greaterThanOrEqualTo(quoteItem.<Date>get("sentOutTime"), criteria.getSentOutDateFrom()));
		}
		
		if (criteria.getSentOutDateTo() != null ) {
			criterias.add(cb.lessThanOrEqualTo(quoteItem.<Date>get("sentOutTime"), criteria.getSentOutDateTo()));
		}
		
		//to fix incident INC0076866  June Guo 2015/01/07 to use pagePoExpiryDate information
		if (criteria.getPagePoExpiryDateFrom() != null ) {
			Predicate p0 = cb.greaterThanOrEqualTo(quoteItem.<Date>get("poExpiryDate"), criteria.getPagePoExpiryDateFrom());
			criterias.add(p0);

		}
		//to fix incident INC0076866  June Guo 2015/01/07 to use pagePoExpiryDate information
		if (criteria.getPagePoExpiryDateTo() != null ) {
			Predicate p0 = cb.lessThanOrEqualTo(quoteItem.<Date>get("poExpiryDate"), criteria.getPagePoExpiryDateTo());
			criterias.add(p0);
		}
		
		//quoteEpxpiryDate is a kind of security control. it does not apply to a quote which have null value in quoteExpiryDate
		if (criteria.getQuoteExpiryDateFrom() != null ) {
			/*Predicate p0 = cb.greaterThanOrEqualTo(quoteItem.<Date>get("quoteExpiryDate"), criteria.getQuoteExpiryDateFrom());
			Predicate p1 = cb.isNull(quoteItem.<Date>get("quoteExpiryDate")); 
			criterias.add(cb.or(p0, p1));*/
			Predicate p0 = cb.greaterThanOrEqualTo(quoteItem.<Date>get("quoteExpiryDate"), criteria.getQuoteExpiryDateFrom());
			Predicate p1 = cb.isNull(quoteItem.<Date>get("quoteExpiryDate")); 
			//Predicate p2 = cb.equal(quoteItem.<String>get("stage"),QuoteSBConstant.QUOTE_STAGE_PENDING);
			Predicate p2 = quoteItem.<String>get("stage").in(QUOTE_STAGE_PENDING, 
					QUOTE_STAGE_DRAFT, QUOTE_STATE_PROG_DRAFT);
			criterias.add(cb.or(p0, p1, p2));
		}

		//pageQuoteEpxpiryDate is from UI. If it has value, a quote with null quoteExpiryDate should not be searched out.
		if (criteria.getPageQuoteExpiryDateFrom() != null ) {
			Predicate p0 = cb.greaterThanOrEqualTo(quoteItem.<Date>get("quoteExpiryDate"), criteria.getPageQuoteExpiryDateFrom());
			criterias.add(p0);
		}		
		
		if (criteria.getQuoteExpiryDateTo() != null ) {
			Predicate p0 = cb.lessThanOrEqualTo(quoteItem.<Date>get("quoteExpiryDate"), criteria.getQuoteExpiryDateTo());
			Predicate p1 = cb.isNull(quoteItem.<Date>get("quoteExpiryDate")); 
			criterias.add(cb.or(p0, p1));
		}
		
		if (criteria.getPageQuoteExpiryDateTo() != null ) {
			Predicate p0 = cb.lessThanOrEqualTo(quoteItem.<Date>get("quoteExpiryDate"), criteria.getPageQuoteExpiryDateTo());
			criterias.add(p0);
		}				
		if (!QuoteUtil.isEmpty(criteria.getSupplierQuote())) {
			Predicate p0 = cb.like(cb.upper(quoteItem.<String>get("vendorQuoteNumber")), "%"+criteria.getSupplierQuote().toUpperCase()+"%");
			criterias.add(p0);
		}	
		if (!QuoteUtil.isEmpty(criteria.getSupplierDebit())) {
			Predicate p0 = cb.like(cb.upper(quoteItem.<String>get("vendorDebitNumber")), "%"+criteria.getSupplierDebit().toUpperCase()+"%");
			criterias.add(p0);
		}	
		if( criteria.getDataAccesses() != null && criteria.getDataAccesses().size() != 0){
			
			List<Predicate> p0 = new ArrayList<Predicate>();
			
			p0 = EJBQuoteUtility.commonCriteriaForQuote(criteria.getDataAccesses(), quoteItem, productGroup, team, manufacturer, cb, p0);
			
			criterias.add(cb.or(p0.toArray(new Predicate[0])));
		}else{
			Predicate predicate = cb.notEqual(manufacturer.<Long>get("id"), manufacturer.<Long>get("id"));
			criterias.add(predicate);
		}
		
		if (criteria.getRestrictedSales() != null && criteria.getRestrictedSales().size()!=0) {
			
			CriteriaBuilder.In<Long> in = cb.in(sales.<Long>get("id"));
			
			for(User user : criteria.getRestrictedSales()){
				in.value(user.getId());
			}			
			criterias.add(in);						
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

		
		List<Predicate> predicate = null;
		if (filters != null && filters.size() > 0
				|| (sortOrder != null && sortOrder.length() > 0 && sortField != null && sortField.length() > 0)) {
			predicate = createFilter(c, criteria, quoteItem, productGroup, quote, sales, team, soldToCustomer,
					endCustomer,shipToCustomer, manufacturer, bizUnit, itemDesign, filters, sortOrder, sortField);
		}
		
		if (predicate != null && !predicate.isEmpty()) {
			criterias.addAll(predicate);
		}
		
		if (criterias.size() == 0) {
			
		} else if (criterias.size() == 1) {
			c.where(criterias.get(0));
		} else {
			c.where(cb.and(criterias.toArray(new Predicate[0])));
		}
	}
	
	/**
	 * Creates the filter.
	 *
	 * @param c
	 *            the c
	 * @param criteria
	 *            the criteria
	 * @param quoteItem
	 *            the quote item
	 * @param productGroup
	 *            the product group
	 * @param quote
	 *            the quote
	 * @param sales
	 *            the sales
	 * @param team
	 *            the team
	 * @param soldToCustomer
	 *            the sold to customer
	 * @param endCustomer
	 *            the end customer
	 * @param shipToCustomer 
	 * @param manufacturer
	 *            the manufacturer
	 * @param bizUnit
	 *            the biz unit
	 * @param itemDesign
	 *            the item design
	 * @param filters
	 *            the filters
	 * @param orderBy
	 *            the order by
	 * @param field
	 *            the field
	 */
	public List<Predicate> createFilter(CriteriaQuery c, MyQuoteSearchCriteria criteria, Root<QuoteItem> quoteItem,
			Join<QuoteItem, ProductGroup> productGroup, Join<QuoteItem, Quote> quote, Join<Quote, User> sales,
			Join<Quote, Team> team, Join<Quote, Customer> soldToCustomer, Join<QuoteItem, Customer> endCustomer,
			Join<QuoteItem, Customer> shipToCustomer, Join<QuoteItem, Manufacturer> manufacturer, Join<Quote, BizUnit> bizUnit,
			Join<QuoteItem, QuoteItemDesign> itemDesign, Map<String, Object> filters, String orderBy, String field) {
		CriteriaBuilder cb = em.getCriteriaBuilder();

		List<Predicate> criterias = new ArrayList<Predicate>();

		if (filters == null) {
			filters = new HashMap<>();
		}
		Set<String> keySet = filters.keySet();
		for (Iterator iterator = keySet.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			Object object = filters.get(key);
			String tableName = getTableName(key);
			String fieldName = getFieldName(key);

			Predicate predicate = null;
			switch (tableName) {
			case "quoteItem":
				if ("pendingDay".equals(fieldName)) {

					Integer days = null;
					try {
						days = Integer.parseInt((String) object);
						Calendar calendarFrom = Calendar.getInstance();

						while (days > 0) {

							calendarFrom.add(Calendar.DAY_OF_MONTH, -1);
                            if(!(calendarFrom.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendarFrom.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)){
                                  days--;
                            }
						}
						calendarFrom.set(Calendar.AM_PM, Calendar.AM);
						calendarFrom.set(Calendar.HOUR, 0);
						calendarFrom.set(Calendar.MINUTE, 0);
						calendarFrom.set(Calendar.SECOND, 0);
						Calendar calendarTo = (Calendar) calendarFrom.clone();
						calendarTo.set(Calendar.HOUR, 23);
						calendarTo.set(Calendar.MINUTE, 59);
						calendarTo.set(Calendar.SECOND, 59);

						predicate = cb.between(quoteItem.<Date>get("submissionDate"), calendarFrom.getTime(),
								calendarTo.getTime());
						criterias.add(cb.or(predicate));

					} catch (NumberFormatException ne) {
						 LOG.warning("Number formate exception"+ne.getMessage());
						 predicate = cb.equal(quoteItem.<Long>get("id"), 0);
						 criterias.add(cb.or(predicate));
					}

				} else if (getPropertyType(QuoteItem.class, fieldName).equals(Boolean.class)|| getPropertyType(QuoteItem.class, fieldName).equals(boolean.class)) {
					Boolean value = true;
					String data = ((String) object).toUpperCase().trim();
					if (data.contains("Y") || data.contains("YE") || data.contains("YES") || data.contains("1")
							|| data.contains("E") || data.contains("S")) {
						value = true;
						predicate = cb.equal(quoteItem.<Boolean>get(fieldName), value);
						criterias.add(cb.or(predicate));
					} else if (data.contains("NO") || data.contains("N") || data.contains("O") || data.contains("0")) {
						value = false;
						predicate = cb.equal(quoteItem.<Boolean>get(fieldName), value);
						criterias.add(cb.or(predicate));
					}
				} else if(getPropertyType(QuoteItem.class, fieldName).equals(Long.class)){
					try{
						Long value = Long.parseLong((String)object);
						//String data = format(value);
					    predicate = cb.like(quoteItem.<String>get(fieldName), "%" + ((String)object).trim() + "%" );
						criterias.add(cb.or(predicate));
					}
					catch(NumberFormatException e){
						 predicate = cb.equal(quoteItem.<Long>get("id"), 0);
						 criterias.add(cb.or(predicate));
					}
				}else if(getPropertyType(QuoteItem.class, fieldName).equals(Integer.class)){
					try{
						Integer value = Integer.parseInt((String)object);
						//String data = format(value);
					    predicate = cb.like(quoteItem.<String>get(fieldName), "%" + ((String)object).trim() + "%" );
						criterias.add(cb.or(predicate));
					}
					catch(NumberFormatException e){
						 predicate = cb.equal(quoteItem.<Long>get("id"), 0);
						 criterias.add(cb.or(predicate));
					}
				}else if(getPropertyType(QuoteItem.class, fieldName).equals(Double.class)){
					try{
						Double value = Double.parseDouble((String)object);
						//String data = format(value);
						String filterWord = ((String)object).trim();
						filterWord = formatDecimal(filterWord);
						if(filterWord.startsWith(".")){
							predicate = cb.like(quoteItem.<String>get(fieldName),  filterWord + "%" );
							criterias.add(cb.or(predicate));
						}else{
							predicate = cb.like(quoteItem.<String>get(fieldName), "%" + filterWord + "%" );
							criterias.add(cb.or(predicate));
						}
					    
					}
					catch(NumberFormatException e){
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
			            	LOG.log(Level.SEVERE, "Not possible to coerce [" + object + "] from class " + object.getClass() + " into a BigDecimal.");
			            	predicate = cb.equal(quoteItem.<Long>get("id"), 0);
							criterias.add(cb.or(predicate));
			            }
			        } catch (NumberFormatException e) {
			        	LOG.log(Level.SEVERE, "Not possible to coerce [" + object + "] from class " + object.getClass() + " into a BigDecimal.");
			        	predicate = cb.equal(quoteItem.<Long>get("id"), 0);
						criterias.add(cb.or(predicate));
			        } catch (ClassCastException e) {
			        	LOG.log(Level.SEVERE, "Not possible to coerce [" + object + "] from class " + object.getClass() + " into a BigDecimal.");
			        	predicate = cb.equal(quoteItem.<Long>get("id"), 0);
						criterias.add(cb.or(predicate));
			        } catch (Exception e) {
			            System.out.println("Exception caught. " + e);
			            LOG.log(Level.SEVERE, "Exception caught " + e);
			            predicate = cb.equal(quoteItem.<Long>get("id"), 0);
						criterias.add(cb.or(predicate));
			        }
				   // String data = format(value);
				    predicate = cb.like(quoteItem.<String>get(fieldName), "%" + ((String)object).trim() + "%" );
					criterias.add(cb.or(predicate));
				}else if (getPropertyType(QuoteItem.class, fieldName).equals(Date.class)) {
					Calendar calendar = Calendar.getInstance();
					calendar.set(9998, 12, 31);
					Date dateFrom = calendar.getTime();

					try {
						dateFrom = QuoteUtil.convertStringToDate((String) object, "dd/MM/yyyy");
					} catch (ParseException e) {
						// LOG.warning("Unable to parse date");
					}
					Calendar calendarFrom = Calendar.getInstance();
					calendarFrom.setTime(dateFrom);
					calendarFrom.set(Calendar.AM_PM, Calendar.AM);
					calendarFrom.set(Calendar.HOUR, 00);
					calendarFrom.set(Calendar.MINUTE, 00);
					calendarFrom.set(Calendar.SECOND, 00);
					Date dateTo = (Date) dateFrom.clone();
					Calendar calendarTo = Calendar.getInstance();
					calendarTo.setTime(dateTo);
					calendarTo.set(Calendar.HOUR, 23);
					calendarTo.set(Calendar.MINUTE, 59);
					calendarTo.set(Calendar.SECOND, 59);
					predicate = cb.between(quoteItem.<Date>get(fieldName), calendarFrom.getTime(),
							calendarTo.getTime());
					criterias.add(cb.or(predicate));

				} else {
					predicate = cb.like(cb.upper(quoteItem.<String>get(fieldName)),
							"%" + ((String) object).toUpperCase().trim() + "%");
					criterias.add(cb.or(predicate));
				}
				break;

			case "quoteItemDesign":

				if (getPropertyType(QuoteItemDesign.class, fieldName).equals(Date.class)) {

					Calendar calendar = Calendar.getInstance();
					calendar.set(9998, 12, 31);
					Date dateFrom = calendar.getTime();

					try {
						dateFrom = QuoteUtil.convertStringToDate((String) object, "dd/MM/yyyy");
					} catch (ParseException e) {
						// LOG.warning("Unable to parse date");
					}
					Calendar calendarFrom = Calendar.getInstance();
					calendarFrom.setTime(dateFrom);
					calendarFrom.set(Calendar.AM_PM, Calendar.AM);
					calendarFrom.set(Calendar.HOUR, 00);
					calendarFrom.set(Calendar.MINUTE, 00);
					calendarFrom.set(Calendar.SECOND, 00);
					Date dateTo = (Date) dateFrom.clone();
					Calendar calendarTo = Calendar.getInstance();
					calendarTo.setTime(dateTo);
					calendarTo.set(Calendar.HOUR, 23);
					calendarTo.set(Calendar.MINUTE, 59);
					calendarTo.set(Calendar.SECOND, 59);
					predicate = cb.between(itemDesign.<Date>get(fieldName), calendarFrom.getTime(),
							calendarTo.getTime());
					criterias.add(cb.or(predicate));

				} else {
					predicate = cb.like(cb.upper(itemDesign.<String>get(fieldName)),
							"%" + ((String) object).toUpperCase().trim() + "%");
					criterias.add(cb.or(predicate));
				}

				break;

			case "productGroup":
				predicate = cb.like(cb.upper(productGroup.<String>get(fieldName)),
						"%" + ((String) object).toUpperCase().trim() + "%");
				criterias.add(cb.or(predicate));
				break;

			case "productGroup2":
				predicate = cb.like(cb.upper(productGroup.<String>get(fieldName)),
						"%" + ((String) object).toUpperCase().trim() + "%");
				criterias.add(cb.or(predicate));
				break;

			case "quote":
				 if (getPropertyType(Quote.class, fieldName).equals(Boolean.class)|| getPropertyType(Quote.class, fieldName).equals(boolean.class)) {
						Boolean value = true;
						String data = ((String) object).toUpperCase().trim();
						if (data.contains("Y") || data.contains("YE") || data.contains("YES") || data.contains("1")
								|| data.contains("E") || data.contains("S")) {
							value = true;
							predicate = cb.equal(quote.<Boolean>get(fieldName), value);
							criterias.add(cb.or(predicate));
						} else if (data.contains("NO") || data.contains("N") || data.contains("O") || data.contains("0")) {
							value = false;
							predicate = cb.equal(quote.<Boolean>get(fieldName), value);
							criterias.add(cb.or(predicate));
						}
					} else {
						predicate = cb.like(cb.upper(quote.<String>get(fieldName)),
								"%" + ((String) object).toUpperCase().trim() + "%");
						criterias.add(cb.or(predicate));
					}
				
				break;
			case "sales":
				predicate = cb.like(cb.upper(sales.<String>get(fieldName)),
						"%" + ((String) object).toUpperCase().trim() + "%");
				criterias.add(cb.or(predicate));
				break;
			case "team":
				predicate = cb.like(cb.upper(team.<String>get(fieldName)),
						"%" + ((String) object).toUpperCase().trim() + "%");
				criterias.add(cb.or(predicate));
				break;

			case "soldToCustomer":

				if ("customerNumber".equals(fieldName) || "accountGroupDesc".equals(fieldName)) {
					predicate = cb.like(cb.upper(soldToCustomer.<String>get(fieldName)),
							"%" + ((String) object).toUpperCase().trim() + "%");
					criterias.add(cb.or(predicate));
				} else {
					if(!StringUtils.isEmpty((String) object)){
					criterias.add(cb.or(
							cb.like(cb.upper(soldToCustomer.<String>get("customerName1")), "%" + ((String) object).toUpperCase().trim() + "%"),
							cb.like(cb.upper(soldToCustomer.<String>get("customerName2")), "%" + ((String) object).toUpperCase().trim() + "%"),
							cb.like(cb.upper(soldToCustomer.<String>get("customerName3")), "%" + ((String) object).toUpperCase().trim() + "%"),
							cb.like(cb.upper(soldToCustomer.<String>get("customerName4")), "%" + ((String) object).toUpperCase().trim() + "%")));
					}
				}

				break;
			case "endCustomer":

				if ("customerNumber".equals(fieldName)) {
					predicate = cb.like(cb.upper(endCustomer.<String>get(fieldName)),
							"%" + ((String) object).toUpperCase().trim() + "%");
					criterias.add(cb.or(predicate));
				} else {
					if(!StringUtils.isEmpty((String) object)){
					criterias
							.add(cb.or(cb.like(cb.upper(endCustomer.<String>get("customerName1")), "%" + ((String) object).toUpperCase().trim() + "%"),
									cb.like(cb.upper(endCustomer.<String>get("customerName2")), "%" + ((String) object).toUpperCase().trim() + "%"),
									cb.like(cb.upper(endCustomer.<String>get("customerName3")), "%" + ((String) object).toUpperCase().trim() + "%"),
									cb.like(cb.upper(endCustomer.<String>get("customerName4")), "%" + ((String) object).toUpperCase().trim() + "%")));
					}
				}

				break;
			case "user":
				predicate = cb.like(cb.upper(sales.<String>get(fieldName)),
						"%" + ((String) object).toUpperCase().trim() + "%");
				criterias.add(cb.or(predicate));
				break;
			case "requestedMfr":
				predicate = cb.like(cb.upper(manufacturer.<String>get(fieldName)),
						"%" + ((String) object).toUpperCase().trim() + "%");
				criterias.add(cb.or(predicate));
				break;

			case "manufacturer":
				predicate = cb.like(cb.upper(manufacturer.<String>get(fieldName)),
						"%" + ((String) object).toUpperCase().trim() + "%");
				criterias.add(cb.or(predicate));
				break;
			case "bizUnit":
				predicate = cb.like(cb.upper(bizUnit.<String>get(fieldName)),
						"%" + ((String) object).toUpperCase().trim() + "%");
				criterias.add(cb.or(predicate));
				break;
			case "itemDesign":
				if(getPropertyType(QuoteItemDesign.class, fieldName).equals(Date.class)){
					Calendar calendar = Calendar.getInstance();
					// Pagination : changes done for WQ-977
					calendar.set(9998, 12, 31);
					// Pagination : changes done for WQ-977
					Date dateFrom = calendar.getTime();
					try {
						dateFrom = QuoteUtil.convertStringToDate((String)object,"dd/MM/yyyy");
					} catch (ParseException e) {
						LOG.warning("Unable to parse date");
					}
					Calendar calendarFrom = Calendar.getInstance();
					calendarFrom.set(Calendar.AM_PM, Calendar.AM);
					calendarFrom.setTime(dateFrom);
					calendarFrom.set(Calendar.HOUR, 00);
					calendarFrom.set(Calendar.MINUTE, 00);
					calendarFrom.set(Calendar.SECOND, 00);
					Date dateTo = (Date) dateFrom.clone();
					Calendar calendarTo = Calendar.getInstance();
					
					calendarTo.setTime(dateTo);
					calendarTo.set(Calendar.AM_PM, Calendar.AM);
					calendarTo.set(Calendar.HOUR, 23);
					calendarTo.set(Calendar.MINUTE, 59);
					calendarTo.set(Calendar.SECOND, 59);
					predicate = cb.between(itemDesign.<Date>get(fieldName), calendarFrom.getTime(), calendarTo.getTime());
					criterias.add(cb.or(predicate));
				}else if(getPropertyType(QuoteItemDesign.class, fieldName).equals(BmtFlag.class)){
					BmtFlag bmtFlag = new BmtFlag();
					bmtFlag.setBmtFlagCode((String)object);
					predicate = cb.equal(itemDesign.<String>get(fieldName), bmtFlag);
					criterias.add(cb.or(predicate));	
				}else{				
				predicate = cb.like(cb.upper(itemDesign.<String>get(fieldName)), "%" + ((String)object).toUpperCase().trim() + "%" );
				criterias.add(cb.or(predicate));
				}
				break;
			case "customer":
				predicate = cb.like(cb.upper(endCustomer.<String>get(fieldName)),
						"%" + ((String) object).toUpperCase().trim() + "%");
				criterias.add(cb.or(predicate));
				break;
			case "shipToCustomer":
				predicate = cb.like(cb.upper(shipToCustomer.<String>get(fieldName)),
						"%" + ((String) object).toUpperCase().trim() + "%");
				criterias.add(cb.or(predicate));
				break;
			default:
				break;
			}

		}

		if (orderBy != null && orderBy.length() > 0 && field != null && field.length() > 0) {
			String tableName = getTableName(field);
			String fieldName = getFieldName(field);

			switch (tableName) {
			case "quoteItem":
				if ("ASC".equals(orderBy)) {
					c.orderBy(cb.asc(quoteItem.get(fieldName)));
				} else if ("DESC".equals(orderBy)) {
					c.orderBy(cb.desc(quoteItem.get(fieldName)));
				}
				break;
			case "productGroup":
				if ("ASC".equals(orderBy)) {
					c.orderBy(cb.asc(productGroup.get(fieldName)));
				} else if ("DESC".equals(orderBy)) {
					c.orderBy(cb.desc(productGroup.get(fieldName)));
				}
				break;

			case "quoteItemDesign":
				if ("ASC".equals(orderBy)) {
					c.orderBy(cb.asc(itemDesign.get(fieldName)));
				} else if ("DESC".equals(orderBy)) {
					c.orderBy(cb.desc(itemDesign.get(fieldName)));
				}
				break;

			case "productGroup2":
				if ("ASC".equals(orderBy)) {
					c.orderBy(cb.asc(productGroup.get(fieldName)));
				} else if ("DESC".equals(orderBy)) {
					c.orderBy(cb.desc(productGroup.get(fieldName)));
				}
				break;
			case "quote":
				if ("ASC".equals(orderBy)) {
					c.orderBy(cb.asc(quote.get(fieldName)));
				} else if ("DESC".equals(orderBy)) {
					c.orderBy(cb.desc(quote.get(fieldName)));
				}
				break;
			case "sales":
				if ("ASC".equals(orderBy)) {
					c.orderBy(cb.asc(sales.get(fieldName)));
				} else if ("DESC".equals(orderBy)) {
					c.orderBy(cb.desc(sales.get(fieldName)));
				}
				break;
			case "team":
				if ("ASC".equals(orderBy)) {
					c.orderBy(cb.asc(team.get(fieldName)));
				} else if ("DESC".equals(orderBy)) {
					c.orderBy(cb.desc(team.get(fieldName)));
				}
				break;
			case "soldToCustomer":
				if ("ASC".equals(orderBy)) {

					if ("customerNumber".equals(fieldName) || "accountGroupDesc".equals(fieldName)) {
						c.orderBy(cb.asc(soldToCustomer.get(fieldName)));
					} else {
						c.orderBy(cb.asc(soldToCustomer.get("customerName1")));
						c.orderBy(cb.asc(soldToCustomer.get("customerName2")));
						c.orderBy(cb.asc(soldToCustomer.get("customerName3")));
						c.orderBy(cb.asc(soldToCustomer.get("customerName4")));
					}

				} else if ("DESC".equals(orderBy)) {

					if ("customerNumber".equals(fieldName) || "accountGroupDesc".equals(fieldName)) {
						c.orderBy(cb.desc(soldToCustomer.get(fieldName)));
					} else {
						c.orderBy(cb.desc(soldToCustomer.get("customerName1")));
						c.orderBy(cb.desc(soldToCustomer.get("customerName2")));
						c.orderBy(cb.desc(soldToCustomer.get("customerName3")));
						c.orderBy(cb.desc(soldToCustomer.get("customerName4")));
					}

				}
				break;
				
			case "shipToCustomer":
				if ("ASC".equals(orderBy)) {
					c.orderBy(cb.asc(shipToCustomer.get(fieldName)));
				} else if ("DESC".equals(orderBy)) {
					c.orderBy(cb.desc(shipToCustomer.get(fieldName)));
				}
				break;
			case "endCustomer":
				if ("ASC".equals(orderBy)) {

					if ("customerNumber".equals(fieldName)) {
						c.orderBy(cb.asc(endCustomer.get(fieldName)));
					} else {
						c.orderBy(cb.asc(endCustomer.get("customerName1")));
						c.orderBy(cb.asc(endCustomer.get("customerName2")));
						c.orderBy(cb.asc(endCustomer.get("customerName3")));
						c.orderBy(cb.asc(endCustomer.get("customerName4")));
					}

				} else if ("DESC".equals(orderBy)) {

					if ("customerNumber".equals(fieldName)) {
						c.orderBy(cb.desc(endCustomer.get(fieldName)));
					} else {
						c.orderBy(cb.desc(endCustomer.get("customerName1")));
						c.orderBy(cb.desc(endCustomer.get("customerName2")));
						c.orderBy(cb.desc(endCustomer.get("customerName3")));
						c.orderBy(cb.desc(endCustomer.get("customerName4")));
					}

				}
				break;

			case "requestedMfr":
				if ("ASC".equals(orderBy)) {
					c.orderBy(cb.asc(manufacturer.get(fieldName)));
				} else if ("DESC".equals(orderBy)) {
					c.orderBy(cb.desc(manufacturer.get(fieldName)));
				}
				break;
			case "manufacturer":
				if ("ASC".equals(orderBy)) {
					c.orderBy(cb.asc(manufacturer.get(fieldName)));
				} else if ("DESC".equals(orderBy)) {
					c.orderBy(cb.desc(manufacturer.get(fieldName)));
				}
				break;
			case "bizUnit":
				if ("ASC".equals(orderBy)) {
					c.orderBy(cb.asc(bizUnit.get(fieldName)));
				} else if ("DESC".equals(orderBy)) {
					c.orderBy(cb.desc(bizUnit.get(fieldName)));
				}
				break;
			case "itemDesign":
				if ("ASC".equals(orderBy)) {
					c.orderBy(cb.asc(itemDesign.get(fieldName)));
				} else if ("DESC".equals(orderBy)) {
					c.orderBy(cb.desc(itemDesign.get(fieldName)));
				}
				break;
			case "user":
				if ("ASC".equals(orderBy)) {
					c.orderBy(cb.asc(sales.get(fieldName)));
				} else if ("DESC".equals(orderBy)) {
					c.orderBy(cb.desc(sales.get(fieldName)));
				}
				break;
			case "customer":
				if ("ASC".equals(orderBy)) {
					c.orderBy(cb.asc(endCustomer.get(fieldName)));
				} else if ("DESC".equals(orderBy)) {
					c.orderBy(cb.desc(endCustomer.get(fieldName)));
				}
				break;
			default:
				break;
			}

		}

		return criterias;
	}
	
	/**
	 * NEC Pagination changes:
	 * This method is used to support the like search on number
	 * @param value
	 * @return
	 */
	public String format1(Number value) {
		   if (value.doubleValue() >= 1 || value.doubleValue() < 0) {
			   if(value.doubleValue()%1==0){
				   if(String.valueOf(value).contains(".")){
				   return String.valueOf(value).substring(0, String.valueOf(value).indexOf('.'));
				   }
			   }
			   return String.valueOf(value);
		   }else if(value.doubleValue()==0){
			   return String.valueOf(0);
		   }
		   return String.valueOf(value).substring(String.valueOf(value).indexOf('.'));
	}
	
	//removes leading zero in case of decimal
	public String formatDecimal(String value) {
		if(value.contains(".")){
			value = StringUtils.stripStart(value, "0");
			
		}
		   return value;
	}

	/**
	 * Gets the property type.
	 *
	 * @param cls
	 *            the cls
	 * @param propertyName
	 *            the property name
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
				// LOG.log(Level.SEVERE, "Exception in getting property type for
				// property name : "+propertyName+", Exception message:
				// "+e.getMessage(), e);
				java.lang.reflect.Field field = cls.getSuperclass().getDeclaredField(propertyName);
				retvalue = field.getType();
			} catch (Exception ex) {
				// LOG.log(Level.SEVERE, "Exception in getting property type for
				// property name : "+propertyName+", Exception message:
				// "+ex.getMessage(), ex);
			}
		}
		return retvalue;
	}

	/**
	 * Gets the table name.
	 *
	 * @param data
	 *            the data
	 * @return the table name
	 */
	private String getTableName(String data) {
		String tableName = "";
		String[] array = data.split("\\.");
		if (array.length >= 2) {
			tableName = array[array.length - 2];
		}
		return tableName;
	}

	/**
	 * Gets the field name.
	 *
	 * @param data
	 *            the data
	 * @return the field name
	 */
	private String getFieldName(String data) {
		String fieldName = "";
		String[] array = data.split("\\.");
		if (array.length >= 1) {
			fieldName = array[array.length - 1];
		} else {
			fieldName = data;
		}
		return fieldName;
	}
	public List<QuoteItemVo> searchByQuoteId(long quoteId, List<String> attachmentTypes,Locale locale) {
		Quote quote = em.find(Quote.class, quoteId);
		
		List<QuoteItem> items = quote.getQuoteItems();
		
		if(items.size() == 0){
			em.detach(quote);
			QuoteItem item = new QuoteItem();
			item.setStage(quote.getStage());
			items.add(item);
			item.setQuote(quote);
			quote.setQuoteItems(items);
		}
		
		List<QuoteItemVo> vos = calculateReadyForOrder(quote.getQuoteItems(),locale);
		return vos;
	}
	
	private void calculateAttachCountForQuote(List<QuoteVo> quoteVos, MyQuoteSearchCriteria criteria){
		
		List<Long> ids = new ArrayList<Long>();
		
		for(QuoteVo vo : quoteVos){
			ids.add(vo.getQuote().getId());
		}
		
		if(ids.size() > 0){
			Query query = em.createQuery("select q.id , COUNT(a) from Quote q join q.attachments a where q.id in :ids and a.type in :types group by q");
			query.setParameter("ids", ids);
			query.setParameter("types", criteria.getAttachmentType());
			
			List result = query.getResultList();
			
			for(Iterator i = result.iterator(); i.hasNext();) {
				Object[] values = (Object[]) i.next();
				Long quoteId = (Long)values[0];
				Long count = (Long)values[1];
				for(QuoteVo vo : quoteVos){
					if(quoteId == vo.getQuote().getId()){
						vo.setAttachCount(count);
						break;
					}
				}
			}
		}
	}
	
    public List<QuoteItemVo> calculateReadyForOrder(List<QuoteItem> quoteItems,Locale locale){
    	
    	//ResourceBundle bundle = ResourceBundle.getBundle(DBResourceBundle.class.getName(),locale);
    	
    	
    	List<QuoteItemVo> vos = new ArrayList<QuoteItemVo>(); 
    	
    	int i= 1;
    	for(QuoteItem item : quoteItems){
			
    		
			QuoteItemVo vo = new QuoteItemVo(item); 
			vo.assignFormAttachmentCode();
			vos.add(vo);
			
			vo.setSeq(i++);
			
			if(item.getStage() == null || ! item.getStage().equalsIgnoreCase(QuoteSBConstant.QUOTE_STAGE_FINISH)){
				continue;
			}
			
			boolean quoteExpired = isQuoteExpired(item);
			
			if(quoteExpired == false){
				if(item.getMultiUsageFlag() == null){
					continue;
				}
				if(item.getMultiUsageFlag()){
					vo.setReadyForOrder(true);
					vo.setReasonForNotReadyForOrder("");
				}else{
					
					long count = salesOrderSB.findCountByQuoteNumer(item.getQuoteNumber());
					
					if(count == 0){
						vo.setReadyForOrder(true);
						vo.setReasonForNotReadyForOrder("");
					}else{
						vo.setReadyForOrder(false);
						//vo.setReasonForNotReadyForOrder(bundle.getString("wq.message.quoteOrdered"));
						//Quote ordered, not multi-usage
						vo.setReasonForNotReadyForOrder("Quote ordered, not multi-usage");
					}					
				}
			}else{
				vo.setReadyForOrder(false);
				//vo.setReasonForNotReadyForOrder(bundle.getString("wq.message.quoteExpired"));
				vo.setReasonForNotReadyForOrder("Quote expired");
			}
			//fix request If the ï¿½ï¿½USEDï¿½ï¿½ flag is yes, the "valid for order" is No. and the "reason for not orderingï¿½ï¿½ is ï¿½ï¿½The quote is marked as USEDï¿½ï¿½. 
			//changed by 20150409 June guo
			if(null!=item.getUsedFlag() && true == item.getUsedFlag()) {
				vo.setReadyForOrder(false);
				vo.setReasonForNotReadyForOrder("The quote is marked as USED");
			}
		}	
    	return vos;  	
    }

    public boolean isQuoteExpired(QuoteItem quoteItem){
    	
    	boolean quoteExpired = false;
    	
		Calendar now = Calendar.getInstance();
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		
		if(quoteItem.getQuoteExpiryDate() != null) {
			
			Date quoteExpiryDate  =quoteItem.getQuoteExpiryDate();
			
			if ((quoteExpiryDate.getTime() - now.getTime().getTime()) < 0 ) {
				quoteExpired = true;
			}else {
				quoteExpired = false;
			}
			
		}else{
			
			Date sentOutTime = quoteItem.getSentOutTime();
			
			if(sentOutTime == null){
				return false;
			}
			
			if(sentOutTime.before(now.getTime())) {
				quoteExpired = true;
			}else {
				quoteExpired = false;
			}
			
		}
		return quoteExpired;   	
    }
    
    //convert employee Id to name for copyToCS field
	private void calculateCopyToCSName(List<QuoteItemVo> quoteItemVos){
		for(QuoteItemVo vo : quoteItemVos){
			String copyToCSName = vo.getQuoteItem().getQuote().getCopyToCsName();
			if(isNotEmpty(copyToCSName)){
				vo.setCopyToCSName(copyToCSName); 
			}
		}
	}
	
	public void hideInfoForUnfinishedQuote(List<QuoteItemVo> vos){
		for(QuoteItemVo vo : vos){
			QuoteItem item = vo.getQuoteItem();
			// amended according to INC0573693 
			if(item.getStage() != null && ! item.getStage().equalsIgnoreCase(QuoteSBConstant.QUOTE_STAGE_FINISH)){
				item.setQuoteNumber(null);
				item.setResaleIndicator(null);
				item.setMultiUsageFlag(null);
				item.setMpq(null);
				item.setMoq(null);
				item.setLeadTime(null);
				item.setTermsAndConditions(null);
				item.setRescheduleWindow(null);
				item.setCancellationWindow(null);
				item.setVendorQuoteNumber(null);
				item.setAllocationFlag(null);
				item.setRevertVersion(null);
				item.setFirstRfqCode(null);
				item.setQuotedPrice(null);
				item.setFinalQuotationPrice(null);	
				item.setCostIndicator(null);
				item.setPmoq(null);
				item.setQuotedPartNumber(null);
			}
		}
	}
	
	private void populateChineseCustoemrName(List<QuoteItemVo> quoteItemVos){
		for(QuoteItemVo quoteItemVo : quoteItemVos){
			QuoteItem quoteItem = quoteItemVo.getQuoteItem();
			if(quoteItem.getSoldToCustomer() != null){
				List<CustomerAddress> customerAddresses = quoteItem.getSoldToCustomer().getCustomerAddresss();
				for(CustomerAddress customerAddress : customerAddresses){
					if(customerAddress.getCountry() != null && customerAddress.getId().getLanguageCode() != null &&
							(
								customerAddress.getId().getLanguageCode().equals(QuoteSBConstant.LANGUAGE_CODE_CHINESE_C)
								|| customerAddress.getId().getLanguageCode().equals(QuoteSBConstant.LANGUAGE_CODE_CHINESE_M)
								|| customerAddress.getId().getLanguageCode().equals(QuoteSBConstant.LANGUAGE_CODE_CHINESE_1)
							)
					){
						String chineseCustomerName = customerAddress.getCustomerName1();
						if(chineseCustomerName != null && customerAddress.getCustomerName2() != null){
							chineseCustomerName += customerAddress.getCustomerName2();
						}					
						quoteItemVo.setSoldToChineseName(chineseCustomerName);
						break;
					}
				}	
			}					
		}
		
	}

    //Special handling for Quote without QuoteItem 
	private List<QuoteItem> searchQuoteWithoutItem(MyQuoteSearchCriteria criteria){
		
		//if criteria's below fields is not null, it shows user does not want to search a draft RFQ
		if( isNotEmpty(criteria.getsQuoteNumber()) || isNotEmpty(criteria.getsQuotedPartNumber()) 
				|| isNotEmpty(criteria.getsMfr()) || isNotEmpty(criteria.getsEndCustomerName()) 
				|| criteria.getPageQuoteExpiryDateFrom() != null || criteria.getPageQuoteExpiryDateTo() != null
				|| criteria.getPagePoExpiryDateFrom() != null || criteria.getPagePoExpiryDateTo() != null
				|| criteria.getPageUploadDateFrom() != null || criteria.getPageUploadDateTo() != null
				|| criteria.getPageSentOutDateFrom() != null || criteria.getPageSentOutDateTo() != null
				){
			return new ArrayList<QuoteItem>();
		}
		
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<Long> c = cb.createQuery(Long.class);

		Root<Quote> quote = c.from(Quote.class);
		
		Join<Quote, QuoteItem> quoteItem = quote.join("quoteItems", JoinType.LEFT);
		
		c.select(quote.<Long>get("id"))
		.groupBy(quote)
		.having(cb.equal(cb.count(quoteItem), 0));
		
		Join<Quote, User> sales = quote.join("sales", JoinType.LEFT);
		Join<Quote, Team> team = quote.join("team", JoinType.LEFT);
		Join<Quote, BizUnit> bizUnit  = quote.join("bizUnit", JoinType.LEFT);
		Join<Quote, Customer> soldToCustomer = quote.join("soldToCustomer", JoinType.LEFT);

		populateCriteria2(c, criteria, quote, sales, team, soldToCustomer, bizUnit);
		
		c.orderBy(cb.desc(quote.get("id")));
		
		TypedQuery<Long> q = em.createQuery(c);
		
		q.setFirstResult(0);
		q.setMaxResults(MAX_RESULT_IN_MYQUOTE);
		
		List<Long> ids = q.getResultList();
		
		List<Quote> quotes = new ArrayList<Quote>();
		if(ids.size() != 0){
			TypedQuery<Quote> query = em.createQuery("select q from Quote q where q.id in :ids", Quote.class);
			query.setParameter("ids", ids);
			quotes = query.getResultList();
			
		}

		List<QuoteItem> result = new ArrayList<QuoteItem>();
		
		for(Quote q1 : quotes){
			em.detach(q1);

			QuoteItem item = new QuoteItem();
			
			List<QuoteItem> items = new ArrayList<QuoteItem>();
			items.add(item);
			item.setQuote(q1);
			item.setStage(q1.getStage());
			q1.setQuoteItems(items);
			result.add(item);
		}
		
		return result;
	}

	private void populateCriteria2(CriteriaQuery<Long> c, MyQuoteSearchCriteria criteria, Root<Quote> quote, 
			Join<Quote, User> sales, Join<Quote, Team> team, Join<Quote, Customer> soldToCustomer,
			Join<Quote, BizUnit> bizUnit) {

		CriteriaBuilder cb = em.getCriteriaBuilder();

		List<Predicate> criterias = new ArrayList<Predicate>();
		
		if(criteria.getQuoteId() != null && criteria.getQuoteId().size() != 0){
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			for(Long quoteId : criteria.getQuoteId()){
				Predicate predicate = cb.equal(quote.<Long>get("id"), quoteId);
				predicates.add(predicate);
			}
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));
			//criteria.setQuoteId(null);
		}
		
		
		if(criteria.getFormNumber() != null && criteria.getFormNumber().size() != 0){
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			for(String formNumber : criteria.getFormNumber()){
				Predicate predicate = cb.like(quote.<String>get("formNumber"), "%" + formNumber + "%" );
				predicates.add(predicate);
			}
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));
		}

		if(criteria.getCustomerName() != null && criteria.getCustomerName().size() != 0){
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			for(String customerName : criteria.getCustomerName()){
				Predicate predicate = cb.like(cb.upper(soldToCustomer.<String>get("customerName1")), "%" + customerName.toUpperCase() + "%" );
				predicates.add(predicate);
			}
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));	
		}		
		
		if (criteria.getSalesName() != null && criteria.getSalesName().size()!=0) {
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			for(String name : criteria.getSalesName()){
				Predicate predicate = cb.like(cb.upper(sales.<String>get("name")), "%" + name.toUpperCase() + "%" );
				predicates.add(predicate);
			}
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));			
			
		}
		
		if (criteria.getYourReference() != null && criteria.getYourReference().size() !=0) {
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			for(String yourReference : criteria.getYourReference()){
				Predicate predicate = cb.like(cb.upper(quote.<String>get("yourReference")), "%" + yourReference.toUpperCase() + "%" );
				predicates.add(predicate);
			}
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));		
		}
		
		if (criteria.getSoldToCode() != null && criteria.getSoldToCode().size() != 0) {
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			for(String soldToCode : criteria.getSoldToCode()){
				Predicate predicate = cb.like(soldToCustomer.<String>get("customerNumber"), "%" + soldToCode + "%" );
				predicates.add(predicate);
			}
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));
									
		}

		if (criteria.getRestrictedSales() != null && criteria.getRestrictedSales().size()!=0) {
			
			CriteriaBuilder.In<Long> in = cb.in(sales.<Long>get("id"));
			
			for(User user : criteria.getRestrictedSales()){
				in.value(user.getId());
			}		
			criterias.add(in);					
		}		

		Predicate p1 = cb.notEqual(quote.<String>get("stage"), QuoteSBConstant.QUOTE_STATE_PROG_DRAFT);
		Predicate p2 = cb.isNull(quote.<String>get("stage"));
		criterias.add(cb.or(p1, p2));
		
		
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
		
			
		if(criteria.getLastUpdatedBy()!= null && criteria.getLastUpdatedBy().size() !=0){

			CriteriaBuilder.In<String> in = cb.in(quote.<String>get("lastUpdatedBy"));
			
			for(String employeeId : criteria.getLastUpdatedBy()){
				in.value(employeeId);
			}			
			criterias.add(in);		
		}

		if (criterias.size() == 0) {
			
		} else if (criterias.size() == 1) {
			c.where(criterias.get(0));
		} else {
			c.where(cb.and(criterias.toArray(new Predicate[0])));
		}
	}
	
	private boolean isNotEmpty(String s){
		if(s== null || s.equals("")){
			return false;
		}else{
			return true;
		}
	}

	/**
	 * Data maintenance search.
	 * @param criteria
	 * @return
	 */
	public List<QuoteItemVo> dataMaintenanceSearch(MyQuoteSearchCriteria criteria) {
	//	LOG.info("Data maintenance search begin...");
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<QuoteItem> c = cb.createQuery(QuoteItem.class);
		Root<QuoteItem> quoteItem = c.from(QuoteItem.class);
		
		Join<QuoteItem, ProductGroup> productGroup = quoteItem.join("productGroup2", JoinType.LEFT);
		
		Join<QuoteItem, Quote> quote = quoteItem.join("quote");
		Join<Quote, User> sales = quote.join("sales", JoinType.LEFT);
		Join<Quote, Team> team = quote.join("team", JoinType.LEFT);
		Join<Quote, BizUnit> bizUnit  = quote.join("bizUnit", JoinType.LEFT);
		Join<Quote, Customer> soldToCustomer = quote.join("soldToCustomer", JoinType.LEFT);
		Join<QuoteItem, Customer> endCustomer = quoteItem.join("endCustomer", JoinType.LEFT);

		Join<QuoteItem, Manufacturer> manufacturer = quoteItem.join("requestedMfr", JoinType.LEFT);
		Join<QuoteItem, QuoteItemDesign> itemDesign = quoteItem.join("quoteItemDesign", JoinType.LEFT);

		c.orderBy(cb.desc(quoteItem.get("quoteNumber")));
		
		populateCriteria(c, criteria, quoteItem, productGroup, quote
				, sales, team, soldToCustomer, endCustomer,null, manufacturer, bizUnit,itemDesign,null,null,null);
		
		TypedQuery<QuoteItem> q = em.createQuery(c);		
		q.setFirstResult(0);
		q.setMaxResults(MAX_RESULT_IN_MYQUOTE);
		List<QuoteItem> searchResult = q.getResultList();
		
		Collections.sort(searchResult, new Comparator<QuoteItem>(){
			public int compare(QuoteItem arg0, QuoteItem arg1) {
				if(arg0.getQuote().getId() == arg1.getQuote().getId()){
					return (int)(arg1.getQuote().getId() - arg0.getQuote().getId());
				}else{
					return (int)(arg1.getId() - arg0.getId());
				}
			}
		});
			
		LOG.info("size:" + searchResult.size());		
		List<QuoteItemVo> vos = new ArrayList<QuoteItemVo>(); 
    	
    	int i= 1;
    	for(QuoteItem item : searchResult){
			QuoteItemVo vo = new QuoteItemVo(item); 			
			vos.add(vo);		
			vo.setSeq(i++);
    	}
    	
	//	LOG.info("Data maintenance search ended.");
		return vos;	
	}
	
	
	
	public List<QuoteItemVo> dataMaintenanceSearch(MyQuoteSearchCriteria criteria,int first, int pageSize,
			String sortField, String sortOrder, Map<String, Object> filters) {
		//LOG.info("Data maintenance search begin...");
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<QuoteItem> c = cb.createQuery(QuoteItem.class);
		//c.distinct(true);
		Root<QuoteItem> quoteItem = c.from(QuoteItem.class);
		
		Join<QuoteItem, ProductGroup> productGroup = quoteItem.join("productGroup2", JoinType.LEFT);
		
		Join<QuoteItem, Quote> quote = quoteItem.join("quote");
		Join<Quote, User> sales = quote.join("sales", JoinType.LEFT);
		Join<Quote, Team> team = quote.join("team", JoinType.LEFT);
		Join<Quote, BizUnit> bizUnit  = quote.join("bizUnit", JoinType.LEFT);
		Join<Quote, Customer> soldToCustomer = quote.join("soldToCustomer", JoinType.LEFT);
		Join<QuoteItem, Customer> endCustomer = quoteItem.join("endCustomer", JoinType.LEFT);

		Join<QuoteItem, Manufacturer> manufacturer = quoteItem.join("requestedMfr", JoinType.LEFT);
		Join<QuoteItem, QuoteItemDesign> itemDesign = quoteItem.join("quoteItemDesign", JoinType.LEFT);

		//c.orderBy(cb.desc(quoteItem.get("quoteNumber")));
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.desc(quoteItem.get("submissionDate")));
		orderList.add(cb.desc(quoteItem.get("id")));

		c.orderBy(orderList);
		
		List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.or(cb.isNotNull(quoteItem.get("quoteNumber")),
                                        cb.isNotNull(quoteItem.get("quotedPartNumber")), cb.isNotNull(quoteItem.get("quotedPrice")),
                                        cb.isNotNull(quoteItem.get("cost")), cb.isNotNull(quoteItem.get("costIndicator"))));

		
		populateCriteria(c, criteria, quoteItem, productGroup, quote
				, sales, team, soldToCustomer, endCustomer,null, manufacturer, bizUnit,itemDesign,sortField,sortOrder,filters);
		
		TypedQuery<QuoteItem> q = em.createQuery(c);		
		q.setFirstResult(first);
		q.setMaxResults(pageSize);
		List<QuoteItem> searchResult = q.getResultList();
		
		
		LOG.info("size:" + searchResult.size());		
		List<QuoteItemVo> vos = new ArrayList<QuoteItemVo>(); 
    	
		
		first++;
		long st = System.currentTimeMillis();
    	for(QuoteItem item : searchResult){
			QuoteItemVo vo = new QuoteItemVo(item); 			
			vos.add(vo);		
			vo.setSeq(first++);
    	}
	//	LOG.info("Data maintenance search ended.");
		return vos;	
	}
	
	
	public int dataMaintenanceCount(MyQuoteSearchCriteria criteria,
			String sortField, String sortOrder, Map<String, Object> filters) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> c = cb.createQuery(Long.class);
		Root<QuoteItem> quoteItem = c.from(QuoteItem.class);
		
		Join<QuoteItem, ProductGroup> productGroup = quoteItem.join("productGroup2", JoinType.LEFT);
		
		Join<QuoteItem, Quote> quote = quoteItem.join("quote");
		Join<Quote, User> sales = quote.join("sales", JoinType.LEFT);
		Join<Quote, Team> team = quote.join("team", JoinType.LEFT);
		Join<Quote, BizUnit> bizUnit  = quote.join("bizUnit", JoinType.LEFT);
		Join<Quote, Customer> soldToCustomer = quote.join("soldToCustomer", JoinType.LEFT);
		Join<QuoteItem, Customer> endCustomer = quoteItem.join("endCustomer", JoinType.LEFT);

		Join<QuoteItem, Manufacturer> manufacturer = quoteItem.join("requestedMfr", JoinType.LEFT);
		Join<QuoteItem, QuoteItemDesign> itemDesign = quoteItem.join("quoteItemDesign", JoinType.LEFT);

		//c.orderBy(cb.desc(quoteItem.get("quoteNumber")));
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.desc(quoteItem.get("submissionDate")));
		orderList.add(cb.desc(quoteItem.get("id")));

		c.orderBy(orderList);
		
		List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.or(cb.isNotNull(quoteItem.get("quoteNumber")),
                                        cb.isNotNull(quoteItem.get("quotedPartNumber")), cb.isNotNull(quoteItem.get("quotedPrice")),
                                        cb.isNotNull(quoteItem.get("cost")), cb.isNotNull(quoteItem.get("costIndicator"))));

        
		populateCriteria(c, criteria, quoteItem, productGroup, quote
				, sales, team, soldToCustomer, endCustomer,null, manufacturer, bizUnit,itemDesign,sortField,sortOrder,filters);
		
		c.select(cb.count(quoteItem));

		int count = em.createQuery(c).getSingleResult().intValue();
		return count;
    	
		
		
	}
	
	@Asynchronous
	public void dataMaintenanceAsyncSearch(MyQuoteSearchCriteria criteria,int first, int pageSize,int pageNumber,
			String sortField, String sortOrder, Map<String, Object> filters,ConcurrentHashMap<Integer, List<QuoteItemVo>> map, Queue<Integer> queue, int cachePageSize) {
	//	LOG.info("Data maintenance search begin...");
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<QuoteItem> c = cb.createQuery(QuoteItem.class);
		Root<QuoteItem> quoteItem = c.from(QuoteItem.class);
		//c.distinct(true);
		
		Join<QuoteItem, ProductGroup> productGroup = quoteItem.join("productGroup2", JoinType.LEFT);
		
		Join<QuoteItem, Quote> quote = quoteItem.join("quote");
		Join<Quote, User> sales = quote.join("sales", JoinType.LEFT);
		Join<Quote, Team> team = quote.join("team", JoinType.LEFT);
		Join<Quote, BizUnit> bizUnit  = quote.join("bizUnit", JoinType.LEFT);
		Join<Quote, Customer> soldToCustomer = quote.join("soldToCustomer", JoinType.LEFT);
		Join<QuoteItem, Customer> endCustomer = quoteItem.join("endCustomer", JoinType.LEFT);

		Join<QuoteItem, Manufacturer> manufacturer = quoteItem.join("requestedMfr", JoinType.LEFT);
		Join<QuoteItem, QuoteItemDesign> itemDesign = quoteItem.join("quoteItemDesign", JoinType.LEFT);

		//c.orderBy(cb.desc(quoteItem.get("quoteNumber")));
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.desc(quoteItem.get("submissionDate")));
		orderList.add(cb.desc(quoteItem.get("id")));

		c.orderBy(orderList);
		
		List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.or(cb.isNotNull(quoteItem.get("quoteNumber")),
                                        cb.isNotNull(quoteItem.get("quotedPartNumber")), cb.isNotNull(quoteItem.get("quotedPrice")),
                                        cb.isNotNull(quoteItem.get("cost")), cb.isNotNull(quoteItem.get("costIndicator"))));

		
		
		populateCriteria(c, criteria, quoteItem, productGroup, quote
				, sales, team, soldToCustomer, endCustomer,null, manufacturer, bizUnit,itemDesign,sortField,sortOrder,filters);
		
		TypedQuery<QuoteItem> q = em.createQuery(c);		
		q.setFirstResult(first);
		q.setMaxResults(pageSize);
		List<QuoteItem> searchResult = q.getResultList();
		
		
		LOG.info("size:" + searchResult.size());		
		List<QuoteItemVo> vos = new ArrayList<QuoteItemVo>(); 
    	
		
		first++;
		
		if(searchResult != null && searchResult.size() > 0){
			for(QuoteItem item : searchResult){
				QuoteItemVo vo = new QuoteItemVo(item); 			
				vos.add(vo);		
				vo.setSeq(first++);
	    	}
	    	
	    	map.put(pageNumber, vos);
			queue.add(pageNumber);
			
			if(cachePageSize < map.size()){
				map.remove(queue.remove());
			}
			
		}
    	
		
		
	//	LOG.info("Data maintenance search ended.");
		
	}

	/*public List<QuoteItemVo> offLineSearch(MyQuoteSearchCriteria criteria) {
		
		LOG.info(criteria.toString());
		
		List<QuoteItem> searchResult = new ArrayList<QuoteItem>();		
		
		if(criteria.getStage() != null && criteria.getStage().size() !=0){
			if(criteria.getStage().contains(QuoteSBConstant.QUOTE_STAGE_DRAFT)){
				searchResult = searchQuoteWithoutItem(criteria);
			}
		}			

		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<QuoteItem> c = cb.createQuery(QuoteItem.class);

		Root<QuoteItem> quoteItem = c.from(QuoteItem.class);
		
		Join<QuoteItem, ProductGroup> productGroup = quoteItem.join("productGroup2", JoinType.LEFT);

		Join<QuoteItem, Quote> quote = quoteItem.join("quote");
		Join<Quote, User> sales = quote.join("sales", JoinType.LEFT);
		Join<Quote, Team> team = quote.join("team", JoinType.LEFT);
		Join<Quote, BizUnit> bizUnit  = quote.join("bizUnit", JoinType.LEFT);
		Join<Quote, Customer> soldToCustomer = quote.join("soldToCustomer", JoinType.LEFT);
		Join<QuoteItem, Customer> endCustomer = quoteItem.join("endCustomer", JoinType.LEFT);
		
		Join<QuoteItem, Manufacturer> manufacturer = quoteItem.join("requestedMfr", JoinType.LEFT);
		Join<QuoteItem, QuoteItemDesign> itemDesign = quoteItem.join("quoteItemDesign", JoinType.LEFT);

		c.orderBy(cb.desc(quoteItem.get("quoteNumber")));
		
		populateCriteria(c, criteria, quoteItem,  productGroup, quote, sales, team, soldToCustomer, endCustomer, manufacturer, bizUnit,itemDesign);
		
		TypedQuery<QuoteItem> q = em.createQuery(c);
		
		q.setFirstResult(criteria.getStart());
		q.setMaxResults(criteria.getEnd());
		
		
		List<QuoteItem> searchResult2 = q.getResultList();
		if(searchResult2!=null)
		{
			for(QuoteItem item : searchResult2)
			{
				em.detach(item);
			}
		}
		searchResult.addAll(searchResult2);
		
		Collections.sort(searchResult, new Comparator<QuoteItem>(){
			public int compare(QuoteItem arg0, QuoteItem arg1) {
				if(arg1.getSubmissionDate()!=null && arg0.getSubmissionDate()!=null) {
					int i =  arg1.getSubmissionDate().compareTo(arg0.getSubmissionDate());
					
					if(i !=0 ){
						return i;
					}else{
						return (int)(arg1.getId() - arg0.getId());
	
					}
				}else {
					return 0;
				}
			}
		});
	
		
		LOG.info("size:" + searchResult.size());
		
		List<QuoteItemVo> vos = calculateReadyForOrder(searchResult);		
		calculateCopyToCSName(vos);				
		return vos;
	}*/
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<QuoteItem> updateReferenceMarginForSubmission(List<QuoteItem> quoteItems){
		List<ReferenceMarginSetting> referenceMarginSettings = manufacturerSB.findAllReferenceMarginSetting();
		
		Collections.sort(referenceMarginSettings, new Comparator() {

	        public int compare(Object o1, Object o2) {

	            long x1 = ((ReferenceMarginSetting) o1).getMaterialID();
	            long x2 = ((ReferenceMarginSetting) o2).getMaterialID();
	            
	            int xComp = 0;
				if (x1 > x2) {
					xComp = 1;
				} else if (x1 < x2) {
					xComp = -1;
				}

	            if (xComp != 0) {
	               return xComp;
	            } else {
	               long s1 = ((ReferenceMarginSetting) o1).getProductGroupID();
	               long s2 = ((ReferenceMarginSetting) o2).getProductGroupID();
	               
					int sComp = 0;
					if (s1 > s2) {
						sComp = 1;
					} else if (s1 < s2) {
						sComp = -1;
					}
		            return sComp;
	            }
	        }
	    });
		Collections.reverse(referenceMarginSettings);
		QuoteItem temp=null;
		try {
			for (int i = 0; i < quoteItems.size(); i++) {
				QuoteItem quoteItemVo = quoteItems.get(i);
				temp = quoteItemVo;
				if(QuoteSBConstant.QUOTE_STAGE_FINISH.equalsIgnoreCase(quoteItemVo.getStage()) &&
						QuoteSBConstant.RFQ_STATUS_AQ.equalsIgnoreCase(quoteItemVo.getStatus()) && 
						quoteItemVo.getResaleMargin()!=null && 
						quoteItemVo.getResaleMargin().doubleValue()!=0d ){
					for (int k = 0 ; k < referenceMarginSettings.size() ; k++){
						try{
							ReferenceMarginSetting referenceMarginSetting = referenceMarginSettings.get(k);
							
							long quoteItemManufacturerId = 0l;
							long quoteItemMaterialId = 0l;  
							long quoteItemProductGroup = 0l;
							String quoteBizUnit = "";
							
							long referenceMarginSettingManufacturerId = 0l; 
							long referenceMarginSettingMaterialId = 0l; 
							long referenceMarginProductGroup = 0l;
							String referenceMarginSettingBizUnit = "";
							
							if(quoteItemVo.getQuotedMaterial().getManufacturer().getId()!=0l){
								quoteItemManufacturerId = quoteItemVo.getQuotedMaterial().getManufacturer().getId();
							}
							if(quoteItemVo.getQuotedMaterial().getId()!=0l){
								quoteItemMaterialId = quoteItemVo.getQuotedMaterial().getId();
							}
							if(quoteItemVo.getProductGroup2()!=null && quoteItemVo.getProductGroup2().getId()!=0){	
								quoteItemProductGroup = quoteItemVo.getProductGroup2().getId();
							}
							if(quoteItemVo.getQuote().getBizUnit().getName()!=null){
								quoteBizUnit = quoteItemVo.getQuote().getBizUnit().getName();
							}
							if(referenceMarginSetting.getManufacturerID()!=0){
								referenceMarginSettingManufacturerId = referenceMarginSetting.getManufacturerID();
							}
							if(referenceMarginSetting.getMaterialID()!=0){
								referenceMarginSettingMaterialId = referenceMarginSetting.getMaterialID();
							}
							if(referenceMarginSetting.getProductGroupID()!=0){
								referenceMarginProductGroup = referenceMarginSetting.getProductGroupID();
							}
							if(referenceMarginSetting.getBizUnitID()!=null){
								referenceMarginSettingBizUnit = referenceMarginSetting.getBizUnitID();
							}
							
							//All 4 items matched
							if(quoteItemManufacturerId==referenceMarginSettingManufacturerId  && 
									quoteBizUnit.equals(referenceMarginSettingBizUnit) &&
									quoteItemMaterialId == referenceMarginSettingMaterialId &&
									quoteItemProductGroup == referenceMarginProductGroup){
								applyReferenceMarginLogicForSubmission(referenceMarginSetting, quoteItemVo);
								break;
							}
							//3 Items matched and  materialId is 0
							else if(quoteItemManufacturerId==referenceMarginSettingManufacturerId && 
									quoteBizUnit.equals(referenceMarginSettingBizUnit) &&
									referenceMarginSettingMaterialId == 0 &&
									quoteItemProductGroup == referenceMarginProductGroup){
								applyReferenceMarginLogicForSubmission(referenceMarginSetting, quoteItemVo);
								break;
							}
							//2 Items matached and materialID, productGroup == 0
							else if(quoteItemManufacturerId==referenceMarginSettingManufacturerId && 
									quoteBizUnit.equals(referenceMarginSettingBizUnit) &&
									referenceMarginSettingMaterialId == 0 &&
									referenceMarginProductGroup == 0){
								applyReferenceMarginLogicForSubmission(referenceMarginSetting, quoteItemVo);
								break;
							}
							
						}
						catch(Exception e)
						{	
							LOG.log(Level.SEVERE, "Problem retrieving part number data. Error occured for manufacturer ID: "+quoteItemVo.getQuotedMaterial().getManufacturer().getId()+", "
									+ "Manufacturer name: "+quoteItemVo.getQuotedMaterial().getManufacturer().getName(), e);
						}				
					}
				}
			}
		} catch (OptimisticLockException ole) {
			LOG.log(Level.SEVERE, "Error occured for project name: "+temp.getProjectName()+", manufacturer ID: "+temp.getQuotedMaterial().getManufacturer().getId()+", "
					+ "Reason for failure: "+MessageFormatorUtil.getParameterizedStringFromException(ole), ole);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Error occured for project name: "+temp.getProjectName()+", manufacturer ID: "+temp.getQuotedMaterial().getManufacturer().getId()+", "
					+ "Reason for failure: "+MessageFormatorUtil.getParameterizedStringFromException(e), e);
		}
		quoteItems =referenceMarginCalculationFilterResult(quoteItems);
		return quoteItems;
	}

	private void applyReferenceMarginLogicForSubmission(ReferenceMarginSetting referenceMarginSetting, QuoteItem quoteItem){
		double resaleMargin = quoteItem.getResaleMargin();
		double factor = referenceMarginSetting.getFactor().doubleValue()/100;
		double floor = referenceMarginSetting.getFloor().doubleValue();
		double cap = referenceMarginSetting.getCap().doubleValue();
		double referenceMargin = factor*resaleMargin;

		try {
			if (referenceMargin != 0d) {
				if (referenceMargin < resaleMargin && resaleMargin < floor) {
					quoteItem.setReferenceMargin(Double.valueOf(resaleMargin));
				} else if (floor != 0 && referenceMargin < floor&& floor <= resaleMargin) {
					quoteItem.setReferenceMargin(Double.valueOf(floor));
				} else if (cap != 0 && referenceMargin > cap) {
					quoteItem.setReferenceMargin(Double.valueOf(cap));
				} else {
					quoteItem.setReferenceMargin(Double.valueOf(referenceMargin));
				}
			}
		} catch (OptimisticLockException ole) {
			LOG.severe("Data was updated by another source " + ole);
		} catch (Exception e) {
			LOG.severe("General update reference margin error " + e);
		}
	}
	
	
	public List<QuoteItem> queryReferenceMarginPatchQI(){
		Query query = em.createQuery("select qi from QuoteItem qi where qi.stage=:stage and qi.referenceMargin is null and qi.resaleMargin is not null and qi.quoteNumber like :part");
		query.setParameter("stage", "FINISH");
		query.setParameter("part", "WR2%");
		List<QuoteItem> results = (List<QuoteItem>)query.getResultList();
		return results;
	}
	
	
	public List<QuoteItem> referenceMarginCalculationFilterResult(List<QuoteItem> tempList)
	{
		if(tempList!=null && tempList.size()>0)
		{
			for(QuoteItem qi : tempList)
			{
				if(qi.getResaleMargin()==null)
				{
					qi.setReferenceMargin(null);
				}
				else
				{
					 if(qi.getResaleMargin()==0d)
					 {
						 qi.setReferenceMargin(0d);
					 }else{
						 if(qi.getReferenceMargin()==null){// INC0043919 if the quote which cannot find the reference margin setting, assign reference quote margin as quoted margin. 
							 qi.setReferenceMargin(qi.getResaleMargin());
						 }
					 }
				}
				
			}
			return tempList;
		}
		else
			return tempList;
	}
	
}