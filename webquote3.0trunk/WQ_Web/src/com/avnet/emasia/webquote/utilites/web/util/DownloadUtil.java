package com.avnet.emasia.webquote.utilites.web.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.ExcelReport;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.exception.WebQuoteException;
import com.avnet.emasia.webquote.poi.DefaultExcelReportFormat;
import com.avnet.emasia.webquote.poi.ExcelGenerator;
import com.avnet.emasia.webquote.poi.ExcelReportFormat;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.quote.vo.WorkingPlatformItemVO;
import com.avnet.emasia.webquote.reports.vo.RORReportVo;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.vo.DrProjectVO;
import com.avnet.emasia.webquote.web.quote.constant.QuoteConstant;

public class DownloadUtil {
	static Logger log= Logger.getLogger(DownloadUtil.class.getSimpleName());

	public static String getMimeType(String fileName){
		if(fileName != null)
		{
			String[] filenames = fileName.split("\\.");
			if(filenames.length > 1)
			{
				fileName = filenames[filenames.length-1];
				if(fileName.equalsIgnoreCase("TXT"))
					return "text/plain; charset=UTF-8";
				else if(fileName.equalsIgnoreCase("XLS"))
					return "application/vnd.ms-excel";
				else if(fileName.equalsIgnoreCase("XLSX"))
					return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
				else if(fileName.equalsIgnoreCase("ZIP"))			
					return "application/zip";
				else if(fileName.equalsIgnoreCase("DOC"))
					return "application/msword";
				else if(fileName.equalsIgnoreCase("DOCX"))
					return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
				else if(fileName.equalsIgnoreCase("MSG"))
					return "application/vnd.ms-outlook";
				else if(fileName.equalsIgnoreCase("PDF"))
					return "application/pdf";
			}
		}
		return "application/octet-stream";
	}
	
	public static String getCustomerFullName(Customer customer){
		if(customer != null){
			/*String customerName = customer.getCustomerName1();
			if(customerName != null && customer.getCustomerName2() != null){
				customerName += customer.getCustomerName2();
			}*/
			
			String customerName = customer.getCustomerFullName();
			return customerName;
		}
		return null;
	}
	
