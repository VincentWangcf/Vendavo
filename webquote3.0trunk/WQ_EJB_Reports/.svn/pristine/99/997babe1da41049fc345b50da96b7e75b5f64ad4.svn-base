package com.avnet.emasia.webquote.reports.ejb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
import com.avnet.emasia.webquote.entity.MaterialType;
import com.avnet.emasia.webquote.entity.ProductGroup;
import com.avnet.emasia.webquote.entity.ProgramType;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.Role;
import com.avnet.emasia.webquote.entity.Team;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.ejb.MaterialSB;
import com.avnet.emasia.webquote.reports.vo.RFQBacklogReportVo;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilities.common.Constants;
import com.avnet.emasia.webquote.utilities.util.QuoteUtil;

@Stateless
@LocalBean
public class RFQBacklogReportSB {
	
	private static final Logger LOG = Logger.getLogger(RFQBacklogReportSB.class.getName());

	@EJB
	MaterialSB materialSB;
	
	@EJB
	UserSB userSB;

	public List<RFQBacklogReportVo> rfqBacklogReportVo = new ArrayList<RFQBacklogReportVo>();

	@PersistenceContext(unitName = "Server_Source")
	EntityManager em;

	public List<RFQBacklogReportVo> getRfqBacklogReportVo() {
		return rfqBacklogReportVo;
	}

	public void setRfqBacklogReportVo(
			List<RFQBacklogReportVo> rfqBacklogReportVo) {
		this.rfqBacklogReportVo = rfqBacklogReportVo;
	}

	private List<RFQBacklogReportVo> convertToReportBean(List<QuoteItem> mdLst) {
		List<RFQBacklogReportVo> resultLst = new ArrayList<RFQBacklogReportVo>();
		for (QuoteItem qit : mdLst) {
			RFQBacklogReportVo rfqBacklogReportVo = new RFQBacklogReportVo();
			if (qit.getQuote() != null) {
				if (qit.getQuote().getTeam() != null) {
					rfqBacklogReportVo.setTeam(qit.getQuote().getTeam()
							.getName());
				}

				if (qit.getQuote().getSales() != null) {
					rfqBacklogReportVo.setSalemanName(qit.getQuote().getSales()
							.getName());
				}

				if (qit.getQuote().getEndCustomer() != null) {
					rfqBacklogReportVo.setCustomerName(qit.getQuote()
							.getEndCustomer().getName());
				}
				rfqBacklogReportVo.setFormNo(qit.getQuote().getFormNumber());
				rfqBacklogReportVo.setYourReference(qit.getQuote()
						.getYourReference());
				rfqBacklogReportVo.setEndCustomerName(qit.getEndCustomerFullName());
			}
			rfqBacklogReportVo.setQuoteType(qit.getQuoteType());

			if (qit.getRequestedPartNumber() != null) {
				if (qit.getRequestedMfr() != null) {
					rfqBacklogReportVo.setMfr(qit.getRequestedMfr());
				}
				rfqBacklogReportVo.setFullPartNo(qit.getRequestedPartNumber());
			}

			// reportBean.setQcPricer(qit.)
			long pending = System.currentTimeMillis()
					- qit.getSubmissionDate().getTime();
			int pendingDay = (int) (pending / (1000 * 60 * 60));
			rfqBacklogReportVo.setPendingTime(String.valueOf(pendingDay));
			if (pending >= 5) {
				rfqBacklogReportVo.setPending("Yes");
			} else {
				rfqBacklogReportVo.setPending("No");
			}

			rfqBacklogReportVo.setPendingFor(qit.getStage());

			resultLst.add(rfqBacklogReportVo);
		}
		return resultLst;
	}

	public List<RFQBacklogReportVo> searchReport(
			RFQBacklogReportVo reportCriteria) {
		List<RFQBacklogReportVo> resultLst = new ArrayList<RFQBacklogReportVo>();

		String sqlStr = "select m from QuoteItem m join m.quote b where b.sales=:currentUser ";

		if (reportCriteria.getMfrLst() != null
				&& reportCriteria.getMfrLst().size() > 0) {
			sqlStr += "and m.quotedMaterial in :mfrLst ";
		}

		if (reportCriteria.getPgLst() != null
				&& reportCriteria.getPgLst().size() > 0) {
			sqlStr += "and m.productGroup1 in :pgLst ";
		}

		if (reportCriteria.getTeamLst() != null
				&& reportCriteria.getTeamLst().size() > 0) {
			sqlStr += "and b.team in :teamLst ";
		}

		if (reportCriteria.getMtTpyeLst() != null
				&& reportCriteria.getMtTpyeLst().size() > 0) {
			sqlStr += "and m.materialType in :mtypeLst ";
		}

		if (reportCriteria.getpTypeLst() != null
				&& reportCriteria.getPgLst().size() > 0) {
			sqlStr += "and m.programType in :pgTypeLst ";
		}

		TypedQuery<QuoteItem> query = em.createQuery(sqlStr, QuoteItem.class);
		query.setParameter("currentUser", reportCriteria.getCurrentUser());

		if (reportCriteria.getMfrLst() != null
				&& reportCriteria.getMfrLst().size() > 0) {
			query.setParameter("mfrLst", reportCriteria.getMfrLst());
		}

		if (reportCriteria.getPgLst() != null
				&& reportCriteria.getPgLst().size() > 0) {
			query.setParameter("pgLst", reportCriteria.getPgLst());
		}

		if (reportCriteria.getTeamLst() != null
				&& reportCriteria.getTeamLst().size() > 0) {
			query.setParameter("teamLst", reportCriteria.getTeamLst());
		}

		if (reportCriteria.getMtTpyeLst() != null
				&& reportCriteria.getMtTpyeLst().size() > 0) {
			query.setParameter("mtypeLst", reportCriteria.getMtTpyeLst());
		}

		if (reportCriteria.getpTypeLst() != null
				&& reportCriteria.getPgLst().size() > 0) {
			query.setParameter("pgTypeLst", reportCriteria.getpTypeLst());
		}

		List<QuoteItem> mdLst = query.getResultList();
		resultLst = convertToReportBean(mdLst);
		return resultLst;
	}

