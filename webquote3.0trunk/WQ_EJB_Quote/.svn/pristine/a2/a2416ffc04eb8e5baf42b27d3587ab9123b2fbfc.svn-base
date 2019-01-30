package com.avnet.emasia.webquote.quote.vo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.avnet.emasia.webquote.rowdata.AStringConverter;
import com.avnet.emasia.webquote.rowdata.Column;
import com.avnet.emasia.webquote.rowdata.DefaultBean;
import com.avnet.emasia.webquote.rowdata.Transient;

public class RestrictedCustomerMappingVo extends DefaultBean{

	@Transient
	private int lineSeq;
	
	@Transient
	private String bizUnit;
	
	@AStringConverter
	@Column(value = "MFR")
	@NotNull(message = " MFR is empty  ")
	//@ExcelAlias(name="MFR",cellNum="1")
	private String mfr;
	
	@AStringConverter
	@Column(value = "Part Number")
	//@ExcelAlias(name="Part Number",cellNum="2")
	private String partNumber;
	
	@AStringConverter
	@Column(value = "Product Group 1")
	//@ExcelAlias(name="Product Group 1",cellNum="3")
	private String productGroup1;
	
	@AStringConverter
	@Column(value = "Product Group 2")
	//@ExcelAlias(name="Product Group 2",cellNum="4")
	private String productGroup2;
	
	@AStringConverter
	@Column(value = "Product Group 3")
	//@ExcelAlias(name="Product Group 3",cellNum="5")
	private String productGroup3;
	
	@AStringConverter
	@Column(value = "Product Group 4")
	//@ExcelAlias(name="Product Group 4",cellNum="6")
	private String productGroup4;
	
	@Transient
	private String costIndicator;
	
	@AStringConverter
	@Column(value = "Sold To Code")
	//@ExcelAlias(name="Sold To Code",cellNum="8")
	private String soldToCode;
	
	@AStringConverter
	@Column(value = "End Customer Code")
	private String endCustomerCode;
	
	public String getEndCustomerCode() {
		return endCustomerCode;
	}

	public void setEndCustomerCode(String endCustomerCode) {
		this.endCustomerCode = endCustomerCode;
	}

	//private String bizUnit;
	@Transient
	private boolean isErr;

	public String getMfr() {
		return mfr;
	}

	public void setMfr(String mfr) {
		this.mfr = mfr;
	}

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public String getProductGroup1() {
		return productGroup1;
	}

	public void setProductGroup1(String productGroup1) {
		this.productGroup1 = productGroup1;
	}

	public String getProductGroup2() {
		return productGroup2;
	}

	public void setProductGroup2(String productGroup2) {
		this.productGroup2 = productGroup2;
	}

	public String getProductGroup3() {
		return productGroup3;
	}

	public void setProductGroup3(String productGroup3) {
		this.productGroup3 = productGroup3;
	}

	public String getProductGroup4() {
		return productGroup4;
	}

	public void setProductGroup4(String productGroup4) {
		this.productGroup4 = productGroup4;
	}

	public String getCostIndicator() {
		return costIndicator;
	}

	public void setCostIndicator(String costIndicator) {
		this.costIndicator = costIndicator;
	}

	public String getSoldToCode() {
		return soldToCode;
	}

	public void setSoldToCode(String soldToCode) {
		this.soldToCode = soldToCode;
	}

	public String getBizUnit() {
		return bizUnit;
	}

	public void setBizUnit(String bizUnit) {
		this.bizUnit = bizUnit;
	}

	public boolean isErr() {
		return isErr;
	}

	public void setErr(boolean isErr) {
		this.isErr = isErr;
	}

	public int getLineSeq() {
		return lineSeq;
	}

	public void setLineSeq(int lineSeq) {
		this.lineSeq = lineSeq;
	}
	
}
