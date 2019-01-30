package com.avnet.emasia.webquote.user.vo;

import java.io.Serializable;

import com.avnet.emasia.webquote.entity.DataAccess;

/**
 * @author Lin, Tough(901518)
 * Created on 2013-2-27
 */

public class DataAccessVo implements Serializable{

	private static final long serialVersionUID = 6917153756382258554L;
	
	private long id;
	
	private long seq;

	private String manufacturer;

	private String materialType;
	
	private String programType;
	
	private String productGroup;

	private String team;
	
	public DataAccessVo(){
		
	}	
	
	public DataAccessVo(DataAccess dataAccess){
		id = dataAccess.getId();
		
		if(dataAccess.getManufacturer() == null){
			this.manufacturer = "*ALL";
		}else{
			this.manufacturer = dataAccess.getManufacturer().getName();
		}
		
		if(dataAccess.getMaterialType() == null){
			this.materialType = "ALL";
		}else{
			this.materialType = dataAccess.getMaterialType().getName();
		}

		if(dataAccess.getProgramType() == null){
			this.programType = "ALL";
		}else{
			this.programType = dataAccess.getProgramType().getName();
		}

		if(dataAccess.getProductGroup() == null){
			this.productGroup = "ALL";
		}else{
			this.productGroup = dataAccess.getProductGroup().getName();
		}

		if(dataAccess.getTeam() == null){
			this.team = "ALL";
		}else{
			this.team = dataAccess.getTeam().getName();
		}
		
	}
	
	public long getSeq()
	{
		return seq;
	}

	public void setSeq(long seq)
	{
		this.seq = seq;
	}
	
	//Getters, Setters
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getMaterialType() {
		return materialType;
	}

	public void setMaterialType(String materialType) {
		this.materialType = materialType;
	}

	public String getProgramType() {
		return programType;
	}

	public void setProgramType(String programType) {
		this.programType = programType;
	}

	public String getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(String productGroup) {
		this.productGroup = productGroup;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

}
