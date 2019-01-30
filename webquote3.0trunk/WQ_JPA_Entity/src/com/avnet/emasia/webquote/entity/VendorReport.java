package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import java.text.ParseException;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.*;




/**
 * The persistent class for the VENDOR_REPORT database table.
 * 
 */
@Entity
@Table(name="VENDOR_REPORT")
public class VendorReport implements Serializable {
	static final Logger LOG= Logger.getLogger(VendorReport.class.getSimpleName());
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="VENDOR_REPORT_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="VENDOR_REPORT_ID_GENERATOR")
	@Column(unique=true, nullable=false, precision=15)
	private long id;

	@Column(name="MFR", length=10)
	private String mfr;
	
	@Column(name="SQ_NUMBER", length=20)
	private String sqNumber;
	
	@Column(name="DEBIT_NUMBER", length=20)
	private String debitNumber;
	
	@Column(name="STATUS", length=20)
	private String status;
	
	@Column(name="FULL_MFR_PART_NUMBER", length=40)
	private String fullMfrPartNumber;
	
	@Column(name="PRODUCT_CATEGORY", length=20)
	private String productCategory;
	
	@Column(name="CUSTOMER", length=60)
	private String customer;
	
	@Column(name="END_CUSTOMER", length=60)
	private String endCustomer;
	
	@Column(name="CURRENCY", length=10)
	private String currency;
	
	@Column(name="COST", length=10)
	private String cost;
	
	@Column(name="RESALE", length=10)
	private String resale;
	
	@Column(name="DEBIT_CREATE_dATE", length=20)
	private String debitCreateDate;
	
	@Column(name="DEBIT_EXPIRE_DATE", length=20)
	private String debitExpireDate;
	
	@Column(name="QUOTE_CREATE_DATE", length=20)
	private String quoteCreateDate;

	@Column(name="QUOTE_EXPIRE_DATE", length=20)
	private String quoteExpireDate;
	
	@Column(name="SOLD_TO_CUSTOMER_NUMBER", length=12)
	private String soldToCustomerNumber;
	
	@Column(name="SHIP_TO_CUSTOMER_NUMBER", length=12)
	private String shipToCustomerNumber;
	
	@Column(name="AUTH_QTY", length=10)
	private String authQty;
	
	@Column(name="CONSUMED_QTY", length=10)
	private String consumedQty;
	
	@Column(name="OPEN_QTY", length=10)
	private String openQty;
	
	@Column(name="CUSTOMER_CITY", length=20)
	private String customerCity;
	
	@Column(name="CUSTOMER_COUNTRY", length=20)
	private String customerCountry;
	
	@Column(name="END_CUSTOMER_CITY", length=20)
	private String endCustomerCity;
	
	@Column(name="END_CUSTOMER_COUNTRY", length=20)
	private String endCustomerCountry;

	@Column(name="SQ_REMARK1", length=100)
	private String sqRemark1;

	@Column(name="SQ_REMARK2", length=100)
	private String sqRemark2;

	@Column(name="SQ_REMARK3", length=100)
	private String sqRemark3;

	@Column(name="SQ_REMARK4", length=100)
	private String sqRemark4;

	@ManyToOne
	@JoinColumn(name="BIZ_UNIT", nullable=false)
	private BizUnit bizUnit;
	
	public VendorReport() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMfr() {
		return mfr;
	}

	public void setMfr(String mfr) {
		this.mfr = mfr;
	}

	public String getSqNumber() {
		return sqNumber;
	}

	public void setSqNumber(String sqNumber) {
		this.sqNumber = sqNumber;
	}

	public String getDebitNumber() {
		return debitNumber;
	}

