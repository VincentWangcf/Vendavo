package com.avnet.emasia.webquote.web.stm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.avnet.emasia.webquote.entity.VendorBpmCustomer;
import com.avnet.emasia.webquote.stm.dto.ShipToCodeMappingVo;
import com.avnet.emasia.webquote.utilites.web.util.POIUtils;
import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil;
import com.avnet.emasia.webquote.utilities.bean.ExcelAliasBean;
import com.avnet.emasia.webquote.web.maintenance.UploadStrategy;

public class ShipToCodeUploadStrategy extends UploadStrategy{


	private static final long serialVersionUID = -623514309861408074L;

	
	@Override
	public List<ShipToCodeMappingVo> excel2Beans(Sheet sheet) {
		List<ShipToCodeMappingVo> beans = new ArrayList<ShipToCodeMappingVo>();
		Row row = null;
		int startRow = 4;
		int lineSeq = startRow+1;
		for(int i=startRow;i<=sheet.getLastRowNum();i++){
		// skip first row
			if (lineSeq == 0) {
				lineSeq++;
				continue;
			}
			row = sheet.getRow(i);
			

			String team="";;
			if(null!=row.getCell(0)) {
				row.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
				team = String.valueOf(row.getCell(0).getStringCellValue());  
			}
			
			if(!QuoteUtil.isEmpty(team)) {
				ShipToCodeMappingVo bean = new ShipToCodeMappingVo();
				bean.setXlsLineSeq(lineSeq++);
				bean.setTeamName(team);
				
				if(null!=row.getCell(1))
					bean.setSgashipToCode(row.getCell(1).getStringCellValue());//Ship-to code (SGA)	

				if(null!=row.getCell(2)) {
					row.getCell(2).setCellType(Cell.CELL_TYPE_STRING);
					bean.setSadashipToCode(row.getCell(2).getStringCellValue());//Ship-to code (SADA)
				}
			
				beans.add(bean);
			}else {
				break;
			}
		}
		sheet = null;
		return beans;
	}

	@Override
	public String verifyFields(List<?> beans) {
		return "";
	}

}
