package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;



/**
 * The persistent class for the DATA_ACCESS database table.
 * 
 */
@Entity
@Table(name="DATA_ACCESS")
public class DataAccess implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="DATA_ACCESS_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DATA_ACCESS_ID_GENERATOR")
	@Column(unique=true, nullable=false, precision=18)
	private long id;

	//uni-directional many-to-one association to Manufacturer
	@ManyToOne
	@JoinColumn(name="MANUFACTURER_ID")
	private Manufacturer manufacturer;

	//uni-directional many-to-one association to MaterialType
	@ManyToOne
	@JoinColumn(name="MATERIAL_TYPE_ID")
	private MaterialType materialType;
	
	//uni-directional many-to-one association to MaterialType
	@ManyToOne
	@JoinColumn(name="PROGRAM_TYPE_ID")
	private ProgramType programType;
	

	//uni-directional many-to-one association to ProductGroup
	@ManyToOne
	@JoinColumn(name="PRODUCT_GROUP_ID")
	private ProductGroup productGroup;

	//uni-directional many-to-one association to Team
	@ManyToOne
	@JoinColumn(name="TEAM_ID")
	private Team team;

	public DataAccess() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Manufacturer getManufacturer() {
		return this.manufacturer;
	}

	public void setManufacturer(Manufacturer manufacturer) {
		this.manufacturer = manufacturer;
	}

	public MaterialType getMaterialType() {
		return this.materialType;
	}

	public void setMaterialType(MaterialType materialType) {
		this.materialType = materialType;
	}

	public ProductGroup getProductGroup() {
		return this.productGroup;
	}

	public void setProductGroup(ProductGroup productGroup) {
		this.productGroup = productGroup;
	}

	public Team getTeam() {
		return this.team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public ProgramType getProgramType() {
		return programType;
	}

	public void setProgramType(ProgramType programType) {
		this.programType = programType;
	}

	@Override
	public String toString() {
		return "DataAccess [id=" + id + ", manufacturer=" + manufacturer
				+ ", materialType=" + materialType + ", programType="
				+ programType + ", productGroup=" + productGroup + ", team="
				+ team + "]";
	}
	
	

}