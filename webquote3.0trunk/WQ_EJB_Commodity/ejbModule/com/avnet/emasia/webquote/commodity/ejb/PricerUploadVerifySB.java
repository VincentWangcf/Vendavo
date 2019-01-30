package com.avnet.emasia.webquote.commodity.ejb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import static java.util.stream.Collectors.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.eclipse.jetty.util.log.Log;
import org.jboss.ejb3.annotation.TransactionTimeout;

import com.avnet.emasia.webquote.commodity.bean.PricerUploadParametersBean;
import com.avnet.emasia.webquote.commodity.bean.PricerUploadTemplateBean;
import com.avnet.emasia.webquote.commodity.bean.VerifyEffectiveDateResult;
import com.avnet.emasia.webquote.commodity.constant.PRICER_TYPE;
import com.avnet.emasia.webquote.commodity.constant.PricerConstant;
import com.avnet.emasia.webquote.commodity.constant.QED_CHECK_STATE;
import com.avnet.emasia.webquote.commodity.constant.UPLOAD_FILE_ERROR_MSG;
import com.avnet.emasia.webquote.commodity.util.CommodityUtil;
import com.avnet.emasia.webquote.commodity.util.PricerUtils;
import com.avnet.emasia.webquote.commodity.util.StringUtils;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.ContractPricer;
import com.avnet.emasia.webquote.entity.CostIndicator;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.MaterialRegional;
import com.avnet.emasia.webquote.entity.Pricer;
import com.avnet.emasia.webquote.entity.ProductCategory;
import com.avnet.emasia.webquote.entity.SalesCostPricer;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.ejb.ManufacturerSB;
import com.avnet.emasia.webquote.quote.ejb.MaterialSB;
import com.avnet.emasia.webquote.utilities.DBResourceBundle;
import com.avnet.emasia.webquote.utilities.util.QuoteUtil;
import com.ctc.wstx.util.StringUtil;
 

@Stateless
@TransactionTimeout(value=1, unit=TimeUnit.HOURS)
public class PricerUploadVerifySB {
	private static final Logger LOG = Logger.getLogger(PricerUploadVerifySB.class.getName());

	@EJB
	private NormalProgramPartMasterUploadSB normalProgramPartMasterUploadSB;
	@EJB
	ContractPriceUploadSB contractPriceUploadSB;
	
	@EJB
	private MaterialSB materialSB;
	
	@EJB
	private SalesCostPricerUploadSB salesCostPricerUploadSB;
	
	@EJB
	private SimplePricerUploadSB simplePricerUploadSB;
	
	@EJB
	private MaterialRegionalUploadSB materialRegionalUplaodSB;
	@EJB
	private ManufacturerSB manufacturerSB;
	
	@PersistenceContext(unitName = "Server_Source")
	public EntityManager em;
	
	@TransactionTimeout(value=1, unit=TimeUnit.HOURS)
	public String verifyMFRInDBForRegionalInBean(List<PricerUploadTemplateBean> beans,Locale locale) {
		//long start = System.currentTimeMillis();
		String errMsg = "";
		StringBuffer notFoundSbf = new StringBuffer("[");
		UPLOAD_FILE_ERROR_MSG msg = UPLOAD_FILE_ERROR_MSG.OKMSG;
		for (PricerUploadTemplateBean bean : beans) {
			if (!manufacturerSB.existsManufacturerByBizUnit(new BizUnit(bean.getRegion()), bean.getMfr())) {
				bean.setErr(true);
				notFoundSbf.append(bean.getLineSeq());
				notFoundSbf.append(",");
				msg = UPLOAD_FILE_ERROR_MSG.MFRNOTFOUND;
			}else {
				bean.setErr(false);
				bean.setMfrInDb(true);
			}
		}

		if (msg != UPLOAD_FILE_ERROR_MSG.OKMSG) {
			notFoundSbf.deleteCharAt(notFoundSbf.length() - 1);
			notFoundSbf.append("]");

			errMsg = CommodityUtil.getText(locale, "wq.message.mfrNotFound") + notFoundSbf.toString() +"<br/>";
		}
		//long end = System.currentTimeMillis();
		//LOG.info("verifyMFRInDB,takes " + (end - start) + " ms");
		return errMsg;
	}
	/**
	 * [MFR] not existing - If [MFR] is not found in MFR master data, append
	 * error message: MFR cannot be found for row(s): [XXXX], [YYYY],
	 * 
	 * @param beans
	 * @param mfrListCache
	 * @return String
	 */
	public String verifyMFRInDB(List<PricerUploadTemplateBean> beans,Locale locale) {
		//long start = System.currentTimeMillis();
		String errMsg = "";
		StringBuffer notFoundSbf = new StringBuffer("[");
		UPLOAD_FILE_ERROR_MSG msg = UPLOAD_FILE_ERROR_MSG.OKMSG;

		for (PricerUploadTemplateBean bean : beans) {
			if (!bean.isMfrInDb()) {
				bean.setErr(true);
				notFoundSbf.append(bean.getLineSeq());
				notFoundSbf.append(",");
				msg = UPLOAD_FILE_ERROR_MSG.MFRNOTFOUND;
			}
		}

		if (msg != UPLOAD_FILE_ERROR_MSG.OKMSG) {
			notFoundSbf.deleteCharAt(notFoundSbf.length() - 1);
			notFoundSbf.append("]");

			errMsg = CommodityUtil.getText(locale, "wq.message.mfrNotFound") + notFoundSbf.toString() +"<br/>";
		}
		//long end = System.currentTimeMillis();
		//LOG.info("verifyMFRInDB,takes " + (end - start) + " ms");
		return errMsg;
	}

	/**
	 * [Cost Indicator] not existing - If [Cost Indicator] is not found in
	 * Cost_Indicator master data, append error message: Cost Indicator cannot
	 * be found for row(s): [XXXX], [YYYY],
	 */
	public String verifyCstIdctInDB(List<PricerUploadTemplateBean> beans, User user, PricerUploadParametersBean puBean) {
		//long start = System.currentTimeMillis();
		String errMsg = "";
		StringBuffer notFoundSbf = new StringBuffer("[");
		String language = user.getUserLocale();
		UPLOAD_FILE_ERROR_MSG msg = UPLOAD_FILE_ERROR_MSG.OKMSG;
		List<CostIndicator> cstIdctLst = normalProgramPartMasterUploadSB.findCstIdct(beans, user, puBean);
		boolean notFound = true;

		for (PricerUploadTemplateBean bean : beans) {
			notFound = true;
			for (CostIndicator cstIdt : cstIdctLst) {
				if (bean.getCostIndicator()!=null&&bean.getCostIndicator().equalsIgnoreCase(cstIdt.getName())) {
					bean.setOcostIndicator(cstIdt);
					notFound = false;
					break;
				}
			}

			if (notFound) {
				bean.setErr(true);
				notFoundSbf.append(bean.getLineSeq());
				notFoundSbf.append(",");
				msg = UPLOAD_FILE_ERROR_MSG.MFRNOTFOUND;
			}
		}

		if (msg != UPLOAD_FILE_ERROR_MSG.OKMSG) {
			errMsg = CommodityUtil.getText(new Locale(language), "wq.message.costIndicatorNotFound") + notFoundSbf.toString()+"]<br/>";
		}
		//long end = System.currentTimeMillis();
		//LOG.info("verifyCstIdctInDB,takes " + (end - start) + " ms");
		return errMsg;
	}
	
