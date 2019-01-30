package com.avnet.emasia.webquote.web.quote;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.avnet.emasia.webquote.entity.Attachment;
import com.avnet.emasia.webquote.exception.WebQuoteException;
import com.avnet.emasia.webquote.quote.ejb.QuoteSB;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.utilites.web.util.DownloadUtil;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;
import com.avnet.emasia.webquote.web.quote.job.FileUtil;


@ManagedBean
@SessionScoped
public class AttachmentDownloadMB implements Serializable {
	
	@EJB
	QuoteSB quoteSB;
	
	
	@EJB
	SysConfigSB sysConfigSB;
	

	private static final long serialVersionUID = -2042156743059308577L;

	private static final Logger LOG =Logger.getLogger(AttachmentDownloadMB.class.getName());
	
	private List<Attachment> attachments;
	
	
	public void listAttachments(){
		
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> params = context.getExternalContext().getRequestParameterMap();
		Long id = Long.parseLong(params.get("id"));
		String type = params.get("type");
		
		attachments = quoteSB.findAttachments(id, type);
		
	}
	
	public void listAttachmentsByType() {
		Long quoteItemId = getId("quoteItem_id");
		Long quoteId = getId("quote_id");
		String type = getParamVal("att_type");
		if(null!=type && !type.isEmpty()) {
			String[] types = type.split(",");
			List<String> attachmentTypes =  Arrays.asList(types);
			attachments = quoteSB.findAttachments(quoteItemId, quoteId, attachmentTypes);
		}
		
		
		
	}

	public void listAttachmentForQC(){
		
		Long quoteItemId = getId("quoteItem_id");
		Long quoteId = getId("quote_id");
		
		List<String> attachmentTypes = new ArrayList<String>();
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE_ITEM);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_REFRESH);			
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_INTERNAL_TRANSFER);
		
		attachments = quoteSB.findAttachments(quoteItemId, quoteId, attachmentTypes);		
	}
	
	public void listAttachmentForSales(){
		
		
		Long quoteItemId = getId("quoteItem_id");
		Long quoteId = getId("quote_id");
		
		List<String> attachmentTypes = new ArrayList<String>();
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE_ITEM);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_REFRESH);		
		
		attachments = quoteSB.findAttachments(quoteItemId, quoteId, attachmentTypes);
		
	}
	
	public void listAttachmentForCS(){
		
		Long quoteItemId = getId("quoteItem_id");
		Long quoteId = getId("quote_id");
		
		List<String> attachmentTypes = new ArrayList<String>();
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE_ITEM);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_REFRESH);
		
		attachments = quoteSB.findAttachments(quoteItemId, quoteId, attachmentTypes);		
	}
	
	public void listAttachmentForPM(){
		
		Long quoteItemId = getId("quoteItem_id");
		Long quoteId = getId("quote_id");
		
		List<String> attachmentTypes = new ArrayList<String>();
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE_ITEM);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_REFRESH);		
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_INTERNAL_TRANSFER);
		
		attachments = quoteSB.findAttachments(quoteItemId, quoteId, attachmentTypes);		
	}
	
	public void listAttachmentForPMInRIT(){
		
		Long quoteItemId = getId("quoteItem_id");
		Long quoteId = getId("quote_id");
		
		List<String> attachmentTypes = new ArrayList<String>();
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE_ITEM);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_REFRESH);		
		
		attachments = quoteSB.findAttachments(quoteItemId, quoteId, attachmentTypes);		
	}	
	
	
	public void listAttachmentForMM(){
		
		Long quoteItemId = getId("quoteItem_id");
		Long quoteId = getId("quote_id");
		
		List<String> attachmentTypes = new ArrayList<String>();
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE_ITEM);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_INTERNAL_TRANSFER);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_REFRESH);		
		
		attachments = quoteSB.findAttachments(quoteItemId, quoteId, attachmentTypes);		
	}
	
	public void listAttachment(String type){
		
		Long quoteItemId = getId("quoteItem_id");
		Long quoteId = getId("quote_id");
		
		List<String> attachmentTypes = new ArrayList<String>();
		attachmentTypes.add(type);
		
		attachments = quoteSB.findAttachments(quoteItemId, quoteId, attachmentTypes);		
	}
	
	
	public StreamedContent getFile(Long id,String fileName,String fileNameActual,String filePath ) throws WebQuoteException {
		
		//Attachment attachment = quoteSB.getAttachment(id);
		String fileRootPath = sysConfigSB.getProperyValue(QuoteSBConstant.ATTACHMENT_ROOT_PATH);
		String realFilePath = fileRootPath + File.separator +  filePath + File.separator + fileNameActual;
		byte[] fileByteArray = FileUtil.file2Byte(realFilePath);
		
		
		ByteArrayInputStream is = new ByteArrayInputStream(fileByteArray);
		
		String mimeType = DownloadUtil.getMimeType(fileName);
		
		String escapedFilename = "Unrecognized!!!";
        try {
            // Encoding
            escapedFilename = URLEncoder.encode(fileName, "UTF-8").replaceAll(
                    "\\+", "%20");
        } catch (UnsupportedEncodingException e1) {         
            LOG.log(Level.WARNING, "Error occured when encoding file name " + fileName + " for attachment:  ID " + id+", Reason for exception: "+e1.getMessage());
        }
		
		StreamedContent file = new DefaultStreamedContent(is, mimeType , escapedFilename);
		
		return file;
	}
	
	/**
	 * JSF??example: <l:download attachment="#{att}"/>
	 * @param attachment
	 * @return
	 * @throws WebQuoteException
	 */
	public StreamedContent getStreamedContent(Attachment attachment) throws WebQuoteException  {
		return getFile(attachment.getId(), attachment.getFileName(), attachment.getFileNameActual(), attachment.getFilePath());
	}

	private long getId(String name){
		
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> params = context.getExternalContext().getRequestParameterMap();
		return Long.parseLong(params.get(name));
		
	}
	
	private String getParamVal(String name){
		
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> params = context.getExternalContext().getRequestParameterMap();
		String paramsValue = params.get(name);
		return paramsValue;
		
	}
	
	
	//Getters, Setters


	public List<Attachment> getAttachments() {
		return attachments;
	}


	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}	
	
     
}
                    