	public List<RFQBacklogReportVo> searchReportSale(
			RFQBacklogReportVo reportCriteria)  {
		List<RFQBacklogReportVo> resultLst = new ArrayList<RFQBacklogReportVo>();
		String sqlStr = "select m,d from QuoteItem m join m.quote b join DataAccess c join User d where b.sales=:currentUser ";
		Query query = em.createQuery(sqlStr);
		query.setParameter("currentUser", reportCriteria.getCurrentUser());
		// List<Object[]> qiArr =
		// query.setFirstResult(0).setMaxResults(500).getResultList();
		List<Object[]> qiArr = query.getResultList();
		QuoteItem qi = null;
		User user = null;
		if (qiArr != null && qiArr.size() > 0) {
			List<String> flagLst = new ArrayList<String>();
			String comparStr = "";
			for (Object[] qiObj : qiArr) {
				qi = (QuoteItem) qiObj[0];
				user = (User) qiObj[1];
				// //
				comparStr = "";
				if (qi.getQuote() != null) {
					if (qi.getQuote().getTeam() != null) {
						comparStr += qi.getQuote().getTeam().getName();
					}

					if (qi.getQuote().getSales() != null) {
						comparStr += qi.getQuote().getSales().getName();
					}

					comparStr += qi.getQuote().getFormNumber();
					comparStr += qi.getQuote().getYourReference();

					comparStr += qi.getSoldToCustomer().getName();

					comparStr += qi.getEndCustomerName();
				}

				comparStr += qi.getQuoteType();

				if (qi.getRequestedPartNumber() != null) {
					if (qi.getRequestedMfr() != null) {
						comparStr += qi.getRequestedMfr();
					}
					comparStr += qi.getRequestedPartNumber();
				}

				String roleName = "";
				if (user != null && user.getRoles() != null
						&& user.getRoles().size() > 0
						&& user.getRoles().get(0) != null) {
					roleName = user.getRoles().get(0).getName();
				}

				if (roleName.contains("ROLE_QC_PRICING")) {
					comparStr += user.getName();// qi.setQcPricer
				} else if (roleName.contains("ROLE_PM")) {
					comparStr += user.getName();// setPm(user)
				}

				long pending = System.currentTimeMillis()
						- qi.getSubmissionDate().getTime();
				int pendingDay = (int) (pending / (1000 * 60 * 60));
				if (pending >= 5) {
					comparStr += "Yes";
				} else {
					comparStr += "No";
				}

				comparStr += qi.getStage();// reportBean.setPendingFor
				// not ready yet
				// reportBean.setPndItnalTfTime();
				// Look Up at MFR to PM Mapping
				// reportBean.setBum(bum)
				// //
				if (flagLst.contains(comparStr)) {
					continue;
				} else {
					flagLst.add(comparStr);
				}

				resultLst.add(convertReportBean(qi, user));
			}
		}

		return resultLst;
	}

	private RFQBacklogReportVo convertReportBean(QuoteItem qitem, User user) {
		RFQBacklogReportVo reportBean = new RFQBacklogReportVo();

		if (qitem.getQuote() != null) {
			if (qitem.getQuote().getTeam() != null) {
				reportBean.setTeam(qitem.getQuote().getTeam().getName());
			}

			if (qitem.getQuote().getSales() != null) {
				reportBean
						.setSalemanName(qitem.getQuote().getSales().getName());
			}

			if (qitem.getQuote().getSoldToCustomer() != null) {
				reportBean.setCustomerName(qitem.getQuote().getSoldToCustomer()
						.getName());
			}
			reportBean.setFormNo(qitem.getQuote().getFormNumber());
			reportBean.setYourReference(qitem.getQuote().getYourReference());
		}

		if (qitem.getEndCustomer() != null) {
			reportBean.setEndCustomerName(qitem.getEndCustomerFullName());
		}
		reportBean.setQuoteType(qitem.getQuoteType());

		if (qitem.getRequestedPartNumber() != null) {
			if (qitem.getRequestedMfr() != null) {
				reportBean.setMfr(qitem.getRequestedMfr());
			}
			reportBean.setFullPartNo(qitem.getRequestedPartNumber());
		}

		String roleName = "";
		if (user != null && user.getRoles() != null
				&& user.getRoles().size() > 0 && user.getRoles().get(0) != null) {
			roleName = user.getRoles().get(0).getName();
		}

		if (roleName.contains("ROLE_QC_PRICING")) {
			reportBean.setQcPricer(user.getName());
		} else if (roleName.contains("ROLE_PM")) {
//			reportBean.setPm(user);
		}

		int pendingDay = QuoteUtil.workDays(new Date(qitem.getSubmissionDate().getTime()), new Date());
		reportBean.setPendingTime(String.valueOf(pendingDay));
		if (pendingDay >= 5) {
			reportBean.setPending("Yes");
		} else {
			reportBean.setPending("No");
		}

		reportBean.setPendingFor(qitem.getStage());
//		reportBean.setPm(user);
		return reportBean;
	}

