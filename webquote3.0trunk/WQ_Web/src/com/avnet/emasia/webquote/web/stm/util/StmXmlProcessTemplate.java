package com.avnet.emasia.webquote.web.stm.util;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXValidator;
import org.dom4j.io.XMLWriter;
import org.dom4j.util.XMLErrorHandler;
import org.xml.sax.SAXException;

import com.avnet.emasia.webquote.stm.annotation.TransAliasBean;
import com.avnet.emasia.webquote.stm.constant.FieldTypeEnum;
import com.avnet.emasia.webquote.stm.constant.StmConstant;
import com.avnet.emasia.webquote.stm.constant.StmQuoteTypeEnum;
import com.avnet.emasia.webquote.stm.dto.InBoundVo;
import com.avnet.emasia.webquote.stm.dto.OutBoundVo;
import com.avnet.emasia.webquote.stm.dto.StmConfigInfo;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil;

public  class StmXmlProcessTemplate implements Serializable {
	static final Logger LOG=Logger.getLogger(StmXmlProcessTemplate.class.getSimpleName());

	private static final long serialVersionUID = -1061739910392537087L;
	
	public StringBuffer batchVerifySendObjFieldType(List<OutBoundVo> list){
		StringBuffer sb = new StringBuffer ();
		for(OutBoundVo vo : list){
			sb.append(verifySendObjFieldType(vo));
		}
		return sb;
	}
	
	public StringBuffer batchVerifyTargetResaleAndCost(List<OutBoundVo> list){
		StringBuffer sb = new StringBuffer ();
		for(OutBoundVo vo : list){
			if(!StmUtil.isDecimal(vo.getTgtCost())){
				sb.append(vo.getTgtCost() +ResourceMB.getText("wq.error.targetCostError")+FieldTypeEnum.NUMBER+", <br/>");
			}
			if(!StmUtil.isDecimal(vo.getTgtResale())){
				sb.append(vo.getTgtResale() +ResourceMB.getText("wq.error.targetResaleError")+FieldTypeEnum.NUMBER+", <br/>");
			}

		}
		return sb;
	}	

	public StringBuffer batchVerifySendObjMandatory(String quoteType,List<OutBoundVo> list){
		StringBuffer sb = new StringBuffer ();
		for(OutBoundVo vo : list){
			sb.append(verifySendObjMandatory(quoteType,vo));
		}
		return sb;
	}
	
	public StringBuffer batchVerifySendObjLength(List<OutBoundVo> list){
		StringBuffer sb = new StringBuffer ();
		for(OutBoundVo vo : list){
			sb.append(verifySendObjLength(vo));
		}
		return sb;
	}
	
	public StringBuffer batchVerifySendObjNoAnnotationFieldLength(List<OutBoundVo> list){
		StringBuffer sb = new StringBuffer ();
		for(OutBoundVo vo : list){
			sb.append(verifySendObjNoAnnotationFieldLength(vo));
		}
		return sb;
	}	
	
	public HashMap<OutBoundVo,String> batchGetSendXMLFileContent(List<OutBoundVo> list){
		HashMap<OutBoundVo,String> hash = new HashMap<OutBoundVo,String> ();
		for(OutBoundVo vo : list){
			String xmlContent = createSendXMLFile(vo);
			hash.put(vo, xmlContent);
		}

		return hash;
	}
	
	public StringBuffer batchValidateOutBoundXMLByXSD(HashMap<OutBoundVo,String> hash){
		StringBuffer sb = new StringBuffer ();
		Set<OutBoundVo> ketSet = hash.keySet();
		for(OutBoundVo vo : ketSet){
			String xmlContent = createSendXMLFile(vo);
			sb.append(validateXMLByXSD( vo.getSeq(),xmlContent,StmConstant.OUTBOUD_XSD));	
		}
		return sb;
	}
	
