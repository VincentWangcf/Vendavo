package com.avnet.emasia.webquote.commodity.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;

import com.avnet.emasia.webquote.commodity.bean.PricerUploadTemplateBean;
import com.avnet.emasia.webquote.commodity.bean.VerifyEffectiveDateResult;
import com.avnet.emasia.webquote.commodity.dto.PISearchBean;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.Currency;
import com.avnet.emasia.webquote.entity.DataAccess;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.MaterialRegional;
import com.avnet.emasia.webquote.entity.Money;
import com.avnet.emasia.webquote.entity.NormalPricer;
import com.avnet.emasia.webquote.entity.Pricer;
import com.avnet.emasia.webquote.entity.ProductGroup;
import com.avnet.emasia.webquote.entity.ProgramPricer;
import com.avnet.emasia.webquote.entity.ProgramType;
import com.avnet.emasia.webquote.entity.QtyBreakPricer;
import com.avnet.emasia.webquote.entity.QuantityBreakPrice;
import com.avnet.emasia.webquote.entity.SalesCostPricer;
import com.avnet.emasia.webquote.entity.SalesCostType;
import com.avnet.emasia.webquote.utilities.DBResourceBundle;
import com.avnet.emasia.webquote.utilities.DateUtils;
import com.avnet.emasia.webquote.utilities.util.QuoteUtil;
import com.sun.xml.bind.v2.runtime.Name;


/**
 * @author punit.singh This Class having common code logic
 */
public class CommodityUtil {

	private static Logger logger = Logger.getLogger(CommodityUtil.class.getName());

	private final static String FIELD_NAME_PROGRAM_TYPE = "programType";

	private final static String LIKE_SIGN = "%";

	private final static String FIELD_NAME_MANUFACTURER = "manufacturer";
	private final static String FIELD_NAME_FULL_MFR_PART_NUMBER = "fullMfrPartNumber";
	private final static String FIELD_NAME_MATERIAL = "material";
	private final static String FIELD_NAME_SALES_COST_FLAG = "salesCostFlag";
	private final static String FIELD_NAME_BIZ_UNIT_BEAN = "bizUnitBean";
	private final static String FIELD_NAME_BIZ_UNIT = "bizUnit";

	private final static String FIELD_NAME_NAME = "name";
	private final static String FIELD_NAME_PRODUCT_GROUP_1 = "productGroup1";
	private final static String FIELD_NAME_PRODUCT_GROUP_2 = "productGroup2";
	private final static String FIELD_NAME_PRODUCT_GROUP_3 = "productGroup3";
	private final static String FIELD_NAME_PRODUCT_GROUP_4 = "productGroup4";

	// private final static String FIELD_NAME_MATERIAL_CATEGORY =
	// "materialCategory";
	private final static String FIELD_NAME_PROGRAM_EFFECTIVE_DATE = "programEffectiveDate";
	private final static String FIELD_NAME_PROGRAM_CLOSING_DATE = "programClosingDate";
	private final static String FIELD_NAME_PROGRAM_SPECIAL_ITEM_FLAG = "specialItemFlag";

	private final static String FIELD_NAME_LAST_UPDATE_ON = "lastUpdatedOn";
	private final static String FIELD_NAME_DISPLAY_ON_TOP = "displayOnTop";

	private final static String FIELD_NAME_PROGRAM_AVAILABLE_TO_SELL_QTY = "availableToSellQty";

	private final static String SORTED_ORDER_DESCENDING = "DESCENDING";

	private final static String SORTED_FIELD_QTY = "availableToSellQty";
	private final static String SORTED_FIELD_PROGRAM_TYPE = "programType.name";
	private final static String SORTED_FIELD_PRO_GROUP2 = "material.productGroup2.name";
	private final static String SORTED_FIELD_PART_NUMBER = "fullMfrPartNumber";
	private final static String SORTED_FIELD_MFR = "material.manufacturer.name";

	private final static String SORTED_FIELD_M_MFR = "mfr";
	private final static String SORTED_FIELD_M_FULL_MFR_PART = "fullMFRPart";
	private final static String SORTED_FIELD_M_PRODUCE_GROUP = "productGroup";
	private final static String SORTED_FIELD_M_PROGRAM = "program";
	private final static String SORTED_FIELD_M_DISPLAY_ON_TOP = "displayOnTop";
	private final static String SORTED_FIELD_M_SPECIAL_ITEM_INDICATOR = "specialItemIndicator";
	private final static String SORTED_FIELD_M_MINIMUM_COST = "minimumCost";
	private final static String SORTED_FIELD_M_AVAILABLE_TO_SELL_QTY = "availableToSellQuantity";
	private final static String SORTED_FIELD_M_CALL_FOR_QUOTE = "callForQuote";
	private final static String SORTED_FIELD_M_MPQ = "mpq";
	private final static String SORTED_FIELD_M_MOQ = "moq";
	private final static String SORTED_FIELD_M_MOV = "mov";
	private final static String SORTED_FIELD_M_COST_INDICATOR = "costIndicator";
	private final static String SORTED_FIELD_M_PROG_EFFECT_DATE = "programEffectiveDate";
	private final static String SORTED_FIELD_M_PROG_CLOSING_DATE = "programClosingDate";
	private final static String SORTED_FIELD_M_VALIDITY = "validity";
	private final static String SORTED_FIELD_M_SHIPMENT_VALIDITY = "shipmentValidity";
	private final static String SORTED_FIELD_M_LEADTIME = "leadTime";
	private final static String SORTED_FIELD_M_QUANT_INDICATOR = "quantityIndicator";
	private final static String SORTED_FIELD_M_NEW_ITEM_INDICATOR = "newItemIndicator";
	private final static String SORTED_FIELD_M_ALLOCATION_FLAG = "allocationFlag";
	private final static String SORTED_FIELD_M_RESALE_INDICATOR = "resaleIndicator";
	private final static String SORTED_FIELD_M_TERM_AND_CONDITIONS = "termsandConditions";
	private final static String SORTED_FIELD_M_AVNET_QUOTE_CENTER_COMMENTS = "avnetQuoteCentreComments";
	private final static String SORTED_FIELD_M_CREATED_ON = "createdOn";
	private final static String SORTED_FIELD_M_CREATED_BY = "createdBy";
	private final static String SORTED_FIELD_M_LAST_UPDATE_BY = "lastUpdateBy";
	
	private final static String FIELD_SALES_COST_FLAG = "salesCostFlag";
	private final static String FIELD_MATERIAL_REGIONALS = "materialRegionals";
	private final static String FIELD_PRICER_TYPE = "pricerType";
	
	private final static String FIELD_QUOTATION_EFFECTIVE_DATE = "quotationEffectiveDate";
	private final static String FIELD_QUOTATION_EFFECTIVE_TO = "quotationEffectiveTo";
	private final static String FIELD_QTY_CONTROL = "qtyControl";
	private final static String FIELD_SALES_COST_TYPE = "salesCostType";



