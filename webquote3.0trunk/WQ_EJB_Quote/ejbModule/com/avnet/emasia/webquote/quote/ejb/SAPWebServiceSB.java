
package com.avnet.emasia.webquote.quote.ejb;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;

import com.avnet.emasia.webquote.entity.DrmsAgpRea;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.SalesCostType;
import com.avnet.emasia.webquote.exception.AppException;
import com.avnet.emasia.webquote.exception.CommonConstants;
import com.avnet.emasia.webquote.exception.WebQuoteRuntimeException;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import com.avnet.emasia.webquote.utilities.bean.MailInfoBean;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;
import com.avnet.emasia.webquote.utilities.ejb.MailUtilsSB;
import com.avnet.emasia.webquote.utilities.schedule.HATimerService;
import com.avnet.emasia.webquote.utilities.util.QuoteUtil;
import com.avnet.emasia.webquote.vo.DrProjectVO;
import com.avnet.emasia.webquote.webservice.customer.ZWQCUST;
import com.avnet.emasia.webquote.webservice.customer.ZsdWqCrCustomerException;
import com.avnet.emasia.webquote.webservice.customer.ZwqCustomer;
import com.avnet.emasia.webquote.webservice.drmsagprea.TableOfZstruDrms;
import com.avnet.emasia.webquote.webservice.drmsagprea.TableOfZstruDrmsMsg;
import com.avnet.emasia.webquote.webservice.drmsagprea.ZWQDRAGP;
import com.avnet.emasia.webquote.webservice.drmsagprea.ZstruDrms;
import com.avnet.emasia.webquote.webservice.drmsagprea.ZstruDrmsMsg;
import com.avnet.emasia.webquote.webservice.drmsagprea.vo.DrmsAgp;
import com.avnet.emasia.webquote.webservice.zwqrmtosap.TableOfZquoteBlklst;
import com.avnet.emasia.webquote.webservice.zwqrmtosap.TableOfZwqrmtosapMsg;
import com.avnet.emasia.webquote.webservice.zwqrmtosap.ZWQRMTOSAP;
import com.avnet.emasia.webquote.webservice.zwqrmtosap.ZquoteBlklst;
import com.sap.document.sap.soap.functions.mc_style.TableOfZquoteLst;
import com.sap.document.sap.soap.functions.mc_style.TableOfZquoteMsg;
import com.sap.document.sap.soap.functions.mc_style.ZWQTOSAP;
import com.sap.document.sap.soap.functions.mc_style.ZquoteLst;
import com.sap.document.sap.soap.functions.mc_style.ZquoteMsg;
/**
 * Session Bean implementation class SAPWebServiceSB
 */

@Stateless
@LocalBean
public class SAPWebServiceSB {
	
	private static final Logger LOG =Logger.getLogger(SAPWebServiceSB.class.getName());
	
	private static final String ID = "SAP_WEB_SERVICE_USER_ID";
	private static final String PASSWORD = "SAP_WEB_SERVICE_USER_PASSWORD";
	private static final String CREATE_PROSPECTIVE_CUSTOMER_URL = "SAP_WEB_SERVICE_CREATE_PROSPECTIVE_CUSTOMER_URL"; 
	private static final String CREATE_SAP_QUOTE_URL = "SAP_WEB_SERVICE_CREATE_SAP_QUOTE_URL";
	private static final String DRMS_AGP_REA_URL = "SAP_WEB_SERVICE_DRMS_AGP_REA_URL";
	
	private static final String DRMS_AGP_MODE_R = "R";
	private static final String DRMS_AGP_MODE_W = "W";
	
	private static final String ZWQ_DR_AGP_URL = "SAP_WEB_SERVICE_ZWQ_DR_AGP_URL";
	@EJB
	SysConfigSB sysConfigSB;
	
	@EJB
	MailUtilsSB mailUtilsSB;
	
	@EJB
	QuoteSB quoteSB;

    public SAPWebServiceSB() {

    }
    
    public TableOfZquoteMsg createSAPQuote(List<QuoteItem> quoteItems) throws AppException{
    	return createSAPQuote(quoteItems, true);
    }
    
    public TableOfZquoteMsg createSAPQuoteNotSendEmail(List<QuoteItem> quoteItems) throws AppException{
    	return createSAPQuote(quoteItems, false);
    }
    
