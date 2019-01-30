package com.avnet.emasia.webquote.quote.ejb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.avnet.emasia.webquote.constants.ActionEnum;
import com.avnet.emasia.webquote.constants.AttachmentEnum;
import com.avnet.emasia.webquote.constants.BmtFlagEnum;
import com.avnet.emasia.webquote.constants.StageEnum;
import com.avnet.emasia.webquote.constants.StatusEnum;
import com.avnet.emasia.webquote.entity.Attachment;
import com.avnet.emasia.webquote.entity.Currency;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.CustomerAddress;
import com.avnet.emasia.webquote.entity.ExchangeRate;
import com.avnet.emasia.webquote.entity.ManufacturerDetail;
import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.Money;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.QuoteItemDesign;
import com.avnet.emasia.webquote.entity.Role;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.exception.AppException;
import com.avnet.emasia.webquote.exception.CommonConstants;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.quote.vo.QuoteItemVo;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;
import com.avnet.emasia.webquote.utilities.util.BeanUtilsExtends;

@Stateless
@LocalBean
public class CopyRefreshQuoteSB {

	@PersistenceContext(unitName = "Server_Source")
	EntityManager em;

	@EJB
	QuoteSB quoteSB;


	private static final Logger logger = Logger.getLogger(CopyRefreshQuoteSB.class.getName());

	public void markAsUserd(List<QuoteItemVo> vos){
		quoteSB.saveQuoteItems(vos);
	}

