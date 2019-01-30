package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;



/**
 * The persistent class for the DrmsAgpRea database table.
 * 
 */
@Entity
@Table(name="DRMS_AGP_REA")
public class DrmsAgpRea implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="AUTH_GP_CHG_REA",unique=true,nullable=false, length=2)
	private String authGpChgRea;

	@Column(name="AUTH_GP_CHG_DESC", length=90)
	private String authGpChgDesc;
	
	
	public DrmsAgpRea() {
	}


	public String getAuthGpChgRea()
	{
		return authGpChgRea;
	}


	public void setAuthGpChgRea(String authGpChgRea)
	{
		this.authGpChgRea = authGpChgRea;
	}


	public String getAuthGpChgDesc()
	{
		return authGpChgDesc;
	}


	public void setAuthGpChgDesc(String authGpChgDesc)
	{
		this.authGpChgDesc = authGpChgDesc;
	}


	
	
}