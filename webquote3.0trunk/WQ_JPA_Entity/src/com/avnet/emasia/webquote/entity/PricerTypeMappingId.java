package com.avnet.emasia.webquote.entity;

// Generated 2014-4-24 17:15:38 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * PricerTypeMappingId generated by hbm2java
 */
@Embeddable
public class PricerTypeMappingId implements java.io.Serializable
{

	private String mfr;
	private String partNumber;
	private String bizUnit;

	public PricerTypeMappingId()
	{
	}

	public PricerTypeMappingId(String mfr, String partNumber, String bizUnit)
	{
		this.mfr = mfr;
		this.partNumber = partNumber;
		this.bizUnit = bizUnit;
	}

	@Column(name = "MFR", length = 10)
	public String getMfr()
	{
		return this.mfr;
	}

	public void setMfr(String mfr)
	{
		this.mfr = mfr;
	}

	@Column(name = "PART_NUMBER", length = 80)
	public String getPartNumber()
	{
		return this.partNumber;
	}

	public void setPartNumber(String partNumber)
	{
		this.partNumber = partNumber;
	}

	@Column(name = "BIZ_UNIT", length = 10)
	public String getBizUnit()
	{
		return this.bizUnit;
	}

	public void setBizUnit(String bizUnit)
	{
		this.bizUnit = bizUnit;
	}

	public boolean equals(Object other)
	{
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof PricerTypeMappingId))
			return false;
		PricerTypeMappingId castOther = (PricerTypeMappingId) other;

		return ((this.getMfr() == castOther.getMfr()) || (this.getMfr() != null
				&& castOther.getMfr() != null && this.getMfr().equals(
				castOther.getMfr())))
				&& ((this.getPartNumber() == castOther.getPartNumber()) || (this
						.getPartNumber() != null
						&& castOther.getPartNumber() != null && this
						.getPartNumber().equals(castOther.getPartNumber())))
				&& ((this.getBizUnit() == castOther.getBizUnit()) || (this
						.getBizUnit() != null && castOther.getBizUnit() != null && this
						.getBizUnit().equals(castOther.getBizUnit())));
	}

	public int hashCode()
	{
		int result = 17;

		result = 37 * result
				+ (getMfr() == null ? 0 : this.getMfr().hashCode());
		result = 37
				* result
				+ (getPartNumber() == null ? 0 : this.getPartNumber()
						.hashCode());
		result = 37 * result
				+ (getBizUnit() == null ? 0 : this.getBizUnit().hashCode());
		return result;
	}

}
