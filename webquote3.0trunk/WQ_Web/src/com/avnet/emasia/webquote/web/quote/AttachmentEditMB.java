package com.avnet.emasia.webquote.web.quote;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.avnet.emasia.webquote.entity.Attachment;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.exception.WebQuoteException;
import com.avnet.emasia.webquote.quote.ejb.QuoteSB;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.quote.vo.QuoteItemVo;
import com.avnet.emasia.webquote.utilites.web.util.DownloadUtil;
import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;
import com.avnet.emasia.webquote.web.datatable.BaseLazyDataMB;
import com.avnet.emasia.webquote.web.datatable.LazyEntityDataModel;
import com.avnet.emasia.webquote.web.quote.job.FileUtil;

/**
 * @author Lin, Tough(901518)
 * Created on 2013-4-18
 */

@ManagedBean
@SessionScoped
public class AttachmentEditMB implements Serializable{
	
	private static final long serialVersionUID = -842620496514294554L;
	
	private static final Logger LOG =Logger.getLogger(AttachmentEditMB.class.getName());
	
	@EJB
	QuoteSB quoteSB;

	List<QuoteItemVo> quoteItemVos = new ArrayList<QuoteItemVo>();	
	
	List<QuoteItemVo> selectedQuoteItemVos = new ArrayList<QuoteItemVo>();
	
	QuoteItemVo currentQuoteItemVo;
	
	List<QuoteItemVo> filteredQuoteItemVos;
	
	//used for the attachment popup box 
	List<Attachment> attachments;
	
	long selectedQuoteItemId;
	
	Map<Long, List<Attachment>> removedAttachments = new HashMap<Long, List<Attachment>>();;
	
	protected String attachmentType;
	
	//For response internal transfer,  the attachment operation is on both existing attachment and new attachment. set it to true;
	//For refresh quote and copy quote, the attachment operation is only on new attachment. set it to false;  
	protected boolean showExistingAttachment;
	
	private String deleteFileName;
	
	@EJB
	SysConfigSB sysConfigSB;
	
	
	public void rowSelectupdate(SelectEvent event){			
		QuoteItemVo item = (QuoteItemVo) event.getObject();
		this.currentQuoteItemVo= item;
		LOG.info("copy bmt quote currently quote number is ===>>>"+item.getQuoteItem().getQuoteNumber());	
		
	}
	
	/***
	 * before call this method, need to load attachment
	 */
	public void listAttachmentsForBMT() {
		
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> params = context.getExternalContext().getRequestParameterMap();
		Long quoteItemId = Long.parseLong(params.get("quoteItem_id"));
		listAttachmentCcmmon(quoteItemId);
	}

	public void listAttachments(){
		
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> params = context.getExternalContext().getRequestParameterMap();
		Long quoteItemId = Long.parseLong(params.get("id"));
		listAttachmentCcmmon(quoteItemId);
	}
	
	private void listAttachmentCcmmon(Long quoteItemId){
selectedQuoteItemId = quoteItemId;
		
		this.attachments = new ArrayList<Attachment>();
		
		for(QuoteItemVo vo : quoteItemVos){
			QuoteItem item = vo.getQuoteItem();
			if(item.getId() == quoteItemId){
				if(item.getAttachments() == null){
					break;
				}
				
				List<Attachment> oldAttachments = vo.getQuoteItem().getAttachments();
				if(null!=oldAttachments && oldAttachments.size()>0) {
					LOG.info("+++++"+oldAttachments.size());
				}
				
				for(Attachment attachment : item.getAttachments()){
					if(showExistingAttachment){
						if(attachment.getType().equals(attachmentType)){
							this.attachments.add(attachment);
						}
					}else{
						if(attachment.isNewAttachment() && attachment.getType().equals(attachmentType)){
							this.attachments.add(attachment);
						}
					}
				}
				break;
			}

		}		

	}
	
	public void addAttachmentForBMT(FileUploadEvent event) {
		addAttachment(event);		
	}

	
	public void addAttachment(FileUploadEvent event){
		
		UploadedFile file = event.getFile();
		
		for(QuoteItemVo vo : quoteItemVos){
			QuoteItem item = vo.getQuoteItem();
			if(item.getId() == selectedQuoteItemId){
				
				Attachment attachment = new Attachment();
				
				attachment.setQuoteItem(item);
				attachment.setFileImage(QuoteUtil.getUploadFileContent(file));
				String fileName = file.getFileName();
				if(fileName.contains("\\")){
					fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
				}
				attachment.setFileName(fileName);
				attachment.setType(attachmentType);
				attachment.setNewAttachment(true);
				
				if(item.getAttachments() == null){
					item.setAttachments(new ArrayList<Attachment>());
				}
				
				item.getAttachments().add(attachment);
				this.attachments = new ArrayList<Attachment>();
				
				for(Attachment atta : item.getAttachments()){
					if(showExistingAttachment){
						if(atta.getType().equals(attachmentType)){
							this.attachments.add(atta);
						}
					}else{
						if(atta.getType().equals(attachmentType) && atta.isNewAttachment()){
							this.attachments.add(atta);
						}
					}
				}
				break;
			}
		}		
	}
	
