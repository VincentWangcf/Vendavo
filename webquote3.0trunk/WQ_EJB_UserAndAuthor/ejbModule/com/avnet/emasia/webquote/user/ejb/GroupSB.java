package com.avnet.emasia.webquote.user.ejb;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.avnet.emasia.webquote.entity.Group;
import com.avnet.emasia.webquote.entity.Role;
import com.avnet.emasia.webquote.entity.User;

/**
 * Session Bean implementation class RoleSB
 */
@Stateless
@LocalBean
public class GroupSB {
	
	private static final Logger LOG = Logger.getLogger(GroupSB.class.getName());	

	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	
	@EJB
	UserSB userSB;
	
	public Group save(Group group){
		return em.merge(group);
	}
	
	public Group save(Group group, List<User> newUsers){
		
		List<User> oldUsers = findUserForGroup(group);
		if( ! (oldUsers.size() == 1 && oldUsers.get(0) == null)){
	    	for(User oldUser : oldUsers){
	    		boolean found = false;
	    		for(User newUser : newUsers){
	    			if(oldUser.getId() == newUser.getId()){
	    				found = true;
	    				break;
	    			}
	    		}
	    		if(found == false){
	    			for(int i = 0; i < oldUser.getGroups().size(); i++){
	    				if(group.getId() == oldUser.getGroups().get(i).getId()){
	    					oldUser.getGroups().remove(i);
	    				}
	    			}
	    			em.merge(oldUser);

	    			LOG.info("Removd Group: " + group.getName() + " from User: " + oldUser.getEmployeeId());    			
	    		}
	    	}
		}
    	
    	for(User newUser : newUsers){
    		boolean found = false;
    		if( ! (oldUsers.size() == 1 && oldUsers.get(0) == null)){
        		for(User oldUser : oldUsers){
        			if(oldUser.getId() == newUser.getId()){
        				found = true;
        				break;
        			}
        		}
    		}
    		
    		if(found == false){
        		User user = em.find(User.class, newUser.getId());
        		List<Group> groups = user.getGroups();
        		groups.add(group);
        		em.merge(user);
        		
        		LOG.info("Added Group: " + group.getName() + " for User: " + newUser.getEmployeeId());		
    		}
    	}
    	
    	group = em.merge(group);
    	
		return group;
	}
	
	
	public List<Group> findAll(){
		
		TypedQuery<Group> query = em.createQuery("select g from Group g order by g.name",Group.class);
		
		List<Group> groups = query.getResultList();

		return groups;
	}
	
	public List<Group> findAllActive(){
		
		TypedQuery<Group> query = em.createQuery("select g from Group g where g.active = true order by g.name",Group.class);
		
		List<Group> groups = query.getResultList();

		return groups;
	}	
	
	public List<User> findUserForGroup(Group group){
		
		TypedQuery<User> query = em.createQuery("select g.users from Group g where g.id=:id", User.class);
		
		query.setParameter("id", group.getId());
		
		List<User> users = query.getResultList();

		return users;
		
	}
	

}
