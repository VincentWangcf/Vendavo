package com.avnet.emasia.webquote.dp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;

import com.avnet.emasia.webquote.commodity.helper.ProgRfqSubmitHelper;
import com.avnet.emasia.webquote.constants.ActionEnum;
import com.avnet.emasia.webquote.constants.DpStatusEnum;
import com.avnet.emasia.webquote.constants.QuoteSourceEnum;
import com.avnet.emasia.webquote.ejb.interceptor.EntityManagerInjector;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.Currency;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.DpMessage;
import com.avnet.emasia.webquote.entity.DpRfq;
import com.avnet.emasia.webquote.entity.DpRfqItem;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.ManufacturerMapping;
import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.Money;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.RFQSubmissionTypeEnum;
import com.avnet.emasia.webquote.entity.SapMaterial;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.exception.AppException;
import com.avnet.emasia.webquote.quote.ejb.CustomerSB;
import com.avnet.emasia.webquote.quote.ejb.ManufacturerSB;
import com.avnet.emasia.webquote.quote.ejb.MaterialSB;
import com.avnet.emasia.webquote.quote.ejb.MyQuoteSearchSB;
import com.avnet.emasia.webquote.quote.ejb.QuoteSB;
import com.avnet.emasia.webquote.quote.ejb.SAPWebServiceSB;
import com.avnet.emasia.webquote.quote.ejb.SapMaterialSB;
import com.avnet.emasia.webquote.quote.ejb.SystemCodeMaintenanceSB;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.quote.vo.DpRfqSearchCriteria;
import com.avnet.emasia.webquote.strategy.StrategyFactory;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.common.CacheUtil;
import com.avnet.emasia.webquote.utilites.web.common.Constants;
import com.avnet.emasia.webquote.utilities.bean.MailInfoBean;
import com.avnet.emasia.webquote.utilities.common.OSTimeZone;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;
import com.avnet.emasia.webquote.utilities.ejb.MailUtilsSB;
import com.avnet.emasia.webquote.utilities.schedule.HATimerService;
import com.avnet.emasia.webquote.utilities.util.DateUtil;
import com.avnet.emasia.webquote.utilities.util.QuoteUtil;
import com.avnet.emasia.webquote.vo.RfqHeaderVO;
import com.avnet.emasia.webquote.vo.RfqItemVO;
import com.avnet.emasia.webquote.web.quote.CommonBean;
import com.avnet.emasia.webquote.web.quote.constant.QuoteConstant;
import com.avnet.emasia.webquote.web.quote.strategy.SendQuotationEmailStrategy;
import com.avnet.emasia.webquote.web.quote.vo.QuotationEmailVO;

/**
 * Session Bean implementation class DpRfqSubmissionSB
 */
@Stateless
@LocalBean
@Interceptors(EntityManagerInjector.class)
public class DpRfqSubmissionSB {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger .getLogger(DpRfqSubmissionSB.class.getName());
	
	private static final int INT_ZERO = 0;
	private static final double DOUBLE_ZERO = 0d;
	
	
	@EJB
	private UserSB userSB;

	@EJB
	private QuoteSB quoteSB;
	
	@EJB
	private SAPWebServiceSB sapWebServiceSB;

	@EJB
	private ManufacturerSB manufacturerSB;
	
	@EJB
	private MaterialSB materialSB;
	
	@EJB
	private SapMaterialSB sapMaterialSB;
	
	@EJB
	private CustomerSB customerSB;
	
	
	@EJB
	private SysConfigSB sysConfigSB;
	
	@EJB
	private MailUtilsSB mailUtilsSB;
	
	
	private User user;
	
	@EJB
	private SystemCodeMaintenanceSB sysMaintSB;
	
	@EJB
	private MyQuoteSearchSB myQuoteSearchSB;
	
	@EJB
	protected EJBCommonSB ejbCommonSB;
	
	private Quote quote = null;
	
	private QuoteItem quoteItem = null;
	
	@EJB
	private CacheUtil cacheUtil;
    
    public DpRfqSubmissionSB() {
        
    }
    
