package com.avnet.emasia.webquote.quote.ejb;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;


import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.avnet.emasia.webquote.entity.Attachment;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.SalesOrg;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.exception.AppException;
import com.avnet.emasia.webquote.quote.ejb.SAPWebServiceSB;
import com.avnet.emasia.webquote.quote.vo.MyQuoteSearchCriteria;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.sap.document.sap.soap.functions.mc_style.ZwqCustomer;



public class SAPWebServiceSBTest {
	
	SAPWebServiceSB sb ;
	
	private static final Logger LOG =Logger.getLogger(SAPWebServiceSBTest.class.getName());
	
	@Before
	public void setUp(){
		sb = new SAPWebServiceSB();
	}
	
	@Ignore
	public void createProspectiveCustomer() throws AppException{
		
      
      javax.xml.ws.Holder<ZwqCustomer> eCustdtl = new javax.xml.ws.Holder<ZwqCustomer>();
      
      javax.xml.ws.Holder<java.lang.String> eKunnr = new javax.xml.ws.Holder<java.lang.String>();
		
/*      sb.createProspectiveCustomer(
				"PENANG",
				"",
				"MY",
				"SHENZHEN WATT ELECTRONIC",
				"", //name2
				"", //Postal code
				"", //Region 
				"", //Search term 
				"", //Division
				"", //Street
				"", //Sales office 
				"HK11", //Sales Org
				"",
				"USD", //currency
				eCustdtl,
				eKunnr);*/
      
      if(eKunnr.value.equals("")){
		 ZwqCustomer exitingCustomer = eCustdtl.value;
		 LOG.info("customer code:" + exitingCustomer.getKunnr());
		 LOG.info("customer name:" + exitingCustomer.getName());
		 LOG.info("sales org:" + exitingCustomer.getVkorg());
		 LOG.info("account type:" + exitingCustomer.getKtokd());

      }else{
    	  LOG.info("new customer code" + eKunnr.value);
    	
      }
      
      
		
	}
	
	@Test
	public void createSAPQuote() throws AppException{
		
		QuoteItem item = new QuoteItem();
		Quote quote = new Quote();
		item.setQuote(quote);
		
		BizUnit bizUnit = new BizUnit();
		bizUnit.setName("AEMC");
		quote.setBizUnit(bizUnit);
		
		List<QuoteItem> items = new ArrayList<QuoteItem>();
		
		items.add(item);
		item.setQuoteNumber("WR0001233");
		item.setSentOutTime(new Date());
		item.setPriceValidity("2013-04-30");
		
		item.setShipmentValidity(new Date());
		
		item.setQuotedPrice(0.35123);
		item.setQuotedQty(100);
		
//		Material material = new Material();
//		material.setSapPartNumber("TISADS1232IPW");
//		item.setQuotedMaterial(material);
//		
//		Manufacturer mfr = new Manufacturer();
//		mfr.setName("TIS");
//		
//		material.setManufacturer(mfr);
//		material.setFullMfrPartNumber("ADS1232IPW");
//		
//		Customer customer = new Customer();
//		customer.setCustomerNumber("420");
//		item.setSoldToCustomer(customer);
//		
//		item.setMultiUsageFlag(true);
		
//		item.setDrmsNumber(1036208L);
		
		/*
        sapQuote.setRfqNo("WR9012345");    	        
        sapQuote.setValidfrom(datatypeFactory.newXMLGregorianCalendar("2013-02-12"));
        sapQuote.setValidto(datatypeFactory.newXMLGregorianCalendar("2013-02-12"));
        
        
        sapQuote.setZregion("AEMC");
        sapQuote.setMatnr("TISADS1232IPW");


        sapQuote.setMultiUse("X");
        
        sapQuote.setQuoteprice(new BigDecimal("0.23"));
        sapQuote.setFinalprice(new BigDecimal("0.23"));
        //sapQuote.setResaleind("");
//        sapQuote.setResaleind(item.getResaleIndicator());
        sapQuote.setUnit(new BigDecimal("1000"));
        sapQuote.setCurr("USD");
        sapQuote.setQuoteqty(new BigDecimal("1000"));
        //sapQuote.setCost(new BigDecimal("0.2"));
//        sapQuote.setCost(new BigDecimal(String.valueOf(item.getCost())));
        sapQuote.setLoekz("");
//        sapQuote.setZzproject(item.getProjectName());
        
        //sapQuote.setErdat(datatypeFactory.newXMLGregorianCalendar("2013-02-12"));
        //sapQuote.setErzet("153345");
        
        sapQuote.setErdat(datatypeFactory.newXMLGregorianCalendar("2013-02-12"));
        sapQuote.setErzet("153345");
        
        sapQuote.setCostCurr("USD");
        sapQuote.setVkorg("HK11");		
		*/
		sb.createSAPQuote(items);
		
	}	
	

	@After
	public void tearDown(){

		
	}
}
