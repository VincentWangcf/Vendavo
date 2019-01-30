package com.avnet.emasia.webquote.reports.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.MaterialRegional;
import com.avnet.emasia.webquote.entity.QuoteItemDesign;
import com.avnet.emasia.webquote.exception.WebQuoteException;
import com.avnet.emasia.webquote.helper.TransferHelper;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.quote.vo.QuoteItemVo;
import com.avnet.emasia.webquote.quote.vo.WorkingPlatformItemVO;
import com.avnet.emasia.webquote.vo.CatalogMainVO;
import com.avnet.emasia.webquote.vo.MyQuoteResultBean;
import com.avnet.emasia.webquote.vo.PartMasterResultBean;
import com.avnet.emasia.webquote.vo.WorkingPlatFormResultBean;

/**
 * @author 906893
 * @createdOn 20130424
 */
public class ReportConvertor {

	private static final Logger LOGGER = Logger.getLogger(ReportConvertor.class.getName());

	 private final static String BLANK_SIGN= "";
	 private final static String PERCENT_SIGN= "%";
	 private final static DecimalFormat DF2 = new DecimalFormat("#.00");
	 private final static String YES_SIGN= "Yes";
	 private final static String NO_SIGN= "No";

		public static List<MyQuoteResultBean> convert2ResultBean(List<QuoteItemVo> quoteItems)throws WebQuoteException{
			LOGGER.fine("call convert2ResultBean");
			List<MyQuoteResultBean> returnList = new ArrayList<MyQuoteResultBean>();
			if(quoteItems!=null && quoteItems.size()>0)
			{
				for(QuoteItemVo qi : quoteItems )
				  {					

						MyQuoteResultBean sqrb = new MyQuoteResultBean();
						sqrb.setPmoq(qi.getQuoteItem().getPmoq());
					    sqrb.setQuoteNumber(qi.getQuoteItem().getQuoteNumber());
						if(qi.getQuoteItem().getShipToCustomer()!=null)
						{
							sqrb.setShipToParty(qi.getQuoteItem().getShipToCustomer().getCustomerFullName());
							sqrb.setShipToCode(qi.getQuoteItem().getShipToCustomer().getCustomerNumber());
						}
					    else
					    {
					    	sqrb.setShipToParty(BLANK_SIGN);
							sqrb.setShipToCode(BLANK_SIGN);
					    }

					    if (qi.getQuoteItem() != null && qi.getQuoteItem().getQuote() != null)
					    {
					    	sqrb.setCustomerType(qi.getQuoteItem().getQuote().getCustomerType());
					    }
					    else 
					    {
					    	sqrb.setCustomerType(BLANK_SIGN);
					    }
						sqrb.setInternalTransferComment(qi.getQuoteItem().getInternalTransferComment());

						sqrb.setTargetMargin(convertDoubleToStrForMargin(qi.getQuoteItem().getTargetMargin()));
						if (qi.getQuoteItem() != null && qi.getQuoteItem().getResaleMax() != null)
						{
							sqrb.setResalesMax(String.valueOf(qi.getQuoteItem().getResaleMax()));
						}
						
						if (qi.getQuoteItem() != null && qi.getQuoteItem().getResaleMin() != null)
						{
							sqrb.setResalesMin(String.valueOf(qi.getQuoteItem().getResaleMin()));
						}
						
						sqrb.setQtyIndicator(qi.getQuoteItem().getQtyIndicator());
						sqrb.setPriceValidity(qi.getQuoteItem().getPriceValidity());
						
						sqrb.setPoExpiryDate(qi.getQuoteItem().getPoExpiryDate());
						if (qi.getQuoteItem() != null && qi.getQuoteItem().getMov() != null)
						{
							sqrb.setMov(String.valueOf(qi.getQuoteItem().getMov()));
						}
						
						if (qi.getQuoteItem() != null && qi.getQuoteItem().getCost() != null)
						{
							sqrb.setCost(convertDoubleToStr(qi.getQuoteItem().getCost()));
						}
						 
						if (qi.getQuoteItem() != null && qi.getQuoteItem().getQuotedQty() != null)
						{
							sqrb.setAvnetQuotedQty(String.valueOf(qi.getQuoteItem().getQuotedQty()));
						}
			        	
				        //Added for Sales Cost Project by DamonChen
				sqrb.setSalesCostFlag((true == qi.getQuoteItem().isSalesCostFlag())?YES_SIGN:NO_SIGN);
				if (qi.getQuoteItem().getSalesCostType() != null) {
					sqrb.setSalesCostTypeName(qi.getQuoteItem().getSalesCostType().name());
				}

				//JP MultiCurrency Project (201810-whwong)
				sqrb.setSalesCostRFQCur(convertBigDecimalToStr(qi.getQuoteItem().convertToRfqValue(qi.getQuoteItem().getSalesCost())));
				sqrb.setSuggestedResaleRFQCur(convertBigDecimalToStr(qi.getQuoteItem().convertToRfqValue(qi.getQuoteItem().getSuggestedResale())));
				sqrb.setSalesCost(convertBigDecimalToStr(qi.getQuoteItem().getSalesCost()));
				sqrb.setSuggestedResale(convertBigDecimalToStr(qi.getQuoteItem().getSuggestedResale()));
				
				sqrb.setCustomerGroupId(qi.getQuoteItem().getCustomerGroupId());
				sqrb.setSalesCostTypeName(qi.getQuoteItem().getSalesCostType() == null?BLANK_SIGN:qi.getQuoteItem().getSalesCostType().name());
				sqrb.setCostIndicator(qi.getQuoteItem().getCostIndicator());
				sqrb.setVendorDebit(qi.getQuoteItem().getVendorDebitNumber());		
						
			            sqrb.setResaleIndicator(qi.getQuoteItem().getResaleIndicator());
			            sqrb.setMultiUsage(convertBoolean(qi.getQuoteItem().getMultiUsageFlag()));
			            if (qi.getQuoteItem() != null && qi.getQuoteItem().getMpq() != null)
			            {
			            	sqrb.setMpq(String.valueOf(qi.getQuoteItem().getMpq()));
			            }
			            
			            if (qi.getQuoteItem() != null && qi.getQuoteItem().getMoq() != null)
			            {
			            	sqrb.setMoq(String.valueOf(qi.getQuoteItem().getMoq()));
			            }
			            
			            sqrb.setLeadTime(qi.getQuoteItem().getLeadTime());
			            sqrb.setAvnetQCComment(qi.getQuoteItem().getAqcc());
			            sqrb.setShipmentValidity(qi.getQuoteItem().getShipmentValidity());
			            
			            if (qi.getQuoteItem().getRescheduleWindow() != null)
			            {
			            	sqrb.setReshedulingWindow(String.valueOf(qi.getQuoteItem().getRescheduleWindow()));
			            }
			            
			            if (qi.getQuoteItem().getCancellationWindow() != null)
			            {
			            	sqrb.setCancellWindow(String.valueOf(qi.getQuoteItem().getCancellationWindow()));
			            }
			            
			            sqrb.setVendorQuote(qi.getQuoteItem().getVendorQuoteNumber());
			            sqrb.setAllocationFlog(convertBoolean(qi.getQuoteItem().getAllocationFlag()));
			            sqrb.setRevertVersion(qi.getQuoteItem().getRevertVersion());
			            sqrb.setFirstRfqCode(qi.getQuoteItem().getFirstRfqCode());

			            if (qi.getQuoteItem() != null && qi.getQuoteItem().getReferenceMargin() != null)
			            {
			            	//sqrb.setReferenceMargin(String.valueOf(qi.getQuoteItem().getReferenceMargin()));
			            	sqrb.setReferenceMargin(convertDoubleToStrForMargin(qi.getQuoteItem().getReferenceMargin()));
			            }
			            else
			            {
			            	sqrb.setReferenceMargin(BLANK_SIGN);
			            }

			            sqrb.setResaleMargin(convertDoubleToStrForResaleMargin(qi.getQuoteItem().getResaleMargin()));
			            
			            //fix ticket INC0378628  
			            sqrb.setEndCustomer(qi.getEndCustomerName());
			            
				        if(qi.getQuoteItem().getEndCustomer()!=null)
				        {
				            sqrb.setEndCustomerCode(qi.getQuoteItem().getEndCustomer().getCustomerNumber());
				        }else
				        {
				        	sqrb.setEndCustomerCode(BLANK_SIGN);
				        }
				        sqrb.setFormNo(qi.getQuoteItem().getQuote().getFormNumber());
				        
				        sqrb.setReadyForOrder(convertBoolean(qi.getReadyForOrder()));			
						sqrb.setReadyForOrderNot(qi.getReasonForNotReadyForOrder());
						if(qi.getQuoteItem()!= null && qi.getQuoteItem().getRequestedMfr()!= null && qi.getQuoteItem().getRequestedMfr().getName() != null && !qi.getQuoteItem().getRequestedMfr().getName().equals(""))
						{
							sqrb.setMfr(qi.getQuoteItem().getRequestedMfr().getName());
						}
						if(qi.getQuoteItem().getQuotedPartNumber() != null)
							sqrb.setQuotedPn(qi.getQuoteItem().getQuotedPartNumber());	
						else
							sqrb.setQuotedPn(BLANK_SIGN);
						if(qi.getQuoteItem().getQuotedMaterial()!=null)
							sqrb.setSapPartNumber(qi.getQuoteItem().getSapPartNumber());	
						else
							sqrb.setSapPartNumber(BLANK_SIGN);
						
						if(qi.getQuoteItem().getSoldToCustomer()!=null && qi.getQuoteItem().getQuote()!=null)
						{
							sqrb.setSolToCustomerChineseName(qi.getQuoteItem().getQuote().getSoldToCustomerNameChinese());	
							sqrb.setSoldToCustomerName(qi.getQuoteItem().getQuote().getSoldToCustomerName());
						}
					    else
					    {
					    	sqrb.setSoldToCustomerName(BLANK_SIGN);	
							sqrb.setSolToCustomerChineseName(BLANK_SIGN);
					    }
						if (qi.getQuoteItem() != null && qi.getQuoteItem().getQuote() != null && qi.getQuoteItem().getQuote().getSoldToCustomer() != null)
						{
							sqrb.setSoldToCustomerCode(qi.getQuoteItem().getQuote().getSoldToCustomer().getCustomerNumber());
							sqrb.setSapSoldToType(qi.getQuoteItem().getQuote().getSoldToCustomer().getAccountGroupDesc());
						}
						else 
						{
							sqrb.setSoldToCustomerCode(BLANK_SIGN);
							sqrb.setSapSoldToType(BLANK_SIGN);
						}
					  
						
						
						if(qi.getQuoteItem().getQuote().getSales()!=null)
						{
							sqrb.setEmployeeName(qi.getQuoteItem().getQuote().getSales().getName());
							sqrb.setSalesman(qi.getQuoteItem().getQuote().getSales().getEmployeeId());
						}
						else
						{
							sqrb.setEmployeeName(BLANK_SIGN);
							sqrb.setSalesman(BLANK_SIGN);
						}
						
						if(qi.getQuoteItem().getQuote().getTeam()!=null)
						{
							sqrb.setTeam(qi.getQuoteItem().getQuote().getTeam().getName());
						}
						else
						{
							sqrb.setTeam(BLANK_SIGN);
						}
						//andy modify 2.2
						if(qi.getQuoteItem().getMaterialTypeId()!=null)
						{
							sqrb.setMaterialType(qi.getQuoteItem().getMaterialTypeId());
						}
						else
						{
							sqrb.setMaterialType(BLANK_SIGN);
						}
						
						if(qi.getQuoteItem().getProgramType()!=null)
						{
							sqrb.setProgram(qi.getQuoteItem().getProgramType());//andy modify 2.2
						}
						else
						{
							sqrb.setProgram(BLANK_SIGN);
						}
						
						sqrb.setQuoteEffectiveDate(qi.getQuoteItem().getQuotationEffectiveDate());
				        sqrb.setQuoteExpiryDate(qi.getQuoteItem().getQuoteExpiryDate());
				        sqrb.setAvnetQuotePrice(convertDoubleToStr(qi.getQuoteItem().getQuotedPrice()));
						//JP MultiCurrency Project (201810-whwong)
				        //sqrb.setQuotedPriceRFQCur(convertDoubleToStr(qi.getQuoteItem().getQuotedPriceRFQCur()));
				        BigDecimal qPriceRFQ = qi.getQuoteItem().convertToRfqValueWithDouble(qi.getQuoteItem().getQuotedPrice());
				        if (qPriceRFQ != null) sqrb.setQuotedPriceRFQCur(convertDoubleToStr(qPriceRFQ.doubleValue()));
				        else sqrb.setQuotedPriceRFQCur("");
				        
				        sqrb.setResaleRange(qi.getQuoteItem().getResaleRange());
				        //#{vo.quoteItem.buyCurr}/#{vo.quoteItem.rfqCurr}
				        sqrb.setMqsCurExc(qi.getQuoteItem().getBuyCurr().name() + "/" + qi.getQuoteItem().getRfqCurr().name());
				        //vo.quoteItem.exRateRfq/(vo.quoteItem.exRateBuy>0.00000?vo.quoteItem.exRateBuy:1
				        
				        BigDecimal eRate = qi.getQuoteItem().getExRate();
				        if (eRate!=null)
				        	sqrb.setMqsExcRate(convertDoubleToStr(eRate.doubleValue()));
				        else
				        	sqrb.setMqsExcRate(null);
				        
				        /***** Below process replace with the same function in MQS (standard shared function)
				        if (qi.getQuoteItem().getExRateRfq()!=null && qi.getQuoteItem().getExRateBuy()!=null && qi.getQuoteItem().getExRateBuy().compareTo(BigDecimal.ZERO)>0){
					        sqrb.setMqsExcRate(convertDoubleToStr(qi.getQuoteItem().getExRateRfq().divide(qi.getQuoteItem().getExRateBuy(),5, BigDecimal.ROUND_HALF_UP).doubleValue()));
				        }
				        else if(qi.getQuoteItem().getExRateRfq()!=null)
				        	sqrb.setMqsExcRate(convertDoubleToStr(qi.getQuoteItem().getExRateRfq().doubleValue()));
				        else
				        	sqrb.setMqsExcRate(null);
				        */
				        
				        sqrb.setFinalCurr(qi.getQuoteItem().getFinalCurr().name());
				        
				        eRate = qi.getQuoteItem().getExRateFnlCalc();//exRateFnlCalc
				        if (eRate!=null)
				        	sqrb.setMqsExcRateFnl(convertDoubleToStr(eRate.doubleValue()));
				        else
				        	sqrb.setMqsExcRateFnl(null);

				        //#{vo.quoteItem.exRateFnl/(vo.quoteItem.exRateRfq>0.00000?vo.quoteItem.exRateRfq:1)
				        /***** Below process replace with the same function in MQS (standard shared function)
				        if (qi.getQuoteItem().getExRateFnl()!=null && qi.getQuoteItem().getExRateRfq()!=null && qi.getQuoteItem().getExRateRfq().compareTo(BigDecimal.ZERO)>0){
					        sqrb.setMqsExcRateFnl(convertDoubleToStr(qi.getQuoteItem().getExRateFnl().divide(qi.getQuoteItem().getExRateRfq(),5, BigDecimal.ROUND_HALF_UP).doubleValue()));
				        }
				        else if (qi.getQuoteItem().getExRateFnl()!=null){
				        	sqrb.setMqsExcRateFnl(convertDoubleToStr(qi.getQuoteItem().getExRateFnl().doubleValue()));
				        }
				        else sqrb.setMqsExcRateFnl(null);
				        */
				        
				        //dLinkFlagStr: #{vo.quoteItem.quote.dLinkFlag? 'Yes':'No'}
				        sqrb.setDLinkFlagStr(qi.getQuoteItem().getQuote().isdLinkFlag()? "Yes":"No");
				        sqrb.setRateRemarks(qi.getQuoteItem().getRateRemarks());
				        sqrb.setBuyCurr(qi.getQuoteItem().getBuyCurr().name());


				        sqrb.setFinalQuotationPrice(convertDoubleToStr(qi.getQuoteItem().getFinalQuotationPrice()));
				       
				        sqrb.setCostIndicator(qi.getQuoteItem().getCostIndicator());
						sqrb.setVendorDebit(qi.getQuoteItem().getVendorDebitNumber());		
						
						if (qi.getQuoteItem() != null && qi.getQuoteItem().getVendorQuoteQty() != null)
						{
							sqrb.setVendorQty(String.valueOf(qi.getQuoteItem().getVendorQuoteQty()));
						}
								
						sqrb.setTermsAndConditions(qi.getQuoteItem().getTermsAndConditions());
						sqrb.setInternalComment(qi.getQuoteItem().getInternalComment());		
						sqrb.setQuoteStage(qi.getQuoteItem().getStage());
						sqrb.setRfqStatus(qi.getQuoteItem().getStatus());	
						
						sqrb.setEnquirySegment(qi.getQuoteItem().getEnquirySegment());
						sqrb.setProjectName(qi.getQuoteItem().getProjectName());
						sqrb.setApplication(qi.getQuoteItem().getApplication());
						sqrb.setDesignLocation(qi.getQuoteItem().getDesignLocation());
						sqrb.setAvnetRegionalDemandCreation(qi.getQuoteItem().getDesignInCat());
						
						if (qi.getQuoteItem().getDrmsNumber() != null)
						{
							sqrb.setDrms(String.valueOf(qi.getQuoteItem().getDrmsNumber()));
						}
						else
						{
							sqrb.setDrms(BLANK_SIGN);
						}
						sqrb.setMpSchedule(qi.getQuoteItem().getMpSchedule());
						sqrb.setPpSchedule(qi.getQuoteItem().getPpSchedule());
						sqrb.setLoa(convertBoolean(qi.getQuoteItem().getLoaFlag()));
						
						//for fix defect 6  June Guo 20140827 start 
						if(null!=qi.getQuoteItem().getExRateRfq() && null!=qi.getQuoteItem().getVat() && null!=qi.getQuoteItem().getHandling()){
							BigDecimal exRate = (qi.getQuoteItem().getExRateRfq().multiply(qi.getQuoteItem().getVat()).multiply(qi.getQuoteItem().getHandling())).setScale(5,RoundingMode.HALF_UP); 
							if(null!=exRate)
								sqrb.setExRateTo(exRate.toString());
						}
						
						
						sqrb.setCurrTo(qi.getQuoteItem().getRfqCurr().name());
						sqrb.setRemarks(qi.getQuoteItem().getQuote().getRemarks()); 
						sqrb.setYourReference(qi.getQuoteItem().getQuote().getYourReference());
						sqrb.setBmtChecked(convertBoolean(qi.getQuoteItem().getBmtCheckedFlag()));
						sqrb.setQuoteType(qi.getQuoteItem().getQuoteType());
						sqrb.setCopyToCs(qi.getCopyToCSName());
						if(qi.getQuoteItem().getRequestedPartNumber()!=null)
						{
							sqrb.setRequestedPn(qi.getQuoteItem().getRequestedPartNumber());
						}
						else
						{
							sqrb.setRequestedPn(BLANK_SIGN);
						}
						
						if(qi.getQuoteItem().getProductGroup2()!=null)
						{
							sqrb.setProductGroup(qi.getQuoteItem().getProductGroup2().getName());
						}
						else
						{
							sqrb.setProductGroup(BLANK_SIGN);
						}
						
						if(qi.getBalanceUnconsumedQty()!=null)
						{
							sqrb.setBalanceUnconsumedQty(String.valueOf(qi.getBalanceUnconsumedQty()));
						}
						else
						{
							sqrb.setBalanceUnconsumedQty(BLANK_SIGN);
						}
						
						sqrb.setSpr(convertBoolean(qi.getQuoteItem().getSprFlag()));
						if (qi.getQuoteItem() != null && qi.getQuoteItem().getRequestedQty() != null)
						{
							sqrb.setRequiredQty(String.valueOf(qi.getQuoteItem().getRequestedQty()));
						}
						
						if (qi.getQuoteItem().getEau() != null)
						{
							sqrb.setEau(String.valueOf(qi.getQuoteItem().getEau()));
						}
						
						if (qi.getQuoteItem().getTargetResale() != null)
						{
							sqrb.setTargetResale(convertDoubleToStr(qi.getQuoteItem().getTargetResale()));
							//JP MultiCurrency Project (201810-whwong)
					        //sqrb.setTargetResaleBuyCur(convertDoubleToStr(qi.getQuoteItem().getTargetResaleBuyCur()));
							BigDecimal tResaleBuy = qi.getQuoteItem().convertToBuyValueWithDouble(qi.getQuoteItem().getTargetResale());
							
							if (tResaleBuy != null) sqrb.setTargetResaleBuyCur(convertDoubleToStr(tResaleBuy.doubleValue()));
							else sqrb.setTargetResaleBuyCur("");
						}
						else 
						{
							sqrb.setTargetResale(BLANK_SIGN);
						}
						
						sqrb.setSupplierDr(qi.getQuoteItem().getSupplierDrNumber());
						sqrb.setCompetitorInformation(qi.getQuoteItem().getCompetitorInformation()); 
						sqrb.setItemRemarks(qi.getQuoteItem().getRemarks());
						sqrb.setReasonForRefresh(qi.getQuoteItem().getReasonForRefresh());
						
						sqrb.setRemarksToCustomer(qi.getQuoteItem().getQuote().getRemarksToCustomer());
						sqrb.setUploadTime(qi.getQuoteItem().getSubmissionDate());
						sqrb.setSendOutTime(qi.getQuoteItem().getSentOutTime());
						if(qi.getQuoteItem().getQuote().getCreatedBy()!=null)
						{
							//sqrb.setCreatedBy(qi.getQuoteItem().getQuote().getCreatedBy());
							sqrb.setCreatedBy(qi.getQuoteItem().getQuote().getCreatedName());
						}
						else
						{
							sqrb.setCreatedBy(BLANK_SIGN);
						}
					
						if(qi.getQuoteItem().getLastUpdatedQc()!=null)
					    {
						   sqrb.setQuotedBy(qi.getQuoteItem().getLastUpdatedQcName());
					    }
						else
						{
							sqrb.setQuotedBy(BLANK_SIGN);
						}
						
						sqrb.setDrmsAuthorizedGP(formatAuthorizedGP(qi.getQuoteItem().getAuthGp()));
						
						sqrb.setAuthorizedGPChangeReason(qi.getQuoteItem().getAuthGpReasonDesc());
						
						sqrb.setRemarkChangeReason(qi.getQuoteItem().getAuthGpRemark());
						sqrb.setDrExpiryDate(qi.getQuoteItem().getDrExpiryDate());
						
						sqrb.setUsedFlag((null!=qi.getQuoteItem().getUsedFlag() && true == qi.getQuoteItem().getUsedFlag()) ?"Yes":"No");
						
						if(null!=qi.getQuoteItem().getDesignRegion()) {
							sqrb.setDesignRegion(qi.getQuoteItem().getDesignRegion());
						}else {
							sqrb.setDesignRegion(BLANK_SIGN);
						}
						
						if(null!=qi.getQuoteItem()) {
							sqrb.setLastUpdateBmtName(qi.getLastUpdateBmtName());
							sqrb.setLastUpdateName(qi.getQuoteItem().getLastUpdatedName());
							if(null!=qi.getQuoteItem().getLastBmtUpdateTime()) {
								sqrb.setLastBmtUpdate(qi.getQuoteItem().getLastBmtUpdateTime());
							}
							
						}else {
							sqrb.setLastUpdateBmtName(BLANK_SIGN);
							sqrb.setLastUpdateName(BLANK_SIGN);
						
						}
						
						if(null!=qi.getQuoteItem()) {
							sqrb.setBmtPendingAt(qi.getDisPendingAt());
						}
						
						/*************BMT information****************/
						if(null!=qi.getQuoteItem().getQuoteItemDesign()) {
							QuoteItemDesign design = qi.getQuoteItem().getQuoteItemDesign();
							sqrb.setBmtFlag(design.getBmtFlag().getDescription());
							sqrb.setBmtMfrDrNo(design.getDrMfrNo());
							sqrb.setBmtSuggestCost(convertBigDecimalToStr(design.getDrCost()));
							sqrb.setBmtSuggestResale(convertBigDecimalToStr(design.getDrResale()));
							sqrb.setBmtMfrQuoteNo(design.getDrMfrQuoteNo());
							sqrb.setBmtsqEffciveDate(design.getDrEffectiveDate());
							sqrb.setBmtSQexpiryDate(design.getDrExpiryDate());
							sqrb.setBmtComments(design.getDrComments());
							
							if(null==design.getDrQuoteQty()){
								sqrb.setBmtQuoteQty(BLANK_SIGN);
							}else {
								sqrb.setBmtQuoteQty(String.valueOf(design.getDrQuoteQty()));
							}
							
							sqrb.setBmtSugCostAltCurrency(convertBigDecimalToStr(design.getDrCostAltCurrency()));
							sqrb.setBmtSugResaleAltCurrency(convertBigDecimalToStr(design.getDrResaleAltCurrency()));
							sqrb.setBmtCurrency(design.getDrAltCurrency());
							sqrb.setBmtExRateAltCurrency(convertBigDecimalToStr(design.getDrExchangeRate()));
							sqrb.setBmtSuggestMargin(design.getDrMargin());
							
						}
						
						if(qi.getQuoteItem() != null && StringUtils.isNotBlank(qi.getQuoteItem().getDpReferenceLineId())) {
							sqrb.setDpReferenceLineId(qi.getQuoteItem().getDpReferenceLineId());
						}
						
						if(qi.getQuoteItem() != null && qi.getQuoteItem().getQuote() != null 
								&& StringUtils.isNotBlank(qi.getQuoteItem().getQuote().getDpReferenceId())) {
							sqrb.setDpReferenceId(qi.getQuoteItem().getQuote().getDpReferenceId());
						}


						returnList.add(sqrb);
				    }

			}
			
			return returnList;
			
		}

		
		public static List<MyQuoteResultBean> noFinishQuoteHandling(List<MyQuoteResultBean> results) {
			for(MyQuoteResultBean sqrb : results ){
				 if(sqrb.getQuoteStage() != null && ! sqrb.getQuoteStage().equalsIgnoreCase(QuoteSBConstant.QUOTE_STAGE_FINISH))
				    {
				        sqrb.setQuoteNumber(BLANK_SIGN);
				        sqrb.setResaleIndicator(BLANK_SIGN);
				        sqrb.setMultiUsage(BLANK_SIGN);
				        sqrb.setMpq(BLANK_SIGN);
				        sqrb.setMoq(BLANK_SIGN);
				        sqrb.setLeadTime(BLANK_SIGN);
//				        sqrb.setAvnetQCComment("In Progress");
//						sqrb.setShipmentValidity(BLANK_SIGN);
						sqrb.setTermsAndConditions(BLANK_SIGN);
						sqrb.setReshedulingWindow(BLANK_SIGN);
						sqrb.setCancellWindow(BLANK_SIGN);
						sqrb.setVendorQuote(BLANK_SIGN);
						sqrb.setAllocationFlog(BLANK_SIGN);
						sqrb.setRevertVersion(BLANK_SIGN);
						sqrb.setFirstRfqCode(BLANK_SIGN);
						sqrb.setAvnetQuotePrice(BLANK_SIGN);
						sqrb.setFinalQuotationPrice(BLANK_SIGN);
						sqrb.setCostIndicator(BLANK_SIGN);
//						sqrb.setQuoteExpiryDate(BLANK_SIGN);
						sqrb.setPmoq(BLANK_SIGN);
						sqrb.setMultiUsage(BLANK_SIGN);
//						sqrb.setAvnetQCComment(BLANK_SIGN);
						//sqrb.setQtyIndicator(BLANK_SIGN);
						sqrb.setQuotedPn(BLANK_SIGN);
				}
			}
			
			return results;
				
		}
		
		

		
		