	public String verifyMRegionalInDB(List<PricerUploadTemplateBean> beans, User user,List<Manufacturer> mfrLst, 
			PricerUploadParametersBean puBean) {
		long start = System.currentTimeMillis();
		//salesCostPricerUploadSB.initPricerUploadParametersBean(beans, user, mfrLst, puBean);
		String errMsg = "";
		StringBuffer notFoundSbf = new StringBuffer("[");
		String language = user.getUserLocale();
		UPLOAD_FILE_ERROR_MSG msg = UPLOAD_FILE_ERROR_MSG.OKMSG;
		boolean hasMRegional = false ;
		String region = user.getDefaultBizUnit().getName();
		//if(material)
		for (PricerUploadTemplateBean bean : beans) {
			hasMRegional = false ;
			Material material =  materialRegionalUplaodSB.findMaterial(bean.getMfr(), 
					bean.getFullMfrPart());
			if(material !=null) {
				bean.setMaterial(material);
				/*****master simple salescost need to use the region in bean,but PARTMASTER do not need verify so *****/
				if(PRICER_TYPE.SIMPLE.getName().equals(bean.getPricerType())||
						PRICER_TYPE.SALESCOST.getName().equals(bean.getPricerType())) {
					region = bean.getRegion();
				}
				MaterialRegional mRegional = materialRegionalUplaodSB.findMRegional(material, region);
				if(mRegional !=null) {
					hasMRegional = true ; 
					bean.setMaterialRegional(mRegional);
				}
			}
			/*MaterialRegional mRegional = materialRegionalUplaodSB.findMRegional(bean.getMfr(), 
					bean.getFullMfrPart(), bean.getRegion());*/
			
			if (!hasMRegional) {
				 
				bean.setErr(true);
				notFoundSbf.append(bean.getLineSeq());
				notFoundSbf.append(",");
				msg = UPLOAD_FILE_ERROR_MSG.NOMREGIONAL;
			}	
		}
		if (msg != UPLOAD_FILE_ERROR_MSG.OKMSG) {
			errMsg = CommodityUtil.getText(new Locale(language), "wq.error.MaterialorMRegionalNotFound") + notFoundSbf.toString()+"]<br/>";
		}
		long end = System.currentTimeMillis();
		LOG.info("verifyMRegionalInDB,takes " + (end - start) + " ms");
		return errMsg;
	}
	// 
	public String checkSCFlagInDB(List<PricerUploadTemplateBean> beans, User user,List<Manufacturer> mfrLst, 
			PricerUploadParametersBean puBean) {
		long start = System.currentTimeMillis();
		salesCostPricerUploadSB.initPricerUploadParametersBean(beans, user, mfrLst, puBean);
		String errMsg = "";
		StringBuffer notFoundSbf = new StringBuffer("[");
		String language = user.getUserLocale();
		UPLOAD_FILE_ERROR_MSG msg = UPLOAD_FILE_ERROR_MSG.OKMSG;
		//boolean notFound = true;
		for (PricerUploadTemplateBean bean : beans) { 
			if (bean.getMaterialRegional() == null || !bean.getMaterialRegional().isSalesCostFlag()) {
				bean.setErr(true);
				notFoundSbf.append(bean.getLineSeq());
				notFoundSbf.append(",");
				msg = UPLOAD_FILE_ERROR_MSG.ERROR;
			}
		}
		//
		if (msg != UPLOAD_FILE_ERROR_MSG.OKMSG) {
			errMsg = CommodityUtil.getText(new Locale(language), "wq.error.uplaodsctononscpart") + notFoundSbf.toString()+"]<br/>";
		}
		long end = System.currentTimeMillis();
		LOG.info("checkSCFlagInDB,takes " + (end - start) + " ms");
		return errMsg;
	}
	/**
	 * Add a checking when upload Normal pricer: A-Book Cost Checking �C if
	 * there��s no MATERIAL_DETAIL record for this [MFR] + [MFR P/N] + [Biz_Unit]
	 * & [MATERIAL_CATEGORY] = ��NORMAL��, and current Cost Indicator is not
	 * ��A-Book Cost��, append the error message: Please upload A-Book Cost for
	 * the row:[XXXX], [YYYY],
	 * 
	 * @return
	 */
	public String verifyABookCostIndicator(List<PricerUploadTemplateBean> beans, User user, List<Manufacturer> mfrLst,PricerUploadParametersBean puBean) {
		long start = System.currentTimeMillis();
		LOG.info("Beans size : " + beans.size() + " | user name: " + user.getName());
		List<Integer> errRows = normalProgramPartMasterUploadSB.findCostIndicator(beans, user, mfrLst, puBean);
		String language = user.getUserLocale();
		
		String message = "";
		if (errRows.size() != 0) {
			Collections.sort(errRows);
			message = CommodityUtil.getText(new Locale(language), "wq.message.uploadABookCost") + errRows.toString()+"<br/>";
		}
		long end = System.currentTimeMillis();
		LOG.info("verifyABookCostIndicator,takes " + (end - start) + " ms");
		return message;
	}

	/**
	 * Program and Contract Pricer add/Update Normal Pricer Checking �C if there��s no record
	 * with this [MFR] + [MFR P/N] + [Biz_Unit] found in MATERIAL_DETAIL table,
	 * append the error message ��Please upload Normal Pricer first for the
	 * Row:[XXXX]�� to final message.
	 * check is there normal pricer for pg and contract 
	 * @return
	 */
	public String verifyComponedKeyInMtlDtl(List<PricerUploadTemplateBean> beans, User user) {
		long start = System.currentTimeMillis();
		List<Integer> errRows = normalProgramPartMasterUploadSB.findCompKeyInMtlDtl(beans, user);
		String message = "";
		String language = user.getUserLocale();
		
		if (errRows.size() != 0) {
			message = CommodityUtil.getText(new Locale(language), "wq.message.uploadNormalPricer") + errRows.toString()+"<br/>";
		}
		long end = System.currentTimeMillis();
		LOG.info("verifyComponedKeyInMtlDtl,takes " + (end - start) + " ms");
		return message;
	}

