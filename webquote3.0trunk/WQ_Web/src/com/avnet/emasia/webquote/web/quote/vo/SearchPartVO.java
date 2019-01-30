package com.avnet.emasia.webquote.web.quote.vo;

import java.io.Serializable;

import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.NormalPricer;

public class SearchPartVO implements Serializable {

	private int id;
	private String mfr;
	private String fullMfrPart; // created for table filtering
	private Integer moq;
	private Integer mpq;
	private String leadTime;
	private Double price;
	private Double cost;
	private int itemNumber;
	private String sapPartNumber;
	private String description;
	private Material material;
	private NormalPricer materialDeail;
	
	public String getMfr() {
		return mfr;
	}
	public void setMfr(String mfr) {
		this.mfr = mfr;
	}
	public String getFullMfrPart() {
		return fullMfrPart;
	}
	public void setFullMfrPart(String fullMfrPart) {
		this.fullMfrPart = fullMfrPart;
	}
	public Integer getMoq() {
		return moq;
	}
	public void setMoq(Integer moq) {
		this.moq = moq;
	}
	public Integer getMpq() {
		return mpq;
	}
	public void setMpq(Integer mpq) {
		this.mpq = mpq;
	}
	public String getLeadTime() {
		return leadTime;
	}
	public void setLeadTime(String leadTime) {
		this.leadTime = leadTime;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public int getItemNumber() {
		return itemNumber;
	}
	public void setItemNumber(int itemNumber) {
		this.itemNumber = itemNumber;
	}
	public String getSapPartNumber() {
		return sapPartNumber;
	}
	public void setSapPartNumber(String sapPartNumber) {
		this.sapPartNumber = sapPartNumber;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Material getMaterial() {
		return material;
	}
	public void setMaterial(Material material) {
		this.material = material;
	}
	public Double getCost() {
		return cost;
	}
	public void setCost(Double cost) {
		this.cost = cost;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public NormalPricer getMaterialDeail() {
		return materialDeail;
	}
	public void setMaterialDeail(NormalPricer materialDeail) {
		this.materialDeail = materialDeail;
	}
	
}
