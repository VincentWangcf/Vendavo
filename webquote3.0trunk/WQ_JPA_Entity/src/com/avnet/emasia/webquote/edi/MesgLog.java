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
 * The persistent class for the TI_MESSAGE_LOG database table.
 * 
 */
@Entity
@Table(name="TI_MESSAGE_LOG")
public class MesgLog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="TI_MESSAGE_LOG_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TI_MESSAGE_LOG_ID_GENERATOR")
	@Column(unique=true, nullable=false)
	private long id;

	@Column(name="PO_NUM", length=35)
	private String poNum;

	@Lob
	@Column(name="READ_TI_MESG")
	private String readTiMesg;

	@Column(name="TI_MESG_FILE_NAME", length=100)
	private String tiMesgFileName;

	@Column(name="MESG_TYPE", length=10)
	private String mesgType;
	 
	@Column(name="STATUS", length=20)
	private String status;

	@Column(name="ERROR_MESG", length=500)
	private String errorMesg;
	
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

	public String getReadTiMesg() {
		return readTiMesg;
	}

	public void setReadTiMesg(String readTiMesg) {
		this.readTiMesg = readTiMesg;
	}

	public String getTiMesgFileName() {
		return tiMesgFileName;
	}

	public void setTiMesgFileName(String tiMesgFileName) {
		this.tiMesgFileName = tiMesgFileName;
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

	public String getErrorMesg() {
		return errorMesg;
	}

	public void setErrorMesg(String errorMesg) {
		this.errorMesg = errorMesg;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
}