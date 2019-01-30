package com.avnet.emasia.webquote.vo;

public class Oqmsp implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8634587793509778025L;
	String oq;
	String msp;

	int startQuantity;
	int endQuantity;
	double price;
	
	public String getOq() {
		return oq;
	}

	public void setOq(String oq) {
		this.oq = oq;
	}

	public String getMsp() {
		return msp;
	}

	public void setMsp(String msp) {
		this.msp = msp;
	}

	public int getStartQuantity() {
		return startQuantity;
	}

	public void setStartQuantity(int startQuantity) {
		this.startQuantity = startQuantity;
	}

	public int getEndQuantity() {
		return endQuantity;
	}

	public void setEndQuantity(int endQuantity) {
		this.endQuantity = endQuantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	

}
