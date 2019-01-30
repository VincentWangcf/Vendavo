package com.avnet.emasia.webquote.utilites.web.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.primefaces.model.UploadedFile;

import com.avnet.emasia.webquote.commodity.bean.PricerUploadTemplateBean;
import com.avnet.emasia.webquote.commodity.constant.PricerConstant;
import com.avnet.emasia.webquote.commodity.util.StringUtils;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilities.DateUtils;
import com.avnet.emasia.webquote.utilities.bean.ExcelAlias;
import com.avnet.emasia.webquote.utilities.bean.ExcelAliasBean;
import com.avnet.emasia.webquote.utilities.bean.PricerExcelAlias;
import com.avnet.emasia.webquote.utilities.bean.QuoteBuilderExcelAlias;

public class POIUtils {
	static final Logger LOGGER= Logger.getLogger(POIUtils.class.getSimpleName());

	public static boolean isValidateFileColumn(UploadedFile uploadFile){

		PricerHeaderProcessSheet pricerHeaderProcessSheet = new PricerHeaderProcessSheet();
		Excel20007Reader reader = new Excel20007Reader(uploadFile, 0,pricerHeaderProcessSheet);
		PricerUploadTemplateBean headerBean = (PricerUploadTemplateBean)reader.getHeaderBean();
		PricerUploadTemplateBean pricerUploadTemplateBean = new PricerUploadTemplateBean();
		List<ExcelAliasBean> excelAliasBeanList = getPricerExcelAliasAnnotation(PricerUploadTemplateBean.class);
		FileInputStream input = null;
		String filePath = DeploymentConfiguration.configPath+File.separator+PricerConstant.PRICER_TEMPLATE_NAME;
		//need delete
		/*//TODO
		try { 
           String address = InetAddress.getLocalHost().getHostName().toString();
           if("cis2115vmts".equalsIgnoreCase(address)) { 
        	   filePath = "C:\\david\\sharefolder\\tempd"+File.separator+PricerConstant.PRICER_TEMPLATE_NAME;
           }
        } catch (Exception e) { 
            e.printStackTrace(); 
        } */
		
		Workbook templateWb = null;
		Boolean bool = true;

		try {
			input = new FileInputStream(filePath);
			
			if(input==null) return false;
			
			templateWb =WorkbookFactory.create(input);

			Sheet templateSheet = templateWb.getSheetAt(0);
			FormulaEvaluator evaluator = templateSheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
			Row templateFristRow = templateSheet.getRow(PricerConstant.PRICER_TEMPLATE_HEADER_ROW_NUMBER-1);

			Cell templateCell = null;
			String filedName = null;
			if(templateFristRow!=null){
				for(int i=0;i<PricerConstant.COLUMN_OF_LENGTH;i++){
					templateCell = templateFristRow.getCell(i);
					filedName = excelAliasBeanList.get(i).getFieldName();
					POIUtils.setter(pricerUploadTemplateBean,filedName,POIUtils.getCellValueWithFormatter(templateCell,evaluator),String.class);
					
				}
			}
		} catch (IOException|InvalidFormatException e) {
			 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					 ResourceMB.getText("wq.message.excelFileFormat")+".", ""));
			LOGGER.log(Level.SEVERE, "Exception occurred for file: "+uploadFile.getFileName()+", Exception message: "+e.getMessage(), e);
		}finally{
			try {
				if(input != null){
				input.close();
				}
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Exception occurred for file: "+uploadFile.getFileName()+", Exception message: "+e.getMessage(), e);
			}
			input = null;
		}
		
		String headerValue= null;
		String templateValue = null;
		for(ExcelAliasBean excelAliasBean:excelAliasBeanList){
			headerValue = POIUtils.getter(headerBean, excelAliasBean.getFieldName());
			templateValue = POIUtils.getter(pricerUploadTemplateBean, excelAliasBean.getFieldName());
			/*if(templateValue == null ||headerValue==null){
				int i= 0;
				////("");
				System.out.println("null");
			}
			headerValue = POIUtils.getter(headerBean, excelAliasBean.getFieldName());
			templateValue = POIUtils.getter(pricerUploadTemplateBean, excelAliasBean.getFieldName());*/
			
			if((templateValue==null&&headerValue!=null)||(templateValue!=null&&headerValue==null)){
				bool = false;
				break;
			}else{
				if(!templateValue.equalsIgnoreCase(headerValue)){
					bool = false;
					break;
				}
				 
			}
			
		}
		return bool;
	}
	
	public static String getCellValueWithFormatter(Cell cell,FormulaEvaluator evaluator) {
		String returnStr = null;
		if(cell ==null) return "";
		if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
			returnStr = cell.getStringCellValue();
		} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				returnStr =DateUtils.formateDate2String(cell.getDateCellValue(), "dd/MM/yyyy");				
			} else {
				returnStr = StringUtils.numberFormatter(cell.getNumericCellValue());
			}
		} else if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
			returnStr = String.valueOf(cell.getBooleanCellValue());
		} else if (cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
			returnStr = "";
		}
		if(returnStr!=null){
			returnStr = returnStr.trim();
		}
		return returnStr;
	}

    public static void setter(Object obj, String att, Object value, Class<?> type) {
        try {
            Method method = obj.getClass().getMethod("set" + initStr(att), type); //get set method
            method.invoke(obj, value); //invoke set method
        } catch (Exception e) {
        	LOGGER.log(Level.SEVERE, "Exception occurred for att: "+att+", Exception message: "+e.getMessage(), e);
        }
    }
    
    public static String getter(Object obj,String att){  
    	String value = null;
        try{  
            Method met = obj.getClass().getMethod("get"+initStr(att)) ; //get get method
            value = (String) met.invoke(obj) ;   //invoke get method
        }catch(Exception e){  
        	LOGGER.log(Level.SEVERE, "Exception occurred for att: "+att+", Exception message: "+e.getMessage(), e); 
        }  
        return value;
    }  
    
    public static String initStr(String old){   // To capitalize the first letter of the word
        String str = old.substring(0,1).toUpperCase() + old.substring(1) ;  
        return str ;  
    } 
  
	public static TreeSet<ExcelAliasBean> getExcelAliasAnnotation(Class<?> beanClass ){
		TreeSet<ExcelAliasBean> treeSet = new TreeSet<ExcelAliasBean>(new ExcelAliasComparator());
	
		Field[] fields = beanClass.getDeclaredFields();
	
		for(Field f :fields){
			f.setAccessible(true);
			ExcelAlias annontation = f.getAnnotation(ExcelAlias.class);
			if(annontation==null) continue;

			ExcelAliasBean bean = new ExcelAliasBean();
			bean.setAliasName( annontation.name());
			bean.setCellNum(QuoteUtil.convertToInteger(annontation.cellNum()));
			bean.setFieldName(f.getName());
			treeSet.add(bean);
		}
		return treeSet;
	}
	
	public static List<ExcelAliasBean> getQuoteBuilderExcelAliasAnnotation(Class<?> beanClass ){
		List<ExcelAliasBean> treeSet = new ArrayList<ExcelAliasBean>();
	
		Field[] fields = beanClass.getDeclaredFields();
	
		for(Field f :fields){
			f.setAccessible(true);
			QuoteBuilderExcelAlias annontation = f.getAnnotation(QuoteBuilderExcelAlias.class);
			if(annontation==null) continue;

			ExcelAliasBean bean = new ExcelAliasBean();
			bean.setAliasName(annontation.name());
			bean.setCellNum(QuoteUtil.convertToInteger(annontation.cellNum()));
			bean.setFieldLength(QuoteUtil.convertToInteger(annontation.fieldLength()));
			bean.setFieldName(f.getName());
			treeSet.add(bean);
		}
		Collections.sort(treeSet,new ExcelAliasComparator());
		return treeSet;
	}
	
	
	public static List<ExcelAliasBean> getPricerExcelAliasAnnotation(Class<?> beanClass ){
		List<ExcelAliasBean> treeSet = new ArrayList<ExcelAliasBean>();
	
		Field[] fields = beanClass.getDeclaredFields();
	
		for(Field f :fields){
			f.setAccessible(true);
			PricerExcelAlias annontation = f.getAnnotation(PricerExcelAlias.class);
			if(annontation==null) continue;

			ExcelAliasBean bean = new ExcelAliasBean();
			bean.setAliasName( annontation.name());
			bean.setCellNum(QuoteUtil.convertToInteger(annontation.cellNum()));
			bean.setFieldName(f.getName());
			bean.setFieldLength(QuoteUtil.convertToInteger(annontation.fieldLength()));
			String mandatory = annontation.mandatory();
			if(mandatory!=null){
				String[] temp = mandatory.split(",");
				if(temp.length==6){
					if(temp[0].equals("true")){
						bean.setNormalMandatory(true);
					}
					if(temp[1].equals("true")){
						bean.setProgramMandatory(true);
					}
					if(temp[2].equals("true")){
						bean.setContractMandatory(true);
					}
					if(temp[3].equals("true")){
						bean.setPastMasterMandatory(true);
					}
					if(temp[4].equals("true")){
						bean.setSimplePricer(true);
					}
					if(temp[5].equals("true")){
						bean.setSalesCostPricer(true);
					}
				}
			}
			treeSet.add(bean);
		}
		Collections.sort(treeSet,new ExcelAliasComparator());
		return treeSet;
	}

	static class ExcelAliasComparator implements Comparator<ExcelAliasBean> {

		public int compare(ExcelAliasBean f1, ExcelAliasBean f2) {

			if (f1.getCellNum() > f2.getCellNum()) {
				return 1;
			} else if (f1.getCellNum() == f2.getCellNum()) {
				return 0;
			} else {
				return -1;
			}
		}
	}		
}
