/**
 * 
 */
package com.avnet.emasia.webquote.reports.ejb;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.ejb3.annotation.TransactionTimeout;

import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.ProductGroup;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.SalesOrder;
import com.avnet.emasia.webquote.entity.Team;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.quote.vo.ReportSearchCriteria;
import com.avnet.emasia.webquote.reports.vo.OrderOnHandReportVo;
import com.avnet.emasia.webquote.reports.vo.QuoteHitRateReportVo;
import com.avnet.emasia.webquote.utilities.common.Constants;
import com.avnet.emasia.webquote.utilities.util.QuoteUtil;



/**
 * @author 916138
 *
 */
@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
public class ReportSB {
	
	private static final Logger LOGGER = Logger.getLogger(ReportSB.class.getName());
	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	
	@Resource
	public UserTransaction ut;
	
	private static final Logger LOG = Logger
			.getLogger(ReportSB.class.getName());
	
	public final static String ALIAS_QUOTE_PERIOD = "quotedPeriod";
	public final static String ALIAS_SALES_MGR = "salesMgr";
	public final static String ALIAS_SALES_TEAM = "salesTeam";
	public final static String ALIAS_SALES_USER = "salesUser";
	public final static String ALIAS_CUSTOMER = "customer";
	public final static String ALIAS_CUSTOMER_NAME = "customerName";
	public final static String ALIAS_MFR = "mfr";
	public final static String ALIAS_PRODUCT_GRP_1 = "productGrp1";
	public final static String ALIAS_PRODUCT_GRP_2 = "productGrp2";
	public final static String ALIAS_PRODUCT_GRP_3 = "productGrp3";
	public final static String ALIAS_PRODUCT_GRP_4 = "productGrp4";
	public final static String ALIAS_QUOTE_HANDLER = "quoteHandler";
	public final static String ALIAS_BU = "bizUnit";
	public final static String ALIAS_PN = "quotedPartNumber";
	
	
	public final static String QUOTE_TYPE_YEAR = "Fiscal Year";
	public final static String QUOTE_TYPE_QTR = "Fiscal Qtr";
	public final static String QUOTE_TYPE_MONTH = "Fiscal Month";
	
	/****for Order On Hand Report***/
	
	public final static String ALIAS_NON_OOH_TP = "nonOohTP";
	public final static String ALIAS_OOH_NO_TP = "oohNoTP";
	public final static String ALIAS_OOH_WITH_TP = "oohWithTP";
	public final static String ALIAS_OOH_AMT = "oohAmt";
	public final static String ALIAS_TOTAL_AMT = "totalAmt";
	
	
	/****for Quote Hit Rate Report***/
	//for search type 0
	public final static String ALIAS_QUOTED_CNT = "quotedCnt";
	public final static String ALIAS_HIT_ORDER_CNT = "hitOrderCnt";
	public final static String ALIAS_MET_TP_CNT = "metTpItemCnt";
	public final static String ALIAS_MET_TP_HIT_CNT = "metTpHitItemCnt";
	
	//for search type 1
	public final static String ALIAS_QUOTED_AMT = "quotedAmt";
	public final static String ALIAS_HIT_ORDER_AMT = "hitOrderAmt";
	
	public final static String ALIAS_MET_TP_HIT_AMT = "metTpHitItemAmt";
	
	
	
	private final static long FALSE_VALUE =0;
	DecimalFormat df = new DecimalFormat("#0.00###");
	
	private final static BigDecimal MULTIPLIER = new BigDecimal("100");
	
	/**
	 * 
	 * @author 916138
	 * @param criteria
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@TransactionTimeout(value = 100000000, unit = TimeUnit.SECONDS) 
	public List<QuoteHitRateReportVo> findQuoteHitRateItem(
			ReportSearchCriteria criteria) {
			
		String sql = constructQHRSQL(criteria,false);
		
		Query query = em.createNativeQuery(sql);
		query.setHint("eclipselink.result-type", "Map");
		//query.setHint("eclipselink.result-collection-type", java.util.Map<?, ?>.class);
		this.setParameterForQHR(query,criteria);
		
		List<Map>  results = query.getResultList();
		
		List<QuoteHitRateReportVo> resultLst = convertToQHRVO(results,criteria);
		
		return resultLst;
		
	}
	
	/**
	 * find the QHR report grand total 
	 * @author 916138
	 * @param criteria
	 * @return
	 */
	public QuoteHitRateReportVo findQuoteHitRateGrandTotal(
			ReportSearchCriteria criteria) {
		String sql = constructQHRSQL(criteria,true);
		
		Query query = em.createNativeQuery(sql);
		query.setHint("eclipselink.result-type", "Map");
		this.setParameterForQHR(query,criteria);
		Map<?, ?> obj = (Map<?, ?>) query.getSingleResult();
		return this.convertToQHRVO(obj, criteria);
		
	}
	
	private void setResultToVo(Map<?, ?> obj, QuoteHitRateReportVo vo) {
		try{
			Class<?> clazz = vo.getClass() ;  
		    Class<?> clazzSupper = clazz.getSuperclass();
		    Field[] fieldsSub = clazz.getDeclaredFields();
		    Field[] fieldsSupper =clazzSupper.getDeclaredFields();
		    
		    Field[] fields = new Field[fieldsSub.length + fieldsSupper.length];
		    System.arraycopy(fieldsSub, 0, fields, 0, fieldsSub.length);
		    System.arraycopy(fieldsSupper, 0, fields, fieldsSub.length, fieldsSupper.length);
		    
		    for(int i=0;i<fields.length;i++) {
		    	Field field =fields[i];
		    	String fieldName = field.getName();
				field.setAccessible(true);
				Class<?> fieldType = field.getType();
				if(obj.containsKey(fieldName.toUpperCase())) {
					 if (fieldType.equals(String.class))
					 {
						 field.set(vo, (String)obj.get(fieldName.toUpperCase()));
						 
					 }else if(fieldType.equals(long.class)||fieldType.equals(Long.class)){
						 BigDecimal value = (BigDecimal)obj.get(fieldName.toUpperCase());
						 field.set(vo, null!=value?value.longValue():0);
					 }else if(fieldType.equals(double.class) || fieldType.equals(Double.class)) {
						 BigDecimal value = (BigDecimal)obj.get(fieldName.toUpperCase());
						 field.set(vo, value==null?0:value.doubleValue());
					 }else {
						 field.set(vo, fieldType.cast(obj.get(fieldName.toUpperCase())));
					 }
					 
				}
				
		    }
		} catch (IllegalArgumentException | IllegalAccessException e) {
	
			LOG.log(Level.SEVERE,"Exception occuring during setting result to Vo object : "+ e.getMessage(), e);
		}
			
	}
	
