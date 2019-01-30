package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;



/**
 * The persistent class for the TEAM database table.
 * 
 */
@Entity
@Table(name="TEAM")
public class Team implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique=true, nullable=false, length=30)
	private String name;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_ON")
	private Date createdOn;

	@Column(length=50)
	private String description;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_UPDATED_ON")
	private Date lastUpdatedOn;

	@Column(name = "LAST_UPDATED_BY", length = 10)
	private String lastUpdatedBy;

	//uni-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="TEAM_LEAD_ID")
	private User teamLead;

	@Column(name = "CREATED_BY", length = 10)
	private String createdBy;

	//uni-directional many-to-one association to BizUnit
	@ManyToOne
	@JoinColumn(name="BIZ_UNIT_ID")
	private BizUnit bizUnit;
	
	@Column(name="ACTIVE")
	private boolean active;

	public Team() {
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getCreatedOn() {
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

	public Object getLastUpdatedOn() {
		return this.lastUpdatedOn;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public String getLastUpdatedBy()
	{
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy)
	{
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public User getTeamLead() {
		return this.teamLead;
	}

	public void setTeamLead(User teamLead) {
		this.teamLead = teamLead;
	}


	public BizUnit getBizUnit() {
		return this.bizUnit;
	}

	public void setBizUnit(BizUnit bizUnit) {
		this.bizUnit = bizUnit;
	}

	public String getCreatedBy()
	{
		return createdBy;
	}

	public void setCreatedBy(String createdBy)
	{
		this.createdBy = createdBy;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}