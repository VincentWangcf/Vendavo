package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.avnet.emasia.webquote.vo.PricerInfo;
import com.avnet.emasia.webquote.vo.RfqItemVO;



@Entity
@Table(name="MATERIAL_REGIONAL")
public class MaterialRegional implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(MaterialRegional.class.getName());

	@Id
	@SequenceGenerator(name="MATERIAL_REGIONAL_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MATERIAL_REGIONAL_GENERATOR")
	@Column(unique=true, nullable=false)
	private long id;

	@ManyToOne
	@JoinColumn(name="MATERIAL_ID")
	private Material material;

	@ManyToOne
	@JoinColumn(name="BIZ_UNIT")
	private BizUnit bizUnit;
	
	@Column(name="SALES_COST_FLAG")
	private boolean salesCostFlag = false;
	
	@ManyToOne
	@JoinColumn(name="PRODUCT_GROUP2_ID")
	private ProductGroup productGroup2;
	
	@ManyToOne
	@JoinColumn(name="PRODUCT_GROUP1_ID")
	private ProductGroup productGroup1;	
	
	@Column(name="PRODUCT_GROUP3")
	private String productGroup3;
	
	@Column(name="PRODUCT_GROUP4")
	private String productGroup4;
	
	@ManyToOne
	@JoinColumn(name="PRODUCT_CATEGORY_ID")
	private ProductCategory productCategory;
	
	//20171115 (wong): replace with batchStatus
//	@Column(name="SAP_SEND_FLAG")
//	private boolean sapSendFlag;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_ON", nullable=false)
	private Date createdOn;

	@Column(name="DELETION_FLAG", length=1)
	private Boolean deletionFlag;


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_UPDATED_ON")
	private Date lastUpdatedOn;


	@Column(name = "CREATED_BY", nullable = false, length = 10)
	private String createdBy;

	@Column(name = "LAST_UPDATED_BY", length = 10)
	private String lastUpdatedBy;
	
	@Column(name="BATCH_STATUS")
	private int batchStatus;
	

	public MaterialRegional() {
	}
	
	

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
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

	public Date getLastUpdatedOn() {
		return this.lastUpdatedOn;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public String getCreatedBy(){
		return createdBy;
	}

	public void setCreatedBy(String createdBy){
		this.createdBy = createdBy;
	}

	public String getLastUpdatedBy(){
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy){
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public ProductCategory getProductCategory() {
		return this.productCategory;
	}

	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}

	public ProductGroup getProductGroup2() {
		return this.productGroup2;
	}

	public void setProductGroup2(ProductGroup productGroup2) {
		this.productGroup2 = productGroup2;
	}

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

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public BizUnit getBizUnit() {
		return bizUnit;
	}

	public void setBizUnit(BizUnit bizUnit) {
		this.bizUnit = bizUnit;
	}

	public boolean isSalesCostFlag() {
		return salesCostFlag;
	}

	public void setSalesCostFlag(boolean salesCostFlag) {
		this.salesCostFlag = salesCostFlag;
	}

	void fillInPricer(RfqItemVO rfqItem, RFQSubmissionTypeEnum submissionType) {
		logger.log(Level.INFO, "MaterialRegional[" + id + "] is used");
		rfqItem.setProductGroup1(productGroup1);
		rfqItem.setProductGroup2(productGroup2);
		rfqItem.setProductGroup3(productGroup3);
		rfqItem.setProductGroup4(productGroup4);
		if (submissionType.equals(RFQSubmissionTypeEnum.NormalRFQSubmission) || submissionType.equals(RFQSubmissionTypeEnum.ShoppingCartSubmission)) {
			rfqItem.setSalesCostFlag(salesCostFlag);
		} else if (submissionType.equals(RFQSubmissionTypeEnum.DPRFQSubmission)) {
			rfqItem.setSalesCostFlag(false);
		}
		//set default value for salesCostType, override in SalesCostPricer
		
		/* no need fill up salesCostType in MaterialRegional
		if (rfqItem.isSalesCostFlag() == false) {
			rfqItem.setSalesCostType(SalesCostType.NonSC);
		} else {
			rfqItem.setSalesCostType(SalesCostType.ZINC);
		}
		*/
	}
	
	
	void fillInPricer(PricerInfo pricerInfo) {
		logger.log(Level.INFO, "MaterialRegional[" + id + "] is used");
		pricerInfo.setProductGroup1(productGroup1);
		pricerInfo.setProductGroup2(productGroup2);
		pricerInfo.setProductGroup3(productGroup3);
		pricerInfo.setProductGroup4(productGroup4);
		pricerInfo.setSalesCostFlag(salesCostFlag);
		//set default value for salesCostType, override in SalesCostPricer
//		if (pricerInfo.getSalesCostType() == null) {
		/* no need fill up salesCostType in MaterialRegional
			if (salesCostFlag == false) {
				pricerInfo.setSalesCostType(SalesCostType.NonSC);
			} else {
				pricerInfo.setSalesCostType(SalesCostType.ZINC);
			}
		*/
//		}
	}
	
	ManufacturerDetail getManufacturerDetail() {
		return material.getManufacturer().getManufacturerDetail(productGroup2, productCategory, bizUnit.getName());
	}

	public int getBatchStatus() {
		return batchStatus;
	}

	public void setBatchStatus(int batchStatus) {
		this.batchStatus = batchStatus;
	}

}