    private TableOfZquoteMsg createSAPQuote(List<QuoteItem> quoteItems, boolean issendEmailWhenFailed) throws AppException{

    	LOG.info("Create SAP Quote - Web Service ");
    	
    	long start = System.currentTimeMillis();

    	JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();

    	factory.setServiceClass(ZWQTOSAP.class);
    	
    	factory.setAddress(sysConfigSB.getProperyValue(CREATE_SAP_QUOTE_URL));
    	
    	ZWQTOSAP createQuoteService = (ZWQTOSAP)factory.create();

    	Client client = ClientProxy.getClient(createQuoteService);
    	HTTPConduit http = (HTTPConduit) client.getConduit();
    	http.getAuthorization().setUserName(getId());
    	http.getAuthorization().setPassword(getPassword());

        try{
        	
        	DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        	
        	TableOfZquoteLst tableOfZquoteLst = new TableOfZquoteLst();
        	
        	TableOfZquoteMsg tableOfZquoteMsg = new TableOfZquoteMsg();
        	
        	for(QuoteItem item : quoteItems){
        		
        		LOG.info("Create SAP Quote for:" + item.getQuoteNumber());
        		
    	        ZquoteLst sapQuote = new ZquoteLst();
    	        
    	        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	        DateFormat timeFormat = new SimpleDateFormat("HHmmss");
    	        
    	        
    	        //Mandatory field
    	        sapQuote.setRfqNo(item.getQuoteNumber());
    	        if(item.getQuotationEffectiveDate() != null){
    	        	sapQuote.setValidfrom(datatypeFactory.newXMLGregorianCalendar(dateFormat.format(item.getQuotationEffectiveDate())));
    	        }
    	        
    	        if(item.getQuoteExpiryDate()!=null){
    	        	sapQuote.setValidto(datatypeFactory.newXMLGregorianCalendar(dateFormat.format(item.getQuoteExpiryDate())));
    	        }
    	        //change  for quotePrice
    	        if(item.getQuotedPrice() != null){
    	        	BigDecimal convertToRfqValueWithDouble = item.convertToRfqValueWithDouble(item.getQuotedPrice());
    	        	sapQuote.setQuoteprice(convertToRfqValueWithDouble ==null ? null:convertToRfqValueWithDouble.multiply(new BigDecimal(1000)).setScale(2,BigDecimal.ROUND_HALF_UP));
    	        	
    	        }
 
    	        //change
    	        sapQuote.setCurr(item.getRfqCurr().name());
    	        //change
    	        if(item.getFinalCurr() != null){
    	        	sapQuote.setTcurr(item.getFinalCurr().name());
    	        }
    	        //change
    	        sapQuote.setCostCurr(item.getBuyCurr().name());
    	        sapQuote.setUnit(new BigDecimal(1000));
   	        	sapQuote.setMatnr(item.getSapPartNumber());
    	        sapQuote.setZregion(item.getQuote().getBizUnit().getName());    	        
    	        
    	        sapQuote.setErdat(datatypeFactory.newXMLGregorianCalendar(dateFormat.format(item.getSentOutTime()))); //creation date

    	        if(item.getShipmentValidity() != null){
    	        	sapQuote.setShipvalidto(datatypeFactory.newXMLGregorianCalendar(dateFormat.format(item.getShipmentValidity())));
    	        }else{
    	        	Calendar cal = Calendar.getInstance();
    	        	cal.set(Calendar.YEAR, 9999);
    	        	cal.set(Calendar.MONTH, 11);
    	        	cal.set(Calendar.DAY_OF_MONTH, 31);
    	        	sapQuote.setShipvalidto(datatypeFactory.newXMLGregorianCalendar(dateFormat.format(cal.getTime())));
    	        }
    	        
    	        //Option field    	        
    	        if(item.getFinalQuotationPrice() != null){
    	        	sapQuote.setFinalprice(multiply1k(item.getFinalQuotationPrice()));
    	        }
    	        //change
    	        if(item.getCost() != null){
    	        	sapQuote.setCost(multiply1k(item.getCost()).setScale(2, RoundingMode.HALF_UP));
    	        }
    	        
    	        if(item.getResaleIndicator()!= null){
    	        	sapQuote.setResaleind(item.getResaleIndicator());
    	        }
    	        
    	        sapQuote.setKunnr(item.getSoldToCustomer().getCustomerNumber());
    	        
    	        if(item.getShipToCustomer() != null){
    	        	sapQuote.setShipto(item.getShipToCustomer().getCustomerNumber());
    	        }
    	        
    	        if(item.getEndCustomer() != null){
    	        	sapQuote.setEndcust(item.getEndCustomer().getCustomerNumber());
    	        }
    	        
    	        if(item.getQuotedQty() != null){
    	        	sapQuote.setQuoteqty(new BigDecimal(item.getQuotedQty()));
    	        }

    	        String mfr = null;
    	        if(item.getQuotedMaterial() != null){
    	        	mfr = item.getQuotedMaterial().getManufacturer().getName();
    	        }else{
//    	        	Issue 5 �C if the length of Quoted MFR code is longer than 3 chars, system should capture the first 3 chars of Quoted MFR Code and send it to SAP. 
//    	        	The checking should be applied to all quotes, but now it is only applied when the quoted material id exists. And system should send the Quoted MFR, not Requested MFR.
    	        	if(null!=item.getQuotedMfr()) { 
    	        		mfr = item.getQuotedMfr().getName();
    	        	}else{
    	        		mfr = item.getRequestedMfr().getName();
    	        	}
    	        }

    	        if(mfr != null && mfr.length() > 3){
    	        	mfr = mfr.substring(0, 3);
    	        }
    	        sapQuote.setMfrnr(mfr);

    	        if(item.getQuotedMaterial() != null){
    	        	sapQuote.setMfrpn(item.getQuotedMaterial().getFullMfrPartNumber());
    	        }else{
    	        	//Issue 2 �C if there is no quoted material Id in the quote item, 
    	        	//system should send quoted part number to SAP, not requested part number.
    	        	//fix INC0176331
    	        	if(item.getQuotedPartNumber() != null){
    	        		sapQuote.setMfrpn(item.getQuotedPartNumber());   
    	        	}else{
    	        		sapQuote.setMfrpn(item.getRequestedPartNumber());
    	        	}
    	        	
    	        }
    	        
    	        if(item.getMultiUsageFlag() != null && item.getMultiUsageFlag()){
    	        	sapQuote.setMultiUse("X");
    	        }else{
    	        	sapQuote.setMultiUse("");
    	        }    	     
    	        
    	        
    	        if(item.getUsedFlag() != null && item.getUsedFlag() == true ){
    	        	sapQuote.setZflagUsed("X");
    	        }else{
    	        	sapQuote.setZflagUsed("");
    	        }  
    	        
    	        if(item.getCustomerGroupId() != null){
    	        	sapQuote.setKonzs(item.getCustomerGroupId()); //Customer group Key for future phase
    	        }
    	        
    	        if(item.getStage() != null && item.getStage().equals(QuoteSBConstant.QUOTE_STAGE_INVALID)){
    	        	sapQuote.setLoekz("X"); //Deletion indicator
    	        }
    	        
    	            	        
    	        sapQuote.setZzproject(QuoteUtil.getDrmsKey(item)); //DRMS number
    	        
    	        if(item.getPmoq() != null){
    	        	sapQuote.setPmoq(item.getPmoq());
    	        }
    	        
    	        sapQuote.setErzet(timeFormat.format(item.getSentOutTime()));
    	        sapQuote.setQcComment(item.getQcComment());
    	        //change
    	        if(item.getFinalCurr() != null){
    	        	sapQuote.setTcurr(item.getFinalCurr().name());
    	        }
    	        
    	        if(item.getHandling() != null){
    	        	sapQuote.setHandling(item.getHandling());
    	        	
    	        	 if(item.getFinalCurr().name().equals(item.getRfqCurr().name())){
    	    	        	
    	    	        	BigDecimal result = item.getExRateRfq().multiply(item.getHandling());
    	    	        	result = result.setScale(5, RoundingMode.HALF_UP);
    	    	        	sapQuote.setExcRate(result);
    	    	        }else{
    	    	        	if (item.getExRateFnl() != null){
    	    	        	BigDecimal result = item.getExRateFnl().multiply(item.getHandling());
    	    	        	result = result.setScale(5, RoundingMode.HALF_UP);
    	    	        	sapQuote.setExcRate(result);
    	    	        }
    	    	        	}
    	     
    	        }
    	        
//    	        if(item.getHandling() != null && item.getExRateRfq() != null){
//    	        	
//    	        	BigDecimal result = item.getHandling().multiply(item.getExRateRfq());
//    	        	result = result.setScale(5, RoundingMode.HALF_UP);
//    	        	sapQuote.setExcRate(result);
//    	        	
//    	        }
    	       
    	        
    	        
    	        //for defect 76&103 adding cost indicator 
    	        sapQuote.setCostInd(item.getCostIndicator());
    	        
    	        // DP project add tow fields by Lenon.Yang(2016-04-26)
    	        if(item.getQuote() != null && StringUtils.isNotBlank(item.getQuote().getDpReferenceId())) {
    	        	sapQuote.setDpRefId(item.getQuote().getDpReferenceId());
    	        }
    	        
    	        if(StringUtils.isNotBlank(item.getDpReferenceLineId())) {
    	        	sapQuote.setDpRefLineId(item.getDpReferenceLineId());
    	        }
    	        
    	        //Bryan Start
    	        if(item.getSalesCostType() != SalesCostType.NonSC){
    	        	
    	        	if(item.getSalesCostType() != null){
    	        		sapQuote.setSalesCostType(item.getSalesCostType().toString());
    	        		LOG.info("sapQuote.getSalesCostType(): " + sapQuote.getSalesCostType());
    	        	}
    	        	//change
    	        	if(item.getSalesCost() != null && item.getExRateRfq() !=null && item.getExRateBuy()!= null){
    	        		BigDecimal divide = item.getExRateRfq().divide(item.getExRateBuy(),10,BigDecimal.ROUND_HALF_UP);
    	        		if(item.getHandling()!= null){
    	        			
    	        			sapQuote.setSalesCost(multiply1k(item.getSalesCost().doubleValue()).multiply(item.getHandling()).multiply(divide).setScale(2, RoundingMode.HALF_UP));
        	        		LOG.info("sapQuote.getSalesCost(): " + sapQuote.getSalesCost());
    	        		}else{
    	        			sapQuote.setSalesCost(multiply1k(item.getSalesCost().doubleValue()).multiply(divide).setScale(2, RoundingMode.HALF_UP));
    	        		}
    	        		
    	        	}

    	        }
    	        //Bryan End
    	        
    	        tableOfZquoteLst.getItem().add(sapQuote);
    	        
    	        ZquoteMsg msg = new ZquoteMsg();
    	        
    	        tableOfZquoteMsg.getItem().add(msg);

    	        
        	}
        	
	        
	        javax.xml.ws.Holder<TableOfZquoteLst> zquoteItm = new javax.xml.ws.Holder<TableOfZquoteLst>();
	        javax.xml.ws.Holder<TableOfZquoteMsg> _return = new javax.xml.ws.Holder<TableOfZquoteMsg>();
	        zquoteItm.value = tableOfZquoteLst;
	        _return.value = tableOfZquoteMsg;
	        
	        LOG.info("Call zsdRecvWebQuote begin");
	        createQuoteService.zsdRecvWebQuote(_return, zquoteItm);
	        LOG.info("Call zsdRecvWebQuote end");
	        TableOfZquoteMsg tableMsg = _return.value;
	        List<ZquoteMsg> msg2 = tableMsg.getItem();
	        for(ZquoteMsg msg : msg2){
	        	LOG.info(msg.getNumber() + ", " + msg.getType() + ", " + msg.getMessage());
	        }

	        long end = System.currentTimeMillis();
	        
	        LOG.info("Web Service - CreateSAPQuote calling takes " + (end - start) + " ms");    
	        
	        return tableMsg;
	        
        }
        catch(Exception e){
        	e.printStackTrace();
        	if(issendEmailWhenFailed) {
        		sendEmailWhenCreateQuoteFailed(quoteItems, e, null);
        	}
        	throw new AppException(CommonConstants.COMMON_FAILED_TO_CREATE_UPDATE_QUOTE_IN_SAP, e);
        	
        	
        /*	// Pagination : changes done for WQ - 983
        	appException.setCausedBy(e.getCause());
        	// Pagination : changes ends
        	
        	// for WQ-999
        	appException.setMessage(e.getMessage());
        	appException.setErrorCode(CommonConstants.COMMON_FAILED_TO_CREATE_UPDATE_QUOTE_IN_SAP);
        	throw appException;*/
        }
    	
    }
    
