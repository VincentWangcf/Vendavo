package com.avnet.emasia.webquote.quote.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.avnet.emasia.webquote.rowdata.ABigDecimalConverter;
import com.avnet.emasia.webquote.rowdata.AIntConverter;
import com.avnet.emasia.webquote.rowdata.AStringConverter;
import com.avnet.emasia.webquote.rowdata.Column;
import com.avnet.emasia.webquote.rowdata.DefaultBean;
import com.avnet.emasia.webquote.rowdata.Transient;
import javax.validation.constraints.NotNull;

public class ExchangeRateVo extends DefaultBean {

	@Transient
	private String exchangeRateFrom;
	
	@ABigDecimalConverter
	@Column(value = "Ex Rate To")
	private BigDecimal exchangeRateTo;
	
	@ABigDecimalConverter
	@Column(value = "VAT")
	private BigDecimal vat;
	
	@ABigDecimalConverter
	@Column(value = "Handling")
	private BigDecimal handling;
	
	@AStringConverter
	@Column(value = "Exchange Rate Type")
	private String exchangeRateType;
	// add vfor mul

	@Transient
	private String bizUnit;
	
	@AStringConverter
	@Column(value = "Rounding")
	private String rounding;
	
	@AIntConverter
	@Column(value = "Decimal Point")
	private Integer decimalPoint;
	
	@AStringConverter
	@Column(value = "Rate Remark")
	private String rateRemark;
	
	@AStringConverter
	@Column(value = "Currency From")
	private String currencyFrom;
	@AStringConverter
	
	@NotNull(message = " Currency To is empty  ")
	@Column(value = "Currency To")
	private String currencyTo;
	
	@AStringConverter
	@Column(value = "Sold To Code")
	private String soldToCode;
	
	@Transient
	private boolean error;

	public String getCurrencyFrom() {
		return currencyFrom;
	}

	public void setCurrencyFrom(String currencyFrom) {
		this.currencyFrom = currencyFrom;
	}

	public String getCurrencyTo() {
		return currencyTo;
	}

	public void setCurrencyTo(String currencyTo) {
		this.currencyTo = currencyTo;
	}

	public String getSoldToCode() {
		return soldToCode;
	}

	public void setSoldToCode(String soldToCode) {
		this.soldToCode = soldToCode;
	}

	public String getExchangeRateType() {
		return exchangeRateType;
	}

	public void setExchangeRateType(String exchangeRateType) {
		this.exchangeRateType = exchangeRateType;
	}

	

	public String getRounding() {
		return rounding;
	}

	public void setRounding(String rounding) {
		this.rounding = rounding;
	}

	public Integer getDecimalPoint() {
		return decimalPoint;
	}

	public void setDecimalPoint(Integer decimalPoint) {
		this.decimalPoint = decimalPoint;
	}

	public String getRateRemark() {
		return rateRemark;
	}

	public void setRateRemark(String rateRemark) {
		this.rateRemark = rateRemark;
	}

	public String getBizUnit() {
		return bizUnit;
	}

	public void setBizUnit(String bizUnit) {
		this.bizUnit = bizUnit;
	}

	public ExchangeRateVo() {
	}

	public String getKey() {
		// Key:Region + From_Currency + To_Currency + Sold_To_Code
		// String key = this.getCurrencyFrom() + this.getCurrencyTo() + "|";
		//
		// String soldToCode = this.getSoldToCode();
		// if(soldToCode != null && ! soldToCode.equals("")){
		// key = key + soldToCode;
		// }
		//
		// return key;
		String key = this.getBizUnit() + this.getCurrencyFrom() + this.getCurrencyTo() + this.getSoldToCode();
		return key;
	}

	public String getExchangeRateFrom() {
		return exchangeRateFrom;
	}

	public void setExchangeRateFrom(String exchangeRateFrom) {
		this.exchangeRateFrom = exchangeRateFrom;
	}

	public BigDecimal getExchangeRateTo() {
		return exchangeRateTo;
	}

	public void setExchangeRateTo(BigDecimal exchangeRateTo) {
		this.exchangeRateTo = exchangeRateTo;
	}

	public BigDecimal getVat() {
		return vat;
	}

	public void setVat(BigDecimal vat) {
		this.vat = vat;
	}

	public BigDecimal getHandling() {
		return handling;
	}

	public void setHandling(BigDecimal handling) {
		this.handling = handling;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

}
