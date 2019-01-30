package com.avnet.emasia.webquote.reports.vo;

import java.io.Serializable;
import java.text.DateFormat;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.DataAccess;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;

public class QuoteReportCriteria implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String SPERATOR = "\r\n";

	private String sFormNumber;
	private String sQuoteNumber;
	private String sMfr;
	private String sQuotedPartNumber;
	private String sCustomerName;
	private String sSoldToCode;
	private String sEndCustomerName;
	private String sSalesName;
	private String sYourReference;
	private String sTeam;
	private String soleToCustomerName;
	private String qcPricer;

	private List<String> soleToCustomerNameLst;
	private List<String> formNumber;
	private List<String> quoteNumber;
	private List<String> quotedPartNumber;
	private List<String> customerName;
	private List<String> mfr;
	private List<String> salesName;
	private List<String> yourReference;
	private List<String> soldToCode;
	private List<String> endCustomerName;
	private List<String> team;
	private List<Long> quoteId;
	private List<String> attachmentType;

	private List<String> pageStatus = new ArrayList<String>();
	private List<String> pageStage = new ArrayList<String>();
	private List<String> pageSalesCSStage = new ArrayList<String>();

	private List<String> status = new ArrayList<String>();
	private List<String> stage = new ArrayList<String>();

	private Date pageUploadDateFrom;
	private Date pageUploadDateTo;
	private Date pageSentOutDateFrom;
	private Date pageSentOutDateTo;
	private Date pagePoExpiryDateFrom;
	private Date pagePoExpiryDateTo;
	private Date pageQuoteExpiryDateFrom;
	private Date pageQuoteExpiryDateTo;

	private Date uploadDateFrom;
	private Date uploadDateTo;
	private Date sentOutDateFrom;
	private Date sentOutDateTo;
	private Date poExpiryDateFrom;
	private Date poExpiryDateTo;
	private Date quoteExpiryDateFrom;
	private Date quoteExpiryDateTo;

	/*
	 * Sales & CS: security range for QuoteExpiryDate ( 6 month before -
	 * 9999/12/31 QC PM : no limit MM: security range for POExpiryDate ( 6 month
	 * before - 9999/12/31
	 */

	private List<User> restrictedSales;
	private List<User> subordinates;

	private List<DataAccess> dataAccesses;

	private List<BizUnit> bizUnits;

	private int start;
	private int end;
	private String sort;
	private String dir;

	private int pageSize;
	private int pageIndex;

	// TODO
	private String qcRole;
	
	private boolean limitResult;

	public QuoteReportCriteria() {

	}

	public String getQcPricer()
	{
		return qcPricer;
	}

	public void setQcPricer(String qcPricer)
	{
		this.qcPricer = qcPricer;
	}

	public boolean isLimitResult() {
		return limitResult;
	}

	public void setLimitResult(boolean limitResult) {
		this.limitResult = limitResult;
	}

	public void setupUIInCriteria() {

		// Special handling for Stage Draft. Draft RFQ has "DQ" status, which is
		// hidden for user
		if (stage.contains(QuoteSBConstant.QUOTE_STAGE_DRAFT)) {
			if (!status.contains(QuoteSBConstant.RFQ_STATUS_DQ)) {
				status.add(QuoteSBConstant.RFQ_STATUS_DQ);
			}
		} else {
			for (int i = 0; i < status.size(); i++) {
				if (status.get(i).equalsIgnoreCase(
						QuoteSBConstant.RFQ_STATUS_DQ)) {
					status.remove(i);
					break;
				}
			}
		}

		if (sFormNumber != null && !sFormNumber.equals("")) {
			String[] array = sFormNumber.split(SPERATOR);
			this.setFormNumber(Arrays.asList(array));
		} else {
			this.setFormNumber(null);
		}

		if (sQuoteNumber != null && !sQuoteNumber.equals("")) {
			String[] array = sQuoteNumber.split(SPERATOR);
			List<String> quoteNumbers = new ArrayList<String>();
			for (String quoteNumber : array) {
				if (!isEmpty(quoteNumber)) {
					quoteNumbers.add(quoteNumber);
				}
			}
			this.setQuoteNumber(quoteNumbers);

		} else {
			this.setQuoteNumber(null);
		}

		if (sMfr != null && !sMfr.equals("")) {
			String[] array = sMfr.split(SPERATOR);
			this.setMfr(Arrays.asList(array));
		} else {
			this.setMfr(null);
		}
		
		if (soleToCustomerName != null && !soleToCustomerName.equals("")) {
			String[] array = soleToCustomerName.split(SPERATOR);
			this.setSoleToCustomerNameLst(Arrays.asList(array));
		} else {
			this.setSoleToCustomerNameLst(null);
		}

		if (sQuotedPartNumber != null && !sQuotedPartNumber.equals("")) {
			String[] array = sQuotedPartNumber.split(SPERATOR);
			this.setQuotedPartNumber(Arrays.asList(array));
		} else {
			this.setQuotedPartNumber(null);
		}

		if (sCustomerName != null && !sCustomerName.equals("")) {
			String[] array = sCustomerName.split(SPERATOR);
			this.setCustomerName(Arrays.asList(array));
		} else {
			this.setCustomerName(null);
		}

		if (sSoldToCode != null && !sSoldToCode.equals("")) {
			String[] array = sSoldToCode.split(SPERATOR);
			this.setSoldToCode(Arrays.asList(array));
		} else {
			this.setSoldToCode(null);
		}

		if (sEndCustomerName != null && !sEndCustomerName.equals("")) {
			String[] array = sEndCustomerName.split(SPERATOR);
			this.setEndCustomerName(Arrays.asList(array));
		} else {
			this.setEndCustomerName(null);
		}

		if (sSalesName != null && !sSalesName.equals("")) {
			String[] array = sSalesName.split(SPERATOR);
			this.setSalesName(Arrays.asList(array));
		} else {
			this.setSalesName(null);
		}

		if (sYourReference != null && !sYourReference.equals("")) {
			String[] array = sYourReference.split(SPERATOR);
			List<String> yourReferences = new ArrayList<String>();
			for (String yourReference : array) {
				if (!isEmpty(yourReference)) {
					yourReferences.add(yourReference);
				}
			}
			this.setYourReference(yourReferences);
		} else {
			this.setYourReference(null);
		}

		if (sTeam != null && !sTeam.equals("")) {
			String[] array = sTeam.split(SPERATOR);
			this.setTeam(Arrays.asList(array));
		} else {
			this.setTeam(null);
		}

		uploadDateFrom = convertToDateBegin(pageUploadDateFrom);
		uploadDateTo = convertToDateEnd(pageUploadDateTo);
		sentOutDateFrom = convertToDateBegin(pageSentOutDateFrom);
		sentOutDateTo = convertToDateEnd(pageSentOutDateTo);


	}
	

	private Date convertToDateBegin(Date date) {
		if (date == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	private Date convertToDateEnd(Date date) {
		if (date == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal.getTime();
	}

	public void clear() {
		sFormNumber = null;
		sQuoteNumber = null;
		sMfr = null;
		sQuotedPartNumber = null;
		sCustomerName = null;
		sSoldToCode = null;
		sEndCustomerName = null;
		sSalesName = null;
		sYourReference = null;
		sTeam = null;

		status = new ArrayList<String>();
		stage = new ArrayList<String>();

		pageUploadDateFrom = null;
		pageUploadDateTo = null;
		pageSentOutDateFrom = null;
		pageSentOutDateTo = null;
		pagePoExpiryDateFrom = null;
		pagePoExpiryDateTo = null;
		pageQuoteExpiryDateFrom = null;
		pageQuoteExpiryDateTo = null;
	}

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer("");

		sb.append("MyQuoteSearchCriteria:");
		sb.append("FormNumber:[");
		if (formNumber != null) {
			for (String s : formNumber) {
				sb.append(s + ",");
			}
		}
		sb.append("] ");

		sb.append("QuoteNumber:[");
		if (quoteNumber != null) {
			for (String s : quoteNumber) {
				sb.append(s + ",");
			}
		}
		sb.append("] ");

		sb.append("FullMfrPartNumber:[");
		if (quotedPartNumber != null) {
			for (String s : quotedPartNumber) {
				sb.append(s + ",");
			}
		}
		sb.append("] ");

		sb.append("CustomerName:[");
		if (customerName != null) {
			for (String s : customerName) {
				sb.append(s + ",");
			}
		}
		sb.append("] ");

		sb.append("MFR:[");
		if (mfr != null) {
			for (String s : mfr) {
				sb.append(s + ",");
			}
			sb.append("] ");
		}

		sb.append("salesName:[");
		if (salesName != null) {
			for (String s : salesName) {
				sb.append(s + ",");
			}
		}
		sb.append("] ");

		sb.append("YourReference:[");
		if (salesName != null) {
			for (String s : salesName) {
				sb.append(s + ",");
			}
		}
		sb.append("] ");

		sb.append("SoldToCode:[");
		if (soldToCode != null) {
			for (String s : soldToCode) {
				sb.append(s + ",");
			}
		}
		sb.append("] ");

		sb.append("EndCustomerName:[");
		if (endCustomerName != null) {
			for (String s : endCustomerName) {
				sb.append(s + ",");
			}
		}
		sb.append("] ");

		sb.append("Team:[");
		if (team != null) {
			for (String s : team) {
				sb.append(s + ",");
			}
		}
		sb.append("] ");

		sb.append("Status:[");
		if (status != null) {
			for (String s : status) {
				sb.append(s + ",");
			}
		}
		sb.append("] ");

		sb.append("Stage:[");
		if (stage != null) {
			for (String s : stage) {
				sb.append(s + ",");
			}
		}
		sb.append("] ");

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		sb.append("UploadDateFrom:[");
		if (uploadDateFrom != null) {
			sb.append(format.format(uploadDateFrom));
		}
		sb.append("] ");

		sb.append("UploadDateTo:[");
		if (uploadDateTo != null) {
			sb.append(format.format(uploadDateTo));
		}
		sb.append("] ");

		sb.append("SentOutDateFrom:[");
		if (sentOutDateFrom != null) {
			sb.append(format.format(sentOutDateFrom));
		}
		sb.append("] ");

		sb.append("SentOutDateTo:[");
		if (sentOutDateTo != null) {
			sb.append(format.format(sentOutDateTo));
		}
		sb.append("] ");

		sb.append("PoExpiryDateFrom:[");
		if (poExpiryDateFrom != null) {
			sb.append(format.format(poExpiryDateFrom));
		}
		sb.append("] ");

		sb.append("PoExpiryDateTo:[");
		if (poExpiryDateTo != null) {
			sb.append(format.format(poExpiryDateTo));
		}
		sb.append("] ");

		sb.append("QuoteExpiryDateFrom:[");
		if (quoteExpiryDateFrom != null) {
			sb.append(format.format(quoteExpiryDateFrom));
		}
		sb.append("] ");

		sb.append("QuoteExpiryDateTo:[");
		if (quoteExpiryDateTo != null) {
			sb.append(format.format(quoteExpiryDateTo));
		}
		sb.append("] ");

		return sb.toString();
	}

	private boolean isEmpty(String source) {
		if (source == null || source.equals("")) {
			return true;
		}
		return false;
	}

	// Getters, Setters

	public List<String> getFormNumber() {
		return formNumber;
	}

	public void setFormNumber(List<String> formNumber) {
		this.formNumber = formNumber;
	}

	public List<String> getQuoteNumber() {
		return quoteNumber;
	}

	public void setQuoteNumber(List<String> quoteNumber) {
		this.quoteNumber = quoteNumber;
	}

	public List<String> getSalesName() {
		return salesName;
	}

	public void setSalesName(List<String> salesName) {
		this.salesName = salesName;
	}

	public List<String> getYourReference() {
		return yourReference;
	}

	public void setYourReference(List<String> yourReference) {
		this.yourReference = yourReference;
	}

	public List<String> getSoldToCode() {
		return soldToCode;
	}

	public void setSoldToCode(List<String> soldToCode) {
		this.soldToCode = soldToCode;
	}

	public List<String> getEndCustomeName() {
		return endCustomerName;
	}

	public void setEndCustomerName(List<String> endCustomerName) {
		this.endCustomerName = endCustomerName;
	}

	public List<String> getStatus() {
		return status;
	}

	public void setStatus(List<String> status) {
		this.status = status;
	}

	public Date getUploadDateFrom() {
		return uploadDateFrom;
	}

	public void setUploadDateFrom(Date uploadDateFrom) {
		this.uploadDateFrom = uploadDateFrom;
	}

	public Date getUploadDateTo() {
		return uploadDateTo;
	}

	public void setUploadDateTo(Date uploadDateTo) {
		this.uploadDateTo = uploadDateTo;
	}

	public Date getSentOutDateFrom() {
		return sentOutDateFrom;
	}

	public void setSentOutDateFrom(Date sentOutDateFrom) {
		this.sentOutDateFrom = sentOutDateFrom;
	}

	public Date getSentOutDateTo() {
		return sentOutDateTo;
	}

	public void setSentOutDateTo(Date sentOutDateTo) {
		this.sentOutDateTo = sentOutDateTo;
	}

	public Date getPoExpiryDateFrom() {
		return poExpiryDateFrom;
	}

	public void setPoExpiryDateFrom(Date poExpiryDateFrom) {
		this.poExpiryDateFrom = poExpiryDateFrom;
	}

	public Date getPoExpiryDateTo() {
		return poExpiryDateTo;
	}

	public void setPoExpiryDateTo(Date poExpiryDateTo) {
		this.poExpiryDateTo = poExpiryDateTo;
	}

	public Date getQuoteExpiryDateFrom() {
		return quoteExpiryDateFrom;
	}

	public void setQuoteExpiryDateFrom(Date quoteExpiryDateFrom) {
		this.quoteExpiryDateFrom = quoteExpiryDateFrom;
	}

	public Date getQuoteExpiryDateTo() {
		return quoteExpiryDateTo;
	}

	public void setQuoteExpiryDateTo(Date quoteExpiryDateTo) {
		this.quoteExpiryDateTo = quoteExpiryDateTo;
	}

	public List<User> getRestrictedSales() {
		return restrictedSales;
	}

	public void setRestrictedSales(List<User> restrictedSales) {
		this.restrictedSales = restrictedSales;
	}

	public List<User> getSubordinates() {
		return subordinates;
	}

	public void setSubordinates(List<User> subordinates) {
		this.subordinates = subordinates;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public String getQcRole() {
		return qcRole;
	}

	public void setQcRole(String qcRole) {
		this.qcRole = qcRole;
	}

	public List<DataAccess> getDataAccesses() {
		return dataAccesses;
	}

	public void setDataAccesses(List<DataAccess> dataAccesses) {
		this.dataAccesses = dataAccesses;
	}

	public List<String> getQuotedPartNumber() {
		return quotedPartNumber;
	}

	public void setQuotedPartNumber(List<String> quotedPartNumber) {
		this.quotedPartNumber = quotedPartNumber;
	}

	public List<String> getCustomerName() {
		return customerName;
	}

	public void setCustomerName(List<String> customerName) {
		this.customerName = customerName;
	}

	public List<String> getMfr() {
		return mfr;
	}

	public void setMfr(List<String> mfr) {
		this.mfr = mfr;
	}

	public List<String> getStage() {
		return stage;
	}

	public void setStage(List<String> stage) {
		this.stage = stage;
	}

	public List<String> getTeam() {
		return team;
	}

	public void setTeam(List<String> team) {
		this.team = team;
	}

	public List<String> getEndCustomerName() {
		return endCustomerName;
	}

	public String getsFormNumber() {
		return sFormNumber;
	}

	public void setsFormNumber(String sFormNumber) {
		this.sFormNumber = sFormNumber;
	}

	public String getsQuoteNumber() {
		return sQuoteNumber;
	}

	public void setsQuoteNumber(String sQuoteNumber) {
		this.sQuoteNumber = sQuoteNumber;
	}

	public String getsMfr() {
		return sMfr;
	}

	public void setsMfr(String sMfr) {
		this.sMfr = sMfr;
	}

	public String getSoleToCustomerName() {
		return soleToCustomerName;
	}

	public void setSoleToCustomerName(String soleToCustomerName) {
		this.soleToCustomerName = soleToCustomerName;
	}

	public List<String> getSoleToCustomerNameLst() {
		return soleToCustomerNameLst;
	}

	public void setSoleToCustomerNameLst(List<String> soleToCustomerNameLst) {
		this.soleToCustomerNameLst = soleToCustomerNameLst;
	}

	public String getsQuotedPartNumber() {
		return sQuotedPartNumber;
	}

	public void setsQuotedPartNumber(String sQuotedPartNumber) {
		this.sQuotedPartNumber = sQuotedPartNumber;
	}

	public String getsCustomerName() {
		return sCustomerName;
	}

	public void setsCustomerName(String sCustomerName) {
		this.sCustomerName = sCustomerName;
	}

	public String getsSoldToCode() {
		return sSoldToCode;
	}

	public void setsSoldToCode(String sSoldToCode) {
		this.sSoldToCode = sSoldToCode;
	}

	public String getsEndCustomerName() {
		return sEndCustomerName;
	}

	public void setsEndCustomerName(String sEndCustomerName) {
		this.sEndCustomerName = sEndCustomerName;
	}

	public String getsSalesName() {
		return sSalesName;
	}

	public void setsSalesName(String sSalesName) {
		this.sSalesName = sSalesName;
	}

	public String getsYourReference() {
		return sYourReference;
	}

	public void setsYourReference(String sYourReference) {
		this.sYourReference = sYourReference;
	}

	public String getsTeam() {
		return sTeam;
	}

	public void setsTeam(String sTeam) {
		this.sTeam = sTeam;
	}

	public List<BizUnit> getBizUnits() {
		return bizUnits;
	}

	public void setBizUnits(List<BizUnit> bizUnits) {
		this.bizUnits = bizUnits;
	}

	public Date getPageUploadDateFrom() {
		return pageUploadDateFrom;
	}

	public void setPageUploadDateFrom(Date pageUploadDateFrom) {
		this.pageUploadDateFrom = pageUploadDateFrom;
	}

	public Date getPageUploadDateTo() {
		return pageUploadDateTo;
	}

	public void setPageUploadDateTo(Date pageUploadDateTo) {
		this.pageUploadDateTo = pageUploadDateTo;
	}

	public Date getPageSentOutDateFrom() {
		return pageSentOutDateFrom;
	}

	public void setPageSentOutDateFrom(Date pageSentOutDateFrom) {
		this.pageSentOutDateFrom = pageSentOutDateFrom;
	}

	public Date getPageSentOutDateTo() {
		return pageSentOutDateTo;
	}

	public void setPageSentOutDateTo(Date pageSentOutDateTo) {
		this.pageSentOutDateTo = pageSentOutDateTo;
	}

	public Date getPagePoExpiryDateFrom() {
		return pagePoExpiryDateFrom;
	}

	public void setPagePoExpiryDateFrom(Date pagePoExpiryDateFrom) {
		this.pagePoExpiryDateFrom = pagePoExpiryDateFrom;
	}

	public Date getPagePoExpiryDateTo() {
		return pagePoExpiryDateTo;
	}

	public void setPagePoExpiryDateTo(Date pagePoExpiryDateTo) {
		this.pagePoExpiryDateTo = pagePoExpiryDateTo;
	}

	public Date getPageQuoteExpiryDateFrom() {
		return pageQuoteExpiryDateFrom;
	}

	public void setPageQuoteExpiryDateFrom(Date pageQuoteExpiryDateFrom) {
		this.pageQuoteExpiryDateFrom = pageQuoteExpiryDateFrom;
	}

	public Date getPageQuoteExpiryDateTo() {
		return pageQuoteExpiryDateTo;
	}

	public void setPageQuoteExpiryDateTo(Date pageQuoteExpiryDateTo) {
		this.pageQuoteExpiryDateTo = pageQuoteExpiryDateTo;
	}

	public List<String> getPageStatus() {
		return pageStatus;
	}

	public void setPageStatus(List<String> pageStatus) {
		this.pageStatus = pageStatus;
	}

	public List<String> getPageStage() {
		return pageStage;
	}

	public void setPageStage(List<String> pageStage) {
		this.pageStage = pageStage;
	}

	public List<String> getPageSalesCSStage() {
		return pageSalesCSStage;
	}

	public void setPageSalesCSStage(List<String> pageSalesCSStage) {
		this.pageSalesCSStage = pageSalesCSStage;
	}

	public List<Long> getQuoteId() {
		return quoteId;
	}

	public void setQuoteId(List<Long> quoteId) {
		this.quoteId = quoteId;
	}

	public List<String> getAttachmentType() {
		return attachmentType;
	}

	public void setAttachmentType(List<String> attachmentType) {
		this.attachmentType = attachmentType;
	}

}
