package com.avnet.emasia.webquote.ejb.interceptor;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class EntityManagerInjector {
	@PersistenceContext(unitName="Server_Source")
	private EntityManager em;
	
	@AroundInvoke
	public Object associate(InvocationContext ic)throws Exception {
		ThreadLocalEntityManager.associateWithThread(em); 
		try {
			return ic.proceed();
		} finally {
			ThreadLocalEntityManager.cleanupThread();
		}
	}
}