	/**
	 * @param pisb
	 * @param cb
	 * @param cq
	 * @return
	 */
	/*public static CriteriaQuery<ProgramPricer> programMaterialCriteriaQuery(final PISearchBean pisb,
			final CriteriaBuilder cb, final CriteriaQuery<ProgramPricer> cq) {

		Root<ProgramPricer> programMaterial = cq.from(ProgramPricer.class);
		List<Predicate> predicates = new ArrayList<Predicate>();

		Expression<String> fullMfrPartNumber = programMaterial.<Material>get(FIELD_NAME_MATERIAL)
				.get(FIELD_NAME_FULL_MFR_PART_NUMBER);
		Expression<String> mfrName = programMaterial.<Material>get(FIELD_NAME_MATERIAL)
				.<Manufacturer>get(FIELD_NAME_MANUFACTURER).get(FIELD_NAME_NAME);
		Expression<String> productGroupName = programMaterial.<Material>get(FIELD_NAME_MATERIAL)
				.<ProductGroup>get(FIELD_NAME_PRODUCT_GROUP_2).get(FIELD_NAME_NAME);
		Expression<String> programTypeName = programMaterial.<ProgramType>get(FIELD_NAME_PROGRAM_TYPE)
				.get(FIELD_NAME_NAME);
		Expression<String> bizUnit = programMaterial.<BizUnit>get(FIELD_NAME_BIZ_UNIT_BEAN).get(FIELD_NAME_NAME);

		Expression<Manufacturer> mfr = programMaterial.<Material>get(FIELD_NAME_MATERIAL)
				.<Manufacturer>get(FIELD_NAME_MANUFACTURER);
		Expression<ProductGroup> productGroup = programMaterial.<Material>get(FIELD_NAME_MATERIAL)
				.<ProductGroup>get(FIELD_NAME_PRODUCT_GROUP_2);
		Expression<ProgramType> programType = programMaterial.<ProgramType>get(FIELD_NAME_PROGRAM_TYPE);
		Expression<Integer> programAvailableSellQty = programMaterial
				.<Integer>get(FIELD_NAME_PROGRAM_AVAILABLE_TO_SELL_QTY);

		Expression<Integer> mpq = programMaterial.<Integer>get(SORTED_FIELD_M_MPQ);
		Expression<Integer> moq = programMaterial.<Integer>get(SORTED_FIELD_M_MOQ);
		Expression<Integer> mov = programMaterial.<Integer>get(SORTED_FIELD_M_MOV);

		Expression<Integer> specialItemIndicator = programMaterial.<Integer>get("specialItemFlag");
		Expression<Boolean> allocationFlag = programMaterial.<Boolean>get(SORTED_FIELD_M_ALLOCATION_FLAG);
		Expression<Date> createdOn = programMaterial.<Date>get(SORTED_FIELD_M_CREATED_ON);
		Expression<Boolean> newItemIndicator = programMaterial.<Boolean>get("newItemIndicator");
		Expression<String> leadTime = programMaterial.<String>get(SORTED_FIELD_M_LEADTIME);
		Expression<Integer> qtyIndicator = programMaterial.<Integer>get("qtyIndicator");
		Expression<String> validity = programMaterial.<String>get("priceValidity");
		Expression<Date> shipmentValidity = programMaterial.<Date>get(SORTED_FIELD_M_SHIPMENT_VALIDITY);
		Expression<Date> effectDate = programMaterial.<Date>get(SORTED_FIELD_M_PROG_EFFECT_DATE);
		Expression<Date> closingDate = programMaterial.<Date>get(SORTED_FIELD_M_PROG_CLOSING_DATE);
		Expression<Boolean> callForQuote = programMaterial.<Boolean>get("programCallForQuote");
		Expression<String> resaleIndicator = programMaterial.<String>get(SORTED_FIELD_M_RESALE_INDICATOR);
		Expression<String> termsAndCondition = programMaterial.<String>get("termsAndConditions");
		Expression<Double> miniCost = programMaterial.<Double>get("programMinimumCost");
		Expression<String> avnetQuoteCenterComments = programMaterial.<String>get("avnetQcComments");
		Expression<String> costIndicator = programMaterial.<CostIndicator>get(SORTED_FIELD_M_COST_INDICATOR)
				.get("name");
		Expression<String> createdBy = programMaterial.<String>get(SORTED_FIELD_M_CREATED_BY);
		Expression<String> updatedBy = programMaterial.<String>get("lastUpdatedBy");
		
		if (pisb.getBizUnit() != null)
			predicates.add(cb.equal(bizUnit, pisb.getBizUnit()));

		if (pisb.getMfrPart() != null && !pisb.getMfrPart().isEmpty())
			if (pisb.getExact() == null) {
				predicates.add(
						cb.like(cb.upper(fullMfrPartNumber), LIKE_SIGN + pisb.getMfrPart().toUpperCase() + LIKE_SIGN));
			} else {
				predicates.add(cb.equal(fullMfrPartNumber, pisb.getMfrPart()));
			}

		if (pisb.getMfr() != null) {
			predicates.add(cb.like(cb.upper(mfrName), LIKE_SIGN + pisb.getMfr().toUpperCase() + LIKE_SIGN));
		}
		
		if(pisb.getSalesCostFlag() != null) {
			javax.persistence.criteria.Join<ProgramPricer, Material> materail = programMaterial.join(FIELD_NAME_MATERIAL, JoinType.INNER);
			javax.persistence.criteria.Join<Material, MaterialRegional> materialRegional = materail.join(FIELD_MATERIAL_REGIONALS, JoinType.INNER);
			predicates.add(cb.equal(materialRegional.<Boolean>get(FIELD_SALES_COST_FLAG), pisb.getSalesCostFlag()));
		}

		// Added by Punit for CPD Exercise
		predicates = preparePISearchBean(pisb, predicates, mfr, cb, programType, programTypeName, productGroupName,
				productGroup, mfrName);

		if (pisb.getActive() != null && !pisb.getActive().isEmpty()) {
			predicates.add(cb.lessThanOrEqualTo(programMaterial.<Date>get(FIELD_NAME_PROGRAM_EFFECTIVE_DATE),
					DateUtils.getCurrentAsiaDateObj()));
			predicates.add(cb.greaterThanOrEqualTo(programMaterial.<Date>get(FIELD_NAME_PROGRAM_CLOSING_DATE),
					DateUtils.getCurrentAsiaDateObj()));
		}
		// Common code
		predicates = securityCheckPISearchBean(pisb, cb, programMaterial, predicates);

		cq.select(programMaterial);
		cq.where(predicates.toArray(new Predicate[] {}));
		if (pisb.getSortField() != null) {
			if (SORTED_ORDER_DESCENDING.equalsIgnoreCase(pisb.getSortOrder())) {
				if (SORTED_FIELD_QTY.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.desc(programAvailableSellQty));
				} else if (SORTED_FIELD_PROGRAM_TYPE.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.desc(programTypeName));
				} else if (SORTED_FIELD_PRO_GROUP2.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.desc(productGroupName));
				} else if (SORTED_FIELD_PART_NUMBER.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.desc(fullMfrPartNumber));
				} else if (SORTED_FIELD_MFR.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.desc(mfrName));
				} else if (SORTED_FIELD_M_MFR.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.desc(mfrName));
				} else if (SORTED_FIELD_M_FULL_MFR_PART.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.desc(fullMfrPartNumber));
				} else if (SORTED_FIELD_M_PRODUCE_GROUP.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.desc(productGroupName));
				} else if (SORTED_FIELD_M_PROGRAM.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.desc(programTypeName));
				} else if (SORTED_FIELD_M_DISPLAY_ON_TOP.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(
							cb.desc(programMaterial.<Material>get(FIELD_NAME_MATERIAL).get(FIELD_NAME_DISPLAY_ON_TOP)));
				} else if (SORTED_FIELD_M_SPECIAL_ITEM_INDICATOR.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.desc(specialItemIndicator));
				} else if (SORTED_FIELD_M_MINIMUM_COST.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.desc(miniCost));
				} else if (SORTED_FIELD_M_AVAILABLE_TO_SELL_QTY.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.desc(programAvailableSellQty));
				} else if (SORTED_FIELD_M_CALL_FOR_QUOTE.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.desc(callForQuote));
				} else if (SORTED_FIELD_M_MPQ.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.desc(mpq));
				} else if (SORTED_FIELD_M_MOQ.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.desc(moq));
				} else if (SORTED_FIELD_M_MOV.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.desc(mov));
				} else if (SORTED_FIELD_M_COST_INDICATOR.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.desc(costIndicator));
				} else if (SORTED_FIELD_M_PROG_EFFECT_DATE.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.desc(effectDate));
				} else if (SORTED_FIELD_M_PROG_CLOSING_DATE.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.desc(closingDate));
				} else if (SORTED_FIELD_M_VALIDITY.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.desc(validity));
				} else if (SORTED_FIELD_M_SHIPMENT_VALIDITY.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.desc(shipmentValidity));
				} else if (SORTED_FIELD_M_LEADTIME.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.desc(leadTime));
				} else if (SORTED_FIELD_M_QUANT_INDICATOR.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.desc(qtyIndicator));
				} else if (SORTED_FIELD_M_NEW_ITEM_INDICATOR.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.desc(newItemIndicator));
				} else if (SORTED_FIELD_M_ALLOCATION_FLAG.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.desc(allocationFlag));
				} else if (SORTED_FIELD_M_RESALE_INDICATOR.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.desc(resaleIndicator));
				} else if (SORTED_FIELD_M_TERM_AND_CONDITIONS.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.desc(termsAndCondition));
				} else if (SORTED_FIELD_M_AVNET_QUOTE_CENTER_COMMENTS.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.desc(avnetQuoteCenterComments));
				} else if (SORTED_FIELD_M_CREATED_ON.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.desc(createdOn));
				} else if (SORTED_FIELD_M_CREATED_BY.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.desc(createdBy));
				} else if (SORTED_FIELD_M_LAST_UPDATE_BY.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.desc(updatedBy));
				}

			} else {
				if (SORTED_FIELD_QTY.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.asc(programAvailableSellQty));
				} else if (SORTED_FIELD_PROGRAM_TYPE.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.asc(programTypeName));
				} else if (SORTED_FIELD_PRO_GROUP2.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.asc(productGroupName));
				} else if (SORTED_FIELD_PART_NUMBER.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.asc(fullMfrPartNumber));
				} else if (SORTED_FIELD_MFR.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.asc(mfrName));
				} else if (SORTED_FIELD_M_MFR.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.asc(mfrName));
				} else if (SORTED_FIELD_M_FULL_MFR_PART.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.asc(fullMfrPartNumber));
				} else if (SORTED_FIELD_M_PRODUCE_GROUP.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.asc(productGroupName));
				} else if (SORTED_FIELD_M_PROGRAM.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.asc(programTypeName));
				} else if (SORTED_FIELD_M_DISPLAY_ON_TOP.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(
							cb.asc(programMaterial.<Material>get(FIELD_NAME_MATERIAL).get(FIELD_NAME_DISPLAY_ON_TOP)));
				} else if (SORTED_FIELD_M_SPECIAL_ITEM_INDICATOR.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.asc(specialItemIndicator));
				} else if (SORTED_FIELD_M_MINIMUM_COST.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.asc(miniCost));
				} else if (SORTED_FIELD_M_AVAILABLE_TO_SELL_QTY.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.asc(programAvailableSellQty));
				} else if (SORTED_FIELD_M_CALL_FOR_QUOTE.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.asc(callForQuote));
				} else if (SORTED_FIELD_M_MPQ.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.asc(mpq));
				} else if (SORTED_FIELD_M_MOQ.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.asc(moq));
				} else if (SORTED_FIELD_M_MOV.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.asc(mov));
				} else if (SORTED_FIELD_M_COST_INDICATOR.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.asc(costIndicator));
				} else if (SORTED_FIELD_M_PROG_EFFECT_DATE.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.asc(effectDate));
				} else if (SORTED_FIELD_M_PROG_CLOSING_DATE.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.asc(closingDate));
				} else if (SORTED_FIELD_M_VALIDITY.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.asc(validity));
				} else if (SORTED_FIELD_M_SHIPMENT_VALIDITY.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.asc(shipmentValidity));
				} else if (SORTED_FIELD_M_LEADTIME.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.asc(leadTime));
				} else if (SORTED_FIELD_M_QUANT_INDICATOR.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.asc(qtyIndicator));
				} else if (SORTED_FIELD_M_NEW_ITEM_INDICATOR.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.asc(newItemIndicator));
				} else if (SORTED_FIELD_M_ALLOCATION_FLAG.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.asc(allocationFlag));
				} else if (SORTED_FIELD_M_RESALE_INDICATOR.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.asc(resaleIndicator));
				} else if (SORTED_FIELD_M_TERM_AND_CONDITIONS.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.asc(termsAndCondition));
				} else if (SORTED_FIELD_M_AVNET_QUOTE_CENTER_COMMENTS.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.asc(avnetQuoteCenterComments));
				} else if (SORTED_FIELD_M_CREATED_ON.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.asc(createdOn));
				} else if (SORTED_FIELD_M_CREATED_BY.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.asc(createdBy));
				} else if (SORTED_FIELD_M_LAST_UPDATE_BY.equalsIgnoreCase(pisb.getSortField())) {
					cq.orderBy(cb.asc(updatedBy));
				}
			}

		} else {
			cq.orderBy(
					cb.asc(cb.selectCase()
							.when(cb.isNull(
									programMaterial.<Material>get(FIELD_NAME_MATERIAL).get(FIELD_NAME_DISPLAY_ON_TOP)),
									999999)
							.otherwise(
									programMaterial.<Material>get(FIELD_NAME_MATERIAL).get(FIELD_NAME_DISPLAY_ON_TOP))),
					cb.asc(cb.selectCase()
							.when(cb.isNull(programMaterial.get(FIELD_NAME_PROGRAM_SPECIAL_ITEM_FLAG)), 999999)
							.otherwise(programMaterial.get(FIELD_NAME_PROGRAM_SPECIAL_ITEM_FLAG))),
					cb.desc(programMaterial.get(FIELD_NAME_LAST_UPDATE_ON)), cb.asc(mfrName),
					cb.asc(fullMfrPartNumber));
		}

		return cq;
	}
 */


 
	
	
	public static CriteriaQuery<QtyBreakPricer> qtyBreakPricerCriteriaQuery(final PISearchBean pisb, final CriteriaBuilder cb,
			final CriteriaQuery<QtyBreakPricer> cq) {
		Root<QtyBreakPricer> pricer = cq.from(QtyBreakPricer.class);
		List<Predicate> predicates = new ArrayList<Predicate>();
		Expression<String> fullMfrPartNumber = pricer.<Material>get(FIELD_NAME_MATERIAL)
				.get(FIELD_NAME_FULL_MFR_PART_NUMBER);
		Expression<String> mfrName = pricer.<Material>get(FIELD_NAME_MATERIAL)
				.<Manufacturer>get(FIELD_NAME_MANUFACTURER).get(FIELD_NAME_NAME);
		
		Expression<String> programTypeName = pricer
				.<ProgramType>get(FIELD_NAME_PROGRAM_TYPE).get(FIELD_NAME_NAME);

		Expression<String> bizUnit = pricer.<BizUnit>get(FIELD_NAME_BIZ_UNIT_BEAN).get(FIELD_NAME_NAME);
		Expression<Manufacturer> mfr = pricer.<Material>get(FIELD_NAME_MATERIAL)
				.<Manufacturer>get(FIELD_NAME_MANUFACTURER);
		Path<Object> salesCostPart = pricer.<Material>get(FIELD_NAME_MATERIAL).get("materialRegionals").get(FIELD_NAME_SALES_COST_FLAG);
		//Expression<ProductGroup> productGroup = pricer.<Material>get(FIELD_NAME_MATERIAL).<ProductGroup>get(FIELD_NAME_PRODUCT_GROUP_2);
//		Path<Currency> buyCurrency = pricer.<Currency>get("currency").get("name");
		Path<Currency> buyCurrency = pricer.<Currency>get("currency");
		Expression<ProgramType> programType = pricer.<ProgramType>get(FIELD_NAME_PROGRAM_TYPE);

		if (pisb.getBizUnit() != null) {
			predicates.add(cb.equal(bizUnit, pisb.getBizUnit()));
		}
			
		if (pisb.getMfrPart() != null && !pisb.getMfrPart().isEmpty())
			if (pisb.getExact() == null) {
				predicates.add(
						cb.like(cb.upper(fullMfrPartNumber), LIKE_SIGN + pisb.getMfrPart().toUpperCase() + LIKE_SIGN));
			} else {
				predicates.add(cb.equal(fullMfrPartNumber, pisb.getMfrPart()));
			}

		if (pisb.getMfr() != null) {
			predicates.add(cb.like(cb.upper(mfrName), LIKE_SIGN + pisb.getMfr().toUpperCase() + LIKE_SIGN));
		}

		Predicate allPredicate = getAllPredicate(pisb, cb, pricer, predicates, mfrName,programTypeName, mfr, programType,"search");
		
		cq.select(pricer);

		cq.where(allPredicate);
		String orderBy = pisb.getSortOrder();
		String sortField = pisb.getSortField();
		
		if("buyCurrency".equals(sortField)){
			if ("ASCENDING".equals(orderBy)) {
			
				cq.orderBy(cb.asc(buyCurrency));
			}
			if ("DESCENDING".equals(orderBy)) {
				//qp.getMaterial().getFullMfrPartNumber());
			
				cq.orderBy(cb.desc(buyCurrency));
			}
		}
		if("salesCostPart".equals(sortField)){
			if ("ASCENDING".equals(orderBy)) {
				
				cq.orderBy(cb.asc(salesCostPart));
			}
			if ("DESCENDING".equals(orderBy)) {
				//qp.getMaterial().getFullMfrPartNumber());
				
				cq.orderBy(cb.desc(salesCostPart));
			}
		}
		if("fullPartNumber".equals(sortField)||"fullMFRPart".equals(sortField)){
			if ("ASCENDING".equals(orderBy)) {
				//qp.getMaterial().getFullMfrPartNumber());
			
				cq.orderBy(cb.asc(pricer.get(FIELD_NAME_MATERIAL).get(FIELD_NAME_FULL_MFR_PART_NUMBER)));
			}
			if ("DESCENDING".equals(orderBy)) {
				//qp.getMaterial().getFullMfrPartNumber());
			
				cq.orderBy(cb.desc(pricer.get(FIELD_NAME_MATERIAL).get(FIELD_NAME_FULL_MFR_PART_NUMBER)));
			}
		}
		if("mfr".equals(sortField)){
			if ("ASCENDING".equals(orderBy)) {				
				cq.orderBy(cb.asc(pricer.get(FIELD_NAME_MATERIAL).get(FIELD_NAME_MANUFACTURER).get(FIELD_NAME_NAME)));
			}
			if ("DESCENDING".equals(orderBy)) {							
				cq.orderBy(cb.desc(pricer.get(FIELD_NAME_MATERIAL).get(FIELD_NAME_MANUFACTURER).get(FIELD_NAME_NAME)));
			}
		}
		//vo.setProgram(qp.getProgramType().getName());  FIELD_NAME_PROGRAM_TYPE
		if("program".equals(sortField)){
			if ("ASCENDING".equals(orderBy)) {				
				cq.orderBy(cb.asc(pricer.get(FIELD_NAME_PROGRAM_TYPE).get(FIELD_NAME_NAME)));
			}
			if ("DESCENDING".equals(orderBy)) {							
				cq.orderBy(cb.desc(pricer.get(FIELD_NAME_PROGRAM_TYPE).get(FIELD_NAME_NAME)));
			}
		}
		
//		cq.orderBy(
//				cb.asc(cb.selectCase()
//						.when(cb.isNull(pricer.get(FIELD_NAME_DISPLAY_ON_TOP)),
//								999999)
//						.otherwise(pricer.get(FIELD_NAME_DISPLAY_ON_TOP))),
//				cb.asc(cb.selectCase().when(cb.isNull(pricer.get(FIELD_NAME_PROGRAM_SPECIAL_ITEM_FLAG)), 999999)
//						.otherwise(pricer.get(FIELD_NAME_PROGRAM_SPECIAL_ITEM_FLAG))),
//				cb.desc(pricer.get(FIELD_NAME_LAST_UPDATE_ON)), cb.asc(mfrName), cb.asc(fullMfrPartNumber));

		return cq;

	}


