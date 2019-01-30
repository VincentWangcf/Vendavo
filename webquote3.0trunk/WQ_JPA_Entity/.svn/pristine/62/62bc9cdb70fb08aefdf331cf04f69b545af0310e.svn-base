package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.logging.Logger;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.Customizer;

import com.avnet.emasia.webquote.entity.util.StringUtils;


/**
 * The persistent class for the QUOTE_ITEM_DESIGN database table.
 * 
 */
@Entity
@Table(name="QUOTE_ITEM_DESIGN")
@Customizer(com.avnet.emasia.webquote.entity.QuoteItemDesignCustomizer.class)
public class QuoteItemDesign implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="QuoteItemDesign_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="QuoteItemDesign_ID_GENERATOR")
	private Long id;

	@Embedded
	private BmtFlag bmtFlag;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_TIME")
	private Date createdTime;

	@Column(name="DR_ALT_CURRENCY")
	private String drAltCurrency;

	@Column(name="DR_COMMENTS")
	private String drComments;

	@Column(name="DR_COST")
	private BigDecimal drCost;

	@Column(name="DR_COST_ALT_CURRENCY")
	private BigDecimal drCostAltCurrency;

	@Temporal(TemporalType.DATE)
	@Column(name="DR_EFFECTIVE_DATE")
	private Date drEffectiveDate;

	@Column(name="DR_EXCHANGE_RATE")
	private BigDecimal drExchangeRate;

	@Temporal(TemporalType.DATE)
	@Column(name="DR_EXPIRY_DATE")
	private Date drExpiryDate;

	@Column(name="DR_MFR_NO")
	private String drMfrNo;

	@Column(name="DR_MFR_QUOTE_NO")
	private String drMfrQuoteNo;

	@Column(name="DR_NO")
	private String drNo;

	@Column(name="DR_QUOTE_QTY")
	private Integer drQuoteQty;

	@Column(name="DR_RESALE")
	private BigDecimal drResale;

	@Column(name="DR_RESALE_ALT_CURRENCY")
	private BigDecimal drResaleAltCurrency;

	@Column(name="LAST_UPDATED_BY")
	private String lastUpdatedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_UPDATED_TIME")
	private Date lastUpdatedTime;

	@OneToOne(cascade=CascadeType.MERGE)
	@JoinColumn(name="QUOTE_ITEM_ID")
	private QuoteItem quoteItem;

	@Version
	@Column(name="\"VERSION\"")
	private Integer version;

	public QuoteItemDesign() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BmtFlag getBmtFlag() {
		return this.bmtFlag;
	}

	public void setBmtFlag(BmtFlag bmtFlag) {
		this.bmtFlag = bmtFlag;
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedTime() {
		return this.createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public String getDrAltCurrency() {
		return this.drAltCurrency;
	}

	public void setDrAltCurrency(String drAltCurrency) {
		this.drAltCurrency = drAltCurrency;
	}

	public String getDrComments() {
		return this.drComments;
	}

	public void setDrComments(String drComments) {
		this.drComments = drComments;
	}

	public BigDecimal getDrCost() {
		return this.drCost;
	}

	public void setDrCost(BigDecimal drCost) {
		this.drCost = drCost;
	}

	public BigDecimal getDrCostAltCurrency() {
		return this.drCostAltCurrency;
	}

	public void setDrCostAltCurrency(BigDecimal drCostAltCurrency) {
		this.drCostAltCurrency = drCostAltCurrency;
	}

	public Date getDrEffectiveDate() {
		return this.drEffectiveDate;
	}

	public void setDrEffectiveDate(Date drEffectiveDate) {
		this.drEffectiveDate = drEffectiveDate;
	}

	public BigDecimal getDrExchangeRate() {
		return this.drExchangeRate;
	}

	public void setDrExchangeRate(BigDecimal drExchangeRate) {
		this.drExchangeRate = drExchangeRate;
	}

	public Date getDrExpiryDate() {
		return this.drExpiryDate;
	}

	public void setDrExpiryDate(Date drExpiryDate) {
		this.drExpiryDate = drExpiryDate;
	}

	public String getDrMfrNo() {
		return this.drMfrNo;
	}

	public void setDrMfrNo(String drMfrNo) {
		this.drMfrNo = drMfrNo;
	}

	public String getDrMfrQuoteNo() {
		return this.drMfrQuoteNo;
	}

	public void setDrMfrQuoteNo(String drMfrQuoteNo) {
		this.drMfrQuoteNo = drMfrQuoteNo;
	}

	public String getDrNo() {
		return this.drNo;
	}

	public void setDrNo(String drNo) {
		this.drNo = drNo;
	}

	public Integer getDrQuoteQty() {
		return this.drQuoteQty;
	}

	public void setDrQuoteQty(Integer drQuoteQty) {
		this.drQuoteQty = drQuoteQty;
	}

	public BigDecimal getDrResale() {
		return this.drResale;
	}

	public void setDrResale(BigDecimal drResale) {
		this.drResale = drResale;
	}

	public BigDecimal getDrResaleAltCurrency() {
		return this.drResaleAltCurrency;
	}

	public void setDrResaleAltCurrency(BigDecimal drResaleAltCurrency) {
		this.drResaleAltCurrency = drResaleAltCurrency;
	}

	public String getLastUpdatedBy() {
		return this.lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public Date getLastUpdatedTime() {
		return this.lastUpdatedTime;
	}

	public void setLastUpdatedTime(Date lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}

	public QuoteItem getQuoteItem() {
		return this.quoteItem;
	}

	public void setQuoteItem(QuoteItem quoteItem) {
		this.quoteItem = quoteItem;
	}

	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getDrMargin(){
		try{
			if(drCost != null && drResale != null && drCost.doubleValue() != 0 && drResale.doubleValue() != 0){
				return StringUtils.formatNumber(((drResale.doubleValue()- drCost.doubleValue()) /drResale.doubleValue()));
			}
		}catch(Exception e){
			Logger.getLogger(this.getClass().getSimpleName()).warning("Exception in formating number"+e.getMessage());
			return "0%";
		}
		return "0%";
	}
	
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if(obj == null){
            return false;
        }
        if(obj instanceof QuoteItemDesign){
           final QuoteItemDesign o = (QuoteItemDesign)obj;
           return o.getId().equals(getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getId().hashCode() * 37;
    }
 
        

    @Override
    public String toString() {
        return "QuoteItemDesign{" + "id=" + id + ", bmtFlag=" + bmtFlag.getBmtFlagCode() + ", drAltCurrency=" + drAltCurrency + ", drComments=" + drComments + ", drCost=" + drCost + ", drCostAltCurrency=" + drCostAltCurrency + ", drMfrNo=" + drMfrNo + ", drMfrQuoteNo=" + drMfrQuoteNo + ", drNo=" + drNo + ", drQuoteQty=" + drQuoteQty + ", drResaleAltCurrency=" + drResaleAltCurrency + ", version=" + version + '}';
    }
 

}