    public DpRfq getDpRfqObject()  {
    	Date date = new Date();
    	DpMessage dpMess = new DpMessage();
    	dpMess.setId(0L);
    	dpMess.setCreateRfqMessage("test message");
    	dpMess.setCreateRfqTime(new Timestamp(date.getTime()));
    	DpMessage tempMess = this.saveDpMessage(dpMess);
    	
    	DpRfq dpRfq = new DpRfq();
    	dpRfq.setDpMessage(tempMess);
    	
    	
 		
    	dpRfq.setId(0L);
    	dpRfq.setSalesPersonId("901518");
    	dpRfq.setContactEmailAddress("Lenon.Yang@avnet.com");
    	dpRfq.setContactName("Lenon");
    	dpRfq.setContactPhoneNo("0755-83782861");
    	dpRfq.setCreationTime(date);
    	dpRfq.setSoldToCustomerAccountNo("969");
    	dpRfq.setSoldToCustomername("KEI KONG ELECTRONICS LTD.");
    	dpRfq.setCustomerType("OTHER");
    	dpRfq.setLastUpdateTime(date);
    	dpRfq.setSource("Web");
    	dpRfq.setReferenceId("6000010001137");
    	dpRfq.setSalesOffice("10");
    	dpRfq.setSegment("INDUSTRIAL");
    	dpRfq.setQuoteType("Order On Hand");
    	dpRfq.setOperation("Create Quote");
    	dpRfq.setSalesOffice("10");
    	dpRfq.setSalesOrganization("HK32");
    	dpRfq.setRfqCreationTimestamp(new Timestamp(date.getTime()));
    	
    	List<DpRfqItem> dpRfqItems = new ArrayList<DpRfqItem>();
    	DpRfqItem dpRfqItem = new DpRfqItem();
    	dpRfqItem.setId(0L);
    	dpRfqItem.setLineItemNumber(123);
    	dpRfqItem.setReferenceLineId("5130137");
    	dpRfqItem.setCurrency("USD");
    	dpRfqItem.setRequestedPart("ONSMBR230LSFT1G");
    	dpRfqItem.setRequestedManufacturer("ONS");
    	//dpRfqItem.setRequestedPart("MCCLAN8710A-EZC-TR");
    	//dpRfqItem.setRequestedManufacturer("MCC");
    	dpRfqItem.setTargetResale(new BigDecimal(0.0D));
    	//dpRfqItem.setQuotedResale(new BigDecimal(0.6D));
    	dpRfqItem.setQuotedQty(60L);
    	dpRfqItem.setQuantity(60L);
    	dpRfqItem.setExpiryDate(new Timestamp(DateUtil.string2Date("2017/11/14", "yyyy/MM/dd").getTime()));
    	
    
    	
    	dpRfqItems.add(dpRfqItem);
    	//dpRfqItems.add(dpRfqItem2);
    	dpRfq.setDpRfqItems(dpRfqItems);
    	return dpRfq;
    }
    
    
    public DpRfq validateDpRfqData(DpRfq dpRfq) {
    	boolean canValidate = true;
    	// validate sales User
    	if(!validateSalesUser(dpRfq)) {
    		LOGGER.info("<<<<< Sales User Id Not Exist,DP Reference Id>>>>>= " + dpRfq.getReferenceId());
    		canValidate = false;
    		for(DpRfqItem item : dpRfq.getDpRfqItems()) {
    			item.setQuoteLineStatus(DpStatusEnum.OFFLINE.code());
        		item.setRejectionReason("sales user id " + dpRfq.getSalesPersonId() + " not found");
        		
        	}
    		/*String emailSubject = "Digital Portal Quote Request Error";
    		String fromEmailAddr = "Asia-AEMC.QuoteCentre@Avnet.com";
    		String emailFromInName = "Asia-AEMC.QuoteCentre";
    		String content = "Dear " + dpRfq.getContactName() + ",<br/><br/>";
    		StringBuffer emailContent = new StringBuffer(content);
    		emailContent.append("The error reason for your quote  request:<br/><br/>");
    		emailContent.append("the sales user id is not exists!please create account in WQ ").append("<br/><br/>");
    		emailContent.append("Best Regards,<br/>");
    		emailContent.append(emailFromInName).append("<br/>");
    		List<String> toEmails = new ArrayList<String>();
    		toEmails.add(dpRfq.getContactEmailAddress());
    		sendEmail(emailSubject, emailContent.toString(), fromEmailAddr, toEmails);*/
    	} else {
    		User user = getSales(dpRfq.getSalesPersonId());
    		String roleString = user.getRoleString();
    		boolean isSaleRole = false;
    		if(StringUtils.isNotBlank(roleString)) {
    			String submitRole = QuoteSBConstant.ROLE_SALES + ","
    					+ QuoteSBConstant.ROLE_INSIDE_SALES + ","
    					+ QuoteSBConstant.ROLE_SALES_MANAGER + ","
    					+ QuoteSBConstant.ROLE_SALES_DIRECTOR + ","
    					+ QuoteSBConstant.ROLE_SALES_GM;
    			
    			for(String roleName : roleString.split(",")) {
    				if(submitRole.contains(roleName)) {
    					isSaleRole = true;
    					break;
    				}
    			}
    		}
			
			if(!isSaleRole) {
				LOGGER.info("<<<<<  the User is Not Sales Role,User id >>>=" + dpRfq.getSalesPersonId() + "DP Reference Id>>>>>= " + dpRfq.getReferenceId());
	    		canValidate = false;
	    		for(DpRfqItem item : dpRfq.getDpRfqItems()) {
	    			item.setQuoteLineStatus(DpStatusEnum.OFFLINE.code());
	        		item.setRejectionReason(ResourceMB.getText("wq.message.userNoSales"));
	        		
	        	}
	    		/*String emailSubject = "Digital Portal Quote Request Error";
	    		String fromEmailAddr = "Asia-AEMC.QuoteCentre@Avnet.com";
	    		String emailFromInName = "Asia-AEMC.QuoteCentre";
	    		String content = "Dear " + dpRfq.getContactName() + ",<br/><br/>";
	    		StringBuffer emailContent = new StringBuffer(content);
	    		emailContent.append("The error reason for your quote  request:<br/><br/>");
	    		emailContent.append("the sales user id is not exists!please create account in WQ ").append("<br/><br/>");
	    		emailContent.append("Best Regards,<br/>");
	    		emailContent.append(emailFromInName).append("<br/>");
	    		List<String> toEmails = new ArrayList<String>();
	    		toEmails.add(dpRfq.getContactEmailAddress());
	    		sendEmail(emailSubject, emailContent.toString(), fromEmailAddr, toEmails);*/
			}
    	}
    	
    	// validate MFR + sales in region
    	if(canValidate) {
        	for(DpRfqItem item : dpRfq.getDpRfqItems()) {
        		if(!validateMfrSalesRegion(dpRfq, item)) {
        			LOGGER.info("<<<<< MRF Not Exist,DP Reference Line Id>>>>>= " + item.getReferenceLineId());
        			canValidate = false;
        			//item.setQuoteLineStatus(DpStatusEnum.MFR_NOT_EXSIT.code());
        			item.setQuoteLineStatus(DpStatusEnum.OFFLINE.code());
        			item.setRejectionReason(ResourceMB.getText("wq.message.toBeHandledOffline")+".");
        		}
        	}
    	}
    	
    	// validate Customer
    	if(canValidate) {
    		for(DpRfqItem item : dpRfq.getDpRfqItems()) {
        		if(!validateCustomer(dpRfq)) {
        			LOGGER.info("<<<<< Customer Number Not Exist,Customer Number>>>>>= " + dpRfq.getSoldToCustomerAccountNo());
        			canValidate = false;
        			item.setQuoteLineStatus(DpStatusEnum.OFFLINE.code());
            		item.setRejectionReason("customer number " + dpRfq.getSoldToCustomerAccountNo() + " not found");
        		}
        	}
    	}
    	
    	//validate Sap request part number
    	if(canValidate) {
    		for(DpRfqItem item : dpRfq.getDpRfqItems()) {
        		if(!validateSapMaterial(item)) {
        			LOGGER.info("<<<<< SAP Part Number Not Exist,SAP Part Number>>>>>= " + item.getRequestedPart());
        			canValidate = false;
        			item.setQuoteLineStatus(DpStatusEnum.OFFLINE.code());
            		item.setRejectionReason("sap part number " + item.getRequestedPart() + " not found");
        		}
        	}
    	}
    	
    	return dpRfq;
    }
    
    public boolean validateSalesUser(DpRfq dpRfq) {
    	LOGGER.info(" >>>>>validate Sales User>>>>> User Id=== " + dpRfq.getSalesPersonId());
    	String userEmpId = dpRfq.getSalesPersonId();
    	if(StringUtils.isNotBlank(userEmpId)) {
    		User user = getSales(userEmpId);
    		return user != null;
    	}
    	
    	return false;
    }
    
    public boolean validateMfrSalesRegion(DpRfq dpRfq,DpRfqItem dpRfqItem) {
    	LOGGER.info(" >>>>>validate Mfr Sales Region>>>>>> MFR==== " + dpRfqItem.getRequestedManufacturer());
    	User user = getSales(dpRfq.getSalesPersonId());
    	if(StringUtils.isNotBlank(dpRfqItem.getRequestedManufacturer())) {
    		ManufacturerMapping mfrMapping = manufacturerSB
    				.findManufacturerMappingByBizUnit(user.getDefaultBizUnit(), dpRfqItem.getRequestedManufacturer());
    		return mfrMapping != null;
    	}
    	return false;
    }
    
    public boolean validateSapMaterial(DpRfqItem dpRfqItem) {
    	LOGGER.info(">>>>>validate Sap Number >>>>>SAP Numnber=== " + dpRfqItem.getRequestedPart());
    	if(StringUtils.isNotBlank(dpRfqItem.getRequestedPart())) {
    		SapMaterial sapMaterial = sapMaterialSB.findSapMaterialBySapPartNumber(dpRfqItem.getRequestedPart());
    		return  sapMaterial != null;
    	}
    	return false;
    }
    
