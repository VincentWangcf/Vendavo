package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.annotations.Customizer;

import com.avnet.emasia.webquote.constants.Constants;
import com.avnet.emasia.webquote.entity.util.ExchangeRateListener;


/**
 * The persistent class for the EXCHANGE_RATE database table.
 * 
 */
@Entity
@Table(name="EXCHANGE_RATE")
@EntityListeners(ExchangeRateListener.class)
@Customizer(com.avnet.emasia.webquote.entity.ExchangeRateCustomizer.class)
public class ExchangeRate implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	static final Logger logger = Logger.getLogger(ExchangeRate.class.getName());
	@Id
	@SequenceGenerator(name="EXCHANGE_RATE_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1 )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="EXCHANGE_RATE_ID_GENERATOR")
	@Column(unique=true, nullable=false)
	private long id;

	@Column(name="BIZ_UNIT", nullable=false, length=10)
	private String bizUnit;

	@Column(name="CURR_FROM", nullable=false, length=20)
	private String currFrom;

	@Column(name="CURR_TO", nullable=false, length=20)
	private String currTo;

	/*@Column(name="EX_RATE_FROM")
	private BigDecimal exRateFrom;*/

	@Column(name="EX_RATE_TO")
	private BigDecimal exRateTo;

	@Column(name="HANDLING")
	private BigDecimal handling;

	@Column(name="SOLD_TO_CODE", length=20)
	private String soldToCode;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="VALID_FROM")
	private Date validFrom;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="VALID_TO")
	private Date validTo;

	@Column(name="VAT")
	private BigDecimal vat;
	
	@Column(name="LAST_UPDATED_BY")
	private String lastUpdatedBy;
	
//	@Column(name="DELETE_FLAG")
//	private Boolean deleteFlag;
	
	@Column(name="SCALE")
	private Integer scale;
	
	@Column(name="ROUND_TYPE")
	private Integer roundType;
	
	@Column(name="REMARKS")
	private String remarks;
	
	@Column(name="Exchange_Rate_Type",  nullable = false)
	@Enumerated(EnumType.STRING)
	private ExchangeRateType exchangeRateType;
	
	
	

	public ExchangeRate() {
	}
	
	public String getKey(){
//		String key = this.getBizUnit() + "|" + this.getCurrFrom() + "|" + this.getCurrTo() + "|";
//		
//		String soldToCode = this.getSoldToCode();
//		if(soldToCode != null &&  ! soldToCode.equals("")){
//			key = key + soldToCode;
//		}
		String  key=  this.getExchangeRateType().name() + this.getBizUnit()+this.getCurrFrom()+this.getCurrTo()+this.getSoldToCode();
		return key;
	}	
	
	@Override
	public String toString() {
		return "ExchangeRate [id=" + id + ", bizUnit=" + bizUnit
				+ ", currFrom=" + currFrom + ", currTo=" + currTo
				+ ", exRateTo=" + exRateTo
				+ ", handling=" + handling + ",  soldToCode="
				+ soldToCode + ", validFrom=" + validFrom + ", vat=" + vat
				+ ",scale="+scale +",roundType" +roundType +", remarks" +remarks
				+ "]";
	}
	
	

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getBizUnit() {
		return this.bizUnit;
	}

	public void setBizUnit(String bizUnit) {
		this.bizUnit = bizUnit;
	}

	public String getCurrFrom() {
		return this.currFrom;
	}

	public void setCurrFrom(String currFrom) {
		this.currFrom = currFrom;
	}

	public String getCurrTo() {
		return this.currTo;
	}

	public void setCurrTo(String currTo) {
		this.currTo = currTo;
	}

	/*public BigDecimal getExRateFrom() {
		return this.exRateFrom;
	}

	public void setExRateFrom(BigDecimal exRateFrom) {
		this.exRateFrom = exRateFrom;
	}*/

	public BigDecimal getExRateTo() {
		return this.exRateTo;
	}

	public void setExRateTo(BigDecimal exRateTo) {
		this.exRateTo = exRateTo;
	}

	public BigDecimal getHandling() {
		return this.handling;
	}

	public void setHandling(BigDecimal handling) {
		this.handling = handling;
	}

	public String getSoldToCode() {
		return this.soldToCode;
	}

	public void setSoldToCode(String soldToCode) {
		this.soldToCode = soldToCode;
	}

	public Date getValidFrom() {
		return this.validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}
	
	public Date getValidTo() {
		return validTo;
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}

	public BigDecimal getVat() {
		return this.vat;
	}

	public void setVat(BigDecimal vat) {
		this.vat = vat;
	}

	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

