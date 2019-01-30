package com.avnet.emasia.webquote.utilites.web.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;

import com.avnet.emasia.webquote.commodity.bean.PricerUploadTemplateBean;
import com.avnet.emasia.webquote.commodity.bean.ProgramItemUploadCounterBean;
import com.avnet.emasia.webquote.commodity.constant.PRICER_TYPE;
import com.avnet.emasia.webquote.commodity.constant.PricerConstant;
import com.avnet.emasia.webquote.commodity.constant.QtyIndicatorEnum;
import com.avnet.emasia.webquote.commodity.util.PricerUtils;
import com.avnet.emasia.webquote.dp.EJBCommonSB;
import com.avnet.emasia.webquote.entity.Currency;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.SalesCostType;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.common.CacheUtil;
import com.avnet.emasia.webquote.utilities.bean.ExcelAliasBean;
import com.avnet.emasia.webquote.utilities.bean.MailInfoBean;
import com.avnet.emasia.webquote.web.quote.cache.MfrCacheManager;

public class PricerUploadHelper {
	
	
	//public static final String ZINM = SalesCostType.ZINM.toString();
	//Hql
	public static final String[] FORZINMSCTYPE = {"shipmentValidity","qtyControl"};
	//public static final String salesCos
	static final Logger LOGGER=Logger.getLogger(PricerUploadHelper.class.getSimpleName());
	private  static  List<ExcelAliasBean> excelAliasTreeSet = POIUtils.getPricerExcelAliasAnnotation(PricerUploadTemplateBean.class);
	
	public static  List<ExcelAliasBean> getPricerExcelAliasAnnotation(){
		if(excelAliasTreeSet==null){
			excelAliasTreeSet  = POIUtils.getPricerExcelAliasAnnotation(PricerUploadTemplateBean.class);		
		}
		return excelAliasTreeSet;
	}
	
	public static HashMap<String, Object> getPricerCache(User user){
		HashMap<String,Object> hashMap = new HashMap<String,Object>();
		
		//Bryan Start
		//List<Manufacturer> mfrCache = MfrCacheManager.getMfrListByBizUnit(user.getDefaultBizUnit().getName());
		CacheUtil cacheUtil = null;
		try{
    		Context ctx = new InitialContext();
    		cacheUtil = (CacheUtil) ctx.lookup("java:app/WQ_Web/CacheUtil");
    	
    	}catch(Exception e){
    		e.printStackTrace();
    	}
		List<Manufacturer> mfrCache = cacheUtil.getMfrListByBizUnit(user.getDefaultBizUnit().getName());
		//Bryan End
		hashMap.put("mfr", mfrCache);
		return hashMap ;
	}
	public static String validateActionAndPricerType(PricerUploadTemplateBean bean){
		StringBuffer validateMsg = new StringBuffer();
		//String action = bean.getAction();
		String pricerType = bean.getPricerType();
		
/*		if (QuoteUtil.isEmpty(action)) {
			validateMsg.append("Row ["+bean.getLineSeq()+"] :you must select fields of action, <br/>");
			bean.setErr(true);
		}*/
		
		if (QuoteUtil.isEmpty(pricerType)) {   
			validateMsg.append(ResourceMB.getParameterizedText("wq.error.pricerTypeError", String.valueOf(bean.getLineSeq()))+", <br/>");
			bean.setErr(true);
		}

		return validateMsg.toString();
	}
	
	
	private static Boolean ForZINMvalidateMandatoryFields(PricerUploadTemplateBean bean, ExcelAliasBean excelAliasBean)
	{
		if(SalesCostType.ZINM.toString().equals(bean.getSalesCostType()))
		{
			for(String fieldName : FORZINMSCTYPE ){
				if(fieldName.equals(excelAliasBean.getFieldName())){
					return true;
				}
			}
		}
		return false;
	}
	
