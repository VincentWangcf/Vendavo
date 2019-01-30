package com.avnet.emasia.webquote.quote.ejb;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.avnet.emasia.webquote.entity.NormalPricer;
import com.avnet.emasia.webquote.entity.PricerTypeMapping;
import com.avnet.emasia.webquote.entity.PricerTypeMappingId;
import com.avnet.emasia.webquote.quote.vo.FuturePriceCriteria;
import com.avnet.emasia.webquote.utilities.util.QuoteUtil;

/**
 * Session Bean implementation class UserSB
 */
@Stateless
@LocalBean
public class PricerTypeMappingSB {

	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	
	@EJB
	private SystemCodeMaintenanceSB sysCodeMaintSB;
	
	private static final Logger LOG =Logger.getLogger(PricerTypeMappingSB.class.getName());
	
/*	
	public PricerTypeMapping findByPK(String mfr, String partNumber, String bizUnit){
		
		PricerTypeMappingId id = new PricerTypeMappingId();
		id.setBizUnit(bizUnit);
		id.setMfr(mfr);
		id.setPartNumber(partNumber);
		return em.find(PricerTypeMapping.class, id);
	}*/
		
/*	public boolean isHasFuturePrice(String mfr, String partNumber, String bizUnit)
	{
		PricerTypeMapping pricerTypeMapping = findByPK( mfr,  partNumber,  bizUnit);
		if(pricerTypeMapping==null)
			return false;
		if(pricerTypeMapping.getHasFuturePricer()==null)
			return false;
		
		return pricerTypeMapping.getHasFuturePricer().booleanValue();
	}*/
	
/*	
	public Set<String> getFuturePriceMap(List<FuturePriceCriteria> futurePriceCriterias)
	{
		Set<String> materialFutureSet = new HashSet<String>();

		List<List<FuturePriceCriteria>> batchFuturePriceCriteriaList = getBatchList(futurePriceCriterias);
		for(List<FuturePriceCriteria> fpcList : batchFuturePriceCriteriaList)
		{
			List<PricerTypeMapping> pricerTypeMappings = getFuturePriceList(fpcList);
			if(pricerTypeMappings!=null && pricerTypeMappings.size()>0)
			{
				for(PricerTypeMapping ptm: pricerTypeMappings)
				{
					materialFutureSet.add(ptm.getId().getMfr()+"|"+ptm.getId().getPartNumber());
				}
			}
		}
		return materialFutureSet;
	}
	*/
	/*public List<List<FuturePriceCriteria>> getBatchList(List<FuturePriceCriteria> futurePriceCriterias)
	{
		int batchNumber = sysCodeMaintSB.getFunturePriceBatchQueryNumber();
		
		List<List<FuturePriceCriteria>> batchList = new ArrayList<List<FuturePriceCriteria>>();
		List<FuturePriceCriteria> tempList = new ArrayList<FuturePriceCriteria>();
		for(int i=0 ; i < futurePriceCriterias.size() ; i++)
		{
			FuturePriceCriteria fpc  = futurePriceCriterias.get(i);
			tempList.add(fpc);
			
			if(i+1==futurePriceCriterias.size() || (i+1)%batchNumber==0)
			{
				batchList.add(tempList);
			}

			if((i+1)%batchNumber==0)
			{
				tempList = new ArrayList<FuturePriceCriteria>();
			}
			
			
		}
		return batchList;
	}*/
	
	/*public List<PricerTypeMapping> getFuturePriceList(List<FuturePriceCriteria> futurePriceCriterias)
	{
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PricerTypeMapping> cq = cb.createQuery(PricerTypeMapping.class);
		Root<PricerTypeMapping> pricerTypeMapping = cq.from(PricerTypeMapping.class);

		Expression<String> mfr = pricerTypeMapping.get("id").get("mfr");
		Expression<String> partNumber = pricerTypeMapping.get("id").get("partNumber");
		Expression<String> bizUnit = pricerTypeMapping.get("id").get("bizUnit");
		Expression<Boolean> hasFuturePricer = pricerTypeMapping.get("hasFuturePricer");
		
		List<Predicate> predicates = new ArrayList<Predicate>();
		List<Predicate> subPredicatesWhole = new ArrayList<Predicate>();
		for(FuturePriceCriteria cpsc : futurePriceCriterias)
		{
			List<Predicate> subPredicates = new ArrayList<Predicate>();
			if(cpsc.getMfr() != null && cpsc.getPartNumber()!=null && cpsc.getBizUnit()!=null)
			{
				Predicate predicate = cb.equal(mfr, cpsc.getMfr());
				subPredicates.add(predicate);
				Predicate predicate1 = cb.equal(partNumber, cpsc.getPartNumber());
				subPredicates.add(predicate1);
				Predicate predicate2 = cb.equal(bizUnit, cpsc.getBizUnit());
				subPredicates.add(predicate2);
				Predicate predicate3 = cb.equal(hasFuturePricer, true);
				subPredicates.add(predicate3);
			}
			subPredicatesWhole.add(cb.and(subPredicates.toArray(new Predicate[0])));
		}
	    predicates.add(cb.or(subPredicatesWhole.toArray(new Predicate[0])));
		cq.select(pricerTypeMapping);
		cq.where(predicates.toArray(new Predicate[]{}));
		TypedQuery<PricerTypeMapping> q = em.createQuery(cq);
		
		List<PricerTypeMapping> pricerTypeMappings = q.getResultList();
		return pricerTypeMappings;
	}*/
/*	
	public List<NormalPricer> filterOutFuturePriceForInstantCheck( List<NormalPricer> materialDetailList, String bizUnit)
	{

// 		Set<String> returnSet  = new HashSet<String>();
//		List<FuturePriceCriteria> futurePriceCriterias = new ArrayList<FuturePriceCriteria>();
//		if(materialDetailList!=null && materialDetailList.size()>0)
//		{
//			for(MaterialDetail md : materialDetailList)
//			{
//				Material m = md.getMaterial();
//				if(md.getMaterial().getManufacturer()!=null)
//				{
//					FuturePriceCriteria fpc = new FuturePriceCriteria();
//					fpc.setBizUnit(bizUnit);
//					fpc.setMfr(m.getManufacturer().getName());
//					fpc.setPartNumber(m.getFullMfrPartNumber());
//					futurePriceCriterias.add(fpc);
//				}		
//			}
//		}
//
//		if(futurePriceCriterias!=null && futurePriceCriterias.size()>0)
//		{
//			returnSet = getFuturePriceMap(futurePriceCriterias);
//		}
		List<NormalPricer>  newMaterialDetailList = new ArrayList<NormalPricer>();
		if(materialDetailList!=null && materialDetailList.size()>0)
		{
				for(NormalPricer md : materialDetailList)
				{
//						String key = md.getMaterial().getManufacturer().getName()+"|"+md.getMaterial().getFullMfrPartNumber();
//						if(!returnSet.contains(key))
//						{
//							newMaterialDetailList.add(md);
//						}
//						else
//						{

							if(md.getQuotationEffectiveDate()!=null && !md.getQuotationEffectiveDate().after(QuoteUtil.getCurrentDateZeroHour()) || md.getQuotationEffectiveDate()==null)
							{
								newMaterialDetailList.add(md);
							}
							
//						}
	
				}
				return newMaterialDetailList;
		}
		else
			return materialDetailList;
	}*/
/*	
	public void callInitPriceTypePro() {
		em.createNativeQuery("{call INIT_PRICER_TYPE_MAPPING()}").executeUpdate();
	}*/
}