	private void setParameterForQHR(Query query, ReportSearchCriteria criteria) {
		query.setParameter(1, criteria.getBizUnit());
		query.setParameter(2, criteria.getQuoteFrm());
		query.setParameter(3, criteria.getQuoteTo());
		query.setParameter(4, criteria.getQuoteFrm());
		query.setParameter(5, criteria.getQuoteTo());
		query.setParameter(6, criteria.getQuoteFrm());
		query.setParameter(7, criteria.getQuoteTo());
		//sales manager
		if(!QuoteUtil.isEmpty(criteria.getSalesMgr()) && !QuoteSBConstant.WILDCARD_ALL.equals(criteria.getSalesMgr()))
			query.setParameter(8, criteria.getSalesMgr());
		
		//sales team
		if(!QuoteUtil.isEmpty(criteria.getSalesTeam()) && !QuoteSBConstant.WILDCARD_ALL.equals(criteria.getSalesTeam()))
			query.setParameter(9, criteria.getSalesTeam());
		//salesUser
		if(!QuoteUtil.isEmpty(criteria.getSalesUser()) && !QuoteSBConstant.WILDCARD_ALL.equals(criteria.getSalesUser()))
			query.setParameter(10, criteria.getSalesUser());
		
		//customer search filter
		if(!QuoteUtil.isEmpty(criteria.getCustomerName()))
			query.setParameter(11, "%"+criteria.getCustomerName().toUpperCase()+"%");
		if(!QuoteUtil.isEmpty(criteria.getCustomerNum()))
			query.setParameter(12, criteria.getCustomerNum());

		//Manufactor
		if(!QuoteUtil.isEmpty(criteria.getMfr())&& !QuoteSBConstant.WILDCARD_ALL.equals(criteria.getMfr()))
			query.setParameter(13, criteria.getMfr());
		
		//product group information
		if(!QuoteUtil.isEmpty(criteria.getProductGrp1()))
			query.setParameter(14, "%"+criteria.getProductGrp1().toUpperCase()+"%");
		
		if(!QuoteUtil.isEmpty(criteria.getProductGrp2()))
			query.setParameter(15, "%"+criteria.getProductGrp2().toUpperCase()+"%");

		if(!QuoteUtil.isEmpty(criteria.getProductGrp3()))
			query.setParameter(16, "%"+criteria.getProductGrp3().toUpperCase()+"%");

		if(!QuoteUtil.isEmpty(criteria.getProductGrp4()))
			query.setParameter(17, "%"+criteria.getProductGrp4().toUpperCase()+"%");

		
		//quoteHandler
		if(!QuoteUtil.isEmpty(criteria.getQuoteHandler())&& !QuoteSBConstant.WILDCARD_ALL.equals(criteria.getQuoteHandler()))
			query.setParameter(18, criteria.getQuoteHandler());

		//bu
		if(!QuoteUtil.isEmpty(criteria.getBizUnit())&& !QuoteSBConstant.WILDCARD_ALL.equals(criteria.getBizUnit()))
			query.setParameter(19, criteria.getBizUnit());
		//pn
		if(!QuoteUtil.isEmpty(criteria.getQuotedPartNumber()))
			query.setParameter(20, "%"+criteria.getQuotedPartNumber().toUpperCase()+"%");
	
		
	}

