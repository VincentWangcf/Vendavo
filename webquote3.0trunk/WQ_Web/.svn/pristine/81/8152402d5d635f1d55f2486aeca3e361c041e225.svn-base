package com.avnet.emasia.webquote.web.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.jboss.logmanager.Level;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;

import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.ManufacturerMapping;
import com.avnet.emasia.webquote.quote.ejb.ManufacturerSB;
import com.avnet.emasia.webquote.user.ejb.BizUnitSB;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.web.user.UserInfo;

@ManagedBean
@SessionScoped
public class MfrMaintenanceMB implements Serializable {

	private static final long serialVersionUID = -1551862600324442649L;

	private static final Logger LOG = Logger.getLogger(MfrMaintenanceMB.class.getName());

	@EJB
	private ManufacturerSB manufacturerSB;

	@EJB
	private BizUnitSB bizUnitSB;


	//private Map<String, String> bizUnitsMap = new HashMap<String, String>();
	private List<BizUnit> bizUnitAll;
	//private List<BizUnit> bizUnitSelected;
	private String buSelected;
	
	private String mfrSelected = "";
	
	private ManufacturerMapping mfrMappingNew;
	
	private ManufacturerMapping mfrMappingDelete;
	
	private List<Manufacturer> mfrAll;
	private List<Manufacturer> mfrResult;
	private List<ManufacturerMapping> mfrMappingResult;
	
	private int mfrMappingNewPricerFlag;

	private Manufacturer manufacturerNew = new Manufacturer();
	
	private String editMFRName = "";
	private String editMFRDesc = "";
	private String editMFRNameCurrent = "";

	public List<BizUnit> getBizUnitAll() {
		return bizUnitAll;
	}

	public void setBizUnitAll(List<BizUnit> bizUnitAll) {
		this.bizUnitAll = bizUnitAll;
	}

	

	public List<Manufacturer> getMfrAll() {
		return mfrAll;
	}

	public void setMfrAll(List<Manufacturer> mfrAll) {
		this.mfrAll = mfrAll;
	}

	public String getBuSelected() {
		if(buSelected==null) buSelected = "";
		return buSelected;
	}

	public void setBuSelected(String buSelected) {
		this.buSelected = buSelected;
	}

	public String getMfrSelected() {
		return mfrSelected;
	}

	public void setMfrSelected(String mfrSelected) {
		this.mfrSelected = mfrSelected;
	}

	public ManufacturerMapping getMfrMappingNew() {
		return mfrMappingNew;
	}

	public void setMfrMappingNew(ManufacturerMapping mfrMappingNew) {
		this.mfrMappingNew = mfrMappingNew;
	}

	public ManufacturerMapping getMfrMappingDelete() {
		return mfrMappingDelete;
	}

	public void setMfrMappingDelete(ManufacturerMapping mfrMappingDelete) {
		this.mfrMappingDelete = mfrMappingDelete;
	}

	public List<ManufacturerMapping> getMfrMappingResult() {
		if (mfrMappingResult==null) mfrMappingResult = new ArrayList<ManufacturerMapping>();
		return mfrMappingResult;
	}

	public void setMfrMappingResult(List<ManufacturerMapping> mfrMappingResult) {
		this.mfrMappingResult = mfrMappingResult;
	}

	
	public int getMfrMappingNewPricerFlag() {
		return mfrMappingNewPricerFlag;
	}

	public void setMfrMappingNewPricerFlag(int mfrMappingNewPricerFlag) {
		this.mfrMappingNewPricerFlag = mfrMappingNewPricerFlag;
	}

	
	public Manufacturer getManufacturerNew() {
		return manufacturerNew;
	}

	public void setManufacturerNew(Manufacturer manufacturerNew) {
		this.manufacturerNew = manufacturerNew;
	}

	
	
	public String getEditMFRName() {
		return editMFRName;
	}

	public void setEditMFRName(String editMFRName) {
		this.editMFRName = editMFRName;
	}

	public String getEditMFRDesc() {
		return editMFRDesc;
	}

	public void setEditMFRDesc(String editMFRDesc) {
		this.editMFRDesc = editMFRDesc;
	}

	
	
	public String getEditMFRNameCurrent() {
		return editMFRNameCurrent;
	}

	public void setEditMFRNameCurrent(String editMFRNameCurrent) {
		this.editMFRNameCurrent = editMFRNameCurrent;
	}

	@PostConstruct
	public void initialize() {
		
		try {
			
			bizUnitAll = bizUnitSB.findAll();
			mfrAll = manufacturerSB.findAll();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			LOG.log(Level.SEVERE,
					"exception message: " + MessageFormatorUtil.getParameterizedStringFromException(e));
		}
	}

