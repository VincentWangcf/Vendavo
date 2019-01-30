package com.avnet.emasia.webquote.quote.ejb;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.avnet.emasia.webquote.entity.FreeStock;
import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.Plant;
import com.avnet.emasia.webquote.entity.Quote;

/**
 * Session Bean implementation class FreeStockSB
 */
@Stateless
@LocalBean
public class FreeStockSB {
	
	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	
	private static final Logger LOG =Logger.getLogger(FreeStockSB.class.getName());

    /**
     * Default constructor. 
     */
    public FreeStockSB() {
        // TODO Auto-generated constructor stub
    }
    
    public List<FreeStock> findFreeStock(Material material, List<String> plants){
    	
       	String sql = "select f from FreeStock f where f.material.id = :materialId and f.id.plant in :plants";
		Query query = em.createQuery(sql);
		query.setParameter("materialId", material.getId());		
		query.setParameter("plants", plants);		
		return query.getResultList();	
    }

    public List<FreeStock> searchFreeStock(String mfr, String partNumber, List<String> plants){
    	
       	String sql = "select f from FreeStock f where";
       	sql += " f.material.manufacturer.name like :mfr ";
       	sql += " and f.material.fullMfrPartNumber like :partNumber ";
       	sql += " and f.id.plant in :plants";
		Query query = em.createQuery(sql);
		query.setParameter("mfr", "%"+mfr.toUpperCase()+"%");		
		query.setParameter("partNumber", "%"+partNumber.toUpperCase()+"%");		
		query.setParameter("plants", plants);		
		return query.getResultList();	
    }
}
