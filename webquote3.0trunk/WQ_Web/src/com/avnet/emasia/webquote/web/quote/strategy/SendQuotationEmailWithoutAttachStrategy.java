/**
 * 
 */
package com.avnet.emasia.webquote.web.quote.strategy;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.mail.util.ByteArrayDataSource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jboss.logmanager.Level;

import com.avnet.emasia.webquote.dp.EJBCommonSB;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.ejb.SystemCodeMaintenanceSB;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilities.DateUtils;
import com.avnet.emasia.webquote.utilities.bean.MailInfoBean;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;
import com.avnet.emasia.webquote.utilities.ejb.MailUtilsSB;
import com.avnet.emasia.webquote.web.quote.constant.QuoteConstant;
import com.avnet.emasia.webquote.web.quote.vo.QuotationEmailVO;

/**
 * @author 042659
 *
 */
public class SendQuotationEmailWithoutAttachStrategy implements SendQuotationEmailStrategy {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1690752270928622289L;
	private static final Logger LOGGER= Logger.getLogger(SendQuotationEmailWithoutAttachStrategy.class.getName());
	private SysConfigSB sysConfigSB;
	private UserSB userSB;
	private  MailUtilsSB mailUtilsSB;
	
	private SystemCodeMaintenanceSB systemCodeMaintenanceSB;

	private EJBCommonSB ejbCommonSB;

	/**
	 * 
	 */
	public SendQuotationEmailWithoutAttachStrategy() {
		// TODO Auto-generated constructor stub
		initSB();
	}
	
	/**   
	* @Description: init the dependency  SBs
	* @author 042659       
	* @return void    
	* @throws  
	*/  
	private void initSB() {
		try {
			Map<String, Object> sBMap = getLookupSB();
			userSB = (UserSB) sBMap.get("userSB");
			sysConfigSB = (SysConfigSB) sBMap.get("sysConfigSB");

			mailUtilsSB = (MailUtilsSB) sBMap.get("mailUtilsSB");

			systemCodeMaintenanceSB = (SystemCodeMaintenanceSB) sBMap.get("systemCodeMaintenanceSB");
			sysConfigSB = (SysConfigSB) sBMap.get("sysConfigSB");

			ejbCommonSB = (EJBCommonSB) sBMap.get("ejbCommonSB");

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception in initSB, exception message : " + e.getMessage(), e);
		}

	}

	/* (non-Javadoc)
	 * @see com.avnet.emasia.webquote.web.quote.strategy.SendQuotationEmailStrategy#sendQuotationEmail(com.avnet.emasia.webquote.web.quote.vo.QuotationEmailVO)
	 */
	@Override
	public void sendQuotationEmail(QuotationEmailVO vo) {

		LOGGER.info("sendQuotationEmail(QuotationEmailVO vosendQuotationEmail)  begin");
		MailInfoBean mailInfoBean = new MailInfoBean();
		mailInfoBean.setMailSubject(vo.getSubject());
		mailInfoBean.setMailFrom(vo.getFromEmail());
		mailInfoBean.setMailFromInName(vo.getFromEmailInName());
		if (vo.getToEmails() != null && vo.getToEmails().size() > 0) {
			List<User> users = userSB.findByEmployeeIds(vo.getToEmails());
			List<String> emails = new ArrayList<String>();
			for (User user : users) {
				emails.add(user.getEmailAddress());
			}
			mailInfoBean.setMailTo(emails);
		}
		if (vo.getCcEmails() != null && vo.getCcEmails().size() > 0) {
			List<User> users = userSB.findByEmployeeIds(vo.getCcEmails());
			List<String> emails = new ArrayList<String>();
			for (User user : users) {
				emails.add(user.getEmailAddress());
			}
			mailInfoBean.setMailCc(emails);
		}
		mailInfoBean.setMailBcc(vo.getBccEmails());

		String salesLink = getUrl() + "/RFQ/MyQuoteListForSales.jsf";
		String csLink = getUrl() + "/RFQ/MyQuoteListForCS.jsf";
		String content = "Dear " + vo.getRecipient() + ",<br/><br/>";
		content += "Remark: " + (vo.getRemark() == null ? "" : vo.getRemark()) + "<br/><br/>";
		content += "Good Selling!<br/><br/>";
		content += "RFQ Form: " + vo.getFormNumber() + "<br/>";
		content += "<a href=\"" + salesLink + "?quoteId=" + vo.getQuoteId() + "\">" + salesLink + "?quoteId="
				+ vo.getQuoteId() + "</a><br/><br/>";
		if (vo.getCcEmails() != null && vo.getCcEmails().size() > 0) {
			content += "Dear CS,<br/><br/>";
			content += "RFQ Form: " + vo.getFormNumber() + "<br/>";
			content += "<a href=\"" + csLink + "?quoteId=" + vo.getQuoteId() + "\">" + csLink + "?quoteId="
					+ vo.getQuoteId() + "</a><br/><br/>";
		}
		content += "Best Regards," + "<br/>";
		content += vo.getSender() + "<br/>";
		mailInfoBean.setMailContent(content);

		try {
			mailUtilsSB.sendHtmlMail(mailInfoBean);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception for email sent from: " + mailInfoBean.getMailFrom() + ", Subject: "
					+ mailInfoBean.getMailSubject() + ", Email Sending Error : " + e.getMessage(), e);
		}

