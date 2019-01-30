package com.avnet.emasia.webquote.utilities.ejb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;

import com.avnet.emasia.webquote.entity.SystemCodeMaintenance;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.utilities.bean.MailInfoBean;
import com.avnet.emasia.webquote.utilities.common.Constants;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;
import com.avnet.emasia.webquote.utilities.schedule.HATimerService;

/**
 * Session Bean implementation class MailUtils
 */
@Stateless
@LocalBean
public class MailUtilsSB {

	private static final Logger LOG = Logger.getLogger(MailUtilsSB.class.getName());
	public static final String ERROR_EMAIL_TO_ADDRESS = "ERROR_EMAIL_TO_ADDRESS";
	public static final String ERROR_EMAIL_FROM_ADDRESS = "ERROR_EMAIL_FROM_ADDRESS";
	@Resource(lookup = Constants.EMAIL_JNDI)
	private Session sendMailSession;
	@EJB
	private SysConfigSB sysConfigSB;

	@PersistenceContext(unitName = "Server_Source")
	private EntityManager em;

	/**
	 * Send text email.if use Resource inject in session ,just only configure
	 * jnid in jboss server.
	 * 
	 * @param mailInfoBean
	 */
	public void sendTextMail(MailInfoBean mailInfoBean)
			throws IllegalArgumentException, NamingException, MessagingException, AddressException {
		if (mailInfoBean == null) {
			LOG.severe("Sets mail paramater error!");
			return;
		}
		MimeMessage mailMessage = new MimeMessage(sendMailSession);
		Address from = null;
		if (mailInfoBean.getMailFrom() != null && mailInfoBean.getMailFrom().length() > 0) {
			from = new InternetAddress(mailInfoBean.getMailFrom());
		}
		mailMessage.setFrom(from);
		List<String> toLst = mailInfoBean.getMailTo();
		toLst.clear();
		toLst.add("Vincent.Wang@AVNET.COM");
		mailMessage = getTextMessage(mailMessage, toLst);
		mailMessage = sendTextEmail(mailInfoBean, mailMessage);
		MimeBodyPart mbpTxt = new MimeBodyPart();
		mbpTxt.setText(mailInfoBean.getMailContent() + "\n");
		Multipart mp = new MimeMultipart("mixed");
		mp.addBodyPart(mbpTxt);
		mailMessage.setContent(mp);
		send(mailMessage);
		String mailToStr = "";
		String mailCcStr = "";
		mailToStr = getMailToStr(mailInfoBean, mailToStr);
		mailToStr ="vincent.wang@avnet.com";
		mailCcStr = getCcStr(mailInfoBean, mailCcStr);
		StringBuffer logStr = new StringBuffer("send text email ");
		if (mailToStr.length() > 0) {
			logStr.append(mailToStr);
		}
		if (mailCcStr.length() > 0) {
			logStr.append(mailCcStr);
		}

		logStr.append(" successfully!");
		LOG.info(logStr.toString());
	}

	private String getMailToStr(final MailInfoBean mailInfoBean, String mailToStr) {
		if (mailInfoBean != null) {
			if (mailInfoBean.getMailTo() != null && mailInfoBean.getMailTo().size() > 0) {
				mailToStr = "to ";
				for (String s : mailInfoBean.getMailTo()) {
					if (s != null && !s.equals("")) {
						mailToStr += s + ",";
					}
				}
				mailToStr = mailToStr.substring(0, mailToStr.length() - 1);
			}
		}
		return mailToStr;
	}

	private String getCcStr(final MailInfoBean mailInfoBean, String mailCcStr) {
		if (mailInfoBean != null) {
			if (mailInfoBean.getMailCc() != null && mailInfoBean.getMailCc().size() > 0) {
				mailCcStr = "cc ";
				for (String s : mailInfoBean.getMailCc()) {
					if (s != null && !s.equals("")) {
						mailCcStr += s + ",";
					}
				}
				mailCcStr = mailCcStr.substring(0, mailCcStr.length() - 1);
			}
		}
		return mailCcStr;
	}

