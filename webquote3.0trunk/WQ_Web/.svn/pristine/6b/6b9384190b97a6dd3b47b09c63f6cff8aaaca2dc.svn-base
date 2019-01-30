package com.avnet.emasia.webquote.web.quote.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.avnet.emasia.webquote.entity.Attachment;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.User;

public class WorkingPlatformEmailVO implements Serializable {
	
	private List<QuoteItem> quoteItems;
	private String formNumber;
	private List<String> emailsTo;
	private List<User> emailsToSelectList;
	private List<String> emailsCc;
	private List<User> emailsCcSelectList;
	/*
	private List<User> emailToAddresses;
	private List<User> emailCcAddresses;
	*/
	private String message;
	private List<Attachment> attachments;
	
	/*
	 * for quotation only
	 */
	private List<String> customers;
	private List<String> projects;	
	/*
	 *	end for quotation only 
	 */
	
	/*
	 * for internal transfer only
	 */
	private String mfr;
	private List<String> partNumbers;
	/*
	 * end for internal transfer only
	 */
	
	
	/*
	 * for invalidate quote pop up only	
	 */	
	private String soldToCustomerName;
	private String endCustomerName;
	private String salesman;
	private String team;	
	private List<String> mfrs;
	/*
	 * end for invalidate quote pop up only
	 */
	
	public List<QuoteItem> getQuoteItems() {
		return quoteItems;
	}
	public void setQuoteItems(List<QuoteItem> quoteItems) {
		this.quoteItems = quoteItems;
	}
	public String getMfr() {
		return mfr;
	}
	public void setMfr(String mfr) {
		this.mfr = mfr;
	}
	public List<String> getPartNumbers() {
		return partNumbers;
	}
	public void setPartNumbers(List<String> partNumbers) {
		this.partNumbers = partNumbers;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<String> getCustomers() {
		return customers;
	}
	public void setCustomers(List<String> customers) {
		this.customers = customers;
	}
	public List<String> getProjects() {
		return projects;
	}
	public void setProjects(List<String> projects) {
		this.projects = projects;
	}
	public List<Attachment> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public String getFormNumber() {
		return formNumber;
	}
	public void setFormNumber(String formNumber) {
		this.formNumber = formNumber;
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
	public String getSoldToCustomerName() {
		return soldToCustomerName;
	}
	public void setSoldToCustomerName(String soldToCustomerName) {
		this.soldToCustomerName = soldToCustomerName;
	}
	public String getEndCustomerName() {
		return endCustomerName;
	}
	public void setEndCustomerName(String endCustomerName) {
		this.endCustomerName = endCustomerName;
	}
	public String getSalesman() {
		return salesman;
	}
	public void setSalesman(String salesman) {
		this.salesman = salesman;
	}
	public String getTeam() {
		return team;
	}
	public void setTeam(String team) {
		this.team = team;
	}
	public List<String> getMfrs() {
		return mfrs;
	}
	public void setMfrs(List<String> mfrs) {
		this.mfrs = mfrs;
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
		
}
