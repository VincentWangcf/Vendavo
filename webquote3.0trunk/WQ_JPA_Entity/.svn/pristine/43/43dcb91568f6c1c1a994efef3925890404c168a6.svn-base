package com.avnet.emasia.webquote.entity.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;

import org.apache.commons.beanutils.PropertyUtils;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.queries.UpdateObjectQuery;
import org.eclipse.persistence.sessions.changesets.ChangeRecord;

import com.avnet.emasia.webquote.entity.AuditExchangeRate;
import com.avnet.emasia.webquote.entity.ExchangeRate;

public class ExchangeRateListener extends DescriptorEventAdapter{
	
	protected static final Logger LOGGER = Logger.getLogger(QuoteItemListener.class.getName());
	private final String JPQL_QUERY_USER_NAME = "Select o.name FROM User o where o.employeeId = :employeeId";
 
	public void postInsert(DescriptorEvent event) {
		postSave(event, true);
	}
	public void postUpdate(DescriptorEvent event) {
		postSave(event, false);
	}

	private void postSave(DescriptorEvent event, boolean isPersist) {
		ExchangeRate old = null;
		try {
			old = (ExchangeRate) getOld(event);
			ExchangeRate rate = (ExchangeRate) event.getSource();			
			AuditExchangeRate audit = new AuditExchangeRate();
			audit.setUpdateDate(new Date());// TODO: verify with tough
			audit.setValidFromNew(rate.getValidFrom());
			audit.setCurrFromNew(rate.getCurrFrom());
			audit.setCurrToNew(rate.getCurrTo());
			//audit.setExRateFromNew(rate.getExRateFrom());
			audit.setExRateToNew(rate.getExRateTo());
			audit.setVatNew(rate.getVat());
			audit.setHandlingNew(rate.getHandling());
			audit.setSoldToCodeNew(rate.getSoldToCode());
			// MFR
			audit.setBizUnitNew(rate.getBizUnit());
			audit.setLastUpdatedBy(rate.getLastUpdatedBy());
			audit.setAction("CREATE");
			EntityManager em = EntityManagerUtils.getEntityManager();
			em.setFlushMode(FlushModeType.COMMIT);
			String name = em.createQuery(JPQL_QUERY_USER_NAME).setParameter("employeeId", rate.getLastUpdatedBy()).getSingleResult().toString();
			audit.setLastUpdatedName(name);
			// insert end
			// update start
			if (!isPersist && old != null) {
				audit.setValidFromOld(old.getValidFrom());
				audit.setCurrFromOld(old.getCurrFrom());
				audit.setCurrToOld(old.getCurrTo());
				//audit.setExRateFromOld(old.getExRateFrom());
				audit.setExRateToOld(old.getExRateTo());
				audit.setVatOld(old.getVat());
				audit.setHandlingOld(old.getHandling());
				audit.setSoldToCodeOld(old.getSoldToCode());
				// MFR
				audit.setBizUnitOld(old.getBizUnit());
//				if (rate.getDeleteFlag()) {
//					audit.setAction("DELETE");
//				} else {
//					audit.setAction("UPDATE");
//				}
			}
			event.getSession().insertObject(audit);
		} catch (Exception e) {
			LOGGER.severe("ExchangeRateListener field [method postSave]:" + e.getMessage());
			
		}
	}
	
	private ExchangeRate getOld(DescriptorEvent event) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		ExchangeRate old = (ExchangeRate) event.getOriginalObject();
		if (event.getQuery() != null && event.getQuery() instanceof UpdateObjectQuery) {
			UpdateObjectQuery query = (UpdateObjectQuery) event.getQuery();
			for (ChangeRecord cr : query.getObjectChangeSet().getChanges()) {
				if (query.getObject().getClass().equals(ExchangeRate.class)) {
					PropertyUtils.setProperty(old, cr.getAttribute(), cr.getOldValue());
				}
			}
		}
		return old;
	}
}