    public boolean validateCustomer(DpRfq dpRfq) {
    	LOGGER.info(" ******validate Customer ******* Customer Number >>>>== " + dpRfq.getSoldToCustomerAccountNo());
    	if(StringUtils.isNotBlank(dpRfq.getSoldToCustomerAccountNo())) {
    		User user = getSales(dpRfq.getSalesPersonId());
    		List<String> accountGroups = new ArrayList<String>();
			accountGroups.add(QuoteSBConstant.ACCOUNT_GROUP_SOLDTO);
			accountGroups.add(QuoteSBConstant.ACCOUNT_GROUP_PROSPECTIVE_CUSTOMER);
			accountGroups.add(QuoteSBConstant.ACCOUNT_GROUP_CPD);
			List<Customer> customers = customerSB.findCustomerByCustomerNumber(dpRfq.getSoldToCustomerAccountNo(),null, accountGroups,user.getDefaultBizUnit(), 0, 100);
			Customer customer = null; 
			if (customers != null && customers.size() > 0) {
				customer = customers.get(0);
				if (!(customer.getDeleteFlag() != null && customer.getDeleteFlag().booleanValue())) {
					boolean isInvalidCustomer = customerSB.isInvalidCustomer(dpRfq.getSoldToCustomerAccountNo());
					return !isInvalidCustomer;
				}
				
			} 
    	}
    	return false;
    }
    
    
    public void sendEmail(String emailSubject,String content,String fromEmailAddr,List<String> emails) {
    	LOGGER.info(" ****** Send Email  ******* ");
    	MailInfoBean mailInfoBean = new MailInfoBean();
		mailInfoBean.setMailSubject(emailSubject);
		mailInfoBean.setMailFrom(fromEmailAddr);
		mailInfoBean.setMailTo(emails);
		mailInfoBean.setMailContent(content);
		try {
			mailUtilsSB.sendHtmlMail(mailInfoBean);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Email Sending failed from : "+fromEmailAddr+" , Exception message" + e.getMessage());
		}
    }
    
    public DpRfq saveDpRfq(DpRfq dpRfq) {
    	List<DpRfqItem> dpRfqItems = dpRfq.getDpRfqItems();
    	for(DpRfqItem item: dpRfqItems) {
    		item.setDpRfq(dpRfq);
    	}
    	return this.saveDpRfqObject(dpRfq);
    }
    
    public User getSales(String userEmpId) {
    	if(user != null && StringUtils.equals(userEmpId, user.getEmployeeId())) {
    		return user;
    	}
    	user = userSB.findByEmployeeIdLazily(userEmpId);
    	return user;
    }
    
    
 
    
    public void sendEmailToWebTeam(DpRfq dpRfq) {
    	LOGGER.info("*********Send Email to Web Team **********");
		String jbossNodeName = System.getProperty(HATimerService.JBOSS_NODE_NAME);
		String sender = sysConfigSB.getProperyValue(QuoteSBConstant.ERROR_EMAIL_FROM_ADDRESS);
		String recipients = sysConfigSB.getProperyValue(QuoteSBConstant.ERROR_EMAIL_TO_ADDRESS);
		List<String> to = new ArrayList<String>();
		if (null != recipients && !recipients.isEmpty()) {
			String[] tos = recipients.split(";");
			for (String recipient : tos) {
				if (null != recipient && !recipient.trim().isEmpty())
					to.add(recipient);
			}
		}

		String emailSubject = "the error data for Dp Rfq";
		if(org.apache.commons.lang.StringUtils.isNotBlank(jbossNodeName)) {
			emailSubject += "(Jboss Node:" + jbossNodeName + ")";
		}
		StringBuffer emailContent = new StringBuffer("");
		emailContent.append("The error detail for Dp Rfq:<br/><br/>");
		emailContent.append("Run at jboss node: ").append(jbossNodeName).append("<br/><br/>");
		emailContent.append("reference line id: ").append(dpRfq.getReferenceId()).append("<br/><br/>");
		List<DpRfqItem> errorItem = new ArrayList<DpRfqItem>();
		for (DpRfqItem item : dpRfq.getDpRfqItems()) {
			if (StringUtils.isNotBlank(item.getRejectionReason())) {
				errorItem.add(item);
			}
		}
		if (errorItem.size() > 0) {
			emailContent.append("Below are the Dp Rfq error detail:<br/><br/>");
			String tableString = getTableString(errorItem);
			emailContent.append(tableString);
		}
		emailContent.append("<br/><br/>");

		sendEmail(emailSubject, emailContent.toString(), sender, to);
    	
    }
    
    private String getTableString(List<DpRfqItem> dpRfqItems) {
		String talbe = 
				"<table style=\"border-top:1px black solid;border-right:0 ;border-bottom:0 ;border-left:1px black solid;\"  "
				+ "width=\"600\" cellpadding=\"0\" cellspacing=\"0\">"
				+ "<tr>"
				+ " <td style=\"border-top:0;border-right: 1px black solid ;border-bottom:1px black solid ;border-left:0;font-weight:bold;\">No.</td>"
				+ " <td style=\"border-top:0;border-right: 1px black solid ;border-bottom:1px black solid ;border-left:0;font-weight:bold;\">reference line id</td>"
				+ "<td style=\"border-top:0;border-right: 1px black solid ;border-bottom:1px black solid ;border-left:0;font-weight:bold;\">Err_desc</td>"
				+ "</tr>";
		StringBuffer buffer = new StringBuffer(talbe);
		int no = 1;
		for(DpRfqItem item : dpRfqItems) {
			buffer.append("<tr>");
			buffer.append("<td style=\"border-top:0;border-right: 1px black solid ;border-bottom:1px black solid ;border-left:0;\">");
			buffer.append(no);
			buffer.append("</td>");
			buffer.append("<td style=\"border-top:0;border-right: 1px black solid ;border-bottom:1px black solid ;border-left:0;\">");
			buffer.append(item.getReferenceLineId());
			buffer.append("</td>");
			buffer.append("<td style=\"border-top:0;border-right: 1px black solid ;border-bottom:1px black solid ;border-left:0;\">");
			buffer.append(item.getRejectionReason());
			buffer.append("</td>");
			buffer.append("</tr>");
			no++;
		}
		buffer.append("</table>");
		return buffer.toString();
	}
    
    
    public QuoteItem getBasicQuoteItem(DpRfqItem dpRfqItem,DpRfq dpRfq) {
    	quoteItem = new QuoteItem();
		quoteItem = copyDataToQuoteItem(quoteItem, dpRfqItem, dpRfq);
		quoteItem.setQuoteType(dpRfq.getQuoteType());

		// set mfr
		Manufacturer mfr = manufacturerSB.findManufacturerByName(dpRfqItem.getRequestedManufacturer(),user.getDefaultBizUnit());
		quoteItem.setRequestedMfr(mfr);
		quoteItem.setQuotedMfr(mfr);

		// set Customer
		Customer soldTo = customerSB.findByPK(dpRfq.getSoldToCustomerAccountNo());
		quoteItem.setSoldToCustomer(soldTo);
		quoteItem.setCustomerType(dpRfq.getCustomerType());
		quoteItem.setDpReferenceLineId(dpRfqItem.getReferenceLineId());
		//set Action
		quoteItem.setAction(ActionEnum.DP_QUOTE_CREATE.toString());
    	return quoteItem;
    }
    