	/**
	 * Send html email.if use Resource inject in session ,just only configure
	 * jnid in jboss server.
	 * 
	 * @param from
	 * @param to
	 * @param subject
	 * @param content
	 */
	public void sendHtmlMail(MailInfoBean mailInfoBean)
			throws IllegalArgumentException, NamingException, MessagingException, AddressException {
		if (mailInfoBean == null) {
			LOG.severe("Sets mail paramater error!");
			return;
		}
		Message mailMessage = new MimeMessage(sendMailSession);
		Address from = null;
		if (mailInfoBean.getMailFrom() != null && mailInfoBean.getMailFrom().length() > 0) {
			try {
				from = new InternetAddress(mailInfoBean.getMailFrom(), mailInfoBean.getMailFromInName());
			} catch (UnsupportedEncodingException e) {
				LOG.log(Level.SEVERE, "error in sending mail from : " + mailInfoBean.getMailFrom()
						+ ", Error message : " + e.getMessage(), e);
			}
		}
		mailMessage.setFrom(from);
		List<String> toLst = mailInfoBean.getMailTo();

		mailMessage = getTextMessage(mailMessage, toLst);
		mailMessage = sendTextEmail(mailInfoBean, mailMessage);
		Multipart mp = new MimeMultipart();
		// our file system must to use .7z suffix file name
		if (mailInfoBean.getFileByteArray() != null) {
			// need to invoke compress file method to compress attached
			// file.
			MimeBodyPart mbp = new MimeBodyPart();
			mbp.setDataHandler(new DataHandler(mailInfoBean.getFileByteArray()));
			mbp.setFileName(mailInfoBean.getFileName());
			mp.addBodyPart(mbp);
		}

		BodyPart html = new MimeBodyPart();
		String content = mailInfoBean.getMailContent();
		content = content.replaceAll("%", "&#37;");
		content = content.replaceAll("'", "&#39;");
		String mailToStr = "";
		String mailCcStr = "";
		sendAttachedEmail(html, mp, mailMessage, mailInfoBean, mailToStr, mailCcStr, content);
		StringBuffer logStr = new StringBuffer("send html email ");
		if (mailToStr.length() > 0) {
			logStr.append(mailToStr);
		}
		if (mailCcStr.length() > 0) {
			logStr.append(mailCcStr);
		}
		logStr.append(" successfully!");
		LOG.info(logStr.toString());
	}

	private MimeMessage sendTextEmail(final MailInfoBean mailInfoBean, final Message mailMessage)
			throws AddressException, MessagingException {
		List<String> ccLst = mailInfoBean.getMailCc();
		if (((ccLst != null) && (ccLst.size()) > 0)) {
			int capacity = 0;
			for (String sd : ccLst) {
				if (sd != null && !sd.equals("")) {
					capacity++;
				}
			}
			if (capacity > 0) {
				InternetAddress[] address = new InternetAddress[capacity];
				int i = 0;
				for (String sCc : ccLst) {
					if (sCc != null && !sCc.equals("")) {
						address[i++] = new InternetAddress(sCc);
					}
				}
				mailMessage.addRecipients(Message.RecipientType.CC, address);
			}
		}
		List<String> bccLst = mailInfoBean.getMailBcc();
		if (((bccLst != null) && (bccLst.size()) > 0)) {
			int capacity = 0;
			for (String bcc : bccLst) {
				if (bcc != null && !bcc.equals("")) {
					capacity++;
				}
			}
			if (capacity > 0) {
				InternetAddress[] address = new InternetAddress[capacity];
				int i = 0;
				for (String sBcc : bccLst) {
					if (sBcc != null && !sBcc.equals("")) {
						address[i++] = new InternetAddress(sBcc);
					}
				}
				mailMessage.addRecipients(Message.RecipientType.BCC, address);
			}
		}
		mailMessage.setSubject(mailInfoBean.getMailSubject());
		mailMessage.setSentDate(new Date());
		return (MimeMessage) mailMessage;
	}

