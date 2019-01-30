package com.avnet.emasia.webquote.web.quote.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.User;

public class ProceedQuotationVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2128865840919204683L;
	private List<QuoteItem> quoteItems;
	private String employeeName;
	private String employeeId;
	private String formId;
	private String formNumber;
	private String message;
	private Customer soldToCustomer;
	private Quote quote;
	private List<String> emailsTo;
	private List<User> emailsToSelectList;
	private List<String> emailsCc;
	private List<User> emailsCcSelectList;
	/*
	private List<User> emailToAddresses;
	private List<User> emailCcAddresses;
	*/
	private List<Long> highlightedRecords;
	
	public List<QuoteItem> getQuoteItems() {
		return quoteItems;
	}
	public void setQuoteItems(List<QuoteItem> quoteItems) {
		this.quoteItems = quoteItems;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getFormNumber() {
		return formNumber;
	}
	public void setFormNumber(String formNumber) {
		this.formNumber = formNumber;
	}
	public Customer getSoldToCustomer() {
		return soldToCustomer;
	}
	public void setSoldToCustomer(Customer soldToCustomer) {
		this.soldToCustomer = soldToCustomer;
	}
	public Quote getQuote() {
		return quote;
	}
	public void setQuote(Quote quote) {
		this.quote = quote;
	}
	public List<Long> getHighlightedRecords() {
		return highlightedRecords;
	}
	public void setHighlightedRecords(List<Long> highlightedRecords) {
		this.highlightedRecords = highlightedRecords;
	}
	public List<String> getEmailsTo() {
		return emailsTo;
	}
	public void setEmailsTo(List<String> emailsTo) {
		this.emailsTo = emailsTo;
	}
	public List<String> getEmailsCc() {
		return emailsCc;
	}
	public void setEmailsCc(List<String> emailsCc) {
		this.emailsCc = emailsCc;
	}
	/*
	public List<User> getEmailToAddresses() {
		return emailToAddresses;
	}
	public void setEmailToAddresses(List<User> emailToAddresses) {
		this.emailToAddresses = emailToAddresses;
	}
	public List<User> getEmailCcAddresses() {
		return emailCcAddresses;
	}
	public void setEmailCcAddresses(List<User> emailCcAddresses) {
		this.emailCcAddresses = emailCcAddresses;
	}
	*/
	public List<User> getEmailsToSelectList() {
		return emailsToSelectList;
	}
	public void setEmailsToSelectList(List<String> emailsTo, List<User> emailsToSelectList) {
		this.emailsToSelectList = new ArrayList<User>();
		List<User> unselectedList = new ArrayList<User>();
		for(User user : emailsToSelectList){
			if(emailsTo.contains(user.getEmployeeId()))
				this.emailsToSelectList.add(user);
			else
				unselectedList.add(user);
		}
		this.emailsToSelectList.addAll(unselectedList);
	}
	public List<User> getEmailsCcSelectList() {
		return emailsCcSelectList;
	}
	public void setEmailsCcSelectList(List<String> emailsCc, List<User> emailsCcSelectList) {
		this.emailsCcSelectList = new ArrayList<User>();
		List<User> unselectedList = new ArrayList<User>();
		for(User user : emailsCcSelectList){
			if(emailsCc.contains(user.getEmployeeId()))
				this.emailsCcSelectList.add(user);
			else
				unselectedList.add(user);
		}
		this.emailsCcSelectList.addAll(unselectedList);
	}
	public String getFormId() {
		return formId;
	}
	public void setFormId(String formId) {
		this.formId = formId;
	}
	
	
}
