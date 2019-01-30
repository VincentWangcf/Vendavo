package com.avnet.emasia.webquote.web.catalogsearch.helper;

import java.text.DateFormat;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.avnet.emasia.webquote.entity.ExcelReport;
import com.avnet.emasia.webquote.entity.QuantityBreakPrice;
import com.avnet.emasia.webquote.helper.TransferHelper;
import com.avnet.emasia.webquote.quote.vo.CatalogSearchResult;
import com.avnet.emasia.webquote.vo.PricerInfo;
import com.avnet.emasia.webquote.web.maintenance.DownLoadStrategy;
import com.avnet.emasia.webquote.web.quote.constant.QuoteConstant;

public class CatalogSearchResultDownLoadStrategy extends DownLoadStrategy {
	static final Logger LOG = Logger.getLogger(CatalogSearchResultDownLoadStrategy.class.getSimpleName());
	private ExcelReport excelReport;

	private static final long serialVersionUID = 1826810634921438019L;

	/*
	 * public CatalogSearchResultDownLoadStrategy(ExcelReport excelReport) {
	 * this.excelReport = excelReport; }
	 */
	private static String convertToYYYYMMDD(Date date) {// convert m/d/yy to
														// dd/MM/yyyy format

		String returnStr = "";
		try {
			DateFormat format1 = new SimpleDateFormat("yyyy/MM/dd");
			returnStr = format1.format(date);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnStr;
	}

	private static String convertToDDMMYYYY(Date date) {// convert m/d/yy to
		// dd/MM/yyyy format
		String returnStr = "";
		try {
		DateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
		returnStr = format1.format(date);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnStr;
	}
	
	private static String convertBigDcToStr(java.math.BigDecimal bigDecimal) {

		if (bigDecimal == null) {
			return "";
		}
		return bigDecimal.toString();

	}

	@Override
	public void setDateTosheet(XSSFWorkbook wb, List<?> data) {
		XSSFCell cell = null;
		XSSFSheet sheet = wb.getSheetAt(0);
		XSSFCellStyle cellStyle=wb.createCellStyle();     
		cellStyle.setWrapText(true);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		int HeaderRowCnt =  QuoteConstant.CATALOG_RESULT_TEMPLATE_HEADER_ROW_NUMBER;
		if (data != null) {
			for (int j = 0; j < data.size(); j++) {
				XSSFRow dataRow = sheet.createRow(j + HeaderRowCnt);
				PricerInfo obj = (PricerInfo) data.get(j);
			 
				/** MFR */
				cell = dataRow.createCell(0);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if (obj.getMfr() != null) {
					cell.setCellValue(obj.getMfr());
				}
				/**Requested P/N */
				cell = dataRow.createCell(1);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if (obj.getFullMfrPartNumber() != null) {
					cell.setCellValue(obj.getFullMfrPartNumber());
				}
				/** Price Type */
				cell = dataRow.createCell(2);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if (obj.getPricerType() != null) {
					cell.setCellValue(obj.getPricerType());
				}
				/** Required Qty */
				cell = dataRow.createCell(3);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				if (obj.getRequestedQty() != null) {
					cell.setCellValue(obj.getRequestedQty());
				}
				/** MPQ */
				cell = dataRow.createCell(4);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				if (obj.getMpq() != null) {
					cell.setCellValue(obj.getMpq());
				}
				/** MOQ */
				cell = dataRow.createCell(5);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				if (obj.getMoq() != null) {
					cell.setCellValue(obj.getMoq());
				}
				
				 
				List<QuantityBreakPrice> qbkList =obj.getOrderQties();
				/** Order Qty */
				cell = dataRow.createCell(6);
				//cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellStyle(cellStyle);
				if (!qbkList.isEmpty()) {
					cell.setCellValue(TransferHelper.convertQrderQtyForExcel(qbkList, true));
				}
				/** RFQ Curr */
				cell = dataRow.createCell(7);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if (obj.getRfqCurr() != null) {
					cell.setCellValue(obj.getRfqCurr());
				}
				
				/** Sales Cost */
				cell = dataRow.createCell(8);
				//cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellStyle(cellStyle);
				if (!qbkList.isEmpty()) {
					cell.setCellValue(TransferHelper.convertSalesCostForExcel(qbkList, true));
				}
				/** Suggested Resale */
				cell = dataRow.createCell(9);
				//cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellStyle(cellStyle);
				if (!qbkList.isEmpty()) {
					cell.setCellValue(TransferHelper.convertSuggestedResaleForExcel(qbkList, true));
				}
				/** Normal Sell Price */
				cell = dataRow.createCell(10);
				//cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellStyle(cellStyle);
				 if(obj.getMinSell() !=null &&obj.getMinSell()>0&&obj.getDisRfqCurrMinSell() !=null){
					cell.setCellValue(String.valueOf(obj.getDisRfqCurrMinSell()));
				}else if (!qbkList.isEmpty()) {
					cell.setCellValue(TransferHelper.convertNormalSellPriceForExcel(qbkList, true));
				}
				/** Cost Indicator */
				cell = dataRow.createCell(11);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if (obj.getCostIndicator() != null) {
					cell.setCellValue(obj.getCostIndicator());
				}
				/** Quotation Effective Date */
				cell = dataRow.createCell(12);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if (obj.getQuotationEffectiveDate() != null) {
					cell.setCellValue(convertToDDMMYYYY(obj.getQuotationEffectiveDate()));
				}
				/** Quote Expiry Date */
				cell = dataRow.createCell(13);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if (obj.getQuoteExpiryDate() != null) {
					cell.setCellValue(convertToDDMMYYYY(obj.getQuoteExpiryDate()));
				}
				/** Lead Time */
				cell = dataRow.createCell(14);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if (obj.getLeadTime() != null) {
					cell.setCellValue(obj.getLeadTime());
				}
				/** Description */
				cell = dataRow.createCell(15);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if (obj.getDescription() != null) {
					cell.setCellValue(obj.getDescription());
				}
				/** Cancellation Window */
				cell = dataRow.createCell(16);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				if (obj.getCancellationWindow() != null) {
					cell.setCellValue(obj.getCancellationWindow());
				}
				/** Rescheduling Window */
				cell = dataRow.createCell(17);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				if (obj.getReschedulingWindow() != null) {
					cell.setCellValue(obj.getReschedulingWindow());
				}
				/** Sold-To-Code */
				cell = dataRow.createCell(18);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if (obj.getSoldToCustomerNumber() != null) {
					cell.setCellValue(obj.getSoldToCustomerNumber());
				}
				
				/** Sold-To-Party */
				cell = dataRow.createCell(19);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if (obj.getSoldToCustomerName() != null) {
					cell.setCellValue(obj.getSoldToCustomerName());
				}
				
				/** End Customer Code */
				cell = dataRow.createCell(20);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if (obj.getEndToCustomerNumber() != null) {
					cell.setCellValue(obj.getEndToCustomerNumber());
				}
				
				/** End Customer Name */
				cell = dataRow.createCell(21);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if (obj.getEndToCustomerName() != null) {
					cell.setCellValue(obj.getEndToCustomerName());
				}
				/**Region */
				cell = dataRow.createCell(22);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if (obj.getBizUnit() != null) {
					cell.setCellValue(obj.getBizUnit());
				}
				
			}
		}
	}
}
