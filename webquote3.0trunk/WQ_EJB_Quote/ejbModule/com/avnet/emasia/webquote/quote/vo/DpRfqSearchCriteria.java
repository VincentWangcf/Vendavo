package com.avnet.emasia.webquote.quote.vo; 

import java.io.Serializable;
import java.text.DateFormat;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.avnet.emasia.webquote.entity.BizUnit;


/**
 * @author Yang, Lenon(043044) Created on 2016-01-25
 */

public class DpRfqSearchCriteria extends AuditLogAndDpRfqSearchFields implements Serializable {

	private static final long serialVersionUID = -3403994098709715754L;
	private static final String SPERATOR = "\r\n";
	private Date createRfqTimeFrom;
	
	private Date createRfqTimeTo;
	
	private Date updateRfqTimeFrom;
	
	private Date updateRfqTimeTo;
	
	private String mfr;
	
	private String sSapPartNumber;
	
	private String sReferenceId;
	
	private String sReferenceLineId;
	
	
	
	private List<String> referenceId;
	
	private List<String> referenceLineId;
	
	private List<String> sapPartNumber;
	
	private String inBoundMessage;
	
	private List<BizUnit> bizUnits;
	
	public DpRfqSearchCriteria() {

	}
	
	public String toString() {
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		StringBuffer sb = new StringBuffer("");

		sb.append("DpRfqSearchCriteria:");
	

		sb.append("SapPartNumber:[");
		if (sapPartNumber != null) {
			for (String s : sapPartNumber) {
				sb.append(s + ",");
			}
		}
		sb.append("] ");
		
		sb.append("QuoteNumber:[");
		if (getQuoteNumber() != null) {
			for (String s : getQuoteNumber()) {
				sb.append(s + ",");
			}
		}
		sb.append("] ");
		
		sb.append("MFR:[");
		if(StringUtils.isNotBlank(mfr)) {
			sb.append(mfr);
		}
		sb.append("] ");
		
		sb.append("Inbound Message:[");
		if(StringUtils.isNotBlank(inBoundMessage)) {
			sb.append(inBoundMessage);
		}
		sb.append("] ");
		
		sb.append("Reference Id:[");
		if (referenceId != null) {
			for (String s : referenceId) {
				sb.append(s + ",");
			}
		}
		sb.append("] ");
		
		sb.append("Reference Line Id:[");
		if (referenceLineId != null) {
			for (String s : referenceLineId) {
				sb.append(s + ",");
			}
		}
		sb.append("] ");
		
		sb.append("Receive Time From:[");
		if (createRfqTimeFrom != null) {
			sb.append(format.format(createRfqTimeFrom));
		}
		sb.append("] ");
		
		sb.append("Receive Time To:[");
		if (createRfqTimeTo != null) {
			sb.append(format.format(createRfqTimeTo));
		}
		sb.append("] ");
		
		sb.append("Update Time From:[");
		if (updateRfqTimeFrom != null) {
			sb.append(format.format(updateRfqTimeFrom));
		}
		sb.append("] ");
		
		sb.append("Update Time To:[");
		if (updateRfqTimeTo != null) {
			sb.append(format.format(updateRfqTimeTo));
		}
		sb.append("] ");
		
		return sb.toString();
	}
	
	
	public void setupUIInCriteria() {
		if (StringUtils.isNotBlank(getsQuoteNumber())) {
			String[] array = getsQuoteNumber().split(SPERATOR);
			List<String> quoteNumbers = new ArrayList<String>();
			for (String quoteNumber : array) {
				if (StringUtils.isNotBlank(quoteNumber)) {
					String s = quoteNumber.trim();
					if (!s.equals("")) {
						quoteNumbers.add(s);
					}
				}
			}
			this.setQuoteNumber(quoteNumbers);

		} else {
			this.setQuoteNumber(null);
		}
		
		if (StringUtils.isNotBlank(sSapPartNumber)) {
			String[] array = sSapPartNumber.split(SPERATOR);
			List<String> sapPartNoList = new ArrayList<String>();
			for (String sapPartNo : array) {
				if (StringUtils.isNotBlank(sapPartNo)) {
					String s = sapPartNo.trim();
					if (!s.equals("")) {
						sapPartNoList.add(s);
					}
				}
			}
			this.setSapPartNumber(sapPartNoList);

		} else {
			this.setSapPartNumber(null);
		}

		if (StringUtils.isNotBlank(sReferenceId)) {
			String[] array = sReferenceId.split(SPERATOR);
			List<String> referenceIdList = new ArrayList<String>();
			for (String refId : array) {
				if (StringUtils.isNotBlank(refId)) {
					String s = refId.trim();
					if (!s.equals("")) {
						referenceIdList.add(s);
					}
				}
			}
			this.setReferenceId(referenceIdList);

		} else {
			this.setReferenceId(null);
		}

		if (StringUtils.isNotBlank(sReferenceLineId)) {
			String[] array = sReferenceLineId.split(SPERATOR);
			List<String> referenceLineIdList = new ArrayList<String>();
			for (String refLineId : array) {
				if (StringUtils.isNotBlank(refLineId)) {
					String s = refLineId.trim();
					if (!s.equals("")) {
						referenceLineIdList.add(s);
					}
				}
			}
			this.setReferenceLineId(referenceLineIdList);

		} else {
			this.setReferenceLineId(null);
		}

	}

