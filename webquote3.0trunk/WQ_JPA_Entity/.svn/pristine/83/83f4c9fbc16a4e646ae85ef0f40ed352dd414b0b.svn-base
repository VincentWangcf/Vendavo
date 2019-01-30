package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.avnet.emasia.webquote.dp.xml.requestquote.WebQuoteLineRequest;


/**
 * The persistent class for the DP_RFQ_ITEM database table.
 * 
 */
@Entity
@Table(name="DP_RFQ_ITEM")
public class DpRfqItem implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOG = Logger.getLogger(DpRfqItem.class.getName());

	@Id
	@SequenceGenerator(name="DPRFQ_ITEM_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DPRFQ_ITEM_ID_GENERATOR")
	private long id;

	@Column(name="CURRENCY")
	private String currency;

	@Column(name="EXPIRY_DATE")
	private Timestamp expiryDate;

	@Column(name="INTERNAL_ERROR")
	private String internalError;

	@Column(name="LINE_ITEM_NUMBER")
	private Integer lineItemNumber;

	@Column(name="PROC_STATUS")
	private String procStatus;

	@Column(name="QUANTITY")
	private Long quantity;

	@OneToOne
	@JoinColumn(name="QUOTE_ITEM_ID")
	private QuoteItem quoteItem;

	@Column(name="QUOTE_LINE_STATUS")
	private String quoteLineStatus;

	@Column(name="QUOTED_QTY")
	private Long quotedQty;

	@Column(name="QUOTED_RESALE")
	private BigDecimal quotedResale;

	@Column(name="REFERENCE_LINE_ID")
	private String referenceLineId;

	@Column(name="REJECTION_REASON")
	private String rejectionReason;

	@Column(name="REMARKS")
	private String remarks;

	@Column(name="REQUESTED_MANUFACTURER")
	private String requestedManufacturer;

	@Column(name="REQUESTED_PART")
	private String requestedPart;

	@Column(name="TARGET_RESALE")
	private BigDecimal targetResale;
	
	@Version
	@Column(name="\"VERSION\"")
	private Integer version;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="REPROCESS_TIME")
	private Date reprocessTime;
	
	
	@Column(name="REPROCESS_COUNT")
	private Integer reprocessCount = 0;

	//bi-directional many-to-one association to DpRfq
	@ManyToOne
	@JoinColumn(name="DP_RFQ_ID")
	private DpRfq dpRfq;
	
	@Transient
	private int seq;

	public DpRfqItem() {
	}

	
	public static DpRfqItem createInstance(WebQuoteLineRequest request){
		
		if(request == null){
			return null;
		}
		
		DpRfqItem rfqItem = new DpRfqItem();
		
		if(request.getQuantity1() != null){
			try{
				rfqItem.setQuantity(Long.valueOf(request.getQuantity1()));	
			}catch(Exception e){
				LOG.log(Level.WARNING, e.getMessage() + " Failed to convert quantity1 " + request.getQuantity1() + 
						" to Long. for reference_line_id " + request.getReferenceLineID());
			}
		}
		
		rfqItem.setReferenceLineId(request.getReferenceLineID());
		rfqItem.setRequestedManufacturer(request.getRequestedManufacturer());
		rfqItem.setRequestedPart(request.getRequestedPart());
		if(request.getTargetResale() != null){
			try{
				rfqItem.setTargetResale(new BigDecimal(request.getTargetResale()));
			}catch(Exception e){
				LOG.log(Level.WARNING, e.getMessage() + " Failed to convert targetResale " + request.getTargetResale() + 
						" to BigDecimal for reference_line_id " + rfqItem.getReferenceLineId());
			}
		}
		
		rfqItem.setCurrency(request.getCurrency());
		
		return rfqItem;
		
	}		
	
	
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCurrency() {
		return this.currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Timestamp getExpiryDate() {
		return this.expiryDate;
	}

	public void setExpiryDate(Timestamp expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getInternalError() {
		return this.internalError;
	}

	public void setInternalError(String internalError) {
		this.internalError = internalError;
	}

	public Integer getLineItemNumber() {
		return this.lineItemNumber;
	}

	public void setLineItemNumber(Integer lineItemNumber) {
		this.lineItemNumber = lineItemNumber;
	}

	public String getProcStatus() {
		return this.procStatus;
	}

	public void setProcStatus(String procStatus) {
		this.procStatus = procStatus;
	}

	public Long getQuantity() {
		return this.quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public QuoteItem getQuoteItem() {
		return this.quoteItem;
	}

	public void setQuoteItem(QuoteItem quoteItem) {
		this.quoteItem = quoteItem;
	}

	public String getQuoteLineStatus() {
		return this.quoteLineStatus;
	}

	public void setQuoteLineStatus(String quoteLineStatus) {
		this.quoteLineStatus = quoteLineStatus;
	}

	public Long getQuotedQty() {
		return this.quotedQty;
	}

	public void setQuotedQty(Long quotedQty) {
		this.quotedQty = quotedQty;
	}

	public BigDecimal getQuotedResale() {
		return this.quotedResale;
	}

	public void setQuotedResale(BigDecimal quotedResale) {
		this.quotedResale = quotedResale;
	}

	public String getReferenceLineId() {
		return this.referenceLineId;
	}

	public void setReferenceLineId(String referenceLineId) {
		this.referenceLineId = referenceLineId;
	}

	public String getRejectionReason() {
		return this.rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getRequestedManufacturer() {
		return this.requestedManufacturer;
	}

	public void setRequestedManufacturer(String requestedManufacturer) {
		this.requestedManufacturer = requestedManufacturer;
	}

	public String getRequestedPart() {
		return this.requestedPart;
	}

	public void setRequestedPart(String requestedPart) {
		this.requestedPart = requestedPart;
	}
	
	public BigDecimal getTargetResale() {
		return this.targetResale;
	}

	public void setTargetResale(BigDecimal targetResale) {
		this.targetResale = targetResale;
	}

	public DpRfq getDpRfq() {
		return this.dpRfq;
	}

	public void setDpRfq(DpRfq dpRfq) {
		this.dpRfq = dpRfq;
	}


	public Date getReprocessTime() {
		return reprocessTime;
	}


	public void setReprocessTime(Date reprocessTime) {
		this.reprocessTime = reprocessTime;
	}


	public Integer getReprocessCount() {
		return reprocessCount;
	}


	public void setReprocessCount(Integer reprocessCount) {
		this.reprocessCount = reprocessCount;
	}


	public Integer getVersion() {
		return version;
	}


	public void setVersion(Integer version) {
		this.version = version;
	}


	public int getSeq() {
		return seq;
	}


	public void setSeq(int seq) {
		this.seq = seq;
	}
	
	

}