	public String search(){
		//LOG.info("MfrMaintenanceMB.search: Begin");		
		
		if(buSelected==null || buSelected.equals("")){  
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.message.selectRegion")+" !", "")); 
			return null;
		}
		//else return "/Admin/MfrMaintenance.xhtml?faces-redirect=true";

		BizUnit bu = bizUnitSB.findByPk(buSelected);
		
		mfrMappingResult = new ArrayList<ManufacturerMapping>();
		mfrResult = manufacturerSB.findManufacturerByBizUnit(bu);
		
		if (mfrResult!=null && !mfrResult.isEmpty()) {
			mfrMappingNew = new ManufacturerMapping();
			mfrMappingNew.setBizUnit(bu);
			mfrMappingNew.setBizUnitID(bu.getName());
			for(Manufacturer mfr : mfrResult){
				this.mfrMappingResult.add(mfr.getManufacturerMapping(buSelected));
			}
			return "";
		} else {
			this.mfrMappingResult = new ArrayList<ManufacturerMapping>();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.message.noRecordFound")+" !", ""));
			return null;
		}
		
	}

	public String save() {
		boolean succeed = commonSave();

		if (succeed) {
/*			
			FacesContext context = FacesContext.getCurrentInstance();
			UserMB userMB = (UserMB) context.getApplication().evaluateExpressionGet(context, "#{userMB}", UserMB.class);			
			userMB.setUsers(null);
*/			
			this.mfrMappingNew = new ManufacturerMapping();
			
			search();//recall data
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,ResourceMB.getText("wq.message.savedSuccess"), ""));
			return null; //"/UserMgmt/UserManagement.xhtml";
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.message.updateFailed")+" !", ""));

