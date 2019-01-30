package com.avnet.emasia.webquote.codeMaintenance.ejb;


import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;


import com.avnet.emasia.webquote.entity.SystemCodeMaintenance;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2012-12-25
 * 
 */
@Stateless
public class CodeMaintenanceSB {
	
    @PersistenceContext(unitName = "Server_Source")
    private EntityManager em;
    static Logger logger = Logger.getLogger("CodeMaintenanceSB");
    final static String AUDIT_TRAIL_ACTION_OPTIONS= "auditAction";

   // private CriteriaBuilder cb;

    public CodeMaintenanceSB() {
    	//cb = em.getCriteriaBuilder();
	}

	@SuppressWarnings("unchecked")
	public List<SystemCodeMaintenance> getAllActions()
    {
        Query query = em.createNamedQuery("SYSTEM_CODE_MAINTENANCE.findByCategory");
        query.setParameter("category", AUDIT_TRAIL_ACTION_OPTIONS);
        return (List<SystemCodeMaintenance>)query.getResultList();
    }
    
  
}
