package com.avnet.emasia.webquote.commodity.util;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EmbeddedId;
import javax.persistence.Id;

/**
 * 
 * @author 041863
 */
public class EntityUtil {

	protected static final Logger LOGGER = Logger.getLogger(EntityUtil.class.getName());

	public static Object getId(Object enttity) {
		// TODO: find getId method first
		Object outcome = "";
		Field[] fields = enttity.getClass().getDeclaredFields();
		try {
			Field idField = null;
			for (Field field : fields) {
				field.setAccessible(true);
				Id ann = field.getAnnotation(Id.class);
				if (ann != null) {
					idField = field;
					break;
				}
				EmbeddedId eid = field.getAnnotation(EmbeddedId.class);
				if (eid != null) {
					idField = field;
					break;
				}
			}

			if (idField != null) {
				outcome = idField.get(enttity);
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE,"Error occures in find getId method "+ enttity.toString()+ "while getting the field" + e.getMessage());
			
		}
		return outcome;
	}

	public static Object getPropertyType(Class<?> cls, String propertyName) {
		Object retvalue = null;
	try {
			if (propertyName.contains(".")) {
				String[] type = propertyName.split("\\.");

				for (int i = 0; i < type.length - 1; i++) {
					cls = cls.getDeclaredField(type[i]).getType();
				}

				propertyName = type[type.length - 1];
			}
			Field field = cls.getDeclaredField(propertyName);
			// field.setAccessible(true);
			retvalue = field.getType();
		} catch (Exception e) {
			try {
				Field field = cls.getSuperclass().getDeclaredField(propertyName);
				retvalue = field.getType();
			} catch (Exception ex) {
				LOGGER.log(Level.SEVERE,"Error occures in find getPropertyType fields :: "+retvalue+" in getPropertyType Method "+ e.getMessage());
			}
			LOGGER.log(Level.SEVERE,"Error occures in find getPropertyType fields :: "+retvalue+" in getPropertyType Method "+ e.getMessage());
		}
		return retvalue;
	}
}
