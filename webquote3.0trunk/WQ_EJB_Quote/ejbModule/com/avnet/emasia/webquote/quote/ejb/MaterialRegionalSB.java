package com.avnet.emasia.webquote.quote.ejb;



import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.eclipse.jetty.util.log.Log;

import com.avnet.emasia.webquote.entity.BizUnit;

import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.MaterialRegional;
import com.avnet.emasia.webquote.entity.SalesCostPricer;


@Stateless
@LocalBean
public class MaterialRegionalSB {
	
	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
    /**
     * Default constructor. 
     */
    public MaterialRegionalSB() {
    }
    
    public Material findMaterial(String mfr, String fullPartNumber) {
		if(mfr == null ||fullPartNumber == null ){
			return null;
		}
		String sql = "SELECT m FROM Material m WHERE "
				+ " m.manufacturer.name = :mfr AND m.fullMfrPartNumber =:fullPartNumber" ;
		Query query = em.createQuery(sql);
		query.setParameter("fullPartNumber", fullPartNumber);
		query.setParameter("mfr", mfr);
		//LOG.info(mfr);
		List<Material> list = query.getResultList();
		if(list.isEmpty()) {
			return null;
		}
		return (Material)list.get(0);
		
	}
    
    //
    public Material findMaterialRegionalByPK(long id){
    	return em.find(Material.class, id);
    }
    
    public MaterialRegional createMaterialRegional(MaterialRegional materialRegional){
    	MaterialRegional m = em.merge(materialRegional);
    	em.flush();
    	return m;
    }
    //by material and bizUnit
    public MaterialRegional findMRegional(Material material, BizUnit bizUnit){
    	String sql = "select mr from MaterialRegional mr where mr.material = :material "
    			+ " and mr.bizUnit = :bizUnit";
    	TypedQuery<MaterialRegional>  query = em.createQuery(sql, MaterialRegional.class);
    	query.setParameter("material", material);
    	query.setParameter("bizUnit", bizUnit);
    	List<MaterialRegional> list = query.getResultList();
		if(list.isEmpty()) {
			return null;
		}
		//Log
		return (MaterialRegional)list.get(0);
    	 
    }
    
    //by MFR,FULLPARTNUM, 
   /* public MaterialRegional findMRegional(Material material, BizUnit bizUnit){
    	String sql = "select mr from MaterialRegional mr where mr.material = :material "
    			+ " and mr.bizUnit = :bizUnit";
    	TypedQuery<MaterialRegional>  query = em.createQuery(sql, MaterialRegional.class);
    	query.setParameter("material", material);
    	query.setParameter("bizUnit", bizUnit);
    	return query.getSingleResult();
    }*/
  

   

    
	
}