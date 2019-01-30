package com.avnet.emasia.webquote.reports.ejb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.ReportRecipient;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.reports.vo.DailyRITVo;

@Stateless
@LocalBean
public class DailyRITReportSB
{
	private static final Logger LOG = Logger.getLogger(DailyRITReportSB.class
			.getName());
	private final static String  DAILY_CRITERIA_STR = "select q from QuoteItem q join q.quote b where q.orginalAuthGp > q.resaleMargin and q.status = 'RIT' and q.drmsNumber is not null and b.bizUnit.name=:rgn and (q.stage = 'PENDING' or (q.stage = 'FINISH' and q.sentOutTime between :startTime and :endTime))";
	private final static String  RECIPIENT_CRITERIA_STR = "select r from ReportRecipient r where r.reportType =:rptType and r.bzUnit.name=:bzUnit";
	private final static String OUT_STANDING_IT_CRITERIA_STR = "select q from QuoteItem q join q.quote b where b.bizUnit.name=:rgn and q.drmsNumber is not null and q.orginalAuthGp > q.resaleMargin and q.status = 'IT' and q.stage = 'PENDING'";
		
	@PersistenceContext(unitName = "Server_Source")
	EntityManager em;
	
	/**
	 * Finds quote item accord to current user's region.
	 * @param regionStr
	 * @return
	 */
 	public List<QuoteItem> findQuoteItem(String regionStr) {
		TypedQuery<QuoteItem> query = em.createQuery(DAILY_CRITERIA_STR, QuoteItem.class);
		query.setParameter("rgn", regionStr);
		query.setParameter("startTime", getStartTime().getTime());
		query.setParameter("endTime", getEndTime().getTime());
		List<QuoteItem> quoteItemLst = (List<QuoteItem>)query.getResultList();		
		return quoteItemLst;
	}
 	
 	
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
 	
 	public List<ReportRecipient> findRecipientsInfo(String reportTypeStr, String regionStr) 
 	{
 		TypedQuery<ReportRecipient> query = em.createQuery(RECIPIENT_CRITERIA_STR, ReportRecipient.class);
		query.setParameter("rptType", reportTypeStr);
		query.setParameter("bzUnit", regionStr);
		List<ReportRecipient> rptRecipientLst = (List<ReportRecipient>)query.getResultList();
		return rptRecipientLst;
 	}
 	/**
 	 * Gets start time of current day.
 	 * @return
 	 */
 	private Date getStartTime(){  
 		  Calendar todayStart = Calendar.getInstance();
 	      todayStart.set(Calendar.HOUR_OF_DAY, 0);
 	      todayStart.set(Calendar.MINUTE, 0);  
 	      todayStart.set(Calendar.SECOND, 0);  
 	      todayStart.set(Calendar.MILLISECOND, 0);
 	      return todayStart.getTime();
    }
      
 	/**
 	 * Gets end time of current day.
 	 * @return
 	 */
    private Date getEndTime(){
    	Calendar todayEnd = Calendar.getInstance();  
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);  
        todayEnd.set(Calendar.MINUTE, 59);  
        todayEnd.set(Calendar.SECOND, 59);  
        todayEnd.set(Calendar.MILLISECOND, 99);  
        return todayEnd.getTime();  
    }
    
    /**
     * Add recipient.
     * @param d
     */
    public void addUserToRecipient(DailyRITVo d) 
    {
    	List<User> uLst = d.getUserLst();
    	if (uLst != null)
    	{
    		for (User u : uLst)
    		{
    			ReportRecipient rp = new ReportRecipient();
    			rp.setBzUnit(d.getBizUnit());
    			rp.setReportType(d.getReportType());
    			rp.setUser(u);
    			em.persist(rp);
    			em.flush();
    		}
    		LOG.info("add recipient Successfully !");
    	}
    	
    }
    
    /**
	 * Deletes recipient from db.
	 * 
	 * @param 
	 */
	public void deleteRecipient(ReportRecipient[] rpArr) {
		for (ReportRecipient rp : rpArr) {
			ReportRecipient rpD = em.find(ReportRecipient.class, rp.getId());
			em.remove(rpD);
			LOG.info("Remove recipient |" + rp.getUser().getEmployeeId() + "| report type:" + rp.getReportType());
		}
	}
	
	/**
	 * Finds outstanding IT report.
	 * @param regionStr
	 * @return
	 */
	public List<QuoteItem> findOutstandingItQuoteItem(String regionStr) 
	{
		TypedQuery<QuoteItem> query = em.createQuery(OUT_STANDING_IT_CRITERIA_STR, QuoteItem.class);
		query.setParameter("rgn", regionStr);
		List<QuoteItem> quoteItemLst = (List<QuoteItem>)query.getResultList();		
		return quoteItemLst;
	}
	
    public static void main(String[] args)
    {
    }
}
