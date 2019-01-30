package com.avnet.emasia.webquote.entity;

import java.text.DateFormat;
import java.text.ParseException;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.apache.commons.lang.StringUtils;

import com.avnet.emasia.webquote.vo.PricerInfo;
import com.avnet.emasia.webquote.vo.RfqItemVO;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@Table(name="PRICER")
@DiscriminatorColumn(name="PRICER_TYPE")
public abstract class Pricer {
	
	protected static final Logger logger = Logger.getLogger(Pricer.class.getName());
	
	@Id
	@SequenceGenerator(name = "MATERIAL_DETAIL_ID_GENERATOR", sequenceName = "WQ_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MATERIAL_DETAIL_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private long id;
	
	@Column(name="AVNET_QC_COMMENTS")
	private String avnetQcComments;	

	@Column(precision = 10, scale = 5)
	private Double cost;
	
	@Column(name="CURRENCY", length=5, nullable = false)
	@Enumerated(EnumType.STRING)
	private Currency currency;
	
	/*@Embedded
	@AttributeOverrides(value = {
			@javax.persistence.AttributeOverride(name = "amount", column = @Column(name = "COST")),
	        @javax.persistence.AttributeOverride(name = "currency", column = @Column(name = "CURRENCY"))})
	private Money costM;*/
	
	@Column(name="PRICER_TYPE")
	private String pricerType;
	
	@ManyToOne
	@JoinColumn(name = "COST_INDICATOR")
	private CostIndicator costIndicator;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_ON", nullable = false)
	private Date createdOn;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_UPDATED_ON")
	private Date lastUpdatedOn;
	
	@Column(name="LEAD_TIME", length=30)
	private String leadTime;

	@Column(precision = 10, scale = 3)
	private Integer moq;
	
	@Column(precision = 10, scale = 3)
	private Integer mpq;

	@Column(precision=11, scale=2)
	private Integer mov;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SHIPMENT_VALIDITY")
	private Date shipmentValidity;
	
	@Column(name = "TERMS_AND_CONDITIONS", length = 255)
	private String termsAndConditions;
	
	@Column(name = "PRICE_VALIDITY", length = 10)
	private String priceValidity;
	
	@Column(name = "\"VERSION\"")
	@Version
	private Integer version;
	
	@Column(name = "LAST_UPDATED_BY", length = 10)
	private String lastUpdatedBy;
	
	@Column(name = "CREATED_BY", nullable = false, length = 10)
	private String createdBy;
	
	@ManyToOne
	@JoinColumn(name = "BIZ_UNIT", nullable = false)
	private BizUnit bizUnitBean;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "MATERIAL_ID", nullable = false)
	private Material material;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "QUOTATION_EFFECTIVE_DATE")
	private Date quotationEffectiveDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "QUOTATION_EFFECTIVE_TO")
	private Date quotationEffectiveTo;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DISPLAY_QUOTE_EFF_DATE", length = 7)
	private Date displayQuoteEffDate;
	
	@Column(name="BATCH_STATUS")
	private int batchStatus;
	

	public int getBatchStatus() {
		return batchStatus;
	}


	public void setBatchStatus(int batchStatus) {
		this.batchStatus = batchStatus;
	}


	public Pricer() {
	}

	
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPricerType() {
		return pricerType;
	}


	public void setPricerType(String pricerType) {
		this.pricerType = pricerType;
	}


	public Double getCost() {
		return this.cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	/*public Money getCostM() {
		return costM;
	}
	
	public void setCostM(Money costM) {
		this.costM = costM;
	}
	*/


	public Currency getCurrency() {
		return currency;
	}


	public void setCurrency(Currency currency) {
		this.currency = currency;
	}


	


	public CostIndicator getCostIndicator() {
		return costIndicator;
	}

	public void setCostIndicator(CostIndicator costIndicator) {
		this.costIndicator = costIndicator;
	}

	public Date getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getLastUpdatedOn() {
		return this.lastUpdatedOn;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public String getLeadTime() {
		return this.leadTime;
	}

	public void setLeadTime(String leadTime) {
		this.leadTime = leadTime;
	}
	
	public Integer getMov() {
		return this.mov;
	}

	public void setMov(Integer mov) {
		this.mov = mov;
	}
	
	public Integer getMoq() {
		return this.moq;
	}

	public void setMoq(Integer moq) {
		this.moq = moq;
	}

	public Integer getMpq() {
		return this.mpq;
	}

	public void setMpq(Integer mpq) {
		this.mpq = mpq;
	}

	public Date getShipmentValidity() {
		return this.shipmentValidity;
	}

	public void setShipmentValidity(Date shipmentValidity) {
		this.shipmentValidity = shipmentValidity;
	}

	public String getPriceValidity() {
		return priceValidity;
	}

	public void setPriceValidity(String priceValidity) {
		this.priceValidity = priceValidity;
	}

	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public BizUnit getBizUnitBean() {
		return this.bizUnitBean;
	}

	public void setBizUnitBean(BizUnit bizUnitBean) {
		this.bizUnitBean = bizUnitBean;
	}

	public Material getMaterial() {
		return this.material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public Date getQuotationEffectiveTo() {
		return quotationEffectiveTo;
	}

	public void setQuotationEffectiveTo(Date quotationEffectiveTo) {
		this.quotationEffectiveTo = quotationEffectiveTo;
	}

	public Date getDisplayQuoteEffDate() {
		return this.displayQuoteEffDate;
	}

	public void setDisplayQuoteEffDate(Date displayQuoteEffDate) {
		this.displayQuoteEffDate = displayQuoteEffDate;
	}
	
	public String getTermsAndConditions() {
		return termsAndConditions;
	}

	public void setTermsAndConditions(String termsAndConditions) {
		this.termsAndConditions = termsAndConditions;
	}

	public Date getQuotationEffectiveDate() {
		return quotationEffectiveDate;
	}

	public void setQuotationEffectiveDate(Date quotationEffectiveDate) {
		this.quotationEffectiveDate = quotationEffectiveDate;
	}
	
	public String getAvnetQcComments() {
		return avnetQcComments;
	}

	public void setAvnetQcComments(String avnetQcComments) {
		this.avnetQcComments = avnetQcComments;
	}
	
	
	void fillInPricer(RfqItemVO rfqItem){
		logger.log(Level.INFO, "Pricer[" + id + "] is used");
		rfqItem.setPricerId(id);
		
		rfqItem.setAllocationFlag(false);
		rfqItem.setAqFlag(true);
		rfqItem.setQcComment(avnetQcComments);
		//bottomPrice, Only normal pricer has
		//cancellationWindow, set in ManufacturerDetail
		rfqItem.setCost(cost);
		rfqItem.setCostIndicator(costIndicator != null ? costIndicator.getName(): null);
		//description, AQ null;
		rfqItem.setLeadTime(leadTime);
		//materialTypeId, set in Material
		//minSellPrice, set in sub class. Only normal pricer has	
		rfqItem.setMoq(moq);
		rfqItem.setMov(mov);
		rfqItem.setMpq(mpq);
		//multiUsage, get from MfrDetail, override in subclass
		//rfqItem.setPmoq(calPmoq(rfqItem.getRequiredQtyAsInt()));
		rfqItem.setPoExpiryDate(calPriceValidityDate());
		//price list remark 1, all set to null in AQ;
		//price list remark 2, all set to null in AQ;
		//price list remark 3, all set to null in AQ;
		//price list remark 4, all set to null in AQ;
		rfqItem.setPriceValidity(priceValidity);
		//productGroup1, all set in MaterialRegional
		//productGroup2, all set in MaterialRegional
		//productGroup3, all set in MaterialRegional
		//productGroup4, all set in MaterialRegional
		//programType, set in sub class.
		//qtyIndicator, complex logic, refer to QtyLogic 
		rfqItem.setQuoteExpiryDate(calQuoteExpiryDate());
		//rfqItem.setQuotedQty(calQuotedQty(rfqItem.getRequiredQtyAsInt()));, complex logic
		//resaleIndicator set in ManufacturerDetail and override in subclass
		//reschedule window, set by ManufacturerDetail
		//salesCost, set in SalesCostPricer
		//salesCostFlag, set in MaterialRegional
		rfqItem.setSalesCostType(SalesCostType.NonSC);//override in SalesCostPricer
		rfqItem.setShipmentValidity(shipmentValidity);
		//suggestedResalle set in SalesCostPricer
		rfqItem.setTermsAndCondition(termsAndConditions);
		
		//fixed by Damonchen@20180412,For defect#259,284
		rfqItem.setQuotaionEffectiveDate(displayQuoteEffDate);
		/***David**/
		rfqItem.setBuyCurr(currency.toString());
		//call after fill buyCurr;
		rfqItem.calExRate();
		//rfqItem.setMaterial(getMaterial());
	}
	
	public void fillupPricerInfo(PricerInfo pricerInfo) {
		logger.log(Level.INFO, "Pricer[" + id + "] is used");
		pricerInfo.setPricerId(id);
		pricerInfo.setQuotationEffectiveDate(quotationEffectiveDate);

		pricerInfo.setAllocationFlag(false);
		pricerInfo.setQcComment(avnetQcComments);
		//bottomPrice, Only normal pricer has
		//cancellationWindow, set in ManufacturerDetail
		pricerInfo.setCost(cost);
		pricerInfo.setCostIndicator(costIndicator != null ? costIndicator.getName(): null);
		//DESCRIPTION, only NormalPricer SubClass has description, fill in cost indicator rule
		pricerInfo.setLeadTime(leadTime);
		//materialTypeId, set in Material
		//minSellPrice, set in sub class. Only normal pricer has	
		pricerInfo.setMoq(moq);
		pricerInfo.setMov(mov);
		pricerInfo.setMpq(mpq);
		//multiUsage, get from MfrDetail, override in subclass
		//pmoq, set to null for all pricer
		pricerInfo.setPoExpiryDate(calPriceValidityDate());
		//PRICE_LIST_REMARKS_1, only NormalPricer has,fill in cost indicator rule
		//PRICE_LIST_REMARKS_2, only NormalPricer has,fill in cost indicator rule
		//PRICE_LIST_REMARKS_3, only NormalPricer has,fill in cost indicator rule
		//PRICE_LIST_REMARKS_4, only NormalPricer has,fill in cost indicator rule
		pricerInfo.setPriceValidity(priceValidity);
		//productGroup1, set in MaterialRegional
		//productGroup2, set in MaterialRegional
		//productGroup3, set in MaterialRegional
		//productGroup4, set in MaterialRegional
		//programType, set in sub class.
		//pricerInfo.setQtyIndicator(calQtyIndicator()); complex logic
		pricerInfo.setQuoteExpiryDate(calQuoteExpiryDate());
		//qty indicator, set by ManufacturerDetail, override in SubClass
		//quoted qty, set as null;
		//resale indicator, set in ManufacturerDetail and override in SubClass
		//reschedule window, set by ManufacturerDetail
		//salesCost, set in SalesCostPricer
		//salesCostFlag, set in MaterialRegional
		pricerInfo.setSalesCostType(SalesCostType.NonSC);//override in SalesCostPricer
		pricerInfo.setShipmentValidity(shipmentValidity);
		//SUGGESTED_RESALE, set in SalesCostPricer 
		pricerInfo.setTermsAndConditions(termsAndConditions);
		
		
//		pricerInfo.setQuotationEffectiveDate(quotationEffectiveDate);
		//fixed by Damonchen@20180412,For defect#259,284
		pricerInfo.setDisplayQuoteEffDate(displayQuoteEffDate);	
		
		pricerInfo.setBuyCurr(currency.toString());
		//call after fill buyCurr;
		pricerInfo.calExRate();
	}
	
	
	 public boolean isEffectivePricer(){
//		Date date = getCurrentDateZeroHour();
		Date date = new Date();
		return (quotationEffectiveDate != null  && ! quotationEffectiveDate.after(date))
				&& (quotationEffectiveTo != null && ! quotationEffectiveTo.before(date));
	}
	 
	boolean isFuturePricer() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.DATE, 1);
		if (quotationEffectiveDate != null  && quotationEffectiveDate.after(cal.getTime())) {
			return true;
		} else {
			return false;
		}
	}
	
	Date getCurrentDateZeroHour() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static final String A_BOOK_COST = "A-Book Cost";

	boolean isABookCostPricer() {
		return costIndicator != null && StringUtils.equalsIgnoreCase(A_BOOK_COST, costIndicator.getName()) && (this instanceof NormalPricer);
	}
	
	abstract int getPriority();
	
	abstract boolean allowOverride();
	
	abstract boolean isCustomerMatched(Customer soldToCustomer, Customer endCustomer, List<Customer> allowedCustomers);
	
	// tell if this pricer's salesCostFlag match the MaterialRegional's salesCostFlag definition 
//	abstract boolean isSalesCostTypeMatched();
	
	// tell if this pricer's salesCostFlag match the input parameter  
	abstract boolean isSalesCostTypeMatched(boolean salesCostType);
	
	boolean isRegionMatched(String region) {
		return bizUnitBean.getName().equalsIgnoreCase(region);
//		return bizUnitBean.getSelfAndAllSubBizUnits().contains(region);
		
	}
	
	boolean isRegionCustomerTypeMatched(String region, Customer soldToCustomer, Customer endCustomer, List<Customer> allowedCustomers, boolean salesCostFlag) {
		return isRegionMatched(region) && isCustomerMatched(soldToCustomer, endCustomer, allowedCustomers) && isSalesCostTypeMatched(salesCostFlag);
	}

	
	public static final String RFQ_COST_INDICATOR_TYPE_N = "N";
	public static final String RFQ_COST_INDICATOR_TYPE_C = "C";
	public static final String RFQ_COST_INDICATOR_TYPE_P = "P";

	/*
	//Override in QtyBreakPricer
	String calQtyIndicator() {
		if(mov != null && cost != null && cost != 0 && moq != null){
			if (mov/cost > moq) {
				return "MOV";
			} else {
				return "MOQ";
			}
		} else if ((mov == null || cost == null || cost == 0) && moq != null) {
			return "MOQ";
		} else if (mov != null && cost != null && cost != 0 && moq == null){
			return "MOV";
		} else {
			return null;
		}
	}	
	
	
	Integer calMoq() {
		if (calQtyIndicator().equalsIgnoreCase("MOQ")) {
			return moq;
		} else if (calQtyIndicator().equalsIgnoreCase("MOV")) {
			return roundUp((int)Math.ceil(mov / cost));
		}
		return null;
	}
	
	
	//Override in QtyBreakPrice
	Integer calQuotedQty(Integer requiredQty) {
		if (requiredQty == null || calMoq() == null) {
			return null;
		}
		if (requiredQty < calMoq()) {
			return roundUp(calMoq());
		} else {
			return roundUp(requiredQty);
		}
	}
	*/
		
	//For AQ only, Override in QtyBreakPricer
	void fillPmoq(RfqItemVO rfqItem) {
		
		if (StringUtils.isEmpty(rfqItem.getQtyIndicator())) {
			return ;
		}
		
		if (rfqItem.getQtyIndicator().equalsIgnoreCase("MOQ") || rfqItem.getQtyIndicator().equalsIgnoreCase("MOV")) {
			if (rfqItem.getQuotedQty() != null) {
				rfqItem.setPmoq(rfqItem.getQuotedQty() + "+");
			}
		} else if (rfqItem.getQtyIndicator().equalsIgnoreCase("MPQ")) {
			rfqItem.setPmoq(rfqItem.getMpq() + "+");
		} else if (rfqItem.getQtyIndicator().contains("%")) {
			try {
				rfqItem.setPmoq(roundUp((int)Math.ceil(rfqItem.getQuotedQty() * 
						Double.parseDouble(rfqItem.getQtyIndicator().substring(0, rfqItem.getQtyIndicator().length()-1)) / 100)) + "+");
			} catch (Exception e) {
				//to do
				e.printStackTrace();
			}
			
		} else if (rfqItem.getQtyIndicator().equalsIgnoreCase("EXACT")) {
			//to do, need roundUp?
			rfqItem.setPmoq(String.valueOf(rfqItem.getRequiredQty()));
			
		} 
	}
	
	/*
	public String calPmoq(Integer requiredQty, Integer quotedQty, Integer mpq, String qtyIndicator) {
		
		String pmoq = null;
		
		if (StringUtils.isEmpty(qtyIndicator)) {
			return pmoq;
		}
		
		if (qtyIndicator.equalsIgnoreCase("MOQ") || qtyIndicator.equalsIgnoreCase("MOV")) {
			if (quotedQty != null) {
				pmoq = quotedQty + "+";
			}
		} else if (qtyIndicator.equalsIgnoreCase("MPQ")) {
			pmoq = mpq + "+";
		} else if (qtyIndicator.contains("%")) {
			try {
				pmoq = roundUp((int)Math.ceil(quotedQty * Double.parseDouble(qtyIndicator.substring(0, qtyIndicator.length()-1)) / 100)) + "+";
			} catch (Exception e) {
				//to do
				e.printStackTrace();
			}
			
		} else if (qtyIndicator.equalsIgnoreCase("EXACT")) {
			//to do, need roundUp?
			pmoq = String.valueOf(requiredQty);
			
		} 
		return pmoq;
	}
	*/
	
	
	Date calPriceValidityDate() {
		if (priceValidity == null) {
			return null;
		}
		
		if (StringUtils.isNumeric(priceValidity)) {
			Calendar calendar = Calendar.getInstance();
//			calendar.set(Calendar.HOUR, 0);
//			calendar.set(Calendar.MINUTE, 0);
//			calendar.set(Calendar.SECOND, 0);
//			calendar.set(Calendar.MILLISECOND, 0);
			calendar.add(Calendar.DATE, Integer.parseInt(priceValidity));
			return calendar.getTime();
		} 
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy"); 
		try {
			Date date =  format.parse(priceValidity);
			return date;
		} catch (ParseException e) {
			//to do
			e.printStackTrace();
		}
		
		return null;
	}
	
	//override in QtyBreakPricer 
	Date calQuoteExpiryDate() {
		if (calPriceValidityDate() == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(calPriceValidityDate());
		calendar.add(Calendar.DATE, -4);
		return calendar.getTime();
	}
	
	
	Integer roundUp(Integer qty){
		if(qty == null || mpq == null || mpq == 0) {
			return null;
		}
		return ((int)Math.ceil(qty * 1.0d / mpq)) * mpq;
	}

	Integer roundDown(Integer qty){
		if(qty == null || mpq == null || mpq == 0) {
			return null;
		}
		if (qty % mpq == 0) {
			return (qty - mpq) < 0 ? 0 : (qty - mpq);
		} else {
			return ((int)Math.floor(qty * 1.0d / mpq) * mpq);
		}
		
	}
	
	public Money getCostAsMoney() {
		return Money.of(cost, currency);
	}
	/*
	Integer calQuotedQty(Integer requiredQty) {
		
		if (requiredQty == null || calQtyIndicator() == null) {
			return null;
		}
		
		if (calQtyIndicator().equalsIgnoreCase("MOQ")) {
			if (getMoq() != null) {
				return calQuotedQty(requiredQty, getMoq(), null, null);
			} else if (getMov() != null && getCost() != null && getMpq() != null) {
				return calQuotedQty(requiredQty, null, getMov(), getCost());
			}
			
		} else if (calQtyIndicator().equalsIgnoreCase("MOV")) {
			if (getMov() != null && getCost() != null && getMpq() != null) {
				return calQuotedQty(requiredQty, null, getMov(), getCost());
			}
			
		} else if (calQtyIndicator().equalsIgnoreCase("EXACT")) {
			return requiredQty;
			
		} else if (calQtyIndicator().equalsIgnoreCase("LIMIT")) {
			return roundUp(requiredQty);

		} else if (calQtyIndicator().contains("%") || calQtyIndicator().equalsIgnoreCase("MPQ")) {
			return  calQuotedQty(requiredQty, getMoq(), getMov(), getCost());
			
		}
		return null;
	}
	
	private Integer calQuotedQty(Integer requiredQty, Integer moq, Integer mov, Double cost) {
		if (mov == null || cost == null || cost==0d) {
			if (moq == null) {
				return null;
			} else if (moq > requiredQty) {
				return moq;
			} else {
				return roundUp(requiredQty);
			}
		} else {
			if (moq == null) {
				if (mov/cost > requiredQty) {
					return roundUp((int)Math.ceil(getMov()/getCost()));
				} else {
					return roundUp(requiredQty);
				}
			} else {
				if (moq >= mov/cost && moq >= requiredQty) {
					return moq;
				} else if (mov/cost >= moq && mov/cost >= requiredQty) {
					return roundUp((int)Math.ceil(getMov()/getCost()));
				} else if (requiredQty >= moq && requiredQty >= mov/cost) {
					return roundUp(requiredQty);
				}
			}
			
		}
		return null;
	}
	*/

}