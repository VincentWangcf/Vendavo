package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the SAP_MATERIAL_DETAIL database table.
 * 
 */
@Entity
@Table(name="SAP_MATERIAL_DETAIL")
public class SapMaterialDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="SAP_MATERIAL_DETAIL_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SAP_MATERIAL_DETAIL_ID_GENERATOR")
	@Column(unique=true, nullable=false)
	private long id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_ON", nullable=false)
	private Date createdOn;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_UPDATED_ON")
	private Date lastUpdatedOn;

	@Column(name="\"VERSION\"")
	private Integer version;
	
	//uni-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="LAST_UPDATED_BY")
	private User lastUpdatedBy;

	//uni-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="CREATED_BY", nullable=false)
	private User createdBy;

	//uni-directional many-to-one association to BizUnit
	@ManyToOne
	@JoinColumn(name="BIZ_UNIT", nullable=false)
	private BizUnit bizUnitBean;

	//uni-directional many-to-one association to Material
	@ManyToOne
	@JoinColumn(name="MATERIAL_ID", nullable=false)
	private Material material;

	//uni-directional many-to-one association to SalesOrg
	@ManyToOne
	@JoinColumn(name="SALES_ORG")
	private SalesOrg salesOrgBean;

	
	@Column(name="PLANT_ID")
	private String plant;
	
	public SapMaterialDetail() {
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

	public Date getLastUpdatedOn() {
		return this.lastUpdatedOn;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
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

	public BizUnit getBizUnitBean() {
		return this.bizUnitBean;
	}

	public void setBizUnitBean(BizUnit bizUnitBean) {
		this.bizUnitBean = bizUnitBean;
	}

	public Material getMaterial() {
		return this.material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public SalesOrg getSalesOrgBean() {
		return this.salesOrgBean;
	}

	public void setSalesOrgBean(SalesOrg salesOrgBean) {
		this.salesOrgBean = salesOrgBean;
	}

	public String getPlant() {
		return this.plant;
	}

	public void setPlant(String plant) {
		this.plant = plant;
	}

	@Override
	public String toString() {
		return "MaterialDetail [bizUnitBean="
				+ bizUnitBean + ", material=" + material + ", salesOrgBean="
				+ salesOrgBean + ", plant=" + plant + "]";
	}
	
	
}