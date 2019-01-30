package com.avnet.emasia.webquote.web.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jboss.logmanager.Level;
import org.primefaces.model.DualListModel;

import com.avnet.emasia.webquote.constants.Constants;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.DataAccess;
import com.avnet.emasia.webquote.entity.Group;
import com.avnet.emasia.webquote.entity.Role;
import com.avnet.emasia.webquote.entity.Team;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.user.ejb.BizUnitSB;
import com.avnet.emasia.webquote.user.ejb.GroupSB;
import com.avnet.emasia.webquote.user.ejb.RoleSB;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.user.vo.DataAccessVo;
import com.avnet.emasia.webquote.user.vo.UserSearchCriteria;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.web.quote.cache.RoleCacheManager;

@ManagedBean
@SessionScoped
public class UserAdminRegionMB implements Serializable {

	private static final long serialVersionUID = -1551862600324442649L;

	private static final Logger LOG = Logger.getLogger(UserAdminRegionMB.class.getName());

	@EJB
	private UserSB userSB;

	@EJB
	private BizUnitSB bizUnitSB;

	//private List<User> users;
	//private List<User> filterUsers;	
	
	//private User user;
	
	private User resultUser = new User();
	
	private LazyUserDataModel lazyModel;
	//Search Criteria
	//private UserSearchCriteria criteria;
	
	private String employeeId="";// = "Wong";
	private String employeeName="";// = "Wong";

	//Used for Primefaces selectManyCheckbox	
	//private Map<String, Long> groupsMap = new HashMap<String, Long>();

	//private Map<String, String> bizUnitsMap = new HashMap<String, String>();
	private List<BizUnit> bizUnitAll;
	//private List<BizUnit> bizUnitSelected;
	private List<String> buSelected;
	
	

	public List<String> getBuSelected() {
		return buSelected;
	}

	public void setBuSelected(List<String> buSelected) {
		this.buSelected = buSelected;
	}
/*
	public List<BizUnit> getBizUnitSelected() {
		return bizUnitSelected;
	}

	public void setBizUnitSelected(List<BizUnit> bizUnitSelected) {
		this.bizUnitSelected = bizUnitSelected;
	}
*/
	public List<BizUnit> getBizUnitAll() {
		return bizUnitAll;
	}

