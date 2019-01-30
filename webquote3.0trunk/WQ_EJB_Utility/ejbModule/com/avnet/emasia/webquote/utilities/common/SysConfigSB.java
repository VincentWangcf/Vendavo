package com.avnet.emasia.webquote.utilities.common;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.avnet.emasia.webquote.entity.AppProperty;
import com.avnet.emasia.webquote.entity.util.AttachmentListenter;

/**
 * Session Bean implementation class SystemConfiguration
 */
@Startup
@Singleton
@LocalBean
public class SysConfigSB {
	private static final Logger LOG = Logger.getLogger(SysConfigSB.class
			.getName());

	@PersistenceContext(unitName = "Server_Source")
	private EntityManager em;
	private List<AppProperty> appPropertyLst = new ArrayList<AppProperty>();
	private String propertyValue;
	
	public static final String ATTACHMENT_ROOT_PATH = "ATTACHMENT_ROOT_PATH";

	@PostConstruct
	public void init()  {
		LOG.info("initializing the system configuration properties...");
		Query query = em.createQuery("SELECT a FROM AppProperty a");
		if (query.getResultList() instanceof List<?>) {
			appPropertyLst = (List<AppProperty>) query.getResultList();
		}
		
		for (AppProperty appPrty : appPropertyLst) {
			if (ATTACHMENT_ROOT_PATH.equals(appPrty.getPropKey())) {
				String rootPath = appPrty.getPropValue();
				AttachmentListenter.setRootPath(rootPath);
				break;
			}
		}
		
		LoginConfigCache.setLoginType(getProperyValue("LOGIN_TYPE"));
		LoginConfigCache.setLogoutUrl(getProperyValue("SECURITY_LOGOUT_URL"));
		
	}
	
	/**
	 * It's a common interface for get system configuration value.
	 * @param propretyKey
	 * @return
	 */
	public String getProperyValue(String propretyKey) {
		for (AppProperty appPrty : appPropertyLst) {
			if (propretyKey.equals(appPrty.getPropKey())) {
				return appPrty.getPropValue(); 
			}
		}
		return null;
	}

	/**
	 * Default constructor.
	 */
	public SysConfigSB() {

	}

	/**
	 * get all system property information.
	 * 
	 * @return
	 */
	public List<AppProperty> getProperties() {
		return appPropertyLst;
	}

	/**
	 * add configuration property.
	 * 
	 * @param appProperty
	 */
	public void addProperty(AppProperty appProperty)  {
		em.persist(appProperty);
		em.flush();
		init();//need to refresh the page.
		LOG.info("add system property.");
	}

	/**
	 * update configuration property.
	 * 
	 * @param appProperty
	 */
	public void updateProperty(AppProperty appProperty)  {
		AppProperty prop = em.find(AppProperty.class, appProperty.getPropKey());
		prop.setPropKey(appProperty.getPropKey());
		prop.setPropValue(appProperty.getPropValue());
		prop.setPropDesc(appProperty.getPropDesc());
		prop.setPropReserved(appProperty.getPropReserved());
		em.merge(prop);
		em.flush();
		LOG.info("update system property.");
	}

	/**
	 * delete configuration proptery.
	 * 
	 * @param appPropertyArr
	 */
	public void deleteProperty(AppProperty[] appPropertyArr)  {
		for (AppProperty ap : appPropertyArr) {
			AppProperty apDelegated = em.find(AppProperty.class,
					ap.getPropKey());
			em.remove(apDelegated);
			init();//need to refresh the page.
			LOG.info("remove system property.");
		}
	}
}
