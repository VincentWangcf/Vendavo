package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;
import java.util.List;


/**
 * The persistent class for the DR_PROJECT_HEADER database table.
 * 
 */
@Entity
@Table(name="DR_PROJECT_HEADER")
public class DrProjectHeader implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="PROJECT_ID", unique=true, nullable=false, precision=7)
	private long projectId;

	@Column(name="ACCOUNT_MANAGER_ID", precision=12)
	private Integer accountManagerId;

	@Column(name="ACCOUNT_MANAGER_NAME", length=30)
	private String accountManagerName;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_ON", nullable=false)
	private Date createdOn;

	@Column(name="GROUP_PROJECT_ID", precision=7)
	private Integer groupProjectId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_UPDATED_ON")
	private Date lastUpdatedOn;

	@Column(name="PROJECT_DESCRIPTION", length=85)
	private String projectDescription;

	@Column(name="PROJECT_NAME", length=80)
	private String projectName;

	@ManyToOne
	@JoinColumn(name="SALES_ORG")
	private SalesOrg salesOrgBean;


	@Column(name="SALES_ORG_DESCRIPTION", length=20)
	private String salesOrgDescription;

	//bi-directional many-to-one association to DrNedaHeader
	@OneToMany(mappedBy="drProjectHeader")
	private List<DrNedaHeader> drNedaHeaders;

	//bi-directional many-to-one association to DrProjectCustomer
	@OneToMany(mappedBy="drProjectHeader")
	private List<DrProjectCustomer> drProjectCustomers;

	@Transient 
	private String originalSapData;

	
	public DrProjectHeader() {
	}

	public long getProjectId() {
		return this.projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public Integer getAccountManagerId() {
		return this.accountManagerId;
	}

	public void setAccountManagerId(Integer accountManagerId) {
		this.accountManagerId = accountManagerId;
	}

	public String getAccountManagerName() {
		return this.accountManagerName;
	}

	public void setAccountManagerName(String accountManagerName) {
		this.accountManagerName = accountManagerName;
	}

	public Date getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Integer getGroupProjectId() {
		return this.groupProjectId;
	}

	public void setGroupProjectId(Integer groupProjectId) {
		this.groupProjectId = groupProjectId;
	}

	public Date getLastUpdatedOn() {
		return this.lastUpdatedOn;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public String getProjectDescription() {
		return this.projectDescription;
	}

	public void setProjectDescription(String projectDescription) {
		this.projectDescription = projectDescription;
	}

	public String getProjectName() {
		return this.projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}



	public String getSalesOrgDescription() {
		return this.salesOrgDescription;
	}

	public void setSalesOrgDescription(String salesOrgDescription) {
		this.salesOrgDescription = salesOrgDescription;
	}

	public List<DrNedaHeader> getDrNedaHeaders() {
		return this.drNedaHeaders;
	}

	public void setDrNedaHeaders(List<DrNedaHeader> drNedaHeaders) {
		this.drNedaHeaders = drNedaHeaders;
	}

	public List<DrProjectCustomer> getDrProjectCustomers() {
		return this.drProjectCustomers;
	}

	public void setDrProjectCustomers(List<DrProjectCustomer> drProjectCustomers) {
		this.drProjectCustomers = drProjectCustomers;
	}

	public SalesOrg getSalesOrgBean() {
		return salesOrgBean;
	}

	public void setSalesOrgBean(SalesOrg salesOrgBean) {
		this.salesOrgBean = salesOrgBean;
	}

	@Override
	public String toString() {
		return "DrProjectHeader [projectId=" + projectId
				+ ", accountManagerId=" + accountManagerId
				+ ", accountManagerName=" + accountManagerName
				+  ", createdOn=" + createdOn
				+ ", groupProjectId=" + groupProjectId + ", lastUpdatedOn="
				+ lastUpdatedOn  
				+ ", projectDescription=" + projectDescription
				+ ", projectName=" + projectName + ", salesOrgBean="
				+ salesOrgBean + ", salesOrgDescription=" + salesOrgDescription
				+ ", drNedaHeaders=" + drNedaHeaders + ", drProjectCustomers="
				+ drProjectCustomers + "]";
	}


	public String getOriginalSapData() {
		return originalSapData;
	}
	@Transient
	public void setOriginalSapData(String originalSapData) {
		this.originalSapData = originalSapData;
	}

}