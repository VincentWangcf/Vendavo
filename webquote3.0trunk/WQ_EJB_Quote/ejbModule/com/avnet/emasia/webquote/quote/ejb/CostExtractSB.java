package com.avnet.emasia.webquote.quote.ejb;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.avnet.emasia.webquote.entity.DataAccess;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.quote.vo.CostComparisonBean;
import com.avnet.emasia.webquote.quote.vo.CostExtractSearchCriterial;
import com.avnet.emasia.webquote.util.CostAndDateComparator;
import com.avnet.emasia.webquote.utilities.util.QuoteUtil;

@Stateless
@LocalBean
public class CostExtractSB {
	private static final Logger LOG = Logger.getLogger(CostExtractSB.class.getName());

	@PersistenceContext(unitName = "Server_Source")
	private EntityManager em;
	


//	// Get 00:00:00 on the day time
//	private Date getTimesmorning(Date date) {
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(date);
//		cal.set(Calendar.HOUR_OF_DAY, 0);
//		cal.set(Calendar.SECOND, 0);
//		cal.set(Calendar.MINUTE, 0);
//		cal.set(Calendar.MILLISECOND, 0);
//		return cal.getTime();
//	}

//	// Get 23:59:59 on the day time
//	private Date getTimesnight(Date date) {
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(date);
//		cal.set(Calendar.HOUR_OF_DAY, 23);
//		cal.set(Calendar.SECOND, 59);
//		cal.set(Calendar.MINUTE, 59);
//		return cal.getTime();
//	}
	
	public static Date convertToDate(String source) {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		format.setLenient(false);
		try {
			return format.parse(source);
		} catch (ParseException e) {
			LOG.log(Level.WARNING, "Error occureed in parsing the date :: " + e.getMessage(), e);
		}
		return null;
	}
	
	public List<CostExtractSearchCriterial> convertPricerToBeans(List<Object[]> pricerLst) {
		List<CostExtractSearchCriterial> resultLst = new ArrayList<CostExtractSearchCriterial>();
		if (pricerLst != null) {

			for (Object[] item : pricerLst) {

				CostExtractSearchCriterial bean = new CostExtractSearchCriterial();

				if (item[0] != null) {
					bean.setRegion(item[0].toString());
				}

				if (item[1] != null){
					bean.setSaleCostPart(item[1].toString().equals("Y")?QuoteSBConstant.OPTION_YES:QuoteSBConstant.OPTION_NO);
				}

				if (item[2] != null) {
					bean.setMfr(item[2].toString());
				}

				if (item[3] != null) {
					bean.setPartNumber(item[3].toString());
				}

				if (item[4] != null) {
					bean.setCost(((BigDecimal)item[4]).doubleValue());
				}

				if (item[5] != null) {
					bean.setCostSource(item[5].toString());
				}

				if (item[6] != null) {
					bean.setCostIndicator(item[6].toString());
				}

				if (item[7] != null) {
					bean.setQuoteEffectiveDate(((Date)item[7]));
				}

				if (item[8] != null) {
					bean.setMpq(((BigDecimal)item[8]).toString());
				}

				if (item[9] != null) {
					bean.setMoq(((BigDecimal)item[9]).toString());
				}

				if (item[10] != null) {
					bean.setProductGroup1(item[10].toString());
				}

				if (item[11] != null) {
					bean.setProductGroup2(item[11].toString());
				}
				if (item[12] != null) {
					bean.setProductGroup3(item[12].toString());
				}

				if (item[13] != null) {
					bean.setProductGroup4(item[13].toString());
				}
				
				if (item[14] != null) {
					bean.setSoldToCode(item[14].toString());
				}

				if (item[15] != null) {
					bean.setSoldToName(item[15].toString());
				}
				
				if (item[16] != null) {
					
					if(item[16] instanceof BigDecimal){
						int value = ((BigDecimal)item[16]).intValue();
						bean.setQuotedQty(value);
					}else{
						bean.setQuotedQty((int)item[16]);
					}
				}

				resultLst.add(bean);
			}
		}

		return resultLst;
	}
	
	public List<CostExtractSearchCriterial> convertResultToBeans(List<Object[]> list) {
		List<CostExtractSearchCriterial> resultLst = new ArrayList<CostExtractSearchCriterial>();
		
		if (list != null) {
			for (Object[] item : list) {
				CostExtractSearchCriterial bean = new CostExtractSearchCriterial();

				if (item[0] != null) {
					bean.setMfr(item[0].toString());
				}
				
				if (item[1] != null) {
					bean.setPartNumber(item[1].toString());
				}

				if (item[2] != null) {
					bean.setCost(((BigDecimal)item[2]).doubleValue());
				}

				if (item[3] != null) {
					bean.setCostSource(item[3].toString());
				}

				if (item[4] != null) {
					bean.setCostIndicator(item[4].toString());
				}
				
				if (item[5] != null) {
					bean.setRegion(item[5].toString());
				}
				
				if (item[6] != null) {
					bean.setQuoteEffectiveDate(((Date)item[6]));
				}

				resultLst.add(bean);
			}
		}

		return resultLst;
	}
	
	public int countCostInformation(CostExtractSearchCriterial criteria, boolean isOnline) {
		int count = 0;
		
		StringBuffer sql = new StringBuffer();
		StringBuffer sql2 = new StringBuffer();
		boolean quoteRecord = false;
		boolean pricer = false;
	
		if (criteria.getSelectedCostSoures() != null && criteria.getSelectedCostSoures().size() > 0) {
			
			for(String cs : criteria.getSelectedCostSoures()){
				if(cs.contains("QuoteRecord")){
					quoteRecord = true;
				}else{
					pricer = true;
				}
			}
		}
    	
		sql.append("SELECT count(*) FROM ( ");
		if(pricer){
			sql2.append(buildPricerQuery(criteria, false));
		}
		if(pricer && quoteRecord){
			sql2.append(" union all ");
		}
		if (quoteRecord) {
			sql2.append(buildQuoteQuery(criteria, false, false));
		}
		
		//filter by DataAccess
		if(criteria.getDataAccesses() != null && criteria.getDataAccesses().size() > 0){
			sql.append(buildDataAccessQuery(criteria.getDataAccesses(), sql2.toString()));
		}else{
			sql.append(sql2);
		}
		
		sql.append(" ) ");

		Query query = em.createNativeQuery(sql.toString());
		
		queryCriteria(criteria, query, pricer, quoteRecord);

		count = ((BigDecimal) query.getSingleResult()).intValue();
		
		return count;
	}
	
	public int countCostComparison(CostExtractSearchCriterial criteria, boolean isOnline) {
		int count = 0;
		
		StringBuffer sql = new StringBuffer();
		StringBuffer sql2 = new StringBuffer();
		boolean quoteRecord = false;
		boolean pricer = false;

		
		if (criteria.getSelectedCostSoures() != null && criteria.getSelectedCostSoures().size() > 0) {
			
			for(String cs : criteria.getSelectedCostSoures()){
				if(cs.contains("QuoteRecord")){
					quoteRecord = true;
				}else{
					pricer = true;
				}
			}	
		}  	

		sql.append("SELECT count(*) FROM ( ");
		sql.append("	SELECT distinct mfr_name, part_no FROM ( ");
		
		if(pricer){
			sql2.append(buildPricerQuery(criteria, false));
		}
		if(pricer && quoteRecord){
			sql2.append(" union all ");
		}
		if (quoteRecord) {
			sql2.append(buildQuoteQuery(criteria, false, false));
		}
		
		//filter by DataAccess
		if(criteria.getDataAccesses() != null && criteria.getDataAccesses().size() > 0){
			sql.append(buildDataAccessQuery(criteria.getDataAccesses(), sql2.toString()));	
		}else{
			sql.append(sql2);
		}

		sql.append("	) 				");
		sql.append(") ");


		Query query = em.createNativeQuery(sql.toString());
		
		queryCriteria(criteria, query, pricer, quoteRecord);

		count = ((BigDecimal) query.getSingleResult()).intValue();
		
		return count;
	}
	
