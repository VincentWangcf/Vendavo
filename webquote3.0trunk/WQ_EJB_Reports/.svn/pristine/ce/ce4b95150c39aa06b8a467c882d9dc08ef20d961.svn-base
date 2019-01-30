package com.avnet.emasia.webquote.reports.ejb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.DataAccess;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.ProductGroup;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.Team;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.ejb.MaterialSB;
import com.avnet.emasia.webquote.reports.vo.QuoteReportCriteria;
import com.avnet.emasia.webquote.reports.vo.RFQBacklogReportVo;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilities.common.Constants;
import com.avnet.emasia.webquote.utilities.util.DateUtil;
import com.avnet.emasia.webquote.utilities.util.QuoteUtil;

@Stateless
@LocalBean
public class QuoteCycleReportSB {
	private static final Logger LOG = Logger.getLogger(QuoteCycleReportSB.class
			.getName());

	public List<RFQBacklogReportVo> rfqBacklogReportVo = new ArrayList<RFQBacklogReportVo>();

	@PersistenceContext(unitName = "Server_Source")
	EntityManager em;

	@EJB
	UserSB userSB;

	@EJB
	MaterialSB materialSB;

	public List<RFQBacklogReportVo> getRfqBacklogReportVo() {
		return rfqBacklogReportVo;
	}

	public void setRfqBacklogReportVo(
			List<RFQBacklogReportVo> rfqBacklogReportVo) {
		this.rfqBacklogReportVo = rfqBacklogReportVo;
	}

	private List<RFQBacklogReportVo> convertReportBean(
			List<QuoteItem> qitemLst, List<User> qcps) {
		LOG.info("Convert to Quote Cycle Time report.");
		List<RFQBacklogReportVo> qvoLst = new ArrayList<RFQBacklogReportVo>();

		for (QuoteItem qi : qitemLst) {

			RFQBacklogReportVo rVo = new RFQBacklogReportVo();
			rVo.setAvnetQuotedN(qi.getQuoteNumber());
			if (qi.getStage().equalsIgnoreCase(Constants.QUOTE_STAGE_FINISH)) {
				rVo.setStage("1");// Finished.just only search finished and
									// invalided quote.
			} else {
				rVo.setStage("0");// Finished
			}
			if (qi.getStage().equalsIgnoreCase(Constants.QUOTE_STAGE_FINISH)) {
				rVo.setQuoted("1");
			} else {
				rVo.setQuoted("0");
			}

			if (qi.getStage().equalsIgnoreCase(Constants.QUOTE_STAGE_INVALID)) {
				rVo.setInvalid("1");
			} else {
				rVo.setInvalid("0");
			}

			if (qi.getStatus() != null
					&& qi.getStatus()
							.equalsIgnoreCase(Constants.RFQ_STATUS_RIT)) {
				rVo.setInternalTransfer("1");
			} else {
				rVo.setInternalTransfer("0");
			}

			if (qi.getStatus() != null
					&& qi.getStatus().equalsIgnoreCase(Constants.RFQ_STATUS_SQ)) {
				rVo.setSystemQuote("1");
			} else {
				rVo.setSystemQuote("0");
			}

			if (qi.getQuotedPrice() != null) {
				rVo.setQuotedPrice(String.valueOf(qi.getQuotedPrice()));
			}
			if (qi.getCost() != null) {
				rVo.setCost(String.valueOf(qi.getCost()));
			}
			if (qi.getQuotedQty() != null) {
				rVo.setAvnetQuotedQty(String.valueOf(qi.getQuotedQty()));
			}
			if (qi.getQuote() != null && qi.getSubmissionDate() != null) {
				
				rVo.setUploadTime(DateUtil.dateToString(qi.getSubmissionDate(), "dd/MM/yyyy hh:mm:ss"));
			}
			if (qi.getSentOutTime() != null) {
				rVo.setSentOutTime(DateUtil.dateToString(qi.getSentOutTime(), "dd/MM/yyyy hh:mm:ss"));
			}
			if (qi.getQuote() != null && qi.getQuote().getSales() != null
					&& qi.getQuote().getSales().getName() != null) {
				rVo.setSalemanName(qi.getQuote().getSales().getName());
			}
			if (qi.getQuote() != null && qi.getQuote().getTeam() != null) {
				rVo.setTeam(qi.getQuote().getTeam().getName());
			}

			if (qi.getRequestedPartNumber() != null
					&& qi.getRequestedMfr() != null
					&& qi.getRequestedMfr().getName() != null) {
				rVo.setMfrName(qi.getRequestedMfr().getName());
			}
			if (qi.getRequestedQty() != null) {
				rVo.setRequiredQty(String.valueOf(qi.getRequestedQty()));
			}
			if (qi.getTargetResale() != null) {
				rVo.setTargetResales(String.valueOf(qi.getTargetResale()));
			}
			rVo.setQuoteType(qi.getQuoteType());
			rVo.setCostIndicator(qi.getCostIndicator());
			rVo.setStatus(qi.getStatus());
			rVo.setCustomerName(qi.getSoldToCustomerFullName());
			rVo.setAvnetQuotedPN(qi.getQuotedPartNumber());
			rVo.setEnquirySegment(qi.getEnquirySegment());
			rVo.setFirstRFQCode(qi.getFirstRfqCode());
			rVo.setRevertVersion(qi.getRevertVersion());

			rVo.setQcPricer(qi.getLastUpdatedQcName());

			if (qi.getQuotedPrice() != null && qi.getQuotedQty() != null) {
				double quotedAmount = qi.getQuotedPrice() * qi.getQuotedQty();

				quotedAmount = (double) Math.round(quotedAmount * 100000) / 100000;
				rVo.setQuotedAmount(String.valueOf(quotedAmount));
			}

			if (qi.getQuote() != null) {
				if (qi.getSentOutTime() != null
						&& qi.getSubmissionDate() != null) {

					//Fix incidents INC0153484 June Guo 2015/05/18
					int newResponseTime = QuoteUtil.getDiffInhoursNoWeekends(qi.getSubmissionDate(),qi.getSentOutTime());  
					rVo.setResponseTime(String.valueOf(newResponseTime));
					
					
					int newWorkDays = QuoteUtil.getDay(newResponseTime);
					
					rVo.setResponseTimebyDay(String.valueOf(newWorkDays));

					if (newWorkDays <= 5) {
						rVo.setResponseTimegpgidays(String.valueOf("0-5"));
					} else if (newWorkDays <= 10) {
						rVo.setResponseTimegpgidays(String.valueOf("5-10"));
					} else if (newWorkDays <= 20) {
						rVo.setResponseTimegpgidays(String.valueOf("10-20"));
					} else {
						rVo.setResponseTimegpgidays(String.valueOf(">20"));
					}
				}
			}
			if (qi.getStatus() != null) {
				if (qi.getStatus().equalsIgnoreCase(Constants.RFQ_STATUS_AQ)) {
					rVo.setAq("Yes");
				} else {
					rVo.setAq("No");
				}
			} else {
				rVo.setAq("No");
			}

			if (qi.getQuoteNumber() != null) {
				if (qi.getQuoteNumber().equalsIgnoreCase(qi.getFirstRfqCode())) {
					rVo.setFirstRFQ("Yes");
				} else {
					rVo.setFirstRFQ("No");
				}
			}

			qvoLst.add(rVo);
		}

		return qvoLst;
	}

