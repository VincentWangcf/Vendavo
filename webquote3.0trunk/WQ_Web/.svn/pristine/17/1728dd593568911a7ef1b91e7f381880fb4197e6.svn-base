package com.avnet.emasia.webquote.web.quote;

import java.io.Serializable;
import java.util.List;

import javax.faces.model.SelectItem;

import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil;
import com.avnet.emasia.webquote.web.quote.constant.QuoteConstant;

public class UpdateSprVO implements Serializable {
 
	private static final long serialVersionUID = 1L;
	private String designLocation;
	private String shipToCode;
	private String shipToParty;
	private String projectName;
	private String application;
	private String competitorInformation;
	private Integer eau;
	private String mfrDr;
	private String ppSchedule;
	private String mpSchedule;
	private String applyType;
	private String designRegion;
	private String formNumber;
	private SelectItem[] ppScheduleSelectItem;
	private SelectItem[] mpScheduleSelectItem;

	public SelectItem[] getPpScheduleSelectItem() {
		if (ppScheduleSelectItem == null) {
			ppScheduleSelectItem = QuoteUtil.createFilterOptions(QuoteUtil.generateLatest24Month(QuoteConstant.DATE_FORMAT_MP_PP_SCHEUDLE));
		}
		return ppScheduleSelectItem;
	}

	public SelectItem[] getMpScheduleSelectItem() {
		if (mpScheduleSelectItem == null) {
			mpScheduleSelectItem = QuoteUtil.createFilterOptions(QuoteUtil.generateLatest24Month(QuoteConstant.DATE_FORMAT_MP_PP_SCHEUDLE));
		}
		return mpScheduleSelectItem;
	}
 
	public String getDesignLocation() {
		return designLocation;
	}

	public void setDesignLocation(String designLocation) {
		this.designLocation = designLocation;
	}

	public String getShipToCode() {
		return shipToCode;
	}

	public void setShipToCode(String shipToCode) {
		this.shipToCode = shipToCode;
	}

	public String getShipToParty() {
		return shipToParty;
	}

	public void setShipToParty(String shipToParty) {
		this.shipToParty = shipToParty;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getCompetitorInformation() {
		return competitorInformation;
	}

	public void setCompetitorInformation(String competitorInformation) {
		this.competitorInformation = competitorInformation;
	}

	public Integer getEau() {
		return eau;
	}

	public void setEau(Integer eau) {
		this.eau = eau;
	}

	public String getMfrDr() {
		return mfrDr;
	}

	public void setMfrDr(String mfrDr) {
		this.mfrDr = mfrDr;
	}

	public String getPpSchedule() {
		return ppSchedule;
	}

	public void setPpSchedule(String ppSchedule) {
		this.ppSchedule = ppSchedule;
	}

	public String getMpSchedule() {
		return mpSchedule;
	}

	public void setMpSchedule(String mpSchedule) {
		this.mpSchedule = mpSchedule;
	}

	public String getApplyType() {
		return applyType;
	}

	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}

	public String getDesignRegion() {
		return designRegion;
	}

	public void setDesignRegion(String designRegion) {
		this.designRegion = designRegion;
	}

	public String getFormNumber() {
		return formNumber;
	}

	public void setFormNumber(String formNumber) {
		this.formNumber = formNumber;
	}
}
