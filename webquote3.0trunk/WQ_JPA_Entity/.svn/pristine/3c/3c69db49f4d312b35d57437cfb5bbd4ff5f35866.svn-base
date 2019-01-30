package com.avnet.emasia.webquote.entity;

import java.io.Serializable;

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
@Table(name="DCODE_REGION_MAPPING")
public class DcodeRrgionMapping implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="DCODE_REGION_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DCODE_REGION_ID_GENERATOR")
	@Column(unique=true, nullable=false, precision=19)
	private long id;

	@Column(name="DISTRIBUTOR_CODE", length=20)
	private String distributorCode;

	@ManyToOne
	@JoinColumn(name="BIZ_UNIT", nullable=false)
	private BizUnit bizUnit;
	
	public DcodeRrgionMapping() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDistributorCode() {
		return distributorCode;
	}

	public void setDistributorCode(String distributorCode) {
		this.distributorCode = distributorCode;
	}

	public BizUnit getBizUnit() {
		return bizUnit;
	}

	public void setBizUnit(BizUnit bizUnit) {
		this.bizUnit = bizUnit;
	}

}