/**
 * @author 916138
 * 2014��6��9��
 */
package com.avnet.emasia.webquote.utilites.web.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ValueExpression;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.primefaces.model.UploadedFile;

import com.avnet.emasia.webquote.reports.vo.ColumnModelVo;

/**
 * @author 916138
 * 
 */
public class ExcelUtil {
	private static final Logger LOGGER = Logger.getLogger(ExcelUtil.class
			.getName());

	private static XSSFCellStyle doubleCellStyle;
	private static XSSFCellStyle longCellStyle;

	public static CellStyle getDefaultCellStyle(SXSSFWorkbook workbook) {

		CellStyle style = workbook.createCellStyle();
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setBorderTop(CellStyle.BORDER_MEDIUM_DASHED);
		return style;
	}

	/**
	 * add header for a sheet
	 * 
	 * @author 916138
	 * @param workbook
	 * @param sheet
	 * @param rowIndex
	 * @param templateArray
	 * @param isDefaultCellStyle
	 * @param userCellStyle
	 * @return
	 */
	public static SXSSFSheet addSheetHeader(SXSSFWorkbook workbook,
			SXSSFSheet sheet, int rowIndex, String[] templateArray,
			boolean isDefaultCellStyle, CellStyle userCellStyle) {

		Row row = sheet.createRow(rowIndex);
		CellStyle cellStyle = null;
		if (isDefaultCellStyle)
			cellStyle = getDefaultCellStyle(workbook);
		else
			cellStyle = userCellStyle;
		Cell cell = null;
		// Style the cell with borders all around.

		int cellLength = templateArray.length;
		for (int i = 0; i < cellLength; i++) {
			cell = row.createCell(i);
			cell.setCellValue(templateArray[i]);
			cell.setCellStyle(cellStyle);
		}

		return sheet;
	}
	
	public static List<SXSSFWorkbook> generateExcelFileLst(List<?> resultList,
			String sheetName, String[] columnNameArr,
			List<ColumnModelVo> columns, int grandColspanSize, Object grandTotal) {
		List<SXSSFWorkbook> wbLst = new ArrayList<SXSSFWorkbook>();
		BigDecimal maxRowbg = new BigDecimal("20000"); // one file contain 200000
		int rowaccess = 100;
		
		BigDecimal objSize = new BigDecimal(resultList.size());
		int fileCount = (objSize.divide(maxRowbg, 0, RoundingMode.UP)).intValue();
		try {
			
			LOGGER.log(Level.INFO,"-----------------------------FileCount =>>>> "+ fileCount);
			int sheetRowCnt = 0;
			for (int i = 1; i <= fileCount; i++) {
				SXSSFWorkbook wb = new SXSSFWorkbook(rowaccess);
				wbLst.add(wb);
				wb.setCompressTempFiles(false);
				XSSFDataFormat df = (XSSFDataFormat) wb.createDataFormat();
				doubleCellStyle = (XSSFCellStyle) wb.createCellStyle();
				doubleCellStyle.setDataFormat(df.getFormat("#,##0.00"));

				longCellStyle = (XSSFCellStyle) wb.createCellStyle();
				longCellStyle.setDataFormat(df.getFormat("#,##0"));

				SXSSFSheet sheet = (SXSSFSheet) wb.createSheet("Sheet" + "" + i);
				int sheetRowIndex = 0;
				sheet = addSheetHeader(wb, sheet, sheetRowIndex, columnNameArr,
						true, null);

				if (fileCount == i) { //last file record size calculate
					sheetRowCnt = (objSize.subtract(maxRowbg
							.multiply(new BigDecimal(i - 1))).intValue());
				} else {
					sheetRowCnt = maxRowbg.intValue(); 
				}

				sheetRowIndex = sheetRowIndex + 1;
				int objIndex = 0;

				if (sheetRowCnt > 0) {
					for (int index = 0; index < sheetRowCnt; index++) {
						objIndex = index + (maxRowbg.intValue() * (i - 1));
						Object bean = resultList.get(objIndex);
						SXSSFRow row = null;
						int rowIndex = index + sheetRowIndex;
						row = (SXSSFRow) sheet.createRow(rowIndex);
						row = addRow(wb, row, bean, columns);

					}
					sheet.flushRows();
				}
				// add footer to the last excel file
				if (fileCount == i && null != grandTotal) {
					addFooterToExcel(wb, sheet.getSheetName(), sheetRowCnt,
							grandTotal, columns, grandColspanSize);
				}

			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "File name +"+sheetName+" not Found :: "+e.getMessage(), e);
		}

		return wbLst;
	}

