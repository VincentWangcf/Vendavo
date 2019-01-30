package com.avnet.emasia.webquote.reports.ejb;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.reports.vo.RORReportSearchCriteria;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilities.util.QuoteUtil;

@Stateless
@LocalBean
public class RORReportSB
{
	@PersistenceContext(unitName = "Server_Source")
	EntityManager em;
	
	@EJB
	UserSB userSB;	

	private static final Logger LOGGER = Logger.getLogger(RORReportSB.class.getName());

	public List<QuoteItem> findRORReport(RORReportSearchCriteria criteria, User user){
		
		List<User> users = userSB.findAllSalesForCs(user);
		List<String> stringBizUnits = new ArrayList<String>();
		
		if(criteria.getBizUnits()!=null)
		{
			for(int i = 0 ; i < criteria.getBizUnits().size() ; i++)
			{
				stringBizUnits.add(criteria.getBizUnits().get(i).getName());
			}
		}
		
		if(users == null || (users != null && users.size() == 0)){
			return null;
		}
				
		String sql = "select qi from QuoteItem qi where ";
		
		sql += " qi.quote.sales in :users";
		
		List<String> stages = criteria.getStage();
		if(stages == null || (stages != null && stages.size() == 0)){
			stages = new ArrayList<String>();
			stages.add(" ");
		}
		
		sql += " and qi.stage in :stages";
		
		if(!QuoteUtil.isEmpty(criteria.getCustomer())){
			sql += " and UPPER(CONCAT(qi.soldToCustomer.customerName1, qi.soldToCustomer.customerName2)) like :soldToCustomer";
		}
		
		if(!QuoteUtil.isEmpty(criteria.getEmployeeName())){
			sql += " and qi.quote.sales.name like :employeeName";
		}

		if(!QuoteUtil.isEmpty(criteria.getTeam())){
			sql += " and qi.quote.sales.team.name like :team";
		}

		if(!QuoteUtil.isEmpty(criteria.getMfr())){
			sql += " and qi.quotedMaterial.manufacturer.name like :mfr";
		}

		if(criteria.getStartDate() != null){
			sql += " and qi.submissionDate >= :startDate";
		}
		
		if(criteria.getEndDate() != null){
			sql += " and qi.submissionDate <= :endDate";
		}
		
		if(stringBizUnits.size() > 0){
			
			sql += " and qi.quote.bizUnit.name in :stringBizUnits";
		}
		
		Query query = em.createQuery(sql);

		query.setParameter("users", users);		
		query.setParameter("stages", stages);		
		
		if(!QuoteUtil.isEmpty(criteria.getCustomer())){		
			query.setParameter("soldToCustomer", "%"+criteria.getCustomer()+"%");		
		}

		if(!QuoteUtil.isEmpty(criteria.getEmployeeName())){
			query.setParameter("employeeName", "%"+criteria.getEmployeeName()+"%");		
		}
		
		if(!QuoteUtil.isEmpty(criteria.getTeam())){
			query.setParameter("team", "%"+criteria.getTeam()+"%");		
		}		

		if(!QuoteUtil.isEmpty(criteria.getMfr())){
			query.setParameter("mfr", "%"+criteria.getMfr()+"%");		
		}

		if(criteria.getStartDate() != null){
			query.setParameter("startDate", criteria.getStartDate());		
		}
		
		if(criteria.getEndDate() != null){
			query.setParameter("endDate", criteria.getEndDate());		
		}
		
		if(criteria.getBizUnits() != null){
			query.setParameter("stringBizUnits", stringBizUnits);	
		}
		
		List<QuoteItem> results = query.getResultList();
		
		return results; 
	}
	
}
