package com.avnet.emasia.webquote.web.stm.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class Dom4jUtil {
	static final Logger LOG=Logger.getLogger(Dom4jUtil.class.getSimpleName());
	public static Document read(String fileName) throws DocumentException {
		SAXReader reader = new SAXReader();
		return reader.read(new File(fileName));
	}
	
	public static String getStringXml(Document doc) {
		return doc.asXML();
	}

	public static Document getDocumentXml(String xml) throws DocumentException {
		return DocumentHelper.parseText(xml);
	}

	public static void simpleWrite(Document doc, String fileName) throws IOException {
		FileWriter out = new FileWriter(fileName);
		doc.write(out);
	}

	public static void formatWrite(Document doc, String fileName) {
		try {
			FileWriter fw = new FileWriter(fileName);
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			XMLWriter xmlWriter = new XMLWriter(fw, format);
			xmlWriter.write(doc);
			xmlWriter.close();
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "EXception in format write for file : "+fileName+" , Exception message : "+e.getMessage(), e);
		}
	}

}