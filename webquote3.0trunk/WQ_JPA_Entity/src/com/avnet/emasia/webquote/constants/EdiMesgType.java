package com.avnet.emasia.webquote.constants;

public enum EdiMesgType {
	TARONE("3A1_R1"),
	TARTWO("3A1_R2"),
	FDRONE("5D1_R1"),
	FDRTWO("5D1_R2"),
	TAONE("3A1");
	
	private String mesgTypeName ;
	
	
	EdiMesgType(String mesgTypeName){
		this.mesgTypeName = mesgTypeName;
	}
	
	public String getMesgTypeName(){
		return mesgTypeName;
	}
}
