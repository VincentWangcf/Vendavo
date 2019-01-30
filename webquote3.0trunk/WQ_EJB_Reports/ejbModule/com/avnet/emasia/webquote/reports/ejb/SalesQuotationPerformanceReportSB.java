package com.avnet.emasia.webquote.reports.ejb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.avnet.emasia.webquote.entity.DrProjectHeader;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.ReportRecipient;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.reports.util.ReportsUtil;
import com.avnet.emasia.webquote.utilities.util.QuoteUtil;

@Stateless
@LocalBean
public class SalesQuotationPerformanceReportSB
{
	private static final Logger LOG = Logger.getLogger(SalesQuotationPerformanceReportSB.class.getName());
	private static final String RECIPIENT_CRITERIA_STR = "select r from ReportRecipient r where r.reportType =:rptType and r.bzUnit.name=:bzUnit";
	private static final String SEARCH_QI_STR = 
			"select qi from QuoteItem qi join qi.quote q join q.sales u join u.roles r " +
			"where r.name = 'ROLE_SALES_GM' " +
			"and qi.stage='FINISH' " +
			"and u.defaultBizUnit.name = :bzName " +
			"and qi.sentOutTime between :startTime and :endTime";
//	private final static String searchQiStr = 
//			"select qi from QuoteItem qi join qi.quote q join q.sales u join u.roles r " +
//			"where qi.stage='FINISH' " +
//			"and u.defaultBizUnit.name = :bzName " +
//			"and qi.sentOutTime between :startTime and :endTime";

	private static String searchSubQiStr = "select qi from QuoteItem qi join qi.quote q join q.sales u where qi.stage='FINISH' and u in :usrLst and u.defaultBizUnit.name = :bzName and qi.sentOutTime between :startTime and :endTime";
	
	@PersistenceContext(unitName = "Server_Source")
	private EntityManager em;
	
	/**
 	 * Finds recipient accord to report type.
 	 * @param reportTypeStr
 	 * @return
 	 */
 	public List<User> findRecipients(String reportTypeStr, String regionStr) 
 	{
 		List<User> usrLst = new ArrayList<User>();
 		TypedQuery<ReportRecipient> query = em.createQuery(RECIPIENT_CRITERIA_STR, ReportRecipient.class);
		query.setParameter("rptType", reportTypeStr);
		query.setParameter("bzUnit", regionStr);
		List<ReportRecipient> rptRecipientLst = (List<ReportRecipient>)query.getResultList();
		if (rptRecipientLst != null)
		{
			for (ReportRecipient rp : rptRecipientLst)
			{
				usrLst.add(rp.getUser());
			}
		}
		return usrLst;
 	}
 	
 	public List<QuoteItem> findQuotItems(String bzStr) 
 	{
 		TypedQuery<QuoteItem> query = em.createQuery(SEARCH_QI_STR, QuoteItem.class);
 		query.setParameter("bzName", bzStr);
 		query.setParameter("startTime", QuoteUtil.getPreviousSunday().getTime());
 		query.setParameter("endTime", QuoteUtil.getPreviousSatDay().getTime());
 		LOG.info("Submitted time start :" + QuoteUtil.getPreviousSunday() + " ended : " + QuoteUtil.getPreviousSatDay());
// 		List<QuoteItem> qitemLst = (List<QuoteItem>) query.setFirstResult(0).setMaxResults(5).getResultList();
 		List<QuoteItem> qitemLst = (List<QuoteItem>) query.getResultList();
 		LOG.info("quote Item size: " + qitemLst.size());
 		return qitemLst;
 	}
 	
 	public Map<Long, DrProjectHeader> findDrpjHeader(List<Long> lst) 
 	{
 		return ReportsUtil.findDrpjHeaderSalesQuote(lst, em);
 	}
 	
 	public List<QuoteItem> getSuborinateQuotes(List<User> uLst, String bzStr)
 	{
 		TypedQuery<QuoteItem> query = em.createQuery(searchSubQiStr, QuoteItem.class);
 		query.setParameter("bzName", bzStr);
 		query.setParameter("usrLst", uLst);
 		query.setParameter("startTime", QuoteUtil.getPreviousSunday().getTime());
 		query.setParameter("endTime", QuoteUtil.getPreviousSatDay().getTime());
// 		List<QuoteItem> qitemLst = (List<QuoteItem>) query.setFirstResult(0).setMaxResults(5).getResultList();
 		List<QuoteItem> qitemLst = (List<QuoteItem>) query.getResultList();
 		LOG.info("subordinate's quote Item size: " + qitemLst.size());
 		return qitemLst;
 	}
}