	public String constructQHRSQL(ReportSearchCriteria criteria,boolean isTotal){
		StringBuffer sqlSelect = new StringBuffer();
		StringBuffer sqlGroupBy = new StringBuffer();
		StringBuffer sqlFrom = new StringBuffer();
		
		StringBuffer sqlOrderBy = new StringBuffer();
		
		sqlFrom.append(" FROM QUOTE_ITEM t0 ");
		String sqlSalesOrder = " ( "
				+ "SELECT t11.quote_number, "
				+ "sum(t11.SALES_ORDER_QTY * t11.SALES_ORDER_RESALE) as hitOrderAmt, "
				+ "sum(CASE WHEN (t12.QUOTED_PRICE <= t12.TARGET_RESALE) THEN t11.SALES_ORDER_QTY * t11.SALES_ORDER_RESALE ELSE 0 END) as metTpHitItemAmt, "
				+ "sum(CASE WHEN (t12.QUOTED_PRICE <= t12.TARGET_RESALE ) THEN 1 ELSE 0 END) as metTpHitItem "
				+ "FROM SALES_ORDER  t11,quote_item t12,quote t13 "
				+ "WHERE t11.quote_number = t12.quote_number "
				+ "AND t13.id = t12.quote_id "
				+ "AND t13.BIZ_UNIT = ?1 "
				+ "AND t12.stage = 'FINISH' "
				+ "AND t12.SENT_OUT_TIME >=  ?2 "
				+ "AND t12.SENT_OUT_TIME <?3 "
				+ "AND t11.LAST_UPDATED_ON >=  ?4 "  // change create on to use last update on 
				+ "AND t11.LAST_UPDATED_ON <  ?5 "
				+ "GROUP BY t11.quote_number "
				+  ") ";
		
		sqlFrom.append("LEFT JOIN "+sqlSalesOrder+" t1 ON  t0.QUOTE_NUMBER  = t1.quote_number ");
		sqlFrom.append("LEFT OUTER JOIN QUOTE t4 ON (t4.ID = t0.QUOTE_ID) ");
		sqlFrom.append("LEFT OUTER JOIN BIZ_UNIT t10 ON (t10.NAME = t4.BIZ_UNIT) ");
		sqlFrom.append("LEFT OUTER JOIN TEAM t3 ON (t3.NAME = t4.TEAM_ID)  ");
		sqlFrom.append("LEFT OUTER JOIN APP_USER t5 ON (t5.ID = t4.SALES_USER_ID) ");
		sqlFrom.append("LEFT OUTER JOIN APP_USER t2 ON (t2.ID = t5.report_to) "); // fix defect 316 20141224
		sqlFrom.append("LEFT OUTER JOIN CUSTOMER t6 ON (t6.CUSTOMER_NUMBER = t4.SOLD_TO_CUSTOMER_NUMBER) ");
		sqlFrom.append("LEFT OUTER JOIN MANUFACTURER t7 ON (t7.ID = t0.REQUESTED_MFR_ID) ");
		sqlFrom.append("LEFT OUTER JOIN PRODUCT_GROUP t8 ON (t8.ID = t0.PRODUCT_GROUP1_ID) ");
		sqlFrom.append("LEFT OUTER JOIN PRODUCT_GROUP t9 ON (t9.ID = t0.PRODUCT_GROUP2_ID) " );
		//sqlFrom.append("LEFT OUTER JOIN APP_USER t12 ON (t0.LAST_UPDATED_QC = t12.id) ");
		
		
		
		if(!isTotal) {
			sqlSelect.append("SELECT distinct ");
		
			sqlGroupBy.append(" GROUP BY ");
			sqlOrderBy.append(" ORDER BY ");
			//group by quote last update date
			if(criteria.isShowQuotePeriod()) {
				if (QUOTE_TYPE_YEAR.equals(criteria.getSearchQuoteType())) {
					sqlSelect.append(" t0.FIS_YEAR as " + ALIAS_QUOTE_PERIOD );
					sqlGroupBy.append("t0.FIS_YEAR ");
					sqlOrderBy.append("t0.FIS_YEAR ");
				} else if (QUOTE_TYPE_QTR.equals(criteria.getSearchQuoteType())) {
					sqlSelect.append(" t0.FIS_QTR as " + ALIAS_QUOTE_PERIOD );
					sqlGroupBy.append("t0.FIS_QTR ");
					sqlOrderBy.append("t0.FIS_QTR ");
				} else {
					sqlSelect.append(" t0.FIS_MTH as " + ALIAS_QUOTE_PERIOD );
					sqlGroupBy.append("t0.FIS_MTH ");
					//sqlOrderBy.append("to_date(SUBSTR(t0.FIS_MTH,3,length(t0.FIS_MTH)),'YY MM') ");
					sqlOrderBy.append("(SUBSTR(t0.FIS_MTH,1,4) || ");
					sqlOrderBy.append("(case when (SUBSTR(t0.FIS_MTH,6,LENGTH(t0.FIS_MTH)) ='AUG') then 'M02' ");
					sqlOrderBy.append(" when (SUBSTR(t0.FIS_MTH,6,LENGTH(t0.FIS_MTH)) ='SEP') then 'M03'");
					sqlOrderBy.append(" when (SUBSTR(t0.FIS_MTH,6,LENGTH(t0.FIS_MTH)) ='OCT') then 'M04'");
					sqlOrderBy.append(" when (SUBSTR(t0.FIS_MTH,6,LENGTH(t0.FIS_MTH)) ='NOV') then 'M05'");
					sqlOrderBy.append(" when (SUBSTR(t0.FIS_MTH,6,LENGTH(t0.FIS_MTH)) ='DEC') then 'M06'");
					sqlOrderBy.append(" when (SUBSTR(t0.FIS_MTH,6,LENGTH(t0.FIS_MTH)) ='JAN') then 'M07'");
					sqlOrderBy.append(" when (SUBSTR(t0.FIS_MTH,6,LENGTH(t0.FIS_MTH)) ='FEB') then 'M08'");
					sqlOrderBy.append(" when (SUBSTR(t0.FIS_MTH,6,LENGTH(t0.FIS_MTH)) ='MAR') then 'M09'");
					sqlOrderBy.append(" when (SUBSTR(t0.FIS_MTH,6,LENGTH(t0.FIS_MTH)) ='APR') then 'M10'");
					sqlOrderBy.append(" when (SUBSTR(t0.FIS_MTH,6,LENGTH(t0.FIS_MTH)) ='MAY') then 'M11'");
					sqlOrderBy.append(" when (SUBSTR(t0.FIS_MTH,6,LENGTH(t0.FIS_MTH)) ='JUN') then 'M12'");
					sqlOrderBy.append(" when (SUBSTR(t0.FIS_MTH,6,LENGTH(t0.FIS_MTH)) ='JUL') then 'M01' end))");
					sqlOrderBy.append("asc");
					
				}
			}
	
			//group by sales manager
			if(criteria.isShowSalesMgr()) {
				sqlSelect.append(",t2.NAME as " + ALIAS_SALES_MGR );
				sqlGroupBy.append(",t2.NAME ");
				sqlOrderBy.append(",t2.NAME ");
			}
			
			//group by sales team
			if(criteria.isShowSalesTeam()){
				sqlSelect.append(",t3.NAME as " + ALIAS_SALES_TEAM );
				sqlGroupBy.append(",t3.NAME ");
				sqlOrderBy.append(",t3.NAME ");
			}
			
			//group by sales User
			if(criteria.isShowSalesUser()){
				sqlSelect.append(",t5.NAME as " + ALIAS_SALES_USER );
				sqlGroupBy.append(",t5.NAME ");
				sqlOrderBy.append(",t5.NAME ");
			}
			
			//group by customer
			if(criteria.isShowCustomer() ) { 
				// sold to code
				sqlSelect.append(",t6.CUSTOMER_NUMBER as " + ALIAS_CUSTOMER );
				sqlGroupBy.append(",t6.CUSTOMER_NUMBER ");
				sqlOrderBy.append(",t6.CUSTOMER_NUMBER ");
			 // sold to party
				sqlSelect.append(",t6.CUSTOMER_NAME1 as " + ALIAS_CUSTOMER_NAME );
				sqlGroupBy.append(",t6.CUSTOMER_NAME1 ");
				sqlOrderBy.append(",t6.CUSTOMER_NAME1  ");
			}
			
			//group by manufacturer
			if(criteria.isShowmfr()) {
				sqlSelect.append(",t7.NAME as " + ALIAS_MFR );
				sqlGroupBy.append(",t7.NAME ");
				sqlOrderBy.append(",t7.NAME  ");
			}
			
			//group by product group  information 
			if(criteria.isShowProductGrp1()) {
				sqlSelect.append(",t8.NAME as " + ALIAS_PRODUCT_GRP_1 );
				sqlGroupBy.append(",t8.NAME ");
				sqlOrderBy.append(",t8.NAME  ");
			}
			if(criteria.isShowProductGrp2()) {
				sqlSelect.append(",t9.NAME as " + ALIAS_PRODUCT_GRP_2 );
				sqlGroupBy.append(",t9.NAME ");
				sqlOrderBy.append(",t9.NAME  ");
			}
			if(criteria.isShowProductGrp3()) {
				sqlSelect.append(",t0.PRODUCT_GROUP3 as " + ALIAS_PRODUCT_GRP_3 );
				sqlGroupBy.append(",t0.PRODUCT_GROUP3 ");
				sqlOrderBy.append(",t0.PRODUCT_GROUP3  ");
			}
			if(criteria.isShowProductGrp4()) {
				sqlSelect.append(",t0.PRODUCT_GROUP4 as " + ALIAS_PRODUCT_GRP_4 );
				sqlGroupBy.append(",t0.PRODUCT_GROUP4 ");
				sqlOrderBy.append(",t0.PRODUCT_GROUP4  ");
			}
			//group by Quote Handler
			if(criteria.isShowQuoteHandler()) {
				/*sqlSelect.append(",t0.LAST_UPDATED_QC as " + Alias_QUOTE_HANDLER );
				sqlGroupBy.append(",t0.LAST_UPDATED_QC ");*/
				sqlSelect.append(",NVL(t0.LAST_UPDATED_QC_NAME,'AQ') AS " + ALIAS_QUOTE_HANDLER );
				sqlGroupBy.append(",NVL(t0.LAST_UPDATED_QC_NAME,'AQ')  ");
				sqlOrderBy.append(",NVL(t0.LAST_UPDATED_QC_NAME,'AQ')   ");
			}
			
			//group by bu region
			if(criteria.isShowBizUnit()) {
				sqlSelect.append(",t10.NAME as " + ALIAS_BU );
				sqlGroupBy.append(",t10.NAME ");
				sqlOrderBy.append(",t10.NAME  ");
			}
			//group by part numaber
			if(criteria.isShowPN()) {
				sqlSelect.append(",t0.QUOTED_PART_NUMBER as " + ALIAS_PN );
				sqlGroupBy.append(",t0.QUOTED_PART_NUMBER ");
				sqlOrderBy.append(",t0.QUOTED_PART_NUMBER  ");
			}
			
			LOG.info("NOT TOTAL SEARCCH TYPE IS ===>>>"+criteria.getSearchResultType());
			if("0".equals(criteria.getSearchResultType())) { 
				sqlSelect.append(" ,count(distinct t0.QUOTE_NUMBER ) AS " + ALIAS_QUOTED_CNT );
				sqlSelect.append(" ,count(distinct t1.QUOTE_NUMBER ) AS " + ALIAS_HIT_ORDER_CNT );
				sqlSelect.append(" ,SUM(CASE WHEN (t0.QUOTED_PRICE <= t0.TARGET_RESALE) THEN 1 ELSE 0 END )" + ALIAS_MET_TP_CNT ) ;
				sqlSelect.append(" ,SUM(t1.metTpHitItem )" + ALIAS_MET_TP_HIT_CNT ) ;
			}else { // search the amount 
				//Quoted Amount column �C cumulative (quote price * quote qty) based on the selected quotes
				sqlSelect.append(" ,sum(t0.QUOTED_PRICE * t0.QUOTED_QTY ) AS " + ALIAS_QUOTED_AMT );
				//Hit Amount column �C cumulative (SO resales * SO Order Qty)
				sqlSelect.append(" ,sum(t1.hitOrderAmt) AS " + ALIAS_HIT_ORDER_AMT );
				
				//Met TP Hit Amount====In Sales order, cumulate SO Resales which is less or equal to target resale.,
				sqlSelect.append(" ,sum(t1.metTpHitItemAmt) AS " + ALIAS_MET_TP_HIT_AMT );
			}
		}else {
			
			LOG.info(" TOTAL SEARCCH TYPE IS ===>>>"+criteria.getSearchResultType());
			if("0".equals(criteria.getSearchResultType())) { 
				sqlSelect.append(" SELECT count(distinct t0.QUOTE_NUMBER ) AS " + ALIAS_QUOTED_CNT );
				sqlSelect.append(" ,count(distinct t1.QUOTE_NUMBER ) AS " + ALIAS_HIT_ORDER_CNT );
				sqlSelect.append(" ,SUM(CASE WHEN (t0.QUOTED_PRICE <= t0.TARGET_RESALE) THEN 1 ELSE 0 END )" + ALIAS_MET_TP_CNT ) ;
				sqlSelect.append(" ,SUM(t1.metTpHitItem )" + ALIAS_MET_TP_HIT_CNT ) ;
			}else { // search the amount 
				//Quoted Amount column �C cumulative (quote price * quote qty) based on the selected quotes
				sqlSelect.append(" SELECT sum(t0.QUOTED_PRICE * t0.QUOTED_QTY ) AS " + ALIAS_QUOTED_AMT );
				
				//Hit Amount column �C cumulative (SO resales * SO Order Qty)
				sqlSelect.append(" ,SUM(t1.hitOrderAmt ) AS " + ALIAS_HIT_ORDER_AMT );
				
				//Met TP Hit Amount====In Sales order, cumulate SO Resales which is less or equal to target resale.,
				sqlSelect.append(" ,sum(t1.metTpHitItemAmt) AS " + ALIAS_MET_TP_HIT_AMT );
				
			}
			
		}
		
		StringBuffer sqlWhere = this.constructQHRWhere(criteria);
		String sql = sqlSelect.toString() + sqlFrom.toString() + sqlWhere.toString() + sqlGroupBy.toString();
		if(!isTotal) 
			sql = sql + sqlOrderBy.toString();
		
		return sql;
		
	
	}
	