    public void sendEmailWhenCreateQuoteFailed(final List<QuoteItem> quoteItems, Exception e, String otherMesg) {
    	try{
    		
    		StringBuffer sb = new StringBuffer();
    		
    		List<String> quoteNumbers = new ArrayList<String>();
    		for(QuoteItem item : quoteItems){
    			quoteNumbers.add(item.getQuoteNumber());
    		}
    		sb.append("Quote Number: " + quoteNumbers);
    		
    		sb.append("\n\n");
    		
    		sb.append("HostName: " + getHostName());
    		
    		sb.append("\n\n");
    		
    		if (otherMesg != null) {
    			sb.append(otherMesg);
        		
        		sb.append("\n\n");
			}
    		
    		sb.append(getErrorStack(e)); 
    		String jbossNodeName = System.getProperty(HATimerService.JBOSS_NODE_NAME);

            String title = "WebQuote 2.0 - Web Service (Create SAP Quote) Throws Exception";
            if(org.apache.commons.lang.StringUtils.isNotBlank(jbossNodeName)) {
            	title += "(Jboss Node:" + jbossNodeName + ")";
    		}
    		String msg = sb.toString();
    		mailUtilsSB.sendErrorEmail(msg,title);
    		
    	}catch(Exception e1){
    		LOG.log(Level.SEVERE, "Failed to send email notification to web team regarding web service error, Exception message: "+MessageFormatorUtil.getParameterizedStringFromException(e1),  e1);
    	}
    }
    
    public void createProspectiveCustomer(String customerType ,String city, String duplicate, String country, String name, String name2, 
    		String postalCode, String region, String searchTerm, String division, String street, String salesOffice, String salesOrg, 
    		String distributionChannel,String currency, javax.xml.ws.Holder<ZwqCustomer> eCustdtl, javax.xml.ws.Holder<java.lang.String> eKunnr) throws AppException{
    	
        /*LOG.info("Create Prospective Customer - Web Service ");
        //Login method 
        LOG.info("Parameters:  name:" + name + ", name2:" + name2 + ",  country:" + country + ", city:" + city + ", street:" + street + 
        		", salesOrg: " + salesOrg + ", currency:" + currency + ", searchTerm:" + searchTerm + ", duplicate:" + duplicate);
        
        
        ZWQCUST_Service service1 = new ZWQCUST_Service();
        
        LOG.info("Create Web Service...");
        ZWQCUST port1 = service1.getZWQCUST();
		
        BindingProvider prov = (BindingProvider)port1;
        prov.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, getId());
        prov.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, getPassword());
        */
    	
    	if(street != null && street.length() > 35){
    		throw new AppException(CommonConstants.COMMON_FAILED_TO_CREATE_PROSPECTIVE_CUSTOMER);
    	}
    	
    	long start = System.currentTimeMillis();
    	JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();

    	factory.setServiceClass(ZWQCUST.class);
    	factory.setAddress(sysConfigSB.getProperyValue(CREATE_PROSPECTIVE_CUSTOMER_URL));
    	ZWQCUST createProsCustService = (ZWQCUST) factory.create();

    	Client client = ClientProxy.getClient(createProsCustService);
    	HTTPConduit http = (HTTPConduit) client.getConduit();

    	
    	http.getAuthorization().setUserName(getId());
    	http.getAuthorization().setPassword(getPassword());


    	
        try {
        	createProsCustService.zsdWqCrCustomer(
        			customerType,
					city, //city
					duplicate, //duplicate
					country, // country
					name, //name
					name2,//name2
					postalCode,//PSTLZ - Postal code
					region,//IRegio - Region
					searchTerm,//search team
					division,//iSpart - Division
					street,//iStreet
					salesOffice,//iVkbur - sales office
					salesOrg,//iVkorg - sales org
					distributionChannel,//iVtweg - Distribution channel
					currency,//IWaers- currency
					eCustdtl, //Output Parameter: Ecustdtl is a structure with following information. When SAP do the  customer checking, it will return customer code, name, sales org, account type if customer already exists 
					eKunnr //Output Parameter: Prospective Customer Code, If it is blank, the duplication customer exist in SAP and new customer will not create unless IDuplicateFlg is set as Y
					);
		} catch (ZsdWqCrCustomerException e) {
			long end = System.currentTimeMillis();
			LOG.info("takes " + (end -start) + " ms");
			
        	try{
        		StringBuffer sb = new StringBuffer();
        		
        		sb.append("HostName: " + getHostName());
        		sb.append("\n\n");
        		
        		sb.append("Customer Name: " + name);
        		sb.append("\n\n");
        		sb.append("City: " + city);
        		sb.append("\n\n");
        		sb.append("Country: " + country);
        		sb.append("\n\n");
        		sb.append("Sales Org: " + salesOrg);
        		sb.append("\n\n");
        		
        		sb.append(getErrorStack(e)); 
        		String jbossNodeName = System.getProperty(HATimerService.JBOSS_NODE_NAME);

                String title = "WebQuote 2.0 - Web Service (Create Prospective Customer) Throws Exception";
                if(org.apache.commons.lang.StringUtils.isNotBlank(jbossNodeName)) {
                	title += "(Jboss Node:" + jbossNodeName + ")";
        		}
        		String msg = sb.toString();
        		mailUtilsSB.sendErrorEmail(msg,title);
        		LOG.log(Level.SEVERE, msg);
        		
        	}catch(Exception e1){
        		LOG.log(Level.SEVERE, "Failed to send email notification to web team regarding web service error, Exception message: "+MessageFormatorUtil.getParameterizedStringFromException(e1),  e1);
        	}
        				
			throw new AppException(CommonConstants.COMMON_FAILED_TO_CREATE_PROSPECTIVE_CUSTOMER_, e);
		}
        
        long end = System.currentTimeMillis();
        
        if (eKunnr.value.equals("")){
        	LOG.info("Duplicate customer found. It took " + (end-start) + " s");
        }else{
        	LOG.info("Prospective Customer " + eKunnr.value +  " was created. It took " + (end-start)/1000L + " s");
        }
        
    	
    }
    
