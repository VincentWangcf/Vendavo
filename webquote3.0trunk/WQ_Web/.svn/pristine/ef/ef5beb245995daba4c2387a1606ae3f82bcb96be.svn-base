package com.avnet.emasia.webquote.web.datatable.export;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jboss.logmanager.Level;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.util.Constants;

import com.avnet.emasia.webquote.utilites.web.util.ExcelUtil;
import com.avnet.emasia.webquote.utilites.web.util.POICellColor;

//TODO: check text left on cell
public class XSSFExcelExporter extends ExtExporter {
	static final Logger LOGGER=Logger.getLogger(XSSFExcelExporter.class.getSimpleName());

	private String[] exportOrder = null;
    @Override
	public void export(FacesContext context, DataTable table, String filename, boolean pageOnly, boolean selectionOnly, String encodingType,MethodExpression exportOrderME, MethodExpression preProcessor, MethodExpression postProcessor) throws IOException {    	
    	HSSFWorkbook wb = new HSSFWorkbook();
    	HSSFSheet sheet = wb.createSheet();
    	
    	if(exportOrderME != null){   
    		try{
    			StringBuilder sb = new StringBuilder();
    			exportOrderME.invoke(context.getELContext(), new Object[]{sb});
    			if(sb.length() > 0){
    				exportOrder = sb.toString().split(",");
    			}
    		}catch(Exception e){
    			LOGGER.log(Level.SEVERE, "Exception in exporting file : "+ filename+" , exception message : "+e.getMessage(), e);
    		}
    		addColumnFacets(sheet);
    	}else{
    		addColumnFacets(table, sheet, ColumnType.HEADER);
    	}
    	//select 200 records once when export all data,add this logic fix write time out exception
    	int currentRow = table.getRows();
    	if(!selectionOnly) {
    		table.setRows(200);
    	}
    	exportAll(context, table, sheet);
    	
    	table.setRowIndex(-1);
            	
    	if(postProcessor != null) {
    		postProcessor.invoke(context.getELContext(), new Object[]{wb});
    	}
    	for(int i=0;i<sheet.getRow(0).getPhysicalNumberOfCells();i++){
    		sheet.autoSizeColumn(i, true);
    	}
    	table.setRows(currentRow);
    	writeExcelToResponse(context.getExternalContext(), wb, filename);
	}
	  
    protected void exportAll(FacesContext context, DataTable table, HSSFSheet sheet) {
        int first = table.getFirst();
    	int rowCount = table.getRowCount();
        int rows = table.getRows();
        boolean lazy = table.isLazy();
        CellStyle style  =  ExcelUtil.createStyle(sheet.getWorkbook(), POICellColor.NORMAL);
        if(lazy) {
            for(int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                if(rowIndex % rows == 0) {
                    table.setFirst(rowIndex);
                    table.loadLazyData();
                }

                exportRow(table, sheet,style, rowIndex);
            }
     
            table.setFirst(first);
            table.loadLazyData();
        } 
        else {
            for(int rowIndex = 0; rowIndex < rowCount; rowIndex++) {                
                exportRow(table, sheet,style, rowIndex);
            }
            
            table.setFirst(first);
        }
    }

    protected void exportRow(DataTable table, HSSFSheet sheet,CellStyle style, int rowIndex) {
        table.setRowIndex(rowIndex);
        
        if(!table.isRowAvailable()) {
            return;
        }
        
        exportCells(table, sheet,style);
    }
    
    protected void exportCells(DataTable table, Object obj,CellStyle style) {
    	HSSFSheet sheet = (HSSFSheet)obj;
    	int sheetRowIndex = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(sheetRowIndex);
        row.setHeight((short)300);
        
        for(UIColumn col : table.getColumns()) {
            if(!col.isRendered()) {
                continue;
            }
            
            if(col.isExportable()) {
            	if(exportOrder != null){
            	String columnHeader = col.getHeaderText(); 
            	
	            	for(int i = 0;i<exportOrder.length;i++){
		            	if(columnHeader.equals(exportOrder[i])){
		            		addColumnValue(row, col.getChildren(),i,style);
		            		break;
		            	}
	            	}
            	}else{
            		addColumnValue(row, col.getChildren());
            	}
            }
        }
    }
    
	protected void addColumnFacets(HSSFSheet sheet) {
        Row rowHeader = sheet.createRow(0);
        CellStyle style =  ExcelUtil.createStyle(sheet.getWorkbook(), POICellColor.NORMAL);
        for(int i = 0;i< exportOrder.length;i++) {
        	 Cell cell = rowHeader.createCell(i);
             cell.setCellValue(exportOrder[i]);
             cell.setCellStyle(style);
        }
    }
	
	protected void addColumnFacets(DataTable table, Sheet sheet, ColumnType columnType) {
        int sheetRowIndex = columnType.equals(ColumnType.HEADER) ? 0 : (sheet.getLastRowNum() + 1);
		Row rowHeader = sheet.createRow(sheetRowIndex);
		
		for(UIColumn col : table.getColumns()) {
			if(!col.isRendered()) {
				continue;
			}
			
			if(col instanceof DynamicColumn) {
				((DynamicColumn) col).applyModel();
			}
			
			if(col.isExportable()) {
				addColumnValue(rowHeader, col.getFacet(columnType.facet()));
			}
		}
	}
	
    protected void addColumnValue(Row row, UIComponent component) {
        int cellIndex = row.getLastCellNum() == -1 ? 0 : row.getLastCellNum();
        Cell cell = row.createCell(cellIndex);
        String value = component == null ? "" : ExcelUtil.exportValue(FacesContext.getCurrentInstance(), component);

        cell.setCellValue(new HSSFRichTextString(value));
    }
    
    protected void addColumnValue(Row row, List<UIComponent> components) {
        int cellIndex = row.getLastCellNum() == -1 ? 0 : row.getLastCellNum();
        Cell cell = row.createCell(cellIndex);
        StringBuilder builder = new StringBuilder();
        FacesContext context = FacesContext.getCurrentInstance();
        
        for(UIComponent component : components) {
        	if(component.isRendered()) {
                String value = ExcelUtil.exportValue(context, component);
                
                if(value != null) 
                	builder.append(value);
            }
		}  
        
        cell.setCellValue(new HSSFRichTextString(builder.toString()));
    }
    
    protected void addColumnValue(Row row, List<UIComponent> components,int cellIndex,CellStyle style) {
    	Cell cell = row.createCell(cellIndex);
    	StringBuilder builder = new StringBuilder();
    	FacesContext context = FacesContext.getCurrentInstance();
    	
    	for(UIComponent component : components) {
    		if(component.isRendered()) {
    			String value = ExcelUtil.exportValue(context, component);
    			
    			if(value != null) 
    				builder.append(value);
    		}
    	}  
    	
    	cell.setCellValue(new HSSFRichTextString(builder.toString()));
    	
    	cell.setCellStyle(style);
    }
    
    protected void writeExcelToResponse(ExternalContext externalContext, Workbook generatedExcel, String filename) throws IOException {
    	externalContext.setResponseContentType("application/vnd.ms-excel");
    	externalContext.setResponseHeader("Expires", "0");
    	externalContext.setResponseHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
    	externalContext.setResponseHeader("Pragma", "public");
    	externalContext.setResponseHeader("Content-disposition", "attachment;filename="+ filename + ".xls");
    	externalContext.addResponseCookie(Constants.DOWNLOAD_COOKIE, "true", new HashMap<String, Object>());

        OutputStream out = externalContext.getResponseOutputStream();
        generatedExcel.write(out);
        externalContext.responseFlushBuffer();        
    }
}