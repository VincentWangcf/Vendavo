package com.avnet.emasia.webquote.edi;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;
import java.util.Date;

/**
 * The persistent class for the MATERIAL_DETAIL database table.
 * 
 */
@Entity
@Table(name="TI_3A1_R2")
public class AMesgApprove implements Serializable {
	private static final long serialVersionUID = 1213L;

	@Id
	@SequenceGenerator(name="TI_A_APPROVE_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TI_A_APPROVE_ID_GENERATOR")
	@Column(unique=true, nullable=false)
	private long id;

	@Column(name="PO_NUM", length=35, nullable=false)
	private String poNum;

	@Column(name="PO_ITEM_NUM", length=5, nullable=false)
	private String poItemNum;

	@Column(name="QUOTE_NUM", length=20, nullable=false)
	private String quoteNum;

	@Column(name="TI_QUOTE_NUM", length=20, nullable=false)
	private String tiQuoteNum;

	@Column(name="TI_QUOTE_ITEM_NUM", length=10, nullable=false)
	private String tiQuoteItemNum;

	@Column(name="TI_PART_NUM", length=50, nullable=false)
	private String tiPartNum;

	@Temporal(TemporalType.DATE)
	@Column(name="START_DATE")
	private Date startDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name="END_DATE")
	private Date endDate;
	
	@Column(name="STATUS", length=20, nullable=false)
	private String status;

	@Column(name="STATUS_DESC", length=250)
	private String statusDesc;
	
	@Column(name="QTY")
	private Long qty;
	
	@Column(name="ZCPC")
	private BigDecimal zcpc;

	@Column(name="ZCPC_CURR", length=10)
	private String zcpcCurr;
	
	@Column(name="OFFER_PRICE")
	private BigDecimal offerPrice;
	
	@Column(name="OFFER_PRICE_CURR", length=10)
	private String offerPriceCurr;
	
	@Column(name="RESALES_PRICE")
	private BigDecimal resalesPrice;
	
	@Column(name="RESALES_PRICE_CURR", length=10)
	private String resalesPriceCurr;
	
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	public long getQty() {
		return qty;
	}

	public void setQty(long qty) {
		this.qty = qty;
	}

	public BigDecimal getZcpc() {
		return zcpc;
	}

	public void setZcpc(BigDecimal zcpc) {
		this.zcpc = zcpc;
	}

	public String getZcpcCurr() {
		return zcpcCurr;
	}

	public void setZcpcCurr(String zcpcCurr) {
		this.zcpcCurr = zcpcCurr;
	}

	public BigDecimal getOfferPrice() {
		return offerPrice;
	}

	public void setOfferPrice(BigDecimal offerPrice) {
		this.offerPrice = offerPrice;
	}

	public String getOfferPriceCurr() {
		return offerPriceCurr;
	}

	public void setOfferPriceCurr(String offerPriceCurr) {
		this.offerPriceCurr = offerPriceCurr;
	}

	public BigDecimal getResalesPrice() {
		return resalesPrice;
	}

	public void setResalesPrice(BigDecimal resalesPrice) {
		this.resalesPrice = resalesPrice;
	}

	public String getResalesPriceCurr() {
		return resalesPriceCurr;
	}

	public void setResalesPriceCurr(String resalesPriceCurr) {
		this.resalesPriceCurr = resalesPriceCurr;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	@Override
	public String toString() {
		return "AMesgApprove [poNum=" + poNum + ", status=" + status + ", createdOn=" + createdOn + "]";
	}

}