	public static String convertDoubleToStrForMargin(Double doub) {
		if (doub == null || doub == 0d || doub.equals(new Double(0d))) {
			// return "0.00"+PERCENT_SIGN;
			return BLANK_SIGN;
		} else {
			return formatMargin(doub);
		}
	}
	
	public static String convertBigDecimalToStr(BigDecimal bg) {
		if (null==bg) {
			// return "0.00"+PERCENT_SIGN;
			return BLANK_SIGN;
		} else {
			//changed by DamonChen
			try{
			return bg.setScale(5, BigDecimal.ROUND_HALF_UP).toString();
			}catch(Exception e){
				LOGGER.info("throw exception when covert bigdecimal to string, the bigdecimal is :"+bg);
				return BLANK_SIGN;
			}
			/*Double doub = bg.doubleValue();
			String result = BLANK_SIGN;
			if (doub != null)
				result = DF2.format(doub) ;
			LOGGER.info("call ....="+result);
			return result;*/
		}
	}

	public static String convertDoubleToStr(Double doub) {
		if (doub == null || doub == 0d || doub.equals(new Double(0d))) {
			return BLANK_SIGN;
		} else {
			String str1 = String.valueOf(doub);
			if(str1.contains("E")){
				String substring2 = str1.substring(0,3).replace(".", "");
				if(substring2.endsWith("0")){
					substring2=substring2.substring(0, 1);
				}
				String substring = str1.substring(str1.length()-1, str1.length());
				int parseInt = Integer.parseInt(substring);
				 String result = "";
				 for(int i = 0; i< parseInt; i++) {
					
				       result = result.concat("0");
				       //Removed by DamonChen@20181226
				       //System.out.println(result);
				    }
				 if(result.length()>1){
					 String resultString=result.substring(0,1)+"."+result.substring(1,result.length())+substring2;
					 
				return	 resultString;
				 }
			}else
			return String.valueOf(doub);
		}
		return null;
	}

