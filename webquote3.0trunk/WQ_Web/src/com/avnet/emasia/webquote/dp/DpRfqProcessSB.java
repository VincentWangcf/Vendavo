package com.avnet.emasia.webquote.dp;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.avnet.emasia.webquote.constants.DpStatusEnum;
import com.avnet.emasia.webquote.dp.xml.requestquote.WebQuoteLineRequest;
import com.avnet.emasia.webquote.dp.xml.requestquote.WebRequestObject;
import com.avnet.emasia.webquote.dp.xml.responsequote.AvnQuoteIdentifierType;
import com.avnet.emasia.webquote.dp.xml.responsequote.ObjectFactory;
import com.avnet.emasia.webquote.dp.xml.responsequote.ResponseQuoteType;
import com.avnet.emasia.webquote.entity.DpMessage;
import com.avnet.emasia.webquote.entity.DpRfq;
import com.avnet.emasia.webquote.entity.DpRfqItem;
import com.avnet.emasia.webquote.exception.AppException;
import com.avnet.emasia.webquote.exception.CommonConstants;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;

@Stateless
@LocalBean
public class DpRfqProcessSB {
	
	@PersistenceContext(unitName = "Server_Source")
	private EntityManager em;
	
	@EJB
	private DpRfqSubmissionSB dpRfqSubmissionSB;
	
	@EJB
	private SysConfigSB sysConfigSB;
	
	@EJB
	private DpMDB dpMDB;
	
	private Validator validator;
	
	private static final Logger LOGGER = Logger.getLogger(DpRfqProcessSB.class.getName());
    
    @PostConstruct
    public void init(){
    	ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    	validator = (Validator) factory.getValidator();
    }
    
    public void onMessage(DpMessage dpMessage) throws AppException{
    	
    	WebRequestObject webRequestObject = null;

    	try {
			webRequestObject = convertXmlToWebRequestObject(dpMessage.getCreateRfqMessage());
		} catch (JAXBException e) {
			dpMessage.setBadFormedCreateRfqMessage(true);
			em.merge(dpMessage);
			throw new AppException(CommonConstants.WQ_EJB_MASTER_DATA_ERROR_OCCURED_WHEN_CONVERTING_CREATERFQXML_TO_WEBREQUESTOBJECT, e);
		}
    	
    	
    	//validate object converted from xml
    	String inboundError = validate(webRequestObject);
		
		if(StringUtils.isNotEmpty(inboundError)){
			
			ResponseQuoteType responseQuoteType = ResponseQuoteType.createInstance(webRequestObject);
			AvnQuoteIdentifierType avnQuoteIdentifier = responseQuoteType.getAvnQuoteIdentifier().get(0);
			avnQuoteIdentifier.setQuoteLineStatus(DpStatusEnum.XML_VALIDATION_ERROR.code());
			avnQuoteIdentifier.setRejectionReason("Validation error occurred.");
			avnQuoteIdentifier.setRemarks(inboundError.length() > 254 ? inboundError.substring(0, 254) : inboundError);
			
			
			String outboundError = validate(responseQuoteType);
			
			if(StringUtils.isNotEmpty(outboundError)){
				throw new AppException(CommonConstants.WQ_EJB_MASTER_DATA_DP_VALIDATION_ERROR_FOR_WEBREQUESTOBJECT, new Object[]{inboundError,webRequestObject.toString()});
			}
			
			String xml = null;
			try {
				xml = convertResponseQuoteTypeToXml(responseQuoteType);
				
			} catch (Exception e) {
				throw new AppException(CommonConstants.COMMON_DP_ERROR_OCCURED_CONVERT_RESPONSEQUOTETYPE_TO_XML, e);
			}
			
			dpMessage.setUpdateRfqMessage(xml);
			dpMessage.setUpdateRfqTime(new Timestamp((new Date()).getTime()));
			em.merge(dpMessage);

			try {
				sendoutMessage(xml);
			} catch (Exception e) {
				throw new AppException(CommonConstants.WQ_WEB_SERVICE_FAILED_SEND_MESSAGE_TO_DP, e);
			}
			throw new AppException(CommonConstants.WQ_EJB_MASTER_DATA_DP_VALIDATION_ERROR_FOR_WEBREQUESTOBJECT, new Object[]{inboundError,webRequestObject.toString()});
		}
		
    	
    	DpRfq dpRfq = DpRfq.createInstance(webRequestObject);
    	dpRfq.setDpMessage(dpMessage);
    	
    	TypedQuery<Long> query = em.createQuery("select count(r) from DpRfq r where r.referenceId = ?1", Long.class);
    	query.setParameter(1, dpRfq.getReferenceId());
    	if (query.getSingleResult() > 0L){
    		LOGGER.info("duplicate reference id " + dpRfq.getReferenceId() + " in createRfqXml message, message ingored." );
    		return;
    	}
    	
    	query = em.createQuery("select count(r) from DpRfqItem r where r.referenceLineId = ?1", Long.class);
    	query.setParameter(1, dpRfq.getDpRfqItems().get(0).getReferenceLineId());
    	if (query.getSingleResult() > 0L){
    		LOGGER.info("duplicate reference line id " + dpRfq.getDpRfqItems().get(0).getReferenceLineId() + " in createRfqXml message, message ingored." );
    		return;
    	}
    	
    	em.persist(dpRfq);
    	
    	submitRfq(dpRfq);
    	
    }
    
