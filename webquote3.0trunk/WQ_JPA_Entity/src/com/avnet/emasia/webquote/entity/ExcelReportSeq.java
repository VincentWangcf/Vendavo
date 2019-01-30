package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the EXCEL_REPORT_SEQ database table.
 * 
 */
@Entity
@Table(name="EXCEL_REPORT_SEQ")
public class ExcelReportSeq implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	@Column(name="SEQ_NO")
	private Integer seqNo;

	//bi-directional many-to-one association to ExcelReport
	@ManyToOne
	@JoinColumn(name="REPORT_ID")
	private ExcelReport excelReport;

	//bi-directional many-to-one association to ExcelReportColumn
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="COLUMN_ID")
	private ExcelReportColumn excelReportColumn;

	public ExcelReportSeq() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Integer getSeqNo() {
		return this.seqNo;
	}

	public void setSeqNo(Integer seqNo) {
		this.seqNo = seqNo;
	}

	public ExcelReport getExcelReport() {
		return this.excelReport;
	}

	public void setExcelReport(ExcelReport excelReport) {
		this.excelReport = excelReport;
	}

	public ExcelReportColumn getExcelReportColumn() {
		return this.excelReportColumn;
	}

	public void setExcelReportColumn(ExcelReportColumn excelReportColumn) {
		this.excelReportColumn = excelReportColumn;
	}

}