	public static String validateMandatoryFields(PricerUploadTemplateBean bean){
		Iterator<ExcelAliasBean> iterator =  getPricerExcelAliasAnnotation().iterator();
		ExcelAliasBean excelAliasBean = null;
		StringBuffer sb = new StringBuffer();
		PRICER_TYPE pricerType = PRICER_TYPE.getPricerTypeByName(bean.getPricerType());
		Boolean isNeedCheck = false;
		
		while (iterator.hasNext()) {			
			excelAliasBean = iterator.next();
			isNeedCheck = false;
			switch(pricerType){
			case NORMAL:
				isNeedCheck = excelAliasBean.isNormalMandatory();
				break;
			case PROGRAM:
				isNeedCheck = excelAliasBean.isProgramMandatory();
				break;
			case CONTRACT:
				isNeedCheck = excelAliasBean.isContractMandatory();
				break;
			case PARTMASTER:
				isNeedCheck = excelAliasBean.isPastMasterMandatory();
				break;
			case SIMPLE:
				isNeedCheck = excelAliasBean.isSimplePricer();
				break;
			case SALESCOST:
				isNeedCheck = excelAliasBean.isSalesCostPricer();
				if(!isNeedCheck){
					isNeedCheck = ForZINMvalidateMandatoryFields(bean,excelAliasBean);
				}
				break;
			default:
				break;
			}
			if(isNeedCheck){
				if (QuoteUtil.isEmpty(POIUtils.getter(bean, excelAliasBean.getFieldName()))) {
					sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] :"+ResourceMB.getText("wq.error.enterMandatoryFields")+" "+ResourceMB.getText(excelAliasBean.getAliasName())+", <br/>");
					bean.setErr(true);
				}
			}
		}
		return sb.toString();
	}
	public static String validateMandatoryFields_old(PricerUploadTemplateBean bean){
		Iterator<ExcelAliasBean> iterator =  getPricerExcelAliasAnnotation().iterator();;
		ExcelAliasBean excelAliasBean = null;
		StringBuffer sb = new StringBuffer();
		String pricerType = bean.getPricerType();
		boolean isNormalPricer = PRICER_TYPE.NORMAL.getName().equalsIgnoreCase(pricerType );
		boolean isProgramPricer = PRICER_TYPE.PROGRAM.getName().equalsIgnoreCase(pricerType );
		boolean isContractPricer = PRICER_TYPE.CONTRACT.getName().equalsIgnoreCase(pricerType );
		boolean isPastMaster = PRICER_TYPE.PARTMASTER.getName().equalsIgnoreCase(pricerType );
		boolean isSimplePricer = PRICER_TYPE.SIMPLE.getName().equalsIgnoreCase(pricerType );
		boolean isSalesCostPricer = PRICER_TYPE.SALESCOST.getName().equalsIgnoreCase(pricerType );
		//PRICER_TYPE pricerType
		
		while (iterator.hasNext()) {			
			excelAliasBean = iterator.next();
			
			if(isNormalPricer&&excelAliasBean.isNormalMandatory()){ 
				if (QuoteUtil.isEmpty(POIUtils.getter(bean, excelAliasBean.getFieldName()))) {
					sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] :"+ResourceMB.getText("wq.error.enterMandatoryFields")+" "+ResourceMB.getText(excelAliasBean.getAliasName())+", <br/>");
					bean.setErr(true);
				}
			}
			
			if(isProgramPricer&&excelAliasBean.isProgramMandatory()){
				if (QuoteUtil.isEmpty(POIUtils.getter(bean, excelAliasBean.getFieldName()))) {
					sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] :"+ResourceMB.getText("wq.error.enterMandatoryFields")+" "+ResourceMB.getText(excelAliasBean.getAliasName())+", <br/>");
					bean.setErr(true);
				}
			}
						
			if(isContractPricer&&excelAliasBean.isContractMandatory()){
				if (QuoteUtil.isEmpty(POIUtils.getter(bean, excelAliasBean.getFieldName()))) {
					sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] :"+ResourceMB.getText("wq.error.enterMandatoryFields")+" "+ResourceMB.getText(excelAliasBean.getAliasName())+", <br/>");
					bean.setErr(true);
				}
			}
			
			if(isPastMaster&&excelAliasBean.isPastMasterMandatory()){
				if (QuoteUtil.isEmpty(POIUtils.getter(bean, excelAliasBean.getFieldName()))) {
					sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] :"+ResourceMB.getText("wq.error.enterMandatoryFields")+" "+ResourceMB.getText(excelAliasBean.getAliasName())+", <br/>");
					bean.setErr(true);
				}
			}
			if(isSimplePricer&&excelAliasBean.isSimplePricer()){
				if (QuoteUtil.isEmpty(POIUtils.getter(bean, excelAliasBean.getFieldName()))) {
					sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] :"+ResourceMB.getText("wq.error.enterMandatoryFields")+" "+ResourceMB.getText(excelAliasBean.getAliasName())+", <br/>");
					bean.setErr(true);
				}
			}
			if(isSalesCostPricer&&excelAliasBean.isSalesCostPricer()){
				if (QuoteUtil.isEmpty(POIUtils.getter(bean, excelAliasBean.getFieldName()))) {
					sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] :"+ResourceMB.getText("wq.error.enterMandatoryFields")+" "+ResourceMB.getText(excelAliasBean.getAliasName())+", <br/>");
					bean.setErr(true);
				}
			}
		}
		return sb.toString();
	}
	
	public static String validateFieldType(PricerUploadTemplateBean bean){
		StringBuffer sb = new StringBuffer();
		if (!QuoteUtil.isEmpty(bean.getCost())&&!PricerUtils.isDecimal(bean.getCost())) {	 	  	
			sb.append(ResourceMB.getParameterizedText("wq.error.costNumeric", String.valueOf(bean.getLineSeq()))+",<br/> ");	
			bean.setErr(true);
		}

		if (!QuoteUtil.isEmpty(bean.getMinSell())&&!PricerUtils.isDecimal(bean.getMinSell())) {	    		
			sb.append(ResourceMB.getParameterizedText("wq.error.minSellNumeric", String.valueOf(bean.getLineSeq()))+",<br/> ");	
			bean.setErr(true);
		}

		if (!QuoteUtil.isEmpty(bean.getBottomPrice())&&!PricerUtils.isDecimal(bean.getBottomPrice())) {	     		
			sb.append(ResourceMB.getParameterizedText("wq.error.bottomPriceNumeric", String.valueOf(bean.getLineSeq()))+",<br/> ");	
			bean.setErr(true);
		}
		if (!QuoteUtil.isEmpty(bean.getMPQ())&&!PricerUtils.isInteger(bean.getMPQ())) {	 		  
			sb.append(ResourceMB.getParameterizedText("wq.error.MPQNumeric", String.valueOf(bean.getLineSeq()))+", <br/>");	
			bean.setErr(true);
		}
		
		if (!QuoteUtil.isEmpty(bean.getMOQ())&&!PricerUtils.isInteger(bean.getMOQ())) {		   	 
			sb.append(ResourceMB.getParameterizedText("wq.error.MOQNumeric", String.valueOf(bean.getLineSeq()))+", <br/>");	
			bean.setErr(true);
		}
		
		if (!QuoteUtil.isEmpty(bean.getMOV())&&!PricerUtils.isInteger(bean.getMOV())) {	 	  	
			sb.append(ResourceMB.getParameterizedText("wq.error.MOVNumeric", String.valueOf(bean.getLineSeq()))+", <br/>");	
			bean.setErr(true);
		}
		
		if (!QuoteUtil.isEmpty(bean.getQtyControl())&&!PricerUtils.isInteger(bean.getQtyControl())) {	 	  	
			sb.append(ResourceMB.getParameterizedText("wq.error.zINMControlQty", String.valueOf(bean.getLineSeq()))+", <br/>");	
			bean.setErr(true);
		}
		
		if (!QuoteUtil.isEmpty(bean.getQuantityBreakPrice1())&&!PricerUtils.isValidQuantityBreakPricer(bean.getQuantityBreakPrice1())) {			
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] : "+ResourceMB.getParameterizedText("wq.error.quantityBreakPriceNumeric", "1")+", <br/>");	
			bean.setErr(true);
		}
		if (!QuoteUtil.isEmpty(bean.getQuantityBreak1())&&!PricerUtils.isValidQuantityBreak(bean.getQuantityBreak1())) {			
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] : "+ResourceMB.getParameterizedText("wq.error.quantityBreakPricePositive", "1")+",<br/> ");	
			bean.setErr(true);
		}
		if (!QuoteUtil.isEmpty(bean.getQuantityBreakPrice2())&&!PricerUtils.isValidQuantityBreakPricer(bean.getQuantityBreakPrice2())) {			
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] : "+ResourceMB.getParameterizedText("wq.error.quantityBreakPriceNumeric", "2")+", <br/>");	
			bean.setErr(true);
		}
		if (!QuoteUtil.isEmpty(bean.getQuantityBreak2())&&!PricerUtils.isValidQuantityBreak(bean.getQuantityBreak2())) {			
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] : "+ResourceMB.getParameterizedText("wq.error.quantityBreakPricePositive", "2")+", <br/>");	
			bean.setErr(true);
		}
		if (!QuoteUtil.isEmpty(bean.getQuantityBreakPrice3())&&!PricerUtils.isValidQuantityBreakPricer(bean.getQuantityBreakPrice3())) {			
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] : "+ResourceMB.getParameterizedText("wq.error.quantityBreakPriceNumeric", "3")+",<br/> ");	
			bean.setErr(true);
		}
		if (!QuoteUtil.isEmpty(bean.getQuantityBreak3())&&!PricerUtils.isValidQuantityBreak(bean.getQuantityBreak3())) {			
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] : "+ResourceMB.getParameterizedText("wq.error.quantityBreakPricePositive", "3")+",<br/> ");	
			bean.setErr(true);
		}
		if (!QuoteUtil.isEmpty(bean.getQuantityBreakPrice4())&&!PricerUtils.isValidQuantityBreakPricer(bean.getQuantityBreakPrice4())) {			
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] : "+ResourceMB.getParameterizedText("wq.error.quantityBreakPriceNumeric", "4")+",<br/> ");	
			bean.setErr(true);
		}
		if (!QuoteUtil.isEmpty(bean.getQuantityBreak4())&&!PricerUtils.isValidQuantityBreak(bean.getQuantityBreak4())) {			
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] : "+ResourceMB.getParameterizedText("wq.error.quantityBreakPricePositive", "4")+", <br/>");	
			bean.setErr(true);
		}
		if (!QuoteUtil.isEmpty(bean.getQuantityBreakPrice5())&&!PricerUtils.isValidQuantityBreakPricer(bean.getQuantityBreakPrice5())) {			
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] : "+ResourceMB.getParameterizedText("wq.error.quantityBreakPriceNumeric", "5")+",<br/>");	
			bean.setErr(true);
		}
		if (!QuoteUtil.isEmpty(bean.getQuantityBreak5())&&!PricerUtils.isValidQuantityBreak(bean.getQuantityBreak5())) {			
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] : "+ResourceMB.getParameterizedText("wq.error.quantityBreakPricePositive", "5")+", <br/>");	
			bean.setErr(true);
		}
		if (!QuoteUtil.isEmpty(bean.getQuantityBreakSalesCost1())&&!PricerUtils.isValidQuantityCostOrResale(bean.getQuantityBreakSalesCost1())) {			
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] : "+ResourceMB.getParameterizedText("wq.error.quantityBreakSalesCostBigDecimal", "1")+",<br/> ");	
			bean.setErr(true);
		}
		if (!QuoteUtil.isEmpty(bean.getQuantityBreakSuggestedResale1())&&!PricerUtils.isValidQuantityCostOrResale(bean.getQuantityBreakSuggestedResale1())) {			
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] : "+ResourceMB.getParameterizedText("wq.error.SuggestedResaleBigDecimal", "1")+",<br/> ");	
			bean.setErr(true);
		}
		
		if (!QuoteUtil.isEmpty(bean.getQuantityBreakSalesCost2())&&!PricerUtils.isValidQuantityCostOrResale(bean.getQuantityBreakSalesCost2())) {			
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] : "+ResourceMB.getParameterizedText("wq.error.quantityBreakSalesCostBigDecimal", "2")+",<br/> ");	
			bean.setErr(true);
		}
		if (!QuoteUtil.isEmpty(bean.getQuantityBreakSuggestedResale2())&&!PricerUtils.isValidQuantityCostOrResale(bean.getQuantityBreakSuggestedResale2())) {			
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] : "+ResourceMB.getParameterizedText("wq.error.SuggestedResaleBigDecimal", "2")+",<br/> ");	
			bean.setErr(true);
		}
		if (!QuoteUtil.isEmpty(bean.getQuantityBreakSalesCost3())&&!PricerUtils.isValidQuantityCostOrResale(bean.getQuantityBreakSalesCost3())) {			
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] : "+ResourceMB.getParameterizedText("wq.error.quantityBreakSalesCostBigDecimal", "3")+",<br/> ");	
			bean.setErr(true);
		}
		if (!QuoteUtil.isEmpty(bean.getQuantityBreakSuggestedResale3())&&!PricerUtils.isValidQuantityCostOrResale(bean.getQuantityBreakSuggestedResale3())) {			
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] : "+ResourceMB.getParameterizedText("wq.error.SuggestedResaleBigDecimal", "3")+",<br/> ");	
			bean.setErr(true);
		}
		if (!QuoteUtil.isEmpty(bean.getQuantityBreakSalesCost4())&&!PricerUtils.isValidQuantityCostOrResale(bean.getQuantityBreakSalesCost4())) {			
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] : "+ResourceMB.getParameterizedText("wq.error.quantityBreakSalesCostBigDecimal", "4")+",<br/> ");	
			bean.setErr(true);
		}
		if (!QuoteUtil.isEmpty(bean.getQuantityBreakSuggestedResale4())&&!PricerUtils.isValidQuantityCostOrResale(bean.getQuantityBreakSuggestedResale4())) {			
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] : "+ResourceMB.getParameterizedText("wq.error.SuggestedResaleBigDecimal", "4")+",<br/> ");	
			bean.setErr(true);
		}
		if (!QuoteUtil.isEmpty(bean.getQuantityBreakSalesCost5())&&!PricerUtils.isValidQuantityCostOrResale(bean.getQuantityBreakSalesCost5())) {			
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] : "+ResourceMB.getParameterizedText("wq.error.quantityBreakSalesCostBigDecimal", "5")+",<br/> ");	
			bean.setErr(true);
		}
		if (!QuoteUtil.isEmpty(bean.getQuantityBreakSuggestedResale5())&&!PricerUtils.isValidQuantityCostOrResale(bean.getQuantityBreakSuggestedResale5())) {			
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] : "+ResourceMB.getParameterizedText("wq.error.SuggestedResaleBigDecimal", "5")+",<br/> ");	
			bean.setErr(true);
		}
		if (!QuoteUtil.isEmpty(bean.getAvailabletoSellQuantity())&&
				!PricerUtils.isIntegerIncluleNegative(bean.getAvailabletoSellQuantity())) {	 	  	
			sb.append(ResourceMB.getParameterizedText("wq.error.availableSellQuantity", String.valueOf(bean.getLineSeq()))+",<br/> ");	
			bean.setErr(true);
		}  																								
		if (!QuoteUtil.isEmpty(bean.getSpecialItemIndicator())&&!PricerUtils.isInteger(bean.getSpecialItemIndicator())) {			
			sb.append(ResourceMB.getParameterizedText("wq.error.specialItemIndicator", String.valueOf(bean.getLineSeq()))+", <br/>");	
			bean.setErr(true);
		}                                         						
		if (!QuoteUtil.isEmpty(bean.getDisplayOnTop())&&!PricerUtils.isInteger(bean.getDisplayOnTop())) {			
			sb.append(ResourceMB.getParameterizedText("wq.error.displayTopInteger", String.valueOf(bean.getLineSeq()))+",<br/> ");	
			bean.setErr(true);
		}                                                 
		if (!QuoteUtil.isEmpty(bean.getValidity())&&!(PricerUtils.isInteger(bean.getValidity())||PricerUtils.isDate(bean.getValidity()))) {			
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] : "+ResourceMB.getText("wq.error.validaityInt")+",<br/> ");	
			bean.setErr(true);
		}                                                   
		if (!QuoteUtil.isEmpty(bean.getShipmentValidity())&&!(PricerUtils.isDate(bean.getShipmentValidity()))) {			
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] : "+ResourceMB.getText("wq.error.shipmntValidityDate")+", <br/>");	
			bean.setErr(true);
		}                                    
		if (!QuoteUtil.isEmpty(bean.getProgramEffectiveDate())&&!PricerUtils.isDate(bean.getProgramEffectiveDate())) {			
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] : "+ResourceMB.getText("wq.error.progEffectiveDate")+",<br/> ");	
			bean.setErr(true);
		}                              
		if (!QuoteUtil.isEmpty(bean.getProgramClosingDate())&&!PricerUtils.isDate(bean.getProgramClosingDate())) {			
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] : "+ResourceMB.getText("wq.error.progClosingDate")+", <br/>");	
			bean.setErr(true);
		}
		
		if (!QuoteUtil.isEmpty(bean.getStartDate())&&!PricerUtils.isDate(bean.getStartDate())) {			
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] : "+ResourceMB.getText("wq.error.startDateError")+", <br/>");	
			bean.setErr(true);
		}
		
		if (!QuoteUtil.isEmpty(bean.getQuotationEffectiveDate())&&!PricerUtils.isDate(bean.getQuotationEffectiveDate())) {			
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] : "+ResourceMB.getText("wq.error.quoteEffectiveDate")+", <br/>");	
			bean.setErr(true);
		}
		
		if (!QuoteUtil.isEmpty(bean.getCurrency())&& !Currency.hasValue(bean.getCurrency())) {	
			//Row [x]: Must input a Buy Currency that is supported by your default region.
			//Row [x]: Must input a Buy Currency that is supported by your excel.
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] : "+ResourceMB.getText("wq.error.notCurrencyType")+", <br/>");	
			bean.setErr(true);
		}
		
		return sb.toString();
	}
	
	public static String validateLengthOfFields(PricerUploadTemplateBean bean){
		Iterator<ExcelAliasBean> iterator =  getPricerExcelAliasAnnotation().iterator();;
		ExcelAliasBean excelAliasBean = null;
		StringBuffer sb = new StringBuffer();
		String fieldValue = null;
		int fieldLength = -1;
		while (iterator.hasNext()) {			
			excelAliasBean = iterator.next();
			fieldValue = POIUtils.getter(bean, excelAliasBean.getFieldName());
			fieldLength = excelAliasBean.getFieldLength();      
			if(fieldValue!=null&&fieldLength!=-1&&fieldValue.length() >fieldLength ){//if fieldLength =-1  this field not need to verify  
				sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] : "+ResourceMB.getParameterizedText("wq.error.lenghtMaximumError"," "+ResourceMB.getText(excelAliasBean.getAliasName()))+" "+excelAliasBean.getFieldLength()+" "+ResourceMB.getText("wq.message.inMaximum")+", <br/>");	
				bean.setErr(true);
			}  
		}
		return sb.toString();
	}
	
	public static String validateValidity(PricerUploadTemplateBean bean){
		StringBuffer sb = new StringBuffer();
		if(bean!=null){
			
			if(PricerUtils.isDate(bean.getQuotationEffectiveDate())){
				if(PricerUtils.isDate(bean.getValidity())){
					Date validaty = PricerUtils.convertToDate(bean.getValidity());
					if(validaty.before(PricerUtils.convertToDate(bean.getQuotationEffectiveDate()))){  
						sb.append(ResourceMB.getParameterizedText("wq.error.mustValidateQuoteEffectiveDate", String.valueOf(bean.getLineSeq()))+",<br/>");	
						bean.setErr(true);
					}
				}
	
			}else if(QuoteUtil.isEmpty(bean.getQuotationEffectiveDate())){
				if(PricerUtils.isDate(bean.getValidity())){
					Date validaty = PricerUtils.convertToDate(bean.getValidity());
					if(validaty.before(PricerUtils.getTimesmorning(new Date()))){  
						sb.append(ResourceMB.getParameterizedText("wq.error.mustValidateQuoteEffectiveDate", String.valueOf(bean.getLineSeq()))+",<br/>");	
						bean.setErr(true);
					}
				}
			}

		}
		return sb.toString();
	}
	
	public static String validateValidityForEditPricer(PricerUploadTemplateBean bean){
		StringBuffer sb = new StringBuffer();
		if(bean!=null){
			
			if(PricerUtils.isDate(bean.getQuotationEffectiveDate())){
				if(PricerUtils.isDate(bean.getValidity())){
					Date validaty = PricerUtils.convertToDate(bean.getValidity());
					if(validaty.before(PricerUtils.convertToDate(bean.getQuotationEffectiveDate()))){  
						sb.append(ResourceMB.getParameterizedText("wq.error.mustValidateQuoteEffectiveDate", String.valueOf(bean.getLineSeq()))+",<br/>");	
						bean.setErr(true);
					}
				}
	
			}else if(QuoteUtil.isEmpty(bean.getQuotationEffectiveDate())){
				if(PricerUtils.isDate(bean.getValidity())){
					Date validaty = PricerUtils.convertToDate(bean.getValidity());
					if(null!=bean.getDisQuotationEffectiveDate() && validaty.before(bean.getDisQuotationEffectiveDate())){  // fix incident INC0143568  by June Guo 20150519
						sb.append(ResourceMB.getParameterizedText("wq.error.mustValidateQuoteEffectiveDate", String.valueOf(bean.getLineSeq()))+",<br/>");	
						bean.setErr(true);
					}
				}
			}

		}
		return sb.toString();
	}
	
	public static String validateMandatoryFieldsForRemove(PricerUploadTemplateBean bean) {
		StringBuffer sb = new StringBuffer();
		if(QuoteUtil.isEmpty(bean.getMfr())) {		 	
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] :"+ResourceMB.getText("wq.error.mandatoryMFR")+",<br/>");
			bean.setErr(true);
		}
		if(QuoteUtil.isEmpty(bean.getFullMfrPart())) {			 
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] :"+ResourceMB.getText("wq.error.mandatoryMFRP/N")+",<br/>");
			bean.setErr(true);
		}
		//beacuse of simple pricer do not have this MandatoryField
		/*if(QuoteUtil.isEmpty(bean.getCostIndicator())) {	 		
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] :"+ResourceMB.getText("wq.error.mandatoryCostIndicator")+",<br/>");
			bean.setErr(true);
		}*/
		return sb.toString();
	}

	public static String validateMandatoryFieldsForRemoveAllPricer(PricerUploadTemplateBean bean) {
		StringBuffer sb = new StringBuffer();
		if(QuoteUtil.isEmpty(bean.getMfr())) {			
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] :"+ResourceMB.getText("wq.error.mandatoryMFR")+",<br/>");
			bean.setErr(true);
		}
		if(QuoteUtil.isEmpty(bean.getFullMfrPart())) {		 	
			sb.append(ResourceMB.getText("wq.message.row")+"["+bean.getLineSeq()+"] :"+ResourceMB.getText("wq.error.mandatoryMFRP/N")+",<br/>");
			bean.setErr(true);
		}
		return sb.toString();
	}
	
	//private static int index = 0;
	/*public static String validateDuplicate(PricerUploadTemplateBean bean, List<?> beans) {
		LOGGER.info("begin v ::" + ++index);
		String pricerType = bean.getPricerType();
		if(pricerType!=null&&PRICER_TYPE.CONTRACT.getName().equalsIgnoreCase(pricerType)){
			StringBuffer currentBf = new StringBuffer();
			
			currentBf.append(bean.getKeyNoQed()).append(bean.getQuotationEffectiveDate());
		
			StringBuffer validateMsg = new StringBuffer();
			PricerUploadTemplateBean nextBean = null;
			StringBuffer nextBf = new StringBuffer(); 
			Boolean isDuplicate = false;
			
			for (int j = 0; j < beans.size(); j++) {
				nextBean = (PricerUploadTemplateBean) beans.get(j);
			
				nextBf.append(nextBean.getKeyNoQed()).append(nextBean.getQuotationEffectiveDate());
			
				if (bean.getLineSeq()!=nextBean.getLineSeq()&&!nextBean.isDuplicate()&&currentBf.toString().equals(nextBf.toString())) {
					bean.setErr(true);	
					bean.setDuplicate(true);		
					validateMsg.append( "(["+bean.getLineSeq()+"],["+nextBean.getLineSeq()+"]) ");
					nextBean.setErr(true);	
					nextBean.setDuplicate(true);
					isDuplicate = true;
				}	
			nextBf.delete(0, nextBf.length());
			}
			if(isDuplicate){  
				validateMsg.insert(0, ResourceMB.getText("wq.error.removeDuplicatePricer")+":");
			}
			if(validateMsg.length()>0){
				validateMsg.append(",<br/>");
			}
			return validateMsg.toString();
		}else if(pricerType!=null&&PRICER_TYPE.PROGRAM.getName().equalsIgnoreCase(pricerType)){
			StringBuffer currentBf = new StringBuffer();
		
			currentBf.append(bean.getKeyNoQed())
			.append(bean.getQuotationEffectiveDate());
		
			StringBuffer validateMsg = new StringBuffer();
			PricerUploadTemplateBean nextBean = null;
			StringBuffer nextBf = new StringBuffer(); 
			Boolean isDuplicate = false;
			
			for (int j = 0; j < beans.size(); j++) {
				nextBean = (PricerUploadTemplateBean) beans.get(j);
			
				nextBf.append(nextBean.getKeyNoQed())
					.append(nextBean.getQuotationEffectiveDate());
			
				if (bean.getLineSeq()!=nextBean.getLineSeq()&&!nextBean.isDuplicate()&&currentBf.toString().equals(nextBf.toString())) {
					bean.setErr(true);	
					bean.setDuplicate(true);		
					validateMsg.append( "(["+bean.getLineSeq()+"],["+nextBean.getLineSeq()+"]) ");
					nextBean.setErr(true);	
					nextBean.setDuplicate(true);
					isDuplicate = true;
				}	
			nextBf.delete(0, nextBf.length());
			}
			if(isDuplicate){  
				validateMsg.insert(0, ResourceMB.getText("wq.error.removeDuplicatePricerProg")+":");
			}
			if(validateMsg.length()>0){
				validateMsg.append(",<br/>");
			}
			return validateMsg.toString();
		}else if(pricerType!=null&&PRICER_TYPE.ALLPRICER.getName().equalsIgnoreCase(pricerType)){
			StringBuffer currentBf = new StringBuffer();
		
			currentBf.append(bean.getMfr()).append(bean.getFullMfrPart());
		
			StringBuffer validateMsg = new StringBuffer();
			PricerUploadTemplateBean nextBean = null;
			StringBuffer nextBf = new StringBuffer(); 
			Boolean isDuplicate = false;
			
			for (int j = 0; j < beans.size(); j++) {
				nextBean = (PricerUploadTemplateBean) beans.get(j);
			
				nextBf.append(nextBean.getMfr()).append(nextBean.getFullMfrPart());
			
				if (bean.getLineSeq()!=nextBean.getLineSeq()&&!nextBean.isDuplicate()&&currentBf.toString().equals(nextBf.toString())) {
					bean.setErr(true);	
					bean.setDuplicate(true);		
					validateMsg.append( "(["+bean.getLineSeq()+"],["+nextBean.getLineSeq()+"]) ");
					nextBean.setErr(true);	
					nextBean.setDuplicate(true);
					isDuplicate = true;
				}	
			nextBf.delete(0, nextBf.length());
			}
			if(isDuplicate){  
				validateMsg.insert(0, ResourceMB.getText("wq.error.removeDuplicatePricerMFR")+":");
			}
			if(validateMsg.length()>0){
				validateMsg.append(",<br/>");
			}
			return validateMsg.toString();
		}else if (pricerType!=null&&PRICER_TYPE.SIMPLE.getName().equalsIgnoreCase(pricerType)){
			StringBuffer currentBf = new StringBuffer();
			currentBf.append(bean.getKeyNoQed());
			StringBuffer validateMsg = new StringBuffer();
			PricerUploadTemplateBean nextBean = null;
			StringBuffer nextBf = new StringBuffer(); 
			Boolean isDuplicate = false;
			
			for (int j = 0; j < beans.size(); j++) {
				nextBean = (PricerUploadTemplateBean) beans.get(j);
				nextBf.append(nextBean.getKeyNoQed());
				if (bean.getLineSeq()!=nextBean.getLineSeq()&&!nextBean.isDuplicate()&&currentBf.toString().equals(nextBf.toString())) {
					bean.setErr(true);	
					bean.setDuplicate(true);		
					validateMsg.append( "(["+bean.getLineSeq()+"],["+nextBean.getLineSeq()+"]) ");
					nextBean.setErr(true);	
					nextBean.setDuplicate(true);
					isDuplicate = true;
				}	
				nextBf.delete(0, nextBf.length());  
			}
			if(isDuplicate){
				validateMsg.insert(0, ResourceMB.getText("wq.error.removeDuplicatePricerSimple")+":");
			}
			if(validateMsg.length()>0){
				validateMsg.append(",<br/>");
			}
			return validateMsg.toString();
		}
		else if (pricerType!=null&&PRICER_TYPE.SALESCOST.getName().equalsIgnoreCase(pricerType)){
			StringBuffer currentBf = new StringBuffer();
			currentBf.append(bean.getKeyNoQed()).append(bean.getQuotationEffectiveDate());
			StringBuffer validateMsg = new StringBuffer();
			PricerUploadTemplateBean nextBean = null;
			StringBuffer nextBf = new StringBuffer(); 
			Boolean isDuplicate = false;
			
			for (int j = 0; j < beans.size(); j++) {
				nextBean = (PricerUploadTemplateBean) beans.get(j);
				nextBf.append(nextBean.getKeyNoQed()).append(nextBean.getQuotationEffectiveDate());
				if (bean.getLineSeq()!=nextBean.getLineSeq()&&!nextBean.isDuplicate()&&currentBf.toString().equals(nextBf.toString())) {
					bean.setErr(true);	
					bean.setDuplicate(true);		
					validateMsg.append( "(["+bean.getLineSeq()+"],["+nextBean.getLineSeq()+"]) ");
					nextBean.setErr(true);	
					nextBean.setDuplicate(true);
					isDuplicate = true;
				}	
				nextBf.delete(0, nextBf.length());  
			}
			if(isDuplicate){
				validateMsg.insert(0, ResourceMB.getText("wq.error.removeDuplicatePricerSalesCost")+":");
			}
			if(validateMsg.length()>0){
				validateMsg.append(",<br/>");
			}
			return validateMsg.toString();
		}
		else if(pricerType!=null&&PRICER_TYPE.NORMAL.getName().equalsIgnoreCase(pricerType)){
			StringBuffer currentBf = new StringBuffer();
			currentBf.append(bean.getKeyNoQed())
			.append(bean.getQuotationEffectiveDate());
		
			StringBuffer validateMsg = new StringBuffer();
			//PricerUploadTemplateBean nextBean = null;
			StringBuffer nextBf = new StringBuffer(); 
			Boolean isDuplicate = false;
			
			for (PricerUploadTemplateBean nextBean : (List<PricerUploadTemplateBean>)beans) {
				//nextBean = (PricerUploadTemplateBean) beans.get(j);
			
				nextBf.append(nextBean.getKeyNoQed())
					.append(nextBean.getQuotationEffectiveDate());
			
				if (bean.getLineSeq()!=nextBean.getLineSeq()&&!nextBean.isDuplicate()&&currentBf.toString().equals(nextBf.toString())) {
					bean.setErr(true);	
					bean.setDuplicate(true);		
					validateMsg.append( "(["+bean.getLineSeq()+"],["+nextBean.getLineSeq()+"]) ");
					nextBean.setErr(true);	
					nextBean.setDuplicate(true);
					isDuplicate = true;
				}	
			nextBf.delete(0, nextBf.length());  
			}
			if(isDuplicate){
				validateMsg.insert(0, ResourceMB.getText("wq.error.removeDuplicatePricerQED")+":");
			}
			if(validateMsg.length()>0){
				validateMsg.append(",<br/>");
			}
			return validateMsg.toString();
		}
		return "";
		
	}
	*/
	public static MailInfoBean generateUploadPricerEmailContent(Date createOn,String fileName, User user,String errMsg,ProgramItemUploadCounterBean countBean,String fromEmailAddr) {
		MailInfoBean mib = new MailInfoBean();
		List<String> toList = new ArrayList<String>();
		//toList.add("Andy.Hu@AVNET.COM"); // ;user.getEmailAddress()
		toList.add(user.getEmailAddress());
		if (toList.size() > 0) {
			String subject  = "price upload offline";;
			mib.setMailSubject(subject);
			mib.setMailTo(toList);
			String content = "";
			if(!errMsg.equals("")){
				content = "File Name: "+fileName+" Uploaded By: "+user.getName()+"  Uploaded by user at: "+PricerUtils.convertFormat2Time(createOn)+" Proceeded by system at: "+PricerUtils.convertFormat2Time(new Date())+" <br/>";
				content = content + "Upload file didn't pass the verification, please modify the upload files according to follow information,"
						+ " authentication information is as follows:<br/>";
				content = content + errMsg +"<br/>";
				content += "Best Regards," + "<br/>";
				content += fromEmailAddr + "<br/>";
			}
			mib = EJBCommonSB.sendUploadPricerEmail(createOn, fileName, user, countBean, fromEmailAddr, mib, content, "pricerUploadHelper");
		}
		return mib;
	}


	
	public static boolean isHaveErrorMsg(String errMsg){
		if(errMsg ==null||errMsg.equals("") ||errMsg.equals("[]")||errMsg.equals("null")){
			return false;
		}else{
			return true;
		}
	}
	
	public static String isHaveSameKey( List<PricerUploadTemplateBean> list ){
		for(int k = 0;k<list.size();k++){//set Duplicate is false ,
			PricerUploadTemplateBean bean = list.get(k);	
			bean.setDuplicate(false);
		}
		StringBuffer validateMsg = new StringBuffer();
		for(int currentIndex = 0;currentIndex<list.size();currentIndex ++){
			PricerUploadTemplateBean bean = list.get(currentIndex);	
			StringBuffer currentBf = new StringBuffer();
		
			currentBf.append(bean.getMfr()).append(bean.getFullMfrPart()).append(bean.getCostIndicator());
	
			
			PricerUploadTemplateBean nextBean = null;
			StringBuffer nextBf = new StringBuffer(); 
			Boolean isDuplicate = false;
		
			for (int j = currentIndex + 1; j < list.size(); j++) {
				nextBean = (PricerUploadTemplateBean) list.get(j);
		
				nextBf.append(nextBean.getMfr()).append(nextBean.getFullMfrPart()).append(nextBean.getCostIndicator());
		
				if (!nextBean.isDuplicate()&&currentBf.toString().equals(nextBf.toString())) {
					bean.setErr(true);	
					bean.setDuplicate(true);		
					validateMsg.append( "(["+bean.getLineSeq()+"],["+nextBean.getLineSeq()+"]) ");
					nextBean.setErr(true);	
					nextBean.setDuplicate(true);
					isDuplicate = true;
				}	
				nextBf.delete(0, nextBf.length());
			}
			if(isDuplicate){ 
				validateMsg.insert(0, ResourceMB.getText("wq.error.duplicatePricer")+":");
			}
		}
		return validateMsg.toString();
	}
	
	
	public static List<PricerUploadTemplateBean> getUploadBeansByPricerType(List<PricerUploadTemplateBean> totalBeans,String pricerType){
		List<PricerUploadTemplateBean> tempBeans = new LinkedList<PricerUploadTemplateBean>();
		for(PricerUploadTemplateBean bean :totalBeans){
			if(bean.getPricerType()!=null&&bean.getPricerType().equalsIgnoreCase(pricerType )){
				tempBeans.add(bean);
			}			
		}
		return tempBeans;
	}
	
	public static List<PricerUploadTemplateBean> getHaveABookCostBean(List<PricerUploadTemplateBean> totalBeans){
		List<PricerUploadTemplateBean> tempBeans = new LinkedList<PricerUploadTemplateBean>();
		for(PricerUploadTemplateBean bean :totalBeans){
			if(bean.getCostIndicator()!=null&&bean.getCostIndicator().equalsIgnoreCase(PricerConstant.A_COST_INDICATOR )){
				tempBeans.add(bean);
			}			
		}
		return tempBeans;
	}
	public static List<PricerUploadTemplateBean> getNoHaveABookCostBean(List<PricerUploadTemplateBean> totalBeans){
		List<PricerUploadTemplateBean> tempBeans = new LinkedList<PricerUploadTemplateBean>();
		for(PricerUploadTemplateBean bean :totalBeans){
			if(bean.getCostIndicator()!=null&&!bean.getCostIndicator().equalsIgnoreCase(PricerConstant.A_COST_INDICATOR )){
				tempBeans.add(bean);
			}			
		}
		return tempBeans;
	}
	public static String getErrorInfoFromException(Exception e) {
		try {
	    	StringWriter sw = new StringWriter();
	        PrintWriter pw = new PrintWriter(sw);
	        LOGGER.log(Level.SEVERE, e.getMessage(), e);
	        return "\r\n" + sw.toString() + "\r\n";
	    } catch (Exception e2) {
	        return "bad getErrorInfoFromException";
	    }
	}

	public static String validateBoolean(PricerUploadTemplateBean bean) {
		StringBuffer validateMsg = new StringBuffer();
		String aFlag = bean.getAllocationFlag();
		String aq = bean.getAQFlag();
		//String cfq = bean.getCallForQuote();
		String nidr = bean.getNewItemIndicator();
		String availableToSellMoreFlag = bean.getAvailableToSellMoreFlag();
		String overriddenStr = bean.getOverriddenStr();
		String salesCostFlag = bean.getSalesCostFlag();
		
		if (!QuoteUtil.isEmpty(aFlag)&&!(aFlag.equalsIgnoreCase("YES")||aFlag.equalsIgnoreCase("NO"))) {  
			validateMsg.append(ResourceMB.getParameterizedText("wq.error.allocationFlagInput", String.valueOf(bean.getLineSeq()))+", <br/>");
			bean.setErr(true);
		}
		
		if (!QuoteUtil.isEmpty(aq)&&!(aq.equalsIgnoreCase("YES")||aq.equalsIgnoreCase("NO"))) {  
			validateMsg.append(ResourceMB.getParameterizedText("wq.error.AQFlagInput", String.valueOf(bean.getLineSeq()))+", <br/>");
			bean.setErr(true);
		}
		/*if (!QuoteUtil.isEmpty(cfq)&&!(cfq.equalsIgnoreCase("YES")||cfq.equalsIgnoreCase("NO"))) {  
			validateMsg.append(ResourceMB.getParameterizedText("wq.error.callQuoteInput", String.valueOf(bean.getLineSeq()))+", <br/>");
			bean.setErr(true);
		}		*/																					
		if (!QuoteUtil.isEmpty(availableToSellMoreFlag)&&!(availableToSellMoreFlag.equalsIgnoreCase("YES")||availableToSellMoreFlag.equalsIgnoreCase("NO"))) {
			validateMsg.append(ResourceMB.getParameterizedText("wq.error.availableSellMoreInput", String.valueOf(bean.getLineSeq()))+", <br/>");
			bean.setErr(true);
		}
		if (!QuoteUtil.isEmpty(nidr)&&!(nidr.equalsIgnoreCase("YES")||nidr.equalsIgnoreCase("NO"))) {  
			validateMsg.append(ResourceMB.getParameterizedText("wq.error.newItemIndicator", String.valueOf(bean.getLineSeq()))+", <br/>");
			bean.setErr(true);
		}																															
		if (!QuoteUtil.isEmpty(overriddenStr)&&!(overriddenStr.equalsIgnoreCase("YES")||overriddenStr.equalsIgnoreCase("NO"))) {
			validateMsg.append(ResourceMB.getParameterizedText("wq.error.overriddenInput", String.valueOf(bean.getLineSeq()))+", <br/>");
			bean.setErr(true);
		}
		if (!QuoteUtil.isEmpty(salesCostFlag)&&!(salesCostFlag.equalsIgnoreCase("YES")||salesCostFlag.equalsIgnoreCase("NO"))) {
			validateMsg.append(ResourceMB.getParameterizedText("wq.error.salesCostFlagInput", String.valueOf(bean.getLineSeq()))+", <br/>");
			bean.setErr(true);
		}
		return validateMsg.toString();
	}
	
	 /**
	  * if cost indicator is normal 'A-Book Cost' then disallow removal and show msg
	  *  ' A-Book Cost cannot be removed'. All other costs could be removed. 
	  * @param bean
	  * @return
	  */
	public static Object validateRemoveABookCost(PricerUploadTemplateBean bean) {
		StringBuffer sb = new StringBuffer();
		String pricerType =bean.getPricerType();
		String costIndicator = bean.getCostIndicator();
		if (pricerType!=null&&pricerType.equalsIgnoreCase(PRICER_TYPE.NORMAL.getName())
				&&costIndicator!=null&&costIndicator.equalsIgnoreCase(PricerConstant.A_COST_INDICATOR)) {
			sb.append(ResourceMB.getParameterizedText("wq.error.cantRemoveBookCost", String.valueOf(bean.getLineSeq()))+",<br/>");
			bean.setErr(true);	
		}		
		return sb.toString(); 
	}

	public static String validateValue(PricerUploadTemplateBean bean) {
		StringBuffer validateMsg = new StringBuffer();	
		String qtyIndicatorBean = bean.getQtyIndicator();
		Boolean bool = false;
		if(!QuoteUtil.isEmpty(qtyIndicatorBean)){
			qtyIndicatorBean = qtyIndicatorBean.trim();		

			QtyIndicatorEnum[] values = QtyIndicatorEnum.values();
			
			for(int i= 0;i<values.length;i++){
				QtyIndicatorEnum qtyIndicator = values[i];
				String qtyIndicatorName = qtyIndicator.getName();

				if(qtyIndicatorBean.equalsIgnoreCase(qtyIndicatorName)){
					bool = true;
					break;
				}
			}
			if(!bool){ 
				validateMsg.append(ResourceMB.getParameterizedText("wq.error.exactLmtQtyIndicator", String.valueOf(bean.getLineSeq()))+", <br/>");
				bean.setErr(true);	
			}
		}

		return validateMsg.toString();
	}

	/**
	 * for Program pricer, both MOQ and MOV are empty then prompt error message, else pass
	 * 
	 * @param bean
	 * @return
	 */
	public static String validateMOQAndMOV(PricerUploadTemplateBean bean) {
		StringBuffer validateMsg = new StringBuffer();
		try {
			if (PRICER_TYPE.PROGRAM.getName().equalsIgnoreCase(bean.getPricerType())) {
				int moq = 0, mov = 0;
				Boolean bool = false;
				if (QuoteUtil.isEmpty(bean.getMOQ())) {
					moq = 0;
				} else {
					moq = Integer.parseInt(bean.getMOQ());
				}

				if (QuoteUtil.isEmpty(bean.getMOV())) {
					mov = 0;
				} else {
					mov = Integer.parseInt(bean.getMOV());
				}

				if ((moq + mov) > 0) {
					bool = true;
				}

				if (!bool) {   
					validateMsg.append(ResourceMB.getParameterizedText("wq.error.inputMOVMOQ", String.valueOf(bean.getLineSeq()))+", <br/>");
					bean.setErr(true);
				}
			}
		} catch (NumberFormatException e) {  
			LOGGER.log(Level.WARNING, "Exception occured for MOQ: "+bean.getMOQ()+", MOV: "+bean.getMOV()+", pricer type: "+bean.getPricerType()+", Exception message: "+e.getMessage());
			validateMsg.append(ResourceMB.getParameterizedText("wq.error.integerMOV", String.valueOf(bean.getLineSeq()))+" , <br/>");
		}

		return validateMsg.toString();
	}

	/**
	 * fix defect 359
	 * for the pricer upload, A-Book Cost is for Normal Price only. 
	 * If A-Book Cost is uploaded as Contract Pricer or Program Pricer (Pricer Type = CONTRACT or PROGRAM), 
	 * system will display the error message "A-Book Cost in Contract/Program is not allowed. 
	 * Please check the pricer template."
	 * @param bean
	 * @return
	 */
	public static Object validateABookCost(PricerUploadTemplateBean bean) {
		StringBuffer sb = new StringBuffer();
		String pricerType =bean.getPricerType();
		String costIndicator = bean.getCostIndicator();
		if ((PRICER_TYPE.CONTRACT.getName().equalsIgnoreCase(pricerType) ||PRICER_TYPE.PROGRAM.getName().equalsIgnoreCase(pricerType))
				&&PricerConstant.A_COST_INDICATOR.equalsIgnoreCase(costIndicator)) {
			sb.append(ResourceMB.getParameterizedText("wq.error.reviseYourPricer", String.valueOf(bean.getLineSeq()))+".,<br/>");
			bean.setErr(true);	
		}		  
		return sb.toString();
	}
	
	
	/**
	 * the maximum length is 20
	 * @param bean
	 * @return
	 */
	public static String validateProgramType(PricerUploadTemplateBean bean) {
		StringBuffer sb = new StringBuffer();
		String programType =bean.getProgram();
		if (programType!=null && programType.length()>20) {
			sb.append(ResourceMB.getParameterizedText("wq.error.progDataRevisePricer", String.valueOf(bean.getLineSeq()))+".,<br/>");
			bean.setErr(true);	
		}		
		return sb.toString();  
	}

}
