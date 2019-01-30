package com.avnet.emasia.webquote.web.maintenance;

import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.avnet.emasia.webquote.quote.vo.RestrictedCustomerMappingVo;

public class RestrictedCustomerMappingDownLoadStrategy extends DownLoadStrategy {

	private static final long serialVersionUID = 1117219060393543439L;

	@Override
	public void setDateTosheet(XSSFWorkbook wb,List<?> data){
		XSSFCell cell = null;
		XSSFSheet sheet = wb.getSheetAt(0);
		XSSFRow fristRow = sheet.getRow(0);

		XSSFCellStyle cellStyle = fristRow.getCell(1).getCellStyle();
		if(data != null){
			for(int j=0; j<data.size(); j++){
				XSSFRow dataRow = sheet.createRow(j+1);
				RestrictedCustomerMappingVo bean = (RestrictedCustomerMappingVo) data.get(j);
				/**MFR      now MFR*/    
				cell = dataRow.createCell(0);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(bean.getMfr()!=null){
					cell.setCellValue(bean.getMfr());
				}
				//Part Number  /
				cell = dataRow.createCell(1);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				//cell.setCellStyle(cellStyle);				
				if(bean.getPartNumber()!=null){
					cell.setCellValue(bean.getPartNumber());
				}
				//**Product Group 1*      /
				cell = dataRow.createCell(2);
				cell.setCellType(Cell.CELL_TYPE_STRING);					
				if(bean.getProductGroup1()!=null){
					cell.setCellValue(bean.getProductGroup1());
				}
				//**Product Group 2*     /
				cell = dataRow.createCell(3);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				//cell.setCellStyle(cellStyle);					
				if(bean.getProductGroup2()!=null){
					cell.setCellValue(bean.getProductGroup2());
				}
				//**Product Group 3*    /
				cell = dataRow.createCell(4);
				//cell.setCellStyle(cellStyle);
				if(bean.getProductGroup3()!=null){
					cell.setCellValue(bean.getProductGroup3());	
				}
				
				//**Product Group 4*     /
				cell = dataRow.createCell(5);
				//cell.setCellStyle(cellStyle);
				if(bean.getProductGroup4()!=null){
					cell.setCellValue(bean.getProductGroup4());	
				}
				
				//**Cost Indicator*    now sold to code/
				cell = dataRow.createCell(6);
				//cell.setCellStyle(cellStyle);
				if(bean.getSoldToCode()!=null){
					cell.setCellValue(bean.getSoldToCode());	
				}
				
				//**PSold To Code*     Product Group 1/
				cell = dataRow.createCell(7);
				//cell.setCellStyle(cellStyle);
				if(bean.getEndCustomerCode()!=null){
					cell.setCellValue(bean.getEndCustomerCode());	
				}
			
			}
		}
	}

}