    private  BigDecimal multiply1k(double price){
    	BigDecimal b = new BigDecimal(Double.toString(price));
    	b = b.multiply(new BigDecimal(1000));
    	b = b.setScale(2,BigDecimal.ROUND_HALF_UP);
    	return b;
    }
    
    private String getId(){
    	return sysConfigSB.getProperyValue(ID);
    }
    
    private String getPassword(){
    	
    	String value = sysConfigSB.getProperyValue(PASSWORD);
    	String password = null;
		try {
			password = AESEncrp.decrypt(value);
		} catch (Exception e) {
			throw new WebQuoteRuntimeException(e);
		}
    	return password;
    }
    
    private String getHostName(){

		try {
			InetAddress addr = InetAddress.getLocalHost();
			
			return addr.getHostName().toString();

		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Exception occured while getting host name, Reason for failure: "+MessageFormatorUtil.getParameterizedStringFromException(e), e);
			return null;
		}
	}    
    
    private String getErrorStack(Exception e){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString(); 
    	
    }
    
   
    
    
    
    public List<DrmsAgp> COMMON_FAILED_TO_ENQUIRY_AGP_IN_SAP(List<DrmsAgp> drmsAgpList) throws AppException {
    	
    	LOG.info("Enquiry AGP - Web Service ");
    	String addr = sysConfigSB.getProperyValue(DRMS_AGP_REA_URL);
    	List<DrmsAgp> returnDrmsAgpList = new ArrayList<DrmsAgp>();
    	if(StringUtils.isNotBlank(addr)) {
        	if(drmsAgpList==null)
        	{
        		LOG.info("Enquiry AGP - Web Service | drmsAgpList is null");
        		return null;
        	}
        	if(drmsAgpList.size()<=0)
        	{
        		LOG.info("Enquiry AGP - Web Service | drmsAgpList is empty");
        		return null;
        	}	
        	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        	long start = System.currentTimeMillis();
        	JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        	factory.setServiceClass(ZWQDRAGP.class);
        	//factory.setAddress("http://emadas0.avnet.com:1081/sap/bc/srt/rfc/sap/zwq_dr_agp/030/zwq_dr_agp/zwq_dr_agp");
        	factory.setAddress(addr);
        	ZWQDRAGP drmsAgpService = (ZWQDRAGP)factory.create();

        	Client client = ClientProxy.getClient(drmsAgpService);
        	HTTPConduit http = (HTTPConduit) client.getConduit();
        	http.getAuthorization().setUserName(getId());
        	http.getAuthorization().setPassword(getPassword());
//        	http.getAuthorization().setUserName("test001");
//        	http.getAuthorization().setPassword("Y5zg&9");
            try{
            	
            	DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
            	TableOfZstruDrms tableOfZstruDrms = new TableOfZstruDrms();
            	TableOfZstruDrmsMsg tableOfZstruDrmsMsg = new TableOfZstruDrmsMsg();
            	for(DrmsAgp item : drmsAgpList)
            	{
            		ZstruDrms zstruDrms = new ZstruDrms();
        	        zstruDrms.setZzproject(item.getZzProject());
            		zstruDrms.setAuthGpChgRea(item.getAuthGpChgRea());
            		zstruDrms.setAuthGpChgRem(item.getAuthGpChgRem());
            		zstruDrms.setAuthGpPercent(item.getAuthGpPercent());
            		if(item.getExpiryDate()!=null){
            			zstruDrms.setExpiryDate(datatypeFactory.newXMLGregorianCalendar(dateFormat.format(item.getExpiryDate())));        		
            		}else{
        	        	Calendar cal = Calendar.getInstance();
        	        	cal.set(Calendar.YEAR, 9999);
        	        	cal.set(Calendar.MONTH, 11);
        	        	cal.set(Calendar.DAY_OF_MONTH, 31);
        	        	zstruDrms.setExpiryDate(datatypeFactory.newXMLGregorianCalendar(dateFormat.format(cal.getTime())));
            			
            		}
            		zstruDrms.setUpnam(item.getUpnam());
            		zstruDrms.setMode(DRMS_AGP_MODE_R);
        	        
        	        tableOfZstruDrms.getItem().add(zstruDrms);
        	        ZstruDrmsMsg msg = new ZstruDrmsMsg();
        	        tableOfZstruDrmsMsg.getItem().add(msg);
        	        
            	}   
    	        javax.xml.ws.Holder<TableOfZstruDrms> zdrmsItm = new javax.xml.ws.Holder<TableOfZstruDrms>();
    	        javax.xml.ws.Holder<TableOfZstruDrmsMsg> _return = new javax.xml.ws.Holder<TableOfZstruDrmsMsg>();
    	        
    	        
    	        zdrmsItm.value = tableOfZstruDrms;
    	        _return.value = tableOfZstruDrmsMsg;
    	        
    	        
    	        ((ZWQDRAGP) drmsAgpService).zsdWqDrAgp(_return, zdrmsItm);
    	        
    	        TableOfZstruDrmsMsg tableMsg = _return.value;
    	        List<ZstruDrmsMsg> msgList = tableMsg.getItem();
    	        for(ZstruDrmsMsg msg : msgList){
    	        	LOG.info(msg.getZzproject() + ", " + msg.getId() + ", " + msg.getType()+ ", " + msg.getNumber()+ ", " + msg.getMessage());
    	        }

    	        TableOfZstruDrms returnZstruDrms = zdrmsItm.value;
    	        List<ZstruDrms> tempList = returnZstruDrms.getItem();
    	        if(tempList!=null && tempList.size()>=0)
    	        	for(ZstruDrms item: tempList)
    	        	{
    	        		DrmsAgp drmsItem = new DrmsAgp();
    	        		drmsItem.setAuthGpChgRea(item.getAuthGpChgRea());
    	        		drmsItem.setAuthGpChgRem(item.getAuthGpChgRem());
    	        		drmsItem.setAuthGpPercent(item.getAuthGpPercent());
    	        		if(item.getExpiryDate()!=null)
    	        		drmsItem.setExpiryDate(item.getExpiryDate().toGregorianCalendar().getTime());
    	        		drmsItem.setMode(item.getMode());
    	        		drmsItem.setUpnam(item.getUpnam());
    	        		drmsItem.setZzProject(item.getZzproject());
    	        		returnDrmsAgpList.add(drmsItem);
    	        	}
    	        
    	        
    	        long end = System.currentTimeMillis();
    	        
    	        LOG.info("Web Service - Enquiry AGP calling takes " + (end - start) + " ms");    
    	        
    	        return returnDrmsAgpList;
    	        
            }
            catch(Exception e)
            {
            	sendErrorEmail("WebQuote 2.0 - Web Service (Enquiry AGP) Throws Exception", e,drmsAgpList );
            	throw new AppException(CommonConstants.COMMON_FAILED_TO_ENQUIRY_AGP_IN_SAP, e);
            }
    	}
    	
    	return returnDrmsAgpList;
    }
    
    
/*    public static void main(String[] args) {
        System.out.println("***********************");
        System.out.println("Create Web Service Client...");
        ZWQDRAGP_Service service1 = new ZWQDRAGP_Service();
        System.out.println("Create Web Service...");
        ZWQDRAGP port1 = service1.getZWQDRAGPSOAP12();
        System.out.println("Call Web Service Operation...");
        System.out.println("Server said: port1.zsdWqDrAgp() is a void method!");
        System.out.println("Create Web Service...");
        ZWQDRAGP port2 = service1.getZWQDRAGPSOAP12();
        System.out.println("Call Web Service Operation...");
        System.out.println("Server said: port2.zsdWqDrAgp() is a void method!");
        System.out.println("Create Web Service...");
        ZWQDRAGP port3 = service1.getZWQDRAGP();
        System.out.println("Call Web Service Operation...");
        System.out.println("Server said: port3.zsdWqDrAgp() is a void method!");
        System.out.println("Create Web Service...");
        ZWQDRAGP port4 = service1.getZWQDRAGP();
        System.out.println("Call Web Service Operation...");
        System.out.println("Server said: port4.zsdWqDrAgp() is a void method!");
        System.out.println("***********************");
        System.out.println("Call Over!");
        
        List<DrmsAgp> drmsAgpList = new ArrayList<DrmsAgp>();
        DrmsAgp dd = new DrmsAgp();
        dd.setZzProject("DRMS/1004730/001/001");
//        dd.setAuthGpChgRea("te");
//        dd.setAuthGpChgRem("gg");
//        dd.setAuthGpPercent(new BigDecimal("23.00"));
//        dd.setExpiryDate(new Date());
        dd.setUpnam("906893");
        drmsAgpList.add(dd);
        try
		{
        	List<DrmsAgp> returnM = enquiryAGP(drmsAgpList);
        	System.out.print("@@@@@@@@@@@@:"+returnM.size());
        	System.out.print("@@@@@@@@@@@@:"+returnM.get(0).getAuthGpChgRea());
        	System.out.print("@@@@@@@@@@@@:"+returnM.get(0).getAuthGpChgRem());
        	System.out.print("@@@@@@@@@@@@:"+returnM.get(0).getZzProject());
        	System.out.print("@@@@@@@@@@@@:"+returnM.get(0).getAuthGpPercent());
        	System.out.print("@@@@@@@@@@@@:"+returnM.get(0).getAuthGpChgRea());
		}
		catch (AppException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     }*/
    
    
    public void updateSAPAGP(List<QuoteItem> quoteItems, String employeeId) throws AppException {

    	LOG.info("Enquiry AGP - Web Service ");
    	String addr = sysConfigSB.getProperyValue(DRMS_AGP_REA_URL);
    	if(StringUtils.isNotBlank(addr)) {
    		if(quoteItems==null || quoteItems.size()==0)
        	{
        		LOG.info("Enquiry AGP - Web Service | drmsAgpList is null");
        		return;
        	}
        	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        	long start = System.currentTimeMillis();
        	JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        	factory.setServiceClass(ZWQDRAGP.class);
        	factory.setAddress(addr);
        	//factory.setAddress("http://emadas0.avnet.com:1081/sap/bc/srt/rfc/sap/zwq_dr_agp/030/zwq_dr_agp/zwq_dr_agp");
        	ZWQDRAGP drmsAgpService = (ZWQDRAGP)factory.create();

        	Client client = ClientProxy.getClient(drmsAgpService);
        	HTTPConduit http = (HTTPConduit) client.getConduit();
        	http.getAuthorization().setUserName(getId());
        	http.getAuthorization().setPassword(getPassword());
        	
        	Calendar cal = Calendar.getInstance();
        	cal.set(Calendar.YEAR, 9999);
        	cal.set(Calendar.MONTH, 11);
        	cal.set(Calendar.DAY_OF_MONTH, 31);

    		NumberFormat format = new DecimalFormat("#");
    		format.setMaximumFractionDigits(2);    	
        	
            try{
            	
            	DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
            	
            	TableOfZstruDrms tableOfZstruDrms = new TableOfZstruDrms();
            	
            	TableOfZstruDrmsMsg tableOfZstruDrmsMsg = new TableOfZstruDrmsMsg();
            	
            	for(QuoteItem item : quoteItems)
            	{
            		
            		ZstruDrms zstruDrms = new ZstruDrms();
            		String key = QuoteUtil.getDrmsKey(item);
            		if(key == null){
            			continue;
            		}
        	        zstruDrms.setZzproject(key);
            		if(item.getDrExpiryDate()!=null){
            			zstruDrms.setExpiryDate(datatypeFactory.newXMLGregorianCalendar(dateFormat.format(item.getDrExpiryDate())));        		
            		}else{
        	        	zstruDrms.setExpiryDate(datatypeFactory.newXMLGregorianCalendar(dateFormat.format(cal.getTime())));
            			
            		}
            		zstruDrms.setUpnam(employeeId);
        	        
        	        zstruDrms.setAuthGpPercent(new BigDecimal(format.format(item.getAuthGp())));
        	        
            		zstruDrms.setAuthGpChgRea(item.getAuthGpReasonCode());
            		zstruDrms.setAuthGpChgRem(item.getAuthGpRemark());
            		zstruDrms.setMode(DRMS_AGP_MODE_W);
        	        
        	        tableOfZstruDrms.getItem().add(zstruDrms);
        	        
        	        ZstruDrmsMsg msg = new ZstruDrmsMsg();
        	        tableOfZstruDrmsMsg.getItem().add(msg);
        	        
            	}   
    	        javax.xml.ws.Holder<TableOfZstruDrms> zdrmsItm = new javax.xml.ws.Holder<TableOfZstruDrms>();
    	        javax.xml.ws.Holder<TableOfZstruDrmsMsg> _return = new javax.xml.ws.Holder<TableOfZstruDrmsMsg>();
    	        
    	        
    	        zdrmsItm.value = tableOfZstruDrms;
    	        _return.value = tableOfZstruDrmsMsg;
    	        
    	        
    	        ((ZWQDRAGP) drmsAgpService).zsdWqDrAgp(_return, zdrmsItm);
    	        
    	        List<ZstruDrmsMsg> messages = _return.value.getItem();
    	        for(ZstruDrmsMsg msg : messages){
    	        	LOG.info(msg.getZzproject() + ", " + msg.getId() + ", " + msg.getType()+ ", " + msg.getNumber()+ ", " + msg.getMessage());
    	        }
    	        
    	        for(QuoteItem item : quoteItems){
    	        	
    	        	String key = QuoteUtil.getDrmsKey(item);
    	        	if(key == null){
    	        		continue;
    	        	}
    	        	
    	        	item.setDrmsUpdated(false);
    	        	
    	        	for(ZstruDrmsMsg message : messages){
    	        		if(message != null && message.getZzproject().equals(key)){
            				if(message.getMessage().equalsIgnoreCase("Update successful") || message.getMessage().equalsIgnoreCase("No changes to save")){
            					item.setDrmsUpdated(true);
            				}else{
            					item.setDrmsUpdated(false);
            					item.setDrmsUpdateFailedDesc(message.getMessage());
            				}
            				break;
    	        		}
    	        		
    	        	}
    	        }

    	        long end = System.currentTimeMillis();
    	        
    	        LOG.info("Web Service - Update AGP calling takes " + (end - start) + " ms");    
    	        
            }
            catch(Exception e)
            {
            	
            	try{

            		StringBuffer sb = new StringBuffer();
            		List<String> messages = new ArrayList<String>();
            		for(QuoteItem item : quoteItems){
            			messages.add("Quote Number: " + item.getQuoteNumber() + " DRMS Key:" + QuoteUtil.getDrmsKey(item));
            		}
            		
            		sb.append(messages);
            		
            		sb.append("\n\n");
            		
            		sb.append("HostName: " + getHostName());
            		
            		sb.append("\n\n");
            		
            		sb.append(getErrorStack(e)); 
            		String jbossNodeName = System.getProperty(HATimerService.JBOSS_NODE_NAME);

                    String title = "WebQuote 2.0 - Web Service (updateSAPAGP) Throws Exception";
                    if(org.apache.commons.lang.StringUtils.isNotBlank(jbossNodeName)) {
                    	title += "(Jboss Node:" + jbossNodeName + ")";
            		}
            		String msg = sb.toString();
            		mailUtilsSB.sendErrorEmail(msg,title);
                    
            		
            	}catch(Exception e1){
            		LOG.log(Level.SEVERE, "Failed to send email notification to web team regarding web service error, Exception message: "+MessageFormatorUtil.getParameterizedStringFromException(e1),  e1);
            	}        	
            	throw new AppException(CommonConstants.WQ_EJB_QUOTE_FAILED_TO_UPDATE_AGP_IN_SAP, e);
            }
    	}
    	
    	
    }
    
    
    