	String getText(Locale locale,String key){
		ResourceBundle bundle = ResourceBundle.getBundle(DBResourceBundle.class.getName(),locale);
		return bundle.getString(key);
	}
	
	/**
	 * [Sold To Code] / [End Customer Code] not existing - If [Sold To Code] /
	 * [End Customer Code] is not found in Program master data, append the error
	 * message, ��Sold To Code/End Customer Code cannot be found for these
	 * row(s)! Row: [XXXX], [YYYY],����. Sold To/End Customer not matched between
	 * code and name �C if [Sold To Code] / [End Customer Code] is not matched
	 * with [Sold To Party]/[End Customer], append error message: Sold To
	 * Code/End Customer Code is not matched with Sold To Party/End Customer for
	 * these row(s): [XXXX], [YYYY]
	 * 
	 * @param batchBeans
	 * @return String
	 */
	public String checkCustomerNumber(List<PricerUploadTemplateBean> beans, Locale locale) {
		long start = System.currentTimeMillis();
		// errRows1 stores the seq no for item without valid sold to customer number;
		// errRows2 stores the seq no for item without valid end customer number;
		// errRows3 stores the seq no for item whose sold to customer number does not match customer name;
		// errRows4 stores the seq no for item whose end customer number does not match customer name;
		List<Integer> errRows1 = new ArrayList<Integer>();
		List<Integer> errRows2 = new ArrayList<Integer>();
		List<Integer> errRows3 = new ArrayList<Integer>();
		List<Integer> errRows4 = new ArrayList<Integer>();

		Set<String> customerNumbers = new HashSet<String>();

		for (PricerUploadTemplateBean bean : beans) {
			customerNumbers.add(bean.getSoldToCode());
			if (bean.getEndCustomerCode() != null) {
				customerNumbers.add(bean.getEndCustomerCode());
			}
		}

		List<Customer> results = normalProgramPartMasterUploadSB.findCustomerNumbers(customerNumbers);

		for (PricerUploadTemplateBean bean : beans) {
			if (bean != null) {
				boolean found1 = false;
				boolean found2 = false;

				for (Customer customer : results) {
					if (customer != null) {
						if (bean.getSoldToCode().equals(customer.getCustomerNumber())) {
							bean.setSoldToCustomer(customer);
							found1 = true;
/*
							String customerName = customer.getCustomerName1();
							if (!QuoteUtil.isEmpty(customer.getCustomerName2())) {
								customerName = customerName  + customer.getCustomerName2();
							}
*/
							String customerName = customer.getCustomerFullName();
							String customerNameFromScreen = bean.getCustomerName()==null? null : bean.getCustomerName().trim();
							if (!StringUtils.isEmpty(customerNameFromScreen)) {
								if (!customerNameFromScreen.equalsIgnoreCase(customerName)) {
									errRows3.add(bean.getLineSeq());
								}
							}else if (QuoteUtil.isEmpty(bean.getSoldToCode()) && !QuoteUtil.isEmpty(bean.getCustomerName())) {				
								errRows3.add(bean.getLineSeq());
							}

							if (found1 && found2) {
								break;
							}
						}

						if (bean.getEndCustomerCode() != null && bean.getEndCustomerCode().equals(customer.getCustomerNumber())) {
							bean.setEndCustomer(customer);
							found2 = true;
/*
							String customerName = customer.getCustomerName1();
							if (!QuoteUtil.isEmpty(customer.getCustomerName2())) {
								customerName = customerName + customer.getCustomerName2();
							}*/

							String customerName = customer.getCustomerFullName();
							String endCustNameFromScreen = bean.getEndCustomerName()==null? null : bean.getEndCustomerName().trim();
							if (!StringUtils.isEmpty(endCustNameFromScreen)&&!endCustNameFromScreen.equalsIgnoreCase(customerName)) {
								errRows4.add(bean.getLineSeq());
							}else if (QuoteUtil.isEmpty(bean.getEndCustomerCode()) && !QuoteUtil.isEmpty(bean.getEndCustomerName())) {
								found2 = true;
								errRows4.add(bean.getLineSeq());
							}

							if (found1 && found2) {
								break;
							}
						}
					}
				}

				if (found1 == false) {
					bean.setErr(true);
					errRows1.add(bean.getLineSeq());
				}

				if (!QuoteUtil.isEmpty(bean.getEndCustomerCode()) && found2 == false) {
					bean.setErr(true);
					errRows2.add(bean.getLineSeq());
				}
			}
		}

		String errMsg = "";

		if (errRows1.size() != 0) {
			Collections.sort(errRows1);
			errMsg = CommodityUtil.getText(locale, "wq.message.soldToCodeNotFound") + errRows1;
		}

		if (errRows2.size() != 0) {
			Collections.sort(errRows2);
			if (errMsg == "") {				
				errMsg = CommodityUtil.getText(locale, "wq.message.endCustomerCodeNotFound") + errRows2;
			} else {
				errMsg = errMsg + "<br/>" + CommodityUtil.getText(locale, "wq.message.endCustomerCodeNotFound") + errRows2;
			}

		}

		if (errRows3.size() != 0) {
			Collections.sort(errRows3);
			if (errMsg == "") {
				errMsg = CommodityUtil.getText(locale, "wq.message.custNameNotSoldToCode") + errRows3;
			} else {
				errMsg = errMsg + "<br/>" + CommodityUtil.getText(locale, "wq.message.custNameNotSoldToCode") + errRows3;
			}

		}

		if (errRows4.size() != 0) {
			Collections.sort(errRows4);
			if (errMsg == "") {
				errMsg = CommodityUtil.getText(locale, "wq.message.endCustNameNotEndCustCode") + errRows4;
			} else {
				errMsg = errMsg + "<br/>" + CommodityUtil.getText(locale, "wq.message.endCustNameNotEndCustCode") + errRows4;
			}

		}
		long end = System.currentTimeMillis();
		LOG.info("checkCustomerNumber,takes " + (end - start) + " ms");
		return errMsg;
	}
	