	public static Predicate getAllPredicate(final PISearchBean pisb, final CriteriaBuilder cb,
			Root<QtyBreakPricer> pricer, List<Predicate> predicates,
			Expression<String> mfrName,
			Expression<String> programTypeName, Expression<Manufacturer> mfr, Expression<ProgramType> programType,String type) {
		preparePISearchBean(pisb, cb, predicates, mfrName, programTypeName, mfr,programType);
		List<Predicate> programPredicates = new ArrayList<Predicate>();
		List<Predicate> salesCostPredicates = new ArrayList<Predicate>();

		Date currentDate = DateUtils.getCurrentAsiaDateObj();
		if (pisb.getActive() != null && !pisb.getActive().isEmpty() ) {
			Predicate programEffectiveDate = cb.lessThanOrEqualTo(
					cb.treat(pricer, ProgramPricer.class).<Date>get(FIELD_NAME_PROGRAM_EFFECTIVE_DATE), currentDate);
			Predicate programClosingDate = cb.greaterThanOrEqualTo(cb.treat(pricer, ProgramPricer.class).<Date>get(FIELD_NAME_PROGRAM_CLOSING_DATE),
					currentDate);
			programPredicates.add(programEffectiveDate);
			programPredicates.add(programClosingDate);
			
			
			salesCostPredicates.add(cb.lessThanOrEqualTo(pricer.<Date>get(FIELD_QUOTATION_EFFECTIVE_DATE),currentDate));
			salesCostPredicates.add(cb.greaterThanOrEqualTo(pricer.<Date>get(FIELD_QUOTATION_EFFECTIVE_TO),currentDate));
			Predicate salesCostType = cb.notEqual(cb.treat(pricer, SalesCostPricer.class).<SalesCostType>get(FIELD_SALES_COST_TYPE),SalesCostType.ZINM);
			Predicate qtyControl = cb.greaterThan(cb.treat(pricer, SalesCostPricer.class).<Integer>get(FIELD_QTY_CONTROL),0);
			Predicate tempPredicate = cb.or(salesCostType,qtyControl);
			salesCostPredicates.add(tempPredicate);
		}
		
		
		securityCheckPISearchBean(pisb, cb, predicates, mfrName, mfr, programType);
		javax.persistence.criteria.Join<Pricer, Material> materail = pricer.join(FIELD_NAME_MATERIAL,
				JoinType.INNER);
		javax.persistence.criteria.Join<Material, MaterialRegional> materialRegional = materail
				.join(FIELD_MATERIAL_REGIONALS, JoinType.INNER);
		
		if (pisb.getBizUnit() != null) {
			predicates.add(cb.equal(materialRegional.<BizUnit>get(FIELD_NAME_BIZ_UNIT).get(FIELD_NAME_NAME), pisb.getBizUnit()));
		}

		if (StringUtils.isNotBlank(pisb.getProductGroupStr())) {
		   	Expression<String>  productGroupName1 = materialRegional.<ProductGroup>get(FIELD_NAME_PRODUCT_GROUP_1).get(FIELD_NAME_NAME);
		   	Expression<String>  productGroupName2 = materialRegional.<ProductGroup>get(FIELD_NAME_PRODUCT_GROUP_2).get(FIELD_NAME_NAME);
		   	Expression<String>  productGroupName3 = materialRegional.<String>get(FIELD_NAME_PRODUCT_GROUP_3);
		   	Expression<String>  productGroupName4 = materialRegional.<String>get(FIELD_NAME_PRODUCT_GROUP_4);
			Predicate pLike1 = cb.like(cb.upper(productGroupName1), LIKE_SIGN + pisb.getProductGroupStr().toUpperCase() + LIKE_SIGN);
			Predicate pLike2 = cb.like(cb.upper(productGroupName2), LIKE_SIGN + pisb.getProductGroupStr().toUpperCase() + LIKE_SIGN);
			Predicate pLike3 = cb.like(cb.upper(productGroupName3), LIKE_SIGN + pisb.getProductGroupStr().toUpperCase() + LIKE_SIGN);
			Predicate pLike4 = cb.like(cb.upper(productGroupName4), LIKE_SIGN + pisb.getProductGroupStr().toUpperCase() + LIKE_SIGN);
			Predicate orPruduct = cb.or(pLike1,pLike2,pLike3,pLike4);
			predicates.add(orPruduct);
		}
		
		if (pisb.getSalesCostFlag() != null ) {
			if(pisb.getSalesCostFlag()) {
				predicates.add(cb.equal(materialRegional.<Boolean>get(FIELD_SALES_COST_FLAG), true));
			}else {
				predicates.add(cb.equal(materialRegional.<Boolean>get(FIELD_SALES_COST_FLAG), false));
			}
			
		} 
		Predicate allPredicate = null;
		
		Predicate p1 = cb.and(predicates.toArray(new Predicate[] {}));
		if(StringUtils.isNotBlank(type)) {
			Predicate p2 = cb.and(programPredicates.toArray(new Predicate[] {}));
			Predicate p3 = cb.and(salesCostPredicates.toArray(new Predicate[] {}));
			Predicate p4  = cb.or(p2,p3);
			allPredicate  = cb.and(p1,p4);
		} else {
			allPredicate = p1;
		}

		return allPredicate;
	}


