package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.avnet.emasia.webquote.constants.Constants;
import com.avnet.emasia.webquote.constants.StageEnum;
import com.avnet.emasia.webquote.vo.PricerInfo;
import com.avnet.emasia.webquote.vo.RfqItemVO;



@Entity
@Table(name="MATERIAL")
public class Material implements Serializable {
	private static final Logger logger = Logger.getLogger(Material.class.getName());
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="MATERIAL_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MATERIAL_ID_GENERATOR")
	@Column(unique=true, nullable=false)
	private long id;

	@Column(name="ALLOCATION_FLAG", length=1)
	private Boolean allocationFlag;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_ON", nullable=false)
	private Date createdOn;

	@Column(name="DELETION_FLAG", length=1)
	private Boolean deletionFlag;

	@Column(length=45)
	private String description;

	@Column(name="FULL_MFR_PART_NUMBER", length=40)
	private String fullMfrPartNumber;

	@Column(name="PACKAGE_TYPE", length=100)
	private String packageType;
	
	@Column(name="PACKAGING_TYPE", length=100)
	private String packagingType;
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_UPDATED_ON")
	private Date lastUpdatedOn;

	@Column(name="MATERIAL_TYPE", length=10)
	private String materialType;

	@ManyToOne
	@JoinColumn(name="MANUFACTURER_ID")	
	private Manufacturer manufacturer;


	@Column(name="SAP_PART_NUMBER", length=18)
	private String sapPartNumber;

	@Column(name="SPECIAL_MATERIAL", length=4)
	private String specialMaterial;//If the SPECIAL_MATERIAL is   0001  , it means that it is special material.

	@Column(name = "CREATED_BY", nullable = false, length = 10)
	private String createdBy;

	@Column(name = "LAST_UPDATED_BY", length = 10)
	private String lastUpdatedBy;

	@OneToMany(mappedBy="material",cascade=CascadeType.ALL)
	private List<Pricer> pricers;
	
	@OneToMany(mappedBy="material")
	private List<MaterialRegional> materialRegionals;
	

	//introduced this variable in INC0253990 
	//used for determine if a material is program material
	//according to Victor Tam, need consider program effective date, but not price validity
	//pay attention to the diff with variable isProgramPart, which consider program effective date and price validity
	@Transient
	private boolean programMaterial;
	
	@Column(name="VALID", length=1)
	private Boolean valid;
			
	@Column(name = "HAS_SAP_FLAG", precision = 1, scale = 0)
	private Boolean hasSapFlag;
	
	@OneToMany(mappedBy="material",fetch = FetchType.LAZY)
	private List<SapMaterial> sapMaterials = new ArrayList<SapMaterial>(0);

	
	@Transient
	private boolean isProgramPart;
	
	@Transient
	private boolean hasPricers;
	
	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public Material() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Boolean getAllocationFlag() {
		return this.allocationFlag;
	}

	public void setAllocationFlag(Boolean allocationFlag) {
		this.allocationFlag = allocationFlag;
	}

	public Date getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Boolean getDeletionFlag() {
		return this.deletionFlag;
	}

	public void setDeletionFlag(Boolean deletionFlag) {
		this.deletionFlag = deletionFlag;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFullMfrPartNumber() {
		return this.fullMfrPartNumber;
	}

	public void setFullMfrPartNumber(String fullMfrPartNumber) {
		this.fullMfrPartNumber = fullMfrPartNumber;
	}


	public String getPackageType() {
		return packageType;
	}

	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}

	public String getPackagingType() {
		return packagingType;
	}

	public void setPackagingType(String packagingType) {
		this.packagingType = packagingType;
	}

	public Date getLastUpdatedOn() {
		return this.lastUpdatedOn;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public String getMaterialType() {
		return this.materialType;
	}

	public void setMaterialType(String materialType) {
		this.materialType = materialType;
	}
	
	public String getSpecialMaterial() {
		return specialMaterial;
	}

	public void setSpecialMaterial(String specialMaterial) {
		this.specialMaterial = specialMaterial;
	}
	/*public String getProductHierarchy() {
		return this.productHierarchy;
	}

	public void setProductHierarchy(String productHierarchy) {
		this.productHierarchy = productHierarchy;
	}
*/
	/*
	public String getSalesOrg() {
		return this.salesOrg;
	}

	public void setSalesOrg(String salesOrg) {
		this.salesOrg = salesOrg;
	}

	public String getSalesPlant() {
		return this.salesPlant;
	}

	public void setSalesPlant(String salesPlant) {
		this.salesPlant = salesPlant;
	}
	*/

	public String getSapPartNumber() {
		return this.sapPartNumber;
	}

	public void setSapPartNumber(String sapPartNumber) {
		this.sapPartNumber = sapPartNumber;
	}

	public String getCreatedBy()
	{
		return createdBy;
	}

	public void setCreatedBy(String createdBy)
	{
		this.createdBy = createdBy;
	}

	public String getLastUpdatedBy()
	{
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy)
	{
		this.lastUpdatedBy = lastUpdatedBy;
	}

	/*public ProductCategory getProductCategory() {
		return this.productCategory;
	}

	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}*/

	/*
	public ProductGroup getProductGroup2() {
		return this.productGroup2;
	}

	public void setProductGroup2(ProductGroup productGroup2) {
		this.productGroup2 = productGroup2;
	}
	*/

	public Manufacturer getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(Manufacturer manufacturer) {
		this.manufacturer = manufacturer;
	}

	/*public String getxDistrChainStatus() {
		return xDistrChainStatus;
	}

	public void setxDistrChainStatus(String xDistrChainStatus) {
		this.xDistrChainStatus = xDistrChainStatus;
	}*/

	/*public String getSapProductCategory() {
		return sapProductCategory;
	}

	public void setSapProductCategory(String sapProductCategory) {
		this.sapProductCategory = sapProductCategory;
	}*/

 	public void setProgramMaterial(boolean programMaterial) {
		this.programMaterial = programMaterial;
	}

	
 	public boolean isProgramMaterial() {
		return programMaterial;
	}

	/*
	public String getSapMaterialType() {
		return sapMaterialType;
	}

	public void setSapMaterialType(String sapMaterialType) {
		this.sapMaterialType = sapMaterialType;
	}
	*/

	@Override
	public String toString() {
		return "Material [fullMfrPartNumber=" + fullMfrPartNumber
				+ ", manufacturer="
				+ manufacturer + ", sapPartNumber ,"+ sapPartNumber+ "]";
	}

	/*
	public ProductGroup getProductGroup1() {
		return productGroup1;
	}

	public void setProductGroup1(ProductGroup productGroup1) {
		this.productGroup1 = productGroup1;
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
	*/

	public Boolean getHasSapFlag() {
		return hasSapFlag;
	}

	public void setHasSapFlag(Boolean hasSapFlag){
		this.hasSapFlag = hasSapFlag;
	}

	public List<SapMaterial> getSapMaterials(){
		return sapMaterials;
	}

	public void setSapMaterials(List<SapMaterial> sapMaterials)	{
		this.sapMaterials = sapMaterials;
	}

	public boolean isProgramPart()	{
		return isProgramPart;
	}
	@Transient
	public void setProgramPart(boolean isProgramPart){
		this.isProgramPart = isProgramPart;
	}
	
	public MaterialRegional getMaterialRegaional(String region) {
		
		return materialRegionals
			.stream()
			.filter(materialRegional -> materialRegional.getBizUnit().getName().equalsIgnoreCase(region))
			.findFirst()
			.orElse(null);
		
	}

	public boolean isHasPricers() {
		return hasPricers;
	}

	public void setHasPricers(boolean hasPricers) {
		this.hasPricers = hasPricers;
	}

	public List<Pricer> getPricers() {
		return pricers;
	}

	public void setPricers(List<Pricer> pricers) {
		this.pricers = pricers;
	}
	
	public List<MaterialRegional> getMaterialRegionals() {
		return materialRegionals;
	}

	public void setMaterialRegionals(List<MaterialRegional> materialRegionals) {
		this.materialRegionals = materialRegionals;
	}	
	
	//this method is used only on RFQ submission page - move from step 1 to step 2
	public void applyDefaultCostIndictorLogic(RfqItemVO rfqItem, String region, Customer soldToCustomer, Customer endCustomer, 
			List<Customer> allowedCustomers){
		
		rfqItem.clearPricingInfo();
		MaterialRegional materialRegional = this.getMaterialRegaional(region);
		if (materialRegional == null) {
			logger.log(Level.INFO, "No MaterialRegional found");
			return;
		}
		materialRegional.fillInPricer(rfqItem, RFQSubmissionTypeEnum.NormalRFQSubmission);
		
		List<Pricer> pricers = getCandidatePricers(region, Currency.valueOf(rfqItem.getRfqCurr()), soldToCustomer, endCustomer, allowedCustomers, materialRegional.isSalesCostFlag(), true, rfqItem.getExRateDate());
		
		//TO DO, need remove the transient property hasPricers. 
		if (! pricers.isEmpty()) {
			this.setHasPricers(true);
		}
		
		if(! pricers.isEmpty()) {
			logger.log(Level.INFO, "Fill with default pricer.");
			pricers.get(0).fillInPricer(rfqItem);
			pricers.get(0).getCostIndicator().apply(this.getEffectiveOrExpiredABookCostPricer(region, Currency.valueOf(rfqItem.getRfqCurr()), soldToCustomer, rfqItem.getExRateDate()), rfqItem);
		} 
		logger.info("At the end of applyDefaultCostIndictorLogic. " + rfqItem);

	}
	
	
	public void applyAQLogic(RfqItemVO rfqItem, String region, Customer soldToCustomer, Customer endCustomer, 
			List<Customer> allowedCustomers, RFQSubmissionTypeEnum submissionType){
		
		logger.log(Level.INFO, "applyAQLogic for " + rfqItem);
		rfqItem.clearPricingInfo();
		rfqItem.setQuotedMaterial(this);
		rfqItem.setQuotedPartNumber(fullMfrPartNumber);
		
		MaterialRegional materialRegional = this.getMaterialRegaional(region);
		if (materialRegional == null) {
			rfqItem.setQtyIndicator("MOQ");
			logger.log(Level.INFO, "No MaterialRegional found");
			return;
		}
		materialRegional.fillInPricer(rfqItem, submissionType);
		
		ManufacturerDetail mfrDetail = materialRegional.getManufacturerDetail();
		
		if (mfrDetail != null) {
			mfrDetail.fillIn(rfqItem);
		} else {
			logger.log(Level.INFO, "No MfrDetail found");
			rfqItem.setMultiUsage(true);
			rfqItem.setQtyIndicator("MOQ");
		}
		
		List<Pricer> pricers = getCandidatePricers(region, Currency.valueOf(rfqItem.getRfqCurr()), soldToCustomer, endCustomer, allowedCustomers, rfqItem.isSalesCostFlag(), true, rfqItem.getExRateDate());
		
		//TO DO, need remove the transient property hasPricers. 
		if (! pricers.isEmpty()) {
			this.setHasPricers(true);
		}
		
		if (! hasProgramPricer(pricers)) {
			rfqItem.setMaterialTypeId("NORMAL");
		} else {
			rfqItem.setMaterialTypeId("PROGRAM");
		}
		
		if (! rfqItem.isSpecialPriceIndicator()) {
			for(Pricer pricer : pricers) {
				materialRegional.fillInPricer(rfqItem, submissionType);
				if (mfrDetail != null) {
					mfrDetail.fillIn(rfqItem);
				} else {
					rfqItem.setMultiUsage(true);
				}

				pricer.fillInPricer(rfqItem);
//				rfqItem.calQty();
//				pricer.fillPmoq(rfqItem);
				rfqItem.calAfterFillin();
				
				if (rfqItem.getStage().equals(StageEnum.FINISH.toString())){
					return;
				} else {
					rfqItem.clearPricingInfo();
				}
			}
		}
		
		materialRegional.fillInPricer(rfqItem, submissionType);
		if (mfrDetail != null) {
			mfrDetail.fillIn(rfqItem);
		} else {
			rfqItem.setMultiUsage(true);
		}
		if(! pricers.isEmpty() && (rfqItem.isSpecialPriceIndicator() || ! StageEnum.FINISH.toString().equals(rfqItem.getStage()))  ) {
			logger.log(Level.INFO, "Fill with default pricer.");
			pricers.get(0).fillInPricer(rfqItem);
			pricers.get(0).getCostIndicator().apply(this.getEffectiveOrExpiredABookCostPricer(region, Currency.valueOf(rfqItem.getRfqCurr()), soldToCustomer, rfqItem.getExRateDate()), rfqItem);
			rfqItem.calAfterFillin();
			if (submissionType.equals(RFQSubmissionTypeEnum.ShoppingCartSubmission)) {
				if (rfqItem.isSalesCostFlag()) {
					rfqItem.setStage(StageEnum.PENDING.toString());
				} else {
					rfqItem.setStage(StageEnum.DRAFT.toString());
					rfqItem.setStatus("DQ");
				}
				
			} else {
				rfqItem.setStage(StageEnum.PENDING.toString());
			}
		}
		
		rfqItem.fillWithDefaultValue();
		
		logger.info("At the end of applyAQLogic. " + rfqItem);
	}
	
	public List<Pricer> getCandidatePricersForAQ(String region,Currency rfqCurr, Customer soldToCustomer, Customer endCustomer, List<Customer> allowedCustomers, Date rateDate){
		
		if (getMaterialRegaional(region) ==  null) {
			return  new ArrayList<Pricer>();
		}
		
		return getCandidatePricers(region, rfqCurr, soldToCustomer, endCustomer, allowedCustomers, getMaterialRegaional(region).isSalesCostFlag(), true, rateDate);

	}
	
	private List<Pricer> getCandidatePricers(String region, Currency targetCurrency, Customer soldToCustomer, Customer endCustomer, 
			List<Customer> allowedCustomers, boolean isSalesCostFlag, boolean allowOverrideIsEffective, Date exchangeRateDate ){
		
		if (getMaterialRegaional(region) ==  null) {
			return Collections.emptyList();
		}
		
		List<Pricer> results = pricers.stream()
				.filter(pricer -> pricer.isRegionCustomerTypeMatched(region, soldToCustomer, endCustomer, allowedCustomers, isSalesCostFlag) 
						&& pricer.isEffectivePricer())
			.collect(Collectors.toList());
		
		if (allowOverrideIsEffective) {
			List<Pricer> results2 = results.stream().filter(pricer -> ! pricer.allowOverride()).collect(Collectors.toList());
			
			if (! results2.isEmpty()) {
				results = results2;
			}
		}
		Customer soldTo = soldToCustomer;
		//Customer soldTo = null;
		//soldToCustomer;
		/*Collections.sort(results, (p1, p2) -> {
			if((p1.getCost() > 0 && p2.getCost() > 0) && !(p1.getCost().equals(p2.getCost()))){
				return p1.getCost().compareTo(p2.getCost());
			}else if ((p1.getCost() ==0 || p2.getCost() ==0) && !(p1.getCost().equals(p2.getCost()))){
				return p2.getCost().compareTo(p1.getCost());
			}else{
				if(p1.getPriority() != p2.getPriority()){
					return p1.getPriority() - p2.getPriority();
				}else{
					return p1.getCostIndicator().getName().compareTo(p2.getCostIndicator().getName());
				}
			}
		});*/
		//com.avnet.emasia.webquote.constants.Constants.SCALE_FOR_COMPARE;
		// fiter the null MONEY after convert.
		results = results.stream().filter(p -> {
			try {
				if(p.getCostAsMoney().convertToRfq(targetCurrency, p.getBizUnitBean(), soldTo , 
						exchangeRateDate) == null ) {
					logger.warning("AQ,CostAsMoney after convert is null,will filter out."
							+ MessageFormat.format("pricerID:[{0}], cost:[{1}], buycurrency[{2}];",
									p.getId(), p.getCost(), p.getCurrency()));
					return false;
				}
			} catch(Exception e) {
				logger.severe(e.getMessage());
				return false;
			}
			return true;
		}).collect(Collectors.toList());
		
		Collections.sort(results, (p1, p2) -> {
			//TODO
			Money m1 = p1.getCostAsMoney().convertToRfq(targetCurrency, p1.getBizUnitBean(),
					soldTo, exchangeRateDate, Constants.SCALE_FOR_COMPARE);
			
			Money m2 = p2.getCostAsMoney().convertToRfq(targetCurrency, p2.getBizUnitBean(),
					soldTo, exchangeRateDate, Constants.SCALE_FOR_COMPARE);
			
			Money m0 = Money.of(BigDecimal.ZERO, targetCurrency);
			
			if((m1.compareTo(m0) > 0 && m2.compareTo(m0) > 0) && !(m1.equals(m2))){
				return m1.compareTo(m2);
				//if amount equals 0 then treat it as max, put at the last position of collection. 
			}else if ((m1.equals(m0) ^ m2.equals(m0))){
				return  m2.compareTo(m1);
				//if cost are equal then compare as below;
			}else if (p1.getPriority() != p2.getPriority()){
				return p1.getPriority() - p2.getPriority();
			}else {
				return p1.getCostIndicator().getName().compareTo(p2.getCostIndicator().getName());
			}
			
		});
		
		if (! results.stream().anyMatch(pricer -> pricer.isABookCostPricer())) {
			if (getEffectiveOrExpiredABookCostPricer(region, targetCurrency, soldToCustomer, exchangeRateDate) != null ) {
				results.add(getEffectiveOrExpiredABookCostPricer(region, targetCurrency, soldToCustomer, exchangeRateDate));
			}
		}
		
		return results;	
	}
	
	
	private Pricer getClosestExpiredABookCostPricer(String region, Currency targetCurrency, Customer soldToCustomer, Date exchangeRateDate ) {

		List<Pricer> aBookCostPricers = pricers
				.stream()
				.filter(pricer -> pricer.isRegionMatched(region) 
						&& pricer.isABookCostPricer() 
						&& ((targetCurrency == null || soldToCustomer == null || exchangeRateDate == null) ? 
								true : pricer.getCostAsMoney().convertToRfq(targetCurrency, pricer.getBizUnitBean(),soldToCustomer, exchangeRateDate, Constants.SCALE_FOR_COMPARE)!= null ))
				.collect(Collectors.toList());
		
		if (aBookCostPricers.isEmpty()) {
			return null;
		}
		
		Collections.sort(aBookCostPricers, (pricer1, pricer2)-> {return pricer2.getQuotationEffectiveDate().compareTo(pricer1.getQuotationEffectiveDate());});
		
		
		return aBookCostPricers
				.stream()
				.filter(pricer -> pricer.getQuotationEffectiveTo()!=null && pricer.getQuotationEffectiveTo().before(new Date()))
				.findFirst()
				.orElse(null);
		
	}
	
	private Pricer getEffectiveABookCostPricer(String region, Currency targetCurrency, Customer soldToCustomer, Date exchangeRateDate ) {
		
		return pricers
				.stream()
				.filter(pricer -> (pricer.getBizUnitBean().getName().equalsIgnoreCase(region) 
						&& pricer.isABookCostPricer()
						&& pricer.isEffectivePricer() 
						&& ((targetCurrency == null || soldToCustomer == null || exchangeRateDate == null) ? 
								true : pricer.getCostAsMoney().convertToRfq(targetCurrency, pricer.getBizUnitBean(),soldToCustomer, exchangeRateDate, Constants.SCALE_FOR_COMPARE)!= null)) )
				.findFirst()
				.orElse(null);
		
	}

	
	private NormalPricer getEffectiveOrExpiredABookCostPricer(String region, Currency targetCurrency, Customer soldToCustomer, Date exchangeRateDate) {
		if (getEffectiveABookCostPricer(region, targetCurrency, soldToCustomer, exchangeRateDate) != null) {
			Pricer pricer = getEffectiveABookCostPricer(region, targetCurrency, soldToCustomer, exchangeRateDate);
			if(pricer instanceof NormalPricer) {
				return (NormalPricer)pricer;
			}
		}
		
		if (getClosestExpiredABookCostPricer(region, targetCurrency, soldToCustomer, exchangeRateDate) != null) {
			Pricer pricer = getClosestExpiredABookCostPricer(region, targetCurrency, soldToCustomer, exchangeRateDate);
			if(pricer instanceof NormalPricer) {
				return (NormalPricer)pricer;
			}
		}
		
		return null;

	}	
	
	public List<PricerInfo> searchForPricerPopup(PricerInfo pricerInfo) {
		
		List<Pricer> pricers = this.getPricers()
				.stream()
				.filter(pricer -> pricer.isRegionCustomerTypeMatched(pricerInfo.getBizUnit(), pricerInfo.getSoldToCustomer(), 
						pricerInfo.getEndCustomer(), pricerInfo.getAllowedCustomers(), pricerInfo.isSalesCostFlag()))
				.collect(Collectors.toList());
		
		List<PricerInfo> results = new ArrayList<>();
		for (Pricer pricer : pricers) {
			PricerInfo returnPrincerInfo = PricerInfo.newInstance(pricerInfo);
			pricer.fillupPricerInfo(returnPrincerInfo);
			pricer.getCostIndicator().apply(this.getEffectiveOrExpiredABookCostPricer(pricerInfo.getBizUnit(), 
					Currency.valueOf(pricerInfo.getRfqCurr()), pricerInfo.getSoldToCustomer(), pricerInfo.getExRateDate()), returnPrincerInfo);
			results.add(returnPrincerInfo);
		}
		
		Collections.sort(results, (p1, p2) -> {
			return p1.getQuotationEffectiveDate().compareTo(p2.getQuotationEffectiveDate());
		});
		return results;
		
	}
	
	
	private Pricer getPricerByCostIndicatorName(String costIndicator, Currency targetCurr, Currency buyCurr, String region, Customer soldToCustomer, 
			Customer endCustomer, List<Customer> allowedCustomers, boolean salesCostFlag, Date exRateDate) {
		
		return getCandidatePricers(region, targetCurr, soldToCustomer, endCustomer, allowedCustomers, salesCostFlag, false, exRateDate)
			.stream()
			.filter(pricer -> pricer.getCostIndicator().getName().equalsIgnoreCase(costIndicator) && buyCurr == pricer.getCurrency())
			.findFirst()
			.orElse(null);
		
		
	}
	
	
	public boolean isValidForMaterialPoupup(String region, Customer soldToCustomer, Customer endCustomer, List<Customer> allowedCustomers) {
		for (Pricer pricer : pricers) {
			if (getMaterialRegaional(region) == null) {
				return false;
			}
			if (pricer.isRegionCustomerTypeMatched(region, soldToCustomer, endCustomer, allowedCustomers, getMaterialRegaional(region).isSalesCostFlag())
					&& pricer.isEffectivePricer()) {
				return true;
			}
		}
		return false;
	}
	
	private void fillWithMfrDetail(PricerInfo pricerInfo) {
		MaterialRegional materialRegional = this.getMaterialRegaional(pricerInfo.getBizUnit());
		if (materialRegional != null) {
			ManufacturerDetail mfrDetail = materialRegional.getManufacturerDetail();
			if (mfrDetail != null) {
				mfrDetail.fillIn(pricerInfo);
			} else {
				pricerInfo.setQtyIndicator("MOQ");
			}
		}
		if (pricerInfo.getMultiUsageFlag() == null) {
			pricerInfo.setMultiUsageFlag(true);
		}

	}
	
	
	public void selectPricer(long pricerId, PricerInfo pricerInfo) {
		pricerInfo.clearPricingInfo();

//		fillWithMfrDetail(pricerInfo);
		
		Pricer pricer = null;
		for (Pricer p : pricers) {
			if (p.getId() == pricerId) {
				pricer = p; 
				break;
			}
		}

		if (pricer != null) {
			pricer.fillupPricerInfo(pricerInfo);
			pricer.getCostIndicator().apply(this.getEffectiveOrExpiredABookCostPricer(pricerInfo.getBizUnit(), 
					Currency.valueOf(pricerInfo.getRfqCurr()), pricerInfo.getSoldToCustomer(), pricerInfo.getExRateDate()), pricerInfo);
//			pricerInfo.calQty();
		}
		
		pricerInfo.fillWithDefaultValue();
	}
	
	public void switchCostIndicator(PricerInfo pricerInfo, CostIndicator costIndicator, Currency buyCurr) {
		
		pricerInfo.clearPricingInfo();
		/*if(costIndicator == null) {
			logger.info("costIndicator is null in switchCostIndicator function.");
			return ;
		}*/
//		fillWithMfrDetail(pricerInfo);
		Date date = pricerInfo.getExRateDate();
		String costIndicatorName = null;
		if(costIndicator !=null )costIndicatorName = costIndicator.getName();
				//pricerInfo.getSubmitDate() ==null ? new Date() : pricerInfo.getSubmitDate();
		Pricer pricer = getPricerByCostIndicatorName(costIndicatorName, Currency.valueOf(pricerInfo.getRfqCurr()), buyCurr, pricerInfo.getBizUnit(), pricerInfo.getSoldToCustomer(), 
				pricerInfo.getEndCustomer(), pricerInfo.getAllowedCustomers(), pricerInfo.isSalesCostFlag(), date);
		if (pricer != null) {
			pricer.fillupPricerInfo(pricerInfo);
			pricer.getCostIndicator().apply(this.getEffectiveOrExpiredABookCostPricer(pricerInfo.getBizUnit(), 
					Currency.valueOf(pricerInfo.getRfqCurr()), pricerInfo.getSoldToCustomer(), pricerInfo.getExRateDate()), pricerInfo);
//			pricerInfo.calQty();
		} else {
			if(costIndicator !=null) costIndicator.apply(this.getEffectiveOrExpiredABookCostPricer(pricerInfo.getBizUnit(), 
					Currency.valueOf(pricerInfo.getRfqCurr()), pricerInfo.getSoldToCustomer(), pricerInfo.getExRateDate()), pricerInfo);
			pricerInfo.setCostIndicator(costIndicatorName);
			pricerInfo.setBuyCurr(buyCurr.toString());
			pricerInfo.calExRate();
		}
		
		pricerInfo.fillWithDefaultValue();

	}

	
	public Pricer getValidProgPricerByBizUintAndCostIndicator(String bizUnitName,String costIndicator) {
		Pricer validProgPricer = null;
		for(Pricer p : pricers) {
			if ((bizUnitName != null && p.getBizUnitBean() != null
					&& StringUtils.equalsIgnoreCase(bizUnitName, p.getBizUnitBean().getName())) 
					&& p.isEffectivePricer()
					&& (p.getCostIndicator() != null && p.getCostIndicator().getName() != null
							&& StringUtils.equals(p.getCostIndicator().getName(), costIndicator))) {
				if(p instanceof ProgramPricer) {
					validProgPricer = p;
				}
				
			}
		}
		return validProgPricer;
	}
	
	
	public void applyDefaultCostIndicatorLogic(PricerInfo pricerInfo, boolean needRefillAccessRelatedFields) {
		logger.log(Level.INFO, "applyDefaultCostIndicatorLogic for " + pricerInfo.toString() + 
				" , needRefillAccessRelatedFields:" + needRefillAccessRelatedFields);
		
		pricerInfo.clearPricingInfo();

		pricerInfo.setQuotedMaterial(this);
		pricerInfo.setQuotedMfr(manufacturer);
		
		MaterialRegional materialRegional = this.getMaterialRegaional(pricerInfo.getBizUnit());
		if (materialRegional == null) {
			pricerInfo.setQtyIndicator("MOQ");
			pricerInfo.adjustAfterNoFoundBuyCurr();
			logger.log(Level.INFO, "No MaterialRegional found");
			return;
		}
		
		if (needRefillAccessRelatedFields) {
			materialRegional.fillInPricer(pricerInfo);
		}
		
		fillWithMfrDetail(pricerInfo);
		//TODO
		Date targetDate = pricerInfo.getExRateDate();
				//pricerInfo.getSubmitDate() == null? new Date() : pricerInfo.getSubmitDate();
		List<Pricer> pricers = getCandidatePricers(pricerInfo.getBizUnit(), Currency.valueOf(pricerInfo.getRfqCurr()) ,pricerInfo.getSoldToCustomer(), 
				pricerInfo.getEndCustomer(), pricerInfo.getAllowedCustomers(), pricerInfo.isSalesCostFlag(), true, targetDate); 
		
		if (needRefillAccessRelatedFields) {
			if (! hasProgramPricer(pricers)) {
				pricerInfo.setMaterialTypeId("NORMAL");
				pricerInfo.setProgramType(null);
			} else {
				pricerInfo.setMaterialTypeId("PROGRAM");
			}
		}
		
		if( ! pricers.isEmpty()) {
			pricers.get(0).fillupPricerInfo(pricerInfo);
			pricers.get(0).getCostIndicator().apply(this.getEffectiveOrExpiredABookCostPricer(pricerInfo.getBizUnit(), 
					Currency.valueOf(pricerInfo.getRfqCurr()), pricerInfo.getSoldToCustomer(), pricerInfo.getExRateDate()), pricerInfo);
//			pricerInfo.calQty();
			pricerInfo.calAfterFillin();
		}
		
		pricerInfo.fillWithDefaultValue();
		
		logger.log(Level.INFO, "At the end of applyDefaultCostIndicatorLogic.");

	}
	

	
	private boolean hasProgramPricer(List<Pricer> pricers) {
		return pricers
				.stream()
				.anyMatch(pricer -> pricer instanceof ProgramPricer && pricer.isEffectivePricer());
	}
	
	/*
	 * in the return array, 
	 * 1st element is the count of SalesCostPricer
	 * 2nd element is the count of ProgramPricer
	 * 3rd  element is the count of Future Pricer
	 */
	public int[] calPricerNumber(PricerInfo pricerInfo) {
		
		int s = 0 , p = 0, f = 0;
		/*
		List<Pricer> pricers = getCandidatePricers(pricerInfo.getBizUnit(), pricerInfo.getSoldToCustomer(), pricerInfo.getEndCustomer(), 
				pricerInfo.getAllowedCustomers(), pricerInfo.isSalesCostFlag(), false);
		for (Pricer pricer : pricers) {
			if (pricer instanceof SalesCostPricer) {
				s++;
			} else if (pricer instanceof ProgramPricer) {
				p++;
			}
		}
		*/
		
		for (Pricer pricer : this.pricers) {
			if (getMaterialRegaional(pricerInfo.getBizUnit()) ==  null) {
				return new int[]{0, 0, 0};
			}
			
			if (pricer.isRegionCustomerTypeMatched(pricerInfo.getBizUnit(), pricerInfo.getSoldToCustomer(), pricerInfo.getEndCustomer(), 
				pricerInfo.getAllowedCustomers(), pricerInfo.isSalesCostFlag())) {

				if (pricer instanceof SalesCostPricer && pricer.isEffectivePricer()) {
					s++;
				} else if (pricer instanceof ProgramPricer && pricer.isEffectivePricer()) {
					p++;
				}
				
				if (pricer.isFuturePricer()) {
					f++;
				}
			}
		}
		
		return new int[]{s, p, f};
	}
	
	public boolean hasPricersForBizUnit(String bizUnit) {
		for (Pricer pricer : pricers) {
			if (pricer.getBizUnitBean().getName().equalsIgnoreCase(bizUnit)) {
				return true;
			}
		}
		return false;
	}

}