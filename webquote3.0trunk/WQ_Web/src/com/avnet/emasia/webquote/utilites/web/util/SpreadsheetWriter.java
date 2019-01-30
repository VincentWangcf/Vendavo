package com.avnet.emasia.webquote.utilites.web.util;

import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;

import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.util.CellReference;

import com.avnet.emasia.webquote.commodity.constant.PricerConstant;

public class SpreadsheetWriter {

	 private final Writer _out;
     private int rowNum;

     public SpreadsheetWriter(Writer out){
         _out = out;
     }

     public void beginSheet() throws IOException {
         _out.write("<?xml version=\"1.0\" encoding=\""+PricerConstant.XML_ENCODING+"\"?>" +
                 "<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">" );
         _out.write("<sheetData>\n");
     }

     public void endSheet() throws IOException {
         _out.write("</sheetData>");
         _out.write("</worksheet>");
     }

     /**
      * Insert a new row
      *
      * @param rownum 0-based row number
      */
     public void insertRow(int rownum) throws IOException {
         _out.write("<row spans='1:3'  r=\""+(rownum+1)+"\">\n");
         this.rowNum = rownum;
     }

     /**
      * Insert row end marker
      */
     public void endRow() throws IOException {
         _out.write("</row>\n");
     }

     public void createCell(int columnIndex, String value, int styleIndex) throws IOException {
         String ref = new CellReference(rowNum, columnIndex).formatAsString();
         _out.write("<c r=\""+ref+"\" t=\"inlineStr\"");
         if(styleIndex != -1) _out.write(" s=\""+styleIndex+"\"");
         _out.write(">");
         _out.write("<is><t>"+value+"</t></is>");
         _out.write("</c>");
     }

     public void createCell(int columnIndex, String value) throws IOException {
         createCell(columnIndex, value, -1);
     }

     public void createCell(int columnIndex, double value, int styleIndex) throws IOException {
         String ref = new CellReference(rowNum, columnIndex).formatAsString();
         _out.write("<c r=\""+ref+"\" t=\"n\"");
         if(styleIndex != -1) _out.write(" s=\""+styleIndex+"\"");
         _out.write(">");
         _out.write("<v>"+value+"</v>");
         _out.write("</c>");
     }

     public void createCell(int columnIndex, double value) throws IOException {
         createCell(columnIndex, value, -1);
     }

     public void createCell(int columnIndex, Calendar value, int styleIndex) throws IOException {
         createCell(columnIndex, DateUtil.getExcelDate(value, false), styleIndex);
     }
 }