	/*
	public List<RFQBacklogReportVo> searchCriteriaReportSale(
			RFQBacklogReportVo reportCriteria) throws Exception {
		String sqlStr = "select m from QuoteItem m join m.quote b where b.sales=:currentUser and m.stage='PENDING' ";

		if (reportCriteria.getSalemanName() != null
				&& !reportCriteria.getSalemanName().equalsIgnoreCase("")) {
			sqlStr += "and b.sales.name = :salemanName ";
		}

		if (reportCriteria.getFormNo() != null
				&& !reportCriteria.getFormNo().equalsIgnoreCase("")) {
			sqlStr += "and b.formNumber = :forNumber ";
		}

		if (reportCriteria.getYourReference() != null
				&& !reportCriteria.getYourReference().equalsIgnoreCase("")) {
			sqlStr += "and lower(b.yourReference) like :yourReference ";
		}

		if (reportCriteria.getCreateFrom() != null
				&& reportCriteria.getCreateTo() != null) {
			sqlStr += "and b.createdOn > :createFrom and b.createdOn < :createTo ";
		}

		Query query = em.createQuery(sqlStr);
		query.setParameter("currentUser", reportCriteria.getCurrentUser());

		if (reportCriteria.getSalemanName() != null
				&& !reportCriteria.getSalemanName().equalsIgnoreCase("")) {
			query.setParameter("salemanName", reportCriteria.getSalemanName());
		}

		if (reportCriteria.getFormNo() != null
				&& !reportCriteria.getFormNo().equalsIgnoreCase("")) {
			query.setParameter("forNumber", reportCriteria.getFormNo());
		}

		if (reportCriteria.getYourReference() != null
				&& !reportCriteria.getYourReference().equalsIgnoreCase("")) {
			query.setParameter("yourReference", "%"
					+ reportCriteria.getYourReference().toLowerCase() + "%");
		}
		if(reportCriteria.getBizUnits()!=null&&reportCriteria.getBizUnits().size()>0){
			sqlStr += "and b.bizUnit in :bizUnits ";
		}
		if (reportCriteria.getCreateFrom() != null
				&& reportCriteria.getCreateTo() != null) {
			query.setParameter("createFrom", getTimesmorning(reportCriteria.getCreateFrom()));
			query.setParameter("createTo", getTimesnight(reportCriteria.getCreateTo()));
		}
		if(reportCriteria.getBizUnits()!=null&&reportCriteria.getBizUnits().size()>0){
			Set<String> bu = new TreeSet<String>();
	    	for(BizUnit bizUnit : reportCriteria.getBizUnits()){
	    		bu.add(bizUnit.getName());
	    	}
			query.setParameter("bizUnits",bu);
		}
		List<QuoteItem> qiArr = query.getResultList();

		CriteriaBuilder cb = em.getCriteriaBuilder();
		List<Predicate> predicates = new ArrayList<Predicate>();
		CriteriaQuery<User> c = cb.createQuery(User.class);
		Root<User> user = c.from(User.class);
		// user.fetch("dataAccesses");
		// c.distinct(true);
		Join<User, DataAccess> dataAccess = user.join("dataAccesses",
				JoinType.LEFT);
		Join<DataAccess, Manufacturer> manufacturer = dataAccess.join(
				"manufacturer", JoinType.LEFT);
		Join<DataAccess, ProductGroup> productGroup = dataAccess.join(
				"productGroup", JoinType.LEFT);
		Join<DataAccess, MaterialType> materialType = dataAccess.join(
				"materialType", JoinType.LEFT);
		Join<DataAccess, ProgramType> programType = dataAccess.join(
				"programType", JoinType.LEFT);
		Join<DataAccess, Team> team = dataAccess.join("team", JoinType.LEFT);
		Join<User, Role> role = user.join("roles");

		List<Predicate> p0 = new ArrayList<Predicate>();

		for (QuoteItem qi : qiArr) {

			List<Predicate> p1 = new ArrayList<Predicate>();
			if (qi.getRequestedPartNumber() != null
					&& qi.getRequestedMfr() != null) {
				Predicate predicate = cb.or(cb.isNull(manufacturer),
						cb.equal(manufacturer, qi.getRequestedMfr()));
				p1.add(predicate);
			}

			if (qi.getProductGroup2() != null) {
				Predicate predicate = cb.or(cb.isNull(productGroup),
						cb.equal(productGroup, qi.getProductGroup2()));
				p1.add(predicate);
			}

			if (qi.getMaterialTypeId() != null) {
				Predicate predicate = cb.or(cb.isNull(materialType),
						cb.equal(materialType.<String>get("name"), qi.getMaterialTypeId()));
				p1.add(predicate);
			}

			if (qi.getProgramType() != null) {
				Predicate predicate = cb.or(cb.isNull(programType),
						cb.equal(programType.<String>get("name"), qi.getProgramType()));
				p1.add(predicate);
			}

			if (qi.getQuote() != null && qi.getQuote().getTeam() != null) {
				Predicate predicate = cb.or(cb.isNull(team),
						cb.equal(team, qi.getQuote().getTeam()));
				p1.add(predicate);
			}

			p0.add(cb.and(p1.toArray(new Predicate[0])));
		}

		predicates.add(cb.or(p0.toArray(new Predicate[0])));

		List<String> roleNameList = new ArrayList<String>();
		roleNameList.add("ROLE_QC_PRICING");
		roleNameList.add("ROLE_QCP_MANAGER");
		roleNameList.add("ROLE_PM");
		roleNameList.add("ROLE_PM_BUM");

		if (roleNameList.size() != 0) {
			CriteriaBuilder.In<String> in = cb.in(role.<String> get("name"));
			for (String r : roleNameList) {
				in.value(r);
			}
			predicates.add(in);
		}

		Predicate predicate = cb.equal(user.<Boolean> get("active"), true);
		predicates.add(predicate);

		if (predicates.size() == 0) {

		} else if (predicates.size() == 1) {
			c.where(predicates.get(0));
		} else {
			c.where(cb.and(predicates.toArray(new Predicate[0])));
		}

		TypedQuery<User> q = em.createQuery(c);
		List<User> qcs = q.getResultList();
		List<RFQBacklogReportVo> vos = new ArrayList<RFQBacklogReportVo>();
		vos = convertReportToBean(qiArr, qcs, reportCriteria);
		return vos;
	}

	public List<RFQBacklogReportVo> searchCriteriaReportManager(
			RFQBacklogReportVo reportCriteria) throws Exception {
		String sqlStr = "select m from QuoteItem m join m.quote b where b.sales in :currentUser and m.stage='PENDING' ";
		// String sqlStr =
		// "select m from QuoteItem m join m.quote b where 1=1 ";

		if (reportCriteria.getSalemanName() != null
				&& !reportCriteria.getSalemanName().equalsIgnoreCase("")) {
			sqlStr += "and b.sales.name = :salemanName ";
		}

		if (reportCriteria.getFormNo() != null
				&& !reportCriteria.getFormNo().equalsIgnoreCase("")) {
			sqlStr += "and b.formNumber = :forNumber ";
		}

		if (reportCriteria.getCreateFrom() != null
				&& reportCriteria.getCreateTo() != null) {
			sqlStr += "and b.createdOn > :createFrom and b.createdOn < :createTo ";
		}

		if (reportCriteria.getYourReference() != null
				&& !reportCriteria.getYourReference().equalsIgnoreCase("")) {
			sqlStr += "and lower(b.yourReference) like :yourReference ";
		}
		if(reportCriteria.getBizUnits()!=null&&reportCriteria.getBizUnits().size()>0){
			sqlStr += "and b.bizUnit in :bizUnits ";
		}
		Query query = em.createQuery(sqlStr);
		query.setParameter("currentUser", reportCriteria.getCurrentUser());

		if (reportCriteria.getSalemanName() != null
				&& !reportCriteria.getSalemanName().equalsIgnoreCase("")) {
			query.setParameter("salemanName", reportCriteria.getSalemanName());
		}

		if (reportCriteria.getFormNo() != null
				&& !reportCriteria.getFormNo().equalsIgnoreCase("")) {
			query.setParameter("forNumber", reportCriteria.getFormNo());
		}

		if (reportCriteria.getCreateFrom() != null
				&& reportCriteria.getCreateTo() != null) {
			query.setParameter("createFrom", getTimesmorning(reportCriteria.getCreateFrom()));
			query.setParameter("createTo", getTimesnight(reportCriteria.getCreateTo()));
		}

		if (reportCriteria.getYourReference() != null
				&& !reportCriteria.getYourReference().equalsIgnoreCase("")) {
			query.setParameter("yourReference", "%"
					+ reportCriteria.getYourReference().toLowerCase() + "%");
		}
		if(reportCriteria.getBizUnits()!=null&&reportCriteria.getBizUnits().size()>0){
			Set<String> bu = new TreeSet<String>();
	    	for(BizUnit bizUnit : reportCriteria.getBizUnits()){
	    		bu.add(bizUnit.getName());
	    	}
			query.setParameter("bizUnits",bu);
		}
		List<QuoteItem> qiArr = query.getResultList();

		CriteriaBuilder cb = em.getCriteriaBuilder();
		List<Predicate> predicates = new ArrayList<Predicate>();
		CriteriaQuery<User> c = cb.createQuery(User.class);
		Root<User> user = c.from(User.class);
		Join<User, DataAccess> dataAccess = user.join("dataAccesses",
				JoinType.LEFT);
		Join<DataAccess, Manufacturer> manufacturer = dataAccess.join(
				"manufacturer", JoinType.LEFT);
		Join<DataAccess, ProductGroup> productGroup = dataAccess.join(
				"productGroup", JoinType.LEFT);
		Join<DataAccess, MaterialType> materialType = dataAccess.join(
				"materialType", JoinType.LEFT);
		Join<DataAccess, ProgramType> programType = dataAccess.join(
				"programType", JoinType.LEFT);
		Join<DataAccess, Team> team = dataAccess.join("team", JoinType.LEFT);
		Join<User, Role> role = user.join("roles");

		List<Predicate> p0 = new ArrayList<Predicate>();

		for (QuoteItem qi : qiArr) {

			List<Predicate> p1 = new ArrayList<Predicate>();
			if (qi.getRequestedPartNumber() != null
					&& qi.getRequestedMfr() != null) {
				Predicate predicate = cb.or(cb.isNull(manufacturer),
						cb.equal(manufacturer, qi.getRequestedMfr()));
				p1.add(predicate);
			}

			if (qi.getProductGroup2() != null) {
				Predicate predicate = cb.or(cb.isNull(productGroup),
						cb.equal(productGroup, qi.getProductGroup2()));
				p1.add(predicate);
			}

			if (qi.getMaterialTypeId() != null) {
				Predicate predicate = cb.or(cb.isNull(materialType),
						cb.equal(materialType.<String>get("name"), qi.getMaterialTypeId()));
				p1.add(predicate);
			}

			if (qi.getProgramType() != null) {
				Predicate predicate = cb.or(cb.isNull(programType),
						cb.equal(programType.<String>get("name"), qi.getProgramType()));
				p1.add(predicate);
			}

			if (qi.getQuote() != null && qi.getQuote().getTeam() != null) {
				Predicate predicate = cb.or(cb.isNull(team),
						cb.equal(team, qi.getQuote().getTeam()));
				p1.add(predicate);
			}

			p0.add(cb.and(p1.toArray(new Predicate[0])));
		}

		predicates.add(cb.or(p0.toArray(new Predicate[0])));

		List<String> roleNameList = new ArrayList<String>();
		roleNameList.add("ROLE_QC_PRICING");
		roleNameList.add("ROLE_QCP_MANAGER");
		roleNameList.add("ROLE_PM");
		// roleNameList.add("ROLE_PM_SPM");
		roleNameList.add("ROLE_PM_BUM");
		// roleNameList.add("ROLE_PM_MD");

		if (roleNameList.size() != 0) {
			CriteriaBuilder.In<String> in = cb.in(role.<String> get("name"));
			for (String r : roleNameList) {
				in.value(r);
			}
			predicates.add(in);
		}

		Predicate predicate = cb.equal(user.<Boolean> get("active"), true);
		predicates.add(predicate);

		if (predicates.size() == 0) {

		} else if (predicates.size() == 1) {
			c.where(predicates.get(0));
		} else {
			c.where(cb.and(predicates.toArray(new Predicate[0])));
		}

		TypedQuery<User> q = em.createQuery(c);
		List<User> qcs = q.getResultList();
		List<RFQBacklogReportVo> vos = new ArrayList<RFQBacklogReportVo>();
		vos = convertReportToBean(qiArr, qcs, reportCriteria);
		return vos;
	}

	public List<RFQBacklogReportVo> searchCriteriaReportQc(
			RFQBacklogReportVo reportCriteria) throws Exception {
		String sqlStr = "select m from QuoteItem m join m.quote b where m.stage='PENDING' ";

		if (reportCriteria.getSalemanName() != null
				&& !reportCriteria.getSalemanName().equalsIgnoreCase("")) {
			sqlStr += "and b.sales.name = :salemanName ";
		}

		if (reportCriteria.getFormNo() != null
				&& !reportCriteria.getFormNo().equalsIgnoreCase("")) {
			sqlStr += "and b.formNumber = :forNumber ";
		}

		if (reportCriteria.getCreateFrom() != null
				&& reportCriteria.getCreateTo() != null) {
			sqlStr += "and b.createdOn > :createFrom and b.createdOn < :createTo ";
		}

		if (reportCriteria.getYourReference() != null
				&& !reportCriteria.getYourReference().equalsIgnoreCase("")) {
			sqlStr += "and lower(b.yourReference) like :yourReference ";

		}
		if(reportCriteria.getBizUnits()!=null&&reportCriteria.getBizUnits().size()>0){
			sqlStr += "and b.bizUnit in :bizUnits ";
		}
		Query query = em.createQuery(sqlStr);

		if (reportCriteria.getSalemanName() != null
				&& !reportCriteria.getSalemanName().equalsIgnoreCase("")) {
			query.setParameter("salemanName", reportCriteria.getSalemanName());
		}

		if (reportCriteria.getFormNo() != null
				&& !reportCriteria.getFormNo().equalsIgnoreCase("")) {
			query.setParameter("forNumber", reportCriteria.getFormNo());
		}

		if (reportCriteria.getCreateFrom() != null
				&& reportCriteria.getCreateTo() != null) {
			query.setParameter("createFrom", getTimesmorning(reportCriteria.getCreateFrom()));
			query.setParameter("createTo", getTimesnight(reportCriteria.getCreateTo()));
		}

		if (reportCriteria.getYourReference() != null
				&& !reportCriteria.getYourReference().equalsIgnoreCase("")) {
			query.setParameter("yourReference", "%"
					+ reportCriteria.getYourReference().toLowerCase() + "%");
		}
		if(reportCriteria.getBizUnits()!=null&&reportCriteria.getBizUnits().size()>0){
			Set<String> bu = new TreeSet<String>();
	    	for(BizUnit bizUnit : reportCriteria.getBizUnits()){
	    		bu.add(bizUnit.getName());
	    	}
			query.setParameter("bizUnits",bu);
		}
		List<QuoteItem> qiArr = query.getResultList();

		CriteriaBuilder cb = em.getCriteriaBuilder();
		List<Predicate> predicates = new ArrayList<Predicate>();
		CriteriaQuery<User> c = cb.createQuery(User.class);
		Root<User> user = c.from(User.class);
		Join<User, DataAccess> dataAccess = user.join("dataAccesses",
				JoinType.LEFT);
		Join<DataAccess, Manufacturer> manufacturer = dataAccess.join(
				"manufacturer", JoinType.LEFT);
		Join<DataAccess, ProductGroup> productGroup = dataAccess.join(
				"productGroup", JoinType.LEFT);
		Join<DataAccess, MaterialType> materialType = dataAccess.join(
				"materialType", JoinType.LEFT);
		Join<DataAccess, ProgramType> programType = dataAccess.join(
				"programType", JoinType.LEFT);
		Join<DataAccess, Team> team = dataAccess.join("team", JoinType.LEFT);
		Join<User, Role> role = user.join("roles");

		List<Predicate> p0 = new ArrayList<Predicate>();

		for (QuoteItem qi : qiArr) {

			List<Predicate> p1 = new ArrayList<Predicate>();

			if (qi.getRequestedPartNumber() != null
					&& qi.getRequestedMfr() != null) {
				Predicate predicate = cb.or(cb.isNull(manufacturer),
						cb.equal(manufacturer, qi.getRequestedMfr()));
				p1.add(predicate);
			}

			if (qi.getProductGroup2() != null) {
				Predicate predicate = cb.or(cb.isNull(productGroup),
						cb.equal(productGroup, qi.getProductGroup2()));
				p1.add(predicate);
			}

			if (qi.getMaterialTypeId() != null) {
				Predicate predicate = cb.or(cb.isNull(materialType),
						cb.equal(materialType.<String>get("name"), qi.getMaterialTypeId()));
				p1.add(predicate);
			}

			if (qi.getProgramType() != null) {
				Predicate predicate = cb.or(cb.isNull(programType),
						cb.equal(programType.<String>get("name"), qi.getProgramType()));
				p1.add(predicate);
			}

			if (qi.getQuote() != null && qi.getQuote().getTeam() != null) {
				Predicate predicate = cb.or(cb.isNull(team),
						cb.equal(team, qi.getQuote().getTeam()));
				p1.add(predicate);
			}

			p0.add(cb.and(p1.toArray(new Predicate[0])));
		}

		predicates.add(cb.or(p0.toArray(new Predicate[0])));

		List<String> roleNameList = new ArrayList<String>();
		roleNameList.add("ROLE_QC_PRICING");
		roleNameList.add("ROLE_QCP_MANAGER");
		roleNameList.add("ROLE_PM");
		// roleNameList.add("ROLE_PM_SPM");
		roleNameList.add("ROLE_PM_BUM");
		// roleNameList.add("ROLE_PM_MD");

		if (roleNameList.size() != 0) {
			CriteriaBuilder.In<String> in = cb.in(role.<String> get("name"));
			for (String r : roleNameList) {
				in.value(r);
			}
			predicates.add(in);
		}

		Predicate predicate = cb.equal(user.<Boolean> get("active"), true);
		predicates.add(predicate);

		if (predicates.size() == 0) {

		} else if (predicates.size() == 1) {
			c.where(predicates.get(0));
		} else {
			c.where(cb.and(predicates.toArray(new Predicate[0])));
		}

		TypedQuery<User> q = em.createQuery(c);
		List<User> qcs = q.getResultList();
		List<RFQBacklogReportVo> vos = new ArrayList<RFQBacklogReportVo>();
		vos = convertReportToBean(qiArr, qcs, reportCriteria);
		return vos;
	}
   */
	
