package com.avnet.emasia.webquote.ejb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.avnet.emasia.webquote.constants.ActionEnum;
import com.avnet.emasia.webquote.dp.EJBCommonSB;
import com.avnet.emasia.webquote.entity.Attachment;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.CostIndicator;
import com.avnet.emasia.webquote.entity.Currency;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.MaterialRegional;
import com.avnet.emasia.webquote.entity.Pricer;
import com.avnet.emasia.webquote.entity.ProgramPricer;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.SalesCostType;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.exception.WebQuoteException;
import com.avnet.emasia.webquote.quote.ejb.CostIndicatorSB;
import com.avnet.emasia.webquote.quote.ejb.CustomerSB;
import com.avnet.emasia.webquote.quote.ejb.ManufacturerSB;
import com.avnet.emasia.webquote.quote.ejb.MaterialSB;
import com.avnet.emasia.webquote.quote.ejb.QuoteSB;
import com.avnet.emasia.webquote.quote.ejb.RestrictedCustomerMappingSB;
import com.avnet.emasia.webquote.quote.ejb.SAPWebServiceSB;
import com.avnet.emasia.webquote.quote.ejb.SapDpaCustSB;
import com.avnet.emasia.webquote.quote.ejb.SystemCodeMaintenanceSB;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.quote.vo.QuoteBuilderBean;
import com.avnet.emasia.webquote.quote.vo.QuoteBuilderNonSalesCostBean;
import com.avnet.emasia.webquote.quote.vo.QuoteBuilderSalesCostBean;
import com.avnet.emasia.webquote.quote.vo.RestrictedCustomerMappingParameter;
import com.avnet.emasia.webquote.quote.vo.RestrictedCustomerMappingSearchCriteria;
import com.avnet.emasia.webquote.strategy.StrategyFactory;
import com.avnet.emasia.webquote.user.ejb.ApplicationSB;
import com.avnet.emasia.webquote.user.ejb.BizUnitSB;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilites.web.common.CacheUtil;
import com.avnet.emasia.webquote.utilites.web.util.DeploymentConfiguration;
import com.avnet.emasia.webquote.utilities.DBResourceBundle;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import com.avnet.emasia.webquote.utilities.bean.ExcelAliasBean;
import com.avnet.emasia.webquote.utilities.bean.MailInfoBean;
import com.avnet.emasia.webquote.utilities.bean.QuoteBuilderExcelAlias;
import com.avnet.emasia.webquote.utilities.common.OSTimeZone;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;
import com.avnet.emasia.webquote.utilities.ejb.MailUtilsSB;
import com.avnet.emasia.webquote.web.quote.constant.QuoteConstant;
import com.avnet.emasia.webquote.web.quote.strategy.SendQuotationEmailStrategy;
import com.avnet.emasia.webquote.web.quote.vo.QuotationEmailVO;

@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
public class QuoteBuilderSB {
	
	private static final Logger LOGGER = Logger .getLogger(QuoteBuilderSB.class.getName());
	
	
	@EJB
	private UserSB userSB;
	
	@EJB
	RestrictedCustomerMappingSB restrictedCustomerSB;

	@EJB
	private QuoteSB quoteSB;

	@EJB
	private CustomerSB customerSB;
	
	@EJB
	private ManufacturerSB manufacturerSB;
	
	@EJB
	private MaterialSB materialSB;
	
	@EJB
	private BizUnitSB bizUnitSB;
	
	@EJB
	private SAPWebServiceSB sapWebServiceSB;
	
	@EJB
	private SapMesgProducerSB sapMesgProducerSB;
	
	@EJB
	private MailUtilsSB mailUtilsSB;
	
	@EJB
	private SysConfigSB sysConfigSB;
	
	@EJB
	private EJBCommonSB ejbCommonSB;
	
	@EJB
	private SystemCodeMaintenanceSB sysMaintSB;
	
	@EJB
	private CostIndicatorSB costIndicatorSB;
	
	@EJB
	private SapDpaCustSB sapDpaCustSB;
	
	@EJB
	private ApplicationSB applicationSB;
	
	@EJB
	private CacheUtil cacheUtil;
	int indexQuoteItem =1;
	