	public static String convertDoubleToStrForResaleMargin(Double doub) {
		if (doub == null || doub == 0d || doub.equals(new Double(0d))) {
			return "0.00" + PERCENT_SIGN;
		} else {
			return formatMargin(doub);
		}
	}

	public static String convertDateToStr(Date date) {
		if (date == null) {
			return BLANK_SIGN;
		} else {

			String returnStr;
			try {
				SimpleDateFormat formatter = new SimpleDateFormat(
						"dd/MM/yyyy HH:mm:ss");
				returnStr = formatter.format(date);
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Exception in parse date : "+e.getMessage(), e);
				return BLANK_SIGN;
			}
			return returnStr;
		}

	}
	
	public static String convertIntToStr(int value) {
		if (value == 0) {
			return BLANK_SIGN;
		} else {
			return String.valueOf(value);
		}

	}

	public static String convertBoolean(boolean b) {
		if (b) {
			return YES_SIGN;
		} else
			return NO_SIGN;
	}

	public static String convertBoolean(Boolean b) {
		if (b == null)
			return NO_SIGN;
		else {
			if (b) {
				return YES_SIGN;
			} else
				return NO_SIGN;
		}
	}
        
	public static String formatMargin(Double margin) {
		String returnStr = "";
		if (margin != null)
			returnStr = DF2.format(margin) + PERCENT_SIGN;
		return returnStr;
	}

