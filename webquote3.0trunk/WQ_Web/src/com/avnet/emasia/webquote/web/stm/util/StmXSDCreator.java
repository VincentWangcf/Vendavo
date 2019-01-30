package com.avnet.emasia.webquote.web.stm.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;

import com.avnet.emasia.webquote.stm.constant.StmConstant;

public class StmXSDCreator {
	static final Logger LOG=Logger.getLogger(StmXSDCreator.class.getSimpleName());

	public String createInBoundXsd()  {
		String inBoundFilePath = "D:\\Users\\916487\\Documents\\Desktop\\word\\wwwwww\\" +StmConstant.INBOUD_XSD;
		StmXmlProcessTemplate stmXmlProcess = new StmXmlProcessTemplate();

		Document documents = DocumentHelper.createDocument();
		Element documentsRoot = documents.addElement("xs:schema").addAttribute(StmConstant.VERSION, StmConstant.VERSIONNUM);
		documentsRoot.add(new Namespace(StmConstant.NAMESPACE, StmConstant.NAMESPACEADDRESS));
		Element elementItem= documentsRoot.addElement("xs:element")
				.addAttribute("name", "item");
		Element sequenceEle = elementItem.addElement("xs:complexType").addElement("xs:sequence");

		elementItem = stmXmlProcess.createInBoundXsd(sequenceEle);
		documents.setXMLEncoding(StmConstant.XMLENCODING);
		Dom4jUtil.formatWrite(documents,inBoundFilePath );
		return documents.asXML();
	}
	
	public String createOutBoundXsd(){
		String outBoundFilePath = "D:\\Users\\916487\\Documents\\Desktop\\word\\wwwwww\\" +StmConstant.OUTBOUD_XSD;
		StmXmlProcessTemplate stmXmlProcess = new StmXmlProcessTemplate();
		Document documents = DocumentHelper.createDocument();
		Element documentsRoot = documents.addElement("xs:schema").addAttribute(StmConstant.VERSION, StmConstant.VERSIONNUM);
		documentsRoot.add(new Namespace(StmConstant.NAMESPACE, StmConstant.NAMESPACEADDRESS));
		Element elementItem= documentsRoot.addElement("xs:element")
				.addAttribute("name", "item");
		Element sequenceEle = elementItem.addElement("xs:complexType").addElement("xs:sequence");

		elementItem = stmXmlProcess.createOutBoundXsd(sequenceEle);
		documents.setXMLEncoding(StmConstant.XMLENCODING);
		Dom4jUtil.formatWrite(documents, outBoundFilePath);
		return documents.asXML();
	}
	
	public static void main(String[] args) {
		try {
			StmXSDCreator creater = new StmXSDCreator();
			String result = creater.createInBoundXsd();
			System.out.println(result);			
			
			String result3 = creater.createOutBoundXsd();
			System.out.println(result3);

		} catch (Exception ex)
		{
			 LOG.log(Level.SEVERE, "Exception in main method while creating inBoundXsd : "+ex.getMessage(), ex);
		}
	}
}
