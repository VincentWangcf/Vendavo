package com.avnet.emasia.webquote.poi;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class CataLogPageExcelReportFormat implements ExcelReportFormat {

	@Override
	public void setColumnWidth(SXSSFSheet sh) {
		sh.setColumnWidth(0, 3000);
		sh.setColumnWidth(1, 5000);
		sh.setColumnWidth(2, 3000);
		sh.setColumnWidth(3, 10000);
		sh.setColumnWidth(4, 5000);
		sh.setColumnWidth(5, 3000);
		sh.setColumnWidth(6, 5000);
		sh.setColumnWidth(7, 5000);
		sh.setColumnWidth(8, 5000);
		sh.setColumnWidth(9, 6000);
		sh.setColumnWidth(10, 6000);

	}

	@Override
	public void setCellStyle(SXSSFWorkbook workbook, Cell cell, Integer colIndex) {
		CellStyle style = workbook.createCellStyle();
		style.setWrapText(true);
		cell.setCellStyle(style);
	}




}