	public static void preparePISearchBean(final PISearchBean pisb, final CriteriaBuilder cb,
			List<Predicate> predicates,  Expression<String> mfrName,
			Expression<String> programTypeName, Expression<Manufacturer> mfr,
			Expression<ProgramType> programType) {
		if (pisb.getMfrList() != null) {
			if (pisb.getMfrList().isEmpty()) {
				predicates.add(cb.isNull(mfr));
			} else {
				predicates.add(mfrName.in(pisb.getMfrList()));
			}
		}

		/*if (pisb.getProductGroupList() != null) {
			if (pisb.getProductGroupList().isEmpty()) {
				predicates.add(cb.isNull(productGroup));
				predicates2.add(cb.isNull(productGroup));
			} else {
				predicates.add(productGroupName.in(pisb.getProductGroupList()));
				predicates2.add(productGroupName.in(pisb.getProductGroupList()));
			}
		}*/
		

		if (pisb.getProgramTypeList() != null) {
			if (pisb.getProgramTypeList().isEmpty()) {
				predicates.add(cb.isNull(programType));
			} else {
				predicates.add(programTypeName.in(pisb.getProgramTypeList()));
			}
		}
	}


	public static void securityCheckPISearchBean(final PISearchBean pisb, final CriteriaBuilder cb,
			List<Predicate> predicates, Expression<String> mfrName,
			 Expression<Manufacturer> mfr, Expression<ProgramType> programType) {
		if (pisb.isDataAccessCheckRequired()) {
			boolean isContainFullAccess = false;
			if (pisb.getDataAccesses() != null && pisb.getDataAccesses().size() > 0) {
				List<DataAccess> dataAccList = pisb.getDataAccesses();
				List<Predicate> subPredicatesWhole = new ArrayList<Predicate>();
				for (DataAccess da : dataAccList) {
					List<Predicate> subPredicates = new ArrayList<Predicate>();
					if (da.getManufacturer() != null) {
						subPredicates.add(cb.equal(mfr, da.getManufacturer()));
					}
					/*if (da.getProductGroup() != null) {
						subPredicates.add(cb.equal(productGroup, da.getProductGroup()));
					}*/
					if (da.getProgramType() != null) {
						subPredicates.add(cb.equal(programType, da.getProgramType()));
					}

					if (subPredicates.size() == 0) {
						isContainFullAccess = true;
					}
					subPredicatesWhole.add(cb.and(subPredicates.toArray(new Predicate[0])));
				}
				if (!isContainFullAccess) {
					predicates.add(cb.or(subPredicatesWhole.toArray(new Predicate[0])));

				}
			} else {
				// without any data access .
				predicates.add(cb.equal(mfrName, "NA"));

			}

		}
	}
	
	
	public static List<QuantityBreakPrice> updatePPMDQuantityBreakPriceList_OLD(final Pricer mDetail,
			final PricerUploadTemplateBean pricr, final EntityManager em) {
		List<QuantityBreakPrice> qpLst = new ArrayList<QuantityBreakPrice>();
		try {
			if (!QuoteUtil.isEmpty(pricr.getQuantityBreak1()) && (!QuoteUtil.isEmpty(pricr.getQuantityBreakPrice1())
					|| !QuoteUtil.isEmpty(pricr.getQuantityBreakSalesCost1()))) {
				QuantityBreakPrice qp = new QuantityBreakPrice();
				qp.setQuantityBreak(Integer.parseInt(pricr.getQuantityBreak1()));
				if (!QuoteUtil.isEmpty(pricr.getQuantityBreakPrice1())) {
					qp.setQuantityBreakPrice(Double.parseDouble(pricr.getQuantityBreakPrice1()));
				}
				if (!QuoteUtil.isEmpty(pricr.getQuantityBreakSalesCost1())) {
					qp.setSalesCost(new BigDecimal(pricr.getQuantityBreakSalesCost1()));
				}
				if (!QuoteUtil.isEmpty(pricr.getQuantityBreakSuggestedResale1())) {
					qp.setSuggestedResale(new BigDecimal(pricr.getQuantityBreakSuggestedResale1()));
				}
				qp.setMaterialDetail(mDetail);
				em.persist(qp);
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
				if (!QuoteUtil.isEmpty(pricr.getQuantityBreakSalesCost2())) {
					qp.setSalesCost(new BigDecimal(pricr.getQuantityBreakSalesCost2()));
				}
				if (!QuoteUtil.isEmpty(pricr.getQuantityBreakSuggestedResale2())) {
					qp.setSuggestedResale(new BigDecimal(pricr.getQuantityBreakSuggestedResale2()));
				}
				qp.setMaterialDetail(mDetail);
				em.persist(qp);
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
				if (!QuoteUtil.isEmpty(pricr.getQuantityBreakSalesCost3())) {
					qp.setSalesCost(new BigDecimal(pricr.getQuantityBreakSalesCost3()));
				}
				if (!QuoteUtil.isEmpty(pricr.getQuantityBreakSuggestedResale3())) {
					qp.setSuggestedResale(new BigDecimal(pricr.getQuantityBreakSuggestedResale3()));
				}
				qp.setMaterialDetail(mDetail);
				em.persist(qp);
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
				if (!QuoteUtil.isEmpty(pricr.getQuantityBreakSalesCost4())) {
					qp.setSalesCost(new BigDecimal(pricr.getQuantityBreakSalesCost4()));
				}
				if (!QuoteUtil.isEmpty(pricr.getQuantityBreakSuggestedResale4())) {
					qp.setSuggestedResale(new BigDecimal(pricr.getQuantityBreakSuggestedResale4()));
				}
				qp.setMaterialDetail(mDetail);
				em.persist(qp);
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
				if (!QuoteUtil.isEmpty(pricr.getQuantityBreakSalesCost5())) {
					qp.setSalesCost(new BigDecimal(pricr.getQuantityBreakSalesCost5()));
				}
				if (!QuoteUtil.isEmpty(pricr.getQuantityBreakSuggestedResale5())) {
					qp.setSuggestedResale(new BigDecimal(pricr.getQuantityBreakSuggestedResale5()));
				}
				qp.setMaterialDetail(mDetail);
				em.persist(qp);
				qpLst.add(qp);
			}
		} catch (Exception e) {
			throw e;
		}

		return qpLst;
	}
	public static List<QuantityBreakPrice> updatePPMDQuantityBreakPriceList(final Pricer mDetail,
			final PricerUploadTemplateBean pricr, final EntityManager em) {
		List<QuantityBreakPrice> qpLst = new ArrayList<QuantityBreakPrice>();
		try {
			if (!QuoteUtil.isEmpty(pricr.getQuantityBreak1()) && !QuoteUtil.isEmpty(pricr.getQuantityBreakPrice1())) {
				QuantityBreakPrice qp = new QuantityBreakPrice();
				qp.setQuantityBreak(Integer.parseInt(pricr.getQuantityBreak1()));
				if (!QuoteUtil.isEmpty(pricr.getQuantityBreakPrice1())) {
					qp.setQuantityBreakPrice(Double.parseDouble(pricr.getQuantityBreakPrice1()));
				}
				qp.setMaterialDetail(mDetail);
				em.persist(qp);
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
				qp.setMaterialDetail(mDetail);
				em.persist(qp);
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
				qp.setMaterialDetail(mDetail);
				em.persist(qp);
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
				qp.setMaterialDetail(mDetail);
				em.persist(qp);
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
				qp.setMaterialDetail(mDetail);
				em.persist(qp);
				qpLst.add(qp);
			}
		} catch (Exception e) {
			throw e;
		}

		return qpLst;
	}
	
