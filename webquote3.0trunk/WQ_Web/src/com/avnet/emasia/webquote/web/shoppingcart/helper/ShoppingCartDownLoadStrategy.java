package com.avnet.emasia.webquote.web.shoppingcart.helper;


import java.util.List;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFont;

import com.avnet.emasia.webquote.commodity.constant.ShoppingCartConstant;
import com.avnet.emasia.webquote.quote.vo.ShoppingCartLoadItemBean;
import com.avnet.emasia.webquote.utilites.web.util.POIUtils;
import com.avnet.emasia.webquote.utilities.bean.ExcelAliasBean;
import com.avnet.emasia.webquote.web.maintenance.DownLoadStrategy;

public class ShoppingCartDownLoadStrategy extends DownLoadStrategy {
	static final Logger LOG = Logger.getLogger(ShoppingCartDownLoadStrategy.class.getSimpleName());

	private static final long serialVersionUID = 1826810634921438019L;

	@Override
	public void setDateTosheet(XSSFWorkbook wb, List<?> data) {
		
		List<ExcelAliasBean> excelAliasTreeSet = 
				POIUtils.getQuoteBuilderExcelAliasAnnotation(ShoppingCartLoadItemBean.class);
		XSSFCell cell = null;
		XSSFSheet sheet = wb.getSheetAt(0);
		// get the first column to get the clor in every column.
		XSSFRow headerRow = sheet.getRow(0);
		XSSFCellStyle[] cellStyles = new XSSFCellStyle[excelAliasTreeSet.size()];
		for(int i=0;i<excelAliasTreeSet.size();i++) {
			XSSFCellStyle cellStyle= (XSSFCellStyle) headerRow.getCell(i).getCellStyle().clone();
			cellStyle.setWrapText(true);
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			//set no bold and no infect the header style.
			//XSSFFont font = cellStyle.getFont();
			XSSFFont font2 = new XSSFFont(CTFont.Factory.newInstance());
			cellStyle.setFont(font2);
			cellStyles[i] =cellStyle;
		}
		
		

		if(data != null){
			/*XSSFCellStyle cellStyle=wb.createCellStyle();     
			cellStyle.setWrapText(true);
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);*/
			for(int j=0; j<data.size(); j++){
				XSSFRow row = sheet.createRow(ShoppingCartConstant.TEMPLATE_HEADER_ROW_NUMBER+j);
				ShoppingCartLoadItemBean bean = (ShoppingCartLoadItemBean) data.get(j);
				int i = 0;
				for(ExcelAliasBean excelAliasBean:excelAliasTreeSet){					
					cell = row.createCell(excelAliasBean.getCellNum()-1);
					cell.setCellType(Cell.CELL_TYPE_STRING);
					cell.setCellStyle(cellStyles[i]);
					if(cell!=null){
						cell.setCellValue(POIUtils.getter(bean,excelAliasBean.getFieldName()));
					}
					i++;
				}
			}
		}		
	}
}