	/**
	 * compress email attached file.
	 * 
	 * @param targetFileName
	 * @param fileNames
	 * @return
	 * @throws IOException
	 */
	public static File compressFile(String targetFileName, List<String> fileNameLst) throws IOException {
		byte[] buffer = new byte[1024];
		// Create input and output streams
		File f = new File(targetFileName);
		ZipOutputStream outStream = new ZipOutputStream(new FileOutputStream(f));
		if ((fileNameLst != null) && (fileNameLst.size() > 0)) {
			for (String fileName : fileNameLst) {
				FileInputStream in = new FileInputStream(fileName);
				outStream.putNextEntry(new ZipEntry(fileName));
				int bytesRead;
				// Each chunk of data read from the input stream
				// is written to the output stream
				while ((bytesRead = in.read(buffer)) > 0) {
					outStream.write(buffer, 0, bytesRead);
				}
				// Close zip entry and file streams
				outStream.closeEntry();
				in.close();
			}
		}
		outStream.close();
		LOG.info("compress email successfully!");
		return f;
	}

	/**
	 * For offline reprot.
	 * 
	 * @param mailInfoBean
	 */
	public void sendAttachedMail(MailInfoBean mailInfoBean)
			throws IllegalArgumentException, NamingException, MessagingException, AddressException {
		if (mailInfoBean == null) {
			LOG.severe("Sets mail paramater error!");
			return;
		}
		Message mailMessage = new MimeMessage(sendMailSession);
		Address from = null;
		if (mailInfoBean.getMailFrom() != null && mailInfoBean.getMailFrom().length() > 0) {
			from = new InternetAddress(mailInfoBean.getMailFrom());
		}

		mailMessage.setFrom(from);
		List<String> toLst = mailInfoBean.getMailTo();
		mailMessage = getTextMessage(mailMessage, toLst);
		mailMessage = sendTextEmail(mailInfoBean, mailMessage);
		Multipart mp = new MimeMultipart();
		if (mailInfoBean.getZipFile() != null) {
			FileDataSource fds = new FileDataSource(mailInfoBean.getZipFile());
			MimeBodyPart mbp = new MimeBodyPart();
			mbp.setDataHandler(new DataHandler(fds));
			mbp.setHeader("Content-Type", "application/vnd.ms-excel");
			mbp.setFileName(fds.getName());
			mp.addBodyPart(mbp);
		}
		String mailToStr = "";
		String mailCcStr = "";
		BodyPart html = new MimeBodyPart();
		String content = mailInfoBean.getMailContent();
		sendAttachedEmail(html, mp, mailMessage, mailInfoBean, mailToStr, mailCcStr, content);
		StringBuffer logStr = new StringBuffer("send attach html email ");
		if (mailToStr.length() > 0) {
			logStr.append(mailToStr);
		}
		if (mailCcStr.length() > 0) {
			logStr.append(mailCcStr);
		}

		logStr.append(" successfully!");
		LOG.info(logStr.toString());
	}

	private MimeMessage getTextMessage(final Message mailMessage, final List<String> toLst)
			throws AddressException, MessagingException {
		if (((toLst != null) && (toLst.size()) > 0)) {
			int capacity = 0;
			for (String t : toLst) {
				if (t != null && !t.equals("")) {
					capacity++;
				}
			}
			if (capacity > 0) {
				InternetAddress[] address = new InternetAddress[capacity];
				int i = 0;
				for (String sTo : toLst) {
					if (sTo != null && !sTo.equals("")) {
						address[i++] = new InternetAddress(sTo);
					}
				}
				mailMessage.addRecipients(Message.RecipientType.TO, address);
			}
		}
		return (MimeMessage) mailMessage;
	}

