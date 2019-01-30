package com.avnet.emasia.webquote.utilites.web.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.StylesTable;
import org.primefaces.model.UploadedFile;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
    
public class Excel20007Reader { 
	static final Logger LOG =Logger.getLogger(Excel20007Reader.class.getSimpleName());
  
    private ProcessSheetInterface handlerInterface;  
    private OPCPackage xlsxPackage;  
    private int minColumns;  
     
  
    /** 
     * Creates a new XLSX -> CSV converter 
     *  
     * @param pkg  The XLSX package to process 
     * @param output  The PrintStream to output the CSV to 
     * @param minColumns  The minimum number of columns to output, or -1 for no minimum 
     */  
    public Excel20007Reader(UploadedFile uploadFile,int sheetNo,ProcessSheetInterface handlerInterface) {  
    	OPCPackage excelPackage = null;     
        this.minColumns = minColumns;  
        this.handlerInterface = handlerInterface;  
		try {
			excelPackage = OPCPackage.open(uploadFile.getInputstream());
			 this.xlsxPackage = excelPackage;  

			process(sheetNo);
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, "exception in excel sheer number reading, sheet number : "+sheetNo+" , upload file: "+uploadFile.getFileName()+" exception message : "+ex.getMessage(), ex);
		}
    }  

    public Excel20007Reader(InputStream inputstream,int sheetNo,ProcessSheetInterface handlerInterface) {  
    	OPCPackage excelPackage = null;     
        this.minColumns = minColumns;  
        this.handlerInterface = handlerInterface;  
		try {
			excelPackage = OPCPackage.open(inputstream);
			 this.xlsxPackage = excelPackage;  

			process(sheetNo);
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, "exception in excel sheer number reading, sheet number : "+sheetNo+" , exception message : "+ex.getMessage(), ex);
		}
    }  

	public int getCountrows() {
		return handlerInterface.getCountrows()+1;
	}	
	public Object getHeaderBean() {
		return handlerInterface.getHeaderBean();
	}
    public List excel2Beans(Comparator c){
    	List mdtList = handlerInterface.getBeanList();
    	if(c != null) {
    		Collections.sort(mdtList, c);
    	}
    	return mdtList;
    }
    /** 
     * Parses and shows the content of one sheet using the specified styles and 
     * shared-strings tables. 
     *  
     * @param styles 
     * @param strings 
     * @param sheetInputStream 
     */  
    public void processSheet(StylesTable styles,  
            ReadOnlySharedStringsTable strings, InputStream sheetInputStream)  
            throws IOException, ParserConfigurationException, SAXException {  
  
        InputSource sheetSource = new InputSource(sheetInputStream);  
        SAXParserFactory saxFactory = SAXParserFactory.newInstance();  
        SAXParser saxParser = saxFactory.newSAXParser();  
        XMLReader sheetParser = saxParser.getXMLReader();  
        ContentHandler handler = new XSSFSheetHandler(styles, strings, this.minColumns, handlerInterface, minColumns);  
        sheetParser.setContentHandler(handler);  
        sheetParser.parse(sheetSource);  
    }  
  
    /** 
     * Initiates the processing of the XLS workbook file to CSV. 
     *  
     * @throws IOException 
     * @throws OpenXML4JException 
     * @throws ParserConfigurationException 
     * @throws SAXException 
     */  
    public void process(int sheetNo) throws IOException, OpenXML4JException,  
            ParserConfigurationException, SAXException {  
  
        ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(  this.xlsxPackage);  
        XSSFReader xssfReader = new XSSFReader(this.xlsxPackage);  
  
        StylesTable styles = xssfReader.getStylesTable();  
        XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader  
                .getSheetsData();  
        int index = 0;  
        while (iter.hasNext()) {  
            InputStream stream = iter.next();  
			if (index == sheetNo) {
				processSheet(styles, strings, stream);
			}
            stream.close();  
            ++index;  
        }  
    }  

} 