		LOGGER.info("sendQuotationEmail(QuotationEmailVO vosendQuotationEmail)  end");
	
	}

	/* (non-Javadoc)
	 * @see com.avnet.emasia.webquote.web.quote.strategy.SendQuotationEmailStrategy#getQuoteTemplateBySoldTo(com.avnet.emasia.webquote.entity.Customer, com.avnet.emasia.webquote.entity.Quote)
	 */
	@Override
	public HSSFWorkbook getQuoteTemplateBySoldTo(Customer soldToCustomer, Quote quote,boolean isSubmissionDateFromQuote) {
		// TODO Auto-generated method stub
		return null;
	}
	public String getUrl() {
		String url = sysConfigSB.getProperyValue(QuoteConstant.WEBQUOTE2_DOMAIN);
		return url;
	}

	public QuotationEmailVO genQuotationEmailVO(Quote quote, User user){
		QuotationEmailVO vo = new QuotationEmailVO();
		//String soldToCode = quote.getQuoteItems().get(0).getSoldToCustomer().getCustomerNumber();
		//String formNumbers ="";
		
		vo.setFormNumber(quote.getFormNumber());
		vo.setQuoteId(quote.getId());
		//Customer customer = customerSB.findByPK(soldToCode);
		//vo.setHssfWorkbook(getQuoteTemplateBySoldTo(customer, submitQuote));
		vo.setQuote(quote);
		vo.setSoldToCustomer(quote.getSoldToCustomer());
		//vo.setSoldToCustomer(customer);
		vo.setSubmissionDateFromQuote(true);
		vo.setRecipient(user.getName());
		//vo.setRecipient(rfqHeader.getSalesEmployeeName());
		
		String regionCodeName = quote.getBizUnit().getName();
		vo.setSender(systemCodeMaintenanceSB.getEmailSignName(regionCodeName) + "<br/>"
				+ systemCodeMaintenanceSB.getEmailHotLine(regionCodeName) + "<br/>Email Box: "
				+ systemCodeMaintenanceSB.getEmailSignContent(regionCodeName) + "<br/>");
		vo.setFromEmail(systemCodeMaintenanceSB.getEmailAddress(regionCodeName));

		//String fullCustomerName = getCustomerFullName(quote.getSoldToCustomer());
		String fullCustomerName = quote.getSoldToCustomer().getCustomerFullName();

		vo = ejbCommonSB.commonLogicForDpRFQandRFQ(quote, vo, quote.getSoldToCustomer(), user,
				fullCustomerName);


		return vo;
	}
}