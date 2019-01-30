package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;

import com.avnet.emasia.webquote.constants.RemoteEjbClassEnum;

import java.math.BigDecimal;
import java.util.List;


/**
 * The persistent class for the EXCEL_REPORT database table.
 * 
 */
@Entity
@Table(name="EXCEL_REPORT")
public class ExcelReport implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	@Column(name="SHEET_NO")
	private Integer sheetNo;

	@Column(name="REPORT_NAME")
	private String reportName;

	@Column(name="TEMPLATE_NAME")
	private String templateName;

	//bi-directional many-to-one association to ExcelReportSeq
	@OneToMany(mappedBy="excelReport",fetch=FetchType.EAGER)
	private List<ExcelReportSeq> excelReportSeqs;

	public ExcelReport() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Integer getSheetNo() {
		return sheetNo;
	}

	public void setSheetNo(Integer sheetNo) {
		this.sheetNo = sheetNo;
	}

	public String getReportName() {
		return this.reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getTemplateName() {
		return this.templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public List<ExcelReportSeq> getExcelReportSeqs() {
		return this.excelReportSeqs;
	}

	public void setExcelReportSeqs(List<ExcelReportSeq> excelReportSeqs) {
		this.excelReportSeqs = excelReportSeqs;
	}

	public ExcelReportSeq addExcelReportSeq(ExcelReportSeq excelReportSeq) {
		getExcelReportSeqs().add(excelReportSeq);
		excelReportSeq.setExcelReport(this);

		return excelReportSeq;
	}

	public ExcelReportSeq removeExcelReportSeq(ExcelReportSeq excelReportSeq) {
		getExcelReportSeqs().remove(excelReportSeq);
		excelReportSeq.setExcelReport(null);

		return excelReportSeq;
	}

	@Override
	public String toString() {
		return "ExcelReport [id=" + id + ", sheetNo=" + sheetNo + ", reportName=" + reportName + ", templateName=" + templateName+
				"]";
	}
	
	
	

}