	public StringBuffer constructQHRWhere(ReportSearchCriteria criteria) {
		StringBuffer sqlWhere = new StringBuffer();
		sqlWhere.append( " WHERE t0.STAGE = 'FINISH' " ) ;
		
		// quote period
		sqlWhere.append( " AND t0.SENT_OUT_TIME >=  ?6 " ) ;
		sqlWhere.append( " AND t0.SENT_OUT_TIME <  ?7 " ) ;
		

		//salesmanager
		if(!QuoteUtil.isEmpty(criteria.getSalesMgr()) && !QuoteSBConstant.WILDCARD_ALL.equals(criteria.getSalesMgr()))
			sqlWhere.append( " AND t2.NAME =  ?8 " ) ;
		
		//salesteam
		if(!QuoteUtil.isEmpty(criteria.getSalesTeam()) && !QuoteSBConstant.WILDCARD_ALL.equals(criteria.getSalesTeam()))
			sqlWhere.append( " AND t3.NAME =  ?9 " ) ;
		//salesUser
		if(!QuoteUtil.isEmpty(criteria.getSalesUser()) && !QuoteSBConstant.WILDCARD_ALL.equals(criteria.getSalesUser()))
			sqlWhere.append( " AND t5.NAME =  ?10 " ) ;
		
		//customer search filter
		if(!QuoteUtil.isEmpty(criteria.getCustomerName()))
			sqlWhere.append( " AND UPPER(t6.CUSTOMER_NAME1) like  ?11 " ) ;
		if(!QuoteUtil.isEmpty(criteria.getCustomerNum()))
			sqlWhere.append( " AND t6.CUSTOMER_NUMBER =  ?12 " ) ;
		//mfr
		if(!QuoteUtil.isEmpty(criteria.getMfr())&& !QuoteSBConstant.WILDCARD_ALL.equals(criteria.getMfr()))
			sqlWhere.append( " AND t7.NAME =  ?13 " ) ;
		
		//product group information
		if(!QuoteUtil.isEmpty(criteria.getProductGrp1()))
			sqlWhere.append( " AND UPPER(t8.NAME) like  ?14 " ) ;
		if(!QuoteUtil.isEmpty(criteria.getProductGrp2()))
			sqlWhere.append( " AND UPPER(t9.NAME) like  ?15 " ) ;
		if(!QuoteUtil.isEmpty(criteria.getProductGrp3()))
			sqlWhere.append( " AND UPPER(t0.PRODUCT_GROUP3) like  ?16 " ) ;
		if(!QuoteUtil.isEmpty(criteria.getProductGrp4()))
			sqlWhere.append( " AND UPPER(t0.PRODUCT_GROUP4) like  ?17 " ) ;
		
		//quoteHandler
		if(!QuoteUtil.isEmpty(criteria.getQuoteHandler())&& !QuoteSBConstant.WILDCARD_ALL.equals(criteria.getQuoteHandler()))
			sqlWhere.append( " AND t0.LAST_UPDATED_QC =  ?18 " ) ;
		//bu
		if(!QuoteUtil.isEmpty(criteria.getBizUnit())&& !QuoteSBConstant.WILDCARD_ALL.equals(criteria.getBizUnit()))
			sqlWhere.append( " AND t10.NAME =  ?19 " ) ;
		//pn
		if(!QuoteUtil.isEmpty(criteria.getQuotedPartNumber()))
			sqlWhere.append( " AND UPPER(t0.QUOTED_PART_NUMBER) like ?20 " ) ;
		
		
		return sqlWhere;
	}
	
	
	
	



