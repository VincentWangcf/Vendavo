package com.avnet.emasia.webquote.commodity.constant;

public enum PRICER_TYPE {

	NORMAL("NormalPricer", "Normal Pricer"),
	PROGRAM("ProgramPricer", "Program Pricer"), 
	CONTRACT("ContractPricer", "Contract Pricer"),
	PARTMASTER("PART MASTER", "Part Master"), 
	ALLPRICER("ALLPRICER", "All Pricer"),
	SIMPLE("SimplePricer", "Simple Pricer"), 
	SALESCOST("SalesCostPricer", "SalesCost Pricer");

	private String name;
	private String displayName;
	
	private PRICER_TYPE(String name, String displayName) {
		this.setName(name);
		this.setDisplayName(displayName);
	}

	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}



	/*public static String getNameInDbByName(String name){
		PRICER_TYPE[] pTypes = PRICER_TYPE.values();
		for(PRICER_TYPE pType :pTypes ){
			if(pType.getName().equalsIgnoreCase(name)){
				return pType.getNameInDB();
			}
		}
		return "";
	}*/
	public static PRICER_TYPE getPricerTypeByName(String name) {
		if(name==null || name.length()<1) {
			return null;
		}
		PRICER_TYPE[] pricerTypes = PRICER_TYPE.values();
		for(PRICER_TYPE pricerType:pricerTypes) {
			if(pricerType.getName().equalsIgnoreCase(name)) {
				return pricerType;
			}
		}
		return null;
	}
}