	public String buildPricerQuery(CostExtractSearchCriterial criteria, boolean costComparison){
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT                                                                                 ");
		sql.append("    b.name AS region,                                                                  ");
		sql.append("    mr.sales_cost_flag AS sales_cost_flag,                                             ");
		sql.append("    mfr.name AS mfr_name,                                                              ");
		sql.append("    m.full_mfr_part_number AS part_no,                                                 ");
		sql.append("    p.cost AS cost,                                                                    ");
		sql.append("    p.pricer_type AS cost_source,                                                      ");
		sql.append("    ci.name AS cost_indicator,                                                         ");
		sql.append("    p.quotation_effective_date AS effective_date,                                      ");
		sql.append("    p.mpq AS mpq,                                                                      ");
		sql.append("    p.moq AS moq,                                                                      ");
		sql.append("    pg1.name AS product_group1,                                                        ");
		sql.append("    pg2.name AS product_group2,                                                        ");
		sql.append("    mr.product_group3 AS product_group3,                                               ");
		sql.append("    mr.product_group4 AS product_group4,                                               ");
		sql.append("    '' as customer_number,                                                             ");
		sql.append("    '' as customer_name,                                                               ");
		sql.append("    0 as quoted_qty,                                                                   ");
		sql.append("    mfr.id AS mfr_id, 																   ");
		sql.append("    mr.product_group2_id as product_group2_id, 										   ");
		sql.append("    p.material_category as material_type,                                              ");
		sql.append("    pt.name AS program_type_name,                                                      ");
		sql.append("    '' as team                                                                         ");
		sql.append("FROM                                                                                   ");
		sql.append("    pricer p                                                                           ");
		sql.append("    LEFT OUTER JOIN program_type pt ON ( pt.id = p.program_type_id ),                  ");
		sql.append("    biz_unit b,                                                                        ");
		sql.append("    manufacturer mfr,                                                                  ");
		sql.append("    material m,                                                                        ");
		sql.append("	material_regional mr                                                               ");
		sql.append("    LEFT OUTER JOIN product_group pg1 ON ( pg1.id = mr.product_group1_id )             ");
		sql.append("    LEFT OUTER JOIN product_group pg2 ON ( pg2.id = mr.product_group2_id ),            ");
		sql.append("    cost_indicator ci                                                                  ");
		sql.append("WHERE                                                                                  ");
		sql.append("    p.material_id = m.id                                                               ");
		sql.append("	AND p.biz_unit = b.name                                                            ");
		sql.append("	AND p.cost_indicator = ci.name                                                     ");
		sql.append("    AND mr.material_id = m.id                                                          ");
		sql.append("    AND mfr.id = m.manufacturer_id                                                     ");
		sql.append("    and p.biz_unit = mr.biz_unit                                                       ");
		sql.append("    AND p.cost > 0                                                                     ");
    	
    	if (criteria.getSelectedRegions() != null && criteria.getSelectedRegions().size() > 0) {	
    		sql.append("    AND p.biz_unit IN " + buildInSql(new HashSet<String>(criteria.getSelectedRegions())));
    	}
    	
    	if (criteria.getSelectedRegions() != null && criteria.getSelectedRegions().size() > 0) {
    		sql.append("    AND mr.biz_unit IN " + buildInSql(new HashSet<String>(criteria.getSelectedRegions())));
    	}
    	
    	if (criteria.getSelectedCostSoures() != null && criteria.getSelectedCostSoures().size() > 0) {
    		sql.append("    AND p.pricer_type IN " + buildInSql(new HashSet<String>(criteria.getSelectedCostSoures())));
		}
    	
    	if(!costComparison){
    		if (criteria.getSelectedMfrs() != null && criteria.getSelectedMfrs().size() > 0) {
    			sql.append("    AND mfr.name IN " + buildInSql(new HashSet<String>(criteria.getSelectedMfrs())));	
    		}

    		if (!QuoteUtil.isEmpty(criteria.getPartNumber())) {
    			sql.append("    AND m.full_mfr_part_number LIKE #partNo                                                  ");	
    		}
    	}else{
    		if (criteria.getMfrAndPartNo() != null && criteria.getMfrAndPartNo().size() > 0) {
    			sql.append("    AND (mfr.name|| m.full_mfr_part_number) IN " + buildInSql(new HashSet<String>(criteria.getMfrAndPartNo())));	
    		}
    	}
		
    	if (!QuoteUtil.isEmpty(criteria.getSaleCostPart())
				&& !criteria.getSaleCostPart().equalsIgnoreCase(QuoteSBConstant.OPTION_ALL)) {
    		sql.append("    AND mr.sales_cost_flag = #salesCostFlag                                                         ");
		}
    	
		if (criteria.getQuoteEffectiveDateFrom() != null || criteria.getQuoteEffectiveDateTo() != null) {
			sql.append("    AND (                                                                              ");
			sql.append("			(( p.quotation_effective_date >= #quotationEffectiveFrom) AND ( p.quotation_effective_date <= #quotationEffectiveTo))  ");
			sql.append("			OR                                                                         ");
			sql.append("			(( p.quotation_effective_to >= #quotationEffectiveFrom) AND ( p.quotation_effective_to <= #quotationEffectiveTo))      ");
			sql.append("            OR                                                                         ");
			sql.append("			(( p.quotation_effective_date >= #quotationEffectiveFrom) AND ( p.quotation_effective_to <= #quotationEffectiveTo))    ");
			sql.append("        )                                                                              ");
		}
		
		if (criteria.getSelectedCostIndicators() != null && criteria.getSelectedCostIndicators().size() > 0) {
			sql.append("    AND p.cost_indicator IN " + buildInSql(new HashSet<String>(criteria.getSelectedCostIndicators())));
		}
		
		if (!QuoteUtil.isEmpty(criteria.getProductGroup1())) {
			sql.append("    AND pg1.name LIKE #pg1                                                                ");
		}
		
		if (!QuoteUtil.isEmpty(criteria.getProductGroup2())) {
			sql.append("    AND pg2.name LIKE #pg2                                                                ");
		}
		
		if (!QuoteUtil.isEmpty(criteria.getProductGroup3())) {
			sql.append("    AND mr.product_group3 LIKE #pg3                                                       ");
		}
		
		if (!QuoteUtil.isEmpty(criteria.getProductGroup4())) {
			sql.append("    AND mr.product_group4 LIKE #pg4                                                       ");
		}
		
		return sql.toString();
	}
	
	public List<CostExtractSearchCriterial> searchCostInformation(CostExtractSearchCriterial criteria, boolean isOnline, int first,
			int pageSize) {
		
		StringBuffer sql = new StringBuffer();
		StringBuffer sql2 = new StringBuffer();
		boolean quoteRecord = false;
		boolean pricer = false;
		List<CostExtractSearchCriterial> result = null;
		
		if (criteria.getSelectedCostSoures() != null && criteria.getSelectedCostSoures().size() > 0) {
			
			for(String cs : criteria.getSelectedCostSoures()){
				if(cs.contains("QuoteRecord")){
					quoteRecord = true;
				}else{
					pricer = true;
				}
			}
			
		}
		sql.append("SELECT * FROM ( ");
		sql.append("	SELECT a.*, ROWNUM rnum FROM ( ");
		
		if(pricer){
			sql2.append(buildPricerQuery(criteria, false));
		}
		if(pricer && quoteRecord){
			sql2.append(" union all ");
		}
		if (quoteRecord) {
			sql2.append(buildQuoteQuery(criteria, false, false));
		}
		
		//filter by DataAccess
		if(criteria.getDataAccesses() != null && criteria.getDataAccesses().size() > 0){
			sql.append(buildDataAccessQuery(criteria.getDataAccesses(), sql2.toString()));	
		}else{
			sql.append(sql2);
		}
		
		sql.append("	) a WHERE ROWNUM <= #rowTo ");
		sql.append(") WHERE rnum > #rowFrom ");

    	
		Query query = em.createNativeQuery(sql.toString());
		
		queryCriteria(criteria, query, pricer, quoteRecord);

		query.setParameter("rowTo", pageSize + first);
		query.setParameter("rowFrom", first);			

		List<Object[]> searchResult = query.getResultList();

		result = convertPricerToBeans(searchResult);

		return result;
	}
	
