package com.avnet.emasia.webquote.commodity.helper;


import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;

import com.avnet.emasia.webquote.commodity.util.StringUtils;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.Pricer;
import com.avnet.emasia.webquote.entity.ProgramPricer;
import com.avnet.emasia.webquote.entity.QuantityBreakPrice;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.QuoteToSoPending;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.exception.AppException;
import com.avnet.emasia.webquote.exception.CommonConstants;
import com.avnet.emasia.webquote.exception.WebQuoteRuntimeException;
import com.avnet.emasia.webquote.quote.ejb.QuoteToSoPendingSB;
import com.avnet.emasia.webquote.quote.ejb.SAPWebServiceSB;
import com.avnet.emasia.webquote.quote.ejb.SystemCodeMaintenanceSB;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil;
import com.avnet.emasia.webquote.utilities.DateUtils;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;
import com.avnet.emasia.webquote.vo.Oqmsp;
import com.avnet.emasia.webquote.vo.ProgramMaterialQtyRange;
import com.avnet.emasia.webquote.vo.RfqItemVO;
import com.avnet.emasia.webquote.web.quote.CommonBean;
import com.sap.document.sap.soap.functions.mc_style.TableOfZquoteMsg;
import com.sap.document.sap.soap.functions.mc_style.ZquoteMsg;
import com.sap.document.sap.soap.functions.mc_style.ZwqCustomer;

public class ProgRfqSubmitHelper extends CommonBean  implements java.io.Serializable{


	private static final long serialVersionUID = 8021351838428225340L;
	private static Logger logger = Logger.getLogger(ProgRfqSubmitHelper.class.getName());
	
	private final static String INDICATOR_LIMIT = "Limit";
	private final static String INDICATOR_EXACT = "Exact";
	private final static String INDICATOR_MOQ = "MOQ";
	private final static String INDICATOR_MPQ =  "MPQ";
	private final static String INDICATOR_PERSENT = "%";
	private final static String INDICATOR_PLUS = "+";
	private final static String INDICATOR_SUBSTR = "-";
	private static final String CALL_FOR_SQ=ResourceMB.getText("wq.message.callforsq");
	private static double nullPrice = -999999d;
	@EJB
	private SysConfigSB sysConfigSB;
	@EJB
	private SAPWebServiceSB sapWebServiceSB;
	@EJB
	private QuoteToSoPendingSB quoteToSoPendingSB;
	
	@EJB
	private SystemCodeMaintenanceSB sysCodeMaintSB;
	
	public static List<String> standardTermsCondition = new ArrayList<String>();
	static{
		standardTermsCondition.add(ResourceMB.getText("wq.message.t&C"));
		standardTermsCondition.add("A. "+ResourceMB.getText("wq.message.t&C.delSchedule")+".");
		standardTermsCondition.add("B. "+ResourceMB.getText("wq.message.t&C.leadTime")+".");
		standardTermsCondition.add("C. "+ResourceMB.getText("wq.message.t&C.orderQty")+".");
	}
	public ProgRfqSubmitHelper() {
		// TODO Auto-generated constructor stub
	}
    
