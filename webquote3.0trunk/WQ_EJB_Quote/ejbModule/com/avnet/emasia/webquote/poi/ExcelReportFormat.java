package com.avnet.emasia.webquote.poi;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public interface ExcelReportFormat {
	
	public void setColumnWidth(SXSSFSheet sh);
	
	public void setCellStyle(SXSSFWorkbook workbook,Cell cell,Integer colIndex);

	
}