	@TransactionTimeout(value = 7200, unit = TimeUnit.SECONDS) 
	public String batchVerifyEffectiveDateInDB(String pricerType, List<PricerUploadTemplateBean> beans, User user, List<Manufacturer> mfrLst) {
		long start = System.currentTimeMillis();
		List<Integer> errRows1 = new ArrayList<Integer>();
		// batch
		List<VerifyEffectiveDateResult> allDBList = normalProgramPartMasterUploadSB.batchFindMaterialDetailForVerify(beans, user, pricerType,mfrLst);
		HashMap<String, List<VerifyEffectiveDateResult>> allQedList =  this.constructQEDCompareList(pricerType,allDBList, user);
		
		//Collections.sort(beans,new PricerUploadTemplateBeanQedComparator());
		for (PricerUploadTemplateBean bean : beans) {
			//1, get the query key
			String dbKey = pricerType.toUpperCase() + " " + PricerUtils.getUkFromPricerUploadTemplateBean(bean, user);

			//2, get the list from local memory
			List<VerifyEffectiveDateResult> subQedList = allQedList.get(dbKey);
			
			if (subQedList!=null&&subQedList .size() != 0) {
				long materialId = subQedList.get(0).getMaterialId();
				long metarialDetailId = -1;
				Date qedDateInFile = null;
				Date quotationEffectiveDate = null;
				Date displayQuoteEffDate = null;
				Date quotationEffectiveTo = null;
				boolean isTimeOverlap = false;
				boolean isSameQuotationEffectiveDate = false;
				VerifyEffectiveDateResult materialDetailHavaNullQed = null;

				/**
				 * Retrieve the [Quotation Effective Date] and [Validity]
				 * from the file,
				 */
				String qedStringInFile = bean.getQuotationEffectiveDate();
				String validityInFile = bean.getValidity();
				/**
				 * convert them into a pair of date range called [Effective
				 * From] and [Effective To]
				 */
				Date effectiveForm = PricerUtils.getEffectiveFrom(qedStringInFile);
				Date effectiveTo = PricerUtils.getEffectiveTo(effectiveForm, validityInFile);
				// Added by Punit for CPD Exercise
				qedDateInFile = deleteMaterialDetailAndContractPriceQuery(bean, bean.getQuotationEffectiveDate());
				
				VerifyEffectiveDateResult matchedQed = null;
				for (VerifyEffectiveDateResult materialDetail : subQedList) {
					displayQuoteEffDate = materialDetail.getDisplayQuoteEffDate();
					quotationEffectiveDate = materialDetail.getQuotationEffectiveDate();
					quotationEffectiveTo = materialDetail.getQuotationEffectiveTo();

					if (qedDateInFile != null && quotationEffectiveDate != null && 
							PricerUtils.isSameDate(qedDateInFile, displayQuoteEffDate)) {
						isSameQuotationEffectiveDate = true;
						metarialDetailId = materialDetail.getMaterialDetailId();
						matchedQed = materialDetail;
					}
					if (displayQuoteEffDate == null) {
						materialDetailHavaNullQed = materialDetail;
						matchedQed = materialDetail;
					}
				}			
				/**
				 * see:Upload Pricer Updated 3.1.2.2.1
				 */
				if (QuoteUtil.isEmpty(qedStringInFile)) {// the [QED] in file isempty
					isTimeOverlap = PricerUtils.comparisonTimeByList(effectiveForm, effectiveTo, subQedList);
					if (isTimeOverlap) {
						errRows1.add(bean.getLineSeq());
						bean.setQedCheckState(QED_CHECK_STATE.OVERLAPPED.getName());
						subQedList= this.updateLocalDBList(subQedList, bean, user);
						allQedList.put(dbKey, subQedList);
					} else {
						if (materialDetailHavaNullQed != null) {
							bean.setQedCheckState(QED_CHECK_STATE.UPDATE.getName());// set update
							bean.setMetarialId(materialDetailHavaNullQed.getMaterialId());
							bean.setMetarialDetailId(materialDetailHavaNullQed.getMaterialDetailId());
							subQedList= this.updateLocalDBList(subQedList, bean, user); // fix enhancement 357 -- to upload local DB list
							allQedList.put(dbKey, subQedList);
						} else {
							bean.setQedCheckState(QED_CHECK_STATE.INSERT.getName());
							subQedList = this.insertLocalDBList(subQedList,bean,user); // fix enhancement 357 -- to add new entity local DB list
							allQedList.put(dbKey, subQedList);
							
						}
					}
				} else {// 3.1.2.2.2.the [QED] in file is not empty
					if (isSameQuotationEffectiveDate) {
						for (VerifyEffectiveDateResult materialDetail : subQedList) {

							quotationEffectiveDate = materialDetail.getQuotationEffectiveDate();
							quotationEffectiveTo = materialDetail.getQuotationEffectiveTo();// Quotation Effective To

							if (!PricerUtils.isSameDate(effectiveForm, quotationEffectiveDate)) {
								Boolean temp = PricerUtils.comparisonTime(effectiveForm, effectiveTo, quotationEffectiveDate, quotationEffectiveTo);
								if (temp) {
									isTimeOverlap = true;
									break;
								}
							}
						}
						// Added by Punit for CPD Exercise
						subQedList = verifyEffectiveDateForQuotationInsert(isTimeOverlap, errRows1, bean, subQedList, allQedList, dbKey, user,metarialDetailId,materialId);
					} else {
						for (VerifyEffectiveDateResult materialDetail : subQedList) {
							quotationEffectiveDate = materialDetail.getQuotationEffectiveDate();
							quotationEffectiveTo = materialDetail.getQuotationEffectiveTo();
							Boolean temp = PricerUtils.comparisonTime(effectiveForm, effectiveTo, quotationEffectiveDate, quotationEffectiveTo);
							if (temp) {
								isTimeOverlap = true;
								break;
							}
						}
						// Added by Punit for CPD Exercise
						subQedList = verifyEffectiveDateForQuotation(isTimeOverlap, errRows1, bean, subQedList,
								allQedList, dbKey, user);
					}
				}
			} else {
				bean.setQedCheckState(QED_CHECK_STATE.INSERT.getName());
				subQedList= this.insertLocalDBList(subQedList,bean,user); // fix enhancement 357 -- to add new entity local DB list
				allQedList.put(dbKey, subQedList);
			}
		}

		long end = System.currentTimeMillis();
		LOG.info("batchVerifyEffectiveDateInDB,takes " + (end - start) + " ms");
		String errMsg = "";
		String language = user.getUserLocale();
		if (errRows1.size() != 0) {
			Collections.sort(errRows1);
			errMsg = CommodityUtil.getText(new Locale(language), "wq.message.plsCorrectRecords") + errRows1+"<br/>";
		}

		return errMsg;
	}
	

