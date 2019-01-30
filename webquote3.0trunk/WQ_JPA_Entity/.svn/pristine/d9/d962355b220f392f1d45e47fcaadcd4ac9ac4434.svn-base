package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;


/**
 * The persistent class for the AUDIT_QUOTE_ITEM database table.
 * 
 */
@Entity
@Table(name="AUDIT_QUOTE_ITEM")
public class AuditQuoteItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private AuditQuoteItemPK id;
	
	@Column(name="QUOTE_NUMBER", nullable=false)
	private String quoteNumber;
	
	@Column(name="FORM_NUMBER", nullable=false)
	private String formNumber;

	@Column(name="BIZ_UNIT", nullable=false)
	private String bizUnit;
	
	@Column(name="STAGE_OLD", nullable=false)
	private String stageOld;

	@Column(name="STAGE_NEW", nullable=false)
	private String stageNew;

	@Column(name="STATUS_OLD", nullable=false)
	private String statusOld;

	@Column(name="STATUS_NEW", nullable=false)
	private String statusNew;

	@Column(name="QUOTED_MFR_OLD", nullable=false)
	private String quotedMfrOld;

	@Column(name="QUOTED_MFR_NEW", nullable=false)
	private String quotedMfrNew;

	@Column(name="QUOTED_MFR_NAME_OLD", nullable=false)
	private String quotedMfrNameOld;

	@Column(name="QUOTED_MFR_NAME_NEW", nullable=false)
	private String quotedMfrNameNew;

	@Column(name="QUOTED_PART_NUMBER_OLD", nullable=false)
	private String quotedPartNumberOld;

	@Column(name="QUOTED_PART_NUMBER_NEW", nullable=false)
	private String quotedPartNumberNew;

	@Column(name="QUOTED_QTY_OLD", nullable=false)
	private Integer quotedQtyOld;

	@Column(name="QUOTED_QTY_NEW", nullable=false)
	private Integer quotedQtyNew;

	@Column(name="PMOQ_OLD", nullable=false)
	private String pmoqOld;

	@Column(name="PMOQ_NEW", nullable=false)
	private String pmoqNew;

	@Column(name="QTY_INDICATOR_OLD", nullable=false)
	private String qtyIndicatorOld;

	@Column(name="QTY_INDICATOR_NEW", nullable=false)
	private String qtyIndicatorNew;

	@Column(name="COST_INDICATOR_OLD", nullable=false)
	private String costIndicatorOld;

	@Column(name="COST_INDICATOR_NEW", nullable=false)
	private String costIndicatorNew;

	@Column(name="COST_OLD", nullable=false)
	private Double costOld;

	@Column(name="COST_NEW", nullable=false)
	private Double costNew;

	@Column(name="QUOTED_PRICE_OLD", nullable=false)
	private Double quotedPriceOld;

	@Column(name="QUOTED_PRICE_NEW", nullable=false)
	private Double quotedPriceNew;
	
	@Column(name="LEAD_TIME_OLD", nullable=false)
	private String leadTimeOld;

	@Column(name="LEAD_TIME_NEW", nullable=false)
	private String leadTimeNew;

	@Column(name="MPQ_OLD", nullable=false)
	private Integer mpqOld;

	@Column(name="MPQ_NEW", nullable=false)
	private Integer mpqNew;

	@Column(name="MOQ_OLD", nullable=false)
	private Integer moqOld;

	@Column(name="MOQ_NEW", nullable=false)
	private Integer moqNew;

	@Column(name="MOV_OLD", nullable=false)
	private Integer movOld;

	@Column(name="MOV_NEW", nullable=false)
	private Integer movNew;

	@Column(name="PRICE_VALIDITY_OLD", nullable=false)
	private String priceValidityOld;

	@Column(name="PRICE_VALIDITY_NEW", nullable=false)
	private String priceValidityNew;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="SHIPMENT_VALIDITY_OLD", nullable=false)
	private Date shipmentValidityOld;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="SHIPMENT_VALIDITY_NEW", nullable=false)
	private Date shipmentValidityNew;

	@Column(name="UPDATE_BY", nullable=false)
	private String updateBy;

	@Column(name="UPDATE_BY_NAME", nullable=false)
	private String updateByName;

	@Column(name="UPDATE_BY_ID", nullable=false)
	private String updateById;

	@Column(name="RESALE_INDICATOR_OLD", nullable=false)
	private String resaleIndicatorOld;

	@Column(name="RESALE_INDICATOR_NEW", nullable=false)
	private String resaleIndicatorNew;
 
	 
	
	@Column(name="CUSTOMER_TYPE_OLD")
	private String customerTypeOld;
	
	@Column(name="CUSTOMER_TYPE_NEW")
	private String customerTypeNew;
	
	@Column(name="MATERIAL_TYPE_NEW")
	private String materialTypeNew;
	
	@Column(name="MATERIAL_TYPE_OLD")
	private String materialTypeOld;
	
	@Column(name="DESIGN_IN_CAT_OLD")
	private String designInCatOld;
	
	@Column(name="DESIGN_IN_CAT_NEW")
	private String designInCatNew;
	
	@Column(name="QUOTE_TYPE_NEW")
	private String quoteTypNew;
	
	@Column(name="QUOTE_TYPE_OLD")
	private String quoteTypOld;
	
	public String getQuoteTypNew() {
		return quoteTypNew;
	}

	public void setQuoteTypNew(String quoteTypNew) {
		this.quoteTypNew = quoteTypNew;
	}

	public String getQuoteTypOld() {
		return quoteTypOld;
	}

	public void setQuoteTypOld(String quoteTypOld) {
		this.quoteTypOld = quoteTypOld;
	}

	public String getDesignInCatOld() {
		return designInCatOld;
	}

	public void setDesignInCatOld(String designInCatOld) {
		this.designInCatOld = designInCatOld;
	}

	public String getDesignInCatNew() {
		return designInCatNew;
	}

	public void setDesignInCatNew(String designInCatNew) {
		this.designInCatNew = designInCatNew;
	}

	public String getMaterialTypeNew() {
		return materialTypeNew;
	}

	public void setMaterialTypeNew(String materialTypeNew) {
		this.materialTypeNew = materialTypeNew;
	}

	public String getMaterialTypeOld() {
		return materialTypeOld;
	}

	public void setMaterialTypeOld(String materialTypeOld) {
		this.materialTypeOld = materialTypeOld;
	}

	public String getCustomerTypeOld() {
		return customerTypeOld;
	}

	public void setCustomerTypeOld(String customerTypeOld) {
		this.customerTypeOld = customerTypeOld;
	}

	public String getCustomerTypeNew() {
		return customerTypeNew;
	}

	public void setCustomerTypeNew(String customerTypeNew) {
		this.customerTypeNew = customerTypeNew;
	}
 

	public AuditQuoteItemPK getId() {
		return id;
	}

	public void setId(AuditQuoteItemPK id) {
		this.id = id;
	}

	public String getStageOld() {
		return stageOld;
	}

	public void setStageOld(String stageOld) {
		this.stageOld = stageOld;
	}

	public String getStageNew() {
		return stageNew;
	}

	public void setStageNew(String stageNew) {
		this.stageNew = stageNew;
	}

	public String getStatusOld() {
		return statusOld;
	}

	public void setStatusOld(String statusOld) {
		this.statusOld = statusOld;
	}

	public String getStatusNew() {
		return statusNew;
	}

	public void setStatusNew(String statusNew) {
		this.statusNew = statusNew;
	}

	public String getQuotedMfrOld() {
		return quotedMfrOld;
	}

	public void setQuotedMfrOld(String quotedMfrOld) {
		this.quotedMfrOld = quotedMfrOld;
	}

	public String getQuotedMfrNew() {
		return quotedMfrNew;
	}

	public void setQuotedMfrNew(String quotedMfrNew) {
		this.quotedMfrNew = quotedMfrNew;
	}

	public String getQuotedPartNumberOld() {
		return quotedPartNumberOld;
	}

	public void setQuotedPartNumberOld(String quotedPartNumberOld) {
		this.quotedPartNumberOld = quotedPartNumberOld;
	}

	public String getQuotedPartNumberNew() {
		return quotedPartNumberNew;
	}

	public void setQuotedPartNumberNew(String quotedPartNumberNew) {
		this.quotedPartNumberNew = quotedPartNumberNew;
	}

	public Integer getQuotedQtyOld() {
		return quotedQtyOld;
	}

	public void setQuotedQtyOld(Integer quotedQtyOld) {
		this.quotedQtyOld = quotedQtyOld;
	}

	public Integer getQuotedQtyNew() {
		return quotedQtyNew;
	}

	public void setQuotedQtyNew(Integer quotedQtyNew) {
		this.quotedQtyNew = quotedQtyNew;
	}

	public String getPmoqOld() {
		return pmoqOld;
	}

	public void setPmoqOld(String pmoqOld) {
		this.pmoqOld = pmoqOld;
	}

	public String getPmoqNew() {
		return pmoqNew;
	}

	public void setPmoqNew(String pmoqNew) {
		this.pmoqNew = pmoqNew;
	}

	public String getQtyIndicatorOld() {
		return qtyIndicatorOld;
	}

	public void setQtyIndicatorOld(String qtyIndicatorOld) {
		this.qtyIndicatorOld = qtyIndicatorOld;
	}

	public String getQtyIndicatorNew() {
		return qtyIndicatorNew;
	}

	public void setQtyIndicatorNew(String qtyIndicatorNew) {
		this.qtyIndicatorNew = qtyIndicatorNew;
	}

	public String getCostIndicatorOld() {
		return costIndicatorOld;
	}

	public void setCostIndicatorOld(String costIndicatorOld) {
		this.costIndicatorOld = costIndicatorOld;
	}

	public String getCostIndicatorNew() {
		return costIndicatorNew;
	}

	public void setCostIndicatorNew(String costIndicatorNew) {
		this.costIndicatorNew = costIndicatorNew;
	}

	public Double getCostOld() {
		return costOld;
	}

	public void setCostOld(Double costOld) {
		this.costOld = costOld;
	}

	public Double getCostNew() {
		return costNew;
	}

	public void setCostNew(Double costNew) {
		this.costNew = costNew;
	}

	public String getLeadTimeOld() {
		return leadTimeOld;
	}

	public void setLeadTimeOld(String leadTimeOld) {
		this.leadTimeOld = leadTimeOld;
	}

	public String getLeadTimeNew() {
		return leadTimeNew;
	}

	public void setLeadTimeNew(String leadTimeNew) {
		this.leadTimeNew = leadTimeNew;
	}

	public Integer getMpqOld() {
		return mpqOld;
	}

	public void setMpqOld(Integer mpqOld) {
		this.mpqOld = mpqOld;
	}

	public Integer getMpqNew() {
		return mpqNew;
	}

	public void setMpqNew(Integer mpqNew) {
		this.mpqNew = mpqNew;
	}

	public Integer getMoqOld() {
		return moqOld;
	}

	public void setMoqOld(Integer moqOld) {
		this.moqOld = moqOld;
	}

	public Integer getMoqNew() {
		return moqNew;
	}

	public void setMoqNew(Integer moqNew) {
		this.moqNew = moqNew;
	}

	public Integer getMovOld() {
		return movOld;
	}

	public void setMovOld(Integer movOld) {
		this.movOld = movOld;
	}

	public Integer getMovNew() {
		return movNew;
	}

	public void setMovNew(Integer movNew) {
		this.movNew = movNew;
	}

	public String getPriceValidityOld() {
		return priceValidityOld;
	}

	public void setPriceValidityOld(String priceValidityOld) {
		this.priceValidityOld = priceValidityOld;
	}

	public String getPriceValidityNew() {
		return priceValidityNew;
	}

	public void setPriceValidityNew(String priceValidityNew) {
		this.priceValidityNew = priceValidityNew;
	}

	public Date getShipmentValidityOld() {
		return shipmentValidityOld;
	}

	public void setShipmentValidityOld(Date shipmentValidityOld) {
		this.shipmentValidityOld = shipmentValidityOld;
	}

	public Date getShipmentValidityNew() {
		return shipmentValidityNew;
	}

	public void setShipmentValidityNew(Date shipmentValidityNew) {
		this.shipmentValidityNew = shipmentValidityNew;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public String getResaleIndicatorOld() {
		return resaleIndicatorOld;
	}

	public void setResaleIndicatorOld(String resaleIndicatorOld) {
		this.resaleIndicatorOld = resaleIndicatorOld;
	}

	public String getResaleIndicatorNew() {
		return resaleIndicatorNew;
	}

	public void setResaleIndicatorNew(String resaleIndicatorNew) {
		this.resaleIndicatorNew = resaleIndicatorNew;
	}

	public String getQuoteNumber() {
		return quoteNumber;
	}

	public void setQuoteNumber(String quoteNumber) {
		this.quoteNumber = quoteNumber;
	}

	public String getFormNumber() {
		return formNumber;
	}

	public void setFormNumber(String formNumber) {
		this.formNumber = formNumber;
	}

	public String getBizUnit() {
		return bizUnit;
	}

	public void setBizUnit(String bizUnit) {
		this.bizUnit = bizUnit;
	}

	public String getQuotedMfrNameOld() {
		return quotedMfrNameOld;
	}

	public void setQuotedMfrNameOld(String quotedMfrNameOld) {
		this.quotedMfrNameOld = quotedMfrNameOld;
	}

	public String getQuotedMfrNameNew() {
		return quotedMfrNameNew;
	}

	public void setQuotedMfrNameNew(String quotedMfrNameNew) {
		this.quotedMfrNameNew = quotedMfrNameNew;
	}

	public Double getQuotedPriceOld() {
		return quotedPriceOld;
	}

	public void setQuotedPriceOld(Double quotedPriceOld) {
		this.quotedPriceOld = quotedPriceOld;
	}

	public Double getQuotedPriceNew() {
		return quotedPriceNew;
	}

	public void setQuotedPriceNew(Double quotedPriceNew) {
		this.quotedPriceNew = quotedPriceNew;
	}

	public String getUpdateByName() {
		return updateByName;
	}

	public void setUpdateByName(String updateByName) {
		this.updateByName = updateByName;
	}

	public String getUpdateById() {
		return updateById;
	}

	public void setUpdateById(String updateById) {
		this.updateById = updateById;
	}

	
	
}