    public void submitRfq(DpRfq dpRfq) throws AppException{
    	try {
    		dpRfqSubmissionSB.submitRfq(dpRfq);
    	} catch (AppException e) {
    		throw new AppException(CommonConstants.WQ_EJB_MASTER_DATA_ERROR_OCCURED_IN_AQ_LOGIC , e);
    	}
    	
    	DpRfqItem item = dpRfq.getDpRfqItems().get(0);
    	if (StringUtils.isNotEmpty(item.getInternalError())){
    		return;
    	}
    	
    	//after AQ logic, send to DP
    	
    	ResponseQuoteType responseQuoteType = ResponseQuoteType.createInstance(dpRfq);
    	String outboundError = validate(responseQuoteType);
    	if (StringUtils.isNotEmpty(outboundError)){
    		throw new AppException(CommonConstants.WQ_EJB_MASTER_DATA_DP_VALIDATION_ERROR_FOR_RESPONSEQUOTETYPE,new Object[]{outboundError,responseQuoteType.toString()});
    	}
    	
    	String updateRfqXml = null;
    	try {
    		updateRfqXml = convertResponseQuoteTypeToXml(responseQuoteType);
    	} catch (JAXBException e) {
    		throw new AppException(CommonConstants.COMMON_DP_ERROR_OCCURED_CONVERT_RESPONSEQUOTETYPE_TO_XML, e);
    	}
    	DpMessage dpMessage = dpRfq.getDpMessage();
    	dpMessage.setUpdateRfqMessage(updateRfqXml);
    	dpMessage.setUpdateRfqTime(new Timestamp((new Date()).getTime()));
    	em.merge(dpMessage);
    	try {
    		sendoutMessage(updateRfqXml);
    	} catch (Exception e) {
    		throw new AppException(CommonConstants.WQ_EJB_MASTER_DATA_DP_ERROR_OCCURED_WHEN_SENDING_OUT_MESSAGE, e);
    	}
    	
    }
    
