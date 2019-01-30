package com.avnet.emasia.webquote.commodity.constant;

public enum UPLOAD_ACTION {
	ADD_UPDATE("ADD/UPDATE"),REMOVE_PRICER_ONLY("REMOVE (PRICER ONLY)"), REMOVE_PART_AND_ITS_PRICERS("REMOVE (PART AND ITS PRICERS"),
	REMOVE_ALL_PRICER("REMOVE (ALL PRICERS)")
	;

	private String name;

	private UPLOAD_ACTION( String name) {
		this.setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public static UPLOAD_ACTION getActionByName(String name)
	{
		if(name==null || name.length()<1)
		{
			return null;
		}
		UPLOAD_ACTION[] actions = UPLOAD_ACTION.values();
		for(UPLOAD_ACTION action:actions)
		{
			if(action.getName().equalsIgnoreCase(name))
			{
				return action;
			}
		}
		return null;
	}
	
}