	public static List<QuantityBreakPrice> updateQuantityBreakPriceListForSS(final Pricer mDetail,
			final PricerUploadTemplateBean pricr, final EntityManager em) {
		List<QuantityBreakPrice> qpLst = new ArrayList<QuantityBreakPrice>();
		try {
			if (!QuoteUtil.isEmpty(pricr.getQuantityBreak1()) && 
					(!QuoteUtil.isEmpty(pricr.getQuantityBreakSuggestedResale1())
					|| !QuoteUtil.isEmpty(pricr.getQuantityBreakSalesCost1()))) {
				QuantityBreakPrice qp = new QuantityBreakPrice();
				qp.setQuantityBreak(Integer.parseInt(pricr.getQuantityBreak1()));
				
				if (!QuoteUtil.isEmpty(pricr.getQuantityBreakSalesCost1())) {
					qp.setSalesCost(new BigDecimal(pricr.getQuantityBreakSalesCost1()));
				}
				if (!QuoteUtil.isEmpty(pricr.getQuantityBreakSuggestedResale1())) {
					qp.setSuggestedResale(new BigDecimal(pricr.getQuantityBreakSuggestedResale1()));
				}
				qp.setMaterialDetail(mDetail);
				em.persist(qp);
				qpLst.add(qp);
			}

			if ((pricr.getQuantityBreak2() != null) && (!pricr.getQuantityBreak2().equals(""))) {
				QuantityBreakPrice qp = new QuantityBreakPrice();
				qp.setQuantityBreak(Integer.parseInt(pricr.getQuantityBreak2()));
				if (!QuoteUtil.isEmpty(pricr.getQuantityBreakSalesCost2())) {
					qp.setSalesCost(new BigDecimal(pricr.getQuantityBreakSalesCost2()));
				}
				if (!QuoteUtil.isEmpty(pricr.getQuantityBreakSuggestedResale2())) {
					qp.setSuggestedResale(new BigDecimal(pricr.getQuantityBreakSuggestedResale2()));
				}
				qp.setMaterialDetail(mDetail);
				em.persist(qp);
				qpLst.add(qp);
			}

			if ((pricr.getQuantityBreak3() != null) && (!pricr.getQuantityBreak3().equals(""))) {
				QuantityBreakPrice qp = new QuantityBreakPrice();
				qp.setQuantityBreak(Integer.parseInt(pricr.getQuantityBreak3()));
				
				if (!QuoteUtil.isEmpty(pricr.getQuantityBreakSalesCost3())) {
					qp.setSalesCost(new BigDecimal(pricr.getQuantityBreakSalesCost3()));
				}
				if (!QuoteUtil.isEmpty(pricr.getQuantityBreakSuggestedResale3())) {
					qp.setSuggestedResale(new BigDecimal(pricr.getQuantityBreakSuggestedResale3()));
				}
				qp.setMaterialDetail(mDetail);
				em.persist(qp);
				qpLst.add(qp);
			}

			if ((pricr.getQuantityBreak4() != null) && (!pricr.getQuantityBreak4().equals(""))) {
				QuantityBreakPrice qp = new QuantityBreakPrice();
				qp.setQuantityBreak(Integer.parseInt(pricr.getQuantityBreak4()));
				
				if (!QuoteUtil.isEmpty(pricr.getQuantityBreakSalesCost4())) {
					qp.setSalesCost(new BigDecimal(pricr.getQuantityBreakSalesCost4()));
				}
				if (!QuoteUtil.isEmpty(pricr.getQuantityBreakSuggestedResale4())) {
					qp.setSuggestedResale(new BigDecimal(pricr.getQuantityBreakSuggestedResale4()));
				}
				qp.setMaterialDetail(mDetail);
				em.persist(qp);
				qpLst.add(qp);
			}

			if ((pricr.getQuantityBreak5() != null) && (!pricr.getQuantityBreak5().equals(""))) {
				QuantityBreakPrice qp = new QuantityBreakPrice();
				qp.setQuantityBreak(Integer.parseInt(pricr.getQuantityBreak5()));
				if (!QuoteUtil.isEmpty(pricr.getQuantityBreakSalesCost5())) {
					qp.setSalesCost(new BigDecimal(pricr.getQuantityBreakSalesCost5()));
				}
				if (!QuoteUtil.isEmpty(pricr.getQuantityBreakSuggestedResale5())) {
					qp.setSuggestedResale(new BigDecimal(pricr.getQuantityBreakSuggestedResale5()));
				}
				qp.setMaterialDetail(mDetail);
				em.persist(qp);
				qpLst.add(qp);
			}
		} catch (Exception e) {
			throw e;
		}

		return qpLst;
	}
	