	/*public static QuoteItem processAutoQuoteItem(QuoteItem item) throws ParseException
	{
		
		//PROGRM PRICER ENHANCEMENT
		//ProgramMaterial pm = item.getRequestedMaterialForProgram().getProgramMaterial();
		ProgramPricer pm = item.getRequestedProgramMaterialForProgram();
		//9.If a RFQ can be automatically quoted and its target resale is empty, 
		//the quoted price of the quote for the RFQ equals to the price of the range, 
		//which the required quantity of the RFQ falls to.
		//10.If a RFQ can be automatically quoted and its target resale is not empty, 
		//the quoted price of the quote for the RFQ equals to the target resale.
		if(item.getTargetResale() == null)
		{
			item.setQuotedPrice(getPrice(item));  //Fix incident INC0198809  June Guo 20150907
		}
		else
		{
			item.setQuotedPrice(item.getTargetResale());
		}
		
		//11.If a RFQ can be automatically quoted and its required quantity is not greater than the MOQ of 
		//corresponding program item. The Avnet quoted qty of the quote for the RFQ equals to the MOQ. 
		//If the required quantity is greater than the MOQ, The Avnet quoted qty of the quote equals to 
		//the result of rounding the required quantity up to the least multiple of the MPQ of the program item. 
		//Then if the MOV of the program item is not empty and the Avnet quoted qty is less than the result of 
		//dividing the MOV by the minimum cost of the program item, Avnet quoted qty equals to the result of 
		//dividing MOV by minimum cost, and rounded up to the least of the MPQ
		int avnetQuotedQty = 0;
		if(item.getRequestedQty().intValue() <= item.getMoq().intValue())
		{
			avnetQuotedQty= item.getMoq().intValue();
		}
		else
		{
			avnetQuotedQty=mpqRoundUp(item.getRequestedQty().intValue(),item.getMpq().intValue());
		}
		
		
		
		//new logic, no need to consider MOV
		
		if(programItem.getMov() != Constant.DEFAULT_FLOAT_VALUE &&
				avnetQuotedQty < (programItem.getMov()/ programItem.getMinimumCost()) &&
				programItem.getQuantityIndicator().equals("Exact")){
			avnetQuotedQty = mpqRoundUp(programItem.getMov()/ programItem.getMinimumCost(),programItem.getMpq());
		}
		
		item.setQuotedQty(avnetQuotedQty);
		
		//12.If a RFQ can be automatically quoted and the quantity indicator of corresponding program 
		//item is not "Limit", the PMOQ of the quote for the RFQ is calculated based on the quantity indicator of 
		//the program item and the Avnet quoted qty of the quote.	If the quantity indicator is "Exact", 
		//the PMOQ equals to the Avnet quoted qty. If the quantity indicator is "MOQ", 
		//the PMOQ equals to the MOQ of the program item followed by a plus sign. 
		//If the quantity indicator is "MPQ", the PMOQ equals to the MPQ of the program item followed by a plus sign. 
		//If the quantity indicator is percentage, the PMOQ equals to the result of multiplying the Avnet quoted qty by 
		//the quantity indicator, followed by a plus sign. Regarding to the calculation of the PMOQ, 
		//refer to 9.1.1.3 PMOQ Illustration.
		//13.If the RFQ can be automatically quoted and quantity indicator of the program item is "Limit", 
		//====================================================================================================
		//for fixed the issue 1102
		//IF the required quantity is less than MOQ. Avent quoted quantity will instead of required quantity .
		//====================================================================================================
		//PMOQ is calculated based on the range of the neighbouring quantity breaks, 
		//where the required quantity of the RFQ falls. If the required quantity is not less than 
		//the last quantity break of the program item, and call for quote of the program item is "No", 
		//PMOQ equals to the last quantity break followed by a plus sign. Otherwise, the lower limit of the PMOQ 
		//is calculated by rounding the lower quantity break up to the least multiple of 
		//the MPQ of the program item. The upper limit of the PMOQ is calculated by rounding the upper quantity 
		//break down to the greatest multiple of the MPQ. Regarding to the calculation of the PMOQ for 
		//limit quantity indicator, refer to 9.1.1.3 PMOQ Illustration for Limit Quantity Indicator.
		//logger.info("Set PMOQ");
		item.setPmoq(getPMOQ(item));
		
		//14.If a RFQ can be automatically quoted and the validity of its corresponding program item is 
		//in date format, PO expiry date of the quote for the RFQ equals to the validity.
		//15.If a RFQ can be automatically quoted and the validity of its corresponding program item is 
		//in numeric format, the PO expiry date of the quote for the RFQ is the 
		//date after <the validity of the program item> days from current date.
		//PROGRM PRICER ENHANCEMENT
		//String validity = item.getRequestedMaterialForProgram().getProgramMaterial().getPriceValidity();
		String validity = item.getRequestedProgramMaterialForProgram().getPriceValidity();
		item.setPriceValidity(validity);
		if(validity!=null)
		{
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			if(DateUtils.isDate(validity))
			{			
				item.setPoExpiryDate(dateFormat.parse(validity));
			}
			else if(org.apache.commons.lang.StringUtils.isNumeric(validity))
			{
				   
				   GregorianCalendar calendar = new GregorianCalendar(); 
				   calendar.setTime(new Date()); 
				   calendar.add(GregorianCalendar.DATE, Integer.parseInt(validity)); 
				   item.setPoExpiryDate(calendar.getTime());
			}
		}
		

		
		//16.	If a RFQ can be automatically quoted, the quote expired date of the quote for the RFQ is 
		//the date 1 day before the PO expired date of the quote.
        item.setQuoteExpiryDate(DateUtils.addDay(item.getPoExpiryDate(), -1));

		
		//17.If a RFQ can be automatically quoted and the shipment validity of its corresponding program item is 
		//in date format, the shipment validity of the quote for the RFQ equals to 
		//the shipment validity of the program item
		//18.If a RFQ can be automatically quoted and the shipment validity of its corresponding program item is 
		//in numeric format, the shipment validity of the quote for the RFQ is 
		//the date after <the shipment validity of the program item> days from current date.
        Date shipmentValidity = pm.getShipmentValidity();
        if(shipmentValidity!=null)
		item.setShipmentValidity(shipmentValidity);
		
		
		//19.If a RFQ can be automatically quoted, the fields of its corresponding program item, 
		//minimum cost, MPQ, MOQ, MOV, cost indicator, lead time, quantity indicator, allocation flag, 
		//terms and conditions, Avnet quote centre comments will be brought to the corresponding fields 
		//in the quote for the RFQ. The minimum cost will be brought to the field, cost, 
		//and the quantity indicator will be brought to the field, qty indicator of the quote for the RFQ 
		//and Avnet quote centre comments will be brought to the field, Avnet quote centre comment of 
		//the quote for the RFQ.
        //item.setCost(pm.getProgramMinimumCost());
        //for defect 378
        item.setCost(pm.getCost());
        item.setMoq(pm.getMpq());
        item.setMoq(pm.getMoq());
        item.setMov(pm.getMov());
        if(pm.getCostIndicator()!=null)
        item.setCostIndicator(pm.getCostIndicator().getName());
        item.setLeadTime(pm.getLeadTime());
        item.setQtyIndicator(pm.getQtyIndicator());
        item.setAllocationFlag(pm.getAllocationFlag());
        item.setTermsAndConditions(pm.getTermsAndConditions());
        item.setQcComment(pm.getAvnetQcComments());
        // New field "Quotation effective date"
        if(pm.getQuotationEffectiveDate()!=null)
        item.setQuotationEffectiveDate(pm.getQuotationEffectiveDate());
		
		//20. If a RFQ can be automatically quoted, the resale indicator of the quote for the RFQ equals to 
		//"00" followed by the last 2 characters of the resale indicator of its corresponding program item
        String resaleIndicator = pm.getResaleIndicator();
        if(resaleIndicator!=null)
		item.setResaleIndicator("00" + resaleIndicator.substring(2));
		
		//21.If a RFQ can be automatically quoted, the resales min of the quote for the RFQ equals to the first 2 characters 
		//of the resale indicator of the quote.
        
		if(resaleIndicator!=null)
		item.setResaleMin(Double.parseDouble(resaleIndicator.substring(0,2)));
		//22.If a RFQ can be automatically quoted and the last 2 characters of the resale indictor of 
		//the quote for the RFQ is not "AA", the resales max of the quote equals to the last 2 characters of 
		//the resale indicator. If the last 2 characters of the resale indictor is "AA, 
		//the resales max equals to "9999".
		if(resaleIndicator!=null)
		if(resaleIndicator.substring(2).equals("AA"))
		{
			item.setResaleMax(9999d);
		}
		else
		{
			item.setResaleMax(new Double(resaleIndicator.substring(2)));
		}
		
		//23. If a RFQ can be automatically quoted, the rescheduling window and the cancellation window of 
		//the quote for the RFQ are empty, and multi-usage of the quote for the RFQ is "No".
		item.setRescheduleWindow(null);
		item.setCancellationWindow(null);
		item.setMultiUsageFlag(false);
		
		//TO-DO
		item.setStage(QuoteSBConstant.QUOTE_STAGE_FINISH);
		item.setStatus(QuoteSBConstant.RFQ_STATUS_AQ);
		
		return item;
	}*/
	/*
	 * Check if this quote item is avaiable for auto quote.
	 * @return: if it can auto quote , return true.
	 *  otherwise, return false.
	 * @param:  quote item object.
	 */
	/*public static boolean checkAutoQuoteForItem(QuoteItem item) throws IllegalArgumentException,ParseException
	{
			   //PROGRM PRICER ENHANCEMENT
			   //ProgramMaterial pm = item.getRequestedMaterialForProgram().getProgramMaterial();
			   ProgramPricer pm = item.getRequestedProgramMaterialForProgram(); 
		       //1. If the AQ flag of a program item is "No", the RFQ will be sent to program working platform
		       //logger.info("Step1 ");
		       if(pm.getSpecialItemFlag()!=null && pm.getAqFlag()==false){
					return false;
				}
				
				//2. If the call for quote of a program item is "Yes" and the required quantity of the RFQ 
				//for the program item is not less than the last quantity break of the program item, 
				//the RFQ will be sent to program working platform
		        //Change: improved the logic. if there is not quantity break price, return false;
		        //logger.info("Step2 ");
		        if(pm.getProgramCallForQuote()==true)
				{
					List<QuantityBreakPrice> prices = pm.getQuantityBreakPrices();
					if(prices!= null && prices.size()>0)
					{
						QuantityBreakPrice qtyPrice = prices.get(prices.size()-1);
						if(item.getRequestedQty().compareTo(qtyPrice.getQuantityBreak())>=0)
						{
							return false;
						}
					}
					else
					{
						return false;
					}
					
				}
				
				//3. If program effective date of a program item is later than current date or 
				//program closing date of the program is earlier than current date, 
				//the program item is considered as inactive. 
				//RFQ for inactive program items will be sent to program working platform.
		        //logger.info("Step3 ");
				if(! isActive(item)){
					return false;
				}
				
				//4.If the validity of a program item is in date format and not later than current date, 
				//the RFQ for the program item will be sent to program working platform. 
				//5.If the validity of the program item is in numeric format and not greater than 0, 
				//the RFQ for the program item will be sent to program working platform
				//logger.info("Step4 ");
				if(!isValidity(pm.getPriceValidity())){
					return false;
				}
				
				
				
				//6.If the required quantity of a RFQ does not fall to any range of quantity breaks of 
				//corresponding program item, the RFQ will be sent to program working platform
				//logger.info("Step5 ");
				if(!isQtyInRangesNew(item)){
					return false;
				}

				
				
				//8. If the target resales of the RFQ for the program item is not empty and not within the price tolerance, 
				//the RFQ will be sent to program working platform. 		
				//logger.info("Step6 ");
//				if((item.getTargetResale()!=null && item.getTargetResale().doubleValue()!= 0d)  && !(isPriceInTolerance(item))){
//					return false;
//				}
				Double inPrice = isInRangesPriceNew(item);
				if(inPrice!=null && inPrice.doubleValue()==nullPrice)
				{
					return false;
				}
				
				if((item.getTargetResale()!=null && item.getTargetResale().doubleValue()!= 0d))
				{
					if(inPrice!=null && item.getTargetResale().doubleValue()<inPrice.doubleValue())
					{
						return false;
					}
				}
				
				//logger.info("Step7 ");
				return true;
	}*/
	
	public static boolean isActive(QuoteItem item)
	{
		//PROGRM PRICER ENHANCEMENT
		//ProgramMaterial pm = item.getRequestedMaterialForProgram().getProgramMaterial();
		ProgramPricer pm = item.getRequestedProgramMaterialForProgram(); 
		Calendar calEffectiveDate = Calendar.getInstance();
		calEffectiveDate.setTime(pm.getProgramEffectiveDate());
		
		Calendar calClosingDate = Calendar.getInstance();
		calClosingDate.setTime(pm.getProgramClosingDate());
		calClosingDate.add(Calendar.DATE, 1);
		
		Calendar today = DateUtils.getCurrentAsiaCal();

		if(calEffectiveDate.after(today) || calClosingDate.before(today)){
			return false;
		}
		return true;
	}
	
	
	public static boolean isQtyInRanges(QuoteItem item)
	{
		int requestQty = item.getRequestedQty().intValue();
		if(item.getMpq()!=null && requestQty < item.getMpq().intValue())
		{
			requestQty = item.getMpq();
		}
		List<ProgramMaterialQtyRange> ranges =  getQtyRanges(item);
		for(ProgramMaterialQtyRange range : ranges)
		{
			if(range.isQtyInRange(requestQty))
			{
				return true;
			}
		}
		return false;

	}
	
	
	
	public static boolean isPriceInTolerance(QuoteItem item)  throws IllegalArgumentException{
		List<ProgramMaterialQtyRange> ranges = getQtyRanges(item);
		for(ProgramMaterialQtyRange range : ranges)
		{
			if(range.isQtyAndPriceInRange(item.getRequestedQty().intValue(), item.getTargetResale().doubleValue())){
				return true;
			}
		}
		return false;
	}
	
