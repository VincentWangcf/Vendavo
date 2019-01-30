package com.avnet.emasia.webquote.utilites.resources;

import java.io.IOException;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.avnet.emasia.webquote.constants.Constants;
import com.avnet.emasia.webquote.entity.LocaleMaster;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilites.web.common.CacheUtil;
import com.avnet.emasia.webquote.utilities.DBResourceBundle;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.web.security.WQUserDetails;
import com.avnet.emasia.webquote.web.user.UserInfo;
import com.avnet.emasia.webquote.web.user.UserMB;

/**
 * @author preeti.barthwal
 *
 */
@ManagedBean(name = "resourceMB")
@SessionScoped
public class ResourceMB implements Serializable {
	/**
	 * serial version UUID
	 */
	private static final long serialVersionUID = -8915966773723549985L;
	
	private static final Logger LOGGER = Logger.getLogger(ResourceMB.class.getSimpleName());
	/** The country */
	private String language = "";
	/** The countries */
	private Map<String, String> languages;
	
	public String defaultLocaleAsString;
	
	/** The resource MB. */
	@ManagedProperty(value="#{userMB}")
	/** To Inject ResourceMB instance  */
	private UserMB userMB;
	
	//Bryan Start
	@EJB
	private CacheUtil cacheUtil;
	//Bryan End
	
	/**
	 * @param userMB the userMB to set
	 */
	public void setUserMB(UserMB userMB) {
		this.userMB = userMB;
	}


	/**
	 * This List contains Locale Master
	 */
	public static List<LocaleMaster> LOCALE_MASTER = new ArrayList<LocaleMaster>();
	
	@EJB
	private UserSB userSB;
	

	/**
	 * Used to initialize values needed
	 */
	@PostConstruct
	public void init() {
		//Bryan Start
		LOCALE_MASTER = cacheUtil.getLocaleMasterList();
		//Bryan End
		
		populateLanguages();
		for (LocaleMaster localeMaster : LOCALE_MASTER) {
			if (localeMaster.getLocalId().equalsIgnoreCase(getDefaultLocaleAsString())) {
				this.language = localeMaster.getLocalId();
			}
		}

	}

	/*
	 * To populate countries values
	 * */
	private void populateLanguages() {

		languages = new HashMap<>();
		for(LocaleMaster localeMaster : LOCALE_MASTER){
			languages.put(localeMaster.getLocalName(), localeMaster.getLocalId());
		}
	}

	/**
	 * @param key
	 * @return
	 */
	public static String getText(final String key) {
		final Locale locale = getLocale();
		ResourceBundle bundle = ResourceBundle.getBundle(DBResourceBundle.class.getName(),locale);
		return bundle.getString(key);
	}
	
	/**
	 * @param key
	 * @return
	 */
	public static String getDefaultText(final String key) {
		ResourceBundle bundle = ResourceBundle.getBundle(DBResourceBundle.class.getName());
		return bundle.getString(key);
	}
	
	/**
	 * @param key
	 * @param parameters
	 * @return
	 */
	public static String getParameterizedText(final String key, final String parameters) {
		final Locale locale = getLocale();
		ResourceBundle bundle = ResourceBundle.getBundle(DBResourceBundle.class.getName(),locale);
		return MessageFormat.format(bundle.getString(key), parameters);
	}
	
	// first get from page ,no get then get default locale en.
	public static Locale getLocale() {
		//String language 
		try {
			if(FacesContext.getCurrentInstance() !=null) {
				return FacesContext.getCurrentInstance().getViewRoot().getLocale();
			}else {
				return new Locale(Constants.ENGLISH_LOCALE_STRING);
			}
		}catch(Exception e) {
			return new Locale(Constants.ENGLISH_LOCALE_STRING);
		}
		
		//locale = new Locale(localeMaster.getLocalId());
	}
	/**
	 * @param pattern
	 * @return String
	 */
	public String getLocalizedCurrencyPattern(final String pattern) {
		return pattern;
	}

	/**
	 * To retrieve default locale of user as string
	 * 
	 * @return String
	 */
	public String getDefaultLocaleAsString() {
		/*String language = getLocale().getLanguage();
		defaultLocaleAsString = language==null?Constants.ENGLISH_LOCALE_STRING:language;*/
		return getLocale().getLanguage();
	}

	/**
	 * To retrieve locale from session
	 * 
	 * @return Locale
	 */
	public Locale getResourceLocale() {
		//Bryan Start
		LOCALE_MASTER = cacheUtil.getLocaleMasterList();
		//Bryan End
		
		Locale locale = null;
		for (LocaleMaster localeMaster : LOCALE_MASTER) {
			if (localeMaster.getLocalId().equalsIgnoreCase(getDefaultLocaleAsString())) {
				locale = new Locale(localeMaster.getLocalId());
			}
		}
		return locale;
	}

	/**
	 * Gets the Parameterized string.
	 * 
	 * @param str
	 *            the String format
	 * @param arr
	 *            the Value array
	 * @return the Parameterized string
	 */
	public static String getParameterizedString(final String str, final Object... arr) {
		final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		return MessageFormatorUtil.getParameterizedString(locale,str, arr);
	}

	/**
	 * Method used to change language
	 * 
	 */
	public void changeLanguage() throws IOException {
		//Bryan Start
		LOCALE_MASTER = cacheUtil.getLocaleMasterList();
		//Bryan End
		
		if (!StringUtils.isBlank(language)) {
			for (LocaleMaster localeMaster : LOCALE_MASTER) {
				if (localeMaster.getLocalId() !=null && localeMaster.getLocalId().trim().equalsIgnoreCase(language)) {
					this.language = localeMaster.getLocalId().trim();
					User user = UserInfo.getUser();
					// update user locale preference
					if (userSB.updateUserLocale(user, localeMaster.getLocalId())) {
						try {
							// get user object from spring security context update the changed locale
							Authentication auth = SecurityContextHolder.getContext().getAuthentication();
							if (auth != null) {
								WQUserDetails userDetails = (WQUserDetails) auth.getPrincipal();
								if (userDetails != null) {
									userDetails.getUser().setUserLocale(language);
								}
							}
						} catch (Exception e) {
							LOGGER.log(Level.SEVERE, "Error occured while modifying , Reason for failure: ", e);
						}
						userMB.getUser().setUserLocale(language);
					}
					this.defaultLocaleAsString = language;
				}
			}
			populateLanguages();
		}
		final ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		if (null != externalContext) {
			externalContext.redirect(((HttpServletRequest) externalContext.getRequest()).getRequestURI());
		}
	}
	
	/**
	 * put Locale Master String into LOCAL_MASTER List
	 * 
	 * @param defaultLocaleAsString
	 * @param LocaleMaster
	 */
	public  static void putLocaleMaster(LocaleMaster localeMaster) {
		
		LOCALE_MASTER.add(localeMaster);
	}

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * @return the languages
	 */
	public Map<String, String> getLanguages() {
		return languages;
	}

	/**
	 * @param languages the languages to set
	 */
	public void setLanguages(Map<String, String> languages) {
		this.languages = languages;
	}

	public void setDefaultLocaleAsString(String defaultLocaleAsString) {
		this.defaultLocaleAsString = defaultLocaleAsString;
	}

}