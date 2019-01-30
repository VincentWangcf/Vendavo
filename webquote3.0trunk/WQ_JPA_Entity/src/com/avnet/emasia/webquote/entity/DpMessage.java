
package com.avnet.emasia.webquote.entity;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the DP_MESSAGE database table.
 * 
 */
@Entity
@Table(name="DP_MESSAGE")
public class DpMessage implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="DPMESSAGE_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DPMESSAGE_ID_GENERATOR")
	private long id;

	@Lob
	@Column(name="CREATE_RFQ_MESSAGE")
	private String createRfqMessage;

	@Column(name="CREATE_RFQ_TIME")
	private Timestamp createRfqTime;

	@Lob
	@Column(name="UPDATE_RFQ_MESSAGE")
	private String updateRfqMessage;

	@Column(name="UPDATE_RFQ_TIME")
	private Timestamp updateRfqTime;

	@OneToOne(mappedBy="dpMessage")
	private DpRfq dpRfq;
	
	@Version
	@Column(name="\"VERSION\"")
	private Integer version;
	
	
	@Column(name="BAD_FORMED_CREATE_RFQ_MESSAGE")
	private boolean badFormedCreateRfqMessage;
	
	@ManyToOne
	@JoinColumn(name="BIZ_UNIT")
	private BizUnit bizUnit;

	public DpMessage() {
	}

	@Override
	public String toString() {
		return "DpMessage [id=" + id + ", createRfqMessage=" + createRfqMessage
				+ ", createRfqTime=" + createRfqTime + ", updateRfqMessage="
				+ updateRfqMessage + ", updateRfqTime=" + updateRfqTime
				+ ", dpRfqs=" + dpRfq + ", version=" + version + "]";
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCreateRfqMessage() {
		return this.createRfqMessage;
	}

	public void setCreateRfqMessage(String createRfqMessage) {
		this.createRfqMessage = createRfqMessage;
	}

	public Timestamp getCreateRfqTime() {
		return this.createRfqTime;
	}

	public void setCreateRfqTime(Timestamp createRfqTime) {
		this.createRfqTime = createRfqTime;
	}

	public String getUpdateRfqMessage() {
		return this.updateRfqMessage;
	}

	public void setUpdateRfqMessage(String updateRfqMessage) {
		this.updateRfqMessage = updateRfqMessage;
	}

	public Timestamp getUpdateRfqTime() {
		return this.updateRfqTime;
	}

	public void setUpdateRfqTime(Timestamp updateRfqTime) {
		this.updateRfqTime = updateRfqTime;
	}

	public DpRfq getDpRfq() {
		return this.dpRfq;
	}

	public void setDpRfq(DpRfq dpRfq) {
		this.dpRfq = dpRfq;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public boolean isBadFormedCreateRfqMessage() {
		return badFormedCreateRfqMessage;
	}

	public void setBadFormedCreateRfqMessage(boolean badFormedCreateRfqMessage) {
		this.badFormedCreateRfqMessage = badFormedCreateRfqMessage;
	}

	public BizUnit getBizUnit() {
		return bizUnit;
	}

	public void setBizUnit(BizUnit bizUnit) {
		this.bizUnit = bizUnit;
	}


}