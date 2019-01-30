package com.avnet.emasia.webquote.commodity.constant;

public enum QtyIndicatorEnum {
	//MOQ/MPQ/MOV/25%/50%/75%/100%/Exact/Limit
	MOQ("MOQ"),MPQ("MPQ"), MOV("MOV"),TWENTY_FIVE_PERCENT("25%"),FIFTY_PERCENT("50%"),SEVENTY_FIVE_PERCENT("75%"),
	ONE_HUNDRED_PERCENT("100%"),Exact("Exact"),Limit("Limit"),;

	private String name;

	private QtyIndicatorEnum( String name) {
		this.setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}