	private Date deleteMaterialDetailAndContractPriceQuery(PricerUploadTemplateBean bean, String qedStringInFile) {
		Date qedDateInFile = null;
		
		if (!QuoteUtil.isEmpty(qedStringInFile)) {
			qedDateInFile = PricerUtils.convertToDate(qedStringInFile);
		}
		return qedDateInFile;
	}

	private List<VerifyEffectiveDateResult> insertLocalDBList(
						List<VerifyEffectiveDateResult> list,
						PricerUploadTemplateBean bean, User user) {
		VerifyEffectiveDateResult effectiveResult = new VerifyEffectiveDateResult();
		effectiveResult.setBizUnit(user.getDefaultBizUnit().getName());
		effectiveResult.setCostIndicator(bean.getCostIndicator());
				
		effectiveResult.setFullMfrPartNumber(bean.getFullMfrPart());
		//effectiveResult.setMaterialCategory();
		//effectiveResult.setMaterialDetailId();
		effectiveResult.getMaterialId();
		effectiveResult.setMfr(bean.getMfr());

		if (!QuoteUtil.isEmpty(bean.getQuotationEffectiveDate())) {
			Date date = PricerUtils.convertToDate(bean.getQuotationEffectiveDate());

			effectiveResult.setQuotationEffectiveDate(date);
			effectiveResult.setDisplayQuoteEffDate(date);
			Date effectiveTo = PricerUtils.getEffectiveTo(date, bean.getValidity());
			effectiveResult.setQuotationEffectiveTo(effectiveTo);
		} else {
			Date date = PricerUtils.getTimesmorning(new Date());
			effectiveResult.setQuotationEffectiveDate(date);
			effectiveResult.setDisplayQuoteEffDate(null);
			Date effectiveTo = PricerUtils.getEffectiveTo(date, bean.getValidity());
			effectiveResult.setQuotationEffectiveTo(effectiveTo);
		}
		effectiveResult.setPriceValidity(bean.getValidity());	
		if(null==list ) {
			list = new ArrayList<VerifyEffectiveDateResult>();
		}
		list.add(effectiveResult);
		
		return list;
	}

	private List<VerifyEffectiveDateResult> updateLocalDBList(
						List<VerifyEffectiveDateResult> results,
						PricerUploadTemplateBean bean, User user) {
		VerifyEffectiveDateResult effResult= null;
		Date quotationEffectiveDate = null;
		if (!QuoteUtil.isEmpty(bean.getQuotationEffectiveDate())) {
			quotationEffectiveDate= PricerUtils.convertToDate(bean.getQuotationEffectiveDate());
		} else {
			quotationEffectiveDate= PricerUtils.getTimesmorning(new Date());
		}
		
		Date effectiveTo = PricerUtils.getEffectiveTo(quotationEffectiveDate, bean.getValidity());
		
		for(VerifyEffectiveDateResult result:results) {
			if(result.getMaterialDetailId() == bean.getMetarialDetailId() && quotationEffectiveDate.equals(result.getQuotationEffectiveDate())) {
				effResult = result;
			}
		}
		if(null==effResult) {
			 return insertLocalDBList(results, bean, user);
		}else {
			Date originalEffectiveTo = effResult.getQuotationEffectiveTo();
			boolean isUpdate = false;
			
			if(originalEffectiveTo.compareTo(effectiveTo)<0) { //when the list to date is ealier than the new, then update it
				isUpdate = true;
			}else {
				isUpdate = false;
			}
			if(isUpdate) {
				if (!QuoteUtil.isEmpty(bean.getQuotationEffectiveDate())) {
		
					effResult.setQuotationEffectiveDate(quotationEffectiveDate);
					effResult.setDisplayQuoteEffDate(quotationEffectiveDate);
					effResult.setQuotationEffectiveTo(effectiveTo);
				} else {
					effResult.setQuotationEffectiveDate(quotationEffectiveDate);
					effResult.setDisplayQuoteEffDate(null);
					effResult.setQuotationEffectiveTo(effectiveTo);
				}
				effResult.setPriceValidity(bean.getValidity());
			}
			return results;
		}
		
					
	}

