package com.avnet.emasia.webquote.web.quote.job;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.Region;
import org.jboss.logmanager.Level;

import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.web.pricerupload.PricerUploadMB;


/**
 * @author 906893
 * @createdOn 20130424
 */
public class DownloadUtil {
	private static final Logger LOG = Logger.getLogger(DownloadUtil.class
			.getName());
    private static int intFormat = 0;
    private static int doubleFormat = 1;
    private static int stringFormat = 2;
    private static final String FORMMAT_ONE = "#,##0.00000";
    private static final String FORMMAT_TWO = "#,##";
    
    private static final String TEMPLATE1 = "-999999";
    private static final String TEMPLATE2 = "0.0";
    private static final String TEMPLATE3 = "-999999.0";
    private static final String TEMPLATE4 = "null";
    private static final String TEMPLATE5 = "0";
    private static final String TEMPLATE_BLANK = "";
    
    private static final String TEMPLATE_GET = "get";
    
    public static HSSFWorkbook  buildWorkBook(){
        HSSFWorkbook wb = new HSSFWorkbook();
        return wb;
    }
    
    public static HSSFSheet buildSheet(HSSFWorkbook workbook,String sheetName){
        HSSFSheet sheet = workbook.createSheet(sheetName);
        return sheet;
    }
    
    public static HSSFCellStyle setCellStyleHeader(HSSFWorkbook workbook){
        HSSFCellStyle style = workbook.createCellStyle();
//        style.setDataFormat(HSSFDataFormat.getBuiltinFormat("($#,##0_);[Red]($#,##0)"));
        style.setFillBackgroundColor(HSSFColor.AQUA.index);
        style.setFillPattern(HSSFCellStyle.BIG_SPOTS);
        return style;
    }
    
    public static HSSFCellStyle createDecimalCellFormat(HSSFWorkbook  workbook){
        HSSFCellStyle styleDecimalFormat = null;
        HSSFDataFormat format = workbook.createDataFormat();
        styleDecimalFormat = workbook.createCellStyle();
        styleDecimalFormat.setDataFormat(format.getFormat("#,##0.0000"));
        return styleDecimalFormat;
    }
    
    public static HSSFCellStyle getDefaultCellStyle(HSSFWorkbook workbook){
        
        HSSFCellStyle style = workbook.createCellStyle();
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM_DASHED);
        