	public void setBizUnitAll(List<BizUnit> bizUnitAll) {
		this.bizUnitAll = bizUnitAll;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	@PostConstruct
	public void initialize() {
		
		try {
			if(UserInfo.getUser() ==null){
				return;
			}
			
			employeeId="";
			employeeName="";
			//user = userSB.findByEmployeeIdWithAllRelation(UserInfo.getUser().getEmployeeId());
			//dataAccessVos = new ArrayList<DataAccessVo>();		
			
			//criteria = new UserSearchCriteria();
			//bizUnitSelected = new ArrayList<BizUnit>();
			buSelected = new ArrayList<String>();
			//buSelected = new DualListModel<BizUnit>(UserInfo.getUser().getAdminBizUnits(), new ArrayList<BizUnit>());
			
			bizUnitAll = bizUnitSB.findAll();
/*			for(BizUnit bizUnit : bizUnitSB.findAll()){
				bizUnitsMap.put(bizUnit.getName(), bizUnit.getName()); 
			}
*//*			
			if (!employeeId.equals("")) {
				searchUser(employeeId);
			}
*/			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			LOG.log(Level.SEVERE,
					"exception message: " + MessageFormatorUtil.getParameterizedStringFromException(e));
		}
	}

	public String search(){
		//LOG.info("UserAdminRegionMB.search: Begin");		
		
		if(employeeId.equals("")){  
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.message.empIdcannotEmpty")+" !", "")); 
			return null;
		}
		//else return "/UserMgmt/UserAdminRegionManagement.xhtml?faces-redirect=true";

		resultUser = userSB.findByEmployeeIdWithAllRelation(employeeId);
		
		if (resultUser!=null && resultUser.getId()!=0) {
			//resultUser = users.get(0);
			employeeName = resultUser.getName();
			//this.setBizUnitSelected(resultUser.getBizUnits());
			this.setBuSelected(genBuSelected(resultUser.getAdminBizUnits()));
			//employeeName = resultUser.getBizUnits().toString();
			//if record more than 1, display the first info
			//if (users.size()>0) criteria.setEmployeeId(resultUser.getEmployeeId());
			//return "/UserMgmt/UserAdminRegionManagement.xhtml?faces-redirect=true";
			return "";
		} else {
			resultUser = new User();
			employeeName = "";
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.message.noRecordFound")+" !", ""));
			return null;
		}
		
	}
/*
	public void searchUser(String employeeId){
		LOG.info("UserAdminRegionMB.searchUser: Begin");		
		
		resultUser = userSB.findByEmployeeIdWithAllRelation(employeeId);
		
		if (resultUser!=null && resultUser.getId()!=0) {
			//resultUser = users.get(0);
			employeeName = resultUser.getName();
			//if record more than 1, display the first info
			//if (users.size()>0) criteria.setEmployeeId(resultUser.getEmployeeId());
			//return "/UserMgmt/UserAdminRegionManagement.xhtml?faces-redirect=true";	
		} else {
			resultUser = new User();
			employeeName = "";
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.message.noRecordFound")+" !", ""));
			return ;
		}
		
	}
*/	
	public String save() {
		boolean succeed = commonSave();

		if (succeed) {
/*			
			FacesContext context = FacesContext.getCurrentInstance();
			UserMB userMB = (UserMB) context.getApplication().evaluateExpressionGet(context, "#{userMB}", UserMB.class);			
			userMB.setUsers(null);
*/			
			resultUser = userSB.findByEmployeeIdWithAllRelation(employeeId);//recall data
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,ResourceMB.getText("wq.message.savedSuccess"), ""));
			return null; //"/UserMgmt/UserManagement.xhtml";
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.message.updateFailed")+" !", ""));

			return null;
		}

	}

	
	private boolean commonSave() {
		String message  = null;
        FacesMessage msg = null;
		
		if(resultUser.getId() == 0 && userSB.findByEmployeeIdLazily(resultUser.getEmployeeId()) == null){
			Object[] objArr = {employeeId};
			message = ResourceMB.getParameterizedString("wq.message.empIdcannotEmpty",objArr);
			msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", message+".") ;
			FacesContext.getCurrentInstance().addMessage(null, msg);
			return false;
		}
		
		//if (resultUser.getBizUnits().isEmpty() || resultUser.getBizUnits().size() == 0) {
		//if (bizUnitSelected.isEmpty() || bizUnitSelected.size() == 0) {
		//String[] strBizUnit = bizUnitSelected.toArray(new String[bizUnitSelected.size()]);
		//Object[] objBizUnit = bizUnitSelected.toArray();
		List<BizUnit> selectedBU = getSelectedBizUnit();
		
		//if (!validation(selectedBU)) return false;
		
		//resultUser.setBizUnits(selectedBU);
		resultUser.setAdminBizUnits(selectedBU);
		resultUser.setLastUpdatedBy(UserInfo.getUser().getEmployeeId());
		resultUser.setLastUpdatedOn(new Date());

		try {
			//userSB.findByEmployeeIdWithAllRelation(employeeId);
			userSB.save(resultUser);
			//userSB.saveWithAllRelation(resultUser);

			LOG.info("User " + resultUser.getEmployeeId() + " saved successfully");
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Save user " + resultUser.getEmployeeId() + " failed. Exception message: "+MessageFormatorUtil.getParameterizedStringFromException(e) , e);
			String s = e.getMessage();
			
			if(s != null && s.toUpperCase().contains("OptimisticLockException".toUpperCase())){
				
				message = ResourceMB.getText("wq.message.userProfileModify");
				s = message;
			}else{
				if(e.getCause() != null){
					s = s + ". " + e.getCause().getMessage();
				}
				
			}
			
			msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "",	s);
			FacesContext.getCurrentInstance().addMessage(null, msg);
			return false;
		}

		return true;
	}
	
	private Boolean validation(List<BizUnit> selectedBU){
		String message  = null;
        FacesMessage msg = null;
		
		if (buSelected.isEmpty() || buSelected.size() == 0) {
			message = ResourceMB.getText("wq.message.selectedAndDefaultRegionCannotEmpty");
			msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", message+".");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			return false;
		}else{
			boolean found = false;
			
			for(BizUnit bu: selectedBU){
				if(bu.getName().equals(resultUser.getDefaultBizUnit().getName())){
					found = true;
					break;
				}
			}
/*			
			for(Object bu : objBizUnit){
				String ss = (String) bu;
				
				if(ss.equals(resultUser.getDefaultBizUnit().getName())){
					found = true;
					break;
				}
			}
*/
			/*				
			if(bu.getName().equals(resultUser.getDefaultBizUnit().getName())){
				found = true;
				break;
			}
*/				

			if(found == false){
				message = ResourceMB.getText("wq.message.defaultRegionNotMatchSelectedRegion");
				msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", message+".");
				FacesContext.getCurrentInstance().addMessage(null, msg);
				return false;
			} else return true;
		}
		
	}

	private List<BizUnit> getSelectedBizUnit(){
		List<BizUnit> buOut = new ArrayList<BizUnit>();
		
		for(String bu : buSelected){
			buOut.add(bizUnitSB.findByPk(bu));
		}
		
		return buOut;
	}
	
	private List<String> genBuSelected(List<BizUnit> buList){
		List<String> buOut = new ArrayList<String>();
		
		for(BizUnit bu : buList){
			buOut.add(bu.getName());
		}
		
		return buOut;
	}
/*
	public void setDefaultUser(){
		user = UserInfo.getUser();
	}
*/
/*	
	public Map<String, String> getBizUnitsMap() {
		return bizUnitsMap;
	}

	public void setBizUnitsMap(Map<String, String> bizUnitsMap) {
		this.bizUnitsMap = bizUnitsMap;
	}
*/
	public User getResultUser() {
		//if (resultUser==null) resultUser = userSB.findByEmployeeIdWithAllRelation("P03318");
		return resultUser;
	}

	public void setResultUser(User resultUser) {
		this.resultUser = resultUser;
	}
	
	
	
}