	public static String formatWindow(Integer windowInt) {
		String returnStr = "";
		if (windowInt != null) {
			returnStr = String.valueOf(windowInt);
		}
		return returnStr;

	}
        
	public static String convertDateToStr2(Date date) {
		if (date == null) {
			return BLANK_SIGN;
		} else {

			String returnStr;
			try {
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				returnStr = formatter.format(date);
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Exception in parse date : "+e.getMessage(), e);
				return BLANK_SIGN;
			}
			return returnStr;
		}
	}
	
	public static Date convertStrToDate(String date) {
		if (date == null) {
			return null;
		} else {
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			try {
				return formatter.parse(date);
			} catch (ParseException e) {
				LOGGER.log(Level.WARNING, "Exception in parse date : "+e.getMessage(), e);
			}
			return null;

		}
	}

	public static String formatAuthorizedGP(Double gp) {
		String returnStr = "";
		if (gp != null)
			returnStr = DF2.format(gp) + PERCENT_SIGN;
		return returnStr;
	}
	
	public static List convertPartMasterForQC(List<Material> materials, String region)
	{

		List<PartMasterResultBean> returnList = new ArrayList<PartMasterResultBean>();
		if(materials!=null && materials.size()>0) {
			for(Material matrial : materials ) {					
				    PartMasterResultBean sqrb = new PartMasterResultBean();
				    if(matrial.getManufacturer()!=null)
				    sqrb.setMfr(matrial.getManufacturer().getName());
				    sqrb.setPartNumber(matrial.getFullMfrPartNumber());
				    sqrb.setPackageType(matrial.getPackageType());
				    sqrb.setPackagingType(matrial.getPackagingType());
				    MaterialRegional mRegional = matrial.getMaterialRegaional(region);
				    if(null!=mRegional) {
				    	if(mRegional.getBizUnit()!=null) {
				    		sqrb.setRegion(mRegional.getBizUnit().getName());
				    	}
				    	sqrb.setSalesCostPart(mRegional.isSalesCostFlag()?QuoteSBConstant.OPTION_YES:QuoteSBConstant.OPTION_NO);
				    	if(mRegional.getProductGroup1() !=null) {
				    		sqrb.setProductGroup1(mRegional.getProductGroup1().getName());
				    	}
				    	if(mRegional.getProductGroup2() !=null) {
				    		sqrb.setProductGroup2(mRegional.getProductGroup2().getName());
				    	}
				    	sqrb.setProductGroup3(mRegional.getProductGroup3());
				    	sqrb.setProductGroup4(mRegional.getProductGroup4());
				    }
				   
					returnList.add(sqrb);
			    }

		}
		
		return returnList;
		
	}
	