	public List<QuoteItemVo> refreshQuote(List<QuoteItemVo> vos,
			User currentUser, String action) throws AppException {

		List<QuoteItemVo> newVos = new ArrayList<QuoteItemVo>();

		User user = em.find(User.class, currentUser.getId()); // Re-attach user
																// to entity
																// manager
		List<Role> roles = user.getRoles();
		boolean isCS = false;
		for (Role role : roles) {
			if (role.getName().contains("ROLE_CS")) {
				isCS = true;
				break;
			}
		}

		Map<String, List<QuoteItemVo>> map = new HashMap<String, List<QuoteItemVo>>();

		for (QuoteItemVo vo : vos) {
			QuoteItem item = vo.getQuoteItem();
			Quote quote = item.getQuote();

			List<QuoteItemVo> list = map.get(quote.getFormNumber());

			if (list == null) {
				list = new ArrayList<QuoteItemVo>();
				list.add(vo);
				map.put(quote.getFormNumber(), list);
			} else {
				list.add(vo);
			}

		}
		
		String auditAction = ActionEnum.MYQUOTE_COPY_QUOTE.name();
		
		if("copyBMT".equalsIgnoreCase(action)){
			auditAction = ActionEnum.MYQUOTE_COPY_BMT_QUOTE.name();
		}else if("refresh".equalsIgnoreCase(action)){
			auditAction = ActionEnum.MYQUOTE_REFRERSH_QUOTE.name();
		}else {
			auditAction = ActionEnum.MYQUOTE_COPY_QUOTE.name();
		}
		

		for (String formNumber : map.keySet()) {

			List<QuoteItemVo> vosInSameQuote = map.get(formNumber);

			Quote oldQuote = vosInSameQuote.get(0).getQuoteItem().getQuote();

			Quote newQuote = copyQuoteSpecific(oldQuote, currentUser);
			newQuote.setStage(StageEnum.PENDING.toString());

			int seq = 1;

			for (QuoteItemVo vo : vosInSameQuote) {

				QuoteItem oldItem = vo.getQuoteItem();
//				QuoteItemDesign oldDesign = oldItem.getQuoteItemDesign();
				
				QuoteItem newItem = copyQuoteItem(oldItem, currentUser);
				
				if("copyBMT".equalsIgnoreCase(action)){
					newItem.setStatus(StatusEnum.RBQ.toString());
				}
				
				newItem.setAction(auditAction); // add audit action 

				if("refresh".equalsIgnoreCase(action) || "copy".equalsIgnoreCase(action)){
					newItem.setExRateFnl(null);
					newItem.setBuyCurr(oldItem.getBuyCurr());
					newItem.setRfqCurr(oldItem.getRfqCurr());
					
					ExchangeRate exRates[];
					if (newItem.getQuote().isdLinkFlag()){
						exRates = Money.getExchangeRateForDlink(newItem.getBuyCurr(), newItem.getRfqCurr(), newItem.getQuote().getBizUnit(), newItem.getSoldToCustomer(), new Date());
					}
					else {
						exRates = Money.getExchangeRate(newItem.getBuyCurr(), newItem.getRfqCurr(), newItem.getQuote().getBizUnit(), newItem.getSoldToCustomer(), new Date());
					}
					//if(null!=latestRate) {
					if(null!=exRates[1] && exRates[0]!=null) {
						newItem.setExRateRfq(exRates[1].getExRateTo());
						newItem.setExRateBuy(exRates[0].getExRateTo());
						newItem.setHandling(exRates[1].getHandling());
						newItem.setVat(exRates[1].getVat());
						//if (newItem.getExchangeRateType()==null) newItem.setExchangeRateType(exRates[1].getExchangeRateType());
					}
					
				}
				/*
				if(null!=oldDesign) { // for the old record which no quote item design 
					QuoteItemDesign newDesign = this.copyItemDesign(oldDesign, user);
					newItem.setQuoteItemDesign(newDesign);
					newDesign.setQuoteItem(newItem);
				}
				*/
				if (newItem.getQuoteItemDesign() != null) {
					newItem.getQuoteItemDesign().setCreatedBy(user.getEmployeeId());
					newItem.getQuoteItemDesign().setLastUpdatedBy(user.getEmployeeId());
					newItem.getQuoteItemDesign().setCreatedTime(new Date());
					newItem.getQuoteItemDesign().setLastUpdatedTime(new Date());
				}
				
				newItem.setItemSeq(seq++);
				ncnrFilter(newItem); // fix INC0026988 added by June 20140902 add NCNR filter in refresh quote

				newItem.setQuote(newQuote);
				newQuote.addItem(newItem);

				clearQuotationField(newItem);

				specialTISStatus(newItem, action);
				copyAttachment(oldItem,newItem,action);
				
				if(vo.getEau()==null || vo.getEau()==0) {
					newItem.setEau(null);
				} else {
					newItem.setEau(vo.getEau());
				}

				if (isMoreThanOneYearAgo(newItem.getSentOutTime())) {
					newItem.setFirstRfqCode(null); // popuate first rfq after getting new quote number in this method
					newItem.setRevertVersion("0A");
				} else {
					newItem.setFirstRfqCode(oldItem.getFirstRfqCode());
					String postfix = newItem.getRevertVersion().substring(newItem.getRevertVersion().length() - 1);
					long count = getRevertVersionByFirstRFQCode(newItem.getFirstRfqCode());
					count++;
					newItem.setRevertVersion(count + postfix);
				}

				quoteSB.applyDefaultCostIndicatorLogic(newItem, true);

				

				QuoteItemVo newVo = new QuoteItemVo();
				newVo.setQuoteItem(newItem);
				newVo.setSeq(seq);
				newVo.setSeq2(seq);
				newVos.add(newVo);
			}

			if (isCS) {
				if (newQuote.getCopyToCS() == null) {
					newQuote.setCopyToCS(user.getEmployeeId() + ";");
				} else if (!newQuote.getCopyToCS().contains(user.getEmployeeId())) {
					newQuote.setCopyToCS(newQuote.getCopyToCS()
							+ user.getEmployeeId() + ";");
				}
				if (newQuote.getCopyToCsName() == null) {
					newQuote.setCopyToCsName(user.getEmployeeId() + ";");
				} else if (!newQuote.getCopyToCS().contains(user.getEmployeeId())) {
					newQuote.setCopyToCsName(newQuote.getCopyToCsName()
							+ user.getName() + ";");
				}
			}

			quoteSB.populateQuoteNumber(newQuote);
			String quoteNumber = "";
			for (QuoteItem item : newQuote.getQuoteItems()) {
				if (item.getFirstRfqCode() == null) {
					item.setFirstRfqCode(item.getQuoteNumber());
				}
				quoteNumber = quoteNumber + "," + item.getQuoteNumber();
			}

			quoteSB.saveQuote(newQuote);
			logger.info( action + " quote " + newQuote.getFormNumber() + "==QuoteNumber " + quoteNumber + " succcessfully." );			

		}

		return newVos;

	}

