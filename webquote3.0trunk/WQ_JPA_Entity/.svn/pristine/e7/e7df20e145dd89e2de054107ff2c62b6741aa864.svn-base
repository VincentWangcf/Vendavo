package com.avnet.emasia.webquote.utilities;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Named;

import com.sun.faces.application.ApplicationAssociate;
import com.sun.faces.application.ApplicationResourceBundle;

@Named
@ApplicationScoped
public class DBResourceBundleReloader  implements Serializable{

	private static final Logger LOGGER = Logger.getLogger(DBResourceBundleReloader.class.getSimpleName());
	
	 private static final long serialVersionUID = 1L;

	  public void onReload(@Observes final DBResourceBundleReloadEvent bundleReloadEvent) {
		LOGGER.info("Resource Bundle clear started");
	    ResourceBundle.clearCache(bundleReloadEvent.getApplicationResourceBundle().getClass().getClassLoader());
	    Set<String> keySet = bundleReloadEvent.getApplicationResourceBundle().getResourceBundles().keySet();
	    for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			String string = iterator.next();
			ApplicationResourceBundle appBundle = bundleReloadEvent.getApplicationResourceBundle().
                    getResourceBundles().get(string);	
			Map resources = (Map) getFieldValue(appBundle, "resources");
			resources.clear();
		}
	    LOGGER.info("Resource Bundle clear completed");
	  }

	  private static  Object getFieldValue(Object object, String fieldName) {
	    try {
	      Field field = object.getClass().getDeclaredField(fieldName);
	      field.setAccessible(true);
	      return field.get(object);
	    } catch (Exception e) {
	      return null;
	    }
	  }
	  
}