	@SuppressWarnings("unchecked")
	public List<OrderOnHandReportVo> findOrderOnHandItem(ReportSearchCriteria criteria) {
		
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		
		
		//Root<QuoteItem> quoteItem = cq.from(QuoteItem.class);
		Root<QuoteItem> quoteItem = cq.from(QuoteItem.class);
		
		//Join<SalesOrder,QuoteItem> quoteItem = salesOrder.join("quoteItem", JoinType.INNER);
		Join<QuoteItem,Quote> quote = quoteItem.join("quote", JoinType.LEFT);
		Join<QuoteItem, ProductGroup> productGroup1 = quoteItem.join("productGroup1", JoinType.LEFT);
		Join<QuoteItem, ProductGroup> productGroup2 = quoteItem.join("productGroup2", JoinType.LEFT);
		Join<Quote, Team> team = quote.join("team", JoinType.LEFT);
		Join<Quote, User> saleUser = quote.join("sales", JoinType.LEFT);
		Join<User, User> salesMgr = saleUser.join("reportTo", JoinType.LEFT);
		Join<Quote, BizUnit> bizUnit = quote.join("bizUnit", JoinType.LEFT);
		Join<QuoteItem, Manufacturer> manufacturer = quoteItem.join("requestedMfr", JoinType.LEFT);
		Join<Quote, Customer> customer = quote.join("soldToCustomer", JoinType.LEFT);
		
		//group by list
		List<Expression<?>> grpColumns = new ArrayList<Expression<?>>();
		
		//group by list
		List<Order> orderBy = new ArrayList<Order>();
				
		//query columns 
		List<Selection<?>> selectionList = new ArrayList<Selection<?>>();
		getQuerySelectionAndGroupForOOH(cb,grpColumns, selectionList, criteria, quoteItem, quote, productGroup1, productGroup2, team, salesMgr, saleUser, bizUnit, manufacturer, customer,orderBy);
		
		//filter where condition
		List<Predicate> predicates = new ArrayList<Predicate>();
		getPredicatesForOOH(predicates,criteria,cb,quoteItem,quote,productGroup1,productGroup2,team,salesMgr,saleUser,bizUnit,manufacturer,customer);
		
		//add the calculateColumn to the search query result by search type is count or sum amount
		addCaculateColumnForOOH(cb, selectionList, quoteItem, criteria);
		
		//Selection<?> selectionLst;
		cq.multiselect(selectionList);
		//cq.select(cb.construct(QuoteHitRateReportVo.class, selection,));
		cq.groupBy(grpColumns);
		//where
		cq.where(predicates.toArray(new Predicate[]{}));
		
		cq.orderBy(orderBy);
		
		Query query = em.createQuery(cq);
		query.setHint("eclipselink.jdbc.bind-parameters", false);
		List<Tuple> objs = query.getResultList();
		
		
		return convertToOOHVO(objs);
	}
	
	
	@SuppressWarnings("unchecked")
	public OrderOnHandReportVo findOrderOnHandGrandTotal(
			ReportSearchCriteria criteria) {
		
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		
		
		Root<QuoteItem> quoteItem = cq.from(QuoteItem.class);
	
		Join<QuoteItem,Quote> quote = quoteItem.join("quote", JoinType.LEFT);
		Join<QuoteItem, ProductGroup> productGroup1 = quoteItem.join("productGroup1", JoinType.LEFT);
		Join<QuoteItem, ProductGroup> productGroup2 = quoteItem.join("productGroup2", JoinType.LEFT);
		Join<Quote, Team> team = quote.join("team", JoinType.LEFT);
		Join<Quote, User> saleUser = quote.join("sales", JoinType.LEFT);
		Join<User, User> salesMgr = saleUser.join("reportTo", JoinType.LEFT); //fix defect 316 20141224
		Join<Quote, BizUnit> bizUnit = quote.join("bizUnit", JoinType.LEFT);
		Join<QuoteItem, Manufacturer> manufacturer = quoteItem.join("requestedMfr", JoinType.LEFT);
		Join<Quote, Customer> customer = quote.join("soldToCustomer", JoinType.LEFT);
		
		
		//query columns 
		List<Selection<?>> selectionList = new ArrayList<Selection<?>>();
		
		//filter where condition
		List<Predicate> predicates = new ArrayList<Predicate>();
		getPredicatesForOOH(predicates,criteria,cb,quoteItem,quote,productGroup1,productGroup2,team,salesMgr,saleUser,bizUnit,manufacturer,customer);
		
		//add the calculateColumn to the search query result by search type is count or sum amount
		addCaculateColumnForOOH(cb,selectionList,quoteItem,criteria);
		
		//Selection<?> selectionLst;
		cq.multiselect(selectionList);

		//where
		cq.where(predicates.toArray(new Predicate[]{}));
		
		Query query = em.createQuery(cq);
		query.setHint("eclipselink.jdbc.bind-parameters", false);
		List<Tuple> objs = query.getResultList();

		
		return convertToOOHVO(objs).get(0);
	}

