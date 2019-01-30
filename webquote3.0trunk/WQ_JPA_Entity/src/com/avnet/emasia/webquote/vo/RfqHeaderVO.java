package com.avnet.emasia.webquote.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.avnet.emasia.webquote.entity.Attachment;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.Customer;

public class RfqHeaderVO implements Serializable {
	 
	private static final long serialVersionUID = 1L;
	private long id;
	private int version;
	//row 1
	private String bizUnit;
	private String salesEmployeeNumber;
	private String salesEmployeeName;
	private String team;
	//WebQuote 2.2 enhancement :  fields changes.
//	private boolean bmtChecked = false;
	private String enquirySegment;

	//row2
	private String soldToCustomerName;
	private String soldToCustomerNumber;
	private boolean newCustomer = false;
	private String chineseSoldToCustomerName;
	private String customerType;

	//row3
	private String shipToCustomerNumber;
	private String shipToCustomerName;
	private String endCustomerNumber;
	private String endCustomerName;

	//row4
	private String application;
	private String projectName;
	
	//row5
	private String designLocation;
	private String mpSchedule;
	private String ppSchedule;
	private String yourReference;
	private String copyToCs;
	private String designRegion;
	//Material restructure and quote_item delinkage. changed on 10 Apr 2014.
	private String copyToCsName;

	//row6
	private String remarks;

	//row7
	private boolean loa = false;

	//other
	private String formNumber;
	private List<Attachment> formAttachments;
	
	//added by June for Project RMB cur 20140704
	private String rfqCurr;
	//for Resubmit Invalid RFQ damon@20160801
	@Transient
	private String reSubmitType;
	@Transient
	private boolean isContinueSubmit; 
	@Transient
	private boolean hasAttach;
	@Transient
	private Date uploadTime;
	
	private boolean dLinkFlag;
	
	private String quoteType;
	private List<Customer> allowCustomers;
	@Transient
	private boolean isDisableRfqCurr; 
	
	public String getReSubmitType() {
		return reSubmitType;
	}

	public void setReSubmitType(String reSubmitType) {
		this.reSubmitType = reSubmitType;
	}
	
	public boolean isDisableRfqCurr() {
		return isDisableRfqCurr;
	}

	public void setDisableRfqCurr(boolean isDisableRfqCurr) {
		this.isDisableRfqCurr = isDisableRfqCurr;
	}

	private String designInCat;
	
	private BizUnit salesBizUnit;
	public String getSalesEmployeeNumber() {
		return salesEmployeeNumber;
	}

	public void setSalesEmployeeNumber(String salesEmployeeNumber) {
		this.salesEmployeeNumber = salesEmployeeNumber;
		if(StringUtils.isBlank(salesEmployeeNumber)){
			setSalesBizUnit(null);
		}
	}

	
	public boolean isdLinkFlag() {
		return dLinkFlag;
	}

	public void setdLinkFlag(boolean dLinkFlag) {
		this.dLinkFlag = dLinkFlag;
	}

	public String getSalesEmployeeName() {
		return salesEmployeeName;
	}

	public void setSalesEmployeeName(String salesEmployeeName) {
		this.salesEmployeeName = salesEmployeeName;
		if(StringUtils.isBlank(salesEmployeeName)){
			setSalesBizUnit(null);
		}
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}
	//WebQuote 2.2 enhancement :  fields changes.
//	public boolean isBmtChecked() {
//		return bmtChecked;
//	}
//
//	public void setBmtChecked(boolean bmtChecked) {
//		this.bmtChecked = bmtChecked;
//	}

	public String getEnquirySegment() {
		return enquirySegment;
	}

	public void setEnquirySegment(String enquirySegment) {
		this.enquirySegment = enquirySegment;
	}

	public String getSoldToCustomerName() {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(soldToCustomerName!=null)
    		soldToCustomerName=soldToCustomerName.trim();
    	
		return soldToCustomerName;
	}

