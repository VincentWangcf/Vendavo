package com.avnet.emasia.webquote.entity;

public enum SalesCostType {
	ZBMP,
	ZBMD,
	ZINC,
	ZIND,
	ZINM,
	NonSC
	;

	public static SalesCostType GetSCTypeByStr(String enumStr) {
		if(enumStr ==null ||enumStr.length()==0) {
			return null;
		}
		SalesCostType [] values = SalesCostType.values();
		for(SalesCostType value :values){
			if(value.toString().equals(enumStr)){
				return value;
			}
		}
		return null;
	}
}
