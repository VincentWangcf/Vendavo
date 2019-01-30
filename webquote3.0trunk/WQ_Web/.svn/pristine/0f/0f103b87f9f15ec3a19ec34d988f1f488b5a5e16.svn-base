package com.avnet.emasia.webquote.web.user;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
//todo hu.andy
/*import com.avnet.emasia.webquote.dbmigrate.MfrDetailsMigrateSB;
import com.avnet.emasia.webquote.dbmigrate.QuoteMigrateSB;
import com.avnet.emasia.webquote.dbmigrate.SystemCodeMigrateSB;
import com.avnet.emasia.webquote.dbmigrate.UserMigrateSB;*/
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.Manufacturer;
//todo hu.andy
/*import com.avnet.emasia.webquote.entity.Material;*/
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.exception.AppException;
import com.avnet.emasia.webquote.quote.ejb.QuoteSB;
import com.avnet.emasia.webquote.quote.ejb.SAPWebServiceSB;
import com.avnet.emasia.webquote.user.ejb.UserSB;
//import com.sap.document.sap.soap.functions.mc_style.ZwqCustomer;
import com.avnet.emasia.webquote.webservice.customer.ZwqCustomer;


@ManagedBean
@SessionScoped

public class EjbTestMB implements Serializable {
	
	
	private static final Logger LOG = Logger.getLogger(EjbTestMB.class.getName());

	@EJB
	private transient QuoteSB quoteSB;
	
	@EJB
	private UserSB userSB;
	
	private transient UploadedFile file;
	
	@EJB
	SAPWebServiceSB sapWebServiceSB;
	
	//todo hu.andy
/*	@EJB
	private QuoteMigrateSB quoteMigrateSB;

	@EJB
	private UserMigrateSB userMigrateSB;
	
	@EJB
	private MfrDetailsMigrateSB mfrDetailsMigrateSB ;

	@EJB
	private SystemCodeMigrateSB systemCodeMigrateSB ;*/
	
	private String batchRef;
	
	private String region;

	@PostConstruct
	public void initialize() {


	}
	//todo hu.andy
/*	public void callQuoteEJB(ActionEvent event) {
		
		quoteMigrateSB.load(batchRef, region);
	}


	public void callQuoteEJBForAttachment(ActionEvent event) {
		
		quoteMigrateSB.loadAttachment(batchRef);
	}
	
	public void callUserEJB(ActionEvent event) {
		userMigrateSB.load();

	}
	
	
	public void uploadAll(ActionEvent event) {
		userMigrateSB.load();
		quoteMigrateSB.load(batchRef, region);
		quoteMigrateSB.loadAttachment(batchRef);

	}
	public void callSystemCodeEJB(ActionEvent event) {
		systemCodeMigrateSB.load();

	}	
	

	public void callMfrDetailsEJB(ActionEvent event) {
		mfrDetailsMigrateSB.load();

	}	*/
	
	public void handleFileUpload(FileUploadEvent event){
		
		
	}
	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}
	
	public StreamedContent getFile2() throws IOException {
		 InputStream stream = file.getInputstream();  
		 StreamedContent  file2 = new DefaultStreamedContent(stream, "image/jpg", "downloaded_optimus.jpg");
		
		return file2;
	}	
	
	public void createProspectiveCustomer(){
	      javax.xml.ws.Holder<ZwqCustomer> eCustdtl = new javax.xml.ws.Holder<ZwqCustomer>();
	      javax.xml.ws.Holder<java.lang.String> eKunnr = new javax.xml.ws.Holder<java.lang.String>();
	      
	      try {
    	  
//			sapWebServiceSB.createProspectiveCustomer(
//					"PENANG",
//					"Y",
//					"MY",
//					"SHENZHEN WATT ELECTRONIC",
//					"", //name2
//					"", //Postal code
//					"", //Region 
//					"", //Search term 
//					"", //Division
//					"", //Street
//					"", //Sales office 
//					"HK11", //Sales Org
//					"",
//					"",
//					"USD", //currency
//					eCustdtl,
//					eKunnr);
		      if(eKunnr.value.equals("")){
		 		 ZwqCustomer exitingCustomer = eCustdtl.value;
		 		 LOG.info("customer code:" + exitingCustomer.getKunnr());
		 		 LOG.info("customer name:" + exitingCustomer.getName());
		 		 LOG.info("sales org:" + exitingCustomer.getVkorg());
		 		 LOG.info("account type:" + exitingCustomer.getKtokd());

		       }else{
		    	  LOG.info("new customer code" + eKunnr.value);
		     	
		       }			
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Exception in creatin Prospective Customer"+eCustdtl.value.getKunnr()+" , Exception  message : "+e.getMessage(), e);
		}	
	}

	    	  
	public void createQuote(){
		QuoteItem item = new QuoteItem();
		Quote quote = new Quote();
		item.setQuote(quote);
		
		BizUnit bizUnit = new BizUnit();
		bizUnit.setName("AEMC");
		quote.setBizUnit(bizUnit);
		
		List<QuoteItem> items = new ArrayList<QuoteItem>();
		
		items.add(item);
		item.setQuoteNumber("WR0001234");
		item.setSentOutTime(new Date());
		item.setPriceValidity("60");
		item.setShipmentValidity(new Date());
		
		item.setQuotedPrice(0.35123);
		item.setQuotedQty(100);
		//todo hu.andy
		/*Material material = new Material();
		material.setSapPartNumber("TISADS1232IPW");
		item.setQuotedMaterial(material);
		
		Manufacturer mfr = new Manufacturer();
		mfr.setName("TIS");
		
		material.setManufacturer(mfr);
		material.setFullMfrPartNumber("ADS1232IPW");
		*/
		Customer customer = new Customer();
		customer.setCustomerNumber("420");
		//todo hu.andy
		//item.setSoldToCustomer(customer);
		
		item.setMultiUsageFlag(true);
		
		try {
			sapWebServiceSB.createSAPQuote(items);
		} catch (AppException e) {
			LOG.log(Level.SEVERE, "Exception in creating quote in SAP for quote  : "+item.getQuoteNumber()+" , Exception message : "+e.getMessage(), e);
		}
		
	}

	public String getBatchRef() {
		return batchRef;
	}

	public void setBatchRef(String batchRef) {
		this.batchRef = batchRef;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}
	

	
}