    /**
     * if AQLogic create Quote
     * @param dpRfq
     * @throws AppException 
     */
    public void submitRfq(DpRfq dpRfq) throws AppException{
    	LOGGER.info("<<<<<<<< Start to Submit DP RFQ >>>>>>>>>>>");
    	if(dpRfq != null && dpRfq.getDpRfqItems() != null && dpRfq.getDpRfqItems().size() > 0) {
    		//clear RejectionReason 
    		for(DpRfqItem item : dpRfq.getDpRfqItems()) {
    			item.setRejectionReason("");
    		}
    		//validate data
    		dpRfq = validateDpRfqData(dpRfq);
    		LOGGER.info("<<<<<<<< validate data finished >>>>>>>>>>>");
    		// save dpRfq
    		dpRfq = saveDpRfq(dpRfq);
    		LOGGER.info("<<<<<<<< save DP RFQ finished >>>>>>>>>>>");
    		//boolean sendWebTeam = false;
    		String userEmpId = dpRfq.getSalesPersonId();
			String soldToCode = dpRfq.getSoldToCustomerAccountNo();
			user = getSales(userEmpId);
    		if(user != null) {
				//update Dp Region
				DpMessage mess = dpRfq.getDpMessage();
				mess.setBizUnit(user.getDefaultBizUnit());
				this.saveDpMessage(mess);
				
				List<Customer> allowCustomers = null;
				if(user != null) {
					allowCustomers = userSB.findAllCustomers(user);
				}
				
				List<QuoteItem> sapQuoteItems = new ArrayList<QuoteItem>();
				Quote savedQuote = null;
				DpRfq tempDpRfq = null;
				List<DpRfqItem> dpRfqItems = dpRfq.getDpRfqItems();
				List<QuoteItem> quoteItems = null;
				List<QuoteItem> savedQuoteItems = new ArrayList<QuoteItem>();
				
				for (DpRfqItem dpRfqItem : dpRfqItems) {
					if(StringUtils.isBlank(dpRfqItem.getRejectionReason()) && StringUtils.isBlank(dpRfqItem.getQuoteLineStatus())||true) {
						quoteItems = new ArrayList<QuoteItem>();
						quoteItem = this.getBasicQuoteItem(dpRfqItem, dpRfq);
						Material tempMaterial = null;
						LOGGER.info("<<<<<<<< find SAP Material >>>>>>>>>>> SAP Material Number ===>> " + dpRfqItem.getRequestedPart());
						SapMaterial sapMaterial = sapMaterialSB.findSapMaterialBySapPartNumber(dpRfqItem.getRequestedPart());
						if (sapMaterial != null) {
							tempMaterial = sapMaterial.getMaterial();
							quoteItem.setRequestedPartNumber(tempMaterial.getFullMfrPartNumber());
							quoteItem.setQuotedPartNumber(tempMaterial.getFullMfrPartNumber());
						}
						Material lowestCostMaterial = null;
						List<RfqItemVO> rfqItemVOS = new ArrayList<RfqItemVO>();
						if (tempMaterial != null) {
							//new RfqHeaderVO
							RfqHeaderVO headerVo = new RfqHeaderVO();
							headerVo.setBizUnit(user.getDefaultBizUnit().getName());
							headerVo.setAllowCustomers(allowCustomers);
							headerVo.setSalesEmployeeNumber(dpRfq.getSalesPersonId());
							
							//new RfqItemVO
							RfqItemVO itemVo = new RfqItemVO();
							itemVo.setRfqHeaderVO(headerVo);
							itemVo.setMfr(dpRfqItem.getRequestedManufacturer());
							itemVo.setQuotedPartNumber(tempMaterial.getFullMfrPartNumber());
							itemVo.setRequiredPartNumber(tempMaterial.getFullMfrPartNumber());
							itemVo.setSoldToCustomerNumber(soldToCode);
							itemVo.setSoldToCustomerName(dpRfq.getSoldToCustomername());
							itemVo.setTargetResale(String.valueOf(dpRfqItem.getTargetResale()));
							itemVo.setSoldToCustomer(quoteItem.getSoldToCustomer());
							itemVo.setRequiredQty(String.valueOf(dpRfqItem.getQuantity()));
							itemVo.setSapPartNumber(dpRfqItem.getRequestedPart());
							itemVo.setEnquirySegment(dpRfq.getSegment());
							itemVo.setCustomerType(dpRfq.getCustomerType());
							itemVo.setRfqCurr("USD");
							rfqItemVOS.add(itemVo);
						}
						//search pricer and fill pricer into VO
						materialSB.applyAQLogic(rfqItemVOS, RFQSubmissionTypeEnum.DPRFQSubmission);
						RfqItemVO aqItemVo = null;
						if(rfqItemVOS.size() > 0) {
							aqItemVo = rfqItemVOS.get(0);
							lowestCostMaterial = aqItemVo.getMaterial();
						}

						if (lowestCostMaterial != null) {
							if (aqItemVo.matchAQConditions()) {
								LOGGER.info("=========== Run AQ Logic============== ");
								//aqItemVo.fillPriceInfoToQuoteItem(quoteItem);
								quoteSB.fillPriceInfoToQuoteItem(aqItemVo, quoteItem);
								quote = copyDataToQuote(dpRfq);
								quoteItem.setQuote(quote);
								//quoteSB.updateRateForQuoteItems(quoteItems);
								
								
								quoteItem.setSubmissionDate(quote.getSubmissionDate());
								// PROGRM PRICER ENHANCEMENT
								quoteItem.setStatus(QuoteSBConstant.RFQ_STATUS_AQ);
								quoteItem.setStage(QuoteSBConstant.QUOTE_STAGE_FINISH);
								quoteSB.postProcessAfterRfqSubmitFinish(quoteItem);
								// set Quote Number
								quoteSB.calculateDPQuoteNumber(quoteItem);
								LOGGER.info("<<< New DP Quote Number====>>>>>>" + quoteItem.getQuoteNumber() ); 
								sapQuoteItems.add(quoteItem);
								quoteItems.add(quoteItem);
								quoteItem = quoteItems.get(0);
								//
								if (savedQuote == null) {
									savedQuote = quoteSB.saveDPQuote(quote);
									LOGGER.info("=========== Save Quote finished ============== ");
								}

								quoteItem.setQuote(savedQuote);
								//add By Vincent  20181031
								
								if(!quoteItem.getBuyCurr().equals(Currency.USD)){
									quoteItem.setCost(quoteItem.convertToRfqValueWithDouble(quoteItem.getCost()).doubleValue());
									quoteItem.setBottomPrice(quoteItem.convertToRfqValueWithDouble(quoteItem.getBottomPrice()).doubleValue());
									quoteItem.setSalesCost(quoteItem.convertToRfqValue(quoteItem.getSalesCost()));
									quoteItem.setExRateBuy(new BigDecimal("1"));
									if(quoteItem.getQuotedPrice()==null){
										quoteItem.setQuotedPrice(null);
									}else{
										quoteItem.setQuotedPrice(quoteItem.convertToRfqValueWithDouble(quoteItem.getQuotedPrice()).doubleValue());
									}
								
								}
								
								
								//quoteItem.postProcessAfterFinish(); 
								QuoteItem tempQuoteItem = quoteSB.saveQuoteItem(quoteItem);
								LOGGER.info("=========== Save QuoteItem finished ============== ");
								savedQuoteItems.add(tempQuoteItem);
								if (tempDpRfq == null) {
									dpRfq.setQuote(savedQuote);
									tempDpRfq = this.saveDpRfq(dpRfq);
								}
								dpRfqItem.setQuoteItem(tempQuoteItem);
								dpRfqItem.setDpRfq(tempDpRfq);
								dpRfqItem.setQuoteLineStatus(DpStatusEnum.QUOTED.code());
								dpRfqItem.setQuotedQty(tempQuoteItem.getQuotedQty() != null ? tempQuoteItem.getQuotedQty().longValue() : 0L);
								dpRfqItem.setExpiryDate(new Timestamp(tempQuoteItem.getQuoteExpiryDate() != null ? tempQuoteItem.getQuoteExpiryDate().getTime() : null));	
								dpRfqItem.setQuotedResale(tempQuoteItem.getQuotedPrice() != null ? new BigDecimal(String.valueOf(tempQuoteItem.getQuotedPrice())) : null);
								
							} else {
								LOGGER.info("<<<<<<<<Has No Bid DR RFQ,Reference Line Id >>>>==" + dpRfqItem.getReferenceLineId());
								dpRfqItem.setDpRfq(dpRfq);
								dpRfqItem.setQuoteLineStatus(DpStatusEnum.OFFLINE.code());
								dpRfqItem.setRejectionReason(ResourceMB.getText("wq.message.noBid")+ ".");
							}
							this.saveDpRfqItem(dpRfqItem);
							
						}
						
					
					} else {  // validate not through
						// mfr + sales region not exists
						if(StringUtils.isNotBlank(dpRfqItem.getQuoteLineStatus()) 
								&& StringUtils.equals(DpStatusEnum.OFFLINE.code(),
										dpRfqItem.getQuoteLineStatus())) { 
							LOGGER.info("Has MRF Not Exist DP RFQ,the Reference Line Id >>>>==" + dpRfqItem.getReferenceLineId());
						}
						// sales user id or customer  number or request sap no not exists
						/*if(StringUtils.isNotBlank(dpRfqItem.getRejectionReason()) && !(dpRfqItem.getReprocessCount() > 0)) {
							sendWebTeam = true;
						}*/
						
					}
					
				}

				if (sapQuoteItems != null && sapQuoteItems.size() > 0) {
					LOGGER.info("<<<<<<<<<<< Call SAP WebService  >>>>> ");
					sapWebServiceSB.createSAPQuote(sapQuoteItems);
				}
				if (savedQuote != null) {
					LOGGER.info("=========== Send Quote Success Email ============== ");
					savedQuote.setQuoteItems(savedQuoteItems);
					sendEmail(savedQuote, soldToCode, user);
				}
				// send email to web team
				/*if(sendWebTeam) {
					sendEmailToWebTeam(dpRfq);
				}*/
    		}
    		
    		
    		
    		
    	}
    	
    }
    