	public static List<List<String>> convertDrProjectToReportVO(List<DrProjectVO> drProjects){
		if(drProjects == null)
			return null;		

		List<List<String>> list = new ArrayList<List<String>>();
		for(DrProjectVO drProject : drProjects){
			List<String> subList = new ArrayList<String>();
			if(drProject.getDrNedaItem() != null
				&& drProject.getDrNedaItem().getDrNedaHeader() != null
				&& drProject.getDrNedaItem().getDrNedaHeader().getDrProjectHeader() != null
				&& drProject.getDrNedaItem().getDrNedaHeader().getDrProjectHeader().getSalesOrgBean() != null){
				subList.add(drProject.getDrNedaItem().getDrNedaHeader().getDrProjectHeader().getSalesOrgBean().getName());
			} else {
				subList.add(null);
			}
			if(drProject.getDrNedaItem() != null
				&& drProject.getDrNedaItem().getDrNedaHeader() != null){
				subList.add(drProject.getDrNedaItem().getDrNumber());				
			} else {
				subList.add(null);
			}
			if(drProject.getDrNedaItem() != null
				&& drProject.getDrNedaItem().getDrNedaHeader() != null
				&& drProject.getDrNedaItem().getDrNedaHeader().getDrProjectHeader() != null){
				subList.add(drProject.getDrNedaItem().getDrNedaHeader().getDrProjectHeader().getProjectName());				
			} else {
				subList.add(null);				
			}
			if(drProject.getDrNedaItem() != null
				&& drProject.getDrNedaItem().getDrNedaHeader() != null
				&& drProject.getDrNedaItem().getDrNedaHeader().getDrProjectHeader() != null){
				subList.add(drProject.getDrNedaItem().getDrNedaHeader().getDrProjectHeader().getProjectDescription());			
			} else {
				subList.add(null);				
			}
			if(drProject.getDrNedaItem() != null){
				subList.add(drProject.getDrNedaItem().getPartMask());				
			} else {
				subList.add(null);				
			}
			if(drProject.getDrNedaItem() != null
					&& drProject.getDrNedaItem().getDrNedaHeader() != null
					&& drProject.getDrNedaItem().getDrNedaHeader().getDrProjectHeader() != null
					&& drProject.getDrNedaItem().getDrNedaHeader().getDrProjectHeader().getProjectId() > 0){
				subList.add(String.valueOf(drProject.getDrNedaItem().getDrNedaHeader().getDrProjectHeader().getProjectId()));				
			} else {
				subList.add(null);				
			}
			if(drProject.getDrNedaItem() != null
					&& drProject.getDrNedaItem().getId() != null
					&& drProject.getDrNedaItem().getId().getNedaId() > 0){
				subList.add(String.valueOf(drProject.getDrNedaItem().getId().getNedaId()));
			} else {
				subList.add(null);				
			}
			if(drProject.getDrNedaItem() != null
					&& drProject.getDrNedaItem().getCreatedOn() != null){
				subList.add(QuoteUtil.convertDateToString(drProject.getDrNedaItem().getCreatedOn()));
			} else {
				subList.add(null);				
			}
			if(drProject.getDrNedaItem() != null
					&& drProject.getDrNedaItem().getDrNedaHeader() != null){
				subList.add(drProject.getDrNedaItem().getFaeName());
			} else {
				subList.add(null);				
			}
			if(drProject.getDrNedaItem() != null
					&& drProject.getDrNedaItem().getDrNedaHeader() != null
					&& drProject.getDrNedaItem().getDrNedaHeader().getDrProjectHeader() != null
					&& drProject.getDrNedaItem().getDrNedaHeader().getDrProjectHeader().getGroupProjectId() != null){
				subList.add(drProject.getDrNedaItem().getDrNedaHeader().getDrProjectHeader().getGroupProjectId().toString());
			} else {
				subList.add(null);				
			}
			if(drProject.getDrNedaItem() != null
					&& drProject.getDrNedaItem().getDrNedaHeader() != null){
				subList.add(drProject.getDrNedaItem().getSupplierName());
			} else {
				subList.add(null);				
			}
			if(drProject.getDrNedaItem() != null){
				subList.add(drProject.getDrNedaItem().getCoreId());
			} else {
				subList.add(null);				
			}
			if(drProject.getDrNedaItem() != null
					&& drProject.getDrNedaItem().getSuggestedPrice() != null){
				subList.add(drProject.getDrNedaItem().getSuggestedPrice().toString());
			} else {
				subList.add(null);				
			}
			if(drProject.getDrNedaItem() != null
					&& drProject.getDrNedaItem().getQtyPerBoard() != null){					
				subList.add(drProject.getDrNedaItem().getQtyPerBoard().toString());
			} else {
				subList.add(null);				
			}
			if(drProject.getDrNedaItem() != null
					&& drProject.getDrNedaItem().getDrNedaHeader() != null
					&& drProject.getDrNedaItem().getDrNedaHeader().getDrProjectHeader() != null
					&& drProject.getDrNedaItem().getDrNedaHeader().getProductionDate() != null){
				subList.add(QuoteUtil.convertDateToString(drProject.getDrNedaItem().getDrNedaHeader().getProductionDate()));
			} else {
				subList.add(null);				
			}
			if(drProject.getDrNedaItem() != null
					&& drProject.getDrNedaItem().getDrNedaHeader() != null
					&& drProject.getDrNedaItem().getDrNedaHeader().getAssemblyQty() != null){
				subList.add(drProject.getDrNedaItem().getDrNedaHeader().getAssemblyQty().toString());
			} else {
				subList.add(null);				
			}			

			
			//deleted by Damon
			/*if(drProject.getDrNedaItem() != null){
				subList.add(drProject.getDrNedaItem().getAdditionalInfo3());
			} else {
				subList.add(null);				
			}*/
			list.add(subList);			
		}		
		return list;
	}
	