	public static List convertToWorkingPlatFormBean(List<WorkingPlatformItemVO> workingPlatformItems) {
		List<WorkingPlatFormResultBean> returnList = new ArrayList<WorkingPlatFormResultBean>();
		LOGGER.fine("call convertToWorkingPlatFormBean");
		if(workingPlatformItems != null && workingPlatformItems.size() > 0) {
			for(WorkingPlatformItemVO vo : workingPlatformItems) {
				WorkingPlatFormResultBean bean = new WorkingPlatFormResultBean();
				bean.setAlternativeQuoteNumber(vo.getQuoteItem().getAlternativeQuoteNumber());
				bean.setRevertVersion(vo.getQuoteItem().getRevertVersion());
				bean.setFirstRfqCode(vo.getQuoteItem().getFirstRfqCode());
				bean.setPendingDay(String.valueOf(vo.getQuoteItem().getPendingDay()));
				bean.setStatus(vo.getQuoteItem().getStatus());
				bean.setValidFlagStr(vo.getValidFlagStr());
				bean.setResaleIndicatorStr(vo.getResaleIndicatorStr());
				bean.setQuotedPriceStr(vo.getQuotedPriceStr());
				bean.setQuotedQtyStr(vo.getQuotedQtyStr());
				bean.setQtyIndicatorStr(vo.getQtyIndicatorStr());
				bean.setAllocationFlagStr(vo.getAllocationFlagStr());
				bean.setAqccStr(vo.getAqccStr());
				bean.setQuotedPartNumber(vo.getQuotedPartNumber());
				bean.setMultiUsageFlagStr(vo.getMultiUsageFlagStr());
				bean.setVendorQuoteNumberStr(vo.getVendorQuoteNumberStr());
				bean.setInternalCommentStr(vo.getInternalCommentStr());
				bean.setInternalTransferComment(vo.getQuoteItem().getInternalTransferComment());
				bean.setVendorDebitNumberStr(vo.getVendorDebitNumberStr());
				bean.setVendorQuoteQtyStr(vo.getVendorQuoteQtyStr());
				bean.setPriceValidityStr(vo.getPriceValidityStr());
				bean.setShipmentValidityStr(vo.getShipmentValidityStr());
				bean.setPriceListRemarks1Str(vo.getPriceListRemarks1Str());
				bean.setPriceListRemarks2Str(vo.getPriceListRemarks2Str());
				bean.setPriceListRemarks3Str(vo.getPriceListRemarks3Str());
				bean.setPriceListRemarks4Str(vo.getPriceListRemarks4Str());
				bean.setProductGroup2Str(vo.getProductGroup2Str());
				bean.setDescription(vo.getDescriptionStr());
				bean.setCostIndicatorStr(vo.getCostIndicatorStr());
				bean.setCostStr(vo.getCostStr());
				bean.setBottomceStr(vo.getBottomPriceStr());
				bean.setMinSellPriceStr(vo.getMinSellPriceStr());
				bean.setProgramType(vo.getProgramType());
				//bean.setQuantityBreak(vo.getQuantityBreakStr());
				bean.setLeadTimeStr(vo.getLeadTimeStr());
				bean.setMpqStr(vo.getMpqStr());
				bean.setMoqStr(vo.getMoqStr());
				bean.setMovStr(vo.getMovStr());
				bean.setQuotationEffectiveDateStr(vo.getQuotationEffectiveDateStr());
				bean.setTermsAndConditionsStr(vo.getTermsAndConditionsStr());
				bean.setRescheduleWindowStr(vo.getRescheduleWindowStr());
				bean.setCancellationWindowStr(vo.getCancellationWindowStr());
				bean.setMfr(vo.getRequestedMfr());
				bean.setRequestedPartNumber(vo.getRequestedPartNumber());
				bean.setRequestedQty(convertNumber(String.valueOf(vo.getQuoteItem().getRequestedQty())));
				bean.setEau(convertNumber(String.valueOf(vo.getQuoteItem().getEau())));
//				convertDoubleToStr    
				bean.setTargetResale(convertDoubleToStr(vo.getQuoteItem().getTargetResale()));
				bean.setQuoteType(vo.getQuoteItem().getQuoteType());
				bean.setSubmissionDate(vo.getQuoteItem().getSubmissionDate());
				bean.setYourReference(vo.getQuoteItem().getQuote().getYourReference());
				bean.setEmployeeId(vo.getQuoteItem().getQuote().getSales().getEmployeeId());
				bean.setSalesman(vo.getQuoteItem().getQuote().getSales().getName());
				//fixed by DamonChen@20171124
				if (vo.getQuoteItem().getQuote().getTeam() != null) {
					bean.setTeam(vo.getQuoteItem().getQuote().getTeam().getName());
				}
				bean.setSoldToCustomerNameStr(vo.getSoldToCustomerNameStr());
				bean.setSoldToCustomerNumber(vo.getQuoteItem().getQuote().getSoldToCustomer().getCustomerNumber());
				bean.setCustomerType(vo.getQuoteItem().getQuote().getCustomerType());
				bean.setProjectName(vo.getQuoteItem().getProjectName());
				bean.setApplication(vo.getQuoteItem().getApplication());
				bean.setEnquirySegment(vo.getQuoteItem().getEnquirySegment());
				bean.setDesignLocation(vo.getQuoteItem().getDesignLocation());
				bean.setDesignInCatStr(vo.getDesignInCatStr());
				if(vo.getQuoteItem().getDrmsNumber() != null ) {
					bean.setDrmsNumber(vo.getQuoteItem().getDrmsNumber().intValue());
				}
				//for change to get end cust name david
				bean.setEndCustomerCodeStr(vo.getEndCustomerNameStr());
				if(vo.getQuoteItem().getShipToCustomer() != null) {
					bean.setShipToCustomerNumber(vo.getQuoteItem().getShipToCustomer().getCustomerNumber());

				}
				/*****
				if(vo.getQuoteItem().getSalesCost() !=null){
					bean.setSalesCostStr(vo.getQuoteItem().getSalesCost().toString());
				}
				if(vo.getQuoteItem().getSuggestedResale() !=null){
					bean.setSuggestedResaleStr(vo.getQuoteItem().getSuggestedResale().toString());
				}
				
				if(vo.getQuoteItem().getSalesCostType()!=null){
					bean.setSalesCostTypeName(vo.getQuoteItem().getSalesCostType().name());
				}
				**********/
				 
				bean.setSalesCostStr(vo.getSalesCostStr());
				bean.setSuggestedResaleStr(vo.getSuggestedResaleStr()); 
				bean.setSalesCostTypeName(vo.getSalesCostTypeName());
				bean.setSalesCostPart(vo.getQuoteItem().isSalesCostFlag() == true?YES_SIGN:NO_SIGN);
				bean.setCustomerGroupId(vo.getQuoteItem().getCustomerGroupId());
				bean.setShipToCustomerFullName(vo.getQuoteItem().getShipToCustomerFullName());
				bean.setEndCustomerCode(vo.getEndCustomerCodeStr());
				bean.setLoaFlagStr(vo.getLoaFlagStr());
				bean.setQuoteRemarks(vo.getQuoteItem().getQuote().getRemarks());
				bean.setCompetitorInformation(vo.getQuoteItem().getCompetitorInformation());
				bean.setSupplierDrNumber(vo.getQuoteItem().getSupplierDrNumber());
				bean.setPpSchedule(vo.getQuoteItem().getPpSchedule());
				bean.setMpSchedule(vo.getQuoteItem().getMpSchedule());
				bean.setItemRemarks(vo.getQuoteItem().getRemarks());
				bean.setSprFlagStr(vo.getSprFlagStr());
				bean.setReasonForRefresh(vo.getQuoteItem().getReasonForRefresh());
				bean.setFormNumber(vo.getQuoteItem().getQuote().getFormNumber());
				bean.setCopyToCS(vo.getQuoteItem().getQuote().getCopyToCS());
				bean.setSoldToPartyChineseName(vo.getSoldToChineseNameStr());
				bean.setSystemID("A" + vo.getQuoteItem().getId());
				bean.setQcRegion(vo.getQuoteItem().getQuote().getBizUnit().getName());
				bean.setDesignRegion(vo.getQuoteItem().getDesignRegion());
				bean.setSapPartNumberStr(vo.getQuoteItem().getSapPartNumber());
				//
				bean.setDollarLinkStr(vo.getDollarLinkStr());
				bean.setBuyCurrStr(vo.getBuyCurrStr());
				bean.setRfqCurrStr(vo.getRfqCurrStr());
				bean.setCostStr_Rfq(vo.getCostStr_rfq());
				bean.setQuotedPriceStr_Rfq(vo.getQuotedPriceStr_rfq());
				bean.setTargetResaleStr_Buy(vo.getTargetResaleStr_buy());
				bean.setExRateStr(vo.getExRateStr());
				
				
				if(vo.getQuoteItem().getQuoteItemDesign() != null) {
					bean.setBmtFlagCode(vo.getQuoteItem().getQuoteItemDesign().getBmtFlag().getDescription());
					bean.setDrMfrNo(vo.getQuoteItem().getQuoteItemDesign().getDrMfrNo());
					bean.setDrCost(vo.getQuoteItem().getQuoteItemDesign().getDrCost());
					bean.setDrResale(vo.getQuoteItem().getQuoteItemDesign().getDrResale());
					bean.setDrMfrQuoteNo(vo.getQuoteItem().getQuoteItemDesign().getDrMfrQuoteNo());
					bean.setDrEffectiveDate(vo.getQuoteItem().getQuoteItemDesign().getDrEffectiveDate());
					bean.setBmtSQExpiryDate(vo.getQuoteItem().getQuoteItemDesign().getDrExpiryDate());
					bean.setDrComments(vo.getQuoteItem().getQuoteItemDesign().getDrComments());
					bean.setDrQuoteQty(vo.getQuoteItem().getQuoteItemDesign().getDrQuoteQty());
					bean.setDrCostAltCurrency(vo.getQuoteItem().getQuoteItemDesign().getDrCostAltCurrency());
					bean.setDrResaleAltCurrency(vo.getQuoteItem().getQuoteItemDesign().getDrResaleAltCurrency());
					bean.setDrAltCurrency(vo.getQuoteItem().getQuoteItemDesign().getDrAltCurrency());
					bean.setDrExchangeRate(vo.getQuoteItem().getQuoteItemDesign().getDrExchangeRate());
					bean.setLastBmtUpdateTime(vo.getQuoteItem().getQuoteItemDesign().getLastUpdatedTime());
				}
				
				if(vo.getOrderQties()!=null && vo.getOrderQties().size()>0){
					bean.setPricerMinsell(TransferHelper.convertNormalSellPriceForExcel(vo.getOrderQties()));
					bean.setPricerSalesCost(TransferHelper.convertSalesCostForExcel(vo.getOrderQties()));
					bean.setPricerSuggestedResale(TransferHelper.convertSuggestedResaleForExcel(vo.getOrderQties()));
					bean.setOrderQty(TransferHelper.convertQrderQtyForExcel(vo.getOrderQties()));
				}

				
				returnList.add(bean);
			}
			
		}
		return returnList;
	}
	
