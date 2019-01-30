package com.avnet.emasia.webquote.quote.ejb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.DrProjectHeader;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.QuoteNumber;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.entity.ValidationRule;

/**
 * Session Bean implementation class QuoteSB
 */
@Stateless
@LocalBean
public class ValidationRuleSB {
	
	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	

	public List<ValidationRule> findAll(){
		Query query = em.createQuery("select v from ValidationRule v");
		return query.getResultList();
	}
	
	public List<ValidationRule> findByBizUnit(BizUnit bizUnit){
		Query query = em.createQuery("select v from ValidationRule v where v.bizUnit = :bizUnit");
		query.setParameter("bizUnit", bizUnit);
		return query.getResultList();
		
	}

	
}