	public StringBuffer batchWriteOutboundFileToLocal(StmConfigInfo stmConfigInfo,HashMap<OutBoundVo, String> hash) {
		StringBuffer sb = new StringBuffer();
		Set<OutBoundVo> ketSet = hash.keySet();
		for (OutBoundVo vo : ketSet) {
			String sendFileName = StmUtil.getSendFileName(vo.getRfqCode());

			HashMap<String, Object> hashReturn = StmUtil.writeOutboundFileToLocal(hash.get(vo), stmConfigInfo,sendFileName);
			Boolean outBoundSuccess = (Boolean) hashReturn.get(StmConstant.RETURN_BOOL);

			if (!outBoundSuccess) {
				String message = (String) hashReturn.get(StmConstant.RETURN_MESSAGE);
				sb.append(message);
			}
		}

		return sb;
	}
	
	private String createSendXMLFile(OutBoundVo dbObj) {
		Document doc = DocumentHelper.createDocument();
		Element rootElement = doc.addElement("item");

		TreeMap<String, TransAliasBean> sendTransaliasbeanList = StmUtil.getOutboundAnnotationTreeMap();
		Set<String> set = sendTransaliasbeanList.keySet();
		for(String key:set){
			TransAliasBean bean = sendTransaliasbeanList.get(key);

			Object dbFieldValue = StmUtil.getter(dbObj, bean.getFieldName());
			dbFieldValue = dbFieldValue==null?"":dbFieldValue;
			if(dbFieldValue instanceof String){
				String tempObj = (String)dbFieldValue;
				if(tempObj.length()>bean.getFieldLength()){
					tempObj = tempObj.substring(0, bean.getFieldLength());
				}
				rootElement.addElement(key).setText(tempObj);
			}else{
				rootElement.addElement(key).setText(String.valueOf(dbFieldValue));
			}
		}	
		return doc.asXML();

	}
	
	public InBoundVo covertInBoundXml2Bean(Document doc){
		InBoundVo vo = new InBoundVo();
		Element root = doc.getRootElement();
		TreeMap<String,TransAliasBean>  inBoundAnnotationTreeMap = StmUtil.getInboundAnnotationTreeMap();
		for (Iterator<?> i = root.elementIterator(); i.hasNext();) {
			Element element = (Element) i.next();
			String eleName = element.getName();
	
			TransAliasBean bean = inBoundAnnotationTreeMap.get(eleName);
			String eleText = element.getText();
			StmUtil.setter(vo, bean.getFieldName(), eleText, String.class);
		}
		return vo;
		
	}	

	 /** 
     * by XSD��XML Schema��validate XML 
     */ 
	public StringBuffer validateXMLByXSD( int seq,String xmlContent,String xsdName)  { 
    	StringBuffer sb = new StringBuffer ();
  
        try { 
            XMLErrorHandler errorHandler = new XMLErrorHandler(); 
 
            SAXParserFactory factory = SAXParserFactory.newInstance(); 
    
            factory.setValidating(true); 

            factory.setNamespaceAware(true); 
     
            SAXParser parser = factory.newSAXParser();  
      
            Document xmlDocument = Dom4jUtil.getDocumentXml(xmlContent); 
    
            parser.setProperty( 
                    "http://java.sun.com/xml/jaxp/properties/schemaLanguage", 
                    "http://www.w3.org/2001/XMLSchema"); 
/*            parser.setProperty( 
                    "http://java.sun.com/xml/jaxp/properties/schemaSource", 
                    "file:" + StmXmlProcessTemplate.class.getResource("").getPath() +xsdName); 
*/
            parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", StmXmlProcessTemplate.class.getClassLoader().getResourceAsStream(xsdName));
            SAXValidator validator = new SAXValidator(parser.getXMLReader()); 
        
            validator.setErrorHandler(errorHandler); 
            
            validator.validate(xmlDocument); 

            XMLWriter writer = new XMLWriter(OutputFormat.createPrettyPrint()); 
          
            if (errorHandler.getErrors().hasContent()) { 
                writer.write(errorHandler.getErrors()); 
                sb.append(errorHandler.getErrors().asXML());
            } 
        } catch (Exception ex) { 
        	
        		Object[] arr ={String.valueOf(seq),String.valueOf(xsdName)};
				sb.append(ResourceMB.getParameterizedString("wq.error.XSDValidateFailed", arr)+".\n");
            LOG.log(Level.SEVERE, "Exception in validating XML By XSD, exception message : "+ex.getMessage(), ex);
        } 
        return sb;
    } 
    