        return style;
    }
    
    public static HSSFSheet addColorBar(HSSFSheet sheet,HSSFCellStyle cellStyle,int rowIndex,int columnCount){
        HSSFRow row = sheet.createRow(rowIndex);
        
        cellStyle.setFillBackgroundColor(HSSFColor.AQUA.index);
        cellStyle.setFillPattern(HSSFCellStyle.BIG_SPOTS);
        
        HSSFCell cell = null;
        int cellLength = columnCount;
        for( int i=0;i<cellLength;i++ ){
            cell = row.createCell((short)i);
            cell.setCellStyle(cellStyle);
        }
        
	    return sheet;
    }
    
    /**
     * As mergedColumnCount the the length of title name, but the length of excel file is started with 0, but not 1,
     * so here, you need to minus one from the length of mergedColumnCount
     * @param sheet
     * @param mergedRowCount
     * @param mergedColumnCount
     * @param headerName
     * @param headerStyle
     * @param font
     * @return
     */
    public static HSSFSheet addFileHeader(HSSFSheet sheet,int mergedRowCount,int mergedColumnCount,String headerName,HSSFCellStyle headerStyle,HSSFFont font){
        HSSFRow headerRow = sheet.createRow(0);
        --mergedColumnCount;
        sheet.addMergedRegion(new Region(0, (short) 0, mergedRowCount, (short) mergedColumnCount));
        HSSFCell headerCell =  headerRow.createCell((short) 0);
        headerCell.setCellValue(headerName);
        headerStyle.setAlignment( HSSFCellStyle.ALIGN_CENTER);
        headerCell.setCellStyle(headerStyle);
        
        if(font!=null){
            font.setColor(HSSFFont.COLOR_RED);
            font.setBoldweight((short)5);
            headerStyle.setFont(font);
        }
        headerStyle.setFillBackgroundColor(HSSFColor.BLUE_GREY.index);

        
        return sheet;
    }
    
    public static HSSFSheet setColumnWidth(HSSFSheet sheet,int columnWidth,int columnSize){
        if(columnSize>0){
            for( int i=0;i<columnSize;i++ ){
                sheet.setColumnWidth((short)i,(short)columnSize);
            }
        }
        

	    return sheet;
    }    
    
    public static HSSFSheet addSheetHeader(HSSFSheet sheet,Map headerNameMap){
        
        HSSFRow row = sheet.createRow(0);
        Set headerSet = headerNameMap.keySet();
        Iterator iter = headerSet.iterator();
        int cellIndex = 0;
        HSSFCell cell = null;
        while(iter.hasNext()){
            String headerName = (String)iter.next();
            cell = row.createCell((short)cellIndex++);
            cell.setCellValue(headerName);
        }
        
	    return sheet;
    }
    
    public static HSSFSheet addSheetHeader(HSSFWorkbook workbook,HSSFSheet sheet,int rowIndex,String[] templateArray){
        
        return DownloadUtil.addSheetHeader(workbook,sheet,rowIndex,templateArray,true,null);
    }
    
    public static HSSFSheet addSheetHeader(HSSFWorkbook workbook,HSSFSheet sheet,int rowIndex,String[] templateArray,boolean isDefaultCellStyle,HSSFCellStyle userCellStyle){
        
        HSSFRow row = sheet.createRow(rowIndex);
        HSSFCellStyle cellStyle = null;
        if(isDefaultCellStyle)
            cellStyle = DownloadUtil.getDefaultCellStyle(workbook);
        else
            cellStyle = userCellStyle;
        HSSFCell cell = null;
//      Style the cell with borders all around.
        
        int cellLength =templateArray.length;
        for( int i=0;i<cellLength;i++ ){
            cell = row.createCell((short)i);
            cell.setCellValue(templateArray[i]);
            cell.setCellStyle(cellStyle);
        }
        
	    return sheet;
    }
    
    private HSSFSheet buildRowWithBean(HSSFSheet sheet,String classFullName,Map cellMap){
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = null;
        int cellSize = cellMap.size();
	   	for(int i=0; i<cellSize; i++){    
	   	    cell = row.createCell((short)i);
//			cell.setCellValue(getFieldValue(tempRsr,
//			        (String)Constant.RFQ_SUBMISSION_SINGLE_RESULT_HEADER.get(Constant.RFQ_SUBMISSION_SINGLE_RESULT_HEADER_SEQ[i]))); 
		 } 
        
        return sheet;
    }
    
    public static HSSFSheet sheetBuilder(String sheetName,Object bean,Map templateMap){
        HSSFWorkbook workbook = DownloadUtil.buildWorkBook();
        HSSFSheet sheet = DownloadUtil.buildSheet(workbook,sheetName);
        
        // build the header of file
//        sheet = downloadUtil.buildSheetHeader(sheet,templateMap);
//        sheet = downloadUtil.buildSheetHeader(sheet,Constant.SALE_DOWNLOAD_TEMPLATE_HEADER);
        
        return sheet;
    }
    
    public static HSSFSheet sheetBuilder(String sheetName,Object bean){
        HSSFWorkbook workbook = DownloadUtil.buildWorkBook();
        HSSFSheet sheet = DownloadUtil.buildSheet(workbook,sheetName);
        
        // build the header of file
//        sheet = downloadUtil.buildSheetHeader(sheet,templateMap);
//        sheet = downloadUtil.buildSheetHeader(sheet,Constant.SALE_DOWNLOAD_TEMPLATE_HEADER);
        
        return sheet;
    }
    
    public static HSSFRow addRow(HSSFRow row,Object bean,Map cellNameMap){

        try{
            Set set = cellNameMap.keySet();
            Iterator iter = set.iterator();
            
            Class clazz = bean.getClass();
            
            HSSFCell cell = null;
            int index = 0;
            while(iter.hasNext()){
                String key = (String)iter.next();
                String fieldName = (String)cellNameMap.get(key);
                fieldName = fieldName.substring(0,1).toUpperCase()+fieldName.substring(1,fieldName.length());
                String methodName = TEMPLATE_GET+fieldName;
    			Method  m = clazz.getDeclaredMethod(methodName,new Class[]{});
    			String value = (String) m.invoke(bean, new Object []{});
    			
    			//convert the default value
    			if(TEMPLATE1.equalsIgnoreCase(value))
    			{
    			    value = TEMPLATE_BLANK;
    			}
    			else if(TEMPLATE2.equalsIgnoreCase(value))
    			{
    			    value = TEMPLATE_BLANK;
    			}
    			else if(TEMPLATE3.equalsIgnoreCase(value))
    			{
    			    value = TEMPLATE_BLANK;
    			}
    			else if(TEMPLATE4.equalsIgnoreCase(value))
    			{
    			    value = TEMPLATE_BLANK;
    			}
    			
    			row.createCell((short)index);
    			//cell.setCellValue(value);
            }
            

        }catch(Exception e){
        	LOG.log(Level.SEVERE, "exception in adding row in excel , exception message : "+e.getMessage(), e);
        }
        
        return row;
    }

    public static HSSFRow addRow(HSSFRow row,Object bean,String[] templateArray){
        try{
            Class clazz = bean.getClass();
            int cellLength = templateArray.length;
            HSSFCell cell = null;
            for( int i=0;i<cellLength;i++ ){
                String fieldName = templateArray[i];
                fieldName = fieldName.substring(0,1).toUpperCase()+fieldName.substring(1,fieldName.length());
                String methodName = TEMPLATE_GET+fieldName;
    			Method  m = clazz.getDeclaredMethod(methodName,new Class[]{});
    			String value = (String) m.invoke(bean, new Object []{});
    			
    			//convert the default value
    			if(TEMPLATE1.equalsIgnoreCase(value))
    			{
    			    value = TEMPLATE5;
    			}
    			else if(TEMPLATE2.equalsIgnoreCase(value))
    			{
    			    value = TEMPLATE5;
    			}
    			else if(TEMPLATE3.equalsIgnoreCase(value))
    			{
    			    value = TEMPLATE5;
    			}
    			else if(TEMPLATE4.equalsIgnoreCase(value))
    			{
    			    value = TEMPLATE5;
    			}
    			
    			cell = row.createCell((short)i);
    			cell.setCellValue(value);

            }

        }catch(Exception e){
        	LOG.log(Level.SEVERE, "exception in adding row in excel , exception message : "+e.getMessage(), e);
        }
        
        return row;
    }
    
    public static HSSFRow addRow(HSSFWorkbook workbook,HSSFRow row,Object bean,String[] templateArray,int[] dataTypeArray){
    	int dataTypeIndicator = -1;
    	int lineNumber = -1;
    	String methName = "";
    	HSSFCell cell = null;
    	
        try
        {
            
        	Class clazz = bean.getClass();
            int cellLength = templateArray.length;
            HSSFDataFormat format = workbook.createDataFormat();
            HSSFDataFormat format2 = workbook.createDataFormat();
            short dataType = format.getFormat(FORMMAT_ONE);
            short dataTypeInt = format2.getFormat(FORMMAT_TWO);
            HSSFCellStyle style = workbook.createCellStyle();
            HSSFCellStyle style2 = workbook.createCellStyle();            
            for( int i=0;i<cellLength;i++ )
            {
            	lineNumber = i;
                String fieldName = templateArray[i];
                fieldName = fieldName.substring(0,1).toUpperCase()+fieldName.substring(1,fieldName.length());
                String methodName = TEMPLATE_GET+fieldName;
                methName = methodName;
    			Method  m = clazz.getDeclaredMethod(methodName,new Class[]{});
    			String value = (String) m.invoke(bean, new Object []{});
    			
    			//convert the default value
    			if(TEMPLATE1.equalsIgnoreCase(value))
    			{
    			    value = TEMPLATE5;
    			}
    			else if(TEMPLATE2.equalsIgnoreCase(value))
    			{
    			    value = TEMPLATE5;
    			}
    			else if(TEMPLATE3.equalsIgnoreCase(value))
    			{
    			    value = TEMPLATE5;
    			}
    			else if(TEMPLATE4.equalsIgnoreCase(value))
    			{
    			    value = TEMPLATE5;
    			}
    			
    			cell = row.createCell((short)i);
    			
    			dataTypeIndicator = dataTypeArray[i];
    			if(dataTypeIndicator == intFormat || dataTypeIndicator == doubleFormat)
    			{
    				//it means it should be numeric data type
    			    if(value!=null && value.length()>0)
    			    {
        			    //for data convert,you need to change it with the data format indicator
        			    if(dataTypeIndicator == intFormat)
        			    {
        			        style.setDataFormat(dataTypeInt);
            			    cell.setCellStyle(style);
        			        cell.setCellValue(Integer.parseInt(value));
        			    }
        			    else if(dataTypeIndicator == doubleFormat)
        			    {
        			        style2.setDataFormat(dataType);
            			    cell.setCellStyle(style2);
        			        cell.setCellValue(Double.parseDouble(value));
        			    }
    			    }
    			}
    			else
    			{
    			    cell.setCellValue(value);
    			}
    			
            }

        }
        catch(Exception e)
        {
           
            LOG.log(Level.SEVERE, "Wrong Line " + lineNumber + " |type is : " + dataTypeIndicator + "|Method Name: " + methName+", Exception message: "+MessageFormatorUtil.getParameterizedStringFromException(e), e);
        }
//        finally
//        {
//        	if(cell!=null)
//        	{
//        		cell = null;
//        	}
//        }
        
        return row;
    }    
    
    public static HSSFSheet setAutoSizeColumns(HSSFSheet sheet,int columnSize){
        if(sheet == null)
            return sheet;
        
        if(columnSize>0){
            for(int i=0;i<columnSize;i++ ){
                sheet.autoSizeColumn(i); //adjust width of the first column
            }
        }
        return sheet;
    }
	
}