	/**
	 * @param pricr
	 * @param mDetail
	 * @throws ParseException
	 */
	public static ProgramPricer updateProgramPricerMaterialDetailFromPUTBean(final PricerUploadTemplateBean pricr,
			final ProgramPricer mDetail) throws ParseException {

		if ((pricr.getSpecialItemIndicator() != null) && (!pricr.getSpecialItemIndicator().equals(""))) {
			mDetail.setSpecialItemFlag(Integer.parseInt(pricr.getSpecialItemIndicator()));
		} else {
			mDetail.setSpecialItemFlag(null);
		}

		String aq = pricr.getAQFlag();
		if ((aq != null) && (!aq.equals(""))) {
			aq = aq.trim();
			if (aq.equalsIgnoreCase("Yes")) {
				mDetail.setAqFlag(true);
			} else {
				mDetail.setAqFlag(false);
			}
		} else {
			mDetail.setAqFlag(false);
		}

		/*String cfq = pricr.getCallForQuote();
		if ((cfq != null) && (!cfq.equals(""))) {
			cfq = cfq.trim();
			if (cfq.equalsIgnoreCase("Yes")) {
				mDetail.setProgramCallForQuote(true);
			} else {
				mDetail.setProgramCallForQuote(false);
			}
		} else {
			mDetail.setProgramCallForQuote(false);
		}*/

		if (pricr.getProgramClosingDate() != null && !pricr.getProgramClosingDate().equals("")) {
			mDetail.setProgramClosingDate(QuoteUtil.convertStringToDate(pricr.getProgramClosingDate(), "dd/MM/yyyy"));
		} else {
			mDetail.setProgramClosingDate(null);
		}

		if (pricr.getProgramEffectiveDate() != null && !pricr.getProgramEffectiveDate().equals("")) {
			mDetail.setProgramEffectiveDate(
					QuoteUtil.convertStringToDate(pricr.getProgramEffectiveDate(), "dd/MM/yyyy"));
		} else {
			mDetail.setProgramEffectiveDate(null);
		}

		if ((pricr.getAvailabletoSellQuantity() != null) && (!pricr.getAvailabletoSellQuantity().equals(""))) {
			mDetail.setAvailableToSellQty(Integer.parseInt(pricr.getAvailabletoSellQuantity()));
		} else {
			mDetail.setAvailableToSellQty(null);
		}

		return mDetail;
	}

