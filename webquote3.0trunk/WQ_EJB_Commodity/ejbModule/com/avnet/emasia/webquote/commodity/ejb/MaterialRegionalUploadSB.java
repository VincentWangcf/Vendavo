package com.avnet.emasia.webquote.commodity.ejb; 

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.MaterialRegional;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class MaterialRegionalUploadSB {
	
	private static final Logger LOG =Logger.getLogger(MaterialRegionalUploadSB.class.getName());

	@PersistenceContext(unitName = "Server_Source")
	public EntityManager em;
	
	public MaterialRegional findMRegional(Material material, String bizUnit) {
		if(material == null ||material.getId()<1 ||bizUnit == null){
			return null;
		}
		String sql = "SELECT m FROM MaterialRegional m WHERE m.material =:material AND m.bizUnit.name =:bizUnit" ;
		Query query = em.createQuery(sql);
		query.setParameter("material", material);
		query.setParameter("bizUnit", bizUnit.toUpperCase());
		//Object obj = query.getSingleResult();
		//(Material)
		List<MaterialRegional> list = query.getResultList();
		if(list.isEmpty()) {
			return null;
		}
		//Log
		return (MaterialRegional)list.get(0);
	}
	
	public MaterialRegional findMRegional(String mfr, String fullPartNumber, String bizUnit) {
		
		if(mfr == null ||fullPartNumber == null ||bizUnit ==null){
			return null;
		}
		String sql = "SELECT m FROM MaterialRegional m fetch join material mt WHERE mt.fullPartNumber =:fullPartNumber"
				+ "AND mt.manufacturer.name =:mfr AND m.bizUnit.name =:bizUnit" ;
		Query query = em.createQuery(sql);
		 
		query.setParameter("bizUnit", bizUnit.toUpperCase());
		query.setParameter("fullPartNumber", fullPartNumber.toUpperCase());
		query.setParameter("mfr", mfr.toUpperCase());
		//(Material)
		List<MaterialRegional> list = query.getResultList();
		if(list.isEmpty()) {
			return null;
		}
		//Log
		return (MaterialRegional)list.get(0);
	}
	//
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
		//LOG.info("TransactionManagementType.BEAN MaterialRegionalUploadSB::" + em.hashCode() +" " );
		return (Material)list.get(0);
		
	}
}