			return null;
		}

	}

	
	private boolean commonSave() {
		LOG.info("MfrMaintenanceMB.commonSave: Begin");		
		String message  = null;
        FacesMessage msg = null;
		
		if(mfrSelected==null || mfrSelected.equals("")){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.message.mrfField")+" !", ""));
			return false;
		}
		

		Manufacturer mfr = manufacturerSB.findManufacturerByName(mfrSelected);
		if(mfr==null || mfr.getId()==0){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.message.mfrMessage")+" !", ""));
			return false;
		}
		
		mfrMappingNew = new ManufacturerMapping();
		
		if (!buSelected.equals("")) {
			BizUnit bu = bizUnitSB.findByPk(buSelected);
			if (bu!=null && !bu.getName().equals("")){
				mfrMappingNew.setBizUnit(bu);
				mfrMappingNew.setBizUnitID(buSelected);
			}
		}
		
		if(mfrMappingNew.getBizUnitID() == null || mfrMappingNew.getBizUnitID() == ""){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.message.systemError")+" !", ""));
			return false;
			
		}

		try {
			//userSB.findByEmployeeIdWithAllRelation(employeeId);
			//Manufacturer mfr = mfrMappingNew.getManufacturer();
			if(mfrMappingNewPricerFlag==1) mfrMappingNew.setPricer(true); else mfrMappingNew.setPricer(false);
			mfrMappingNew.setManufacturerID(mfr.getId());
			
			mfr.getManufacturerMappings().add(mfrMappingNew);
			//mfr.setLastUpdatedBy(UserInfo.getUser().getEmployeeId());
			//mfr.setLastUpdatedOn(new Date());
			manufacturerSB.save(mfr);

			LOG.info("Manufacturer " + mfr.getName() + " saved successfully");
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Save Manufacturer " + mfr.getName() + " failed. Exception message: "+MessageFormatorUtil.getParameterizedStringFromException(e) , e);
			String s = e.getMessage();
			
			if(s != null && s.toUpperCase().contains("OptimisticLockException".toUpperCase())){
				
				message = ResourceMB.getText("wq.message.mfrModify");
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
	
	public Boolean delete() {
		//LOG.info("MfrMaintenanceMB.delete: Begin");
		
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> params = context.getExternalContext().getRequestParameterMap();
		String manufacturerName = params.get("mfrName");
		BizUnit bu = bizUnitSB.findByPk(buSelected);
		boolean buFound = false;
		
		Manufacturer mfr = manufacturerSB.findManufacturerByName(manufacturerName);
		
		if(mfr==null  || bu==null) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.message.systemError")+" !", ""));
			return false;
		}
		
		List<ManufacturerMapping> mppList = mfr.getManufacturerMappings(); 
		LOG.info("MfrMaintenanceMB.delete: mpp.size = " + mppList.size());
		//mfr.getManufacturerMappings().removeIf(m -> (m.bizUnit.name == buSelected));
		for(int i=0; i<mppList.size(); i++){
			ManufacturerMapping mpp = mppList.get(i);
			BizUnit mBU = mpp.getBizUnit();
			
			if (mBU.getName().equals(buSelected)){
				mppList.remove(mpp);
				buFound = true;
				break;
			}
		}
		//LOG.info("MfrMaintenanceMB.delete: mpp.size = " + mppList.size());
		
		if (buFound){
			mfr.setManufacturerMappings(mppList);
			//LOG.info("MfrMaintenanceMB.delete: mfr.mpp.size = " + mfr.getManufacturerMappings().size());
			Manufacturer mOut = manufacturerSB.save(mfr);
			LOG.info("MfrMaintenanceMB.delete: mOut.mpp.size = " + mOut.getManufacturerMappings().size());
			
			clearAllFilters();
			search();
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.exception.delFail")+" !", ""));
			return false;
		}
		
		return true;
	}
	
	public String editSearch() {
		//LOG.info("MfrMaintenanceMB.search: Begin");		
		
		if(editMFRName==null || editMFRName.equals("")){  
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.message.mrfField")+" !", "")); 
			return null;
		}
		//else return "/Admin/MfrMaintenance.xhtml?faces-redirect=true";

		editMFRNameCurrent="";
		Manufacturer mfr = manufacturerSB.findManufacturerByName(editMFRName);
		
		if (mfr!=null && mfr.getId()>0) {
			editMFRNameCurrent = editMFRName;
			editMFRDesc = mfr.getDescription();

			return "";
		} else {
			editMFRDesc = "";
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.message.noRecordFound")+" !", ""));
			return null;
		}
		
	}
	
	public Boolean editSave() {
		LOG.info("MfrMaintenanceMB.editSave: Begin > " + editMFRDesc);		
		String message  = null;
        FacesMessage msg = null;
        Manufacturer mfr = new Manufacturer();
		
		if(editMFRDesc==null || editMFRDesc.equals("")){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.message.entrDescr")+" !", ""));
			return false;
		}
		
		if(editMFRNameCurrent==null || editMFRNameCurrent.equals("")){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.message.recordMissing"), ""));
			return false;
		}
		else {
			mfr = manufacturerSB.findManufacturerByName(editMFRNameCurrent);
			if(mfr==null || mfr.getId()==0){ //if the current MFR not correct, it possible some other ppl had deleted or updated, also possible system problem, temporary use error message ="System Error"
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.message.systemError")+" !", ""));
				return false;
			}
			
		}

		try {
			mfr.setDescription(editMFRDesc);

			//mfr.setLastUpdatedBy(UserInfo.getUser().getEmployeeId());
			//mfr.setLastUpdatedOn(new Date());
			manufacturerSB.save(mfr);

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,ResourceMB.getText("wq.message.savedSuccess"), ""));
			LOG.info("Manufacturer " + mfr.getName() + " > " + mfr.getDescription() + " saved successfully");
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Save Manufacturer " + mfr.getName() + " failed. Exception message: "+MessageFormatorUtil.getParameterizedStringFromException(e) , e);
			String s = e.getMessage();
			
			if(s != null && s.toUpperCase().contains("OptimisticLockException".toUpperCase())){
				
				message = ResourceMB.getText("wq.message.mfrModify");
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
	
	public Boolean addSave() {
		LOG.info("MfrMaintenanceMB.addSave: Begin > " + manufacturerNew.getName());		
		String message  = null;
        FacesMessage msg = null;
		
        if(manufacturerNew.getName()==null || manufacturerNew.getName().equals("")){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.message.mrfField")+" !", "")); 
			return null;
		}

		if(manufacturerNew.getDescription()==null || manufacturerNew.getDescription().equals("")){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.message.entrDescr")+" !", ""));
			return false;
		}
		
		Manufacturer mfr = manufacturerSB.findManufacturerByName(manufacturerNew.getName());
		
		if (mfr!=null){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.message.mfrExist")+" !", ""));
			return false;
		}
		
		try {
			//mfr.setLastUpdatedBy(UserInfo.getUser().getEmployeeId());
			//mfr.setLastUpdatedOn(new Date());
			manufacturerSB.addNew(manufacturerNew);

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,ResourceMB.getText("wq.message.savedSuccess"), ""));
			LOG.info("Manufacturer " + manufacturerNew.getName() + " > " + manufacturerNew.getDescription() + " saved successfully");

			manufacturerNew = new Manufacturer();
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Save Manufacturer " + manufacturerNew.getName() + " failed. Exception message: "+MessageFormatorUtil.getParameterizedStringFromException(e) , e);
			String s = e.getMessage();
			
			if(s != null && s.toUpperCase().contains("OptimisticLockException".toUpperCase())){
				
				message = ResourceMB.getText("wq.message.mfrModify");
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

	public void clearAllFilters() {

		final DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent(":form:mfrMapping_Table");
		if (dataTable!=null){
		    if (!dataTable.getFilters().isEmpty()) {
		        dataTable.reset();

		        RequestContext requestContext = RequestContext.getCurrentInstance();
		        requestContext.update(":form:mfrMapping_Table");
		    }
		} else {
			 RequestContext requestContext = RequestContext.getCurrentInstance();
			 requestContext.execute("PF('mfrMappingTable').clearFilters()");			
		}
	}
}