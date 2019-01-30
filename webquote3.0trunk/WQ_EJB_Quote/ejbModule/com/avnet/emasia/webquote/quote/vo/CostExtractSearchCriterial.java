package com.avnet.emasia.webquote.quote.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.avnet.emasia.webquote.entity.DataAccess;
import com.avnet.emasia.webquote.utilities.bean.CostExtractionExcelAlias;
import com.avnet.emasia.webquote.utilities.bean.PricerExcelAlias;

public class CostExtractSearchCriterial implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<DataAccess> dataAccesses;
	
	@CostExtractionExcelAlias(mandatory="false",fieldLength="-1" ,name="wq.label.region",cellNum="1")
	private String region;
	
	@CostExtractionExcelAlias(mandatory="false",fieldLength="-1" ,name="wq.label.salesCostPart",cellNum="2")
	private String saleCostPart;
	
	@CostExtractionExcelAlias(mandatory="false",fieldLength="-1" ,name="wq.label.mfr",cellNum="3")
	private String mfr;
	
	@CostExtractionExcelAlias(mandatory="false",fieldLength="-1" ,name="wq.label.mfrPN",cellNum="4")
	private String partNumber;
	
	@CostExtractionExcelAlias(mandatory="false",fieldLength="-1" ,name="wq.label.cost",cellNum="5")
	private double cost;
	
	@CostExtractionExcelAlias(mandatory="false",fieldLength="-1" ,name="wq.label.costsource",cellNum="6")
	private String costSource;
	
	@CostExtractionExcelAlias(mandatory="false",fieldLength="-1" ,name="wq.label.costIndicator",cellNum="7")
	private String costIndicator;
	
	@CostExtractionExcelAlias(mandatory="false",fieldLength="-1" ,name="wq.label.priceValidity",cellNum="8")
	private Date quoteEffectiveDate;
	
	@CostExtractionExcelAlias(mandatory="false",fieldLength="-1" ,name="wq.label.mpq",cellNum="9")
	private String mpq;
	
	@CostExtractionExcelAlias(mandatory="false",fieldLength="-1" ,name="wq.label.moq",cellNum="10")
	private String moq;
	
//	private String[] selectedRegions;
//	
//	private String[] selectedCostSoures;
//	
//	private String[] selectedMfrs;
//	
//
//	
//	private String[] selectedCostIndicators;
	
	private List<String> selectedRegions = new ArrayList<String>();	
	private List<String> selectedCostSoures = new ArrayList<String>();;
	private List<String> selectedMfrs = new ArrayList<String>();
	private List<String> selectedCostIndicators = new ArrayList<String>();
	
	
	
	@CostExtractionExcelAlias(mandatory="false",fieldLength="-1" ,name="wq.label.proGroup1",cellNum="11")
	private String productGroup1;
	
	@CostExtractionExcelAlias(mandatory="false",fieldLength="-1" ,name="wq.label.proGroup2",cellNum="12")
	private String productGroup2;
	
	@CostExtractionExcelAlias(mandatory="false",fieldLength="-1" ,name="wq.label.proGroup3",cellNum="13")
	private String productGroup3;
	
	@CostExtractionExcelAlias(mandatory="false",fieldLength="-1" ,name="wq.label.proGroup4",cellNum="14")
	private String productGroup4;
	
	private Date quoteEffectiveDateFrom;
	
	private Date quoteEffectiveDateTo;
	
	@CostExtractionExcelAlias(mandatory="false",fieldLength="-1" ,name="wq.label.SoldToCode",cellNum="15")
	private String soldToCode;
	
	@CostExtractionExcelAlias(mandatory="false",fieldLength="-1" ,name="wq.label.soldToName",cellNum="16")
	private String soldToName;
	
	@CostExtractionExcelAlias(mandatory="false",fieldLength="-1" ,name="wq.label.Qty",cellNum="17")
	private int quotedQty;
	
	private Date quoteEffectiveDate3MthFrom;
	private Date quoteEffectiveDate3MthTo;
	private Date quoteEffectiveDate6MthFrom;
	private Date quoteEffectiveDate6MthTo;
	private Date quoteEffectiveDate12MthFrom;
	private Date quoteEffectiveDate12MthTo;
	
	private List<String> mfrAndPartNo;
	private Map<String, CostComparisonBean> costComparisonBean;
	
	
	public CostExtractSearchCriterial(String region, double cost ){
		this.region = region;
		this.cost = cost;
		
	}
	
	public CostExtractSearchCriterial(){

	}