    /**
     * 
     * @param quoteItems
     * @throws AppException
     * @History 
     * June Guo 2014/12/2 to use enquirySAPAGP to call SAP
     */
    @SuppressWarnings("unchecked")
	public void enquirySAPAGP(List<QuoteItem> quoteItems) throws AppException {

    	LOG.info("Enquiry AGP - Web Service ");
    	String addr = sysConfigSB.getProperyValue(DRMS_AGP_REA_URL);
    	if(StringUtils.isBlank(addr)) {
    		LOG.info("Enquiry AGP - Web Service | DRMS_AGP_REA_URL is not exists");
    		return;
    	}
    	
    	if(quoteItems==null || quoteItems.size()==0)
    	{
    		LOG.info("Enquiry AGP - Web Service | drmsAgpList is null");
    		return;
    	}
    	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	long start = System.currentTimeMillis();
    	
    	Calendar cal = Calendar.getInstance();
    	cal.set(Calendar.YEAR, 9999);
    	cal.set(Calendar.MONTH, 11);
    	cal.set(Calendar.DAY_OF_MONTH, 31);
    	
        try{
        	
        	DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        	TableOfZstruDrms tableOfZstruDrms = new TableOfZstruDrms();
        	TableOfZstruDrmsMsg tableOfZstruDrmsMsg = new TableOfZstruDrmsMsg();
        	for(QuoteItem item : quoteItems)
        	{
        		ZstruDrms zstruDrms = new ZstruDrms();
        		
        		String key = QuoteUtil.getDrmsKey(item);
        		
        		if(key == null){
        			continue;
        		}

        		zstruDrms.setZzproject(key);
        		if(item.getDrExpiryDate()!=null){
        			zstruDrms.setExpiryDate(datatypeFactory.newXMLGregorianCalendar(dateFormat.format(item.getDrExpiryDate())));        		
        		}else{
    	        	zstruDrms.setExpiryDate(datatypeFactory.newXMLGregorianCalendar(dateFormat.format(cal.getTime())));
        			
        		}
        		zstruDrms.setUpnam(item.getQuote().getSales().getEmployeeId());
        		zstruDrms.setMode(DRMS_AGP_MODE_R);
    	        
    	        tableOfZstruDrms.getItem().add(zstruDrms);
    	        
    	        ZstruDrmsMsg msg = new ZstruDrmsMsg();
    	        tableOfZstruDrmsMsg.getItem().add(msg);
    	        
        	}   
	        
	        List<Object> lst = this.enquirySAPAGPBasic(tableOfZstruDrms, tableOfZstruDrmsMsg);
	        
	        List<ZstruDrmsMsg> messages = (List<ZstruDrmsMsg>) lst.get(1);
	        
	        List<ZstruDrms> drmss =(List<ZstruDrms>) lst.get(0);
	        
        	for(ZstruDrmsMsg message : messages){
        		LOG.info(message.getZzproject() + ", " + message.getId() + ", " + message.getType()+ ", " + message.getNumber()+ ", " + message.getMessage());
        	}
	        
	        for(QuoteItem item : quoteItems){
	        	
	        	String key = QuoteUtil.getDrmsKey(item);
	        	item.setDrmsUpdated(true);
	        	for(ZstruDrmsMsg message : messages){
	        		if(key!= null && message.getZzproject().equals(key)){
	        			item.setDrmsUpdated(false);
	        			item.setDrmsUpdateFailedDesc(message.getMessage());
	        			
	        			break;
	        		}
	        	}
	        }
	        
	        List<DrmsAgpRea> drmsAgpReasons = quoteSB.findAllDrmsAgpReasons();;
	        
	        for(QuoteItem item : quoteItems){
	        	if(item.getDrmsUpdated() == false){
	        		continue;
	        	}
	        	
	        	String key = QuoteUtil.getDrmsKey(item);
	        	
	        	if(key == null){
	        		continue;
	        	}
	        	
	        	for(ZstruDrms drms : drmss){
	        		if(drms.getZzproject().equals(key)){
						item.setAuthGp(drms.getAuthGpPercent() == null ? null : drms.getAuthGpPercent().doubleValue());
						item.setAuthGpReasonCode(drms.getAuthGpChgRea());
						item.setAuthGpRemark(drms.getAuthGpChgRem());
						
						XMLGregorianCalendar date = drms.getExpiryDate();
		
						if(date != null &&  !(date.getYear()== 9999 && date.getMonth() ==12 && date.getDay() == 31)  ){
							
							Calendar calendar = Calendar.getInstance();
							calendar.set(Calendar.YEAR, date.getYear());
							calendar.set(Calendar.MONTH, date.getMonth()-1);
							calendar.set(Calendar.DAY_OF_MONTH, date.getDay());
							calendar.set(Calendar.HOUR_OF_DAY, 15);
							calendar.set(Calendar.MINUTE, 0);
							calendar.set(Calendar.SECOND, 0);
							calendar.set(Calendar.MILLISECOND, 0);
							
							item.setDrExpiryDate(calendar.getTime());
						}
						
						for(DrmsAgpRea drmsAgpRea : drmsAgpReasons){
							if(drmsAgpRea.getAuthGpChgRea().equals(item.getAuthGpReasonCode())){
								item.setAuthGpReasonDesc(drmsAgpRea.getAuthGpChgDesc());
								break;
							}
						}						
						
	        			break;
	        		}
	        	}
	        }	        

	        
	        long end = System.currentTimeMillis();
	        
	        LOG.info("Web Service - Enquiry AGP calling takes " + (end - start) + " ms");    
	        
	        return;
	        
        }
        catch(Exception e)
        {
        	
        	try{
        		
         		
        		StringBuffer sb = new StringBuffer();
        		List<String> messages = new ArrayList<String>();
        		for(QuoteItem item : quoteItems){
        			messages.add("Quote Number: " + item.getQuoteNumber() + " DRMS Key:" + QuoteUtil.getDrmsKey(item));
        		}
        		
        		sb.append(messages);
        		
        		sb.append("\n\n");
        		
        		sb.append("HostName: " + getHostName());
        		
        		sb.append("\n\n");
        		
        		sb.append(getErrorStack(e)); 
        		String jbossNodeName = System.getProperty(HATimerService.JBOSS_NODE_NAME);
        		String title = "WebQuote 2.0 - Web Service (enquirySAPAGP) Throws Exception";
        		if(org.apache.commons.lang.StringUtils.isNotBlank(jbossNodeName)) {
        			title += "(Jboss Node:" + jbossNodeName + ")";
        		}
        		String msg = sb.toString();
        		mailUtilsSB.sendErrorEmail(msg,title);
        		
        	}catch(Exception e1){
        		LOG.log(Level.SEVERE, "Failed to send email notification to web team regarding web service error, Reason for failure: "+MessageFormatorUtil.getParameterizedStringFromException(e1),  e1);
        	}
        	        	
        	
        	throw new AppException(CommonConstants.COMMON_FAILED_TO_ENQUIRY_AGP_IN_SAP, e);
        }
    	
    }
    
