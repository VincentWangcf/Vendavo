package com.avnet.emasia.webquote.user.ejb;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.avnet.emasia.webquote.entity.Role;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.exception.AppException;
import com.avnet.emasia.webquote.exception.CommonConstants;

/**
 * Session Bean implementation class RoleSB
 */
@Stateless
@LocalBean
public class RoleSB {

	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	
	private static final Logger LOG = Logger.getLogger(RoleSB.class.getName());
	
	public Role save(Role role) throws AppException{
		
		if(role.getId() == 0){
			TypedQuery<Role> query = em.createQuery("select r from Role r where r.name=:name", Role.class);
			query.setParameter("name", role.getName());
			List<Role> roles = query.getResultList();
			if(roles.size() !=0){
				throw new AppException(CommonConstants.WQ_EJB_USR_AUTH_ROLE_NAME_ALREADY_EXISTS);
			}
		}
		
		role = em.merge(role);
		LOG.info("Saved Role: " + role.getName());
		return role;
	}
	
	
	public List<Role> findAll(){
		
		TypedQuery<Role> query = em.createQuery("select r from Role r order by r.name", Role.class);

		return query.getResultList();
		
	}
	
	public List<Role> findAllForUserAdmin(){
		
		TypedQuery<Role> query = em.createQuery("select r from Role r where r.name != 'ROLE_SYS_ADMIN' and r.active = true order by r.name", Role.class);

		return query.getResultList();
		
	}	


	public Role findRoleWithAppFunction(Role role) {
		
		TypedQuery<Role> query = em.createQuery("select r from Role r left join fetch r.appFunctions where r.id=:role", Role.class);
		
		query.setParameter("role", role.getId());
		
		return query.getSingleResult();

	}
	
	public List<User> findUserByRole(Role role) {
		
		TypedQuery<User> query = em.createQuery("select u from User u join u.roles r where r.id = :role order by u.name ", User.class);
		
		query.setParameter("role", role.getId());
		
		return query.getResultList();

	}	
	
	public List<User> wFindUserByRoleAndEmployeeId(Role role, String key) {
		
		TypedQuery<User> query = em.createQuery("select u from User u join u.roles r where r.id = :role and u.employeeId like :employeeId", User.class);
		query.setParameter("role", role.getId());
		query.setParameter("employeeId", "%"+key+"%");
		
		return query.getResultList();

	}	

	public List<User> wFindUserByRoleAndEmployeeName(Role role, String key) {
		TypedQuery<User> query = em.createQuery("select u from User u join u.roles r where r = :role and u.name like :name", User.class);
		query.setParameter("role", role);
		query.setParameter("name", "%"+key+"%");
		
		return query.getResultList();

	}	

	public Role findByName(String name) {
		Query query = em.createQuery("select r from Role r where r.name=:name");
		query.setParameter("name", name);
		Role role = null;
		try{
			role = (Role)query.getSingleResult();
		}catch(Exception e){
			LOG.log(Level.WARNING, "Multiple roles were found for role name " + name);
		}
		return role;
			
	}


	public void save(Role role, List<User> newUsers) throws AppException {
		List<User> oldUsers = findUserByRole(role);
		
		save(role);
		for(User oldUser : oldUsers){
    		boolean found = false;
    		for(User newUser : newUsers){
    			if(oldUser.getId() == newUser.getId()){
    				found = true;
    				break;
    			}
    		}
    		if(found ==false){
    			for(int i = 0; i < oldUser.getRoles().size(); i++){
    				if(role.getId() == oldUser.getRoles().get(i).getId()){
    					oldUser.getRoles().remove(i);
    				}
    			}
    			
    			em.merge(oldUser);
    			
    			LOG.info("Removd Role: " + role.getName() + " from User: " + oldUser.getEmployeeId());
    		}
    	}
    	
		
    	for(User newUser : newUsers){
    		boolean found = false;
    		for(User oldUser : oldUsers){
    			if(oldUser.getId() == newUser.getId()){
    				found = true;
    				break;
    			}
    		}
    		if(found == false){
    			newUser = em.find(User.class, newUser.getId());
    			List<Role> roles = newUser.getRoles();
    			roles.add(role);
        		em.merge(newUser);
        		
        		LOG.info("Added Role: " + role.getName() + " for User: " + newUser.getEmployeeId());
    		}
    	}		
		
		
		
	}
	
	

}
