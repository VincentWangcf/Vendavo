package com.avnet.emasia.webquote.web.mbt;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.ParseException;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.log.Log;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.UploadedFile;

import com.avnet.emasia.webquote.cache.DesignLocationCacheManager;
import com.avnet.emasia.webquote.constants.ActionEnum;
import com.avnet.emasia.webquote.constants.BmtFlagEnum;
import com.avnet.emasia.webquote.entity.Attachment;
import com.avnet.emasia.webquote.entity.BmtFlag;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.QuoteItemDesign;
import com.avnet.emasia.webquote.exception.WebQuoteException;
import com.avnet.emasia.webquote.quote.ejb.BmtQuoteSB;
import com.avnet.emasia.webquote.quote.ejb.SystemCodeMaintenanceSB;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.util.ExcelUtil;
import com.avnet.emasia.webquote.utilites.web.util.POICellColor;
import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.utilities.common.FilterMatchMode;
import com.avnet.emasia.webquote.utilities.common.QueryBean;
import com.avnet.emasia.webquote.utilities.util.DateUtil;
import com.avnet.emasia.webquote.web.datatable.BaseLazyDataMB;
import com.avnet.emasia.webquote.web.datatable.LazyEntityDataModel;
import com.avnet.emasia.webquote.web.quote.FacesUtil;
import com.avnet.emasia.webquote.web.user.UserInfo;

@ManagedBean
@SessionScoped
public class BmtPlatformMB extends BaseLazyDataMB<QuoteItemDesign> implements Serializable {

	private static final long serialVersionUID = 1L;

	private LazyEntityDataModel<QuoteItemDesign> bmtPlatformVOs;
	@EJB
	private BmtQuoteSB bmtQuoteSB;
	private List<QuoteItemDesign> transferToQcList;
	private List<Attachment> bmtAttachment;
	private HashMap<Long, List<Attachment>> cacheUploadFiles = new HashMap<>();
	@EJB
	private SystemCodeMaintenanceSB codeMaintenanceSB;
	@ManagedProperty("#{bmtQuotationHistoryMB}")
	private BmtQuotationHistoryMB bmtQuotationHistoryMB;
	
	public String MSG_SUMMARY_TO_QC=ResourceMB.getText("wq.button.TransferToQC"); 
	public String MSG_SUMMARY_UPLOAD_TEMPLATE=ResourceMB.getText("wq.button.uploadTemplate");
	public String MSG_SUMMARY_SAVE_ALL=ResourceMB.getText("wq.button.saveAll");
	
	@ManagedProperty(value="#{resourceMB}")
	/** To Inject ResourceMB instance  */
    private ResourceMB resourceMB;
	
	@PostConstruct
	private void init() {
		if (bmtPlatformVOs == null) {
			bmtPlatformVOs = new LazyEntityDataModel<QuoteItemDesign>(bmtQuoteSB.getDataAccess(UserInfo.getUser()), bmtQuoteSB ,getDataAccess(), "topForm:lazyData");
		}
	}
	public void  preExportOrder(Object order){
		order = ((StringBuilder)order).append("QC Region,Avnet Quote#,Revert Version,First RFQ Code,Pending Day,RFQ Status,Avnet Quoted Price,Avnet Quoted P/N,Multi-Usage,QC Internal Comment,PM Comment,MFR Quote #,MFR Debit #,MFR Quote Qty,Price Validity,Shipment Validity,Description,Cost Indicator,Cost,Quotation Effective Date,MFR,Requested P/N,Required Qty,EAU,RFQ CUR,Target Resale,Quote Type,RFQ Submission Date,Salesman Employee Code,Salesman,Team,Sold-To-Party,Sold-To-Code,Customer Type,Project Name,Application,Segment,Design Location,Design In Cat,DRMS Project ID,End Customer,Ship-To-Code,Ship-To-Party,End Customer Code,Remarks,Competitor Information,MFR DR #,PP Schedule,MP Schedule,Item Remarks,SPR,Refresh Attachment,Reason For Refresh,Form No,Sold To Party (Chinese),Design Region,SAP Material Number,Target Margin%,BMT Flag,BMT MFR DR#,BMT Suggest Cost,BMT Suggest Resale,BMT Suggest Margin,BMT MFR Quote#,BMT SQ Effective Date,BMT SQ Expiry Date,BMT Comments,BMT Quoted Qty,BMT Suggest Cost (Non-USD),BMT Suggest Resale (Non-USD),BMT Currency,BMT Exchange Rate (Non-USD),BMT Attachment,Last Update BMT");
	}
	