     public void sendErrorEmail(String msg, Exception e, List<DrmsAgp> drmsAgpList)
     {
    	 try{
    		String sender = sysConfigSB.getProperyValue(QuoteSBConstant.ERROR_EMAIL_FROM_ADDRESS);
    		String recipients = sysConfigSB.getProperyValue(QuoteSBConstant.ERROR_EMAIL_TO_ADDRESS);
    		List<String> to = new ArrayList<String>();
     		if(null!=recipients && !recipients.isEmpty()) {
     			String[] tos = recipients.split(";");
     			for(String recipient:tos) {
     				if(null!=recipient && !recipient.trim().isEmpty())
     					to.add(recipient);
     			}
     		}
     		//add by Lenon.Yang(043044) 2016/05/23
     		String jbossNodeName = System.getProperty(HATimerService.JBOSS_NODE_NAME);
     		if(org.apache.commons.lang.StringUtils.isNotBlank(jbossNodeName)) {
     			msg += "(Jboss Node:" + jbossNodeName + ")";
			}
     		MailInfoBean mail = new MailInfoBean();
     		mail.setMailSubject(msg);
     		mail.setMailFrom(sender);
     		mail.setMailTo(to);
     		StringBuffer sb = new StringBuffer();
     		List<String> quoteNumbers = new ArrayList<String>();
     		for(DrmsAgp item : drmsAgpList){
     			quoteNumbers.add(item.toString());
     		}
     		sb.append("Quote Number: " + quoteNumbers);
     		sb.append("\n\n");
     		sb.append("HostName: " + getHostName());
     		sb.append("\n\n");
     		sb.append(getErrorStack(e)); 
     		mail.setMailContent(sb.toString());
            mailUtilsSB.sendTextMail(mail);
     		
     	}catch(Exception e1){
     		LOG.log(Level.SEVERE, "Failed to send email notification to web team regarding web service error, Exception message: "+MessageFormatorUtil.getParameterizedStringFromException(e1),  e1);
     	}
     }
         
