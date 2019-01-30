package com.avnet.emasia.webquote.quote.ejb;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;

import com.avnet.emasia.webquote.entity.SapDpaCust;

@Stateless
@LocalBean
public class SapDpaCustSB {

	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	
	private static final Logger LOGGER = Logger.getLogger(SapDpaCustSB.class.getName());
	
	
	public SapDpaCust findSapDpaCust(String custGroupId, String region, String soldToCode) {
		TypedQuery<SapDpaCust> query = em.createQuery(
				"select s from SapDpaCust s where s.custGroupId = :custGroupId and s.bizUnit.name= :region and s.soldToCustNumber = :soldToCode",
				SapDpaCust.class);
		query.setParameter("custGroupId", custGroupId);
		query.setParameter("region", region);
		query.setParameter("soldToCode", soldToCode);
		List<SapDpaCust> custGroups = query.getResultList();
		if (custGroups.size() == 0) {
			return null;
		} else {
			return custGroups.get(0);
		}

	}



	/**
	* @Description: TODO
	* @param custGroupId
	* @param bizUnitName 
	* @param soldToCustomer
	* @param endCustomer
	* @return  boolean    
	* @throws  
	*/  
	public boolean isValidCustomerGroup(String custGroupId, String bizUnitName, String soldToCustCode, String endCustCode) {
		// TODO Auto-generated method stub
		try {
			List<SapDpaCust> sapDpaCustLsit = findSapDpaCusts(custGroupId, bizUnitName, soldToCustCode);

			List<String> endCustNumberList = new ArrayList<String>();
			for (SapDpaCust sapDpaCust : sapDpaCustLsit) {
				if (StringUtils.isBlank(sapDpaCust.getEndCustNumber())) {
					return true;
				} else {
					endCustNumberList.add(sapDpaCust.getEndCustNumber());
				}

			}

			if (endCustNumberList.size() > 0 && endCustCode != null && !"".equals(endCustCode.trim()) && endCustNumberList.contains(endCustCode)) {
				return true;
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "theow exception when call  isValidCustomerGroup function:", e);
		}
		return false;
	}



	/**   
	* @Description: TODO
	* @author 042659
	* @param custGroupId
	* @param bizUnitName
	* @param soldToCustomer
	* @return List<SapDpaCust>    
	* @throws  
	*/  
	private List<SapDpaCust> findSapDpaCusts(String custGroupId, String bizUnitName, String soldToCode) {
		TypedQuery<SapDpaCust> query = em.createQuery(
				"select s from SapDpaCust s where s.custGroupId = :custGroupId and s.bizUnit.name= :region and s.soldToCustNumber = :soldToCode",
				SapDpaCust.class);
		query.setParameter("custGroupId", custGroupId);
		query.setParameter("region", bizUnitName);
		query.setParameter("soldToCode", soldToCode);
		return query.getResultList();
	}



	/**   
	* @Description: TODO
	* @author 042659
	* @param custGroupId      
	* @return SapDpaCust 
	* @throws  
	*/  
	public SapDpaCust findSapDpaCustByCustGroupId(String custGroupId) {
		TypedQuery<SapDpaCust> query = em.createQuery(
				"select s from SapDpaCust s where s.custGroupId = :custGroupId",
				SapDpaCust.class);
		query.setParameter("custGroupId", custGroupId);
	
		List<SapDpaCust> custGroups = query.getResultList();
		if (custGroups.size() == 0) {
			return null;
		} else {
			return custGroups.get(0);
		}
		
	}
	
}
