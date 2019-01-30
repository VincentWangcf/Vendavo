package com.avnet.emasia.webquote.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.avnet.emasia.webquote.vo.PricerInfo;
import com.avnet.emasia.webquote.vo.RfqItemVO;

@Entity
public class SalesCostPricer extends QtyBreakPricer implements Serializable {
	
	private static final long serialVersionUID = 1L;

	
	@Column(name="SALES_COST_TYPE")
	@Enumerated(EnumType.STRING)
	private SalesCostType salesCostType;

	@Column(name="QTY_CONTROL")
	private Integer qtyControl;
	
	public SalesCostType getSalesCostType() {
		return salesCostType;
	}

	public void setSalesCostType(SalesCostType salesCostType) {
		this.salesCostType = salesCostType;
	}

	public Integer getQtyControl() {
		return qtyControl;
	}

	public void setQtyControl(Integer qtyControl) {
		this.qtyControl = qtyControl;
	}

	@Override
	int getPriority() {
		return 2;
	}


	/*
	@Override
	boolean isSalesCostTypeMatched() {
		if (this.salesCostType.equals(SalesCostType.ZINM)) {
			return false;
		}
		MaterialRegional materialRegional = getMaterial().getMaterialRegaional(getBizUnitBean().getName());
		if (materialRegional != null) {
			return materialRegional.isSalesCostFlag(); 
		}
		return false;
	}
	*/
	
	@Override
	boolean isSalesCostTypeMatched(boolean salesCostType) {
		
		if (this.salesCostType.equals(SalesCostType.ZINM)) {
			return false;
		}
		return salesCostType;
	}

	
	@Override
	void fillInPricer(RfqItemVO rfqItem) {
		
		super.fillInPricer(rfqItem);
//		rfqItem.setResaleIndicator("00AA");
		
		rfqItem.setSalesCostType(salesCostType);
//		rfqItem.calAfterFillin();

	}


	@Override
	public void fillupPricerInfo(PricerInfo pricerInfo) {

		super.fillupPricerInfo(pricerInfo);
		pricerInfo.setPricerType("SalesCost");
		
		if (salesCostType.equals(SalesCostType.ZBMP)) {
			pricerInfo.setSalesCostType(SalesCostType.ZINC);
		} else if (salesCostType.equals(SalesCostType.ZBMD)) {
			pricerInfo.setSalesCostType(SalesCostType.ZIND);
		}
		
//		pricerInfo.setSalesCostType(salesCostType);
//		pricerInfo.setResaleIndicator("00AA");
		
		
//		super.applyCostIndicator(pricerInfo);
//		pricerInfo.calQty();
	}
	


	@Override
	Object calPrice(QuantityBreakPrice quantityBreakPrice) {
		return quantityBreakPrice.getSalesCost();
	}	

}