	public List<RFQBacklogReportVo> quoteCycleTmeSearch(
			QuoteReportCriteria criteria, List<BizUnit> bizUnits) {
		LOG.info("Quote cycle time search begin...");

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<QuoteItem> c = cb.createQuery(QuoteItem.class);
		Root<QuoteItem> quoteItem = c.from(QuoteItem.class);

		Join<QuoteItem, ProductGroup> productGroup = quoteItem.join("productGroup2", JoinType.LEFT);
		Join<QuoteItem, Quote> quote = quoteItem.join("quote");
		Join<Quote, Team> team = quote.join("team", JoinType.LEFT);
		Join<Material, Manufacturer> manufacturer = quoteItem.join("requestedMfr", JoinType.LEFT);
		c.orderBy(cb.desc(quoteItem.get("quoteNumber")));

		populateCriteria(bizUnits,c, criteria, quoteItem, productGroup, quote, team,manufacturer);

		TypedQuery<QuoteItem> q = em.createQuery(c);
		q.setFirstResult(0);
		List<QuoteItem> searchResult = q.getResultList();

		Collections.sort(searchResult, new Comparator<QuoteItem>() {
			public int compare(QuoteItem arg0, QuoteItem arg1) {
				if (arg0.getQuote().getId() == arg1.getQuote().getId()) {
					return (int) (arg1.getQuote().getId() - arg0.getQuote().getId());
				} else {
					return (int) (arg1.getId() - arg0.getId());
				}
			}
		});

		LOG.info("size:" + searchResult.size());

		String[] roleNameList = { "ROLE_QC_PRICING", "ROLE_QCP_MANAGER" };
		List<User> qcps = userSB.findAllQcpsForReports(roleNameList, bizUnits);

		List<RFQBacklogReportVo> vos = new ArrayList<RFQBacklogReportVo>();
		vos = convertReportBean(searchResult, qcps);

		LOG.info("Quote cycle time search ended.");
		return vos;
	}