	public void setSoldToCustomerName(String soldToCustomerName) {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(soldToCustomerName!=null)
    		soldToCustomerName=soldToCustomerName.trim();
    	
		this.soldToCustomerName = soldToCustomerName;
	}

	public String getSoldToCustomerNumber() {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(soldToCustomerNumber!=null)
    		soldToCustomerNumber=soldToCustomerNumber.trim();
    	
		return soldToCustomerNumber;
	}

	public void setSoldToCustomerNumber(String soldToCustomerNumber) {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(soldToCustomerNumber!=null)
    		soldToCustomerNumber=soldToCustomerNumber.trim();
    	
		this.soldToCustomerNumber = soldToCustomerNumber;
	}

	public boolean isNewCustomer() {
		return newCustomer;
	}

	public void setNewCustomer(boolean newCustomer) {
		this.newCustomer = newCustomer;
	}

	public String getChineseSoldToCustomerName() {
		return chineseSoldToCustomerName;
	}

	public void setChineseSoldToCustomerName(String chineseSoldToCustomerName) {
		this.chineseSoldToCustomerName = chineseSoldToCustomerName;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		//this.customerType = customerType;
		//fixed the customer type become empty issue .
    	if(!StringUtils.isEmpty(customerType))
    	{
    		customerType=customerType.trim();
    		this.customerType = customerType;
    	}
	}

	public String getShipToCustomerNumber() {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(shipToCustomerNumber!=null)
    		shipToCustomerNumber=shipToCustomerNumber.trim();
    	
		return shipToCustomerNumber;
	}

	public void setShipToCustomerNumber(String shipToCustomerNumber) {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(shipToCustomerNumber!=null)
    		shipToCustomerNumber=shipToCustomerNumber.trim();
    	
		this.shipToCustomerNumber = shipToCustomerNumber;
	}

	public String getShipToCustomerName() {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(shipToCustomerName!=null)
    		shipToCustomerName=shipToCustomerName.trim();
    	
		return shipToCustomerName;
	}

	public void setShipToCustomerName(String shipToCustomerName) {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(shipToCustomerName!=null)
    		shipToCustomerName=shipToCustomerName.trim();
    	
		this.shipToCustomerName = shipToCustomerName;
	}

	public String getEndCustomerNumber() {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(endCustomerNumber!=null)
    		endCustomerNumber=endCustomerNumber.trim();
    	
		return endCustomerNumber;
	}

	public void setEndCustomerNumber(String endCustomerNumber) {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(endCustomerNumber!=null)
    		endCustomerNumber=endCustomerNumber.trim();
    	
		this.endCustomerNumber = endCustomerNumber;
	}

	public String getEndCustomerName() {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(endCustomerName!=null)
    		endCustomerName=endCustomerName.trim();
    	
		return endCustomerName;
	}

	public void setEndCustomerName(String endCustomerName) {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(endCustomerName!=null)
    		endCustomerName=endCustomerName.trim();
    	
		this.endCustomerName = endCustomerName;
	}

	public String getApplication() {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(application!=null)
    		application=application.trim();
    	
		return application;
	}

	public void setApplication(String application) {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(application!=null)
    		application=application.trim();
    	
		this.application = application;
	}

	public String getProjectName() {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(projectName!=null)
    		projectName=projectName.trim();
    	
		return projectName;
	}

	public void setProjectName(String projectName) {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(projectName!=null)
    		projectName=projectName.trim();
    	
		this.projectName = projectName;
	}

	public String getDesignLocation() {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(designLocation!=null)
    		designLocation=designLocation.trim();
    	
		return designLocation;
	}

	public String getDesignRegion() {
		return designRegion;
	}

	public void setDesignRegion(String designRegion) {
		this.designRegion = designRegion;
	}

	public void setDesignLocation(String designLocation) {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(designLocation!=null)
    		designLocation=designLocation.trim();
    	
		this.designLocation = designLocation;
	}

