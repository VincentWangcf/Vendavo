package com.avnet.emasia.webquote.vo;

public class ProgramMaterialQtyRange implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -670882907761108433L;
	private int qtyFrom;
	private int qtyTo;
	private double price;
	private double priceToleranceFrom;
	private double priceToleranceTo;
	private boolean callForQuote;
	
	public ProgramMaterialQtyRange(){
		
	}	

	public ProgramMaterialQtyRange(int qtyFrom, int qtyTo, double price, double priceToleranceFrom,
			double priceToleranceTo, boolean callForQuote){
		this.qtyFrom = qtyFrom;
		this.qtyTo = qtyTo;
		this.price = price;
		this.priceToleranceFrom = priceToleranceFrom;
		this.priceToleranceTo = priceToleranceTo;
		this.callForQuote = callForQuote;
		
	}
	
	
	public boolean isQtyInRange(int qty){
		if(callForQuote){
			return false;
		}
		
		if(qty >= qtyFrom && qty <= qtyTo) {
			return true;
		}
		return false;
	}	
	public boolean isQtyAndPriceInRange(int qty, double price){
		if(callForQuote){
			return false;
		}
		
		if((qty >= qtyFrom && qty <= qtyTo) && (price >= priceToleranceFrom && price <= priceToleranceTo)){
			return true;
		}
		return false;
	}

	
	@Override
	public String toString(){
		return "callForQuote:" + callForQuote + ", qtyFrom:" + qtyFrom + ", qtyTo:" + qtyTo + ", price:" + price;
		
	}
	
	
	public int getQtyFrom() {
		return qtyFrom;
	}
	public void setQtyFrom(int qtyFrom) {
		this.qtyFrom = qtyFrom;
	}
	public int getQtyTo() {
		return qtyTo;
	}
	public void setQtyTo(int qtyTo) {
		this.qtyTo = qtyTo;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public double getPriceToleranceFrom() {
		return priceToleranceFrom;
	}
	public void setPriceToleranceFrom(double priceToleranceFrom) {
		this.priceToleranceFrom = priceToleranceFrom;
	}
	public double getPriceToleranceTo() {
		return priceToleranceTo;
	}
	public void setPriceToleranceTo(double priceToleranceTo) {
		this.priceToleranceTo = priceToleranceTo;
	}
	public boolean isCallForQuote() {
		return callForQuote;
	}
	public void setCallForQuote(boolean callForQuote) {
		this.callForQuote = callForQuote;
	}


}