	private void copyAttachment(QuoteItem oldItem, QuoteItem newItem,
			String action) {
		if ("refresh".equalsIgnoreCase(action)) {
			List<Attachment> oldAttachments = oldItem.getAttachments();
			if (oldAttachments != null && oldAttachments.size() != 0) {
				List<Attachment> newAttachments = new ArrayList<Attachment>();
				for (Attachment oldAttachment : oldAttachments) {
					if (oldAttachment.isNewAttachment()) {
						oldAttachment.setQuoteItem(newItem);
						newAttachments.add(oldAttachment);

					}else {
						if(AttachmentEnum.QC.getName().equalsIgnoreCase(oldAttachment.getType())
								||AttachmentEnum.BMT.getName().equalsIgnoreCase(oldAttachment.getType()))  {
							Attachment newAttach = new Attachment();
							BeanUtilsExtends.copyProperties(newAttach, oldAttachment);
							newAttach.setId(0L);
							newAttach.setQuoteItem(newItem);
							
							newAttachments.add(newAttach);
						}
						
					}
				}
				newItem.setAttachments(newAttachments);
			}
		}else if("copyBMT".equalsIgnoreCase(action)) {

			List<Attachment> oldAttachments = oldItem.getAttachments();
			if (oldAttachments != null && oldAttachments.size() != 0) {
				List<Attachment> newAttachments = new ArrayList<Attachment>();
				for (Attachment oldAttachment : oldAttachments) {
					if (oldAttachment.isNewAttachment()) {
						oldAttachment.setQuoteItem(newItem);
						newAttachments.add(oldAttachment);

					}else {
						Attachment newAttach = new Attachment();
						BeanUtilsExtends.copyProperties(newAttach, oldAttachment);
						newAttach.setId(0L);
						newAttach.setQuoteItem(newItem);
						newAttachments.add(newAttach);
						
					}
				}
				newItem.setAttachments(newAttachments);
			}
		
		}else {
			List<Attachment> oldAttachments = oldItem.getAttachments();
			if (oldAttachments != null && oldAttachments.size() != 0) {
				List<Attachment> newAttachments = new ArrayList<Attachment>();
				for (Attachment oldAttachment : oldAttachments) {
					if (oldAttachment.isNewAttachment()) {
						oldAttachment.setQuoteItem(newItem);
						newAttachments.add(oldAttachment);

					}
				}
				newItem.setAttachments(newAttachments);
			}
		}
	}

	public int getRevertVersionByFirstRFQCode(String firstRFQCode)
			throws AppException {
		TypedQuery<QuoteItem> query = em.createQuery(
				"select i from QuoteItem i where i.firstRfqCode=:firstRFQCode",
				QuoteItem.class);
		query.setParameter("firstRFQCode", firstRFQCode);
		List<QuoteItem> quoteItems = query.getResultList();
		int compare = 0;
		for (QuoteItem item : quoteItems) {
			String revertVersion = item.getRevertVersion();
			if (revertVersion == null || revertVersion.length() < 2) {
//				AppException e = new AppException(
//						"Cannot refresh quote. Invalid revert version "
//								+ item.getRevertVersion() + " in RFQ:"
//								+ item.getQuoteNumber());
				AppException e = new AppException(CommonConstants.COMMON_GET_CONFIG_HAS_EMPTY_KEY,new Object[]{item.getRevertVersion(),item.getQuoteNumber()});
				throw e;
			}
			revertVersion = revertVersion.substring(0,
					revertVersion.length() - 1);
			int iRevertVersion = 0;
			try {
				iRevertVersion = Integer.parseInt(revertVersion);
			} catch (Exception e) {
				AppException e1 = new AppException(CommonConstants.COMMON_GET_CONFIG_HAS_EMPTY_KEY,new Object[]{item.getRevertVersion(),item.getQuoteNumber()});
				
				throw e1;
			}
			if (compare < iRevertVersion) {
				compare = iRevertVersion;
			}
		}

		return compare;
	}

