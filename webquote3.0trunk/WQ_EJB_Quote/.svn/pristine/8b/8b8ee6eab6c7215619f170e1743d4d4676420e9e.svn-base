package com.avnet.emasia.webquote.quote.ejb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.SapMaterial;

@Stateless
@LocalBean
public class SapMaterialSB {
	
	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	
	private static final Logger LOG =Logger.getLogger(SapMaterialSB.class.getName());

	public List<String> findSapPartNumbersByMaterialID(long materialId) {
		String sql = "select s.sapPartNumber from SapMaterial s where  s.material.id=:materialId ";
		TypedQuery<String> query = em.createQuery(sql, String.class);		
		query.setParameter("materialId", materialId);
		return query.getResultList();
	}
	    
	public Set<Long> findDeletedSapPartNumbersByMaterials(List<Material> materails) {

		Set<Long> returnSet = new HashSet<Long>();
		if (materails == null || materails.size() == 0)
			return returnSet;
        //updated below by damon@20160413
			if (materails != null && materails.size() > 0) {
				LOG.info(" materials size:" + materails.size());
				for (Material m : materails) {
					LOG.info(" material id:" + m.getId());
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<SapMaterial> cq = cb.createQuery(SapMaterial.class);

					Root<SapMaterial> sapMaterial = cq.from(SapMaterial.class);
					List<Predicate> predicates = new ArrayList<Predicate>();

					Expression<Long> materialId = sapMaterial.<Material> get("material").<Long> get("id");
					// Expression<Boolean> deletionFlag = sapMaterial.<Boolean>
					// get("deletionFlag");
					List<Predicate> subPredicatesWhole = new ArrayList<Predicate>();

					subPredicatesWhole.add(cb.equal(materialId, m.getId()));

					if (subPredicatesWhole != null && subPredicatesWhole.size() > 0) {
						predicates.add(cb.or(subPredicatesWhole.toArray(new Predicate[0])));
					}

					// predicates.add(cb.isTrue(deletionFlag));
					cq.select(sapMaterial);
					cq.where(predicates.toArray(new Predicate[] {}));
					TypedQuery<SapMaterial> q = em.createQuery(cq);
					List<SapMaterial> resultList = q.getResultList();
					// LOG.info(" resultList size:"+resultList.size());
					String flagOne = "N";
					String flagTwo = "N";
					if (resultList != null && resultList.size() > 0) {
						for (SapMaterial sm : resultList) {
							if (sm.getDeletionFlag() != null && sm.getDeletionFlag() == true) {
								if (flagOne.equals("N")) {
									flagOne = "Y";
								}
							} else if (sm.getDeletionFlag() != null && sm.getDeletionFlag() == false) {
								if (flagTwo.equals("N")) {
									flagTwo = "Y";
								}
							}
						}

						if (flagOne.equals("Y") && (!flagTwo.equals("Y"))) {
							returnSet.add(resultList.get(0).getMaterial().getId());
						}

					}

				}
			}
		return returnSet;
	}
	
	
	public SapMaterial findSapMaterialBySapPartNumber(String sapPartNumber) {
		String sql = "select s from SapMaterial s where  s.sapPartNumber = :sapPartNumber ";
		TypedQuery<SapMaterial> query = em.createQuery(sql, SapMaterial.class);	
		query.setParameter("sapPartNumber", sapPartNumber);
		List<SapMaterial> sapMaterials = query.getResultList();
		return (sapMaterials != null && sapMaterials.size() > 0) ? sapMaterials.get(0) : null ;
	}
}
