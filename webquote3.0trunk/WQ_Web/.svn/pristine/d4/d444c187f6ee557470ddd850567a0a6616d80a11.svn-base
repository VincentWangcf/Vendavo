package com.avnet.emasia.webquote.web.maintenance;

import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.avnet.emasia.webquote.entity.BalUnconsumedQty;

public class BalanceUnconsumedQtyDownLoadStrategy extends DownLoadStrategy{

	private static final long serialVersionUID = 4295074079224674391L;

	@Override
	public void setDateTosheet(XSSFWorkbook wb, List<?> data) {
		XSSFCell cell = null;
		XSSFSheet sheet = wb.getSheetAt(0);
		XSSFRow fristRow = sheet.getRow(0);

		XSSFCellStyle cellStyle = fristRow.getCell(1).getCellStyle();
		if(data != null){
			for(int j=0; j<data.size(); j++){
				XSSFRow dataRow = sheet.createRow(j+1);
				BalUnconsumedQty bean = (BalUnconsumedQty) data.get(j);
				/**Avnet Quote Number*/
				cell = dataRow.createCell(0);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(bean.getQuoteNumber()!=null){
					cell.setCellValue(bean.getQuoteNumber());
				}
				/**MFR*/
				cell = dataRow.createCell(1);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellStyle(cellStyle);				
				if(bean.getMfr()!=null){
					cell.setCellValue(bean.getMfr());
				}
				/**Quoted Part Number*/
				cell = dataRow.createCell(2);
				cell.setCellType(Cell.CELL_TYPE_STRING);					
				if(bean.getQuotedPartNumber()!=null){
					cell.setCellValue(bean.getQuotedPartNumber());
				}
				/**MFR Quote #*/
				cell = dataRow.createCell(3);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellStyle(cellStyle);					
				if(bean.getSupplierQuoteNumber()!=null){
					cell.setCellValue(bean.getSupplierQuoteNumber());
				}
				/**Balance Unconsumed Qty*/
				cell = dataRow.createCell(4);
				cell.setCellStyle(cellStyle);
				if(bean.getBuQty()!=null){
					cell.setCellValue(bean.getBuQty());	
				}
			}
		}	
	}

	
}