	private boolean isMoreThanOneYearAgo(Date date) {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.YEAR, -1);
		return date.before(now.getTime());
	}

	private void clearQuotationField(QuoteItem item) {
		item.setId(0L);
		item.setSprFlag(true);
		item.setQuotedPrice(null);
		item.setQuotedQty(null);
		item.setCost(null);
		item.setMpq(null);
		item.setMoq(null);
		item.setMov(null);
		item.setPriceValidity(null);
		item.setPmoq(null);
		item.setLeadTime(null);
		item.setVendorQuoteNumber(null);
		item.setPoExpiryDate(null);
		item.setQuoteExpiryDate(null);
		item.setVendorDebitNumber(null);
		item.setResaleMax(null);
		item.setResaleMin(null);
		item.setCostIndicator(null);
		item.setTermsAndConditions(null);
		item.setShipmentValidity(null);
		item.setReferenceMargin(null);
		item.setTargetMargin(null);
		item.setResaleMargin(null);
		item.setVendorQuoteQty(null);
		
		//for the BMT defect 206
		item.setLastQcInDate(null);   // Last_QC_In_Date
		item.setLastQcOutDate(null);   // Last_QC_Out_date
		item.setLastBmtInDate(null);   //		- Last_BMT_IN_Date
        item.setLastBmtOutDate(null);  //		- Last_BMT_Out_Date
        item.setLastItUpdateTime(null);  //		- Last_IT_Update_time
        item.setLastRitUpdateTime(null); //		- Last_RIT_Update_time
        item.setLastSqInDate(null);   //		- Last_SQ_In_Date
        item.setLastSqOutDate(null);  //		- Last_SQ_Out_Date

		
	}

	private Quote copyQuote(Quote oldQuote) {

//		Quote newQuote = new Quote();
//		BeanUtilsExtends.copyProperties(newQuote, oldQuote);
		
		Quote newQuote =  Quote.newInstance(oldQuote);
		
		newQuote.setSoldToCustomerNameChinese(getCustomerFullChineseName(oldQuote.getSoldToCustomer()));
		if (oldQuote.getEndCustomer() != null){
			newQuote.setEndCustomerName(oldQuote.getEndCustomer().getCustomerFullName());
		}else{
			newQuote.setEndCustomerName(oldQuote.getEndCustomerName());
		}

//		newQuote.setFormAttachmentFlag(false);
		newQuote.setSoldToCustomerName(oldQuote.getSoldToCustomer().getCustomerFullName());
//why comment...
		//Fixed for defect#305 that Sales teams name is not aligned when see in pending list, RFQ details and quotation history. by Damonchen@20180514
		newQuote.setTeam(oldQuote.getSales().getTeam());
//		newQuote.setQuoteItems(new ArrayList<QuoteItem>());
//		newQuote.setAttachments(null);
//		newQuote.setVersion(0);
//		newQuote.setId(0L);
//		newQuote.setFormNumber(null);
		return newQuote;
	}

	private String getCustomerFullChineseName(Customer customer) {
		String fullChineseName = "";
		List<CustomerAddress> customerAddresses = customer
				.getCustomerAddresss();
		for (CustomerAddress customerAddress : customerAddresses) {
			if (customerAddress.getCountry() != null
					&& customerAddress.getId().getLanguageCode() != null
					&& (customerAddress.getId().getLanguageCode()
							.equals(QuoteSBConstant.LANGUAGE_CODE_CHINESE_C)
							|| customerAddress
									.getId()
									.getLanguageCode()
									.equals(QuoteSBConstant.LANGUAGE_CODE_CHINESE_M) || customerAddress
							.getId().getLanguageCode()
							.equals(QuoteSBConstant.LANGUAGE_CODE_CHINESE_1))) {
				fullChineseName = customerAddress.getCustomerName1();
				if (fullChineseName != null
						&& customerAddress.getCustomerName2() != null) {
					fullChineseName += customerAddress.getCustomerName2();
				}
			}
		}
		return fullChineseName;
	}

	public Quote copyQuoteSpecific(Quote oldQuote, User currentUser) {
		Date date = new Date();
		Quote newQuote = this.copyQuote(oldQuote);

		newQuote.setLastUpdatedOn(date);
		newQuote.setLastUpdatedBy(currentUser.getEmployeeId());
		newQuote.setLastUpdatedName(currentUser.getName());
		newQuote.setCreatedOn(date);
		newQuote.setCreatedBy(currentUser.getEmployeeId());
		newQuote.setCreatedName(currentUser.getName());
		newQuote.setMpSchedule(null);
		newQuote.setPpSchedule(null);

		return newQuote;
	}

	private QuoteItem copyQuoteItem(QuoteItem oldItem, User user) {
//		QuoteItem newItem = new QuoteItem();
//		BeanUtilsExtends.copyProperties(newItem, oldItem);
		QuoteItem newItem = QuoteItem.newInstance(oldItem);
		newItem.setAttachments(new ArrayList<Attachment>());
		
		newItem.setLastPmUpdatedOn(null);
		newItem.setLastQcUpdatedOn(null);
		newItem.setLastUpdatedPm(null);
		newItem.setLastUpdatedPmName(null);
		newItem.setLastUpdatedQc(null);
		newItem.setLastUpdatedQcName(null);
		newItem.setStatus(StatusEnum.QC.toString());
		newItem.setStage(StageEnum.PENDING.toString()); // added by June for BMT project
		
		//newItem.setFinalQuotationPrice(oldItem.getFinalQuotationPrice());
		newItem.setFinalQuotationPrice(null);
		Date date = new Date();
		newItem.setLastUpdatedOn(date);
		newItem.setLastUpdatedBy(user.getEmployeeId());// andy modify 2.2
		newItem.setLastUpdatedName(user.getName());
		newItem.setSentOutTime(date);
		newItem.setSubmissionDate(date);
		newItem.setQuoteResponseTimeHistorys(null);
		newItem.setVersion(0);
		newItem.setId(0L);
		newItem.setQuoteNumber(null);
		newItem.setRequestedPartNumber(oldItem.getQuotedPartNumber());
		newItem.setAttachmentFlag("00000"); // fix defect attachment flag

		if (oldItem.getEau() == null || oldItem.getEau() == 0) {
			newItem.setEau(null);
		}

		if (oldItem.getEndCustomer() != null) {
			newItem.setEndCustomerName(oldItem.getEndCustomer().getCustomerFullName());
		} else{
			newItem.setEndCustomerName(oldItem.getEndCustomerName());			
		}
		
		if (oldItem.getQuotedMaterial() != null) {
			newItem.setQuotedMfr(oldItem.getQuotedMaterial().getManufacturer());
			newItem.setQuotedPartNumber(oldItem.getQuotedMaterial().getFullMfrPartNumber());
		} else {
			newItem.setQuotedMfr(oldItem.getRequestedMfr());
			newItem.setQuotedPartNumber(oldItem.getRequestedPartNumber());
		}
		// added by June for RMB CUR PROJECT 20140702
		newItem.setExRateBuy(null);
		newItem.setExRateRfq(null);
		newItem.setVat(null);
		newItem.setHandling(null);
		if (oldItem.getRfqCurr() == null || "".equals(oldItem.getRfqCurr())) {
			newItem.setBuyCurr(null);
			newItem.setRfqCurr(null);
		} else {
			newItem.setBuyCurr(Currency.USD);
			newItem.setRfqCurr(oldItem.getRfqCurr());
		}
		
		newItem.setFisYear(null);
		newItem.setFisMth(null);
		newItem.setFisMth(null);
		newItem.setUsedFlag(null);
		newItem.setFisQtr(null);
		newItem.setOrginalAuthGp(null);
		//modified by lenon 2016/03/10 set the SapPartNumber follow to old quoteItem value
		//newItem.setSapPartNumber(null);
		
		return newItem;

	}

	public QuoteItem copyQuoteItem(QuoteItem oldItem, Quote quote,
			User currentUser) {
		QuoteItem newItem = copyQuoteItem(oldItem, currentUser);
		List<QuoteItem> newQuoteItemLst = quote.getQuoteItems();
		if (null == newQuoteItemLst) {
			List<QuoteItem> items = new ArrayList<QuoteItem>();
			items.add(newItem);
			quote.setQuoteItems(items);
		} else {
			newQuoteItemLst.add(newItem); // add this new Item to quote;
		}
		newItem.setQuote(quote);
		quoteSB.calculateQuoteNumber(quote, newItem);
		newItem.setFirstRfqCode(newItem.getQuoteNumber());
		return newItem;
	}

	/**
	 * set cancellation windows and reschedule windows is null when NCNR is
	 * found
	 * 
	 * @param qi
	 * @return
	 */
	public QuoteItem ncnrFilter(QuoteItem qi) {
		boolean findNcnr = false;
		String tempTerm = qi.getTermsAndConditions();
		if (tempTerm != null && (tempTerm.toUpperCase().contains("NCNR"))) {
			// tempTerm = tempTerm.replace("NCNR", "");
			// tempTerm = tempTerm.replace("ncnr", "");
			// /qi.setTermsAndConditions(tempTerm); no need to replace
			// ��NCNR�� with ����
			findNcnr = true;
		}
		if (findNcnr) {
			qi.setCancellationWindow(null);
			qi.setRescheduleWindow(null);
		}
		return qi;
	}