	public static List<List<String>> convertQuoteItemToReportVO(List<QuoteItem> items,boolean isqco){
		List<List<String>> list = new ArrayList<List<String>>();
		for(QuoteItem item : items){
			List<String> subList = new ArrayList<String>();
			//enhance the logic for keep the excel records same with the page display. by Damonchen@20180312
			if(item.getQuote().getFormNumber()!=null && item.getQuote().getFormNumber().startsWith("DF"))
			{
				subList.add("");
			}
			else
			{
				subList.add(item.getQuote().getFormNumber());
			}
			if(QuoteSBConstant.RFQ_STATUS_AQ.equals(item.getStatus()) || isqco){
				subList.add(item.getQuoteNumber());
			}else{
				subList.add("");
			}
		//end  by Damonchen@20180312
//			if(item.getRequestedMaterial() != null){
//				subList.add(item.getRequestedMaterial().getManufacturer().getName());
//				subList.add(item.getRequestedMaterial().getFullMfrPartNumber());
			if(item.getRequestedPartNumber() != null)
			{
			subList.add(item.getRequestedMfr().getName());
			subList.add(item.getRequestedPartNumber());
			} 
//			else {
//				subList.add(item.getInvalidMfr());
//				subList.add(item.getInvalidPartNumber());				
//			}
			subList.add(item.getQuotedMaterial()==null?null:item.getQuotedMaterial().getFullMfrPartNumber());
			subList.add(item.getRequestedQty()==null?null:item.getRequestedQty().toString());
			subList.add(item.getEau()==null?null:item.getEau().toString());
			subList.add(item.isSalesCostFlag() ==true?"Yes":"No");
			subList.add(item.getRfqCurr()==null?null:item.getRfqCurr().name());
			subList.add(item.getTargetResale()==null?null:item.getTargetResale().toString());
			subList.add(item.convertToRfqValue(item.getSalesCost()) ==null?null:item.convertToRfqValue(item.getSalesCost()).toString());
			subList.add(item.convertToRfqValue(item.getSuggestedResale()) ==null?null:item.convertToRfqValue(item.getSuggestedResale()).toString());
			subList.add(item.convertToRfqValueWithDouble(item.getQuotedPrice()) ==null?null:item.convertToRfqValueWithDouble(item.getQuotedPrice()).toString());
			subList.add(item.getStatus());
			subList.add(item.getSprFlag()?QuoteConstant.OPTION_YES:QuoteConstant.OPTION_NO);
			subList.add(item.getStage());
			list.add(subList);
		}
		return list;
	}
	
	public static StreamedContent generateExcelForWorkingPlatform(List<String> headers,
			List<List<String>> data, String filename) {
		StreamedContent file = null;
		try {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			XSSFWorkbook workbook = new XSSFWorkbook();
			DataFormat format = workbook.createDataFormat();
			CellStyle style = workbook.createCellStyle();
			style.setDataFormat(format.getFormat("@"));
			XSSFSheet worksheet = workbook.createSheet();
			XSSFRow headerRow = worksheet.createRow(0);
			
			if (headers != null) {
				for (int i = 0; i < headers.size(); i++) {
					XSSFCell cell = headerRow.createCell(i);
					cell.setCellValue(headers.get(i));
				}
			}
			if (data != null) {
				for (int j = 0; j < data.size(); j++) {
					XSSFRow dataRow = worksheet.createRow(j + 1);
					List<String> subList = data.get(j);
					for (int i = 0; i < subList.size(); i++) {
						XSSFCell cell = dataRow.createCell(i);
						cell.setCellType(Cell.CELL_TYPE_STRING);
						cell.setCellValue(subList.get(i));
						cell.setCellStyle(style);
					}
				}
			}
			workbook.write(byteOut);
			byteOut.flush();

			ByteArrayInputStream byteIn = new ByteArrayInputStream(
					byteOut.toByteArray());
			file = new DefaultStreamedContent(byteIn,
					"application/vnd.ms-excel", filename);

			byteIn.close();
			byteOut.close();
		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE, "exception in generating excel file "+filename+""+e.getMessage(), e);
		} catch (IOException e) {
			log.log(Level.SEVERE, "exception in generating excel file "+filename+""+e.getMessage(), e);
		}

