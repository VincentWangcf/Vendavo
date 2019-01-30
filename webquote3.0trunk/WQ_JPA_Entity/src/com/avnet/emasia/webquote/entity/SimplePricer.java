package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Entity
public class SimplePricer extends Pricer implements Serializable{

	private static final long serialVersionUID = 1L;

	
	@JoinColumn(name="PROGRAM_TYPE_ID")
	private ProgramType programType;
	
	@Column(name="DISPLAY_ON_TOP")
	private Integer displayOnTop;
	
	@Column(name="PROGRAM_SPECIAL_ITEM_FLAG")
	private Integer specialItemFlag;
	
	@Column(name="PROGRAM_AVAILABLE_TO_SELL_QTY")
	private Integer availableToSellQty;
	
	@Column(name="QTY_CONTROL")
	private Integer qtyControl;
	
	@OneToMany(mappedBy="materialDetail", cascade={CascadeType.PERSIST, CascadeType.REMOVE})
	private List<QuantityBreakPrice> quantityBreakPrices;

	@Column(name="VENDOR_QUOTE_NUMBER")
	private String vendorQuoteNumber;

	@Column(name="VENDOR_QUOTE_QUANTITY")
	private Integer vendorQuoteQuantity;


	
	public Boolean getAqFlag() {
		return false;
	}

	public ProgramType getProgramType() {
		return programType;
	}
	
	public void setProgramType(ProgramType programType) {
		this.programType = programType;
	}

	public Integer getDisplayOnTop() {
		return displayOnTop;
	}

	public void setDisplayOnTop(Integer displayOnTop) {
		this.displayOnTop = displayOnTop;
	}

	public Integer getSpecialItemFlag() {
		return specialItemFlag;
	}

	public void setSpecialItemFlag(Integer specialItemFlag) {
		this.specialItemFlag = specialItemFlag;
	}

	public Integer getAvailableToSellQty() {
		return availableToSellQty;
	}

	public void setAvailableToSellQty(Integer availableToSellQty) {
		this.availableToSellQty = availableToSellQty;
	}

	public Integer getQtyControl() {
		return qtyControl;
	}

	public void setQtyControl(Integer qtyControl) {
		this.qtyControl = qtyControl;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<QuantityBreakPrice> getQuantityBreakPrices() {
		List<QuantityBreakPrice> sorted = new ArrayList<QuantityBreakPrice>(quantityBreakPrices);
		Collections.sort(sorted,
				new Comparator(){
					 @Override
					 public int compare(Object o1, Object o2) 
					 {
						 int sortByAgeFlag=((QuantityBreakPrice)o1).getQuantityBreak().compareTo(((QuantityBreakPrice)o2).getQuantityBreak());
						 return sortByAgeFlag;
					 }
				}
	    );
		return sorted;
	}


	public void setQuantityBreakPrices(List<QuantityBreakPrice> quantityBreakPrices) {
		this.quantityBreakPrices = quantityBreakPrices;
	}

	public String getVendorQuoteNumber() {
		return vendorQuoteNumber;
	}

	public void setVendorQuoteNumber(String vendorQuoteNumber) {
		this.vendorQuoteNumber = vendorQuoteNumber;
	}

	public Integer getVendorQuoteQuantity() {
		return vendorQuoteQuantity;
	}

	public void setVendorQuoteQuantity(Integer vendorQuoteQuantity) {
		this.vendorQuoteQuantity = vendorQuoteQuantity;
	}

	@Override
	int getPriority() {
		return 10;
	}

	@Override
	boolean allowOverride() {
		return true;
	}

	@Override
	boolean isCustomerMatched(Customer soldToCustomer, Customer endCustomer, List<Customer> allowedCustomers) {
		return false;
	}

	@Override
	boolean isRegionMatched(String region) {
		return false;
	}

	@Override
	boolean isSalesCostTypeMatched(boolean salesCostType) {
		return false;
	}
	

}