	public static Double isInRangesPrice(QuoteItem item)
	{
		Double returnPrice = null; 
		List<ProgramMaterialQtyRange> ranges =  getQtyRanges(item);
		for(ProgramMaterialQtyRange range : ranges)
		{
			if(range.isQtyInRange(item.getRequestedQty().intValue()))
			{
				returnPrice = range.getPrice();
				break;
			}
		}
		return returnPrice;

	}
	public static List<ProgramMaterialQtyRange> getQtyRanges(QuoteItem item) throws IllegalArgumentException
	{
		List<ProgramMaterialQtyRange> qtyRanges = new ArrayList<ProgramMaterialQtyRange>();
		//PROGRM PRICER ENHANCEMENT
		//ProgramMaterial pm = item.getRequestedMaterialForProgram().getProgramMaterial();
		ProgramPricer pm = item.getRequestedProgramMaterialForProgram();
		List<QuantityBreakPrice> priceList = pm.getQuantityBreakPrices();
		
		if(priceList!=null && priceList.size()>0)
		{
			int size = priceList.size();
			for(int i=0; i < size; i++)
			{
				int qtyFrom = 0;
				double qtyTo = 0;
				double price = 0;
				double priceToleranceFrom = 0;
				double priceToleranceTo = 0;
				boolean callForQuote = false;
/*				if((i+1) < size)
				{
					qtyTo = priceList.get(i+1).getQuantityBreak();
				}
				else
				{
					if(pm.getProgramCallForQuote()!=null && pm.getProgramCallForQuote()==true)
					{
						callForQuote = true;
						qtyTo= Integer.MAX_VALUE;
					}
					else
					{
						callForQuote = false;
						if(price == 0d)
						{
							qtyTo= priceList.get(i).getQuantityBreak();
							break;
						}else{
							qtyTo = Integer.MAX_VALUE;
						}
					}
				}*/
				
				//above conment is replace of calling function setCommonData by Damonchen
				setCommonData(priceList, pm, i, size, qtyFrom, qtyTo, price, priceToleranceFrom, priceToleranceTo, callForQuote);
//				logger.fine("qtyFrom: "+ qtyFrom);
//				logger.fine("qtyTo: "+ qtyTo);
//				logger.fine("from: "+ mpqRoundUp(qtyFrom, pm.getMpq().intValue()));
//				logger.fine("to: "+ mpqRoundDown(qtyTo,  pm.getMpq().intValue()));
//				logger.fine("priceToleranceFrom: "+ priceToleranceFrom);
//				logger.fine("priceToleranceTo: "+ priceToleranceTo);
				
				qtyRanges.add(new ProgramMaterialQtyRange(mpqRoundUpPi(qtyFrom, pm.getMpq().intValue()), mpqRoundDownPi(qtyTo,  pm.getMpq().intValue()),price,priceToleranceFrom,priceToleranceTo,callForQuote));
			}
		}
		
		if(qtyRanges.size() >= 6){
			for(int i = qtyRanges.size()-1; i >=5 ; i--){
				qtyRanges.remove(i);
			}
		}

		return qtyRanges;
	}
	
	
	private static double getPriceToleranceFrom(String resaleIndicator, double price)throws IllegalArgumentException{
		if(null==resaleIndicator || resaleIndicator.length() != 4){
			throw new IllegalArgumentException(CommonConstants.COMMON_RESALEINDICATOR_MUST_BE_4_CHARATERS_LANG_FOR_PROGRAMMATERIAL);
		}
		String indicator = resaleIndicator.substring(0,2);
		double lowerLimit = Double.parseDouble(indicator);
		lowerLimit = lowerLimit / 100;
		return price * (1 - lowerLimit);
	}

	private static double getPriceToleranceTo(String resaleIndicator, double price)throws IllegalArgumentException{
		if(null==resaleIndicator || resaleIndicator.length() != 4){
			throw new IllegalArgumentException(CommonConstants.COMMON_RESALEINDICATOR_MUST_BE_4_CHARATERS_LANG_FOR_PROGRAMMATERIAL);
		}
		String indicator = resaleIndicator.substring(2);
		if(indicator.equalsIgnoreCase("AA")){
			return Integer.MAX_VALUE;
		}
		double higherLimit = Double.parseDouble(indicator);
		higherLimit = higherLimit / 100;
		return price * (1 + higherLimit);
	}
	
	private static int mpqRoundUp(double qty, int mpq){
		double  d = qty / mpq;
		if(d==0){
			d = 1;
		}
		return  (int) (mpq * Math.ceil(d));
	}
	
	private static int mpqRoundDown(double qty, int mpq){
		double d = qty / mpq;
		int i = (int) (mpq * Math.floor(d));
		if (i == (int)qty){
			i = (int)qty - mpq;
		}
		return  (int) (mpq * Math.floor(d));
	}	
	
	private static int mpqRoundUpPi(double qty, int mpq){
		double  d = qty / mpq;
		if(d==0){
			d = 1;
		}
		return  (int) (mpq * Math.ceil(d));
	}
	
	private static int mpqRoundDownPi(double qty, int mpq){
		double d = qty / mpq;
		int i = (int) (mpq * Math.floor(d));
		if (i == (int)qty){
			i = (int)qty - mpq;
		}
		return  i;
	}	
	
	public static double getPrice(QuoteItem item) {
		if (! isQtyInRanges(item))
		{
			throw new WebQuoteRuntimeException(CommonConstants.WQ_EJB_MASTER_DATA_QUANTITY_IS_NOT_IN_ANY_RANGE, new Object[]{item.getRequestedQty()});
		}
		List<ProgramMaterialQtyRange> ranges = getQtyRanges(item);
		for(ProgramMaterialQtyRange range : ranges)
		{
			int requestQty = item.getRequestedQty().intValue();
			if(item.getMpq()!=null && requestQty< item.getMpq().intValue())
			{
				requestQty=item.getMpq().intValue();
			}
			if(range.isQtyInRange(requestQty))
			{
				return range.getPrice();
			}
		}
		return 0;
	}


	
	private static String getPMOQ(QuoteItem item)
	{
		
		//logger.info("call getPMOQ");
		//PROGRM PRICER ENHANCEMENT
		//String qtyIndicator = item.getRequestedMaterialForProgram().getProgramMaterial().getQtyIndicator();
		String qtyIndicator = item.getRequestedProgramMaterialForProgram().getQtyIndicator();
		//logger.info("call getPMOQ qtyIndicator:" + qtyIndicator);
		String pmoq = "";
		
		if(! qtyIndicator.equalsIgnoreCase(INDICATOR_LIMIT))
		{
			//logger.info("No Limit type indicator");
			if(qtyIndicator.equalsIgnoreCase(INDICATOR_EXACT))
			{
				pmoq = String.valueOf(item.getQuotedQty().intValue());
			}
			else if(qtyIndicator.equalsIgnoreCase(INDICATOR_MOQ))
			{
				pmoq = item.getMoq() + INDICATOR_PLUS;
			}
			else if(qtyIndicator.equalsIgnoreCase(INDICATOR_MPQ))
			{
				pmoq = item.getMpq() + INDICATOR_PLUS;
			}
			else if(qtyIndicator.contains(INDICATOR_PERSENT))
			{
				String s = qtyIndicator.substring(0, qtyIndicator.length()-1);
				double percent = Double.parseDouble(s);
				int mpq = item.getMpq().intValue();
				double dpmoq = item.getQuotedQty() * percent / 100;
				dpmoq = mpqRoundUp(dpmoq,mpq); 
				
				pmoq = String.valueOf(dpmoq);
				//remove the .0 at the end of a double data type
				pmoq = pmoq.substring(0,pmoq.length()-2);
				pmoq = pmoq + "+";
			}
		}
		else
		{
			
			//logger.info("Limit type indicator");
			int mpq = item.getMpq().intValue();
			//PROGRM PRICER ENHANCEMENT
			//ProgramMaterial pm = item.getRequestedMaterialForProgram().getProgramMaterial();
			ProgramPricer pm = item.getRequestedProgramMaterialForProgram();
			List<QuantityBreakPrice> prices = pm.getQuantityBreakPrices();
			List<ProgramMaterialQtyRange> qtyRanges = getQtyRanges(item);
//			if(qtyRanges!=null && qtyRanges.size()>0)
//			{
//				for(ProgramMaterialQtyRange tt : qtyRanges)
//				{
//					logger.info("TT: "+ tt.toString());
//				}
//			}
			QuantityBreakPrice lastQbp = prices.get(prices.size()-1);
//			logger.info("item.getRequestedQty().intValue(): "+ item.getRequestedQty().intValue());
//			logger.info("lastQbp.getQuantityBreak().intValue(): "+ lastQbp.getQuantityBreak().intValue());
//			logger.info("pm.getProgramCallForQuote(): "+ pm.getProgramCallForQuote());
			if(item.getRequestedQty().intValue() >= lastQbp.getQuantityBreak().intValue() && pm.getProgramCallForQuote()==false)
			{
				pmoq = String.valueOf(mpqRoundUp(lastQbp.getQuantityBreak().intValue(),mpq)) + INDICATOR_PLUS;
			}
			else
			{
				//change for issue 1102
				int comparedInt = item.getRequestedQty().intValue();
				if(item.getRequestedQty().intValue() <= item.getMoq().intValue())
				{
					comparedInt= item.getMoq().intValue();
				}
				for(ProgramMaterialQtyRange qtyRange : qtyRanges)
				{
					if(comparedInt >= qtyRange.getQtyFrom() && comparedInt  <= qtyRange.getQtyTo())
					{
//						logger.info("mpqRoundUp(qtyRange.getQtyFrom(),mpq): "+ mpqRoundUp(qtyRange.getQtyFrom(),mpq));
						//logger.info("mpqRoundDown(qtyRange.getQtyTo(),mpq): "+ mpqRoundDown2(qtyRange.getQtyTo(),mpq));
						pmoq = String.valueOf(mpqRoundUp(qtyRange.getQtyFrom(),mpq));
						pmoq = pmoq + "-" + String.valueOf(mpqRoundDown(qtyRange.getQtyTo(),mpq));
						break;
					}
				}
			}
		}
		
		return pmoq;
	}
	
	
	

