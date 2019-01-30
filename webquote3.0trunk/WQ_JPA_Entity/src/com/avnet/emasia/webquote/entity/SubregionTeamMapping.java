package com.avnet.emasia.webquote.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "SUBREGION_TEAM_MAPPING")
public class SubregionTeamMapping implements java.io.Serializable{

	private static final long serialVersionUID = 2515946734575474250L;
	
	@Id
	@SequenceGenerator(name="STMAPPING_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="STMAPPING_ID_GENERATOR")
	@Column(unique=true, nullable=false, precision=19)
	private long id;
	
	@Column(name = "VENDOR", nullable = false, length = 3)
	private String vendor;

	@Column(name = "SUB_REGION", nullable = false, length = 10)
	private String subRegion;
	
	@JoinColumn(name="TEAM_ID")	
	@ManyToOne()
	private Team team;
	
	@ManyToOne
	@JoinColumn(name="BIZ_UNIT", nullable=false)
	private BizUnit bizUnit;
	
	public BizUnit getBizUnit() {
		return bizUnit;
	}

	public void setBizUnit(BizUnit bizUnit) {
		this.bizUnit = bizUnit;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getSubRegion() {
		return subRegion;
	}

	public void setSubRegion(String subRegion) {
		this.subRegion = subRegion;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