	public List<CostComparisonBean> searchCostComparison(CostExtractSearchCriterial criteria, boolean isOnline, int first, int pageSize) {
		
		StringBuffer sql = new StringBuffer();
		StringBuffer sql2 = new StringBuffer();
		boolean quoteRecord = false;
		boolean pricer = false;
		
		if (criteria.getSelectedCostSoures() != null && criteria.getSelectedCostSoures().size() > 0) {
			
			for(String cs : criteria.getSelectedCostSoures()){
				if(cs.contains("QuoteRecord")){
					quoteRecord = true;
				}else{
					pricer = true;
				}
			}
			
		}
		sql.append("SELECT * FROM ( ");
		sql.append("	SELECT a.*, ROWNUM rnum FROM ( ");
		sql.append("		SELECT distinct mfr_name, part_no FROM ( ");

		
		if(pricer){
			sql2.append(buildPricerQuery(criteria, false));
		}
		if(pricer && quoteRecord){
			sql2.append(" union all ");
		}
		if (quoteRecord) {
			sql2.append(buildQuoteQuery(criteria, false, false));
		}
		
		//filter by DataAccess
		if(criteria.getDataAccesses() != null && criteria.getDataAccesses().size() > 0){
			sql.append(buildDataAccessQuery(criteria.getDataAccesses(), sql2.toString()));	
		}else{
			sql.append(sql2);
		}

		sql.append("		) 				");
		sql.append("	) a WHERE ROWNUM <= #rowTo ");
		sql.append(") WHERE rnum > #rowFrom ");
    	
		Query query = em.createNativeQuery(sql.toString());
		
		queryCriteria(criteria, query, pricer, quoteRecord);

		query.setParameter("rowTo", pageSize + first);
		query.setParameter("rowFrom", first);			

		List<Object[]> searchResult = query.getResultList();
		

		//Start prepare key
		List<String> mfrAndPartNo = new ArrayList<String>(); 
		Map<String,CostComparisonBean> costComparisonMap = new HashMap<String,CostComparisonBean>();
		for(Object[] p : searchResult){
			if(p[0] != null && p[1] != null){
				String key = p[0].toString()+p[1].toString();
				mfrAndPartNo.add(key);
				costComparisonMap.put(key, new CostComparisonBean(p[0].toString(), p[1].toString()));
			}

		}
		criteria.setMfrAndPartNo(mfrAndPartNo);
		//End
		
		if(criteria.getMfrAndPartNo()!= null && criteria.getMfrAndPartNo().size() > 0){
			find3LowestCostByPart(costComparisonMap, criteria);

//			for (Map.Entry<String, CostComparisonBean> entry : costComparisonMap.entrySet()) {
//				System.out.println("show top 3 lowest cost : " + entry.getKey() );
//
//
//				CostComparisonBean b = entry.getValue(); 
//				System.out.println(b.getFirstLowestCost() + " " +b.getFirstCostSource() + " " +b.getFirstCostIndicator() + " " +b.getFirstRegion()+ " " +b.getFirstQuoteEffectiveDate() 
//				+ "\n "+ " " +b.getSecondLowestCost() + " " +b.getSecondCostSource() + " " +b.getSecondCostIndicator() + " " +b.getSecondRegion()+ " " +b.getSecondQuoteEffectiveDate() + " " +b.getDelta1stAnd2ndLowestCost()
//				+ "\n "+ " " +b.getThirdLowestCost() + " " +b.getThirdCostSource() + " " +b.getThirdCostIndicator() + " " +b.getThirdRegion()+ " " +b.getThirdQuoteEffectiveDate() + " " +b.getDelta1stAnd3rdLowestCost()
//						);
//
//			}

			findLowestCostWithinNMonth(costComparisonMap, criteria);

//			for (Map.Entry<String, CostComparisonBean> entry : costComparisonMap.entrySet()) {
//				System.out.println("show  lowest cost within 3/6/12mth : " + entry.getKey() );
//
//
//				CostComparisonBean b = entry.getValue(); 
//				System.out.println(b.getLowestCostPast12Mths() + " " +b.getLowestCostRegionPast12Mths() + " " +b.getLowestCostQuoteEffectiveDatePast12Mths()  
//				+ "\n "+ " " +b.getLowestCostPast6Mths() + " " +b.getLowestCostRegionPast6Mths() + " " +b.getLowestCostQuoteEffectiveDatePast6Mths() 
//				+ "\n "+ " " +b.getLowestCostPast3Mths() + " " +b.getLowestCostRegionPast3Mths() + " " +b.getLowestCostQuoteEffectiveDatePast3Mths() 
//						);
//
//			}
		}
		
		return new ArrayList<CostComparisonBean>(costComparisonMap.values());
	}
	
	public List<CostComparisonBean> searchCostComparisonOffline(CostExtractSearchCriterial criteria, User user, boolean isOnline, int first, int pageSize) {

		Map<String,CostComparisonBean> costComparisonMap = new HashMap<String,CostComparisonBean>();

		find3LowestCostByPartForCostComparisonOffline(costComparisonMap, criteria, user, first, pageSize);

		//			for (Map.Entry<String, CostComparisonBean> entry : costComparisonMap.entrySet()) {
		//				System.out.println("show top 3 lowest cost : " + entry.getKey() );
		//
		//
		//				CostComparisonBean b = entry.getValue(); 
		//				System.out.println(b.getFirstLowestCost() + " " +b.getFirstCostSource() + " " +b.getFirstCostIndicator() + " " +b.getFirstRegion()+ " " +b.getFirstQuoteEffectiveDate() 
		//				+ "\n "+ " " +b.getSecondLowestCost() + " " +b.getSecondCostSource() + " " +b.getSecondCostIndicator() + " " +b.getSecondRegion()+ " " +b.getSecondQuoteEffectiveDate() + " " +b.getDelta1stAnd2ndLowestCost()
		//				+ "\n "+ " " +b.getThirdLowestCost() + " " +b.getThirdCostSource() + " " +b.getThirdCostIndicator() + " " +b.getThirdRegion()+ " " +b.getThirdQuoteEffectiveDate() + " " +b.getDelta1stAnd3rdLowestCost()
		//						);
		//
		//			}

		findLowestCostWithinNMonthForCostComparisonOffline(costComparisonMap, criteria, user, first, pageSize);

		//			for (Map.Entry<String, CostComparisonBean> entry : costComparisonMap.entrySet()) {
		//				System.out.println("show  lowest cost within 3/6/12mth : " + entry.getKey() );
		//
		//
		//				CostComparisonBean b = entry.getValue(); 
		//				System.out.println(b.getLowestCostPast12Mths() + " " +b.getLowestCostRegionPast12Mths() + " " +b.getLowestCostQuoteEffectiveDatePast12Mths()  
		//				+ "\n "+ " " +b.getLowestCostPast6Mths() + " " +b.getLowestCostRegionPast6Mths() + " " +b.getLowestCostQuoteEffectiveDatePast6Mths() 
		//				+ "\n "+ " " +b.getLowestCostPast3Mths() + " " +b.getLowestCostRegionPast3Mths() + " " +b.getLowestCostQuoteEffectiveDatePast3Mths() 
		//						);
		//
		//			}
		//}

		return new ArrayList<CostComparisonBean>(costComparisonMap.values());
	}
	
	public String buildQuoteQuery(CostExtractSearchCriterial criteria, boolean costComparison, boolean lowestCostNMothFlag) {
		
		StringBuffer sql = new StringBuffer();
    	
		sql.append("SELECT																														");
		sql.append("    b.name  AS region,                                                                                                      ");
		sql.append("    qi.sales_cost_flag AS sales_cost_flag,                                                                                  ");
		sql.append("    mfr.name AS mfr_name,                                                                                                   ");
		sql.append("    qi.quoted_part_number AS part_no,                                                                                       ");
		sql.append("    qi.cost AS cost,                                                                                                        ");
		sql.append("    'Quote Record' AS cost_source,                                                                                          ");
		sql.append("    qi.cost_indicator AS cost_indicator,                                                                                    ");
		sql.append("    qi.quote_expiry_date AS effective_date,                                                                                 ");
		sql.append("    qi.mpq AS mpq,                                                                                                          ");
		sql.append("    qi.moq AS moq,                                                                                                          ");
		sql.append("    pg1.name AS product_group1,                                                                                             ");
		sql.append("    pg2.name AS product_group2,                                                                                             ");
		sql.append("    qi.product_group3 AS product_group3,                                                                                    ");
		sql.append("    qi.product_group4 AS product_group4,                                                                                    ");
		sql.append("    c.customer_number as customer_number,                                                                                   ");
		sql.append("    (c.customer_name1 || ' ' || c.customer_name2 || ' ' || c.customer_name3 || ' ' || c.customer_name4) as customer_name,  ");
		sql.append("    qi.quoted_qty as quoted_qty,                                                                                            ");
		sql.append("    mfr.id as mfr_id, ");
		sql.append("    qi.product_group2_id AS product_group2_id, ");
		sql.append("    qi.material_type_id AS material_type, "); 
		sql.append("    qi.program_type AS program_type, ");
		sql.append("    q.team_id AS team ");
		sql.append("  FROM                                                                                                                      ");
		sql.append("    quote_item qi                                                                                                         ");
		sql.append("    LEFT OUTER JOIN product_group pg1 ON ( pg1.id = qi.product_group1_id )                                                  ");
		sql.append("    LEFT OUTER JOIN product_group pg2 ON ( pg2.id = qi.product_group2_id ),                                                 ");
		sql.append("	quote q                                                                                                                ");
		sql.append("	LEFT OUTER JOIN team t ON ( t.name = q.team_id ), ");
		sql.append("	biz_unit b,                                                                                                             ");
		sql.append("	manufacturer mfr,                                                                                                       ");
		sql.append("    customer c                                                                                                            ");
		sql.append("  WHERE                                                                                                                     ");
		sql.append("	q.id = qi.quote_id                                                                                                      ");
		sql.append("    AND mfr.id = qi.requested_mfr_id                                                                                        ");
		sql.append("    AND b.name = q.biz_unit                                                                                                 ");
		sql.append("    AND c.customer_number = qi.sold_to_customer_number                                                                      ");
		sql.append("	AND qi.stage = 'FINISH'                                                                                                 ");
		sql.append("	AND qi.cost > 0                                                                                                 ");
    	
    	if (criteria.getSelectedRegions() != null && criteria.getSelectedRegions().size() > 0) {	
    		sql.append("    AND q.biz_unit IN " + buildInSql(new HashSet<String>(criteria.getSelectedRegions())));
    	}
    	
    	if(!costComparison){
    		if (criteria.getSelectedMfrs() != null && criteria.getSelectedMfrs().size() > 0) {
    			sql.append("    AND mfr.name IN " + buildInSql(new HashSet<String>(criteria.getSelectedMfrs())));
    		}

    		if (!QuoteUtil.isEmpty(criteria.getPartNumber())) {
    			sql.append("    AND qi.quoted_part_number LIKE #partNo                                                                                        ");	
    		}
    	}else{
    		if (criteria.getMfrAndPartNo() != null && criteria.getMfrAndPartNo().size() > 0) {
    			sql.append("    AND (mfr.name || qi.quoted_part_number) IN " + buildInSql(new HashSet<String>(criteria.getMfrAndPartNo())));
    		}
    	}
		
    	if (!QuoteUtil.isEmpty(criteria.getSaleCostPart())
				&& !criteria.getSaleCostPart().equalsIgnoreCase(QuoteSBConstant.OPTION_ALL)) {
    		sql.append("    AND qi.sales_cost_flag = #salesCostFlag                                                                                               ");
		}
    	
    	if (criteria.getSelectedCostIndicators() != null && criteria.getSelectedCostIndicators().size() > 0) {	
			sql.append("    AND qi.cost_indicator IN " + buildInSql(new HashSet<String>(criteria.getSelectedCostIndicators())));
		}
    	
    	if(!lowestCostNMothFlag){
    		if (criteria.getQuoteEffectiveDateFrom() != null ) {
    			sql.append("    AND qi.quote_expiry_date >= #quoteExpiryDateFrom                                                                                            ");
    		}

    		if (criteria.getQuoteEffectiveDateTo() != null ) {
    			sql.append("    AND qi.quote_expiry_date <= #quoteExpiryDateTo                                                                                            ");
    		}
    	}else{
    		if (criteria.getQuoteEffectiveDate12MthFrom() != null ) {
    			sql.append("    AND qi.quote_expiry_date >= #quoteExpiryDateFrom                                                                                            ");
    		}

    		if (criteria.getQuoteEffectiveDate12MthTo() != null ) {
    			sql.append("    AND qi.quote_expiry_date <= #quoteExpiryDateTo                                                                                            ");
    		}
    	}
    	
    	if (!QuoteUtil.isEmpty(criteria.getProductGroup1())) {
    		sql.append("    AND   pg1.name LIKE #pg1                                                                                                   ");
		}
		if (!QuoteUtil.isEmpty(criteria.getProductGroup2())) {
			sql.append("    AND   pg2.name LIKE #pg2                                                                                                   ");
		}
		
		if (!QuoteUtil.isEmpty(criteria.getProductGroup3())) {
			sql.append("    AND   qi.product_group3 LIKE #pg3                                                                                          ");
		}
		
		if (!QuoteUtil.isEmpty(criteria.getProductGroup4())) {
			sql.append("    AND   qi.product_group3 LIKE #pg3                                                                                          ");
		}

    	return sql.toString();
	}