	public String pricerMatchingInDB(String pricerType, List<PricerUploadTemplateBean> beans, User user, List<Manufacturer> mfrLst) {
		List<Integer> errRows1 = new LinkedList<Integer>();
		//batch 
		List<PricerUploadTemplateBean> subItemLst = null;

		int size = beans.size();
		int batchCount = size / PricerConstant.BATCH_SIZE;
		int mod = size % PricerConstant.BATCH_SIZE;
		if (mod != 0) {
			batchCount++;
		}
		
		for(int i=0;i<batchCount;i++){
			if(PricerConstant.BATCH_SIZE*(i+1)>size){
				subItemLst= beans.subList(i*PricerConstant.BATCH_SIZE,size);
			}else{
				subItemLst= beans.subList(i*PricerConstant.BATCH_SIZE,PricerConstant.BATCH_SIZE*(i+1));
			}
			if (PRICER_TYPE.NORMAL.getName().equalsIgnoreCase(pricerType)||PRICER_TYPE.PROGRAM.getName().equalsIgnoreCase(pricerType)) {
				List<VerifyEffectiveDateResult> pricerList = normalProgramPartMasterUploadSB.batchFindMaterialDetail(subItemLst, user,pricerType,mfrLst);
				String value1 = null;
				String value2 =null;
				for(PricerUploadTemplateBean bean:subItemLst){
					boolean isfindDataInDb = false;
					value1 = PricerUtils.getUkFromPricerUploadTemplateBeanForNPRm(bean,user);
					if(!QuoteUtil.isEmpty(bean.getQuotationEffectiveDate())){		
						value1 = value1 + PricerUtils.convertToDate(bean.getQuotationEffectiveDate()).getTime();
					}
					for(VerifyEffectiveDateResult materialDetail :pricerList){
						value2 = PricerUtils.getUkFromVerifyEffectiveDateResult(materialDetail) ;
						if(materialDetail.getDisplayQuoteEffDate()!=null){		
							value2 = value2 + materialDetail.getDisplayQuoteEffDate().getTime();
						}
						if(value1.equalsIgnoreCase(value2)){
							bean.setMetarialDetailId(materialDetail.getMaterialDetailId());
							isfindDataInDb = true;
							break;
						}
					}
					
					if(!isfindDataInDb){
						errRows1.add(bean.getLineSeq());
					}
					
				}
			} else if (PRICER_TYPE.CONTRACT.getName().equalsIgnoreCase(pricerType)) {
				List<ContractPricer> pricerList = contractPriceUploadSB.batchFindContractPrice(beans, user);
				String value1 = null;
				String value2 =null;
				for(PricerUploadTemplateBean bean:subItemLst){
					boolean isfindDataInDb = false;
					
					value1 = PricerUtils.getUkFromPricerUploadTemplateBeanForCRm(bean,user)+bean.getQuotationEffectiveDate();
					//fix defect 351 june guo 20150324
					if(bean.getEndCustomerCode()!=null){
						value1 = value1	 + bean.getEndCustomerCode();
					}

					for(ContractPricer contractPrice :pricerList){
						if(contractPrice.getSoldtoCustomer()!=null){
						value2 = PricerUtils.getUkFromContractPrice(contractPrice) +QuoteUtil.convertDateToStr(contractPrice.getDisplayQuoteEffDate());
						}
						if(contractPrice.getEndCustomer()!=null){
							value2 = value2	 + contractPrice.getEndCustomer().getCustomerNumber();
						}

						if(value1.equalsIgnoreCase(value2)){
							isfindDataInDb = true;
							bean.setContractPriceId(contractPrice.getId());
							break;
						}
					}
					
					if(!isfindDataInDb){
						errRows1.add(bean.getLineSeq());
					}
					
				}
			}else if (PRICER_TYPE.SIMPLE.getName().equals(pricerType)) {
				
				for(PricerUploadTemplateBean bean:subItemLst){
					if(bean.getMaterial()==null) {
						bean.setMaterial(materialRegionalUplaodSB.findMaterial(bean.getMfr(), bean.getFullMfrPart()));
					}
					Pricer pricer = simplePricerUploadSB.getPricerByComponentKey(bean);
					if(pricer ==null){
						errRows1.add(bean.getLineSeq());
					}else {
						bean.setPricer(pricer);
					}
				}
			}else if (PRICER_TYPE.SALESCOST.getName().equals(pricerType)) {
				for(PricerUploadTemplateBean bean:subItemLst){
					if(bean.getMaterial()==null) {
						bean.setMaterial(materialRegionalUplaodSB.findMaterial(bean.getMfr(), bean.getFullMfrPart()));
					}
					Pricer pricer = salesCostPricerUploadSB.getSCPricerByComponentKey(bean);
					if(pricer ==null){
						errRows1.add(bean.getLineSeq());
					}else {
						bean.setPricer(pricer);
					}
				}
				
			}
			
		}
	
		String errMsg = "";
		String language = user.getUserLocale();
		if (errRows1.size() != 0) {
			Collections.sort(errRows1);			
			errMsg = CommodityUtil.getText(new Locale(language), "wq.message.row") + errRows1 +": "+CommodityUtil.getText(new Locale(language), "wq.message.recordsNotFound")+"!";
		}
		return errMsg;
	}

	public String verifyEffectiveDateForContract(List<PricerUploadTemplateBean> beans, User user, List<Manufacturer> mfrLst) {
		long start = System.currentTimeMillis();
		List<Integer> errRows1 = new ArrayList<Integer>();
		String pricerType = PRICER_TYPE.CONTRACT.getName();
		
//		Map<String, List<ContractPrice>> dbList = new HashMap<String, List<ContractPrice>>();
		List<VerifyEffectiveDateResult> dbQedList = contractPriceUploadSB.batchFindMaterialDetail(beans, user,mfrLst);
		
		HashMap<String, List<VerifyEffectiveDateResult>> allQedList = this.constructQEDCompareList(pricerType,dbQedList,user);

		//Collections.sort(beans,new PricerUploadTemplateBeanQedComparator());
		
		for (PricerUploadTemplateBean bean : beans) {
			
			//1, get the query key
			String dbKey = pricerType.toUpperCase() + " " + PricerUtils.getUkFromPricerUploadTemplateBean(bean, user);
			
			//2, get the list from local memory
			List<VerifyEffectiveDateResult> subQedList = allQedList.get(dbKey.toString());
		
			int size = null==subQedList?0:subQedList.size();
			if (size != 0) {
				long materialId =subQedList.get(0).getMaterialId();
				long metarialDetailId = -1;
				Date qedDateInFile = null;
				Date quotationEffectiveDate = null;
				Date displayQuoteEffDate = null;
				Date quotationEffectiveTo = null;
				boolean isTimeOverlap = false;			
				boolean isSameQuotationEffectiveDate = false;
				VerifyEffectiveDateResult materialDetailHavaNullQed = null;
	
				/**
				 * Retrieve the [Quotation Effective Date] and [Validity] from
				 * the file,
				 */
				String qedStringInFile = bean.getQuotationEffectiveDate();
				String validityInFile = bean.getValidity();
				/**
				 * convert them into a pair of date range called [Effective
				 * From] and [Effective To]
				 */
				Date effectiveForm = PricerUtils.getEffectiveFrom(qedStringInFile);
				Date effectiveTo = PricerUtils.getEffectiveTo(effectiveForm, validityInFile);
				
				// Added by Punit for CPD Exercise
				qedDateInFile = deleteMaterialDetailAndContractPriceQuery(bean, bean.getQuotationEffectiveDate());
				
//				VerifyEffectiveDateResult matchedPrice = null;
				for (VerifyEffectiveDateResult contractPrice : subQedList) {
					displayQuoteEffDate = contractPrice.getDisplayQuoteEffDate();
					quotationEffectiveDate = contractPrice.getQuotationEffectiveDate();
					quotationEffectiveTo = contractPrice.getQuotationEffectiveTo();

					if(qedDateInFile!=null&&quotationEffectiveDate!=null&&PricerUtils.isSameDate(qedDateInFile, quotationEffectiveDate)){
						isSameQuotationEffectiveDate = true;
						metarialDetailId = contractPrice.getMaterialDetailId();
//						matchedPrice = contractPrice;
					}
					if (displayQuoteEffDate == null) {
						materialDetailHavaNullQed = contractPrice;
//						matchedPrice = contractPrice;
					}				
				}
				/**
				 * see:Upload Pricer Updated 3.1.2.2.1
				 */
				if (QuoteUtil.isEmpty(qedStringInFile)) {//the [QED] in file is empty
					isTimeOverlap = PricerUtils.comparisonTimeByList(effectiveForm, effectiveTo, subQedList);
					if(isTimeOverlap){
						errRows1.add(bean.getLineSeq());
						bean.setQedCheckState(QED_CHECK_STATE.OVERLAPPED.getName());// set overlapped
						subQedList= this.updateLocalDBList(subQedList, bean, user);
						allQedList.put(dbKey, subQedList);
					}else{
						if (materialDetailHavaNullQed!=null) {
							bean.setQedCheckState(QED_CHECK_STATE.UPDATE.getName());// set update
							bean.setMetarialId(materialId);
							bean.setMetarialDetailId(materialDetailHavaNullQed.getMaterialDetailId());
//							this.updateLocalDBListForContract(list,bean,user); // fix enhancement 357 -- to upload local DB list
							subQedList= this.updateLocalDBList(subQedList, bean, user);
							allQedList.put(dbKey, subQedList);
						} else {
							bean.setQedCheckState(QED_CHECK_STATE.INSERT.getName());// set insert
//							this.insertLocalDBListForContract(list,bean,user); // fix enhancement 357 -- to add new entity local DB lists
							subQedList= this.insertLocalDBList(subQedList, bean, user);
							allQedList.put(dbKey, subQedList);
						}
					}
				}else{//3.1.2.2.2.the [QED] in file is not empty
					if(isSameQuotationEffectiveDate){
						for (VerifyEffectiveDateResult contractPrice : subQedList) {
							
							quotationEffectiveDate = contractPrice.getQuotationEffectiveDate();
							quotationEffectiveTo = contractPrice.getQuotationEffectiveTo();//Quotation Effective To
								
							if(!PricerUtils.isSameDate(effectiveForm, quotationEffectiveDate)){
								Boolean temp = PricerUtils.comparisonTime(effectiveForm, effectiveTo, quotationEffectiveDate, quotationEffectiveTo);
								if (temp) {
									isTimeOverlap = true;
									break;
								}
							}
						}
						// Added by Punit for CPD Exercise
						subQedList = verifyEffectiveDateForQuotationInsert(isTimeOverlap, errRows1, bean, subQedList,
								allQedList, dbKey, user, metarialDetailId, materialId);
					}else{
						for (VerifyEffectiveDateResult contractPrice : subQedList) {
							quotationEffectiveDate = contractPrice.getQuotationEffectiveDate();
							quotationEffectiveTo = contractPrice.getQuotationEffectiveTo();//Quotation Effective To
							Boolean temp = PricerUtils.comparisonTime(effectiveForm, effectiveTo, quotationEffectiveDate, quotationEffectiveTo);
							if (temp) {
								isTimeOverlap = true;
								break;
							}
						}
						// Added by Punit for CPD Exercise
						subQedList = verifyEffectiveDateForQuotation(isTimeOverlap, errRows1, bean, subQedList,
								allQedList, dbKey, user);
					}						
				}
			}else{
				bean.setQedCheckState(QED_CHECK_STATE.INSERT.getName());
				subQedList= this.insertLocalDBList(subQedList, bean, user);
				allQedList.put(dbKey, subQedList);
			}
		}
		long end = System.currentTimeMillis();
		LOG.info("verifyEffectiveDateForContract,takes " + (end - start) + " ms");
		String errMsg = "";
		String language = user.getUserLocale();
		if (errRows1.size() != 0) {
			Collections.sort(errRows1);
			errMsg = CommodityUtil.getText(new Locale(language), "wq.message.plsCorrectRecords") + errRows1+"<br/>";
		}
		return errMsg;
	}
	