     public void sendMfrAndRegion2SAP(List<ZquoteBlklst> list) throws AppException {

     	LOG.info(" send the mapping of MFR and region to SAP - Web Service ");
    	
	    long start = System.currentTimeMillis();
     	if(list==null || list.size()==0)
     	{
     		LOG.info("send the mapping of MFR and region to SAP - Web Service | List is null");
     		return;
     	}

     	JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
     	factory.setServiceClass(ZWQRMTOSAP.class);
     	factory.setAddress(sysConfigSB.getProperyValue(ZWQ_DR_AGP_URL));
     	ZWQRMTOSAP drmsAgpService = (ZWQRMTOSAP)factory.create();

     	Client client = ClientProxy.getClient(drmsAgpService);
     	HTTPConduit http = (HTTPConduit) client.getConduit();
     	http.getAuthorization().setUserName(getId());
     	http.getAuthorization().setPassword(getPassword());     	
     	
         try{         	       	
         	TableOfZquoteBlklst tableOfZquoteBlklst = new TableOfZquoteBlklst();        	
         	TableOfZwqrmtosapMsg tableOfZstruDrmsMsg = new TableOfZwqrmtosapMsg();
         	
         	for(ZquoteBlklst item : list){        		   	        
         		tableOfZquoteBlklst.getItem().add(item);    	        
         		com.avnet.emasia.webquote.webservice.zwqrmtosap.ZquoteMsg msg = new com.avnet.emasia.webquote.webservice.zwqrmtosap.ZquoteMsg();
     	        tableOfZstruDrmsMsg.getItem().add(msg); 	        
         	}   
 	        javax.xml.ws.Holder<TableOfZquoteBlklst> zwqBlklst = new javax.xml.ws.Holder<TableOfZquoteBlklst>();
 	        javax.xml.ws.Holder<TableOfZwqrmtosapMsg> _return = new javax.xml.ws.Holder<TableOfZwqrmtosapMsg>();
 	        
 	        
 	        zwqBlklst.value = tableOfZquoteBlklst;
 	        _return.value = tableOfZstruDrmsMsg;
 	        
 	        drmsAgpService.zsdRecvWqmrpblkctl(_return, zwqBlklst);
 	        
 	        List<com.avnet.emasia.webquote.webservice.zwqrmtosap.ZquoteMsg> messages = _return.value.getItem();
 	        for(com.avnet.emasia.webquote.webservice.zwqrmtosap.ZquoteMsg msg : messages){
 	        	LOG.info( msg.getId() + ", " + msg.getType()+ ", " + msg.getNumber()+ ", " + msg.getMessage());
 	        }
 	        	
 	        long end = System.currentTimeMillis();
 	        LOG.info("Web Service - Update AGP calling takes " + (end - start) + " ms");    
 	        
         } catch(Exception e){
         	
         	try{
         		StringBuffer sb = new StringBuffer();
         		
         		List<String> messages = new ArrayList<String>();
         		for(ZquoteBlklst item : list){
         			messages.add("mfr: " + item.getMfrnr() + " region:" + item.getZregion());
         		}
         		
         		sb.append(messages).append("\n\n").append("HostName: " + getHostName());
         		sb.append("\n\n").append(getErrorStack(e));        		
        		String jbossNodeName = System.getProperty(HATimerService.JBOSS_NODE_NAME);

                String title = "WebQuote 2.0 - Web Service (sendMfrAndRegion2SAP) Throws Exception";
                if(org.apache.commons.lang.StringUtils.isNotBlank(jbossNodeName)) {
                	title += "(Jboss Node:" + jbossNodeName + ")";
        		}
        		String msg = sb.toString();
        		mailUtilsSB.sendErrorEmail(msg,title);
         		
         	}catch(Exception e1){
         		LOG.log(Level.SEVERE, "Failed to send email notification to web team regarding web service error, Exception message: "+MessageFormatorUtil.getParameterizedStringFromException(e1),  e1);
         	}        	
         	throw new AppException(CommonConstants.COMMON_FAILED_TO_SEND_THE_MAPPING_OF_MFR_AND_REGION_TO_SAP, e);
         }
     	
     }
     
