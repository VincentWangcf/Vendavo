package com.avnet.emasia.webquote.commodity.util;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.avnet.emasia.webquote.commodity.bean.PricerUploadParametersBean;
import com.avnet.emasia.webquote.commodity.bean.PricerUploadTemplateBean;
import com.avnet.emasia.webquote.commodity.bean.VerifyEffectiveDateResult;
import com.avnet.emasia.webquote.commodity.constant.PricerConstant;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.ContractPricer;
import com.avnet.emasia.webquote.entity.Currency;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.Money;
import com.avnet.emasia.webquote.entity.NormalPricer;
import com.avnet.emasia.webquote.entity.Pricer;
import com.avnet.emasia.webquote.entity.ProductCategory;
import com.avnet.emasia.webquote.entity.ProgramPricer;
import com.avnet.emasia.webquote.entity.ProgramType;
import com.avnet.emasia.webquote.entity.QuantityBreakPrice;
import com.avnet.emasia.webquote.entity.SalesCostPricer;
import com.avnet.emasia.webquote.entity.SalesCostType;
import com.avnet.emasia.webquote.entity.SimplePricer;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.utilities.util.QuoteUtil;

public class PricerUtils {
	static final Logger LOG = Logger.getLogger(PricerUtils.class.getSimpleName());

	/**
	 * format a double in scientific notation to a double not in scientific
	 * notation
	 * 
	 * @param value
	 * @return
	 */
	public static String formatDouble(double value) {
		NumberFormat formatter = new DecimalFormat("###.#####");
		String f = formatter.format(value);
		return f;
	}

	public static String formatBigDecimal(BigDecimal value) {
		NumberFormat formatter = new DecimalFormat("###.#####");
		String f = formatter.format(value);
		return f;
	}
	
	/**
	 * Get the specified month after (before) the date , the parameters of the
	 * negative can be specified month before
	 */
	public static Date getAfterMonth(int month) {
		Calendar c = Calendar.getInstance();
		Date date = new Date();
		c.setTime(date);
		c.add(Calendar.MONTH, month);
		return c.getTime();
	}

	public static String convertFormat2Time(Date date) {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String returnStr = null;
		try {
			returnStr = format1.format(date);

		} catch (Exception e) {
			LOG.log(Level.WARNING, "EXCEPTION OCCOURED IN convertFormat2Time METHOD" + e.getMessage(), e);
		}
		return returnStr;
	}

	public static String convertFormat2ddMMyyyy(String dateStr) {// convert
																	// m/d/yy to
																	// dd/MM/yyyy
																	// format
		DateFormat format = new SimpleDateFormat("MM/dd/yy");
		String returnStr = null;

		try {
			format.setLenient(false);

			Date date = format.parse(dateStr);
			DateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
			returnStr = format1.format(date);

		} catch (Exception e) {
			LOG.log(Level.WARNING, "EXCEPTION OCCOURED IN convertFormat2ddMMyyyy METHOD" + e.getMessage(), e);
		}
		return returnStr;
	}

	// Get 00:00:00 on the day time
	public static Date getTimesmorning(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	// Get 23:59:59 on the day time
	public static Date getTimesnight(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MINUTE, 59);
		return cal.getTime();
	}

	public static boolean isValidQuantityBreak(String value) {
		boolean returnBool = false;
		try {
			int num = Integer.valueOf(value);
			if (num > 0) {
				returnBool = true;
			}
		} catch (Exception e) {
			returnBool = false;
			LOG.log(Level.WARNING, "Error occures in isValidQuantityBreak for value:: " + value + " " + e.getMessage());
		}
		return returnBool;
	}

	public static boolean isValidQuantityBreakPricer(String value) {
		boolean returnBool = false;
		try {
			Double num = Double.valueOf(value);
			if (num > 0) {
				returnBool = true;
			}
		} catch (Exception e) {
			returnBool = false;
			LOG.log(Level.SEVERE,
					"Error occures in isValidQuantityBreakPricer for value:: " + value + " " + e.getMessage());
		}
		return returnBool;
	}

	public static boolean isValidQuantityCostOrResale(String value) {
		boolean returnBool = false;
		try {
			BigDecimal num = new BigDecimal(value);
			if (num.doubleValue() > 0) {
				returnBool = true;
			}
		} catch (Exception e) {
			returnBool = false;
			LOG.log(Level.SEVERE,
					"Error occures in isValidQuantityCostOrResale for value:: " + value + " " + e.getMessage());
		}
		return returnBool;
	}

	public static boolean isInteger(String value) {
		if (value != null && value.matches("[0-9]{1}[0-9]{0,9}"))
			return true;
		return false;
	}

	public static boolean isDecimal(String value) {
		if (value != null && value.matches("^\\d{1,9}(\\.\\d{0,6})?$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isIntegerIncluleNegative(String value) {
		try {
			int num = Integer.valueOf(value);
			return true;
		} catch (Exception e) {
			LOG.log(Level.WARNING,
					"Error occures in isIntegerIncluleNegative for value:: " + value + " " + e.getMessage());
			return false;
		}
	}

	public static Integer convertToInteger(String value) {
		if (value != null) {
			if (value.contains(".")) {
				value = value.substring(0, value.indexOf("."));
			}
			try {
				return Integer.parseInt(value);
			} catch (Exception ex) {
				LOG.log(Level.WARNING,
						"Error occures in convertToInteger for value:: " + value + " " + ex.getMessage());
				return null;
			}
		}
		return null;
	}

	public static boolean isDate(String str) {
		if(str ==null || str.trim().length()<1) {
			return false;
		}
		if (str.length() > 10) {
			return false;
		}
		String regex = "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/(\\d\\d\\d\\d)";
		if (!str.matches(regex)) {
			return false;
		}
		Boolean b = false;
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		try {
			format.setLenient(false);
			format.parse(str);
			b = true;
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Error occures in isDate :: " + str + " " + e.getMessage());
			b = false;
		}

		return b;
	}

	public static File createPricerUploadTempFile(InputStream inputStream) {
		File tmp = null;
		BufferedWriter writer = null;
		try {
			String tmpfolder = System.getProperty("java.io.tmpdir");

			tmp = File.createTempFile("pricer", ".xlsx", new File(tmpfolder));
			FileOutputStream outSTr = new FileOutputStream(tmp);
			BufferedOutputStream buff = new BufferedOutputStream(outSTr);

			int data;
			while ((data = inputStream.read()) != -1) {
				buff.write(data);
			}
			buff.close();

		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Error occured in method createPricerUploadTempFile " + e.getMessage(), e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					LOG.log(Level.SEVERE, "Error occured in closing BufferedWriter " + e.getMessage(), e);
				}
			}
		}
		return tmp;
	}

	public static Date convertToDate(String source) {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		format.setLenient(false);
		try {
			return format.parse(source);
		} catch (ParseException e) {
			LOG.log(Level.WARNING, "Error occureed in parsing the date :: " + e.getMessage(), e);
		}
		return null;
	}

	public static Date getEffectiveFrom(String quotationEffectiveDate) {

		Date effectiveForm = null;
		if (QuoteUtil.isEmpty(quotationEffectiveDate)) {
			Date date = new Date();
			String value = QuoteUtil.convertDateToStr(date);
			effectiveForm = convertToDate(value);
		} else {
			effectiveForm = convertToDate(quotationEffectiveDate);
		}
		return effectiveForm;
	}

