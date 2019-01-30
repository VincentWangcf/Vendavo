package com.avnet.emasia.webquote.reports.ejb;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.DataAccess;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.MaterialType;
import com.avnet.emasia.webquote.entity.ProductGroup;
import com.avnet.emasia.webquote.entity.ProgramType;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.Role;
import com.avnet.emasia.webquote.entity.Team;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.ejb.MaterialSB;
import com.avnet.emasia.webquote.reports.vo.QuoteDailyOptVo;
import com.avnet.emasia.webquote.reports.vo.QuoteReportCriteria;
import com.avnet.emasia.webquote.reports.vo.RFQBacklogReportVo;
import com.avnet.emasia.webquote.user.ejb.UserSB;

@Stateless
@LocalBean
public class QuoteDailyOptReportSB
{
	private static final Logger LOG = Logger
			.getLogger(QuoteDailyOptReportSB.class.getName());

	public List<RFQBacklogReportVo> rfqBacklogReportVo = new ArrayList<RFQBacklogReportVo>();

	@EJB
	private UserSB userSB;

	@EJB
	MaterialSB materialSB;
	
	@PersistenceContext(unitName = "Server_Source")
	EntityManager em;

	public List<RFQBacklogReportVo> getRfqBacklogReportVo()
	{
		return rfqBacklogReportVo;
	}

	public void setRfqBacklogReportVo(
			List<RFQBacklogReportVo> rfqBacklogReportVo)
	{
		this.rfqBacklogReportVo = rfqBacklogReportVo;
	}


	/**
	 * Data search.
	 * 
	 * @param criteria
	 * @return
	 */
	public List<Tuple> quoteDailyOutputSearch(
			QuoteReportCriteria criteria)
	{
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery(); 
		Root<QuoteItem> quoteItem = cq.from(QuoteItem.class);
		
		Expression<Date> lastQcUpdatedOn = quoteItem.get("lastQcUpdatedOn");
		Expression<BizUnit> bizUnit = quoteItem.<Quote>get("quote").<BizUnit>get("bizUnit");
		Predicate predicateLastUpdatedQcIsNotNull = cb.isNotNull(quoteItem.<String>get("lastUpdatedQc"));
		Predicate predicateLastQcUpdatedOnIsNotNull = cb.isNotNull(quoteItem.<Boolean>get("lastQcUpdatedOn"));
		Predicate predicateLastQcUpdatedOnIsGreaterThan = cb.greaterThanOrEqualTo(lastQcUpdatedOn,new Timestamp(criteria.getSentOutDateFrom().getTime()));
		Predicate predicateLastQcUpdatedOnIsLessThan = cb.lessThanOrEqualTo(lastQcUpdatedOn,new Timestamp(criteria.getSentOutDateTo().getTime()));
		
		
		Predicate predicateBizUnitIn = bizUnit.in(criteria.getBizUnits());
		Predicate status = quoteItem.<String>get("status").in(criteria.getStatus());
		Predicate stage = null;
		Predicate quotedBy = null;
		
		
		if(criteria.getStage()!=null && criteria.getStage().size()!=0)
		{
			stage = quoteItem.<String>get("stage").in(criteria.getStage());
		}
		else
		{
			stage = cb.isNull(quoteItem.<Boolean>get("id"));
		}
		
		if(criteria.getQcPricer()!=null && !criteria.getQcPricer().equals(""))
		{
			quotedBy = cb.like(cb.lower(quoteItem.<String>get("lastUpdatedQcName")), criteria.getQcPricer().toLowerCase());
		}
		else
		{
			quotedBy = cb.isNotNull(quoteItem.<Boolean>get("id"));
		}
		
		cq.multiselect(quoteItem.get("lastUpdatedQcName"), quoteItem.get("lastQcUpdatedOn"));
	    cq.where(cb.and(
	    		predicateBizUnitIn,
	    		predicateLastUpdatedQcIsNotNull,
	    		predicateLastQcUpdatedOnIsNotNull,
	    		predicateLastQcUpdatedOnIsGreaterThan,
	    		predicateLastQcUpdatedOnIsLessThan,
	    		quotedBy,
	    		status,
	    		stage
	    		));
	    cq.orderBy(cb.desc(quoteItem.get("lastQcUpdatedOn")));
		
		Query query = (Query) em.createQuery(cq);
    	
		List<Tuple> tuples = query.getResultList();
		
		return tuples;
	}
	



}
