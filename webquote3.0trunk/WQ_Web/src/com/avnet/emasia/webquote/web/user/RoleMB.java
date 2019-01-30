package com.avnet.emasia.webquote.web.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;

import com.avnet.emasia.webquote.dp.EJBCommonSB;
import com.avnet.emasia.webquote.entity.AppFunction;
import com.avnet.emasia.webquote.entity.Role;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.exception.AppException;
import com.avnet.emasia.webquote.user.ejb.AppFunctionSB;
import com.avnet.emasia.webquote.user.ejb.RoleSB;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;




@ManagedBean
@SessionScoped
public class RoleMB extends GroupRoleMB implements Serializable {
	
	private static final long serialVersionUID = -5761376167958897716L;

	private static final Logger LOG =Logger.getLogger(RoleMB.class.getName()); 

	@EJB
	private RoleSB roleSB;
	
	@EJB
	private UserSB userSB;
	
	@EJB
	private AppFunctionSB appFunctionSB;
	
	private Role role;
	
	private List<Role> roles;
	
	private DualListModel<AppFunction> appFunctions;
	
	private List<AppFunction> appFunctionAll;
	
	@EJB
	protected EJBCommonSB ejbCommonSB;
	
	

	@PostConstruct
	public void initialize() {
		roles = (List<Role>)roleSB.findAll();
		
		appFunctionAll = appFunctionSB.findAll();
		List<AppFunction> appFunctionTarget = new ArrayList<AppFunction>();
		appFunctions = new DualListModel<AppFunction>(appFunctionAll, appFunctionTarget);

		role = new Role();
		setUsers(new ArrayList<User>());
	}
	
	public void add(ActionEvent event){
		
		initialize();
		role.setActive(true);
	}

	
	public void refresh(ActionEvent event){
		initialize();
	}
	
	public void addUser(){
		for(User user : getUsers()){
			if(user.getEmployeeId().equalsIgnoreCase(getEmployeeId())){
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "", ResourceMB.getParameterizedText("wq.message.customerAlreadyInList", getEmployeeId())+".");
				FacesContext.getCurrentInstance().addMessage(null, msg);				
				return;
			}
		}
		User user = userSB.findByEmployeeIdLazily(getEmployeeId());
		if(user != null){
			 getUsers().add(user);
			setEmployeeId("");
		}else{
	        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "", ResourceMB.getParameterizedText("wq.message.usrNotFound", getEmployeeId())+"!");  
        	FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	public void removeUser() {
		ejbCommonSB.removeUser(getUsers());
	}
	
	public void save(ActionEvent event){
        RequestContext context = RequestContext.getCurrentInstance();  
        boolean saved = false;  
        
        User user = UserInfo.getUser();
        
        if(role.getId() == 0){
        	role.setCreatedBy(user);
        	role.setCreatedOn(new Date());
        }else{
        	role.setLastUpdatedBy(user);
        	role.setLastUpdatedOn(new Date());
        }        	
        
        try{
        	role.setAppFunctions(appFunctions.getTarget());
        	roleSB.save(role, getUsers());
        	
        	saved = true;
        	
        	LOG.info("Role " + role.getName() + " has been saved by " + user.getEmployeeId());
        	
        	initialize();
        	
        }catch(AppException e){
        	ResourceMB resourceMB=(ResourceMB)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("resourceMB");
        	Locale locale = Locale.ENGLISH;
        	if(null!=resourceMB){
        		locale = resourceMB.getResourceLocale();
        	}
        	LOG.log(Level.WARNING, "failed for role :"+role.getName()+ "by user id : "+user.getEmployeeId()+", Reason to failed : "+MessageFormatorUtil.getParameterizedStringFromException(e));
        	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.error.saveError"), MessageFormatorUtil.getParameterizedStringFromException(e, locale)));

        }catch(Exception e){
			Date date = new Date();
			String timeStamp = String.valueOf(date.getTime());
			ResourceMB resourceMB=(ResourceMB)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("resourceMB");
			Locale locale = Locale.ENGLISH;
			if(null!=resourceMB){
				locale = resourceMB.getResourceLocale();
			}
			LOG.log(Level.WARNING, "failed for role :"+role.getName()+ "by user id : "+user.getEmployeeId()+", Reason to failed : "+e.getMessage());
			String s = ResourceMB.getText("wq.message.errorCode")+": " + timeStamp + ", " + e.getMessage();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,s,""));
        	
        }
        
        context.addCallbackParam("saved", saved);  
		
	}	
	
	private void changeSelection() {
		
		role = roleSB.findRoleWithAppFunction(role);
		
		List<AppFunction> appFunctionTarget = role.getAppFunctions();
		
		List<AppFunction> appFunctionSource = new ArrayList<AppFunction>();
		for(AppFunction appFunction1 : appFunctionAll){
			boolean found = false;
			for(AppFunction appFunction2 : appFunctionTarget){
				if(appFunction1.getId() == appFunction2.getId()){
					found =true;
					break;
				}
			}
			if (! found){
				appFunctionSource.add(appFunction1);
			}
			
		}
		
		appFunctions = new DualListModel<AppFunction>(appFunctionSource, appFunctionTarget);
		setUsers(roleSB.findUserByRole(role));
		
	}

		

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
		changeSelection();
		
	}
	
	public DualListModel<AppFunction> getAppFunctions() {
		return appFunctions;
	}

	public void setAppFunctions(DualListModel<AppFunction> appFunctions) {
		List<AppFunction> appFunctionSource = appFunctions.getSource();
		List<AppFunction> appFunctionTarget = appFunctions.getTarget();
		
		for(int i =0; i < appFunctionSource.size(); i++){
			AppFunction appFunction = getFullAppFunction(appFunctionSource.get(i));
			appFunctionSource.set(i, appFunction);
		}
		
		for(int i =0; i < appFunctionTarget.size(); i++){
			AppFunction appFunction = getFullAppFunction(appFunctionTarget.get(i));
			appFunctionTarget.set(i, appFunction);
		}
		
		this.appFunctions = appFunctions;
		
	}
	
	private AppFunction getFullAppFunction(AppFunction arg0){
		for(AppFunction appFunction : appFunctionAll){
			if(appFunction.getId() == arg0.getId()){
				return appFunction;
			}
		}
			
		return arg0;
	}

	
	
}
