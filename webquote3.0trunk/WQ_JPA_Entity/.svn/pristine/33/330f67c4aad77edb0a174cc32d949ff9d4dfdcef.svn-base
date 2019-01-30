package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;
import java.util.List;


/**
 * The persistent class for the "ROLE" database table.
 * 
 */
@Entity
@Table(name="\"ROLE\"")
public class Role implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="ROLE_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ROLE_ID_GENERATOR")
	@Column(unique=true, nullable=false, precision=19)
	private long id;

	@Column(nullable=false, precision=1)
	private boolean active;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_ON", nullable=false)
	private Date createdOn;

	@Column(length=100)
	private String description;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_UPDATED_ON")
	private Date lastUpdatedOn;

	@Column(length=255)
	private String name;
	
	@ManyToMany
	@JoinTable(name="APP_FUNCTION_ROLE_MAPPING",
	joinColumns= @JoinColumn(name="ROLE_ID"),
	inverseJoinColumns=@JoinColumn(name="FUNCTION_ID"))
	private List<AppFunction> appFunctions; 
		
	@Version
	@Column(name="\"VERSION\"", nullable=false, precision=10)
	private int version;

	//uni-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="LAST_UDPATED_BY")
	private User lastUpdatedBy;

	//uni-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="CREATED_BY", nullable=false)
	private User createdBy;

	public Role() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean getActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
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

	public Date getLastUpdatedOn() {
		return this.lastUpdatedOn;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
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

	public List<AppFunction> getAppFunctions() {
		return appFunctions;
	}

	public void setAppFunctions(List<AppFunction> appFunctions) {
		this.appFunctions = appFunctions;
	}

}