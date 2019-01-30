package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;


import java.util.Date;


/**
 * The persistent class for the ANNOUNCEMENT database table.
 * 
 */
@Entity
@Table(name="ANNOUNCEMENT")
@XmlRootElement
@NamedQueries(
{
	@NamedQuery(name="Announcement.findById", query="select a from Announcement a where a.id=:id")
})
public class Announcement implements Serializable , Auditable{
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="ANNOUNCEMENT_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ANNOUNCEMENT_ID_GENERATOR")
	@Column(unique=true, nullable=false)
	private long id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_ON", nullable=false)
	private Date createdOn;

	@Column(length=255)
	private String description;

	@Column(name="URL",length=500)
	private String url;
	
	@Temporal(TemporalType.DATE)
	@Column(name="EXPIRATION_DATE", nullable=false)
	private Date expirationDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_UPDATED_ON")
	private Date lastUpdatedOn;

	@Column(name="\"SEQUENCE\"", precision=5)
	private int sequence;

	@Version
	@Column(name="\"VERSION\"", nullable=false, precision=5)
	private int version;

	//uni-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="LAST_UDPATED_BY")
	private User lastUpdatedBy;

	//uni-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="CREATED_BY", nullable=false)
	private User createdBy;

	//uni-directional many-to-one association to BizUnit
	@ManyToOne
	@JoinColumn(name="BIZ_UNIT_ID", nullable=false)
	private BizUnit bizUnit;

	public Announcement() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getExpirationDate() {
		return this.expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Date getLastUpdatedOn() {
		return this.lastUpdatedOn;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public User getLastUpdatedBy() {
		return this.lastUpdatedBy;
	}

	public void setLastUpdatedBy(User lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public User getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public BizUnit getBizUnit() {
		return this.bizUnit;
	}

	public void setBizUnit(BizUnit bizUnit) {
		this.bizUnit = bizUnit;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "Announcement [id=" + id + ", createdOn=" + createdOn
				+ ", description=" + description + ", expirationDate="
				+ expirationDate + ", lastUpdatedOn=" + lastUpdatedOn
				+ ", sequence=" + sequence + ", version=" + version
				+ ", lastUpdatedBy=" + lastUpdatedBy + ", createdBy="
				+ createdBy + ", bizUnit=" + bizUnit + "]";
	}

	@Override
	public String getTargetId() {
		return String.valueOf(id);
	}

	
}