	public void postExportExcel(Object doc) {
		LOGGER.info("User: "+UserInfo.getUser().getEmployeeId() +" Export BMT Working platform table");
//		if(true){return;}
		HSSFWorkbook  wb = (HSSFWorkbook) doc;
		Sheet sheet = wb.getSheetAt(0);
		//13
		int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
		CellStyle stylePink = ExcelUtil.createStyle(wb, POICellColor.OPTIONAL,CellStyle.ALIGN_LEFT);
		CellStyle styleYellow = ExcelUtil.createStyle(wb, POICellColor.MANDATORY,CellStyle.ALIGN_LEFT);     
		CellStyle sheetStyle = ExcelUtil.createStyle(wb, POICellColor.NORMAL,CellStyle.ALIGN_LEFT);
		int offset = 1;
		for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
			Row row = sheet.getRow(i);
			if(i!=0){
				BmtFlag bmtFlag = new BmtFlag();
				bmtFlag.setBmtFlagCode(row.getCell(57 + offset).getStringCellValue());
				row.getCell(57 + offset).setCellValue(bmtFlag.getDescription());
			}
			for (int c = 0; c < row.getPhysicalNumberOfCells(); c++) {
				if (c == 36 + offset || c == 69 + offset || c == 70 + offset || c == 62 + offset || c == 63 + offset || c == 64 + offset || c == 65 + offset
						|| c == 66 + offset || c == 67 + offset || c == 68 + offset) {
					row.getCell(c).setCellStyle(stylePink);
				} else if (c == 57 + offset || c == 58 + offset || c == 59 + offset || c == 60 + offset) {
					row.getCell(c).setCellStyle(styleYellow);
				}else{
					row.getCell(c).setCellStyle(sheetStyle);					
				}
			}
		}
		CellRangeAddressList designLocaationRegions = new CellRangeAddressList(1, physicalNumberOfRows, 36 + offset, 36 + offset);  
        String[] designLocaationItems = (String[]) DesignLocationCacheManager.getDesignLocationMap().keySet().toArray(new String[0]);
        List<String> currencyLst;
        
        String[] currencyItems =  {""};
		try {
			currencyLst = codeMaintenanceSB.findCurrency();
			currencyItems = (String[]) currencyLst.toArray(new String[0]);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error occured for User Id: "+UserInfo.getUser().getEmployeeId()+", Reason for failure: "+MessageFormatorUtil.getParameterizedStringFromException(e), e);
		}
        String[] bmtItems = (String[]) getAllMmtFlag().values().toArray(new String[0]);
        Sheet hidden = wb.createSheet("hidden");
        wb.setSheetHidden(1, true);
        
        for (int i = 0, length = designLocaationItems.length; i < length; i++) {
          Row row = hidden.createRow(i);
          Cell cell = row.createCell(0);
          cell.setCellValue(designLocaationItems[i]);
        }
        
        for (int i = 0, length = currencyItems.length; i < length; i++) {
        	Row row = hidden.getRow(i);
        	if(row == null){
        		row = hidden.createRow(i);
        	}
        	Cell cell = row.createCell(2);
        	cell.setCellValue(currencyItems[i]);
        }
        
        Name namedCelldesignLocation = wb.createName();
        namedCelldesignLocation.setNameName("hidden");
        namedCelldesignLocation.setRefersToFormula("hidden!$A$1:$A$" + designLocaationItems.length);
		DVConstraint constraintdesignLocation = DVConstraint.createFormulaListConstraint("hidden");

        HSSFDataValidation designLocationDataValidation = new HSSFDataValidation(designLocaationRegions,constraintdesignLocation); 
        designLocationDataValidation.setShowErrorBox(false);
        sheet.addValidationData(designLocationDataValidation);
        
        CellRangeAddressList bmtRegions = new CellRangeAddressList(1, physicalNumberOfRows, 57 + offset, 57 + offset);  
      
        DVConstraint bmtConstraint = DVConstraint.createExplicitListConstraint(bmtItems);  
        HSSFDataValidation bmtDataValidation = new HSSFDataValidation(bmtRegions,bmtConstraint);  
        bmtDataValidation.setShowErrorBox(false);
        sheet.addValidationData(bmtDataValidation);
        //need to change to cell 69
        CellRangeAddressList currencyRegions = new CellRangeAddressList(1, physicalNumberOfRows, 69 + offset, 69 + offset);  
        
       
        Name namedCellCurrency = wb.createName();
        namedCellCurrency.setNameName("hidden2");
        namedCellCurrency.setRefersToFormula("hidden!$C$1:$C$" + currencyItems.length);
        DVConstraint constraintCurrency = DVConstraint.createFormulaListConstraint("hidden2");
        
