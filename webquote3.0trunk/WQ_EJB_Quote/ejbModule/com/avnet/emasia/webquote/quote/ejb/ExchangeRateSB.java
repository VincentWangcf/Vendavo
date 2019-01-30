package com.avnet.emasia.webquote.quote.ejb;

import java.math.BigDecimal;
import java.text.DateFormat;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
//import java.util.logging.Logger;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang.StringUtils;

import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.ExcCurrencyDefault;
import com.avnet.emasia.webquote.entity.ExchangeRate;
import com.avnet.emasia.webquote.quote.vo.ExchangeRateVo;
import com.avnet.emasia.webquote.utilities.DBResourceBundle;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;

@Stateless
@LocalBean
public class ExchangeRateSB {

	@PersistenceContext(unitName = "Server_Source")
	EntityManager em;

	private static final Logger LOG = Logger.getLogger(ExchangeRateSB.class.getName());
	private final List<Integer> decimalPointList = new ArrayList<>(Arrays.asList(0,1,2,3,4,5)) ;
	/**
	 * get the exchange currency by distinct
	 * 
	 * @author 916138
	 * @return
	 */
	public List<String> findAllExCurrencyByBu(String buName) {
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<String> c = cb.createQuery(String.class);
		Root<ExchangeRate> rate = c.from(ExchangeRate.class);

		List<Predicate> criterias = new ArrayList<Predicate>();
		criterias.add(cb.equal(rate.get("bizUnit"), buName));

		// query columns
		List<Selection<?>> selectionList = new ArrayList<Selection<?>>();

		Selection<String> expCurTo = rate.get("currTo");
		selectionList.add(expCurTo);
		c.multiselect(selectionList).distinct(true);
		c.where(criterias.toArray(new Predicate[] {}));

		TypedQuery<String> q = em.createQuery(c);
		List<String> curLst = q.getResultList();
		return curLst;

	}

	/**
	 * get exchange Rate accordingly
	 * 
	 * @author 916138
	 * @param currFrom
	 * @param currTo
	 * @param mfr
	 * @param soldToCode
	 * @param buName
	 * @return
	 */
	public ExchangeRate findExchangeRate(String currFrom, String currTo, String soldToCode, String buName) {
		// add by Lenon,Yang 2016/3/15 the curr_from is null the set default
		// "USD"
		if (StringUtils.isBlank(currFrom)) {
			currFrom = "USD";
		}
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<ExchangeRate> c = cb.createQuery(ExchangeRate.class);
		Root<ExchangeRate> rate = c.from(ExchangeRate.class);

		List<Predicate> criterias = new ArrayList<Predicate>();
		criterias.add(cb.equal(rate.get("currFrom"), currFrom));
		criterias.add(cb.equal(rate.get("currTo"), currTo));
		criterias.add(cb.equal(rate.get("bizUnit"), buName));
		// if(null!=soldToCode) {
		criterias.add(cb.equal(rate.get("soldToCode"), soldToCode));
		// }else {

		// }
		// if(null!=mfr) {
		// criterias.add(cb.equal(rate.get("mfr"), mfr));
		// }

		// criterias.add(cb.equal(rate.get("deleteFlag"), false));

		c.where(criterias.toArray(new Predicate[] {}));

		TypedQuery<ExchangeRate> q = em.createQuery(c);
		List<ExchangeRate> rateLst = q.getResultList();
		if (null != rateLst && rateLst.size() > 0) {
			return rateLst.get(0);
		} else {
			return null;
		}

	}