	private void sendAttachedEmail(final BodyPart html, final Multipart mp, final Message mailMessage,
			final MailInfoBean mailInfoBean, String mailToStr, String mailCcStr, final String content)
			throws MessagingException {

		html.setContent("<font face=\"calibri\">" + content + "</font>", "text/html; charset=utf-8");
		mp.addBodyPart(html);
		mailMessage.setContent(mp);
		send(mailMessage);
		mailToStr = getMailToStr(mailInfoBean, mailToStr);
		mailCcStr = getCcStr(mailInfoBean, mailCcStr);

	}

	private void send(Message mailMessage) throws MessagingException {
		try {
			Transport.send(mailMessage);
		} catch (MessagingException e) {
			if (e instanceof SendFailedException) {
				LOG.log(Level.WARNING, ">>>>>Catch SendFailedException<<<<<<<<<<<", "");
				Address[] addrs = ((SendFailedException) e).getInvalidAddresses();
				if (addrs != null && addrs.length > 0) {
					for (Address failAddr : addrs) {
						InternetAddress addr = (InternetAddress) failAddr;
						String failEmail = addr.getAddress();
						LOG.log(Level.WARNING, ">>>>>Fail Email Address <<<<<<<<<<<,==" + failEmail, "");

						String emailfrom = "Webquote@Avnet.com";
						if (StringUtils.isNotBlank(failEmail)) {
							List<SystemCodeMaintenance> commonCenters = findCommonCenter();
							failEmail = failEmail.trim();
							User failUser = findByEmailAddr(failEmail);
							if (failUser != null) {
								LOG.log(Level.WARNING, ">>>>>Fail Email User In APP_USER <<<<<<<<<<<", "");
								for (SystemCodeMaintenance center : commonCenters) {
									String region = center.getRegion();
									if (StringUtils.equals(region, failUser.getDefaultBizUnit().getName())) {
										emailfrom = center.getValue().trim();
										break;
									}
								}
								List<User> userAdmins = findUserAdminsByRegion(failUser.getDefaultBizUnit().getName());
								if (userAdmins != null && userAdmins.size() > 0) {
									LOG.log(Level.WARNING, ">>>>>Fail Email User Has User Admins <<<<<<<<<<<", "");
									List<String> to = new ArrayList<String>();
									String toStr = "";
									for (User admin : userAdmins) {
										String emailAddr = admin.getEmailAddress();
										if (StringUtils.isNotBlank(emailAddr)) {
											to.add(emailAddr.trim());
											toStr += emailAddr.trim() + ",";
										}
									}
									LOG.info(">>>>>User admins ===" + toStr);
									sendEmailtoAdmin(emailfrom, to, failEmail);
								} else {
									LOG.log(Level.WARNING, ">>>>>Fail Email User Not Has User Admins <<<<<<<<<<<", "");
									sendToDevTeam(emailfrom, failEmail);

								}
							} else {
								LOG.log(Level.WARNING, ">>>>>Fail Email User Not In APP_USER <<<<<<<<<<<", "");
								sendToDevTeam(emailfrom, failEmail);
							}
						}
					}
				}
			} else {
				LOG.info(">>>>>Send Email Error<<<<<<<<<<<");
				LOG.log(Level.SEVERE, "Error in mail sending " + e.getMessage(), e);
				throw e;
			}

		}
	}

	private void sendToDevTeam(String emailfrom, String failAddr) throws MessagingException {
		LOG.info(">>>>>Send Email to Dev Team <<<<<<<<<<<");
		String recipients = sysConfigSB.getProperyValue(ERROR_EMAIL_TO_ADDRESS);
		List<String> to = new ArrayList<String>();
		if (null != recipients && !recipients.isEmpty()) {
			String[] tos = recipients.split(";");
			for (String recipient : tos) {
				if (null != recipient && !recipient.trim().isEmpty())
					to.add(recipient);
			}
		}
		sendEmailtoAdmin(emailfrom, to, failAddr);
	}

