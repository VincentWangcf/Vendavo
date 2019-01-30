package com.avnet.emasia.webquote.entity.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

import com.avnet.emasia.webquote.exception.WebQuoteException;

public class EntityManagerUtils {
	private final static String JNDI_ENTITY_LISTNER_SB = "java:app/WQ_EJB_Quote/EntityLisenterSB";

	public static EntityManager getEntityManager() throws WebQuoteException {
		Object sb;
		EntityManager data;
		try {
			sb = new InitialContext().lookup(JNDI_ENTITY_LISTNER_SB);
			Method method = sb.getClass().getMethod("getEntityManager");
			data = (EntityManager) method.invoke(sb);
		} catch (NamingException | NoSuchMethodException | SecurityException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new WebQuoteException("", e);
		}
		return data;
	}
}