	private void addCaculateColumnForOOH(
			CriteriaBuilder cb, List<Selection<?>> selectionList, 
			Root<QuoteItem> quoteItem,
			ReportSearchCriteria criteria) {
		
		 String oohIdicator = "Order On Hand";
		
		 Expression<Number>  amt = cb.prod(quoteItem.<Number>get("quotedPrice") , quoteItem.<Number>get("quotedQty"));
		
		 Expression<Boolean> tpExp = cb.lessThanOrEqualTo(quoteItem.<Float>get("quotedPrice"), quoteItem.<Float>get("targetResale"));
		 Expression<Boolean> oohExp = cb.equal(quoteItem.<String>get("quoteType"), oohIdicator);
		 
		 Expression<Boolean> noTpExp = cb.isNull(quoteItem.<Number>get("targetResale"));
		 Expression<Boolean> noOOHExp = cb.notEqual(quoteItem.<String>get("quoteType"), oohIdicator);
		 
		 
		// Non OOH with TP Met	Total Amount of FINISH Quotes in which the quotes are without OOH and the Quoted �� the Target Price.
		Expression<Number> nonOOHWithTP = cb.sum(cb.<Number>selectCase().when(noOOHExp,cb.<Number>selectCase().when(tpExp, amt).otherwise(FALSE_VALUE)).otherwise(FALSE_VALUE));
		selectionList.add(nonOOHWithTP.alias(ALIAS_NON_OOH_TP));
		
		//OOH without TP	Total Amount of FINISH Quotes in which the quotes are with OOH and the Target Price are blank (Sales did not input).
		Expression<Number> oohNonTP = cb.sum(cb.<Number>selectCase().when(oohExp,cb.<Number>selectCase().when(noTpExp, amt).otherwise(FALSE_VALUE)).otherwise(FALSE_VALUE));
		selectionList.add(oohNonTP.alias(ALIAS_OOH_NO_TP));
		
		//OOH with TP Met	Total Amount of FINISH Quotes in which the quotes are with OOH and the Quoted Price �� the Target Price.
		Expression<Number> oohWithTP = cb.sum(cb.<Number>selectCase().when(oohExp,cb.<Number>selectCase().when(tpExp, amt).otherwise(FALSE_VALUE)).otherwise(FALSE_VALUE));
		selectionList.add(oohWithTP.alias(ALIAS_OOH_WITH_TP));
		
		//OOH Total Amount	Total Amount of FINISH Quotes in which the quotes are with OOH.
		Expression<Number> oohAmt = cb.sum(cb.<Number>selectCase().when(oohExp,amt).otherwise(FALSE_VALUE));
		selectionList.add(oohAmt.alias(ALIAS_OOH_AMT));
		
		//Total Amount	Non OOH with TP Met plus OOH Total Amount
		/*Expression<Number> totalAmt = cb.sum(cb.<Number>selectCase().when(tpExp,amt).otherwise(falseValue));
		selectionList.add(totalAmt.alias(Alias_TOTAL_AMT));*/

		
		
	}

	private void getPredicatesForOOH(List<Predicate> predicates,
			ReportSearchCriteria criteria,
			CriteriaBuilder cb,
			Root<QuoteItem> quoteItem, Join<QuoteItem, Quote> quote,
			Join<QuoteItem, ProductGroup> productGroup1,
			Join<QuoteItem, ProductGroup> productGroup2,
			Join<Quote, Team> team, Join<User, User> salesMgr,
			Join<Quote, User> saleUser, Join<Quote, BizUnit> bizUnit,
			Join<QuoteItem, Manufacturer> manufacturer,
			Join<Quote, Customer> customer) {

		//always get the stage is finish 
		predicates.add(cb.equal(quoteItem.get("stage"), Constants.QUOTE_STAGE_FINISH));
		
		// quote period
		if(null!=criteria.getQuoteFrm())
			predicates.add(cb.greaterThanOrEqualTo(quoteItem.<Date>get("sentOutTime"), criteria.getQuoteFrm()));
		if(null!=criteria.getQuoteTo())
			predicates.add(cb.lessThan(quoteItem.<Date>get("sentOutTime"), criteria.getQuoteTo()));

		//sales manager
		if(!QuoteUtil.isEmpty(criteria.getSalesMgr()) && !QuoteSBConstant.WILDCARD_ALL.equals(criteria.getSalesMgr()))
			predicates.add(cb.equal(salesMgr.get("name"), criteria.getSalesMgr()));
		
		//sale steam
		if(!QuoteUtil.isEmpty(criteria.getSalesTeam()) && !QuoteSBConstant.WILDCARD_ALL.equals(criteria.getSalesTeam()))
			predicates.add(cb.equal(team.get("name"), criteria.getSalesTeam()));
		//salesUser
		if(!QuoteUtil.isEmpty(criteria.getSalesUser()) && !QuoteSBConstant.WILDCARD_ALL.equals(criteria.getSalesUser()))
			predicates.add(cb.equal(saleUser.get("name"), criteria.getSalesUser()));
		
		//customer search filter
		if(!QuoteUtil.isEmpty(criteria.getCustomerName()))
			predicates.add(cb.like(cb.upper(customer.<String>get("customerName1")),"%"+criteria.getCustomerName().toUpperCase()+"%"));
		
		if(!QuoteUtil.isEmpty(criteria.getCustomerNum()))
			predicates.add(cb.equal(customer.get("customerNumber"), criteria.getCustomerNum()));
		
		//mfr
		if(!QuoteUtil.isEmpty(criteria.getMfr())&& !QuoteSBConstant.WILDCARD_ALL.equals(criteria.getMfr()))
			predicates.add(cb.equal(manufacturer.get("name"), criteria.getMfr()));
		
		//product group information
		if(!QuoteUtil.isEmpty(criteria.getProductGrp1()))
			predicates.add(cb.like(productGroup1.<String>get("name"), "%"+criteria.getProductGrp1().toUpperCase()+"%"));
		if(!QuoteUtil.isEmpty(criteria.getProductGrp2()))
			predicates.add(cb.like(productGroup2.<String>get("name"), "%"+criteria.getProductGrp2().toUpperCase()+"%"));
		if(!QuoteUtil.isEmpty(criteria.getProductGrp3()))
			predicates.add(cb.like(cb.upper(quoteItem.<String>get("productGroup3")),"%"+criteria.getProductGrp3().toUpperCase()+"%"));
		if(!QuoteUtil.isEmpty(criteria.getProductGrp4()))
			predicates.add(cb.like(cb.upper(quoteItem.<String>get("productGroup4")),"%"+criteria.getProductGrp4().toUpperCase()+"%"));
		
		//quoteHandler
		if(!QuoteUtil.isEmpty(criteria.getQuoteHandler())&& !QuoteSBConstant.WILDCARD_ALL.equals(criteria.getQuoteHandler()))
			predicates.add(cb.equal(quoteItem.get("lastUpdatedQc"), criteria.getQuoteHandler()));
		//BU
		if(!QuoteUtil.isEmpty(criteria.getBizUnit())&& !QuoteSBConstant.WILDCARD_ALL.equals(criteria.getBizUnit()))
			predicates.add(cb.equal(bizUnit.get("name"), criteria.getBizUnit()));
		//PN
		if(!QuoteUtil.isEmpty(criteria.getQuotedPartNumber()))
			predicates.add(cb.like(cb.upper(quoteItem.<String>get("quotedPartNumber")), "%"+criteria.getQuotedPartNumber().toUpperCase()+"%"));
		
	}

