package com.avnet.emasia.webquote.quote.vo; 

import java.io.Serializable;
import java.util.Date;

import com.avnet.emasia.webquote.entity.BizUnit;

public class AuditExchangeReportSearchCriteria extends ExcahngeRateAndAuditExchangeFields implements Serializable {

	private static final long serialVersionUID = -3403994098709715754L;
	private static final String SPERATOR = "\r\n";
	private Date updateDateFrom;
	private Date updateDateTo;
	private BizUnit bizUnit;
	
	public AuditExchangeReportSearchCriteria() {

	}
	public Date getUpdateDateFrom() {
		return updateDateFrom;
	}

	public void setUpdateDateFrom(Date updateDateFrom) {
		this.updateDateFrom = updateDateFrom;
	}

	public Date getUpdateDateTo() {
		return updateDateTo;
	}

	public void setUpdateDateTo(Date updateDateTo) {
		this.updateDateTo = updateDateTo;
	}

	public BizUnit getBizUnit() {
		return bizUnit;
	}

	public void setBizUnit(BizUnit bizUnit) {
		this.bizUnit = bizUnit;
	}


	
}