   public void sendEmail(Quote submitQuote,String soldToCode,User saleUser ) {
		QuotationEmailVO vo = new QuotationEmailVO();
		vo.setFormNumber(submitQuote.getFormNumber());
		vo.setQuoteId(submitQuote.getId());
		Customer customer = customerSB.findByPK(soldToCode);
/*		vo.setHssfWorkbook(ejbCommonSB.getQuoteTemplateBySoldTo(new CommonBean(),
				customer, submitQuote));*/
		vo.setQuote(submitQuote);
		vo.setSoldToCustomer(customer);
		vo.setSubmissionDateFromQuote(true);
		
		vo.setRecipient(saleUser.getName());
		String regionCode = saleUser.getDefaultBizUnit().getName();
		vo.setSender(sysMaintSB.getEmailSignName(regionCode)
				+ "<br/>"
				+ sysMaintSB
						.getEmailHotLine(regionCode)
				+ "<br/>Email Box: "
				+ sysMaintSB.getEmailSignContent(regionCode)
				+ "<br/>");
		vo.setFromEmail(sysMaintSB.getEmailAddress(regionCode));

		String fullCustomerName = getCustomerFullName(customer);

		vo = ejbCommonSB.commonLogicForDpRFQandRFQ(submitQuote, vo, customer, user, fullCustomerName);
		
		//ejbCommonSB.sendQuotationEmail(vo);
		
	/*	SendQuotationEmailStrategy strategy = (SendQuotationEmailStrategy) cacheUtil.getStrategy(SendQuotationEmailStrategy.class,
				regionCode);*/
		
		SendQuotationEmailStrategy strategy =(SendQuotationEmailStrategy) StrategyFactory.getSingletonFactory()
				.getStrategy(SendQuotationEmailStrategy.class,regionCode,
						this.getClass().getClassLoader());
		
		strategy.sendQuotationEmail(vo);
	
    }
    
    private QuoteItem copyDataToQuoteItem(QuoteItem quoteItem,DpRfqItem dpRfqItem,DpRfq dpRfq) {
    	Date currentTime = com.avnet.emasia.webquote.utilites.web.util.QuoteUtil.getCurrentTime();
    	//add on 20181029 By Vincent
    	quoteItem.setRfqCurr(Currency.USD);
		quoteItem.setBuyCurr(Currency.getByCurrencyName(dpRfqItem.getCurrency()));
		quoteItem.setRequestedQty(dpRfqItem.getQuantity().intValue());
    	quoteItem.setTargetResale(dpRfqItem.getTargetResale().doubleValue());
		quoteItem.setRevertVersion(QuoteSBConstant.DEFAULT_REVERT_VERISON);
		quoteItem.setSentOutTime(currentTime);
		quoteItem.setLastUpdatedBy(user.getEmployeeId());
		quoteItem.setLastUpdatedName(user.getName());
		quoteItem.setLastUpdatedOn(currentTime);
		quoteItem.setSapPartNumber(dpRfqItem.getRequestedPart());
		quoteItem.setEnquirySegment(dpRfq.getSegment());
		quoteItem.setDesignInCat("Local Fulfillment");
    	quoteItem.setTargetResale(dpRfqItem.getTargetResale().doubleValue());
		return quoteItem;
	}

    
    private Quote copyDataToQuote(DpRfq dpRfq) {
    	quote = new Quote();
    	Date currentTime = com.avnet.emasia.webquote.utilites.web.util.QuoteUtil.getCurrentTime();
    	quote.setId(0L);
    	quote.setBizUnit(user.getDefaultBizUnit());
		quote.setCreatedBy(user.getEmployeeId());
		quote.setCreatedName(user.getName());
		quote.setCreatedOn(currentTime);
		quote.setLastUpdatedBy(user.getEmployeeId());
		quote.setLastUpdatedName(user.getName());
		quote.setLastUpdatedOn(currentTime);
		quote.setSubmissionDate(currentTime);
		quote.setSales(user);
		quote.setTeam(user.getTeam());
		quote.setEnquirySegment(dpRfq.getSegment());
		
		// set Customer
		Customer soldTo = customerSB.findByPK(dpRfq.getSoldToCustomerAccountNo());
		quote.setSoldToCustomer(soldTo);
		quote.setSoldToCustomerName(dpRfq.getSoldToCustomername());
		//quote.setSoldToCustomerNameChinese(getCustomerFullName(soldTo)); 
		quote.setCustomerType(dpRfq.getCustomerType());
		quote.setQuoteType(dpRfq.getQuoteType());
		//set form_number prefix DF
		quote.setFormNumber(quoteSB.populateDPFormNumber(quote));
		LOGGER.info("<<<<<<< New Quote Form Number >>>>====" + quote.getFormNumber());
		quote.setQuoteSource(QuoteSourceEnum.DP.toString());
		quote.setDpReferenceId(dpRfq.getReferenceId());
    	return quote;
    }
    
	