	public void deleteAttachmentForItem(){
		deleteAttachment();		
	}
	public void deleteAttachment(){
		
		for(QuoteItemVo vo : quoteItemVos){
			QuoteItem item = vo.getQuoteItem();
			if(item.getId() == selectedQuoteItemId){
				
				List<Attachment> attachments = item.getAttachments();
				
				for(int i =0; i < attachments.size(); i++){
					Attachment attachment = attachments.get(i);
					if(attachment.getFileName().equals(deleteFileName)){
						attachment.setQuoteItem(null);
						attachments.remove(i);
						if(attachment.getId() != null){
							if(removedAttachments.get(item.getId()) == null){
								removedAttachments.put(item.getId(), new ArrayList<Attachment>());
							}
							removedAttachments.get(item.getId()).add(attachment);
						}
					}
				}
				
				this.attachments = new ArrayList<Attachment>();
				for(Attachment attachment : item.getAttachments()){
					if(showExistingAttachment){
						if(attachment.getType().equals(attachmentType)){
							this.attachments.add(attachment);
						}
					}else{
						if(attachment.isNewAttachment() && attachment.getType().equals(attachmentType)){
							this.attachments.add(attachment);
						}
					}
				}
				
				break;
			}
		}		
	}	
	
	public StreamedContent getFile(String fileName) throws WebQuoteException  {
		
		for(QuoteItemVo vo : quoteItemVos){
			QuoteItem item = vo.getQuoteItem();
			if(item.getId() == selectedQuoteItemId){
				List<Attachment> attachments = item.getAttachments();
				
				for(int i =0; i < attachments.size(); i++){
					Attachment attachment = attachments.get(i);
					
					if(attachment.getFileName().equals(fileName)){
						byte[] fileByteArray = attachment.getFileImage();
						if(fileByteArray == null || !(fileByteArray.length > 0)) {
							// modified by Yang,Lenon for read attachments from host server(2015.12.30)
							String fileRootPath = sysConfigSB.getProperyValue(QuoteSBConstant.ATTACHMENT_ROOT_PATH);
							String realFilePath = fileRootPath + File.separator + attachment.getFilePath() + File.separator + attachment.getFileNameActual();
							fileByteArray = FileUtil.file2Byte(realFilePath);
						} 
						
						ByteArrayInputStream is = new ByteArrayInputStream(fileByteArray);
						
						String mimeType = DownloadUtil.getMimeType(attachment.getFileName());

						String escapedFilename = "Unrecognized!!!";
				        try {
				            // Encoding
				            escapedFilename = URLEncoder.encode(attachment.getFileName(), "UTF-8").replaceAll(
				                    "\\+", "%20");
				        } catch (UnsupportedEncodingException e1) {         
				            LOG.log(Level.WARNING, "Error occured when encoding file name " + attachment.getFileName() + " for attachment:  ID " + attachment.getId()+", Exception message: "+e1.getMessage());
				        }
						
						StreamedContent file = new DefaultStreamedContent(is, mimeType , escapedFilename);
						
						return file;
					}
				}
			}
		}			
		
		return null;
		
	}
	
	public List<Attachment> getBMTQCAtts(QuoteItem item) {
		List<String> attTypes =  new ArrayList<String>();
		attTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QC);
		attTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_BMT);
		List<Attachment> atts = quoteSB.findAttachments(item.getId(), item.getQuote().getId(), attTypes);
		return atts;
	}

	
	List<Attachment> getSelectedRemovedAttachments(){
		List<Attachment> lAttachments = new ArrayList<Attachment>();
		for(QuoteItemVo vo : selectedQuoteItemVos){
			QuoteItem item = vo.getQuoteItem();
			if(removedAttachments.get(item.getId())!= null){
				lAttachments.addAll(removedAttachments.get(item.getId()));
			}
		}
		return lAttachments;
	}
	
	
	//Getters, Setters
	
	public List<QuoteItemVo> getQuoteItemVos() {
		return quoteItemVos;
	}

	public void setQuoteItemVos(List<QuoteItemVo> quoteItemVos) {
		this.quoteItemVos = quoteItemVos;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public List<QuoteItemVo> getSelectedQuoteItemVos() {
		return selectedQuoteItemVos;
	}

	public void setSelectedQuoteItemVos(List<QuoteItemVo> selectedQuoteItemVos) {
		this.selectedQuoteItemVos = selectedQuoteItemVos;
	}

	public List<QuoteItemVo> getFilteredQuoteItemVos() {
		return filteredQuoteItemVos;
	}

	public void setFilteredQuoteItemVos(List<QuoteItemVo> filteredQuoteItemVos) {
		this.filteredQuoteItemVos = filteredQuoteItemVos;
	}

	public String getDeleteFileName() {
		return deleteFileName;
	}

	public void setDeleteFileName(String deleteFileName) {
		this.deleteFileName = deleteFileName;
	}

	public QuoteItemVo getCurrentQuoteItemVo() {
		return currentQuoteItemVo;
	}

	public void setCurrentQuoteItemVo(QuoteItemVo currentQuoteItemVo) {
		this.currentQuoteItemVo = currentQuoteItemVo;
	}
	
	public void setAttachmentContent(List<QuoteItemVo> items) {
		for(QuoteItemVo vo : items ){
			List<Attachment> attachments = vo.getQuoteItem().getAttachments();
			if(null!=attachments && attachments.size()>0){
				for(Attachment att:attachments) {
					byte[] fileByteArray = att.getFileImage();
					if(fileByteArray == null || !(fileByteArray.length > 0)) {
	
						String fileRootPath = sysConfigSB.getProperyValue(QuoteSBConstant.ATTACHMENT_ROOT_PATH);
						String realFilePath = fileRootPath + File.separator+ att.getFilePath() + File.separator + att.getFileNameActual();
						try {
							fileByteArray = FileUtil.file2Byte(realFilePath);
						} catch (Exception e) {
							LOG.log(Level.SEVERE, "Exception occured for real file path: "+realFilePath+", Exception message: "+e.getMessage(), e);
						}
						att.setFileImage(fileByteArray);
					} 
				}
			}
			
		}
	}

	
	
	
}