	@TransactionTimeout(value=1, unit=TimeUnit.HOURS)
	public String verifyEffectiveDateForSimplePricer(List<PricerUploadTemplateBean> beans, User user) {
		VerifyEffectiveDateTemplate verify = new VerifyEffectiveDateTemplate() {
			@Override
			public Pricer getPricer(PricerUploadTemplateBean bean) {
				return  simplePricerUploadSB.getPricerByComponentKey(bean);
			}
			@Override
			public List<Pricer> getOverLapPricers(PricerUploadTemplateBean bean, Long pricerId) {
				return simplePricerUploadSB.getAllOverLapSimplePricers(bean, pricerId);
			}
		};
		return verify.verifyEffectiveDate(beans, user);
	}

	@TransactionTimeout(value=1, unit=TimeUnit.HOURS)
	public String verifyEffectiveDateForSalesCost(List<PricerUploadTemplateBean> beans, User user) {
		VerifyEffectiveDateTemplate verify = new VerifyEffectiveDateTemplate() {
			@Override
			public Pricer getPricer(PricerUploadTemplateBean bean) {
				return salesCostPricerUploadSB.getSCPricerByComponentKey(bean);
			}
			@Override
			public List<Pricer> getOverLapPricers(PricerUploadTemplateBean bean, Long pricerId) {
				return salesCostPricerUploadSB.getAllOverLapSCPricers(bean, pricerId);
			}
		};
		return verify.verifyEffectiveDate(beans, user);
	}
	/**********
	 * for using verify the overlap qed.
	 * @author 044878
	 *
	 */
	public abstract class VerifyEffectiveDateTemplate {
		
		public abstract Pricer getPricer(PricerUploadTemplateBean bean);
		public abstract List<Pricer> getOverLapPricers(PricerUploadTemplateBean bean, Long pricerId);
		
