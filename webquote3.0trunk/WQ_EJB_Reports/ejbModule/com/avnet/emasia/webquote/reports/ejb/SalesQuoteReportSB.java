package com.avnet.emasia.webquote.reports.ejb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.UserTransaction;

import com.avnet.emasia.webquote.entity.DrProjectHeader;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.ReportRecipient;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.reports.util.ReportsUtil;
import com.avnet.emasia.webquote.utilities.util.QuoteUtil;

@Stateless
@LocalBean
//@TransactionManagement(TransactionManagementType.BEAN)
public class SalesQuoteReportSB
{

	private static final Logger LOG = Logger.getLogger(SalesQuoteReportSB.class
			.getName());
	private static final String RECIPIENT_CRITERIA_STR = "select r from ReportRecipient r where r.reportType =:rptType";
	private static final String SERACH_QI_STR = "select qi from QuoteItem qi join qi.quote q where qi.stage='FINISH' and qi.sentOutTime between :startTime and :endTime";

	@PersistenceContext(unitName = "Server_Source")
	private EntityManager em;

//	@Resource
//	private UserTransaction ut;

	/**
	 * Finds recipient accord to report type.
	 * 
	 * @param reportTypeStr
	 * @return
	 */
	public List<User> findRecipients(String reportTypeStr) 
	{
		List<User> usrLst = new ArrayList<User>();
		TypedQuery<ReportRecipient> query = em.createQuery(
				RECIPIENT_CRITERIA_STR, ReportRecipient.class);
		query.setParameter("rptType", reportTypeStr);
		List<ReportRecipient> rptRecipientLst = (List<ReportRecipient>) query
				.getResultList();
		if (rptRecipientLst != null)
		{
			for (ReportRecipient rp : rptRecipientLst)
			{
				usrLst.add(rp.getUser());
			}
		}
		return usrLst;
	}

	public List<QuoteItem> findQuotItems() 
	{
		// ut.setTransactionTimeout(100000000);
		// ut.begin();
		TypedQuery<QuoteItem> query = em.createQuery(SERACH_QI_STR,
				QuoteItem.class);
		query.setParameter("startTime", QuoteUtil.getPreviousSunday());
		query.setParameter("endTime", QuoteUtil.getPreviousSatDay());
		// List<QuoteItem> qitemLst = (List<QuoteItem>)
		// query.setFirstResult(0).setMaxResults(500).getResultList();
		List<QuoteItem> qitemLst = (List<QuoteItem>) query.getResultList();
		LOG.info("quote Item size: " + qitemLst.size());
		// ut.commit();
		return qitemLst;
	}

	public Map<Long, DrProjectHeader> findDrpjHeader(List<Long> lst)
	{
		return ReportsUtil.findDrpjHeaderSalesQuote(lst, em);
	}

}