	/**
	 * @param pisb
	 * @param cb
	 * @param programMaterial
	 * @param predicates
	 */
	/*public static List<Predicate> securityCheckPISearchBean(final PISearchBean pisb, final CriteriaBuilder cb,
			final Root<ProgramPricer> programMaterial, final List<Predicate> predicates) {

		Expression<Manufacturer> mfr = programMaterial.<Material>get(FIELD_NAME_MATERIAL)
				.<Manufacturer>get(FIELD_NAME_MANUFACTURER);
		Expression<ProductGroup> productGroup = programMaterial.<Material>get(FIELD_NAME_MATERIAL)
				.<ProductGroup>get(FIELD_NAME_PRODUCT_GROUP_2);
		Expression<ProgramType> programType = programMaterial.<ProgramType>get(FIELD_NAME_PROGRAM_TYPE);
		// Expression<String> materialType =
		// programMaterial.<String>get(FIELD_NAME_MATERIAL_CATEGORY);
		Expression<String> mfrName = programMaterial.<Material>get(FIELD_NAME_MATERIAL)
				.<Manufacturer>get(FIELD_NAME_MANUFACTURER).get(FIELD_NAME_NAME);
		// Security check
		if (pisb.isDataAccessCheckRequired()) {
			logger.fine("call searchProgramMaterial : security check required .");
			boolean isContainFullAccess = false;
			if (pisb.getDataAccesses() != null && pisb.getDataAccesses().size() > 0) {
				logger.fine("call searchProgramMaterial : security check required | dataAccess size  "
						+ pisb.getDataAccesses().size());
				List<DataAccess> dataAccList = pisb.getDataAccesses();
				List<Predicate> subPredicatesWhole = new ArrayList<Predicate>();
				for (DataAccess da : dataAccList) {
					List<Predicate> subPredicates = new ArrayList<Predicate>();
					if (da.getManufacturer() != null) {
						subPredicates.add(cb.equal(mfr, da.getManufacturer()));
					}
					if (da.getProductGroup() != null) {
						subPredicates.add(cb.equal(productGroup, da.getProductGroup()));
					}
					if (da.getProgramType() != null) {
						subPredicates.add(cb.equal(programType, da.getProgramType()));
					}

					if (subPredicates.size() == 0) {
						isContainFullAccess = true;
					}
					subPredicatesWhole.add(cb.and(subPredicates.toArray(new Predicate[0])));
				}
				if (!isContainFullAccess) {
					predicates.add(cb.or(subPredicatesWhole.toArray(new Predicate[0])));
				}
			} else {
				// without any data access .
				predicates.add(cb.equal(mfrName, "NA"));
			}

		}
		return predicates;
	}*/


