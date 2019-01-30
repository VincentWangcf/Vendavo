package com.avnet.emasia.webquote.audit.web;


import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;


import com.avnet.emasia.webquote.audit.ejb.AuditSB;
import com.avnet.emasia.webquote.audit.ejb.AuditSearchCriteria;
import com.avnet.emasia.webquote.codeMaintenance.ejb.CodeMaintenanceSB;
import com.avnet.emasia.webquote.entity.AuditTrail;
import com.avnet.emasia.webquote.entity.SystemCodeMaintenance;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2012-12-25
 * Audit module :  audit log query .
 */


@ManagedBean(name = "auditMB")
@RequestScoped
public class AuditMB  implements java.io.Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -6337758649325631333L;
	static Logger logger = Logger.getLogger(AuditMB.class.getName());
	@EJB
    private transient AuditSB auditSB;
	@EJB
    private transient CodeMaintenanceSB codeMaintenanceSB;

	//search criteria
    private AuditSearchCriteria auditSearchCriteria; 
    //search result
	private List<AuditTrail> auditTrails;
	//filters
	private List<AuditTrail> filteredAuditTrails; 
    //selected item
    private AuditTrail selectedAuditTrail;
    //action options
	private List<SystemCodeMaintenance> actions;
    //constructor
	public AuditMB()
	{
		auditSearchCriteria = new AuditSearchCriteria();
			
	}
	
    @PostConstruct
    public void initialize()
    {
    	FacesContext.getCurrentInstance().getExternalContext().getSession(true);

    	logger.fine("call method initialize: ");
    	actions = codeMaintenanceSB.getAllActions();
    	auditTrails = auditSB.getAllAuditTrails();
    }

    public AuditTrail getSelectedAuditTrail() {
		return selectedAuditTrail;
	}


	public void setSelectedAuditTrail(AuditTrail selectedAuditTrail) {
		this.selectedAuditTrail = selectedAuditTrail;
	}
	
	public List<AuditTrail> getAuditTrails() {
		return auditTrails;
	}

	public void setAuditTrail(List<AuditTrail> auditTrails) {
		this.auditTrails = auditTrails;
	}

	public AuditSearchCriteria getAuditSearchCriteria() {
		return auditSearchCriteria;
	}

	public void setAuditSearchCriteria(AuditSearchCriteria auditSearchCriteria) {
		this.auditSearchCriteria = auditSearchCriteria;
	}

	public void setAuditTrails(List<AuditTrail> auditTrails) {
		this.auditTrails = auditTrails;
	}

	
	public List<AuditTrail> getFilteredAuditTrails() {
		return filteredAuditTrails;
	}

	public void setFilteredAuditTrails(List<AuditTrail> filteredAuditTrails) {
		this.filteredAuditTrails = filteredAuditTrails;
	}


    public List<SystemCodeMaintenance> getActions() {
		return actions;
	}

	public void setActions(List<SystemCodeMaintenance> actions) {
		this.actions = actions;
	}

	/*
     * Search functionality
     */
	public String search()
	{

		  FacesContext context = FacesContext.getCurrentInstance();
	      try
	      {
	    	  auditTrails = auditSB.search(auditSearchCriteria);
	    	  return goToSearch();
	      }
	      catch (Exception ex)
	      {
	            Logger.getLogger(AuditMB.class.getName()).log(Level.SEVERE, "Exception in searching audit report", ex);
	            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
	            		ResourceMB.getText("wq.message.fail"), ex.getCause().getMessage()));
	      }
	      
	      return goToSearch();
	}
	
	
	public String goToSearch()
	{
		return "auditLayout";

	}
	
	

}