	private static String convertNumber(String value) {	
		if(StringUtils.isNotBlank(value) && !"null".equals(value)){
			try {
				value = value.replaceAll(",", "");
				boolean withPlusSign = value.endsWith("+");
				BigDecimal bd = null;
				if(withPlusSign) {
					value = value.replaceAll("\\+", "");
				}
					
				bd = new BigDecimal(value);

				DecimalFormat formatter = new DecimalFormat("###,###,###,###");
				if(withPlusSign)
					return formatter.format(bd.longValue())+"+";
				else
					return formatter.format(bd.longValue());
			} catch (NumberFormatException ex) {
				LOGGER.log(Level.SEVERE, "Exception occured while conversion, for date: "+value.toString()+", "
				 		+ "Exception message: "+ex.getMessage(), ex);
			}
		}
		return "";
	}
	
    private static final String NEXT_LINE = "\r";

    private static final String REMOVE_ONE = "<div style='width:100%'><div>";
    
    private static final String REMOVE_TWO = "<div style='width:100%'><div style='text-align:center'>";
   
    private static final String REMOVE_THREE = "</div></div>";

    private static final String NEXT_ONE = "</div><div>";

    private static final String NEXT_TWO= "</div><div style='text-align:center'>";

    private static final String REMOVE_BLANK_VALUE = "<div style='width:100%'></div>";
    