        HSSFDataValidation currencyDataValidation = new HSSFDataValidation(currencyRegions,constraintCurrency);  
        currencyDataValidation.setShowErrorBox(false);
        sheet.addValidationData(currencyDataValidation);
        LOGGER.info("User: "+UserInfo.getUser().getEmployeeId() +" Export BMT Working platform table end");
	}

	// TODO: move to utils class
	private Map<String, String> getAllMmtFlag() {
		Map<String, String> flags = new HashMap<>(5);
		for (BmtFlagEnum bfe : BmtFlagEnum.values()) {
			flags.put(bfe.code(), bfe.description());
		}
		return flags;
	}
 
	public void doTransferToQc(String uiId) {
		bmtQuoteSB.transferToQcSendEmail(transferToQcList, UserInfo.getUser());		
		try{
			boolean successful = bmtQuoteSB.doTransferToQc(transferToQcList, UserInfo.getUser());
			if (successful) {
				transferToQcList = null;
				bmtPlatformVOs.clearCachePageSelectDatas();
				FacesUtil.showInfoMessage(ResourceMB.getText("wq.button.TransferToQC"), 
						ResourceMB.getText("wq.message.successful")+".");
				FacesUtil.updateUI(uiId.split(","));
			}
		}catch(MessagingException e){ 
			LOGGER.log(Level.SEVERE, "Error occured for User: "+UserInfo.getUser().getEmployeeId()+", Reason for failure: "+MessageFormatorUtil.getParameterizedStringFromException(e), e);
			FacesUtil.showWarnMessage(ResourceMB.getText("wq.button.TransferToQC"),ResourceMB.getText("wq.label.sendEmailError")+".");
		}catch(Exception e){
			LOGGER.log(Level.SEVERE, "Error occured for User: "+UserInfo.getUser().getEmployeeId()+", Reason for failure: "+MessageFormatorUtil.getParameterizedStringFromException(e), e);
			FacesUtil.showWarnMessage(ResourceMB.getText("wq.button.TransferToQC"), ResourceMB.getText("wq.message.operationFailed")+".");
		}
	}
	
	public void clearBmtDatasFilter(String uiId) {
		if (bmtPlatformVOs.getCacheModifyDatas().isEmpty()) {
			doClearBmtDatasFilter(uiId);
		}
	}

	public void doClearBmtDatasFilter(String uiId) {
		FacesUtil.executeJS("PF('lazyBmtData').clearFilters()");
		FacesUtil.showInfoMessage(ResourceMB.getText("wq.button.clearFilter"), ResourceMB.getText("wq.message.successful")+".");
		FacesUtil.updateUI(uiId.split(","));
	}
	
	public void clearFilter() {
		DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot()
				.findComponent("topForm:lazyData");
		if (dataTable != null) {
			dataTable.setFilters(null);
			dataTable.setFilteredValue(null);
			dataTable.reset();
		}
	}
	
	private List<QueryBean> getDataAccess(){
		List<QueryBean> dataaccess = new ArrayList<QueryBean>();
		dataaccess.add(new QueryBean(FilterMatchMode.IN, "o.quoteItem.status", Arrays.asList("BQ", "BIT")));
		dataaccess.add(new QueryBean(FilterMatchMode.IN, "o.quoteItem.quote.bizUnit", UserInfo.getUser().getBizUnits()));
		return dataaccess;
	}
	
	public boolean hasModifiedRecords() {
		return !bmtPlatformVOs.getCacheModifyDatas().isEmpty() || !cacheUploadFiles.values().isEmpty();
	}

	

	public void setBmtAttachment(List<Attachment> atts) {
		this.bmtAttachment = atts;
	}

	public void removeAttachment(Long objectId, Attachment attachmentfue) {
		List<Attachment> atts = cacheUploadFiles.get(objectId);
		atts.remove(attachmentfue);
		if (atts == null || atts.isEmpty()) {
			cacheUploadFiles.remove(objectId);
		}
		FacesUtil.showInfoMessage(ResourceMB.getText("wq.message.removeAttachment"),ResourceMB.getText("wq.message.successfulSmall")+".");
	}
	
	public void handleTemplateUpload(FileUploadEvent fue) {
		String status = null;
		UploadedFile file = fue.getFile();
		String message=null;
		List<QuoteItemDesign> items = new ArrayList<>();
		try {
			String fileName = file.getFileName().toString();
			LOGGER.info("User: "+UserInfo.getUser().getEmployeeId() + " upload template: "+fileName);
			Workbook xls = null;
			if(fileName.endsWith("xls") || fileName.endsWith("XLS")){
				xls = new HSSFWorkbook(file.getInputstream());					
			}else{
				xls = new XSSFWorkbook(file.getInputstream());	
			}			
			Sheet sheet = xls.getSheetAt(0);
			Row row=null;
			boolean StatusFlag=true;
			
			for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
				
				row = sheet.getRow(i);
				try {
					
					Cell cellQuoteNumber = row.getCell(1);
					String quoteNumber = cellQuoteNumber.getStringCellValue().trim();
					LOGGER.info("User: "+UserInfo.getUser().getEmployeeId() + " upload quote#: "+quoteNumber);
					QuoteItemDesign qid = bmtQuoteSB.findByQuoteNumber(quoteNumber,UserInfo.getUser());
					status = qid.getQuoteItem().getStatus();
					if(!("BQ").equalsIgnoreCase(status) && !("BIT").equalsIgnoreCase(status)){
						int errRowNum=row.getRowNum()+1;
						  message = ResourceMB.getText("wq.message.uploadFailed")+" Row["+errRowNum+
									"]: the quote number in the excel file is not ‘BQ’ or ‘BIT’.";
						FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR, message, ""));
						StatusFlag=false;
						break;	
					
					}
					
					items.add(checkingUploadData(row, qid));
					for(QuoteItemDesign quoteItemDesign:items){
						//check quoteItem If Quote_item.Stage != ‘Pending’ or Quote_Item.Status != ‘BQ’
						String stage = quoteItemDesign.getQuoteItem().getStage();
						if(!stage.equalsIgnoreCase("Pending")){			
							int errRowNum=row.getRowNum()+1;
							  message = ResourceMB.getText("wq.message.uploadFailed")+" Row["+errRowNum+
										"]: The QuoteItem Stage is not ‘PENDING’";
								FacesContext.getCurrentInstance().addMessage(null,
										new FacesMessage(FacesMessage.SEVERITY_ERROR, message,
												""));
							break;
							}
						}
					
				} catch (Exception e) {
					LOGGER.log(Level.SEVERE, "Error occured for User: "+UserInfo.getUser().getEmployeeId()+", Reason for failure: "+MessageFormatorUtil.getParameterizedStringFromException(e), e);
					FacesUtil.showWarnMessage(ResourceMB.getText("wq.button.uploadTemplate"), MessageFormatorUtil.getParameterizedStringFromException(e,resourceMB.getResourceLocale()) +" "+ResourceMB.getText("wq.message.atRow")+": "+(row.getRowNum() +1));
					return;
				} 
			}
			if(!items.isEmpty()&&StatusFlag){
				bmtQuoteSB.batchSave(items);
				bmtPlatformVOs.clearCachePageSelectDatas();
				FacesUtil.updateUI("topForm:lazyData");
				FacesUtil.showInfoMessage(ResourceMB.getText("wq.button.uploadTemplate"), ResourceMB.getText("wq.message.successful")+".");
			}else{
				
				if(!("BQ").equalsIgnoreCase(status)){
					if(!("BIT").equalsIgnoreCase(status)){
						return;	
					}
					
				}
				
				FacesUtil.showWarnMessage(ResourceMB.getText("wq.button.uploadTemplate"), ResourceMB.getText("wq.message.emptyTemplate")+".");
			}
		} catch (IOException e) {
			FacesUtil.showWarnMessage(ResourceMB.getText("wq.button.uploadTemplate"), ResourceMB.getText("wq.message.uploadFileError")+".");
			LOGGER.log(Level.SEVERE, "Error occured for User Id: "+UserInfo.getUser().getEmployeeId()+", Reason for failure: "+MessageFormatorUtil.getParameterizedStringFromException(e),e);
		} catch (Exception e) {
			FacesUtil.showWarnMessage(ResourceMB.getText("wq.button.uploadTemplate"), ResourceMB.getText("wq.message.dataUploadError")+".");
			LOGGER.log(Level.SEVERE, "Error occured for User Id: "+UserInfo.getUser().getEmployeeId()+", Reason for failure: "+MessageFormatorUtil.getParameterizedStringFromException(e),e);
			
		}
	}

	private QuoteItemDesign checkingUploadData(Row row, QuoteItemDesign qid) throws WebQuoteException  {
		int offset = 1;
		row.getCell(1).setCellType(Cell.CELL_TYPE_STRING);
		Cell cellQuoteNumber = row.getCell(1);
		if (cellQuoteNumber.getStringCellValue() == null || "".equals(cellQuoteNumber.getStringCellValue())) {
			throw new WebQuoteException(ResourceMB.getText("wq.error.90024") + cellQuoteNumber);
		}
		

		// TODO: check api
		String quoteNumber = cellQuoteNumber.getStringCellValue().trim();
		LOGGER.info("User: "+UserInfo.getUser().getEmployeeId() + " upload quote#: "+quoteNumber);
		for(int i=0;i<row.getPhysicalNumberOfCells();i++){
			row.getCell(i).setCellType(Cell.CELL_TYPE_STRING);
		}
		
		String designLocation = row.getCell(36 + offset).getStringCellValue();
		String bmtFlagDesc = row.getCell(57 + offset).getStringCellValue();
		String drMfrNo = row.getCell(58 + offset).getStringCellValue();
		String drCost = row.getCell(59 + offset).getStringCellValue();
		String drResale = row.getCell(60 + offset).getStringCellValue();
		String drMfrQuoteNo = row.getCell(62 + offset).getStringCellValue();
		
		String drEffectiveDate = row.getCell(63 + offset).getStringCellValue();
		String drExpiryDate = row.getCell(64 + offset).getStringCellValue();
		String drComments = row.getCell(65 + offset).getStringCellValue();
		String drQuoteQty = row.getCell(66 + offset).getStringCellValue();
		String drCostAltCurrency = row.getCell(67 + offset).getStringCellValue();
		String drResaleAltCurrency = row.getCell(68 + offset).getStringCellValue();
		String drAltCurrency = row.getCell(69 + offset).getStringCellValue();
		String drExchangeRate = row.getCell(70 + offset).getStringCellValue();
		
		try{
			//modified by Lenon 2016-09-29
			Date date = new Date();
			qid.getQuoteItem().setLastUpdatedBmt(UserInfo.getUser().getEmployeeId());
			qid.getQuoteItem().setLastBmtUpdateTime(date);
			qid.getQuoteItem().setLastUpdatedBy(UserInfo.getUser().getEmployeeId());
			qid.getQuoteItem().setLastUpdatedName(UserInfo.getUser().getName());
			qid.getQuoteItem().setLastUpdatedOn(date);
			qid.getQuoteItem().setDesignLocation(designLocation);
			qid.setLastUpdatedBy(UserInfo.getUser().getEmployeeId());
			qid.setLastUpdatedTime(date);
			
			qid.getBmtFlag().setBmtFlagDesc(bmtFlagDesc);
			qid.setDrMfrNo(drMfrNo);
			qid.setDrCost(getCellDecimalValue(drCost));
			qid.setDrResale(getCellDecimalValue(drResale));
			qid.setDrMfrQuoteNo(drMfrQuoteNo);
			qid.setDrEffectiveDate((drEffectiveDate != null && drEffectiveDate.length() > 0) ? DateUtil.stringToDate(drEffectiveDate, "dd/MM/yyyy","(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)"):null);
			//qid.setDrEffectiveDate(drEffectiveDate);
			qid.setDrExpiryDate((drExpiryDate != null && drExpiryDate.length() > 0) ? DateUtil.stringToDate(drExpiryDate, "dd/MM/yyyy","(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)"):null);
			qid.setDrComments(drComments);
			qid.setDrQuoteQty((drQuoteQty != null && drQuoteQty.length() > 0)?Integer.valueOf(drQuoteQty.replace(",","")):null);
			qid.setDrCostAltCurrency(getCellDecimalValue(drCostAltCurrency));
			qid.setDrResaleAltCurrency(getCellDecimalValue(drResaleAltCurrency));
			qid.setDrAltCurrency(drAltCurrency);
			qid.setDrExchangeRate(getCellDecimalValue(drExchangeRate));
		}catch(Exception e){
			LOGGER.log(Level.INFO, e.getMessage(), e);
			throw new WebQuoteException(ResourceMB.getText("wq.error.90006"));
		}
		return qid;
	}
	
	private BigDecimal getCellDecimalValue(String cellValue){
		if (cellValue != null && cellValue.trim().length() == 0){
			return  null;			
		}else{
			return	new BigDecimal(cellValue.replaceAll(",", "")).setScale(5,BigDecimal.ROUND_HALF_UP);
		}
	}
	
	public void handleFileUpload(FileUploadEvent fue) {
		Long objectId = Long.valueOf(fue.getComponent().getAttributes().get("currentRecordObjectId").toString());
		List<Attachment> files = cacheUploadFiles.get(objectId);
		if (files == null) {
			files = new ArrayList<>();
			files.add(getAttachmentByUploadedFile(fue.getFile()));
			cacheUploadFiles.put(objectId, files);
		} else {
			files.add(getAttachmentByUploadedFile(fue.getFile()));
		}
		LOGGER.info("User: "+UserInfo.getUser().getEmployeeId() +" Upload file "+fue.getFile().getFileName()+" to : " + objectId);
	}

	private Attachment getAttachmentByUploadedFile(UploadedFile file) {
		Attachment a = new Attachment();
		a.setFileImage(QuoteUtil.getUploadFileContent(file));
		a.setFileName(file.getFileName());
		a.setType(QuoteSBConstant.ATTACHMENT_TYPE_BMT);
		return a;
	}

	public HashMap<Long, List<Attachment>> getCacheUploadFiles() {
		return cacheUploadFiles;
	}

	public void setCacheUploadFiles(HashMap<Long, List<Attachment>> cacheUploadFiles) {
		this.cacheUploadFiles = cacheUploadFiles;
	}

	public List<Attachment> getBmtAttachment() {
		return bmtAttachment;
	}

	public LazyEntityDataModel<QuoteItemDesign> getBmtPlatformVOs() {

		return bmtPlatformVOs;
	}

	public void setBmtPlatformVOs(LazyEntityDataModel<QuoteItemDesign> bmtPlatformVOs) {
		this.bmtPlatformVOs = bmtPlatformVOs;
	}
	
	private boolean validNoMisMatchRow(String id) {
		try {
			String uiRowKey = getCurrentRowUiId();
			if(id != null && id.equals(uiRowKey)) {
				return true;
			} else {
				LOGGER.warning(MessageFormat.format("UI ID is {0}, but server matchedID is {1}.",
						uiRowKey, id));
			}
		} catch(Exception e) {
			LOGGER.log(Level.SEVERE, "", e);
		}
		
		return false;
	}
	
	private String getCurrentRowUiId() {
		try {
			FacesContext fContext = FacesContext.getCurrentInstance();
			ExternalContext extContext = fContext.getExternalContext();
			Map<String, String> map = extContext.getRequestParameterMap();
			String uiRowKey = map.get(MessageFormat.format("topForm:lazyData:{0}:rowKeyCol", bmtPlatformVOs.getRowIndex()));
			return uiRowKey;
		} catch(Exception e) {
			LOGGER.log(Level.SEVERE, "", e);
		}
		return null;
	}
	
	@Override
	public void cellChangeListener(String id) {
		
		if(!validNoMisMatchRow(id)) {
			this.clearModifyDatas();
			//UI ID is {0} but could not match the ID {1} in server. 
			//We will refresh table. Please input your information and try to save again.
			String mesg = MessageFormat.format("UI ID is {0}, but not match the ID {1}."
					+ " We will refresh table. Please input your information and try to save again.",
					getCurrentRowUiId(), id);
			FacesUtil.showWarnMessage("", mesg);
			FacesUtil.updateUI("growl", "topForm:save_all_Button", "topForm:lazyData");
			return;
		}
		bmtPlatformVOs.setCacheModifyData(id);
		FacesUtil.updateUI("topForm:save_all_Button");
		
		 
	}

	public void saveAll(String uiId) {
		if (!hasModifiedRecords()) {
			FacesUtil.showWarnMessage(ResourceMB.getText("wq.button.saveAll"), ResourceMB.getText("wq.message.noUpdatedRecord")+".");
			return;  
		}
		if (doVerifySaveData()) {
			boolean successful = bmtQuoteSB.batchSaveBMTObject(UserInfo.getUser().getEmployeeId(),UserInfo.getUser(),bmtPlatformVOs.getCacheModifyDatas(), cacheUploadFiles, ActionEnum.BMT_SAVE);
			if (successful) {
				clearModifyDatas();
				if(currentSelectedQuoteItem != null){
					currentSelectedQuoteItem = bmtQuoteSB.find(currentSelectedQuoteItem.getId());
				}
				FacesUtil.showInfoMessage(ResourceMB.getText("wq.button.saveAll"), ResourceMB.getText("wq.message.successfulSmall")+".");
				FacesUtil.updateUI(uiId.split(","));
			} else {
				FacesUtil.showWarnMessage(ResourceMB.getText("wq.button.saveAll"), ResourceMB.getText("wq.message.updateFailed")+".");
			}
		}
	}

	private void clearModifyDatas() {
		bmtPlatformVOs.clearCacheModifyDatas();
		getCacheUploadFiles().clear();
	}

	private boolean doVerifySaveData() {
		Set<QuoteItemDesign> datas = bmtPlatformVOs.getCacheModifyDatas();
		StringBuilder msg = new StringBuilder();
		StringBuilder rowMsg = new StringBuilder();
		for (QuoteItemDesign qid : datas) {
			if(msg.length() > 500){
				msg.append(ResourceMB.getText("wq.message.andMore"));
				msg.append("...&nbsp;&nbsp; &nbsp;");
				break;
			}

			if (qid.getDrMfrNo() != null && qid.getDrMfrNo().length() > 150) {
				rowMsg.append("-");
				rowMsg.append(ResourceMB.getText("wq.message.bmtMfrMaxlength"));
				rowMsg.append(",<br/>");
			}
			if (qid.getDrMfrQuoteNo() != null && qid.getDrMfrQuoteNo().length() > 150) {
				rowMsg.append("-");
				rowMsg.append(ResourceMB.getText("wq.message.bmtMfrQuotelength"));
				rowMsg.append(",<br/> ");
			}

			if (qid.getDrComments() != null && qid.getDrComments().length() > 1000) {
				rowMsg.append("-");
				rowMsg.append(ResourceMB.getText("wq.message.bmtCommentsMaxlength"));
				rowMsg.append(",<br/>");
			}
			if(qid.getBmtFlag() == null || "".equals(qid.getBmtFlag().getBmtFlagCode())){
				qid.getBmtFlag().setBmtFlagCode(BmtFlagEnum.EMPTY.code());
			}
			if (rowMsg.length() > 0) {
				msg.append(ResourceMB.getText("wq.message.forRowQuote")+"# ").append(qid.getQuoteItem().getQuoteNumber()).append(":<br/>");
				msg.append(rowMsg).append("<br/>");
				rowMsg = new StringBuilder();
			}
		}
		if (msg.length() > 0) {
			FacesUtil.showInfoMessage(ResourceMB.getText("wq.button.saveAll"), msg.append(ResourceMB.getText("wq.message.error")+".").toString());
			return false;
		}
		return true;
	}
	
	private boolean doVerifyBeforeToQcSaveData() {
		Set<QuoteItemDesign> datas = getCacheSelectionDatas();
		StringBuilder msg = new StringBuilder();
		StringBuilder rowMsg = new StringBuilder();
		for (QuoteItemDesign qid : datas) {
			if(msg.length() > 500){
				msg.append(ResourceMB.getText("wq.message.andMore"));  
				msg.append("...&nbsp;&nbsp; &nbsp;");
				break;
			}
			if (qid.getBmtFlag() != null && StringUtils.equals(BmtFlagEnum.BMT_DR.code(), qid.getBmtFlag().getBmtFlagCode())) {
				if (QuoteUtil.isEmpty(qid.getDrMfrNo())) {
					rowMsg.append("-");
					rowMsg.append(ResourceMB.getText("wq.message.bmtMfrMandatory"));
					rowMsg.append(",<br/> ");
				}
				
				if (qid.getDrCost() == null || qid.getDrCost().doubleValue() == 0) {
					rowMsg.append("-");
					rowMsg.append(ResourceMB.getText("wq.message.bmtCostMandatory"));
					rowMsg.append(",<br/> ");
				}
				if (qid.getDrResale() == null || qid.getDrResale().doubleValue() == 0) {
					rowMsg.append("-");
					rowMsg.append(ResourceMB.getText("wq.message.bmtResaleMandatory"));
					rowMsg.append(",<br/> ");
				}
				if(rowMsg.length() > 0){  
					rowMsg.insert(0, ResourceMB.getText("wq.message.bmtFlageDR")+",<br/>");
				}
			}
			if(qid.getBmtFlag() == null || "99".equals(qid.getBmtFlag().getBmtFlagCode())){
				rowMsg.append("-");
				rowMsg.append(ResourceMB.getText("wq.message.bmtFlagNotempty"));
				rowMsg.append(",<br/> ");
			}
			if (qid.getDrMfrNo() != null && qid.getDrMfrNo().length() > 150) {
				rowMsg.append("-");
				rowMsg.append(ResourceMB.getText("wq.message.bmtMfrMaxlength"));
				rowMsg.append(",<br/> ");
				
			}
			if (qid.getDrMfrQuoteNo() != null && qid.getDrMfrQuoteNo().length() > 150) {
				rowMsg.append("-");
				rowMsg.append(ResourceMB.getText("wq.message.bmtMfrQuotelength"));
				rowMsg.append(",<br/> ");
			}
			
			if (qid.getDrComments() != null && qid.getDrComments().length() > 1000) {
				rowMsg.append("-");
				rowMsg.append(ResourceMB.getText("wq.message.bmtCommentsMaxlength"));
				rowMsg.append(",<br/> ");
			}
			
			if (rowMsg.length() > 0) {
				msg.append(ResourceMB.getText("wq.message.forRowQuote")+"# ").append(qid.getQuoteItem().getQuoteNumber()).append(":<br/>");
				msg.append(rowMsg).append("<br/>");
				rowMsg = new StringBuilder();
			}
		}
		if (msg.length() > 0) {
			FacesUtil.showWarnMessage(ResourceMB.getText("wq.button.TransferToQC"), msg.append(ResourceMB.getText("wq.message.error")+".").toString());
			return false;  
		}  
		return true;
	}

	/**
	 * before do transfer to qc logic
	 */
	public void transferToQc() {
		if(getCacheSelectionDatas().isEmpty()){
			FacesUtil.showWarnMessage(ResourceMB.getText("wq.button.TransferToQC"), ResourceMB.getText("wq.label.selectQuote")+"."); 
		}else
		if (doVerifyBeforeToQcSaveData()) {
			boolean successful = bmtQuoteSB.batchSaveBMTObject(UserInfo.getUser().getEmployeeId(), UserInfo.getUser(),bmtPlatformVOs.getCacheModifyDatas(), cacheUploadFiles,ActionEnum.BMT_SAVE_BEFORE_SEND_TO_QC);
			if (successful) {
				clearModifyDatas();
				currentSelectedQuoteItem = null;
				FacesUtil.updateUI("topForm:save_all_Button","topForm:lazyData");
				FacesUtil.executeJS("PF('toQc').show()");
			} else {
				FacesUtil.showWarnMessage(ResourceMB.getText("wq.button.TransferToQC"), ResourceMB.getText("wq.message.updateFailed")+".");
			}
		}
	}

	@Override
	public void onRowSelect(SelectEvent event) {
		super.onRowSelect(event);
		updateHistoryCondation((QuoteItemDesign) event.getObject());
	}

	@Override
	public void onRowSelectCheckbox(SelectEvent event) {
		super.onRowSelectCheckbox(event);
		updateHistoryCondation((QuoteItemDesign) event.getObject());
	}

	private QuoteItemDesign currentSelectedQuoteItem;

	private void updateHistoryCondation(QuoteItemDesign t) {
		if(t != null){
			currentSelectedQuoteItem = t;
			if(bmtQuotationHistoryMB != null){				
				bmtQuotationHistoryMB.setMfr(t.getQuoteItem().getRequestedMfr().getName());
				bmtQuotationHistoryMB.setPn(t.getQuoteItem().getRequestedPartNumber());
				bmtQuotationHistoryMB.setSoldToName(t.getQuoteItem().getSoldToCustomer().getCustomerFullName());
			}else{
				LOGGER.severe("updateHistoryCondation error: bmtQuotationHistoryMB is null");
			}
			
			String ecName = "";
			if(t.getQuoteItem().getEndCustomer() != null){
				ecName = t.getQuoteItem().getEndCustomer().getName();
			}
			bmtQuotationHistoryMB.setEcName(ecName);
		}else{
			LOGGER.severe("updateHistoryCondation error: QuoteItemDesign is null");
		}
	}

	@Override
	protected List<QuoteItemDesign> getCurrentPageDatas() {
		return bmtPlatformVOs.getCurrentPageDatas();
	}

	@Override
	public Set<QuoteItemDesign> getCacheSelectionDatas() {
		return bmtPlatformVOs.getCachePageSelectDatas();
	}

	public void setBmtQuotationHistoryMB(BmtQuotationHistoryMB bmtQuotationHistoryMB) {
		this.bmtQuotationHistoryMB = bmtQuotationHistoryMB;
	}

	public List<QuoteItemDesign> getTransferToQcList() {
		transferToQcList = new ArrayList<>(bmtPlatformVOs.getCachePageSelectDatas());
		for (QuoteItemDesign qid : transferToQcList) {
			qid.getQuoteItem().setDrmsUpdateFailedDesc("");
		}
		return transferToQcList;
	}

	public void setTransferToQcList(List<QuoteItemDesign> transferToQcList) {
		this.transferToQcList = transferToQcList;
	}

	protected LazyEntityDataModel<QuoteItemDesign> getLazyData() {
		return bmtPlatformVOs;
	}

	public QuoteItemDesign getCurrentSelectedQuoteItem() {
		return currentSelectedQuoteItem;
	}

	public void setCurrentSelectedQuoteItem(QuoteItemDesign currentSelectedQuoteItem) {
		this.currentSelectedQuoteItem = currentSelectedQuoteItem;
	}
	@Override
	public void onFilter(FilterEvent event) {
	}
	
	/**
	 * Setter for resourceMB
	 * @param resourceMB ResourceMB
	 * */
	public void setResourceMB(final ResourceMB resourceMB) {
		this.resourceMB = resourceMB;
	}
}