//	public Boolean getDeleteFlag() {
//		return deleteFlag;
//	}
//
//	public void setDeleteFlag(Boolean deleteFlag) {
//		this.deleteFlag = deleteFlag;
//	}

	public Integer getScale() {
		return scale;
	}

	public void setScale(Integer scale) {
		this.scale = scale;
	}

	public Integer getRoundType() {
		return roundType;
	}

	public void setRoundType(Integer roundType) {
		this.roundType = roundType;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public BigDecimal getRateInTotal() {
		return getExRateTo().multiply(getVat()).multiply(getHandling());
	}
	public ExchangeRateType getExchangeRateType() {
		return exchangeRateType;
	}

	public void setExchangeRateType(ExchangeRateType exchangeRateType) {
		this.exchangeRateType = exchangeRateType;
	}
	

	@SuppressWarnings("unchecked")  
	public static void sortByValidFrom(List list){  
	    Collections.sort(list, new Comparator(){  
	        @Override  
	        public int compare(Object o1, Object o2) {  
	        	ExchangeRate ex1=(ExchangeRate)o1;  
	        	ExchangeRate ex2=(ExchangeRate)o2;  
	            return ex2.getValidFrom().compareTo(ex1.getValidFrom());  
	        }             
	    });  
//	    System.out.println("/////////////排序之后///////////////");  
//	    for(int i=0;i<list.size();i++){  
//	    	ExchangeRate st=(ExchangeRate)list.get(i);  
//	        System.out.println("st.getValidTo="+st.getValidTo());  
//	    }  
	}  
	
	
	
	
	
	@SuppressWarnings("unchecked")  
	public static void sortByExchangeRateType(List list){  
	    Collections.sort(list, new Comparator(){  
	        @Override  
	        public int compare(Object o1, Object o2) {  
	        	ExchangeRate ex1=(ExchangeRate)o1;  
	        	ExchangeRate ex2=(ExchangeRate)o2;  
	            return ex2.getExchangeRateType().name().compareTo(ex1.getExchangeRateType().name());  
	        }             
	    });  
//	    System.out.println("/////////////排序之后///////////////");  
//	    for(int i=0;i<list.size();i++){  
//	    	ExchangeRate st=(ExchangeRate)list.get(i);  
//	        System.out.println("st.getExchangeRateType="+st.getExchangeRateType().name());  
//	    }  
	}  
	
	private static class InnerLazy {
		private static ExchangeRate exchangeRateOne = new ExchangeRate();
		static{
			exchangeRateOne.setCurrFrom(Currency.USD.toString());
			exchangeRateOne.setCurrTo(Currency.USD.toString());
			exchangeRateOne.setExRateTo(BigDecimal.ONE);
			exchangeRateOne.setHandling(BigDecimal.ONE);
			exchangeRateOne.setVat(BigDecimal.ONE);
			exchangeRateOne.setScale(Constants.SCALE);
			exchangeRateOne.setRoundType(Constants.ROUNDING_MODE);
		}
		static ExchangeRate getSingleExchangeRateOne() {
			return exchangeRateOne;
		}
	}
	
	static ExchangeRate getExchangeRateOne() { 
		return InnerLazy.getSingleExchangeRateOne();
	}
	
	
	public ExchangeRate clone() {
		try {
			return (ExchangeRate) super.clone();
		} catch(Exception e){
			logger.log(Level.SEVERE, "ExchangeRate clone() failed.", e);
		}
		return null;
	}
	
	
}