package com.avnet.emasia.webquote.entity;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name="SUBREGION_RECIPIENT_MAPPING")
public class SubregionRecipientMapping implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="SB_RECIPIENT_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SB_RECIPIENT_ID_GENERATOR")
	@Column(unique=true, nullable=false)
	private long id;

	@Column(name = "supplier",nullable=false, length=3)
	private String supplier;

	@Column(name = "SUB_REGION", nullable = false, length = 10)
	private String subRegion;
	
	@Column(name = "RECIPIENT", nullable = false, length = 50)
	private String recipient;
	
	@ManyToOne
	@JoinColumn(name="BIZ_UNIT", nullable=false)
	private BizUnit bizUnit;

	public BizUnit getBizUnit() {
		return bizUnit;
	}

	public void setBizUnit(BizUnit bizUnit) {
		this.bizUnit = bizUnit;
	}

	public SubregionRecipientMapping() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getSubRegion() {
		return subRegion;
	}

	public void setSubRegion(String subRegion) {
		this.subRegion = subRegion;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}



}