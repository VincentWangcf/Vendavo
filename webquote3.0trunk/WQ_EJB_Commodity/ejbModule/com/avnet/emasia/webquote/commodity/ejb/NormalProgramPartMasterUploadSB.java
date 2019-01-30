package com.avnet.emasia.webquote.commodity.ejb;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.avnet.emasia.webquote.commodity.bean.PricerUploadParametersBean;
import com.avnet.emasia.webquote.commodity.bean.PricerUploadTemplateBean;
import com.avnet.emasia.webquote.commodity.bean.VerifyEffectiveDateResult;
import com.avnet.emasia.webquote.commodity.constant.PRICER_TYPE;
import com.avnet.emasia.webquote.commodity.constant.PricerConstant;
import com.avnet.emasia.webquote.commodity.constant.QED_CHECK_STATE;
import com.avnet.emasia.webquote.commodity.dao.PricerUploadCommonSB;
import com.avnet.emasia.webquote.commodity.util.CommodityUtil;
import com.avnet.emasia.webquote.commodity.util.PricerUtils;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.CostIndicator;
import com.avnet.emasia.webquote.entity.Currency;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.MaterialRegional;
import com.avnet.emasia.webquote.entity.Money;
import com.avnet.emasia.webquote.entity.NormalPricer;
import com.avnet.emasia.webquote.entity.ProductCategory;
import com.avnet.emasia.webquote.entity.ProductGroup;
import com.avnet.emasia.webquote.entity.ProgramPricer;
import com.avnet.emasia.webquote.entity.ProgramType;
import com.avnet.emasia.webquote.entity.QuantityBreakPrice;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.ejb.ManufacturerSB;
import com.avnet.emasia.webquote.utilities.util.QuoteUtil;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class NormalProgramPartMasterUploadSB extends PricerUploadCommonSB {
	private static final Logger LOG = Logger.getLogger(NormalProgramPartMasterUploadSB.class.getName());
	
	@EJB
	ManufacturerSB manufacturerSB;
	/**
	 * insert or Upload pricer to db.
	 * 
	 * @param items
	 * @param user
	 */
	@Override
	public void insertUploadOperater(List<PricerUploadTemplateBean> items, String materialTypeStr, User user,
			List<Manufacturer> manufacturerLst, int[] countArr, PricerUploadParametersBean puBean) {
		long start = System.currentTimeMillis();
		// init the data the next step need.
		List<Material> allMtlLstInDB = this.findAllMaterial(items, user, manufacturerLst, puBean);
		if (PRICER_TYPE.PARTMASTER.getName().equalsIgnoreCase(materialTypeStr)) {
			handleMaterialAndRegionalAddOrUpdate(items, materialTypeStr, user, manufacturerLst, countArr, puBean);
			return;
		}

		Map<String, Material> allMtlMap = new HashMap<String, Material>();
		if (allMtlLstInDB != null) {
			for (Material mtl : allMtlLstInDB) {
				if (allMtlMap.containsKey(mtl.getFullMfrPartNumber() + mtl.getManufacturer().getName())) {
					if (allMtlMap.get(mtl.getFullMfrPartNumber() + mtl.getManufacturer().getName()).getId() > mtl
							.getId()) {
						allMtlMap.put(mtl.getFullMfrPartNumber() + mtl.getManufacturer().getName(), mtl);
					} else {
						continue;
					}
				}
				allMtlMap.put(mtl.getFullMfrPartNumber() + mtl.getManufacturer().getName(), mtl);
			}
		}

		String existfgStr = null;
		for (PricerUploadTemplateBean pricr : items) {
			existfgStr = pricr.getFullMfrPart() + pricr.getMfr();
			Material mtl = allMtlMap.get(existfgStr);
			if (mtl == null) {
				LOG.warning("No material when  insert pricer. "
						+ "This error should not be occured ,please check the FUNCTION: verify material in DB .");
				continue;
			}
			pricerTypeUpdate(manufacturerLst, countArr, puBean, materialTypeStr, mtl, pricr, user);
		}

		long end = System.currentTimeMillis();
		LOG.info("insertUploadOperater,takes " + (end - start) + " ms  processed line:"
				+ items.get(items.size() - 1).getLineSeq());
	}

	// handle for upload material
	private void handleMaterialAndRegionalAddOrUpdate(List<PricerUploadTemplateBean> items, String materialTypeStr,
			User user, List<Manufacturer> manufacturerLst, int[] countArr, PricerUploadParametersBean puBean) {
		//for materialRegional
		boolean isNew = false;
		Material mtl = null;
		for (PricerUploadTemplateBean bean : items) {
			isNew = false;
			mtl = this.findMaterial(bean.getMfr(), bean.getFullMfrPart());
			if (mtl != null) {
				updateMaterial(materialTypeStr, bean, mtl, user, puBean);
				isNew = updateOrInsertMRegional(bean, mtl, puBean, user, false);
			} else {
				try {
					mtl = insertPricerToMaterial(materialTypeStr, bean, user, puBean);
					updateOrInsertMRegional(bean, mtl, puBean, user, true);
					isNew = true;
				} catch (Exception e) {
					LOG.log(Level.SEVERE, "Exception occurs in insertPricerToMaterial: " + materialTypeStr
							+ " :: user Id and name " + user.getId() + " & " + user.getName() + e.getMessage(), e);
				}
			}
			if (isNew)
				countArr[0]++;// addedPartCount++;
			else
				countArr[3]++;// updatedPartCount++
		}
	}
	
	//
	public Material findMaterial(String mfr, String fullPartNumber) {
		if(mfr == null ||fullPartNumber == null ){
			return null;
		}
		String sql = "SELECT m FROM Material m WHERE "
				+ " m.manufacturer.name = :mfr AND m.fullMfrPartNumber =:fullPartNumber" ;
		Query query = em.createQuery(sql);
		query.setParameter("fullPartNumber", fullPartNumber);
		query.setParameter("mfr", mfr);
		//LOG.info(mfr);
		List<Material> list = query.getResultList();
		if(list.isEmpty()) {
			return null;
		}
		return (Material)list.get(0);
		
	}
	/**
	 * insert or Upload pricer to db.
	 * 
	 * @param items
	 * @param user
	 */
	/*
	 * public void insertUploadOperaterOld(List<PricerUploadTemplateBean> items,
	 * String materialTypeStr, User user, List<Manufacturer> manufacturerLst,
	 * int[] countArr, PricerUploadParametersBean puBean) { long start =
	 * System.currentTimeMillis(); BizUnit bz = user.getDefaultBizUnit();
	 * 
	 * List<Material> allMtlLstInDB = this.findAllMaterial(items, user,
	 * manufacturerLst, puBean); Map<String, Material> allMtlMap = new
	 * HashMap<String, Material>(); Set<Long> mtrIdSet = new
	 * HashSet<Long>();//all materialID in db if ((allMtlLstInDB != null) &&
	 * (allMtlLstInDB.size() > 0)) {// Divide update and insert pricer.
	 * 
	 * for (Material mtl : allMtlLstInDB) { if
	 * (allMtlMap.containsKey(mtl.getFullMfrPartNumber() +
	 * mtl.getManufacturer().getName())) { if
	 * (allMtlMap.get(mtl.getFullMfrPartNumber() +
	 * mtl.getManufacturer().getName()).getId() > mtl.getId()) {
	 * allMtlMap.put(mtl.getFullMfrPartNumber() +
	 * mtl.getManufacturer().getName(), mtl); } else { continue; } }
	 * allMtlMap.put(mtl.getFullMfrPartNumber() +
	 * mtl.getManufacturer().getName(), mtl); mtrIdSet.add(mtl.getId()); } //
	 * insert and update Set<String> costidctNameSet = new
	 * HashSet<String>();//need to update in this set String existfgStr = null;
	 * 
	 * for (PricerUploadTemplateBean pricr : items) {
	 * 
	 * existfgStr = pricr.getFullMfrPart() + pricr.getMfr(); if
	 * (allMtlMap.containsKey(existfgStr)) { // update the material out of this
	 * scrop. costidctNameSet.add(pricr.getCostIndicator());
	 * 
	 * Material mtl = allMtlMap.get(existfgStr);
	 * 
	 * if (mtl != null) { // update material(minimum id one)
	 * updateMaterial(materialTypeStr,pricr, mtl, user, puBean);
	 * if(PRICER_TYPE.PARTMASTER.getName().equalsIgnoreCase(materialTypeStr)) {
	 * countArr[3]++;//updatedPartCount++ } // Added by Punit for CPD Exercise
	 * pricerTypeUpdate(manufacturerLst, countArr, puBean, materialTypeStr, mtl,
	 * pricr, user); } } else { // insert material Material material = null; try
	 * { material = insertPricerToMaterial(materialTypeStr,pricr, user, puBean);
	 * } catch (Exception e) { LOG.log(Level.SEVERE,
	 * "insertPricerToMaterial error : "+materialTypeStr+" :: user Id and name "
	 * + user.getId() +" & "+user.getName() + e.getMessage(), e); }
	 * 
	 * if (material != null) { insertNormalAndProgramMaterial(materialTypeStr,
	 * pricr, user, puBean, countArr, material); } } } } else {
	 *//**
		 * without these data in db,so we need to insert all , pastMaster only
		 * insert material but Normal and program insert material and
		 * meterial_detail
		 *//*
		 * for (PricerUploadTemplateBean pricr : items) { Material material =
		 * null; try { material = insertPricerToMaterial(materialTypeStr,pricr,
		 * user, puBean); } catch (Exception e) { LOG.log(Level.SEVERE,
		 * "insertProgramPricerToMaterialDetail error : "
		 * +materialTypeStr+" :: user Id and name "+ user.getId()
		 * +" & "+user.getName() + e.getMessage(), e); }
		 * 
		 * if (material != null) {
		 * insertNormalAndProgramMaterial(materialTypeStr,pricr, user, puBean,
		 * countArr, material); } } } long end = System.currentTimeMillis();
		 * LOG.info("insertUploadOperater,takes " + (end - start) +
		 * " ms  processed line:"+items.get(items.size()-1).getLineSeq()); }
		 */

	/**
	 * @param puBean,
	 *            manufacturerLst, int[] countArr
	 * @param user
	 * @param pricr
	 * @param materialTypeStr
	 * @param countArr
	 * 
	 */
	/*
	 * private void insertNormalAndProgramMaterial(String materialTypeStr,
	 * PricerUploadTemplateBean pricr, User user, PricerUploadParametersBean
	 * puBean, int[] countArr,Material material) {
	 * if(PRICER_TYPE.PARTMASTER.getName().equalsIgnoreCase(materialTypeStr)) {
	 * countArr[0]++;//addedPartCount++; }else if
	 * (PRICER_TYPE.NORMAL.getName().equalsIgnoreCase(materialTypeStr)) { try {
	 * insertNormalPricerToMaterialDetail(pricr, material, user, puBean);
	 * countArr[1]++;//addedNormalCount++; } catch (Exception e) {
	 * LOG.log(Level.SEVERE, "insertNormalPricerToMaterialDetail error : "
	 * +materialTypeStr+" :: user Id and name "+ user.getId()
	 * +" & "+user.getName() + e.getMessage(), e); }
	 * 
	 * } else
	 * if(PRICER_TYPE.PROGRAM.getName().equalsIgnoreCase(materialTypeStr)) { try
	 * { insertProgramPricerToMaterialDetail(pricr, material, user, puBean);
	 * countArr[2]++;//addedProgramCount++; } catch (Exception e) {
	 * LOG.log(Level.SEVERE, "insertProgramPricerToMaterialDetail error : "
	 * +materialTypeStr+" :: user Id and name "+ user.getId()
	 * +" & "+user.getName() + e.getMessage(), e); }
	 * 
	 * }
	 * 
	 * }
	 */

	private void removeQuantityBreakPrice(PricerUploadTemplateBean pricr) {
		long materialDetailId = pricr.getMetarialDetailId();
		// TODO a.materialDetail.id
		Query query = em.createQuery("delete FROM QuantityBreakPrice a where a.materialDetail.id = :matDtlId");
		query.setParameter("matDtlId", materialDetailId);
		int rowCount = query.executeUpdate();
		// LOG.info("delete QuantityBreakPrice size :"+ rowCount);
	}

	/**
	 * according MFR,MFR# and KIT_PAER_FLAG to insert table material.
	 * 
	 * @param uploadBean
	 */
	public Material insertPricerToMaterial(String materialTypeStr, PricerUploadTemplateBean uploadBean, User user,
			PricerUploadParametersBean puBean) throws ParseException {
		if (uploadBean != null) {

			// LOG.info("insert pricer to material");
			Material material = new Material();
			Manufacturer mftr = new Manufacturer();
			/*Map<Object, Object> manfterNMap = puBean.getManfterNMap();
			mftr.setId((Long) manfterNMap.get(pricr.getMfr().toUpperCase()));*/
			material.setManufacturer(manufacturerSB.findManufacturerByName(uploadBean.getMfr().toUpperCase(), 
					new BizUnit(uploadBean.getRegion().toUpperCase())));

			material.setFullMfrPartNumber(uploadBean.getFullMfrPart());
			/*material.setPackageType(uploadBean.getPackageType());
			material.setPackagingType(uploadBean.getPackagingType());*/
			material.setCreatedOn(new Date());
			material.setCreatedBy(user.getEmployeeId());

			material.setLastUpdatedBy(user.getEmployeeId());
			material.setLastUpdatedOn(new Date());

			if (PRICER_TYPE.PARTMASTER.getName().equalsIgnoreCase(materialTypeStr)) {
				// Map<Object, Object> categoryMap = puBean.getCategoryMap();
				// material.setProductCategory((ProductCategory)
				// categoryMap.get(pricr.getProductCat()));
				material.setMaterialType("PARTMASTER");
			} else if (PRICER_TYPE.NORMAL.getName().equalsIgnoreCase(materialTypeStr)) {
				// Map<Object, Object> categoryMap = puBean.getCategoryMap();
				// material.setProductCategory((ProductCategory)
				// categoryMap.get(pricr.getProductCat()));
				material.setMaterialType("NORMAL");
			} else if (PRICER_TYPE.PROGRAM.getName().equalsIgnoreCase(materialTypeStr)) {
				material.setMaterialType("PROGRAM");
			}
			material.setValid(true);
			em.persist(material);
			return material;
		}

		return null;
	}

	// update or insert MaterialRegional
	// true insert
	//false update
	private boolean updateOrInsertMRegional(PricerUploadTemplateBean pricr, Material material,
			PricerUploadParametersBean puBean, User user, Boolean isInsertDirectly) {

		BizUnit bizUnit = new BizUnit(pricr.getRegion().toUpperCase());
		/*if (!QuoteUtil.isEmpty(pricr.getRegion())
				&& puBean.getRegionMap().containsKey(pricr.getRegion().toUpperCase())) {
			bizUnit = (BizUnit) puBean.getRegionMap().get(pricr.getRegion().toUpperCase());
		} else {
			bizUnit = user.getDefaultBizUnit();
		}*/
		boolean isNew = false;
		MaterialRegional materialRegional = null;
		if (!isInsertDirectly) {
			materialRegional = materialRegionalSB.findMRegional(material, bizUnit);
		}
		if (materialRegional == null || materialRegional.getId() < 1) {
			isNew =true;
			materialRegional = new MaterialRegional();
			fillMaterialRegional(materialRegional, pricr, puBean);
			materialRegional.setCreatedOn(new Date());
			materialRegional.setCreatedBy(user.getEmployeeId());
			materialRegional.setLastUpdatedOn(new Date());
			materialRegional.setLastUpdatedBy(user.getEmployeeId());
			materialRegional.setBizUnit(bizUnit);
			materialRegional.setMaterial(material);
			materialRegional.setBatchStatus(0);
			em.persist(materialRegional);
		} else {
			//before fill need check is SalesCostFlag changed
			if("YES".equalsIgnoreCase(pricr.getSalesCostFlag())!=materialRegional.isSalesCostFlag()) {
				materialRegional.setBatchStatus(1);
			}
			fillMaterialRegional(materialRegional, pricr, puBean);
			materialRegional.setLastUpdatedOn(new Date());
			materialRegional.setLastUpdatedBy(user.getEmployeeId());
			em.merge(materialRegional);
		}
		return isNew;
	}

	private void fillMaterialRegional(MaterialRegional materialRegional, PricerUploadTemplateBean pricr,
			PricerUploadParametersBean puBean) {

		String materialTypeStr = pricr.getPricerType();
		// materialRegional.setSapSendFlag(false);
		materialRegional.setDeletionFlag(false);
		if (PRICER_TYPE.PARTMASTER.getName().equalsIgnoreCase(materialTypeStr)) {
			Map<Object, Object> categoryMap = puBean.getCategoryMap();
			materialRegional.setProductCategory((ProductCategory) categoryMap.get(pricr.getProductCat()));
			materialRegional.setSalesCostFlag("YES".equalsIgnoreCase(pricr.getSalesCostFlag()));
			//may can be deleted this.
		} else if (PRICER_TYPE.NORMAL.getName().equalsIgnoreCase(materialTypeStr)) {
			Map<Object, Object> categoryMap = puBean.getCategoryMap();
			materialRegional.setProductCategory((ProductCategory) categoryMap.get(pricr.getProductCat()));
		}
		if (pricr.getProductGroup1() != null && !pricr.getProductGroup1().equals("")) {
			Map<Object, Object> groupMap1 = puBean.getGroupMap1();
			materialRegional.setProductGroup1((ProductGroup) groupMap1.get(pricr.getProductGroup1().toUpperCase()));
		} else {
			//if (pricr.getProgramEffectiveDate() == null || pricr.getProgramEffectiveDate().equals("")) {
			materialRegional.setProductGroup1(null);
			//}
		}

		// fix the missed function when the mfr and region can be found mapping
		// in table PG_MASTER_MAPPING, then to do product group 2 updating
		// June Guo 20150519
		if (pricr.getProductGroup2() != null && !pricr.getProductGroup2().equals("")) {
			// if(pricr.isCanUpdatePG2()) {
			Map<Object, Object> groupMap2 = puBean.getGroupMap2();
			materialRegional.setProductGroup2((ProductGroup) groupMap2.get(pricr.getProductGroup2().toUpperCase()));
			// }
		} else {
			//if (pricr.getProgramEffectiveDate() == null || pricr.getProgramEffectiveDate().equals("")) {
			materialRegional.setProductGroup2(null);
			//}
		}

		if (!QuoteUtil.isEmpty(pricr.getProductGroup3())) {
			materialRegional.setProductGroup3(pricr.getProductGroup3());
		}else {
			materialRegional.setProductGroup3(null);
		}

		if (!QuoteUtil.isEmpty(pricr.getProductGroup4())) {
			materialRegional.setProductGroup4(pricr.getProductGroup4());
		}else {
			materialRegional.setProductGroup4(null);
		}
	}

	/**
	 * Except [MFR] and [Full MFR Part Number] in Material table, [Full MFR Part
	 * Number], [Cost Indicator] and [Material Category] in Material_Detail
	 * table, all other fields will be refreshed into new values if this
	 * uploading is an update action.
	 */
	public void updateMaterial(String materialTypeStr, PricerUploadTemplateBean uploadBean, Material mtl, User user,
			PricerUploadParametersBean puBean) {
		if (uploadBean != null) {
			// LOG.info("insert pricer to material");
			String aFlag = uploadBean.getAllocationFlag();
			if ((aFlag != null) && (!aFlag.equals(""))) {
				aFlag = aFlag.trim();
				if (aFlag.equalsIgnoreCase("Yes")) {
					mtl.setAllocationFlag(true);
				} else {
					mtl.setAllocationFlag(false);
				}
			}
			//null or blank should not override this two fields PackageType,PackagingType
			/*if (!QuoteUtil.isEmpty(uploadBean.getPackageType())) {
				mtl.setPackageType(uploadBean.getPackageType());
			}
			if (!QuoteUtil.isEmpty(uploadBean.getPackagingType())) {
				mtl.setPackagingType(uploadBean.getPackagingType());
			}*/
			mtl.setLastUpdatedBy(user.getEmployeeId());
			mtl.setLastUpdatedOn(new Date());

			if (PRICER_TYPE.PARTMASTER.getName().equalsIgnoreCase(materialTypeStr)) {
				/*
				 * Map<Object, Object> categoryMap = puBean.getCategoryMap();
				 * mtl.setProductCategory((ProductCategory)
				 * categoryMap.get(pricr.getProductCat()));
				 */
				mtl.setMaterialType("PARTMASTER");
			}
			/*
			 * else if
			 * (PRICER_TYPE.NORMAL.getName().equalsIgnoreCase(materialTypeStr))
			 * { Map<Object, Object> categoryMap = puBean.getCategoryMap();
			 * //mtl.setProductCategory((ProductCategory)
			 * categoryMap.get(pricr.getProductCat())); //reviewlist
			 * mtl.setMaterialType("NORMAL"); } else
			 * if(PRICER_TYPE.PROGRAM.getName().equalsIgnoreCase(materialTypeStr
			 * )) { ////reviewlist mtl.setMaterialType("PROGRAM"); }
			 */
			mtl.setValid(true);
			em.merge(mtl);
		}
	}

	/**
	 * insertNormalPricerToMaterialDetail according to material_id,region and
	 * cost_indicator to insert table materialDetail.
	 * 
	 * @param pricr
	 * @param mtl
	 * @throws ParseException
	 */
	public void insertNormalPricerToMaterialDetail(PricerUploadTemplateBean pricr, Material mtl, User user,
			PricerUploadParametersBean puBean) throws ParseException {
		// LOG.info("insert Normal pricer to material detail");
		BizUnit bz = user.getDefaultBizUnit();
		Map<Object, CostIndicator> costIdtMap = puBean.getCostIdtMap();

		NormalPricer mtrDtl = new NormalPricer();
		PricerUtils.setQedAllInMaterialDetail(pricr, mtrDtl);

		/*
		 * if (pricr.getAvailableToSellMoreFlag() != null) { if
		 * (pricr.getAvailableToSellMoreFlag().equalsIgnoreCase("YES")) {
		 * mtrDtl.setAvailableToSellMoreFlag(true); } else {
		 * mtrDtl.setAvailableToSellMoreFlag(false); } } else {
		 * mtrDtl.setAvailableToSellMoreFlag(false); }
		 */
		mtrDtl.setPartDescription(pricr.getDescription());
		mtrDtl.setAvnetQcComments(pricr.getAvnetQuoteCentreComments());
		mtrDtl.setCostIndicator(costIdtMap.get(pricr.getCostIndicator()));
		mtrDtl.setMaterial(mtl);
		mtrDtl.setCreatedOn(new Date());
		mtrDtl.setBizUnitBean(bz);
		mtrDtl.setCreatedBy(user.getEmployeeId());
		mtrDtl.setCostIndicator(costIdtMap.get(pricr.getCostIndicator()));
		mtrDtl.setFullMfrPartNumber(pricr.getFullMfrPart());
		mtrDtl.setPriceListRemarks1(pricr.getPriceListRemarks());
		mtrDtl.setPriceListRemarks2(pricr.getPriceListRemarks2());
		mtrDtl.setPriceListRemarks3(pricr.getPriceListRemarks3());
		mtrDtl.setPriceListRemarks4(pricr.getPriceListRemarks4());

		if ((pricr.getMinSell() != null) && (!pricr.getMinSell().equals(""))) {
			mtrDtl.setMinSellPrice(Double.parseDouble(pricr.getMinSell()));
		}
		//mtrDtl.setMinSellPriceM(new Money(pricr.getCurrency(), pricr.getMinSell()));
		if ((pricr.getCost() != null) && (!pricr.getCost().equals(""))) {
			mtrDtl.setCost(Double.parseDouble(pricr.getCost()));
		}
		mtrDtl.setCurrency(Currency.valueOf(pricr.getCurrency()));
		//mtrDtl.setCostM(new Money(pricr.getCurrency(), pricr.getCost()));

		if ((pricr.getBottomPrice() != null) && (!pricr.getBottomPrice().equals(""))) {
			mtrDtl.setBottomPrice(Double.parseDouble(pricr.getBottomPrice()));
		}

		mtrDtl.setLeadTime(pricr.getLeadTime());
		if ((pricr.getMPQ() != null) && (!pricr.getMPQ().equals(""))) {
			mtrDtl.setMpq(Integer.parseInt(pricr.getMPQ()));
		}

		if ((pricr.getMOQ() != null) && (!pricr.getMOQ().equals(""))) {
			mtrDtl.setMoq(Integer.parseInt(pricr.getMOQ()));
		}

		if ((pricr.getMOV() != null) && (!pricr.getMOV().equals(""))) {
			mtrDtl.setMov(Integer.parseInt(pricr.getMOV()));
		}

		if (pricr.getShipmentValidity() != null && !pricr.getShipmentValidity().equals("")) {
			Date date = QuoteUtil.convertStringToDate(pricr.getShipmentValidity(), "dd/MM/yyyy");
			mtrDtl.setShipmentValidity(date);
		}

		mtrDtl.setCostIndicator(costIdtMap.get(pricr.getCostIndicator()));
		mtrDtl.setTermsAndConditions(pricr.getTermsAndConditions());

		mtrDtl.setLastUpdatedOn(new Date());
		mtrDtl.setLastUpdatedBy(user.getEmployeeId());
		mtrDtl.setPriceValidity(pricr.getValidity());

		// mtrDtl.setQtyIndicator(pricr.getQtyIndicator());
		/*
		 * String aFlag = pricr.getAllocationFlag(); if ((aFlag != null) &&
		 * (!aFlag.equals(""))) { aFlag = aFlag.trim(); if
		 * (aFlag.equalsIgnoreCase("Yes")) { mtrDtl.setAllocationFlag(true); }
		 * else { mtrDtl.setAllocationFlag(false); } }
		 */

		em.persist(mtrDtl);

	}

	/**
	 * insertProgramPricerToMaterialDetail according to material_id,region and
	 * cost_indicator to insert table materialDetail.
	 * 
	 * @param pricr
	 * @param mtl
	 * @throws ParseException
	 */
	public void insertProgramPricerToMaterialDetail(PricerUploadTemplateBean pricr, Material mtl, User user,
			PricerUploadParametersBean puBean) throws ParseException {
		// LOG.info("insert ProgramPricer to material detail");

		BizUnit bz = user.getDefaultBizUnit();
		Map<Object, CostIndicator> costIdtMap = puBean.getCostIdtMap();

		ProgramPricer mtrDtl = new ProgramPricer();
		PricerUtils.setQedAllInMaterialDetail(pricr, mtrDtl);

		if (pricr.getAvailableToSellMoreFlag() != null) {
			if ("YES".equalsIgnoreCase(pricr.getAvailableToSellMoreFlag())) {
				mtrDtl.setAvailableToSellMoreFlag(true);
			} else {
				mtrDtl.setAvailableToSellMoreFlag(false);
			}
		} else {
			mtrDtl.setAvailableToSellMoreFlag(false);
		}
		if ((pricr.getCost() != null) && (!pricr.getCost().equals(""))) {
			mtrDtl.setCost(Double.parseDouble(pricr.getCost()));
		}
		mtrDtl.setCurrency(Currency.valueOf(pricr.getCurrency()));
		//mtrDtl.setCostM(new Money(pricr.getCurrency(), pricr.getCost()));

		// mtrDtl.setDescription(pricr.getDescription());

		if (!QuoteUtil.isEmpty(pricr.getDisplayOnTop())) {
			mtrDtl.setDisplayOnTop(Integer.parseInt(pricr.getDisplayOnTop()));
		}
		mtrDtl.setResaleIndicator(pricr.getResaleIndicator());
		String nidr = pricr.getNewItemIndicator();
		if ((nidr != null) && (!nidr.equals(""))) {
			nidr = nidr.trim();
			if (nidr.equalsIgnoreCase("Yes")) {
				mtrDtl.setNewItemIndicator(true);
			} else {
				mtrDtl.setNewItemIndicator(false);
			}
		}

		Map<Object, Object> pgmTypeMap = puBean.getPgmTypeMap();
		if (pricr.getProgram() != null) {
			mtrDtl.setProgramType((ProgramType) pgmTypeMap.get(pricr.getProgram().toUpperCase()));
		}

		if ((pricr.getSpecialItemIndicator() != null) && (!pricr.getSpecialItemIndicator().equals(""))) {
			mtrDtl.setSpecialItemFlag(Integer.parseInt(pricr.getSpecialItemIndicator()));
		}

		
		  String aq = pricr.getAQFlag(); 
		  if ((aq != null) && (!aq.equals(""))) { 
			  aq = aq.trim(); 
			  if (aq.equalsIgnoreCase("Yes")) {
				 mtrDtl.setAqFlag(true); 
			  } else { 
				  mtrDtl.setAqFlag(false); 
			 } 
		  }
		 

		/*
		 * String cfq = pricr.getCallForQuote(); if ((cfq != null) &&
		 * (!cfq.equals(""))) { cfq = cfq.trim(); if
		 * (cfq.equalsIgnoreCase("Yes")) { mtrDtl.setProgramCallForQuote(true);
		 * } else { mtrDtl.setProgramCallForQuote(false); } }
		 */

		if (pricr.getProgramClosingDate() != null && !pricr.getProgramClosingDate().equals("")) {
			mtrDtl.setProgramClosingDate(QuoteUtil.convertStringToDate(pricr.getProgramClosingDate(), "dd/MM/yyyy"));
		}

		if (pricr.getProgramEffectiveDate() != null && !pricr.getProgramEffectiveDate().equals("")) {
			mtrDtl.setProgramEffectiveDate(
					QuoteUtil.convertStringToDate(pricr.getProgramEffectiveDate(), "dd/MM/yyyy"));
		}

		if ((pricr.getAvailabletoSellQuantity() != null) && (!pricr.getAvailabletoSellQuantity().equals(""))) {
			mtrDtl.setAvailableToSellQty(Integer.parseInt(pricr.getAvailabletoSellQuantity()));
		}

		mtrDtl.setCostIndicator(costIdtMap.get(pricr.getCostIndicator()));
		mtrDtl.setMaterial(mtl);
		mtrDtl.setCreatedOn(new Date());
		mtrDtl.setBizUnitBean(bz);
		mtrDtl.setCreatedBy(user.getEmployeeId());
		mtrDtl.setFullMfrPartNumber(pricr.getFullMfrPart());
		mtrDtl.setLeadTime(pricr.getLeadTime());
		if ((pricr.getMPQ() != null) && (!pricr.getMPQ().equals(""))) {
			mtrDtl.setMpq(Integer.parseInt(pricr.getMPQ()));
		}

		if ((pricr.getMOQ() != null) && (!pricr.getMOQ().equals(""))) {
			mtrDtl.setMoq(Integer.parseInt(pricr.getMOQ()));
		} else if ((pricr.getMOV() != null) && (!pricr.getMOV().equals("")) && (pricr.getCost() != null)
				&& (!pricr.getCost().equals("")) && (pricr.getMPQ() != null) && (!pricr.getMPQ().equals(""))) {

			mtrDtl.setMoq(PricerUtils.moqCalculate(Double.parseDouble(pricr.getMOV()),
					Double.parseDouble(pricr.getCost()), Integer.parseInt(pricr.getMPQ())));
		}
		if ((pricr.getMOV() != null) && (!pricr.getMOV().equals(""))) {
			mtrDtl.setMov(Integer.parseInt(pricr.getMOV()));
		}

		if (pricr.getShipmentValidity() != null && !pricr.getShipmentValidity().equals("")) {
			Date date = QuoteUtil.convertStringToDate(pricr.getShipmentValidity(), "dd/MM/yyyy");
			mtrDtl.setShipmentValidity(date);
		}

		mtrDtl.setCostIndicator(costIdtMap.get(pricr.getCostIndicator()));
		mtrDtl.setTermsAndConditions(pricr.getTermsAndConditions());

		mtrDtl.setLastUpdatedOn(new Date());
		mtrDtl.setLastUpdatedBy(user.getEmployeeId());
		mtrDtl.setPriceValidity(pricr.getValidity());
		mtrDtl.setQtyIndicator(pricr.getQtyIndicator());
		String aFlag = pricr.getAllocationFlag();
		if ((aFlag != null) && (!aFlag.equals(""))) {
			aFlag = aFlag.trim();
			if (aFlag.equalsIgnoreCase("Yes")) {
				mtrDtl.setAllocationFlag(true);
			} else {
				mtrDtl.setAllocationFlag(false);
			}
		}

		mtrDtl.setAvnetQcComments(pricr.getAvnetQuoteCentreComments());

		List<QuantityBreakPrice> qpLst = new ArrayList<QuantityBreakPrice>();

		if ((pricr.getQuantityBreak1() != null) && (!pricr.getQuantityBreak1().equals(""))
				&& (pricr.getQuantityBreakPrice1() != null) && (!pricr.getQuantityBreakPrice1().equals(""))) {
			QuantityBreakPrice qp = new QuantityBreakPrice();
			qp.setQuantityBreak(Integer.parseInt(pricr.getQuantityBreak1()));
			qp.setQuantityBreakPrice(Double.parseDouble(pricr.getQuantityBreakPrice1()));
			qpLst.add(qp);
		}

		if ((pricr.getQuantityBreak2() != null) && (!pricr.getQuantityBreak2().equals(""))) {
			QuantityBreakPrice qp = new QuantityBreakPrice();
			qp.setQuantityBreak(Integer.parseInt(pricr.getQuantityBreak2()));
			if ((pricr.getQuantityBreakPrice2() != null) && (!pricr.getQuantityBreakPrice2().equals(""))) {
				qp.setQuantityBreakPrice(Double.parseDouble(pricr.getQuantityBreakPrice2()));
			} else {
				qp.setQuantityBreakPrice(null);
			}
			qpLst.add(qp);
		}

		if ((pricr.getQuantityBreak3() != null) && (!pricr.getQuantityBreak3().equals(""))) {
			QuantityBreakPrice qp = new QuantityBreakPrice();
			qp.setQuantityBreak(Integer.parseInt(pricr.getQuantityBreak3()));
			if ((pricr.getQuantityBreakPrice3() != null) && (!pricr.getQuantityBreakPrice3().equals(""))) {
				qp.setQuantityBreakPrice(Double.parseDouble(pricr.getQuantityBreakPrice3()));
			} else {
				qp.setQuantityBreakPrice(null);
			}
			qpLst.add(qp);
		}

		if ((pricr.getQuantityBreak4() != null) && (!pricr.getQuantityBreak4().equals(""))) {
			QuantityBreakPrice qp = new QuantityBreakPrice();
			qp.setQuantityBreak(Integer.parseInt(pricr.getQuantityBreak4()));
			if ((pricr.getQuantityBreakPrice4() != null) && (!pricr.getQuantityBreakPrice4().equals(""))) {
				qp.setQuantityBreakPrice(Double.parseDouble(pricr.getQuantityBreakPrice4()));
			} else {
				qp.setQuantityBreakPrice(null);
			}
			qpLst.add(qp);
		}

		if ((pricr.getQuantityBreak5() != null) && (!pricr.getQuantityBreak5().equals(""))) {
			QuantityBreakPrice qp = new QuantityBreakPrice();
			qp.setQuantityBreak(Integer.parseInt(pricr.getQuantityBreak5()));
			if ((pricr.getQuantityBreakPrice5() != null) && (!pricr.getQuantityBreakPrice5().equals(""))) {
				qp.setQuantityBreakPrice(Double.parseDouble(pricr.getQuantityBreakPrice5()));
			} else {
				qp.setQuantityBreakPrice(null);
			}
			qpLst.add(qp);
		}

		/*
		 * if ((pricr.getQuantityBreak6() != null) &&
		 * (!pricr.getQuantityBreak6().equals(""))) { QuantityBreakPrice qp =
		 * new QuantityBreakPrice();
		 * qp.setQuantityBreak(Integer.parseInt(pricr.getQuantityBreak6())); if
		 * ((pricr.getQuantityBreakPrice6() != null) &&
		 * (!pricr.getQuantityBreakPrice6().equals(""))) {
		 * qp.setQuantityBreakPrice(Double.parseDouble(pricr.
		 * getQuantityBreakPrice6())); } else { qp.setQuantityBreakPrice(null);
		 * } qpLst.add(qp); }
		 * 
		 * if ((pricr.getQuantityBreak7() != null) &&
		 * (!pricr.getQuantityBreak7().equals(""))) { QuantityBreakPrice qp =
		 * new QuantityBreakPrice();
		 * qp.setQuantityBreak(Integer.parseInt(pricr.getQuantityBreak7())); if
		 * ((pricr.getQuantityBreakPrice7() != null) &&
		 * (!pricr.getQuantityBreakPrice7().equals(""))) {
		 * qp.setQuantityBreakPrice(Double.parseDouble(pricr.
		 * getQuantityBreakPrice7())); } else { qp.setQuantityBreakPrice(null);
		 * } qpLst.add(qp); }
		 * 
		 * if ((pricr.getQuantityBreak8() != null) &&
		 * (!pricr.getQuantityBreak8().equals(""))) { QuantityBreakPrice qp =
		 * new QuantityBreakPrice();
		 * qp.setQuantityBreak(Integer.parseInt(pricr.getQuantityBreak8())); if
		 * ((pricr.getQuantityBreakPrice8() != null) &&
		 * (!pricr.getQuantityBreakPrice8().equals(""))) {
		 * qp.setQuantityBreakPrice(Double.parseDouble(pricr.
		 * getQuantityBreakPrice8())); } else { qp.setQuantityBreakPrice(null);
		 * } qpLst.add(qp); }
		 * 
		 * if ((pricr.getQuantityBreak9() != null) &&
		 * (!pricr.getQuantityBreak9().equals(""))) { QuantityBreakPrice qp =
		 * new QuantityBreakPrice();
		 * qp.setQuantityBreak(Integer.parseInt(pricr.getQuantityBreak9())); if
		 * ((pricr.getQuantityBreakPrice9() != null) &&
		 * (!pricr.getQuantityBreakPrice9().equals(""))) {
		 * qp.setQuantityBreakPrice(Double.parseDouble(pricr.
		 * getQuantityBreakPrice9())); } else { qp.setQuantityBreakPrice(null);
		 * } qpLst.add(qp); }
		 * 
		 * if ((pricr.getQuantityBreak10() != null) &&
		 * (!pricr.getQuantityBreak10().equals(""))) { QuantityBreakPrice qp =
		 * new QuantityBreakPrice();
		 * qp.setQuantityBreak(Integer.parseInt(pricr.getQuantityBreak10())); if
		 * ((pricr.getQuantityBreakPrice10() != null) &&
		 * (!pricr.getQuantityBreakPrice10().equals(""))) {
		 * qp.setQuantityBreakPrice(Double.parseDouble(pricr.
		 * getQuantityBreakPrice10())); } else { qp.setQuantityBreakPrice(null);
		 * } qpLst.add(qp); }
		 */

		mtrDtl.setQuantityBreakPrices(qpLst);

		em.persist(mtrDtl);

		for (QuantityBreakPrice abp : qpLst) {
			abp.setMaterialDetail(mtrDtl);
			em.persist(abp);
		}

	}

	/**
	 * updateProgramPricerMaterialDetail According to material_id,region and
	 * cost_indicator to update table.
	 * 
	 * @param pricr
	 * @param user
	 * @throws ParseException
	 */
	public void updateProgramPricerMaterialDetail(PricerUploadTemplateBean pricr, User user,
			PricerUploadParametersBean puBean) throws ParseException {
		// LOG.info("update Program pricer at material detail");
		ProgramPricer mDetail = null;
		if (pricr.getMetarialDetailId() <= 0) {
			String pricerType = PRICER_TYPE.PROGRAM.getName();
			mDetail = this.findMaterialDetailByQED(pricr, user, pricr.getQuotationEffectiveDate(), pricerType);
		} else {
			mDetail = em.find(ProgramPricer.class, pricr.getMetarialDetailId());
		}
		if (null != mDetail) {
			// ProgramMaterial mDetail =
			// PricerUtils.getProgramMaterial(mtlDtlLst,
			// pricr.getMetarialDetailId());
			PricerUtils.setQedAllInMaterialDetail(pricr, mDetail);

			BizUnit bz = user.getDefaultBizUnit();
			Map<Object, CostIndicator> costIdtMap = puBean.getCostIdtMap();
			Map<Object, Object> pgmTypeMap = puBean.getPgmTypeMap();

			if (pricr.getAvailableToSellMoreFlag() != null) {
				if ("YES".equalsIgnoreCase(pricr.getAvailableToSellMoreFlag())) {
					mDetail.setAvailableToSellMoreFlag(true);
				} else {
					mDetail.setAvailableToSellMoreFlag(false);
				}
			} else {
				mDetail.setAvailableToSellMoreFlag(false);
			}
			//mDetail.setCostM(new Money(pricr.getCurrency(), pricr.getCost()));
			if (!QuoteUtil.isEmpty(pricr.getCost())) {
				mDetail.setCost(Double.parseDouble(pricr.getCost()));
			} else {
				mDetail.setCost(null);
			}
			mDetail.setCurrency(Currency.valueOf(pricr.getCurrency()));
			// mDetail.setDescription(pricr.getDescription());

			mDetail.setResaleIndicator(pricr.getResaleIndicator());

			String nidr = pricr.getNewItemIndicator();
			if ((nidr != null) && (!nidr.equals(""))) {
				nidr = nidr.trim();
				if (nidr.equalsIgnoreCase("Yes")) {
					mDetail.setNewItemIndicator(true);
				} else {
					mDetail.setNewItemIndicator(false);
				}
			} else {
				mDetail.setNewItemIndicator(false);
			}

			if (pricr.getProgram() != null) {
				mDetail.setProgramType((ProgramType) pgmTypeMap.get(pricr.getProgram().toUpperCase()));
			}
			// Added by Punit for CPD exercise
			mDetail = CommodityUtil.updateProgramPricerMaterialDetailFromPUTBean(pricr, mDetail);

			mDetail.setMpq(new Integer(pricr.getMPQ()));
			mDetail.setBizUnitBean(bz);
			mDetail.setCostIndicator(costIdtMap.get(pricr.getCostIndicator()));
			mDetail.setFullMfrPartNumber(pricr.getFullMfrPart());

			mDetail.setLeadTime(pricr.getLeadTime());

			if ((pricr.getMPQ() != null) && (!pricr.getMPQ().equals(""))) {
				mDetail.setMpq(Integer.parseInt(pricr.getMPQ()));
			} else {
				mDetail.setMpq(null);
			}

			if ((pricr.getMOQ() != null) && (!pricr.getMOQ().equals(""))) {
				mDetail.setMoq(Integer.parseInt(pricr.getMOQ()));
			} else if ((pricr.getMOV() != null) && (!pricr.getMOV().equals("")) && (pricr.getCost() != null)
					&& (!pricr.getCost().equals("")) && (pricr.getMPQ() != null) && (!pricr.getMPQ().equals(""))) {

				mDetail.setMoq(PricerUtils.moqCalculate(Double.parseDouble(pricr.getMOV()),
						Double.parseDouble(pricr.getCost()), Integer.parseInt(pricr.getMPQ())));
			}
			if (!QuoteUtil.isEmpty(pricr.getDisplayOnTop())) {
				mDetail.setDisplayOnTop(Integer.parseInt(pricr.getDisplayOnTop()));
			}

			if ((pricr.getMOV() != null) && (!pricr.getMOV().equals(""))) {
				mDetail.setMov(Integer.parseInt(pricr.getMOV()));
			} else {
				mDetail.setMov(null);
			}

			if ((pricr.getShipmentValidity() != null) && (!pricr.getShipmentValidity().equals(""))) {
				mDetail.setShipmentValidity(QuoteUtil.convertStringToDate(pricr.getShipmentValidity(), "dd/MM/yyyy"));
			} else {
				mDetail.setShipmentValidity(null);
			}

			mDetail.setCostIndicator(costIdtMap.get(pricr.getCostIndicator()));
			mDetail.setTermsAndConditions(pricr.getTermsAndConditions());

			mDetail.setLastUpdatedOn(new Date());
			mDetail.setLastUpdatedBy(user.getEmployeeId());
			mDetail.setPriceValidity(pricr.getValidity());
			mDetail.setQtyIndicator(pricr.getQtyIndicator());
			String aFlag = pricr.getAllocationFlag();
			if ((aFlag != null) && (!aFlag.equals(""))) {
				aFlag = aFlag.trim();
				if (aFlag.equalsIgnoreCase("Yes")) {
					mDetail.setAllocationFlag(true);
				} else {
					mDetail.setAllocationFlag(false);
				}
			} else {
				mDetail.setAllocationFlag(false);
			}

			mDetail.setAvnetQcComments(pricr.getAvnetQuoteCentreComments());

			removeQuantityBreakPrice(pricr);

			List<QuantityBreakPrice> qpLst = CommodityUtil.updatePPMDQuantityBreakPriceList(mDetail, pricr, em);
			mDetail.setQuantityBreakPrices(qpLst);
			em.merge(mDetail);
		}

	}

	/**
	 * updateNormalPricerMaterialDetail According to material_id,region and
	 * cost_indicator to update table.
	 * 
	 * @param pricr
	 * @param user
	 * @throws ParseException
	 */
	public void updateNormalPricerMaterialDetail(PricerUploadTemplateBean pricr, User user,
			PricerUploadParametersBean puBean) throws ParseException {
		// LOG.info("update Normal pricer at material detail");
		NormalPricer mDtl = null;
		if (pricr.getMetarialDetailId() <= 0) {
			String pricerType = PRICER_TYPE.NORMAL.getName();
			mDtl = this.findMaterialDetailofQED(pricr, user, pricr.getQuotationEffectiveDate(), pricerType);
		} else {
			mDtl = em.find(NormalPricer.class, pricr.getMetarialDetailId());
		}
		if (null != mDtl) {

			// MaterialDetail mDtl = PricerUtils.getMaterialDetail(mtlDtlLst,
			// pricr.getMetarialDetailId());
			PricerUtils.setQedAllInMaterialDetail(pricr, mDtl);
			BizUnit bz = user.getDefaultBizUnit();
			Map<Object, CostIndicator> costIdtMap = puBean.getCostIdtMap();
			/*
			 * if (pricr.getAvailableToSellMoreFlag() != null) { if
			 * (pricr.getAvailableToSellMoreFlag().equalsIgnoreCase("YES")) {
			 * mDtl.setAvailableToSellMoreFlag(true); } else {
			 * mDtl.setAvailableToSellMoreFlag(false); } } else {
			 * mDtl.setAvailableToSellMoreFlag(false); }
			 */
			mDtl.setPartDescription(pricr.getDescription());
			mDtl.setAvnetQcComments(pricr.getAvnetQuoteCentreComments());
			mDtl.setBizUnitBean(bz);
			mDtl.setCostIndicator(costIdtMap.get(pricr.getCostIndicator()));
			mDtl.setFullMfrPartNumber(pricr.getFullMfrPart());

			// Added by Punit for CPD Exercise
			mDtl = CommodityUtil.pricerMasterEnquiryDetails(pricr, mDtl, user.getEmployeeId());

			mDtl.setCostIndicator(costIdtMap.get(pricr.getCostIndicator()));

			em.merge(mDtl);
		}
	}

	@Override
	public void removeOnlyPricerOperater(List<PricerUploadTemplateBean> items, String pricerType, User user,
			int[] countArr) {
		this.removeOnlyMaterialDetail(items, user, countArr, pricerType);
	}

	private void removeOnlyMaterialDetail(List<PricerUploadTemplateBean> beans, User user, int[] countArr,
			String pricerType) {

		StringBuffer sb = new StringBuffer("(");
		for (PricerUploadTemplateBean bean : beans) {
			sb.append(bean.getMetarialDetailId()).append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(")");
		String deleteQuantityBreakPrice = "DELETE FROM QuantityBreakPrice e WHERE e.materialDetail.id in "
				+ sb.toString();
		Query deleteQbpQuery = em.createQuery(deleteQuantityBreakPrice);
		deleteQbpQuery.executeUpdate();
		String hql = "DELETE FROM Pricer e WHERE e.id in " + sb.toString();
		Query query = em.createQuery(hql);
		int rowCount = query.executeUpdate();
		if (PRICER_TYPE.NORMAL.getName().equalsIgnoreCase(pricerType)) {
			countArr[0] = countArr[0] + rowCount;
		} else if (PRICER_TYPE.PROGRAM.getName().equalsIgnoreCase(pricerType)) {
			countArr[1] = countArr[1] + rowCount;
		}

	}

	public List<VerifyEffectiveDateResult> batchFindMaterialDetailForVerify(List<PricerUploadTemplateBean> lists,
			User user, String pricerType, List<Manufacturer> mfrLst) {
		List<VerifyEffectiveDateResult> results = new LinkedList<VerifyEffectiveDateResult>();
		int size = lists.size();
		int batchCount = size / PricerConstant.BATCH_SIZE;
		int mod = size % PricerConstant.BATCH_SIZE;
		if (mod != 0) {
			batchCount++;
		}

		List<PricerUploadTemplateBean> subItemLst = null;

		for (int m = 0; m < batchCount; m++) {
			if (PricerConstant.BATCH_SIZE * (m + 1) > size) {
				subItemLst = lists.subList(m * PricerConstant.BATCH_SIZE, size);
			} else {
				subItemLst = lists.subList(m * PricerConstant.BATCH_SIZE, PricerConstant.BATCH_SIZE * (m + 1));
			}

			List<VerifyEffectiveDateResult> pricerList = batchFindMaterialDetail(subItemLst, user, pricerType, mfrLst);
			LOG.info("batchFindMaterialDetailForVerify:results.size():::" +(results.size()+pricerList.size()));
			results.addAll(pricerList);
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<VerifyEffectiveDateResult> batchFindMaterialDetail(List<PricerUploadTemplateBean> beans, User user,
			String pricerType, List<Manufacturer> mfrLst) {

		long start = System.currentTimeMillis();
		List<VerifyEffectiveDateResult> results = new ArrayList<VerifyEffectiveDateResult>();
		BizUnit bz = user.getDefaultBizUnit();
		/** method 1 */
		Set<String> fullPartNumSet = new HashSet<String>();
		Set<String> costIndicatorSet = new HashSet<String>();
		Set<Long> mftIdSet = new HashSet<Long>();
		Set<String> currencySet = new HashSet<String>();
		// the material has region fields and can be update by? TODO
		Set<String> bizUnitSet = new HashSet<String>();
		if (!PRICER_TYPE.PARTMASTER.getName().equals(pricerType)) {
			for (PricerUploadTemplateBean pBean : beans) {
				String fullMfrPart = PricerUtils.escapeSQLIllegalCharacters(pBean.getFullMfrPart());
				fullPartNumSet.add(fullMfrPart);
				costIndicatorSet.add(pBean.getCostIndicator());
				currencySet.add(pBean.getCurrency());
				for (Manufacturer mfr : mfrLst) {
					if (mfr.getName().equalsIgnoreCase(pBean.getMfr())) {
						mftIdSet.add(mfr.getId());
						break;
					}
				}
			}
		} else {
			for (PricerUploadTemplateBean pBean : beans) {
				String fullMfrPart = PricerUtils.escapeSQLIllegalCharacters(pBean.getFullMfrPart());
				fullPartNumSet.add(fullMfrPart);
				costIndicatorSet.add(pBean.getCostIndicator());
				bizUnitSet.add(pBean.getRegion());
				for (Manufacturer mfr : mfrLst) {
					if (mfr.getName().equalsIgnoreCase(pBean.getMfr())) {
						mftIdSet.add(mfr.getId());
						break;
					}
				}
			}
		}
		if (mftIdSet.size() == 0 || fullPartNumSet.size() == 0 ||
				costIndicatorSet.size() == 0 || currencySet.size()==0){
			return results;
		}
		String inMfr = PricerUtils.buildInSql(mftIdSet);
		String inFullPartNum = PricerUtils.buildInSql(fullPartNumSet);
		String inCostindicator = PricerUtils.buildInSql(costIndicatorSet);
		//String inbizUnitSet = PricerUtils.buildInSql(bizUnitSet);
		String incurrency = PricerUtils.buildInSql(currencySet);
		StringBuffer sb = new StringBuffer();
		// TODO t2.MATERIAL_CATEGORY=
		sb.append(" SELECT t2.ID, t2.PRICER_TYPE,  t2.DISPLAY_QUOTE_EFF_DATE, t2.FULL_MFR_PART_NUMBER, ");
		sb.append(
				" t2.PRICE_VALIDITY, t2.QUOTATION_EFFECTIVE_DATE, t2.QUOTATION_EFFECTIVE_TO,  t2.BIZ_UNIT, t2.COST_INDICATOR, t2.MATERIAL_ID,t1.MANUFACTURER_ID,t3.NAME ");
		sb.append(", t2.CURRENCY ");
		// sb.append(" FROM PRICER t2, MATERIAL t1 where t1.ID = t2.MATERIAL_ID
		// and t2.BIZ_UNIT='"+bz.getName()+"'");
		// TODO NOT CONFIRM
		/*
		 * if(PRICER_TYPE.PARTMASTER.getName().equals(pricerType)) { sb.
		 * append("  FROM PRICER t2, MATERIAL t1  where t1.ID = t2.MATERIAL_ID  and t2.BIZ_UNIT in "
		 * + inbizUnitSet); }else { sb.
		 * append("  FROM PRICER t2, MATERIAL t1  where t1.ID = t2.MATERIAL_ID  and t2.BIZ_UNIT='"
		 * +bz.getName()+"'"); }
		 */
		sb.append(
				"  FROM PRICER t2 left join program_type t3 on t2.program_type_id = t3.id, MATERIAL t1  where t1.ID = t2.MATERIAL_ID  and t2.BIZ_UNIT='"
						+ bz.getName() + "'");
		sb.append(" and t2.PRICER_TYPE='" + pricerType + "' and t1.MANUFACTURER_ID in " + inMfr
				+ " and t1.FULL_MFR_PART_NUMBER in " + inFullPartNum + " and " + " t2.COST_INDICATOR in "
				+ inCostindicator + "");
		sb.append(" AND T2.CURRENCY in "+incurrency+"");
		Query query = em.createNativeQuery(sb.toString());
		List<?> nlist = query.getResultList();
		// Added by Punit for CPD Exercise
		results = CommodityUtil.verifyEffectiveDateResult(results, mfrLst, nlist, null);

		long end = System.currentTimeMillis();
		LOG.info("batchFindMaterialDetail take " + (end - start) + "ms");
		return results;
	}

	public ProgramPricer findMaterialDetailByQED(PricerUploadTemplateBean bean, User user, String qedInFile,
			String pricerType) {
		BizUnit bz = user.getDefaultBizUnit();

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProgramPricer> c = cb.createQuery(ProgramPricer.class);

		Root<ProgramPricer> materialDetail = c.from(ProgramPricer.class);
		Join<ProgramPricer, BizUnit> bizUnit = materialDetail.join("bizUnitBean");
		Join<ProgramPricer, CostIndicator> costIndicator = materialDetail.join("costIndicator");
		Join<ProgramPricer, Material> material = materialDetail.join("material");
		Join<Material, Manufacturer> manufacturer = material.join("manufacturer");

		List<Predicate> criterias = new ArrayList<Predicate>();
		Predicate p1 = cb.equal(manufacturer.<String>get("name"), bean.getMfr());
		Predicate p2 = cb.equal(material.<String>get("fullMfrPartNumber"), bean.getFullMfrPart());
		Predicate p3 = cb.equal(costIndicator.<String>get("name"), bean.getCostIndicator());
		Predicate p4 = cb.equal(bizUnit.<String>get("name"), bz.getName());
		Predicate p5 = cb.equal(materialDetail.<Currency>get("currency"), Currency.valueOf(bean.getCurrency()));
		// Predicate p5 = cb.equal(materialDetail.<String>
		// get("materialCategory"), pricerType.toUpperCase());
		criterias.add(p1);
		criterias.add(p2);
		criterias.add(p3);
		criterias.add(p4);
		criterias.add(p5);
		// criterias.add(p5);
		// this logic
		if (!QuoteUtil.isEmpty(qedInFile)) {
			Predicate p6 = cb.equal(materialDetail.<Date>get("displayQuoteEffDate"),
					PricerUtils.convertToDate(qedInFile));
			criterias.add(p6);
		} else {
			Predicate p6 = cb.isNull(materialDetail.<Date>get("displayQuoteEffDate"));
			criterias.add(p6);
		}
		if (criterias.size() == 0) {

		} else if (criterias.size() == 1) {
			c.where(criterias.get(0));
		} else {
			c.where(cb.and(criterias.toArray(new Predicate[0])));
		}

		TypedQuery<ProgramPricer> q = em.createQuery(c);
		List<ProgramPricer> queryList = q.getResultList();
		if (queryList != null && queryList.size() > 0)
			return queryList.get(0);
		else
			return null;
	}

	/**
	 * MFR] + [MFR P/N] + [Cost Indicator] + [Biz Unit] in each line, find if
	 * there are existing MATERIAL_DETAIL records with the same combination, and
	 * [Material_Category] = ��NORMAL
	 * 
	 * @return
	 */
	public NormalPricer findMaterialDetailofQED(PricerUploadTemplateBean bean, User user, String displayQED,
			String pricerType) {
		// findMaterialDetailByQED(PricerUploadTemplateBean bean,User
		// user,String qedInFile,String pricerType ){
		long start = System.currentTimeMillis();
		BizUnit bz = user.getDefaultBizUnit();

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<NormalPricer> c = cb.createQuery(NormalPricer.class);

		Root<NormalPricer> materialDetail = c.from(NormalPricer.class);
		Join<NormalPricer, BizUnit> bizUnit = materialDetail.join("bizUnitBean");
		Join<NormalPricer, CostIndicator> costIndicator = materialDetail.join("costIndicator");
		Join<NormalPricer, Material> material = materialDetail.join("material");
		Join<Material, Manufacturer> manufacturer = material.join("manufacturer");

		List<Predicate> criterias = new ArrayList<Predicate>();
		Predicate p1 = cb.equal(manufacturer.<String>get("name"), bean.getMfr());
		Predicate p2 = cb.equal(material.<String>get("fullMfrPartNumber"), bean.getFullMfrPart());
		Predicate p3 = cb.equal(costIndicator.<String>get("name"), bean.getCostIndicator());
		Predicate p4 = cb.equal(bizUnit.<String>get("name"), bz.getName());
		Predicate p5 = cb.equal(materialDetail.<Currency>get("currency"), Currency.valueOf(bean.getCurrency()));
		// Predicate p5 = cb.equal(materialDetail.<String> get("pricerType"),
		// pricerType);
		criterias.add(p1);
		criterias.add(p2);
		criterias.add(p3);
		criterias.add(p4);
		criterias.add(p5);
		// if the display qed in file is null then extract the null qed of DB
		if (!QuoteUtil.isEmpty(displayQED)) {
			Predicate p6 = cb.equal(materialDetail.<Date>get("displayQuoteEffDate"),
					PricerUtils.convertToDate(displayQED));
			criterias.add(p6);
		} else {
			Predicate p6 = cb.isNull(materialDetail.<Date>get("displayQuoteEffDate"));
			criterias.add(p6);
		}

		if (criterias.size() == 0) {

		} else if (criterias.size() == 1) {
			c.where(criterias.get(0));
		} else {
			c.where(cb.and(criterias.toArray(new Predicate[0])));
		}

		TypedQuery<NormalPricer> q = em.createQuery(c);
		List<NormalPricer> list = q.getResultList();
		long end = System.currentTimeMillis();
		LOG.info("findMaterialDetailofQED take " + (end - start) + "ms");
		if (list != null && list.size() > 0)
			return list.get(0);
		else
			return null;
	}

	private void pricerTypeUpdate(List<Manufacturer> manufacturerLst, int[] countArr, PricerUploadParametersBean puBean,
			String materialTypeStr, Material mtl, PricerUploadTemplateBean pricr, User user) {
		if (QED_CHECK_STATE.UPDATE.getName().equals(pricr.getQedCheckState())) {

			if (PRICER_TYPE.NORMAL.getName().equalsIgnoreCase(materialTypeStr)) {
				try {
					updateNormalPricerMaterialDetail(pricr, user, puBean);
					countArr[4]++; // updatedNormalCount++;
				} catch (Exception e) {
					LOG.log(Level.SEVERE, "updateNormalPricerMaterialDetail error : " + materialTypeStr
							+ " :: user Id and name " + user.getId() + " & " + user.getName() + e.getMessage(), e);
				}

			} else if (PRICER_TYPE.PROGRAM.getName().equalsIgnoreCase(materialTypeStr)) {
				// removeQuantityBreakPrice( materialTypeStr,
				// bz,mtrIdSet,puBean,costidctNameSet);
				try {
					updateProgramPricerMaterialDetail(pricr, user, puBean);
					countArr[5]++;// updatedProgramCount++;
				} catch (Exception e) {
					LOG.log(Level.SEVERE, "updateProgramPricerMaterialDetail error : " + materialTypeStr
							+ " :: user Id and name " + user.getId() + " & " + user.getName() + e.getMessage(), e);
				}

			}
		} else if (QED_CHECK_STATE.INSERT.getName().equals(pricr.getQedCheckState())) {
			// to if material is not null but materialdetail is null; need to
			// inserted;
			if (PRICER_TYPE.NORMAL.getName().equalsIgnoreCase(materialTypeStr)) {
				try {
					insertNormalPricerToMaterialDetail(pricr, mtl, user, puBean);
					countArr[1]++;// addedNormalCount++;
				} catch (Exception e) {
					LOG.log(Level.SEVERE, "insertNormalPricerToMaterialDetail error : " + materialTypeStr
							+ " :: user Id and name " + user.getId() + " & " + user.getName() + e.getMessage(), e);
				}

			} else if (PRICER_TYPE.PROGRAM.getName().equalsIgnoreCase(materialTypeStr)) {
				try {
					insertProgramPricerToMaterialDetail(pricr, mtl, user, puBean);
					countArr[2]++;// addedProgramCount++;
				} catch (Exception e) {
					LOG.log(Level.SEVERE, "insertProgramPricerToMaterialDetail error : " + materialTypeStr
							+ " :: user Id and name " + user.getId() + " & " + user.getName() + e.getMessage(), e);
				}
			}
		}
	}

}