	/**
	 * @param results
	 * @param mfrLst
	 * @param nlist
	 * @param param
	 */
	public static List<VerifyEffectiveDateResult> verifyEffectiveDateResult(
			final List<VerifyEffectiveDateResult> results, final List<Manufacturer> mfrLst, final List<?> nlist,
			final String param) {
		Object[] obj = null;

		VerifyEffectiveDateResult result = null;
		for (int i = 0; i < nlist.size(); i++) {
			obj = (Object[]) nlist.get(i);
			result = new VerifyEffectiveDateResult();
			result.setMaterialDetailId(Long.parseLong(obj[0].toString()));
			result.setPricerType((String) obj[1]);
			result.setDisplayQuoteEffDate(PricerUtils.timestampToDate((Timestamp) obj[2]));
			result.setFullMfrPartNumber((String) obj[3]);
			result.setPriceValidity((String) obj[4]);
			result.setQuotationEffectiveDate(PricerUtils.timestampToDate((Timestamp) obj[5]));
			result.setQuotationEffectiveTo(PricerUtils.timestampToDate((Timestamp) obj[6]));
			result.setBizUnit((String) obj[7]);
			result.setCostIndicator((String) obj[8]);
			result.setMaterialId(Long.parseLong(obj[9].toString()));

			for (Manufacturer mfr : mfrLst) {
				if (mfr.getId() == Long.parseLong(obj[10].toString())) {
					result.setMfr(mfr.getName());
					break;
				}
			}
			if (param != null && param.equalsIgnoreCase("contractPriceUpload")) {
				result.setSoldToCode((String) obj[11]);
			} else {
				result.setProgramType((String) obj[11]);
			}
			result.setCurrency((String) obj[12]);
			if (results == null || !results.contains(result)) {
				results.add(result);
			}

		}

		return results;
	}

	/**
	 * @param pricr
	 * @param mDtl
	 * @param employeeId
	 * @throws ParseException
	 */
	public static NormalPricer pricerMasterEnquiryDetails(PricerUploadTemplateBean pricr, NormalPricer mDtl,
			String employeeId) throws ParseException {
		mDtl.setPriceListRemarks1(pricr.getPriceListRemarks());
		mDtl.setPriceListRemarks2(pricr.getPriceListRemarks2());
		mDtl.setPriceListRemarks3(pricr.getPriceListRemarks3());
		mDtl.setPriceListRemarks4(pricr.getPriceListRemarks4());
		//mDtl.setMinSellPriceM(new Money(pricr.getCurrency(), pricr.getMinSell()));
		if ((pricr.getMinSell() != null) && (!pricr.getMinSell().equals(""))) {
			mDtl.setMinSellPrice(Double.parseDouble(pricr.getMinSell()));
		} else {
			mDtl.setMinSellPrice(null);
		}

		if (!QuoteUtil.isEmpty(pricr.getCost())) {
			mDtl.setCost(Double.parseDouble(pricr.getCost()));
		} else {
			mDtl.setCost(null);
		}

		if (!QuoteUtil.isEmpty(pricr.getBottomPrice())) {
			mDtl.setBottomPrice(Double.parseDouble(pricr.getBottomPrice()));
		} else {
			mDtl.setBottomPrice(null);
		}

		mDtl.setLeadTime(pricr.getLeadTime());

		if (!QuoteUtil.isEmpty(pricr.getMPQ())) {
			mDtl.setMpq(Integer.parseInt(pricr.getMPQ()));
		} else {
			mDtl.setMpq(null);
		}

		if (!QuoteUtil.isEmpty(pricr.getMOQ())) {
			mDtl.setMoq(Integer.parseInt(pricr.getMOQ()));
		} else {
			mDtl.setMoq(null);
		}

		if (!QuoteUtil.isEmpty(pricr.getMOV())) {
			mDtl.setMov(Integer.parseInt(pricr.getMOV()));
		} else {
			mDtl.setMov(null);
		}

		if (!QuoteUtil.isEmpty(pricr.getShipmentValidity())) {
			Date date = null;
			try {
				date = QuoteUtil.convertStringToDate(pricr.getShipmentValidity(), "dd/MM/yyyy");
			} catch (ParseException e) {
				throw e;
			}
			mDtl.setShipmentValidity(date);
		} else {
			mDtl.setShipmentValidity(null);
		}

		mDtl.setTermsAndConditions(pricr.getTermsAndConditions());

		mDtl.setLastUpdatedOn(new Date());
		mDtl.setLastUpdatedBy(employeeId);
		mDtl.setPriceValidity(pricr.getValidity());

		/*
		 * String aFlag = pricr.getAllocationFlag(); if
		 * (!QuoteUtil.isEmpty(aFlag)) { aFlag = aFlag.trim(); if
		 * (aFlag.equalsIgnoreCase("Yes")) { mDtl.setAllocationFlag(true); }
		 * else { mDtl.setAllocationFlag(false); } } else {
		 * mDtl.setAllocationFlag(null); }
		 */

		return mDtl;
	}

	/**
	 * @param pisb
	 * @param predicates
	 * @param mfr
	 * @param cb
	 * @param programType
	 * @param programTypeName
	 * @param productGroupName
	 * @param productGroup
	 * @param mfrName
	 */
	/*public static List<Predicate> preparePISearchBean(PISearchBean pisb, List<Predicate> predicates,
			Expression<Manufacturer> mfr, CriteriaBuilder cb, Expression<ProgramType> programType,
			Expression<String> programTypeName, Expression<String> productGroupName,
			Expression<ProductGroup> productGroup, Expression<String> mfrName) {
		if (pisb.getMfrList() != null) {
			if (pisb.getMfrList().isEmpty()) {
				predicates.add(cb.isNull(mfr));
			} else {
				predicates.add(mfrName.in(pisb.getMfrList()));
			}
		}

		if (pisb.getProductGroupList() != null) {
			if (pisb.getProductGroupList().isEmpty()) {
				predicates.add(cb.isNull(productGroup));
			} else {
				predicates.add(productGroupName.in(pisb.getProductGroupList()));
			}
		}

		if (pisb.getProgramTypeList() != null) {
			if (pisb.getProgramTypeList().isEmpty()) {
				predicates.add(cb.isNull(programType));
			} else {
				predicates.add(programTypeName.in(pisb.getProgramTypeList()));
			}
		}

		return predicates;
	}*/

	public static String getText(Locale locale, final String key) {
		ResourceBundle bundle = ResourceBundle.getBundle(DBResourceBundle.class.getName(), locale);
		return bundle.getString(key);
	}

}