	private void sendEmailtoAdmin(String emailfrom, List<String> to, String failAddr) throws MessagingException {
		LOG.info(">>>>>Send Email to  Admins <<<<<<<<<<<");
		String env = sysConfigSB.getProperyValue("CURRENT_ENV");
		String subject = "Invalid Email Address In WebQuote(Server:"
				+ System.getProperty(HATimerService.JBOSS_NODE_NAME) + ",Env:" + env + ")";
		String content = "The following email address is no longer valid. Please update User Profile.<br/><br/>";
		StringBuffer contentBuf = new StringBuffer(content);
		User failUser = findByEmailAddr(failAddr);
		if (failUser != null) {
			contentBuf.append(failUser.getEmployeeId()).append("&nbsp;&nbsp;&nbsp;&nbsp;").append(failUser.getName())
					.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		}
		contentBuf.append(failAddr).append("<br/>");
		MimeMessage msg = new MimeMessage(sendMailSession);
		Address from = new InternetAddress(emailfrom);
		msg.setFrom(from);

		InternetAddress[] address = new InternetAddress[to.size()];
		int i = 0;
		for (String sTo : to) {
			if (StringUtils.isNotBlank(sTo)) {
				address[i++] = new InternetAddress(sTo);
			}
		}
		msg.addRecipients(Message.RecipientType.TO, address);
		msg.setSubject(subject);
		msg.setSentDate(new Date());
		Multipart mp = new MimeMultipart();
		BodyPart html = new MimeBodyPart();
		content = content.replaceAll("%", "&#37;");
		content = content.replaceAll("'", "&#39;");
		html.setContent("<font face=\"calibri\">" + contentBuf.toString() + "</font>", "text/html; charset=utf-8");
		mp.addBodyPart(html);
		msg.setContent(mp);
		Transport.send(msg);
		LOG.info(">>>>>Send Email to  Admins Successfully <<<<<<<<<<<");
	}

	private User findByEmailAddr(String emailAddr) {
		Query query = em
				.createQuery("select distinct u from User u where u.active = true and u.emailAddress=:emailAddress");
		query.setParameter("emailAddress", emailAddr);
		User user = null;
		List<User> users = (List<User>) query.getResultList();
		if (users != null && users.size() > 0) {
			user = users.get(0);
		}
		return user;
	}

	private List<User> findUserAdminsByRegion(String region) {
		String sql = "select distinct u from User u join fetch  u.roles r join fetch  u.bizUnits b where u.active = true "
				+ " and r.name =:roleName and b.name = :region order by u.name ";
		Query query = em.createQuery(sql);
		query.setParameter("roleName", "ROLE_USER_ADMIN");
		query.setParameter("region", region);
		List<User> users = query.getResultList();
		return users;
	}

	private List<SystemCodeMaintenance> findCommonCenter() {
		List<SystemCodeMaintenance> commonCenters = em
				.createQuery("select c from SystemCodeMaintenance c where c.category='EMAIL_SIGNATURE_EMAIL_ADDRESS'")
				.getResultList();
		return commonCenters;
	}

	@Asynchronous
	public void sendErrorEmail(String msg, String title)
			throws AddressException, IllegalArgumentException, NamingException, MessagingException {

		String sender = sysConfigSB.getProperyValue(ERROR_EMAIL_FROM_ADDRESS);
		String recipients = sysConfigSB.getProperyValue(ERROR_EMAIL_TO_ADDRESS);
		recipients ="Vincent.Wang@AVNET.COM";
		List<String> to = new ArrayList<String>();
		if (null != recipients && !recipients.isEmpty()) {
			String[] tos = recipients.split(";");
			for (String recipient : tos) {
				if (null != recipient && !recipient.trim().isEmpty())
					to.add(recipient);
			}
		}

		MailInfoBean mail = new MailInfoBean();
		mail.setMailSubject(title);
		mail.setMailFrom(sender);
		mail.setMailTo(to);
		msg += " --from server: " + System.getProperty(HATimerService.JBOSS_NODE_NAME);
		mail.setMailContent(msg);
		this.sendTextMail(mail);
	}
}
