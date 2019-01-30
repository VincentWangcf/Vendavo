package com.avnet.emasia.webquote.commodity.ejb;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.Query;
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
import com.avnet.emasia.webquote.entity.NormalPricer;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.utilities.util.QuoteUtil;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ContractPriceUploadSB extends PricerUploadCommonSB {

	private static final Logger LOG = Logger.getLogger(ContractPriceUploadSB.class.getName());

	public List<VerifyEffectiveDateResult> batchFindMaterialDetail(List<PricerUploadTemplateBean> lists, User user,
			List<Manufacturer> mfrLst) {
		long start = System.currentTimeMillis();
		List<VerifyEffectiveDateResult> results = new ArrayList<VerifyEffectiveDateResult>();
		BizUnit bz = user.getDefaultBizUnit();

		// once the record is large need to split too many batch to do query in
		// DB.
		int size = lists.size();
		int batchCount = size / PricerConstant.BATCH_SIZE;
		int mod = size % PricerConstant.BATCH_SIZE;
		if (mod != 0) {
			batchCount++;
		}

		List<PricerUploadTemplateBean> subItemLst = null;

		for (int m = 0; m < batchCount; m++) {
			if (PricerConstant.BATCH_SIZE * (m + 1) > size) {
				subItemLst = lists.subList(m * PricerConstant.BATCH_SIZE, size);
			} else {
				subItemLst = lists.subList(m * PricerConstant.BATCH_SIZE, PricerConstant.BATCH_SIZE * (m + 1));
			}

			/** method 1 */
			Set<String> fullPartNumSet = new HashSet<String>();
			Set<String> costIndicatorSet = new HashSet<String>();
			Set<String> currencySet = new HashSet<String>();
			Set<Long> mftIdSet = new HashSet<Long>();
			for (PricerUploadTemplateBean pBean : subItemLst) {
				String fullMfrPart = PricerUtils.escapeSQLIllegalCharacters(pBean.getFullMfrPart());
				fullPartNumSet.add(fullMfrPart);
				costIndicatorSet.add(pBean.getCostIndicator());
				currencySet.add(pBean.getCurrency());
				for (Manufacturer mfr : mfrLst) {
					if (mfr.getName().equalsIgnoreCase(pBean.getMfr())) {
						mftIdSet.add(mfr.getId());
						break;
					}
				}
			}
			if (mftIdSet.size() == 0 || fullPartNumSet.size() == 0 || costIndicatorSet.size() == 0
					|| currencySet.size() == 0) {
				return results;
			}
			String inMfr = PricerUtils.buildInSql(mftIdSet);
			String inFullPartNum = PricerUtils.buildInSql(fullPartNumSet);
			String inCostindicator = PricerUtils.buildInSql(costIndicatorSet);
			String incurrency = PricerUtils.buildInSql(currencySet);
			StringBuffer sb = new StringBuffer();
			// TODO 'CONTRACT' as MATERIAL_CATEGORY
			sb.append(" SELECT t2.ID, t2.PRICER_TYPE,  t2.DISPLAY_QUOTE_EFF_DATE, t1.FULL_MFR_PART_NUMBER, ");
			sb.append(
					" t2.PRICE_VALIDITY as PRICE_VALIDITY, t2.QUOTATION_EFFECTIVE_DATE, t2.QUOTATION_EFFECTIVE_TO,  t2.BIZ_UNIT as BIZ_UNIT, t2.COST_INDICATOR as COST_INDICATOR, t2.MATERIAL_ID,t1.MANUFACTURER_ID,t2.SOLD_CUSTOMER_ID as SOLD_TO_CODE ");
			sb.append(", t2.CURRENCY ");
			sb.append("  FROM PRICER t2, MATERIAL t1  where t2.PRICER_TYPE='" + PRICER_TYPE.CONTRACT.getName()
					+ "' AND t1.ID = t2.MATERIAL_ID  and t2.BIZ_UNIT='" + bz.getName() + "'");
			sb.append(" and  t1.MANUFACTURER_ID in " + inMfr + " and t1.FULL_MFR_PART_NUMBER in " + inFullPartNum
					+ " and " + " t2.COST_INDICATOR in " + inCostindicator + "");
			sb.append(" AND T2.CURRENCY in " + incurrency + "");
			Query query = em.createNativeQuery(sb.toString());
			List<?> nlist = query.getResultList();

			// Added by Punit for CPD Exercise
			String param = "contractPriceUpload";
			results = CommodityUtil.verifyEffectiveDateResult(results, mfrLst, nlist, param);

		}

		long end = System.currentTimeMillis();
		LOG.info("batchFindMaterialDetail contract take " + (end - start) + "ms");
		return results;
	}

	public List<ContractPricer> batchFindContractPrice(List<PricerUploadTemplateBean> beans, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ContractPricer> c = cb.createQuery(ContractPricer.class);

		Root<ContractPricer> contractPrice = c.from(ContractPricer.class);
		Join<NormalPricer, BizUnit> bizUnit = contractPrice.join("bizUnitBean");
		Join<NormalPricer, Material> material = contractPrice.join("material");
		Join<Material, Manufacturer> manufacturer = material.join("manufacturer");
		Join<ContractPricer, CostIndicator> costIndicator = contractPrice.join("costIndicator");
		Join<ContractPricer, Customer> soldToCustomer = contractPrice.join("soldtoCustomer", JoinType.LEFT);
		Join<ContractPricer, Customer> endCustomer = contractPrice.join("endCustomer", JoinType.LEFT);
		List<Predicate> predicates = new ArrayList<Predicate>();
		List<Predicate> p = new ArrayList<Predicate>();

		for (PricerUploadTemplateBean bean : beans) {

			Predicate p1 = cb.equal(manufacturer.<String>get("name"), bean.getMfr().toUpperCase());
			Predicate p2 = cb.equal(material.<String>get("fullMfrPartNumber"), bean.getFullMfrPart().toUpperCase());
			Predicate p3 = cb.equal(bizUnit.<String>get("name"), user.getDefaultBizUnit().getName());
			Predicate p4 = cb.equal(soldToCustomer.<String>get("customerNumber"), bean.getSoldToCode());
			Predicate p5 = cb.equal(cb.upper(costIndicator.<String>get("name")), bean.getCostIndicator().toUpperCase());
			Predicate p0 = cb.equal(contractPrice.<Currency>get("currency"), Currency.valueOf(bean.getCurrency()));
			Predicate p6 = null;
			if (!QuoteUtil.isEmpty(bean.getEndCustomerCode())) {
				p6 = cb.equal(endCustomer.<String>get("customerNumber"), bean.getEndCustomerCode());
			}

			if (p6 == null) {
				p.add(cb.and(p1, p2, p3, p4, p5, p0));
			} else {
				p.add(cb.and(p1, p2, p3, p4, p5, p6, p0));
			}
		}
		predicates.add(cb.or(p.toArray(new Predicate[0])));

		if (predicates.size() == 0) {

		} else if (predicates.size() == 1) {
			c.where(predicates.get(0));
		} else {
			c.where(predicates.toArray(new Predicate[0]));
		}

		TypedQuery<ContractPricer> q = em.createQuery(c);
		return q.getResultList();
	}

	public List<ContractPricer> findContractPriceofQED(PricerUploadTemplateBean bean, User user, String displayQED) {
		BizUnit bz = user.getDefaultBizUnit();

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ContractPricer> c = cb.createQuery(ContractPricer.class);

		Root<ContractPricer> contractPrice = c.from(ContractPricer.class);
		Join<NormalPricer, BizUnit> bizUnit = contractPrice.join("bizUnitBean");
		Join<NormalPricer, Material> material = contractPrice.join("material");
		Join<Material, Manufacturer> manufacturer = material.join("manufacturer");
		Join<ContractPricer, CostIndicator> costIndicator = contractPrice.join("costIndicator");
		Join<ContractPricer, Customer> soldtoCustomer = contractPrice.join("soldtoCustomer", JoinType.LEFT);
		Join<ContractPricer, Customer> endCustomer = contractPrice.join("endCustomer", JoinType.LEFT);
		List<Predicate> criterias = new ArrayList<Predicate>();
		Predicate p1 = cb.equal(manufacturer.<String>get("name"), bean.getMfr());
		Predicate p2 = cb.equal(material.<String>get("fullMfrPartNumber"), bean.getFullMfrPart());
		Predicate p3 = cb.equal(costIndicator.<String>get("name"), bean.getCostIndicator());
		Predicate p4 = cb.equal(bizUnit.<String>get("name"), bz.getName());
		Predicate p0 = cb.equal(contractPrice.<Currency>get("currency"), Currency.valueOf(bean.getCurrency()));
		criterias.add(p1);
		criterias.add(p2);
		criterias.add(p3);
		criterias.add(p4);
		criterias.add(p0);
		if (!QuoteUtil.isEmpty(displayQED)) {
			Predicate p5 = cb.equal(contractPrice.<Date>get("displayQuoteEffDate"),
					PricerUtils.convertToDate(displayQED));
			criterias.add(p5);
		} else {
			Predicate p5 = cb.isNull(contractPrice.<Date>get("displayQuoteEffDate"));
			criterias.add(p5);
		}
		if (!QuoteUtil.isEmpty(bean.getSoldToCode())) {
			Predicate p6 = cb.equal(soldtoCustomer.<String>get("customerNumber"), bean.getSoldToCode());
			criterias.add(p6);
		}
		if (!QuoteUtil.isEmpty(bean.getEndCustomerCode())) {
			Predicate p7 = cb.equal(endCustomer.<String>get("customerNumber"), bean.getEndCustomerCode());
			criterias.add(p7);
		}
		if (criterias.size() == 0) {

		} else if (criterias.size() == 1) {
			c.where(criterias.get(0));
		} else {
			c.where(cb.and(criterias.toArray(new Predicate[0])));
		}

		TypedQuery<ContractPricer> q = em.createQuery(c);
		return q.getResultList();
	}

	@Override
	public void insertUploadOperater(List<PricerUploadTemplateBean> items, String materialTypeStr, User user,
			List<Manufacturer> manufacturerLst, int[] countArr, PricerUploadParametersBean puBean) {
		for (PricerUploadTemplateBean bean : items) {
			if (QED_CHECK_STATE.INSERT.getName().equals(bean.getQedCheckState())) {
				this.insertContractPricer2Db(bean, user);
				countArr[6]++;
			} else if (QED_CHECK_STATE.UPDATE.getName().equals(bean.getQedCheckState())) {
				this.updateContractPricer2Db(bean, user);
				countArr[7]++;
			}
		}

	}

	@Override
	public void removeOnlyPricerOperater(List<PricerUploadTemplateBean> items, String pricerType, User user,
			int[] countArr) {
		this.removeOnlyContractPricer(items, user, countArr);
	}

	private void removeOnlyContractPricer(List<PricerUploadTemplateBean> beans, User user, int[] countArr) {
		StringBuffer sb = new StringBuffer("(");
		for (PricerUploadTemplateBean bean : beans) {
			sb.append(bean.getContractPriceId()).append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(")");
		String hql = "DELETE FROM Pricer e WHERE e.id in " + sb.toString();
		Query query = em.createQuery(hql);
		int rowCount = query.executeUpdate();
		countArr[2] = countArr[2] + rowCount;
	}

	private void insertContractPricer2Db(PricerUploadTemplateBean bean, User user) {
		Date date = new Date();
		ContractPricer contractPrice = new ContractPricer();
		ContractPricer cp = PricerUtils.convert(contractPrice, bean, user, date);
		cp.setCreatedBy(user.getEmployeeId());
		cp.setCreatedOn(date);
		em.persist(cp);
	}

	private void updateContractPricer2Db(PricerUploadTemplateBean bean, User user) {
		Date date = new Date();
		ContractPricer contractPrice = null;
		if (bean.getMetarialDetailId() > 0) {
			contractPrice = em.find(ContractPricer.class, bean.getMetarialDetailId());
		} else {
			// for the updating entity which not saved into DB but was be
			// inserted to DB before this row in same excel file
			List<ContractPricer> list = findContractPriceofQED(bean, user, bean.getQuotationEffectiveDate());
			Date qedDateInFile = null;
			String qedStringInFile = bean.getQuotationEffectiveDate();
			if (!QuoteUtil.isEmpty(qedStringInFile)) {
				qedDateInFile = PricerUtils.convertToDate(qedStringInFile);
			}
			if (null != list && list.size() > 0) {
				for (ContractPricer price : list) {
					Date displayQuoteEffDate = price.getDisplayQuoteEffDate();
					Date quotationEffectiveDate = price.getQuotationEffectiveDate();

					if (qedDateInFile != null && quotationEffectiveDate != null
							&& PricerUtils.isSameDate(qedDateInFile, quotationEffectiveDate)) {
						contractPrice = price;
					}
					if (displayQuoteEffDate == null) {
						contractPrice = price;
					}
				}
			}
		}
		if (null != contractPrice) {
			ContractPricer cp = PricerUtils.convert(contractPrice, bean, user, date);
			em.merge(cp);
		}
	}
}