	@SuppressWarnings("rawtypes")
	@Asynchronous
	public void buildQuote(List beans,String quoteCostType,Attachment quoteAttachment,User user,Locale locale) throws Exception {
		LOGGER.log(Level.INFO, "=====QuoteBuilderSB buildQuote Start===== ");
		List<QuoteBuilderBean> quoteBuilderBeans = convertToQuoteBuilderBean(beans);
		for (QuoteBuilderBean quoteBuilderBean : quoteBuilderBeans) {
			
			if(StringUtils.isNotBlank(quoteBuilderBean.getRegion())){
			quoteBuilderBean.setRegion(quoteBuilderBean.getRegion().toUpperCase());
			}
			if(StringUtils.isNotBlank(quoteBuilderBean.getMfr())){
			quoteBuilderBean.setMfr(quoteBuilderBean.getMfr().toUpperCase());
			}
			
			if(StringUtils.isNotBlank(quoteBuilderBean.getSalesCostType())){
				quoteBuilderBean.setSalesCostType(quoteBuilderBean.getSalesCostType().toUpperCase());
			}
			if(StringUtils.isNotBlank(quoteBuilderBean.getResaleIndicator())){
				// fix ResaleIndicator 
				String rIndicator = quoteBuilderBean.getResaleIndicator();
				String rit = rIndicator.replaceAll("a", "A");
				quoteBuilderBean.setResaleIndicator(rit);
			}
				
		}
		// find allcostIndicators
//		List<String> costIndicators = costIndicatorSB.findAllNameList();
//		List<String> cIndicators= new ArrayList<>();
//		for (String cIndicator : costIndicators) {
//			cIndicators.add(cIndicator);
//		}
		boolean hasErrorData = false;

		List<QuoteBuilderBean> tmpBeanList = new ArrayList<QuoteBuilderBean>();

		for(QuoteBuilderBean bean : quoteBuilderBeans) {
			try {
				int errorIndex = 1;
				StringBuffer sb = new StringBuffer("");
				//check Mandatory Fields
				boolean hasEmptyRequiredField = validateMandatoryColumn(quoteCostType, bean);
				if(hasEmptyRequiredField) {
					hasErrorData = true;
					String errorMsg =getLoclaizedText("wq.message.NotFilledMandatoryField",locale);
					errorMsg = errorIndex + "."  + errorMsg + "\r\n";
					sb.append(errorMsg);
					errorIndex++;
				}
				
//				//Sales Employee Code
//				// If the Sales Emp Code is invalid, no need to check if the Salesperson is in the region or not.

				User salesman = getSales(bean.getSalesEmpCode());
				boolean salesRole = false;
				if (salesman != null) {
					salesRole = applicationSB.isActionAccessibleByUser(salesman, "RfqSubmission.roleProspective.Sales");
					//added by damonChen@20170207
					if (!salesRole) {
						salesRole=applicationSB.isActionAccessibleByUser(salesman, "RfqSubmission.roleProspective.InsideSales");
					}

				}

				if(salesman == null || !salesRole) {
					hasErrorData = true;
					String errorMsg =getLoclaizedText("wq.message.invalidSalesEmpCode",locale);
					errorMsg = errorIndex + "."  + errorMsg + "\r\n";
					sb.append(errorMsg);
					errorIndex++;
				}else {
					if(StringUtils.isNotBlank(bean.getRegion())) {
						if(!validateSalesRegion(bean.getSalesEmpCode(), bean.getRegion().toUpperCase())) {
							hasErrorData = true;
							String errorMsg =getLoclaizedText("wq.message.invalidSalesRegion",locale);
							errorMsg = errorIndex + "."  + errorMsg + "\r\n";
							sb.append(errorMsg);
							errorIndex++;
						}
					}
					 
				}
				
				
				if(StringUtils.isNotBlank(bean.getRegion())) {
					//Sold To Code
					if(!validateSoldToCust(bean.getSoldToCode(), bean.getRegion().toUpperCase())) {
						hasErrorData = true;
						String errorMsg =getLoclaizedText("wq.message.invalidSoldToCode",locale);
						errorMsg = errorIndex + "."  + errorMsg + "\r\n";
						sb.append(errorMsg);
						errorIndex++;
					}
					
					if(!validateUserAccess(bean.getRegion().toUpperCase(),user)) {
						hasErrorData = true;
						String errorMsg =getLoclaizedText("wq.message.UserNotHasAccess",locale);
						errorMsg = errorIndex + "."  + errorMsg + "\r\n";
						sb.append(errorMsg);
						errorIndex++;
					}
				}
				
					
				if(StringUtils.isNotBlank(bean.getEndCustCode())) {
					//End Customer code
					boolean isValidEndCust = customerSB.isValidEndCustomer(bean.getEndCustCode());
					if(!isValidEndCust) {
						hasErrorData = true;
						String errorMsg =getLoclaizedText("wq.message.invalidEndCustomerCode",locale);
						errorMsg = errorIndex + "."  + errorMsg + "\r\n";
						sb.append(errorMsg);
						errorIndex++;
					}
				}
				//isValidMfr   String mfr,String beanRegin
				//update 20190111
				if(!isValidMfr(bean.getMfr())){
					hasErrorData = true;
					String errorMsg =getLoclaizedText("wq.message.invalidRegion",locale);
					errorMsg = errorIndex + "."  + errorMsg + "\r\n";
					sb.append(errorMsg);
					errorIndex++;
				}
				
				
//				if(!isValidMfr(bean.getMfr(), bean.getRegion())){
//					hasErrorData = true;
//					String errorMsg =getLoclaizedText("wq.message.invalidRegion",locale);
//					errorMsg = errorIndex + "."  + errorMsg + "\r\n";
//					sb.append(errorMsg);
//					errorIndex++;
//				}
				
				
				
				//mfr validate
//				if(StringUtils.isNotBlank(bean.getMfr())) {
//				
//					//mfr validate   show errormessgae  findManufacturerByName
//					Manufacturer mfnufacturer = manufacturerSB.findManufacturerByName(bean.getMfr().toUpperCase());
//					List  biz= new ArrayList();
//					if(mfnufacturer!=null){
//						List<BizUnit> bizUnits = mfnufacturer.getBizUnits();
//						
//						for(BizUnit bis:bizUnits){
//							biz.add(bis.getName());
//							
//						}
//					}
//					
//					if(!biz.contains(bean.getRegion().toUpperCase())){
//						hasErrorData = true;
//						String errorMsg =getLoclaizedText("wq.message.invalidRegion",locale);
//						errorMsg = errorIndex + "."  + errorMsg + "\r\n";
//						sb.append(errorMsg);
//						errorIndex++;
//					}
//				}
				
// 				if(StringUtils.isNotBlank(bean.getCostIndicator())) {
//					if(!cIndicators.contains(bean.getCostIndicator().trim())) {
//						hasErrorData = true;
//						String errorMsg =getLoclaizedText("wq.message.invalidCostIndicator",locale);
//						errorMsg = errorIndex + "."  + errorMsg + "\r\n";
//						sb.append(errorMsg);
//						errorIndex++;
//					}
//				}
				
 				if(StringUtils.isNotBlank(bean.getCostIndicator())) {
					if(!isValidCostIndicator(bean.getCostIndicator().trim())) {
						hasErrorData = true;
						String errorMsg =getLoclaizedText("wq.message.invalidCostIndicator",locale);
						errorMsg = errorIndex + "."  + errorMsg + "\r\n";
						sb.append(errorMsg);
						errorIndex++;
					}
				}
				if(StringUtils.isNotBlank(bean.getShipmentValidity())) {
					/*boolean isExpired = isExpiredDate(parseStringToDate(bean.getShipmentValidity()));
					if(isExpired) {
						hasErrorData = true;
						String errorMsg =getLoclaizedText("wq.message.isExpiredShipmentValidity",locale);
						errorMsg = errorIndex + "."  + errorMsg + "\r\n";
						sb.append(errorMsg);
						errorIndex++;
					}*/
					
					Date d = parseStringToDate(bean.getShipmentValidity());
					if(d == null) {
						hasErrorData = true;
						String errorMsg =getLoclaizedText("wq.message.invalidShipmentValidityDate",locale);
						errorMsg = errorIndex + "."  + errorMsg + "\r\n";
						sb.append(errorMsg);
						errorIndex++;
					}
				}
				
				if(StringUtils.isNotBlank(bean.getQuotationEffectiveDate())) {
					Date d = parseStringToDate(bean.getQuotationEffectiveDate());
					if(d == null) {
						hasErrorData = true;
						String errorMsg =getLoclaizedText("wq.message.invalidQuotationEffectiveDate",locale);
						errorMsg = errorIndex + "."  + errorMsg + "\r\n";
						sb.append(errorMsg);
						errorIndex++;
					}
				}
				
				
				if(StringUtils.isNotBlank(bean.getCost())) {
					if(!isNumber(bean.getCost())) {
						hasErrorData = true;
						String errorMsg =getLoclaizedText("wq.message.CostNotNumber",locale);
						errorMsg = errorIndex + "."  + errorMsg + "\r\n";
						sb.append(errorMsg);
						errorIndex++;
					}
				}
				
				if(StringUtils.isNotBlank(bean.getCustGroupId())) {
					boolean isValid = isValidCustGroup(bean.getCustGroupId(), bean.getRegion(), bean.getSoldToCode(), bean.getEndCustCode());
					if(!isValid) {
						hasErrorData = true;
						String errorMsg =getLoclaizedText("wq.message.invalidCustGroupID",locale);
						errorMsg = errorIndex + "."  + errorMsg + "\r\n";
						sb.append(errorMsg);
						errorIndex++;
					}
				}
				boolean isValid = isValidSalesCostPart(bean.getMfr(), bean.getQuotePartNumber(),bean.getRegion());
				bean.setSaleCostFlag(isValid);
				if(StringUtils.equals(quoteCostType, QuoteConstant.SALES_COST)) { 
					if(!isValid) {
						hasErrorData = true;
						String errorMsg =getLoclaizedText("wq.message.notSalesCostPart",locale);
						errorMsg = errorIndex + "."  + errorMsg + "\r\n";
						sb.append(errorMsg);
						errorIndex++;
					}
				}
				
				if(StringUtils.isNotBlank(bean.getSalesCost())) {
					if(!isNumber(bean.getSalesCost())) {
						hasErrorData = true;
						String errorMsg =getLoclaizedText("wq.message.SalesCostNotNumber",locale);
						errorMsg = errorIndex + "."  + errorMsg + "\r\n";
						sb.append(errorMsg);
						errorIndex++;
					}
				}
				
				if(StringUtils.isNotBlank(bean.getSuggestedResale())) {
					if(!isNumber(bean.getSuggestedResale())) {
						hasErrorData = true;
						String errorMsg =getLoclaizedText("wq.message.SuggestedResaleNotNumber",locale);
						errorMsg = errorIndex + "."  + errorMsg + "\r\n";
						sb.append(errorMsg);
						errorIndex++;
					}
				}
				
				if(StringUtils.isNotBlank(bean.getQuotedPrice())) {
					if(!isNumber(bean.getQuotedPrice())) {
						hasErrorData = true;
						String errorMsg =getLoclaizedText("wq.message.AvnetQuotedPriceNotNumber",locale);
						errorMsg = errorIndex + "."  + errorMsg + "\r\n";
						sb.append(errorMsg);
						errorIndex++;
					}
				}
				
				
				
				if(StringUtils.isNotBlank(bean.getMpq())) {
					if(!isNumber(bean.getMpq())) {
						hasErrorData = true;
						String errorMsg =getLoclaizedText("wq.message.MPQNotNumber",locale);
						errorMsg = errorIndex + "."  + errorMsg + "\r\n";
						sb.append(errorMsg);
						errorIndex++;
					}
				}
				
				if(StringUtils.isNotBlank(bean.getMoq())) {
					if(!isNumber(bean.getMoq())) {
						hasErrorData = true;
						String errorMsg =getLoclaizedText("wq.message.MOQNotNumber",locale);
						errorMsg = errorIndex + "."  + errorMsg + "\r\n";
						sb.append(errorMsg);
						errorIndex++;
					}
				}
				
				if(StringUtils.isNotBlank(bean.getMov())) {
					if(!isNumber(bean.getMov())) {
						hasErrorData = true;
						String errorMsg =getLoclaizedText("wq.message.MOVNotNumber",locale);
						errorMsg = errorIndex + "."  + errorMsg + "\r\n";
						sb.append(errorMsg);
						errorIndex++;
					}
				}
				
				if(StringUtils.isNotBlank(bean.getVendorQuoteQty())) {
					if(!isNumber(bean.getVendorQuoteQty())) {
						hasErrorData = true;
						String errorMsg =getLoclaizedText("wq.message.MFRQuoteQtyNotNumber",locale);
						errorMsg = errorIndex + "."  + errorMsg + "\r\n";
						sb.append(errorMsg);
						errorIndex++;
					}
				}
				
				if(StringUtils.isNotBlank(bean.getQuotedQty())) {
					if(!isNumber(bean.getQuotedQty())) {
						hasErrorData = true;
						String errorMsg =getLoclaizedText("wq.message.AvnetQuotedQtyNotNumber",locale);
						errorMsg = errorIndex + "."  + errorMsg + "\r\n";
						sb.append(errorMsg);
						errorIndex++;
					}
				}
				
				if(StringUtils.isNotBlank(bean.getPriceValidity())) {
					if(!isValidPriceValidity(bean.getPriceValidity())) {
						hasErrorData = true;
						String errorMsg =getLoclaizedText("wq.message.PriceValidityNotNumberOrDate",locale);
						errorMsg = errorIndex + "."  + errorMsg + "\r\n";
						sb.append(errorMsg);
						errorIndex++;
					}
				}
				

				try {
					tmpBeanList.clear();
					tmpBeanList.add(bean);
					this.validateRestrictedCustomer(tmpBeanList, locale);
				} catch (WebQuoteException we) {
					hasErrorData = true;
					String errorMsg =getLoclaizedText("wq.message.restrictedCustomerFailsWithoutRow",locale);
					errorMsg = errorIndex + "."  + errorMsg + "\r\n";
					sb.append(errorMsg);
					errorIndex++;
				}

				
				bean.setErrorMsg(sb.toString());
				if(!("").equals(bean.getErrorMsg())){
					bean.setErrorFlag(true);
				}
				
				
				
			} catch (Exception e) {
				throw e; 
			}
		}
		
		
		
		//List<QuoteBuilderBean> clearErrorQuoteBuilderBeans = quoteBuilderBeans.stream().filter(quoteBuilderBean -> ("").equals(quoteBuilderBean.getErrorMsg())).collect(Collectors.toList());
	
		//check logic
		
			List<Quote> quoteList = convertBeanToQuote(quoteBuilderBeans, quoteAttachment, user,quoteCostType);
			for(Quote q : quoteList) {
				Quote savedQuote = quoteSB.saveQuote(q);
				//send to SAP
				//ejbCommonSB.createQuoteToSo(savedQuote.getQuoteItems());
				//sapWebServiceSB.groupListForSendQuoteToQueue(savedQuote.getQuoteItems());
				sapMesgProducerSB.groupListForSendQuoteToQueue(savedQuote.getQuoteItems());
				
				//send Quote email
				sendQuoteEmail(savedQuote, user);
			}
			//send success email
			sendSuccessEmail(quoteBuilderBeans, quoteCostType,user);
		
			//send fail email
			//sendFailEmail(quoteBuilderBeans, quoteCostType, user);
		indexQuoteItem =1;
		LOGGER.log(Level.INFO, "=====QuoteBuilderSB buildQuote End===== ");
	}
	
	