		public String verifyEffectiveDate(List<PricerUploadTemplateBean> beans, User user)  {
			long start = System.currentTimeMillis();
			List<Integer> errRows1 = new ArrayList<Integer>();
			Map<String,List<PricerUploadTemplateBean>> groupByKeyNoQedMap = beans.stream().collect(groupingBy(b->b.getKeyNoQed()));
			for (PricerUploadTemplateBean bean : beans) {
				// no need verify next step because of MaterialRegional null
				if(null == bean.getMaterial()) {
					bean.setMaterial(materialRegionalUplaodSB.findMaterial(bean.getMfr(), bean.getFullMfrPart()));
					if(null == bean.getMaterial()) return "";
				}
				
				if(bean.getMaterialRegional()==null) return "";
				
				// 1 update or insert ?
				Pricer pricer = null;
				if(bean.getMetarialDetailId()>0) {
					pricer = em.find(Pricer.class, bean.getMetarialDetailId());
				}else {
					pricer = getPricer(bean);
				}
				
				if(pricer !=null &&pricer.getId()>0) {
					bean.setQedCheckState(QED_CHECK_STATE.UPDATE.getName());
					bean.setPricer(pricer);
				}else {
					bean.setQedCheckState(QED_CHECK_STATE.INSERT.getName());
				}
					 
				// 2 has overlap for update or insert?
				Long id = (pricer !=null &&pricer.getId()>0) ? pricer.getId():null;
				List<Pricer> pricers = getOverLapPricers(bean, id);
				boolean hasOverLap = false;
				if(pricers.isEmpty()) {// check overlap data in excel if no overlap in db.
					List<PricerUploadTemplateBean> groupList = groupByKeyNoQedMap.get(bean.getKeyNoQed());
					if(!groupList.isEmpty()) {
						boolean isEqual = false;//set true when (null == null or empty == empty or other equals for QuotationEffectiveDate)
						for(PricerUploadTemplateBean beanSec :groupList) {
							isEqual = false;
							if(QuoteUtil.isEmpty(bean.getQuotationEffectiveDate()) && 
									QuoteUtil.isEmpty(beanSec.getQuotationEffectiveDate())) {
								isEqual = true;
							} else if(bean.getQuotationEffectiveDate().equals(beanSec.getQuotationEffectiveDate())) {
								isEqual = true;
							}
							//not have same QED ,NEED TO CHECK OVERLAP.
							if(!isEqual) {
								Date effectiveForm = PricerUtils.getEffectiveFrom(bean.getQuotationEffectiveDate());
								Date effectiveTo = PricerUtils.getEffectiveTo(effectiveForm, bean.getValidity());
								Date effectiveFormSec = PricerUtils.getEffectiveFrom(beanSec.getQuotationEffectiveDate());
								Date effectiveToSec = PricerUtils.getEffectiveTo(effectiveForm, beanSec.getValidity());
								hasOverLap = PricerUtils.comparisonTime(effectiveForm, effectiveTo, effectiveFormSec, effectiveToSec);
								if(hasOverLap) {
									break;
								}
							}
						}
					}
				}else {
					//has overlap in db
					hasOverLap = true;
				}
				// not data for hasOverLap
				if(hasOverLap) {
					errRows1.add(bean.getLineSeq());
					bean.setQedCheckState(QED_CHECK_STATE.OVERLAPPED.getName());
				}
			}
			long end = System.currentTimeMillis();
			LOG.info("verifyEffectiveDate For " + beans.get(0).getPricerType() +",takes " + (end - start) + " ms");
			String errMsg = "";
			String language = user.getUserLocale();
			if (errRows1.size() != 0) {
				Collections.sort(errRows1);
				errMsg = CommodityUtil.getText(new Locale(language), "wq.message.plsCorrectRecords") + errRows1+"<br/>";
			}
			return errMsg;
		}
	}
	
	private HashMap<String, List<VerifyEffectiveDateResult>> constructQEDCompareList(String pricerType,List<VerifyEffectiveDateResult> list,User user) {
		HashMap<String, List<VerifyEffectiveDateResult>> qedList = new HashMap<String, List<VerifyEffectiveDateResult>>();
		for(VerifyEffectiveDateResult result:list) {
			String dbKey = PricerUtils.getUkFromEffeciveResult(pricerType,result, user);
			if(null==qedList || null==qedList.get(dbKey)) {
				List<VerifyEffectiveDateResult> subQedList = new LinkedList<VerifyEffectiveDateResult>();
				subQedList.add(result);
				qedList.put(dbKey, subQedList);
			}else {
				List<VerifyEffectiveDateResult> subQedList =qedList.get(dbKey);
				if(!subQedList.contains(result)) {
					subQedList.add(result);
				}
				
			}
		}
	
		return qedList;
	}


	/**
	 * [ProductCat] not existing - If [ProductCat] is not found in ProductCat master data, append
	 * error message: ProductCat cannot be found for row(s): [XXXX], [YYYY],
	 * 
	 * @param beans
	 * @return String
	 */
	public Object verifyProductCatInDB(List<PricerUploadTemplateBean> beans, Locale locale) {
		List<Integer> errRows1 = new ArrayList<Integer>();
		List<ProductCategory> productCategorys = materialSB.findAllProductCategory();
		
		for (PricerUploadTemplateBean bean : beans) {
			if(!QuoteUtil.isEmpty(bean.getProductCat())&&!PricerUtils.isValidProductCat(bean.getProductCat(), productCategorys)){
				bean.setErr(true);
				errRows1.add(bean.getLineSeq());
			}
			
		}
		String errMsg = "";
		if (errRows1.size() != 0) {
			Collections.sort(errRows1);
			errMsg = CommodityUtil.getText(locale, "wq.message.productCatNotFound") +errRows1 +"<br/>";
		}
		
		return errMsg;
	}

	/**
	 * @param isTimeOverlap
	 * @param errRows1
	 * @param bean
	 * @param subQedList
	 * @param allQedList
	 * @param dbKey
	 * @param user
	 * @return
	 */
	private List<VerifyEffectiveDateResult> verifyEffectiveDateForQuotation(boolean isTimeOverlap,
			List<Integer> errRows1, PricerUploadTemplateBean bean, List<VerifyEffectiveDateResult> subQedList,
			HashMap<String, List<VerifyEffectiveDateResult>> allQedList, String dbKey, User user) {
		if (isTimeOverlap) {
			errRows1.add(bean.getLineSeq());
			bean.setQedCheckState(QED_CHECK_STATE.OVERLAPPED.getName());
			subQedList = this.updateLocalDBList(subQedList, bean, user);
			allQedList.put(dbKey, subQedList);
		} else {
			bean.setQedCheckState(QED_CHECK_STATE.INSERT.getName());
			subQedList = this.insertLocalDBList(subQedList, bean, user); 
			allQedList.put(dbKey, subQedList);
		}
		
		return subQedList;
	}
	
	
	/**
	 * @param isTimeOverlap
	 * @param errRows1
	 * @param bean
	 * @param subQedList
	 * @param allQedList
	 * @param dbKey
	 * @param user
	 * @param metarialDetailId
	 * @param materialId
	 * @return
	 */
	private List<VerifyEffectiveDateResult> verifyEffectiveDateForQuotationInsert(boolean isTimeOverlap,
			List<Integer> errRows1, PricerUploadTemplateBean bean, List<VerifyEffectiveDateResult> subQedList,
			HashMap<String, List<VerifyEffectiveDateResult>> allQedList, String dbKey, User user,Long metarialDetailId,long materialId) {
		if (isTimeOverlap) {
			errRows1.add(bean.getLineSeq());
			bean.setQedCheckState(QED_CHECK_STATE.OVERLAPPED.getName());// set overlapped
			subQedList= this.updateLocalDBList(subQedList, bean, user);
			allQedList.put(dbKey, subQedList);
		} else {
			bean.setMetarialId(materialId);
			bean.setMetarialDetailId(metarialDetailId);
			bean.setQedCheckState(QED_CHECK_STATE.UPDATE.getName());// set update
			subQedList= this.updateLocalDBList(subQedList, bean, user);
			allQedList.put(dbKey, subQedList);
		}
		
		return subQedList;
	}
}