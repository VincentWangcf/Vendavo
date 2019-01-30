package com.avnet.emasia.webquote.commodity.ejb;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;

import org.apache.commons.lang.StringUtils;
import org.jboss.ejb3.annotation.TransactionTimeout;

import com.avnet.emasia.webquote.commodity.bean.PricerUploadTemplateBean;
import com.avnet.emasia.webquote.commodity.bean.ProgramItemUploadCounterBean;
import com.avnet.emasia.webquote.commodity.constant.PRICER_TYPE;
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
import com.avnet.emasia.webquote.entity.ProductGroup;
import com.avnet.emasia.webquote.entity.ProgramPricer;
import com.avnet.emasia.webquote.entity.ProgramType;
import com.avnet.emasia.webquote.entity.QuantityBreakPrice;
import com.avnet.emasia.webquote.entity.SalesCostPricer;
import com.avnet.emasia.webquote.entity.SimplePricer;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.ejb.MaterialRegionalSB;
import com.avnet.emasia.webquote.quote.ejb.SystemCodeMaintenanceSB;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.utilities.ejb.MailUtilsSB;
import com.avnet.emasia.webquote.utilities.util.QuoteUtil;

/**
 * The Class PricerEnquirySB.
 */
@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
// NEC Pagination Changes: Class modified for implementing lazy loading in
// pagination
public class PricerEnquirySB {

	/** The Constant MAX_RESULT_IN_PRICERINQUIRY. */
	private static final int MAX_RESULT_IN_PRICERINQUIRY = 501;

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(PricerEnquirySB.class.getName());

	/** The em. */
	@PersistenceContext(unitName = "Server_Source")
	private EntityManager em;
	// ffffff

	/** The ut. */
	@Resource
	public UserTransaction ut;

	/** The mail utils SB. */
	@EJB
	protected MailUtilsSB mailUtilsSB;

	/** The sys code maint SB. */
	@EJB
	private SystemCodeMaintenanceSB sysCodeMaintSB;

	/** The pricer upload common SB. */
	@EJB
	private PricerUploadCommonSB pricerUploadCommonSB;

	@EJB
	private MaterialRegionalSB mRegionalSB;

	@TransactionTimeout(value = 30000, unit = TimeUnit.SECONDS)
	// NEC Pagination Changes: search method overloaded for fetching data
	// through lazy loading
	public List<PricerUploadTemplateBean> pricerEnquiry(PricerUploadTemplateBean criteria, boolean isOnline, int first,
			int pageSize) {
		List<PricerUploadTemplateBean> pricerLst = null;
		//LOG.severe("EntityManager system id::: " + System.identityHashCode(em));
		if (criteria.getPricerType().equalsIgnoreCase(PRICER_TYPE.NORMAL.getName())) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<NormalPricer> c = cb.createQuery(NormalPricer.class);
			c.distinct(true);
			Root<NormalPricer> mt = c.from(NormalPricer.class);
			mt.fetch("material", JoinType.LEFT);
			Join<NormalPricer, BizUnit> bizUnit = mt.join("bizUnitBean");

			Join<NormalPricer, Material> material = mt.join("material");
			populateCriteriaForNormal(criteria, material, c, bizUnit, mt);
			TypedQuery<NormalPricer> q = em.createQuery(c);
			// NEC Pagination Changes: Sets numbers of records to be fetched for
			// current page
			if (isOnline) {
				q.setFirstResult(first);

				q.setMaxResults(pageSize);
			}

			List<NormalPricer> searchResult = q.getResultList();

			pricerLst = convertNorPricerToBeans(searchResult);
		} else if (criteria.getPricerType().equalsIgnoreCase(PRICER_TYPE.PROGRAM.getName())) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ProgramPricer> c = cb.createQuery(ProgramPricer.class);
			c.distinct(true);
			Root<ProgramPricer> mt = c.from(ProgramPricer.class);
			mt.fetch("material", JoinType.LEFT);
			Join<ProgramPricer, BizUnit> bizUnit = mt.join("bizUnitBean");

			Join<ProgramPricer, Material> material = mt.join("material");

			this.populateCriteriaForProgram(criteria, material, c, bizUnit, mt, null, null);
			TypedQuery<ProgramPricer> q = em.createQuery(c);

