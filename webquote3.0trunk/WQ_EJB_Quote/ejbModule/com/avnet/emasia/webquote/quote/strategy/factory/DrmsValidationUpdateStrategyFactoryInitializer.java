package com.avnet.emasia.webquote.quote.strategy.factory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.avnet.emasia.webquote.cache.Constants;
import com.avnet.emasia.webquote.entity.SystemCodeMaintenance;

@Startup
@Local
@Singleton
public class DrmsValidationUpdateStrategyFactoryInitializer {
	
	@PersistenceContext(unitName="Server_Source")
	EntityManager em;

	@PostConstruct
	public void init() {
		Query query = em.createQuery("select c from SystemCodeMaintenance c where c.category = :category");
		query.setParameter("category", "DRMS_VALIDATION_IMPL");
		List<SystemCodeMaintenance> configList = query.getResultList();

		if (configList != null && configList.size() > 0) {
			Map<String, String> map = new HashMap<String, String>();
			for (SystemCodeMaintenance config : configList) {
				String region = config.getRegion().trim();
				String value = config.getValue().trim();
				map.put(region, value);
			}
			DrmsValidationUpdateStrategyFactory.getInstance().initImplMap(map);

			TreeMap<String, DrmsValidationUpdateStrategyFactory> drmsValidationMap = new TreeMap<String, DrmsValidationUpdateStrategyFactory>();
			drmsValidationMap.put(Constants.DRMS_VALIDATION, DrmsValidationUpdateStrategyFactory.getInstance());
		}
	}

}