	public static Date getEffectiveTo(Date effectiveForm, String validity) {
		Date effectiveTo = null;
		if (isInteger(validity)) {
			effectiveTo = getDateAfter(effectiveForm, convertToInteger(validity) - 1); // date
		} else if (isDate(validity)) {
			effectiveTo = convertToDate(validity);
		}
		return effectiveTo;
	}

	/**
	 * get Time for a few days ago
	 * 
	 * @param d
	 * @param day
	 * @return
	 */
	public static Date getDateBefore(Date d, int day) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
		return now.getTime();
	}

	/**
	 * get time After a few days
	 * 
	 * @param d
	 * @param day
	 * @return
	 */
	public static Date getDateAfter(Date d, int day) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
		return now.getTime();
	}

	public static Date timestampToDate(Timestamp tt) {
		if (tt == null)
			return null;
		return new Date(tt.getTime());
	}

	/**
	 * Compare two period whether there is overlap
	 * 
	 * @param dt1
	 * @param dt2
	 * @param list
	 * @return
	 */
	public static Boolean comparisonTimeByList(Date dt1, Date dt2, List<?> list) {
		boolean isTimeOverlap = false;
		Date dt3 = null;
		Date dt4 = null;
		NormalPricer materialDetail = null;
		ContractPricer contractPrice = null;
		VerifyEffectiveDateResult verifyEffectiveDateResult = null;
		for (int i = 0; i < list.size(); i++) {
			Object obj = list.get(i);

			if (obj instanceof NormalPricer) {
				materialDetail = (NormalPricer) obj;
				// dt3 = materialDetail.getQuotationEffectiveDate();
				// dt4 = materialDetail.getQuotationEffectiveTo();
				/**
				 * compare it with all date ranges from DB whose [Display QED]
				 * is not null to check whether there is any overlapped
				 */
				if (materialDetail.getDisplayQuoteEffDate() != null) {
					dt3 = materialDetail.getDisplayQuoteEffDate();
					dt4 = materialDetail.getQuotationEffectiveTo();
					isTimeOverlap = comparisonTime(dt1, dt2, dt3, dt4);
				}
			} else if (obj instanceof ContractPricer) {
				contractPrice = (ContractPricer) obj;
				// dt3 = contractPrice.getDisplayQuoteEffDate();
				// dt4 = contractPrice.getQuotationEffectiveDate();
				/**
				 * compare it with all date ranges from DB whose [Display QED]
				 * is not null to check whether there is any overlapped
				 */
				if (contractPrice.getDisplayQuoteEffDate() != null) {
					dt3 = contractPrice.getDisplayQuoteEffDate();
					dt4 = contractPrice.getQuotationEffectiveTo();
					isTimeOverlap = comparisonTime(dt1, dt2, dt3, dt4);
				}
			} else if (obj instanceof VerifyEffectiveDateResult) {
				verifyEffectiveDateResult = (VerifyEffectiveDateResult) obj;
				// dt3 = verifyEffectiveDateResult.getDisplayQuoteEffDate();
				// dt4 = verifyEffectiveDateResult.getQuotationEffectiveDate();
				/**
				 * compare it with all date ranges from DB whose [Display QED]
				 * is not null to check whether there is any overlapped
				 */
				if (verifyEffectiveDateResult.getDisplayQuoteEffDate() != null) {
					dt3 = verifyEffectiveDateResult.getDisplayQuoteEffDate();
					dt4 = verifyEffectiveDateResult.getQuotationEffectiveTo();
					isTimeOverlap = comparisonTime(dt1, dt2, dt3, dt4);
				}
			}

		}

		return isTimeOverlap;
	}

	/**
	 * Compare two period whether there is overlap
	 * 
	 * @param dt1
	 * @param dt2
	 * @param list
	 * @return
	 */
	public static Boolean comparisonTime_old(Date dt1, Date dt2, Date dt3, Date dt4) {
		boolean isTimeOverlap = false;
		if (dt1 != null && dt2 != null && dt3 != null && dt4 != null) {
			if (dt1.getTime() <= dt3.getTime() && dt3.getTime() <= dt2.getTime()) {
				return true;// overlap
			}
			if (dt1.getTime() <= dt3.getTime() && dt4.getTime() <= dt2.getTime()) {
				return true;// overlap
			}
			if (dt1.getTime() <= dt4.getTime() && dt3.getTime() <= dt2.getTime()) {
				return true;// overlap
			}
		}
		return isTimeOverlap;
	}

	public static Boolean comparisonTime(Date dt1, Date dt2, Date dt3, Date dt4) {
		boolean isTimeOverlap = false;
		if (dt1 != null && dt2 != null && dt3 != null && dt4 != null) {
			if (dt1.getTime() <= dt4.getTime() && dt2.getTime() >= dt3.getTime()) {
				return true;// overlap
			}
		}
		return isTimeOverlap;
	}

	/**
	 * setQedAllInMaterialDetail(quotationEffectiveDate,quotationEffectiveTo,displayQuoteEffDate)
	 * Quotation Effective To provides quick access for retrieving the cost
	 * effective period when validate whether cost is overlapped when upload.
	 * Value = Validity when Validity is date or ([Quotation Effective Date] +
	 * Validity) when Validity is number
	 * 
	 * @param pricr
	 * @param mDtl
	 * @throws ParseException
	 */
	public static void setQedAllInMaterialDetail(PricerUploadTemplateBean pricr, Pricer mDtl) throws ParseException {
		if (!QuoteUtil.isEmpty(pricr.getQuotationEffectiveDate())) {
			Date date = convertToDate(pricr.getQuotationEffectiveDate());

			mDtl.setQuotationEffectiveDate(date);
			mDtl.setDisplayQuoteEffDate(date);
			Date effectiveTo = getEffectiveTo(date, pricr.getValidity());
			mDtl.setQuotationEffectiveTo(effectiveTo);
		} else {
			Date date = PricerUtils.getTimesmorning(new Date());
			mDtl.setQuotationEffectiveDate(date);
			mDtl.setDisplayQuoteEffDate(null);
			Date effectiveTo = getEffectiveTo(date, pricr.getValidity());
			mDtl.setQuotationEffectiveTo(effectiveTo);
		}
	}

	public static void setQedAllInContractPrice(PricerUploadTemplateBean pricr, ContractPricer contractPrice) {
		if (!QuoteUtil.isEmpty(pricr.getQuotationEffectiveDate())) {
			Date date = convertToDate(pricr.getQuotationEffectiveDate());

			contractPrice.setQuotationEffectiveDate(date);
			contractPrice.setDisplayQuoteEffDate(date);
			Date effectiveTo = getEffectiveTo(date, pricr.getValidity());
			contractPrice.setQuotationEffectiveTo(effectiveTo);
		} else {
			Date date = PricerUtils.getTimesmorning(new Date());
			contractPrice.setQuotationEffectiveDate(date);
			contractPrice.setDisplayQuoteEffDate(null);
			Date effectiveTo = getEffectiveTo(date, pricr.getValidity());
			contractPrice.setQuotationEffectiveTo(effectiveTo);
		}
	}

	/**
	 * The same [MFR] + [Full MFR Part Number] + [Region] + [COST INDICATOR]
	 * data in a List < MaterialDetail >, then these collections on the List <
	 * List < MaterialDetail >
	 *
	 */
	public static HashMap<String, List<VerifyEffectiveDateResult>> getSortVerifyEffectiveDateResult(
			List<VerifyEffectiveDateResult> mdtList) {
		HashMap<String, List<VerifyEffectiveDateResult>> hashMap = new HashMap<String, List<VerifyEffectiveDateResult>>();

		if (mdtList == null || mdtList.size() == 0)
			return hashMap;

		Collections.sort(mdtList, new VerifyEffectiveDateResultComparator());

		HashMap<String, Integer> ilist = getSortIndexForVedResult(mdtList);
		List<Integer> intList = new ArrayList<Integer>();
		Iterator<String> it = ilist.keySet().iterator();
		while (it.hasNext()) {
			intList.add(ilist.get(it.next()));
		}

		Collections.sort(intList);
		// Integer[] indexList = (Integer[]) ilist.values().toArray();

		List<VerifyEffectiveDateResult> tempMdtList = null;
		String value1 = null;
		if (intList.size() <= 1) {
			value1 = getUkFromVerifyEffectiveDateResult(mdtList.get(0));
			hashMap.put(value1, mdtList);
		} else {
			int toIndex = 0;
			int isize = intList.size();
			for (int i = 0; i < isize; i++) {
				if (i + 1 < isize) {
					toIndex = intList.get(i + 1);
				} else {
					toIndex = mdtList.size();
				}
				tempMdtList = mdtList.subList(intList.get(i), toIndex);
				value1 = getUkFromVerifyEffectiveDateResult(tempMdtList.get(0));
				hashMap.put(value1, tempMdtList);
			}
		}
		return hashMap;
	}

	public static HashMap<String, Integer> getSortIndexForVedResult(List<VerifyEffectiveDateResult> mdtList) {
		Collections.sort(mdtList, new VerifyEffectiveDateResultComparator());

		String value1 = "";
		String value2 = "";
		HashMap<String, Integer> ilist = new HashMap<String, Integer>();
		VerifyEffectiveDateResult mdt = null;

		for (int i = 0; i < mdtList.size(); i++) {

			mdt = mdtList.get(i);
			value1 = getUkFromVerifyEffectiveDateResult(mdt);
			if (!value1.equalsIgnoreCase(value2)) {
				ilist.put(value1, i);
			}

			value2 = value1;

		}
		return ilist;
	}

	public static String getUkFromVerifyEffectiveDateResult(VerifyEffectiveDateResult mdt) {
		return mdt.getMfr() + " " + mdt.getFullMfrPartNumber() + " " + mdt.getCurrency() + " " + mdt.getBizUnit() + " " + mdt.getCostIndicator();
	}

	// replace getUkFromPricerUploadTemplateBean when normal or program type
	// pricer get key; couple with getUkFromVerifyEffectiveDateResult when
	// remove validation
	public static String getUkFromPricerUploadTemplateBeanForNPRm(PricerUploadTemplateBean mdt, User user) {
		return mdt.getMfr() + " " + mdt.getFullMfrPart() + " " + mdt.getCurrency().toString() 
				+ " " + user.getDefaultBizUnit().getName() + " "
				+ mdt.getCostIndicator();
	}
	

	/*public static String getUkFromMaterialDetial(NormalPricer mdt) {
		return mdt.getMaterial().getManufacturer().getName() + " " + mdt.getMaterial().getFullMfrPartNumber() + " "
				+ mdt.getBizUnitBean().getName() + " " + mdt.getCostIndicator().getName();
	}*/

	public static String getUkFromContractPrice(ContractPricer mdt) {
		// updated by damon@2017013
		String key =  mdt.getMaterial().getManufacturer().getName() + " " + mdt.getMaterial().getFullMfrPartNumber() + " "
				+ mdt.getCurrency().toString() + " "
				+ mdt.getBizUnitBean().getName() + " " + mdt.getCostIndicator().getName(); 
		if (null != mdt.getSoldtoCustomer() && !QuoteUtil.isEmpty(mdt.getSoldtoCustomer().getCustomerNumber())) {
			key = key + " " + mdt.getSoldtoCustomer().getCustomerNumber();
		}  
		return key;
	}

	// replace getUkFromPricerUploadTemplateBean when normal or program type
	// pricer get key ;couple with getUkFromContractPrice when remove validation
	public static String getUkFromPricerUploadTemplateBeanForCRm(PricerUploadTemplateBean mdt, User user) {
		String key = mdt.getMfr() + " " + mdt.getFullMfrPart() + " "
				+ mdt.getCurrency() + " "
				+ user.getDefaultBizUnit().getName() + " "
				+ mdt.getCostIndicator();
		if (!QuoteUtil.isEmpty(mdt.getSoldToCode())) {
			key = key + " " + mdt.getSoldToCode();
		}
		return  key;

	}
	

	public static String getUkFromPricerUploadTemplateBean(PricerUploadTemplateBean mdt, User user) { // fix
																										// ticket
																										// INC0521555
		if (!QuoteUtil.isEmpty(mdt.getSoldToCode())) {
			return mdt.getMfr() + " " + mdt.getFullMfrPart() + " " +  mdt.getCurrency() + " " +
					user.getDefaultBizUnit().getName() + " "
					+ mdt.getCostIndicator() + " " + mdt.getSoldToCode();
		} else {
			return mdt.getMfr() + " " + mdt.getFullMfrPart() + " " +  mdt.getCurrency() + " " +
					user.getDefaultBizUnit().getName() + " "
					+ mdt.getCostIndicator() + " "
					+ (QuoteUtil.isEmpty(mdt.getProgram()) ? "" : mdt.getProgram().toUpperCase());
			// use "" replace "null" for generating map key like as
			// getUkFromEffeciveResult
			// which fields come from DB with null
			// by David 2017-08-28
		}

	}
