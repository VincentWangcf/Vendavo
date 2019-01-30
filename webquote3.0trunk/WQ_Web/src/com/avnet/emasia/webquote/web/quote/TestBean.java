package com.avnet.emasia.webquote.web.quote;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;
import org.jboss.logmanager.Level;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.security.core.context.SecurityContextHolder;

import com.avnet.emasia.webquote.ejb.QuoteBuilderSB;
import com.avnet.emasia.webquote.entity.Attachment;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.exception.WebQuoteException;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.util.DeploymentConfiguration;
import com.avnet.emasia.webquote.utilites.web.util.DownloadUtil;
import com.avnet.emasia.webquote.utilites.web.util.Excel20007Reader;
import com.avnet.emasia.webquote.utilites.web.util.NonSalesCostQuoteBuilderProcessSheet;
import com.avnet.emasia.webquote.utilites.web.util.ProcessSheetInterface;
import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil;
import com.avnet.emasia.webquote.utilites.web.util.SalesCostQuoteBuilderProcessSheet;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.web.quote.constant.QuoteConstant;
import com.avnet.emasia.webquote.web.quote.validator.QuoteBuiderValidator;
import com.avnet.emasia.webquote.web.security.WQUserDetails;

/**
 * 
 * <p>Quote Builder ManagerBean</p>
 * @author Lenon.Yang
 * @version 1.0
 * @date 2017/9/22
 *
 */
@ManagedBean
@SessionScoped
public class TestBean implements Serializable {

	/**
	 * 
	 */
	
	@EJB
	QuoteBuilderSB quoteBuilderSB;
	private User user;
	@ManagedProperty(value="#{resourceMB}")
	/** To Inject ResourceMB instance  */
    private ResourceMB resourceMB;
	public void setResourceMB(final ResourceMB resourceMB) {
		this.resourceMB = resourceMB;
	}
	
	public ResourceMB getResourceMB() {
		return resourceMB;
	}

	public TestBean() {
	
	}

	@PostConstruct
	public void postContruct(){
		user =((WQUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
		
	}
	public void demon(){
		System.out.println("is ok....");
	}
}