	public void setDebitNumber(String debitNumber) {
		this.debitNumber = debitNumber;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFullMfrPartNumber() {
		return fullMfrPartNumber;
	}

	public void setFullMfrPartNumber(String fullMfrPartNumber) {
		this.fullMfrPartNumber = fullMfrPartNumber;
	}

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getEndCustomer() {
		return endCustomer;
	}

	public void setEndCustomer(String endCustomer) {
		this.endCustomer = endCustomer;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getResale() {
		return resale;
	}

	public void setResale(String resale) {
		this.resale = resale;
	}

	public String getDebitCreateDate() {
		return convertDateFormat(debitCreateDate);
	}

	public void setDebitCreateDate(String debitCreateDate) {
		this.debitCreateDate = debitCreateDate;
	}

	public String getDebitExpireDate() {
		return convertDateFormat(debitExpireDate);
	}

	public void setDebitExpireDate(String debitExpireDate) {
		this.debitExpireDate = debitExpireDate;
	}

	public String getQuoteCreateDate() {
		return convertDateFormat(quoteCreateDate);
	}

	public void setQuoteCreateDate(String quoteCreateDate) {
		this.quoteCreateDate = quoteCreateDate;
	}

	public String getSoldToCustomerNumber() {
		return soldToCustomerNumber;
	}

	public void setSoldToCustomerNumber(String soldToCustomerNumber) {
		this.soldToCustomerNumber = soldToCustomerNumber;
	}

	public String getShipToCustomerNumber() {
		return shipToCustomerNumber;
	}

	public void setShipToCustomerNumber(String shipToCustomerNumber) {
		this.shipToCustomerNumber = shipToCustomerNumber;
	}

	public String getAuthQty() {
		return authQty;
	}

	public void setAuthQty(String authQty) {
		this.authQty = authQty;
	}

	public String getConsumedQty() {
		return consumedQty;
	}

	public void setConsumedQty(String consumedQty) {
		this.consumedQty = consumedQty;
	}

	public String getOpenQty() {
		return openQty;
	}

	public void setOpenQty(String openQty) {
		this.openQty = openQty;
	}

	public String getCustomerCity() {
		return customerCity;
	}

	public void setCustomerCity(String customerCity) {
		this.customerCity = customerCity;
	}

	public String getCustomerCountry() {
		return customerCountry;
	}

	public void setCustomerCountry(String customerCountry) {
		this.customerCountry = customerCountry;
	}

	public String getEndCustomerCity() {
		return endCustomerCity;
	}

	public void setEndCustomerCity(String endCustomerCity) {
		this.endCustomerCity = endCustomerCity;
	}

	public String getEndCustomerCountry() {
		return endCustomerCountry;
	}

	public void setEndCustomerCountry(String endCustomerCountry) {
		this.endCustomerCountry = endCustomerCountry;
	}

	public String getQuoteExpireDate() {
		return convertDateFormat(quoteExpireDate);
	}

	public void setQuoteExpireDate(String quoteExpireDate) {
		this.quoteExpireDate = quoteExpireDate;
	}

	public String getSqRemark1() {
		return sqRemark1;
	}

	public void setSqRemark1(String sqRemark1) {
		this.sqRemark1 = sqRemark1;
	}

	public String getSqRemark2() {
		return sqRemark2;
	}

	public void setSqRemark2(String sqRemark2) {
		this.sqRemark2 = sqRemark2;
	}

	public String getSqRemark3() {
		return sqRemark3;
	}

	public void setSqRemark3(String sqRemark3) {
		this.sqRemark3 = sqRemark3;
	}

	public String getSqRemark4() {
		return sqRemark4;
	}

	public void setSqRemark4(String sqRemark4) {
		this.sqRemark4 = sqRemark4;
	}
	
	public String convertDateFormat(String date)
	{
		String returnStr = "";
		SimpleDateFormat sdfV = new SimpleDateFormat("MM/dd/yy");
		SimpleDateFormat sdfP = new SimpleDateFormat("dd/MM/yyyy");
		if(date != null)
		{
			try 
			{
				returnStr =  sdfP.format(sdfV.parse(date));
			} 
			catch (ParseException e) 
			{
				  LOG.log(Level.WARNING, "Exception in formating date"+date+" Exception message  : "+e.getMessage());
				return date;
			}
		}
		return returnStr;
	}

	public BizUnit getBizUnit() {
		return bizUnit;
	}

	public void setBizUnit(BizUnit bizUnit) {
		this.bizUnit = bizUnit;
	}
	
	public Integer getAuthQtyInt(){
		if(this.authQty != null){
			return Integer.parseInt(this.authQty);
		}
		return null;
	}

	public Integer getConsumedQtyInt(){
		if(this.consumedQty != null){
			return Integer.parseInt(this.consumedQty);
		}
		return null;
	}
	
	public Integer getOpenQtyInt(){
		if(this.openQty != null){
			return Integer.parseInt(this.openQty);
		}
		return null;
	}
}