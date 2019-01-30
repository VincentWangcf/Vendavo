package com.avnet.emasia.webquote.audit.ejb;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


import com.avnet.emasia.webquote.audit.ejb.AuditSearchCriteria;
import com.avnet.emasia.webquote.entity.AuditFieldMapping;
import com.avnet.emasia.webquote.entity.AuditTrail;
import com.avnet.emasia.webquote.util.Constants;
/**
 * @author Tonmy,Li(906893)
 * @Created on 2012-12-25
 * 
 */
@Stateless
public class AuditSB {
	
    @PersistenceContext(unitName = "Server_Source")
    private EntityManager em;
    static Logger logger = Logger.getLogger("AuditSB");
    private static Map<String,String> auditFieldMapingMap;
    
    private final String AUDIT_TRAIL_FIND_All  ="select a from AuditTrail a";
    private final String AUDIT_FIELD_MAPPING_FIND_All  ="select a from AuditFieldMapping a";

    public AuditSB() {
	}

    public synchronized Map<String,String> getAuditFieldMappingMap()
    {
    	logger.fine("AuditSB | getAuditFieldMappingMap");
    	if(auditFieldMapingMap==null)
    		auditFieldMapingMap = createAuditFieldMappingMap();
    				 
    	return auditFieldMapingMap;
    }
    
	@SuppressWarnings("unchecked")
	public List<AuditTrail> getAllAuditTrails()
    {
        Query query = em.createQuery(AUDIT_TRAIL_FIND_All);
        return (List<AuditTrail>)query.getResultList();
    }
    
    @SuppressWarnings("unchecked")   
	public List<AuditTrail> search(AuditSearchCriteria auditSearchCriteria)
    {
    	logger.fine("AuditSB AuditSearchCriteria : " + auditSearchCriteria.toString());
    	
    	CriteriaBuilder cb = em.getCriteriaBuilder();
    	CriteriaQuery<AuditTrail> cq = cb.createQuery(AuditTrail.class);
    	
    	Root<AuditTrail> auditTrail = cq.from(AuditTrail.class);
    	List<Predicate> predicates = new ArrayList<Predicate>();
    	if(!auditSearchCriteria.getAction().isEmpty() && !Constants.DEFAULT_OPTION_ALL.equalsIgnoreCase(auditSearchCriteria.getAction()))
    	{
    		predicates.add(cb.equal(auditTrail.get("action"), auditSearchCriteria.getAction()));
    	}
		if(!auditSearchCriteria.getEmployeeName().isEmpty())
		{
			predicates.add(cb.like(cb.upper(auditTrail.<String>get("employeeName")), Constants.SIGN_LIKE+auditSearchCriteria.getEmployeeName().toUpperCase()+Constants.SIGN_LIKE));
		}		
		if(!auditSearchCriteria.getEmployeeId().isEmpty())
		{
			predicates.add(cb.equal(auditTrail.get("createdBy"), auditSearchCriteria.getEmployeeId()));
		}
		if(!auditSearchCriteria.getRecordNumber().isEmpty())
		{
			predicates.add(cb.like(cb.upper(auditTrail.<String>get("targetId")), Constants.SIGN_LIKE+auditSearchCriteria.getRecordNumber().toUpperCase()+Constants.SIGN_LIKE));
		}		
		if(auditSearchCriteria.getFromDate() !=null)
		{	
			predicates.add(cb.greaterThanOrEqualTo(auditTrail.<Date>get("createdOn"), auditSearchCriteria.getFromDate()));
		}
		if(auditSearchCriteria.getToDate()!=null )
		{
			predicates.add(cb.lessThanOrEqualTo(auditTrail.<Date>get("createdOn"), auditSearchCriteria.getToDate()));
		}
    	cq.select(auditTrail);
    	cq.where(predicates.toArray(new Predicate[]{}));
    	cq.orderBy(cb.desc(auditTrail.<Date>get("createdOn")));
    	TypedQuery<AuditTrail> q = em.createQuery(cq);
    	return q.getResultList();
    	
    	
//        Query query = em.createNamedQuery("AUDIT_Trail.search");
//        if(auditSearchCriteria !=null)
//        {
//        	if(!auditSearchCriteria.getAction().isEmpty())
//        		query.setParameter("action", auditSearchCriteria.getAction());
//        	if(!auditSearchCriteria.getEmployeeName().isEmpty())
//        		query.setParameter("employeeName", auditSearchCriteria.getEmployeeName());
//        	if(auditSearchCriteria.getFromDate() !=null)
//        		query.setParameter("fromDate", auditSearchCriteria.getFromDate());
//        	if(auditSearchCriteria.getToDate()!=null )
//        		query.setParameter("toDate", auditSearchCriteria.getToDate());
//        }
//        return (List<AuditTrail>)query.getResultList();
    }
    
    
	@SuppressWarnings("unchecked")
	public List<AuditFieldMapping> getAllFieldMappings()
    {
        Query query = em.createQuery(AUDIT_FIELD_MAPPING_FIND_All);
        return (List<AuditFieldMapping>)query.getResultList();
    }
	
	public Map<String,String> createAuditFieldMappingMap()
	{

		Map<String,String> auditFieldMap =  new HashMap<String,String>();
		List<AuditFieldMapping> fieldList = getAllFieldMappings();
		for(AuditFieldMapping am : fieldList)
		{
			auditFieldMap.put(am.getType().trim()+"|"+am.getKey().trim(), am.getValue());
		}
		return auditFieldMap;
	}
}
