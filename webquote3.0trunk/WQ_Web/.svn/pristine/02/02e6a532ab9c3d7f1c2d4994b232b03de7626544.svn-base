package com.avnet.emasia.webquote.web.quote;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.eclipse.jetty.util.log.Log;
import org.primefaces.context.RequestContext;

public class FacesUtil {

    public static void showWarnMessage(String summary, String detail, String updateId) {
        showMessage(FacesMessage.SEVERITY_WARN, summary, detail, updateId);
    }

    public static void showWarnMessage(String summary, String detail) {
        showMessage(FacesMessage.SEVERITY_WARN, summary, detail, null);
    }

    public static void showInfoMessage(String summary, String detail, String updateId) {
        showMessage(FacesMessage.SEVERITY_INFO, summary, detail, updateId);
    }

    public static void showInfoMessage(String summary, String detail) {
        showMessage(FacesMessage.SEVERITY_INFO, summary, detail, null);
    }

    public static void showErrorMessage(String summary, String detail, String updateId) {
        showMessage(FacesMessage.SEVERITY_ERROR, summary, detail, updateId);
    }

    public static void showErrorMessage(String summary, String detail) {
        showMessage(FacesMessage.SEVERITY_ERROR, summary, detail, null);
    }

    public static void showFatalMessage(String summary, String detail, String updateId) {
        showMessage(FacesMessage.SEVERITY_FATAL, summary, detail, updateId);
    }

    public static void showFatalMessage(String summary, String detail) {
        showMessage(FacesMessage.SEVERITY_FATAL, summary, detail, null);
    }

    public static void showMessage(Severity severity, String summary, String detail, String updateId) {
        FacesMessage msg = new FacesMessage(severity, summary, detail);
        FacesContext.getCurrentInstance().addMessage(updateId, msg);
    }

	public static void showMessage(FacesMessage facesMessage) {
		FacesContext.getCurrentInstance().addMessage(null, facesMessage);
	}
	
    public static List<SelectItem> selectItemsProvider(Map<String, String> map, boolean selectOption) {
        List<SelectItem> items = new ArrayList<>();
        if (selectOption) {
            items.add(new SelectItem("", "-select-"));
        }
        for (String key : map.keySet()) {
            items.add(new SelectItem(key, map.get(key)));
        }
        return items;
    }
    public static List<SelectItem> selectItemsProvider(List<String> sources, boolean selectOption) {
    	List<SelectItem> items = new ArrayList<>();
    	if (selectOption) {
    		items.add(new SelectItem("", "-select-"));
    	}
    	for (String key : sources) {
    		items.add(new SelectItem(key, key));
    	}
    	return items;
    }

	/**
	 * 
	 * 
	 * @param uiId jsf id
	 */
	public static void updateUI(String ...uiId) {
		RequestContext.getCurrentInstance().update(Arrays.asList(uiId));		
	}
	
	public static void executeJS(String js) {
		RequestContext.getCurrentInstance().execute(js);		
	}
	
	public static void redirect(String url) {
		FacesContext fContext = FacesContext.getCurrentInstance();
		ExternalContext extContext = fContext.getExternalContext();
		try {
			extContext.redirect(extContext.getRequestContextPath() + "/" + url);
		} catch (IOException e) {
			Log.info("Redirect error in FacesUtil redirect(), url :" + url +"\n" + e.getMessage() + e.getStackTrace());
		}
	}
	//handle this url which start with http: or https:
	public static void redirectOutter(String url) {
		FacesContext fContext = FacesContext.getCurrentInstance();
		ExternalContext extContext = fContext.getExternalContext();
		try {
			extContext.redirect(url);
		} catch (IOException e) {
			Log.info("Redirect error in FacesUtil redirectOutter(), url :" + url +"\n" + e.getMessage() + e.getStackTrace());
		}
	}

}