	private void queryCriteria(CostExtractSearchCriterial criteria, Query query, boolean pricer, boolean quoteRecord){
	
		if (!QuoteUtil.isEmpty(criteria.getPartNumber())) {
			query.setParameter("partNo", "%" + criteria.getPartNumber().toUpperCase() + "%");	
    	}
		
		if (!QuoteUtil.isEmpty(criteria.getSaleCostPart()) && !criteria.getSaleCostPart().equalsIgnoreCase(QuoteSBConstant.OPTION_ALL)) {
			query.setParameter("salesCostFlag", criteria.getSaleCostPart().equals(QuoteSBConstant.OPTION_YES) ? true : false);
		}
		
		if(pricer){
			Date quotationEffectiveDateFrom = criteria.getQuoteEffectiveDateFrom();
			Date quotationEffectiveDateTo = criteria.getQuoteEffectiveDateTo();
			if (criteria.getQuoteEffectiveDateFrom() != null || criteria.getQuoteEffectiveDateTo() != null) {
				if (criteria.getQuoteEffectiveDateFrom() == null) {// dd/MM/yyyy
					quotationEffectiveDateFrom = convertToDate("01/01/1900");
				}

				if (criteria.getQuoteEffectiveDateTo() == null) {
					quotationEffectiveDateTo = getNextNYear(quotationEffectiveDateFrom, 1);
				}

				query.setParameter("quotationEffectiveFrom", quotationEffectiveDateFrom);
				query.setParameter("quotationEffectiveTo", quotationEffectiveDateTo);
			}
		}
		
		if (!QuoteUtil.isEmpty(criteria.getProductGroup1())) {
			query.setParameter("pg1", "%" + criteria.getProductGroup1() + "%");
		}
		if (!QuoteUtil.isEmpty(criteria.getProductGroup2())) {
			query.setParameter("pg2", "%" + criteria.getProductGroup2() + "%");
		}
		
		if (!QuoteUtil.isEmpty(criteria.getProductGroup3())) {
			query.setParameter("pg3", "%" + criteria.getProductGroup3() + "%");
		}
		
		if (!QuoteUtil.isEmpty(criteria.getProductGroup4())) {
			query.setParameter("pg4", "%" + criteria.getProductGroup4() + "%");
		}
		
		//For Quote Record - Start
		if (quoteRecord) {

			if (criteria.getQuoteEffectiveDateFrom() != null ) {
				query.setParameter("quoteExpiryDateFrom", criteria.getQuoteEffectiveDateFrom());
				
				if (criteria.getQuoteEffectiveDateTo() == null) {
					query.setParameter("quoteExpiryDateTo", getNextNYear(criteria.getQuoteEffectiveDateFrom(), 1));
					
				}
			}

			if (criteria.getQuoteEffectiveDateTo() != null ) {
				query.setParameter("quoteExpiryDateTo", criteria.getQuoteEffectiveDateTo());
			}
		}
    	//For Quote Record - End
	}
	
	public Map<String,CostComparisonBean> getCostComparisonList(CostExtractSearchCriterial criteria, boolean isOnline, int first,
			int pageSize) {

		//START
		StringBuffer sql = new StringBuffer();
		StringBuffer sql2 = new StringBuffer();
		boolean quoteRecord = false;
		boolean pricer = false;
		
		if (criteria.getSelectedCostSoures() != null && criteria.getSelectedCostSoures().size() > 0) {
			
			for(String cs : criteria.getSelectedCostSoures()){
				if(cs.contains("QuoteRecord")){
					quoteRecord = true;
				}else{
					pricer = true;
				}
			}
			
		}
		sql.append("SELECT * FROM ( ");
		sql.append("	SELECT a.*, ROWNUM rnum FROM ( ");
		sql.append("		SELECT distinct mfr_name, part_no FROM ( ");

		
		if(pricer){
			sql2.append(buildPricerQuery(criteria, false));
		}
		if(pricer && quoteRecord){
			sql2.append(" union all ");
		}
		if (quoteRecord) {
			sql2.append(buildQuoteQuery(criteria, false, false));
		}
		
		//filter by DataAccess
		if(criteria.getDataAccesses() != null && criteria.getDataAccesses().size() > 0){
			sql.append(buildDataAccessQuery(criteria.getDataAccesses(), sql2.toString()));	
		}else{
			sql.append(sql2);
		}

		sql.append("		) 				");
		sql.append("	) a WHERE ROWNUM <= #rowTo ");
		sql.append(") WHERE rnum > #rowFrom ");
    	
		Query query = em.createNativeQuery(sql.toString());
		
		queryCriteria(criteria, query, pricer, quoteRecord);

		query.setParameter("rowTo", pageSize + first);
		query.setParameter("rowFrom", first);			

		List<Object[]> searchResult = query.getResultList();
		
		List<String> mfrAndPartNo = new ArrayList<String>(); 
		Map<String,CostComparisonBean> costComparisonMap = new HashMap<String,CostComparisonBean>();
		for(Object[] p : searchResult){
			if(p[0] != null && p[1] != null){
				String key = p[0].toString()+p[1].toString();
				mfrAndPartNo.add(key);
				costComparisonMap.put(key, new CostComparisonBean(p[0].toString(), p[1].toString()));
			}

		}
		criteria.setMfrAndPartNo(mfrAndPartNo);
		
		if(criteria.getMfrAndPartNo()!= null && criteria.getMfrAndPartNo().size() > 0){
			find3LowestCostByPart(costComparisonMap, criteria);

//			for (Map.Entry<String, CostComparisonBean> entry : costComparisonMap.entrySet()) {
//				System.out.println("show top 3 lowest cost : " + entry.getKey() );
//
//
//				CostComparisonBean b = entry.getValue(); 
//				System.out.println(b.getFirstLowestCost() + " " +b.getFirstCostSource() + " " +b.getFirstCostIndicator() + " " +b.getFirstRegion()+ " " +b.getFirstQuoteEffectiveDate() 
//				+ "\n "+ " " +b.getSecondLowestCost() + " " +b.getSecondCostSource() + " " +b.getSecondCostIndicator() + " " +b.getSecondRegion()+ " " +b.getSecondQuoteEffectiveDate()
//				+ "\n "+ " " +b.getThirdLowestCost() + " " +b.getThirdCostSource() + " " +b.getThirdCostIndicator() + " " +b.getThirdRegion()+ " " +b.getThirdQuoteEffectiveDate()
//						);
//
//			}

			findLowestCostWithinNMonth(costComparisonMap, criteria);

//			for (Map.Entry<String, CostComparisonBean> entry : costComparisonMap.entrySet()) {
//				System.out.println("show  lowest cost within 3/6/12mth : " + entry.getKey() );
//
//
//				CostComparisonBean b = entry.getValue(); 
//				System.out.println(b.getLowestCostPast12Mths() + " " +b.getLowestCostRegionPast12Mths() + " " +b.getLowestCostQuoteEffectiveDatePast12Mths()  
//				+ "\n "+ " " +b.getLowestCostPast6Mths() + " " +b.getLowestCostRegionPast6Mths() + " " +b.getLowestCostQuoteEffectiveDatePast6Mths() 
//				+ "\n "+ " " +b.getLowestCostPast3Mths() + " " +b.getLowestCostRegionPast3Mths() + " " +b.getLowestCostQuoteEffectiveDatePast3Mths() 
//						);
//
//			}
		}
		//END

		return costComparisonMap;
	}
	