	/**
	 * Generate a report file.
	 * 
	 * @param resultList
	 * @param bzStr
	 * @return
	 */
	public static SXSSFWorkbook generateExcelFile(List<?> resultList,
			String sheetName, String[] columnNameArr,
			List<ColumnModelVo> columns, int grandColspanSize, Object grandTotal) {

		BigDecimal maxRowbg = new BigDecimal("30000");
		int rowaccess = 100;
		SXSSFWorkbook wb = new SXSSFWorkbook(rowaccess);
		wb.setCompressTempFiles(false);
		BigDecimal rowTolCount = new BigDecimal(resultList.size());
		int sheetCount = (rowTolCount.divide(maxRowbg, 0, RoundingMode.UP))
				.intValue();
		try {

			XSSFDataFormat df = (XSSFDataFormat) wb.createDataFormat();
			doubleCellStyle = (XSSFCellStyle) wb.createCellStyle();
			doubleCellStyle.setDataFormat(df.getFormat("#,##0.00"));

			longCellStyle = (XSSFCellStyle) wb.createCellStyle();
			longCellStyle.setDataFormat(df.getFormat("#,##0"));

			LOGGER.log(Level.INFO,
					"-----------------------------sheetCount =>>>> "
							+ sheetCount);

			for (int i = 1; i <= sheetCount; i++) {
				SXSSFSheet sheet = (SXSSFSheet) wb
						.createSheet("Sheet" + "" + i);
				int sheetRowIndex = 0;
				sheet = addSheetHeader(wb, sheet, sheetRowIndex, columnNameArr,
						true, null);

				int sheetRowCount = 0;

				if (sheetCount == i) {
					sheetRowCount = (rowTolCount.subtract(maxRowbg.multiply(new BigDecimal(i - 1))).intValue());
				} else {
					sheetRowCount = maxRowbg.intValue(); // one sheet contain
														// 65536 and reduce a
														// header row , so it
														// is 65535
				}

				sheetRowIndex = sheetRowIndex + 1;
				int objIndex = 0;

				if (sheetRowCount > 0) {
					for (int index = 0; index < sheetRowCount; index++) {
						objIndex = index + (maxRowbg.intValue() * (i - 1));
						Object bean = resultList.get(objIndex);
						SXSSFRow row = null;
						int rowIndex = index + sheetRowIndex;
						row = (SXSSFRow) sheet.createRow(rowIndex);
						row = addRow(wb, row, bean, columns);

					}
					sheet.flushRows();
					LOGGER.log(Level.INFO,
							"-----------------------------flush ok-----------------------------------");
				}
				// add footer to the excel
				if (sheetCount == i && null != grandTotal) {
					addFooterToExcel(wb, sheet.getSheetName(), sheetRowCount,
							grandTotal, columns, grandColspanSize);
				}

			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "exception in excel sheer number reading, excel sheet name : "+sheetName+" , exception message : "+e.getMessage(), e);
		}

		return wb;
	}

	public static Field getDeclaredField(Object object, String fieldName) {
		Field field = null;

		Class<?> clazz = object.getClass();

		for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
			try {
				field = clazz.getDeclaredField(fieldName);
				return field;
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Exception in getting field : "+fieldName+" , Exception message"+e.getMessage(), e);
			}
		}