	public  StringBuffer verifySendObjMandatory(String quoteType,OutBoundVo dbObj){
		StringBuffer sb = new StringBuffer ();
		TreeMap<String, TransAliasBean> sendTransaliasbeanList = StmUtil.getOutboundAnnotationTreeMap();
		Set<String> set = sendTransaliasbeanList.keySet();
		for(String key:set){
			TransAliasBean bean = sendTransaliasbeanList.get(key);
			Object dbFieldValue = StmUtil.getter(dbObj, bean.getFieldName());
			
			if(quoteType!=null&&quoteType.equalsIgnoreCase(StmQuoteTypeEnum.SGA.name())){
				if(bean.isSgaMandatory()&&(dbFieldValue==null||dbFieldValue.equals(""))){
					Object[] arr ={String.valueOf(dbObj.getSeq()),String.valueOf(ResourceMB.getText(bean.getUiFieldName()))};
					sb.append(ResourceMB.getParameterizedString("wq.message.mandFeildRow", arr)+",<br/>");
				}
			}else if(quoteType!=null&&quoteType.equalsIgnoreCase(StmQuoteTypeEnum.SADA.name())){
				if(bean.isSadaMandatory()&&(dbFieldValue==null||dbFieldValue.equals(""))){
					Object[] arr ={String.valueOf(dbObj.getSeq()),String.valueOf(ResourceMB.getText(bean.getUiFieldName()))};
					sb.append(ResourceMB.getParameterizedString("wq.message.mandFeildRow", arr)+",<br/>");
				}
			}
	
		}
		
		return sb;
	}

	public StringBuffer verifyInBoundXMlContent(String quoteType,Document doc){
		StringBuffer sb = new StringBuffer ();

		Element root = doc.getRootElement();

		TreeMap<String, TransAliasBean> receiveTransaliasbeanList = StmUtil.getInboundAnnotationTreeMap();

		for (Iterator<?> i = root.elementIterator(); i.hasNext();) {
			Element element = (Element) i.next();
			String eleName = element.getName();
			String eleText = element.getText();
			TransAliasBean bean = receiveTransaliasbeanList.get(eleName);
			if(quoteType!=null&&quoteType.equalsIgnoreCase(StmQuoteTypeEnum.SGA.name())){
				if(QuoteUtil.isEmpty(eleText)&&bean.isSgaMandatory()){
					sb.append( eleText +ResourceMB.getText("wq.message.needMandatoryField")+eleName+", <br/>");				
				}
			}else if(quoteType!=null&&quoteType.equalsIgnoreCase(StmQuoteTypeEnum.SADA.name())){
				if(QuoteUtil.isEmpty(eleText)&&bean.isSadaMandatory()){
					sb.append( eleText +ResourceMB.getText("wq.message.needMandatoryField")+eleName+", <br/>");				
				}				
			}
			
			
			if(!QuoteUtil.isEmpty(eleText)&&bean!=null&&eleText!=null&&eleText.length()>bean.getFieldLength()){
				Object[] arr ={String.valueOf(eleName)};
				sb.append( eleText + " " + ResourceMB.getParameterizedString("wq.error.lenghtMaximumError", arr) +" "+bean.getFieldLength()+" "+ResourceMB.getText("wq.message.inMaximum")+", <br/>");				
			}	
			if(!QuoteUtil.isEmpty(eleText)&&bean!=null&&bean.getFieldType().equalsIgnoreCase("Integer")&&!QuoteUtil.isInteger(eleText)){
				Object[] arr ={String.valueOf(eleName),String.valueOf(FieldTypeEnum.INTEGER)};
				sb.append(eleText +ResourceMB.getParameterizedString("wq.error.fieldError", arr)+", <br/> ");								
			}else if (!QuoteUtil.isEmpty(eleText)&&bean!=null&&bean.getFieldType().equalsIgnoreCase("Date")&&!StmUtil.isValidDate(eleText)){
				Object[] arr ={String.valueOf(eleName),String.valueOf(FieldTypeEnum.DATE)};
				sb.append(eleText +ResourceMB.getParameterizedString("wq.error.fieldError", arr)+", <br/> ");	
			}else if (!QuoteUtil.isEmpty(eleText)&&bean!=null&&bean.getFieldType().equalsIgnoreCase("Number")&&!QuoteUtil.isNumber(eleText)){
				Object[] arr ={String.valueOf(eleName),String.valueOf(FieldTypeEnum.NUMBER)};
				sb.append(eleText +ResourceMB.getParameterizedString("wq.error.fieldError", arr)+", <br/> ");	
			}
		}
		
		return sb;
	}
	public Element createOutBoundXsd(Element elementItem){
		TreeMap<String, TransAliasBean> hashMap = StmUtil.getOutboundAnnotationTreeMap();
		addAllChildNode(hashMap,elementItem);			
		return elementItem;
	}
	