	public static String buildInSql(Set<?> set) {
		Iterator<?> it = set.iterator();
		StringBuffer sb = new StringBuffer("(");
		while (it.hasNext()) {
			sb.append("'").append(it.next()).append("',");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(")");
		return sb.toString();
	}
	
	public String buildDataAccessQuery(List<DataAccess> list, String query) {

		StringBuffer sql = new StringBuffer();
		boolean firstCriteria = true;
		int count = 1;
			
		if(list != null && list.size()==1){
			if(list.get(0).getManufacturer() == null 
				&& list.get(0).getMaterialType() == null 
				&& list.get(0).getProductGroup() == null
				&& list.get(0).getProgramType() == null
				&& list.get(0).getTeam() == null){
				
				return query;
			}
		}
		

		sql.append("SELECT	* FROM ( ");
		sql.append(query);
		sql.append(") WHERE ");

		for(DataAccess dataAccess : list){
			System.out.println(dataAccess.getManufacturer() + " - " + dataAccess.getMaterialType() + " - " + dataAccess.getProgramType() + " - " + dataAccess.getProductGroup() + " - " + dataAccess.getTeam());
			if(count > 1){
				sql.append(" OR ");
			}
			sql.append("( ");
			if(dataAccess.getManufacturer() != null){
				if(!firstCriteria)
					sql.append(" AND ");

				sql.append(" mfr_id = " + dataAccess.getManufacturer().getId());

				firstCriteria = false;
			}

			if(dataAccess.getMaterialType() != null){
				if(!firstCriteria)
					sql.append(" AND ");

				sql.append(" material_type = " + dataAccess.getMaterialType().getName());

				firstCriteria = false;
			}

			if(dataAccess.getProgramType() != null){
				if(!firstCriteria)
					sql.append(" AND ");

				sql.append(" program_type_name = " + dataAccess.getProgramType().getId());

				firstCriteria = false;
			}

			if(dataAccess.getProductGroup() !=null){
				if(!firstCriteria)
					sql.append(" AND ");

				sql.append(" (product_group2_id = " + dataAccess.getProductGroup().getId() + " OR product_group2_id IS NULL ) ");

				firstCriteria = false;
			}

			if(dataAccess.getTeam() !=	null){
				if(!firstCriteria)
					sql.append(" AND ");	

				sql.append(" (team = " + dataAccess.getTeam().getName() + " OR team IS NULL ) ");

				firstCriteria = false;
			}

			sql.append(") ");

			count++;
			firstCriteria = true;
		}

		return sql.toString();
	}
	
	private void find3LowestCostByPart(Map<String,CostComparisonBean> costComparisonMap, CostExtractSearchCriterial criteria){

		StringBuffer sql = new StringBuffer();
		StringBuffer sql2 = new StringBuffer();
		boolean quoteRecord = false;
		boolean pricer = false;
		//List<CostExtractSearchCriterial> result = null;

		if (criteria.getSelectedCostSoures() != null && criteria.getSelectedCostSoures().size() > 0) {
			for(String cs : criteria.getSelectedCostSoures()){
				if(cs.contains("QuoteRecord")){
					quoteRecord = true;
				}else{
					pricer = true;
				}
			}
		}

		sql.append("SELECT mfr_name, part_no, cost, cost_source, cost_indicator, region, effective_date FROM ( ");

		if(pricer){
			sql2.append(buildPricerQuery(criteria, true));
		}
		if(pricer && quoteRecord){
			sql2.append(" union all ");
		}
		if (quoteRecord) {
			sql2.append(buildQuoteQuery(criteria, true, false));
		}

		//filter by DataAccess
		if(criteria.getDataAccesses() != null && criteria.getDataAccesses().size() > 0){
			sql.append(buildDataAccessQuery(criteria.getDataAccesses(), sql2.toString()));	
		}else{
			sql.append(sql2);
		}
		sql.append(") ");

		Query query = em.createNativeQuery(sql.toString());

		queryCriteriaLowestCost(criteria, query, pricer, quoteRecord);

		List<Object[]> searchResult = query.getResultList();
		
		process3LowestCost(costComparisonMap, searchResult);
	}
	
	private void find3LowestCostByPartForCostComparisonOffline(Map<String,CostComparisonBean> costComparisonMap, CostExtractSearchCriterial criteria, User user, int first, int pageSize){

		StringBuffer sql = new StringBuffer();
		StringBuffer sql2 = new StringBuffer();
		boolean quoteRecord = false;
		boolean pricer = false;
		//List<CostExtractSearchCriterial> result = null;

		if (criteria.getSelectedCostSoures() != null && criteria.getSelectedCostSoures().size() > 0) {
			for(String cs : criteria.getSelectedCostSoures()){
				if(cs.contains("QuoteRecord")){
					quoteRecord = true;
				}else{
					pricer = true;
				}
			}
		}

		sql.append("SELECT mfr_name, part_no, cost, cost_source, cost_indicator, region, effective_date FROM ( ");

		if(pricer){
			sql2.append(buildPricerQueryForCostComparisonOffline(criteria, user, first, pageSize));
		}
		if(pricer && quoteRecord){
			sql2.append(" union all ");
		}
		if (quoteRecord) {
			sql2.append(buildQuoteQueryForCostComparisonOffline(criteria, false, user, first, pageSize));
		}

		//filter by DataAccess
		if(criteria.getDataAccesses() != null && criteria.getDataAccesses().size() > 0){
			sql.append(buildDataAccessQuery(criteria.getDataAccesses(), sql2.toString()));	
		}else{
			sql.append(sql2);
		}
		sql.append(") ");
		//sql.append(") ORDER BY mfr_name, part_no, cost, effective_date DESC");


		Query query = em.createNativeQuery(sql.toString());

		queryCriteriaLowestCost(criteria, query, pricer, quoteRecord);

		List<Object[]> searchResult = query.getResultList();
		
		process3LowestCost(costComparisonMap, searchResult);	
	}
	
	private void process3LowestCost(Map<String,CostComparisonBean> costComparisonMap, List<Object[]> list){
		List<CostExtractSearchCriterial> convertedList = convertResultToBeans(list);

		//Grouping
		Map<String, List<CostExtractSearchCriterial>> groupedMap = convertedList.stream()
				.collect(Collectors.groupingBy(CostExtractSearchCriterial::getKey));

		//Sort list for each element.
		for (Map.Entry<String, List<CostExtractSearchCriterial>> entry : groupedMap.entrySet()) {
			Collections.sort(entry.getValue(), new CostAndDateComparator(true, false));
		}

		for (Map.Entry<String, List<CostExtractSearchCriterial>> entry : groupedMap.entrySet()) {

			String previousCostind = "";
			double previousCost= 0;
			int count = 0;
			for(CostExtractSearchCriterial b : entry.getValue()) {

				if(b.getCost() != previousCost && !b.getCostIndicator().equals(previousCostind)){
					CostComparisonBean bean = null;

					if(costComparisonMap.get(entry.getKey()) == null){
						costComparisonMap.put(entry.getKey(), new CostComparisonBean(b.getMfr(), b.getPartNumber()));
					}
					
					if(count == 0){
						bean = costComparisonMap.get(entry.getKey());
						bean.setFirstLowestCost(b.getCost());
						bean.setFirstCostIndicator(b.getCostIndicator());
						bean.setFirstCostSource(b.getCostSource());
						bean.setFirstQuoteEffectiveDate(b.getQuoteEffectiveDate());
						bean.setFirstRegion(b.getRegion());

						count++;
					}else if(count == 1){
						bean = costComparisonMap.get(entry.getKey());
						bean.setSecondLowestCost(b.getCost());
						bean.setSecondCostIndicator(b.getCostIndicator());
						bean.setSecondCostSource(b.getCostSource());
						bean.setSecondQuoteEffectiveDate(b.getQuoteEffectiveDate());
						bean.setSecondRegion(b.getRegion());

						count++;
					}else if(count == 1){
						bean = costComparisonMap.get(entry.getKey());
						bean.setThirdLowestCost(b.getCost());
						bean.setThirdCostIndicator(b.getCostIndicator());
						bean.setThirdCostSource(b.getCostSource());
						bean.setThirdQuoteEffectiveDate(b.getQuoteEffectiveDate());
						bean.setThirdRegion(b.getRegion());

						count++;
					}

					previousCostind = b.getCostIndicator();
					previousCost = b.getCost();
				}
			}
		}
	}
	
	private void findLowestCostWithinNMonth(Map<String,CostComparisonBean> costComparisonMap, CostExtractSearchCriterial criteria){

		StringBuffer sql = new StringBuffer();
		StringBuffer sql2 = new StringBuffer();
		boolean quoteRecord = false;
		boolean pricer = false;

		if (criteria.getSelectedCostSoures() != null && criteria.getSelectedCostSoures().size() > 0) {
			for(String cs : criteria.getSelectedCostSoures()){
				if(cs.contains("QuoteRecord")){
					quoteRecord = true;
				}else{
					pricer = true;
				}
			}
		}

		sql.append("SELECT mfr_name, part_no, cost, cost_source, cost_indicator, region, effective_date FROM ( ");

		if(pricer){
			sql2.append(buildPricerQuery(criteria, true));
		}
		if(pricer && quoteRecord){
			sql2.append(" union all ");
		}
		if (quoteRecord) {
			sql2.append(buildQuoteQuery(criteria, true, true));
		}

		//filter by DataAccess
		if(criteria.getDataAccesses() != null && criteria.getDataAccesses().size() > 0){
			sql.append(buildDataAccessQuery(criteria.getDataAccesses(), sql2.toString()));	
		}else{
			sql.append(sql2);
		}
		sql.append(") ");
		//sql.append(") ORDER BY mfr_name, part_no, cost, effective_date DESC");


		Query query = em.createNativeQuery(sql.toString());

		queryCriteriaForLowestCostWithNMonth(criteria, query, pricer, quoteRecord);



		List<Object[]> searchResult = query.getResultList();
		
		processLowestCostWithinNMonth(costComparisonMap, searchResult, criteria);
	}
	
	private void findLowestCostWithinNMonthForCostComparisonOffline(Map<String,CostComparisonBean> costComparisonMap, CostExtractSearchCriterial criteria, User user, int first, int pageSize){

		StringBuffer sql = new StringBuffer();
		StringBuffer sql2 = new StringBuffer();
		boolean quoteRecord = false;
		boolean pricer = false;


		if (criteria.getSelectedCostSoures() != null && criteria.getSelectedCostSoures().size() > 0) {
			for(String cs : criteria.getSelectedCostSoures()){
				if(cs.contains("QuoteRecord")){
					quoteRecord = true;
				}else{
					pricer = true;
				}
			}
		}

		sql.append("SELECT mfr_name, part_no, cost, cost_source, cost_indicator, region, effective_date FROM ( ");

		if(pricer){
			sql2.append(buildPricerQueryForCostComparisonOffline(criteria, user, first, pageSize));
		}
		if(pricer && quoteRecord){
			sql2.append(" union all ");
		}
		if (quoteRecord) {
			sql2.append(buildQuoteQueryForCostComparisonOffline(criteria, true, user, first, pageSize));
		}

		//filter by DataAccess
		if(criteria.getDataAccesses() != null && criteria.getDataAccesses().size() > 0){
			sql.append(buildDataAccessQuery(criteria.getDataAccesses(), sql2.toString()));	
		}else{
			sql.append(sql2);
		}
		sql.append(") ");

		Query query = em.createNativeQuery(sql.toString());

		queryCriteriaForLowestCostWithNMonth(criteria, query, pricer, quoteRecord);


		List<Object[]> searchResult = query.getResultList();
		
		processLowestCostWithinNMonth(costComparisonMap, searchResult, criteria);
	}
	
	private void processLowestCostWithinNMonth(Map<String,CostComparisonBean> costComparisonMap, List<Object[]> list, CostExtractSearchCriterial criteria){
		List<CostExtractSearchCriterial> convertedList = convertResultToBeans(list);

		//Grouping
		Map<String, List<CostExtractSearchCriterial>> groupedMap = convertedList.stream()
				.collect(Collectors.groupingBy(CostExtractSearchCriterial::getKey));

		//Sort list for each element.
		for (Map.Entry<String, List<CostExtractSearchCriterial>> entry : groupedMap.entrySet()) {			
			Collections.sort(entry.getValue(), new CostAndDateComparator(true, false));
		}

		//Get the lowest within 12 month
		for (Map.Entry<String, List<CostExtractSearchCriterial>> entry : groupedMap.entrySet()) {

			for(CostExtractSearchCriterial b : entry.getValue()) {

				if(costComparisonMap.get(entry.getKey()) == null){
					costComparisonMap.put(entry.getKey(), new CostComparisonBean(b.getMfr(), b.getPartNumber()));
				}
				
				CostComparisonBean bean = costComparisonMap.get(entry.getKey());
				bean.setLowestCostPast12Mths(b.getCost());
				bean.setLowestCostQuoteEffectiveDatePast12Mths(b.getQuoteEffectiveDate());
				bean.setLowestCostRegionPast12Mths(b.getRegion());

				break;
			}
		}

		//Get the lowest within 6 month
		for (Map.Entry<String, List<CostExtractSearchCriterial>> entry : groupedMap.entrySet()) {

			List<CostExtractSearchCriterial> filterList = filterList(entry.getValue(), criteria.getQuoteEffectiveDate6MthFrom(), criteria.getQuoteEffectiveDate6MthTo());
			for(CostExtractSearchCriterial b : filterList) {

				if(costComparisonMap.get(entry.getKey()) == null){
					costComparisonMap.put(entry.getKey(), new CostComparisonBean(b.getMfr(), b.getPartNumber()));
				}
				
				CostComparisonBean bean = costComparisonMap.get(entry.getKey());
				bean.setLowestCostPast6Mths(b.getCost());
				bean.setLowestCostQuoteEffectiveDatePast6Mths(b.getQuoteEffectiveDate());
				bean.setLowestCostRegionPast6Mths(b.getRegion());

				break;
			}
		}

		//Get the lowest within 3 month
		for (Map.Entry<String, List<CostExtractSearchCriterial>> entry : groupedMap.entrySet()) {
			
			List<CostExtractSearchCriterial> filterList = filterList(entry.getValue(), criteria.getQuoteEffectiveDate3MthFrom(), criteria.getQuoteEffectiveDate3MthTo());
			for(CostExtractSearchCriterial b : filterList) {

				if(costComparisonMap.get(entry.getKey()) == null){
					costComparisonMap.put(entry.getKey(), new CostComparisonBean(b.getMfr(), b.getPartNumber()));
				}
				
				CostComparisonBean bean = costComparisonMap.get(entry.getKey());
				bean.setLowestCostPast3Mths(b.getCost());
				bean.setLowestCostQuoteEffectiveDatePast3Mths(b.getQuoteEffectiveDate());
				bean.setLowestCostRegionPast3Mths(b.getRegion());

				break;
			}
		}
	}
	
	private List<CostExtractSearchCriterial> filterList(List<CostExtractSearchCriterial> list, Date from, Date to){

		List<CostExtractSearchCriterial> result = 
				list.stream()                // convert list to stream
				.filter(item -> {
					if((item.getQuoteEffectiveDate().after(from) || item.getQuoteEffectiveDate().equals(from))
							&& (item.getQuoteEffectiveDate().before(to) || item.getQuoteEffectiveDate().equals(to))) {

						return true;
					}
					return false;
				}).collect(Collectors.toList());


		return result;
	}
	
	private void queryCriteriaLowestCost(CostExtractSearchCriterial criteria, Query query, boolean pricer, boolean quoteRecord){

		if (!QuoteUtil.isEmpty(criteria.getSaleCostPart()) && !criteria.getSaleCostPart().equalsIgnoreCase(QuoteSBConstant.OPTION_ALL)) {
			query.setParameter("salesCostFlag", criteria.getSaleCostPart().equals(QuoteSBConstant.OPTION_YES) ? true : false);
		}
		
		if(pricer){
			Date quotationEffectiveDateFrom = criteria.getQuoteEffectiveDateFrom();
			Date quotationEffectiveDateTo = criteria.getQuoteEffectiveDateTo();
			if (criteria.getQuoteEffectiveDateFrom() != null || criteria.getQuoteEffectiveDateTo() != null) {
				if (criteria.getQuoteEffectiveDateFrom() == null) {// dd/MM/yyyy
					quotationEffectiveDateFrom = convertToDate("01/01/1900");
				}

				if (criteria.getQuoteEffectiveDateTo() == null) {
					quotationEffectiveDateTo = getNextNYear(quotationEffectiveDateFrom, 1);
				}

				query.setParameter("quotationEffectiveFrom", quotationEffectiveDateFrom);
				query.setParameter("quotationEffectiveTo", quotationEffectiveDateTo);

			}
		}
		
		if (!QuoteUtil.isEmpty(criteria.getProductGroup1())) {
			query.setParameter("pg1", "%" + criteria.getProductGroup1() + "%");
		}
		if (!QuoteUtil.isEmpty(criteria.getProductGroup2())) {
			query.setParameter("pg2", "%" + criteria.getProductGroup2() + "%");
		}
		
		if (!QuoteUtil.isEmpty(criteria.getProductGroup3())) {
			query.setParameter("pg3", "%" + criteria.getProductGroup3() + "%");
		}
		
		if (!QuoteUtil.isEmpty(criteria.getProductGroup4())) {
			query.setParameter("pg4", "%" + criteria.getProductGroup4() + "%");
		}
		
		//For Quote Record - Start
		if (quoteRecord) {

			if (criteria.getQuoteEffectiveDateFrom() != null ) {
				query.setParameter("quoteExpiryDateFrom", criteria.getQuoteEffectiveDateFrom());
				
				if (criteria.getQuoteEffectiveDateTo() == null) {
					query.setParameter("quoteExpiryDateTo", getNextNYear(criteria.getQuoteEffectiveDateFrom(), 1));	
				}
			}

			if (criteria.getQuoteEffectiveDateTo() != null ) {
				query.setParameter("quoteExpiryDateTo", criteria.getQuoteEffectiveDateTo());
			}
		}
    	//For Quote Record - End
	}
	
	private void queryCriteriaForLowestCostWithNMonth(CostExtractSearchCriterial criteria, Query query, boolean pricer, boolean quoteRecord){

		if (!QuoteUtil.isEmpty(criteria.getSaleCostPart()) && !criteria.getSaleCostPart().equalsIgnoreCase(QuoteSBConstant.OPTION_ALL)) {
			query.setParameter("salesCostFlag", criteria.getSaleCostPart().equals(QuoteSBConstant.OPTION_YES) ? true : false);
		}
		
		if(pricer){
			Date quotationEffectiveDateFrom = criteria.getQuoteEffectiveDate12MthFrom();
			Date quotationEffectiveDateTo = criteria.getQuoteEffectiveDate12MthTo();
//			if (criteria.getQuoteEffectiveDateFrom() != null || criteria.getQuoteEffectiveDateTo() != null) {
//				if (criteria.getQuoteEffectiveDateFrom() == null) {// dd/MM/yyyy
//					quotationEffectiveDateFrom = convertToDate("01/01/1900");
//				}
//
//				if (criteria.getQuoteEffectiveDateTo() == null) {
//					quotationEffectiveDateTo = convertToDate("01/01/2100");
//				}
//
//				query.setParameter("quotationEffectiveFrom", quotationEffectiveDateFrom);
//				query.setParameter("quotationEffectiveTo", quotationEffectiveDateTo);
//
//			}
			if (quotationEffectiveDateFrom != null) {
				query.setParameter("quotationEffectiveFrom", quotationEffectiveDateFrom);
			}
			
			if (quotationEffectiveDateTo != null) {
				query.setParameter("quotationEffectiveTo", quotationEffectiveDateTo);
			}
		}
		
		if (!QuoteUtil.isEmpty(criteria.getProductGroup1())) {
			query.setParameter("pg1", "%" + criteria.getProductGroup1() + "%");
		}
		if (!QuoteUtil.isEmpty(criteria.getProductGroup2())) {
			query.setParameter("pg2", "%" + criteria.getProductGroup2() + "%");
		}
		
		if (!QuoteUtil.isEmpty(criteria.getProductGroup3())) {
			query.setParameter("pg3", "%" + criteria.getProductGroup3() + "%");
		}
		
		if (!QuoteUtil.isEmpty(criteria.getProductGroup4())) {
			query.setParameter("pg4", "%" + criteria.getProductGroup4() + "%");
		}
		
		//For Quote Record - Start
		if (quoteRecord) {

			if (criteria.getQuoteEffectiveDate12MthFrom() != null ) {
				query.setParameter("quoteExpiryDateFrom", criteria.getQuoteEffectiveDate12MthFrom());
				
			}

			if (criteria.getQuoteEffectiveDate12MthTo() != null ) {
				query.setParameter("quoteExpiryDateTo", criteria.getQuoteEffectiveDate12MthTo());
			}
		}
    	//For Quote Record - End
	}
	
	public String buildPricerQueryForCostComparisonOffline(CostExtractSearchCriterial criteria, User user, int first, int pageSize){
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT                                                                                 ");
		sql.append("    b.name AS region,                                                                  ");
		sql.append("    mr.sales_cost_flag AS sales_cost_flag,                                             ");
		sql.append("    mfr.name AS mfr_name,                                                              ");
		sql.append("    m.full_mfr_part_number AS part_no,                                                 ");
		sql.append("    p.cost AS cost,                                                                    ");
		sql.append("    p.pricer_type AS cost_source,                                                      ");
		sql.append("    ci.name AS cost_indicator,                                                         ");
		sql.append("    p.quotation_effective_date AS effective_date,                                      ");
		sql.append("    p.mpq AS mpq,                                                                      ");
		sql.append("    p.moq AS moq,                                                                      ");
		sql.append("    pg1.name AS product_group1,                                                        ");
		sql.append("    pg2.name AS product_group2,                                                        ");
		sql.append("    mr.product_group3 AS product_group3,                                               ");
		sql.append("    mr.product_group4 AS product_group4,                                               ");
		sql.append("    '' as customer_number,                                                             ");
		sql.append("    '' as customer_name,                                                               ");
		sql.append("    0 as quoted_qty,                                                                   ");
		sql.append("    mfr.id AS mfr_id, 																   ");
		sql.append("    mr.product_group2_id as product_group2_id, 										   ");
		sql.append("    p.material_category as material_type,                                              ");
		sql.append("    pt.name AS program_type_name,                                                      ");
		sql.append("    '' as team                                                                         ");
		sql.append("FROM                                                                                   ");
		sql.append("    pricer p                                                                           ");
		sql.append("    LEFT OUTER JOIN program_type pt ON ( pt.id = p.program_type_id ),                  ");
		sql.append("    biz_unit b,                                                                        ");
		sql.append("    manufacturer mfr,                                                                  ");
		sql.append("    material m,                                                                        ");
		sql.append("	material_regional mr                                                               ");
		sql.append("    LEFT OUTER JOIN product_group pg1 ON ( pg1.id = mr.product_group1_id )             ");
		sql.append("    LEFT OUTER JOIN product_group pg2 ON ( pg2.id = mr.product_group2_id ),            ");
		sql.append("    (SELECT mfr, part_no from cost_comparison_temp WHERE user_id='"+user.getEmployeeId()+"' AND row_no > " + first + " AND row_no <=" + (first+pageSize) + ") cct, ");
		sql.append("    cost_indicator ci                                                                  ");
		sql.append("WHERE                                                                                  ");
		sql.append("    p.material_id = m.id                                                               ");
		sql.append("	AND p.biz_unit = b.name                                                            ");
		sql.append("	AND p.cost_indicator = ci.name                                                     ");
		sql.append("    AND mr.material_id = m.id                                                          ");
		sql.append("    AND mfr.id = m.manufacturer_id                                                     ");
		sql.append("    and p.biz_unit = mr.biz_unit                                                       ");
		sql.append("    AND p.cost > 0                                                                     ");
		sql.append("    AND mfr.name = cct.mfr 														   ");
		sql.append("    AND m.full_mfr_part_number = cct.part_no 										   ");
    	
    	if (criteria.getSelectedRegions() != null && criteria.getSelectedRegions().size() > 0) {	
    		sql.append("    AND p.biz_unit IN " + buildInSql(new HashSet<String>(criteria.getSelectedRegions())));
    	}
    	
    	if (criteria.getSelectedRegions() != null && criteria.getSelectedRegions().size() > 0) {
    		sql.append("    AND mr.biz_unit IN " + buildInSql(new HashSet<String>(criteria.getSelectedRegions())));
    	}
    	
    	if (criteria.getSelectedCostSoures() != null && criteria.getSelectedCostSoures().size() > 0) {
    		sql.append("    AND p.pricer_type IN " + buildInSql(new HashSet<String>(criteria.getSelectedCostSoures())));
		}	
		
    	if (!QuoteUtil.isEmpty(criteria.getSaleCostPart())
				&& !criteria.getSaleCostPart().equalsIgnoreCase(QuoteSBConstant.OPTION_ALL)) {
    		sql.append("    AND mr.sales_cost_flag = #salesCostFlag                                                         ");
		}
    	
		if (criteria.getQuoteEffectiveDateFrom() != null || criteria.getQuoteEffectiveDateTo() != null) {
			sql.append("    AND (                                                                              ");
			sql.append("			(( p.quotation_effective_date >= #quotationEffectiveFrom) AND ( p.quotation_effective_date <= #quotationEffectiveTo))  ");
			sql.append("			OR                                                                         ");
			sql.append("			(( p.quotation_effective_to >= #quotationEffectiveFrom) AND ( p.quotation_effective_to <= #quotationEffectiveTo))      ");
			sql.append("            OR                                                                         ");
			sql.append("			(( p.quotation_effective_date >= #quotationEffectiveFrom) AND ( p.quotation_effective_to <= #quotationEffectiveTo))    ");
			sql.append("        )                                                                              ");
		}
		
		if (criteria.getSelectedCostIndicators() != null && criteria.getSelectedCostIndicators().size() > 0) {
			sql.append("    AND p.cost_indicator IN " + buildInSql(new HashSet<String>(criteria.getSelectedCostIndicators())));
		}
		
		if (!QuoteUtil.isEmpty(criteria.getProductGroup1())) {
			sql.append("    AND pg1.name LIKE #pg1                                                                ");
		}
		
		if (!QuoteUtil.isEmpty(criteria.getProductGroup2())) {
			sql.append("    AND pg2.name LIKE #pg2                                                                ");
		}
		
		if (!QuoteUtil.isEmpty(criteria.getProductGroup3())) {
			sql.append("    AND mr.product_group3 LIKE #pg3                                                       ");
		}
		
		if (!QuoteUtil.isEmpty(criteria.getProductGroup4())) {
			sql.append("    AND mr.product_group4 LIKE #pg4                                                       ");
		}
		
		return sql.toString();
	}
	
	public String buildQuoteQueryForCostComparisonOffline(CostExtractSearchCriterial criteria, boolean lowestCostNMothFlag, User user, int first, int pageSize) {

		StringBuffer sql = new StringBuffer();

		sql.append("SELECT																														");
		sql.append("    b.name  AS region,                                                                                                      ");
		sql.append("    qi.sales_cost_flag AS sales_cost_flag,                                                                                  ");
		sql.append("    mfr.name AS mfr_name,                                                                                                   ");
		sql.append("    qi.quoted_part_number AS part_no,                                                                                       ");
		sql.append("    qi.cost AS cost,                                                                                                        ");
		sql.append("    'Quote Record' AS cost_source,                                                                                          ");
		sql.append("    qi.cost_indicator AS cost_indicator,                                                                                    ");
		sql.append("    qi.quote_expiry_date AS effective_date,                                                                                 ");
		sql.append("    qi.mpq AS mpq,                                                                                                          ");
		sql.append("    qi.moq AS moq,                                                                                                          ");
		sql.append("    pg1.name AS product_group1,                                                                                             ");
		sql.append("    pg2.name AS product_group2,                                                                                             ");
		sql.append("    qi.product_group3 AS product_group3,                                                                                    ");
		sql.append("    qi.product_group4 AS product_group4,                                                                                    ");
		sql.append("    c.customer_number as customer_number,                                                                                   ");
		sql.append("    (c.customer_name1 || ' ' || c.customer_name2 || ' ' || c.customer_name3 || ' ' || c.customer_name4) as customer_name,  ");
		sql.append("    qi.quoted_qty as quoted_qty,                                                                                            ");
		sql.append("    mfr.id as mfr_id, ");
		sql.append("    qi.product_group2_id AS product_group2_id, ");
		sql.append("    qi.material_type_id AS material_type, "); 
		sql.append("    qi.program_type AS program_type, ");
		sql.append("    q.team_id AS team ");
		sql.append("  FROM                                                                                                                      ");
		sql.append("    quote_item qi                                                                                                         ");
		sql.append("    LEFT OUTER JOIN product_group pg1 ON ( pg1.id = qi.product_group1_id )                                                  ");
		sql.append("    LEFT OUTER JOIN product_group pg2 ON ( pg2.id = qi.product_group2_id ),                                                 ");
		sql.append("	quote q                                                                                                                ");
		sql.append("	LEFT OUTER JOIN team t ON ( t.name = q.team_id ), ");
		sql.append("	biz_unit b,                                                                                                             ");
		sql.append("	manufacturer mfr,                                                                                                       ");
		sql.append("    customer c,                                                                                                            ");
		sql.append("    (SELECT mfr, part_no from cost_comparison_temp WHERE user_id='"+user.getEmployeeId()+"' AND row_no > " + first + " AND row_no <=" + (first+pageSize) + ") cct ");
		sql.append("  WHERE                                                                                                                     ");
		sql.append("	q.id = qi.quote_id                                                                                                      ");
		sql.append("    AND mfr.id = qi.requested_mfr_id                                                                                        ");
		sql.append("    AND b.name = q.biz_unit                                                                                                 ");
		sql.append("    AND c.customer_number = qi.sold_to_customer_number                                                                      ");
		sql.append("	AND qi.stage = 'FINISH'                                                                                                 ");
		sql.append("	AND qi.cost > 0                                                                                                 ");
		sql.append("    AND mfr.name = cct.mfr 														   ");
		sql.append("    AND qi.quoted_part_number = cct.part_no 										   ");

		if (criteria.getSelectedRegions() != null && criteria.getSelectedRegions().size() > 0) {	
			sql.append("    AND q.biz_unit IN " + buildInSql(new HashSet<String>(criteria.getSelectedRegions())));
		}

		if (!QuoteUtil.isEmpty(criteria.getSaleCostPart())
				&& !criteria.getSaleCostPart().equalsIgnoreCase(QuoteSBConstant.OPTION_ALL)) {
			sql.append("    AND qi.sales_cost_flag = #salesCostFlag                                                                                               ");
		}

		if (criteria.getSelectedCostIndicators() != null && criteria.getSelectedCostIndicators().size() > 0) {	
			sql.append("    AND qi.cost_indicator IN " + buildInSql(new HashSet<String>(criteria.getSelectedCostIndicators())));
		}
		
		if(!lowestCostNMothFlag){
    		if (criteria.getQuoteEffectiveDateFrom() != null ) {
    			sql.append("    AND qi.quote_expiry_date >= #quoteExpiryDateFrom                                                                                            ");
    		}

    		if (criteria.getQuoteEffectiveDateTo() != null ) {
    			sql.append("    AND qi.quote_expiry_date <= #quoteExpiryDateTo                                                                                            ");
    		}
    	}else{
    		if (criteria.getQuoteEffectiveDate12MthFrom() != null ) {
    			sql.append("    AND qi.quote_expiry_date >= #quoteExpiryDateFrom                                                                                            ");
    		}

    		if (criteria.getQuoteEffectiveDate12MthTo() != null ) {
    			sql.append("    AND qi.quote_expiry_date <= #quoteExpiryDateTo                                                                                            ");
    		}
    	}

		if (!QuoteUtil.isEmpty(criteria.getProductGroup1())) {
			sql.append("    AND   pg1.name LIKE #pg1                                                                                                   ");
		}
		if (!QuoteUtil.isEmpty(criteria.getProductGroup2())) {
			sql.append("    AND   pg2.name LIKE #pg2                                                                                                   ");
		}

		if (!QuoteUtil.isEmpty(criteria.getProductGroup3())) {
			sql.append("    AND   qi.product_group3 LIKE #pg3                                                                                          ");
		}

		if (!QuoteUtil.isEmpty(criteria.getProductGroup4())) {
			sql.append("    AND   qi.product_group3 LIKE #pg3                                                                                          ");
		}

		return sql.toString();
	}

	public int prepareDataCostComparisonOffline(CostExtractSearchCriterial criteria, User user){
		StringBuffer sql = new StringBuffer();
		StringBuffer sql2 = new StringBuffer();
		boolean quoteRecord = false;
		boolean pricer = false;
		
		if (criteria.getSelectedCostSoures() != null && criteria.getSelectedCostSoures().size() > 0) {
			
			for(String cs : criteria.getSelectedCostSoures()){
				if(cs.contains("QuoteRecord")){
					quoteRecord = true;
				}else{
					pricer = true;
				}
			}
			
		}

		sql.append("INSERT INTO COST_COMPARISON_TEMP (ROW_NO, MFR, PART_NO, USER_ID) ");
		sql.append("	SELECT ROWNUM, mfr_name, part_no, '" + user.getEmployeeId() + "' FROM ( ");
		sql.append("		SELECT distinct mfr_name, part_no FROM ( ");

		
		if(pricer){
			sql2.append(buildPricerQuery(criteria, false));
		}
		if(pricer && quoteRecord){
			sql2.append(" union all ");
		}
		if (quoteRecord) {
			sql2.append(buildQuoteQuery(criteria, false, false));
		}
		
		//filter by DataAccess
		if(criteria.getDataAccesses() != null && criteria.getDataAccesses().size() > 0){
			sql.append(buildDataAccessQuery(criteria.getDataAccesses(), sql2.toString()));	
		}else{
			sql.append(sql2);
		}

		sql.append("		) 				");
		sql.append("		) 				");

    	
		Query query = em.createNativeQuery(sql.toString());
		
		queryCriteria(criteria, query, pricer, quoteRecord);

		int count = query.executeUpdate();

		LOG.info("inserted record to COST_COMPARISON_TEMP table: " + count);
		
		return count;
	}
	
	public int countCostComparisonTemp(User user){
		
		String sql = "SELECT count(*) FROM COST_COMPARISON_TEMP WHERE user_id=#userId";
		
		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("userId", user.getEmployeeId());
		
		return ((BigDecimal) query.getSingleResult()).intValue();
	}
	
	public int deleteCostComparisonTemp(User user) throws Exception{
		String sql = "DELETE FROM COST_COMPARISON_TEMP WHERE user_id=#userId";
		
		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("userId", user.getEmployeeId());
		
		return query.executeUpdate();
	}
	
	private Date getNextNYear(Date date, int year){
		Calendar nextYearCal = Calendar.getInstance();
		nextYearCal.setTime(date);
		nextYearCal.add(Calendar.YEAR, year);
		nextYearCal.set(Calendar.HOUR_OF_DAY, 23);
		nextYearCal.set(Calendar.SECOND, 59);
		nextYearCal.set(Calendar.MINUTE, 59);
		
		return nextYearCal.getTime();
	}
}