	private void getQuerySelectionAndGroupForOOH(CriteriaBuilder cb,
			List<Expression<?>> grpColumns,
			List<Selection<?>> selectionList,
			ReportSearchCriteria criteria,
			Root<QuoteItem> quoteItem, Join<QuoteItem, Quote> quote,
			Join<QuoteItem, ProductGroup> productGroup1,
			Join<QuoteItem, ProductGroup> productGroup2,
			Join<Quote, Team> team, Join<User, User> salesMgr,
			Join<Quote, User> saleUser, Join<Quote, BizUnit> bizUnit,
			Join<QuoteItem, Manufacturer> manufacturer,
			Join<Quote, Customer> customer,
			List<Order> orderBy) {
		//group by quote last update date
				if(criteria.isShowQuotePeriod()) {

					if (QUOTE_TYPE_YEAR.equals(criteria.getSearchQuoteType())) {
						grpColumns.add(quoteItem.get("fisYear"));
						selectionList.add(quoteItem.get("fisYear").alias(ALIAS_QUOTE_PERIOD));

						orderBy.add(cb.asc(quoteItem.get("fisYear")));
						
					} else if (QUOTE_TYPE_QTR.equals(criteria.getSearchQuoteType())) {
						
//						Expression<String> start1 = cb.literal("0");
//						Expression<String> length1 = cb.literal("2");
//						Expression<String> qtrYear = cb.function("subStr", String.class,quoteItem.get("fisQtr"),start1,length1);
//						Expression<String> start2 = cb.literal("3");
//						Expression<String> length2 = cb.literal("1");
//						Expression<String> qtrNo = cb.function("subStr", String.class,quoteItem.get("fisQtr"),start2,length2);
//						Expression<String> qtr = cb.concat("FY", qtrYear);
//						qtr = cb.concat(qtr, "Q");
//						qtr = cb.concat(qtr, qtrNo);
						selectionList.add(quoteItem.get("fisQtr").alias(ALIAS_QUOTE_PERIOD));  // fix defect 316 ---issue 4
						
						grpColumns.add(quoteItem.get("fisQtr"));
						
						orderBy.add(cb.asc(quoteItem.get("fisQtr")));
					} else {
						Expression<String> startY = cb.literal("1");
						Expression<String> lenY =  cb.literal("4");
						Expression<String> yearStr = cb.function("subStr", String.class,quoteItem.get("fisMth"),startY,lenY);
						
						 
//						Expression<String> dtformat = cb.literal("YY MM");
						//Expression<String> toCharformat = cb.literal("MON-YY");
//						sqlOrderBy.append("to_date(SUBSTR(t0.FIS_MTH,3,length(t0.FIS_MTH)),'YY MM') ");
						Expression<String> startM = cb.literal("6");
						Expression<String> lenM = cb.function("LENGTH",String.class,quoteItem.get("fisMth"));
						
						Expression<String> mthStr = cb.function("subStr", String.class,quoteItem.get("fisMth"),startM,lenM);
						/*cb.selectCase().when(cb.equal(mthStr, "AUG"), "M02")
							.otherwise(cb.selectCase().when(cb.equal(mthStr, "SEP"), "M03")
									.otherwise(cb.selectCase().when(cb.equal(mthStr, "OCT"), "M04")
											.otherwise(cb.selectCase().when(cb.equal(mthStr, "NOV"), "M05"))));
						*/
						
//						Expression<Date> mthDetail = cb.function("TO_DATE", Date.class,mthStr,dtformat);

						selectionList.add(quoteItem.get("fisMth").alias(ALIAS_QUOTE_PERIOD));  // fix defect 316 ---issue 4
						grpColumns.add(quoteItem.get("fisMth"));
						
						orderBy.add(cb.asc( 
								cb.selectCase().when(cb.equal(mthStr, "AUG"), cb.concat(yearStr, "M02"))
									.when(cb.equal(mthStr, "SEP"), cb.concat(yearStr,"M03"))
									.when(cb.equal(mthStr, "OCT"), cb.concat(yearStr,"M04"))
									.when(cb.equal(mthStr, "NOV"), cb.concat(yearStr,"M05"))
									.when(cb.equal(mthStr, "DEC"), cb.concat(yearStr,"M06"))
									.when(cb.equal(mthStr, "JAN"), cb.concat(yearStr,"M07"))
									.when(cb.equal(mthStr, "FEB"), cb.concat(yearStr,"M08"))
									.when(cb.equal(mthStr, "MAR"), cb.concat(yearStr,"M09"))
									.when(cb.equal(mthStr, "APR"), cb.concat(yearStr,"M10"))
									.when(cb.equal(mthStr, "MAY"), cb.concat(yearStr,"M11"))
									.when(cb.equal(mthStr, "JUN"), cb.concat(yearStr,"M12"))
									.when(cb.equal(mthStr, "JUL"), cb.concat(yearStr,"M01"))
								.otherwise( cb.concat(yearStr,"M01"))));
					}

				}
				
				
				
				

				//group by sales manager
				if(criteria.isShowSalesMgr()) {
					grpColumns.add(salesMgr.get("name"));
					selectionList.add(salesMgr.get("name").alias(ALIAS_SALES_MGR));
					orderBy.add(cb.asc(salesMgr.get("name")));
				}
				
				//group by sales team
				if(criteria.isShowSalesTeam()){
					grpColumns.add(team.get("name"));
					selectionList.add(team.get("name").alias(ALIAS_SALES_TEAM));
					orderBy.add(cb.asc(team.get("name")));
				}
				
				//group by sales User
				if(criteria.isShowSalesUser()){
					grpColumns.add(saleUser.get("name"));
					selectionList.add(saleUser.get("name").alias(ALIAS_SALES_USER));
					orderBy.add(cb.asc(saleUser.get("name")));
				}
				
				//group by customer
				if(criteria.isShowCustomer()) { // sold to code
					grpColumns.add(customer.get("customerNumber"));
					selectionList.add(customer.get("customerNumber").alias(ALIAS_CUSTOMER));
					orderBy.add(cb.asc(customer.get("customerNumber")));
					
					grpColumns.add(customer.get("customerName1"));
					selectionList.add(customer.get("customerName1").alias(ALIAS_CUSTOMER_NAME));
					orderBy.add(cb.asc(customer.get("customerName1")));
				}
				
				//group by manufacturer
				if(criteria.isShowmfr()) {
					grpColumns.add(manufacturer.get("name"));
					selectionList.add(manufacturer.get("name").alias(ALIAS_MFR));
					orderBy.add(cb.asc(manufacturer.get("name")));
				}
				
				//group by product group  information 
				if(criteria.isShowProductGrp1()) {
					grpColumns.add(productGroup1.get("name"));
					selectionList.add(productGroup1.get("name").alias(ALIAS_PRODUCT_GRP_1));
					orderBy.add(cb.asc(productGroup1.get("name")));
				}
				
				if(criteria.isShowProductGrp2()) {
					grpColumns.add(productGroup2.get("name"));
					selectionList.add(productGroup2.get("name").alias(ALIAS_PRODUCT_GRP_2));
					orderBy.add(cb.asc(productGroup2.get("name")));
				}
				
				if(criteria.isShowProductGrp3()) {
					grpColumns.add(quoteItem.get("productGroup3"));
					selectionList.add(quoteItem.get("productGroup3").alias(ALIAS_PRODUCT_GRP_3));
					orderBy.add(cb.asc(quoteItem.get("productGroup3")));
				}
				
				if(criteria.isShowProductGrp4()) {
					grpColumns.add(quoteItem.get("productGroup4"));
					selectionList.add(quoteItem.get("productGroup4").alias(ALIAS_PRODUCT_GRP_4));
					orderBy.add(cb.asc(quoteItem.get("productGroup4")));
				}
				
				//group by Quote Handler
				if(criteria.isShowQuoteHandler()) {
					grpColumns.add(quoteItem.get("lastUpdatedQc"));
					selectionList.add(quoteItem.get("lastUpdatedQc").alias(ALIAS_QUOTE_HANDLER));
					orderBy.add(cb.asc(quoteItem.get("lastUpdatedQc")));
				}
				
				//group by BU region
				if(criteria.isShowBizUnit()) {
					grpColumns.add(bizUnit.get("name"));
					selectionList.add(bizUnit.get("name").alias(ALIAS_BU));
					orderBy.add(cb.asc(bizUnit.get("name")));
				}
				
				//group by part number
				if(criteria.isShowPN()) {
					grpColumns.add(quoteItem.get("quotedPartNumber"));
					selectionList.add(quoteItem.get("quotedPartNumber").alias(ALIAS_PN));
					orderBy.add(cb.asc(quoteItem.get("quotedPartNumber")));
				}
		
	}
	
	
	private List<OrderOnHandReportVo> convertToOOHVO(List<Tuple> reports){
		List<OrderOnHandReportVo> reportsLst = new ArrayList<OrderOnHandReportVo>();
		for(Tuple obj:reports){
			OrderOnHandReportVo vo = new OrderOnHandReportVo();
			setResultToVo(obj,vo);
			//Total Amount	Non OOH with TP Met plus OOH Total Amount
			vo.setTotalAmt(Double.parseDouble(df.format(vo.getNonOohTP()+vo.getOohAmt())));
			
			reportsLst.add(vo);
		}
		return reportsLst;
		
	
	}
	
