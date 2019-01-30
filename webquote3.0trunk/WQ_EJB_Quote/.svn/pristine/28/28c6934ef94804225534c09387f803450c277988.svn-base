package com.avnet.webquote.quote.ejb.utility;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.DataAccess;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.ProductGroup;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.Team;
import com.avnet.emasia.webquote.quote.vo.MyQuoteSearchCriteria;
import com.avnet.emasia.webquote.user.ejb.MaterialTypeSB;
import com.avnet.emasia.webquote.user.ejb.ProgramTypeSB;

public class EJBQuoteUtility
{

	public static void getQuery(CriteriaQuery c, final List<BizUnit> bizUnits,final Join<Quote, BizUnit> bizUnit,
			CriteriaBuilder cb, List<Predicate> criterias) {
		if (bizUnits != null && bizUnits.size()!=0) {
			CriteriaBuilder.In<String> in = cb.in(bizUnit.<String>get("name"));
			
			for(BizUnit bu : bizUnits){
				in.value(bu.getName());
			}
			criterias.add(in);			
		}else{
			Predicate predicate = cb.like(bizUnit.<String>get("name"), "" );
			criterias.add(predicate);
		}

		if (criterias.size() == 0) {
			
		} else if (criterias.size() == 1) {
			c.where(criterias.get(0));
		} else {
			c.where(cb.and(criterias.toArray(new Predicate[0])));
		}
	}
	
	public static List<Predicate> genericQuery(final List<DataAccess> dataAceesss,final Root<QuoteItem> quoteItem,
			final Join<QuoteItem, ProductGroup> productGroup, final Join<Quote, Team> team,
			final Join<QuoteItem, Manufacturer> manufacturer, final CriteriaBuilder cb, final List<Predicate> criterias) {
		if( dataAceesss != null && dataAceesss.size() != 0){
			List<Predicate> p0 = new ArrayList<Predicate>();
			
			p0 = commonCriteriaForQuote(dataAceesss, quoteItem, productGroup, team, manufacturer, cb, p0);
			
			criterias.add(cb.or(p0.toArray(new Predicate[0])));
		}else{
			Predicate predicate = cb.notEqual(manufacturer.<Long>get("id"), manufacturer.<Long>get("id"));
			criterias.add(predicate);
		}
		
		return criterias;
	}
	
	public static List<Predicate> addPredicates(CriteriaBuilder cb, Path<String> materialTypeId, Path<String> programType,
			Join<QuoteItem, Manufacturer> manufacturer, Join<QuoteItem, ProductGroup> productGroup,
			DataAccess dataAccess, List<Predicate> p1) {
		if(dataAccess.getManufacturer() != null){
			Predicate predicate = cb.equal(manufacturer.<Long>get("id"), dataAccess.getManufacturer().getId());
			p1.add(predicate);
		}
		//andy modify 2.2 delink materialType
		if(dataAccess.getMaterialType() != null){				
			Predicate predicate = cb.equal(materialTypeId, dataAccess.getMaterialType().getName());
			p1.add(predicate);
		}
		//andy modify 2.2 delink programType
		if(dataAccess.getProgramType() != null){
			Predicate predicate = cb.equal(programType, dataAccess.getProgramType().getName());
			p1.add(predicate);
		}

		if(dataAccess.getProductGroup() !=null){
			Predicate predicate = cb.equal(productGroup.<Long>get("id"), dataAccess.getProductGroup().getId());
			Predicate predicate2 = cb.isNull(productGroup);
			p1.add(cb.or(predicate, predicate2));
		}
		
		return p1;
	} 

	public static void findReceiver(final List<Object[]> dataAccessAndBizUnits, final QuoteItem item, final MaterialTypeSB materialTypeSB,final ProgramTypeSB programTypeSB ) {
		Object[] dataAccessAndBizUnit = new Object[2]; 
		DataAccess dataAccess = new DataAccess();
		dataAccess.setManufacturer(item.getRequestedMfr());
		dataAccess.setProductGroup(item.getProductGroup2());
		//andy modify 2.2
		if(item.getMaterialTypeId()!=null){
			dataAccess.setMaterialType(materialTypeSB.findByPK(item.getMaterialTypeId()));
		}
		if(item.getProgramType()!=null){
			dataAccess.setProgramType(programTypeSB.findByName(item.getProgramType()));
		}
		dataAccess.setTeam(item.getQuote().getTeam());
		dataAccessAndBizUnit[0] = dataAccess;
		dataAccessAndBizUnit[1] = item.getQuote().getBizUnit();
		dataAccessAndBizUnits.add(dataAccessAndBizUnit);
	}
	
	/**
	 * @param list
	 * @param quoteItem
	 * @param productGroup
	 * @param team
	 * @param manufacturer
	 * @param cb
	 * @param p0
	 */
	public static List<Predicate> commonCriteriaForQuote(List<DataAccess> list, Root<QuoteItem> quoteItem,
			Join<QuoteItem, ProductGroup> productGroup, Join<Quote, Team> team,
			Join<QuoteItem, Manufacturer> manufacturer, CriteriaBuilder cb, List<Predicate> p0) {
		for(DataAccess dataAccess : list){
			
			List<Predicate> p1 = new ArrayList<Predicate>();

			if(dataAccess.getManufacturer() != null){
				Predicate predicate = cb.equal(manufacturer.<Long>get("id"), dataAccess.getManufacturer().getId());
				p1.add(predicate);
			}
			
			if(dataAccess.getMaterialType() != null){
				//andy modify 2.2 2014/4/11					
				Predicate predicate = cb.equal(quoteItem.<String>get("materialTypeId"), dataAccess.getMaterialType().getName());
				p1.add(predicate);
			}
			
			if(dataAccess.getProgramType() != null){
				//andy modify 2.2
				Predicate predicate = cb.equal(quoteItem.<String>get("programType"), dataAccess.getProgramType().getName());
				p1.add(predicate);
			}

			if(dataAccess.getProductGroup() !=null){
				Predicate predicate = cb.equal(productGroup.<Long>get("id"), dataAccess.getProductGroup().getId());
				Predicate predicate2 = cb.isNull(productGroup);
				p1.add(cb.or(predicate, predicate2));
			}
			
			if(dataAccess.getTeam() !=	null){
				Predicate predicate = cb.equal(team.<String>get("name"), dataAccess.getTeam().getName());
				p1.add(predicate);
			}
			
			//in case p1.size() is 0 (all dataAccess member is null), add below fake expression to make
			//p1 has one element, otherwise p0 will not have p1 element
			if(p1.size() ==0){
				Predicate predicate = cb.equal(quoteItem.<Long>get("id"), quoteItem.<Long>get("id"));
				p1.add(predicate);
			}
			
			p0.add(cb.and(p1.toArray(new Predicate[0])));

		}
		return p0;
	}
}


