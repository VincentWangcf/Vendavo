package com.avnet.emasia.webquote.web.quote.vo;

import java.io.Serializable;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.Quote;

public class QuotationEmailVO implements Serializable {
	
	private List<String> toEmails;
	private List<String> ccEmails;
	private List<String> bccEmails;
	private String fromEmail;
	private String fromEmailInName;
	private String subject;
	private String recipient;
	private String formNumber;
	private long quoteId;
	private String link;
	private String sender;
	private transient HSSFWorkbook hssfWorkbook;
	private String remark;
	private String fileName;
	
	//Added by DamonChen20190919
	private Customer SoldToCustomer;
	private Quote quote;
	//the submission date in Excel file will from quote if isSubmissionDateFromQuotei true
	private boolean isSubmissionDateFromQuote;
	
	
	
	
	
	public Quote getQuote() {
		return quote;
	}
	public void setQuote(Quote quote) {
		this.quote = quote;
	}
	public boolean isSubmissionDateFromQuote() {
		return isSubmissionDateFromQuote;
	}
	public void setSubmissionDateFromQuote(boolean isSubmissionDateFromQuote) {
		this.isSubmissionDateFromQuote = isSubmissionDateFromQuote;
	}
	
	public Customer getSoldToCustomer() {
		return SoldToCustomer;
	}
	public void setSoldToCustomer(Customer soldToCustomer) {
		SoldToCustomer = soldToCustomer;
	}
	public List<String> getToEmails() {
		return toEmails;
	}
	public void setToEmails(List<String> toEmails) {
		this.toEmails = toEmails;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getRecipient() {
		return recipient;
	}
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}
	public String getFormNumber() {
		return formNumber;
	}
	public void setFormNumber(String formNumber) {
		this.formNumber = formNumber;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public HSSFWorkbook getHssfWorkbook() {
		return hssfWorkbook;
	}
	public void setHssfWorkbook(HSSFWorkbook hssfWorkbook) {
		this.hssfWorkbook = hssfWorkbook;
	}
	public String getFromEmail() {
		return fromEmail;
	}
	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}
	public List<String> getCcEmails() {
		return ccEmails;
	}
	public void setCcEmails(List<String> ccEmails) {
		this.ccEmails = ccEmails;
	}
	public long getQuoteId() {
		return quoteId;
	}
	public void setQuoteId(long quoteId) {
		this.quoteId = quoteId;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public List<String> getBccEmails() {
		return bccEmails;
	}
	public void setBccEmails(List<String> bccEmails) {
		this.bccEmails = bccEmails;
	}
	public String getFromEmailInName() {
		return fromEmailInName;
	}
	public void setFromEmailInName(String fromEmailInName) {
		this.fromEmailInName = fromEmailInName;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}
