package com.avnet.emasia.webquote.user.ejb;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.ProductGroup;
import com.avnet.emasia.webquote.entity.Team;

/**
 * Session Bean implementation class UserSB
 */
@Stateless
@LocalBean
public class TeamSB {

	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	
	
	public List<Team> findAll(){
		
		TypedQuery<Team> query = em.createQuery("select t from Team t where t.bizUnit is not null and t.active = true order by t.name", Team.class);
		List<Team> teams = query.getResultList();
		
		return teams;
	}
	
	public List<Team> findByName(String key, List<BizUnit> bizUnits){
		
		StringBuffer sb = new StringBuffer("(");
		
		int i = 1; 
		for(BizUnit bizUnit : bizUnits){
			sb.append("'" + bizUnit.getName() + "'" );
			if(i < bizUnits.size()){
				sb.append(",");
			}
		}
		sb.append(")");
		TypedQuery<Team> query = em.createQuery("select t from Team t where t.active = true and t.name like :name and t.bizUnit.name in " + sb.toString() , Team.class);
		query.setParameter("name", "%" + key + "%");
		List<Team> teams = query.getResultList();
		
		return teams;
		
	}
	
	public List<Team> findByName(String key){
		TypedQuery<Team> query = em.createQuery("select t from Team t where t.active = true and UPPER(t.name) like :name", Team.class);
		query.setParameter("name", "%" + key.toUpperCase() + "%");
		List<Team> teams = query.setFirstResult(0).setMaxResults(10).getResultList();
		
		return teams;
	}

	public List<Team> findByBizUnits(List<BizUnit> bizUnits){
		
		List<String> bu = new ArrayList<String>();
		for(BizUnit bizUnit : bizUnits){
			bu.add(bizUnit.getName());
		}

		TypedQuery<Team> query = em.createQuery("select t from Team t join t.bizUnit b where t.active = true and b in :bu order by t.name ", Team.class);
		
		query.setParameter("bu", bu);
		
		List<Team> teams = query.getResultList();
		
		return teams;
		
	}
	
	/**
	 * 
	 * @author 916138
	 * 
	 * @param buName
	 * @return
	 */
	public List<Team> findByBizUnits(String buName){

		TypedQuery<Team> query = em.createQuery("select t from Team t join t.bizUnit b where t.active = true and b.name = :bu order by t.name ", Team.class);
		
		query.setParameter("bu", buName);
		
		List<Team> teams = query.getResultList();
		
		return teams;
		
	}
	
	/**
	 * 
	 * @param name
	 * @param bizUnits
	 * @return
	 */
	public Team findByNameAndBU(String name, List<BizUnit> bizUnits) {
		List<String> bu = new ArrayList<String>();
		for(BizUnit bizUnit : bizUnits){
			bu.add(bizUnit.getName());
		}

		TypedQuery<Team> query = em.createQuery("select t from Team t join t.bizUnit b where t.active = true and b in :bu and t.name = :name order by t.name ", Team.class);
		
		query.setParameter("bu", bu);
		query.setParameter("name", name);
		
		Team team = query.getSingleResult();

		return team;
	}
	
}