	public String getMpSchedule() {
		return mpSchedule;
	}

	public void setMpSchedule(String mpSchedule) {
		this.mpSchedule = mpSchedule;
	}

	public String getPpSchedule() {
		return ppSchedule;
	}

	public void setPpSchedule(String ppSchedule) {
		this.ppSchedule = ppSchedule;
	}

	public String getYourReference() {
		if(yourReference!=null)
			yourReference=yourReference.trim();
		
		return yourReference;
	}

	public void setYourReference(String yourReference) {
		if(yourReference!=null)
			yourReference=yourReference.trim();

		this.yourReference = yourReference;
	}

	public String getCopyToCs() {
		return copyToCs;
	}

	public void setCopyToCs(String copyToCs) {
		this.copyToCs = copyToCs;
	}

	public String getRemarks() {
		if(remarks!=null)
			remarks=remarks.trim();
		
		return remarks;
	}

	public void setRemarks(String remarks) {
		if(remarks!=null)
			remarks=remarks.trim();
		
		this.remarks = remarks;
	}

	public boolean isLoa() {
		return loa;
	}

	public void setLoa(boolean loa) {
		this.loa = loa;
	}

	public String getFormNumber() {
		return formNumber;
	}

	public void setFormNumber(String formNumber) {
		this.formNumber = formNumber;
	}

	public String getBizUnit() {
		return bizUnit;
	}

	public void setBizUnit(String bizUnit) {
		this.bizUnit = bizUnit;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<Attachment> getFormAttachments() {
		return formAttachments;
	}

	public void setFormAttachments(List<Attachment> formAttachments) {
		this.formAttachments = formAttachments;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public boolean isContinueSubmit() {
		return isContinueSubmit;
	}

	@Transient
	public void setContinueSubmit(boolean isContinueSubmit) {
		this.isContinueSubmit = isContinueSubmit;
	}
	
	public boolean isHasAttach()
	{
		if(this.getFormAttachments()!=null && this.getFormAttachments().size()>0)
		{
			return true;
		}
		return false;
	}
	public void setHasAttach(boolean hasAttach)
	{
		this.hasAttach = hasAttach;
	}


	public Date getUploadTime()
	{
		return uploadTime;
	}
	
	@Transient
	public void setUploadTime(Date uploadTime)
	{
		this.uploadTime = uploadTime;
	}

	public String getCopyToCsName()
	{
		return copyToCsName;
	}

	public void setCopyToCsName(String copyToCsName)
	{
		this.copyToCsName = copyToCsName;
	}

	public String getQuoteType()
	{
		return quoteType;
	}

	public void setQuoteType(String quoteType)
	{
		if(!StringUtils.isEmpty(quoteType))
    	{
			quoteType=quoteType.trim();
    		this.quoteType = quoteType;
    	}
		//this.quoteType = quoteType;
	}

	public String getDesignInCat()
	{
		return designInCat;
	}

	public void setDesignInCat(String designInCat)
	{
		if(!StringUtils.isEmpty(designInCat))
    	{
			designInCat=designInCat.trim();
    		this.designInCat = designInCat;
    	}
		//this.designInCat = designInCat;
	}


	public String getRfqCurr() {
		return rfqCurr;
	}

	public void setRfqCurr(String exCurrency) {
		this.rfqCurr = exCurrency;
	}
	
	

	public BizUnit getSalesBizUnit()
	{
		return salesBizUnit;
	}

	public void setSalesBizUnit(BizUnit salesBizUnit)
	{
		this.salesBizUnit = salesBizUnit;
	}
	public boolean disableRegion(){
		return StringUtils.isNotEmpty(formNumber) && !formNumber.startsWith("DF");
	}

	public List<Customer> getAllowCustomers() {
		return allowCustomers;
	}

	public void setAllowCustomers(List<Customer> allowCustomers) {
		this.allowCustomers = allowCustomers;
	}	
	
}