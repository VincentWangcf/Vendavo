/**
 * 
 */
package com.avnet.emasia.webquote.quote.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 916138
 *
 */
public class ReportSearchCriteria implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -118693269950221119L;
	private Date quoteFrm;
	private Date quoteTo;
	private String salesMgr;
	private String salesTeam;
	private String salesUser;
	private String customer;
	private String mfr;
	private String productGrp1;
	private String productGrp2;
	private String productGrp3;
	private String productGrp4;
	private String quoteHandler;
	private String bizUnit;
	private String quotedPartNumber;
	private String customerNum;
	private String customerName;
	
	private boolean showQuotePeriod = false;
	private boolean showSalesMgr  = false;
	private boolean showSalesTeam  = false;
	private boolean showSalesUser  = false;
	private boolean showCustomer  = false;
	private boolean showmfr  = false;
	private boolean showProductGrp1  = false;
	private boolean showProductGrp2  = false;
	private boolean showProductGrp3  = false;
	private boolean showProductGrp4  = false;
	private boolean showQuoteHandler  = false;
	private boolean showBizUnit = false;
	private boolean showPN  = false;
	

	private String searchCustomerType="0"; // 0 is sold to code, 1 is sold to party
	private String searchResultType = "0"; // 0 is to count , 1 is amount 
	private String searchQuoteType = "Year"; //Year Qtr Month
	
	private String quoteTypeFormat ;
	
	public final BigDecimal RATE_MULTIPLIER = new BigDecimal("100");
	
	
	public Date getQuoteFrm() {
		return quoteFrm;
	}

	public void setQuoteFrm(Date quoteFrm) {
		this.quoteFrm = quoteFrm;
	}

	public Date getQuoteTo() {
		return quoteTo;
	}

	public void setQuoteTo(Date quoteTo) {
		this.quoteTo = quoteTo;
	}

	public String getSalesMgr() {
		return salesMgr;
	}

	public void setSalesMgr(String salesMgr) {
		this.salesMgr = salesMgr;
	}

	public String getSalesTeam() {
		return salesTeam;
	}

	public void setSalesTeam(String salesTeam) {
		this.salesTeam = salesTeam;
	}

	public String getSalesUser() {
		return salesUser;
	}

	public void setSalesUser(String salesUser) {
		this.salesUser = salesUser;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getMfr() {
		return mfr;
	}

	public void setMfr(String mfr) {
		this.mfr = mfr;
	}

	public String getProductGrp1() {
		return productGrp1;
	}

	public void setProductGrp1(String productGrp1) {
		this.productGrp1 = productGrp1;
	}

	public String getProductGrp2() {
		return productGrp2;
	}

	public void setProductGrp2(String productGrp2) {
		this.productGrp2 = productGrp2;
	}

	public String getProductGrp3() {
		return productGrp3;
	}

	public void setProductGrp3(String productGrp3) {
		this.productGrp3 = productGrp3;
	}

	public String getProductGrp4() {
		return productGrp4;
	}

	public void setProductGrp4(String productGrp4) {
		this.productGrp4 = productGrp4;
	}

	public String getQuoteHandler() {
		return quoteHandler;
	}

	public void setQuoteHandler(String quoteHandler) {
		this.quoteHandler = quoteHandler;
	}

	public String getBizUnit() {
		return bizUnit;
	}

	public void setBizUnit(String bizUnit) {
		this.bizUnit = bizUnit;
	}

	public String getQuotedPartNumber() {
		return quotedPartNumber;
	}

	public void setQuotedPartNumber(String quotedPartNumber) {
		this.quotedPartNumber = quotedPartNumber;
	}

	public String getCustomerNum() {
		return customerNum;
	}

	public void setCustomerNum(String customerNum) {
		this.customerNum = customerNum;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public boolean isShowSalesMgr() {
		return showSalesMgr;
	}

	public void setShowSalesMgr(boolean showSalesMgr) {
		this.showSalesMgr = showSalesMgr;
	}

	public boolean isShowSalesTeam() {
		return showSalesTeam;
	}

	public void setShowSalesTeam(boolean showSalesTeam) {
		this.showSalesTeam = showSalesTeam;
	}

	public boolean isShowSalesUser() {
		return showSalesUser;
	}

	public void setShowSalesUser(boolean showSalesUser) {
		this.showSalesUser = showSalesUser;
	}

	public boolean isShowCustomer() {
		return showCustomer;
	}

	public void setShowCustomer(boolean showCustomer) {
		this.showCustomer = showCustomer;
	}

	public boolean isShowmfr() {
		return showmfr;
	}

	public void setShowmfr(boolean showmfr) {
		this.showmfr = showmfr;
	}

	public boolean isShowProductGrp1() {
		return showProductGrp1;
	}

	public void setShowProductGrp1(boolean showProductGrp1) {
		this.showProductGrp1 = showProductGrp1;
	}

	public boolean isShowProductGrp2() {
		return showProductGrp2;
	}

	public void setShowProductGrp2(boolean showProductGrp2) {
		this.showProductGrp2 = showProductGrp2;
	}

	

	public boolean isShowProductGrp3() {
		return showProductGrp3;
	}

	public void setShowProductGrp3(boolean showProductGrp3) {
		this.showProductGrp3 = showProductGrp3;
	}

	public boolean isShowProductGrp4() {
		return showProductGrp4;
	}

	public void setShowProductGrp4(boolean showProductGrp4) {
		this.showProductGrp4 = showProductGrp4;
	}

	public boolean isShowQuoteHandler() {
		return showQuoteHandler;
	}

	public void setShowQuoteHandler(boolean showQuoteHandler) {
		this.showQuoteHandler = showQuoteHandler;
	}

	public boolean isShowBizUnit() {
		return showBizUnit;
	}

	public void setShowBizUnit(boolean showBizUnit) {
		this.showBizUnit = showBizUnit;
	}

	public boolean isShowPN() {
		return showPN;
	}

	public void setShowPN(boolean showPN) {
		this.showPN = showPN;
	}

	public String getSearchCustomerType() {
		return searchCustomerType;
	}

	public void setSearchCustomerType(String searchCustomerType) {
		this.searchCustomerType = searchCustomerType;
	}

	public String getSearchResultType() {
		return searchResultType;
	}

	public void setSearchResultType(String searchResultType) {
		this.searchResultType = searchResultType;
	}

	public String getSearchQuoteType() {
		return searchQuoteType;
	}

	public void setSearchQuoteType(String searchQuoteType) {
		this.searchQuoteType = searchQuoteType;
	}

	
	public boolean isShowQuotePeriod() {
		return showQuotePeriod;
	}

	public void setShowQuotePeriod(boolean showQuotePeriod) {
		this.showQuotePeriod = showQuotePeriod;
	}

	public String getQuoteTypeFormat() {
		return quoteTypeFormat;
	}

	public void setQuoteTypeFormat(String quoteTypeFormat) {
		this.quoteTypeFormat = quoteTypeFormat;
	}

	


	

	
	
	
	
	
}
