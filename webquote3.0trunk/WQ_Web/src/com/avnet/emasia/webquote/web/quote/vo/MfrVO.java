package com.avnet.emasia.webquote.web.quote.vo;

import java.io.Serializable;

public class MfrVO implements Serializable {

	private long id;
	private String name;
	private String description;
	private String bizUnit;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getBizUnit() {
		return bizUnit;
	}
	public void setBizUnit(String bizUnit) {
		this.bizUnit = bizUnit;
	}

	
}