		return file;

	}
	
	public static StreamedContent generateExcel(List<String> headers, List<List<String>> data, String filename){
		StreamedContent file = null;
		try {
			ByteArrayOutputStream  byteOut = new ByteArrayOutputStream ();
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet worksheet = workbook.createSheet();

			XSSFRow headerRow = worksheet.createRow(0);
			if(headers != null){
				for(int i=0; i<headers.size(); i++){
					XSSFCell cell = headerRow.createCell(i);
					cell.setCellValue(headers.get(i));				
				}
			}
			if(data != null){
				for(int j=0; j<data.size(); j++){
					XSSFRow dataRow = worksheet.createRow(j+1);
					List<String> subList = data.get(j);
					for(int i=0; i<subList.size(); i++){
						XSSFCell cell = dataRow.createCell(i);
						cell.setCellType(Cell.CELL_TYPE_STRING);
						cell.setCellValue(subList.get(i));				
					}				
				}
			}
			workbook.write(byteOut);
			byteOut.flush();
			
			ByteArrayInputStream  byteIn = new ByteArrayInputStream(byteOut.toByteArray());
			file = new DefaultStreamedContent(byteIn, "application/vnd.ms-excel", filename);			
			
			
			byteIn.close();
			byteOut.close();
		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE, "exception in generating excel file "+filename+""+e.getMessage(), e);
		} catch (IOException e) {
			log.log(Level.SEVERE, "exception in generating excel file "+filename+""+e.getMessage(), e);
		}
		
		return file;

	}

	public static List<List<String>> convertWorkingPlatformTable(List<WorkingPlatformItemVO> workingPlatformItems, BizUnit bizUnit) {
		if(workingPlatformItems == null)
			return null;
		List<List<String>> list = new ArrayList<List<String>>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		for(WorkingPlatformItemVO item : workingPlatformItems){
			List<String> subList = new ArrayList<String>();
			
			subList.add(item.getQuoteItem().getQuote()==null?null:item.getQuoteItem().getAlternativeQuoteNumber());
			subList.add(item.getQuoteItem().getRevertVersion());
			subList.add(item.getQuoteItem().getFirstRfqCode());
			//subList.add(item.getQuoteItem().getQuote()==null?null:item.getQuoteItem().getStage());
			subList.add(String.valueOf(item.getQuoteItem().getPendingDay()));
			subList.add(item.getQuoteItem().getStatus());			
			subList.add(item.getQuoteItem().getValidFlag()==null?QuoteConstant.OPTION_NO:item.getQuoteItem().getValidFlag().booleanValue()?QuoteConstant.OPTION_YES:QuoteConstant.OPTION_NO);
			subList.add(item.getQuoteItem().getResaleIndicator());
			subList.add(item.getQuoteItem().getQuotedPrice()==null?null:item.getQuoteItem().getQuotedPrice().toString());
			subList.add(item.getQuoteItem().getQuotedQty()==null?null:item.getQuoteItem().getQuotedQty().toString());
			subList.add(item.getQuoteItem().getQtyIndicator());
			//subList.add(item.getQuoteItem().getPmoq());
			//for issue 1541 . blank the PMOQ value
			//subList.add("");
			
			subList.add(item.getQuoteItem().getAllocationFlag()==null?QuoteConstant.OPTION_NO:item.getQuoteItem().getAllocationFlag().booleanValue()?QuoteConstant.OPTION_YES:QuoteConstant.OPTION_NO);
			subList.add(item.getQuoteItem().getAqcc());
			subList.add(item.getQuotedPartNumber()==null?item.getQuoteItem().getQuotedPartNumber():item.getQuotedPartNumber()); //fix INC0031247 June Guo 20140926 when item.getQuotedPartNumber() is null, then use item. quote part number
			subList.add(item.getQuoteItem().getMultiUsageFlag()==null?QuoteConstant.OPTION_NO:item.getQuoteItem().getMultiUsageFlag().booleanValue()?QuoteConstant.OPTION_YES:QuoteConstant.OPTION_NO);
			subList.add(item.getQuoteItem().getVendorQuoteNumber());
			subList.add(item.getQuoteItem().getInternalComment());
			subList.add(item.getQuoteItem().getInternalTransferComment());
			subList.add(item.getQuoteItem().getVendorDebitNumber());
			subList.add(item.getQuoteItem().getVendorQuoteQty()==null?null:item.getQuoteItem().getVendorQuoteQty().toString());
			subList.add(item.getQuoteItem().getPriceValidity()==null?null:item.getQuoteItem().getPriceValidity().toString());
			subList.add(item.getQuoteItem().getShipmentValidity()==null?null:sdf.format(item.getQuoteItem().getShipmentValidity()));
			subList.add(item.getQuoteItem().getPriceListRemarks1());
			subList.add(item.getQuoteItem().getPriceListRemarks2());
			subList.add(item.getQuoteItem().getPriceListRemarks3());
			subList.add(item.getQuoteItem().getPriceListRemarks4());
			subList.add(item.getQuoteItem().getProductGroup2()==null?null:item.getQuoteItem().getProductGroup2().getName());
			subList.add(item.getQuoteItem().getDescription());
			subList.add(item.getQuoteItem().getCostIndicator());
			subList.add(item.getQuoteItem().getCost()==null?null:item.getQuoteItem().getCost().toString());
			subList.add(item.getQuoteItem().getBottomPrice()==null?null:item.getQuoteItem().getBottomPrice().toString());
			subList.add(item.getQuoteItem().getMinSellPrice()==null?null:item.getQuoteItem().getMinSellPrice().toString());
			//Material restructure and quote_item delinkage andy 
			subList.add(item.getQuoteItem().getProgramType()==null?null:item.getQuoteItem().getProgramType());
			subList.add(item.getQbpStr());
			subList.add(item.getQuoteItem().getLeadTime()==null?null:item.getQuoteItem().getLeadTime().toString());
			subList.add(item.getQuoteItem().getMpq()==null?null:item.getQuoteItem().getMpq().toString());
			subList.add(item.getQuoteItem().getMoq()==null?null:item.getQuoteItem().getMoq().toString());
			subList.add(item.getQuoteItem().getMov()==null?null:item.getQuoteItem().getMov().toString());
			subList.add(item.getQuoteItem().getQuotationEffectiveDate()==null?null:sdf.format(item.getQuoteItem().getQuotationEffectiveDate()));
			subList.add(item.getQuoteItem().getTermsAndConditions());
			subList.add(item.getQuoteItem().getRescheduleWindow()==null?null:item.getQuoteItem().getRescheduleWindow().toString());
			subList.add(item.getQuoteItem().getCancellationWindow()==null?null:item.getQuoteItem().getCancellationWindow().toString());
//			subList.add(item.getQuoteItem().getRequestedMaterial()==null?null:item.getQuoteItem().getRequestedMaterial().getManufacturer().getName());
//			subList.add(item.getQuoteItem().getRequestedMaterial()==null?null:item.getQuoteItem().getRequestedMaterial().getFullMfrPartNumber());
			subList.add(item.getQuoteItem().getRequestedPartNumber()==null?null:item.getQuoteItem().getRequestedMfr().getName());
			subList.add(item.getQuoteItem().getRequestedPartNumber()==null?null:item.getQuoteItem().getRequestedPartNumber());
			subList.add(item.getQuoteItem().getRequestedQty()==null?null:item.getQuoteItem().getRequestedQty().toString());
			subList.add(item.getQuoteItem().getEau()==null?null:item.getQuoteItem().getEau().toString());
			subList.add(item.getQuoteItem().getTargetResale()==null?null:item.getQuoteItem().getTargetResale().toString());
			subList.add(item.getQuoteItem().getQuoteType());
			
			subList.add(item.getQuoteItem().getSubmissionDate()==null?null:sdf.format(item.getQuoteItem().getSubmissionDate()));
			subList.add(item.getQuoteItem().getQuote().getYourReference());
			subList.add(item.getQuoteItem().getQuote().getSales().getEmployeeId());
			subList.add(item.getQuoteItem().getQuote().getSales()==null?null:item.getQuoteItem().getQuote().getSales().getName());
			
			if(item.getQuoteItem().getQuote().getSales() != null && item.getQuoteItem().getQuote().getSales().getTeam() != null){
				subList.add(item.getQuoteItem().getQuote().getSales().getTeam().getName());
			} else {
				subList.add(null);
			}
			
			subList.add(item.getQuoteItem().getQuote().getSoldToCustomerName());
			
			if(item.getQuoteItem().getSoldToCustomer() != null)
				subList.add(item.getQuoteItem().getSoldToCustomer().getCustomerNumber());
			else
				subList.add(null);			
			
			if(item.getQuoteItem().getSoldToCustomer() != null)
				subList.add(item.getQuoteItem().getQuote().getCustomerType());
			else
				subList.add(null);
			
			subList.add(item.getQuoteItem().getProjectName());
			subList.add(item.getQuoteItem().getApplication());
			subList.add(item.getQuoteItem().getEnquirySegment());
			subList.add(item.getQuoteItem().getDesignLocation());
			subList.add(item.getQuoteItem().getDesignInCat());
			
			subList.add(item.getQuoteItem().getDrmsNumber()==null?null:item.getQuoteItem().getDrmsNumber().toString());
			//remove the old fields.
			//subList.add(item.getQuoteItem().getBmtCheckedFlag()==null?QuoteConstant.OPTION_NO:item.getQuoteItem().getBmtCheckedFlag().booleanValue()?QuoteConstant.OPTION_YES:QuoteConstant.OPTION_NO);
			
			if(item.getQuoteItem().getEndCustomer() != null)
				subList.add(getCustomerFullName(item.getQuoteItem().getEndCustomer()));
			else if(item.getQuoteItem().getEndCustomerName() != null)
				subList.add(item.getQuoteItem().getEndCustomerName());
			else
				subList.add(null);
			
			if(item.getQuoteItem().getShipToCustomer() != null)
				subList.add(item.getQuoteItem().getShipToCustomer().getCustomerNumber());
			else
				subList.add(null);

			if(item.getQuoteItem().getShipToCustomer() != null)
				subList.add(getCustomerFullName(item.getQuoteItem().getShipToCustomer()));
			else if(item.getQuoteItem().getShipToCustomerName() != null)
				subList.add(item.getQuoteItem().getShipToCustomerName());
			else
				subList.add(null);
			
			if(item.getQuoteItem().getEndCustomer() != null)
				subList.add(item.getQuoteItem().getEndCustomer().getCustomerNumber());
			else
				subList.add(null);				
			
			subList.add(QuoteConstant.OPTION_NO);
			subList.add(item.getQuoteItem().getLoaFlag()==null?QuoteConstant.OPTION_NO:item.getQuoteItem().getLoaFlag().booleanValue()?QuoteConstant.OPTION_YES:QuoteConstant.OPTION_NO);
			
			if(
				(item.getQuoteItem().getItemAttachmentFlag() != null && item.getQuoteItem().getItemAttachmentFlag().booleanValue())
				|| (item.getQuoteItem().getPmAttachmentFlag() != null && item.getQuoteItem().getPmAttachmentFlag().booleanValue())
				|| (item.getQuoteItem().getQuote().getFormAttachmentFlag() != null && item.getQuoteItem().getQuote().getFormAttachmentFlag().booleanValue())
			){			
				subList.add(QuoteConstant.OPTION_YES);
			} else {
				subList.add(QuoteConstant.OPTION_NO);				
			}
			
			subList.add(item.getQuoteItem().getQuote().getRemarks());
			subList.add(item.getQuoteItem().getCompetitorInformation());
			subList.add(item.getQuoteItem().getSupplierDrNumber());
			subList.add(item.getQuoteItem().getPpSchedule());
			subList.add(item.getQuoteItem().getMpSchedule());
			subList.add(item.getQuoteItem().getRemarks());
			subList.add(item.getQuoteItem().getSprFlag()==null?QuoteConstant.OPTION_NO:item.getQuoteItem().getSprFlag().booleanValue()?QuoteConstant.OPTION_YES:QuoteConstant.OPTION_NO);
			
			if(
				item.getQuoteItem().getRefreshAttachmentFlag() != null && item.getQuoteItem().getRefreshAttachmentFlag().booleanValue()
			){			
				subList.add(QuoteConstant.OPTION_YES);
			} else {
				subList.add(QuoteConstant.OPTION_NO);				
			}
			
			subList.add(item.getQuoteItem().getReasonForRefresh());
			subList.add(item.getQuoteItem().getQuote()==null?null:item.getQuoteItem().getQuote().getFormNumber());
			subList.add(item.getQuoteItem().getQuote().getCopyToCS());
			subList.add(item.getSoldToChineseNameStr());
			//Remove the CC columns for issue 1321
			//subList.add(item.getDescriptionStr());
			subList.add("A"+String.valueOf(item.getQuoteItem().getId()));
			list.add(subList);
		}
		return list;

	}

	public static List<List<String>> convertRorReportResult(List<RORReportVo> rorReportVos) {
		if(rorReportVos == null)
			return null;
		List<List<String>> list = new ArrayList<List<String>>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		for(RORReportVo item : rorReportVos){
			List<String> subList = new ArrayList<String>();
			
			subList.add(item.getCustomer());
			subList.add(item.getEndCustomer());
			subList.add(item.getEmployeeName());
			subList.add(item.getSalesmanCode());
			subList.add(item.getTeam());
			subList.add(item.getItemRemarks());
			subList.add(item.getMfr());
			subList.add(item.getRequestedPartNumber());
			subList.add(item.getQuotedPartNumber());
			subList.add(item.getRequiredQty());
			subList.add(item.getTargetResales());
			subList.add(item.getYourReference());
			subList.add(item.getStage());
			subList.add(item.getUploadTimeStr());
			subList.add(item.getFormNumber());
			subList.add(item.getCopyToCs());
			list.add(subList);
		}
		return list;

	}
	
	public static void outputExcelFile(ExcelReport excelReport,String templatePath,List resultList,FacesContext facesContext) throws WebQuoteException {
		outputExcelFile( excelReport, templatePath, resultList,new DefaultExcelReportFormat() , facesContext);
	}
	
	public static void outputExcelFile(ExcelReport excelReport,String templatePath,List resultList,ExcelReportFormat reportFormat,FacesContext facesContext) throws WebQuoteException {
		String fileName = excelReport.getReportName() + "_" + System.currentTimeMillis() + ".xlsx";
		HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
		response.setContentType("application/vnd.ms-excel");
		response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
		
		try {
			SXSSFWorkbook workbook = ExcelGenerator.generateExcelTemplate(excelReport, "example", templatePath);
			workbook = ExcelGenerator.generateExcelFile(workbook, resultList, excelReport,reportFormat);
			workbook.write(response.getOutputStream());
			workbook.dispose();
			facesContext.responseComplete();
			facesContext.renderResponse();
			
		} catch (IOException e) {
			log.log(Level.SEVERE, "run outputExcelFile method ,throw out error:"+e.getMessage(), e);
			throw new WebQuoteException(e);
		}
	}

}
