package com.avnet.emasia.webquote.web.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

import com.avnet.emasia.webquote.dp.EJBCommonSB;
import com.avnet.emasia.webquote.entity.Group;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.user.ejb.GroupSB;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;

@ManagedBean
@SessionScoped
public class GroupMB extends GroupRoleMB implements Serializable {

	private static final long serialVersionUID = 8959196777383211157L;

	private static final Logger LOG = Logger.getLogger(GroupMB.class.getName());

	@EJB
	private GroupSB groupSB;

	@EJB
	private UserSB userSB;

	private Group group;

	private List<Group> groups;
	
	@EJB
	protected EJBCommonSB ejbCommonSB;

	@PostConstruct
	public void initialize() {
		groups = (List<Group>) groupSB.findAll();
		group = new Group();
		group.setActive(true);
		setUsers(new ArrayList<User>());
	}

	public void refresh(ActionEvent event) {
		initialize();
	}

	public void add(ActionEvent event) {
		group = new Group();
		group.setActive(true);
		setUsers(new ArrayList<User>());
	}

	public void save(ActionEvent event) {
		RequestContext context = RequestContext.getCurrentInstance();
		boolean saved = false;

		try {
			group.setUsers(getUsers());

			User user = UserInfo.getUser();

			if (group.getId() == 0) {
				group.setCreatedBy(user);
				group.setCreatedOn(new Date());
			} else {
				group.setLastUpdatedBy(user);
				group.setLastUpdatedOn(new Date());
			}

			groupSB.save(group, getUsers());
			saved = true;

			LOG.info("Group " + group.getName() + " has been saved successfully");

			initialize();
		} catch (Exception e) {
			User user = group.getId() == 0 ? group.getCreatedBy() : group.getLastUpdatedBy();
			Date date = group.getId() == 0 ? group.getCreatedOn() : group.getLastUpdatedOn();
			String message = e.getMessage();
			LOG.log(Level.WARNING, "Error occured when saving group : " + group.getName() + ", by User Name"
					+ user.getName() + ",on date : " + date.toString() + ", Message : " + message);
			/*ResourceMB resourceMB = (ResourceMB) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
					.get("resourceMB");*/
			//Do not internationalize as the error message comes from third party.
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					message, ""));
		}

		context.addCallbackParam("saved", saved);

	}

	private void changeSelection() {
		setUsers(groupSB.findUserForGroup(group));
		if (getUsers().size() == 1 && getUsers().get(0) == null) {
			setUsers(new ArrayList<User>());
		}
	}

	public void addUser() {

		for (User user : getUsers()) {
			if (user.getEmployeeId().equalsIgnoreCase(getEmployeeId())) {
				Object[] objArr = {getEmployeeId()};
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "",
						ResourceMB.getParameterizedString("wq.message.alreadyInList",objArr) + ".");
				FacesContext.getCurrentInstance().addMessage(null, msg);
				return;
			}
		}

		User user = userSB.findByEmployeeIdLazily(getEmployeeId());
		if (user != null) {
			getUsers().add(user);
			setEmployeeId("");
		} else {
			Object[] objArr = {getEmployeeId()};
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "",
					ResourceMB.getParameterizedString("wq.message.usrNotFound",objArr) + "!");
			FacesContext.getCurrentInstance().addMessage("messages", msg);
		}
	}

	public void removeUser() {
		ejbCommonSB.removeUser(getUsers());
	}

	// Getters, Setters
	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
		changeSelection();
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
	
}
