package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the DATA_SYNC_ERROR database table.
 * 
 */
@Entity
@Table(name="DATA_SYNC_ERROR")
public class DataSyncError implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="DATA_SYNC_ERROR_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DATA_SYNC_ERROR_ID_GENERATOR")
	@Column(unique=true, nullable=false)
	private long id;

	@Temporal(TemporalType.DATE)
	@Column(name="CREATING_DATE", nullable=false)
	private Date creatingDate;

	@Column(name="ERROR_MESSAGE", nullable=false, length=2000)
	private String errorMessage;

	@Column(name="ERROR_RECORD", nullable=false, length=255)
	private String errorRecord;

	@Column(name="FILE_NAME", nullable=false, length=50)
	private String fileName;

	@Column(name="FUNCTION_CODE", nullable=false, length=20)
	private String functionCode;

	public DataSyncError() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getCreatingDate() {
		return this.creatingDate;
	}

	public void setCreatingDate(Date creatingDate) {
		this.creatingDate = creatingDate;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorRecord() {
		return this.errorRecord;
	}

	public void setErrorRecord(String errorRecord) {
		this.errorRecord = errorRecord;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFunctionCode() {
		return this.functionCode;
	}

	public void setFunctionCode(String functionCode) {
		this.functionCode = functionCode;
	}

}