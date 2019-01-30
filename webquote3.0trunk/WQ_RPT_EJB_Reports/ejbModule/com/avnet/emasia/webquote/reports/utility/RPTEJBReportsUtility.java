package com.avnet.emasia.webquote.reports.utility;

import java.util.List;
import java.util.logging.Logger;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;

import com.avnet.emasia.webquote.reports.offlinereport.DownloadUtil;
import com.avnet.emasia.webquote.reports.offlinereport.OfflineReportConstants;
import com.avnet.emasia.webquote.reports.offlinereport.vo.MyQuoteResultBean;
import com.avnet.emasia.webquote.reports.offlinereport.vo.PartMasterResultBean;

public class RPTEJBReportsUtility {
	private static final Logger LOGGER = Logger.getLogger(RPTEJBReportsUtility.class.getName());
	private static final String QC_BEAN = "qc";
	private static final String FORMAT_SHORT_DATE = "dd/MM/yyyy";
	private static final String FORMAT_LONG_DATE = "dd/MM/yyyy hh:mm:ss";
	private static final String SALE_BEAN = "sales";
	private static final String CS_BEAN = "cs";
	private static final String MM_BEAN = "mm";
	private static final String PM_BEAN = "pm";
	private static final String BMT_BEAN = "bmt";

	public static HSSFWorkbook generateExcelFile(final String bizUnitNam, final List resultList, final String[] templateName,
			final	String[] parameterName, final String sheetName, final String reportName, final String role, final String flowName) {
		String[] templateArray = templateName;
		LOGGER.fine("call generateExcelFile");
		HSSFWorkbook workbook = DownloadUtil.buildWorkBook();
		HSSFSheet sheet = DownloadUtil.buildSheet(workbook, sheetName);

		HSSFCellStyle intStyle = workbook.createCellStyle();
		HSSFCellStyle doubleStyle = workbook.createCellStyle();

		CreationHelper shortDateHelper = workbook.getCreationHelper();
		HSSFCellStyle shortDateStyle = workbook.createCellStyle();
		shortDateStyle.setDataFormat(shortDateHelper.createDataFormat().getFormat(FORMAT_SHORT_DATE));

		HSSFCellStyle longDateStyle = workbook.createCellStyle();
		longDateStyle.setDataFormat(shortDateHelper.createDataFormat().getFormat(FORMAT_LONG_DATE));

		int rowIndex = 0;
		sheet = DownloadUtil.addSheetHeader(workbook, sheet, rowIndex, templateArray);

		int rowCount = resultList.size();
		rowIndex = rowIndex + 1;
		if (rowCount > 0) {
			if ("partMasterExcelFileGenerator".equals(flowName)) {
				for (int index = 0; index < rowCount; index++) {

					if (QC_BEAN.equalsIgnoreCase(role)) {
						PartMasterResultBean bean = (PartMasterResultBean) resultList.get(index);
						HSSFRow row = null;
						row = sheet.createRow(index + rowIndex);
						row = DownloadUtil.addRow(workbook, row, bean,
								OfflineReportConstants.QC_PART_MASTER_DOWNLOAD_TEMPLATE_HEADER_PARAMATERS,
								OfflineReportConstants.QC_PART_MASTER_DOWNLOAD_TEMPLATE_DATE_TYPE, intStyle,
								doubleStyle, shortDateStyle, longDateStyle);
					}
				}
			}
			if ("myQuoteExcelFileGenerator".equals(flowName)) {
				for (int index = 0; index < rowCount; index++) {
					if (SALE_BEAN.equalsIgnoreCase(role)) {
						MyQuoteResultBean bean = (MyQuoteResultBean) resultList.get(index);
						HSSFRow row = null;
						row = sheet.createRow(index + rowIndex);
						row = DownloadUtil.addRow(workbook, row, bean,
								OfflineReportConstants.SALE_DOWNLOAD_TEMPLATE_HEADER_PARAMATERS,
								OfflineReportConstants.SALE_DOWNLOAD_TEMPLATE_DATE_TYPE, intStyle, doubleStyle,
								shortDateStyle, longDateStyle);
					} else if (CS_BEAN.equalsIgnoreCase(role)) {
						MyQuoteResultBean bean = (MyQuoteResultBean) resultList.get(index);
						HSSFRow row = null;
						row = sheet.createRow(index + rowIndex);
						row = DownloadUtil.addRow(workbook, row, bean,
								OfflineReportConstants.CS_DOWNLOAD_TEMPLATE_HEADER_PARAMATERS,
								OfflineReportConstants.CS_DOWNLOAD_TEMPLATE_DATE_TYPE, intStyle, doubleStyle,
								shortDateStyle, longDateStyle);
					} else if (QC_BEAN.equalsIgnoreCase(role)) {
						MyQuoteResultBean bean = (MyQuoteResultBean) resultList.get(index);
						HSSFRow row = null;
						row = sheet.createRow(index + rowIndex);
						row = DownloadUtil.addRow(workbook, row, bean,
								OfflineReportConstants.QC_DOWNLOAD_TEMPLATE_HEADER_PARAMATERS,
								OfflineReportConstants.QC_DOWNLOAD_TEMPLATE_DATE_TYPE, intStyle, doubleStyle,
								shortDateStyle, longDateStyle);
					} else if (MM_BEAN.equalsIgnoreCase(role)) {
						MyQuoteResultBean bean = (MyQuoteResultBean) resultList.get(index);
						HSSFRow row = null;
						row = sheet.createRow(index + rowIndex);
						row = DownloadUtil.addRow(workbook, row, bean,
								OfflineReportConstants.MM_DOWNLOAD_TEMPLATE_HEADER_PARAMATERS,
								OfflineReportConstants.MM_DOWNLOAD_TEMPLATE_DATE_TYPE, intStyle, doubleStyle,
								shortDateStyle, longDateStyle);
					} else if (PM_BEAN.equalsIgnoreCase(role)) {
						MyQuoteResultBean bean = (MyQuoteResultBean) resultList.get(index);
						HSSFRow row = null;
						row = sheet.createRow(index + rowIndex);
						row = DownloadUtil.addRow(workbook, row, bean,
								OfflineReportConstants.PM_DOWNLOAD_TEMPLATE_HEADER_PARAMATERS,
								OfflineReportConstants.PM_DOWNLOAD_TEMPLATE_DATE_TYPE, intStyle, doubleStyle,
								shortDateStyle, longDateStyle);
					} else if (BMT_BEAN.equalsIgnoreCase(role)) {
						MyQuoteResultBean bean = (MyQuoteResultBean) resultList.get(index);
						HSSFRow row = null;
						row = sheet.createRow(index + rowIndex);
						row = DownloadUtil.addRow(workbook, row, bean,
								OfflineReportConstants.BMT_DOWNLOAD_TEMPLATE_HEADER_PARAMATERS,
								OfflineReportConstants.BMT_DOWNLOAD_TEMPLATE_DATE_TYPE, intStyle, doubleStyle,
								shortDateStyle, longDateStyle);
					}
				}
			}
		}
		if (workbook == null) {
			return null;
		} else
			return workbook;

	}

}