	//Added by Alex Xu on 22 Oct 2014 for INC0040301--
		public List<RFQBacklogReportVo> searchCriteriaReportByRole(
				RFQBacklogReportVo reportCriteria,String roleType)  {
			String sqlStr="";
			if(Constants.BACKLOG_SEARCH_RPT_ROLE_TYPE_MGR.equals(roleType) || Constants.BACKLOG_SEARCH_RPT_ROLE_TYPE_SALES.equals(roleType)){
				sqlStr = "select m from QuoteItem m join m.quote b where b.sales=:currentUser and m.stage='PENDING' ";	
			}else if(Constants.BACKLOG_SEARCH_RPT_ROLE_TYPE_QC.equals(roleType)){
				sqlStr = "select m from QuoteItem m join m.quote b where m.stage='PENDING' ";
			}
			if (reportCriteria.getSalemanName() != null
					&& !reportCriteria.getSalemanName().equalsIgnoreCase("")) {
				sqlStr += "and b.sales.name = :salemanName ";
			}

			if (reportCriteria.getFormNo() != null
					&& !reportCriteria.getFormNo().equalsIgnoreCase("")) {
				sqlStr += "and b.formNumber = :forNumber ";
			}

			if (reportCriteria.getCreateFrom() != null
					&& reportCriteria.getCreateTo() != null) {
				sqlStr += "and b.createdOn > :createFrom and b.createdOn < :createTo ";
			}

			if (reportCriteria.getYourReference() != null
					&& !reportCriteria.getYourReference().equalsIgnoreCase("")) {
				sqlStr += "and lower(b.yourReference) like :yourReference ";

			}
			if(reportCriteria.getBizUnits()!=null&&reportCriteria.getBizUnits().size()>0){
				sqlStr += "and b.bizUnit in :bizUnits ";
			}
			Query query = em.createQuery(sqlStr);
			
			if(Constants.BACKLOG_SEARCH_RPT_ROLE_TYPE_MGR.equals(roleType) || Constants.BACKLOG_SEARCH_RPT_ROLE_TYPE_SALES.equals(roleType)){
				query.setParameter("currentUser", reportCriteria.getCurrentUser());
			}

			if (reportCriteria.getSalemanName() != null
					&& !reportCriteria.getSalemanName().equalsIgnoreCase("")) {
				query.setParameter("salemanName", reportCriteria.getSalemanName());
			}

			if (reportCriteria.getFormNo() != null
					&& !reportCriteria.getFormNo().equalsIgnoreCase("")) {
				query.setParameter("forNumber", reportCriteria.getFormNo());
			}

			if (reportCriteria.getCreateFrom() != null
					&& reportCriteria.getCreateTo() != null) {
				query.setParameter("createFrom", getTimesmorning(reportCriteria.getCreateFrom()));
				query.setParameter("createTo", getTimesnight(reportCriteria.getCreateTo()));
			}

			if (reportCriteria.getYourReference() != null
					&& !reportCriteria.getYourReference().equalsIgnoreCase("")) {
				query.setParameter("yourReference", "%"
						+ reportCriteria.getYourReference().toLowerCase() + "%");
			}
			if(reportCriteria.getBizUnits()!=null&&reportCriteria.getBizUnits().size()>0){
				Set<String> bu = new TreeSet<String>();
		    	for(BizUnit bizUnit : reportCriteria.getBizUnits()){
		    		bu.add(bizUnit.getName());
		    	}
				query.setParameter("bizUnits",bu);
			}
			List<QuoteItem> qiArr = query.getResultList();

			CriteriaBuilder cb = em.getCriteriaBuilder();
			List<Predicate> predicates = new ArrayList<Predicate>();
			CriteriaQuery<User> c = cb.createQuery(User.class);
			Root<User> user = c.from(User.class);
			Join<User, DataAccess> dataAccess = user.join("dataAccesses",
					JoinType.LEFT);
			Join<DataAccess, Manufacturer> manufacturer = dataAccess.join(
					"manufacturer", JoinType.LEFT);
			Join<DataAccess, ProductGroup> productGroup = dataAccess.join(
					"productGroup", JoinType.LEFT);
			Join<DataAccess, MaterialType> materialType = dataAccess.join(
					"materialType", JoinType.LEFT);
			Join<DataAccess, ProgramType> programType = dataAccess.join(
					"programType", JoinType.LEFT);
			Join<DataAccess, Team> team = dataAccess.join("team", JoinType.LEFT);
			Join<User, Role> role = user.join("roles");

			List<DataAccess> dataAccessList=new ArrayList<DataAccess>();
			long daId=1l;
			for(QuoteItem qi : qiArr){
				DataAccess da=new DataAccess();
				da.setId(daId++);
				da.setManufacturer(qi.getRequestedMfr());
				
				MaterialType mtrType=null;
				if(qi.getMaterialTypeId()!=null && !"".equals(qi.getMaterialTypeId().trim())){
					mtrType=new MaterialType();
					mtrType.setName(qi.getMaterialTypeId());
				}
				da.setMaterialType(mtrType);
				
				da.setProductGroup(qi.getProductGroup2());
				
				ProgramType prgmType=null;
				String prgmTypeName=qi.getProgramType();
				if(prgmTypeName!=null && !"".equals(prgmTypeName.trim())){
					prgmType=new ProgramType();
					long prgmTypeId=0l;
					if("Commodity".equals(prgmTypeName)){
						prgmTypeId=1l;
					}else if("Fire Sales".equals(prgmTypeName)){
						prgmTypeId=2l;
					}
					prgmType.setId(prgmTypeId);
					prgmType.setName(prgmTypeName);
				}
				da.setProgramType(prgmType);
				
				da.setTeam(qi.getQuote().getTeam());
				dataAccessList.add(da);
			}
			
			List<DataAccess> uniqueDataAccesses=userSB.removeDuplicateDataAccess(dataAccessList);
			
			List<Predicate> p0 = new ArrayList<Predicate>();
			
			if(uniqueDataAccesses!=null && uniqueDataAccesses.size()>0){
				for(DataAccess da:uniqueDataAccesses){
					List<Predicate> p1 = new ArrayList<Predicate>();
					if(da.getManufacturer()!=null){
						Predicate predicate = cb.or(cb.isNull(manufacturer),
								cb.equal(manufacturer, da.getManufacturer()));
						p1.add(predicate);
					}
					
					if(da.getProductGroup()!=null){
						Predicate predicate = cb.or(cb.isNull(productGroup),
								cb.equal(productGroup, da.getProductGroup()));
						p1.add(predicate);
					}
					
					if(da.getMaterialType()!=null){
						Predicate predicate = cb.or(cb.isNull(materialType),
								cb.equal(materialType, da.getMaterialType()));
						p1.add(predicate);
					}
					
					if(da.getProgramType()!=null){
						Predicate predicate = cb.or(cb.isNull(programType),
								cb.equal(programType, da.getProgramType()));
						p1.add(predicate);
					}
					
					if(da.getTeam()!=null){
						Predicate predicate = cb.or(cb.isNull(team),
								cb.equal(team, da.getTeam()));
						p1.add(predicate);
					}
					p0.add(cb.and(p1.toArray(new Predicate[0])));
				}
			}
			
			predicates.add(cb.or(p0.toArray(new Predicate[0])));

			List<String> roleNameList = new ArrayList<String>();
			roleNameList.add("ROLE_QC_PRICING");
			roleNameList.add("ROLE_QCP_MANAGER");
			roleNameList.add("ROLE_PM");
			roleNameList.add("ROLE_PM_BUM");

			if (roleNameList.size() != 0) {
				CriteriaBuilder.In<String> in = cb.in(role.<String> get("name"));
				for (String r : roleNameList) {
					in.value(r);
				}
				predicates.add(in);
			}

			Predicate predicate = cb.equal(user.<Boolean> get("active"), true);
			predicates.add(predicate);

			if (predicates.size() == 0) {

			} else if (predicates.size() == 1) {
				c.where(predicates.get(0));
			} else {
				c.where(cb.and(predicates.toArray(new Predicate[0])));
			}

			TypedQuery<User> q = em.createQuery(c);
			List<User> qcs = q.getResultList();
			List<RFQBacklogReportVo> vos = new ArrayList<RFQBacklogReportVo>();
			vos = convertReportToBean(qiArr, qcs, reportCriteria);
			return vos;
		}
		
	
	private List<RFQBacklogReportVo> convertReportToBean(
			List<QuoteItem> quoteItemList, List<User> users,
			RFQBacklogReportVo reportCriteria) {

		List<RFQBacklogReportVo> rfqBacklogReportVos = new ArrayList<RFQBacklogReportVo>();
		for (int i = 0; i < quoteItemList.size(); i++) {
			try {
				RFQBacklogReportVo rfqBacklogReportVo = new RFQBacklogReportVo();
				QuoteItem quoteItem = quoteItemList.get(i);
				Quote tempQuote = quoteItem.getQuote();
                if(quoteItem.getQuote().getTeam()!=null){
                	rfqBacklogReportVo.setTeam(quoteItem.getQuote().getTeam().getName());
                }
				rfqBacklogReportVo.setSalemanName(quoteItem.getQuote()
						.getSales().getName());
				rfqBacklogReportVo.setFormNo(quoteItem.getQuote()
						.getFormNumber());
				rfqBacklogReportVo.setYourReference(quoteItem.getQuote()
						.getYourReference());
				rfqBacklogReportVo.setCustomerName(quoteItem.getQuote()
						.getSoldToCustomer().getName());
				rfqBacklogReportVo.setEndCustomerName(quoteItem.getEndCustomerFullName());
				rfqBacklogReportVo.setQuoteType(quoteItem.getQuote().getQuoteType());

				rfqBacklogReportVo.setMfr(quoteItem.getRequestedMfr());
				rfqBacklogReportVo.setFullPartNo(quoteItem
						.getRequestedPartNumber());

				String qcPricer = "";
				String bumStr = "";
				String pmStr = "";
				for (int j = 0; j < users.size(); j++) {
					User user = users.get(j);
					List<DataAccess> dataAccesses = user.getDataAccesses();
					for (int k = 0; k < dataAccesses.size(); k++) {
						DataAccess dataAccess = dataAccesses.get(k);

						if (dataAccess.getManufacturer() == null
								&& dataAccess.getMaterialType() == null
								&& dataAccess.getProductGroup() == null
								&& dataAccess.getProgramType() == null
								&& dataAccess.getTeam() == null) {
							List<Role> roles = user.getRoles();
							for (Role role : roles) {
								if (role.getName().equals("ROLE_QC_PRICING")
										|| role.getName().equals(
												"ROLE_QCP_MANAGER")) {
									if (!qcPricer.contains(user.getName())) 
									{
										if(tempQuote.getBizUnit()!=null && user.getDefaultBizUnit()!=null && tempQuote.getBizUnit().getName().equalsIgnoreCase(user.getDefaultBizUnit().getName()))
										{
											qcPricer += "[" + user.getName() + "]";
										}
										
									}
								}
							}
						} else {
							if ((dataAccess.getManufacturer() == null || (quoteItem
									.getRequestedMfr()!=null&&dataAccess
									.getManufacturer().getId() == quoteItem
									.getRequestedMfr().getId()))
									&& (dataAccess.getMaterialType() == null || dataAccess
											.getMaterialType()
											.getName()
											.equals(quoteItem.getMaterialTypeId()))
									&& (dataAccess.getProductGroup() == null || (quoteItem
											.getProductGroup2()!=null&&dataAccess
											.getProductGroup().getId() == quoteItem
											.getProductGroup2().getId()))
									&& (dataAccess.getProgramType() == null || dataAccess
											.getProgramType().getName().equals(quoteItem.getProgramType()))
									&& (dataAccess.getTeam() == null || (quoteItem
											.getQuote().getTeam()!=null&&dataAccess
											.getTeam().getName().equals(quoteItem
											.getQuote().getTeam().getName())))) {
								if (!qcPricer.contains(user.getName())) {
									List<Role> roles = user.getRoles();
									for (Role role : roles) {
										if (role.getName().equals("ROLE_QC_PRICING")
												|| role.getName().equals(
														"ROLE_QCP_MANAGER")) {
											if(tempQuote.getBizUnit()!=null && user.getDefaultBizUnit()!=null && tempQuote.getBizUnit().getName().equalsIgnoreCase(user.getDefaultBizUnit().getName()))
											{
											    qcPricer += "[" + user.getName() + "]";
											}
										}
									}
								}
								
								if (!bumStr.contains(user.getName()))
								{
									List<Role> roles = user.getRoles();
									for (Role role : roles) {
										if (role.getName().equals("ROLE_PM_BUM")) {
											if(tempQuote.getBizUnit()!=null && user.getDefaultBizUnit()!=null && tempQuote.getBizUnit().getName().equalsIgnoreCase(user.getDefaultBizUnit().getName()))
											{
											    bumStr += "[" + user.getName() + "]";
											}
										}
									}
								}
								if (!pmStr.contains(user.getName()))
								{
									List<Role> roles = user.getRoles();
									for (Role role : roles) {
										if (role.getName().equals("ROLE_PM")) {
											if(tempQuote.getBizUnit()!=null && user.getDefaultBizUnit()!=null && tempQuote.getBizUnit().getName().equalsIgnoreCase(user.getDefaultBizUnit().getName()))
											{
											    pmStr += "[" + user.getName() + "]";
											}
										}
									}
								}
							}
						}
					}
				}

				rfqBacklogReportVo.setQcPricer(qcPricer);
				rfqBacklogReportVo.setPm(pmStr);
				rfqBacklogReportVo.setBum(bumStr);
				rfqBacklogReportVo.setPendingTime(Integer.toString(quoteItem
						.getPendingDay()));

				int pendingTime = quoteItem.getPendingDay();
				if (pendingTime >= 5) {
					rfqBacklogReportVo.setPending("Yes");
				} else {
					rfqBacklogReportVo.setPending("No");
				}

				rfqBacklogReportVo.setPendingFor(quoteItem.getStatus());

				if (rfqBacklogReportVo.getPendingFor().equals("IT")) {

					if (quoteItem.getPendingDayForIT() != -1) // -1 represents
																// getLastItUpdateTime
																// in db table
																// is null
					{
						rfqBacklogReportVo.setPndItnalTfTime(String
								.valueOf(quoteItem.getPendingDayForIT()));
					}
				}
				boolean matchSearchCriteria = true;
				if (reportCriteria.getPendingTime() != null
						&& reportCriteria.getPendingTime().length() > 0) {
					int reportPendingTime = Integer.parseInt(reportCriteria
							.getPendingTime());

					if (reportPendingTime != pendingTime) {
						matchSearchCriteria = false;
					}
				}
				if (reportCriteria.getQcPricer() != null
						&& reportCriteria.getQcPricer().length() > 0) {
					if (!qcPricer.contains(reportCriteria.getQcPricer())) {
						matchSearchCriteria = false;
					}
				}
				if (matchSearchCriteria) {
					rfqBacklogReportVos.add(rfqBacklogReportVo);
				}

			} catch (Exception e) {
				// LOG.severe("Error when assigning backlog report: " + e );
				LOG.log(Level.SEVERE, "error for converting to report bean for form"+reportCriteria.getFormNo()+" , salesman : "+reportCriteria.getSalemanName()+" , Error message : "+e.getMessage(), e);
			}
		}

		return rfqBacklogReportVos;
	}

	// Get 00:00:00 on the day time
	private Date getTimesmorning(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	// Get 23:59:59 on the day time
	public Date getTimesnight(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MINUTE, 59);
		return cal.getTime();
	}
}