	/**
	 * 
	 * convert the result to report VO.
	 * return list report object
	 * @author 916138
	 * @param results
	 * @param criteria
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private List<QuoteHitRateReportVo> convertToQHRVO(List<Map> results,ReportSearchCriteria criteria){
		List<QuoteHitRateReportVo> reportsLst = new ArrayList<QuoteHitRateReportVo>();
		for(Map<?, ?> obj:results) {
			QuoteHitRateReportVo newVo = convertToQHRVO(obj,criteria);
			reportsLst.add(newVo);
		}
		
		return reportsLst;
	}
	
	/**
	 * convert the result to report VO 
	 * return report object 
	 * @author 916138
	 * @param obj
	 * @param criteria
	 * @return
	 */
	private QuoteHitRateReportVo convertToQHRVO(Map<?, ?> obj,ReportSearchCriteria criteria){

			QuoteHitRateReportVo newVo = new QuoteHitRateReportVo();
			setResultToVo(obj,newVo);
			BigDecimal quoted;
			BigDecimal hitOrder;

			if("0".equals(criteria.getSearchResultType())) { 
				quoted = new BigDecimal(newVo.getQuotedCnt());
				hitOrder = new BigDecimal(newVo.getHitOrderCnt());
			}else {
				quoted = new BigDecimal(newVo.getQuotedAmt());
				hitOrder = new BigDecimal(newVo.getHitOrderAmt());
			}

			if(null!=quoted && (quoted.compareTo(BigDecimal.ZERO) != 0)) {
				BigDecimal hitOrderRate = hitOrder.divide(quoted, 5, BigDecimal.ROUND_UP);
				newVo.setHitOrderRate((hitOrderRate.multiply(MULTIPLIER)).doubleValue() );
			}
			
		
		return newVo;
	}
	
	


	private void setResultToVo(Tuple obj, Object vo) {

			List<TupleElement<?>> elements = obj.getElements();
			try {
				for(TupleElement<?> element:elements) {
					String alias = element.getAlias();
					//LOG.info("the element type is ===>> " + element.getJavaType());
					 Field field = getDeclaredField(vo,alias);
					 field.setAccessible(true);
					 Class<?> fieldType = field.getType();
					 Class<?> elementType = element.getJavaType();
					 if (fieldType.equals(long.class) || fieldType.equals(Long.class))
					 {
						 
						 if(elementType.equals(long.class) || elementType.equals(Long.class)) {
							 Long value = (Long)obj.get(alias);
							 field.set(vo, value==null?0:value.longValue());
						 }else {
							 BigDecimal value = (BigDecimal)obj.get(alias);
							 field.set(vo, value==null?0:value.longValue());
						 }
						
						
					 }else if (fieldType.equals(double.class) || fieldType.equals(Double.class))
					 {
						 BigDecimal value = (BigDecimal)obj.get(alias);
						 
						 field.set(vo, value==null?0:value.doubleValue());
					 }else {
						 field.set(vo, fieldType.cast(obj.get(alias)));
					 }
					
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {

				LOG.log(Level.SEVERE,"Exception occuring during setting value to Vo object : "+ e.getMessage(), e); 
			}
		
		
	}


	public static Field getDeclaredField(Object object, String fieldName){  
	        Field field = null ;  
	          
	        Class<?> clazz = object.getClass() ;  
	          
	        for(; clazz != Object.class ; clazz = clazz.getSuperclass()) {  
	            try {  
	                field = clazz.getDeclaredField(fieldName) ;  
	                return field ;  
	            } catch (Exception e) {  
	                 LOG.log(Level.SEVERE,"Exception occuring for getting declared field :"+field+" :: "+ e.getMessage(), e); 
	            }   
	        }  
	      
	        return null;  
	    }     

	
	

	

}