    public void resendMessage(List<DpRfqItem> dpRfqItems) {
    	if (dpRfqItems == null) {
    		return;
    	}
    	for(DpRfqItem dpRfqItem : dpRfqItems){
    		try{
    			
    			ResponseQuoteType responseQuoteType = ResponseQuoteType.createInstance(dpRfqItem.getDpRfq());
    			String outboundError = validate(responseQuoteType);
    			if (StringUtils.isNotEmpty(outboundError)){
    				throw new AppException(CommonConstants.WQ_EJB_MASTER_DATA_DP_VALIDATION_ERROR_FOR_RESPONSEQUOTETYPE,new Object[]{outboundError,responseQuoteType.toString()});	
    			}
    			String updateRfqXml = null;
    			try {
    				updateRfqXml = convertResponseQuoteTypeToXml(responseQuoteType);
    			} catch (JAXBException e) {
    				throw new AppException(CommonConstants.COMMON_DP_ERROR_OCCURED_CONVERT_RESPONSEQUOTETYPE_TO_XML, e);
    			}
    			DpMessage dpMessage = dpRfqItem.getDpRfq().getDpMessage();
    			dpMessage.setUpdateRfqMessage(updateRfqXml);
    			dpMessage.setUpdateRfqTime(new Timestamp((new Date()).getTime()));
    			em.merge(dpMessage);
    			sendoutMessage(updateRfqXml);
    		}catch(Exception e ){
    			dpMDB.sendEmail(e, null);
    		}
        	

    	}
    }
    
    

    
    
    private WebRequestObject convertXmlToWebRequestObject(String createRfqXml) throws JAXBException{
    	
		JAXBContext jc = JAXBContext.newInstance(WebRequestObject.class);
		Unmarshaller u = jc.createUnmarshaller();
		WebRequestObject webRequestObject = (WebRequestObject)u.unmarshal(new StreamSource(new StringReader(createRfqXml)));
		return webRequestObject;
    }   
    
    
    private String convertResponseQuoteTypeToXml(ResponseQuoteType object) throws JAXBException{
    	
		JAXBContext jc = JAXBContext.newInstance(ResponseQuoteType.class);
		Marshaller m = jc.createMarshaller();
		ObjectFactory factory = new ObjectFactory();
    	JAXBElement<ResponseQuoteType> o = factory.createResponseQuote(object);
		StringWriter sw = new StringWriter();
		m.marshal(o,sw);
		return sw.toString();
    }   
    
    private void sendoutMessage(String s) throws IOException{
		String outboundPath = sysConfigSB.getProperyValue("DP_OUTBOUND_PATH");
		outboundPath="C:\\Users\\046755\\Desktop\\excel\\DP";
		String fileName  = sysConfigSB.getProperyValue("DP_OUTBOUND_FILE_NAME") + System.currentTimeMillis() + ".xml";
		File file = new File(outboundPath + File.separator + fileName);
		FileUtils.write(file, s, "utf-8");
		LOGGER.info("write to DP outbound Message to file:" + fileName);
	}
    
    private String validate(ResponseQuoteType responseQuoteType){
    	StringBuilder sb = new StringBuilder();
    	for(ConstraintViolation<ResponseQuoteType> constraintVioloation : validator.validate(responseQuoteType)){
			sb.append(constraintVioloation.getMessage());
		}
		
		for(AvnQuoteIdentifierType item : responseQuoteType.getAvnQuoteIdentifier()){
			for(ConstraintViolation<AvnQuoteIdentifierType> constraintVioloation : validator.validate(item)){
				sb.append(constraintVioloation.getMessage());
			}
		}
		return sb.toString();
    }
    
    private String validate(WebRequestObject webRequestObject){
    	StringBuilder sb = new StringBuilder();
    	
		Set<ConstraintViolation<WebRequestObject>> constraintViolations = validator.validate(webRequestObject);
		if(constraintViolations.isEmpty() == false){
			for(ConstraintViolation<WebRequestObject> constraintVioloation : constraintViolations){
				sb.append(constraintVioloation.getMessage()).append(" ");
			}
		}
		
		List<WebQuoteLineRequest> items = webRequestObject.getWebQuoteLineRequest();
		if(items != null && items.isEmpty() == false){
			int lineItem = 0;
			for(WebQuoteLineRequest line: webRequestObject.getWebQuoteLineRequest()){
				for(ConstraintViolation<WebQuoteLineRequest> constraintVioloation : validator.validate(line)){
					sb.append("Line Item ").append(lineItem+1).append(" ").append(constraintVioloation.getMessage()).append(" ");
				}
				lineItem++;
			}
		}  
		return sb.toString();
    }
    
}