/*	
	  private QuoteItemDesign copyItemDesign(QuoteItemDesign oldDesign,User user) {
    	QuoteItemDesign newDesign = new QuoteItemDesign();
    	if(null!=oldDesign) {
			BeanUtilsExtends.copyProperties(newDesign,oldDesign);
			Date date = new Date();
			newDesign.setCreatedBy(user.getEmployeeId());
			newDesign.setCreatedTime(date);
			newDesign.setLastUpdatedBy(user.getEmployeeId());
			newDesign.setLastUpdatedTime(date);
			
			
			
		}
    	newDesign.setId(0L);
    	newDesign.setVersion(0);
    	
    	return newDesign;
    	
    }
*/
	private void specialTISStatus(QuoteItem newItem, String action) {
		if ("refresh".equalsIgnoreCase(action)) {
			String mfr = null != newItem.getRequestedMfr() ? newItem.getRequestedMfr().getName() : null;
			if(null!=newItem.getQuoteItemDesign() && null!=newItem.getQuoteItemDesign().getBmtFlag()) {
				String bmtFlag =newItem.getQuoteItemDesign().getBmtFlag().getBmtFlagCode();
				if ("TIS".equalsIgnoreCase(mfr)
						&& (BmtFlagEnum.BMT_DR.code().equals(bmtFlag) || BmtFlagEnum.BMT_DR_Incomplete.code().equals(bmtFlag))) {
					newItem.setStatus(StatusEnum.BQ.toString());
				}
			}
			
		}

	}
}
