package com.avnet.emasia.webquote.entity.util;

import java.util.Date;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;

import com.avnet.emasia.webquote.entity.NormalPricer;
import com.avnet.emasia.webquote.entity.PricerTypeMapping;
import com.avnet.emasia.webquote.entity.PricerTypeMappingId;

public class MaterialDetailListener extends DescriptorEventAdapter {
	protected static final Logger LOGGER = Logger.getLogger(QuoteItemListener.class.getName());

	public void postInsert(DescriptorEvent event) {
		postSave(event);
	}

	public void postUpdate(DescriptorEvent event) {
		postSave(event);
	}

	private void postSave(DescriptorEvent event) {
		try {
			EntityManager em = EntityManagerUtils.getEntityManager();
			em.setFlushMode(FlushModeType.COMMIT);
			NormalPricer detail = (NormalPricer) event.getSource();
			PricerTypeMappingId id = new PricerTypeMappingId();
			id.setBizUnit(detail.getBizUnitBean().getName());
			id.setMfr(detail.getMaterial().getManufacturer().getName());
			id.setPartNumber(detail.getMaterial().getFullMfrPartNumber());
			PricerTypeMapping ptm = em.find(PricerTypeMapping.class, id);

			if (ptm != null) {
				ptm.setHasFuturePricer(detail.getQuotationEffectiveDate().after(new Date()));
				event.getSession().updateObject(ptm);
			} else {
				PricerTypeMapping newPtm = new PricerTypeMapping();
				newPtm.setId(id);
				newPtm.setCreateDate(new Date());
				newPtm.setUpdateDate(new Date());
				newPtm.setHasNormalFlag(false);
				newPtm.setHasContractFlag(false);
				newPtm.setHasProgramFlag(false);
				newPtm.setHasFuturePricer(detail.getQuotationEffectiveDate().after(new Date()));
				event.getSession().insertObject(newPtm);
			}
		} catch (Exception e) {
			LOGGER.severe("MaterialDetailListener field [method postSave]:" + e.getMessage());
			
		}
	}
}
