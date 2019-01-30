package com.avnet.emasia.webquote.commodity.helper;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.EJB;

import org.apache.commons.lang.StringUtils;

import com.avnet.emasia.webquote.commodity.ejb.ProgramMaterialSB;
import com.avnet.emasia.webquote.commodity.vo.ValidateError;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.web.quote.CommonBean;


public class ValidateHelper extends CommonBean {

	
	private static final String LINE_BREAK = "<br/>";
	private static final String ALERT_MST_ROW ="wq.message.rowSmall";
	private static final String ALERT_MST_REQUIED_QUANTITY = "wq.error.requiredQuantity";
	private static final String SUB_NO_EXIST_ITEM_REQUIRED = " [{1}] ";
	
	private static Logger logger = Logger.getLogger("ValidateHelper");

	@EJB
	ProgramMaterialSB programMaterialSB;
	
	
	public ValidateHelper() {
		// TODO Auto-generated constructor stub
	}
    
	public static ValidateError validate(Quote quote, boolean isCheckSales)
	{
		StringBuffer sb = new StringBuffer();
		String message=null;
		ValidateError error = new ValidateError();
		Customer tempCus = quote.getSoldToCustomer();
		if(isCheckSales)
		{
		    User tempUser = quote.getSales();
			if (tempCus ==null ||(StringUtils.isEmpty(tempCus.getCustomerName1()) && StringUtils.isEmpty(tempUser.getName())))
			{
				message =ResourceMB.getText("wq.message.entrSoldPartyError");
				sb.append(message).append(LINE_BREAK);
			}
			else if(StringUtils.isEmpty(tempCus.getCustomerName1()) && !StringUtils.isEmpty(tempUser.getName()))
			{
				message =ResourceMB.getText("wq.message.entrCustError");
				sb.append(message).append(LINE_BREAK);
			}
			else if(!StringUtils.isEmpty(tempCus.getCustomerName1()) && StringUtils.isEmpty(tempUser.getName()))
			{
				message =ResourceMB.getText("wq.message.entrSalesmanError");
				sb.append(message).append(LINE_BREAK);
			}
		}
		else
		{
			if(tempCus==null ||StringUtils.isEmpty(quote.getSoldToCustomer().getCustomerName1()))
			{	message =ResourceMB.getText("wq.message.entrCustError");
				sb.append(message).append(LINE_BREAK);
			}
		}
		

		List<QuoteItem> items = quote.getQuoteItems();
		if(items!=null && items.size()<=0)
		{
			message =ResourceMB.getText("wq.message.slectOneItemError");
			sb.append(message).append(LINE_BREAK);
		}
		//TO-DO 
		//Check the program item whether exist.
		
		
		
		
		
		//Check mandatory field
		Map<Long,Integer> issueRows = new HashMap<Long,Integer>();
		List<QuoteItem> itemsList =  quote.getQuoteItems();
		StringBuffer mandatorySb = new StringBuffer();
		for(int i = 0; i < itemsList.size(); i ++)
		{
		     QuoteItem item = itemsList.get(i);
		     if(item.getRequestedQty()==null || item.getRequestedQty()==0)
		     {
		    	 issueRows.put(new Long(i+1), new Integer(i+1));
		    	 mandatorySb.append(ResourceMB.getParameterizedString(ALERT_MST_REQUIED_QUANTITY, i+1)+" ");
		     }
		     if(item.getSoldToCustomer()==null) {
		    	 issueRows.put(new Long(i+1), new Integer(i+1));
		    	 mandatorySb.append(ResourceMB.getParameterizedString("wq.error.soldToCustomer", i+1)+" ");
		     }
		}
		
		if(mandatorySb.length()>0)
		{
			issueRows = sortByValue(issueRows); 
			sb.append(ResourceMB.getText("wq.message.mandatoryFields")).append(LINE_BREAK).append(ResourceMB.getText(ALERT_MST_ROW)+":");
			sb.append(mandatorySb);  
		}

		error.setErroeMsg(sb.toString());
		error.setIssueRows(issueRows);
		
		
//		if(!error.isHasError())
//		{
//			error = validateExistPI(quote);
//		}
		return error;
	}

	
	public static ValidateError validateBodyMandatoryFields(List<QuoteItem> quoteItemList)
	{
		logger.fine("call validateBodyMandatoryFields");
		ValidateError error = new ValidateError();
		
		StringBuffer sb = new StringBuffer();
		if(quoteItemList==null || quoteItemList.size()==0)
		{
			error.setHasError(false);
			return error;
		}
		
		Map<Long,Integer> issueRows = new HashMap<Long,Integer>();
		for(int i = 0; i < quoteItemList.size(); i ++)
		{
		     QuoteItem item = quoteItemList.get(i);
		     if(item.getRequestedQty()==null)
		     {
		    	//PROGRM PRICER ENHANCEMENT
		    	 //issueRows.put(new Long(item.getRequestedMaterialForProgram().getProgramMaterial().getId()),new Integer(i+1));
		    	 issueRows.put(new Long(item.getRequestedProgramMaterialForProgram().getId()),new Integer(i+1));
		     }
		}
		
		if(issueRows.size()>0)
		{
			issueRows = sortByValue(issueRows);
			sb.append(ResourceMB.getText("wq.message.mandatoryFields")).append(LINE_BREAK).append(ResourceMB.getText(ALERT_MST_ROW)+":");
			
			Set set =issueRows.entrySet(); 
			Iterator it = set.iterator();    
			while(it.hasNext())
			{           
				Map.Entry<Long,Integer>  entry = (Map.Entry<Long,Integer>) it.next();  
				sb.append(ResourceMB.getParameterizedString(ALERT_MST_REQUIED_QUANTITY, entry.getValue().toString())+" ");
			}
			
		}
		error.setErroeMsg(sb.toString());
		error.setIssueRows(issueRows);
		return error;
	}

	
	public static Map<Long,Integer> sortByValue(Map<Long,Integer> map) 
	{
        List list = new LinkedList(map.entrySet());        
        Collections.sort(list, 
        	new Comparator() 
            {            
        	    public int compare(Object o1, Object o2) 
        	    {                
        		   return ((Comparable) ((Map.Entry) (o1)).getKey()).compareTo(((Map.Entry) (o2)).getKey());            
        		}
        	});        
        Map result = new LinkedHashMap();        
        for (Iterator it = list.iterator(); it.hasNext();) 
        {           
        	Map.Entry entry = (Map.Entry) it.next();            
        	result.put(entry.getKey(), entry.getValue());        
        }       
        return result;   
    }
	
	
	public static ValidateError validateExistPI(Quote quote,List<Long> idList)
	{
		logger.fine("call validateExistPI.");
		StringBuffer sb = new StringBuffer();
		Map<Long,Integer> issueRows = new HashMap<Long,Integer>();
		List<QuoteItem> itemsList =  quote.getQuoteItems();
		for(int i = 0; i < itemsList.size(); i ++)
		{
		     QuoteItem item = itemsList.get(i);
		     //PROGRM PRICER ENHANCEMENT
//		     if(!idList.contains(Long.valueOf(item.getRequestedMaterialForProgram().getProgramMaterial().getId())))
//		     {
//		    	 issueRows.put(new Long(item.getRequestedMaterialForProgram().getProgramMaterial().getId()),new Integer(i+1));
//		     }
		     if(!idList.contains(Long.valueOf(item.getPricerId())))
		     {
		    	 issueRows.put(new Long(item.getPricerId()),new Integer(i+1));
		     }
		}
		
		if(issueRows.size()>0)
		{
			issueRows = sortByValue(issueRows);
			final String message =ResourceMB.getText("wq.message.removeRowError");
			sb.append(message).append(LINE_BREAK).append(ResourceMB.getText(ALERT_MST_ROW)+":");
			Set set =issueRows.entrySet();       
			Iterator it = set.iterator();      
			while(it.hasNext())
			{           
				Map.Entry<Long,Integer>  entry = (Map.Entry<Long,Integer>) it.next();  
				sb.append(SUB_NO_EXIST_ITEM_REQUIRED.replace("{1}",entry.getValue().toString()));
			}
			
		}
		
		ValidateError error = new ValidateError();
		error.setErroeMsg(sb.toString());
		error.setIssueRows(issueRows);

		return error;
	}
}

 