    private final static String STR_SPACE = "<br>";


	public static List<CatalogMainVO> removeHtmlText(List<CatalogMainVO> vos) {
		if(vos != null && vos.size() > 0) {
			for(CatalogMainVO mainVo : vos) {
				String orderQty = replaceOrderQtyHtml(mainVo.getOrderQty());
				String normalSellPrice = replacePriceHtml(mainVo.getNormalSellPrice());
				System.out.println("normalSellPrice value is"+normalSellPrice);
				String salesCost = replacePriceHtml(mainVo.getSalesCost());
				String suggestedResale = replacePriceHtml(mainVo.getSuggestedResale());
				mainVo.setOrderQty(orderQty);
				mainVo.setNormalSellPrice(normalSellPrice);
				mainVo.setSalesCost(salesCost);
				mainVo.setSuggestedResale(suggestedResale);
			}
		}
		return vos;
	}
	
	private static String replaceOrderQtyHtml(String htmlText) {
		String text = "";
		if(StringUtils.isNotBlank(htmlText)) {
			text = htmlText.replaceAll(REMOVE_TWO, "").replaceAll(REMOVE_BLANK_VALUE, "").replaceAll(REMOVE_THREE, "").replaceAll(NEXT_TWO, NEXT_LINE);

		}
		return text;
	}
	

	private static String replacePriceHtml(String htmlText) {
		String text = "";
		if(StringUtils.isNotBlank(htmlText)) {
			text = htmlText.replaceAll(REMOVE_ONE, "").replaceAll(REMOVE_BLANK_VALUE, "").replaceAll(REMOVE_THREE, "").replace(STR_SPACE, "").replaceAll(NEXT_ONE,NEXT_LINE);

		}
		return text;
	}
	
}