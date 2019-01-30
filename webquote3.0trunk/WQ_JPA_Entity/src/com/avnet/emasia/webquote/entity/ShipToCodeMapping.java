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
@Table(name = "SHIP_TO_CODE_MAPPING")
public class ShipToCodeMapping implements java.io.Serializable{

	private static final long serialVersionUID = 2515946734575474250L;
	@Id
	@SequenceGenerator(name="STCM_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="STCM_ID_GENERATOR")
	@Column(unique=true, nullable=false, precision=19)
	private long id;
	
	@Column(name = "TYPE", nullable = false, length = 5)
	private String type;

	@Column(name = "SHIP_TO_CODE", nullable = false, length = 35)
	private String shipToCode;
	
	@ManyToOne
	@JoinColumn(name="TEAM_ID")	
	private Team team;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getShipToCode() {
		return shipToCode;
	}

	public void setShipToCode(String shipToCode) {
		this.shipToCode = shipToCode;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}
}
