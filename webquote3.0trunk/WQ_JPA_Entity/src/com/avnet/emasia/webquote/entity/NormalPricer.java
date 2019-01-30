package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import com.avnet.emasia.webquote.vo.PricerInfo;
import com.avnet.emasia.webquote.vo.RfqItemVO;


/**
 * The persistent class for the MATERIAL_DETAIL database table.
 * 
 */
@Entity
//@DiscriminatorValue("NormalPricer")
public class NormalPricer extends Pricer implements Serializable {
	private static final long serialVersionUID = 1L;


	@Column(name="FULL_MFR_PART_NUMBER", length=40)
	private String fullMfrPartNumber;


	@Column(name="BOTTOM_PRICE", precision=10, scale=5)
	private Double bottomPrice;

	@Column(name="MIN_SELL_PRICE", precision=10, scale=5)
	private Double minSellPrice;

	/*@Embedded
	@AttributeOverrides(value = {
			@javax.persistence.AttributeOverride(name = "amount", column = @Column(name = "MIN_SELL_PRICE")),
	        @javax.persistence.AttributeOverride(name = "currency", column = @Column(name = "CURRENCY"))})
	private Money minSellPriceM;*/
	
	@Column(name="PRICE_LIST_REMARKS1", length=255)
	private String priceListRemarks1;

	@Column(name="PRICE_LIST_REMARKS2", length=255)
	private String priceListRemarks2;

	@Column(name="PRICE_LIST_REMARKS3", length=255)
	private String priceListRemarks3;

	@Column(name="PRICE_LIST_REMARKS4", length=255)
	private String priceListRemarks4;

	@Column(name = "PART_DESCRIPTION")
	private String partDescription;


	public NormalPricer() {
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getFullMfrPartNumber() {
		return this.fullMfrPartNumber;
	}

	public void setFullMfrPartNumber(String fullMfrPartNumber) {
		this.fullMfrPartNumber = fullMfrPartNumber;
	}

	public Double getBottomPrice() {
		return this.bottomPrice;
	}

	public void setBottomPrice(Double bottomPrice) {
		this.bottomPrice = bottomPrice;
	}

	public Double getMinSellPrice() {
		return this.minSellPrice;
	}

	public void setMinSellPrice(Double minSellPrice) {
		this.minSellPrice = minSellPrice;
	}

	/*public Money getMinSellPriceM() {
		return minSellPriceM;
	}


	public void setMinSellPriceM(Money minSellPriceM) {
		this.minSellPriceM = minSellPriceM;
	}*/


	public String getPriceListRemarks1() {
		return this.priceListRemarks1;
	}

	public void setPriceListRemarks1(String priceListRemarks1) {
		this.priceListRemarks1 = priceListRemarks1;
	}

	public String getPriceListRemarks2() {
		return this.priceListRemarks2;
	}

	public void setPriceListRemarks2(String priceListRemarks2) {
		this.priceListRemarks2 = priceListRemarks2;
	}

	public String getPriceListRemarks3() {
		return this.priceListRemarks3;
	}

	public void setPriceListRemarks3(String priceListRemarks3) {
		this.priceListRemarks3 = priceListRemarks3;
	}

	public String getPriceListRemarks4() {
		return this.priceListRemarks4;
	}

	public void setPriceListRemarks4(String priceListRemarks4) {
		this.priceListRemarks4 = priceListRemarks4;
	}

	public String getPartDescription() {
		return partDescription;
	}


	public void setPartDescription(String partDescription) {
		this.partDescription = partDescription;
	}

	 
	@Override
	public String toString() {
		return "MaterialDetail [costIndicator=" + getCostIndicator()
				+ ", fullMfrPartNumber=" + fullMfrPartNumber + ", bizUnitBean=" + 
			getBizUnitBean() + ", material=" + getMaterial() + "]";
	}

	@Override
	int getPriority() {
		return 3;
	}

	@Override
	void fillInPricer(RfqItemVO rfqItem) {
		super.fillInPricer(rfqItem);
		rfqItem.setBottomPrice(bottomPrice);
		rfqItem.setDescription(partDescription);
		rfqItem.setMinSellPrice(minSellPrice);
		
		rfqItem.setPriceListRemarks1(priceListRemarks1);
		rfqItem.setPriceListRemarks2(priceListRemarks2);
		rfqItem.setPriceListRemarks3(priceListRemarks3);
		rfqItem.setPriceListRemarks4(priceListRemarks4);
		
//		rfqItem.calAfterFillin();
		
	}
	
	@Override
	public void fillupPricerInfo(PricerInfo pricerInfo) {
		super.fillupPricerInfo(pricerInfo);
		pricerInfo.setBottomPrice(bottomPrice);
		pricerInfo.setDescription(partDescription);
		pricerInfo.setMinSell(minSellPrice);
		pricerInfo.setPriceListRemarks1(priceListRemarks1);
		pricerInfo.setPriceListRemarks2(priceListRemarks2);
		pricerInfo.setPriceListRemarks3(priceListRemarks3);
		pricerInfo.setPriceListRemarks4(priceListRemarks4);
		pricerInfo.setPricerType("Normal");
		
//		super.applyCostIndicator(pricerInfo);
//		pricerInfo.calQty();
	}
	 
 

	public String getCostIndicatorType() {
		return RFQ_COST_INDICATOR_TYPE_N;
	}



	@Override
	boolean allowOverride() {
		return true;
	}



	@Override
	boolean isCustomerMatched(Customer soldToCustomer, Customer endCustomer, List<Customer> allowedCustomers) {
		return true;
	}



	/*@Override
	boolean isSalesCostTypeMatched() {
		MaterialRegional materialRegional = getMaterial().getMaterialRegaional(getBizUnitBean().getName());
		if (materialRegional != null) {
			return ! materialRegional.isSalesCostFlag(); 
		}
		return false;
	}*/
	
	@Override
	boolean isSalesCostTypeMatched(boolean salesCostType) {
		return ! salesCostType;
	}
	
	public Money getMinSellAsMoney() {
		return Money.of(minSellPrice, this.getCurrency());
	}

}