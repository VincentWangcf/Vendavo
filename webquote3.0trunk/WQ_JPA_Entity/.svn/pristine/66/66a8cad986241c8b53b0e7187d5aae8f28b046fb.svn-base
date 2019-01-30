package com.avnet.emasia.webquote.entity;

import java.io.Serializable;

import javax.persistence.*;

import java.util.Date;


/**
 * The persistent class for the QUOTE_RESPONSE_TIME_HISTORY database table.
 * 
 */
@Entity
@Table(name="QUOTE_RESPONSE_TIME_HISTORY")
public class QuoteResponseTimeHistory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="QuoteResponseTimeHistory_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="QuoteResponseTimeHistory_ID_GENERATOR")
	private Long id;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_TIME")
	private Date createdTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_IN_DATE")
	private Date lastInDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_OUT_DATE")
	private Date lastOutDate;

	@Column(name="LAST_UPDATED_BY")
	private String lastUpdatedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_UPDATED_TIME")
	private Date lastUpdatedTime;

	@Column(name="PENDING_BY")
	private String pendingBy;

	@ManyToOne
	@JoinColumn(name="QUOTE_ITEM_ID",updatable=false)
	private QuoteItem quoteItem;

	@Version
	@Column(name="\"VERSION\"")
	private Integer version;

	public QuoteResponseTimeHistory() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Date getLastInDate() {
		return this.lastInDate;
	}

	public void setLastInDate(Date lastInDate) {
		this.lastInDate = lastInDate;
	}

	public Date getLastOutDate() {
		return this.lastOutDate;
	}

	public void setLastOutDate(Date lastOutDate) {
		this.lastOutDate = lastOutDate;
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

	public String getPendingBy() {
		return this.pendingBy;
	}

	public void setPendingBy(String pendingBy) {
		this.pendingBy = pendingBy;
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

}