	private void populateCriteria(List<BizUnit> bizUnits,CriteriaQuery c,
			QuoteReportCriteria criteria, Root<QuoteItem> quoteItem,
			Join<QuoteItem, ProductGroup> productGroup,
			Join<QuoteItem, Quote> quote, Join<Quote, Team> team,
			Join<Material, Manufacturer> manufacturer) {

		CriteriaBuilder cb = em.getCriteriaBuilder();

		List<Predicate> criterias = new ArrayList<Predicate>();
		if(bizUnits!=null&&bizUnits.size()>0){
			
			CriteriaBuilder.In<BizUnit> inBizUnit = cb.in(quote.<BizUnit>get("bizUnit"));
			
			for(BizUnit bizUnit : bizUnits){
				inBizUnit.value(bizUnit);
			}
			criterias.add(inBizUnit);
		}
		if (criteria.getStage() != null && criteria.getStage().size() != 0) {

			List<Predicate> predicates = new ArrayList<Predicate>();

			for (String stage : criteria.getStage()) {
				Predicate predicate = cb.like(quoteItem.<String> get("stage"),stage);
				predicates.add(predicate);
			}
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));

		} else {
			Predicate predicate = cb.like(quoteItem.<String> get("stage"), "");
			criterias.add(predicate);
		}

		if (criteria.getsTeam() != null && !criteria.getsTeam().equals("")) {
			criterias.add(cb.equal(cb.upper(team.<String> get("name")),criteria.getsTeam().toUpperCase()));
		}

		if (criteria.getsMfr() != null && !criteria.getsMfr().equals("")) {
			criterias.add(cb.equal(cb.upper(manufacturer.<String> get("name")),criteria.getsMfr().toUpperCase()));
		}

		if (criteria.getSentOutDateFrom() != null) {
			criterias.add(cb.greaterThanOrEqualTo(quoteItem.<Date> get("sentOutTime"),criteria.getSentOutDateFrom()));
		}

		if (criteria.getSentOutDateTo() != null) {
			criterias.add(cb.lessThanOrEqualTo(quoteItem.<Date> get("sentOutTime"),criteria.getSentOutDateTo()));
		}
		
		if (!QuoteUtil.isEmpty(criteria.getQcPricer())){
			//Fix INC0286383 ignore case when search for QC Owner june guo 2015/11/30
			criterias.add(cb.like(cb.upper(quoteItem.<String> get("lastUpdatedQcName")),"%"+criteria.getQcPricer().toUpperCase()+"%")); 
		}
		
		if (criteria.getDataAccesses() != null
				&& criteria.getDataAccesses().size() != 0) {

			List<Predicate> p0 = new ArrayList<Predicate>();

			for (DataAccess dataAccess : criteria.getDataAccesses()) {

				List<Predicate> p1 = new ArrayList<Predicate>();

				if (dataAccess.getManufacturer() != null) {
					Predicate predicate = cb.equal(manufacturer.<Long> get("id"), dataAccess.getManufacturer().getId());
					p1.add(predicate);
				}

				if (dataAccess.getMaterialType() != null) {
					Predicate predicate = cb.equal(quoteItem.<String> get("materialTypeId"),dataAccess.getMaterialType().getName());
					p1.add(predicate);
				}

				if (dataAccess.getProgramType() != null) {
					Predicate predicate = cb.equal(quoteItem.<String> get("programType"), dataAccess.getProgramType().getName());
					p1.add(predicate);
				}

				if (dataAccess.getProductGroup() != null) {
					Predicate predicate = cb.equal(productGroup.<Long> get("id"), dataAccess.getProductGroup().getId());
					Predicate predicate2 = cb.isNull(productGroup);
					p1.add(cb.or(predicate, predicate2));
				}

				if (dataAccess.getTeam() != null) {
					Predicate predicate = cb.equal(team.<String> get("name"),dataAccess.getTeam().getName());
					Predicate predicate2 = cb.isNull(team);
					p1.add(cb.or(predicate, predicate2));
				}

				// in case p1.size() is 0 (all dataAccess member is null), add
				// below fake expression to make
				// p1 has one element, otherwise p0 will not have p1 element
				if (p1.size() == 0) {
					Predicate predicate = cb.equal(
							manufacturer.<Long> get("id"),
							manufacturer.<Long> get("id"));
					p1.add(predicate);
				}

				p0.add(cb.and(p1.toArray(new Predicate[0])));

			}
			criterias.add(cb.and(p0.toArray(new Predicate[0])));
		}

		if (criterias.size() == 0) {

		} else if (criterias.size() == 1) {
			c.where(criterias.get(0));
		} else {
			c.where(cb.and(criterias.toArray(new Predicate[0])));
		}
	}
}
