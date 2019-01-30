package com.avnet.emasia.webquote.quote.ejb;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
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
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import com.avnet.emasia.webquote.entity.BalUnconsumedQty;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.quote.vo.BalUnconsumedQtyTemplateBean;
import com.avnet.emasia.webquote.utilities.DBResourceBundle;
import com.avnet.emasia.webquote.utilities.util.QuoteUtil;

@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
public class BalanceUnconsumedQtySB {

	private static final Logger LOG = Logger.getLogger(BalanceUnconsumedQtySB.class.getName());
	
	@Resource
	private UserTransaction ut;

	@PersistenceContext(unitName = "Server_Source")
	private EntityManager em;
	
	
	@EJB
	private QuoteSB quoteSB;
	
	public BalUnconsumedQty findByAllUk(String mfr, String quoteNumber,
			String quotedPartNumber, String supplierQuoteNumber){
		
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<BalUnconsumedQty> c = cb.createQuery(BalUnconsumedQty.class);

		Root<BalUnconsumedQty> balUnconsumedQty = c.from(BalUnconsumedQty.class);
		List<Predicate> criterias = new ArrayList<Predicate>();
		Predicate predicate = cb.equal(balUnconsumedQty.<String>get("mfr"),mfr );
		criterias.add(predicate);
		Predicate predicate1 = cb.equal(balUnconsumedQty.<String>get("supplierQuoteNumber"),supplierQuoteNumber );
		criterias.add(predicate1);
	
		if(QuoteUtil.isEmpty(quoteNumber)){
			Predicate predicate2 = cb.isNull(balUnconsumedQty.<String>get("quoteNumber"));
			criterias.add(predicate2);
		}else{
			Predicate predicate2 = cb.equal(balUnconsumedQty.<String>get("quoteNumber"),quoteNumber );
			criterias.add(predicate2);
		}
	
		if(QuoteUtil.isEmpty(quotedPartNumber)){
			Predicate predicate3 = cb.isNull(balUnconsumedQty.<String>get("quotedPartNumber"));
			criterias.add(predicate3);
		}else{
			Predicate predicate3 = cb.equal(balUnconsumedQty.<String>get("quotedPartNumber"),quotedPartNumber );
			criterias.add(predicate3);
		}
		if (criterias.size() == 1) {
		c.where(criterias.get(0));
		} else {
			c.where(cb.and(criterias.toArray(new Predicate[0])));
		}
		TypedQuery<BalUnconsumedQty> q = em.createQuery(c);
		
		q.setFirstResult(0);
		q.setMaxResults(1);
		List<BalUnconsumedQty> list = q.getResultList();
	
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}		
	}
	
	public Boolean batchPersist(List<BalUnconsumedQtyTemplateBean>  list,String bizUnit) {
		Boolean bool = false;

		try {
			ut.setTransactionTimeout(100000);
			ut.begin();
		} catch (NotSupportedException e) {
			LOG.log(Level.SEVERE,"NotSupportedException occurs on set TransactionTimeout " + e.getMessage(), e);
		} catch (SystemException e) {
			LOG.log(Level.SEVERE,"SystemException occurs on set TransactionTimeout " + e.getMessage(), e);
		}
	
		LOG.info("begin bulk Persist BalUnconsumedQty to Db!");
		BalUnconsumedQty beanIndataBase = null;
		for(BalUnconsumedQtyTemplateBean bean:list){
			beanIndataBase =findByAllUk(bean.getMfr(),bean.getQuoteNumber(),bean.getQuotedPartNumber(),bean.getSupplierQuoteNumber());
			if(beanIndataBase==null){
				beanIndataBase = new BalUnconsumedQty();
				beanIndataBase.setMfr(bean.getMfr());
				beanIndataBase.setQuotedPartNumber(bean.getQuotedPartNumber());
				beanIndataBase.setQuoteNumber(bean.getQuoteNumber());
				beanIndataBase.setSupplierQuoteNumber(bean.getSupplierQuoteNumber());
				beanIndataBase.setBizUnit(bizUnit);
				beanIndataBase.setBuQty(QuoteUtil.strToInt(bean.getBuQty()));
				em.persist(beanIndataBase);
			}else{
				beanIndataBase.setBuQty(QuoteUtil.strToInt(bean.getBuQty()));
				em.merge(beanIndataBase);
			}
		}
		try {
			ut.commit();
			bool = true;
		} catch (SecurityException e) {
			LOG.log(Level.SEVERE,"SecurityException occurs on bulk Persist BalUnconsumedQty to Db " + e.getMessage(), e);
		} catch (IllegalStateException e) {
			LOG.log(Level.SEVERE,"IllegalStateException occurs on bulk Persist BalUnconsumedQty to Db " + e.getMessage(), e);
		} catch (RollbackException e) {
			LOG.log(Level.SEVERE,"RollbackException occurs on bulk Persist BalUnconsumedQty to Db " + e.getMessage(), e);
		} catch (HeuristicMixedException e) {
			LOG.log(Level.SEVERE,"HeuristicMixedException occurs on bulk Persist BalUnconsumedQty to Db " + e.getMessage(), e);
		} catch (HeuristicRollbackException e) {
			LOG.log(Level.SEVERE,"HeuristicRollbackException occurs on bulk Persist BalUnconsumedQty to Db " + e.getMessage(), e);
		} catch (SystemException e) {
			LOG.log(Level.SEVERE,"SystemException occurs on bulk Persist BalUnconsumedQty to Db " + e.getMessage(), e);
		}
		LOG.info("end bulk Persist BalUnconsumedQty to Db!");
		return bool;
	}
	
	public List<BalUnconsumedQty> findAllByBizUnits(String bizUnitName){
		TypedQuery<BalUnconsumedQty> query = em.createQuery("select u from BalUnconsumedQty u "
				+ "where u.bizUnit = :bizUnitName order by u.id" , BalUnconsumedQty.class);		
		query.setParameter("bizUnitName", bizUnitName);		
		return query.getResultList();
	}

	/**
	 * Priority
	 * MFR + MFR Quote# + Avnet Quote Number + Quoted Part Number  Priority1
	 * MFR + MFR Quote# + Avnet Quote Number Priority2
	 * MFR + MFR Quote# + Quoted Part number Priority3
	 * MFR + MFR Quote# Priority4
	 * @param mfr
	 * @param quoteNumber
	 * @param quotedPartNumber
	 * @param supplierQuoteNumber
	 * @param bizUnit
	 * @return
	 */
	public Integer getBalUnconsumedQtyByCriteria(String mfr, String quoteNumber,
			String quotedPartNumber, String supplierQuoteNumber,String bizUnit) {

		TypedQuery<BalUnconsumedQty> query = em.createQuery("select u from BalUnconsumedQty u where u.mfr = :mfr "
					+ "and u.supplierQuoteNumber= :supplierQuoteNumber and u.bizUnit= :bizUnit " , BalUnconsumedQty.class);	
		query.setParameter("mfr", mfr);
		query.setParameter("supplierQuoteNumber", supplierQuoteNumber);
		query.setParameter("bizUnit", bizUnit);
		List<BalUnconsumedQty> list =  query.getResultList();
		Integer buQty = null;
		if(list.size()==0){
				return buQty;
		}else{
			buQty =getBalUnconsumedQtyByPriority1(mfr, quoteNumber,quotedPartNumber, supplierQuoteNumber,list);
			if(buQty==null){
				buQty =getBalUnconsumedQtyByPriority2(mfr, quoteNumber, supplierQuoteNumber,list);
			}
			if(buQty==null){
				buQty =getBalUnconsumedQtyByPriority3(mfr, quotedPartNumber, supplierQuoteNumber,list);
			}
			if(buQty==null){
				buQty =getBalUnconsumedQtyByPriority4(mfr, supplierQuoteNumber,list);
			}
		}
					
		return buQty;
	}	
	
	private Integer getBalUnconsumedQtyByPriority1(String mfr, String quoteNumber,
			String quotedPartNumber, String supplierQuoteNumber,List<BalUnconsumedQty> list){
		for(BalUnconsumedQty bean:list){
			String mfrBean = bean.getMfr();
			String quotedPartNumberBean = bean.getQuotedPartNumber();
			String quoteNumberBean = bean.getQuoteNumber();
			String supplierQuoteNumberBean = bean.getSupplierQuoteNumber();
			if((mfrBean!=null&&mfr!=null&&mfrBean.equalsIgnoreCase(mfr))
				&&(quotedPartNumberBean!=null&&quotedPartNumber!=null&&quotedPartNumberBean.equalsIgnoreCase(quotedPartNumber))
				&&(quoteNumberBean!=null&&quoteNumber!=null&&quoteNumberBean.equalsIgnoreCase(quoteNumber))
				&&(supplierQuoteNumberBean!=null&&supplierQuoteNumber!=null&&supplierQuoteNumberBean.equalsIgnoreCase(supplierQuoteNumber))){
				return bean.getBuQty();
			}
		}
		return null;
	}
	
	private Integer getBalUnconsumedQtyByPriority2(String mfr, String quoteNumber,
			String supplierQuoteNumber,List<BalUnconsumedQty> list){
		for(BalUnconsumedQty bean:list){
			String mfrBean = bean.getMfr();
			String quotedPartNumberBean = bean.getQuotedPartNumber();
			String quoteNumberBean = bean.getQuoteNumber();
			String supplierQuoteNumberBean = bean.getSupplierQuoteNumber();
			if((mfrBean!=null&&mfr!=null&&mfrBean.equalsIgnoreCase(mfr))
				&&(QuoteUtil.isEmpty(quotedPartNumberBean))
				&&(quoteNumberBean!=null&&quoteNumber!=null&&quoteNumberBean.equalsIgnoreCase(quoteNumber))
				&&(supplierQuoteNumberBean!=null&&supplierQuoteNumber!=null&&supplierQuoteNumberBean.equalsIgnoreCase(supplierQuoteNumber))){
				return bean.getBuQty();
			}
		}
		return null;
	}
	
	private Integer getBalUnconsumedQtyByPriority3(String mfr, 
			String quotedPartNumber, String supplierQuoteNumber,List<BalUnconsumedQty> list){
		for(BalUnconsumedQty bean:list){
			String mfrBean = bean.getMfr();
			String quotedPartNumberBean = bean.getQuotedPartNumber();
			String quoteNumberBean = bean.getQuoteNumber();
			String supplierQuoteNumberBean = bean.getSupplierQuoteNumber();
			if((mfrBean!=null&&mfr!=null&&mfrBean.equalsIgnoreCase(mfr))
				&&(quotedPartNumberBean!=null&&quotedPartNumber!=null&&quotedPartNumberBean.equalsIgnoreCase(quotedPartNumber))
				&&(QuoteUtil.isEmpty(quoteNumberBean))
				&&(supplierQuoteNumberBean!=null&&supplierQuoteNumber!=null&&supplierQuoteNumberBean.equalsIgnoreCase(supplierQuoteNumber))){
				return bean.getBuQty();
			}
		}
		return null;
	}
	
	private Integer getBalUnconsumedQtyByPriority4(String mfr,String supplierQuoteNumber,List<BalUnconsumedQty> list){
		for(BalUnconsumedQty bean:list){
			String mfrBean = bean.getMfr();
			String quotedPartNumberBean = bean.getQuotedPartNumber();
			String quoteNumberBean = bean.getQuoteNumber();
			String supplierQuoteNumberBean = bean.getSupplierQuoteNumber();
			if((mfrBean!=null&&mfr!=null&&mfrBean.equalsIgnoreCase(mfr))
				&&(QuoteUtil.isEmpty(quotedPartNumberBean))
				&&(QuoteUtil.isEmpty(quoteNumberBean))
				&&(supplierQuoteNumberBean!=null&&supplierQuoteNumber!=null&&supplierQuoteNumberBean.equalsIgnoreCase(supplierQuoteNumber))){
				return bean.getBuQty();
			}
		}
		return null;
	}

	public String verifyQuotedPartNumber(List<BalUnconsumedQtyTemplateBean> beans, Locale locale) {
		StringBuffer sb = new StringBuffer();
		ResourceBundle bundle = ResourceBundle.getBundle(DBResourceBundle.class.getName(),locale);

		for (BalUnconsumedQtyTemplateBean bean : beans) {
			if (!QuoteUtil.isEmpty(bean.getQuoteNumber())&& !QuoteUtil.isEmpty(bean.getQuotedPartNumber())) {
				QuoteItem quoteItem = quoteSB.findQuoteItemByQuoteNumber(bean.getQuoteNumber());
				
				if (quoteItem != null) {
					if (quoteItem.getQuotedPartNumber() != null&&
								!bean.getQuotedPartNumber().trim().equalsIgnoreCase(quoteItem.getQuotedPartNumber())) {
						sb.append(bundle.getString("wq.message.row")+" <"+ bean.getLineSeq()
									+ "> :"+bundle.getString("wq.message.QPNnotMatch")+" ("+bean.getQuoteNumber()+")<br>");
					}
				}else{
					sb.append(bundle.getString("wq.message.row")+" <"+ bean.getLineSeq()
								+ "> :"+bundle.getString("wq.message.quoteNoNotExist")+"<br>");
				}				
			}
		}
		return sb.toString();
	}
}