     /**
      * This method to get information from AGP SAP , return value is List , List.get(0) is drmss information, List.get(1) is message information
      * @param tableOfZstruDrms
      * @param tableOfZstruDrmsMsg
      * @return
      * @throws AppException
      */
     public List<Object> enquirySAPAGPBasic(TableOfZstruDrms tableOfZstruDrms,TableOfZstruDrmsMsg tableOfZstruDrmsMsg) throws AppException {

     	LOG.info("Enquiry AGP - Web Service ---basic");
     	String addr = sysConfigSB.getProperyValue(DRMS_AGP_REA_URL);
     	 List<Object> lst = new ArrayList<Object>();
     	if(StringUtils.isNotBlank(addr)) {
     		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
         	factory.setServiceClass(ZWQDRAGP.class);
         	factory.setAddress(addr);
         	ZWQDRAGP drmsAgpService = (ZWQDRAGP)factory.create();

         	Client client = ClientProxy.getClient(drmsAgpService);
         	HTTPConduit http = (HTTPConduit) client.getConduit();
         	http.getAuthorization().setUserName(getId());
         	http.getAuthorization().setPassword(getPassword());
         	
         	Calendar cal = Calendar.getInstance();
         	cal.set(Calendar.YEAR, 9999);
         	cal.set(Calendar.MONTH, 11);
         	cal.set(Calendar.DAY_OF_MONTH, 31);
             try{
             	

     	        javax.xml.ws.Holder<TableOfZstruDrms> zdrmsItm = new javax.xml.ws.Holder<TableOfZstruDrms>();
     	        javax.xml.ws.Holder<TableOfZstruDrmsMsg> _return = new javax.xml.ws.Holder<TableOfZstruDrmsMsg>();
     	        
     	        
     	        zdrmsItm.value = tableOfZstruDrms;
     	        _return.value = tableOfZstruDrmsMsg;
     	        
     	        
     	        ((ZWQDRAGP) drmsAgpService).zsdWqDrAgp(_return, zdrmsItm);
     	        
     	        List<ZstruDrmsMsg> messages = _return.value.getItem();
     	        
     	        List<ZstruDrms> drmss = zdrmsItm.value.getItem();
     	       
     	        lst.add(drmss);
     	        lst.add(messages);
     	        
     	        return lst;
     	        
             }
             catch(Exception e)
             {
             	
             	//TODO:��update to branch
             	for(ZstruDrms zs:tableOfZstruDrms.getItem()){
             		LOG.log(Level.SEVERE, "Failed to enquiry AGP in SAP with key: "+zs.getZzproject());         		
             	}
             	throw new AppException(CommonConstants.COMMON_FAILED_TO_ENQUIRY_AGP_IN_SAP, e);
             }
     	}
     	
     	return lst;
     }
     
     @SuppressWarnings("unchecked")
     /**
      * for fix enhancement 342 change enquirySAPAGPForRFQ to enquirySAPAGPToGetDrExpiryDate 
      * @param drProjectVos
      * @throws AppException
      */
	public void enquirySAPAGPToGetDrExpiryDate(List<DrProjectVO> drProjectVos) throws AppException {

     	LOG.info("Enquiry AGP - Web Service To get expiry date ");
     	
     	String addr = sysConfigSB.getProperyValue(DRMS_AGP_REA_URL);
    	if(StringUtils.isBlank(addr)) {
    		LOG.info("Enquiry AGP - Web Service | DRMS_AGP_REA_URL is not exists");
    		return;
    	}
     	
     	if(drProjectVos==null || drProjectVos.size()==0)
     	{
     		LOG.info("Enquiry AGP - Web Service to get expiry date| drProjectVos is null");
     		return;
     	}
     	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
     	long start = System.currentTimeMillis();
     	
     	Calendar cal = Calendar.getInstance();
     	cal.set(Calendar.YEAR, 9999);
     	cal.set(Calendar.MONTH, 11);
     	cal.set(Calendar.DAY_OF_MONTH, 31);
     	
         try{
        	Date currentDay = new Date(); // for fix defect 220 june guo 20150317 to compare dr expiry >=current day then display
         	DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
         	TableOfZstruDrms tableOfZstruDrms = new TableOfZstruDrms();
         	TableOfZstruDrmsMsg tableOfZstruDrmsMsg = new TableOfZstruDrmsMsg();
         	for(DrProjectVO vo : drProjectVos)
         	{
         		ZstruDrms zstruDrms = new ZstruDrms();
         		String key = QuoteUtil.getDrmsKey(vo.getDrNedaItem().getDrNedaHeader().getDrProjectHeader().getProjectId(),
         				vo.getDrNedaItem().getId().getNedaId(),
         				vo.getDrNedaItem().getId().getNedaLineNumber());
         		
         		if(key == null){
         			continue;
         		}
         		
         		zstruDrms.setZzproject(key);
         		zstruDrms.setExpiryDate(datatypeFactory.newXMLGregorianCalendar(dateFormat.format(cal.getTime())));
         		
         		zstruDrms.setUpnam(vo.getSalesEmployeeId());//----need to confirm 
         		zstruDrms.setMode(DRMS_AGP_MODE_R);
     	        tableOfZstruDrms.getItem().add(zstruDrms);
     	        tableOfZstruDrmsMsg.getItem().add(new ZstruDrmsMsg());
     	        
         	}   
 	        
 	        List<Object> returnValue = this.enquirySAPAGPBasic(tableOfZstruDrms, tableOfZstruDrmsMsg);
 	        List<ZstruDrms> drmss =(List<ZstruDrms>) returnValue.get(0);

 	        for(DrProjectVO vo : drProjectVos){
 	        	String key = QuoteUtil.getDrmsKey(vo.getDrNedaItem().getDrNedaHeader().getDrProjectHeader().getProjectId(),
         				vo.getDrNedaItem().getId().getNedaId(),
         				vo.getDrNedaItem().getId().getNedaLineNumber());
 	        	
 	        	if(key == null){
 	        		continue;
 	        	}
 	        	
 	        	for(ZstruDrms drms : drmss){
 	        		if(drms.getZzproject().equals(key)){
 						XMLGregorianCalendar date = drms.getExpiryDate();
 						LOG.info("RFQ get DR expiry date===>" + date.toString());
 						vo.setDrExpiryDate(QuoteUtil.getDrExpiryDate(date));
 						LOG.info("RFQ get DR expiry date VO IN ===> " +vo.getDrExpiryDate());
 						vo.setDisplayDrExpiryDate(currentDay);
 	        			break;
 	        		}
 	        	}
 	        }	        

 	        
 	        long end = System.currentTimeMillis();
 	        LOG.info("Web Service - Enquiry AGP calling takes " + (end - start) + " ms");    
 	        
 	        return;
 	        
         }
         catch(Exception e)
         {
         	throw new AppException(CommonConstants.COMMON_FAILED_TO_ENQUIRY_AGP_IN_SAP, e);
         }
     	
     }
	
}