	public  String getCustomerFullName(Customer customer){
		if(customer != null){
			/*String customerName = customer.getCustomerName1();
			if(customerName != null && customer.getCustomerName2() != null){
				customerName += customer.getCustomerName2();
			}*/
			return customer.getCustomerFullName();
		}
		return null;
	}
	
	protected QuoteItem copyCostIndicatorRelatedData1ToQuoteItem(QuoteItem quoteItem, RfqItemVO rfqItem) {
		if (quoteItem.getResaleIndicator() == null) {
			quoteItem.setResaleIndicator(QuoteSBConstant.DEFAULT_RESALE_INDICATOR);
			quoteItem.setResaleMin(QuoteSBConstant.DEFAULT_RESALE_INDICATOR_MIN);
			quoteItem.setResaleMax(QuoteSBConstant.DEFAULT_RESALE_INDICATOR_MAX);
		} else {
			quoteItem.setResaleIndicator(quoteItem.getResaleIndicator());
			String minResale = quoteItem.getResaleIndicator().substring(0, 2);
			String maxResale = quoteItem.getResaleIndicator().substring(2, 4);
			quoteItem.setResaleMin(Double.parseDouble(minResale));
			if (maxResale.matches("[0-9]{2}")) {
				quoteItem.setResaleMax(Double.parseDouble(maxResale));
			} else {
				quoteItem.setResaleMax(QuoteSBConstant.DEFAULT_RESALE_INDICATOR_MAX);
			}
		}
		if (quoteItem.getQuotedMaterial() != null && quoteItem.getQuotedMaterial().isProgramPart()) {
			quoteItem.setMaterialTypeId(QuoteSBConstant.MATERIAL_TYPE_PROGRAM);
		} else {
			quoteItem.setMaterialTypeId(QuoteSBConstant.MATERIAL_TYPE_NORMAL);
		}

		quoteItem.setCostIndicator(rfqItem.getCostIndicator());
		quoteItem.setPriceListRemarks1(rfqItem.getPriceListRemarks1());
		quoteItem.setPriceListRemarks2(rfqItem.getPriceListRemarks2());
		quoteItem.setPriceListRemarks3(rfqItem.getPriceListRemarks3());
		quoteItem.setPriceListRemarks4(rfqItem.getPriceListRemarks4());
		quoteItem.setCancellationWindow(rfqItem.getCancellationWindow());
		quoteItem.setRescheduleWindow(rfqItem.getReschedulingWindow());
		quoteItem.setMultiUsageFlag(rfqItem.getMultiUsage());
		quoteItem.setLeadTime(rfqItem.getLeadTime());
		quoteItem.setTermsAndConditions(rfqItem.getTermsAndCondition());
		quoteItem.setAqcc(rfqItem.getQcComment());
		quoteItem.setDescription(rfqItem.getDescription());
		quoteItem.setMoq(rfqItem.getMoq());
		quoteItem.setMov(rfqItem.getMov());
		quoteItem.setMpq(rfqItem.getMpq());
		if (rfqItem.getCost() != null) {
			quoteItem.setCost(rfqItem.getCost());
		}
		if (rfqItem.getMinSellPrice() != null) {
			quoteItem.setMinSellPrice(rfqItem.getMinSellPrice());
		}
		quoteItem.setBottomPrice(rfqItem.getBottomPrice());
		if (rfqItem.getQtyIndicator() != null) {
			quoteItem.setQtyIndicator(rfqItem.getQtyIndicator());
		} else {
			quoteItem.setQtyIndicator(QuoteConstant.QTY_INDICATOR_MOQ);
		}
		quoteItem.setQuotationEffectiveDate(rfqItem.getQuotaionEffectiveDate());
		if (rfqItem.getResaleIndicator() == null) {
			quoteItem.setResaleIndicator(QuoteSBConstant.DEFAULT_RESALE_INDICATOR);
			quoteItem.setResaleMin(QuoteSBConstant.DEFAULT_RESALE_INDICATOR_MIN);
			quoteItem.setResaleMax(QuoteSBConstant.DEFAULT_RESALE_INDICATOR_MAX);
		} else {
			quoteItem.setResaleIndicator(rfqItem.getResaleIndicator());
			String minResale = rfqItem.getResaleIndicator().substring(0, 2);
			String maxResale = rfqItem.getResaleIndicator().substring(2, 4);
			quoteItem.setResaleMin(Double.parseDouble(minResale));
			if (maxResale.matches("[0-9]{2}"))
				quoteItem.setResaleMax(Double.parseDouble(maxResale));
			else
				quoteItem.setResaleMax(QuoteSBConstant.DEFAULT_RESALE_INDICATOR_MAX);
		}
		if (rfqItem.getCostIndicatorType() != null
				&& rfqItem.getCostIndicatorType().equals(QuoteSBConstant.MATERIAL_TYPE_PROGRAM)) {
			quoteItem.setMaterialTypeId(QuoteSBConstant.MATERIAL_TYPE_PROGRAM);
		} else if (rfqItem.getCostIndicatorType() != null
				&& rfqItem.getCostIndicatorType().equals(QuoteSBConstant.MATERIAL_TYPE_NORMAL)) {
			quoteItem.setMaterialTypeId(QuoteSBConstant.MATERIAL_TYPE_NORMAL);
		}
		return quoteItem;
	}

	protected QuoteItem copyCostIndicatorRelatedData2ToQuoteItem(QuoteItem quoteItem, RfqItemVO rfqItem) {
		quoteItem.setShipmentValidity(rfqItem.getShipmentValidity());
		quoteItem.setPoExpiryDate(rfqItem.getShipmentValidity());
		quoteItem.setPriceValidity(rfqItem.getPriceValidity());
		return quoteItem;
	}

