package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the EXCEL_REPORT_COLUMN database table.
 * 
 */
@Entity
@Table(name="EXCEL_REPORT_COLUMN")
public class ExcelReportColumn implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	@Column(name="BEAN_PROPERTY")
	private String beanProperty;

	@Column(name="CELL_DATA_TYPE")
	private String cellDataType;

	@Column(name="HEADER_NAME")
	private String headerName;

	//bi-directional many-to-one association to ExcelReportSeq
	@OneToMany(mappedBy="excelReportColumn")
	private List<ExcelReportSeq> excelReportSeqs;

	public ExcelReportColumn() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getBeanProperty() {
		return this.beanProperty;
	}

	public void setBeanProperty(String beanProperty) {
		this.beanProperty = beanProperty;
	}

	public String getCellDataType() {
		return this.cellDataType;
	}

	public void setCellDataType(String cellDataType) {
		this.cellDataType = cellDataType;
	}

	public String getHeaderName() {
		return this.headerName;
	}

	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}

	public List<ExcelReportSeq> getExcelReportSeqs() {
		return this.excelReportSeqs;
	}

	public void setExcelReportSeqs(List<ExcelReportSeq> excelReportSeqs) {
		this.excelReportSeqs = excelReportSeqs;
	}

	public ExcelReportSeq addExcelReportSeq(ExcelReportSeq excelReportSeq) {
		getExcelReportSeqs().add(excelReportSeq);
		excelReportSeq.setExcelReportColumn(this);

		return excelReportSeq;
	}

	public ExcelReportSeq removeExcelReportSeq(ExcelReportSeq excelReportSeq) {
		getExcelReportSeqs().remove(excelReportSeq);
		excelReportSeq.setExcelReportColumn(null);

		return excelReportSeq;
	}

}