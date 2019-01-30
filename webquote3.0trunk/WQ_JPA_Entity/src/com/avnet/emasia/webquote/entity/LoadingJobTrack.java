package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the LOADING_JOB_TRACK database table.
 * 
 */
@Entity
@Table(name="LOADING_JOB_TRACK")
public class LoadingJobTrack implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="LOADING_JOB_TRACK_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="LOADING_JOB_TRACK_ID_GENERATOR")
	@Column(unique=true, nullable=false, precision=18)
	private long id;

	@Temporal(TemporalType.DATE)
	@Column(name="END_TIME")
	private Date endTime;

	@Column(name="FILE_NAME", length=50)
	private String fileName;

	@Temporal(TemporalType.DATE)
	@Column(name="START_TIME")
	private Date startTime;

	@Column(length=5)
	private String status;

	@Column(name="\"TYPE\"", nullable=false, length=20)
	private String type;

	public LoadingJobTrack() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getStartTime() {
		return this.startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

}