	public String validateExchangeRate(List<ExchangeRateVo> exchangeRateVos, String bizUnit, Set<String> allowCurrencys, Locale locale) {

		ResourceBundle bundle = ResourceBundle.getBundle(DBResourceBundle.class.getName(), locale);
		StringBuffer sb = new StringBuffer();

		if (exchangeRateVos == null || exchangeRateVos.size() == 0) {
			return bundle.getString("wq.message.noRecordToUpload");
		}

		int row = 2;

		List<String> foundCustomers = new ArrayList<String>();

		for (ExchangeRateVo vo : exchangeRateVos) {

			StringBuffer sb1 = new StringBuffer();

			if (vo.getconvertAndValidationError() != null) {
				sb1.append(vo.getconvertAndValidationError()+ "; ");

			}
			if (vo.getExchangeRateTo() ==null || BigDecimal.ZERO.compareTo(vo.getExchangeRateTo())> 0){
				sb1.append(bundle.getString("wq.message.exchangeToError") + "; ");
			}
			if ( vo.getHandling() ==null || BigDecimal.ZERO.compareTo(vo.getHandling())> 0){
				sb1.append(bundle.getString("wq.message.HandlingError") + "; ");
			}
			if (vo.getVat()==null || BigDecimal.ZERO.compareTo(vo.getVat())> 0){
				sb1.append(bundle.getString("wq.message.VATError") + "; ");
			}
			if (vo.getCurrencyFrom() == null || vo.getCurrencyFrom().equals("")) {
				sb1.append(bundle.getString("wq.message.currencyFromEmpty") + "; ");
			}
			if (!"USD".equals(vo.getCurrencyFrom())) {
				sb1.append(bundle.getString("wq.message.currencyFromIsnotUSD") + "; ");
			}
			if (vo.getCurrencyTo() == null || vo.getCurrencyTo().equals("")) {
				sb1.append(bundle.getString("wq.message.currencyToEmpty") + "; ");
			}
			// The currency in Currency To column is not allowed for the
			// selected region.
			if (!allowCurrencys.contains(vo.getCurrencyTo())) {
				sb1.append(bundle.getString("wq.message.currencyToNotAllowed") + "; ");
			}
			// if(vo.getExchangeRateFrom() == null ||
			// vo.getExchangeRateFrom().equals("")){
			// sb1.append(bundle.getString("wq.message.exchangeFromEmpty")+";
			// ");
			// }else{
			// try{
			// new BigDecimal(vo.getExchangeRateFrom());
			// }catch(Exception e){
			// LOG.log(Level.WARNING, "Exception occured in validating Exchange
			// rate(Exchange Rate To incorrect) for biz unit: "+bizUnit+", "
			// + "Reason for failure:
			// "+MessageFormatorUtil.getParameterizedStringFromException(e));
			// sb1.append(bundle.getString("wq.message.exchangeFromIncorrect")+";
			// ");
			// }
			// }
			// The currency in Currency To column
			// is not allowed for the selected region.

			// if (vo.getExchangeRateTo() == null ||
			// vo.getExchangeRateTo().equals("")) {
			// sb1.append(bundle.getString("wq.message.exchangeToEmpty") + ";
			// ");
			// } else if(vo.getExchangeRateTo().contains("-")){
			// sb1.append(bundle.getString("wq.message.exchangeToIncorrect") +
			// "; ");
			// }
			// else {
			// try {
			// new BigDecimal(vo.getExchangeRateTo());
			// } catch (Exception e) {
			// LOG.log(Level.WARNING, "Exception occured in validating Exchange
			// rate(Exchange Rate From incorrect) for biz unit: " + bizUnit
			// + ", " + "Reason for failure: " +
			// MessageFormatorUtil.getParameterizedStringFromException(e));
			// sb1.append(bundle.getString("wq.message.exchangeToIncorrect") +
			// "; ");
			// }
			// }

			// if (vo.getVat() == null || vo.getVat().equals("")) {
			// sb1.append(bundle.getString("wq.message.emptyVAT") + "; ");
			// }
			// else if(vo.getVat().contains("-")) {
			// sb1.append(bundle.getString("wq.message.incorrectVAT") + "; ");
			// }
			// else {
			// try {
			// // new BigDecimal(vo.getVat());
			// } catch (Exception e) {
			// LOG.log(Level.WARNING, "Exception occured in validating Exchange
			// rate(VAT incorrect) for biz unit: " + bizUnit + ", "
			// + "Reason for failure: " +
			// MessageFormatorUtil.getParameterizedStringFromException(e));
			// sb1.append(bundle.getString("wq.message.incorrectVAT") + "; ");
			// }
			// }
			//
			// if (vo.getHandling() == null || vo.getHandling().equals("")) {
			// sb1.append(bundle.getString("wq.message.emptyHandling") + "; ");
			// } else if(vo.getHandling().contains("-")){
			// sb1.append(bundle.getString("wq.message.incorrectHandling") + ";
			// ");
			// }
			// else {
			// try {
			// new BigDecimal(vo.getHandling());
			// } catch (Exception e) {
			// LOG.log(Level.WARNING, "Exception occured in validating Exchange
			// rate(Handling incorrect) for biz unit: " + bizUnit + ", "
			// + "Reason for failure: " +
			// MessageFormatorUtil.getParameterizedStringFromException(e));
			// sb1.append(bundle.getString("wq.message.incorrectHandling") + ";
			// ");
			// }
			// }

			String soldToCode = vo.getSoldToCode();

			// Row [x]: Customer Fixed Rate must have value in Sold To Code.
				if ("Customer Fixed Rate".equals(vo.getExchangeRateType()) && soldToCode == null) {
				sb1.append(bundle.getString("wq.message.customerFixedRateMustHaveValue") + "; ");
			}
			//add 20181025
			//10	Company Rate record has value in Sold To Code	Row [x]: Company Rate cannot have value in Sold To Code Column.
				if(soldToCode != null && "Company Rate".equals(vo.getExchangeRateType())){
				sb1.append(bundle.getString("wq.message.CompanyRatehasValueInSoldToCode") + "; ");
			}
			
			if (soldToCode != null && !soldToCode.equals("")) {
				if (!foundCustomers.contains(soldToCode)) {
					TypedQuery<Long> query = em.createQuery("select count(c) from CustomerSale cs join cs.customer c join cs.salesOrgBean org "
							+ "join cs.salesOrgBean.bizUnits b where c.customerNumber = :customerNumber and b.name = :bizUnit", Long.class);
					query.setParameter("customerNumber", soldToCode);
					query.setParameter("bizUnit", bizUnit);
					long count = query.getResultList().get(0);
					if (count >= 1L) {
						foundCustomers.add(soldToCode);
					} else {
						sb1.append(
								bundle.getString("wq.message.soldtocode") + " " + soldToCode + " " + bundle.getString("wq.message.invalid") + "; ");
					}
				}
			}
			// Decimal Point cannot be blank if Rounding field is not blank.

			if (vo.getRounding() != null && vo.getDecimalPoint() == null) {
				sb1.append(bundle.getString("wq.message.roundingFieldIsnotBlankButDecimalPointIsBlank") + "; ");
			}

			if (vo.getDecimalPoint() != null) {

				if (!decimalPointList.contains(vo.getDecimalPoint())) {
//					if (vo.getDecimalPoint() > 5 || vo.getDecimalPoint() < 0) {
					sb1.append(bundle.getString("wq.message.DecimalPointMustBeNumeric") + "; ");
				}
			}
			//add 20181025
//			10	Company Rate record has value in Sold To Code	Row [x]: Company Rate cannot have value in Sold To Code Column.
//			11	Decimal Point field is not blank but Rounding is blank	Row [x]: Rounding cannot be blank if Decimal Point field is not blank.
			if(vo.getDecimalPoint() != null && vo.getRounding() == null){
				sb1.append(bundle.getString("wq.message.DecimalPointIsNotBlankRoundingIsBlank") + "; ");
			}
			if (!sb1.toString().equals("")) {
				if (!sb1.toString().startsWith("Row")) {
					sb.append(bundle.getString("wq.message.row") + " " + row + ": " + sb1.toString() + "<br/>");
				}
				// sb.append(bundle.getString("wq.message.row") + " " + row + ":
				// " + sb1.toString() + "<br/>");
				else {
					sb.append(sb1.toString() + "<br/>");
				}
			}
			row++;
		}

		List<Integer> duplicateRecords = new ArrayList<Integer>();
		for (int i = 0; i < exchangeRateVos.size(); i++) {
			if (duplicateRecords.contains(i)) {
				continue;
			}
			ExchangeRateVo vo1 = exchangeRateVos.get(i);
			StringBuffer sb1 = new StringBuffer();
			for (int j = i + 1; j < exchangeRateVos.size(); j++) {
				ExchangeRateVo vo2 = exchangeRateVos.get(j);
				if (vo1.getKey().equals(vo2.getKey())) {
					duplicateRecords.add(j);
					sb1.append(" " + (j + 1));
				}
			}
			if (!"".equals(sb1.toString())) {
				sb.append(bundle.getString("wq.message.row") + " " + (i + 1) + sb1.toString() + " "
						+ bundle.getString("wq.message.areDuplicateRecords") + " <br/>");
			}

		}

		ArrayList<String> duplicateExchangeRateTypeTocur = new ArrayList<>();
		//int findIndex = 0;
		for (ExchangeRateVo vo : exchangeRateVos) {
			if ("Company Rate".equals(vo.getExchangeRateType())) {
				duplicateExchangeRateTypeTocur.add(vo.getCurrencyTo());
				
				//findIndex++;
				
			}
		}
		
		if(duplicateExchangeRateTypeTocur.stream().distinct().count() < duplicateExchangeRateTypeTocur.size()){
			sb.append(bundle.getString("wq.message.duplicateExchangeRateTypeTocur") + "; ");
			
		}
		if (sb.length() != 0) {
			return sb.toString();
		}

		// Set<String> fromTos = new HashSet<String>();
		// for (ExchangeRateVo vo : exchangeRateVos) {
		// fromTos.add(vo.getCurrencyFrom() + "|" + vo.getCurrencyTo());
		// }

		// for (String fromTo : fromTos) {
		// boolean check = false;
		// String from = fromTo.substring(0, fromTo.indexOf("|"));
		// String to = fromTo.substring(fromTo.indexOf("|") + 1);
		// for (ExchangeRateVo vo : exchangeRateVos) {
		// if (vo.getCurrencyFrom().equals(from) &&
		// vo.getCurrencyTo().equals(to)) {
		// if (vo.getSoldToCode() == null || vo.getSoldToCode().equals("")) {
		// check = true;
		// break;
		// }
		// }
		// }
		// if (check == false) {
		// sb.append(bundle.getString("wq.message.noExchangeRateFound") + from +
		// " " + bundle.getString("wq.message.to") + " " + to);
		// }
		// }

		return sb.toString();
	}

