package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;
import java.util.List;


/**
 * The persistent class for the DR_NEDA_HEADER database table.
 * 
 */
@Entity
@Table(name="DR_NEDA_HEADER")
public class DrNedaHeader implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private DrNedaHeaderPK id;

	@Column(name="ASSEMBLY_QTY", precision=13)
	private Integer assemblyQty;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_ON", nullable=false)
	private Date createdOn;


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_UPDATED_ON")
	private Date lastUpdatedOn;

	@Column(name="NEDA_DESIGN_STAGE", length=2)
	private String nedaDesignStage;

	@Column(name="SUPPLIER_STATUS", length=20)
	private String supplierStatus;

	//bi-directional many-to-one association to DrProjectHeader
	@ManyToOne
	@JoinColumn(name="PROJECT_ID", nullable=false, insertable=false, updatable=false)
	private DrProjectHeader drProjectHeader;

	//bi-directional many-to-one association to DrNedaItem
	@OneToMany(mappedBy="drNedaHeader")
	private List<DrNedaItem> drNedaItems;
	
	@Temporal(TemporalType.DATE)
	@Column(name="PRODUCTION_DATE")
	private Date productionDate;

	
	

	@Transient 
	private String originalSapData;

	
	public DrNedaHeader() {
	}

	public DrNedaHeaderPK getId() {
		return this.id;
	}

	public void setId(DrNedaHeaderPK id) {
		this.id = id;
	}

	public Integer getAssemblyQty() {
		return this.assemblyQty;
	}

	public void setAssemblyQty(Integer assemblyQty) {
		this.assemblyQty = assemblyQty;
	}

	public Date getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getLastUpdatedOn() {
		return this.lastUpdatedOn;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public String getNedaDesignStage() {
		return this.nedaDesignStage;
	}

	public void setNedaDesignStage(String nedaDesignStage) {
		this.nedaDesignStage = nedaDesignStage;
	}

	public String getSupplierStatus() {
		return this.supplierStatus;
	}

	public void setSupplierStatus(String supplierStatus) {
		this.supplierStatus = supplierStatus;
	}

	public DrProjectHeader getDrProjectHeader() {
		return this.drProjectHeader;
	}

	public void setDrProjectHeader(DrProjectHeader drProjectHeader) {
		this.drProjectHeader = drProjectHeader;
	}

	public List<DrNedaItem> getDrNedaItems() {
		return this.drNedaItems;
	}

	public void setDrNedaItems(List<DrNedaItem> drNedaItems) {
		this.drNedaItems = drNedaItems;
	}
	
	public Date getProductionDate() {
		return this.productionDate;
	}

	public void setProductionDate(Date productionDate) {
		this.productionDate = productionDate;
	}
	

	@Override
	public String toString() {
		return "DrNedaHeader [id=" + id + ", assemblyQty=" + assemblyQty
				+ ", createdOn=" + createdOn 
				+  ", lastUpdatedOn=" + lastUpdatedOn
				+ ", nedaDesignStage=" + nedaDesignStage 
				+ ", supplierStatus=" + supplierStatus + ", drProjectHeader="
				+ drProjectHeader + ", drNedaItems=" + drNedaItems + "]";
	}
	
	public String getOriginalSapData() {
		return originalSapData;
	}
	@Transient
	public void setOriginalSapData(String originalSapData) {
		this.originalSapData = originalSapData;
	}

}