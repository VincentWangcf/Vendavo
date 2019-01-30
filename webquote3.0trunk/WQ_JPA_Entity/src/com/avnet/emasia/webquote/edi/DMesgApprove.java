package com.avnet.emasia.webquote.edi;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;
import java.util.Date;
/**
 * The persistent class for the TI_D_APPROVE database table.
 * 
 */
@Entity
@Table(name="TI_5D1_R2")
public class DMesgApprove implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="TI_D_APPROVE_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TI_D_APPROVE_ID_GENERATOR")
	@Column(unique=true, nullable=false)
	private long id;

	@Column(name="PO_NUM", length=35 , nullable=false)
	private String poNum;

	@Column(name="PO_ITEM_NUM", length=5 , nullable=false)
	private String poItemNum;

	@Column(name="QUOTE_NUM", length=20 , nullable=false)
	private String quoteNum;

	@Column(name="TI_QUOTE_NUM", length=20, nullable=false)
	private String tiQuoteNum;

	@Column(name="TI_QUOTE_ITEM_NUM", length=10, nullable=false)
	private String tiQuoteItemNum;

	@Column(name="TI_PART_NUM", length=50, nullable=false)
	private String tiPartNum;

	@Temporal(TemporalType.DATE)
	@Column(name="VALID_START_DATE")
	private Date startDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name="VALID_END_DATE")
	private Date endDate;
	 
	@Column(name="STATUS_DESC", length=250)
	private String statusDesc;
	
	@Column(name="TI_AUTH_NUM", length=20)
	private String tiAuthNum;
	
	@Column(name="AUTH_QTY")
	private Long authQty;

	@Column(name="OFFER_PRICE")
	private BigDecimal offerPricer;

	@Column(name="OFFER_PRICE_CURR", length=10)
	private String offerPricerCurr;
	
	@Column(name="TI_COMMENT", length=500)
	private String tiComment;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_DATE", nullable=false)
	private Date createdOn;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPoNum() {
		return poNum;
	}

	public void setPoNum(String poNum) {
		this.poNum = poNum;
	}

	public String getPoItemNum() {
		return poItemNum;
	}

	public void setPoItemNum(String poItemNum) {
		this.poItemNum = poItemNum;
	}

	public String getQuoteNum() {
		return quoteNum;
	}

	public void setQuoteNum(String quoteNum) {
		this.quoteNum = quoteNum;
	}

	public String getTiQuoteNum() {
		return tiQuoteNum;
	}

	public void setTiQuoteNum(String tiQuoteNum) {
		this.tiQuoteNum = tiQuoteNum;
	}

	public String getTiQuoteItemNum() {
		return tiQuoteItemNum;
	}

	public void setTiQuoteItemNum(String tiQuoteItemNum) {
		this.tiQuoteItemNum = tiQuoteItemNum;
	}

	public String getTiPartNum() {
		return tiPartNum;
	}

	public void setTiPartNum(String tiPartNum) {
		this.tiPartNum = tiPartNum;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	public String getTiAuthNum() {
		return tiAuthNum;
	}

	public void setTiAuthNum(String tiAuthNum) {
		this.tiAuthNum = tiAuthNum;
	}

	public Long getAuthQty() {
		return authQty;
	}

	public void setAuthQty(Long authQty) {
		this.authQty = authQty;
	}

	public BigDecimal getOfferPricer() {
		return offerPricer;
	}

	public void setOfferPricer(BigDecimal offerPricer) {
		this.offerPricer = offerPricer;
	}

	public String getOfferPricerCurr() {
		return offerPricerCurr;
	}

	public void setOfferPricerCurr(String offerPricerCurr) {
		this.offerPricerCurr = offerPricerCurr;
	}

	public String getTiComment() {
		return tiComment;
	}

	public void setTiComment(String tiComment) {
		this.tiComment = tiComment;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	@Override
	public String toString() {
		return "DMesgApprove [poNum=" + poNum + ", createdOn=" + createdOn + "]";
	}
	
}