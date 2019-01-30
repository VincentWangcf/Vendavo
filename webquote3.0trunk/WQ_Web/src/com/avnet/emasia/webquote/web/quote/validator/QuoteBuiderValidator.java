package com.avnet.emasia.webquote.web.quote.validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.primefaces.model.UploadedFile;

import com.avnet.emasia.webquote.quote.vo.QuoteBuilderNonSalesCostBean;
import com.avnet.emasia.webquote.quote.vo.QuoteBuilderSalesCostBean;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.util.DeploymentConfiguration;
import com.avnet.emasia.webquote.utilites.web.util.Excel20007Reader;
import com.avnet.emasia.webquote.utilites.web.util.NonSalesCostHeaderProcessSheet;
import com.avnet.emasia.webquote.utilites.web.util.POIUtils;
import com.avnet.emasia.webquote.utilites.web.util.SalesCostHeaderProcessSheet;
import com.avnet.emasia.webquote.utilities.bean.ExcelAliasBean;
import com.avnet.emasia.webquote.web.quote.constant.QuoteConstant;

public class QuoteBuiderValidator {
	
	private static final Logger LOGGER =Logger.getLogger(QuoteBuiderValidator.class.getName());
	
	public static boolean isSelectQuoteCostType(String quoteCostType) {
		return QuoteConstant.SALES_COST.equals(quoteCostType) || QuoteConstant.NON_SALES_COST.equals(quoteCostType);
	}
	
	public static boolean isValidUploadFile(UploadedFile uploadFile) {
		return !(uploadFile == null);
	}
	
	public static boolean isExcelFile(UploadedFile uploadFile){
		String fileName = uploadFile.getFileName();
		if(fileName ==null) return false;
		String suffixName = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length());
		return ("xls".equalsIgnoreCase(suffixName)|| "xlsx".equalsIgnoreCase(suffixName));
	}
	
	public static boolean isValidTemplate(UploadedFile uploadQuoteFile,String quoteCostType,String templateName) {
		FileInputStream input = null;
		//String filePath ="C:/Users/046755/Desktop/excel_quoteBulider"+ File.separator + templateName;
		// new path @046755   \WQ-QB_NonSalesCost_Template.xlsx
		String filePath ="C:\\excel\\qb"+ File.separator + templateName;
		//String filePath = DeploymentConfiguration.configPath + File.separator + templateName;
		//filePath = "C:/Lenon/temp" + File.separator + templateName;
		Workbook templateWb = null;
		Boolean bool = true;
		Excel20007Reader reader = null;
		List<ExcelAliasBean> excelAliasBeanList = null;
		Object headerBean = null;
		Object templateBean= null;
		try {
			input = new FileInputStream(filePath);
			templateWb =WorkbookFactory.create(input);
			Sheet templateSheet = templateWb.getSheetAt(0);
			FormulaEvaluator evaluator = templateSheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
			Row templateFristRow = templateSheet.getRow(2);

			Cell templateCell = null;
			String filedName = null;
			
			if(QuoteConstant.SALES_COST.equals(quoteCostType)) {
				excelAliasBeanList = POIUtils.getQuoteBuilderExcelAliasAnnotation(QuoteBuilderSalesCostBean.class);
				reader = new Excel20007Reader(uploadQuoteFile, 0,new SalesCostHeaderProcessSheet());
				templateBean = new QuoteBuilderSalesCostBean();
				if(templateFristRow!=null){
					for(int i = 0;i < 30;i++){
						templateCell = templateFristRow.getCell(i);
						filedName = excelAliasBeanList.get(i).getFieldName();
						POIUtils.setter(templateBean,filedName,POIUtils.getCellValueWithFormatter(templateCell,evaluator),String.class);
					}
				}
			}else {
				excelAliasBeanList = POIUtils.getQuoteBuilderExcelAliasAnnotation(QuoteBuilderNonSalesCostBean.class);
 				reader = new Excel20007Reader(uploadQuoteFile, 0,new NonSalesCostHeaderProcessSheet());
				templateBean = new QuoteBuilderNonSalesCostBean();
				if(templateFristRow!=null){
					for(int i=0;i < 28;i++){
						templateCell = templateFristRow.getCell(i);
						filedName = excelAliasBeanList.get(i).getFieldName();
						POIUtils.setter(templateBean,filedName,POIUtils.getCellValueWithFormatter(templateCell,evaluator),String.class);
						
					}
				}
			}
			headerBean  = reader.getHeaderBean();
			String headerValue= null;
			String templateValue = null;
			for (ExcelAliasBean excelAliasBean : excelAliasBeanList) {
				headerValue = POIUtils.getter(headerBean, excelAliasBean.getFieldName());
				templateValue = POIUtils.getter(templateBean, excelAliasBean.getFieldName());
				if ((templateValue == null && headerValue != null) || (templateValue != null && headerValue == null)) {
					bool = false;
					break;
				} else {
					if (!StringUtils.equalsIgnoreCase(templateValue, headerValue)) {
						bool = false;
						break;
					}

				}

			}
				
			
		} catch (IOException | InvalidFormatException e) {
			 bool = false;
			 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
					 ResourceMB.getText("wq.message.excelFileFormat")+".", ""));
			LOGGER.log(Level.SEVERE, "Exception occurred for file: "+uploadQuoteFile.getFileName()+", Exception message: "+e.getMessage(), e);
		}finally{
			try {
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Exception occurred for file: " + uploadQuoteFile.getFileName()
						+ ", Exception message: " + e.getMessage(), e);
			}
			input = null;
		}
		return bool;
		
	}
	
	
	public static boolean isExceedAllowMaxNum(int sheetLastRowNum,int allowMaxUploadNum){
		return (sheetLastRowNum > allowMaxUploadNum);
	}
}
