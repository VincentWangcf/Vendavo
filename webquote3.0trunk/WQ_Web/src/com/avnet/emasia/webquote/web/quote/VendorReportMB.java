package com.avnet.emasia.webquote.web.quote;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.format.CellDateFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.security.core.context.SecurityContextHolder;

import com.avnet.emasia.webquote.commodity.constant.PricerConstant;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.entity.VendorReport;
import com.avnet.emasia.webquote.quote.ejb.VendorReportSB;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.util.DeploymentConfiguration;
import com.avnet.emasia.webquote.utilites.web.util.DownloadUtil;
import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.web.quote.constant.QuoteConstant;
import com.avnet.emasia.webquote.web.security.WQUserDetails;

@ManagedBean
@SessionScoped
public class VendorReportMB extends CommonBean implements Serializable {
	static final Logger LOG =Logger.getLogger(VendorReportMB.class.getSimpleName());

	@EJB
	transient VendorReportSB vendorReportSB;
	
	private User user;
	
	private transient UploadedFile vendorReportExcel;
	
	private static final Logger LOGGER = Logger.getLogger(VendorReportMB.class.getName());	
	
	public VendorReportMB() {	
	}

	@PostConstruct
	private void postContruct(){
		user =((WQUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
		bizUnit = user.getDefaultBizUnit();
	}
	
	
	
	public void readExcelForm(){
		boolean cleared = false;
		try {
			if(vendorReportExcel != null){
				List<VendorReport> vendorReports = new ArrayList<VendorReport>();
				LOGGER.log(Level.INFO, "vendorReportExcel exist");
				InputStream file = new ByteArrayInputStream(vendorReportExcel.getContents()); 
				//FileInputStream file = (FileInputStream) vendorReportExcel.getInputstream();
				XSSFWorkbook workbook = new XSSFWorkbook(file);
				XSSFSheet sheet = workbook.getSheetAt(0);
				Iterator<Row> rowIterator = sheet.iterator();
				while (rowIterator.hasNext()) {
					Row row = rowIterator.next();
					boolean isEndRow = false;
					LOGGER.log(Level.INFO, "read row " + row.getRowNum());			
					if(row.getRowNum() > 0){
						VendorReport vendorReport = new VendorReport();						

						for(int colIndex=0; colIndex<=27; colIndex++){
							String cellValue = null;
							Cell cell = row.getCell(colIndex, Row.CREATE_NULL_AS_BLANK);
							cell.setCellType(Cell.CELL_TYPE_STRING);
							if(cell != null){
								switch (cell.getCellType()) {
									case Cell.CELL_TYPE_BOOLEAN:
										cellValue = cell.getBooleanCellValue()?QuoteConstant.OPTION_YES:QuoteConstant.OPTION_NO;
										break;
									case Cell.CELL_TYPE_NUMERIC:
										if(HSSFDateUtil.isCellDateFormatted(cell)){
											Date date = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());
											String dateFormat = cell.getCellStyle().getDataFormatString();
											cellValue = new CellDateFormatter(dateFormat).format(date);
										} else {
											cellValue = String.valueOf(cell.getNumericCellValue());
										}
										break;
									case Cell.CELL_TYPE_STRING:
										cellValue = cell.getStringCellValue();
										break;
								}							
								if(colIndex == 0) {
									vendorReport.setMfr(cellValue);
									vendorReport.setBizUnit(bizUnit);
									if(!cleared){
										LOGGER.log(Level.INFO, "clear mfr = " + cellValue);
										vendorReportSB.clearVendorReportTable(cellValue, bizUnit);
										cleared = true;
									}
								}
								else if(colIndex == 1) vendorReport.setSqNumber(convertToStringAsInteger(cellValue));
								else if(colIndex == 2) vendorReport.setDebitNumber(cellValue);
								else if(colIndex == 3) vendorReport.setStatus(cellValue);
								else if(colIndex == 4) vendorReport.setFullMfrPartNumber(cellValue);
								else if(colIndex == 5) vendorReport.setProductCategory(cellValue);
								else if(colIndex == 6) vendorReport.setCustomer(cellValue);
								else if(colIndex == 7) vendorReport.setEndCustomer(cellValue);
								else if(colIndex == 8) vendorReport.setCurrency(cellValue);
								else if(colIndex == 9) vendorReport.setCost(cellValue);
								else if(colIndex == 10) vendorReport.setResale(cellValue);
								else if(colIndex == 11) vendorReport.setDebitCreateDate(cellValue);
								else if(colIndex == 12) vendorReport.setDebitExpireDate(cellValue);
								else if(colIndex == 13) vendorReport.setQuoteCreateDate(cellValue);
								else if(colIndex == 14) vendorReport.setQuoteExpireDate(cellValue);
								else if(colIndex == 15) vendorReport.setSoldToCustomerNumber(convertToStringAsInteger(cellValue));
								else if(colIndex == 16) vendorReport.setShipToCustomerNumber(convertToStringAsInteger(cellValue));
								else if(colIndex == 17) vendorReport.setAuthQty(convertToStringAsInteger(cellValue));
								else if(colIndex == 18) vendorReport.setConsumedQty(convertToStringAsInteger(cellValue));
								else if(colIndex == 19) vendorReport.setOpenQty(convertToStringAsInteger(cellValue));
								else if(colIndex == 20) vendorReport.setCustomerCity(cellValue);
								else if(colIndex == 21) vendorReport.setCustomerCountry(cellValue);
								else if(colIndex == 22) vendorReport.setEndCustomerCity(cellValue);
								else if(colIndex == 23) vendorReport.setEndCustomerCountry(cellValue);
								else if(colIndex == 24) vendorReport.setSqRemark1(cellValue);
								else if(colIndex == 25) vendorReport.setSqRemark2(cellValue);
								else if(colIndex == 26) vendorReport.setSqRemark3(cellValue);
								else if(colIndex == 27) vendorReport.setSqRemark4(cellValue);
							}
						}
						if(QuoteUtil.isEmpty(vendorReport.getMfr()) || QuoteUtil.isEmpty(vendorReport.getFullMfrPartNumber())){
							isEndRow = true;
							break;
						}
						vendorReports.add(vendorReport);
						if(vendorReports.size() > 1000){
							vendorReportSB.createVendorReport(vendorReports);
							vendorReports = new ArrayList<VendorReport>();
						}
					}
					if(isEndRow){
						break;
					}
				}
				file.close();
				if(vendorReports.size() > 0){
					vendorReportSB.createVendorReport(vendorReports);
					vendorReports = null;
				}
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, ResourceMB.getText("wq.message.uploadSucc") , "");
				FacesContext.getCurrentInstance().addMessage("vendorReportGrowl", msg);	 
			}
		} catch (Exception e) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,  ResourceMB.getText("wq.message.uploadFailed"), "");
			FacesContext.getCurrentInstance().addMessage("vendorReportGrowl", msg);	 
			LOG.log(Level.SEVERE, "Failed to upload file"+vendorReportExcel.getFileName()+" , for business unit "+bizUnit+" , Error message : "+MessageFormatorUtil.getParameterizedStringFromException(e), e);
		}
	
	}
    /*
     * Wizard to control the work flow
     */
    public String onFlowProcess(FlowEvent event) {  
        if(event.getOldStep() != null && event.getNewStep() != null){      		
        	if(event.getOldStep().equals("upload") && event.getNewStep().equals("confirm")){
        		readExcelForm();
           	}
        }
        return event.getNewStep();
    }
    
    /**
	 * Download vendor Report Template.
	 * 
	 * @return
	 */
	public StreamedContent getVendorReportTemplate() {
		String filePath = DeploymentConfiguration.configPath + File.separator +QuoteConstant.VENDOR_REPORT_TEMPLATE_LOCATION;
		FileInputStream in = null;
		StreamedContent pricerUploadTemplate=null;
		try {
			in = new FileInputStream(filePath);
			if(in!=null)
			pricerUploadTemplate = new DefaultStreamedContent(
					in,
					DownloadUtil
							.getMimeType(QuoteConstant.VENDOR_REPORT_TEMPLATE_LOCATION),
					QuoteConstant.VENDOR_REPORT_TEMPLATE_LOCATION);
		} catch (FileNotFoundException e) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
					"", ResourceMB.getText("wq.message.downloadfail")+".");
			FacesContext.getCurrentInstance().addMessage("vendorReportGrowl",
					msg);
			LOG.log(Level.SEVERE,"File on this path "+filePath+" not found "+ e.getMessage(), e);
		}
		
		
		return pricerUploadTemplate;
	}
	
	
	
	public UploadedFile getVendorReportExcel() {
		return vendorReportExcel;
	}

	public void setVendorReportExcel(UploadedFile vendorReportExcel) {
		this.vendorReportExcel = vendorReportExcel;
	}

}