		return null;
	}

	private static SXSSFRow addRow(SXSSFWorkbook wb, SXSSFRow row,
			Object rowObj, List<ColumnModelVo> columns) {
		int i = 0;
		try {

			SXSSFCell cell = null;
			for (ColumnModelVo vo : columns) {

				Field field = getDeclaredField(rowObj, vo.getProperty());
				Class<?> fieldType = field.getType();
				field.setAccessible(true);
				cell = (SXSSFCell) row.createCell(i++);
				setXlSCellValue(fieldType, cell, vo, field.get(rowObj));

			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception in adding row in excel, Exception message"+e.getMessage(), e);
		}
		return row;
	}

	private static void setXlSCellValue(Class<?> fieldType, SXSSFCell cell,
			ColumnModelVo vo, Object value) {
		if (fieldType.equals(long.class) || fieldType.equals(Long.class)) {
			if (null != value) {
				cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(new Long(value.toString()));
				cell.setCellStyle(longCellStyle);
			}
		} else if (fieldType.equals(double.class)
				|| fieldType.equals(Double.class)) {

			if (null != value) {
				cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(new Double(value.toString()));
				cell.setCellStyle(doubleCellStyle);
			}
		} else {
			if (null != value) {
				cell.setCellType(XSSFCell.CELL_TYPE_STRING);
				cell.setCellValue(value.toString());

			}
		}

	}

	/**
	 * append grand total to a excel file .for OOH AND QHR report
	 * 
	 * @author 916138
	 * 
	 * @param wb
	 * @param sheetName
	 * @param rowIndex
	 * @param rptObj
	 * @param cols
	 * @param colspanSize2
	 */
	public static void addFooterToExcel(SXSSFWorkbook wb, String sheetName,
			int rowIndex, Object rptObj, List<ColumnModelVo> cols,
			int colspanSize2) {
		Sheet sheet = wb.getSheet(sheetName);
		SXSSFRow row = (SXSSFRow) sheet.createRow(rowIndex + 1);
		XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();

		int i = 0;
		try {
			SXSSFCell cell = null;
			for (ColumnModelVo vo : cols) {
				String property = vo.getProperty();
				Field field = getDeclaredField(rptObj, property);
				Class<?> fieldType = field.getType();
				field.setAccessible(true);
				Object value = field.get(rptObj);
				cell = (SXSSFCell) row.createCell(i++);

				XSSFFont font = (XSSFFont) wb.createFont();
				font.setFontHeightInPoints((short) 10);
				font.setBoldweight(Font.BOLDWEIGHT_BOLD);
				style.setFont(font);
				if (i == 1) {
					cell.setCellValue("Grand Total:");
					cell.setCellStyle(style);
				} else {
					setXlSCellValue(fieldType, cell, vo, value);
				}

			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception in adding footer in excel for sheet name: "+sheetName+", exception message : "+e.getMessage(), e);
		}

	}
	public static Workbook shiftColumns(Workbook original, String[] newSort){
		LOGGER.info("shiftColumns start");
		Sheet sheetOriginal = original.getSheetAt(0);
		Sheet sortSheet = original.createSheet("export");
		
		for(int n=0;n < newSort.length;n++){
//			logger.info(n + ","+newSort[n].replaceAll("\r\n", "\\\\r\\\\n"));
			Row rowHeader = sheetOriginal.getRow(0);
			for(int i=0; i < rowHeader.getPhysicalNumberOfCells();i++){
				Cell cell = rowHeader.getCell(i);
				if(cell.getStringCellValue().equals(newSort[n])){
					for(Row r: sheetOriginal){
						Row sortRow = sortSheet.getRow(r.getRowNum());
						if(sortRow == null){
							sortRow = sortSheet.createRow(r.getRowNum());
						}
						if(r.getRowNum() == 0){
							sortRow.createCell(n).setCellValue(r.getCell(i).getStringCellValue().replace("\r\n", " ").replaceAll("- ", "").replace(" -", ""));
						}else{
							sortRow.createCell(n).setCellValue(r.getCell(i).getStringCellValue());
						}
						sortRow.getCell(n).setCellStyle(r.getCell(i).getCellStyle());
						sortRow.getCell(n).setCellType(r.getCell(i).getCellType());
						sortSheet.autoSizeColumn(n, true);
						//TODO: style
					}
					break;
				}
			}
		}
		original.removeSheetAt(0);
		original.setSheetOrder("export", 0);
		LOGGER.info("shiftColumns end");
		return original;
	}
	
	public static CellStyle createStyle(HSSFWorkbook wb,POICellColor color,short alignment){
		CellStyle sheetStyle = createStyle(wb,color);
		sheetStyle.setAlignment(alignment);
		return sheetStyle;
	}
	public static CellStyle createStyle(HSSFWorkbook wb,POICellColor color){
	CellStyle sheetStyle = wb.createCellStyle();
	if(color == POICellColor.MANDATORY){
		HSSFPalette paletteMandtory = wb.getCustomPalette();
        paletteMandtory.setColorAtIndex(HSSFColor.LIME.index, (byte)251, (byte)236, (byte)136);
        sheetStyle.setFillForegroundColor(paletteMandtory.getColor(HSSFColor.LIME.index).getIndex());
        sheetStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
	}else if(color == POICellColor.OPTIONAL){
		HSSFPalette palette = wb.getCustomPalette();
		palette.setColorAtIndex(HSSFColor.LAVENDER.index, (byte)242, (byte)220, (byte)219);
		sheetStyle.setFillForegroundColor(palette.getColor(HSSFColor.LAVENDER.index).getIndex());
		sheetStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
	}
	sheetStyle.setBottomBorderColor(HSSFColor.BLACK.index);
	sheetStyle.setTopBorderColor(HSSFColor.BLACK.index);
	sheetStyle.setLeftBorderColor(HSSFColor.BLACK.index);
	sheetStyle.setRightBorderColor(HSSFColor.BLACK.index);
	
	sheetStyle.setBorderBottom(CellStyle.BORDER_THIN);
	sheetStyle.setBorderTop(CellStyle.BORDER_THIN);
	sheetStyle.setBorderLeft(CellStyle.BORDER_THIN);
	sheetStyle.setBorderRight(CellStyle.BORDER_THIN);
	
	sheetStyle.setAlignment(CellStyle.ALIGN_LEFT);
//		sheetStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
	//sheetStyle.setWrapText(true);
	return sheetStyle;
}
	
	public static String exportValue(FacesContext context, UIComponent uiComponent) {

		if (uiComponent instanceof HtmlCommandLink) {
			HtmlCommandLink link = (HtmlCommandLink) uiComponent;
			Object value = link.getValue();

			if (value != null) {
				return String.valueOf(value);
			} else {
				// export first value holder
				for (UIComponent child : link.getChildren()) {
					if (child instanceof ValueHolder) {
						return exportValue(context, child);
					}
				}

				return "";
			}
		} else if (uiComponent instanceof ValueHolder) {

			if (uiComponent instanceof EditableValueHolder) {
				Object submittedValue = ((EditableValueHolder) uiComponent)
						.getSubmittedValue();
				if (submittedValue != null) {
					return submittedValue.toString();
				}
			}

			ValueHolder valueHolder = (ValueHolder) uiComponent;
			Object value = valueHolder.getValue();
			if (value == null)
				return "";

			// first ask the converter
			if (valueHolder.getConverter() != null) {
				return valueHolder.getConverter().getAsString(context,
						uiComponent, value);
			}
			// Try to guess
			else {
				if (uiComponent instanceof HtmlOutputText){
					return ((HtmlOutputText) uiComponent).getValue().toString();
				}
				ValueExpression expr = uiComponent.getValueExpression("value");
				
				if (expr != null) {
					Class<?> valueType = expr.getType(context.getELContext());
					if (valueType != null) {
						Converter converterForType = context.getApplication()
								.createConverter(valueType);

						if (converterForType != null)
							return converterForType.getAsString(context,
									uiComponent, value);
					}
				}
			}

			// No converter found just return the value as string
			return value.toString();
		} else {
			// This would get the plain texts on UIInstructions when using
			// Facelets
			String value = uiComponent.toString();

			if (value != null)
				return value.trim();
			else
				return "";
		}
	}
	
	public static boolean isExcelFile(UploadedFile uploadFile){
		if(uploadFile==null) return false;
		String fileName = uploadFile.getFileName();
		if(fileName ==null) return false;
		String suffixName = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length());
		if("xls".equalsIgnoreCase(suffixName)||"xlsx".equalsIgnoreCase(suffixName)){
			return true;
		}else{
			return false;
		}	
	}
}
