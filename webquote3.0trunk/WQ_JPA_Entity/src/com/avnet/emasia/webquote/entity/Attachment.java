package com.avnet.emasia.webquote.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.avnet.emasia.webquote.entity.util.AttachmentListenter;


/**
 * The persistent class for the ATTACHMENT database table.
 * 
 */
@Entity
@Table(name="ATTACHMENT")
@EntityListeners(AttachmentListenter.class)
public class Attachment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="ATTACHMENT_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ATTACHMENT_ID_GENERATOR")
	@Column(unique=true, nullable=false)
	private Long id;

	///@Basic(fetch=FetchType.LAZY)
	//@Column(name="FILE_IMAGE")
	@Transient
	private byte[] fileImage;

	@Column(name="FILE_NAME", length=255)
	private String fileName;
	
	@Column(name="TYPE", length=20)
	private String type;
	
	@Column(name="FILE_NAME_ACTUAL", length=1000)
	private String fileNameActual;
	
	@Column(name="FILE_PATH", length=500)
	private String filePath;
	
	//bi-directional many-to-one association to QuoteItem
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="QUOTE_ITEM_ID", nullable=false)
	private QuoteItem quoteItem;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="QUOTE_ID", nullable=false)
	private Quote quote;	
	
	@Transient
	private boolean newAttachment = false;
	
	public Attachment() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public byte[] getFileImage() {
		return this.fileImage;
	}

	public void setFileImage(byte[] fileImage) {
		this.fileImage = fileImage;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public QuoteItem getQuoteItem() {
		return this.quoteItem;
	}

	public void setQuoteItem(QuoteItem quoteItem) {
		this.quoteItem = quoteItem;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Quote getQuote() {
		return quote;
	}

	public void setQuote(Quote quote) {
		this.quote = quote;
	}

	public boolean isNewAttachment() {
		return newAttachment;
	}

	public void setNewAttachment(boolean newAttachment) {
		this.newAttachment = newAttachment;
	}

	public String getFileNameActual() {
		return fileNameActual;
	}

	public void setFileNameActual(String fileNameActual) {
		this.fileNameActual = fileNameActual;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}