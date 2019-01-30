package com.avnet.emasia.webquote.quote.vo;

import java.io.Serializable;

import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.util.QuoteUtil;
import com.avnet.emasia.webquote.utilities.bean.PoiCommonBean;
import com.avnet.emasia.webquote.utilities.bean.QuoteBuilderExcelAlias;
import com.avnet.emasia.webquote.vo.PricerInfo;

public class ShoppingCartLoadItemBean extends PoiCommonBean implements Serializable {
	
	
	private static final long serialVersionUID = -3441679163833448134L;
	
	private int lineSeq;
	//private Integer requestQty;
	
	
	@QuoteBuilderExcelAlias(name="wq.label.mfr",mandatory="true",cellNum="1",fieldLength="-1")
	private String mfr;
	@QuoteBuilderExcelAlias(name="wq.label.reqPN",mandatory="true",cellNum="2",fieldLength="-1")
	private String fullMfrPart;
	@QuoteBuilderExcelAlias(name="wq.label.SoldToCode",mandatory="true",cellNum="3",fieldLength="-1")
	private String soldToCode;
	@QuoteBuilderExcelAlias(name="wq.label.custmrName",mandatory="false",cellNum="4",fieldLength="-1")
	private String soldToParty;
	@QuoteBuilderExcelAlias(name="wq.label.EndCustomerCode",mandatory="false",cellNum="5",fieldLength="-1")
	private String endCustomerCode;
	@QuoteBuilderExcelAlias(name="wq.label.endCust",mandatory="false",cellNum="6",fieldLength="-1")
	private String endCustomerName;
	@QuoteBuilderExcelAlias(name="wq.label.MPQ",mandatory="false",cellNum="7",fieldLength="-1")
	private String mpq;
	@QuoteBuilderExcelAlias(name="wq.label.moq",mandatory="false",cellNum="8",fieldLength="-1")
	private String moq;
	@QuoteBuilderExcelAlias(name="wq.label.reqQty",mandatory="true",cellNum="9",fieldLength="-1")
	private String requestQtyStr;
	@QuoteBuilderExcelAlias(name="wq.label.trgResale",mandatory="false",cellNum="10",fieldLength="-1")
	private String targetResale;
	//@QuoteBuilderExcelAlias(name="wq.label.finalPrice",mandatory="false",cellNum="11",fieldLength="-1")
	private String finalPrice;
	@QuoteBuilderExcelAlias(name="wq.message.orderQty",mandatory="false",cellNum="11",fieldLength="-1")
	private String orderQty;
	@QuoteBuilderExcelAlias(name="wq.label.salesCost",mandatory="false",cellNum="12",fieldLength="-1")
	private String salesCost;
	@QuoteBuilderExcelAlias(name="wq.label.suggestedResale",mandatory="false",cellNum="13",fieldLength="-1")
	private String suggestedResale;
	@QuoteBuilderExcelAlias(name="wq.header.normSellPrice",mandatory="false",cellNum="14",fieldLength="-1")
	private String normalSellPrice;
	@QuoteBuilderExcelAlias(name="wq.label.itmRemark",mandatory="false",cellNum="15",fieldLength="-1")
	private String itemRemarks;
	
	private Customer soldToCustomer;
	
	private Customer endCustomer;
	
	private Manufacturer requestedMfr;
	
	private Material material;
	/*public PricerInfo fillInPricerInfo(final PricerInfo pricerInfo) {
		pricerInfo.setMfr(this.getMfr());
		pricerInfo.setFullMfrPartNumber(this.getFullMfrPart()); 
		pricerInfo.setSoldToCustomer(this.getSoldToCustomer());
		pricerInfo.setEndCustomer(this.getEndCustomer());
		if(!QuoteUtil.isEmpty(this.getRequestQtyStr())) {
			pricerInfo.setRequestedQty(Integer.valueOf(this.getRequestQtyStr()));
		}
		if(!QuoteUtil.isEmpty(this.getRequestQtyStr())) {
			pricerInfo.setRequestedQty(Integer.valueOf(this.getRequestQtyStr()));
		}
		return pricerInfo;
	}*/
	public int getLineSeq() {
		return lineSeq;
	}

	public void setLineSeq(int lineSeq) {
		this.lineSeq = lineSeq;
	}

	/*public Integer getRequestQty() {
		return requestQty;
	}

	public void setRequestQty(Integer requestQty) {
		this.requestQty = requestQty;
	}*/

	public String getMfr() {
		return mfr;
	}

	public void setMfr(String mfr) {
		this.mfr = mfr;
	}

	public String getFullMfrPart() {
		return fullMfrPart;
	}

	public void setFullMfrPart(String fullMfrPart) {
		this.fullMfrPart = fullMfrPart;
	}

	public String getSoldToCode() {
		return soldToCode;
	}

	public void setSoldToCode(String soldToCode) {
		this.soldToCode = soldToCode;
	}

	public String getSoldToParty() {
		return soldToParty;
	}

	public void setSoldToParty(String soldToParty) {
		this.soldToParty = soldToParty;
	}

	public String getEndCustomerCode() {
		return endCustomerCode;
	}

	public void setEndCustomerCode(String endCustomerCode) {
		this.endCustomerCode = endCustomerCode;
	}

	public String getMpq() {
		return mpq;
	}

	public void setMpq(String mpq) {
		this.mpq = mpq;
	}

	public String getMoq() {
		return moq;
	}

	public void setMoq(String moq) {
		this.moq = moq;
	}

	public String getRequestQtyStr() {
		return requestQtyStr;
	}

	public void setRequestQtyStr(String requestQtyStr) {
		this.requestQtyStr = requestQtyStr;
	}

	public String getTargetResale() {
		return targetResale;
	}

	public void setTargetResale(String targetResale) {
		this.targetResale = targetResale;
	}

	public String getFinalPrice() {
		return finalPrice;
	}

	public void setFinalPrice(String finalPrice) {
		this.finalPrice = finalPrice;
	}

	public String getOrderQty() {
		return orderQty;
	}

	public void setOrderQty(String orderQty) {
		this.orderQty = orderQty;
	}

	public String getSalesCost() {
		return salesCost;
	}

	public void setSalesCost(String salesCost) {
		this.salesCost = salesCost;
	}

	public String getSuggestedResale() {
		return suggestedResale;
	}

	public void setSuggestedResale(String suggestedResale) {
		this.suggestedResale = suggestedResale;
	}

	public String getNormalSellPrice() {
		return normalSellPrice;
	}

	public void setNormalSellPrice(String normalSellPrice) {
		this.normalSellPrice = normalSellPrice;
	}

	public String getItemRemarks() {
		return itemRemarks;
	}

	public void setItemRemarks(String itemRemarks) {
		this.itemRemarks = itemRemarks;
	}
	public String getEndCustomerName() {
		return endCustomerName;
	}
	public void setEndCustomerName(String endCustomerName) {
		this.endCustomerName = endCustomerName;
	}
	public Customer getSoldToCustomer() {
		return soldToCustomer;
	}
	public void setSoldToCustomer(Customer soldToCustomer) {
		this.soldToCustomer = soldToCustomer;
	}
	public Customer getEndCustomer() {
		return endCustomer;
	}
	public void setEndCustomer(Customer endCustomer) {
		this.endCustomer = endCustomer;
	}

	public Manufacturer getRequestedMfr() {
		return requestedMfr;
	}

	public void setRequestedMfr(Manufacturer requestedMfr) {
		this.requestedMfr = requestedMfr;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}
	 
}
