package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.Date;


/**
 * The persistent class for the PROMOTION_ITEM database table.
 * 
 */
@Entity
@Table(name="PROMOTION_ITEM")
@XmlRootElement
@NamedQueries(
{
	@NamedQuery(name="PromotionItem.findById", query="select a from PromotionItem a where a.id=:id")
})
public class PromotionItem implements Serializable, Auditable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="PROMOTION_ITEM_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PROMOTION_ITEM_ID_GENERATOR")
	@Column(unique=true, nullable=false, precision=15)
	private long id;

	@ManyToOne
	@JoinColumn(name="BIZ_UNIT_ID", nullable=false)
	private BizUnit bizUnit;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_ON", nullable=false)
	private Date createdOn;

	@Column(length=500)
	private String description;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_UPDATED_ON")
	private Date lastUpdatedOn;

	@Column(name="PROGRAM_CODE", nullable=false, length=20)
	private String programCode;

	@Column(name="\"SEQUENCE\"")
	private Integer sequence;

	@Column(name="\"VERSION\"")
	private Integer version;

	//uni-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="LAST_UDPATED_BY")
	private User lastUpdatedBy;

	//uni-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="CREATED_BY", nullable=false)
	private User createdBy;

	//uni-directional many-to-one association to Material
	@ManyToOne
	@JoinColumn(name="MATERIAL_ID", nullable=false)
	private Material material;

	public PromotionItem() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}



	public BizUnit getBizUnit() {
		return bizUnit;
	}

	public void setBizUnit(BizUnit bizUnit) {
		this.bizUnit = bizUnit;
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

	public String getProgramCode() {
		return this.programCode;
	}

	public void setProgramCode(String programCode) {
		this.programCode = programCode;
	}

	public Integer getSequence() {
		return this.sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
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

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	@Override
	public String getTargetId() {
		return String.valueOf(id);
	}


}