//	public String[] getSelectedRegions() {
//		return selectedRegions;
//	}
//
//	public void setSelectedRegions(String[] selectedRegions) {
//		this.selectedRegions = selectedRegions;
//	}
//
//	public String[] getSelectedCostSoures() {
//		return selectedCostSoures;
//	}
//
//	public void setSelectedCostSoures(String[] selectedCostSoures) {
//		this.selectedCostSoures = selectedCostSoures;
//	}
//
//	public String[] getSelectedMfrs() {
//		return selectedMfrs;
//	}
//
//	public void setSelectedMfrs(String[] selectedMfrs) {
//		this.selectedMfrs = selectedMfrs;
//	}

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	

//	public String[] getSelectedCostIndicators() {
//		return selectedCostIndicators;
//	}
//
//	public void setSelectedCostIndicators(String[] selectedCostIndicators) {
//		this.selectedCostIndicators = selectedCostIndicators;
//	}

	public String getSaleCostPart() {
		return saleCostPart;
	}

	public void setSaleCostPart(String saleCostPart) {
		this.saleCostPart = saleCostPart;
	}

	public String getProductGroup1() {
		return productGroup1;
	}

	public void setProductGroup1(String productGroup1) {
		this.productGroup1 = productGroup1;
	}

	public String getProductGroup2() {
		return productGroup2;
	}

	public void setProductGroup2(String productGroup2) {
		this.productGroup2 = productGroup2;
	}

	public String getProductGroup3() {
		return productGroup3;
	}

	public void setProductGroup3(String productGroup3) {
		this.productGroup3 = productGroup3;
	}

	public String getProductGroup4() {
		return productGroup4;
	}

	public void setProductGroup4(String productGroup4) {
		this.productGroup4 = productGroup4;
	}

	public Date getQuoteEffectiveDateFrom() {
		return quoteEffectiveDateFrom;
	}

	public void setQuoteEffectiveDateFrom(Date quoteEffectiveDateFrom) {
		this.quoteEffectiveDateFrom = quoteEffectiveDateFrom;
	}

	public Date getQuoteEffectiveDateTo() {
		return quoteEffectiveDateTo;
	}

	public void setQuoteEffectiveDateTo(Date quoteEffectiveDateTo) {
		this.quoteEffectiveDateTo = quoteEffectiveDateTo;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getCostSource() {
		return costSource;
	}

	public void setCostSource(String costSource) {
		this.costSource = costSource;
	}

	public String getMfr() {
		return mfr;
	}

	public void setMfr(String mfr) {
		this.mfr = mfr;
	}

	public String getCostIndicator() {
		return costIndicator;
	}

	public void setCostIndicator(String costIndicator) {
		this.costIndicator = costIndicator;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

//	public String getQuoteEffectiveDate() {
//		return quoteEffectiveDate;
//	}
//
//	public void setQuoteEffectiveDate(String quoteEffectiveDate) {
//		this.quoteEffectiveDate = quoteEffectiveDate;
//	}

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

	public String getSoldToCode() {
		return soldToCode;
	}

	public void setSoldToCode(String soldToCode) {
		this.soldToCode = soldToCode;
	}

	public String getSoldToName() {
		return soldToName;
	}

	public void setSoldToName(String soldToName) {
		this.soldToName = soldToName;
	}

	public int getQuotedQty() {
		return quotedQty;
	}

	public void setQuotedQty(int quotedQty) {
		this.quotedQty = quotedQty;
	}

	public List<DataAccess> getDataAccesses() {
		return dataAccesses;
	}

	public void setDataAccesses(List<DataAccess> dataAccesses) {
		this.dataAccesses = dataAccesses;
	}

	public Date getQuoteEffectiveDate3MthFrom() {
		return quoteEffectiveDate3MthFrom;
	}

	public void setQuoteEffectiveDate3MthFrom(Date quoteEffectiveDate3MthFrom) {
		this.quoteEffectiveDate3MthFrom = quoteEffectiveDate3MthFrom;
	}

	public Date getQuoteEffectiveDate3MthTo() {
		return quoteEffectiveDate3MthTo;
	}

	public void setQuoteEffectiveDate3MthTo(Date quoteEffectiveDate3MthTo) {
		this.quoteEffectiveDate3MthTo = quoteEffectiveDate3MthTo;
	}

	public Date getQuoteEffectiveDate6MthFrom() {
		return quoteEffectiveDate6MthFrom;
	}

	public void setQuoteEffectiveDate6MthFrom(Date quoteEffectiveDate6MthFrom) {
		this.quoteEffectiveDate6MthFrom = quoteEffectiveDate6MthFrom;
	}

	public Date getQuoteEffectiveDate6MthTo() {
		return quoteEffectiveDate6MthTo;
	}

	public void setQuoteEffectiveDate6MthTo(Date quoteEffectiveDate6MthTo) {
		this.quoteEffectiveDate6MthTo = quoteEffectiveDate6MthTo;
	}

	public Date getQuoteEffectiveDate12MthFrom() {
		return quoteEffectiveDate12MthFrom;
	}

	public void setQuoteEffectiveDate12MthFrom(Date quoteEffectiveDate12MthFrom) {
		this.quoteEffectiveDate12MthFrom = quoteEffectiveDate12MthFrom;
	}

	public Date getQuoteEffectiveDate12MthTo() {
		return quoteEffectiveDate12MthTo;
	}

	public void setQuoteEffectiveDate12MthTo(Date quoteEffectiveDate12MthTo) {
		this.quoteEffectiveDate12MthTo = quoteEffectiveDate12MthTo;
	}

	public List<String> getMfrAndPartNo() {
		return mfrAndPartNo;
	}

	public void setMfrAndPartNo(List<String> mfrAndPartNo) {
		this.mfrAndPartNo = mfrAndPartNo;
	}

	public void setQuoteEffectiveDate(Date quoteEffectiveDate) {
		this.quoteEffectiveDate = quoteEffectiveDate;
	}

	public Date getQuoteEffectiveDate() {
		return quoteEffectiveDate;
	}
	
	public String getKey(){
		return this.mfr + this.partNumber;
	}

	public Map<String, CostComparisonBean> getCostComparisonBean() {
		return costComparisonBean;
	}

	public void setCostComparisonBean(Map<String, CostComparisonBean> costComparisonBean) {
		this.costComparisonBean = costComparisonBean;
	}

	public List<String> getSelectedRegions() {
		return selectedRegions;
	}

	public void setSelectedRegions(List<String> selectedRegions) {
		this.selectedRegions = selectedRegions;
	}

	public List<String> getSelectedCostSoures() {
		return selectedCostSoures;
	}

	public void setSelectedCostSoures(List<String> selectedCostSoures) {
		this.selectedCostSoures = selectedCostSoures;
	}

	public List<String> getSelectedMfrs() {
		return selectedMfrs;
	}

	public void setSelectedMfrs(List<String> selectedMfrs) {
		this.selectedMfrs = selectedMfrs;
	}

	public List<String> getSelectedCostIndicators() {
		return selectedCostIndicators;
	}

	public void setSelectedCostIndicators(List<String> selectedCostIndicators) {
		this.selectedCostIndicators = selectedCostIndicators;
	}

}
