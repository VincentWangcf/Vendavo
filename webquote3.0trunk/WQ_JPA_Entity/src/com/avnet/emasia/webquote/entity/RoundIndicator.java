package com.avnet.emasia.webquote.entity;

import java.math.BigDecimal;
import java.util.Arrays;


public enum RoundIndicator {
	ROUND_UP("Round up",BigDecimal.ROUND_UP) ,
	ROUND_DOWN("Round down",BigDecimal.ROUND_DOWN),
	ROUND_OFF("Round off",BigDecimal.ROUND_HALF_UP);
	private String roundName;
	private int roundType;
	
	RoundIndicator(String roundName, int roundType){
		this.roundName=roundName;
		this.roundType=roundType;
	}

	public String getRoundName() {
		return roundName;
	}

	public void setRoundName(String roundName) {
		this.roundName = roundName;
	}
	
	public int getRoundType() {
		return roundType;
	}

	public void setRoundType(int roundType) {
		this.roundType = roundType;
	}

	public static Integer getTypeByName(String name) {
		if(name==null || name.length()<1) {
			return null;
		}
		RoundIndicator[] values = RoundIndicator.values();
		for(RoundIndicator v:values) {
			if(v.getRoundName().equalsIgnoreCase(name)) {
				return v.getRoundType();
			}
		}
		return null;
	}
	
	public static String getNameByType(Integer type) {
		
		if(type ==null || type<0) {
			return null;
		}
		RoundIndicator[] values = RoundIndicator.values();
		for(RoundIndicator v:values) {
			if(v.getRoundType()==type) {
				return v.roundName;
			}
		}
		return null;
	}
	
	public static String[] getAllTypeName() {
		
		RoundIndicator[] values = RoundIndicator.values();
		String[] allTypeName = new String[values.length];
		for(int i =0; i< values.length; i++) {
			allTypeName[i] = values[i].getRoundName(); 
		}
		return allTypeName;
	}
}