	@SuppressWarnings("rawtypes")
	public List<QuoteBuilderBean> convertToQuoteBuilderBean(List beans) {
		List<QuoteBuilderBean> beanList = new ArrayList<QuoteBuilderBean>();
		for(Object bean : beans) {
			QuoteBuilderBean quoteBuilderBean = new QuoteBuilderBean();
			try {
				BeanUtils.copyProperties(quoteBuilderBean, bean);
				beanList.add(quoteBuilderBean);
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE,"Exception convert Beans: " + bean.getClass().getName());
			} 
		}
		return beanList;
	}
	
	
	private List<Quote> convertBeanToQuote(List<QuoteBuilderBean> beans,Attachment quoteAttachment,User user,String quoteCostType) {
		QuoteBuilderBean.sortByKey(beans);
		List<Quote> quoteList = new ArrayList<Quote>();
		int flag =0;
		String key =null;
		Map<String,Quote> groupQuoteMap = new HashMap<String,Quote>();
//		groupQuoteMap.size()<1000
		if(beans != null && beans.size() > 0) {
			//List<QuoteBuilderBean> qBeans = beans.stream().filter(quoteBuilderBean -> ("").equals(quoteBuilderBean.getErrorMsg())).collect(Collectors.toList());
			for(QuoteBuilderBean bean : beans) {
				
				String region = bean.getRegion();
				key=getGroupKey(bean,region)+flag;
				if(indexQuoteItem%1000== 0){
					flag++;
				}
				
				if(groupQuoteMap.containsKey(key)) {
					Quote quote = groupQuoteMap.get(key);
					if(!bean.getErrorFlag()){
					QuoteItem item = beanToQuoteItem(bean,quoteCostType,user);
					item.setSubmissionDate(quote.getSubmissionDate());
					item.setQuote(quote);
					quote.getQuoteItems().add(item);
					bean.setFormNumber(quote.getFormNumber());
					bean.setQuoteNumber(item.getQuoteNumber());
					LOGGER.log(Level.INFO, "Form Number:" + quote.getFormNumber() + ",Quote Number:" + item.getQuoteNumber());
					}
				}else {
					List<QuoteItem> quoteItems = new ArrayList<QuoteItem>();
					if(!bean.getErrorFlag()){
						
					
					Quote newQuote = beanToQuote(bean, quoteAttachment, user,quoteCostType);
					BizUnit bizUnit = bizUnitSB.findByPk(region);
					String formNumber = quoteSB.createQuoteBuilderFormNumber(bizUnit);
					newQuote.setFormNumber(formNumber);
					QuoteItem item = beanToQuoteItem(bean,quoteCostType,user);
					item.setSubmissionDate(newQuote.getSubmissionDate());
					item.setQuote(newQuote);
					quoteItems.add(item);
					newQuote.setQuoteItems(quoteItems);
					groupQuoteMap.put(key, newQuote);
					bean.setFormNumber(formNumber);
					bean.setQuoteNumber(item.getQuoteNumber());
					LOGGER.log(Level.INFO, "Form Number:" + formNumber + ",Quote Number:" + item.getQuoteNumber());
					}
				}
			}
			
		}
		if(groupQuoteMap.size() > 0) {
			for(Iterator<String> it = groupQuoteMap.keySet().iterator();it.hasNext();) {
				String ke = it.next();
				Quote q = groupQuoteMap.get(ke);
				quoteList.add(q);
			}
		}
		return quoteList;
	}
	
	private Quote beanToQuote(QuoteBuilderBean bean,Attachment quoteAttachment,User user,String quoteCostType) {
		Quote quote = createBasicQuote(user);
		if(quoteAttachment != null) {
			List<Attachment> attachments = new ArrayList<Attachment>();
			Attachment a = new Attachment();
			try {
				BeanUtils.copyProperties(a, quoteAttachment);
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE,"Exception convert Beans: " + bean.getClass().getName());
			} 
			a.setQuote(quote);
			attachments.add(a);
			quote.setAttachments(attachments);
			
		}
		User sales = getSales(bean.getSalesEmpCode());
		BizUnit bizUnit = null;
		if(StringUtils.isNotBlank(bean.getRegion())) {
			bizUnit = bizUnitSB.findByPk(bean.getRegion());
		}
		
		quote.setBizUnit(bizUnit);
		quote.setSales(sales);
		quote.setSalesUserName(sales.getName());
		quote.setTeam(sales.getTeam());
		
		// set Customer
		Customer soldTo = customerSB.findByPK(bean.getSoldToCode());
		quote.setSoldToCustomer(soldTo);
		quote.setSoldToCustomerName(soldTo.getCustomerFullName());
		return quote;
	}
	
	private Quote createBasicQuote(User user) {
		Quote quote = new Quote();
		Date currentTime = new Date();
    	quote.setId(0L);
    	quote.setBizUnit(user.getDefaultBizUnit());
		quote.setCreatedBy(user.getEmployeeId());
		quote.setCreatedName(user.getName());
		quote.setCreatedOn(currentTime);
		quote.setLastUpdatedBy(user.getEmployeeId());
		quote.setLastUpdatedName(user.getName());
		quote.setLastUpdatedOn(currentTime);
		quote.setSubmissionDate(currentTime);
		return quote;
	}
	
	
	private QuoteItem beanToQuoteItem(QuoteBuilderBean bean,String quoteCostType,User user) {
		indexQuoteItem ++;
		Date currentTime = new Date();
		QuoteItem quoteItem = new QuoteItem();
		//add by Vincent 20181017 
		quoteItem.setFinalCurr(Currency.USD);
		quoteItem.setBuyCurr(Currency.USD);
		quoteItem.setRfqCurr(Currency.USD);
		quoteItem.setExRateBuy(new BigDecimal("1"));
		quoteItem.setExRateRfq(new BigDecimal("1"));
		//quoteItem.setExRateFnl(new BigDecimal("1"));
		//add by Vincent 20181126
		quoteItem.setVat(new BigDecimal("1"));
		quoteItem.setHandling(new BigDecimal("1"));
		//add by vincent 20181203
		quoteItem.setValidFlag(true);
		quoteItem.setSentOutTime(currentTime);
		quoteItem.setLastUpdatedBy(user.getEmployeeId());
		quoteItem.setLastUpdatedName(user.getName());
		quoteItem.setLastUpdatedOn(currentTime);
		quoteItem.setLastUpdatedQc(user.getEmployeeId());
		quoteItem.setLastUpdatedQcName(user.getName());
		BizUnit bizUnit = null;
		if(StringUtils.isNotBlank(bean.getRegion())) {
			bizUnit = bizUnitSB.findByPk(bean.getRegion());
		}
		// set Sold To Code
		Customer soldTo = customerSB.findByPK(bean.getSoldToCode());
		if(soldTo != null) {
			quoteItem.setSoldToCustomer(soldTo);
		}
		// set qty indicator
		if(StringUtils.isNotEmpty(bean.getQtyIndicator())){
			quoteItem.setQtyIndicator(bean.getQtyIndicator());
		}
		//set Customer Group ID
		quoteItem.setCustomerGroupId(bean.getCustGroupId());
		//set End Customer Code
		if(StringUtils.isNotBlank(bean.getEndCustCode())) {
			Customer endCustomer = customerSB.findByPK(bean.getEndCustCode());
			quoteItem.setEndCustomer(endCustomer);
		}
		//set MFR
		Manufacturer mfr = manufacturerSB.findManufacturerByName(bean.getMfr());
		//Manufacturer mfr = manufacturerSB.findManufacturerByName(bean.getMfr(), bizUnit);
		quoteItem.setRequestedMfr(mfr);
		quoteItem.setQuotedMfr(mfr);
		//set Avnet Quoted P/N
		quoteItem.setQuotedPartNumber(bean.getQuotePartNumber());
		quoteItem.setRequestedPartNumber(bean.getQuotePartNumber());
		//set MFR Quote Qty
		quoteItem.setVendorQuoteQty(parseStringToInteger(bean.getVendorQuoteQty()));
		//set Avnet Quoted Qty
		quoteItem.setQuotedQty(parseStringToInteger(bean.getQuotedQty()));
		if(StringUtils.isNotBlank(bean.getSalesCostType())) {
			//set Sales Cost Type
			quoteItem.setSalesCostType(SalesCostType.GetSCTypeByStr(bean.getSalesCostType().toUpperCase()));
		}else {
			quoteItem.setSalesCostType(SalesCostType.GetSCTypeByStr(QuoteConstant.NON_SALES_COST_TYPE));

		}
		//set Cost Indicator
		quoteItem.setCostIndicator(bean.getCostIndicator());
		//set Cost
		quoteItem.setCost(parseStringToDouble(bean.getCost()));
		
		if(StringUtils.isNotBlank(bean.getSalesCost())) {
			//set Sales Cost
			quoteItem.setSalesCost(parseStringToBigdecimal(bean.getSalesCost()));
		}
		
		//set Suggested Resale
		quoteItem.setSuggestedResale(parseStringToBigdecimal(bean.getSuggestedResale()));
		//set Lead Time
		quoteItem.setLeadTime(bean.getLeadTime());
		
		//Quotation Effective Date
		if(StringUtils.isNotBlank(bean.getQuotationEffectiveDate())) {
			quoteItem.setQuotationEffectiveDate(parseStringToDate(bean.getQuotationEffectiveDate()));
		}else {
			// default set today
			quoteItem.setQuotationEffectiveDate(currentTime);
		}
		
		
		//Price Validity
		quoteItem.setPriceValidity(bean.getPriceValidity());
		
		//Shipment Validity
		quoteItem.setShipmentValidity(parseStringToDate(bean.getShipmentValidity()));
		quoteItem.setMpq(parseStringToInteger(bean.getMpq()));
		quoteItem.setMoq(parseStringToInteger(bean.getMoq()));
		quoteItem.setMov(parseStringToInteger(bean.getMov()));
		quoteItem.setVendorDebitNumber(bean.getVendorDebitNumber());
		quoteItem.setVendorQuoteNumber(bean.getVendorQuoteNumber());
		quoteItem.setTermsAndConditions(bean.getTermsAndConditions());
		quoteItem.setAqcc(bean.getAqcc());
		quoteItem.setInternalComment(bean.getInternalComment());
		
		//set Sales Cost Flag
		quoteItem.setSalesCostFlag(bean.isSaleCostFlag());
		quoteItem.setResaleIndicator(bean.getResaleIndicator());
		
		// set Quote Price
		quoteItem.setQuotedPrice(parseStringToDouble(bean.getQuotedPrice()));
		if (StringUtils.isNotBlank(bean.getMultiUsageFlag())) {
			quoteItem.setMultiUsageFlag(parseStringToBoolean(bean.getMultiUsageFlag()));
		}
		quoteItem.setPmoq(bean.getPmoq());

		Material material = materialSB.find(bean.getMfr(), bean.getQuotePartNumber());
		quoteItem.setQuotedMaterial(material);
		String costIndicatorType = "";
		//set Quote Expire Date
		if(QuoteConstant.SALES_COST.equals(quoteCostType)){
			costIndicatorType = QuoteSBConstant.MATERIAL_TYPE_PROGRAM;
		}else {
			if(hasEffectiveProgramPricer(material)) {
				costIndicatorType = QuoteSBConstant.MATERIAL_TYPE_PROGRAM;
			}else {
				costIndicatorType = QuoteSBConstant.MATERIAL_TYPE_NORMAL;

			}
		}
		// set Action
		quoteItem.setAction(ActionEnum.QUOTE_BUILDER.toString());
		quoteItem.setStage(QuoteSBConstant.QUOTE_STAGE_FINISH);
		quoteItem.setStatus(QuoteSBConstant.RFQ_STATUS_QC);
		//set Quote Number
		String quoteNumber = quoteSB.createQuoteBuilderQuoteNumber(bizUnit);
		quoteItem.setQuoteNumber(quoteNumber);
		quoteItem.setAlternativeQuoteNumber(quoteNumber);
		quoteItem.setFirstRfqCode(quoteNumber);
		quoteItem.setRevertVersion("0A");
		quoteItem.setMaterialTypeId(costIndicatorType);
		//set Quote Expire Date
		setQuoteExpireDate(quoteItem, costIndicatorType);
		
		return quoteItem;
	}
	
	private boolean hasEffectiveProgramPricer(Material material) {
		boolean hasEffectiveProgramPricer = false;
		if(material != null) {
			List<Pricer> pricers = material.getPricers();
			if(pricers != null && pricers.size() > 0) {
				for(Pricer pricer : pricers) {
					if(pricer instanceof ProgramPricer && pricer.isEffectivePricer()) {
						hasEffectiveProgramPricer = true;
						break;
					}
				}
			
				
			}
		}
		return hasEffectiveProgramPricer;
	}
	
	private void setQuoteExpireDate(QuoteItem quoteItem,String costIndicatorType) {
		if (StringUtils.isNotBlank(quoteItem.getPriceValidity())) {
			Date validity = null;
			if (quoteItem.getPriceValidity().matches("[0-9]{1,}")) {
				int shift = Integer.parseInt(quoteItem.getPriceValidity());
				validity = com.avnet.emasia.webquote.utilites.web.util.QuoteUtil.shiftDate(quoteItem.getSentOutTime(),
						shift);
			} else {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				sdf.setTimeZone(OSTimeZone.getOsTimeZone());
				try {
					validity = sdf.parse(quoteItem.getPriceValidity());
				} catch (ParseException e) {
					LOGGER.log(Level.SEVERE, "Exception in parsing date : " + quoteItem.getPriceValidity()
							+ " , Exception message : " + e.getMessage(), e);
				}
			}
			if (validity != null) {
				quoteItem.setPoExpiryDate(validity);
				if (StringUtils.equalsIgnoreCase(costIndicatorType,QuoteSBConstant.MATERIAL_TYPE_NORMAL)) {
					Date quoteExpiryDate = calculateQuoteExpiryDate(validity, QuoteSBConstant.MATERIAL_TYPE_NORMAL);
					quoteItem.setQuoteExpiryDate(quoteExpiryDate);
				} else if (StringUtils.equalsIgnoreCase(costIndicatorType,QuoteSBConstant.MATERIAL_TYPE_PROGRAM)) {
					Date quoteExpiryDate = calculateQuoteExpiryDate(validity, QuoteSBConstant.MATERIAL_TYPE_PROGRAM);
					quoteItem.setQuoteExpiryDate(quoteExpiryDate);
				} else {
					Date quoteExpiryDate = calculateQuoteExpiryDate(validity, QuoteSBConstant.MATERIAL_TYPE_NORMAL);
					quoteItem.setQuoteExpiryDate(quoteExpiryDate);
				}
			}
		}
	}
	
	
	private Date calculateQuoteExpiryDate(Date date, String materialType) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		cal.setTime(date);
		if (materialType.equals(QuoteSBConstant.MATERIAL_TYPE_NORMAL)) {
			cal.add(Calendar.DATE,QuoteSBConstant.QUOTE_EXPIRY_DATE_SHIFT_NORMAL);
		} else if (materialType.equals(QuoteSBConstant.MATERIAL_TYPE_PROGRAM)) {
			cal.add(Calendar.DATE,QuoteSBConstant.QUOTE_EXPIRY_DATE_SHIFT_PROGRAM);
		}

		cal.set(Calendar.HOUR_OF_DAY, 15);
		return cal.getTime();
	}
	
	
	private String getGroupKey(QuoteBuilderBean bean,String region) {
		return region + "_" + bean.getSalesEmpCode() + "_" + bean.getSoldToCode();
	}
	
	
	
	@SuppressWarnings("rawtypes")
	private void sendSuccessEmail(List<QuoteBuilderBean> beans,String quoteCostType,User user) throws WebQuoteException {
		int errorCount=(int) beans.stream().filter(bean -> bean.getErrorFlag()== true).count();
		int passCount =beans.size() -errorCount;
		LOGGER.log(Level.INFO, "<<<<QuoteBuilderSB buildQuote Send Success Email,Quote Cost Type: " + quoteCostType);
		List beanList = convertQuoteBuilderBeanToTemplateBean(beans, quoteCostType);
		String filePath = getTemplateFilePath(quoteCostType);
		SXSSFWorkbook wb = forGenerateExcelFile(beanList, filePath, quoteCostType, false);
		String subject = "WQ Quote Builder Upload Results";
		String emailbody = getSuccessEmailBody().replace("{salesperson_name}", user.getName())
				.replace("{success_count}", ("" + beanList.size()))
				.replace("{fail_count}", ("" + errorCount))
				.replace("{Pass_count}", ("" + passCount))
				.replace("{region}", user.getDefaultBizUnit().getName());
		sendAttachmentEmail(wb, subject, emailbody, user, quoteCostType, false);
		LOGGER.log(Level.INFO, "<<<<QuoteBuilderSB buildQuote Send Success Email End.");
	}
	
	private String getTemplateFilePath(String quoteCostType) {
		String filePath = "";
		String tempPath = DeploymentConfiguration.configPath;
		//tempPath = "C:/Lenon/temp";
		tempPath = "C:/temp";
		if(QuoteConstant.SALES_COST.equals(quoteCostType)) {
			filePath = tempPath + File.separator + QuoteConstant.SALES_COST_TEMPLATE;
		}else {
			filePath = tempPath + File.separator + QuoteConstant.NON_SALES_COST_TEMPLATE;
		}
		return filePath;
	}
	
	

	@SuppressWarnings("rawtypes")
	private void sendFailEmail(List<QuoteBuilderBean> beans,String quoteCostType,User user) throws WebQuoteException {
		LOGGER.log(Level.INFO, "<<<<QuoteBuilderSB buildQuote Start Send Fail Email,Quote Cost Type: " + quoteCostType);
		List beanList = convertQuoteBuilderBeanToTemplateBean(beans, quoteCostType);
		String filePath = getTemplateFilePath(quoteCostType);
		SXSSFWorkbook wb = forGenerateExcelFile(beanList, filePath, quoteCostType, true);
		String subject = "WQ Quote Builder Upload Results (Fail)";
		String emailbody = getFailEmailBody().replace("{salesperson_name}", user.getName()).replace("{region}", user.getDefaultBizUnit().getName());
		sendAttachmentEmail(wb, subject, emailbody, user, quoteCostType, true);
		LOGGER.log(Level.INFO, "<<<<QuoteBuilderSB buildQuote Send Fail Email End.");
	}
	
	private void sendQuoteEmail(Quote quote, User user) {
		if (quote != null) {
			String soldToCode = quote.getQuoteItems().get(0).getSoldToCustomer().getCustomerNumber();
			QuotationEmailVO vo = new QuotationEmailVO();
			vo.setFormNumber(quote.getFormNumber());
			vo.setQuoteId(quote.getId());
			Customer customer = customerSB.findByPK(soldToCode);
			//vo.setHssfWorkbook(ejbCommonSB.getQuoteTemplateBySoldTo(this, customer, quote));
			vo.setQuote(quote);
			vo.setSoldToCustomer(customer);
			vo.setSubmissionDateFromQuote(true);
			vo.setRecipient(quote.getSales().getName());
			String regionCodeName = quote.getBizUnit().getName();
			vo.setSender(
					sysMaintSB.getEmailSignName(regionCodeName) + "<br/>" + sysMaintSB.getEmailHotLine(regionCodeName)
							+ "<br/>Email Box: " + sysMaintSB.getEmailSignContent(regionCodeName) + "<br/>");
			vo.setFromEmail(sysMaintSB.getEmailAddress(regionCodeName));

			String fullCustomerName = customer.getCustomerFullName();
			vo = ejbCommonSB.commonLogicForDpRFQandRFQ(quote, vo, customer, user, fullCustomerName);
			//ejbCommonSB.sendQuotationEmail(vo);
			
			/*SendQuotationEmailStrategy strategy = (SendQuotationEmailStrategy) cacheUtil.getStrategy(SendQuotationEmailStrategy.class,
					quote.getBizUnit().getName());*/
			
			SendQuotationEmailStrategy strategy =(SendQuotationEmailStrategy) StrategyFactory.getSingletonFactory()
					.getStrategy(SendQuotationEmailStrategy.class,quote.getBizUnit().getName(),
							this.getClass().getClassLoader());
			strategy.sendQuotationEmail(vo);
		}

	}
	
	public  String getFailEmailBody(){
		return "Dear {salesperson_name}," + "<br/>" + "<br/>" + "<br/>"+" The upload is unsuccessful.  Please amend the error quotes in the Fail Quote Report as attached "
				+ "<br/>"+ "and upload the quote file again." + "<br/>" + "<br/>" + "Best Regards," + "<br/>"
				+ "{region} Quote Centre";
	}
	
	public  String getSuccessEmailBody(){
		return "Dear {salesperson_name}," + "<br/>" + "<br/>" + "<br/>"+" The results of the upload is as attached."
				+ "<br/>" + "Number of Quotes processed: {success_count}"
				+ "<br/>" + "Pass Quotes: {Pass_count}"
				+ "<br/>" + "Fail Quotes: {fail_count}"
				+ "<br/>" + "<br/>" + "Best Regards," + "<br/>"
				+ "{region} Quote Centre";
	}
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List convertQuoteBuilderBeanToTemplateBean(List<QuoteBuilderBean> beans,String quoteCostType) {
		QuoteBuilderBean.sortByErrorMsg(beans);
		List list = new ArrayList();
		try {
			if(QuoteConstant.SALES_COST.equals(quoteCostType)) {
				for(QuoteBuilderBean bean : beans) {
					QuoteBuilderSalesCostBean salesCostBean = new QuoteBuilderSalesCostBean();
					BeanUtils.copyProperties(salesCostBean, bean);
					list.add(salesCostBean);
				}
			}else {
				for(QuoteBuilderBean bean : beans) {
					QuoteBuilderNonSalesCostBean nonSalesCostBean = new QuoteBuilderNonSalesCostBean();
					BeanUtils.copyProperties(nonSalesCostBean, bean);
					list.add(nonSalesCostBean);
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE,"Exception convert Beans: " + beans.get(0).getClass().getName());
		} 
		return list;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean validateMandatoryColumn(String quoteCostType,Object bean) throws Exception {
		boolean hasEmptyColumn = false;
		List<ExcelAliasBean> excelAliasTreeSet = null;
		if(QuoteConstant.SALES_COST.equals(quoteCostType)) {
			excelAliasTreeSet = getQuoteBuilderExcelAliasAnnotation(QuoteBuilderSalesCostBean.class);
		}else {
			excelAliasTreeSet = getQuoteBuilderExcelAliasAnnotation(QuoteBuilderNonSalesCostBean.class);
		}
		Class clazz = bean.getClass();
		for(ExcelAliasBean excelAliasBean : excelAliasTreeSet) {
			String fieldName = excelAliasBean.getFieldName();
			String mandatory = excelAliasBean.getMandatory();
			if(StringUtils.isNotBlank(mandatory) 
					&& StringUtils.equalsIgnoreCase("true", mandatory)) {
				fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1, fieldName.length());
				String methodName = "get" + fieldName;
				methodName = methodName.trim();
				Method m = clazz.getDeclaredMethod(methodName, new Class[] {});
				String value =  (String)m.invoke(bean, new Object[] {});
				if(StringUtils.isBlank(value)) {
					hasEmptyColumn = true;
					break;
				}
			}
			
	
		}
		return hasEmptyColumn;
	}
	
	public boolean isValidSalesCostPart(String mfr,String partNumber, String region) {
		boolean isValid = false;
		Material material = materialSB.find(mfr, partNumber);
		if(material != null) {
			List<MaterialRegional> mRegionals = material.getMaterialRegionals();
			if(mRegionals != null && mRegionals.size() > 0) {
				for(MaterialRegional mr : mRegionals) {
//					if(mr.isSalesCostFlag()) {
//						isValid = true;
//						break;
//					}
					if(mr.getBizUnit().getName().equalsIgnoreCase(region)){
						if(mr.isSalesCostFlag()) {
							isValid = true;
							break;
						}
					}
				}
			}
		}
		return isValid;
	}
	
	public boolean isValidCostIndicator(String costIndicator) {
		CostIndicator costInd= costIndicatorSB.findCostIndicator(costIndicator);
		return (costInd != null);
	}
	
	public boolean isValidCustGroup(String custGroupId,String region,String soldToCode,String endCust) {
		return sapDpaCustSB.isValidCustomerGroup(custGroupId, region, soldToCode, endCust);
	}
	
	
	private boolean validateSalesRegion(String salesPersonId,String beanRegion) {
		boolean isInvalid = false;
		
		User saleUser = getSales(salesPersonId);
		if(saleUser != null) {
			List<BizUnit> salesBizUnits = saleUser.getBizUnits();
			for(BizUnit region : salesBizUnits) {
				if(StringUtils.isNotBlank(beanRegion)) {
					if(beanRegion.equals(region.getName())) {
						isInvalid = true;
						break;
					}
				}
			}
		}
		return isInvalid;
	}
	
	private boolean validateUserAccess(String region, User user) {
		boolean hasAccess = false;
		List<BizUnit> bizUnits = user.getBizUnits();
		for(BizUnit bizunit : bizUnits) {
			if(StringUtils.equals(region, bizunit.getName())) {
				hasAccess = true;
				break;
			}
		}
		
		return hasAccess;
	}
	
	public User getSales(String userEmpId) {
		User user = userSB.findByEmployeeIdLazily(userEmpId);
		return user;
	}
	
	private boolean validateSoldToCust(String custNo, String region) {
		//LOGGER.info(" ******validate Customer ******* Customer Number >>>>== " + custNo);
		if (StringUtils.isNotBlank(custNo)) {
			List<String> accountGroups = new ArrayList<String>();
			accountGroups.add(QuoteSBConstant.ACCOUNT_GROUP_SOLDTO);
			accountGroups.add(QuoteSBConstant.ACCOUNT_GROUP_PROSPECTIVE_CUSTOMER);
			accountGroups.add(QuoteSBConstant.ACCOUNT_GROUP_CPD);
			BizUnit bizUnit = bizUnitSB.findByPk(region);
			List<Customer> customers = customerSB.findCustomerByCustomerNumber(custNo, null, accountGroups,bizUnit, 0, 100);
			Customer customer = null;
			if (customers != null && customers.size() > 0) {
				customer = customers.get(0);
				if (!(customer.getDeleteFlag() != null && customer.getDeleteFlag().booleanValue())) {
					boolean isInvalidCustomer = customerSB.isInvalidCustomer(custNo);
					return !isInvalidCustomer;
				}

			}
		}
		return false;
	}
	
	private static final String ERROR_COLUMN = "ERROR";
	
	private static final String FORM_NUMBER_COLUMN = "Form Number";
	
	private static final String QUOTE_NUMBER_COLUMN = "Quote Number";
	
	@SuppressWarnings("rawtypes")
	private XSSFWorkbook generateExcelFile(List beans,String templateFile,String quoteCostType,boolean isFailReport) throws WebQuoteException{
		XSSFWorkbook wb = null;
		try {
			FileInputStream inputStream = new FileInputStream(templateFile);
			wb = new XSSFWorkbook(inputStream);
			inputStream.close();
			XSSFSheet sh = wb.getSheetAt(0);
			Row firstRow = sh.getRow(sh.getFirstRowNum() + 1);
			Cell firstRowlastCell = firstRow.getCell(firstRow.getLastCellNum()-1);
			CellStyle headerstyle1 = firstRowlastCell.getCellStyle();
			Row headerRow = sh.getRow(sh.getFirstRowNum() + 2);
			CellStyle headerstyle2 = firstRowlastCell.getCellStyle();
			
			Row dataRow = sh.getRow(sh.getFirstRowNum() + 3);
			Cell lastDataCell = dataRow.getCell(dataRow.getLastCellNum()-1);
			CellStyle dataStyle = lastDataCell.getCellStyle();
			
			if (isFailReport) {
				Cell c1 = firstRow.createCell(firstRow.getLastCellNum());
				c1.setCellStyle(headerstyle1);
				c1.setCellValue("");
				Cell errorCell = headerRow.createCell(headerRow.getLastCellNum());
				errorCell.setCellStyle(headerstyle2);
				sh.setColumnWidth(headerRow.getLastCellNum(), 23000);
				errorCell.setCellValue(ERROR_COLUMN);
			} else {
				int idx = firstRow.getLastCellNum();
				Cell c1 = firstRow.createCell(idx);
				c1.setCellStyle(headerstyle1);
				c1.setCellValue("");
				Cell c2 = firstRow.createCell(idx + 1);
				c2.setCellStyle(headerstyle1);
				c2.setCellValue("");
				int headerLastInx = headerRow.getLastCellNum();
				Cell formNumberCell = headerRow.createCell(headerLastInx);
				formNumberCell.setCellStyle(headerstyle2);
				formNumberCell.setCellValue(FORM_NUMBER_COLUMN);
				
				Cell quoteNumberCell = headerRow.createCell(headerLastInx + 1);
				quoteNumberCell.setCellStyle(headerstyle2);
				quoteNumberCell.setCellValue(QUOTE_NUMBER_COLUMN);
			}
			int firstDataRow = sh.getFirstRowNum() + 3;
			if (beans != null && beans.size() > 0) {
				for (int idx = 0; idx < beans.size(); idx++) {
					Object bean = beans.get(idx);
					Row row = sh.createRow(firstDataRow );
					row = createRowData(sh,row,dataStyle, bean, quoteCostType, isFailReport);
					firstDataRow++;
				}
			}
			
			for (int colNum = 0; colNum < sh.getRow(0).getLastCellNum(); colNum++) {
				wb.getSheetAt(0).autoSizeColumn(colNum,true);
			}
			   
			
		} catch (Exception e) {
			throw new WebQuoteException(e);
		}
		return wb;
	}
	
	@SuppressWarnings("rawtypes")
	private SXSSFWorkbook forGenerateExcelFile(List beans,String templateFile,String quoteCostType,boolean isFailReport) throws WebQuoteException{
		SXSSFWorkbook wb = null;
		XSSFWorkbook wb1=null;
		try {
			FileInputStream inputStream = new FileInputStream(templateFile);
			XSSFWorkbook template = new XSSFWorkbook(inputStream);
			XSSFSheet sheetAt = template.getSheetAt(0);
			inputStream.close();
			 wb = new SXSSFWorkbook(template,-1);
		//	XSSFWorkbook
			 Sheet sh = wb.getSheetAt(0);
			 int lastRowIndex = sh.getLastRowNum();
		//	 Row firstRow = sh.createRow(lastRowIndex+1);
			 Row firstRow = sheetAt.getRow(sheetAt.getFirstRowNum() + 1);
			Cell firstRowlastCell = firstRow.getCell(firstRow.getLastCellNum()-1);
			CellStyle headerstyle1 = firstRowlastCell.getCellStyle();
			Row headerRow = sheetAt.getRow(sheetAt.getFirstRowNum() + 2);
			CellStyle headerstyle2 = firstRowlastCell.getCellStyle();
			
			Row dataRow = sheetAt.getRow(sheetAt.getFirstRowNum() + 3);
			Cell lastDataCell = dataRow.getCell(dataRow.getLastCellNum()-1);
			CellStyle dataStyle = lastDataCell.getCellStyle();
			
			if (isFailReport) {
				Cell c1 = firstRow.createCell(firstRow.getLastCellNum());
				c1.setCellStyle(headerstyle1);
				c1.setCellValue("");
				Cell errorCell = headerRow.createCell(headerRow.getLastCellNum());
				errorCell.setCellStyle(headerstyle2);
				sheetAt.setColumnWidth(headerRow.getLastCellNum(), 23000);
				errorCell.setCellValue(ERROR_COLUMN);
			} else {
				int idx = firstRow.getLastCellNum();
				Cell c1 = firstRow.createCell(idx);
				c1.setCellStyle(headerstyle1);
				c1.setCellValue("");
				Cell c2 = firstRow.createCell(idx + 1);
				c2.setCellStyle(headerstyle1);
				c2.setCellValue("");
				Cell c3 = firstRow.createCell(idx + 2);
				c3.setCellStyle(headerstyle1);
				c3.setCellValue("");
				sheetAt.setColumnWidth(headerRow.getLastCellNum(), 23000);
				int headerLastInx = headerRow.getLastCellNum();
				Cell formNumberCell = headerRow.createCell(headerLastInx);
				formNumberCell.setCellStyle(headerstyle2);
				formNumberCell.setCellValue(FORM_NUMBER_COLUMN);
				
				Cell quoteNumberCell = headerRow.createCell(headerLastInx + 1);
				quoteNumberCell.setCellStyle(headerstyle2);
				quoteNumberCell.setCellValue(QUOTE_NUMBER_COLUMN);
				Cell errorMsg = headerRow.createCell(headerLastInx + 2);
				errorMsg.setCellStyle(headerstyle2);
				errorMsg.setCellValue(ERROR_COLUMN);
			}
			int firstDataRow = sh.getFirstRowNum() + 3;
			if (beans != null && beans.size() > 0) {
				for (int idx = 0; idx < beans.size(); idx++) {
					Object bean = beans.get(idx);
					Row row = sheetAt.createRow(firstDataRow );
					row = createRowDatas(sh,row,dataStyle, bean, quoteCostType, isFailReport);
					firstDataRow++;
				}
			}
			
			for (int colNum = 0; colNum < sheetAt.getRow(0).getLastCellNum(); colNum++) {
				wb.getSheetAt(0).autoSizeColumn(colNum,true);
			}
			
			
		} catch (Exception e) {
			throw new WebQuoteException(e);
		}
		return wb;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Row createRowDatas(Sheet sh,Row row,CellStyle dataStyle,Object bean,String quoteCostType,boolean isFailReport) throws Exception {
		List<ExcelAliasBean> excelAliasTreeSet = null;
		if(QuoteConstant.SALES_COST.equals(quoteCostType)) {
			excelAliasTreeSet = getQuoteBuilderExcelAliasAnnotation(QuoteBuilderSalesCostBean.class);
		}else {
			excelAliasTreeSet = getQuoteBuilderExcelAliasAnnotation(QuoteBuilderNonSalesCostBean.class);
		}
		Class clazz = bean.getClass();
		for(ExcelAliasBean excelAliasBean : excelAliasTreeSet) {
			String fieldName = excelAliasBean.getFieldName();
			Integer cellIdx = excelAliasBean.getCellNum();
			fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1, fieldName.length());
			String methodName = "get" + fieldName;
			methodName = methodName.trim();
			Method m = clazz.getDeclaredMethod(methodName, new Class[] {});
			String value =  (String)m.invoke(bean, new Object[] {});
			Cell cell = row.createCell(cellIdx -1);
			cell.setCellStyle(dataStyle);
			cell.setCellValue(value);
		}
		if(isFailReport) {
			int lastCellIdx = row.getLastCellNum();
			Cell errorCell = row.createCell(lastCellIdx);
			sh.setColumnWidth(lastCellIdx, 23000);
			String errorMsgMethod = "getErrorMsg";
			Method m = clazz.getDeclaredMethod(errorMsgMethod, new Class[] {});
			String errorMsg =  (String)m.invoke(bean, new Object[] {});
			errorCell.setCellStyle(dataStyle);
			errorCell.setCellValue(errorMsg);
		}else {
			int lastCellIdx = row.getLastCellNum();
			Cell formNumberCell = row.createCell(lastCellIdx);
			Cell quoteNumberCell = row.createCell(lastCellIdx + 1);
			Cell errorMsgCell = row.createCell(lastCellIdx + 2);
			sh.setColumnWidth(lastCellIdx, 6000);
			sh.setColumnWidth(lastCellIdx + 1, 6000);
			sh.setColumnWidth(lastCellIdx + 2, 23000);
			formNumberCell.setCellStyle(dataStyle);
			quoteNumberCell.setCellStyle(dataStyle);
			errorMsgCell.setCellStyle(dataStyle);
			String formNumberMeghod = "getFormNumber";
			String quoteNumberMeghod = "getQuoteNumber";
			String errorMsgMethod = "getErrorMsg";
			Method m1 = clazz.getDeclaredMethod(formNumberMeghod, new Class[] {});
			Method m2 = clazz.getDeclaredMethod(quoteNumberMeghod, new Class[] {});
			Method m3 = clazz.getDeclaredMethod(errorMsgMethod, new Class[] {});
			String formNumber = (String)m1.invoke(bean, new Object[] {});
			String quoteNumber = (String)m2.invoke(bean, new Object[] {});
			String errorMsg = (String)m3.invoke(bean, new Object[] {});
			formNumberCell.setCellValue(formNumber);
			quoteNumberCell.setCellValue(quoteNumber);
			errorMsgCell.setCellValue(errorMsg);
		}
		return row;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Row createRowData(XSSFSheet sh,Row row,CellStyle dataStyle,Object bean,String quoteCostType,boolean isFailReport) throws Exception {
		List<ExcelAliasBean> excelAliasTreeSet = null;
		if(QuoteConstant.SALES_COST.equals(quoteCostType)) {
			excelAliasTreeSet = getQuoteBuilderExcelAliasAnnotation(QuoteBuilderSalesCostBean.class);
		}else {
			excelAliasTreeSet = getQuoteBuilderExcelAliasAnnotation(QuoteBuilderNonSalesCostBean.class);
		}
		Class clazz = bean.getClass();
		for(ExcelAliasBean excelAliasBean : excelAliasTreeSet) {
			String fieldName = excelAliasBean.getFieldName();
			Integer cellIdx = excelAliasBean.getCellNum();
			fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1, fieldName.length());
			String methodName = "get" + fieldName;
			methodName = methodName.trim();
			Method m = clazz.getDeclaredMethod(methodName, new Class[] {});
			String value =  (String)m.invoke(bean, new Object[] {});
			Cell cell = row.createCell(cellIdx -1);
			cell.setCellStyle(dataStyle);
			cell.setCellValue(value);
		}
		if(isFailReport) {
			int lastCellIdx = row.getLastCellNum();
			Cell errorCell = row.createCell(lastCellIdx);
			sh.setColumnWidth(lastCellIdx, 23000);
			String errorMsgMethod = "getErrorMsg";
			Method m = clazz.getDeclaredMethod(errorMsgMethod, new Class[] {});
			String errorMsg =  (String)m.invoke(bean, new Object[] {});
			errorCell.setCellStyle(dataStyle);
			errorCell.setCellValue(errorMsg);
		}else {
			int lastCellIdx = row.getLastCellNum();
			Cell formNumberCell = row.createCell(lastCellIdx);
			Cell quoteNumberCell = row.createCell(lastCellIdx + 1);
			sh.setColumnWidth(lastCellIdx, 6000);
			sh.setColumnWidth(lastCellIdx + 1, 6000);
			formNumberCell.setCellStyle(dataStyle);
			quoteNumberCell.setCellStyle(dataStyle);
			String formNumberMeghod = "getFormNumber";
			String quoteNumberMeghod = "getQuoteNumber";
			Method m1 = clazz.getDeclaredMethod(formNumberMeghod, new Class[] {});
			Method m2 = clazz.getDeclaredMethod(quoteNumberMeghod, new Class[] {});
			String formNumber = (String)m1.invoke(bean, new Object[] {});
			String quoteNumber = (String)m2.invoke(bean, new Object[] {});
			formNumberCell.setCellValue(formNumber);
			quoteNumberCell.setCellValue(quoteNumber);
		}
		return row;
	}
	
	
	private List<ExcelAliasBean> getQuoteBuilderExcelAliasAnnotation(Class<?> beanClass) {
		List<ExcelAliasBean> treeSet = new ArrayList<ExcelAliasBean>();

		Field[] fields = beanClass.getDeclaredFields();

		for (Field f : fields) {
			f.setAccessible(true);
			QuoteBuilderExcelAlias annontation = f.getAnnotation(QuoteBuilderExcelAlias.class);
			if (annontation == null)
				continue;
			ExcelAliasBean bean = new ExcelAliasBean();
			bean.setAliasName(annontation.name());
			bean.setCellNum(parseStringToInteger(annontation.cellNum()));
			bean.setFieldName(f.getName());
			bean.setMandatory(annontation.mandatory());
			treeSet.add(bean);
		}
		Collections.sort(treeSet, new Comparator<ExcelAliasBean>() {
			@Override
			public int compare(ExcelAliasBean f1, ExcelAliasBean f2) {
				if (f1.getCellNum() > f2.getCellNum()) {
					return 1;
				} else if (f1.getCellNum() == f2.getCellNum()) {
					return 0;
				} else {
					return -1;
				}
			}
		});
		return treeSet;
	}
	
	private void sendAttachmentEmail(SXSSFWorkbook workbook, String subject, String emailbody, User user, String quoteCostType,boolean isFailEmail) {
		File xlsFile = null;
		try {
			MailInfoBean mib = new MailInfoBean();
			List<String> toList = new ArrayList<String>();
			toList.add(user.getEmailAddress());
			mib.setMailSubject(subject);
			mib.setMailContent(emailbody);
			mib.setMailTo(toList);
			mib.setMailFrom("Webquote@Avnet.com");
			//mib.setMailFrom("Vincent.Wang@AVNET.COM");
			//mib.setMailFrom("Lenon.Yang@avnet.com");
			FileOutputStream fileOut = null;
			String tempPath = sysConfigSB.getProperyValue(QuoteConstant.TEMP_DIR);
			//tempPath = "C:/Lenon/temp/";
			tempPath = "C:\\Users\\046755\\Desktop\\excel_quoteBulider\\";
			String fileName = generateReportName(quoteCostType,isFailEmail,user);
			String xlsFileName = tempPath + fileName + ".xlsx";
			xlsFile = new File(xlsFileName);
			fileOut = new FileOutputStream(xlsFile);
			workbook.write(fileOut);
			fileOut.flush();
			fileOut.close();
			mib.setZipFile(xlsFile);
			mailUtilsSB.sendAttachedMail(mib);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception occurred for email subject: "+subject+", Send email failed!" +e.getMessage(), e);
		}finally {
			if(xlsFile != null && xlsFile.exists()) {
				xlsFile.delete();
			}
		}
	}
	
	private String generateReportName(String quoteCostType,boolean isFailEmail,User user) {
		DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String endName = user.getEmployeeId() + "_" + df.format(new Date());
		String reportName = QuoteConstant.SUCCESS_SALES_COST_FILE + "_" + endName;
		if(isFailEmail) {
			if(QuoteConstant.SALES_COST.equals(quoteCostType)) {
				reportName = QuoteConstant.FAIL_SALES_COST_FILE + "_" + endName;
			}else {
				reportName = QuoteConstant.FAIL_NON_SALES_COST_FILE + "_" +endName;
			}
		}else {
			if(!QuoteConstant.SALES_COST.equals(quoteCostType)) {
				reportName = QuoteConstant.SUCCESS_NON_SALES_COST_FILE + "_" + endName;
			}
		}
		return reportName;
	}
	
	
	/*private boolean isExpiredDate(Date validity) {
		Date currentDate = QuoteUtil.getCurrentDateZeroHour();
		return (validity != null && (validity.compareTo(currentDate) < 0));
	}*/
	
	private Date parseStringToDate(String effectiveDate) {
		Date d = null;
		try {
			if(StringUtils.isNotBlank(effectiveDate)) {
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				df.setLenient(false);
				d = df.parse(effectiveDate);
			}
		} catch (ParseException e) {
		}
		return d;
	}
	
	private boolean isNumber(String str) {
/*		final String numberStr = "1234567890.";
		if (StringUtils.isNotBlank(str)) {
			str = str.replaceAll("%", "");
			if (str != null && str.length() > 0) {
				for (int i = 0; i < str.length(); i++) {
					if (numberStr.indexOf(str.charAt(i)) == -1) {
						return false;
					}
				}
			}
		}
		return true;*/
		//Fixed by DamonChen@20181226
		
/*		Double.parseDouble(str);
		if(StringUtils.isBlank(str)){
			return false;
		}
		String reg = "^[0-9]+(.[0-9]+)?$";
		return str.matches(reg);*/

		try {
			Double.parseDouble(str);
			return true;
		} catch (Exception e) {

			return false;
		}
	}
	
	private boolean isValidPriceValidity(String priceValidity) {
		boolean isValid = true;
		if(StringUtils.isNotBlank(priceValidity)) {
			if(!isNumber(priceValidity)) {
				try {
					DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
					df.setLenient(false);
					df.parse(priceValidity);
				} catch (ParseException e) {
					isValid = false;
				}
			}
		
		}
		return isValid;
	}
	
	
	private Integer parseStringToInteger(String numberText) {
		if(StringUtils.isNotBlank(numberText) && isNumber(numberText)) {
			return Integer.parseInt(numberText);
		}
		return null;
	}
	
	private Double parseStringToDouble(String numberText) {
		if(StringUtils.isNotBlank(numberText) && isNumber(numberText)) {
			return Double.parseDouble(numberText);
		}
		return null;
	}
	
	private BigDecimal parseStringToBigdecimal(String numberText) {
		if(StringUtils.isNotBlank(numberText) && isNumber(numberText)) {
			return new BigDecimal(numberText);
		}
		return null;
	}
	
	private Boolean parseStringToBoolean(String boolStr) {
		if(StringUtils.isNotBlank(boolStr)) {
			if("YES".equalsIgnoreCase(boolStr)) {
				return true;
			}else if("NO".equalsIgnoreCase(boolStr)) {
				return false;
			}
		}
		return null;
	}
	
	
	private String getLoclaizedText(String key, Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(DBResourceBundle.class.getName(),locale);
		return bundle.getString(key);

	}
	
	private boolean isValidMfr(String mfr) {
		
		boolean isValid = false;
		if (StringUtils.isNotBlank(mfr)){
			Manufacturer mfnufacturer = manufacturerSB.findManufacturerByName(mfr);
			if(mfnufacturer != null) {
				isValid = true;
			}	
		}
		
		return isValid;
	}
	
	

	/**
	 * @throws WebQuoteException    
	* @Description: valify the customer based on restricted_customer_mapping table
	* @author 042659
	* @param rfqItems
	* @param locale
	* @param bizUnit       
	* @return void    
	* @throws  
	*/  
	public void validateRestrictedCustomer(List<QuoteBuilderBean> quoteBuilderBeans, Locale locale) throws WebQuoteException {
		// TODO Auto-generated method stub
		List<RestrictedCustomerMappingSearchCriteria> restrictedCustomerCriteriaList = new ArrayList<RestrictedCustomerMappingSearchCriteria>();
		List<RestrictedCustomerMappingParameter> restrictedCustomerMappingParamList = new ArrayList<RestrictedCustomerMappingParameter>();
		for (QuoteBuilderBean qbb : quoteBuilderBeans) {
			RestrictedCustomerMappingSearchCriteria rcc = new RestrictedCustomerMappingSearchCriteria();
			rcc.setBizUnit(qbb.getRegion());
			rcc.setMfrKeyword(qbb.getMfr());
			//rcc.setSoldToCodeKeyword(qbb.getSoldToCode());
			restrictedCustomerCriteriaList.add(rcc);
			
			RestrictedCustomerMappingParameter rcp = new RestrictedCustomerMappingParameter();
			rcp.setMfr(qbb.getMfr());
			rcp.setSoldToCustomerNumber(qbb.getSoldToCode());
			rcp.setBizUnit(qbb.getRegion());
			rcp.setEndCustomerCode(qbb.getEndCustCode());
			rcp.setRequiredPartNumber(qbb.getQuotePartNumber());
			rcp.setProductGroup1Name(null);
			rcp.setProductGroup2Name(null);
			rcp.setProductGroup3Name(null);
			rcp.setProductGroup4Name(null);
			restrictedCustomerMappingParamList.add(rcp);
			
			
		}

		restrictedCustomerSB.validateRestrictedCustList(restrictedCustomerMappingParamList, locale, restrictedCustomerCriteriaList);
	}
}