	/*
	public static QuoteItem setDefaultValue(Quote quote,  User user , QuoteItem item)
	{
		//PROGRM PRICER ENHANCEMENT
		ProgramPricer pm = item.getRequestedProgramMaterialForProgram();
		item.setSprFlag(false);
		//item.setFSAS("No");
		item.setBmtCheckedFlag(false);
		//item.setProgramType(item.getRequestedMaterialForProgram().getProgramMaterial().getProgramType());
		//Material restructure and quote_item delinkage. changed on 10 Apr 2014.
		if(pm.getProgramType()!=null)
		item.setProgramType(pm.getProgramType().getName());
		//Material product group change.
		item.setProductGroup2(item.getRequestedMaterialForProgram().getProductGroup2());
		item.setProductGroup1(item.getRequestedMaterialForProgram().getProductGroup2());
		item.setProductGroup3(item.getRequestedMaterialForProgram().getProductGroup2().getName());
		item.setProductGroup4(item.getRequestedMaterialForProgram().getProductGroup2().getName());
//		MaterialType mt = new MaterialType();
//		mt.setName("PROGRAM");
//		item.setMaterialType(mt);
		//Material restructure and quote_item delinkage. changed on 10 Apr 2014.
		item.setMaterialTypeId("PROGRAM");

		item.setStage(QuoteSBConstant.QUOTE_STAGE_PENDING);
		item.setStatus(QuoteSBConstant.RFQ_STATUS_QC);
		item.setInternalComment("No");
		item.setAllocationFlag(false);
		item.setRevertVersion("0A");
		item.setValidFlag(true);
		
		item.setSentOutTime(DateUtils.getCurrentAsiaDateObj());
		item.setLastUpdatedOn(DateUtils.getCurrentAsiaDateObj());
		
	    item.setLoaFlag(false);
		 
        item.setCost(pm.getCost());
        
	    item.setSoldToCustomer(quote.getSoldToCustomer());
	    //item.setLastUpdatedBy(user);
	    //Material restructure and quote_item delinkage. changed on 10 Apr 2014.
	    item.setLastUpdatedBy(user.getEmployeeId());
	    item.setLastUpdatedName(user.getName());
	    //Fixed the issue 1543 and 1548 on Feb 24, 2014
	    item.setSubmissionDate(DateUtils.getCurrentAsiaDateObj());
	    item.setQuotedMaterial(item.getRequestedMaterialForProgram());
		
		return item;
	}
	*/
	
	
	public static List<Oqmsp> getOpmspList(ProgramPricer pm)
	{
		
		List<Oqmsp> returnList = new ArrayList<Oqmsp>();
		List<QuantityBreakPrice> progItemBreakList = pm.getQuantityBreakPrices();
		if(progItemBreakList==null)
			return null;
		
		Oqmsp oqm ;
		QuantityBreakPrice pibp1, pibp2;
		int orgBreakSize = progItemBreakList.size();
		int breakSize = orgBreakSize;
		int lastQ = 0;
		if(breakSize>5)
			breakSize= 5;
		
		for(int i =0 ; i <breakSize; i ++)
		{
			oqm = new Oqmsp();
			if(breakSize==1)
			{
				pibp1 = (QuantityBreakPrice)progItemBreakList.get(i);
				oqm.setStartQuantity(pm.getMpq());
				oqm.setEndQuantity(Integer.MAX_VALUE);
				//oqm.setOq(StringUtils.localizedNumberFormat(pm.getMpq(),new ResourceMB().getResourceLocale())+INDICATOR_PLUS);
				oqm.setOq(StringUtils.thousandFormat(pm.getMpq())+INDICATOR_PLUS);
				if(pm.getProgramCallForQuote()==true)
				{
					oqm.setMsp(CALL_FOR_SQ+convertBigDecimalToStr(pibp1));
					oqm.setPrice(Double.MAX_VALUE);
				}
				else
				{
					oqm.setMsp(String.valueOf(pibp1.getQuantityBreakPrice())+convertBigDecimalToStr(pibp1));
					oqm.setPrice(pibp1.getQuantityBreakPrice());
				}
				returnList.add(oqm);
			}
			else
			{
				if(i+1==breakSize)
				{
					//log.info("getOpmspList | i==breakSize i= "+i );
					if(orgBreakSize>5)
					{
						//log.info("getOpmspList | orgBreakSize >5 " );
						pibp1 = (QuantityBreakPrice)progItemBreakList.get(i);
						pibp2 = (QuantityBreakPrice)progItemBreakList.get(i+1);
						//log.info(" firstQ==  MPQ:"+pi.getMpq()+" first:"+lastQ+" max:"+pibp1.getQuantity()+" "+pi.getMpq());
						int firstQ=roundUp(pm.getMpq(),lastQ,pibp1.getQuantityBreak()+pm.getMpq());
						lastQ=roundUp(pm.getMpq(),lastQ, pibp2.getQuantityBreak());
						//log.info(" lastQ==  MPQ:"+pi.getMpq()+" first:"+lastQ+" max:"+pibp2.getQuantity());
						oqm.setStartQuantity(firstQ);
						oqm.setEndQuantity(lastQ);
						oqm.setPrice(pibp1.getQuantityBreakPrice());
						//oqm.setOq(StringUtils.localizedNumberFormat(firstQ,new ResourceMB().getResourceLocale())+INDICATOR_SUBSTR+StringUtils.localizedNumberFormat(lastQ,new ResourceMB().getResourceLocale()));
						oqm.setOq(StringUtils.thousandFormat(firstQ)+INDICATOR_SUBSTR+StringUtils.thousandFormat(lastQ));
						oqm.setMsp(String.valueOf(pibp1.getQuantityBreakPrice())+convertBigDecimalToStr(pibp1));
						returnList.add(oqm);
					}
					else
					{
						pibp1 = (QuantityBreakPrice)progItemBreakList.get(i);
						if(pm.getProgramCallForQuote()==true)
						{
							int firstq = roundUp(pm.getMpq(),lastQ,pibp1.getQuantityBreak()+pm.getMpq());
							oqm.setStartQuantity(firstq);
							oqm.setEndQuantity(Integer.MAX_VALUE);
							//oqm.setOq(StringUtils.localizedNumberFormat(firstq,new ResourceMB().getResourceLocale())+INDICATOR_PLUS);
							oqm.setOq(StringUtils.thousandFormat(firstq)+INDICATOR_PLUS);
							oqm.setMsp(CALL_FOR_SQ+convertBigDecimalToStr(pibp1));
							oqm.setPrice(Double.MAX_VALUE);
							returnList.add(oqm);
						}
						else
						{
							if(pibp1.getQuantityBreakPrice()!=null && pibp1.getQuantityBreakPrice().doubleValue()!=nullPrice)
							{
								int firstq = roundUp(pm.getMpq(),lastQ,pibp1.getQuantityBreak()+pm.getMpq());
								oqm.setStartQuantity(firstq);
								oqm.setEndQuantity(Integer.MAX_VALUE);
								//oqm.setOq(StringUtils.localizedNumberFormat(firstq,new ResourceMB().getResourceLocale())+INDICATOR_PLUS);
								oqm.setOq(StringUtils.thousandFormat(firstq)+INDICATOR_PLUS);
								oqm.setPrice(pibp1.getQuantityBreakPrice());
								oqm.setMsp(String.valueOf(pibp1.getQuantityBreakPrice())+convertBigDecimalToStr(pibp1));
								returnList.add(oqm);
							}
						}
					}

				}
				else
				{
					pibp1 = (QuantityBreakPrice)progItemBreakList.get(i);
					pibp2 = (QuantityBreakPrice)progItemBreakList.get(i+1);
					int firstQ=roundUp(pm.getMpq(),lastQ,pibp1.getQuantityBreak()+pm.getMpq());
					lastQ=roundUp(pm.getMpq(),lastQ, pibp2.getQuantityBreak());
					oqm.setStartQuantity(firstQ);
					oqm.setEndQuantity(lastQ);
					oqm.setPrice(pibp1.getQuantityBreakPrice());
					//oqm.setOq(StringUtils.localizedNumberFormat(firstQ,new ResourceMB().getResourceLocale())+INDICATOR_SUBSTR+StringUtils.localizedNumberFormat(lastQ,new ResourceMB().getResourceLocale()));
					oqm.setOq(StringUtils.thousandFormat(firstQ)+INDICATOR_SUBSTR+StringUtils.thousandFormat(lastQ));
					oqm.setMsp(String.valueOf(pibp1.getQuantityBreakPrice())+convertBigDecimalToStr(pibp1));
					returnList.add(oqm);
				}
				
			}
				
		}
		return returnList;
	}
	
	
	private static String convertBigDecimalToStr(QuantityBreakPrice qbp) {
		StringBuilder sb = new StringBuilder();
		sb.append(INDICATOR_SUBSTR);
		try {
			if (qbp == null) {
				sb.append(INDICATOR_SUBSTR);
				sb.append(INDICATOR_SUBSTR);
			} else {

				if (qbp.getSalesCost() != null) {
					sb.append(qbp.getSalesCost().toString());
				} else {
					sb.append(INDICATOR_SUBSTR);
				}

				if (qbp.getSuggestedResale() != null) {
					sb.append(INDICATOR_SUBSTR);
					sb.append(qbp.getSuggestedResale().toString());
				} else {
					sb.append(INDICATOR_SUBSTR);
				}

			}

		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Convert salesCost and suggestedResale to string" + "Reason for failure:"
					+ MessageFormatorUtil.getParameterizedStringFromException(ex), ex);
		}
		return sb.toString();
	}

	public static int roundUp(int mpq, int first, int max)
	{
		int temp= first;
		while(max>temp+mpq)
    	{
			temp = temp +mpq;
    	}
		return temp;
	}
	
	
	

	public static List<String> getSpecialTermsAndConditions(List<QuoteItem> quoteItems) {
		
		List<String> termsAndConditions = new ArrayList<String>();
		termsAndConditions.add(ResourceMB.getText("wq.message.st&C"));

		if(quoteItems!=null && quoteItems.size()>0)
		{
			for (QuoteItem item : quoteItems) 
			{
				if (StringUtils.isEmpty(item.getTermsAndConditions())){
					continue;
				}
				String termAndCondition = item.getTermsAndConditions();
				if(termAndCondition.equalsIgnoreCase("No")){
					continue;
				}
				if (!termsAndConditions.contains(termAndCondition)) {
					termsAndConditions.add(termAndCondition);
				}
			}
		}
		
        if(termsAndConditions!=null && termsAndConditions.size()>1)
        {
        	int terms = (int)('A');
        	for(int i = 1 ; i<termsAndConditions.size(); i ++)
        	{
        		termsAndConditions.set(i,(char)terms+"."+termsAndConditions.get(i));
        		terms++;
        	}
        }
		
		return termsAndConditions;
	}
	
	public static List<String> copyList(List<String> list)
	{
		List<String> termsAndConditions = new ArrayList<String>();
		for(String str:list)
		{
			termsAndConditions.add(str);
		}
		return termsAndConditions;
		
	}
	public static  List<String> getFormatSpecialTermsAndConditions(List<String> termsAndConditions)
	{
		
		for(int i = 0; i< termsAndConditions.size(); i++)
		{
			if(i==0)
			{
				termsAndConditions.set(i,  termsAndConditions.get(i));
			}
			else
			{
				termsAndConditions.set(i, (i + ". " + termsAndConditions.get(i)));
			}
			
		}
		return termsAndConditions;
	}
	
	public static boolean isAutoQuote(Quote quote)
	{
		if(quote!=null)
		{
			List<QuoteItem> qiList = quote.getQuoteItems();
			if(qiList!=null && qiList.size()>0)
			{
				for(QuoteItem qi : qiList)
				{
					if(QuoteSBConstant.RFQ_STATUS_AQ.equalsIgnoreCase(qi.getStatus()))
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	
	
    public List<String> generateEmailToList(Quote quote)
	{
         List<String> toList = new ArrayList<String>();
    	 if(quote!=null)
         {
    		 if(null!=quote.getSales())
    		 {
    			    if(StringUtils.isNotEmpty(quote.getSales().getEmployeeId()))
    	    	 	{
    			    	toList.add(quote.getSales().getEmailAddress());
    	    	 	}
    	    	 	if(StringUtils.isNotEmpty(quote.getCopyToCS()))
    	    	 	{
    	    	 		String tempCopyToCSList = null;
    	    	 		if(quote.getCopyToCS().indexOf(";")!=-1)
    	    	 		{
    	    	 			String[] csToList =quote.getCopyToCS().split(";");
    	    	 			for(String str : csToList)
    	    	 			{
    	    	 				if(str!=null && !str.equalsIgnoreCase(""))
    	    	 				    toList.add(str);
    	    	 			}
    	    	 		}
    	    	 			
    	    	 		else
    	    	 		   toList.add(quote.getCopyToCS());
    	    	 	}
    		 }
    		
         	
         }
         return toList;
	}
    
    public static String getQuotationFileName(Quote quote)
	{
	     StringBuffer  returnStr = new StringBuffer();
	     if(quote!=null)
	     {
		     returnStr.append("Quotation");
		     if(StringUtils.isNotEmpty(quote.getFormNumber()))
		     {
                 returnStr.append(" - ");
		    	 returnStr.append(quote.getFormNumber());
		     }
		     returnStr.append(" - ");
		     returnStr.append(quote.getSoldToCustomer().getCustomerFullName());
		     returnStr.append(" ");
		     returnStr.append(DateUtils.getDefaultDateStrEmailTimeStamp());
	     }
	     return StringUtils.removeCommaFilter(returnStr.toString());

	}
    
    public String getQuotationSubject(Quote quote)
	{
	     StringBuffer  returnStr = new StringBuffer();
	     if(quote!=null)
	     {
		     returnStr.append("Quotation");
		     if(StringUtils.isNotEmpty(quote.getFormNumber())){
		    	 returnStr.append(" - ");
		    	 returnStr.append(quote.getFormNumber());
		     }
		     if(quote.getSoldToCustomer()!=null)
		     {
		    	 returnStr.append(" - ");
		    	 returnStr.append(quote.getSoldToCustomer().getCustomerFullName());
		     }
		     
	     }
	     else
	     {
	    	 return "Quotation";
	     }
	     return returnStr.toString();

	}
	
	public String getFromEmail(String region) {

		String fromEmail = sysCodeMaintSB.getCommidityEmailAddress(region);
		if (org.apache.commons.lang.StringUtils.isBlank(fromEmail)) {
			fromEmail = sysCodeMaintSB.getEmailAddress(region);
		}
		return fromEmail;

	}
   
   public String getSender(Quote quote)
   {
	   //sysCodeMaintSB
		String region = null == quote.getBizUnit() ? null : quote.getBizUnit().getName();
		String signature = sysCodeMaintSB.getEmailSignName(region) + "<br/>"
				+ sysCodeMaintSB.getEmailHotLine(region) + "<br/>Email Box: "
				+ sysCodeMaintSB.getEmailSignContent(region) + "<br/>";
		return signature;
   }

   public static boolean isValidity(String priceValidity) throws ParseException
   {
		if(priceValidity==null)
			return false;
	    boolean isNumber = false;
		int intValidity = 0;
		Date dateValidity = null;
		Calendar calDateValidity = null;
		

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		if(DateUtils.isDate(priceValidity))
		{			
			dateValidity = dateFormat.parse(priceValidity);
			calDateValidity = Calendar.getInstance();
			calDateValidity.setTime(dateValidity);
			calDateValidity.add(Calendar.DATE, 1);
		}
		else if(org.apache.commons.lang.StringUtils.isNumeric(priceValidity))
		{
			   
			intValidity = Integer.parseInt(priceValidity);
			isNumber = true;
		}

		if(isNumber)
		{
			if(intValidity <= 0)
				return false;
		}
		else
		{
			if(calDateValidity.before(Calendar.getInstance()))
				return false;
		}
		return true;

   }
   
    public void createQuoteToSoForAQ(List<QuoteItem> quoteItemList,BizUnit bizUnit) {
		List<QuoteItem> items = new ArrayList<QuoteItem>();
		for(QuoteItem item : quoteItemList){
			if(QuoteSBConstant.QUOTE_STAGE_FINISH.equals(item.getStage())
					&& QuoteSBConstant.RFQ_STATUS_AQ.equals(item.getStatus())) {
				items.add(item);
			}
			
		}
		if(items.size()>0){
			createQuoteToSo(items, bizUnit);
		}
	}
	
	public void createQuoteToSo(List<QuoteItem> quoteItems,BizUnit bizUnit){
	      javax.xml.ws.Holder<ZwqCustomer> eCustdtl = new javax.xml.ws.Holder<ZwqCustomer>();
	      javax.xml.ws.Holder<java.lang.String> eKunnr = new javax.xml.ws.Holder<java.lang.String>();
	      
		  try {
				TableOfZquoteMsg tableMsg = sapWebServiceSB.createSAPQuote(quoteItems);
		        List<ZquoteMsg> msgs = tableMsg.getItem();
		        List<String> errorMsgs = new ArrayList<String>();
		        for(ZquoteMsg msg : msgs){
		        	if(! msg.getType().equalsIgnoreCase("S")){
		        		logger.log(Level.WARNING, msg.getId()+"/"+msg.getMessage()+"/"+msg.getNumber()+"/"+msg.getType());
		        		for(QuoteItem item : quoteItems){
		        			if(item.getQuoteNumber() != null && msg.getMessage() != null && msg.getMessage().indexOf(item.getQuoteNumber()) > -1){
		        				QuoteToSoPending quoteToSoPending = new QuoteToSoPending();
		        				quoteToSoPending.setBizUnit(bizUnit);
		        				quoteToSoPending.setCreateDate(QuoteUtil.getCurrentTime());
		        				quoteToSoPending.setCustomerNumber(item.getSoldToCustomer().getCustomerNumber());
		        				quoteToSoPending.setStatus(true);
		    					// Delinkage change on Oct 29 , 2013 by Tonmy
//		    					quoteToSoPending.setMfr(item.getRequestedMfr().getManufacturer().getName());
//		    					quoteToSoPending.setFullMfrPartNumber(item.getRequestedMaterial().getFullMfrPartNumber());
		    					quoteToSoPending.setMfr(item.getRequestedMfr().getName());
		    					quoteToSoPending.setFullMfrPartNumber(item.getRequestedPartNumber());
		    					
		        				quoteToSoPending.setQuoteNumber(item.getQuoteNumber());
		        				try {
		        					QuoteToSoPending detach = quoteToSoPendingSB.updateQuoteToSoPending(quoteToSoPending);
		        				} catch (Exception ex){
		        					logger.log(Level.SEVERE, "Cannot create Quote To So Pending for : "+quoteToSoPending.getQuoteNumber() +" , Bussinees unit : "+quoteToSoPending.getBizUnit()+", "
		        							+ "Reason for failure: "+MessageFormatorUtil.getParameterizedStringFromException(ex),ex);
		        				}
		        				break;
		        			}
		        		}
		        		
		        		errorMsgs.add(msg.getMessage());
		        	}
		        }				
			} catch (AppException e) {
				
				logger.log(Level.SEVERE, "Cannot create Quote To So Pending"+MessageFormatorUtil.getParameterizedStringFromException(e),e);
				
	    		for(QuoteItem item : quoteItems){
					QuoteToSoPending quoteToSoPending = new QuoteToSoPending();
					quoteToSoPending.setBizUnit(bizUnit);
					quoteToSoPending.setCreateDate(QuoteUtil.getCurrentTime());
					quoteToSoPending.setCustomerNumber(item.getSoldToCustomer().getCustomerNumber());
					quoteToSoPending.setStatus(true);
					// Delinkage change on Oct 29 , 2013 by Tonmy
//					quoteToSoPending.setMfr(item.getRequestedMfr().getManufacturer().getName());
//					quoteToSoPending.setFullMfrPartNumber(item.getRequestedMaterial().getFullMfrPartNumber());
					quoteToSoPending.setMfr(item.getRequestedMfr().getName());
					quoteToSoPending.setFullMfrPartNumber(item.getRequestedPartNumber());


					quoteToSoPending.setQuoteNumber(item.getQuoteNumber());
					try {
						QuoteToSoPending detach = quoteToSoPendingSB.updateQuoteToSoPending(quoteToSoPending);
					} catch (Exception ex){
						logger.log(Level.SEVERE, "Cannot create Quote To So Pending for : "+quoteToSoPending.getQuoteNumber() +" , Bussinees unit : "+quoteToSoPending.getBizUnit().getName()+", "
								+ "Exception message: "+MessageFormatorUtil.getParameterizedStringFromException(ex),ex);
					}
	    		}			
			}	      
		}
	
		public static QuoteItem calculateMargin(QuoteItem item)
		{ 
			//PROGRM PRICER ENHANCEMENT
			//ProgramMaterial pm = item.getRequestedMaterialForProgram().getProgramMaterial();
			ProgramPricer pm = item.getRequestedProgramMaterialForProgram();
			if(QuoteSBConstant.RFQ_STATUS_AQ.equalsIgnoreCase(item.getStatus()))
			{
				item.setTargetMargin(QuoteUtil.calculateTargetMargin(item.getCost(),item.getQuotedPrice()));
				item.setResaleMargin(QuoteUtil.calculateResalesMargin(item.getCost(),item.getQuotedPrice()));
			}
			else if(QuoteSBConstant.RFQ_STATUS_QC.equalsIgnoreCase(item.getStatus()))
			{
				//for defect 378
				item.setTargetMargin(QuoteUtil.calculateTargetMargin(pm.getCost(),item.getQuotedPrice()));
			}
			return item;
		}
		
		
		/*public static QuoteItem processAutoQuoteItemForNormalSubmission(QuoteItem item, Pricer md, Material m) throws ParseException
		{
			//PROGRM PRICER ENHANCEMENT
			//ProgramMaterial pm = (ProgramMaterial)md;
			//m.setProgramMaterial(pm);
			ProgramPricer pm = (ProgramPricer)md;
			item.setRequestedProgramMaterialForProgram(pm);
			item.setRequestedMaterialForProgram(m);
			//9.If a RFQ can be automatically quoted and its target resale is empty, 
			//the quoted price of the quote for the RFQ equals to the price of the range, 
			//which the required quantity of the RFQ falls to.
			//10.If a RFQ can be automatically quoted and its target resale is not empty, 
			//the quoted price of the quote for the RFQ equals to the target resale.
			if(item.getTargetResale() == null)
			{
//				Fix incident INC0198809  June Guo 20150907
				Double tempPrice = getPrice(item);
				if(tempPrice!=null && tempPrice!=0d && tempPrice!= -999999d)
				{
				   item.setQuotedPrice(tempPrice);
				}
			}
			else
			{
				item.setQuotedPrice(item.getTargetResale());
			}
			
			//11.If a RFQ can be automatically quoted and its required quantity is not greater than the MOQ of 
			//corresponding program item. The Avnet quoted qty of the quote for the RFQ equals to the MOQ. 
			//If the required quantity is greater than the MOQ, The Avnet quoted qty of the quote equals to 
			//the result of rounding the required quantity up to the least multiple of the MPQ of the program item. 
			//Then if the MOV of the program item is not empty and the Avnet quoted qty is less than the result of 
			//dividing the MOV by the minimum cost of the program item, Avnet quoted qty equals to the result of 
			//dividing MOV by minimum cost, and rounded up to the least of the MPQ
			int avnetQuotedQty = 0;
			if(item.getRequestedQty().intValue() <= item.getMoq().intValue())
			{
				avnetQuotedQty= item.getMoq().intValue();
			}
			else
			{
				avnetQuotedQty=mpqRoundUp(item.getRequestedQty().intValue(),item.getMpq().intValue());
			}
			
			
			
			//new logic, no need to consider MOV
			
			if(programItem.getMov() != Constant.DEFAULT_FLOAT_VALUE &&
					avnetQuotedQty < (programItem.getMov()/ programItem.getMinimumCost()) &&
					programItem.getQuantityIndicator().equals("Exact")){
				avnetQuotedQty = mpqRoundUp(programItem.getMov()/ programItem.getMinimumCost(),programItem.getMpq());
			}
			
			item.setQuotedQty(avnetQuotedQty);
			
			//12.If a RFQ can be automatically quoted and the quantity indicator of corresponding program 
			//item is not "Limit", the PMOQ of the quote for the RFQ is calculated based on the quantity indicator of 
			//the program item and the Avnet quoted qty of the quote.	If the quantity indicator is "Exact", 
			//the PMOQ equals to the Avnet quoted qty. If the quantity indicator is "MOQ", 
			//the PMOQ equals to the MOQ of the program item followed by a plus sign. 
			//If the quantity indicator is "MPQ", the PMOQ equals to the MPQ of the program item followed by a plus sign. 
			//If the quantity indicator is percentage, the PMOQ equals to the result of multiplying the Avnet quoted qty by 
			//the quantity indicator, followed by a plus sign. Regarding to the calculation of the PMOQ, 
			//refer to 9.1.1.3 PMOQ Illustration.
			//13.If the RFQ can be automatically quoted and quantity indicator of the program item is "Limit", 
			//PMOQ is calculated based on the range of the neighbouring quantity breaks, 
			//where the required quantity of the RFQ falls. If the required quantity is not less than 
			//the last quantity break of the program item, and call for quote of the program item is "No", 
			//PMOQ equals to the last quantity break followed by a plus sign. Otherwise, the lower limit of the PMOQ 
			//is calculated by rounding the lower quantity break up to the least multiple of 
			//the MPQ of the program item. The upper limit of the PMOQ is calculated by rounding the upper quantity 
			//break down to the greatest multiple of the MPQ. Regarding to the calculation of the PMOQ for 
			//limit quantity indicator, refer to 9.1.1.3 PMOQ Illustration for Limit Quantity Indicator.
			//logger.info("Set PMOQ");
			item.setPmoq(getPMOQ(item));
			
			//14.If a RFQ can be automatically quoted and the validity of its corresponding program item is 
			//in date format, PO expiry date of the quote for the RFQ equals to the validity.
			//15.If a RFQ can be automatically quoted and the validity of its corresponding program item is 
			//in numeric format, the PO expiry date of the quote for the RFQ is the 
			//date after <the validity of the program item> days from current date.
			String validity = item.getPriceValidity();
			if(validity!=null)
			{
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				if(DateUtils.isDate(validity))
				{			
					item.setPoExpiryDate(dateFormat.parse(validity));
				}
				else if(org.apache.commons.lang.StringUtils.isNumeric(validity))
				{
					   
					   GregorianCalendar calendar = new GregorianCalendar(); 
					   calendar.setTime(new Date()); 
					   calendar.add(GregorianCalendar.DATE, Integer.parseInt(validity)); 
					   item.setPoExpiryDate(calendar.getTime());
				}
			}
			

			
			//16.	If a RFQ can be automatically quoted, the quote expired date of the quote for the RFQ is 
			//the date 1 day before the PO expired date of the quote.
	        item.setQuoteExpiryDate(DateUtils.addDay(item.getPoExpiryDate(), -1));
	
			
			//19.If a RFQ can be automatically quoted, the fields of its corresponding program item, 
			//minimum cost, MPQ, MOQ, MOV, cost indicator, lead time, quantity indicator, allocation flag, 
			//terms and conditions, Avnet quote centre comments will be brought to the corresponding fields 
			//in the quote for the RFQ. The minimum cost will be brought to the field, cost, 
			//and the quantity indicator will be brought to the field, qty indicator of the quote for the RFQ 
			//and Avnet quote centre comments will be brought to the field, Avnet quote centre comment of 
			//the quote for the RFQ.
	        //item.setCost(pm.getProgramMinimumCost());
	      //for defect 378
	        item.setCost(pm.getCost());
	        item.setAllocationFlag(pm.getAllocationFlag());
	        item.setQcComment(pm.getAvnetQcComments());

			
			//20. If a RFQ can be automatically quoted, the resale indicator of the quote for the RFQ equals to 
			//"00" followed by the last 2 characters of the resale indicator of its corresponding program item
	        String resaleIndicator = pm.getResaleIndicator();
	        if(resaleIndicator!=null)
			item.setResaleIndicator("00" + resaleIndicator.substring(2));
			
			
			//23. If a RFQ can be automatically quoted, the rescheduling window and the cancellation window of 
			//the quote for the RFQ are empty, and multi-usage of the quote for the RFQ is "No".
			item.setRescheduleWindow(null);
			item.setCancellationWindow(null);
			item.setMultiUsageFlag(false);
			
			return item;
		}
		*/
		
		public static QuoteItem processAutoQuoteItemForNormalSubmissionForRfqPage(QuoteItem item, RfqItemVO rfqItem) throws ParseException
		{
			//PROGRM PRICER ENHANCEMENT
			//ProgramMaterial pm = (ProgramMaterial)md;
			//m.setProgramMaterial(pm);
			///ProgramPricer pm = (ProgramPricer)md;
			//item.setRequestedProgramMaterialForProgram(pm);
			//item.setRequestedMaterialForProgram(m);
			//9.If a RFQ can be automatically quoted and its target resale is empty, 
			//the quoted price of the quote for the RFQ equals to the price of the range, 
			//which the required quantity of the RFQ falls to.
			//10.If a RFQ can be automatically quoted and its target resale is not empty, 
			//the quoted price of the quote for the RFQ equals to the target resale.
			if(item.getTargetResale() == null)
			{
//				Fix incident INC0198809  June Guo 20150907
				Double tempPrice = getPrice(item);
				if(tempPrice!=null && tempPrice!=0d && tempPrice!= -999999d)
				{
				   item.setQuotedPrice(tempPrice);
				}
			}
			else
			{
				item.setQuotedPrice(item.getTargetResale());
			}
			
			//11.If a RFQ can be automatically quoted and its required quantity is not greater than the MOQ of 
			//corresponding program item. The Avnet quoted qty of the quote for the RFQ equals to the MOQ. 
			//If the required quantity is greater than the MOQ, The Avnet quoted qty of the quote equals to 
			//the result of rounding the required quantity up to the least multiple of the MPQ of the program item. 
			//Then if the MOV of the program item is not empty and the Avnet quoted qty is less than the result of 
			//dividing the MOV by the minimum cost of the program item, Avnet quoted qty equals to the result of 
			//dividing MOV by minimum cost, and rounded up to the least of the MPQ
			int avnetQuotedQty = 0;
			if(item.getRequestedQty().intValue() <= item.getMoq().intValue())
			{
				avnetQuotedQty= item.getMoq().intValue();
			}
			else
			{
				avnetQuotedQty=mpqRoundUp(item.getRequestedQty().intValue(),item.getMpq().intValue());
			}
			
			
			
			//new logic, no need to consider MOV
			/*
			if(programItem.getMov() != Constant.DEFAULT_FLOAT_VALUE &&
					avnetQuotedQty < (programItem.getMov()/ programItem.getMinimumCost()) &&
					programItem.getQuantityIndicator().equals("Exact")){
				avnetQuotedQty = mpqRoundUp(programItem.getMov()/ programItem.getMinimumCost(),programItem.getMpq());
			}
			*/
			item.setQuotedQty(avnetQuotedQty);
			
			//12.If a RFQ can be automatically quoted and the quantity indicator of corresponding program 
			//item is not "Limit", the PMOQ of the quote for the RFQ is calculated based on the quantity indicator of 
			//the program item and the Avnet quoted qty of the quote.	If the quantity indicator is "Exact", 
			//the PMOQ equals to the Avnet quoted qty. If the quantity indicator is "MOQ", 
			//the PMOQ equals to the MOQ of the program item followed by a plus sign. 
			//If the quantity indicator is "MPQ", the PMOQ equals to the MPQ of the program item followed by a plus sign. 
			//If the quantity indicator is percentage, the PMOQ equals to the result of multiplying the Avnet quoted qty by 
			//the quantity indicator, followed by a plus sign. Regarding to the calculation of the PMOQ, 
			//refer to 9.1.1.3 PMOQ Illustration.
			//13.If the RFQ can be automatically quoted and quantity indicator of the program item is "Limit", 
			//PMOQ is calculated based on the range of the neighbouring quantity breaks, 
			//where the required quantity of the RFQ falls. If the required quantity is not less than 
			//the last quantity break of the program item, and call for quote of the program item is "No", 
			//PMOQ equals to the last quantity break followed by a plus sign. Otherwise, the lower limit of the PMOQ 
			//is calculated by rounding the lower quantity break up to the least multiple of 
			//the MPQ of the program item. The upper limit of the PMOQ is calculated by rounding the upper quantity 
			//break down to the greatest multiple of the MPQ. Regarding to the calculation of the PMOQ for 
			//limit quantity indicator, refer to 9.1.1.3 PMOQ Illustration for Limit Quantity Indicator.
			//logger.info("Set PMOQ");
			item.setPmoq(getPMOQ(item));
			
			//14.If a RFQ can be automatically quoted and the validity of its corresponding program item is 
			//in date format, PO expiry date of the quote for the RFQ equals to the validity.
			//15.If a RFQ can be automatically quoted and the validity of its corresponding program item is 
			//in numeric format, the PO expiry date of the quote for the RFQ is the 
			//date after <the validity of the program item> days from current date.
			String validity = item.getPriceValidity();
			if(validity!=null)
			{
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				if(DateUtils.isDate(validity))
				{			
					item.setPoExpiryDate(dateFormat.parse(validity));
				}
				else if(org.apache.commons.lang.StringUtils.isNumeric(validity))
				{
					   
					   GregorianCalendar calendar = new GregorianCalendar(); 
					   calendar.setTime(new Date()); 
					   calendar.add(GregorianCalendar.DATE, Integer.parseInt(validity)); 
					   item.setPoExpiryDate(calendar.getTime());
				}
			}
			

			
			//16.	If a RFQ can be automatically quoted, the quote expired date of the quote for the RFQ is 
			//the date 1 day before the PO expired date of the quote.
	        item.setQuoteExpiryDate(DateUtils.addDay(item.getPoExpiryDate(), -1));
	
			
			//19.If a RFQ can be automatically quoted, the fields of its corresponding program item, 
			//minimum cost, MPQ, MOQ, MOV, cost indicator, lead time, quantity indicator, allocation flag, 
			//terms and conditions, Avnet quote centre comments will be brought to the corresponding fields 
			//in the quote for the RFQ. The minimum cost will be brought to the field, cost, 
			//and the quantity indicator will be brought to the field, qty indicator of the quote for the RFQ 
			//and Avnet quote centre comments will be brought to the field, Avnet quote centre comment of 
			//the quote for the RFQ.
	        //item.setCost(pm.getProgramMinimumCost());
	      //for defect 378
	        item.setCost(rfqItem.getCost());
	        item.setAllocationFlag(rfqItem.getAllocationFlag());
	        item.setQcComment(rfqItem.getQcComment());

			
			//20. If a RFQ can be automatically quoted, the resale indicator of the quote for the RFQ equals to 
			//"00" followed by the last 2 characters of the resale indicator of its corresponding program item
	        String resaleIndicator = rfqItem.getResaleIndicator();
	        if(resaleIndicator!=null)
			item.setResaleIndicator("00" + resaleIndicator.substring(2));
			
			
			//23. If a RFQ can be automatically quoted, the rescheduling window and the cancellation window of 
			//the quote for the RFQ are empty, and multi-usage of the quote for the RFQ is "No".
			item.setRescheduleWindow(null);
			item.setCancellationWindow(null);
			item.setMultiUsageFlag(false);
			
			return item;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		public static boolean isQtyInRangesNew(QuoteItem item)
		{
			List<ProgramMaterialQtyRange> ranges =  getQtyRangesNew(item);
			//Fixed 1102
			int newRequestedQty = item.getRequestedQty().intValue();
			if(newRequestedQty <= item.getMoq().intValue())
			{
				newRequestedQty= item.getMoq().intValue();
			}
			
			for(ProgramMaterialQtyRange range : ranges)
			{
				//logger.info("range :"+range.toString());
				if(range.isQtyInRange(newRequestedQty))
				{
					return true;
				}
			}
			return false;

		}
		
		
		public static List<ProgramMaterialQtyRange> getQtyRangesNew(QuoteItem item) throws IllegalArgumentException
		{
			List<ProgramMaterialQtyRange> qtyRanges = new ArrayList<ProgramMaterialQtyRange>();
			//PROGRM PRICER ENHANCEMENT
			ProgramPricer pm = item.getRequestedProgramMaterialForProgram();
			List<QuantityBreakPrice> priceList = pm.getQuantityBreakPrices();
			
			if(priceList!=null && priceList.size()>0)
			{
				int size = priceList.size();
				for(int i=0; i < size; i++)
				{
					int qtyFrom = 0;
					double qtyTo = 0;
					double price = 0;
					double priceToleranceFrom = 0;
					double priceToleranceTo = 0;
					boolean callForQuote = false;
					
/*					if((i+1) < size)
					{
						qtyTo = priceList.get(i+1).getQuantityBreak();
					}
					else
					{
						if(pm.getProgramCallForQuote()!=null && pm.getProgramCallForQuote()==true)
						{
							callForQuote = true;
							qtyTo= Integer.MAX_VALUE;
						}
						else
						{
							callForQuote = false;
							if(price == 0d)
							{
								qtyTo= priceList.get(i).getQuantityBreak();
								break;
							}else{
								qtyTo = Integer.MAX_VALUE;
							}
						}
					}*/
					
					//above conment is replace of calling function setCommonData by Damonchen
					setCommonData(priceList, pm, i, size, qtyFrom, qtyTo, price, priceToleranceFrom, priceToleranceTo, callForQuote);
					if(i==0)
					{
						qtyRanges.add(new ProgramMaterialQtyRange(qtyFrom, mpqRoundDownPi(qtyTo,  pm.getMpq().intValue()),price,priceToleranceFrom,priceToleranceTo,callForQuote));
					}
					else
					{
						qtyRanges.add(new ProgramMaterialQtyRange(mpqRoundDownPi(qtyFrom, pm.getMpq().intValue()), mpqRoundDownPi(qtyTo,  pm.getMpq().intValue()),price,priceToleranceFrom,priceToleranceTo,callForQuote));
					}
					
				}
			}
			if(qtyRanges.size() >= 6){
				for(int i = qtyRanges.size()-1; i >=5 ; i--){
					qtyRanges.remove(i);
				}
			}
			return qtyRanges;
		}
		
		private static boolean setCommonData(List<QuantityBreakPrice> priceList ,ProgramPricer pm, int i, int size, int qtyFrom, double qtyTo, double price, double priceToleranceFrom, double priceToleranceTo, boolean callForQuote){
			QuantityBreakPrice qbp = priceList.get(i);
			qtyFrom = qbp.getQuantityBreak();
			qtyTo = 0;
			price = qbp.getQuantityBreakPrice().doubleValue();
			String resaleIndicator = pm.getResaleIndicator();
			priceToleranceFrom = getPriceToleranceFrom(resaleIndicator,price);
			priceToleranceTo = getPriceToleranceTo(resaleIndicator,price);
			callForQuote = false;
			
			if((i+1) < size)
			{
				qtyTo = priceList.get(i+1).getQuantityBreak();
			}
			else
			{
				if(pm.getProgramCallForQuote()==true)
				{
					callForQuote = true;
					qtyTo= Integer.MAX_VALUE;
				}
				else
				{
					callForQuote = false;
					if(price == 0d)
					{
						qtyTo= priceList.get(i).getQuantityBreak();
						return false;
					}else{
						qtyTo = Integer.MAX_VALUE;
					}
				}
			}
			return true;
		}
		
		public static Double isInRangesPriceNew(QuoteItem item)
		{
			Double returnPrice = null; 
			List<ProgramMaterialQtyRange> ranges =  getQtyRangesNew(item);
			
			//Fixed 1102
			int newRequestedQty = item.getRequestedQty().intValue();
			if(newRequestedQty <= item.getMoq().intValue())
			{
				newRequestedQty= item.getMoq().intValue();
			}
			
			for(ProgramMaterialQtyRange range : ranges)
			{
				if(range.isQtyInRange(newRequestedQty))
				{
					returnPrice = range.getPrice();
					break;
				}
			}
			return returnPrice;

		}
		
		
		public static double getPriceNew(QuoteItem item) {
			if (! isQtyInRangesNew(item))
			{
				throw new WebQuoteRuntimeException(CommonConstants.WQ_EJB_MASTER_DATA_QUANTITY_IS_NOT_IN_ANY_RANGE, new Object[]{item.getRequestedQty()});
			}
			List<ProgramMaterialQtyRange> ranges = getQtyRangesNew(item);
			for(ProgramMaterialQtyRange range : ranges)
			{
				if(range.isQtyInRange(item.getRequestedQty().intValue()))
				{
					return range.getPrice();
				}
			}
			return 0;
		}
		
		public static QuoteItem setDefaultValueForNoAutoQuoteItem(QuoteItem item) throws ParseException
		{
			
			//PROGRM PRICER ENHANCEMENT
			//ProgramMaterial pm = item.getRequestedMaterialForProgram().getProgramMaterial();
			ProgramPricer pm = item.getRequestedProgramMaterialForProgram();
			item.setMoq(pm.getMpq());
	        item.setMoq(pm.getMoq());
	        item.setMov(pm.getMov());
	        if(pm.getCostIndicator()!=null)
	        item.setCostIndicator(pm.getCostIndicator().getName());
	        item.setLeadTime(pm.getLeadTime());
	        item.setQtyIndicator(pm.getQtyIndicator());
	        item.setAllocationFlag(pm.getAllocationFlag());
	        item.setTermsAndConditions(pm.getTermsAndConditions());
	        item.setQcComment(pm.getAvnetQcComments());
	        // New field "Quotation effective date"
	        if(pm.getQuotationEffectiveDate()!=null)
	        item.setQuotationEffectiveDate(pm.getQuotationEffectiveDate());
		        
		    return item;
		}
}

