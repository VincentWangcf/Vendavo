package com.avnet.emasia.webquote.utilites.web.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.jboss.logmanager.Level;
import org.primefaces.event.RowEditEvent;
import com.avnet.emasia.webquote.entity.AppProperty;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;

@ManagedBean(name = "ConfigurationMB")
@RequestScoped
public class ConfigurationMB implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(ConfigurationMB.class
			.getName());

	private final static String BACK_HOME_PATH = "/Utilities/SystemConfig?faces-redirect=true";
	@EJB
	private SysConfigSB cfgSB;
	private AppProperty[] selectedProperty;
	private PropertyDataModel pModel;
	private AppProperty newProperty;
	private List<AppProperty> appPropertyLst = new ArrayList<AppProperty>();

	public AppProperty getNewProperty() {
		return newProperty;
	}

	public void setNewProperty(AppProperty newProperty) {
		this.newProperty = newProperty;
	}

	public PropertyDataModel getpModel() {
		return pModel;
	}

	public void setpModel(PropertyDataModel pModel) {
		this.pModel = pModel;
	}

	public AppProperty[] getSelectedProperty() {
		return selectedProperty;
	}

	public void setSelectedProperty(AppProperty[] selectedProperty) {
		this.selectedProperty = selectedProperty;
	}

	@PostConstruct
	public void init() {
		LOG.info("initializing management been.");
		appPropertyLst = cfgSB.getProperties();
		pModel = new PropertyDataModel(appPropertyLst);
		initNewProperty();
	}

	public List<AppProperty> getAppPropertyLst() {
		return appPropertyLst;
	}

	public void setAppPropertyLst(List<AppProperty> appPropertyLst) {
		this.appPropertyLst = appPropertyLst;
	}

	/**
	 * close add window event.
	 * 
	 * @param actionEvent
	 */
	public void addDestroyEvent(ActionEvent actionEvent) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
				ResourceMB.getText("wq.message.systemError"), ResourceMB.getText("wq.message.tryAgain")+".");
		FacesContext.getCurrentInstance().addMessage(null, message);
		addProperties();
		backToHome(BACK_HOME_PATH);
	}

	/**
	 * close delete confirm window event.
	 * 
	 * @param actionEvent
	 * @return
	 */
	public String deleteDestroyEvent(ActionEvent actionEvent) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
				ResourceMB.getText("wq.message.systemError"), ResourceMB.getText("wq.message.tryAgain")+".");
		FacesContext.getCurrentInstance().addMessage(null, message);
		deleteProperty();
		backToHome(BACK_HOME_PATH);
		return "SystmConfig";
	}

	/**
	 * todo
	 * 
	 * @return
	 */
	public String goAddProperties() {
		initNewProperty();
		LOG.info("go to add page");
		// return "AddProperty";
		return "SystmConfig";
	}

	/**
	 * (todo) reference to properties of page
	 */
	public String addProperties() {
		try {
			cfgSB.addProperty(newProperty);
			LOG.info("add system successfully!");
			return "SystmConfig";
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "add system failed! exception message : "+MessageFormatorUtil.getParameterizedStringFromException(e),e);
		}

		FacesMessage msg = new FacesMessage(ResourceMB.getText("wq.message.propertyAdd"),
				newProperty.getPropKey());
		FacesContext.getCurrentInstance().addMessage(null, msg);
		return null;
	}

	/**
	 * initializate add property bean.
	 */
	public void initNewProperty() {
		newProperty = new AppProperty();
	}

	/**
	 * update configuration property.
	 * 
	 * @param appProperty
	 */
	public void updateProperty(AppProperty appProperty) {
		try {
			cfgSB.updateProperty(appProperty);
			LOG.info("update system property successfully!");
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "update system property error. exception message : "+MessageFormatorUtil.getParameterizedStringFromException(e),e);
		}

		FacesMessage msg = new FacesMessage(ResourceMB.getText("wq.message.propertyUpdate"),
				appProperty.getPropKey());
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public void onEdit(RowEditEvent event) {
		try {
			updateProperty((AppProperty) event.getObject());
			LOG.info("update system successfully!");
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "update system error. exception message : "+MessageFormatorUtil.getParameterizedStringFromException(e),e);
		}

		FacesMessage msg = new FacesMessage(ResourceMB.getText("wq.message.propertyEdited"),
				((AppProperty) event.getObject()).getPropKey());
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	/**
	 * delete property from db.
	 * 
	 * @return
	 */
	public String deleteProperty() {
		try {
			System.out.println(selectedProperty[0].getPropKey());
			cfgSB.deleteProperty(selectedProperty);
			LOG.info("delete system propperty successfully!");
			// return "SystmConfig";
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "delete system failed! exception message : "+MessageFormatorUtil.getParameterizedStringFromException(e),e);
		}

		FacesMessage msg = new FacesMessage(ResourceMB.getText("wq.message.propertyDelete"), "property");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		return null;
	}

	/**
	 * cancel edit property operation.
	 * 
	 * @param event
	 */
	public void onCancel(RowEditEvent event) {
		FacesMessage msg = new FacesMessage(ResourceMB.getText("wq.message.propertyCancelled"),
				((AppProperty) event.getObject()).getPropKey());
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
	public void backToHome(String homePth) {
		FacesContext f = FacesContext.getCurrentInstance(); 
		f.getApplication().getNavigationHandler().handleNavigation(f, null, homePth);
	}
}