package com.avnet.emasia.webquote.web.stm.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.SAXValidator;
import org.dom4j.io.XMLWriter;
import org.dom4j.util.XMLErrorHandler;
import org.xml.sax.SAXException;
 
public class ValidataXMLTest {
	static final Logger LOG= Logger.getLogger(ValidataXMLTest.class.getSimpleName());
	public static void main(String[] args) {
		String xmlFileName = "D:\\Users\\916487\\Documents\\Desktop\\word\\wwwwww\\RECIEVE_STM_WR2240416_20141223102431.xml";
		String xsdFileName = "D:\\Users\\916487\\Documents\\Desktop\\word\\wwwwww\\InBound.xsd";
		validateXMLByXSD(xmlFileName,xsdFileName);
	}

    /** 
     * ͨ��XSD��XML Schema��У��XML 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     * @throws DocumentException 
     */ 
    public static void validateXMLByXSD( String xmlFileName,  String xsdFileName)  { 

  
        try { 
            //����Ĭ�ϵ�XML�������� 
            XMLErrorHandler errorHandler = new XMLErrorHandler(); 
            //��ȡ���� SAX �Ľ�������ʵ�� 
            SAXParserFactory factory = SAXParserFactory.newInstance(); 
            //�������ڽ���ʱ��֤ XML ���ݡ� 
            factory.setValidating(true); 
            //ָ���ɴ˴������ɵĽ��������ṩ�� XML ���ƿռ��֧�֡� 
            factory.setNamespaceAware(true); 
            //ʹ�õ�ǰ���õĹ����������� SAXParser ��һ����ʵ���� 
            SAXParser parser = factory.newSAXParser(); 
            //����һ����ȡ���� 
            SAXReader xmlReader = new SAXReader(); 
            //��ȡҪУ��xml�ĵ�ʵ�� 
            Document xmlDocument = (Document) xmlReader.read(new File(xmlFileName)); 
            //���� XMLReader �Ļ���ʵ���е��ض����ԡ����Ĺ��ܺ������б������ [url]http://sax.sourceforge.net/?selected=get-set[/url] ���ҵ��� 
            parser.setProperty( 
                    "http://java.sun.com/xml/jaxp/properties/schemaLanguage", 
                    "http://www.w3.org/2001/XMLSchema"); 
            parser.setProperty( 
                    "http://java.sun.com/xml/jaxp/properties/schemaSource", 
                    "file:" + xsdFileName); 
            //����һ��SAXValidatorУ�鹤�ߣ�������У�鹤�ߵ����� 
            SAXValidator validator = new SAXValidator(parser.getXMLReader()); 
            //����У�鹤�ߵĴ�������������������ʱ�����ԴӴ����������еõ�������Ϣ�� 
            validator.setErrorHandler(errorHandler); 
            //У�� 
            validator.validate(xmlDocument); 

            XMLWriter writer = new XMLWriter(OutputFormat.createPrettyPrint()); 
            //���������Ϣ��Ϊ�գ�˵��У��ʧ�ܣ���ӡ������Ϣ 
            if (errorHandler.getErrors().hasContent()) { 
                System.out.println("XML�ļ�ͨ��XSD�ļ�У��ʧ�ܣ�"); 
                writer.write(errorHandler.getErrors()); 
            } else { 
                System.out.println("Good! XML�ļ�ͨ��XSD�ļ�У��ɹ���"); 
            } 
        } catch (Exception ex) { 
            System.out.println("XML�ļ�: " + xmlFileName + " ͨ��XSD�ļ�:" + xsdFileName + "����ʧ�ܡ�\nԭ�� " + ex.getMessage()); 
            LOG.log(Level.SEVERE, ex.getMessage(),ex); 
        } 
    } 

}