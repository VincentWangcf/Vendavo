package com.avnet.emasia.webquote.utilities.bean;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import javax.mail.util.ByteArrayDataSource;

import com.avnet.emasia.webquote.utilities.common.Constants.MAIL_TYPE;

/**
 * 
 * @author 914975
 */
public class MailInfoBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mailFrom;
	private String mailFromInName;
	private List<String> mailTo;
	private List<String> mailCc;
	private List<String> mailBcc;
	private String mailSubject;
	private String mailContent;
	private String mailServerHost;
	private String mailServerPort; //= "25";
	private String mailUserName;
	private String mailPassword;
	private boolean mailIsValidate = false;
	private String[] mailAttachFileNames;
	private transient ByteArrayDataSource fileByteArray;	//This class is not Serializable, need to make transient (Chris Lam, 2013/08/01)
	private MAIL_TYPE mailType;
	private File zipFile;
	private String fileName = "default.xls";

	//todo
	/**
	 * this constructor need configure in jboss and the java session need to use @Resouce to inject to dynamic.
	 */
	public MailInfoBean() {
//		this.mailServerHost = "SMTPRELAY.AVNET.COM";
//		this.mailFrom = "Berry.Zhu2@AVNET.COM";
//		mailTo = new ArrayList<String>();
//		this.mailTo.add("Berry.Zhu2@AVNET.COM");
//		this.mailSubject = "hello berry berry";
//		this.mailContent = "99999999999999999999999999999999999999999999999999999";
//		this.mailType = Constants.MAIL_TYPE.TEXTMAIL;
	}
	
	public List<String> getMailBcc() {
		return mailBcc;
	}

	public void setMailBcc(List<String> mailBcc) {
		this.mailBcc = mailBcc;
	}

	public List<String> getMailTo() {
		return mailTo;
	}

	public void setMailTo(List<String> mailTo) {
		this.mailTo = mailTo;
	}

	public List<String> getMailCc() {
		return mailCc;
	}

	public void setMailCc(List<String> mailCc) {
		this.mailCc = mailCc;
	}

	/**
	 * @return the mailFrom
	 */
	public String getMailFrom() {
		return mailFrom;
	}

	/**
	 * @param mailFrom
	 *            the mailFrom to set
	 */
	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}

	/**
	 * @return the mailSubject
	 */
	public String getMailSubject() {
		return mailSubject;
	}

	/**
	 * @param mailSubject
	 *            the mailSubject to set
	 */
	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}

	/**
	 * @return the mailContent
	 */
	public String getMailContent() {
		return mailContent;
	}

	/**
	 * @param mailContent
	 *            the mailContent to set
	 */
	public void setMailContent(String mailContent) {
		this.mailContent = mailContent;
	}

	/**
	 * @return the mailServerHost
	 */
	public String getMailServerHost() {
		return mailServerHost;
	}

	/**
	 * @param mailServerHost
	 *            the mailServerHost to set
	 */
	public void setMailServerHost(String mailServerHost) {
		this.mailServerHost = mailServerHost;
	}

	/**
	 * @return the mailServerPort
	 */
	public String getMailServerPort() {
		return mailServerPort;
	}

	/**
	 * @param mailServerPort
	 *            the mailServerPort to set
	 */
	public void setMailServerPort(String mailServerPort) {
		this.mailServerPort = mailServerPort;
	}

	/**
	 * @return the mailUserName
	 */
	public String getMailUserName() {
		return mailUserName;
	}

	/**
	 * @param mailUserName
	 *            the mailUserName to set
	 */
	public void setMailUserName(String mailUserName) {
		this.mailUserName = mailUserName;
	}

	/**
	 * @return the mailPassword
	 */
	public String getMailPassword() {
		return mailPassword;
	}

	/**
	 * @param mailPassword
	 *            the mailPassword to set
	 */
	public void setMailPassword(String mailPassword) {
		this.mailPassword = mailPassword;
	}

	/**
	 * @return the mailAttachFileNames
	 */
	public String[] getMailAttachFileNames() {
		return mailAttachFileNames;
	}

	/**
	 * @param mailAttachFileNames
	 *            the mailAttachFileNames to set
	 */
	public void setMailAttachFileNames(String[] mailAttachFileNames) {
		this.mailAttachFileNames = mailAttachFileNames;
	}

	/**
	 * @return the mailIsValidate
	 */
	public boolean getMailIsValidate() {
		return mailIsValidate;
	}

	/**
	 * @param mailIsValidate
	 *            the mailIsValidate to set
	 */
	public void setMailIsValidate(boolean mailIsValidate) {
		this.mailIsValidate = mailIsValidate;
	}

	/**
	 * @return the emailType
	 */
	public MAIL_TYPE getMailType() {
		return mailType;
	}

	/**
	 * @param emailType
	 *            the emailType to set
	 */
	public void setMailType(MAIL_TYPE mailType) {
		this.mailType = mailType;
	}

	public File getZipFile() {
		return zipFile;
	}

	public void setZipFile(File zipFile) {
		this.zipFile = zipFile;
	}

	public ByteArrayDataSource getFileByteArray() {
		return fileByteArray;
	}

	public void setFileByteArray(ByteArrayDataSource fileByteArray) {
		this.fileByteArray = fileByteArray;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getMailFromInName() {
		return mailFromInName;
	}

	public void setMailFromInName(String mailFromInName) {
		this.mailFromInName = mailFromInName;
	}
	
	
}