	public Element createInBoundXsd(Element elementItem){
		TreeMap<String, TransAliasBean> hashMap = StmUtil.getInboundAnnotationTreeMap();
		addAllChildNode(hashMap,elementItem);
		return elementItem;
	}
	
	private void addAllChildNode(TreeMap<String, TransAliasBean> hashMap,Element elementItem){
		Set<String> keySet = hashMap.keySet();
		for (Iterator<String> i = keySet.iterator(); i.hasNext();) {
			String key = (String) i.next();
			TransAliasBean bean = hashMap.get(key);
			Element e = elementItem.addElement("xs:element");
			e.addAttribute("name",key);
	
			Element simpleTypeEle = e.addElement("xs:simpleType");
			Element restrictionEle = simpleTypeEle.addElement("xs:restriction");

			Element lengthEle = restrictionEle.addElement("xs:maxLength");
			lengthEle.addAttribute("value",String.valueOf(bean.getFieldLength()));
			
			
			restrictionEle.addAttribute("base","xs:string");
		}
	}
	
	private StringBuffer verifySendObjLength(OutBoundVo dbObj){
		StringBuffer sb = new StringBuffer();
		TreeMap<String, TransAliasBean> sendTransaliasbeanList = StmUtil.getOutboundAnnotationTreeMap();
		Set<String> set = sendTransaliasbeanList.keySet();
		for(String key:set){
			TransAliasBean bean = sendTransaliasbeanList.get(key);
			Object dbFieldValue = StmUtil.getter(dbObj, bean.getFieldName());
			if(dbFieldValue instanceof String){
				String tempObj = (String)dbFieldValue;
				if(tempObj!=null&&tempObj.length()>bean.getFieldLength()){
					
					Object[] arr ={String.valueOf(dbObj.getSeq()),String.valueOf(ResourceMB.getText(bean.getUiFieldName())),String.valueOf(tempObj),String.valueOf(bean.getFieldLength())};
					
					sb.append(ResourceMB.getParameterizedString("wq.message.rowLenExceed", arr)+". <br/> ");	

				}
			}
		}
		return sb;
	}
		
