package com.avnet.emasia.webquote.dp.xml.requestquote;

import com.avnet.emasia.webquote.entity.Customer;

public class DropDownFilters {
	private Customer soldTo;
	private String team;
	private String designRegion;
	private String mfr;
	private String bmtFlagCode;
	
	public DropDownFilters(Customer soldTo, String team, String designRegion, String mfr, String bmtFlagCode) {
		//super();
		this.soldTo = soldTo;
		this.team = team;
		this.designRegion = designRegion;
		this.mfr = mfr;
		this.bmtFlagCode = bmtFlagCode;
	}
	
	public DropDownFilters(String name1, String name2, String name3, String name4, 
			String team, String designRegion, String mfr, String bmtFlagCode) {
		 
		this.soldTo = new Customer();
		this.soldTo.setCustomerName1(name1);
		this.soldTo.setCustomerName2(name2);
		this.soldTo.setCustomerName3(name3);
		this.soldTo.setCustomerName4(name4);
		this.team = team;
		this.designRegion = designRegion;
		this.mfr = mfr;
		this.bmtFlagCode = bmtFlagCode;
	}
	/*public DropDownFilters(String soldTo, String team, String designRegion, String mfr, String bmtFlagCode) {
		super();
		this.soldTo = soldTo;
		this.team = team;
		this.designRegion = designRegion;
		this.mfr = mfr;
		this.bmtFlagCode = bmtFlagCode;
	}*/
	/*@Override
	public boolean equals(Object obj) {
		if(obj == this) return true;
		if(obj instanceof DropDownFilters ) {
			DropDownFilters o = (DropDownFilters)obj;
			
		}
		return false;
	}*/
	public Customer getSoldTo() {
		return soldTo;
	}
	public void setSoldTo(Customer soldTo) {
		this.soldTo = soldTo;
	}
	public String getTeam() {
		return team;
	}
	public void setTeam(String team) {
		this.team = team;
	}
	public String getDesignRegion() {
		return designRegion;
	}
	public void setDesignRegion(String designRegion) {
		this.designRegion = designRegion;
	}
	public String getMfr() {
		return mfr;
	}
	public void setMfr(String mfr) {
		this.mfr = mfr;
	}
	public String getBmtFlagCode() {
		return bmtFlagCode;
	}
	public void setBmtFlagCode(String bmtFlagCode) {
		this.bmtFlagCode = bmtFlagCode;
	}
}
