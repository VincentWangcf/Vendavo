package com.avnet.emasia.webquote.edi;

import java.io.Serializable;

import javax.persistence.*;

import com.avnet.emasia.webquote.entity.util.MaterialDetailListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the MATERIAL_DETAIL database table.
 * 
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="MESG_TYPE")
@Table(name="TI_3A1_5D1_R1")
public class MesgRefuse implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="TI_AD_REFUSE_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TI_AD_REFUSE_ID_GENERATOR")
	@Column(unique=true, nullable=false)
	private long id;

	@Column(name="PO_NUM", length=35, nullable=false)
	private String poNum;

	@Column(name="PO_ITEM_NUM", length=5, nullable=false)
	private String poItemNum;

	@Column(name="QUOTE_NUM", length=20, nullable=false)
	private String quoteNum;

	@Column(name="TI_QUOTE_NUM", length=20)
	private String tiQuoteNum;

	@Column(name="TI_QUOTE_ITEM_NUM", length=10)
	private String tiQuoteItemNum;

	@Column(name="TI_PART_NUM", length=50)
	private String tiPartNum;

	@Column(name="MESG_TYPE", length=10, nullable=false)
	private String mesgType;
	 
	@Column(name="STATUS", length=20)
	private String status;

	@Column(name="REASON", length=250)
	private String reason;
	
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

	public String getMesgType() {
		return mesgType;
	}

	public void setMesgType(String mesgType) {
		this.mesgType = mesgType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	@Override
	public String toString() {
		return "MesgRefuse [poNum=" + poNum + ", mesgType=" + mesgType + ", status=" + status + ", reason=" + reason
				+ "]";
	}

	
}