/*
	public static String getUkFromEffeciveResult(String pricerType, VerifyEffectiveDateResult mdt, User user) { // fix
																												// ticket
																												// INC0521555

		if (!QuoteUtil.isEmpty(mdt.getSoldToCode())) {
			return pricerType.toUpperCase() + " " + mdt.getMfr() + " " + mdt.getFullMfrPartNumber() + " "
					+ user.getDefaultBizUnit().getName() + " " + mdt.getCostIndicator() + " " + mdt.getSoldToCode();
		} else {
			return pricerType.toUpperCase() + " " + mdt.getMfr() + " " + mdt.getFullMfrPartNumber() + " "
					+ user.getDefaultBizUnit().getName() + " " + mdt.getCostIndicator() + " "
					+ (QuoteUtil.isEmpty(mdt.getPricerType()) ? "" : mdt.getPricerType().toUpperCase());
		}

	}*/
	public static String getUkFromEffeciveResult(String pricerType,VerifyEffectiveDateResult mdt,User user){ // fix ticket INC0521555 

		if(! QuoteUtil.isEmpty(mdt.getSoldToCode())){
			return pricerType.toUpperCase() + " " + mdt.getMfr()+" "+ mdt.getFullMfrPartNumber()+" "+ mdt.getCurrency() + " " +
					user.getDefaultBizUnit().getName()+" "+ mdt.getCostIndicator() + " " + mdt.getSoldToCode();
		}else {
			return pricerType.toUpperCase() + " " + mdt.getMfr()+" "+ mdt.getFullMfrPartNumber()+" "+ mdt.getCurrency() + " " +
					user.getDefaultBizUnit().getName()+" "+ mdt.getCostIndicator() + " " +
					(QuoteUtil.isEmpty(mdt.getProgramType()) ? "" : mdt.getProgramType().toUpperCase());
			//use "" replace "null" for generating map key like as getUkFromEffeciveResult 
			//which fields come from DB with null 
			//by David 2017-08-28
		}
		
	}

	public static ContractPricer convert(ContractPricer contractPrice, PricerUploadTemplateBean bean, User user,
			Date date) {

		contractPrice.setBizUnitBean(user.getDefaultBizUnit());
		contractPrice.setMaterial(bean.getMaterial());

		setQedAllInContractPrice(bean, contractPrice);
		if (!QuoteUtil.isEmpty(bean.getMinSell())) {
			contractPrice.setMinSellPrice(Double.valueOf(bean.getMinSell()));
		}
		//contractPrice.setMinSellPriceM(new Money(bean.getCurrency(), bean.getMinSell()));
		contractPrice.setSoldtoCustomer(bean.getSoldToCustomer());
		contractPrice.setEndCustomer(bean.getEndCustomer());
		contractPrice.setEndCustomerName(bean.getEndCustomerName());

		contractPrice.setAvnetQcComments(bean.getAvnetQuoteCentreComments());
		
		contractPrice.setCost(Double.parseDouble(bean.getCost()));
		//contractPrice.setCostM(new Money(bean.getCurrency(), bean.getCost()));
		contractPrice.setCurrency(Currency.valueOf(bean.getCurrency()));
		contractPrice.setCostIndicator(bean.getOcostIndicator());

		if (!QuoteUtil.isEmpty(bean.getShipmentValidity())) {
			contractPrice.setShipmentValidity(convertToDate(bean.getShipmentValidity()));
		}
		if (!QuoteUtil.isEmpty(bean.getStartDate())) {
			contractPrice.setStartDate(convertToDate(bean.getStartDate()));
		}
		contractPrice.setPriceValidity(bean.getValidity());
		contractPrice.setVendorQuoteNumber(bean.getVendorQuoteNo());

		if (!QuoteUtil.isEmpty(bean.getVendorQuoteQty())) {
			contractPrice.setVendorQuoteQuantity(Integer.parseInt(bean.getVendorQuoteQty()));
		}

		if (bean.getOverriddenStr() != null && bean.getOverriddenStr().equalsIgnoreCase("YES")) {
			contractPrice.setOverrideFlag(true);
		} else {
			contractPrice.setOverrideFlag(false);
		}
		contractPrice.setLastUpdatedBy(user.getEmployeeId());
		contractPrice.setLastUpdatedOn(date);
		contractPrice.setLeadTime(bean.getLeadTime());
		// Fix enhancment 118 to set mpq, moq and term and conditions
		if (!QuoteUtil.isEmpty(bean.getMPQ())) {
			contractPrice.setMpq(Integer.parseInt(bean.getMPQ()));
		} else {
			contractPrice.setMpq(null);
		}
		
		if (!QuoteUtil.isEmpty(bean.getMOQ())) {
			contractPrice.setMoq(Integer.parseInt(bean.getMOQ()));
		} else {
			contractPrice.setMoq(null);
		}
		contractPrice.setLeadTime(bean.getLeadTime());
		if (!QuoteUtil.isEmpty(bean.getMOV())) {
			contractPrice.setMov(Integer.parseInt(bean.getMOV()));
		}
		contractPrice.setTermsAndConditions(bean.getTermsAndConditions());

		return contractPrice;
	}

	public static SalesCostPricer convert(SalesCostPricer pricer, PricerUploadTemplateBean bean, User user,
			PricerUploadParametersBean puBean) throws ParseException {

		pricer.setBizUnitBean(new BizUnit(bean.getRegion()));
		pricer.setMaterial(bean.getMaterial());

		// salesCostPricer.setSalesCostType(SalesCostType.GetSCTypeByStr(bean.getSalesCostType()));
		pricer.setCost(Double.parseDouble(bean.getCost()));
		//pricer.setCostM(new Money(bean.getCurrency(), bean.getCost()));
		pricer.setCurrency(Currency.valueOf(bean.getCurrency()));
		pricer.setSalesCostType(SalesCostType.GetSCTypeByStr(bean.getSalesCostType()));
		if (bean.getShipmentValidity() != null && !bean.getShipmentValidity().equals("")) {
			Date date2 = QuoteUtil.convertStringToDate(bean.getShipmentValidity(), "dd/MM/yyyy");
			pricer.setShipmentValidity(date2);
		}
		pricer.setMaterial(bean.getMaterial());
		// setQedAllInsalesCostPricer(bean, salesCostPricer);

		pricer.setAvnetQcComments(bean.getAvnetQuoteCentreComments());

		pricer.setCostIndicator(bean.getOcostIndicator());

		if (!QuoteUtil.isEmpty(bean.getShipmentValidity())) {
			pricer.setShipmentValidity(convertToDate(bean.getShipmentValidity()));
		}
		// Fix enhancment 118 to set mpq, moq and term and conditions
		if (!QuoteUtil.isEmpty(bean.getMPQ())) {
			pricer.setMpq(Integer.parseInt(bean.getMPQ()));
		} else {
			pricer.setMpq(null);
		}

/*		if (!QuoteUtil.isEmpty(bean.getMOQ())) {
			pricer.setMoq(Integer.parseInt(bean.getMOQ()));
		} else {
			pricer.setMoq(null);
		}*/
		//FOR THE SAME LOGIC AS PROGRAM
		if ((bean.getMOQ() != null) && (!bean.getMOQ().equals(""))) {
			pricer.setMoq(Integer.parseInt(bean.getMOQ()));
		}else if ((bean.getMOV() != null) && (!bean.getMOV().equals(""))
				&& (bean.getCost() != null)
				&& (!bean.getCost().equals(""))
				&& (bean.getMPQ() != null) && (!bean.getMPQ().equals("")))
		{

			pricer.setMoq(PricerUtils.moqCalculate(Double.parseDouble(bean.getMOV()),
					Double.parseDouble(bean.getCost()),
					Integer.parseInt(bean.getMPQ())));
		}

		if (!QuoteUtil.isEmpty(bean.getMOV())) {
			pricer.setMov(Integer.parseInt(bean.getMOV()));
		} else {
			pricer.setMov(null);
		}

		pricer.setLeadTime(bean.getLeadTime());

		if (!QuoteUtil.isEmpty(bean.getQuotationEffectiveDate())) {
			Date date = convertToDate(bean.getQuotationEffectiveDate());

			pricer.setQuotationEffectiveDate(date);
			pricer.setDisplayQuoteEffDate(date);
			Date effectiveTo = getEffectiveTo(date, bean.getValidity());
			pricer.setQuotationEffectiveTo(effectiveTo);
		} else {
			Date date = PricerUtils.getTimesmorning(new Date());
			pricer.setQuotationEffectiveDate(date);
			pricer.setDisplayQuoteEffDate(null);
			Date effectiveTo = getEffectiveTo(date, bean.getValidity());
			pricer.setQuotationEffectiveTo(effectiveTo);
		}
		pricer.setTermsAndConditions(bean.getTermsAndConditions());

		pricer.setAvnetQcComments(bean.getAvnetQuoteCentreComments());
		Map<Object, Object> pgmTypeMap = puBean.getPgmTypeMap();
		if (bean.getProgram() != null) {
			pricer.setProgramType((ProgramType) pgmTypeMap.get(bean.getProgram().toUpperCase()));
		}
		if (!QuoteUtil.isEmpty(bean.getDisplayOnTop())) {
			pricer.setDisplayOnTop(Integer.parseInt(bean.getDisplayOnTop()));
		}
		if (!QuoteUtil.isEmpty(bean.getSpecialItemIndicator())) {
			pricer.setSpecialItemFlag(Integer.parseInt(bean.getSpecialItemIndicator()));
		}

		if (!QuoteUtil.isEmpty(bean.getAvailabletoSellQuantity())) {
			pricer.setAvailableToSellQty(Integer.parseInt(bean.getAvailabletoSellQuantity()));
		}
		if (!QuoteUtil.isEmpty(bean.getQtyControl())) {
			pricer.setQtyControl(Integer.parseInt(bean.getQtyControl()));
		}

		pricer.setQtyIndicator(bean.getQtyIndicator());
		String nidr = bean.getNewItemIndicator();
		if (!QuoteUtil.isEmpty(nidr)) {
			nidr = nidr.trim();
			if (nidr.equalsIgnoreCase("Yes")) {
				pricer.setNewItemIndicator(true);
			} else {
				pricer.setNewItemIndicator(false);
			}
		} else {
			pricer.setNewItemIndicator(false);
		}
		String aFlag = bean.getAllocationFlag();
		if (!QuoteUtil.isEmpty(aFlag)) {
			aFlag = aFlag.trim();
			if (aFlag.equalsIgnoreCase("Yes")) {
				pricer.setAllocationFlag(true);
			} else {
				pricer.setAllocationFlag(false);
			}
		}

		String aq = bean.getAQFlag();
		if (!QuoteUtil.isEmpty(aq)) {
			aq = aq.trim();
			if (aq.equalsIgnoreCase("Yes")) {
				pricer.setAqFlag(true);
			} else {
				pricer.setAqFlag(false);
			}
		}

		pricer.setPriceValidity(bean.getValidity());
		
		List<QuantityBreakPrice> qpLst = new ArrayList<QuantityBreakPrice>();
		// need to confirm break and at least one cost valid.
		if (!QuoteUtil.isEmpty(bean.getQuantityBreak1()) && 
				!QuoteUtil.isEmpty(bean.getQuantityBreakSalesCost1())) {
			QuantityBreakPrice qp = new QuantityBreakPrice();
			qp.setQuantityBreak(Integer.parseInt(bean.getQuantityBreak1()));
			//qp.setSalesCostM(new Money(bean.getCurrency(), bean.getQuantityBreakSalesCost1()));
			if (!QuoteUtil.isEmpty(bean.getQuantityBreakSalesCost1())) {
				qp.setSalesCost(new BigDecimal(bean.getQuantityBreakSalesCost1()));
			} else {
				qp.setSalesCost(null);
			}
			if (!QuoteUtil.isEmpty(bean.getQuantityBreakSuggestedResale1())) {
				qp.setSuggestedResale(new BigDecimal(bean.getQuantityBreakSuggestedResale1()));
			} else {
				qp.setSuggestedResale(null);
			}
			qpLst.add(qp);

		}
		if (!QuoteUtil.isEmpty(bean.getQuantityBreak2()) && (!QuoteUtil.isEmpty(bean.getQuantityBreakSalesCost2())
				|| !QuoteUtil.isEmpty(bean.getQuantityBreakSuggestedResale2()))) {
			QuantityBreakPrice qp = new QuantityBreakPrice();
			qp.setQuantityBreak(Integer.parseInt(bean.getQuantityBreak2()));
			if (!QuoteUtil.isEmpty(bean.getQuantityBreakSalesCost2())) {
				qp.setSalesCost(new BigDecimal(bean.getQuantityBreakSalesCost2()));
			} else {
				qp.setSalesCost(null);
			}
			//qp.setSalesCostM(new Money(bean.getCurrency(), bean.getQuantityBreakSalesCost2()));
			if (!QuoteUtil.isEmpty(bean.getQuantityBreakSuggestedResale2())) {
				qp.setSuggestedResale(new BigDecimal(bean.getQuantityBreakSuggestedResale2()));
			} else {
				qp.setSuggestedResale(null);
			}
			qpLst.add(qp);

		}

		if (!QuoteUtil.isEmpty(bean.getQuantityBreak3()) && (!QuoteUtil.isEmpty(bean.getQuantityBreakSalesCost3())
				|| !QuoteUtil.isEmpty(bean.getQuantityBreakSuggestedResale3()))) {
			QuantityBreakPrice qp = new QuantityBreakPrice();
			qp.setQuantityBreak(Integer.parseInt(bean.getQuantityBreak3()));
			//qp.setSalesCostM(new Money(bean.getCurrency(), bean.getQuantityBreakSalesCost3()));
			if (!QuoteUtil.isEmpty(bean.getQuantityBreakSalesCost3())) {
				qp.setSalesCost(new BigDecimal(bean.getQuantityBreakSalesCost3()));
			} else {
				qp.setSalesCost(null);
			}
			if (!QuoteUtil.isEmpty(bean.getQuantityBreakSuggestedResale3())) {
				qp.setSuggestedResale(new BigDecimal(bean.getQuantityBreakSuggestedResale3()));
			} else {
				qp.setSuggestedResale(null);
			}
			qpLst.add(qp);

		}

		if (!QuoteUtil.isEmpty(bean.getQuantityBreak4()) && (!QuoteUtil.isEmpty(bean.getQuantityBreakSalesCost4())
				|| !QuoteUtil.isEmpty(bean.getQuantityBreakSuggestedResale4()))) {
			QuantityBreakPrice qp = new QuantityBreakPrice();
			qp.setQuantityBreak(Integer.parseInt(bean.getQuantityBreak4()));
			if (!QuoteUtil.isEmpty(bean.getQuantityBreakSalesCost4())) {
				qp.setSalesCost(new BigDecimal(bean.getQuantityBreakSalesCost4()));
			} else {
				qp.setSalesCost(null);
			}
			//qp.setSalesCostM(new Money(bean.getCurrency(), bean.getQuantityBreakSalesCost4()));
			if (!QuoteUtil.isEmpty(bean.getQuantityBreakSuggestedResale4())) {
				qp.setSuggestedResale(new BigDecimal(bean.getQuantityBreakSuggestedResale4()));
			} else {
				qp.setSuggestedResale(null);
			}
			qpLst.add(qp);

		}

		if (!QuoteUtil.isEmpty(bean.getQuantityBreak5()) && (!QuoteUtil.isEmpty(bean.getQuantityBreakSalesCost5())
				|| !QuoteUtil.isEmpty(bean.getQuantityBreakSuggestedResale5()))) {
			QuantityBreakPrice qp = new QuantityBreakPrice();
			qp.setQuantityBreak(Integer.parseInt(bean.getQuantityBreak5()));
			if (!QuoteUtil.isEmpty(bean.getQuantityBreakSalesCost5())) {
				qp.setSalesCost(new BigDecimal(bean.getQuantityBreakSalesCost5()));
			} else {
				qp.setSalesCost(null);
			}
			//qp.setSalesCostM(new Money(bean.getCurrency(), bean.getQuantityBreakSalesCost4()));
			if (!QuoteUtil.isEmpty(bean.getQuantityBreakSuggestedResale5())) {
				qp.setSuggestedResale(new BigDecimal(bean.getQuantityBreakSuggestedResale5()));
			} else {
				qp.setSuggestedResale(null);
			}
			qpLst.add(qp);

		}
		
		pricer.setQuantityBreakPrices(qpLst);
		Date date = new Date();
		if(null == pricer.getCreatedBy()){
			pricer.setCreatedBy(user.getEmployeeId());
			pricer.setCreatedOn(date);
		}
		pricer.setLastUpdatedBy(user.getEmployeeId());
		pricer.setLastUpdatedOn(date);
		return pricer;
	}

	public static SimplePricer convert(SimplePricer pricer, PricerUploadTemplateBean bean, User user,
			PricerUploadParametersBean puBean) throws ParseException {
		pricer.setBizUnitBean(new BizUnit(bean.getRegion()));
		pricer.setMaterial(bean.getMaterial());
		// salesCostPricer.setSalesCostType(SalesCostType.GetSCTypeByStr(bean.getSalesCostType()));
		//pricer.setCost(Double.parseDouble(bean.getCost()));
		// pricer.setSalesCostType(bean.getSalesCostTypeEnum());
		if (bean.getShipmentValidity() != null && !bean.getShipmentValidity().equals("")) {
			Date date = QuoteUtil.convertStringToDate(bean.getShipmentValidity(), "dd/MM/yyyy");
			pricer.setShipmentValidity(date);
		}
		pricer.setMaterial(bean.getMaterial());
		// setQedAllInsalesCostPricer(bean, salesCostPricer);

		pricer.setAvnetQcComments(bean.getAvnetQuoteCentreComments());
		pricer.setCost(Double.parseDouble(bean.getCost()));
		pricer.setCostIndicator(bean.getOcostIndicator());

		if (!QuoteUtil.isEmpty(bean.getShipmentValidity())) {
			pricer.setShipmentValidity(convertToDate(bean.getShipmentValidity()));
		}
		// Fix enhancment 118 to set mpq, moq and term and conditions
		if (!QuoteUtil.isEmpty(bean.getMPQ())) {
			pricer.setMpq(Integer.parseInt(bean.getMPQ()));
		} else {
			pricer.setMpq(null);
		}

		if (!QuoteUtil.isEmpty(bean.getMOQ())) {
			pricer.setMoq(Integer.parseInt(bean.getMOQ()));
		} else {
			pricer.setMoq(null);
		}

		if (!QuoteUtil.isEmpty(bean.getMOV())) {
			pricer.setMov(Integer.parseInt(bean.getMOV()));
		} else {
			pricer.setMov(null);
		}

		pricer.setLeadTime(bean.getLeadTime());

		if (!QuoteUtil.isEmpty(bean.getQuotationEffectiveDate())) {
			Date date = convertToDate(bean.getQuotationEffectiveDate());

			pricer.setQuotationEffectiveDate(date);
			pricer.setDisplayQuoteEffDate(date);
			Date effectiveTo = getEffectiveTo(date, bean.getValidity());
			pricer.setQuotationEffectiveTo(effectiveTo);
		} else {
			Date date = PricerUtils.getTimesmorning(new Date());
			pricer.setQuotationEffectiveDate(date);
			pricer.setDisplayQuoteEffDate(null);
			Date effectiveTo = getEffectiveTo(date, bean.getValidity());
			pricer.setQuotationEffectiveTo(effectiveTo);
		}
		pricer.setTermsAndConditions(bean.getTermsAndConditions());

		pricer.setAvnetQcComments(bean.getAvnetQuoteCentreComments());
		Map<Object, Object> pgmTypeMap = puBean.getPgmTypeMap();
		if (bean.getProgram() != null) {
			pricer.setProgramType((ProgramType) pgmTypeMap.get(bean.getProgram().toUpperCase()));
		}
		if (!QuoteUtil.isEmpty(bean.getDisplayOnTop())) {
			pricer.setDisplayOnTop(Integer.parseInt(bean.getDisplayOnTop()));
		}
		if (!QuoteUtil.isEmpty(bean.getSpecialItemIndicator())) {
			pricer.setSpecialItemFlag(Integer.parseInt(bean.getSpecialItemIndicator()));
		}

		if (!QuoteUtil.isEmpty(bean.getAvailabletoSellQuantity())) {
			pricer.setAvailableToSellQty(Integer.parseInt(bean.getAvailabletoSellQuantity()));
		}
		if (!QuoteUtil.isEmpty(bean.getQtyControl())) {
			pricer.setQtyControl(Integer.parseInt(bean.getQtyControl()));
		}

		pricer.setVendorQuoteNumber(bean.getVendorQuoteNo());

		if (!QuoteUtil.isEmpty(bean.getVendorQuoteQty())) {
			pricer.setVendorQuoteQuantity(Integer.parseInt(bean.getVendorQuoteQty()));
		}

		/*
		 * pricer.setQtyIndicator(bean.getQtyIndicator()); String nidr =
		 * bean.getNewItemIndicator(); if (!QuoteUtil.isEmpty(nidr)) { nidr =
		 * nidr.trim(); if (nidr.equalsIgnoreCase("Yes")) {
		 * pricer.setNewItemIndicator(true); } else {
		 * pricer.setNewItemIndicator(false); } } else {
		 * pricer.setNewItemIndicator(null); }
		 */

		/*
		 * String aq = bean.getAQFlag(); if (!QuoteUtil.isEmpty(aq)) { aq =
		 * aq.trim(); if (aq.equalsIgnoreCase("Yes")) { pricer.setAqFlag(true);
		 * } else { pricer.setAqFlag(false); } }
		 */
		Date date = new Date();
		if(null == pricer.getCreatedBy()){
			pricer.setCreatedBy(user.getEmployeeId());
			pricer.setCreatedOn(date);
		}
		pricer.setLastUpdatedBy(user.getEmployeeId());
		pricer.setLastUpdatedOn(date);

		pricer.setPriceValidity(bean.getValidity());

		List<QuantityBreakPrice> qpLst = new ArrayList<QuantityBreakPrice>();
		// need to confirm break and at least one cost valid.
		if (!QuoteUtil.isEmpty(bean.getQuantityBreak1()) && (!QuoteUtil.isEmpty(bean.getQuantityBreakSalesCost1())
				|| !QuoteUtil.isEmpty(bean.getQuantityBreakSuggestedResale1()))) {
			QuantityBreakPrice qp = new QuantityBreakPrice();
			qp.setQuantityBreak(Integer.parseInt(bean.getQuantityBreak1()));
			if (!QuoteUtil.isEmpty(bean.getQuantityBreakSalesCost1())) {
				qp.setSalesCost(new BigDecimal(bean.getQuantityBreakSalesCost1()));
			} else {
				qp.setSalesCost(null);
			}
			if (!QuoteUtil.isEmpty(bean.getQuantityBreakSuggestedResale1())) {
				qp.setSuggestedResale(new BigDecimal(bean.getQuantityBreakSuggestedResale1()));
			} else {
				qp.setSuggestedResale(null);
			}
			qpLst.add(qp);

		}
		if (!QuoteUtil.isEmpty(bean.getQuantityBreak2()) && (!QuoteUtil.isEmpty(bean.getQuantityBreakSalesCost2())
				|| !QuoteUtil.isEmpty(bean.getQuantityBreakSuggestedResale2()))) {
			QuantityBreakPrice qp = new QuantityBreakPrice();
			qp.setQuantityBreak(Integer.parseInt(bean.getQuantityBreak2()));
			if (!QuoteUtil.isEmpty(bean.getQuantityBreakSalesCost2())) {
				qp.setSalesCost(new BigDecimal(bean.getQuantityBreakSalesCost2()));
			} else {
				qp.setSalesCost(null);
			}
			if (!QuoteUtil.isEmpty(bean.getQuantityBreakSuggestedResale2())) {
				qp.setSuggestedResale(new BigDecimal(bean.getQuantityBreakSuggestedResale2()));
			} else {
				qp.setSuggestedResale(null);
			}
			qpLst.add(qp);

		}

		if (!QuoteUtil.isEmpty(bean.getQuantityBreak3()) && (!QuoteUtil.isEmpty(bean.getQuantityBreakSalesCost3())
				|| !QuoteUtil.isEmpty(bean.getQuantityBreakSuggestedResale3()))) {
			QuantityBreakPrice qp = new QuantityBreakPrice();
			qp.setQuantityBreak(Integer.parseInt(bean.getQuantityBreak3()));
			if (!QuoteUtil.isEmpty(bean.getQuantityBreakSalesCost1())) {
				qp.setSalesCost(new BigDecimal(bean.getQuantityBreakSalesCost3()));
			} else {
				qp.setSalesCost(null);
			}
			if (!QuoteUtil.isEmpty(bean.getQuantityBreakSuggestedResale3())) {
				qp.setSuggestedResale(new BigDecimal(bean.getQuantityBreakSuggestedResale3()));
			} else {
				qp.setSuggestedResale(null);
			}
			qpLst.add(qp);

		}

		if (!QuoteUtil.isEmpty(bean.getQuantityBreak4()) && (!QuoteUtil.isEmpty(bean.getQuantityBreakSalesCost4())
				|| !QuoteUtil.isEmpty(bean.getQuantityBreakSuggestedResale4()))) {
			QuantityBreakPrice qp = new QuantityBreakPrice();
			qp.setQuantityBreak(Integer.parseInt(bean.getQuantityBreak4()));
			if (!QuoteUtil.isEmpty(bean.getQuantityBreakSalesCost4())) {
				qp.setSalesCost(new BigDecimal(bean.getQuantityBreakSalesCost4()));
			} else {
				qp.setSalesCost(null);
			}
			if (!QuoteUtil.isEmpty(bean.getQuantityBreakSuggestedResale4())) {
				qp.setSuggestedResale(new BigDecimal(bean.getQuantityBreakSuggestedResale4()));
			} else {
				qp.setSuggestedResale(null);
			}
			qpLst.add(qp);

		}

		if (!QuoteUtil.isEmpty(bean.getQuantityBreak5()) && (!QuoteUtil.isEmpty(bean.getQuantityBreakSalesCost5())
				|| !QuoteUtil.isEmpty(bean.getQuantityBreakSuggestedResale5()))) {
			QuantityBreakPrice qp = new QuantityBreakPrice();
			qp.setQuantityBreak(Integer.parseInt(bean.getQuantityBreak5()));
			if (!QuoteUtil.isEmpty(bean.getQuantityBreakSalesCost5())) {
				qp.setSalesCost(new BigDecimal(bean.getQuantityBreakSalesCost5()));
			} else {
				qp.setSalesCost(null);
			}
			if (!QuoteUtil.isEmpty(bean.getQuantityBreakSuggestedResale5())) {
				qp.setSuggestedResale(new BigDecimal(bean.getQuantityBreakSuggestedResale5()));
			} else {
				qp.setSuggestedResale(null);
			}
			qpLst.add(qp);

		}
		pricer.setQuantityBreakPrices(qpLst);
		return pricer;

	}

	/**
	 * Determine whether two dates are the same ,Only judge (day) (month) (year)
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameDate(Date date1, Date date2) {
		String value1 = QuoteUtil.convertDateToStr(date1);
		String value2 = QuoteUtil.convertDateToStr(date2);
		if (value1 != null && value1.equals(value2)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * getMfrInUploadFile
	 * 
	 * @param beans
	 * @param mfrListCache
	 * @return
	 */
	public static List<Manufacturer> getMfrInUploadFile(List<PricerUploadTemplateBean> beans,
			List<Manufacturer> mfrListCache) {
		ArrayList<Manufacturer> manufacturerLst = new ArrayList<Manufacturer>();
		for (PricerUploadTemplateBean bean : beans) {

			for (Manufacturer mfr : mfrListCache) {
				if (mfr.getName().equalsIgnoreCase(bean.getMfr())) {
					bean.setMfrInDb(true);
					if (!manufacturerLst.contains(mfr)) {
						manufacturerLst.add(mfr);
					}
					break;
				}
			}
		}
		return manufacturerLst;
	}

	public static void main(String[] args) {
		System.out.println(PricerUtils.isInteger("16775676"));
		/*
		 * Date effectiveForm = getEffectiveFrom("1/02/2014"); Date effectiveTo
		 * = getEffectiveTo(effectiveForm, "28");
		 * System.out.println(effectiveForm); System.out.println(effectiveTo);
		 */
	}

	/**
	 * The same [MFR] + [Full MFR Part Number] data in a List <
	 * PricerUploadTemplateBean >, then these collections on the List < List <
	 * PricerUploadTemplateBean >
	 *
	 */
	public static HashMap<String, List<PricerUploadTemplateBean>> getSortPricerUploadTemplateBean(
			List<PricerUploadTemplateBean> mdtList) {
		HashMap<String, List<PricerUploadTemplateBean>> hashMap = new HashMap<String, List<PricerUploadTemplateBean>>();

		if (mdtList == null || mdtList.size() == 0)
			return hashMap;

		Collections.sort(mdtList, new PricerUploadTemplateBeanComparator());

		HashMap<String, Integer> ilist = getSortIndexForPricerUploadTemplateBeant(mdtList);
		List<Integer> intList = new ArrayList<Integer>();
		Iterator<String> it = ilist.keySet().iterator();
		while (it.hasNext()) {
			intList.add(ilist.get(it.next()));
		}

		Collections.sort(intList);
		// Integer[] indexList = (Integer[]) ilist.values().toArray();

		List<PricerUploadTemplateBean> tempMdtList = null;
		String value1 = null;
		if (intList.size() <= 1) {
			value1 = mdtList.get(0).getMfr() + " " + mdtList.get(0).getFullMfrPart();
			hashMap.put(value1, mdtList);
		} else {
			int toIndex = 0;
			int isize = intList.size();
			for (int i = 0; i < isize; i++) {
				if (i + 1 < isize) {
					toIndex = intList.get(i + 1);
				} else {
					toIndex = mdtList.size();
				}
				tempMdtList = mdtList.subList(intList.get(i), toIndex);
				value1 = tempMdtList.get(0).getMfr() + " " + tempMdtList.get(0).getFullMfrPart();
				hashMap.put(value1, tempMdtList);
			}
		}
		return hashMap;
	}

	public static HashMap<String, Integer> getSortIndexForPricerUploadTemplateBeant(
			List<PricerUploadTemplateBean> mdtList) {
		Collections.sort(mdtList, new PricerUploadTemplateBeanComparator());

		String value1 = "";
		String value2 = "";
		HashMap<String, Integer> ilist = new HashMap<String, Integer>();
		PricerUploadTemplateBean mdt = null;

		for (int i = 0; i < mdtList.size(); i++) {

			mdt = mdtList.get(i);
			value1 = mdt.getMfr() + " " + mdt.getFullMfrPart();
			if (!value1.equalsIgnoreCase(value2)) {
				ilist.put(value1, i);
			}

			value2 = value1;

		}
		return ilist;
	}

	public static String buildInSql(Set<?> set) {
		Iterator<?> it = set.iterator();
		StringBuffer sb = new StringBuffer("(");
		while (it.hasNext()) {
			sb.append("'").append(it.next()).append("',");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(")");
		return sb.toString();
	}

	public static ProgramPricer getProgramMaterial(List<ProgramPricer> mtlDtlLst, long id) {
		for (ProgramPricer bean : mtlDtlLst) {
			if (id == bean.getId()) {
				return bean;
			}
		}
		return null;
	}

	public static NormalPricer getMaterialDetail(List<NormalPricer> mtlDtlLst, long id) {
		for (NormalPricer bean : mtlDtlLst) {
			if (id == bean.getId()) {
				return bean;
			}
		}
		return null;
	}

	public static String roundedFor6(String value) {
		String returnVal = null;
		if (isExceedDecimalDigits(value)) {
			try {
				BigDecimal mData = new BigDecimal(value).setScale(6, BigDecimal.ROUND_HALF_UP);
				returnVal = mData.toString();
			} catch (NumberFormatException ex) {
				LOG.log(Level.WARNING, "Error occureed in roundedFor6 :: " + ex.getMessage(), ex);
				returnVal = value;
			}
		} else {
			returnVal = value;
		}
		return returnVal;
	}

	/**
	 * eg:12.234 return 3
	 * 
	 * @param value
	 * @return
	 */
	private static boolean isExceedDecimalDigits(String value) {
		int digit = 0;
		if (value != null) {
			int index = value.indexOf(".");
			if (index != -1) {
				digit = value.length() - index - 1;
			}
		}
		if (digit > 6) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * If allow append validate message
	 * 
	 * @param length
	 * @return
	 */
	public static boolean isAllowAppend(int length) {
		if (length < PricerConstant.STRING_MAX_LENGTH) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isNullRow(PricerUploadTemplateBean bean) {
		if (bean != null && bean.getAction() == null && bean.getPricerType() == null && bean.getMfr() == null
				&& bean.getFullMfrPart() == null && bean.getCost() == null && bean.getCostIndicator() == null) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isHaveABookCost(PricerUploadTemplateBean u1, List<PricerUploadTemplateBean> beans) {
		String value1 = u1.getMfr() + u1.getFullMfrPart();
		PricerUploadTemplateBean u2 = null;
		String value2 = null;
		for (int i = 0; i < beans.size(); i++) {
			u2 = beans.get(i);
			value2 = u2.getMfr() + u2.getFullMfrPart();
			if (value1.equals(value2) && u2.getCostIndicator() != null
					&& u2.getCostIndicator().equalsIgnoreCase(PricerConstant.A_COST_INDICATOR)) {
				return true;
			}
		}

		return false;

	}

	public static String escapeSQLIllegalCharacters(String currentStr) {
		if (currentStr != null) {
			int index = currentStr.indexOf("'");
			if (index != -1) {
				currentStr = currentStr.replaceAll("'", "''");
			}
		}
		return currentStr;
	}

	/**
	 * If the MOQ of a program item is empty, the MOQ is calculated by dividing
	 * the MOV of the program item by the minimum cost of the program item, and
	 * rounded up to the least multiple of the MPQ of the program item.
	 * 
	 * @param moq
	 * @param minCost
	 * @param mpq
	 * @return
	 */
	public static int moqCalculate(double mov, double minCost, int mpq) {
		if (minCost == 0) {
			return 0;
		}
		double mid = mov / minCost;
		if (mpq == 0) {// mpq can be 0.
			return (int) Math.ceil(mid);
		} else {
			mid = mid / mpq;
			return (int) (mpq * Math.ceil(mid));
		}
	}

	public static void setQuotationEffectiveTo(PricerUploadTemplateBean pricr, Pricer mDtl) {
		if (mDtl.getQuotationEffectiveDate() != null) {
			Date effectiveTo = getEffectiveTo(mDtl.getQuotationEffectiveDate(), pricr.getValidity());

			mDtl.setQuotationEffectiveTo(effectiveTo);
		}
	}

	/*
	 * public static void setQuotationEffectiveTo(PricerUploadTemplateBean
	 * pricr, ContractPricer mDtl) { if(mDtl.getQuotationEffectiveDate()!=null){
	 * Date effectiveTo = getEffectiveTo(mDtl.getQuotationEffectiveDate(),
	 * pricr.getValidity());
	 * 
	 * mDtl.setQuotationEffectiveTo(effectiveTo); } }
	 */

	public static boolean isValidProductCat(String productCat, List<ProductCategory> productCategorys) {
		if (!QuoteUtil.isEmpty(productCat)) {
			for (ProductCategory bean : productCategorys) {
				if (!QuoteUtil.isEmpty(bean.getCategoryCode())
						&& productCat.trim().equalsIgnoreCase(bean.getCategoryCode())) {
					return true;
				}
			}
		}
		return false;
	}

	public static String getEffectiveQryKey(PricerUploadTemplateBean bean, User user, String pricerType) {
		StringBuffer dbKey = new StringBuffer();
		dbKey.append(bean.getMfr());
		dbKey.append(bean.getFullMfrPart());
		dbKey.append(bean.getCostIndicator());
		dbKey.append(user.getDefaultBizUnit().getName());
		dbKey.append(pricerType.toUpperCase());

		if (!QuoteUtil.isEmpty(bean.getSoldToCode())) {
			dbKey.append(bean.getSoldToCode());
		}
		if (!QuoteUtil.isEmpty(bean.getEndCustomerCode())) {
			dbKey.append(bean.getEndCustomerCode());
		}
		return dbKey.toString();
	}

}