	protected QuoteItem copyCostIndicatorRelatedData3ToQuoteItem(QuoteItem quoteItem, RfqItemVO rfqItem) {
		if (quoteItem.getPriceValidity() != null) {
			Date validity = null;
			if (quoteItem.getPriceValidity().matches("[0-9]{1,}")) {
				int shift = Integer.parseInt(quoteItem.getPriceValidity());
				validity = com.avnet.emasia.webquote.utilites.web.util.QuoteUtil.shiftDate(quoteItem.getSentOutTime(),
						shift);
			} else {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				sdf.setTimeZone(OSTimeZone.getOsTimeZone());
				try {
					validity = sdf.parse(quoteItem.getPriceValidity());
				} catch (ParseException e) {
					LOGGER.log(Level.SEVERE, "Exception in parsing date : " + quoteItem.getPriceValidity()
							+ " , Exception message : " + e.getMessage(), e);
				}
			}
			if (validity != null) {
				quoteItem.setPoExpiryDate(validity);
				if (StringUtils.equalsIgnoreCase(rfqItem.getCostIndicatorType(),
						QuoteSBConstant.MATERIAL_TYPE_NORMAL)) {
					Date quoteExpiryDate = calculateQuoteExpiryDate(validity, QuoteSBConstant.MATERIAL_TYPE_NORMAL);
					quoteItem.setQuoteExpiryDate(quoteExpiryDate);
				} else if (StringUtils.equalsIgnoreCase(rfqItem.getCostIndicatorType(),
						QuoteSBConstant.MATERIAL_TYPE_PROGRAM)) {
					Date quoteExpiryDate = calculateQuoteExpiryDate(validity, QuoteSBConstant.MATERIAL_TYPE_PROGRAM);
					quoteItem.setQuoteExpiryDate(quoteExpiryDate);
				} else {
					Date quoteExpiryDate = calculateQuoteExpiryDate(validity, QuoteSBConstant.MATERIAL_TYPE_NORMAL);
					quoteItem.setQuoteExpiryDate(quoteExpiryDate);
				}
			}
		}
		return quoteItem;
	}
	
	
	private Date calculateQuoteExpiryDate(Date date, String materialType) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		cal.setTime(date);
		if (materialType.equals(QuoteSBConstant.MATERIAL_TYPE_NORMAL)) {
			cal.add(Calendar.DATE,QuoteSBConstant.QUOTE_EXPIRY_DATE_SHIFT_NORMAL);
		} else if (materialType.equals(QuoteSBConstant.MATERIAL_TYPE_PROGRAM)) {
			cal.add(Calendar.DATE,QuoteSBConstant.QUOTE_EXPIRY_DATE_SHIFT_PROGRAM);
		}

