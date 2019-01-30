package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;




/**
 * The persistent class for the MANUFACTURER database table.
 * 
 */
@Entity
@Table(name="MANUFACTURER")
public class Manufacturer implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="MANUFACTURER_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MANUFACTURER_ID_GENERATOR")
	@Column(unique=true, nullable=false, precision=15)
	private long id;

	@Column(length=100)
	private String description;

	@Column(nullable=false, length=20)
	private String name;
	
	//uni-directional many-to-many association to BizUnit
	@ManyToMany	//(cascade = CascadeType.ALL)
	@JoinTable(
		name="MANUFACTURER_BIZUNIT_MAPPING"
		, joinColumns={
			@JoinColumn(name="MANUFACTURER_ID", nullable=false)
			}
		, inverseJoinColumns={
			@JoinColumn(name="BIZ_UNIT_ID", nullable=false)
			}
	)
	private List<BizUnit> bizUnits;

	@OneToMany(mappedBy="manufacturer")
	private List<ManufacturerDetail> manufacturerDetails;
	
	@OneToMany(mappedBy="manufacturer")
	private List<ReferenceMarginSetting> referenceMarginSettings;
	
	@OneToMany(mappedBy="manufacturer", cascade = CascadeType.ALL, orphanRemoval=true)
	private List<ManufacturerMapping> manufacturerMappings;
	
	public Manufacturer() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<BizUnit> getBizUnits() {
		return bizUnits;
	}

	public void setBizUnits(List<BizUnit> bizUnits) {
		this.bizUnits = bizUnits;
	}

	public List<ReferenceMarginSetting> getReferenceMarginSettings() {
		return referenceMarginSettings;
	}

	public void setReferenceMarginSettings(List<ReferenceMarginSetting> referenceMarginSettings) {
		this.referenceMarginSettings = referenceMarginSettings;
	}

	public List<ManufacturerDetail> getManufacturerDetails() {
		return manufacturerDetails;
	}

	public void setManufacturerDetailList(List<ManufacturerDetail> manufacturerDetails) {
		this.manufacturerDetails = manufacturerDetails;
	}
	
	
	public List<ManufacturerMapping> getManufacturerMappings() {
		return manufacturerMappings;
	}

	public void setManufacturerMappings(List<ManufacturerMapping> manufacturerMappings) {
		this.manufacturerMappings = manufacturerMappings;
	}
	
	public ManufacturerMapping getManufacturerMapping(String bizUnit) {
		for (ManufacturerMapping manufacturerMapping : manufacturerMappings) {
			if (manufacturerMapping.getBizUnit().getName().equalsIgnoreCase(bizUnit)) {
				return manufacturerMapping;
			}
		}
		return null;
	}

	ManufacturerDetail getManufacturerDetail(ProductGroup productGroup, ProductCategory productCategory, String bizUnit) {
		for (ManufacturerDetail manufacturerDetail : manufacturerDetails) {
			if (manufacturerDetail.getProductGroup().equals(productGroup) && 
					manufacturerDetail.getProductCategory().equals(productCategory) &&
					manufacturerDetail.getBizUnit().getName().equalsIgnoreCase(bizUnit)) {
				return manufacturerDetail;
			}
		}
		return null;
	}

	List<ReferenceMarginSetting> getOrderedReferenceMarginSetting(QuoteItem quoteItem) {

		List<ReferenceMarginSetting> referenceMarginSettings = new ArrayList<>(this.referenceMarginSettings);
		Iterator<ReferenceMarginSetting> itr =  referenceMarginSettings.iterator();
		while(itr.hasNext()) {
			if (! itr.next().getBizUnitID().equalsIgnoreCase(quoteItem.getQuote().getBizUnit().getName())) {
				itr.remove();
			}
		}
		
		Collections.sort(referenceMarginSettings, (r1, r2) -> {
			//compare material id first
			if(r1.getMaterialID() != 0L && r2.getMaterialID() != 0L ) {
				return 0;
			} else if (r1.getMaterialID() == 0L && r2.getMaterialID() != 0L) {
				return 1;
			} else if (r1.getMaterialID() != 0L && r2.getMaterialID() == 0L) {
				return -1;
			} 
			//if material id are both 0, then compare product group id
			if (r1.getProductGroupID() != 0L && r2.getProductGroupID() != 0L) {
				return 0;
			} else if (r1.getProductGroupID() == 0L && r2.getProductGroupID() != 0L) {
				return 1;
			} else if (r1.getProductGroupID() != 0L && r2.getProductGroupID() != 0L) {
				return -1;
			} else {
				return 0;
			}

		});
		return referenceMarginSettings;
	}
	
	public void applyReferenceMarginLogic(QuoteItem quoteItem) {
		List<ReferenceMarginSetting> referenceMarginSettings = getOrderedReferenceMarginSetting(quoteItem);
		
		if (referenceMarginSettings.isEmpty()) {
			ReferenceMarginSetting refMargin = ReferenceMarginSetting.createDefaultOne();
			refMargin.applyReferenceMarginLogic(quoteItem);
			return;
		}
		
		for (ReferenceMarginSetting refMargin : referenceMarginSettings) {
			if (quoteItem.getQuotedMaterial() != null && refMargin.getMaterialID() != 0L && quoteItem.getQuotedMaterial().getId() == refMargin.getMaterialID() &&
				quoteItem.getProductGroup2() != null && refMargin.getProductGroupID() != 0L && quoteItem.getProductGroup2().getId() == refMargin.getProductGroupID() &&
				quoteItem.getQuotedMfr() != null && refMargin.getManufacturer() != null && quoteItem.getQuotedMfr().getId() == refMargin.getManufacturer().getId()) {
				refMargin.applyReferenceMarginLogic(quoteItem);
				return;
			}
		}
		
		for (ReferenceMarginSetting refMargin : referenceMarginSettings) {
			if (quoteItem.getProductGroup2() != null && refMargin.getProductGroupID() != 0L && quoteItem.getProductGroup2().getId() == refMargin.getProductGroupID() &&
				quoteItem.getQuotedMfr() != null && refMargin.getManufacturer() != null && quoteItem.getQuotedMfr().getId() == refMargin.getManufacturer().getId() &&
				refMargin.getMaterialID() == 0L) {
				refMargin.applyReferenceMarginLogic(quoteItem);
				return;
			}
		}
		
		for (ReferenceMarginSetting refMargin : referenceMarginSettings) {
			if (quoteItem.getQuotedMfr() != null && refMargin.getManufacturer() != null && quoteItem.getQuotedMfr().getId() == refMargin.getManufacturer().getId() &&
					refMargin.getMaterialID() ==0l && refMargin.getProductGroupID() == 0l) {
				refMargin.applyReferenceMarginLogic(quoteItem);
				return;
			}
		}
		
		ReferenceMarginSetting refMargin = ReferenceMarginSetting.createDefaultOne();
		refMargin.applyReferenceMarginLogic(quoteItem);
		
	}

	@Override
	public String toString() {
		return "Manufacturer [id=" + id + ", name=" + name + "]";
	}



}