	private StringBuffer verifySendObjNoAnnotationFieldLength(OutBoundVo dbObj){
		StringBuffer sb = new StringBuffer();
	
		if(dbObj.getBqNumber() != null && dbObj.getBqNumber().length() > 35){
			Object[] arr ={String.valueOf(dbObj.getSeq()),String.valueOf(dbObj.getBqNumber())};
		   
			sb.append(ResourceMB.getParameterizedString("wq.message.BQNumberExceedLengthLimit", arr)+". <br/>");
		}
		if(dbObj.getProjectRegistrationNumber() != null && dbObj.getProjectRegistrationNumber().length() > 35){
			Object[] arr ={String.valueOf(dbObj.getSeq()),String.valueOf(dbObj.getProjectRegistrationNumber())};
			   
			sb.append(ResourceMB.getParameterizedString("wq.message.projectRegistrationExceedLengthLimit", arr)+". <br/>");
			
		}
		/*
		if(dbObj.getBqNumber() != null && dbObj.getProject().length() > 255){
			sb.append("Row #<"+dbObj.getSeq()+">, ProjectName '" + dbObj.getProject() + "' exceed length limit 35. <br/>");
		}
		if(dbObj.getBqNumber() != null && dbObj.getApplication().length() > 255){
			sb.append("Row #<"+dbObj.getSeq()+">, Application '" + dbObj.getApplication() + "' exceed length limit 35. <br/>");
		}
		if(dbObj.getBqNumber() != null && String.valueOf(dbObj.getEau()).length() > 38){
			sb.append("Row #<"+dbObj.getSeq()+">, EAU '" + dbObj.getEau() + "' exceed length limit 35. <br/>");
		}
		*/
		return sb;
	}
	
	private StringBuffer verifySendObjFieldType(OutBoundVo dbObj) {
		
		StringBuffer sb = new StringBuffer();
	
		TreeMap<String, TransAliasBean> sendTransaliasbeanList = StmUtil.getOutboundAnnotationTreeMap();

		Set<String> set = sendTransaliasbeanList.keySet();
		
		
		for(String key:set){
			TransAliasBean bean = sendTransaliasbeanList.get(key);
			Object dbFieldValue = StmUtil.getter(dbObj, bean.getFieldName());
			String fieldStr = null;
			if(dbFieldValue!=null){
				fieldStr = dbFieldValue.toString();
			}
			

			
			if(!QuoteUtil.isEmpty(fieldStr)&&!fieldStr.equalsIgnoreCase("NA")&&
					bean!=null&&bean.getFieldType().equalsIgnoreCase(FieldTypeEnum.INTEGER)&&!QuoteUtil.isInteger(fieldStr)){
				Object[] arr ={ResourceMB.getText(bean.getUiFieldName()),String.valueOf(FieldTypeEnum.INTEGER)};
				sb.append(fieldStr +ResourceMB.getParameterizedString("wq.error.fieldError", arr)+", <br/> ");								
			}else if (!QuoteUtil.isEmpty(fieldStr)&&!fieldStr.equalsIgnoreCase("NA")&&
					bean!=null&&bean.getFieldType().equalsIgnoreCase(FieldTypeEnum.DATE)&&!StmUtil.isValidDate(fieldStr)){
				Object[] arr ={ResourceMB.getText(bean.getUiFieldName()),String.valueOf(FieldTypeEnum.DATE)};
				sb.append(fieldStr +ResourceMB.getParameterizedString("wq.error.fieldError", arr)+", <br/> ");		
			}else if (!QuoteUtil.isEmpty(fieldStr)&&!fieldStr.equalsIgnoreCase("NA")&&
					bean!=null&&bean.getFieldType().equalsIgnoreCase(FieldTypeEnum.NUMBER)&&!StmUtil.isDecimal(fieldStr)){
				Object[] arr ={ResourceMB.getText(bean.getUiFieldName()),String.valueOf(FieldTypeEnum.NUMBER)};
				sb.append(fieldStr +ResourceMB.getParameterizedString("wq.error.fieldError", arr)+", <br/> ");		
			}
		}
		return sb;
	}
}