	private static Date convertToYMDDate(String source) throws Exception {
		// DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		format.setLenient(false);
		try {
			return format.parse(source);
		} catch (Exception e) {
			// throw e;
		}
		return null;
	}

	public Date getCurrentDateAfterOneDay() {
		Calendar todayStart = Calendar.getInstance();
		todayStart.set(Calendar.HOUR_OF_DAY, 36);
		// todayStart.set(Calendar.MINUTE, 0);
		// todayStart.set(Calendar.SECOND, 0);
		// todayStart.set(Calendar.MILLISECOND, 0);
		return todayStart.getTime();
	}

	public static Date getDateAfter(Date d, int day) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
		return now.getTime();
	}

	public Date getCurrentDate() {
		Calendar todayStart = Calendar.getInstance();
		// todayStart.set(Calendar.HOUR_OF_DAY, 0);
		// todayStart.set(Calendar.MINUTE, 0);
		// todayStart.set(Calendar.SECOND, 0);
		// todayStart.set(Calendar.MILLISECOND, 0);
		return todayStart.getTime();
	}

	@SuppressWarnings("unused")
	public int[] updateEChangeRate(List<ExchangeRate> exchangeRates, String bizUnit) throws Exception {
		List<ExchangeRate> oldExchangeRates = findExchangeRateByBizUnit(bizUnit);
		int exchangeRatesSize = exchangeRates.size(), update = 0;
		List<ExchangeRate> addExchangeRates = new ArrayList<ExchangeRate>();
		List<ExchangeRate> newExchangeRates = new ArrayList<ExchangeRate>();
		// List<ExchangeRate> updateExchangeRates = new
		// ArrayList<ExchangeRate>();
		Map<String, List<ExchangeRate>> updateExchangeRateMap = new TreeMap<>();
		for (ExchangeRate exchangeRate : exchangeRates) {
			boolean found = false;
			List<ExchangeRate> updateExchangeRates = new ArrayList<ExchangeRate>();
			for (ExchangeRate oldExchangeRate : oldExchangeRates) {

				if (exchangeRate.getKey().equals(oldExchangeRate.getKey())) {
					LOG.info("keyValue" + oldExchangeRate.getKey());
					LOG.info("exchangeRate id is : "+oldExchangeRate.getId());
					found = true;
					
					updateExchangeRates.add(oldExchangeRate);
					
					// break;
				}
			}
			ExchangeRate.sortByValidFrom(updateExchangeRates);
			
			updateExchangeRateMap.put(exchangeRate.getKey(), updateExchangeRates);
		}
		// ExchangeRate.sortByValidTo(updateExchangeRates);
		for (ExchangeRate newexchangeRate : exchangeRates) {

			newexchangeRate.setValidFrom(new Date());
			newexchangeRate.setValidTo(convertToYMDDate("2099-12-31 23:59:59"));
			newExchangeRates.add(newexchangeRate);

		}
		// forbupdate the last ValidTo.
		// newExchangeRateMap.keySet()
		
			for (String key : updateExchangeRateMap.keySet()) {
				if(!updateExchangeRateMap.get(key).isEmpty()){
					LOG.info("validTo : *******"+ updateExchangeRateMap.get(key).get(0).getId());
					updateExchangeRateMap.get(key).get(0).setValidTo(new Date(System.currentTimeMillis() - 1000));
					em.merge(updateExchangeRateMap.get(key).get(0));
					update++;
				}
				
			}
		
		// updateExchangeRates.get(0).setValidTo(new
		// Date(System.currentTimeMillis() - 1000));
		/*
		 * for (ExchangeRate updateExchangeRate : updateExchangeRates) { //
		 * equals to the current system datetime-1
		 * updateExchangeRate.setValidTo(new Date(System.currentTimeMillis() -
		 * 1000)); LOG.info("exchangeRate.setValidTo " +
		 * updateExchangeRate.getValidTo()); }
		 */ /*
			 * for (ExchangeRate upRate : updateExchangeRates) {
			 * em.merge(upRate); }
			 */
		for (ExchangeRate exchangeRate : newExchangeRates) {
			em.merge(exchangeRate);
		}
		int[] results = { exchangeRatesSize-update, update };
		return results;
	}

	/*
	 * public int[] updateExchangeRate(List<ExchangeRate> exchangeRates, String
	 * bizUnit) {
	 * 
	 * List<ExchangeRate> oldExchangeRates = findExchangeRateByBizUnit(bizUnit);
	 * 
	 * int add = 0, update = 0, delete = 0;
	 * 
	 * // Have to use the complex method to add/update/delete as the audit log
	 * // report is based on Exchange_Rate table's trigger. List<ExchangeRate>
	 * addExchangeRates = new ArrayList<ExchangeRate>(); List<ExchangeRate>
	 * updateExchangeRates = new ArrayList<ExchangeRate>(); List<ExchangeRate>
	 * deleteExchangeRates = new ArrayList<ExchangeRate>();
	 * 
	 * // Iterate the new and old list to fill up the addExchangeRates and //
	 * updateExchangeRates for (ExchangeRate exchangeRate : exchangeRates) {
	 * boolean found = false; for (ExchangeRate oldExchangeRate :
	 * oldExchangeRates) { if
	 * (exchangeRate.getKey().equals(oldExchangeRate.getKey())) { found = true;
	 * updateExchangeRates.add(oldExchangeRate); if
	 * (oldExchangeRate.getDeleteFlag()) { add++; } else { update++; } break; }
	 * } if (found == false) { add++; addExchangeRates.add(exchangeRate); } }
	 * 
	 * for (ExchangeRate updateExchangeRate : updateExchangeRates) { for
	 * (ExchangeRate exchangeRate : exchangeRates) { if
	 * (updateExchangeRate.getKey().equals(exchangeRate.getKey())) {
	 * updateExchangeRate.setExRateFrom(exchangeRate.getExRateFrom());
	 * updateExchangeRate.setExRateTo(exchangeRate.getExRateTo());
	 * updateExchangeRate.setHandling(exchangeRate.getHandling());
	 * updateExchangeRate.setVat(exchangeRate.getVat());
	 * updateExchangeRate.setValidFrom(exchangeRate.getValidFrom());
	 * updateExchangeRate.setLastUpdatedBy(exchangeRate.getLastUpdatedBy()); //
	 * updateExchangeRate.setDeleteFlag(false); break; } } }
	 * 
	 * // Iterate the old and new list to fill up the deleteExchangeRates for
	 * (ExchangeRate oldExchangeRate : oldExchangeRates) { boolean found =
	 * false; for (ExchangeRate exchangeRate : exchangeRates) { if
	 * (oldExchangeRate.getKey().equals(exchangeRate.getKey())) { found = true;
	 * break; } } if (found == false) { delete++; //
	 * oldExchangeRate.setDeleteFlag(true);
	 * deleteExchangeRates.add(oldExchangeRate); } }
	 * 
	 * for (ExchangeRate exchangeRate : deleteExchangeRates) {
	 * em.merge(exchangeRate); }
	 * 
	 * for (ExchangeRate exchangeRate : addExchangeRates) {
	 * em.merge(exchangeRate); }
	 * 
	 * for (ExchangeRate exchangeRate : updateExchangeRates) {
	 * em.merge(exchangeRate); }
	 * 
	 * int[] results = { add, update, delete };
	 * 
	 * return results;
	 * 
	 * }
	 */

	public List<ExchangeRate> findExchangeRateByBizUnit(String bizUnit) {
		TypedQuery<ExchangeRate> query = em.createQuery("select e from ExchangeRate e where e.bizUnit = :bizUnit", ExchangeRate.class);
		query.setParameter("bizUnit", bizUnit);
		List<ExchangeRate> exchangeRates = query.getResultList();
		return exchangeRates;

	}

	public List<ExchangeRate> findExchangeRateByBizUnitInValiday(String bizUnit, Date date) {

		TypedQuery<ExchangeRate> query = em.createQuery("select e from ExchangeRate e where e.bizUnit=?1 and e.validFrom<=?2 and e.validTo>=?3",
				ExchangeRate.class);
		query.setParameter(1, bizUnit);
		query.setParameter(2, date);
		query.setParameter(3, date);
		// query.setParameter(3, date);
		List<ExchangeRate> exchangeRates = query.getResultList();
		return exchangeRates;

	}

	public ExcCurrencyDefault findDefaultCurrencyByBu(String bizUnit) {
		TypedQuery<ExcCurrencyDefault> query = em.createQuery("select e from ExcCurrencyDefault e where e.bizUnit = :bizUnit",
				ExcCurrencyDefault.class);
		query.setParameter("bizUnit", bizUnit);
		List<ExcCurrencyDefault> list = query.getResultList();
		if (list == null || list.size() == 0) {
			return null;
		}
		return list.get(0);
	}

	public void updateDefaultCurrency(ExcCurrencyDefault defaultCurrency) {
		em.merge(defaultCurrency);
		em.flush();

	}

	public void updateCurrencybyRegion(BizUnit curr) {
		em.merge(curr);
		em.flush();

	}
}