			// NEC Pagination Changes: Sets numbers of records to be fetched for
			// current page
			if (isOnline) {
				q.setFirstResult(first);

				q.setMaxResults(pageSize);
			}
			List<ProgramPricer> searchResult = q.getResultList();
			pricerLst = convertPgPricerToBeans(searchResult);
		} else if (criteria.getPricerType().equalsIgnoreCase(PRICER_TYPE.CONTRACT.getName())) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ContractPricer> c = cb.createQuery(ContractPricer.class);
			c.distinct(true);
			Root<ContractPricer> mt = c.from(ContractPricer.class);
			mt.fetch("material", JoinType.LEFT);
			Join<ContractPricer, BizUnit> bizUnit = mt.join("bizUnitBean", JoinType.LEFT);

			Join<ContractPricer, Material> material = mt.join("material", JoinType.LEFT);

			this.populateCriteriaForContract(criteria, material, c, bizUnit, mt, null, null);
			TypedQuery<ContractPricer> q = em.createQuery(c);

			// NEC Pagination Changes: Sets numbers of records to be fetched for
			// current page
			if (isOnline) {
				q.setFirstResult(first);

				q.setMaxResults(pageSize);
			}
			List<ContractPricer> searchResult = q.getResultList();
			pricerLst = convertCtrPricerToBeans(searchResult);
		} else if (criteria.getPricerType().equalsIgnoreCase(PRICER_TYPE.SALESCOST.getName())) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<SalesCostPricer> c = cb.createQuery(SalesCostPricer.class);
			c.distinct(true);
			Root<SalesCostPricer> mt = c.from(SalesCostPricer.class);
			mt.fetch("material", JoinType.LEFT);
			Join<SalesCostPricer, BizUnit> bizUnit = mt.join("bizUnitBean", JoinType.LEFT);

			Join<SalesCostPricer, Material> material = mt.join("material", JoinType.LEFT);

			this.populateCriteriaForSalesCost(criteria, material, c, bizUnit, mt, null, null);
			TypedQuery<SalesCostPricer> q = em.createQuery(c);

			if (isOnline) {
				q.setFirstResult(first);

				q.setMaxResults(pageSize);
			}
			List<SalesCostPricer> searchResult = q.getResultList();
			pricerLst = convertSalesCostPricerToBeans(searchResult);
		} else if (criteria.getPricerType().equalsIgnoreCase(PRICER_TYPE.SIMPLE.getName())) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<SimplePricer> c = cb.createQuery(SimplePricer.class);
			c.distinct(true);
			Root<SimplePricer> mt = c.from(SimplePricer.class);
			mt.fetch("material", JoinType.LEFT);
			Join<SimplePricer, BizUnit> bizUnit = mt.join("bizUnitBean", JoinType.LEFT);

			Join<SimplePricer, Material> material = mt.join("material", JoinType.LEFT);

			this.populateCriteriaForSimple(criteria, material, c, bizUnit, mt, null, null);
			TypedQuery<SimplePricer> q = em.createQuery(c);

			if (isOnline) {
				q.setFirstResult(first);

				q.setMaxResults(pageSize);
			}
			List<SimplePricer> searchResult = q.getResultList();
			pricerLst = convertSimplePricerToBeans(searchResult);
		}

		return pricerLst;
	}

	/**
	 * sreach pricer .
	 *
	 * @param criteria
	 *            the criteria
	 * @param isOnline
	 *            true:online false:offline
	 * @return the list
	 */
	@TransactionTimeout(value = 30000, unit = TimeUnit.SECONDS)
	public List<PricerUploadTemplateBean> pricerEnquiry(PricerUploadTemplateBean criteria, boolean isOnline) {

		List<PricerUploadTemplateBean> pricerLst = null;

		if (criteria.getPricerType().equalsIgnoreCase(PRICER_TYPE.NORMAL.getName())) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<NormalPricer> c = cb.createQuery(NormalPricer.class);
			c.distinct(true);
			Root<NormalPricer> mt = c.from(NormalPricer.class);
			mt.fetch("material", JoinType.LEFT);
			Join<NormalPricer, BizUnit> bizUnit = mt.join("bizUnitBean");

			Join<NormalPricer, Material> material = mt.join("material");
			populateCriteriaForNormal(criteria, material, c, bizUnit, mt);
			TypedQuery<NormalPricer> q = em.createQuery(c);
			if (isOnline) {
				q.setFirstResult(0);

				q.setMaxResults(MAX_RESULT_IN_PRICERINQUIRY);
			}

			List<NormalPricer> searchResult = q.getResultList();

			pricerLst = convertNorPricerToBeans(searchResult);
		} else if (criteria.getPricerType().equalsIgnoreCase(PRICER_TYPE.PROGRAM.getName())) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ProgramPricer> c = cb.createQuery(ProgramPricer.class);
			c.distinct(true);
			Root<ProgramPricer> mt = c.from(ProgramPricer.class);
			mt.fetch("material", JoinType.LEFT);
			Join<ProgramPricer, BizUnit> bizUnit = mt.join("bizUnitBean");

			Join<ProgramPricer, Material> material = mt.join("material");

			this.populateCriteriaForProgram(criteria, material, c, bizUnit, mt, null, null);
			TypedQuery<ProgramPricer> q = em.createQuery(c);

			if (isOnline) {
				q.setFirstResult(0);

				q.setMaxResults(MAX_RESULT_IN_PRICERINQUIRY);
			}
			List<ProgramPricer> searchResult = q.getResultList();
			pricerLst = convertPgPricerToBeans(searchResult);
		} else if (criteria.getPricerType().equalsIgnoreCase(PRICER_TYPE.CONTRACT.getName())) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ContractPricer> c = cb.createQuery(ContractPricer.class);
			c.distinct(true);
			Root<ContractPricer> mt = c.from(ContractPricer.class);
			mt.fetch("material", JoinType.LEFT);
			Join<ContractPricer, BizUnit> bizUnit = mt.join("bizUnitBean", JoinType.LEFT);

			Join<ContractPricer, Material> material = mt.join("material", JoinType.LEFT);

			this.populateCriteriaForContract(criteria, material, c, bizUnit, mt, null, null);
			TypedQuery<ContractPricer> q = em.createQuery(c);

			if (isOnline) {
				q.setFirstResult(0);

				q.setMaxResults(MAX_RESULT_IN_PRICERINQUIRY);
			}
			List<ContractPricer> searchResult = q.getResultList();
			pricerLst = convertCtrPricerToBeans(searchResult);
		} else if (criteria.getPricerType().equalsIgnoreCase(PRICER_TYPE.SALESCOST.getName())) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<SalesCostPricer> c = cb.createQuery(SalesCostPricer.class);
			c.distinct(true);
			Root<SalesCostPricer> mt = c.from(SalesCostPricer.class);
			mt.fetch("material", JoinType.LEFT);
			Join<SalesCostPricer, BizUnit> bizUnit = mt.join("bizUnitBean", JoinType.LEFT);

			Join<SalesCostPricer, Material> material = mt.join("material", JoinType.LEFT);

			this.populateCriteriaForSalesCost(criteria, material, c, bizUnit, mt, null, null);
			TypedQuery<SalesCostPricer> q = em.createQuery(c);

			if (isOnline) {
				q.setFirstResult(0);

				q.setMaxResults(MAX_RESULT_IN_PRICERINQUIRY);
			}
			List<SalesCostPricer> searchResult = q.getResultList();
			pricerLst = convertSalesCostPricerToBeans(searchResult);
		} else if (criteria.getPricerType().equalsIgnoreCase(PRICER_TYPE.SIMPLE.getName())) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<SimplePricer> c = cb.createQuery(SimplePricer.class);
			c.distinct(true);
			Root<SimplePricer> mt = c.from(SimplePricer.class);
			mt.fetch("material", JoinType.LEFT);
			Join<SimplePricer, BizUnit> bizUnit = mt.join("bizUnitBean", JoinType.LEFT);

			Join<SimplePricer, Material> material = mt.join("material", JoinType.LEFT);

			this.populateCriteriaForSimple(criteria, material, c, bizUnit, mt, null, null);
			TypedQuery<SimplePricer> q = em.createQuery(c);

			if (isOnline) {
				q.setFirstResult(0);

				q.setMaxResults(MAX_RESULT_IN_PRICERINQUIRY);
			}
			List<SimplePricer> searchResult = q.getResultList();
			pricerLst = convertSimplePricerToBeans(searchResult);
		}

		return pricerLst;

		// return this.pricerEnquiry(criteria, isOnline, 0,
		// MAX_RESULT_IN_PRICERINQUIRY);
	}

	@Asynchronous
	@TransactionTimeout(value = 30000, unit = TimeUnit.SECONDS)
	// NEC Pagination Changes: Method created to fetch records through
	// asynchronous call to database
	public void asynchSearch(PricerUploadTemplateBean criteria, boolean isOnline, int first, int pageSize,
			int pageNumber, ConcurrentHashMap<Integer, List<PricerUploadTemplateBean>> map, Queue<Integer> queue,
			int cachePageSize) {
		List<PricerUploadTemplateBean> pricerLst = null;

		if (criteria.getPricerType().equalsIgnoreCase(PRICER_TYPE.NORMAL.getName())) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<NormalPricer> c = cb.createQuery(NormalPricer.class);
			c.distinct(true);
			Root<NormalPricer> mt = c.from(NormalPricer.class);
			mt.fetch("material", JoinType.LEFT);
			Join<NormalPricer, BizUnit> bizUnit = mt.join("bizUnitBean");

			Join<NormalPricer, Material> material = mt.join("material");
			populateCriteriaForNormal(criteria, material, c, bizUnit, mt);
			TypedQuery<NormalPricer> q = em.createQuery(c);
			if (isOnline) {
				q.setFirstResult(first);

				q.setMaxResults(pageSize);
			}

			List<NormalPricer> searchResult = q.getResultList();

			pricerLst = convertNorPricerToBeans(searchResult);
		} else if (criteria.getPricerType().equalsIgnoreCase(PRICER_TYPE.PROGRAM.getName())) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ProgramPricer> c = cb.createQuery(ProgramPricer.class);
			c.distinct(true);
			Root<ProgramPricer> mt = c.from(ProgramPricer.class);
			mt.fetch("material", JoinType.LEFT);
			Join<ProgramPricer, BizUnit> bizUnit = mt.join("bizUnitBean");

			Join<ProgramPricer, Material> material = mt.join("material");

			this.populateCriteriaForProgram(criteria, material, c, bizUnit, mt, null, null);
			TypedQuery<ProgramPricer> q = em.createQuery(c);

			// NEC Pagination Changes: Sets numbers of records to be fetched for
			// previous/next page
			if (isOnline) {
				q.setFirstResult(first);

				q.setMaxResults(pageSize);
			}
			List<ProgramPricer> searchResult = q.getResultList();
			pricerLst = convertPgPricerToBeans(searchResult);
		} else if (criteria.getPricerType().equalsIgnoreCase(PRICER_TYPE.CONTRACT.getName())) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ContractPricer> c = cb.createQuery(ContractPricer.class);
			c.distinct(true);
			Root<ContractPricer> mt = c.from(ContractPricer.class);
			mt.fetch("material", JoinType.LEFT);
			Join<ContractPricer, BizUnit> bizUnit = mt.join("bizUnitBean", JoinType.LEFT);

			Join<ContractPricer, Material> material = mt.join("material", JoinType.LEFT);

			this.populateCriteriaForContract(criteria, material, c, bizUnit, mt, null, null);
			TypedQuery<ContractPricer> q = em.createQuery(c);

			// NEC Pagination Changes: Sets numbers of records to be fetched for
			// previous/next page
			if (isOnline) {
				q.setFirstResult(first);

				q.setMaxResults(pageSize);
			}
			List<ContractPricer> searchResult = q.getResultList();
			pricerLst = convertCtrPricerToBeans(searchResult);
		} else if (criteria.getPricerType().equalsIgnoreCase(PRICER_TYPE.SALESCOST.getName())) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<SalesCostPricer> c = cb.createQuery(SalesCostPricer.class);
			c.distinct(true);
			Root<SalesCostPricer> mt = c.from(SalesCostPricer.class);
			mt.fetch("material", JoinType.LEFT);
			Join<SalesCostPricer, BizUnit> bizUnit = mt.join("bizUnitBean", JoinType.LEFT);

			Join<SalesCostPricer, Material> material = mt.join("material", JoinType.LEFT);

			this.populateCriteriaForSalesCost(criteria, material, c, bizUnit, mt, null, null);
			TypedQuery<SalesCostPricer> q = em.createQuery(c);

			if (isOnline) {
				q.setFirstResult(first);

				q.setMaxResults(pageSize);
			}
			List<SalesCostPricer> searchResult = q.getResultList();
			pricerLst = convertSalesCostPricerToBeans(searchResult);
		} else if (criteria.getPricerType().equalsIgnoreCase(PRICER_TYPE.SIMPLE.getName())) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<SimplePricer> c = cb.createQuery(SimplePricer.class);
			c.distinct(true);
			Root<SimplePricer> mt = c.from(SimplePricer.class);
			mt.fetch("material", JoinType.LEFT);
			Join<SimplePricer, BizUnit> bizUnit = mt.join("bizUnitBean", JoinType.LEFT);

			Join<SimplePricer, Material> material = mt.join("material", JoinType.LEFT);

			this.populateCriteriaForSimple(criteria, material, c, bizUnit, mt, null, null);
			TypedQuery<SimplePricer> q = em.createQuery(c);

			if (isOnline) {
				q.setFirstResult(first);

				q.setMaxResults(pageSize);
			}
			List<SimplePricer> searchResult = q.getResultList();
			pricerLst = convertSimplePricerToBeans(searchResult);
		}

		// NEC Pagination Changes: Sets sequence number of records fetched for
		// current page
		for (int i = 1; i <= pricerLst.size(); i++) {
			if (null == pricerLst.get(i - 1) || pricerLst.get(i - 1).equals(""))
				break;
			else
				pricerLst.get(i - 1).setLineSeq(first + i);
		}

		if (pricerLst != null && pricerLst.size() > 0) {
			map.put(pageNumber, pricerLst);
			queue.add(pageNumber);
			if (cachePageSize < map.size()) {
				map.remove(queue.remove());
			}
		}
	}

	// NEC Pagination Changes: Calculates count of records to be fetched by lazy
	// loading
	//@TransactionAttribute(TransactionAttributeType.NEVER)
	public int count(PricerUploadTemplateBean criteria, boolean isOnline) {
		int count = 0;
		

		if (criteria.getPricerType().equalsIgnoreCase(PRICER_TYPE.NORMAL.getName())) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> c = cb.createQuery(Long.class);
			c.distinct(true);
			Root<NormalPricer> mt = c.from(NormalPricer.class);
			//mt.join(paramCollectionAttribute)
			//mt.fetch("material", JoinType.LEFT);
			Join<NormalPricer, BizUnit> bizUnit = mt.join("bizUnitBean");

			Join<NormalPricer, Material> material = mt.join("material");
			populateCriteriaForNormal(criteria, material, c, bizUnit, mt);

			c.select(cb.count(mt));
			long ct = System.currentTimeMillis();
			 
			
			count = em.createQuery(c).getSingleResult().intValue();
			LOG.config(() -> "em.createQuery(c).getSingleResult() take time " +(System.currentTimeMillis()-ct));

		} else if (criteria.getPricerType().equalsIgnoreCase(PRICER_TYPE.PROGRAM.getName())) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> c = cb.createQuery(Long.class);
			//c.distinct(true);
			Root<ProgramPricer> mt = c.from(ProgramPricer.class);
			//mt.fetch("material", JoinType.LEFT);
			Join<ProgramPricer, BizUnit> bizUnit = mt.join("bizUnitBean");

			Join<ProgramPricer, Material> material = mt.join("material");

			this.populateCriteriaForProgram(criteria, material, c, bizUnit, mt, null, null);
			c.select(cb.count(mt));
			count = em.createQuery(c).getSingleResult().intValue();

		} else if (criteria.getPricerType().equalsIgnoreCase(PRICER_TYPE.CONTRACT.getName())) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> c = cb.createQuery(Long.class);
			//c.distinct(true);
			Root<ContractPricer> mt = c.from(ContractPricer.class);
			//mt.fetch("material", JoinType.LEFT);// bizUnitBean
			Join<ContractPricer, BizUnit> bizUnit = mt.join("bizUnitBean", JoinType.LEFT);

			Join<ContractPricer, Material> material = mt.join("material", JoinType.LEFT);

			this.populateCriteriaForContract(criteria, material, c, bizUnit, mt, null, null);
			c.select(cb.count(mt));
			count = em.createQuery(c).getSingleResult().intValue();

		} else if (criteria.getPricerType().equalsIgnoreCase(PRICER_TYPE.SALESCOST.getName())) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> c = cb.createQuery(Long.class);
			//c.distinct(true);
			Root<SalesCostPricer> mt = c.from(SalesCostPricer.class);
			//mt.fetch("material", JoinType.LEFT);// bizUnitBean
			Join<SalesCostPricer, BizUnit> bizUnit = mt.join("bizUnitBean", JoinType.LEFT);

			Join<SalesCostPricer, Material> material = mt.join("material", JoinType.LEFT);

			this.populateCriteriaForSalesCost(criteria, material, c, bizUnit, mt, null, null);
			c.select(cb.count(mt));
			count = em.createQuery(c).getSingleResult().intValue();

		} else if (criteria.getPricerType().equalsIgnoreCase(PRICER_TYPE.SIMPLE.getName())) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> c = cb.createQuery(Long.class);
			//c.distinct(true);
			Root<SimplePricer> mt = c.from(SimplePricer.class);
			//mt.fetch("material", JoinType.LEFT);// bizUnitBean
			Join<SimplePricer, BizUnit> bizUnit = mt.join("bizUnitBean", JoinType.LEFT);

			Join<SimplePricer, Material> material = mt.join("material", JoinType.LEFT);

			this.populateCriteriaForSimple(criteria, material, c, bizUnit, mt, null, null);
			c.select(cb.count(mt));
			count = em.createQuery(c).getSingleResult().intValue();
		}

		return count;
	}

	/**
	 * Gets the pricer upload template data.
	 *
	 * @param criteria
	 *            the criteria
	 * @param isOnline
	 *            the is online
	 * @param searchResults
	 *            the search results
	 * @return the pricer upload template data
	 */
	public List<PricerUploadTemplateBean> getPricerUploadTemplateData(PricerUploadTemplateBean criteria,
			boolean isOnline, List searchResults) {

		List<PricerUploadTemplateBean> pricerLst = null;

		if (criteria.getPricerType().equalsIgnoreCase(PRICER_TYPE.NORMAL.getName())) {
			List<NormalPricer> searchResult = searchResults;
			pricerLst = convertNorPricerToBeans(searchResult);
		} else if (criteria.getPricerType().equalsIgnoreCase("Program")) {
			List<ProgramPricer> searchResult = searchResults;
			pricerLst = convertPgPricerToBeans(searchResult);
		} else {
			List<ContractPricer> searchResult = (List<ContractPricer>) searchResults;
			pricerLst = convertCtrPricerToBeans(searchResult);
		}

		return pricerLst;

	}

	/**
	 * Pagination: This Method populate the criteria according to the values in
	 * filters, also sort the fields of Data Table
	 * 
	 *
	 * @param criteria
	 *            the criteria
	 * @param material
	 *            the material
	 * @param c
	 *            the c
	 * @param bizUnit
	 *            the biz unit
	 * @param mt
	 *            the mt
	 * @param orderBy
	 *            the order by
	 * @param field
	 *            the field
	 * @parma criteria
	 * @parma material
	 * @parma c
	 * @parma bizUnit
	 */

	private void populateCriteriaForNormal(PricerUploadTemplateBean criteria, Join<NormalPricer, Material> material,
			CriteriaQuery c, Join<NormalPricer, BizUnit> bizUnit, Root<NormalPricer> mt) {
		List<Predicate> criterias = new ArrayList<Predicate>();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		Predicate p1 = null;
		Join<Material, Manufacturer> manufacturer = material.join("manufacturer");

		// David
		Join<Material, MaterialRegional> mRegional = material.join("materialRegionals");
		if (!QuoteUtil.isEmpty(criteria.getRegion())) {
			Join<MaterialRegional, BizUnit> mBizUnit = mRegional.join("bizUnit");
			p1 = cb.equal(mBizUnit.<String>get("name"), criteria.getRegion());
			criterias.add(p1);
		}

		// Added to create query to compare criteria to DataTable
		if (!QuoteUtil.isEmpty(criteria.getMfr())) {
			p1 = cb.equal(manufacturer.<String>get("name"), criteria.getMfr().toUpperCase());
			criterias.add(p1);
		}
		if (!QuoteUtil.isEmpty(criteria.getMfrName())) {
			p1 = cb.like(cb.upper(manufacturer.<String>get("name")), "%" + criteria.getMfrName().toUpperCase() + "%");
			criterias.add(p1);
		}
		if (!QuoteUtil.isEmpty(criteria.getFullMfrPart())) {
			p1 = cb.like(cb.upper(material.<String>get("fullMfrPartNumber")),
					"%" + criteria.getFullMfrPart().toUpperCase() + "%");
			criterias.add(p1);
		}
		if (!QuoteUtil.isEmpty(criteria.getCurrency()) 
				&& !QuoteSBConstant.WILDCARD_ALL.equals(criteria.getCurrency())) {
			p1 = cb.equal(mt.<Currency>get("currency"), Currency.valueOf(criteria.getCurrency()));
			criterias.add(p1);
		}
		/*if (!QuoteUtil.isEmpty(criteria.getCost())) {
			p1 = cb.equal(mt.<Double>get("cost"), Double.parseDouble(criteria.getCost()));
			criterias.add(p1);
		}*/
		//TODO MAYBE WILL BE ADDED IN FUTURE
		/*if (!QuoteUtil.isEmpty(criteria.getProductCat())) {
			Join<MaterialRegional, ProductGroup> productCa = mRegional.join("productCategory");
			p1 = cb.like(cb.upper(productCa.<String>get("categoryCode")),
					"%" + criteria.getProductCat().toUpperCase() + "%");
			criterias.add(p1);
		}*/
		/**
		 * SALESCOSTFLAG
		 ****/
		if (!QuoteUtil.isEmpty(criteria.getSalesCostFlag())
				&& !criteria.getSalesCostFlag().equals(QuoteSBConstant.OPTION_ALL)) {
			p1 = cb.equal(mRegional.<String>get("salesCostFlag"),
					criteria.getSalesCostFlag().equals(QuoteSBConstant.OPTION_YES) ? true : false);
			criterias.add(p1);
		}

		if (!QuoteUtil.isEmpty(criteria.getMinSell())) {
			p1 = cb.equal(mt.<Double>get("minSell"), Double.parseDouble(criteria.getMinSell()));
			criterias.add(p1);
		}
		if (!QuoteUtil.isEmpty(criteria.getBottomPrice())) {
			p1 = cb.equal(mt.<Double>get("minSell"), Double.parseDouble(criteria.getBottomPrice()));
			criterias.add(p1);
		}
		if (!QuoteUtil.isEmpty(criteria.getShipmentValidity())) {
			Calendar calendarFrom = null;
			Calendar calendarTo = null;
			try {
				Date dateFrom = QuoteUtil.convertStringToDate((String) criteria.getShipmentValidity(), "dd/MM/yyyy");
				calendarFrom = Calendar.getInstance();
				calendarFrom.setTime(dateFrom);
				calendarFrom.set(Calendar.HOUR, 00);
				calendarFrom.set(Calendar.MINUTE, 00);
				calendarFrom.set(Calendar.SECOND, 00);
				Date dateTo = (Date) dateFrom.clone();
				calendarTo = Calendar.getInstance();
				calendarTo.setTime(dateTo);
				calendarTo.set(Calendar.HOUR, 23);
				calendarTo.set(Calendar.MINUTE, 59);
				calendarTo.set(Calendar.SECOND, 59);
			} catch (ParseException e) {
				LOG.log(Level.WARNING,
						"Exception while parsing shipment validity date: " + (String) criteria.getShipmentValidity());
			}
			p1 = cb.between(mt.<Date>get("shipmentValidity"), calendarFrom.getTime(), calendarTo.getTime());
			criterias.add(cb.or(p1));

		}
		if (!QuoteUtil.isEmpty(criteria.getAvnetQuoteCentreComments())) {
			p1 = cb.like(cb.upper(material.<String>get("avnetQcComments")),
					"%" + criteria.getAvnetQuoteCentreComments().toUpperCase() + "%");
			criterias.add(p1);
		}
		if (!QuoteUtil.isEmpty(criteria.getMPQ())) {
			p1 = cb.equal(mt.<Integer>get("mpq"), Integer.parseInt(criteria.getMPQ()));
			criterias.add(p1);
		}
		if (!QuoteUtil.isEmpty(criteria.getMOQ())) {
			p1 = cb.equal(mt.<Integer>get("moq"), Integer.parseInt(criteria.getMOQ()));
			criterias.add(p1);
		}
		if (!QuoteUtil.isEmpty(criteria.getMOV())) {
			p1 = cb.equal(mt.<Integer>get("mov"), Integer.parseInt(criteria.getMOV()));
			criterias.add(p1);
		}
		if (!QuoteUtil.isEmpty(criteria.getLastUpdatedBy())) {
			p1 = cb.equal(mt.<Integer>get("lastUpdatedBy"), Integer.parseInt(criteria.getLastUpdatedBy()));
			criterias.add(p1);
		}
		if (!QuoteUtil.isEmpty(criteria.getLeadTime())) {
			p1 = cb.like(cb.upper(mt.<String>get("leadTime")), "%" + criteria.getLeadTime().toUpperCase() + "%");
			criterias.add(p1);
		}
		if (!QuoteUtil.isEmpty(criteria.getQuotationEffectiveDate())) {
			// p1 = cb.like(cb.upper(material.<String>
			// get("quotationEffectiveDate")), "%" +
			// criteria.getQuotationEffectiveDate().toUpperCase()+"%");
			// criterias.add(p1);
			Calendar calendarFrom = null;
			Calendar calendarTo = null;
			try {
				Date dateFrom = QuoteUtil.convertStringToDate((String) criteria.getQuotationEffectiveDate(),
						"dd/MM/yyyy");
				calendarFrom = Calendar.getInstance();
				calendarFrom.setTime(dateFrom);
				calendarFrom.set(Calendar.HOUR, 00);
				calendarFrom.set(Calendar.MINUTE, 00);
				calendarFrom.set(Calendar.SECOND, 00);
				Date dateTo = (Date) dateFrom.clone();
				calendarTo = Calendar.getInstance();
				calendarTo.setTime(dateTo);
				calendarTo.set(Calendar.HOUR, 23);
				calendarTo.set(Calendar.MINUTE, 59);
				calendarTo.set(Calendar.SECOND, 59);
			} catch (ParseException e) {
				LOG.log(Level.WARNING,
						"Exception while parsing quotation effective date: " + (String) criteria.getShipmentValidity());
			}
			p1 = cb.between(mt.<Date>get("quotationEffectiveDate"), calendarFrom.getTime(), calendarTo.getTime());
			criterias.add(cb.or(p1));
		}
		if (!QuoteUtil.isEmpty(criteria.getLastUpdatedOn())) {
			// p1 = cb.like(cb.upper(material.<String>
			// get("quotationEffectiveDate")), "%" +
			// criteria.getQuotationEffectiveDate().toUpperCase()+"%");
			// criterias.add(p1);

			try {
				Calendar calendarFrom = null;
				Calendar calendarTo = null;

				Date dateFrom = QuoteUtil.convertStringToDate((String) criteria.getLastUpdatedOn(), "dd/MM/yyyy");
				calendarFrom = Calendar.getInstance();
				calendarFrom.setTime(dateFrom);
				calendarFrom.set(Calendar.HOUR, 00);
				calendarFrom.set(Calendar.MINUTE, 00);
				calendarFrom.set(Calendar.SECOND, 00);
				Date dateTo = (Date) dateFrom.clone();
				calendarTo = Calendar.getInstance();
				calendarTo.setTime(dateTo);
				calendarTo.set(Calendar.HOUR, 23);
				calendarTo.set(Calendar.MINUTE, 59);
				calendarTo.set(Calendar.SECOND, 59);

				p1 = cb.between(mt.<Date>get("lastUpdatedOn"), calendarFrom.getTime(), calendarTo.getTime());
				criterias.add(cb.or(p1));

			} catch (ParseException e) {
				LOG.log(Level.WARNING,
						"Exception while parsing last updated on date: " + (String) criteria.getShipmentValidity());
			}

		}
		if (!QuoteUtil.isEmpty(criteria.getAvnetQuoteCentreComments())) {
			p1 = cb.like(cb.upper(material.<String>get("avnetQcComments")),
					"%" + criteria.getAvnetQuoteCentreComments().toUpperCase() + "%");
			criterias.add(p1);
		}
		if (!QuoteUtil.isEmpty(criteria.getDescription())) {
			p1 = cb.like(cb.upper(mt.<String>get("description")), "%" + criteria.getDescription().toUpperCase() + "%");
			criterias.add(p1);
		}
		if (!QuoteUtil.isEmpty(criteria.getValidity())) {
			p1 = cb.equal(mt.<String>get("priceValidity"), criteria.getValidity());
			criterias.add(cb.or(p1));
		}
		// p1 = cb.equal(bizUnit.<String> get("name"),
		// criteria.getBizUnit().getName());
		p1 = cb.equal(bizUnit.<String>get("name"), criteria.getRegion());
		criterias.add(p1);

		if (!QuoteUtil.isEmpty(criteria.getCostIndicator())) {
			Join<NormalPricer, CostIndicator> costIndicator = mt.join("costIndicator");
			p1 = cb.like(cb.upper(costIndicator.<String>get("name")),
					"%" + criteria.getCostIndicator().toUpperCase() + "%");
			criterias.add(p1);
		}
		// Added by Punit for CPD Exercise
		criteria = populateCommonCriteriaForPricer(criteria, criterias, cb, p1, mt.<Date>get("quotationEffectiveDate"),
				mt.<Date>get("quotationEffectiveTo"), mt.<Date>get("shipmentValidity"), mt.get("shipmentValidity"));

		if (!QuoteUtil.isEmpty(criteria.getTermsAndConditions())) {
			p1 = cb.like(mt.<String>get("termsAndConditions"), "%" + criteria.getTermsAndConditions() + "%");
			criterias.add(p1);
		}
		if (!QuoteUtil.isEmpty(criteria.getProductGroup1())) {
			Join<MaterialRegional, ProductGroup> productGroup1 = mRegional.join("productGroup1");
			p1 = cb.equal(productGroup1.<String>get("name"), criteria.getProductGroup1());
			criterias.add(p1);
		}

		if (!QuoteUtil.isEmpty(criteria.getProductGroup2())) {
			Join<MaterialRegional, ProductGroup> productGroup2 = mRegional.join("productGroup2");
			p1 = cb.like(cb.upper(productGroup2.<String>get("name")), "%" + criteria.getProductGroup2() + "%");
			criterias.add(p1);
		}

		if (!QuoteUtil.isEmpty(criteria.getProductGroup3())) {
			p1 = cb.like(mRegional.<String>get("productGroup3"), "%" + criteria.getProductGroup3() + "%");
			criterias.add(p1);
		}
		if (!QuoteUtil.isEmpty(criteria.getProductGroup4())) {
			p1 = cb.like(mRegional.<String>get("productGroup4"), "%" + criteria.getProductGroup4() + "%");
			criterias.add(p1);
		}
		/*
		 * if (!QuoteUtil.isEmpty(criteria.getProductGroup1())) { Join<Material,
		 * ProductGroup> productGroup1 = material.join("productGroup1"); p1 =
		 * cb.equal(productGroup1.<String> get("name"),
		 * criteria.getProductGroup1()); criterias.add(p1); }
		 * 
		 * if (!QuoteUtil.isEmpty(criteria.getProductGroup2())) { Join<Material,
		 * ProductGroup> productGroup2 = material.join("productGroup2"); p1 =
		 * cb.like(cb.upper(productGroup2.<String> get("name")), "%" +
		 * criteria.getProductGroup2() + "%"); criterias.add(p1); }
		 * 
		 * if (!QuoteUtil.isEmpty(criteria.getProductGroup3())) { p1 =
		 * cb.like(material.<String> get("productGroup3"), "%" +
		 * criteria.getProductGroup3() + "%"); criterias.add(p1); } if
		 * (!QuoteUtil.isEmpty(criteria.getProductGroup4())) { p1 =
		 * cb.like(material.<String> get("productGroup4"), "%" +
		 * criteria.getProductGroup4() + "%"); criterias.add(p1); }
		 */

		if (!QuoteUtil.isEmpty(criteria.getValidity())) {
			p1 = cb.equal(mt.<String>get("priceValidity"), criteria.getValidity());
			criterias.add(p1);
		}

		if (!QuoteUtil.isEmpty(criteria.getPriceListRemarks())) {
			p1 = cb.like(mt.<String>get("priceListRemarks1"), "%" + criteria.getPriceListRemarks() + "%");
			criterias.add(p1);
		}
		if (!QuoteUtil.isEmpty(criteria.getPriceListRemarks2())) {
			p1 = cb.like(mt.<String>get("priceListRemarks2"), "%" + criteria.getPriceListRemarks2() + "%");
			criterias.add(p1);
		}
		if (!QuoteUtil.isEmpty(criteria.getPriceListRemarks3())) {
			p1 = cb.like(mt.<String>get("priceListRemarks3"), "%" + criteria.getPriceListRemarks3() + "%");
			criterias.add(p1);
		}
		if (!QuoteUtil.isEmpty(criteria.getPriceListRemarks4())) {
			p1 = cb.like(mt.<String>get("priceListRemarks4"), "%" + criteria.getPriceListRemarks4() + "%");
			criterias.add(p1);
		}

		if (criterias.size() == 0) {

		} else if (criterias.size() == 1) {
			c.where(criterias.get(0));
		} else {
			c.where(cb.and(criterias.toArray(new Predicate[0])));
		}
	}

	/**
	 * Pagination: This Method populate the criteria according to the values in
	 * filters
	 * 
	 *
	 * @param criteria
	 *            the criteria
	 * @param material
	 *            the material
	 * @param c
	 *            the c
	 * @param bizUnit
	 *            the biz unit
	 * @param mt
	 *            the mt
	 * @param orderBy
	 *            the order by
	 * @param field
	 *            the field
	 * @parma criteria
	 * @parma material
	 * @parma c
	 * @parma bizUnit
	 */
	private void populateCriteriaForProgram(PricerUploadTemplateBean criteria, Join<ProgramPricer, Material> material,
			CriteriaQuery c, Join<ProgramPricer, BizUnit> bizUnit, Root<ProgramPricer> mt, String orderBy,
			String field) {
		List<Predicate> criterias = new ArrayList<Predicate>();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		Predicate p1 = null;
		// David
		Join<Material, MaterialRegional> mRegional = material.join("materialRegionals");
		if (!QuoteUtil.isEmpty(criteria.getRegion())) {
			Join<MaterialRegional, BizUnit> mBizUnit = mRegional.join("bizUnit");
			p1 = cb.equal(mBizUnit.<String>get("name"), criteria.getRegion());
			criterias.add(p1);
		}

		if (!QuoteUtil.isEmpty(criteria.getMfr())) {
			Join<Material, Manufacturer> manufacturer = material.join("manufacturer");
			p1 = cb.equal(manufacturer.<String>get("name"), criteria.getMfr().toUpperCase());
			criterias.add(p1);
		}
		if (!QuoteUtil.isEmpty(criteria.getFullMfrPart())) {
			p1 = cb.like(material.<String>get("fullMfrPartNumber"),
					"%" + criteria.getFullMfrPart().toUpperCase() + "%");
			criterias.add(p1);
		}
		
		if (!QuoteUtil.isEmpty(criteria.getCurrency()) 
				&& !QuoteSBConstant.WILDCARD_ALL.equals(criteria.getCurrency())) {
			p1 = cb.equal(mt.<Currency>get("currency"), Currency.valueOf(criteria.getCurrency()));
			criterias.add(p1);
		}
		
		/**
		 * SALESCOSTFLAG
		 ****/
		if (!QuoteUtil.isEmpty(criteria.getSalesCostFlag())
				&& !criteria.getSalesCostFlag().equals(QuoteSBConstant.OPTION_ALL)) {
			p1 = cb.equal(mRegional.<String>get("salesCostFlag"),
					criteria.getSalesCostFlag().equals(QuoteSBConstant.OPTION_YES) ? true : false);
			criterias.add(p1);
		}
		// p1 = cb.equal(bizUnit.<String> get("name"),
		// criteria.getBizUnit().getName());
		p1 = cb.equal(bizUnit.<String>get("name"), criteria.getRegion());
		criterias.add(p1);

		if (!QuoteUtil.isEmpty(criteria.getCostIndicator())) {
			Join<ProgramPricer, CostIndicator> costIndicator = mt.join("costIndicator");
			p1 = cb.equal(costIndicator.<String>get("name"), criteria.getCostIndicator());
			criterias.add(p1);
		}

		if (!QuoteUtil.isEmpty(criteria.getValidity())) {

			p1 = cb.equal(mt.<String>get("priceValidity"), criteria.getValidity());

			criterias.add(p1);
		}

		Date quotationEffectiveDateFrom = criteria.getQuotationEffectiveDateFrom();
		Date quotationEffectiveDateTo = criteria.getQuotationEffectiveDateTo();
		if (criteria.getQuotationEffectiveDateFrom() != null || criteria.getQuotationEffectiveDateTo() != null) {
			if (criteria.getQuotationEffectiveDateFrom() == null) {// dd/MM/yyyy
				quotationEffectiveDateFrom = PricerUtils.convertToDate("01/01/1900");
			}

			if (criteria.getQuotationEffectiveDateTo() == null) {
				quotationEffectiveDateTo = PricerUtils.convertToDate("01/01/2100");
			}
			quotationEffectiveDateFrom = PricerUtils.getTimesmorning(quotationEffectiveDateFrom);
			quotationEffectiveDateTo = PricerUtils.getTimesnight(quotationEffectiveDateTo);
			List<Predicate> qedcriterias = new ArrayList<Predicate>();
			Predicate qedFrom = cb.greaterThanOrEqualTo(mt.<Date>get("quotationEffectiveDate"),
					quotationEffectiveDateFrom);

			Predicate qedTo = cb.lessThanOrEqualTo(mt.<Date>get("quotationEffectiveDate"), quotationEffectiveDateTo);
			Predicate qedPredicate = cb.and(qedFrom, qedTo);
			qedcriterias.add(qedPredicate);

			Predicate qetForm = cb.greaterThanOrEqualTo(mt.<Date>get("quotationEffectiveTo"),
					quotationEffectiveDateFrom);

			Predicate qetTo = cb.lessThanOrEqualTo(mt.<Date>get("quotationEffectiveTo"), quotationEffectiveDateTo);
			Predicate qetPredicate = cb.and(qetForm, qetTo);
			qedcriterias.add(qetPredicate);

			Predicate qetForm2 = cb.lessThanOrEqualTo(mt.<Date>get("quotationEffectiveDate"),
					quotationEffectiveDateFrom);

			Predicate qetTo2 = cb.greaterThanOrEqualTo(mt.<Date>get("quotationEffectiveTo"), quotationEffectiveDateTo);
			Predicate qetPredicate2 = cb.and(qetForm2, qetTo2);
			qedcriterias.add(qetPredicate2);
			criterias.add(cb.or(qedcriterias.toArray(new Predicate[0])));
		}
		List<Predicate> shipmentValiditycriterias = new ArrayList<Predicate>();
		Predicate isNUllPredicate = cb.isNull(mt.get("shipmentValidity"));

		Predicate svForm1 = null;
		Predicate svTo1 = null;
		;
		if (criteria.getShipmentValidityFrom() != null) {
			svForm1 = cb.greaterThanOrEqualTo(mt.<Date>get("shipmentValidity"),
					PricerUtils.getTimesmorning(criteria.getShipmentValidityFrom()));

			if (criteria.getShipmentValidityTo() != null) {
				svTo1 = cb.lessThanOrEqualTo(mt.<Date>get("shipmentValidity"),
						PricerUtils.getTimesnight(criteria.getShipmentValidityTo()));
				Predicate qetPredicate2 = cb.and(svForm1, svTo1);
				shipmentValiditycriterias.add(isNUllPredicate);
				shipmentValiditycriterias.add(qetPredicate2);
			} else {
				shipmentValiditycriterias.add(isNUllPredicate);
				shipmentValiditycriterias.add(svForm1);
			}
		} else {
			if (criteria.getShipmentValidityTo() != null) {
				svTo1 = cb.lessThanOrEqualTo(mt.<Date>get("shipmentValidity"),
						PricerUtils.getTimesnight(criteria.getShipmentValidityTo()));
				shipmentValiditycriterias.add(svTo1);
				shipmentValiditycriterias.add(isNUllPredicate);
			}
		}
		// added for can’t search out records if condition is that both
		// “Shipment Validity From “and “Shipment Validity to “ is empty by
		// damon@20161226
		if (shipmentValiditycriterias.size() > 0) {
			// shipmentValiditycriterias.add(isNUllPredicate);
			criterias.add(cb.or(shipmentValiditycriterias.toArray(new Predicate[0])));

		}

		if (!QuoteUtil.isEmpty(criteria.getTermsAndConditions())) {
			p1 = cb.like(mt.<String>get("termsAndConditions"), "%" + criteria.getTermsAndConditions() + "%");
			criterias.add(p1);
		}

		if (!QuoteUtil.isEmpty(criteria.getAvnetQuoteCentreComments())) {
			p1 = cb.like(mt.<String>get("avnetQcComments"), "%" + criteria.getAvnetQuoteCentreComments() + "%");
			criterias.add(p1);
		}

		if (!QuoteUtil.isEmpty(criteria.getProgram())) {
			Join<ProgramPricer, ProgramType> programType = mt.join("programType");
			p1 = cb.like(programType.<String>get("name"), criteria.getProgram());
			criterias.add(p1);
		}

		if (criterias.size() == 0) {

		} else if (criterias.size() == 1) {
			c.where(criterias.get(0));
		} else {
			c.where(cb.and(criterias.toArray(new Predicate[0])));
		}

	}

	/**
	 * Populate criteria for contract.
	 *
	 * @param criteria
	 *            the criteria
	 * @param material
	 *            the material
	 * @param c
	 *            the c
	 * @param bizUnit
	 *            the biz unit
	 * @param mt
	 *            the mt
	 * @param orderBy
	 *            the order by
	 * @param field
	 *            the field
	 */
	private void populateCriteriaForContract(PricerUploadTemplateBean criteria, Join<ContractPricer, Material> material,
			CriteriaQuery c, Join<ContractPricer, BizUnit> bizUnit, Root<ContractPricer> mt, String orderBy,
			String field) {
		List<Predicate> criterias = new ArrayList<Predicate>();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		Predicate p1 = null;
		// David
		Join<Material, MaterialRegional> mRegional = material.join("materialRegionals");
		if (!QuoteUtil.isEmpty(criteria.getRegion())) {
			Join<MaterialRegional, BizUnit> mBizUnit = mRegional.join("bizUnit");
			p1 = cb.equal(mBizUnit.<String>get("name"), criteria.getRegion());
			criterias.add(p1);
		}

		if (!QuoteUtil.isEmpty(criteria.getMfr())) {
			Join<Material, Manufacturer> manufacturer = material.join("manufacturer");
			p1 = cb.equal(manufacturer.<String>get("name"), criteria.getMfr().toUpperCase());
			criterias.add(p1);
		}
		if (!QuoteUtil.isEmpty(criteria.getFullMfrPart())) {
			p1 = cb.like(material.<String>get("fullMfrPartNumber"),
					"%" + criteria.getFullMfrPart().toUpperCase() + "%");
			criterias.add(p1);
		}
		
		if (!QuoteUtil.isEmpty(criteria.getCurrency()) 
				&& !QuoteSBConstant.WILDCARD_ALL.equals(criteria.getCurrency())) {
			p1 = cb.equal(mt.<Currency>get("currency"), Currency.valueOf(criteria.getCurrency()));
			criterias.add(p1);
		}

		/**
		 * SALESCOSTFLAG
		 ****/
		if (!QuoteUtil.isEmpty(criteria.getSalesCostFlag())
				&& !criteria.getSalesCostFlag().equals(QuoteSBConstant.OPTION_ALL)) {
			p1 = cb.equal(mRegional.<String>get("salesCostFlag"),
					criteria.getSalesCostFlag().equals(QuoteSBConstant.OPTION_YES) ? true : false);
			criterias.add(p1);
		}

		// p1 = cb.equal(bizUnit.<String> get("name"),
		// criteria.getBizUnit().getName());
		p1 = cb.equal(bizUnit.<String>get("name"), criteria.getRegion());
		criterias.add(p1);

		if (!QuoteUtil.isEmpty(criteria.getCostIndicator())) {
			Join<NormalPricer, CostIndicator> costIndicator = mt.join("costIndicator");
			p1 = cb.equal(costIndicator.<String>get("name"), criteria.getCostIndicator());
			criterias.add(p1);
		}
		// Added by Punit for CPD Exercise
		criteria = populateCommonCriteriaForPricer(criteria, criterias, cb, p1, mt.<Date>get("quotationEffectiveDate"),
				mt.<Date>get("quotationEffectiveTo"), mt.<Date>get("shipmentValidity"), mt.get("shipmentValidity"));
		if (!QuoteUtil.isEmpty(criteria.getMinSell())) {
			p1 = cb.like(mt.<String>get("normSell"), criteria.getMinSell());
			criterias.add(p1);
		}

		if (!QuoteUtil.isEmpty(criteria.getValidity())) {
			p1 = cb.equal(mt.<String>get("validity"), criteria.getValidity());
			criterias.add(p1);
		}

		if (!QuoteUtil.isEmpty(criteria.getSoldToCode())) {
			Join<ContractPricer, Customer> soldtoCustomer = mt.join("soldtoCustomer");
			p1 = cb.like(soldtoCustomer.<String>get("customerNumber"), "%" + criteria.getSoldToCode() + "%");
			criterias.add(p1);
		}
		if (!QuoteUtil.isEmpty(criteria.getCustomerName())) {
			Join<ContractPricer, Customer> soldtoCustomer = mt.join("soldtoCustomer");
			Expression<String> expBlank = cb.literal(" ");
			p1 = cb.like(cb.concat(
					cb.concat(
							cb.concat(cb.concat(soldtoCustomer.<String>get("customerName1"), expBlank),
									cb.concat(soldtoCustomer.<String>get("customerName2"), expBlank)),
							cb.concat(soldtoCustomer.<String>get("customerName3"), expBlank)),
					soldtoCustomer.<String>get("customerName4")), "%" + criteria.getCustomerName() + "%");

			criterias.add(p1);
		}
		if (!QuoteUtil.isEmpty(criteria.getEndCustomerCode())) {
			Join<ContractPricer, Customer> endCustomer = mt.join("endCustomer");
			p1 = cb.like(endCustomer.<String>get("customerNumber"), "%" + criteria.getEndCustomerCode() + "%");
			criterias.add(p1);
		}
		if (!QuoteUtil.isEmpty(criteria.getEndCustomerName())) {
			p1 = cb.like(mt.<String>get("endCustomerName"), "%" + criteria.getEndCustomerName() + "%");
			criterias.add(p1);
		}

		if (criterias.size() == 0) {

		} else if (criterias.size() == 1) {
			c.where(criterias.get(0));
		} else {
			c.where(cb.and(criterias.toArray(new Predicate[0])));
		}
	}

	/**
	 * Pagination: This Method populate the criteria according to the values in
	 * filters
	 * 
	 *
	 * @param criteria
	 *            the criteria
	 * @param material
	 *            the material
	 * @param c
	 *            the c
	 * @param bizUnit
	 *            the biz unit
	 * @param mt
	 *            the mt
	 * @param orderBy
	 *            the order by
	 * @param field
	 *            the field
	 * @parma criteria
	 * @parma material
	 * @parma c
	 * @parma bizUnit
	 */
	private void populateCriteriaForSalesCost(PricerUploadTemplateBean criteria,
			Join<SalesCostPricer, Material> material, CriteriaQuery c, Join<SalesCostPricer, BizUnit> bizUnit,
			Root<SalesCostPricer> mt, String orderBy, String field) {
		List<Predicate> criterias = new ArrayList<Predicate>();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		Predicate p1 = null;

		Join<Material, MaterialRegional> mRegional = material.join("materialRegionals");
		if (!QuoteUtil.isEmpty(criteria.getRegion())) {
			Join<MaterialRegional, BizUnit> mBizUnit = mRegional.join("bizUnit");
			p1 = cb.equal(mBizUnit.<String>get("name"), criteria.getRegion());
			criterias.add(p1);
		}

		if (!QuoteUtil.isEmpty(criteria.getSalesCostType())) {
			p1 = cb.like(material.<String>get("salesCostType"), "%" + criteria.getSalesCostType().toUpperCase() + "%");
			criterias.add(p1);
		}
		if (!QuoteUtil.isEmpty(criteria.getMfr())) {
			Join<Material, Manufacturer> manufacturer = material.join("manufacturer");
			p1 = cb.equal(manufacturer.<String>get("name"), criteria.getMfr().toUpperCase());
			criterias.add(p1);
		}
		if (!QuoteUtil.isEmpty(criteria.getFullMfrPart())) {
			p1 = cb.like(material.<String>get("fullMfrPartNumber"),
					"%" + criteria.getFullMfrPart().toUpperCase() + "%");
			criterias.add(p1);
		}
		
		if (!QuoteUtil.isEmpty(criteria.getCurrency()) 
				&& !QuoteSBConstant.WILDCARD_ALL.equals(criteria.getCurrency())) {
			p1 = cb.equal(mt.<Currency>get("currency"), Currency.valueOf(criteria.getCurrency()));
			criterias.add(p1);
		}
		/**
		 * SALESCOSTFLAG
		 ****/
		if (!QuoteUtil.isEmpty(criteria.getSalesCostFlag())
				&& !criteria.getSalesCostFlag().equals(QuoteSBConstant.OPTION_ALL)) {
			p1 = cb.equal(mRegional.<String>get("salesCostFlag"),
					criteria.getSalesCostFlag().equals(QuoteSBConstant.OPTION_YES) ? true : false);
			criterias.add(p1);
		}

		p1 = cb.equal(bizUnit.<String>get("name"), criteria.getRegion());
		criterias.add(p1);

		if (!QuoteUtil.isEmpty(criteria.getCostIndicator())) {
			Join<SalesCostPricer, CostIndicator> costIndicator = mt.join("costIndicator");
			p1 = cb.equal(costIndicator.<String>get("name"), criteria.getCostIndicator());
			criterias.add(p1);
		}

		if (!QuoteUtil.isEmpty(criteria.getValidity())) {

			p1 = cb.equal(mt.<String>get("priceValidity"), criteria.getValidity());

			criterias.add(p1);
		}

		Date quotationEffectiveDateFrom = criteria.getQuotationEffectiveDateFrom();
		Date quotationEffectiveDateTo = criteria.getQuotationEffectiveDateTo();
		if (criteria.getQuotationEffectiveDateFrom() != null || criteria.getQuotationEffectiveDateTo() != null) {
			if (criteria.getQuotationEffectiveDateFrom() == null) {// dd/MM/yyyy
				quotationEffectiveDateFrom = PricerUtils.convertToDate("01/01/1900");
			}

			if (criteria.getQuotationEffectiveDateTo() == null) {
				quotationEffectiveDateTo = PricerUtils.convertToDate("01/01/2100");
			}
			quotationEffectiveDateFrom = PricerUtils.getTimesmorning(quotationEffectiveDateFrom);
			quotationEffectiveDateTo = PricerUtils.getTimesnight(quotationEffectiveDateTo);
			List<Predicate> qedcriterias = new ArrayList<Predicate>();
			Predicate qedFrom = cb.greaterThanOrEqualTo(mt.<Date>get("quotationEffectiveDate"),
					quotationEffectiveDateFrom);

			Predicate qedTo = cb.lessThanOrEqualTo(mt.<Date>get("quotationEffectiveDate"), quotationEffectiveDateTo);
			Predicate qedPredicate = cb.and(qedFrom, qedTo);
			qedcriterias.add(qedPredicate);

			Predicate qetForm = cb.greaterThanOrEqualTo(mt.<Date>get("quotationEffectiveTo"),
					quotationEffectiveDateFrom);

			Predicate qetTo = cb.lessThanOrEqualTo(mt.<Date>get("quotationEffectiveTo"), quotationEffectiveDateTo);
			Predicate qetPredicate = cb.and(qetForm, qetTo);
			qedcriterias.add(qetPredicate);

			Predicate qetForm2 = cb.lessThanOrEqualTo(mt.<Date>get("quotationEffectiveDate"),
					quotationEffectiveDateFrom);

			Predicate qetTo2 = cb.greaterThanOrEqualTo(mt.<Date>get("quotationEffectiveTo"), quotationEffectiveDateTo);
			Predicate qetPredicate2 = cb.and(qetForm2, qetTo2);
			qedcriterias.add(qetPredicate2);
			criterias.add(cb.or(qedcriterias.toArray(new Predicate[0])));
		}
		List<Predicate> shipmentValiditycriterias = new ArrayList<Predicate>();
		Predicate isNUllPredicate = cb.isNull(mt.get("shipmentValidity"));

		Predicate svForm1 = null;
		Predicate svTo1 = null;
		;
		if (criteria.getShipmentValidityFrom() != null) {
			svForm1 = cb.greaterThanOrEqualTo(mt.<Date>get("shipmentValidity"),
					PricerUtils.getTimesmorning(criteria.getShipmentValidityFrom()));

			if (criteria.getShipmentValidityTo() != null) {
				svTo1 = cb.lessThanOrEqualTo(mt.<Date>get("shipmentValidity"),
						PricerUtils.getTimesnight(criteria.getShipmentValidityTo()));
				Predicate qetPredicate2 = cb.and(svForm1, svTo1);
				shipmentValiditycriterias.add(isNUllPredicate);
				shipmentValiditycriterias.add(qetPredicate2);
			} else {
				shipmentValiditycriterias.add(isNUllPredicate);
				shipmentValiditycriterias.add(svForm1);
			}
		} else {
			if (criteria.getShipmentValidityTo() != null) {
				svTo1 = cb.lessThanOrEqualTo(mt.<Date>get("shipmentValidity"),
						PricerUtils.getTimesnight(criteria.getShipmentValidityTo()));
				shipmentValiditycriterias.add(svTo1);
				shipmentValiditycriterias.add(isNUllPredicate);
			}
		}
		// added for can’t search out records if condition is that both
		// “Shipment Validity From “and “Shipment Validity to “ is empty by
		// damon@20161226
		if (shipmentValiditycriterias.size() > 0) {
			// shipmentValiditycriterias.add(isNUllPredicate);
			criterias.add(cb.or(shipmentValiditycriterias.toArray(new Predicate[0])));

		}

		if (!QuoteUtil.isEmpty(criteria.getTermsAndConditions())) {
			p1 = cb.like(mt.<String>get("termsAndConditions"), "%" + criteria.getTermsAndConditions() + "%");
			criterias.add(p1);
		}

		if (!QuoteUtil.isEmpty(criteria.getAvnetQuoteCentreComments())) {
			p1 = cb.like(mt.<String>get("avnetQcComments"), "%" + criteria.getAvnetQuoteCentreComments() + "%");
			criterias.add(p1);
		}

		if (!QuoteUtil.isEmpty(criteria.getProgram())) {
			Join<SalesCostPricer, ProgramType> programType = mt.join("programType");
			p1 = cb.like(programType.<String>get("name"), criteria.getProgram());
			criterias.add(p1);
		}

		if (criterias.size() == 0) {

		} else if (criterias.size() == 1) {
			c.where(criterias.get(0));
		} else {
			c.where(cb.and(criterias.toArray(new Predicate[0])));
		}

	}

	/**
	 * Pagination: This Method populate the criteria according to the values in
	 * filters
	 * 
	 *
	 * @param criteria
	 *            the criteria
	 * @param material
	 *            the material
	 * @param c
	 *            the c
	 * @param bizUnit
	 *            the biz unit
	 * @param mt
	 *            the mt
	 * @param orderBy
	 *            the order by
	 * @param field
	 *            the field
	 * @parma criteria
	 * @parma material
	 * @parma c
	 * @parma bizUnit
	 */
	private void populateCriteriaForSimple(PricerUploadTemplateBean criteria, Join<SimplePricer, Material> material,
			CriteriaQuery c, Join<SimplePricer, BizUnit> bizUnit, Root<SimplePricer> mt, String orderBy, String field) {
		List<Predicate> criterias = new ArrayList<Predicate>();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		Predicate p1 = null;
		Join<Material, MaterialRegional> mRegional = material.join("materialRegionals");
		if (!QuoteUtil.isEmpty(criteria.getRegion())) {
			Join<MaterialRegional, BizUnit> mBizUnit = mRegional.join("bizUnit");
			p1 = cb.equal(mBizUnit.<String>get("name"), criteria.getRegion());
			criterias.add(p1);
		}

		if (!QuoteUtil.isEmpty(criteria.getMfr())) {
			Join<Material, Manufacturer> manufacturer = material.join("manufacturer");
			p1 = cb.equal(manufacturer.<String>get("name"), criteria.getMfr().toUpperCase());
			criterias.add(p1);
		}
		if (!QuoteUtil.isEmpty(criteria.getFullMfrPart())) {
			p1 = cb.like(material.<String>get("fullMfrPartNumber"),
					"%" + criteria.getFullMfrPart().toUpperCase() + "%");
			criterias.add(p1);
		}

		/**
		 * SALESCOSTFLAG
		 ****/
		if (!QuoteUtil.isEmpty(criteria.getSalesCostFlag())
				&& !criteria.getSalesCostFlag().equals(QuoteSBConstant.OPTION_ALL)) {
			p1 = cb.equal(mRegional.<String>get("salesCostFlag"),
					criteria.getSalesCostFlag().equals(QuoteSBConstant.OPTION_YES) ? true : false);
			criterias.add(p1);
		}

		// p1 = cb.equal(bizUnit.<String> get("name"),
		// criteria.getBizUnit().getName());
		p1 = cb.equal(bizUnit.<String>get("name"), criteria.getRegion());
		criterias.add(p1);

		if (!QuoteUtil.isEmpty(criteria.getCostIndicator())) {
			Join<SimplePricer, CostIndicator> costIndicator = mt.join("costIndicator");
			p1 = cb.equal(costIndicator.<String>get("name"), criteria.getCostIndicator());
			criterias.add(p1);
		}

		if (!QuoteUtil.isEmpty(criteria.getValidity())) {

			p1 = cb.equal(mt.<String>get("priceValidity"), criteria.getValidity());

			criterias.add(p1);
		}

		Date quotationEffectiveDateFrom = criteria.getQuotationEffectiveDateFrom();
		Date quotationEffectiveDateTo = criteria.getQuotationEffectiveDateTo();
		if (criteria.getQuotationEffectiveDateFrom() != null || criteria.getQuotationEffectiveDateTo() != null) {
			if (criteria.getQuotationEffectiveDateFrom() == null) {// dd/MM/yyyy
				quotationEffectiveDateFrom = PricerUtils.convertToDate("01/01/1900");
			}

			if (criteria.getQuotationEffectiveDateTo() == null) {
				quotationEffectiveDateTo = PricerUtils.convertToDate("01/01/2100");
			}
			quotationEffectiveDateFrom = PricerUtils.getTimesmorning(quotationEffectiveDateFrom);
			quotationEffectiveDateTo = PricerUtils.getTimesnight(quotationEffectiveDateTo);
			List<Predicate> qedcriterias = new ArrayList<Predicate>();
			Predicate qedFrom = cb.greaterThanOrEqualTo(mt.<Date>get("quotationEffectiveDate"),
					quotationEffectiveDateFrom);

			Predicate qedTo = cb.lessThanOrEqualTo(mt.<Date>get("quotationEffectiveDate"), quotationEffectiveDateTo);
			Predicate qedPredicate = cb.and(qedFrom, qedTo);
			qedcriterias.add(qedPredicate);

			Predicate qetForm = cb.greaterThanOrEqualTo(mt.<Date>get("quotationEffectiveTo"),
					quotationEffectiveDateFrom);

			Predicate qetTo = cb.lessThanOrEqualTo(mt.<Date>get("quotationEffectiveTo"), quotationEffectiveDateTo);
			Predicate qetPredicate = cb.and(qetForm, qetTo);
			qedcriterias.add(qetPredicate);

			Predicate qetForm2 = cb.lessThanOrEqualTo(mt.<Date>get("quotationEffectiveDate"),
					quotationEffectiveDateFrom);

			Predicate qetTo2 = cb.greaterThanOrEqualTo(mt.<Date>get("quotationEffectiveTo"), quotationEffectiveDateTo);
			Predicate qetPredicate2 = cb.and(qetForm2, qetTo2);
			qedcriterias.add(qetPredicate2);
			criterias.add(cb.or(qedcriterias.toArray(new Predicate[0])));
		}
		List<Predicate> shipmentValiditycriterias = new ArrayList<Predicate>();
		Predicate isNUllPredicate = cb.isNull(mt.get("shipmentValidity"));

		Predicate svForm1 = null;
		Predicate svTo1 = null;
		;
		if (criteria.getShipmentValidityFrom() != null) {
			svForm1 = cb.greaterThanOrEqualTo(mt.<Date>get("shipmentValidity"),
					PricerUtils.getTimesmorning(criteria.getShipmentValidityFrom()));

			if (criteria.getShipmentValidityTo() != null) {
				svTo1 = cb.lessThanOrEqualTo(mt.<Date>get("shipmentValidity"),
						PricerUtils.getTimesnight(criteria.getShipmentValidityTo()));
				Predicate qetPredicate2 = cb.and(svForm1, svTo1);
				shipmentValiditycriterias.add(isNUllPredicate);
				shipmentValiditycriterias.add(qetPredicate2);
			} else {
				shipmentValiditycriterias.add(isNUllPredicate);
				shipmentValiditycriterias.add(svForm1);
			}
		} else {
			if (criteria.getShipmentValidityTo() != null) {
				svTo1 = cb.lessThanOrEqualTo(mt.<Date>get("shipmentValidity"),
						PricerUtils.getTimesnight(criteria.getShipmentValidityTo()));
				shipmentValiditycriterias.add(svTo1);
				shipmentValiditycriterias.add(isNUllPredicate);
			}
		}
		// added for can’t search out records if condition is that both
		// “Shipment Validity From “and “Shipment Validity to “ is empty by
		// damon@20161226
		if (shipmentValiditycriterias.size() > 0) {
			// shipmentValiditycriterias.add(isNUllPredicate);
			criterias.add(cb.or(shipmentValiditycriterias.toArray(new Predicate[0])));

		}

		if (!QuoteUtil.isEmpty(criteria.getTermsAndConditions())) {
			p1 = cb.like(mt.<String>get("termsAndConditions"), "%" + criteria.getTermsAndConditions() + "%");
			criterias.add(p1);
		}

		if (!QuoteUtil.isEmpty(criteria.getAvnetQuoteCentreComments())) {
			p1 = cb.like(mt.<String>get("avnetQcComments"), "%" + criteria.getAvnetQuoteCentreComments() + "%");
			criterias.add(p1);
		}

		if (!QuoteUtil.isEmpty(criteria.getProgram())) {
			Join<SimplePricer, ProgramType> programType = mt.join("programType");
			p1 = cb.like(programType.<String>get("name"), criteria.getProgram());
			criterias.add(p1);
		}

		if (criterias.size() == 0) {

		} else if (criterias.size() == 1) {
			c.where(criterias.get(0));
		} else {
			c.where(cb.and(criterias.toArray(new Predicate[0])));
		}

	}

	/**
	 * Populate common criteria for pricer.
	 *
	 * @param criteria
	 *            the criteria
	 * @param criterias
	 *            the criterias
	 * @param cb
	 *            the cb
	 * @param p1
	 *            the p 1
	 * @param quotationEffectiveDate
	 *            the quotation effective date
	 * @param quotationEffectiveTo
	 *            the quotation effective to
	 * @param shipmentValidity
	 *            the shipment validity
	 * @param nullPredicate
	 *            the null predicate
	 * @return the pricer upload template bean
	 */
	private PricerUploadTemplateBean populateCommonCriteriaForPricer(PricerUploadTemplateBean criteria,
			List<Predicate> criterias, CriteriaBuilder cb, Predicate p1, Path<Date> quotationEffectiveDate,
			Path<Date> quotationEffectiveTo, Path<Date> shipmentValidity, Path<Object> nullPredicate) {

		Date quotationEffectiveDateFrom = criteria.getQuotationEffectiveDateFrom();
		Date quotationEffectiveDateTo = criteria.getQuotationEffectiveDateTo();

		if (criteria.getQuotationEffectiveDateFrom() != null || criteria.getQuotationEffectiveDateTo() != null) {
			if (criteria.getQuotationEffectiveDateFrom() == null) {// dd/MM/yyyy
				quotationEffectiveDateFrom = PricerUtils.convertToDate("01/01/1900");
			}

			if (criteria.getQuotationEffectiveDateTo() == null) {
				quotationEffectiveDateTo = PricerUtils.convertToDate("01/01/2100");
			}
			quotationEffectiveDateFrom = PricerUtils.getTimesmorning(quotationEffectiveDateFrom);
			quotationEffectiveDateTo = PricerUtils.getTimesnight(quotationEffectiveDateTo);
			List<Predicate> qedcriterias = new ArrayList<Predicate>();
			Predicate qedFrom = cb.greaterThanOrEqualTo(quotationEffectiveDate, quotationEffectiveDateFrom);

			Predicate qedTo = cb.lessThanOrEqualTo(quotationEffectiveDate, quotationEffectiveDateTo);
			Predicate qedPredicate = cb.and(qedFrom, qedTo);
			qedcriterias.add(qedPredicate);

			Predicate qetForm = cb.greaterThanOrEqualTo(quotationEffectiveTo, quotationEffectiveDateFrom);

			Predicate qetTo = cb.lessThanOrEqualTo(quotationEffectiveTo, quotationEffectiveDateTo);
			Predicate qetPredicate = cb.and(qetForm, qetTo);
			qedcriterias.add(qetPredicate);

			Predicate qetForm2 = cb.lessThanOrEqualTo(quotationEffectiveDate, quotationEffectiveDateFrom);

			Predicate qetTo2 = cb.greaterThanOrEqualTo(quotationEffectiveTo, quotationEffectiveDateTo);
			Predicate qetPredicate2 = cb.and(qetForm2, qetTo2);
			qedcriterias.add(qetPredicate2);

			criterias.add(cb.or(qedcriterias.toArray(new Predicate[0])));
		}

		List<Predicate> shipmentValiditycriterias = new ArrayList<Predicate>();
		Predicate isNUllPredicate = cb.isNull(nullPredicate);

		Predicate svForm1 = null;
		Predicate svTo1 = null;
		;
		if (criteria.getShipmentValidityFrom() != null) {
			svForm1 = cb.greaterThanOrEqualTo(shipmentValidity,
					PricerUtils.getTimesmorning(criteria.getShipmentValidityFrom()));

			if (criteria.getShipmentValidityTo() != null) {
				svTo1 = cb.lessThanOrEqualTo(shipmentValidity,
						PricerUtils.getTimesnight(criteria.getShipmentValidityTo()));
				Predicate qetPredicate2 = cb.and(svForm1, svTo1);
				shipmentValiditycriterias.add(isNUllPredicate);
				shipmentValiditycriterias.add(qetPredicate2);
			} else {
				shipmentValiditycriterias.add(isNUllPredicate);
				shipmentValiditycriterias.add(svForm1);
			}
		} else {
			if (criteria.getShipmentValidityTo() != null) {
				svTo1 = cb.lessThanOrEqualTo(shipmentValidity,
						PricerUtils.getTimesnight(criteria.getShipmentValidityTo()));
				shipmentValiditycriterias.add(svTo1);
				shipmentValiditycriterias.add(isNUllPredicate);
			}
		}

		// added for can’t search out records if condition is that both
		// “Shipment Validity From “and “Shipment Validity to “ is empty by
		// damon@20161226
		if (shipmentValiditycriterias.size() > 0) {
			// shipmentValiditycriterias.add(isNUllPredicate);
			criterias.add(cb.or(shipmentValiditycriterias.toArray(new Predicate[0])));
		}

		return criteria;
	}

	/**
	 * Convert ctr pricer to beans.
	 *
	 * @param pricerLst
	 *            the pricer lst
	 * @return the list
	 */
	public List<PricerUploadTemplateBean> convertCtrPricerToBeans(List<ContractPricer> pricerLst) {
		List<PricerUploadTemplateBean> resultLst = new ArrayList<PricerUploadTemplateBean>();
		if (pricerLst != null) {
			ContractPricer pg = null;
			for (int i = 0; i < pricerLst.size(); i++) {
				pg = pricerLst.get(i);
				PricerUploadTemplateBean bean = new PricerUploadTemplateBean();
				bean.setLineSeq((int) pg.getId());
				bean.setContractPriceId(pg.getId());
				bean.setMetarialDetailId(pg.getId());
				bean.setPricerType(PRICER_TYPE.CONTRACT.getName());
				bean.setMfr(pg.getMaterial().getManufacturer().getName());
				if (pg.getBizUnitBean() != null) {
					bean.setRegion(pg.getBizUnitBean().getName());
				}
				/*if(pg.getCostM() != null) {
					if(pg.getCostM().getAmount() != null) {
						bean.setCost(PricerUtils.formatBigDecimal(pg.getCostM().getAmount()));
					}
					if(pg.getCostM().getCurrency() != null) {
						bean.setCurrency(pg.getCostM().getCurrency().toString());
					}
				}*/
				if (pg.getCost() != null) {
					bean.setCost(PricerUtils.formatDouble(pg.getCost()));
				}
				if (pg.getCurrency() != null) {
					bean.setCurrency(pg.getCurrency().toString());
				}
				if (pg.getCostIndicator() != null) {
					bean.setCostIndicator(pg.getCostIndicator().getName());
				}
				bean.setQuotationEffectiveDate(QuoteUtil.convertDateToStr(pg.getDisplayQuoteEffDate()));
				if (pg.getMaterial() != null) {
					bean.setFullMfrPart(pg.getMaterial().getFullMfrPartNumber());
				}

				if (pg.getSoldtoCustomer() != null) {

					String fullNameString = pg.getSoldtoCustomer().getCustomerFullName();

					bean.setCustomerName(fullNameString);
					bean.setSoldToCode(pg.getSoldtoCustomer().getCustomerNumber());
				}

				if (pg.getEndCustomer() != null) {
					//why has so trange code for endcustomerName.
					/*if (pg.getEndCustomerName() != null && !pg.getEndCustomerName().equals("")) {
						bean.setEndCustomerName(pg.getEndCustomerName());
					} else {
						bean.setEndCustomerName(pg.getEndCustomer().getCustomerFullName());
					}*/
					bean.setEndCustomerName(pg.getEndCustomer().getCustomerFullName());
					bean.setEndCustomerCode(pg.getEndCustomer().getCustomerNumber());
				}

				if (pg.getMinSellPrice() != null) {
					bean.setMinSell(PricerUtils.formatDouble(pg.getMinSellPrice()));
				}
				/*if(pg.getMinSellPriceM() != null) {
					if(pg.getMinSellPriceM().getAmount() != null) {
						bean.setMinSell(PricerUtils.formatBigDecimal(pg.getMinSellPriceM().getAmount()));
					}
				}*/

				
				bean.setStartDate(QuoteUtil.convertDateToStr(pg.getStartDate()));
				bean.setValidity(pg.getPriceValidity());
				bean.setShipmentValidity(QuoteUtil.convertDateToStr(pg.getShipmentValidity()));

				bean.setVendorQuoteNo(pg.getVendorQuoteNumber());
				if (pg.getVendorQuoteQuantity() != null) {
					bean.setVendorQuoteQty(String.valueOf(pg.getVendorQuoteQuantity()));
				}

				// bean.setEndCustomerName(pg.getEndCustomerName()); //line 446
				// and 448 already has setEndCustomerName code;

				bean.setAvnetQuoteCentreComments(pg.getAvnetQcComments());

				if (pg.getOverrideFlag()) {
					bean.setOverriddenStr("Yes");
				} else {
					bean.setOverriddenStr("No");
				}
				bean.setLastUpdatedBy(pg.getLastUpdatedBy());
				bean.setLastUpdatedOn(QuoteUtil.convertDateToStr(pg.getLastUpdatedOn()));
				// fix enhancment 118 at quote match logic enhancment project
				// added by June 2014/12/12
				if (null != pg.getMpq())
					bean.setMPQ(String.valueOf(pg.getMpq()));

				if (null != pg.getMoq())
					bean.setMOQ(String.valueOf(pg.getMoq()));
				if (pg.getMov() != null) {
					bean.setMOV(String.valueOf(pg.getMov()));
				}
				bean.setLeadTime(pg.getLeadTime());
				bean.setTermsAndConditions(pg.getTermsAndConditions());

				bean.setDisQuotationEffectiveDate(pg.getQuotationEffectiveDate()); // fix
																					// INC0143568
																					// By
																					// June
																					// 20150519

				resultLst.add(bean);
			}
		}

		return resultLst;
	}

	/**
	 * Convert nor pricer to beans.
	 *
	 * @param pricerLst
	 *            the pricer lst
	 * @return the list
	 */
	public List<PricerUploadTemplateBean> convertNorPricerToBeans(List<NormalPricer> pricerLst) {
		List<PricerUploadTemplateBean> resultLst = new ArrayList<PricerUploadTemplateBean>();
		if (pricerLst != null) {
			NormalPricer pg = null;
			for (int i = 0; i < pricerLst.size(); i++) {
				pg = pricerLst.get(i);
				PricerUploadTemplateBean bean = new PricerUploadTemplateBean();
				bean.setLineSeq((int) pg.getId());
				bean.setMetarialDetailId(pg.getId());
				bean.setContractPriceId(pg.getId());
				bean.setPricerType(PRICER_TYPE.NORMAL.getName());
				if (pg.getBizUnitBean() != null) {
					bean.setRegion(pg.getBizUnitBean().getName());
				}
				// Added by Punit for CPD Exercise
				bean = convertNorToBean(bean, pg);

				/*if(pg.getMinSellPriceM() != null) {
					if(pg.getMinSellPriceM().getAmount() != null) {
						bean.setMinSell(PricerUtils.formatBigDecimal(pg.getMinSellPriceM().getAmount()));
					}
				}*/
				if (pg.getMinSellPrice() != null) {
					bean.setMinSell(PricerUtils.formatDouble(pg.getMinSellPrice()));
				}

				if (pg.getBottomPrice() != null) {
					bean.setBottomPrice(PricerUtils.formatDouble(pg.getBottomPrice()));
				}

				if (pg.getMaterial() != null) {
					MaterialRegional mRegional = pg.getMaterial().getMaterialRegaional(pg.getBizUnitBean().getName());
					if(mRegional !=null) {
						if (mRegional.getProductGroup1() != null) {
							bean.setProductGroup1(mRegional.getProductGroup1().getName());
						}
	
						if (mRegional.getProductGroup2() != null) {
							bean.setProductGroup2(mRegional.getProductGroup2().getName());
						}
						bean.setProductGroup3(mRegional.getProductGroup3());
						bean.setProductGroup4(mRegional.getProductGroup4());
	
						if (mRegional.getProductCategory() != null) {
							bean.setProductCat(mRegional.getProductCategory().getCategoryCode());
						}
					}

				}
				bean.setAvnetQuoteCentreComments(pg.getAvnetQcComments());
				bean.setDescription(pg.getPartDescription());
				bean.setPriceListRemarks(pg.getPriceListRemarks1());
				bean.setPriceListRemarks2(pg.getPriceListRemarks2());
				bean.setPriceListRemarks3(pg.getPriceListRemarks3());
				bean.setPriceListRemarks4(pg.getPriceListRemarks4());
				bean.setLastUpdatedBy(pg.getLastUpdatedBy());
				bean.setLastUpdatedOn(QuoteUtil.convertDateToStr(pg.getLastUpdatedOn()));

				bean.setDisQuotationEffectiveDate(pg.getQuotationEffectiveDate()); // fix
																					// INC0143568
																					// By
																					// June
																					// 20150519

				resultLst.add(bean);
			}
		}

		return resultLst;
	}

	/**
	 * Convert pg pricer to beans.
	 *
	 * @param pricerLst
	 *            the pricer lst
	 * @return the list
	 */
	public List<PricerUploadTemplateBean> convertPgPricerToBeans(List<ProgramPricer> pricerLst) {
		List<PricerUploadTemplateBean> resultLst = new ArrayList<PricerUploadTemplateBean>();
		if (pricerLst != null) {
			ProgramPricer pg = null;
			for (int i = 0; i < pricerLst.size(); i++) {
				pg = pricerLst.get(i);
				PricerUploadTemplateBean bean = new PricerUploadTemplateBean();
				bean.setLineSeq((int) pg.getId());
				bean.setMetarialDetailId(pg.getId());
				bean.setContractPriceId(pg.getId());
				bean.setPricerType(PRICER_TYPE.PROGRAM.getName());
				if (pg.getBizUnitBean() != null) {
					bean.setRegion(pg.getBizUnitBean().getName());
				}
				// Added by Punit for CPD Exercise
				bean = convertPgToBean(bean, pg);

				if (pg.getProgramType() != null) {
					bean.setProgram(pg.getProgramType().getName());
				}
				if(pg.getDisplayOnTop()!=null) {
					bean.setDisplayOnTop(String.valueOf(pg.getDisplayOnTop()));
				}
				
				if(pg.getNewItemIndicator()) {
					bean.setNewItemIndicator("Yes");
				}else {
					bean.setNewItemIndicator("No");
				}
				/*if (pg.getMaterial() != null) {
					if (pg.getMaterial().getDisplayOnTop() != null) {
						bean.setDisplayOnTop(String.valueOf(pg.getMaterial().getDisplayOnTop()));
					}
					if (pg.getMaterial().getNewItemFlag() != null) {
						if (pg.getMaterial().getNewItemFlag()) {
							bean.setNewItemIndicator("Yes");
						} else {
							bean.setNewItemIndicator("No");
						}
					} else {
						bean.setNewItemIndicator("No");
					}
				}*/

				if (pg.getSpecialItemFlag() == null) {
					bean.setSpecialItemIndicator(null);
				} else {
					bean.setSpecialItemIndicator(String.valueOf(pg.getSpecialItemFlag()));
				}

				if (pg.getAvailableToSellQty() != null) {
					bean.setAvailabletoSellQuantity(String.valueOf(pg.getAvailableToSellQty()));
				}

				List<QuantityBreakPrice> qtbLst = pg.getQuantityBreakPrices();

				if ((qtbLst != null) && qtbLst.size() > 0) {
					setQuantityBreakToTemplateBean(bean, qtbLst);
				}

				/*if (pg.getProgramCallForQuote()) {
					bean.setCallForQuote("Yes");
				} else {
					bean.setCallForQuote("No");
				}*/

				bean.setProgramEffectiveDate(QuoteUtil.convertDateToStr(pg.getProgramEffectiveDate()));
				bean.setProgramClosingDate(QuoteUtil.convertDateToStr(pg.getProgramClosingDate()));
				bean.setQtyIndicator(pg.getQtyIndicator());
				if (pg.getAllocationFlag()) {
					bean.setAllocationFlag("Yes");
				} else {
					bean.setAllocationFlag("No");
				}

				bean.setResaleIndicator(pg.getResaleIndicator());
				if (pg.getAqFlag()) {
					bean.setAQFlag("Yes");
				} else {
					bean.setAQFlag("No");
				}

				bean.setAvnetQuoteCentreComments(pg.getAvnetQcComments());

				if (pg.getAvailableToSellMoreFlag()) {
					bean.setAvailableToSellMoreFlag("Yes");
				} else {
					bean.setAvailableToSellMoreFlag("No");
				}
				bean.setLastUpdatedBy(pg.getLastUpdatedBy());
				bean.setLastUpdatedOn(QuoteUtil.convertDateToStr(pg.getLastUpdatedOn()));

				bean.setDisQuotationEffectiveDate(pg.getQuotationEffectiveDate()); // fix
																					// INC0143568
																					// By
																					// June
																					// 20150519

				resultLst.add(bean);
			}
		}

		return resultLst;
	}

	/**
	 * Convert salescost pricer to beans.
	 *
	 * @param pricerLst
	 *            the pricer lst
	 * @return the list
	 */
	public List<PricerUploadTemplateBean> convertSalesCostPricerToBeans(List<SalesCostPricer> pricerLst) {
		List<PricerUploadTemplateBean> resultLst = new ArrayList<PricerUploadTemplateBean>();
		if (pricerLst != null) {
			SalesCostPricer pg = null;
			for (int i = 0; i < pricerLst.size(); i++) {
				pg = pricerLst.get(i);
				PricerUploadTemplateBean bean = new PricerUploadTemplateBean();
				bean.setLineSeq((int) pg.getId());
				bean.setMetarialDetailId(pg.getId());
				if (pg.getBizUnitBean() != null) {
					bean.setRegion(pg.getBizUnitBean().getName());
				}
				if (pg.getSalesCostType() != null) {
					bean.setSalesCostType(pg.getSalesCostType().toString());
				}
				bean.setPricerType(PRICER_TYPE.SALESCOST.getName());
				if (pg.getMaterial() != null) {
					bean.setMfr(pg.getMaterial().getManufacturer().getName());
					bean.setFullMfrPart(pg.getMaterial().getFullMfrPartNumber());
				}
				if (pg.getCost() != null) {
					bean.setCost(PricerUtils.formatDouble(pg.getCost()));
				}
				if (pg.getCurrency() != null) {
					bean.setCurrency(pg.getCurrency().toString());
				}
				/*if(pg.getCostM() != null) {
					if(pg.getCostM().getAmount() != null) {
						bean.setCost(PricerUtils.formatBigDecimal(pg.getCostM().getAmount()));
					}
					if(pg.getCostM().getCurrency() != null) {
						bean.setCurrency(pg.getCostM().getCurrency().toString());
					}
				}*/
				if (pg.getCostIndicator() != null) {
					bean.setCostIndicator(pg.getCostIndicator().getName());
				}
				bean.setValidity(pg.getPriceValidity());
				bean.setShipmentValidity(QuoteUtil.convertDateToStr(pg.getShipmentValidity()));

				if (pg.getMpq() != null) {
					bean.setMPQ(String.valueOf(pg.getMpq()));
				}

				if (pg.getMoq() != null) {
					bean.setMOQ(String.valueOf(pg.getMoq()));
				}

				if (pg.getMov() != null) {
					bean.setMOV(String.valueOf(pg.getMov()));
				}

				bean.setLeadTime(pg.getLeadTime());

				bean.setQuotationEffectiveDate(QuoteUtil.convertDateToStr(pg.getDisplayQuoteEffDate()));

				bean.setTermsAndConditions(pg.getTermsAndConditions());
				bean.setAvnetQuoteCentreComments(pg.getAvnetQcComments());

				if (pg.getProgramType() != null) {
					bean.setProgram(pg.getProgramType().getName());
				}
				if(pg.getDisplayOnTop()!=null) {
					bean.setDisplayOnTop(String.valueOf(pg.getDisplayOnTop()));
				}
				if(pg.getNewItemIndicator()) {
					bean.setNewItemIndicator("Yes");
				}else {
					bean.setNewItemIndicator("No");
				}
				/*if (pg.getMaterial() != null) {
					if (pg.getMaterial().getDisplayOnTop() != null) {
						bean.setDisplayOnTop(String.valueOf(pg.getMaterial().getDisplayOnTop()));
					}
					if (pg.getMaterial().getNewItemFlag() != null) {
						if (pg.getMaterial().getNewItemFlag()) {
							bean.setNewItemIndicator("Yes");
						} else {
							bean.setNewItemIndicator("No");
						}
					} else {
						bean.setNewItemIndicator("No");
					}
				}*/

				if (pg.getSpecialItemFlag() == null) {
					bean.setSpecialItemIndicator(null);
				} else {
					bean.setSpecialItemIndicator(String.valueOf(pg.getSpecialItemFlag()));
				}

				if (pg.getAvailableToSellQty() != null) {
					bean.setAvailabletoSellQuantity(String.valueOf(pg.getAvailableToSellQty()));
				}

				if (pg.getQtyControl() != null) {
					bean.setQtyControl(String.valueOf(pg.getQtyControl()));
				}

				List<QuantityBreakPrice> qtbLst = pg.getQuantityBreakPrices();

				if ((qtbLst != null) && qtbLst.size() > 0) {
					this.setQuantityBreakToTemplateBean(bean, qtbLst);
				}

				/*** SIMPLE DO NOT HAVE **/
				bean.setQtyIndicator(pg.getQtyIndicator());

				if (pg.getAllocationFlag()) {
					bean.setAllocationFlag("Yes");
				} else {
					bean.setAllocationFlag("No");
				}

				if (pg.getAqFlag()) {
					bean.setAQFlag("Yes");
				} else {
					bean.setAQFlag("No");
				}

				bean.setLastUpdatedBy(pg.getLastUpdatedBy());
				bean.setLastUpdatedOn(QuoteUtil.convertDateToStr(pg.getLastUpdatedOn()));

				bean.setDisQuotationEffectiveDate(pg.getQuotationEffectiveDate()); // fix
																					// INC0143568
																					// By
																					// June
																					// 20150519
				// bean.setQuotationEffectiveDate(quotationEffectiveDate);
				resultLst.add(bean);
			}
		}

		return resultLst;
	}

	/**
	 * Convert simple pricer to beans.
	 *
	 * @param pricerLst
	 *            the pricer lst
	 * @return the list
	 */
	public List<PricerUploadTemplateBean> convertSimplePricerToBeans(List<SimplePricer> pricerLst) {
		List<PricerUploadTemplateBean> resultLst = new ArrayList<PricerUploadTemplateBean>();
		if (pricerLst != null) {
			SimplePricer pg = null;
			for (int i = 0; i < pricerLst.size(); i++) {
				pg = pricerLst.get(i);
				PricerUploadTemplateBean bean = new PricerUploadTemplateBean();
				bean.setLineSeq((int) pg.getId());
				bean.setMetarialDetailId(pg.getId());
				bean.setPricerType(PRICER_TYPE.SIMPLE.getName());
				if (pg.getBizUnitBean() != null) {
					bean.setRegion(pg.getBizUnitBean().getName());
				}
				if (pg.getMaterial() != null) {
					bean.setMfr(pg.getMaterial().getManufacturer().getName());
					bean.setFullMfrPart(pg.getMaterial().getFullMfrPartNumber());
				}
				if (pg.getCost() != null) {
					bean.setCost(PricerUtils.formatDouble(pg.getCost()));
				}
				if (pg.getCostIndicator() != null) {
					bean.setCostIndicator(pg.getCostIndicator().getName());
				}
				bean.setValidity(pg.getPriceValidity());
				bean.setShipmentValidity(QuoteUtil.convertDateToStr(pg.getShipmentValidity()));

				if (pg.getMpq() != null) {
					bean.setMPQ(String.valueOf(pg.getMpq()));
				}

				if (pg.getMoq() != null) {
					bean.setMOQ(String.valueOf(pg.getMoq()));
				}

				if (pg.getMov() != null) {
					bean.setMOV(String.valueOf(pg.getMov()));
				}

				bean.setLeadTime(pg.getLeadTime());

				bean.setQuotationEffectiveDate(QuoteUtil.convertDateToStr(pg.getDisplayQuoteEffDate()));

				bean.setTermsAndConditions(pg.getTermsAndConditions());
				bean.setAvnetQuoteCentreComments(pg.getAvnetQcComments());
				if (pg.getProgramType() != null) {
					bean.setProgram(pg.getProgramType().getName());
				}
				 
				if(pg.getDisplayOnTop()!=null) {
					bean.setDisplayOnTop(String.valueOf(pg.getDisplayOnTop()));
				}
				 
				if (pg.getSpecialItemFlag() == null) {
					bean.setSpecialItemIndicator(null);
				} else {
					bean.setSpecialItemIndicator(String.valueOf(pg.getSpecialItemFlag()));
				}

				if (pg.getAvailableToSellQty() != null) {
					bean.setAvailabletoSellQuantity(String.valueOf(pg.getAvailableToSellQty()));
				}

				if (pg.getQtyControl() != null) {
					bean.setQtyControl(String.valueOf(pg.getQtyControl()));
				}

				List<QuantityBreakPrice> qtbLst = pg.getQuantityBreakPrices();

				if ((qtbLst != null) && qtbLst.size() > 0) {
					this.setQuantityBreakToTemplateBean(bean, qtbLst);
				}

				bean.setVendorQuoteNo(pg.getVendorQuoteNumber());
				if (pg.getVendorQuoteQuantity() != null) {
					bean.setVendorQuoteQty(String.valueOf(pg.getVendorQuoteQuantity()));
				}

				bean.setLastUpdatedBy(pg.getLastUpdatedBy());
				bean.setLastUpdatedOn(QuoteUtil.convertDateToStr(pg.getLastUpdatedOn()));

				resultLst.add(bean);
			}
		}

		return resultLst;
	}

	/**
	 * updateProgramPricerMaterialDetail According to material_id,region and
	 * cost_indicator to update table.
	 *
	 * @param list
	 *            the list
	 * @param user
	 *            the user
	 * @return true, if successful
	 */
	public boolean updateProgramPricerMaterialDetail(List<PricerUploadTemplateBean> list, User user) {
		boolean isSuccess = true;
		try {
			ut.setTransactionTimeout(10000);
			ut.begin();

			for (PricerUploadTemplateBean pricr : list) {
				ProgramPricer mDetail = em.find(ProgramPricer.class, pricr.getMetarialDetailId());
				if (!QuoteUtil.isEmpty(pricr.getCost())) {
					mDetail.setCost(Double.parseDouble(pricr.getCost()));
				} else {
					mDetail.setCost(null);
				}
				PricerUtils.setQuotationEffectiveTo(pricr, mDetail);

				if (pricr.getAvailableToSellMoreFlag() != null) {
					if ("YES".equalsIgnoreCase(pricr.getAvailableToSellMoreFlag())) {
						mDetail.setAvailableToSellMoreFlag(true);
					} else {
						mDetail.setAvailableToSellMoreFlag(false);
					}
				} else {
					mDetail.setAvailableToSellMoreFlag(false);
				}

				// mDetail.setDescription(pricr.getDescription());

				mDetail.setResaleIndicator(pricr.getResaleIndicator());

				String nidr = pricr.getNewItemIndicator();
				if ((nidr != null) && (!nidr.equals(""))) {
					nidr = nidr.trim();
					if (nidr.equalsIgnoreCase("Yes")) {
						mDetail.setNewItemIndicator(true);
					} else {
						mDetail.setNewItemIndicator(false);
					}
				} else {
					mDetail.setNewItemIndicator(false);
				}
				if (!QuoteUtil.isEmpty(pricr.getDisplayOnTop())) {
					mDetail.setDisplayOnTop(Integer.parseInt(pricr.getDisplayOnTop()));
				}
				// Added by Punit for CPD exercise
				mDetail = CommodityUtil.updateProgramPricerMaterialDetailFromPUTBean(pricr, mDetail);

				mDetail.setLeadTime(pricr.getLeadTime());

				if ((pricr.getMPQ() != null) && (!pricr.getMPQ().equals(""))) {
					mDetail.setMpq(Integer.parseInt(pricr.getMPQ()));
				} else {
					mDetail.setMpq(null);
				}

				if ((pricr.getMOQ() != null) && (!pricr.getMOQ().equals(""))) {
					mDetail.setMoq(Integer.parseInt(pricr.getMOQ()));
				} else if ((pricr.getMOV() != null) && (!pricr.getMOV().equals("")) && (pricr.getCost() != null)
						&& (!pricr.getCost().equals("")) && (pricr.getMPQ() != null) && (!pricr.getMPQ().equals(""))) {
					int moq = PricerUtils.moqCalculate(Double.parseDouble(pricr.getMOV()),
							Double.parseDouble(pricr.getCost()), Integer.parseInt(pricr.getMPQ()));
					mDetail.setMoq(moq);
					pricr.setMOQ(String.valueOf(moq));
				}

				if ((pricr.getMOV() != null) && (!pricr.getMOV().equals(""))) {
					mDetail.setMov(Integer.parseInt(pricr.getMOV()));
				} else {
					mDetail.setMov(null);
				}

				if ((pricr.getShipmentValidity() != null) && (!pricr.getShipmentValidity().equals(""))) {
					mDetail.setShipmentValidity(
							QuoteUtil.convertStringToDate(pricr.getShipmentValidity(), "dd/MM/yyyy"));
				} else {
					mDetail.setShipmentValidity(null);
				}

				mDetail.setTermsAndConditions(pricr.getTermsAndConditions());

				mDetail.setLastUpdatedOn(new Date());
				mDetail.setLastUpdatedBy(user.getEmployeeId());
				mDetail.setPriceValidity(pricr.getValidity());
				mDetail.setQtyIndicator(pricr.getQtyIndicator());
				String aFlag = pricr.getAllocationFlag();
				if ((aFlag != null) && (!aFlag.equals(""))) {
					aFlag = aFlag.trim();
					if (aFlag.equalsIgnoreCase("Yes")) {
						mDetail.setAllocationFlag(true);
					} else {
						mDetail.setAllocationFlag(false);
					}
				} else {
					mDetail.setAllocationFlag(false);
				}

				mDetail.setAvnetQcComments(pricr.getAvnetQuoteCentreComments());

				String hql = "DELETE FROM QuantityBreakPrice e WHERE e.materialDetail.id =" + mDetail.getId();
				Query query = em.createQuery(hql);
				query.executeUpdate();

				List<QuantityBreakPrice> qpLst = CommodityUtil.updatePPMDQuantityBreakPriceList(mDetail, pricr, em);
				mDetail.setQuantityBreakPrices(qpLst);

				em.merge(mDetail);
				// A material can have two kinds of materialdetail (normal and
				// program).If find a normal refer to that need update.
			}
			ut.commit();
		} catch (Exception e) {
			try {
				ut.rollback();
				throw e;
			} catch (Exception e1) {
				LOG.log(Level.SEVERE, "updateProgramPricerMaterialDetail roll back error:  user Id and name "
						+ user.getId() + " & " + user.getName() + e.getMessage(), e);
			}
			isSuccess = false;
			LOG.log(Level.SEVERE, "updateProgramPricerMaterialDetail :  user Id and name " + user.getId() + " & "
					+ user.getName() + e.getMessage(), e);
		}
		return isSuccess;
	}

	/**
	 * updateNormalPricerMaterialDetail According to material_id,region and
	 * cost_indicator to update table.
	 *
	 * @param list
	 *            the list
	 * @param user
	 *            the user
	 * @return true, if successful
	 */
	public boolean updateNormalPricerMaterialDetail(List<PricerUploadTemplateBean> list, User user) {
		boolean isSuccess = true;
		try {
			ut.setTransactionTimeout(10000);
			ut.begin();
			for (PricerUploadTemplateBean pricr : list) {
				NormalPricer mDtl = em.find(NormalPricer.class, pricr.getMetarialDetailId());

				PricerUtils.setQuotationEffectiveTo(pricr, mDtl);

				/*
				 * if (pricr.getAvailableToSellMoreFlag() != null) { if
				 * ("YES".equalsIgnoreCase(pricr.getAvailableToSellMoreFlag()))
				 * { mDtl.setAvailableToSellMoreFlag(true); } else {
				 * mDtl.setAvailableToSellMoreFlag(false); } } else {
				 * mDtl.setAvailableToSellMoreFlag(false); }
				 */
				mDtl.setPartDescription(pricr.getDescription());
				mDtl.setAvnetQcComments(pricr.getAvnetQuoteCentreComments());
				// Added by Punit for CPD Exercise
				mDtl = CommodityUtil.pricerMasterEnquiryDetails(pricr, mDtl, user.getEmployeeId());

				em.merge(mDtl);
			}
			ut.commit();
		} catch (Exception e) {
			try {
				ut.rollback();
				throw e;
			} catch (Exception e1) {
				LOG.log(Level.SEVERE, "updateNormalPricerMaterialDetail error in roll back user Id and name "
						+ user.getId() + " & " + user.getName() + e1.getMessage(), e1);
			}
			isSuccess = false;
			LOG.log(Level.SEVERE,
					"Exception occuring in method updateNormalPricerMaterialDetail persisting MaterialDetail :::  user Id and name "
							+ user.getId() + " & " + user.getName() + e.getMessage(),
					e);
		}

		return isSuccess;
	}

	/**
	 * updateContractPricer.
	 *
	 * @param list
	 *            the list
	 * @param user
	 *            the user
	 * @return true, if successful
	 */
	public boolean updateContractPricer(List<PricerUploadTemplateBean> list, User user) {
		boolean isSuccess = true;
		try {
			ut.setTransactionTimeout(10000);
			ut.begin();
			Customer endCustomer = null;
			for (PricerUploadTemplateBean pricr : list) {
				ContractPricer mDtl = em.find(ContractPricer.class, pricr.getContractPriceId());
				endCustomer = pricr.getEndCustomer();
				//so strange
				if (endCustomer != null) {
					mDtl.setEndCustomerName(endCustomer.getCustomerFullName());
				} else {
					mDtl.setEndCustomerName(null);
				}
				mDtl.setEndCustomer(endCustomer);
				mDtl.setSoldtoCustomer(pricr.getSoldToCustomer());

				PricerUtils.setQuotationEffectiveTo(pricr, mDtl);
				/*if(!StringUtils.isEmpty(pricr.getCurrency())) {
					mDtl.setCurrency(Currency.valueOf(pricr.getCurrency()));
				} else {
					mDtl.setCurrency(null);
				}*/
				
				if (!QuoteUtil.isEmpty(pricr.getStartDate())) {
					mDtl.setStartDate(PricerUtils.convertToDate(pricr.getStartDate()));
				}
				if (!QuoteUtil.isEmpty(pricr.getCost())) {
					mDtl.setCost(Double.parseDouble(pricr.getCost()));
				} else {
					mDtl.setCost(null);
				}
				mDtl.setPriceValidity(pricr.getValidity());
				if (!QuoteUtil.isEmpty(pricr.getShipmentValidity())) {
					Date date = QuoteUtil.convertStringToDate(pricr.getShipmentValidity(), "dd/MM/yyyy");
					mDtl.setShipmentValidity(date);
				} else {
					mDtl.setShipmentValidity(null);
				}
				if (!QuoteUtil.isEmpty(pricr.getMinSell())) {
					mDtl.setMinSellPrice(Double.valueOf(pricr.getMinSell()));
				}
				mDtl.setVendorQuoteNumber(pricr.getVendorQuoteNo());
				if (!QuoteUtil.isEmpty(pricr.getVendorQuoteQty())) {
					mDtl.setVendorQuoteQuantity(Integer.valueOf(pricr.getVendorQuoteQty()));
				}
				mDtl.setAvnetQcComments(pricr.getAvnetQuoteCentreComments());
				mDtl.setLastUpdatedOn(new Date());
				mDtl.setLastUpdatedBy(user.getEmployeeId());
				mDtl.setLeadTime(pricr.getLeadTime());
				if (!QuoteUtil.isEmpty(pricr.getMPQ())) {
					mDtl.setMpq(Integer.parseInt(pricr.getMPQ()));
				} else {
					mDtl.setMpq(null);
				}

				if (!QuoteUtil.isEmpty(pricr.getMOQ())) {
					mDtl.setMoq(Integer.parseInt(pricr.getMOQ()));
				} else {
					mDtl.setMoq(null);
				}

				if (!QuoteUtil.isEmpty(pricr.getMOV())) {
					mDtl.setMov(Integer.parseInt(pricr.getMOV()));
				}
				mDtl.setTermsAndConditions(pricr.getTermsAndConditions());
				// update cverrideFlag added by Lenon 2017/03/06
				if (StringUtils.equals("Yes", pricr.getOverriddenStr())) {
					mDtl.setOverrideFlag(true);
				} else {
					mDtl.setOverrideFlag(false);
				}
				em.merge(mDtl);
			}
			ut.commit();
		} catch (Exception e) {
			try {
				ut.rollback();
				throw e;
			} catch (Exception e1) {
				LOG.log(Level.SEVERE, "updateContractPricer error in roll back user Id and name " + user.getId() + " & "
						+ user.getName() + e1.getMessage(), e1);
			}
			isSuccess = false;
			LOG.log(Level.SEVERE,
					"Exception occuring in method updateContractPricer persisting ContractPrice :::   user Id and name "
							+ user.getId() + " & " + user.getName() + e.getMessage(),
					e);
		}
		return isSuccess;
	}

	public MaterialRegional getMRegionalByPricerID(long id, User user) {
		if (id > 0) {
			try {
				Pricer pricer = em.find(Pricer.class, id);
				return pricer.getMaterial().getMaterialRegaional(pricer.getBizUnitBean().getName());
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "getMRegionalByPricerID Error:  user Id and name " + user.getId() + " & "
						+ user.getName() + e.getMessage(), e);
				return null;
			}
		}
		return null;
	}

	/*
	 * updateProgramPricerMaterialDetail According to material_id,region and
	 * cost_indicator to update table.
	 *
	 * @param list the list
	 * 
	 * @param user the user
	 * 
	 * @return true, if successful
	 */
	public boolean updateSalesCostPricerMaterialDetail(List<PricerUploadTemplateBean> list, User user) {
		boolean isSuccess = true;
		try {
			ut.setTransactionTimeout(10000);
			ut.begin();

			for (PricerUploadTemplateBean pricr : list) {
				SalesCostPricer mDetail = em.find(SalesCostPricer.class, pricr.getMetarialDetailId());
				/***Date effectiveTo =PricerUtils.getEffectiveTo(mDetail.getQuotationEffectiveDate(),
						pricr.getValidity());
				if(!effectiveTo.equals(mDetail.getQuotationEffectiveTo())) {
					pricerUploadCommonSB.InsertIntoPricerDeletedByPricerId(mDetail.getId(), user);
					mDetail.setBatchStatus(0);
				}else {
					mDetail.setBatchStatus(1);
				}*****/
				pricerUploadCommonSB.InsertIntoPricerDeletedByPricerId(mDetail.getId(), user);
				mDetail.setBatchStatus(0);
				this.fillPricerWithBean(mDetail, pricr, user);

				String hql = "DELETE FROM QuantityBreakPrice e WHERE e.materialDetail.id =" + mDetail.getId();
				Query query = em.createQuery(hql);
				query.executeUpdate();

				List<QuantityBreakPrice> qpLst = CommodityUtil.updateQuantityBreakPriceListForSS(mDetail, pricr, em);
				mDetail.setQuantityBreakPrices(qpLst);

				em.merge(mDetail);
				// A material can have two kinds of materialdetail (normal and
				// program).If find a normal refer to that need update.
			}
			ut.commit();
		} catch (Exception e) {
			try {
				ut.rollback();
				throw e;
			} catch (Exception e1) {
				LOG.log(Level.SEVERE, "updateSalesCostPricerMaterialDetail roll back error:  user Id and name "
						+ user.getId() + " & " + user.getName() + e.getMessage(), e);
			}
			isSuccess = false;
			LOG.log(Level.SEVERE, "updateSalesCostPricerMaterialDetail :  user Id and name " + user.getId() + " & "
					+ user.getName() + e.getMessage(), e);
		}
		return isSuccess;
	}

	public boolean updateSimplePricerMaterialDetail(List<PricerUploadTemplateBean> list, User user) {
		boolean isSuccess = true;
		try {
			ut.setTransactionTimeout(10000);
			ut.begin();

			for (PricerUploadTemplateBean pricr : list) {
				SimplePricer mDetail = em.find(SimplePricer.class, pricr.getMetarialDetailId());
				this.fillPricerWithBean(mDetail, pricr, user);

				String hql = "DELETE FROM QuantityBreakPrice e WHERE e.materialDetail.id =" + mDetail.getId();
				Query query = em.createQuery(hql);
				query.executeUpdate();

				List<QuantityBreakPrice> qpLst = CommodityUtil.updateQuantityBreakPriceListForSS(mDetail, pricr, em);
				mDetail.setQuantityBreakPrices(qpLst);

				em.merge(mDetail);
				// A material can have two kinds of materialdetail (normal and
				// program).If find a normal refer to that need update.
			}
			ut.commit();
		} catch (Exception e) {
			try {
				ut.rollback();
				throw e;
			} catch (Exception e1) {
				LOG.log(Level.SEVERE, "updateSimplePricerMaterialDetail roll back error:  user Id and name "
						+ user.getId() + " & " + user.getName() + e.getMessage(), e);
			}
			isSuccess = false;
			LOG.log(Level.SEVERE, "updateSimplePricerMaterialDetail :  user Id and name " + user.getId() + " & "
					+ user.getName() + e.getMessage(), e);
		}
		return isSuccess;
	}

	private void fillPricerWithBean(final SalesCostPricer pricer, PricerUploadTemplateBean bean, User user)
			throws ParseException {
		/**********
		 * be careful here** PAGE::::we use the Pricer's disQED fill the bean's
		 * QED ,because usr want to show the real value which they put. Page's
		 * QED value can not be changed,and if it is null we can not use it or
		 * today to calculate the QEDto. Thus we need a confirmed QED value to
		 * calculate the QEDto, so Pricer's QED is the confirmed value and not
		 * be null. This Pricer's QED maintain the date user put in by uploading
		 * or date when user upload if put null.
		 ***/
		PricerUtils.setQuotationEffectiveTo(bean, pricer);
		// pricer.setBizUnitBean(new BizUnit(bean.getRegion()));// TODO
		pricer.setCost(Double.parseDouble(bean.getCost()));
		// pricer.setSalesCostType(SalesCostType.GetSCTypeByStr(bean.getSalesCostType()));//?
		if (bean.getShipmentValidity() != null && !bean.getShipmentValidity().equals("")) {
			Date date2 = QuoteUtil.convertStringToDate(bean.getShipmentValidity(), "dd/MM/yyyy");
			pricer.setShipmentValidity(date2);
		}
		pricer.setAvnetQcComments(bean.getAvnetQuoteCentreComments());
		pricer.setCost(Double.parseDouble(bean.getCost()));

		if (!QuoteUtil.isEmpty(bean.getShipmentValidity())) {
			pricer.setShipmentValidity(PricerUtils.convertToDate(bean.getShipmentValidity()));
		}
		// Fix enhancment 118 to set mpq, moq and term and conditions
		if (!QuoteUtil.isEmpty(bean.getMPQ())) {
			pricer.setMpq(Integer.parseInt(bean.getMPQ()));
		} else {
			pricer.setMpq(null);
		}

		/*if (!QuoteUtil.isEmpty(bean.getMOQ())) {
			pricer.setMoq(Integer.parseInt(bean.getMOQ()));
		} else {
			pricer.setMoq(null);
		}*/
		if ((bean.getMOQ() != null) && (!bean.getMOQ().equals(""))) {
			pricer.setMoq(Integer.parseInt(bean.getMOQ()));
		}else if ((bean.getMOV() != null) && (!bean.getMOV().equals(""))
				&& (bean.getCost() != null)
				&& (!bean.getCost().equals(""))
				&& (bean.getMPQ() != null) && (!bean.getMPQ().equals("")))
		{

			pricer.setMoq(PricerUtils.moqCalculate(Double.parseDouble(bean.getMOV()),
					Double.parseDouble(bean.getCost()),
					Integer.parseInt(bean.getMPQ())));
		}


		if (!QuoteUtil.isEmpty(bean.getMOV())) {
			pricer.setMov(Integer.parseInt(bean.getMOV()));
		} else {
			pricer.setMov(null);
		}

		pricer.setLeadTime(bean.getLeadTime());
		pricer.setTermsAndConditions(bean.getTermsAndConditions());

		pricer.setAvnetQcComments(bean.getAvnetQuoteCentreComments());

		if (!QuoteUtil.isEmpty(bean.getDisplayOnTop())) {
			pricer.setDisplayOnTop(Integer.parseInt(bean.getDisplayOnTop()));
		}
		if (!QuoteUtil.isEmpty(bean.getSpecialItemIndicator())) {
			pricer.setSpecialItemFlag(Integer.parseInt(bean.getSpecialItemIndicator()));
		}

		if (!QuoteUtil.isEmpty(bean.getAvailabletoSellQuantity())) {
			pricer.setAvailableToSellQty(Integer.parseInt(bean.getAvailabletoSellQuantity()));
		}
		if (!QuoteUtil.isEmpty(bean.getQtyControl())) {
			pricer.setQtyControl(Integer.parseInt(bean.getQtyControl()));
		}
		pricer.setQtyIndicator(bean.getQtyIndicator());
		String nidr = bean.getNewItemIndicator();
		if (!QuoteUtil.isEmpty(nidr)) {
			nidr = nidr.trim();
			if (nidr.equalsIgnoreCase("Yes")) {
				pricer.setNewItemIndicator(true);
			} else {
				pricer.setNewItemIndicator(false);
			}
		} else {
			pricer.setNewItemIndicator(false);
		}
		String aFlag = bean.getAllocationFlag();
		if (!QuoteUtil.isEmpty(aFlag)) {
			aFlag = aFlag.trim();
			if (aFlag.equalsIgnoreCase("Yes")) {
				pricer.setAllocationFlag(true);
			} else {
				pricer.setAllocationFlag(false);
			}
		}

		String aq = bean.getAQFlag();
		if (!QuoteUtil.isEmpty(aq)) {
			aq = aq.trim();
			if (aq.equalsIgnoreCase("Yes")) {
				pricer.setAqFlag(true);
			} else {
				pricer.setAqFlag(false);
			}
		}

		pricer.setPriceValidity(bean.getValidity());
		pricer.setLastUpdatedBy(user.getEmployeeId());
		pricer.setLastUpdatedOn(new Date());

	}

	private void fillPricerWithBean(SimplePricer pricer, PricerUploadTemplateBean bean, User user)
			throws ParseException {

		/**********
		 * be careful here** PAGE::::we use the Pricer's disQED fill the bean's
		 * QED ,because usr want to show the real value which they put. Page's
		 * QED value can not be changed,and if it is null we can not use it or
		 * today to calculate the QEDto. Thus we need a confirmed QED value to
		 * calculate the QEDto, so Pricer's QED is the confirmed value and not
		 * be null. This Pricer's QED maintain the date user put in by uploading
		 * or date when user upload if put null.
		 ***/
		PricerUtils.setQuotationEffectiveTo(bean, pricer);
		// pricer.setBizUnitBean(new BizUnit(bean.getRegion()));// TODO
		// salesCostPricer.setSalesCostType(SalesCostType.GetSCTypeByStr(bean.getSalesCostType()));
		pricer.setCost(Double.parseDouble(bean.getCost()));
		// pricer.setSalesCostType(SalesCostType.GetSCTypeByStr(bean.getSalesCostType()));
		if (bean.getShipmentValidity() != null && !bean.getShipmentValidity().equals("")) {
			Date date2 = QuoteUtil.convertStringToDate(bean.getShipmentValidity(), "dd/MM/yyyy");
			pricer.setShipmentValidity(date2);
		}

		pricer.setAvnetQcComments(bean.getAvnetQuoteCentreComments());
		pricer.setCost(Double.parseDouble(bean.getCost()));

		pricer.setCostIndicator(bean.getOcostIndicator());

		if (!QuoteUtil.isEmpty(bean.getShipmentValidity())) {
			pricer.setShipmentValidity(PricerUtils.convertToDate(bean.getShipmentValidity()));
		}
		if (!QuoteUtil.isEmpty(bean.getMPQ())) {
			pricer.setMpq(Integer.parseInt(bean.getMPQ()));
		} else {
			pricer.setMpq(null);
		}

		if (!QuoteUtil.isEmpty(bean.getMOQ())) {
			pricer.setMoq(Integer.parseInt(bean.getMOQ()));
		} else {
			pricer.setMoq(null);
		}

		if (!QuoteUtil.isEmpty(bean.getMOV())) {
			pricer.setMov(Integer.parseInt(bean.getMOV()));
		} else {
			pricer.setMov(null);
		}

		pricer.setLeadTime(bean.getLeadTime());
		pricer.setTermsAndConditions(bean.getTermsAndConditions());

		pricer.setAvnetQcComments(bean.getAvnetQuoteCentreComments());

		if (!QuoteUtil.isEmpty(bean.getDisplayOnTop())) {
			pricer.setDisplayOnTop(Integer.parseInt(bean.getDisplayOnTop()));
		}
		if (!QuoteUtil.isEmpty(bean.getSpecialItemIndicator())) {
			pricer.setSpecialItemFlag(Integer.parseInt(bean.getSpecialItemIndicator()));
		}

		if (!QuoteUtil.isEmpty(bean.getAvailabletoSellQuantity())) {
			pricer.setAvailableToSellQty(Integer.parseInt(bean.getAvailabletoSellQuantity()));
		}
		if (!QuoteUtil.isEmpty(bean.getQtyControl())) {
			pricer.setQtyControl(Integer.parseInt(bean.getQtyControl()));
		}

		pricer.setVendorQuoteNumber(bean.getVendorQuoteNo());

		if (!QuoteUtil.isEmpty(bean.getVendorQuoteQty())) {
			pricer.setVendorQuoteQuantity(Integer.parseInt(bean.getVendorQuoteQty()));
		}

		pricer.setPriceValidity(bean.getValidity());

		pricer.setLastUpdatedBy(user.getEmployeeId());
		pricer.setLastUpdatedOn(new Date());

	}

	/**
	 * Sets the quantity break and quantity break price.
	 *
	 * @param bean
	 *            the bean
	 * @param qtbLst
	 *            the qtb lst
	 */
	private void setQuantityBreakToTemplateBean(PricerUploadTemplateBean bean, List<QuantityBreakPrice> qtbLst) {
		int maxItems = QuoteSBConstant.QUANTITY_ONE_MAX_ITEMS;
		int min = maxItems <= qtbLst.size() ? maxItems : qtbLst.size();
		for (int i = 0; i < min; i++) {
			try {
				Method method = bean.getClass().getMethod("setQuantityBreak" + (i + 1), String.class);
				method.invoke(bean, String.valueOf(qtbLst.get(i).getQuantityBreak()));
				// because QuantityBreakPrice is null when it is salescost
				// pricer. so no need to set when this is
				// null.
				if (null != qtbLst.get(i).getQuantityBreakPrice() && qtbLst.get(i).getQuantityBreakPrice() > 0) {
					method = bean.getClass().getMethod("setQuantityBreakPrice" + (i + 1), String.class);
					method.invoke(bean, String.valueOf(qtbLst.get(i).getQuantityBreakPrice()));
				}
				if (null != qtbLst.get(i).getSalesCost() && qtbLst.get(i).getSalesCost().doubleValue() > 0) {
					method = bean.getClass().getMethod("setQuantityBreakSalesCost" + (i + 1), String.class);
					method.invoke(bean, String.valueOf(qtbLst.get(i).getSalesCost()));
				}
				/*if (null != qtbLst.get(i).getSalesCostM() && null != qtbLst.get(i).getSalesCostM().getAmount()&& qtbLst.get(i).getSalesCostM().getAmount().doubleValue() > 0) {
					method = bean.getClass().getMethod("setQuantityBreakSalesCost" + (i + 1), String.class);
					method.invoke(bean, String.valueOf(qtbLst.get(i).getSalesCostM().getAmount()));
				}*/
				if (null != qtbLst.get(i).getSuggestedResale()
						&& qtbLst.get(i).getSuggestedResale().doubleValue() > 0) {
					method = bean.getClass().getMethod("setQuantityBreakSuggestedResale" + (i + 1), String.class);
					method.invoke(bean, String.valueOf(qtbLst.get(i).getSuggestedResale()));
				}
			} catch (Exception e) {
				LOG.log(Level.SEVERE,
						"Exception occuring in method setQuantityBreakToTemplateBean on invoking method() for material id : "
								+ bean.getMetarialId() + "::" + e.getMessage(),
						e);

			}
		}
	}

	/**
	 * Sets the quantity break and quantity break price.
	 *
	 * @param bean
	 *            the bean
	 * @param qtbLst
	 *            the qtb lst
	 * 
	 * @Column(name="SALES_COST") private BigDecimal salesCost;
	 * 
	 * @Column(name="SUGGESTED_RESALE") private BigDecimal suggestedResale;
	 *//*
		 * private void
		 * setQuantityBreakAndsalesCostAndResale(PricerUploadTemplateBean bean,
		 * List<QuantityBreakPrice> qtbLst) { int maxItems =
		 * QuoteSBConstant.QUANTITY_ONE_MAX_ITEMS; int min = maxItems <=
		 * qtbLst.size()?maxItems:qtbLst.size(); for(int i=0;i<min;i++){ try {
		 * Method method = bean.getClass().getMethod("setQuantityBreak"+(i+1),
		 * String.class); method.invoke(bean,
		 * String.valueOf(qtbLst.get(i).getQuantityBreak()));
		 * 
		 * method = bean.getClass().getMethod("setSalesCost"+(i+1),
		 * String.class); method.invoke(bean,
		 * String.valueOf(qtbLst.get(i).getSalesCost()));
		 * 
		 * method = bean.getClass().getMethod("setSuggestedResale"+(i+1),
		 * String.class); method.invoke(bean,
		 * String.valueOf(qtbLst.get(i).getSuggestedResale())); } catch
		 * (Exception e) { LOG.log(Level.SEVERE,
		 * "Exception occuring in method setQuantityBreakAndQuantityBreakPrice on invoking method() for material id : "
		 * +bean.getMetarialId()+"::"+ e.getMessage(), e);
		 * 
		 * } } }
		 */

	/**
	 * Removes the all pricer for selected mfr.
	 *
	 * @param mfr
	 *            the mfr
	 * @param user
	 *            the user
	 * @param count
	 *            the count
	 * @return true, if successful
	 */
	public boolean removeAllPricerForSelectedMfr(String mfr, User user, int[] count) {
		boolean isSuccess = true;
		int removedProgramCount = 0;
		int removedNormalCount = 0;
		int removedContractCount = 0;
		int removedSalesCostCount = 0;
		int removedSimpleCount = 0;

		try {
			ut.setTransactionTimeout(10000);
			ut.begin();

			String bizUnit = user.getDefaultBizUnit().getName();
			String sql = "DELETE FROM QuantityBreakPrice q  where q.materialDetail.bizUnitBean.name = ?1 and q.materialDetail.material.manufacturer.name = ?2";
			// David
			if (QuoteUtil.isEqual(true, user.isSalsCostAccessFlag())) {
				sql = "DELETE FROM QuantityBreakPrice q  where q.materialDetail.bizUnitBean.name = ?1 and q.materialDetail.material.manufacturer.name = ?2"
						+ " AND q.materialDetail.pricerType='" + PRICER_TYPE.SALESCOST.getName() + "'";
			} else if (QuoteUtil.isEqual(false, user.isSalsCostAccessFlag())) {
				sql = "DELETE FROM QuantityBreakPrice q  where q.materialDetail.bizUnitBean.name = ?1 and q.materialDetail.material.manufacturer.name = ?2"
						+ " AND q.materialDetail.pricerType<>'" + PRICER_TYPE.SALESCOST.getName() + "'";
			}
			Query query = em.createQuery(sql);
			query.setParameter(1, bizUnit);
			query.setParameter(2, mfr);

			query.executeUpdate();
			if (!QuoteUtil.isEqual(true, user.isSalsCostAccessFlag())) {
				sql = "DELETE FROM ProgramPricer p where p.bizUnitBean.name = ?1 and p.material.manufacturer.name = ?2 ";
				query = em.createQuery(sql);
				query.setParameter(1, bizUnit);
				query.setParameter(2, mfr);
				removedProgramCount = query.executeUpdate();

				sql = "DELETE FROM NormalPricer d where d.bizUnitBean.name = ?1 and d.material.manufacturer.name = ?2";
				query = em.createQuery(sql);
				query.setParameter(1, bizUnit);
				query.setParameter(2, mfr);
				removedNormalCount = query.executeUpdate();

				sql = "DELETE FROM ContractPricer c where c.bizUnitBean.name = ?1 and c.material.manufacturer.name = ?2";
				query = em.createQuery(sql);
				query.setParameter(1, bizUnit);
				query.setParameter(2, mfr);
				removedContractCount = query.executeUpdate();

				sql = "DELETE FROM SimplePricer c where c.bizUnitBean.name = ?1 and c.material.manufacturer.name = ?2";
				query = em.createQuery(sql);
				query.setParameter(1, bizUnit);
				query.setParameter(2, mfr);
				removedSimpleCount = query.executeUpdate();
			}
			if (!QuoteUtil.isEqual(false, user.isSalsCostAccessFlag())) {
				
				this.pricerUploadCommonSB.batchInsertIntoPricerDeleted(bizUnit, mfr, user);
				sql = "DELETE FROM SalesCostPricer c where c.bizUnitBean.name = ?1 and c.material.manufacturer.name = ?2";
				sql += " AND c.batchStatus < 0 ";
				query = em.createQuery(sql);
				query.setParameter(1, bizUnit);
				query.setParameter(2, mfr);
				removedSalesCostCount = query.executeUpdate();
			}

			count[0] = removedNormalCount;
			count[1] = removedProgramCount;
			count[2] = removedContractCount;
			count[3] = removedSalesCostCount;
			count[4] = removedSimpleCount;

			LOG.info(user.getEmployeeId()
					+ " removed Normal Pricer, Program Pricer, Contract Pricer, SalesCost Pricer, Simple Pricer"
					+ removedNormalCount + ", " + removedProgramCount + ", " + removedContractCount + ", "
					+ removedSalesCostCount + ", " + removedSimpleCount);

			ut.commit();

		} catch (Exception e) {
			try {
				ut.rollback();
				LOG.info(user.getEmployeeId() + " remove all pricer for selected mfr rolled back.");
				count[0] = 0;
				count[1] = 0;
				count[2] = 0;
				count[3] = 0;
				count[4] = 0;
				throw e;
			} catch (Exception e1) {
				LOG.log(Level.SEVERE, "Exception occuring in remove all pricer for selected mfr : " + mfr + ":user Id: "
						+ user.getEmployeeId() + "" + e.getMessage(), e);
			}
			isSuccess = false;
			return isSuccess;
		}

		ProgramItemUploadCounterBean countBean = new ProgramItemUploadCounterBean();
		countBean.setRemovedNormalCount(removedNormalCount);
		countBean.setRemovedProgramCount(removedProgramCount);
		countBean.setRemovedContractPriceCount(removedContractCount);
		countBean.setRemovedSalesCostPricerCount(removedSalesCostCount);
		countBean.setRemovedSimplePricerCount(removedSimpleCount);
		pricerUploadCommonSB.savePricerUploadSummary2Db(countBean, "RemoveAllPricerForSelectedMfr " + mfr, user);

		return isSuccess;
	}

	/**
	 * Removes the pricer.
	 *
	 * @param pricerType
	 *            the pricer type
	 * @param selectedBeans
	 *            the selected beans
	 * @param user
	 *            the user
	 * @param count
	 *            the count
	 * @return true, if successful
	 */
	public boolean removePricer(String pricerType, List<PricerUploadTemplateBean> selectedBeans, User user,
			int[] count) {
		boolean isSuccess = true;
		int removedProgramCount = 0;
		int removedNormalCount = 0;
		int removedContractCount = 0;
		int removedSalesCostCount = 0;
		int removedSimpleCount = 0;

		try {
			ut.setTransactionTimeout(10000);
			ut.begin();
			if (PRICER_TYPE.CONTRACT.getName().equalsIgnoreCase(pricerType)) { // remove
																				// contract
																				// pricer
				List<Long> contractIds = new ArrayList<Long>();
				for (PricerUploadTemplateBean bean : selectedBeans) {
					contractIds.add(bean.getContractPriceId());
				}
				LOG.info("contract pricer id " + contractIds.toString() + " would be removed");
				String sql = "DELETE FROM ContractPricer c where c.id in ?1 ";
				Query query = em.createQuery(sql);
				query.setParameter(1, contractIds);
				removedContractCount = query.executeUpdate();

			} else { // remove normal and program pricer
				// if(PRICER_TYPE.NORMAL.getName().equalsIgnoreCase(pricerType)
				// ||PRICER_TYPE.PROGRAM.getName().equalsIgnoreCase(pricerType))
				List<Long> materialIds = new ArrayList<Long>();
				for (PricerUploadTemplateBean bean : selectedBeans) {
					materialIds.add(bean.getMetarialDetailId());
				}

				LOG.info("material detail id " + materialIds.toString() + " would be removed");
				//if salescostpricer 
				if(PRICER_TYPE.SALESCOST.getName().equalsIgnoreCase(pricerType)) {
					StringBuilder strBuilder = new StringBuilder(" (");
					for (PricerUploadTemplateBean bean : selectedBeans) {
						strBuilder.append((bean.getMetarialDetailId())).append(",");
					}
					strBuilder.deleteCharAt(strBuilder.length()-1);
					strBuilder.append(")");
					this.pricerUploadCommonSB.insertIntoPricerDeletedByPricerIds(strBuilder.toString(), user);
				}
				String sql = "DELETE FROM QuantityBreakPrice q where q.materialDetail.id in ?1";
				Query query = em.createQuery(sql);
				query.setParameter(1, materialIds);
				query.executeUpdate();

				sql = "DELETE FROM Pricer d where  d.id in ?1";
				query = em.createQuery(sql);
				query.setParameter(1, materialIds);
				int countRemoved = query.executeUpdate();
				if (PRICER_TYPE.NORMAL.getName().equalsIgnoreCase(pricerType)) {
					removedNormalCount = countRemoved;
				} else if (PRICER_TYPE.PROGRAM.getName().equalsIgnoreCase(pricerType)) {
					removedProgramCount = countRemoved;
				} else if (PRICER_TYPE.SALESCOST.getName().equalsIgnoreCase(pricerType)) {
					removedSalesCostCount = countRemoved;
				} else if (PRICER_TYPE.SIMPLE.getName().equalsIgnoreCase(pricerType)) {
					removedSimpleCount = countRemoved;
				}
			}

			count[0] = removedNormalCount;
			count[1] = removedProgramCount;
			count[2] = removedContractCount;
			count[3] = removedSalesCostCount;
			count[4] = removedSimpleCount;

			LOG.info(user.getEmployeeId() + " removed Normal Pricer count =" + removedNormalCount
					+ ", Program Pricer count =" + removedProgramCount + ", Contract Pricer = " + removedContractCount
					+ ", SalesCost Pricer count =" + removedSalesCostCount + ", Simple Pricer count ="
					+ removedSimpleCount);

			ut.commit();

		} catch (Exception e) {
			try {
				ut.rollback();
				LOG.info(user.getEmployeeId() + " remove all pricer for selected mfr rolled back.");
				count[0] = 0;
				count[1] = 0;
				count[2] = 0;
				count[3] = 0;
				count[4] = 0;
				throw e;
			} catch (Exception e1) {
				LOG.log(Level.SEVERE, "Exception occuring in removePricer for pricerType : " + pricerType + ":user Id: "
						+ user.getEmployeeId() + "" + e.getMessage(), e);
			}
			isSuccess = false;
			return isSuccess;
		}

		ProgramItemUploadCounterBean countBean = new ProgramItemUploadCounterBean();
		countBean.setRemovedNormalCount(removedNormalCount);
		countBean.setRemovedProgramCount(removedProgramCount);
		countBean.setRemovedContractPriceCount(removedContractCount);
		countBean.setRemovedSalesCostPricerCount(removedSalesCostCount);
		countBean.setRemovedSimplePricerCount(removedSimpleCount);
		pricerUploadCommonSB.savePricerUploadSummary2Db(countBean, "Remove " + pricerType + "selected Pricer ", user);

		return isSuccess;
	}

	/**
	 * Convert nor and pg to bean.
	 *
	 * @param bean
	 *            the bean
	 * @param pg
	 *            the pg
	 * @return the pricer upload template bean
	 */
	private PricerUploadTemplateBean convertNorToBean(PricerUploadTemplateBean bean, NormalPricer pg) {
		bean.setMfr(pg.getMaterial().getManufacturer().getName());

		bean.setFullMfrPart(pg.getFullMfrPartNumber());
		if (pg.getCost() != null) {
			bean.setCost(PricerUtils.formatDouble(pg.getCost()));
		}
		if (pg.getCurrency() != null) {
			bean.setCurrency(pg.getCurrency().toString());
		}
		/*if(pg.getCostM() != null) {
			if(pg.getCostM().getAmount() != null) {
				bean.setCost(PricerUtils.formatBigDecimal(pg.getCostM().getAmount()));
			}
			if(pg.getCostM().getCurrency() != null) {
				bean.setCurrency(pg.getCostM().getCurrency().toString());
			}
		}*/
		if (pg.getCostIndicator() != null) {
			bean.setCostIndicator(pg.getCostIndicator().getName());
		}
		bean.setValidity(pg.getPriceValidity());
		bean.setShipmentValidity(QuoteUtil.convertDateToStr(pg.getShipmentValidity()));

		if (pg.getMpq() != null) {
			bean.setMPQ(String.valueOf(pg.getMpq()));
		}

		if (pg.getMoq() != null) {
			bean.setMOQ(String.valueOf(pg.getMoq()));
		}

		if (pg.getMov() != null) {
			bean.setMOV(String.valueOf(pg.getMov()));
		}

		bean.setLeadTime(pg.getLeadTime());

		bean.setQuotationEffectiveDate(QuoteUtil.convertDateToStr(pg.getDisplayQuoteEffDate()));

		bean.setTermsAndConditions(pg.getTermsAndConditions());
		return bean;
	}

	private PricerUploadTemplateBean convertPgToBean(PricerUploadTemplateBean bean, ProgramPricer pg) {
		bean.setMfr(pg.getMaterial().getManufacturer().getName());

		bean.setFullMfrPart(pg.getFullMfrPartNumber());
		if (pg.getCost() != null) {
			bean.setCost(PricerUtils.formatDouble(pg.getCost()));
		}
		if (pg.getCurrency() != null) {
			bean.setCurrency(pg.getCurrency().toString());
		}
		/*if(pg.getCostM() != null) {
			if(pg.getCostM().getAmount() != null) {
				bean.setCost(PricerUtils.formatBigDecimal(pg.getCostM().getAmount()));
			}
			if(pg.getCostM().getCurrency() != null) {
				bean.setCurrency(pg.getCostM().getCurrency().toString());
			}
		}*/
		if (pg.getCostIndicator() != null) {
			bean.setCostIndicator(pg.getCostIndicator().getName());
		}
		bean.setValidity(pg.getPriceValidity());
		bean.setShipmentValidity(QuoteUtil.convertDateToStr(pg.getShipmentValidity()));

		if (pg.getMpq() != null) {
			bean.setMPQ(String.valueOf(pg.getMpq()));
		}

		if (pg.getMoq() != null) {
			bean.setMOQ(String.valueOf(pg.getMoq()));
		}

		if (pg.getMov() != null) {
			bean.setMOV(String.valueOf(pg.getMov()));
		}

		bean.setLeadTime(pg.getLeadTime());

		bean.setQuotationEffectiveDate(QuoteUtil.convertDateToStr(pg.getDisplayQuoteEffDate()));

		bean.setTermsAndConditions(pg.getTermsAndConditions());
		return bean;
	}

}