	public Date getCreateRfqTimeFrom() {
		return createRfqTimeFrom;
	}

	public void setCreateRfqTimeFrom(Date createRfqTimeFrom) {
		this.createRfqTimeFrom = createRfqTimeFrom;
	}

	public Date getCreateRfqTimeTo() {
		return createRfqTimeTo;
	}

	public void setCreateRfqTimeTo(Date createRfqTimeTo) {
		this.createRfqTimeTo = createRfqTimeTo;
	}

	public Date getUpdateRfqTimeFrom() {
		return updateRfqTimeFrom;
	}

	public void setUpdateRfqTimeFrom(Date updateRfqTimeFrom) {
		this.updateRfqTimeFrom = updateRfqTimeFrom;
	}

	public Date getUpdateRfqTimeTo() {
		return updateRfqTimeTo;
	}

	public void setUpdateRfqTimeTo(Date updateRfqTimeTo) {
		this.updateRfqTimeTo = updateRfqTimeTo;
	}

	public String getMfr() {
		return mfr;
	}

	public void setMfr(String mfr) {
		this.mfr = mfr;
	}

	public String getsSapPartNumber() {
		return sSapPartNumber;
	}

	public void setsSapPartNumber(String sSapPartNumber) {
		this.sSapPartNumber = sSapPartNumber;
	}

	public String getsReferenceId() {
		return sReferenceId;
	}

	public void setsReferenceId(String sReferenceId) {
		this.sReferenceId = sReferenceId;
	}

	public String getsReferenceLineId() {
		return sReferenceLineId;
	}

	public void setsReferenceLineId(String sReferenceLineId) {
		this.sReferenceLineId = sReferenceLineId;
	}

	public List<String> getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(List<String> referenceId) {
		this.referenceId = referenceId;
	}

	public List<String> getReferenceLineId() {
		return referenceLineId;
	}

	public void setReferenceLineId(List<String> referenceLineId) {
		this.referenceLineId = referenceLineId;
	}

	public List<String> getSapPartNumber() {
		return sapPartNumber;
	}

	public void setSapPartNumber(List<String> sapPartNumber) {
		this.sapPartNumber = sapPartNumber;
	}

	public String getInBoundMessage() {
		return inBoundMessage;
	}

	public void setInBoundMessage(String inBoundMessage) {
		this.inBoundMessage = inBoundMessage;
	}

	public List<BizUnit> getBizUnits() {
		return bizUnits;
	}

	public void setBizUnits(List<BizUnit> bizUnits) {
		this.bizUnits = bizUnits;
	}
	
}
