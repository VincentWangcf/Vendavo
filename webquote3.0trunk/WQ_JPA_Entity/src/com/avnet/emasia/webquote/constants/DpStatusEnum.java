package com.avnet.emasia.webquote.constants;

public enum DpStatusEnum {

	QUOTED("R"),
	OFFLINE("O"),
	XML_VALIDATION_ERROR("X");
	
	private String code;
	
	
	DpStatusEnum(String code){
		this.code = code;
	}
	
	public String code(){
		return code;
	}
	
	
}
