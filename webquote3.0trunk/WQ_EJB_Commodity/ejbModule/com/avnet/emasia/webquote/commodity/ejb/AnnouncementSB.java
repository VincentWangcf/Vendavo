package com.avnet.emasia.webquote.commodity.ejb;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.avnet.emasia.webquote.entity.Announcement;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.utilities.DateUtils;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-01-14
 * 
 */
@Stateless

public class AnnouncementSB {

    @PersistenceContext(unitName = "Server_Source")
    private EntityManager em;
    private static Logger log = Logger.getLogger(AnnouncementSB.class.getName());
    
    private String findBySeq ="select a from Announcement a where a.sequence=:sequence and a.expirationDate>=:expirationDate and a.bizUnit=:bizUnit";
    private String findNextBySeq  ="select a from Announcement a  where a.sequence<:sequence and a.expirationDate>=:expirationDate and a.bizUnit=:bizUnit order by a.sequence DESC";
    private String findPrevBySeq ="select a from Announcement a  where a.sequence>:sequence and a.expirationDate>=:expirationDate and a.bizUnit=:bizUnit order by a.sequence ASC";
    private String findAll  ="select a from Announcement a where a.expirationDate>=:expirationDate and a.bizUnit=:bizUnit order by a.sequence desc";

    //@Interceptors(AuditTrailInterceptor.class)
    public void updateAnnoun(Announcement announ)
	{
		log.fine("AnnouncementSB | updateAnnoun");
		em.merge(announ);
	}
    //@Interceptors(AuditTrailInterceptor.class)
	public void createAnnoun(Announcement announ) 
	{
		log.fine("AnnouncementSB | createAnnoun");
		em.persist(announ);
	}
    //@Interceptors(AuditTrailInterceptor.class)
	public void deleteAnnoun(Announcement announ) 
	{
		log.fine("AnnouncementSB | deleteAnnoun");
		em.remove(em.merge(announ));
		em.flush();
	}
	
	/*
     * @return: return the max seq numbers
     */
	public Integer getMaxSeqNum(BizUnit bizUnit)
	{
		log.fine("AnnouncementSB | getMaxSeqNum");
		List<Announcement> list = getAllAnnouncement(bizUnit); 
		if(list!=null && list.size()>0)
		{
			Announcement annou = (Announcement)list.get(0);
			return annou.getSequence()+1;
		}
		else
			return 1;
		
	}
	/*
     * @return: return a list of announcement object
     */
	public List<Announcement> getAllAnnouncement(BizUnit bizUnit)
	{
		log.fine("AnnouncementSB | getAllAnnouncement");
		List<Announcement> list = null; 
		Query query = em.createQuery(findAll);
		query.setParameter("expirationDate", DateUtils.getCurrentAsiaDateObj());	
		query.setParameter("bizUnit", bizUnit);
		list=  query.getResultList();
		return list ;
	}
	
	/*
     * @param: seq sequence of announcement
     * @return: return a announcement object
     */
	public Announcement getAnnouncBySeq(int seq, BizUnit bizUnit)
	{
		log.fine("AnnouncementSB | getAnnouncBySeq");
		Query query = em.createQuery(findBySeq);
		query.setParameter("sequence", seq);
		query.setParameter("expirationDate", DateUtils.getCurrentAsiaDateObj());
		query.setParameter("bizUnit", bizUnit);
		return (Announcement)query.getSingleResult();
	}
    /*
     * @param: seq sequence of announcement
     * @return: return a announcement object
     */
	public Announcement getNextAnnounBySeq(int seq, BizUnit bizUnit)
	{
		log.fine("AnnouncementSB | getNextAnnounBySeq");
		Query query = em.createQuery(findNextBySeq);
		query.setParameter("sequence", seq);
		query.setParameter("expirationDate", DateUtils.getCurrentAsiaDateObj());
		query.setParameter("bizUnit", bizUnit);
		List results = query.getResultList();
		if(results==null || results.size()==0)
		{
			return null;
		}
		query.setFirstResult(0).setMaxResults(1);
		return (Announcement)query.getSingleResult();
	}
    /*
     * @param: seq sequence of announcement
     * @return: return a announcement object
     */
	public Announcement getPrevAnnounBySeq(int seq, BizUnit bizUnit)
	{
		log.fine("AnnouncementSB | getPrevAnnounBySeq");
		Query query = em.createQuery(findPrevBySeq);
		query.setParameter("sequence", seq);
		query.setParameter("expirationDate", DateUtils.getCurrentAsiaDateObj());
		query.setParameter("bizUnit", bizUnit);
		List results = query.getResultList();
		if(results==null || results.size()==0)
		{
			return null;
		}
		query.setFirstResult(0).setMaxResults(1);
		return (Announcement)query.getSingleResult();
	}
    /*
     * @return: return the total number of announcement
     */  
	public Integer getAnnounNum(BizUnit bizUnit)
	{
		log.fine("AnnouncementSB | getAnnounNum");
		int count = 0;
		List<Announcement> announList = getAllAnnouncement(bizUnit);
		if(announList!=null)
			count = announList.size();
		return count;
	}
	
	/*
	 *Decline the position of the current announcement.
	 *@param: seq : sequence of announcement
	 */
	public void downAnnouncement(int seq, BizUnit bizUnit)
	{
		log.fine("AnnouncementSB downAnnouncement: seq : "+ seq);

		Announcement nextA = getNextAnnounBySeq(seq,bizUnit);
		if(nextA==null)
			return;
		Announcement curA = getAnnouncBySeq(seq,bizUnit);
		int tempSep = curA.getSequence();
		curA.setSequence(nextA.getSequence());
		nextA.setSequence(tempSep);
		updateAnnoun(nextA);
		updateAnnoun(curA);
		
	}
	/*
	 *Ascend the position of the current announcement.
	 *@param: seq : sequence of announcement
	 */
	public void upAnnouncement(int seq, BizUnit bizUnit)
	{
		log.info("AnnouncementSB upAnnouncement : seq : "+ seq);
		
		Announcement preA = getPrevAnnounBySeq(seq, bizUnit);
		if(preA==null)
			return;
		Announcement curA = getAnnouncBySeq(seq, bizUnit);
		int tempSep = curA.getSequence();
		curA.setSequence(preA.getSequence());
		preA.setSequence(tempSep);
		updateAnnoun(preA);
		updateAnnoun(curA);
	}
}