		cal.set(Calendar.HOUR_OF_DAY, 15);
		return cal.getTime();
	}
	
	
	public boolean fieldValidityChecked(QuoteItem quoteItem) {
		boolean cannotGo = ((quoteItem.getMpq() == null || quoteItem.getMpq()
				.intValue() == INT_ZERO)
				|| ((quoteItem.getMoq() == null || quoteItem.getMoq()
						.intValue() == INT_ZERO) && (quoteItem.getMov() == null || quoteItem
						.getMov().intValue() == INT_ZERO))
				|| quoteItem.getPriceValidity() == null
				|| quoteItem.getCostIndicator() == null
				|| quoteItem.getQtyIndicator() == null
				|| (quoteItem.getCost() == null || quoteItem.getCost()
						.doubleValue() == DOUBLE_ZERO)
				|| quoteItem.getLeadTime() == null
				|| quoteItem.getMinSellPrice() == null || (quoteItem
				.getMinSellPrice() != null && quoteItem.getMinSellPrice() <= 0));

		if (!cannotGo) {
			if (quoteItem.getShipmentValidity() != null
					&& quoteItem.getShipmentValidity().before(com.avnet.emasia.webquote.utilites.web.util.QuoteUtil.getCurrentTime())) {
				cannotGo = true;
			}
		}
		if (!cannotGo) {
			Date validity = null;
			if (quoteItem.getPriceValidity().matches("[0-9]{1,}")) {
				int shift = Integer.parseInt(quoteItem.getPriceValidity());
				validity = com.avnet.emasia.webquote.utilites.web.util.QuoteUtil.shiftDate(quoteItem.getSentOutTime(), shift);
			} else {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				try {
					validity = sdf.parse(quoteItem.getPriceValidity());
				} catch (ParseException e) {
					LOGGER.log(Level.SEVERE, "Exception in parsing date : "+quoteItem.getPriceValidity()+" , Exception message : "+e.getMessage(), e);
				}
			}
			if (quoteItem.getMaterialTypeId() != null) {
				validity = com.avnet.emasia.webquote.utilites.web.util.QuoteUtil.shitDateByProgramType(validity,
						quoteItem.getMaterialTypeId());
			} else {
				validity = com.avnet.emasia.webquote.utilites.web.util.QuoteUtil.shitDateByProgramType(validity,
						QuoteSBConstant.MATERIAL_TYPE_NORMAL);
			}
			Date currentDate = QuoteUtil.getCurrentDateZeroHour();
			if (validity != null && (validity.compareTo(currentDate) < 0)) {
				cannotGo = true;
			}
		}
		return cannotGo;
	}
	
	
	public QuoteItem ncnrFilter(QuoteItem qi) {
		boolean findNcnr = false;
		String tempTerm = qi.getTermsAndConditions();
		if (tempTerm != null && (tempTerm.toUpperCase().contains("NCNR"))) {
			findNcnr = true;
		}
		if (findNcnr) {
			qi.setCancellationWindow(null);
			qi.setRescheduleWindow(null);
		}
		return qi;
	}
	
	/*private QuoteItem aqLogic(QuoteItem item, RfqItemVO rfqItem) {
		
		if (QuoteSBConstant.RFQ_COST_INDICATOR_TYPE_P.equalsIgnoreCase(rfqItem.getCostIndicatorType())) {
			try {
				item = ProgRfqSubmitHelper.processAutoQuoteItemForNormalSubmissionForRfqPage(item, rfqItem);
			} catch (ParseException e) {
				LOGGER.log(Level.SEVERE, "Error occured for Item MPQ:" + item.getMpq() + ", Reason for failure: "
						+ MessageFormatorUtil.getParameterizedStringFromException(e), e);
			}
			if (rfqItem.getCostIndicator() != null) {
				item.setQtyIndicator(rfqItem.getQtyIndicator());
			}
		} else {

			item.setTermsAndConditions(rfqItem.getTermsAndCondition());
			item.setLeadTime(rfqItem.getLeadTime());
			item.setMpq(rfqItem.getMpq());

			item = commonAQLogic(item, rfqItem);
		}

		return item;
	}*/
	
	/*private QuoteItem commonAQLogic(QuoteItem item, RfqItemVO rfqItem) {
		// it is normal
		if (item.getMov() == null || item.getMov().doubleValue() == DOUBLE_ZERO) {
			item.setQtyIndicator("MOQ");
			item.setPmoq(com.avnet.emasia.webquote.utilites.web.util.QuoteUtil.findPmoq(item));
		} else if (item.getMoq() == null || item.getMoq().doubleValue() == DOUBLE_ZERO) {
			item.setQtyIndicator("MOV");
			item.setMoq(com.avnet.emasia.webquote.utilites.web.util.QuoteUtil.mpqRoundUp(item.getMov() / item.getCost(),
					item.getMpq().intValue()));
			item.setPmoq(com.avnet.emasia.webquote.utilites.web.util.QuoteUtil.findPmoq(item));
		} else {
			double firstP = item.getMov() / item.getCost();
			double secondP = item.getMoq();
			if (firstP > secondP) {
				item.setQtyIndicator("MOV");
				item.setMoq(com.avnet.emasia.webquote.utilites.web.util.QuoteUtil.mpqRoundUp(firstP,
						item.getMpq().intValue()));
				item.setPmoq(com.avnet.emasia.webquote.utilites.web.util.QuoteUtil.findPmoq(item));
			} else {
				item.setQtyIndicator("MOQ");
				item.setPmoq(com.avnet.emasia.webquote.utilites.web.util.QuoteUtil.findPmoq(item));
			}
		}
		return item;
	}*/

	
	
	
	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	
	public DpRfq saveDpRfqObject(DpRfq dpRfq) {
		DpRfq savedDpRfq = null;
		savedDpRfq = em.merge(dpRfq);
		em.flush();
		return savedDpRfq;
	}
	
	
	public void saveDpRfqItem(DpRfqItem dpRfqItem) {
		em.merge(dpRfqItem);
		em.flush();
	}
	
	public DpMessage saveDpMessage(DpMessage dpMessage) {
		DpMessage tempDpMess = null;
		tempDpMess = em.merge(dpMessage);
		em.flush();
		return tempDpMess;
	}
	
	public void saveDpRfqItems(List<DpRfqItem> dpRfqItems) {
		for(DpRfqItem dpRfqItem : dpRfqItems) {
			em.merge(dpRfqItem);
		}
		em.flush();
	}
	
	public DpRfq findDpRfqByPk(long id) {
		return em.find(DpRfq.class, id);
	}
	
	public DpRfqItem findDpRfqItemByPk(long id) {
		return em.find(DpRfqItem.class, id);
	}

	public List<DpRfqItem> search(DpRfqSearchCriteria criteria) {
		List<DpRfqItem> searchResult = new ArrayList<DpRfqItem>();		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DpRfqItem> c = cb.createQuery(DpRfqItem.class);
		Root<DpRfqItem> dpRfqItem = c.from(DpRfqItem.class);
		Join<DpRfqItem, DpRfq> dpRfq = dpRfqItem.join("dpRfq", JoinType.LEFT);
		Join<DpRfqItem, QuoteItem> quoteItem = dpRfqItem.join("quoteItem", JoinType.LEFT);
		Join<DpRfq, DpMessage> dpMessage = dpRfq.join("dpMessage", JoinType.LEFT);
		c.orderBy(cb.desc(dpMessage.get("createRfqTime")));
		populateCriteria(c, criteria, dpRfqItem, dpRfq, quoteItem, dpMessage);
		TypedQuery<DpRfqItem> q = em.createQuery(c);
		searchResult = q.getResultList();
		return searchResult;
	}

	private void populateCriteria(CriteriaQuery<DpRfqItem> c,
			DpRfqSearchCriteria criteria, Root<DpRfqItem> dpRfqItem,
			Join<DpRfqItem, DpRfq> dpRfq,
			Join<DpRfqItem, QuoteItem> quoteItem,
			Join<DpRfq, DpMessage> dpMessage) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		List<Predicate> criterias = new ArrayList<Predicate>();
		if(criteria.getQuoteNumber() != null && criteria.getQuoteNumber().size() !=0 ) {
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			for(String quoteNumber : criteria.getQuoteNumber()){
				Predicate predicate = cb.like(cb.upper(quoteItem.<String>get("quoteNumber")), "%" + quoteNumber.toUpperCase() + "%" );
				predicates.add(predicate);
			}
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));
			
		}
		
		if (StringUtils.isNotBlank(criteria.getMfr())) {
			List<Predicate> predicates = new ArrayList<Predicate>();
			Predicate predicate = cb.like(cb.upper(dpRfqItem.<String>get("requestedManufacturer")), "%" + criteria.getMfr().toUpperCase() + "%" );
			predicates.add(predicate);
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));
		}	
		
		
		if (StringUtils.isNotBlank(criteria.getInBoundMessage())) {
			List<Predicate> predicates = new ArrayList<Predicate>();
			Predicate predicate = cb.like(cb.upper(dpMessage.<String>get("createRfqMessage")), "%" + criteria.getInBoundMessage().toUpperCase() + "%" );
			predicates.add(predicate);
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));
		}
		
		if(criteria.getSapPartNumber() != null && criteria.getSapPartNumber().size() != 0){
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			for(String sapPartNumber : criteria.getSapPartNumber()){
				Predicate predicate = cb.like(cb.upper(dpRfqItem.<String>get("requestedPart")), "%" + sapPartNumber.toUpperCase() + "%" );
				predicates.add(predicate);
					
			}
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));			
		}
		
		if(criteria.getReferenceId() != null && criteria.getReferenceId().size() != 0){
			List<Predicate> predicates = new ArrayList<Predicate>();
			for(String referenceId : criteria.getReferenceId()){
				Predicate predicate = cb.like(cb.trim(dpRfq.<String>get("referenceId")), "%" + referenceId + "%" );
				predicates.add(predicate);
					
			}
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));			
		}
		
		if(criteria.getReferenceLineId() != null && criteria.getReferenceLineId().size() != 0){
			List<Predicate> predicates = new ArrayList<Predicate>();
			for(String referenceLineId : criteria.getReferenceLineId()){
				Predicate predicate = cb.like(cb.trim(dpRfqItem.<String>get("referenceLineId")), "%" + referenceLineId + "%" );
				predicates.add(predicate);
					
			}
			criterias.add(cb.or(predicates.toArray(new Predicate[0])));			
		}


		if (criteria.getCreateRfqTimeFrom() != null ) {
			Predicate p0 = cb.greaterThanOrEqualTo(dpMessage.<Date>get("createRfqTime"), criteria.getCreateRfqTimeFrom());
			criterias.add(p0);
		}		
		
		if (criteria.getCreateRfqTimeTo() != null ) {
			Predicate p0 = cb.lessThanOrEqualTo(dpMessage.<Date>get("createRfqTime"), criteria.getCreateRfqTimeTo());
			criterias.add(p0);
		}
		
		if (criteria.getUpdateRfqTimeFrom() != null ) {
			Predicate p0 = cb.greaterThanOrEqualTo(dpMessage.<Date>get("updateRfqTime"), criteria.getUpdateRfqTimeFrom());
			criterias.add(p0);
		}		
		
		if (criteria.getUpdateRfqTimeTo() != null ) {
			Predicate p0 = cb.lessThanOrEqualTo(dpMessage.<Date>get("updateRfqTime"), criteria.getUpdateRfqTimeTo());
			criterias.add(p0);
		}	
		
		if (criteria.getBizUnits() != null && criteria.getBizUnits().size() != 0) {
			CriteriaBuilder.In<BizUnit> in = cb.in(dpMessage.<BizUnit>get("bizUnit"));
			
			for(BizUnit bu : criteria.getBizUnits()){
				in.value(bu);
			}
			criterias.add(in);			
		}else{
			Predicate predicate = cb.like(dpMessage.<String>get("bizUnit"), "" );
			criterias.add(predicate);
		}

		if (criterias.size() == 0) {
			
		} else if (criterias.size() == 1) {
			c.where(criterias.get(0));
		} else {
			c.where(cb.and(criterias.toArray(new Predicate[0])));
		}		
	}


	
}
