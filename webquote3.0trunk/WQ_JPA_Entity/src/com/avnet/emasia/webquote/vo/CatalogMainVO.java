package com.avnet.emasia.webquote.vo;

import java.io.Serializable;

import com.avnet.emasia.webquote.entity.QuoteItem;

public class CatalogMainVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String rowNO;
	
	private String id;
	
	public String getBuyCurrency() {
		return buyCurrency;
	}

	public void setBuyCurrency(String buyCurrency) {
		this.buyCurrency = buyCurrency;
	}

	private String mfr;
	private String buyCurrency;
	
	private String fullPartNumber;
	
	private String salesCostPart;
	
	private String productGroup;
	
	private String program;
	
	private String orderQty;
	
	private String normalSellPrice;
	
	private String salesCost;
	
	private String suggestedResale;
	
	private String availableSellQty;
	
	private Integer mpq;
	
	private Integer moq;
	
	private String priceValidity;
	
	private String leadTime;
	
	private String allocation;
	
	private String termsAndConditions;
	
	public CatalogMainVO() {
	}
	
	public void fillPriceInfoToQuoteItem(QuoteItem quoteItem) {
		/*quoteItem.setQuotedMfr(quotedMfr);
		quoteItem.setQuotedMaterial(quotedMaterial);
		quoteItem.setPricerId(pricerId);
		
		quoteItem.setAllocationFlag(allocationFlag);
    	if(quoteItem.getAqcc() != null){
    		if(qcComment != null){
    			if(! quoteItem.getAqcc().toUpperCase().contains(qcComment.toUpperCase())){
    				quoteItem.setAqcc(quoteItem.getAqcc() + ";" + qcComment);
    			}
    		}
    	}else{
    		quoteItem.setAqcc(qcComment);
    	}
		quoteItem.setBottomPrice(bottomPrice);
		quoteItem.setCancellationWindow(cancellationWindow);
		quoteItem.setCost(cost);
		quoteItem.setCostIndicator(costIndicator);
		quoteItem.setDescription(description);
		quoteItem.setLeadTime(leadTime);
		quoteItem.setMaterialTypeId(materialTypeId);
		quoteItem.setMinSellPrice(minSell);
		
		quoteItem.setMoq(moq);
		quoteItem.setMov(mov);
		quoteItem.setMpq(mpq);
		
		quoteItem.setMultiUsageFlag(multiUsageFlag);
		
//		quoteItem.setPmoq(null);
//		quoteItem.setPoExpiryDate(poExpiryDate);
		
		quoteItem.setPriceListRemarks1(priceListRemarks1);
		quoteItem.setPriceListRemarks2(priceListRemarks2);
		quoteItem.setPriceListRemarks3(priceListRemarks3);
		quoteItem.setPriceListRemarks4(priceListRemarks4);
    	
		quoteItem.setPriceValidity(priceValidity);
		
		quoteItem.setProductGroup1(productGroup1);
		quoteItem.setProductGroup2(productGroup2);
		quoteItem.setProductGroup3(productGroup3);
		quoteItem.setProductGroup4(productGroup4);
		
		quoteItem.setProgramType(programType);
		quoteItem.setQtyIndicator(qtyIndicator);
		quoteItem.setQuoteExpiryDate(quoteExpiryDate);
		quoteItem.setQuotedMaterial(quotedMaterial);
//		quoteItem.setQuotedQty(null);
		quoteItem.setResaleIndicator(resaleIndicator);
		quoteItem.setRescheduleWindow(reschedulingWindow);
		quoteItem.setSalesCost(salesCost);
		quoteItem.setSalesCostFlag(salesCostFlag);
		quoteItem.setSalesCostType(salesCostType);
		quoteItem.setShipmentValidity(shipmentValidity);
//		quoteItem.setSuggestedResale(null);
		
		quoteItem.setTermsAndConditions(termsAndConditions);
		quoteItem.setQuotationEffectiveDate(displayQuoteEffDate); */
	}

	public String getRowNO() {
		return rowNO;
	}



	public void setRowNO(String rowNO) {
		this.rowNO = rowNO;
	}



	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMfr() {
		return mfr;
	}

	public void setMfr(String mfr) {
		this.mfr = mfr;
	}

	public String getFullPartNumber() {
		return fullPartNumber;
	}

	public void setFullPartNumber(String fullPartNumber) {
		this.fullPartNumber = fullPartNumber;
	}

	public String getSalesCostPart() {
		return salesCostPart;
	}

	public void setSalesCostPart(String salesCostPart) {
		this.salesCostPart = salesCostPart;
	}

	public String getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(String productGroup) {
		this.productGroup = productGroup;
	}

	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	public String getOrderQty() {
		return orderQty;
	}

	public void setOrderQty(String orderQty) {
		this.orderQty = orderQty;
	}

	public String getNormalSellPrice() {
		return normalSellPrice;
	}

	public void setNormalSellPrice(String normalSellPrice) {
		this.normalSellPrice = normalSellPrice;
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

	public String getAvailableSellQty() {
		return availableSellQty;
	}

	public void setAvailableSellQty(String availableSellQty) {
		this.availableSellQty = availableSellQty;
	}

	

	public Integer getMpq() {
		return mpq;
	}



	public void setMpq(Integer mpq) {
		this.mpq = mpq;
	}



	public Integer getMoq() {
		return moq;
	}



	public void setMoq(Integer moq) {
		this.moq = moq;
	}



	public String getPriceValidity() {
		return priceValidity;
	}

	public void setPriceValidity(String priceValidity) {
		this.priceValidity = priceValidity;
	}

	public String getLeadTime() {
		return leadTime;
	}

	public void setLeadTime(String leadTime) {
		this.leadTime = leadTime;
	}

	public String getAllocation() {
		return allocation;
	}

	public void setAllocation(String allocation) {
		this.allocation = allocation;
	}

	public String getTermsAndConditions() {
		return termsAndConditions;
	}

	public void setTermsAndConditions(String termsAndConditions) {
		this.termsAndConditions = termsAndConditions;
	}

}
