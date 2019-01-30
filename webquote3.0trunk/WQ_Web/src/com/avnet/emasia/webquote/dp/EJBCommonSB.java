package com.avnet.emasia.webquote.dp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.ParseException;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.mail.util.ByteArrayDataSource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.HSSFColor.AQUA;
import org.apache.poi.hssf.util.HSSFColor.LIME;
import org.apache.poi.hssf.util.HSSFColor.PINK;
import org.apache.poi.hssf.util.HSSFColor.RED;
import org.apache.poi.hssf.util.HSSFColor.WHITE;
import org.apache.poi.ss.usermodel.CellStyle;
import org.jboss.logmanager.Level;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.security.core.context.SecurityContextHolder;

import com.avnet.emasia.webquote.cache.ICacheUtil;
import com.avnet.emasia.webquote.commodity.bean.ProgramItemUploadCounterBean;
import com.avnet.emasia.webquote.commodity.dto.CheckBoxBean;
import com.avnet.emasia.webquote.commodity.dto.CheckBoxSet;
import com.avnet.emasia.webquote.commodity.ejb.ProgramMaterialSB;
import com.avnet.emasia.webquote.commodity.util.Constants;
import com.avnet.emasia.webquote.commodity.util.PricerUtils;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.ExchangeRate;
import com.avnet.emasia.webquote.entity.QuotationEmail;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.QuoteToSoPending;
import com.avnet.emasia.webquote.entity.Role;
import com.avnet.emasia.webquote.entity.Team;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.exception.AppException;
import com.avnet.emasia.webquote.exception.WebQuoteException;
import com.avnet.emasia.webquote.quote.ejb.QuoteSB;
import com.avnet.emasia.webquote.quote.ejb.QuoteToSoPendingSB;
import com.avnet.emasia.webquote.quote.ejb.SAPWebServiceSB;
import com.avnet.emasia.webquote.quote.ejb.SystemCodeMaintenanceSB;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.quote.vo.ReportSearchCriteria;
import com.avnet.emasia.webquote.quote.vo.WorkingPlatformItemVO;
import com.avnet.emasia.webquote.reports.ejb.DailyRITReportSB;
import com.avnet.emasia.webquote.reports.vo.ColumnModelVo;
import com.avnet.emasia.webquote.reports.vo.RFQBacklogReportVo;
import com.avnet.emasia.webquote.reports.vo.ReportGroupVo;
import com.avnet.emasia.webquote.strategy.MoneyConvertStrategy;
import com.avnet.emasia.webquote.strategy.StrategyFactory;
import com.avnet.emasia.webquote.user.ejb.TeamSB;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.common.CacheUtil;
import com.avnet.emasia.webquote.utilites.web.schedule.PostQuotationTask;
import com.avnet.emasia.webquote.utilites.web.util.DeploymentConfiguration;
import com.avnet.emasia.webquote.utilites.web.util.DownloadUtil;
import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil;
import com.avnet.emasia.webquote.utilities.DateUtils;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.utilities.bean.MailInfoBean;
import com.avnet.emasia.webquote.utilities.common.OSTimeZone;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;
import com.avnet.emasia.webquote.utilities.ejb.MailUtilsSB;
import com.avnet.emasia.webquote.vo.RfqHeaderVO;
import com.avnet.emasia.webquote.web.maintenance.UploadStrategy;
import com.avnet.emasia.webquote.web.quote.AsyncPostQuotationSB;
import com.avnet.emasia.webquote.web.quote.CommonBean;
import com.avnet.emasia.webquote.web.quote.ReportMB;
import com.avnet.emasia.webquote.web.quote.constant.QuoteConstant;
import com.avnet.emasia.webquote.web.quote.strategy.SendQuotationEmailStrategy;
import com.avnet.emasia.webquote.web.quote.strategy.SendQuotationEmailWithAttachStrategy;
import com.avnet.emasia.webquote.web.quote.vo.QuotationEmailVO;
import com.avnet.emasia.webquote.web.reports.RFQBacklogReportMB;
import com.avnet.emasia.webquote.web.reports.vo.DailyRITReportBean;
import com.avnet.emasia.webquote.web.security.WQUserDetails;
import com.avnet.emasia.webquote.web.user.UserInfo;
import com.sap.document.sap.soap.functions.mc_style.TableOfZquoteMsg;
import com.sap.document.sap.soap.functions.mc_style.ZquoteMsg;

@Stateless
@LocalBean
public class EJBCommonSB {

	private static final Logger LOGGER = Logger.getLogger(EJBCommonSB.class.getName());

	@PersistenceContext(unitName = "Server_Source")
	private EntityManager em;

	public User user;

	@EJB
	SAPWebServiceSB sapWebServiceSB;

	@EJB
	QuoteToSoPendingSB quoteToSoPendingSB;

	@EJB
	protected SysConfigSB sysConfigSB;

	@EJB
	UserSB userSB;

	@EJB
	private QuoteSB quoteSB;

	@EJB
	protected MailUtilsSB mailUtilsSB;

	@EJB
	private SystemCodeMaintenanceSB sysMaintSB;

	@EJB
	private DailyRITReportSB dailyRITReportSB;

	@EJB
	private SystemCodeMaintenanceSB sysCodeMaintSB;

	@EJB
	private CacheUtil cacheUtil;

	private final static DecimalFormat DF2 = new DecimalFormat("#.00");
	private final static DecimalFormat DF3 = new DecimalFormat("#");
	private final static DecimalFormat DF5 = new DecimalFormat("#");

	public final static String QUOTE_TYPE_YEAR = "Fiscal Year";
	public final static String QUOTE_TYPE_QTR = "Fiscal Qtr";
	public final static String QUOTE_TYPE_MONTH = "Fiscal Month";
	public final static String HEADER_QUOTE_PERIOD = "Quote Period";

	private final static String PERCENT_SIGN = "%";
	private final static String[] COLUMN_NAME_ARR = { "Form No", "Avnet Quote#", "Pending Day", "MFR", "Bid-To-Buy",
			"Salesman Name", "Team", "Customer Name(Sold To Party)", "Customer Type", "End Customer", "Requested P/N",
			"Required Qty", "EAU", "Target Resale", "Target Margin %", "Avnet Quoted P/N", "Quoted Margin %",
			"Avnet Quoted Price", "Cost", "Cost Indicator", "Qty Indicator", "Avnet Quoted Qty", "Multi-Usage",
			"QC Comment (Avnet Internal Only)", "Lead Time (wks)", "MPQ", "MOQ", "MOV", "Price Validity",
			"Shipment Validity", "MFR Debit #", "MFR Quote #", "MFR Quote Qty", "Rescheduling Window",
			"Cancellation Window", "Quotation T&C", "Resale Indicator", "RFQ Item Attachment", "RFQ Form Attachment",
			"PM Comment", "Allocation Part", "SPR", "Project Name", "Application", "MP Schedule", "PP Schedule",
			"DRMS Project ID", "DRMS Authorized GP%", "Reasons for Authorized GP% Change", "MFR DR#", "Design Location",
			"Local DR", "LOA", "Bid-To-Bid", "Product Group 2", "Competitor Information", "BMT Biz",
			"Business Program Type", "Remarks", "Item Remarks", "Reason For Refresh", "Segment", "Ship To Party",
			"Requester Reference", "RFQ Submission Date", "Sold To Code", "Ship To Code", "End Customer Code" };

	public void createQuoteToSo(List<QuoteItem> quoteItems) {
		try {
			TableOfZquoteMsg tableMsg = sapWebServiceSB.createSAPQuote(quoteItems);
			List<ZquoteMsg> msgs = tableMsg.getItem();
			List<String> errorMsgs = new ArrayList<String>();
			for (ZquoteMsg msg : msgs) {
				if (!msg.getType().equalsIgnoreCase("S")) {
					LOGGER.log(Level.WARNING,
							msg.getId() + "/" + msg.getMessage() + "/" + msg.getNumber() + "/" + msg.getType());
					for (QuoteItem item : quoteItems) {
						if (item.getQuoteNumber() != null && msg.getMessage() != null
								&& msg.getMessage().indexOf(item.getQuoteNumber()) > -1) {
							QuoteToSoPending quoteToSoPending = new QuoteToSoPending();
							quoteToSoPending.setBizUnit(item.getQuote().getBizUnit());
							quoteToSoPending.setCreateDate(QuoteUtil.getCurrentTime());
							quoteToSoPending.setCustomerNumber(item.getSoldToCustomer().getCustomerNumber());
							quoteToSoPending.setStatus(true);

							quoteToSoPending.setMfr(item.getRequestedMfr().getName());
							quoteToSoPending.setFullMfrPartNumber(item.getRequestedPartNumber());

							quoteToSoPending.setQuoteNumber(item.getQuoteNumber());
							quoteToSoPending.setMessage(msg.getMessage());
							quoteToSoPending.setType(msg.getType());
							try {
								QuoteToSoPending detach = quoteToSoPendingSB.updateQuoteToSoPending(quoteToSoPending);
							} catch (Exception ex) {
								LOGGER.log(Level.SEVERE,
										"Cannot create Quote To So Pending for Quote item biz unit: "
												+ item.getQuote().getBizUnit().getName() + ", " + "Reason for failure: "
												+ MessageFormatorUtil.getParameterizedStringFromException(ex),
										ex);
							}
							break;
						}
					}

					errorMsgs.add(msg.getMessage());
				}
			}
		} catch (AppException e) {

			LOGGER.log(Level.SEVERE,
					"exception message: " + MessageFormatorUtil.getParameterizedStringFromException(e));

			for (QuoteItem item : quoteItems) {
				QuoteToSoPending quoteToSoPending = new QuoteToSoPending();
				quoteToSoPending.setBizUnit(item.getQuote().getBizUnit());
				quoteToSoPending.setCreateDate(QuoteUtil.getCurrentTime());
				quoteToSoPending.setCustomerNumber(item.getSoldToCustomer().getCustomerNumber());
				quoteToSoPending.setStatus(true);

				quoteToSoPending.setMfr(item.getRequestedMfr().getName());
				quoteToSoPending.setFullMfrPartNumber(item.getRequestedPartNumber());

				quoteToSoPending.setQuoteNumber(item.getQuoteNumber());
				try {
					QuoteToSoPending detach = quoteToSoPendingSB.updateQuoteToSoPending(quoteToSoPending);
				} catch (Exception ex) {
					LOGGER.log(Level.SEVERE,
							"Cannot create Quote To So Pending for MFR: " + item.getRequestedMfr().getName()
									+ ", Exception message: "
									+ MessageFormatorUtil.getParameterizedStringFromException(e));
				}
			}
		}
	}

	public void sendQuotationEmail(QuotationEmailVO vo) {
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
		content += "Quotation is attached. Good Selling!<br/><br/>";
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
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			vo.getHssfWorkbook().write(baos);
			byte[] bytes = baos.toByteArray();
			ByteArrayDataSource ds = new ByteArrayDataSource(bytes, "application/excel");
			mailInfoBean.setFileByteArray(ds);

			String fileName = vo.getFileName().replaceAll("\n", "") + " #" + DateUtils.getDefaultDateStrEmailTimeStamp()
					+ "#.xls";
			mailInfoBean.setFileName(fileName);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE,
					"Exception in setting message info for email sent from: " + mailInfoBean.getMailFrom()
							+ ", Subject: " + mailInfoBean.getMailSubject() + ", exception message : " + e.getMessage(),
					e);
		}
		try {
			mailUtilsSB.sendHtmlMail(mailInfoBean);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception for email sent from: " + mailInfoBean.getMailFrom() + ", Subject: "
					+ mailInfoBean.getMailSubject() + ", Email Sending Error : " + e.getMessage(), e);
		}
	}

	public String getUrl() {
		String url = sysConfigSB.getProperyValue(QuoteConstant.WEBQUOTE2_DOMAIN);
		return url;
	}

	/**
	 * fill the exchange rate to the excel if the rate not exist, then remove
	 * this row
	 * 
	 * @param postQuotationTask
	 *            TODO
	 * @param quote
	 * @param sheet
	 */
	/*public void fillRateToXLS(Quote quote, HSSFSheet sheet) {
		ExchangeRate latestRate = getExChangeRateInfo(quote);

		// set latest exchange rate to excel ,
		if (null != latestRate && null != latestRate.getExRateTo() && null != latestRate.getVat()
				&& null != latestRate.getHandling()) {
			BigDecimal exRate = new BigDecimal(0);
			exRate = latestRate.getExRateTo().multiply(latestRate.getVat()).multiply(latestRate.getHandling())
					.setScale(5, RoundingMode.HALF_UP);
			// int rateRowNo = 34+ quote.getQuoteItems().size();
			// HSSFRow rowRate = sheet.getRow(36);
			HSSFRow rowRate = sheet.getRow(22);
			HSSFCell cell = rowRate.getCell(3);
			String value = latestRate.getExRateFrom() + " " + latestRate.getCurrFrom() + " = " + exRate + " "
					+ latestRate.getCurrTo();// 1 USD = 7.43 RMB
			cell.setCellValue(value);
		} else { // if the exchange rate not exist then delete it from excel
			// int rateRowNo = 34+ quote.getQuoteItems().size();
			// HSSFRow rowRate = sheet.getRow(36);
			HSSFRow rowRate = sheet.getRow(22);
			HSSFCell cell = rowRate.getCell(3);
			// cell.setCellValue("1 USD = 8.18052 RMB");
			// rowRate.removeCell(cell);
			// edite
			HSSFRow rowRate2 = sheet.getRow(21);
			HSSFCell cell2 = rowRate2.getCell(3);
			cell2.setCellValue("");
			// rowRate2.removeCell(cell2);

			// cell2.setCellValue("Quoted Ex. Rate:");
		}
*/
	//}

	/**
	 * get the latest exchange rate
	 * 
	 * @param postQuotationTask
	 *            TODO
	 * @param quote
	 * @return
	 */
	public ExchangeRate getExChangeRateInfo(Quote quote) {
		ExchangeRate rate = null;
		if (quote.getQuoteItems().size() > 0) {
			// fix INC0033494 by June Guo on 20140924
			// to extract latest exchangeRate by use first item because all
			// item's currecy from, currecy to and sold to code and region
			// should be same
			rate = quoteSB.findLatestExchangeRate(quote.getQuoteItems().get(0));
		}
		return rate;
	}
/*
	private HSSFWorkbook getQuoteTemplateBySoldTo(Customer soldToCustomer, Quote quote, String flowName) {

		HSSFWorkbook wb = null;
		if (quote != null) {
			DF3.setMaximumFractionDigits(2);
			DF3.setMinimumFractionDigits(2);
			DF3.setMinimumIntegerDigits(1);
			DF5.setMaximumFractionDigits(5);
			DF5.setMinimumFractionDigits(5);
			DF5.setMinimumIntegerDigits(1);

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			List<QuoteItem> items = quote.getQuoteItems();
			int terms = (int) ('A');
			HashMap<String, String> termsAndConditions = new HashMap<String, String>();
			// Defect #4
			
			 * try { FileInputStream inputStream = new
			 * FileInputStream("C:/excel/QuoteTemplate.xls"); wb = new
			 * HSSFWorkbook(inputStream); } catch (Exception e1) { // TODO
			 * Auto-generated catch block e1.printStackTrace(); }
			 

			wb = getHSSFWorkbook(quote.getBizUnit().getName());
			// style definition:
			HSSFFont normalStyle = wb.createFont();
			normalStyle.setFontHeightInPoints((short) 10);
			normalStyle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

			HSSFFont whiteStyle = wb.createFont();
			whiteStyle.setBoldweight((short) 10);
			whiteStyle.setColor(HSSFColor.WHITE.index);

			HSSFFont redBoldStyle = wb.createFont();
			redBoldStyle.setFontHeightInPoints((short) 10);
			redBoldStyle.setColor(HSSFColor.RED.index);
			redBoldStyle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			// redBoldStyle.set
			// ======================================
			HSSFCellStyle redFontPinkBackWrapStyle = wb.createCellStyle();
			redFontPinkBackWrapStyle.setWrapText(true);
			redFontPinkBackWrapStyle.setFillForegroundColor(HSSFColor.PINK.index);
			redFontPinkBackWrapStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			redFontPinkBackWrapStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			redFontPinkBackWrapStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			redFontPinkBackWrapStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			redFontPinkBackWrapStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			redFontPinkBackWrapStyle.setFont(redBoldStyle);
			redFontPinkBackWrapStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			HSSFCellStyle redFontPinkBackStyle = wb.createCellStyle();
			redFontPinkBackStyle.setFillForegroundColor(HSSFColor.PINK.index);
			redFontPinkBackStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			redFontPinkBackStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			redFontPinkBackStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			redFontPinkBackStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			redFontPinkBackStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			redFontPinkBackStyle.setFont(redBoldStyle);
			redFontPinkBackStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			HSSFCellStyle normalFontPinkBackStyle = wb.createCellStyle();
			normalFontPinkBackStyle.setFillForegroundColor(HSSFColor.PINK.index);
			normalFontPinkBackStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			normalFontPinkBackStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			normalFontPinkBackStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			normalFontPinkBackStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			normalFontPinkBackStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			normalFontPinkBackStyle.setFont(normalStyle);
			normalFontPinkBackStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			// ======================================
			HSSFCellStyle wrapNormalStyle = wb.createCellStyle();
			wrapNormalStyle.setWrapText(true);
			wrapNormalStyle.setFillForegroundColor(HSSFColor.WHITE.index);
			wrapNormalStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			wrapNormalStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			wrapNormalStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			wrapNormalStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			wrapNormalStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			wrapNormalStyle.setFont(normalStyle);
			wrapNormalStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			HSSFCellStyle blueCellStyle = wb.createCellStyle();
			blueCellStyle.setWrapText(true);
			blueCellStyle.setFillForegroundColor(HSSFColor.LIME.index);
			blueCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			blueCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			blueCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			blueCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			blueCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			blueCellStyle.setFont(normalStyle);
			blueCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			HSSFCellStyle redFontWhiteBackStyle = wb.createCellStyle();
			redFontWhiteBackStyle.setFillForegroundColor(HSSFColor.WHITE.index);
			redFontWhiteBackStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			redFontWhiteBackStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			redFontWhiteBackStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			redFontWhiteBackStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			redFontWhiteBackStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			redFontWhiteBackStyle.setFont(redBoldStyle);
			redFontWhiteBackStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			HSSFCellStyle redFontBlueBackStyle = wb.createCellStyle();
			redFontBlueBackStyle.setFillForegroundColor(HSSFColor.LIME.index);
			redFontBlueBackStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			redFontBlueBackStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			redFontBlueBackStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			redFontBlueBackStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			redFontBlueBackStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			redFontBlueBackStyle.setFont(redBoldStyle);
			redFontBlueBackStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			// set cellStyle cell header
			HSSFCellStyle cellHeader = wb.createCellStyle();
			cellHeader.setWrapText(true);
			cellHeader.setFillForegroundColor(HSSFColor.SEA_GREEN.index);
			cellHeader.setFillPattern(CellStyle.SOLID_FOREGROUND);
			cellHeader.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			cellHeader.setBorderRight(HSSFCellStyle.BORDER_THIN);
			cellHeader.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			cellHeader.setBorderTop(HSSFCellStyle.BORDER_THIN);
			cellHeader.setFont(whiteStyle);
			cellHeader.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			HSSFCellStyle redFontBlueBackWrapStyle = wb.createCellStyle();
			redFontBlueBackWrapStyle.setWrapText(true);
			redFontBlueBackWrapStyle.setFillForegroundColor(HSSFColor.LIME.index);
			redFontBlueBackWrapStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			redFontBlueBackWrapStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			redFontBlueBackWrapStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			redFontBlueBackWrapStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			redFontBlueBackWrapStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			redFontBlueBackWrapStyle.setFont(redBoldStyle);
			redFontBlueBackWrapStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			HSSFCellStyle normalFontBlueBackStyle = wb.createCellStyle();
			normalFontBlueBackStyle.setWrapText(true);
			normalFontBlueBackStyle.setFillForegroundColor(HSSFColor.LIME.index);
			normalFontBlueBackStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			normalFontBlueBackStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			normalFontBlueBackStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			normalFontBlueBackStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			normalFontBlueBackStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			normalFontBlueBackStyle.setFont(normalStyle);
			normalFontBlueBackStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			HSSFPalette palette = wb.getCustomPalette();
			// palette.setColorAtIndex(HSSFColor.LIME.index, (byte) 204, (byte)
			// 255, (byte) 255);
			palette.setColorAtIndex(HSSFColor.LIME.index, (byte) 191, (byte) 235, (byte) 201);
			palette.setColorAtIndex(HSSFColor.PINK.index, (byte) 242, (byte) 220, (byte) 219);
			// change cellhead colour
			palette.setColorAtIndex(HSSFColor.SEA_GREEN.index, (byte) 56, (byte) 175, (byte) 87);

			HSSFSheet sheet = wb.getSheet("Quotation");

			
			 * for(int i=0; i<13; i++){ for(int j=0; j<=5; j++){ String position
			 * = QuoteUtil.convertExcelPositionToString(i,j); HSSFRow row1 =
			 * sheet.getRow(j); HSSFCell cell1 = row1.getCell(i); switch
			 * (position) { case "E12":
			 * 
			 * cell1.setCellStyle(cellHeader);
			 * 
			 * break; } } }
			 

			fillRateToXLS(quote, sheet); // added by June to get latest exchange
											// rate info according to the item
											// is highlighted and quote is not
											// expiried
			// before i<10
			for (int i = 0; i < 13; i++) {
				for (int j = 0; j <= QuoteConstant.QUOTE_TEMPLATE_HEADER_END; j++) {
					// for(int j=0; j<=25; j++){
					String position = QuoteUtil.convertExcelPositionToString(i, j);
					HSSFRow row = sheet.getRow(j);
					HSSFCell cell = row.getCell(i);
					switch (position) {
					case "E12":
						cell.setCellStyle(cellHeader);
						break;
					case "E3":
						List<QuoteItem> quoteItems = quote.getQuoteItems();
						if (quoteItems != null) {
							Date submitDate = null;
							for (QuoteItem quoteItem : quoteItems) {
								if (quoteItem.getSubmissionDate() != null) {
									if (submitDate == null) {
										submitDate = quoteItem.getSubmissionDate();
									} else {
										if (submitDate.before(quoteItem.getSubmissionDate()))
											submitDate = quoteItem.getSubmissionDate();
									}
								}
							}
							if (submitDate != null)
								cell.setCellValue(sdf.format(submitDate));
						}
						break;
					case "E5":
						cell.setCellValue(sdf.format(QuoteUtil.getCurrentTime()));
						// cell.setCellStyle(e5cellStyle);
						break;
					case "F7":
						cell.setCellValue(quote.getFormNumber());
						break;
					case "H7":
						cell.setCellValue(quote.getYourReference());
						break;
					case "J7":
						if (quote.getSales() != null && quote.getSales().getTeam() != null) {
							cell.setCellValue(quote.getSales().getTeam().getName());
						}
						break;
					case "F8":
						String customerName = quote.getSoldToCustomerName();
						cell.setCellValue(customerName + " (" + soldToCustomer.getCustomerNumber() + ")");
						break;
					case "J8":
						cell.setCellValue(quote.getSales() == null ? null : quote.getSales().getName());
						break;

					case "F9":
						String totalShipTo = "";
						if (items != null) {
							for (QuoteItem item : items) {
								if (item.getShipToCustomer() != null) {
									String shipToCustomerName = DownloadUtil
											.getCustomerFullName(item.getShipToCustomer());
									shipToCustomerName += " (" + item.getShipToCustomer().getCustomerNumber() + ")";
									if (totalShipTo.indexOf(shipToCustomerName) == -1) {
										if (totalShipTo.length() > 0)
											totalShipTo += ", ";
										totalShipTo += shipToCustomerName;
									}
								} else if (item.getShipToCustomerName() != null) {
									if (totalShipTo.indexOf(item.getShipToCustomerName()) == -1) {
										if (totalShipTo.length() > 0)
											totalShipTo += ", ";
										totalShipTo += item.getShipToCustomerName();
									}
								}
							}
						}
						cell.setCellValue(totalShipTo);
						break;
					case "J9":
						if (quote.getSales() != null) {
							cell.setCellValue(quote.getSales().getEmployeeId());
						}
						break;
					case "F10":
						String totalEnd = "";
						if (items != null) {
							for (QuoteItem item : items) {
								if (item.getEndCustomer() != null) {
									String endCustomerName = DownloadUtil.getCustomerFullName(item.getEndCustomer());
									endCustomerName += " (" + item.getEndCustomer().getCustomerNumber() + ")";
									if (totalEnd.indexOf(endCustomerName) == -1) {
										if (totalEnd.length() > 0)
											totalEnd += ", ";
										totalEnd += endCustomerName;
									}
								} else if (item.getEndCustomerName() != null) {
									if (totalEnd.indexOf(item.getEndCustomerName()) == -1) {
										if (totalEnd.length() > 0)
											totalEnd += ", ";
										totalEnd += item.getEndCustomerName();
									}
								}
							}
						}
						cell.setCellValue(totalEnd);
						break;
					case "J10":
						cell.setCellValue(quote.getRemarks());
						break;

					}
				}
			}
			List<QuoteItem> quoteItems = quote.getQuoteItems();
			int termsIndex = QuoteConstant.QUOTE_ITEM_ROW_START + 3;
			if (quoteItems.size() > 1) {
				for (int i = 1; i < quoteItems.size(); i++) {
					QuoteUtil.copyRow(sheet, QuoteConstant.QUOTE_ITEM_ROW_START,
							QuoteConstant.QUOTE_ITEM_ROW_START + i);
					termsIndex = QuoteConstant.QUOTE_ITEM_ROW_START + i + 3;
				}
			}

			HSSFCellStyle quotedStyle = wb.createCellStyle();
			quotedStyle.setFillBackgroundColor(HSSFColor.AQUA.index);

			// added by June for RMB cur project 20140717 to control column
			for (int i = 0; i < quoteItems.size(); i++) {
				QuoteItem quoteItem = quoteItems.get(i);
				int j = 2;
				HSSFRow row = sheet.getRow(QuoteConstant.QUOTE_ITEM_ROW_START + i);
				HSSFCell cell_no = row.getCell(j++); // changed the 2 to j++
				HSSFCell cell_quoteNumber = row.getCell(j++);
				HSSFCell cell_quoteStage = row.getCell(j++);
				// HSSFCell cell_firstRfqCode = row.getCell(j++);
				HSSFCell cell_mfr = row.getCell(j++);
				HSSFCell cell_mfrName = row.getCell(j++);
				HSSFCell cell_requestedPartNumber = row.getCell(j++);
				HSSFCell cell_quotedPartNumber = row.getCell(j++);
				HSSFCell cell_aqcc = row.getCell(j++);
				// cell_aqcc.setCellStyle(wrapNormalStyle);
				// sales cost
				HSSFCell cell_saleCost = row.getCell(j++);
				// suggested_resale
				HSSFCell cell_suggestedResale = row.getCell(j++);
				// Avnet Quoted Price
				HSSFCell cell_quotedPrice = row.getCell(j++);
				HSSFCell cell_targetPrice = row.getCell(j++);
				// finalQuotationPrice
				HSSFCell cell_finalQuotationPrice = row.getCell(j++);
				HSSFCell cell_effectiveDate = row.getCell(j++);
				// HSSFCell cell_priceValidity = row.getCell(j++);
				HSSFCell cell_quoteExpiryDate = row.getCell(j++);
				HSSFCell cell_shipmentValidity = row.getCell(j++);
				// HSSFCell cell_requiredQty = row.getCell(j++);
				HSSFCell cell_termsAndConditions = row.getCell(j++);
				HSSFCell cell_pmoq = row.getCell(j++);
				HSSFCell cell_mpq = row.getCell(j++);
				HSSFCell cell_moq = row.getCell(j++);
				HSSFCell cell_leadTime = row.getCell(j++);

				// cell_aqcc.setCellStyle(wrapNormalStyle);
				HSSFCell cell_allocationFlag = row.getCell(j++);
				HSSFCell cell_rescheduleWindow = row.getCell(j++);
				HSSFCell cell_cancellationWindow = row.getCell(j++);
				HSSFCell cell_requiredQty = row.getCell(j++);

				HSSFCell cell_eau = row.getCell(j++);
				HSSFCell cell_soldTo = row.getCell(j++);
				HSSFCell cell_shipTo = row.getCell(j++);
				HSSFCell cell_endCustomer = row.getCell(j++);
				HSSFCell cell_itemRemarks = row.getCell(j++);
				if (quoteItem.isHightlighted()) {
					cell_quoteNumber.setCellStyle(normalFontBlueBackStyle);
					// cell_quoteNumber.setCellStyle(blueCellStyle);
					cell_quoteStage.setCellStyle(normalFontBlueBackStyle);
					// cell_firstRfqCode.setCellStyle(normalFontBlueBackStyle);
					cell_mfr.setCellStyle(normalFontBlueBackStyle);
					cell_mfrName.setCellStyle(normalFontBlueBackStyle);
					cell_requestedPartNumber.setCellStyle(normalFontBlueBackStyle);
					cell_quotedPartNumber.setCellStyle(normalFontBlueBackStyle);
					cell_aqcc.setCellStyle(normalFontBlueBackStyle);
					cell_saleCost.setCellStyle(normalFontBlueBackStyle);
					cell_suggestedResale.setCellStyle(normalFontBlueBackStyle);
					cell_requiredQty.setCellStyle(normalFontBlueBackStyle);
					cell_eau.setCellStyle(normalFontBlueBackStyle);
					cell_targetPrice.setCellStyle(normalFontBlueBackStyle);
					cell_finalQuotationPrice.setCellStyle(normalFontBlueBackStyle);
					cell_quotedPrice.setCellStyle(normalFontBlueBackStyle);

					// cell_exCurrency.setCellStyle(redFontBlueBackStyle);
					// //added by June 20140717
					// cell_exRate.setCellStyle(redFontBlueBackStyle); //added
					// by June 20140717

					cell_pmoq.setCellStyle(normalFontBlueBackStyle);
					cell_mpq.setCellStyle(normalFontBlueBackStyle);
					cell_moq.setCellStyle(normalFontBlueBackStyle);
					cell_leadTime.setCellStyle(normalFontBlueBackStyle);
					cell_effectiveDate.setCellStyle(normalFontBlueBackStyle);
					cell_aqcc.setCellStyle(normalFontBlueBackStyle);
					cell_quoteExpiryDate.setCellStyle(normalFontBlueBackStyle);
					cell_shipmentValidity.setCellStyle(normalFontBlueBackStyle);
					cell_termsAndConditions.setCellStyle(normalFontBlueBackStyle);
					cell_allocationFlag.setCellStyle(normalFontBlueBackStyle);
					cell_rescheduleWindow.setCellStyle(normalFontBlueBackStyle);
					cell_cancellationWindow.setCellStyle(normalFontBlueBackStyle);
					cell_soldTo.setCellStyle(normalFontBlueBackStyle);
					cell_shipTo.setCellStyle(normalFontBlueBackStyle);
					cell_endCustomer.setCellStyle(normalFontBlueBackStyle);
					cell_itemRemarks.setCellStyle(normalFontBlueBackStyle);
				}

				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_DRAFT)) {
					cell_quoteNumber.setCellStyle(redFontPinkBackStyle);
					cell_quoteStage.setCellStyle(redFontPinkBackStyle);
					// cell_firstRfqCode.setCellStyle(normalFontPinkBackStyle);
					cell_mfr.setCellStyle(normalFontPinkBackStyle);
					cell_mfrName.setCellStyle(normalFontPinkBackStyle);
					cell_requestedPartNumber.setCellStyle(normalFontPinkBackStyle);
					cell_quotedPartNumber.setCellStyle(redFontPinkBackStyle);
					cell_aqcc.setCellStyle(redFontPinkBackWrapStyle);
					cell_saleCost.setCellStyle(normalFontPinkBackStyle);
					cell_suggestedResale.setCellStyle(normalFontPinkBackStyle);
					cell_requiredQty.setCellStyle(normalFontPinkBackStyle);
					cell_eau.setCellStyle(normalFontPinkBackStyle);
					cell_targetPrice.setCellStyle(normalFontPinkBackStyle);
					cell_finalQuotationPrice.setCellStyle(normalFontPinkBackStyle);
					cell_quotedPrice.setCellStyle(redFontPinkBackStyle);

					// cell_exCurrency.setCellStyle(redFontPinkBackStyle);
					// //added by June 20140717
					// cell_exRate.setCellStyle(redFontPinkBackStyle); //added
					// by June 20140717

					cell_pmoq.setCellStyle(redFontPinkBackStyle);
					cell_mpq.setCellStyle(redFontPinkBackStyle);
					cell_moq.setCellStyle(redFontPinkBackStyle);
					cell_leadTime.setCellStyle(redFontPinkBackStyle);

					cell_effectiveDate.setCellStyle(redFontPinkBackStyle); // modified
																			// by
																			// Lenon
																			// 20151223
																			// INC0289600
					cell_quoteExpiryDate.setCellStyle(redFontPinkBackStyle);
					cell_shipmentValidity.setCellStyle(redFontPinkBackStyle);
					cell_termsAndConditions.setCellStyle(redFontPinkBackWrapStyle);
					cell_allocationFlag.setCellStyle(redFontPinkBackStyle);
					cell_rescheduleWindow.setCellStyle(redFontPinkBackStyle);
					cell_cancellationWindow.setCellStyle(redFontPinkBackStyle);
					cell_soldTo.setCellStyle(normalFontPinkBackStyle);
					cell_shipTo.setCellStyle(normalFontPinkBackStyle);
					cell_endCustomer.setCellStyle(normalFontPinkBackStyle);
					cell_itemRemarks.setCellStyle(normalFontPinkBackStyle);
				}

				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)) {
					cell_no.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cell_quoteNumber.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cell_quoteStage.setCellValue(QuoteConstant.DEFAULT_BLANK);
					// cell_firstRfqCode.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cell_mfr.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cell_mfrName.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cell_requestedPartNumber.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cell_quotedPartNumber.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cell_aqcc.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cell_saleCost.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cell_suggestedResale.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cell_requiredQty.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cell_targetPrice.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cell_finalQuotationPrice.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cell_quotedPrice.setCellValue(QuoteConstant.DEFAULT_BLANK);

					// cell_exCurrency.setCellValue(QuoteConstant.DEFAULT_BLANK);
					// //added by June 20140717
					// cell_exRate.setCellValue(QuoteConstant.DEFAULT_BLANK);
					// //added by June 20140717

					cell_pmoq.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cell_mpq.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cell_moq.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cell_leadTime.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cell_effectiveDate.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cell_quoteExpiryDate.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cell_shipmentValidity.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cell_termsAndConditions.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cell_allocationFlag.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cell_rescheduleWindow.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cell_cancellationWindow.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cell_soldTo.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cell_shipTo.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cell_endCustomer.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cell_itemRemarks.setCellValue(QuoteConstant.DEFAULT_BLANK);
				}

				cell_no.setCellValue("No." + (i + 1));
				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)) {
					cell_quoteNumber.setCellValue(quoteItem.getQuoteNumber());
				}

				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)) {
					cell_quoteStage.setCellValue(quoteItem.getStage());
				}

				
				 * if(quoteItem.getStage() != null &&
				 * quoteItem.getStage().equals(QuoteSBConstant.
				 * QUOTE_STAGE_FINISH)){
				 * cell_firstRfqCode.setCellValue(quoteItem.getFirstRfqCode());
				 * }
				 

				if (quoteItem.getRequestedMfr() != null) {
					cell_mfr.setCellValue(quoteItem.getRequestedMfr().getName());
					String mfrDescription = quoteItem.getRequestedMfr().getDescription();
					if (mfrDescription != null) {
						String sub = quoteItem.getRequestedMfr().getName() + " ";
						if (mfrDescription.startsWith(sub)) {
							cell_mfrName
									.setCellValue(quoteItem.getRequestedMfr().getDescription().substring(sub.length()));

						}
					}
				}

				if (quoteItem.getRequestedPartNumber() != null) {
					cell_requestedPartNumber.setCellValue(quoteItem.getRequestedPartNumber());
				}
				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)) {
					if (!QuoteUtil.isEmpty(quoteItem.getQuotedPartNumber())) {
						cell_quotedPartNumber.setCellValue(quoteItem.getQuotedPartNumber());
					} else {
						LOGGER.log(Level.WARNING, "QuotedMaterial is missing in " + quoteItem.getQuoteNumber());
					}
				}
				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_PENDING)) {
					cell_aqcc.setCellValue("In Progress");
				} else if (QuoteUtil.isEmpty(quoteItem.getAqcc())) {
					cell_aqcc.setCellValue(QuoteConstant.OPTION_NO);
				} else {
					cell_aqcc.setCellValue(quoteItem.getAqcc());
				}
				// fix 1054 , fixed by Tonmy on 22, Nov 2013
				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_DRAFT)) {
					// cell_aqcc.setCellStyle(redFontWhiteBackWrapStyle);
					cell_aqcc.setCellValue(QuoteSBConstant.DEFAULT_QC_EXTERNAL_COMMENT);
				}

				// set values for sales cost
				if (quoteItem.getStage() != null && quoteItem.isSalesCostFlag() && quoteItem.getSalesCost() != null) {
					cell_saleCost.setCellValue(DF5.format(quoteItem.getSalesCost()));
				}
				// suggested_resale
				if (quoteItem.getStage() != null && quoteItem.isSalesCostFlag()
						&& quoteItem.getSuggestedResale() != null) {
					cell_suggestedResale.setCellValue(DF5.format(quoteItem.getSuggestedResale()));
				}
				String requestQty = "";
				if (quoteItem.getRequestedQty() != null)
					requestQty = String.valueOf(quoteItem.getRequestedQty());

				cell_requiredQty.setCellValue(requestQty);
				String eau = "";
				if (quoteItem.getEau() != null)
					eau = String.valueOf(quoteItem.getEau());
				else
					eau = QuoteConstant.DEFAULT_BLANK;
				cell_eau.setCellValue(eau);

				if (quoteItem.getTargetResale() != null)
					cell_targetPrice.setCellValue(DF5.format(quoteItem.getTargetResale()));
				if (quoteItem.getFinalQuotationPrice() != null)
					cell_finalQuotationPrice.setCellValue(DF5.format(quoteItem.getFinalQuotationPrice()));

				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)
						&& quoteItem.getQuotedPrice() != null)
					cell_quotedPrice.setCellValue(DF5.format(quoteItem.getQuotedPrice()));

				// added by June 2014074 for project RMB cur start
				
				 * if(quoteItem.getStage() != null && quoteItem.getCurrTo() !=
				 * null) cell_exCurrency.setCellValue(quoteItem.getCurrTo());
				 * 
				 * if(quoteItem.getStage() != null && quoteItem.getExRateTo() !=
				 * null && quoteItem.getVat() != null&& quoteItem.getHandling()
				 * != null)
				 * cell_exRate.setCellValue(df5.format(quoteItem.getExRateTo().
				 * multiply(quoteItem.getVat()).multiply(quoteItem.getHandling()
				 * ).setScale(5,RoundingMode.HALF_UP)));
				  // =-------end-------------------------------------

				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)
						&& quoteItem.getPmoq() != null)
					cell_pmoq.setCellValue(quoteItem.getPmoq());

				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)
						&& quoteItem.getMpq() != null)
					cell_mpq.setCellValue(quoteItem.getMpq());

				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)
						&& quoteItem.getMoq() != null)
					cell_moq.setCellValue(quoteItem.getMoq());

				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)
						&& quoteItem.getLeadTime() != null) {
					String leadTime = quoteItem.getLeadTime();
					if (leadTime.trim().length() <= 5 && !leadTime.toLowerCase().endsWith("wks")
							&& (!leadTime.toUpperCase().contains("STOCK") && !leadTime.toUpperCase().contains("STK"))) {
						leadTime += " wks";
					}
					cell_leadTime.setCellValue(leadTime);
				}

				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)
						&& quoteItem.getQuotationEffectiveDate() != null) {
					cell_effectiveDate.setCellValue(sdf.format(quoteItem.getQuotationEffectiveDate()));
				}
				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)
						&& quoteItem.getQuoteExpiryDate() != null) {
					cell_quoteExpiryDate.setCellValue(sdf.format(quoteItem.getQuoteExpiryDate()));
				}
				
				 * if(quoteItem.getStage() != null &&
				 * quoteItem.getStage().equals(QuoteSBConstant.
				 * QUOTE_STAGE_FINISH) && quoteItem.getPriceValidity() != null){
				 * Date validity = null;
				 * if(quoteItem.getPriceValidity().matches("[0-9]{1,}")){ int
				 * shift = Integer.parseInt(quoteItem.getPriceValidity());
				 * validity = QuoteUtil.shiftDate(quoteItem.getSentOutTime(),
				 * shift); } else { try { validity =
				 * sdf.parse(quoteItem.getPriceValidity()); } catch
				 * (ParseException e) { // TODO Auto-generated catch block
				 * LOGGER.log(Level.SEVERE, e.getMessage()); } } if(validity !=
				 * null){ Date quoteExpiryDate = null; //andy modify 2.2
				 * Material restructure and quote_item delinkage
				 * if(QuoteSBConstant.MATERIAL_TYPE_NORMAL.equals(quoteItem.
				 * getMaterialTypeId())){ quoteExpiryDate =
				 * QuoteUtil.calculateQuoteExpiryDate(validity,
				 * QuoteSBConstant.MATERIAL_TYPE_NORMAL); } else
				 * if(QuoteSBConstant.MATERIAL_TYPE_PROGRAM.equals(quoteItem.
				 * getMaterialTypeId())){ quoteExpiryDate =
				 * QuoteUtil.calculateQuoteExpiryDate(validity,
				 * QuoteSBConstant.MATERIAL_TYPE_PROGRAM); } else {
				 * quoteExpiryDate =
				 * QuoteUtil.calculateQuoteExpiryDate(validity,
				 * QuoteSBConstant.MATERIAL_TYPE_NORMAL); }
				 * cell_priceValidity.setCellValue(sdf.format(quoteExpiryDate));
				 * } }
				 
				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)
						&& quoteItem.getShipmentValidity() != null)
					// cell_shipmentValidity.setCellValue(quoteItem.getShipmentValidityStr());
					cell_shipmentValidity.setCellValue(sdf.format(quoteItem.getShipmentValidity()));
				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)
						&& quoteItem.getTermsAndConditions() != null) {
					String quoteItemTermsAndCondition = quoteItem.getTermsAndConditions().trim()
							.replaceAll("[\\n\\r]+$", "");
					if (!quoteItemTermsAndCondition.equalsIgnoreCase("NO")) {
						String termsAndCondition = termsAndConditions.get(quoteItemTermsAndCondition);
						if (termsAndCondition == null) {
							termsAndConditions.put(quoteItemTermsAndCondition, "" + QuoteUtil.getTermId(terms));
							cell_termsAndConditions
									.setCellValue("Please refer to Special T&C " + QuoteUtil.getTermId(terms));
							terms++;
						} else {
							cell_termsAndConditions.setCellValue("Please refer to Special T&C "
									+ termsAndConditions.get(quoteItemTermsAndCondition));
						}

					}

				}
				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)
						&& quoteItem.getAllocationFlag() != null) {
					cell_allocationFlag.setCellValue(quoteItem.getAllocationFlag().booleanValue()
							? QuoteConstant.OPTION_YES : QuoteConstant.OPTION_NO);
				}
				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)
						&& quoteItem.getRescheduleWindow() != null) {
					if ("PostQuotationTask".equals(flowName)) {
						if (0 != quoteItem.getRescheduleWindow()) { // Fix
																	// INC0026994
																	// add by
																	// June
																	// 20140903
							cell_rescheduleWindow.setCellValue(quoteItem.getRescheduleWindow());
						}
					} else {
						cell_rescheduleWindow.setCellValue(quoteItem.getRescheduleWindow());
					}
				}
				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)
						&& quoteItem.getCancellationWindow() != null) {
					if ("PostQuotationTask".equals(flowName)) {
						if (0 != quoteItem.getCancellationWindow()) { // Fix
																		// INC0026994
																		// add
																		// by
																		// June
																		// 20140903
							cell_cancellationWindow.setCellValue(quoteItem.getCancellationWindow());
						}
					} else {
						cell_cancellationWindow.setCellValue(quoteItem.getCancellationWindow());
					}
				}
				if (quoteItem.getSoldToCustomer() != null) {
					// INC0018065, INC0018819
					// String customerName =
					// DownloadUtil.getCustomerFullName(quoteItem.getSoldToCustomer());
					String customerName = quote.getSoldToCustomerName();
					if (customerName != null)
						cell_soldTo.setCellValue(customerName);
				}
				if (quoteItem.getShipToCustomer() != null) {
					String customerName = DownloadUtil.getCustomerFullName(quoteItem.getShipToCustomer());
					if (customerName != null)
						cell_shipTo.setCellValue(customerName);
				} else if (quoteItem.getShipToCustomerName() != null) {
					cell_shipTo.setCellValue(quoteItem.getShipToCustomerName());
				}
				if (quoteItem.getEndCustomer() != null) {
					String customerName = DownloadUtil.getCustomerFullName(quoteItem.getEndCustomer());
					if (customerName != null)
						cell_endCustomer.setCellValue(customerName);
				} else if (quoteItem.getEndCustomerName() != null) {
					cell_endCustomer.setCellValue(quoteItem.getEndCustomerName());
				}

				if (quoteItem.getRemarks() != null)
					cell_itemRemarks.setCellValue(quoteItem.getRemarks());
			}
			List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(
					termsAndConditions.entrySet());
			Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
				public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
					if (o1.getValue().toString().length() == o2.getValue().toString().length()) {
						return (o1.getValue()).toString().compareTo(o2.getValue());
					} else {
						return o1.getValue().toString().length() - o2.getValue().toString().length();
					}
				}
			});
			if (infoIds != null && infoIds.size() > 0) {
				String tc = "";
				for (int i = 0; i < infoIds.size(); i++) {
					Map.Entry<String, String> id = infoIds.get(i);
					tc += id.getValue() + ". " + id.getKey() + "\n";
				}

				if (tc.length() > 0) {
					HSSFRow row = sheet.getRow(termsIndex);
					HSSFCell cell = row.getCell(8);
					cell.setCellValue(tc);
				}
			}

		}
		return wb;
	}*/
/*
	public HSSFWorkbook getQuoteTemplateBySoldTo(Object postQuotationTask, Customer soldToCustomer, Quote quote) {
		HSSFWorkbook wb = null;
		if (quote != null) {

			DF2.setMaximumFractionDigits(2);
			DF2.setMinimumFractionDigits(2);
			DF2.setMinimumIntegerDigits(1);
			DF5.setMaximumFractionDigits(5);
			DF5.setMinimumFractionDigits(5);
			DF5.setMinimumIntegerDigits(1);

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			List<QuoteItem> items = quote.getQuoteItems();
			int terms = (int) ('A');
			HashMap<String, String> termsAndConditions = new HashMap<String, String>();
			// Defect #4
			wb = getHSSFWorkbook(quote.getBizUnit().getName());
			// style definition:

			HSSFFont whiteStyle = wb.createFont();
			whiteStyle.setBoldweight((short) 10);
			whiteStyle.setColor(HSSFColor.WHITE.index);

			HSSFFont normalStyle = wb.createFont();
			normalStyle.setFontHeightInPoints((short) 10); // PMD bug cannot be
			normalStyle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); // Fixed

			HSSFFont redBoldStyle = wb.createFont();
			redBoldStyle.setFontHeightInPoints((short) 10); // PMD bug cannot be
															// Fixed
			redBoldStyle.setColor(RED.index);
			redBoldStyle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

			// ======================================
			HSSFCellStyle redFontPinkBackWrapStyle = wb.createCellStyle();
			redFontPinkBackWrapStyle.setWrapText(true);
			redFontPinkBackWrapStyle.setFillForegroundColor(PINK.index);
			redFontPinkBackWrapStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			redFontPinkBackWrapStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			redFontPinkBackWrapStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			redFontPinkBackWrapStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			redFontPinkBackWrapStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			redFontPinkBackWrapStyle.setFont(redBoldStyle);
			redFontPinkBackWrapStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			HSSFCellStyle redFontPinkBackStyle = wb.createCellStyle();
			redFontPinkBackStyle.setFillForegroundColor(PINK.index);
			redFontPinkBackStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			redFontPinkBackStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			redFontPinkBackStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			redFontPinkBackStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			redFontPinkBackStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			redFontPinkBackStyle.setFont(redBoldStyle);
			redFontPinkBackStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			HSSFCellStyle normalFontPinkBackStyle = wb.createCellStyle();
			normalFontPinkBackStyle.setFillForegroundColor(PINK.index);
			normalFontPinkBackStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			normalFontPinkBackStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			normalFontPinkBackStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			normalFontPinkBackStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			normalFontPinkBackStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			normalFontPinkBackStyle.setFont(normalStyle);
			normalFontPinkBackStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			// ======================================
			// set cellStyle cell header
			HSSFCellStyle cellHeader = wb.createCellStyle();
			cellHeader.setWrapText(true);
			cellHeader.setFillForegroundColor(HSSFColor.SEA_GREEN.index);
			cellHeader.setFillPattern(CellStyle.SOLID_FOREGROUND);
			cellHeader.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			cellHeader.setBorderRight(HSSFCellStyle.BORDER_THIN);
			cellHeader.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			cellHeader.setBorderTop(HSSFCellStyle.BORDER_THIN);
			cellHeader.setFont(whiteStyle);
			cellHeader.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			HSSFCellStyle wrapNormalStyle = wb.createCellStyle();
			wrapNormalStyle.setWrapText(true);
			wrapNormalStyle.setFillForegroundColor(WHITE.index);
			wrapNormalStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			wrapNormalStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			wrapNormalStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			wrapNormalStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			wrapNormalStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			wrapNormalStyle.setFont(normalStyle);
			wrapNormalStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			HSSFCellStyle blueCellStyle = wb.createCellStyle();
			blueCellStyle.setFillForegroundColor(LIME.index);
			blueCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			blueCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			blueCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			blueCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			blueCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			blueCellStyle.setFont(normalStyle);
			blueCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			HSSFCellStyle redFontWhiteBackStyle = wb.createCellStyle();
			redFontWhiteBackStyle.setFillForegroundColor(WHITE.index);
			redFontWhiteBackStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			redFontWhiteBackStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			redFontWhiteBackStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			redFontWhiteBackStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			redFontWhiteBackStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			redFontWhiteBackStyle.setFont(redBoldStyle);
			redFontWhiteBackStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			HSSFCellStyle redFontBlueBackStyle = wb.createCellStyle();
			redFontBlueBackStyle.setFillForegroundColor(LIME.index);
			redFontBlueBackStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			redFontBlueBackStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			redFontBlueBackStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			redFontBlueBackStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			redFontBlueBackStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			redFontBlueBackStyle.setFont(redBoldStyle);
			redFontBlueBackStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			HSSFCellStyle redFontBlueBackWrapStyle = wb.createCellStyle();
			redFontBlueBackWrapStyle.setWrapText(true);
			redFontBlueBackWrapStyle.setFillForegroundColor(LIME.index);
			redFontBlueBackWrapStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			redFontBlueBackWrapStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			redFontBlueBackWrapStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			redFontBlueBackWrapStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			redFontBlueBackWrapStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			redFontBlueBackWrapStyle.setFont(redBoldStyle);
			redFontBlueBackWrapStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			HSSFCellStyle normalFontBlueBackStyle = wb.createCellStyle();
			normalFontBlueBackStyle.setWrapText(true);
			normalFontBlueBackStyle.setFillForegroundColor(LIME.index);
			normalFontBlueBackStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			normalFontBlueBackStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			normalFontBlueBackStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			normalFontBlueBackStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			normalFontBlueBackStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			normalFontBlueBackStyle.setFont(normalStyle);
			normalFontBlueBackStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			HSSFPalette palette = wb.getCustomPalette();
			// palette.setColorAtIndex(LIME.index, (byte) 204, (byte) 255,
			// (byte) 255);
			palette.setColorAtIndex(LIME.index, (byte) 204, (byte) 255, (byte) 204);
			palette.setColorAtIndex(PINK.index, (byte) 242, (byte) 220, (byte) 219);
			// change cellhead color
			palette.setColorAtIndex(HSSFColor.SEA_GREEN.index, (byte) 56, (byte) 175, (byte) 87);

			HSSFSheet sheet = wb.getSheet("Quotation");
			fillRateToXLS(quote, sheet); // added by June to
											// get latest
											// exchange rate
											// info according to
											// the item is
											// highlighted and
											// quote is not
											// expiried
			// before i< 10
			for (int i = 0; i < 13; i++) {
				for (int j = 0; j <= QuoteConstant.QUOTE_TEMPLATE_HEADER_END; j++) {
					String position = QuoteUtil.convertExcelPositionToString(i, j);
					HSSFRow row = sheet.getRow(j);
					HSSFCell cell = row.getCell(i);
					switch (position) {
					case "E12":
						cell.setCellStyle(cellHeader);
						break;
					case "E3":
						// note: will the quote item SubmissionDate if
						// attachment from WorkPlatform and Post Quotation Task
						if (postQuotationTask instanceof CommonBean) {
							if (quote.getSubmissionDate() != null)
								cell.setCellValue(sdf.format(quote.getSubmissionDate()));
						} else {
							setE3Value(quote, cell);
						}
						break;
					case "E5":
						cell.setCellValue(sdf.format(QuoteUtil.getCurrentTime()));
						// cell.setCellStyle(blueCellStyle);
						break;
					case "F7":
						cell.setCellValue(quote.getFormNumber());
						break;
					case "H7":
						cell.setCellValue(quote.getYourReference());
						break;
					case "J7":
						if (quote.getSales() != null && quote.getSales().getTeam() != null) {
							cell.setCellValue(quote.getSales().getTeam().getName());
						}
						break;
					case "F8":

						String customerName = quote.getSoldToCustomerName();
						cell.setCellValue(customerName + " (" + soldToCustomer.getCustomerNumber() + ")");
						break;
					case "J8":
						cell.setCellValue(quote.getSales() == null ? null : quote.getSales().getName());
						break;
					
					 * case "H8": if (quote.getSales() != null &&
					 * quote.getSales().getTeam() != null) {
					 * cell.setCellValue(quote.getSales().getTeam().getName());
					 * } break;
					 
					
					 * case "J8": if (quote.getSales() != null) {
					 * cell.setCellValue(quote.getSales().getEmployeeId()); }
					 * break;
					 
					
					 * case "F9": // INC0018065, INC0018819 // String
					 * customerName = //
					 * DownloadUtil.getCustomerFullName(soldToCustomer); String
					 * customerName = quote.getSoldToCustomerName();
					 * cell.setCellValue(customerName + " (" +
					 * soldToCustomer.getCustomerNumber() + ")"); break;
					 
					case "F9":
						String totalShipTo = "";
						if (items != null) {
							for (QuoteItem item : items) {
								if (item.getShipToCustomer() != null) {
									String shipToCustomerName = DownloadUtil
											.getCustomerFullName(item.getShipToCustomer());
									shipToCustomerName += " (" + item.getShipToCustomer().getCustomerNumber() + ")";
									if (totalShipTo.indexOf(shipToCustomerName) == -1) {
										if (totalShipTo.length() > 0)
											totalShipTo += ", ";
										totalShipTo += shipToCustomerName;
									}
								} else if (item.getShipToCustomerName() != null) {
									if (totalShipTo.indexOf(item.getShipToCustomerName()) == -1) {
										if (totalShipTo.length() > 0)
											totalShipTo += ", ";
										totalShipTo += item.getShipToCustomerName();
									}
								}
							}
						}
						cell.setCellValue(totalShipTo);
						break;
					case "J9":
						if (quote.getSales() != null) {
							cell.setCellValue(quote.getSales().getEmployeeId());
						}
						break;

					case "F10":
						String totalEnd = "";
						if (items != null) {
							for (QuoteItem item : items) {
								if (item.getEndCustomer() != null) {
									String endCustomerName = DownloadUtil.getCustomerFullName(item.getEndCustomer());
									endCustomerName += " (" + item.getEndCustomer().getCustomerNumber() + ")";
									if (totalEnd.indexOf(endCustomerName) == -1) {
										if (totalEnd.length() > 0)
											totalEnd += ", ";
										totalEnd += endCustomerName;
									}
								} else if (item.getEndCustomerName() != null) {
									if (totalEnd.indexOf(item.getEndCustomerName()) == -1) {
										if (totalEnd.length() > 0)
											totalEnd += ", ";
										totalEnd += item.getEndCustomerName();
									}
								}
							}
						}
						cell.setCellValue(totalEnd);
						break;
					case "J10":
						cell.setCellValue(quote.getRemarks());
						break;
					
					 * case "F12": // INC0018065, INC0018819 //
					 * cell.setCellValue(soldToCustomer.getCustomerType());
					 * cell.setCellValue(quote.getCustomerType()); break; case
					 * "H12": String enquirySegment = ""; if (items != null) {
					 * for (QuoteItem item : items) { if
					 * (item.getEnquirySegment() != null) { if
					 * (enquirySegment.indexOf(item.getEnquirySegment()) == -1)
					 * { if (enquirySegment.length() > 0) enquirySegment +=
					 * ", "; enquirySegment += item.getEnquirySegment(); } } } }
					 * cell.setCellValue(enquirySegment); break; case "F13":
					 * String projectName = ""; if (items != null) { for
					 * (QuoteItem item : items) { if (item.getProjectName() !=
					 * null) { if (projectName.indexOf(item.getProjectName()) ==
					 * -1) { if (projectName.length() > 0) projectName += ", ";
					 * projectName += item.getProjectName(); } } } }
					 * cell.setCellValue(projectName); break; case "H13": String
					 * application = ""; if (items != null) { for (QuoteItem
					 * item : items) { if (item.getApplication() != null) { if
					 * (application.indexOf(item.getApplication()) == -1) { if
					 * (application.length() > 0) application += ", ";
					 * application += item.getApplication(); } } } }
					 * cell.setCellValue(application); break; case "F14": String
					 * designLocation = ""; if (items != null) { for (QuoteItem
					 * item : items) { if (item.getDesignLocation() != null) {
					 * if (designLocation.indexOf(item.getDesignLocation()) ==
					 * -1) { if (designLocation.length() > 0) designLocation +=
					 * ", "; designLocation += item.getDesignLocation(); } } } }
					 * cell.setCellValue(designLocation); break; case "F15": //
					 * WebQuote 2.2 enhancement : fields changes. // String ardc
					 * = ""; // if(items != null){ // for(QuoteItem item :
					 * items){ // if(item.getArdc() == null || item.getArdc() !=
					 * null // && !item.getArdc().booleanValue()){ //
					 * if(ardc.equals(QuoteConstant.OPTION_YES)){ // ardc =
					 * QuoteSBConstant.MULTIPLE_SELECTED; // break; // } // ardc
					 * = QuoteConstant.OPTION_NO; // } else { //
					 * if(ardc.equals(QuoteConstant.OPTION_NO)){ // ardc =
					 * QuoteSBConstant.MULTIPLE_SELECTED; // break; // } // ardc
					 * = QuoteConstant.OPTION_YES; // } // } // } //
					 * cell.setCellValue(ardc); // break; String designInCat =
					 * ""; if (items != null) { for (QuoteItem item : items) {
					 * if (item.getDesignInCat() != null) { if
					 * (designInCat.indexOf(item.getDesignInCat()) == -1) { if
					 * (designInCat.length() > 0) designInCat += ", ";
					 * designInCat += item.getDesignInCat(); } } } }
					 * cell.setCellValue(designInCat); break; case "H15": String
					 * drmsNumber = ""; if (items != null) { for (QuoteItem item
					 * : items) { if (item.getDrmsNumber() != null) { if
					 * (drmsNumber.indexOf(String.valueOf(item.getDrmsNumber()))
					 * == -1) { if (drmsNumber.length() > 0) drmsNumber += ", ";
					 * drmsNumber += item.getDrmsNumber(); } } } }
					 * cell.setCellValue(drmsNumber); break; case "F16": String
					 * ppSchedule = ""; if (items != null) { for (QuoteItem item
					 * : items) { if (item.getPpSchedule() != null) { if
					 * (ppSchedule.indexOf(item.getPpSchedule()) == -1) { if
					 * (ppSchedule.length() > 0) ppSchedule += ", "; ppSchedule
					 * += item.getPpSchedule(); } } } }
					 * cell.setCellValue(ppSchedule); break; case "H16": String
					 * mpSchedule = ""; if (items != null) { for (QuoteItem item
					 * : items) { if (item.getMpSchedule() != null) { if
					 * (mpSchedule.indexOf(item.getMpSchedule()) == -1) { if
					 * (mpSchedule.length() > 0) mpSchedule += ", "; mpSchedule
					 * += item.getMpSchedule(); } } } }
					 * cell.setCellValue(mpSchedule); break; case "F17": //
					 * WebQuote 2.2 enhancement : fields changes. String
					 * quoteType = ""; if (items != null) { for (QuoteItem item
					 * : items) { if (item.getQuoteType() != null) { if
					 * (quoteType.indexOf(item.getQuoteType()) == -1) { if
					 * (quoteType.length() > 0) quoteType += ", "; quoteType +=
					 * item.getQuoteType(); } } } }
					 * cell.setCellValue(quoteType); break; case "H17": String
					 * loa = ""; if (items != null) { for (QuoteItem item :
					 * items) { if (item.getLoaFlag() == null ||
					 * item.getLoaFlag() != null &&
					 * !item.getLoaFlag().booleanValue()) { if
					 * (loa.equals(QuoteConstant.OPTION_YES)) { loa =
					 * QuoteSBConstant.MULTIPLE_SELECTED; break; } loa =
					 * QuoteConstant.OPTION_NO; } else { if
					 * (loa.equals(QuoteConstant.OPTION_NO)) { loa =
					 * QuoteSBConstant.MULTIPLE_SELECTED; break; } loa =
					 * QuoteConstant.OPTION_YES; } } } cell.setCellValue(loa);
					 * break; // WebQuote 2.2 enhancement : fields changes. //
					 * case "F18": // String orderOnHand = ""; // if(items !=
					 * null){ // for(QuoteItem item : items){ //
					 * if(item.getOrderOnHandFlag() == null || //
					 * item.getOrderOnHandFlag() != null && //
					 * item.getOrderOnHandFlag().booleanValue()){ //
					 * if(orderOnHand.equals(QuoteConstant.OPTION_YES)){ //
					 * orderOnHand = QuoteSBConstant.MULTIPLE_SELECTED; //
					 * break; // } // orderOnHand = QuoteConstant.OPTION_NO; //
					 * } else { //
					 * if(orderOnHand.equals(QuoteConstant.OPTION_NO)){ //
					 * orderOnHand = QuoteSBConstant.MULTIPLE_SELECTED; //
					 * break; // } // orderOnHand = QuoteConstant.OPTION_YES; //
					 * } // } // } // cell.setCellValue(orderOnHand); // break;
					 * // case "H18": // //cell.setCellValue(); // break; case
					 * "F18": cell.setCellValue(quote.getCopyToCS()); break;
					 * case "H18": if (quote != null) { String attachmentStr =
					 * ""; List<Attachment> attachments =
					 * quote.getAttachments(); for (Attachment attachment :
					 * attachments) { if (attachmentStr.length() > 0)
					 * attachmentStr += ", "; attachmentStr +=
					 * attachment.getFileName(); }
					 * cell.setCellValue(attachmentStr); } break; case "I18": //
					 * cell.setCellValue(); break; case "J18": //
					 * cell.setCellValue(); break; case "F19":
					 * cell.setCellValue(quote.getRemarks()); break; case "G19":
					 * // cell.setCellValue(); break; case "F20":
					 * cell.setCellValue(quote.getRemarksToCustomer()); break;
					 * case "G20": // cell.setCellValue(); break;
					 
					}
				}
			}
			List<QuoteItem> quoteItems = quote.getQuoteItems();
			int termsIndex = QuoteConstant.QUOTE_ITEM_ROW_START + 3;
			if (quoteItems.size() > 1) {
				for (int i = 1; i < quoteItems.size(); i++) {
					QuoteUtil.copyRow(sheet, QuoteConstant.QUOTE_ITEM_ROW_START,
							QuoteConstant.QUOTE_ITEM_ROW_START + i);
					termsIndex = QuoteConstant.QUOTE_ITEM_ROW_START + i + 3;
				}
			}

			HSSFCellStyle quotedStyle = wb.createCellStyle();
			quotedStyle.setFillBackgroundColor(AQUA.index);

			// added by June for RMB cur project 20140717 to control column
			for (int i = 0; i < quoteItems.size(); i++) {
				QuoteItem quoteItem = quoteItems.get(i);
				int j = 2;
				HSSFRow row = sheet.getRow(QuoteConstant.QUOTE_ITEM_ROW_START + i);

				HSSFCell cellno = row.getCell(j++); // changed the 2 to j++
				HSSFCell cellQuoteNumber = row.getCell(j++);
				// edit
				HSSFCell cellQuoteStage = row.getCell(j++);
				// HSSFCell cell = row.getCell(j++);
				// HSSFCell cellFirstRfqCode = row.getCell(j++);
				HSSFCell cellMfr = row.getCell(j++);
				HSSFCell cellMfrName = row.getCell(j++);
				HSSFCell cellRequestedPartNumber = row.getCell(j++);
				HSSFCell cellQuotedPartNumber = row.getCell(j++);

				HSSFCell cellAqcc = row.getCell(j++);
				// cellAqcc.setCellStyle(wrapNormalStyle);
				// sales cost
				HSSFCell cellSaleCost = row.getCell(j++);
				// suggested_resale
				HSSFCell cellSuggestedResale = row.getCell(j++);
				HSSFCell cellQuotedPrice = row.getCell(j++);
				HSSFCell cellTargetPrice = row.getCell(j++);
				// finalQuotationPrice
				HSSFCell cellFinalQuotationPrice = row.getCell(j++);
				// EffectiveDate
				HSSFCell cellEffectiveDate = row.getCell(j++);
				// Quote Expiry Date
				HSSFCell cellQuoteExpiryDate = row.getCell(j++);
				HSSFCell cellShipmentValidity = row.getCell(j++);
				HSSFCell cellTermsAndConditions = row.getCell(j++);
				HSSFCell cellPmoq = row.getCell(j++);
				HSSFCell cellMpq = row.getCell(j++);
				HSSFCell cellMoq = row.getCell(j++);
				HSSFCell cellLeadTime = row.getCell(j++);
				HSSFCell cellAllocationFlag = row.getCell(j++);
				HSSFCell cellRescheduleWindow = row.getCell(j++);
				HSSFCell cellCancellationWindow = row.getCell(j++);
				HSSFCell cellRequiredQty = row.getCell(j++);
				HSSFCell cellEau = row.getCell(j++);
				HSSFCell cellSoldTo = row.getCell(j++);
				HSSFCell cellShipTo = row.getCell(j++);
				HSSFCell cellEndCustomer = row.getCell(j++);
				HSSFCell cellItemRemarks = row.getCell(j++);

				// added by June for project RMB cur 20140717 --start
				// HSSFCell cell_exCurrency = row.getCell(j++);
				// HSSFCell cell_exRate = row.getCell(j++);
				// added by June for project RMB cur 20140717 --end

				// cellAqcc.setCellStyle(wrapNormalStyle);

				if (quoteItem.isHightlighted()) {
					cellQuoteNumber.setCellStyle(normalFontBlueBackStyle);
					cellQuoteStage.setCellStyle(normalFontBlueBackStyle);
					// cellFirstRfqCode.setCellStyle(normalFontBlueBackStyle);
					cellMfr.setCellStyle(normalFontBlueBackStyle);
					cellMfrName.setCellStyle(normalFontBlueBackStyle);
					cellRequestedPartNumber.setCellStyle(normalFontBlueBackStyle);
					cellQuotedPartNumber.setCellStyle(normalFontBlueBackStyle);

					cellAqcc.setCellStyle(normalFontBlueBackStyle);
					// sales costredFontBlueBackStyle
					cellSaleCost.setCellStyle(normalFontBlueBackStyle);
					// suggested_resale
					cellSuggestedResale.setCellStyle(normalFontBlueBackStyle);
					cellQuotedPrice.setCellStyle(normalFontBlueBackStyle);
					cellTargetPrice.setCellStyle(normalFontBlueBackStyle);
					// finalQuotationPrice
					cellFinalQuotationPrice.setCellStyle(normalFontBlueBackStyle);
					// replaced by
					// cellEffectiveDate.setCellStyle(normalFontBlueBackStyle);,
					// DamonChen@20190918
					
					 * if (postQuotationTask instanceof PostQuotationTask ||
					 * postQuotationTask instanceof CommonBean) {
					 * cellEffectiveDate.setCellStyle(normalFontBlueBackStyle);
					 * } else if (postQuotationTask instanceof
					 * AsyncPostQuotationSB) {
					 * cellEffectiveDate.setCellStyle(normalFontBlueBackStyle);
					 * }
					 
					cellEffectiveDate.setCellStyle(normalFontBlueBackStyle);
					cellQuoteExpiryDate.setCellStyle(normalFontBlueBackStyle);
					cellShipmentValidity.setCellStyle(normalFontBlueBackStyle);
					cellTermsAndConditions.setCellStyle(normalFontBlueBackStyle);
					cellPmoq.setCellStyle(normalFontBlueBackStyle);
					cellMpq.setCellStyle(normalFontBlueBackStyle);
					cellMoq.setCellStyle(normalFontBlueBackStyle);
					cellLeadTime.setCellStyle(normalFontBlueBackStyle);
					cellAllocationFlag.setCellStyle(normalFontBlueBackStyle);
					cellRescheduleWindow.setCellStyle(normalFontBlueBackStyle);
					// cellCancellationWindow.setCellStyle(redFontBlueBackStyle);
					cellCancellationWindow.setCellStyle(normalFontBlueBackStyle);
					cellRequiredQty.setCellStyle(normalFontBlueBackStyle);
					cellEau.setCellStyle(normalFontBlueBackStyle);
					cellSoldTo.setCellStyle(normalFontBlueBackStyle);
					cellShipTo.setCellStyle(normalFontBlueBackStyle);
					cellEndCustomer.setCellStyle(normalFontBlueBackStyle);
					cellItemRemarks.setCellStyle(normalFontBlueBackStyle);

					// cell_exCurrency.setCellStyle(redFontBlueBackStyle);
					// //added by June 20140717
					// cell_exRate.setCellStyle(redFontBlueBackStyle); //added
					// by June 20140717

				}

				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_DRAFT)) {
					cellQuoteNumber.setCellStyle(redFontPinkBackStyle);
					cellQuoteStage.setCellStyle(redFontPinkBackStyle);
					// cellFirstRfqCode.setCellStyle(normalFontPinkBackStyle);
					cellMfr.setCellStyle(normalFontPinkBackStyle);
					cellMfrName.setCellStyle(normalFontPinkBackStyle);
					cellRequestedPartNumber.setCellStyle(normalFontPinkBackStyle);
					cellQuotedPartNumber.setCellStyle(redFontPinkBackStyle);

					cellAqcc.setCellStyle(redFontPinkBackWrapStyle);
					// sales cost
					cellSaleCost.setCellStyle(normalFontPinkBackStyle);
					// suggested_resale
					cellSuggestedResale.setCellStyle(normalFontPinkBackStyle);
					cellQuotedPrice.setCellStyle(redFontPinkBackStyle);
					cellTargetPrice.setCellStyle(normalFontPinkBackStyle);
					// finalQuotationPrice
					cellFinalQuotationPrice.setCellStyle(normalFontPinkBackStyle);
					cellEffectiveDate.setCellStyle(redFontPinkBackStyle);
					cellQuoteExpiryDate.setCellStyle(redFontPinkBackStyle);
					cellShipmentValidity.setCellStyle(redFontPinkBackStyle);
					cellTermsAndConditions.setCellStyle(redFontPinkBackWrapStyle);
					cellPmoq.setCellStyle(redFontPinkBackStyle);
					cellMpq.setCellStyle(redFontPinkBackStyle);
					cellMoq.setCellStyle(redFontPinkBackStyle);
					cellLeadTime.setCellStyle(redFontPinkBackStyle);
					cellAllocationFlag.setCellStyle(redFontPinkBackStyle);
					cellRescheduleWindow.setCellStyle(redFontPinkBackStyle);
					cellCancellationWindow.setCellStyle(redFontPinkBackStyle);
					cellRequiredQty.setCellStyle(normalFontPinkBackStyle);
					cellEau.setCellStyle(normalFontPinkBackStyle);
					cellSoldTo.setCellStyle(normalFontPinkBackStyle);
					cellShipTo.setCellStyle(normalFontPinkBackStyle);
					cellEndCustomer.setCellStyle(normalFontPinkBackStyle);
					cellItemRemarks.setCellStyle(normalFontPinkBackStyle);

					// cell_exCurrency.setCellStyle(redFontPinkBackStyle);
					// //added by June 20140717
					// cell_exRate.setCellStyle(redFontPinkBackStyle); //added
					// by June 20140717

				}

				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)) {
					cellno.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cellQuoteStage.setCellValue(QuoteConstant.DEFAULT_BLANK);
					// cellFirstRfqCode.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cellMfr.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cellMfrName.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cellRequestedPartNumber.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cellQuotedPartNumber.setCellValue(QuoteConstant.DEFAULT_BLANK);

					cellAqcc.setCellValue(QuoteConstant.DEFAULT_BLANK);
					// sales cost
					cellSaleCost.setCellValue(QuoteConstant.DEFAULT_BLANK);
					// suggested_resale
					cellSuggestedResale.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cellQuotedPrice.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cellTargetPrice.setCellValue(QuoteConstant.DEFAULT_BLANK);
					// finalQuotationPrice
					cellFinalQuotationPrice.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cellEffectiveDate.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cellQuoteExpiryDate.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cellShipmentValidity.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cellTermsAndConditions.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cellPmoq.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cellMpq.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cellMoq.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cellLeadTime.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cellAllocationFlag.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cellRescheduleWindow.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cellCancellationWindow.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cellRequiredQty.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cellSoldTo.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cellShipTo.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cellEndCustomer.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cellItemRemarks.setCellValue(QuoteConstant.DEFAULT_BLANK);

					// cell_exCurrency.setCellValue(QuoteConstant.DEFAULT_BLANK);
					// //added by June 20140717
					// cell_exRate.setCellValue(QuoteConstant.DEFAULT_BLANK);
					// //added by June 20140717

				}

				cellno.setCellValue("No." + (i + 1));
				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)) {
					cellQuoteNumber.setCellValue(quoteItem.getQuoteNumber());
				}

				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)) {
					cellQuoteStage.setCellValue(quoteItem.getStage());
				}

				
				 * if (quoteItem.getStage() != null &&
				 * quoteItem.getStage().equals(QuoteSBConstant.
				 * QUOTE_STAGE_FINISH)) {
				 * cellFirstRfqCode.setCellValue(quoteItem.getFirstRfqCode()); }
				 

				if (quoteItem.getRequestedMfr() != null) {
					cellMfr.setCellValue(quoteItem.getRequestedMfr().getName());
					String mfrDescription = quoteItem.getRequestedMfr().getDescription();
					if (mfrDescription != null) {
						String sub = quoteItem.getRequestedMfr().getName() + " ";
						if (mfrDescription.startsWith(sub)) {
							cellMfrName
									.setCellValue(quoteItem.getRequestedMfr().getDescription().substring(sub.length()));
						}
					}
				}

				if (quoteItem.getRequestedPartNumber() != null) {
					cellRequestedPartNumber.setCellValue(quoteItem.getRequestedPartNumber());
				}
				// else {
				// cell_requestedPartNumber.setCellValue(quoteItem.getInvalidPartNumber());
				// }

				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)) {
					if (!QuoteUtil.isEmpty(quoteItem.getQuotedPartNumber())) {
						cellQuotedPartNumber.setCellValue(quoteItem.getQuotedPartNumber());
					} else {
						LOGGER.log(Level.WARNING, "QuotedMaterial is missing in " + quoteItem.getQuoteNumber());
					}
				}

				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_PENDING)) {
					cellAqcc.setCellValue("In Progress");
				} else if (QuoteUtil.isEmpty(quoteItem.getAqcc())) {
					cellAqcc.setCellValue(QuoteConstant.OPTION_NO);
				} else {
					cellAqcc.setCellValue(quoteItem.getAqcc());
				}
				// set values for sales cost
				if (quoteItem.getStage() != null && quoteItem.getSalesCost() != null && quoteItem.isSalesCostFlag()) {
					cellSaleCost.setCellValue(DF5.format(quoteItem.getSalesCost()));
				}
				// suggested_resale
				if (quoteItem.getStage() != null && quoteItem.isSalesCostFlag()
						&& quoteItem.getSuggestedResale() != null) {
					cellSuggestedResale.setCellValue(DF5.format(quoteItem.getSuggestedResale()));
				}
				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)
						&& quoteItem.getQuotedPrice() != null)
					cellQuotedPrice.setCellValue(DF5.format(quoteItem.getQuotedPrice()));
				if (quoteItem.getTargetResale() != null)
					cellTargetPrice.setCellValue(DF5.format(quoteItem.getTargetResale()));
				// fix 1054 , fixed by Tonmy on 22, Nov 2013
				// finalQuotationPrice
				if (quoteItem.getFinalQuotationPrice() != null)
					cellFinalQuotationPrice.setCellValue(DF5.format(quoteItem.getFinalQuotationPrice()));
				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)
						&& quoteItem.getQuotationEffectiveDate() != null) {
					cellEffectiveDate.setCellValue(sdf.format(quoteItem.getQuotationEffectiveDate()));
				}
				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_DRAFT)) {
					// cell_aqcc.setCellStyle(redFontWhiteBackWrapStyle);
					cellAqcc.setCellValue(QuoteSBConstant.DEFAULT_QC_EXTERNAL_COMMENT);
				}

				String requestQty = "";
				if (quoteItem.getRequestedQty() != null)
					requestQty = String.valueOf(quoteItem.getRequestedQty());

				cellRequiredQty.setCellValue(requestQty);
				String eau = "";
				if (quoteItem.getEau() != null)
					eau = String.valueOf(quoteItem.getEau());
				else
					eau = QuoteConstant.DEFAULT_BLANK;
				cellEau.setCellValue(eau);

				// added by June 2014074 for project RMB cur start
				
				 * if(quoteItem.getStage() != null && quoteItem.getCurrTo() !=
				 * null) cell_exCurrency.setCellValue(quoteItem.getCurrTo());
				 * 
				 * if(quoteItem.getStage() != null && quoteItem.getExRateTo() !=
				 * null && quoteItem.getVat() != null&& quoteItem.getHandling()
				 * != null)
				 * cell_exRate.setCellValue(df5.format(quoteItem.getExRateTo().
				 * multiply(quoteItem.getVat()).multiply(quoteItem.getHandling()
				 * ).setScale(5,RoundingMode.HALF_UP)));
				  // =-------end-------------------------------------

				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)
						&& quoteItem.getPmoq() != null)
					cellPmoq.setCellValue(quoteItem.getPmoq());

				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)
						&& quoteItem.getMpq() != null)
					cellMpq.setCellValue(quoteItem.getMpq());

				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)
						&& quoteItem.getMoq() != null)
					cellMoq.setCellValue(quoteItem.getMoq());

				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)
						&& quoteItem.getLeadTime() != null) {
					String leadTime = quoteItem.getLeadTime();
					if (leadTime.trim().length() <= 5 && !leadTime.toLowerCase().endsWith("wks")
							&& (!leadTime.toUpperCase().contains("STOCK") && !leadTime.toUpperCase().contains("STK"))) {
						leadTime += " wks";
					}
					cellLeadTime.setCellValue(leadTime);
				}

				// setprivalidity Price Validity to Quote Expiry Date.
				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)
						&& quoteItem.getQuoteExpiryDate() != null) {
					cellQuoteExpiryDate.setCellValue(sdf.format(quoteItem.getQuoteExpiryDate()));
				}
				// cell_shipmentValidity.setCellValue(sdf.format(quoteItem.getShipmentValidity()));
				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)
						&& quoteItem.getShipmentValidity() != null)
					cellShipmentValidity.setCellValue(sdf.format(quoteItem.getShipmentValidity()));

				
				 * if (quoteItem.getStage() != null &&
				 * quoteItem.getStage().equals(QuoteSBConstant.
				 * QUOTE_STAGE_FINISH) && quoteItem.getPriceValidity() != null)
				 * { Date validity = null; if
				 * (quoteItem.getPriceValidity().matches("[0-9]{1,}")) { int
				 * shift = Integer.parseInt(quoteItem.getPriceValidity());
				 * validity = QuoteUtil.shiftDate(quoteItem.getSentOutTime(),
				 * shift); } else { try { validity =
				 * sdf.parse(quoteItem.getPriceValidity()); } catch
				 * (ParseException e) { LOGGER.log(Level.SEVERE,
				 * "Exception in parsing date : " + quoteItem.getPriceValidity()
				 * + " , exception message : " + e.getMessage(), e); } } if
				 * (validity != null) { if (postQuotationTask instanceof
				 * CommonBean) { Date quoteExpiryDate = null; // andy modify 2.2
				 * Material restructure and // quote_item // delinkage if
				 * (QuoteSBConstant.MATERIAL_TYPE_NORMAL.equals(quoteItem.
				 * getMaterialTypeId())) { quoteExpiryDate =
				 * QuoteUtil.calculateQuoteExpiryDate(validity,
				 * QuoteSBConstant.MATERIAL_TYPE_NORMAL); } else if
				 * (QuoteSBConstant.MATERIAL_TYPE_PROGRAM.equals(quoteItem.
				 * getMaterialTypeId())) { quoteExpiryDate =
				 * QuoteUtil.calculateQuoteExpiryDate(validity,
				 * QuoteSBConstant.MATERIAL_TYPE_PROGRAM); } else {
				 * quoteExpiryDate =
				 * QuoteUtil.calculateQuoteExpiryDate(validity,
				 * QuoteSBConstant.MATERIAL_TYPE_NORMAL); }
				 * cellPriceValidity.setCellValue(sdf.format(quoteExpiryDate));
				 * } else { // set price validity. changed By June for RMB cur
				 * // project 20140711 ---start if (isUseExpiryDate) { // only
				 * for RMB CUR PROJECT // if the item's stage is finish .. and
				 * quote // expiry date is not null .. then use this // quote
				 * expiry date else.. keep the currently // logic ??? if
				 * (quoteItem.getStage() != null &&
				 * quoteItem.getStage().equals(QuoteSBConstant.
				 * QUOTE_STAGE_FINISH) && quoteItem.getQuoteExpiryDate() !=
				 * null) { Date quoteExpiryDate2 =
				 * quoteItem.getQuoteExpiryDate();
				 * cellPriceValidity.setCellValue(sdf.format(quoteExpiryDate2));
				 * } else { Date quoteExpiryDate2 =
				 * calculatePriceValidty(quoteItem); if (null !=
				 * quoteExpiryDate2)
				 * cellPriceValidity.setCellValue(sdf.format(quoteExpiryDate2));
				 * } } else { Date quoteExpiryDate2 = null; //
				 * if(quoteItem.getMaterialType() != null && //
				 * quoteItem.getMaterialType().getName().equals(QuoteSBConstant.
				 * MATERIAL_TYPE_NORMAL)){ // quoteExpiryDate = //
				 * QuoteUtil.calculateQuoteExpiryDate(validity, //
				 * QuoteSBConstant.MATERIAL_TYPE_NORMAL); // } else
				 * if(quoteItem.getMaterialType() != null // && //
				 * quoteItem.getMaterialType().getName().equals(QuoteSBConstant.
				 * MATERIAL_TYPE_PROGRAM)){ // quoteExpiryDate = //
				 * QuoteUtil.calculateQuoteExpiryDate(validity, //
				 * QuoteSBConstant.MATERIAL_TYPE_PROGRAM); // } else { //
				 * quoteExpiryDate = //
				 * QuoteUtil.calculateQuoteExpiryDate(validity, //
				 * QuoteSBConstant.MATERIAL_TYPE_NORMAL); // } // Material
				 * restructure and quote_item // delinkage. changed on 10 Apr
				 * 2014. if (quoteItem.getMaterialTypeId() != null &&
				 * quoteItem.getMaterialTypeId().equals(QuoteSBConstant.
				 * MATERIAL_TYPE_NORMAL)) { quoteExpiryDate2 =
				 * QuoteUtil.calculateQuoteExpiryDate(validity,
				 * QuoteSBConstant.MATERIAL_TYPE_NORMAL); } else if
				 * (quoteItem.getMaterialTypeId() != null &&
				 * quoteItem.getMaterialTypeId()
				 * .equals(QuoteSBConstant.MATERIAL_TYPE_PROGRAM)) {
				 * quoteExpiryDate2 =
				 * QuoteUtil.calculateQuoteExpiryDate(validity,
				 * QuoteSBConstant.MATERIAL_TYPE_PROGRAM); } else {
				 * quoteExpiryDate2 =
				 * QuoteUtil.calculateQuoteExpiryDate(validity,
				 * QuoteSBConstant.MATERIAL_TYPE_NORMAL); }
				 * cellPriceValidity.setCellValue(sdf.format(quoteExpiryDate2));
				 * } } } }
				 * 
				 * if (quoteItem.getStage() != null &&
				 * quoteItem.getStage().equals(QuoteSBConstant.
				 * QUOTE_STAGE_FINISH) && quoteItem.getShipmentValidity() !=
				 * null) cellShipmentValidity.setCellValue(quoteItem.
				 * getShipmentValidityStr());
				 
				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)
						&& quoteItem.getTermsAndConditions() != null) {
					String quoteItemTermsAndCondition = quoteItem.getTermsAndConditions().trim()
							.replaceAll("[\\n\\r]+$", "");
					if (!quoteItemTermsAndCondition.equalsIgnoreCase("NO")) {
						String termsAndCondition = termsAndConditions.get(quoteItemTermsAndCondition);
						if (termsAndCondition == null) {
							termsAndConditions.put(quoteItemTermsAndCondition, "" + QuoteUtil.getTermId(terms));
							cellTermsAndConditions
									.setCellValue("Please refer to Special T&C " + QuoteUtil.getTermId(terms));
							terms++;
						} else {
							cellTermsAndConditions.setCellValue("Please refer to Special T&C "
									+ termsAndConditions.get(quoteItemTermsAndCondition));
						}

					}

				}

				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)
						&& quoteItem.getAllocationFlag() != null) {
					cellAllocationFlag.setCellValue(quoteItem.getAllocationFlag().booleanValue()
							? QuoteConstant.OPTION_YES : QuoteConstant.OPTION_NO);
				}

				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)
						&& quoteItem.getRescheduleWindow() != null) {
					if (0 != quoteItem.getRescheduleWindow()) { // Fix
																// INC0026994
																// add by June
						cellRescheduleWindow.setCellValue(quoteItem.getRescheduleWindow());
					}
				}

				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)
						&& quoteItem.getCancellationWindow() != null) {
					if (0 != quoteItem.getCancellationWindow()) { // Fix
																	// INC0026994
																	// add by
																	// June
																	// 20140903
						cellCancellationWindow.setCellValue(quoteItem.getCancellationWindow());
					}
				}

				if (quoteItem.getSoldToCustomer() != null) {
					// INC0018065, INC0018819
					// String customerName =
					// DownloadUtil.getCustomerFullName(quoteItem.getSoldToCustomer());
					String customerName = quote.getSoldToCustomerName();
					if (customerName != null)
						cellSoldTo.setCellValue(customerName);
				}

				if (quoteItem.getShipToCustomer() != null) {
					String customerName = DownloadUtil.getCustomerFullName(quoteItem.getShipToCustomer());
					if (customerName != null)
						cellShipTo.setCellValue(customerName);
				} else if (quoteItem.getShipToCustomerName() != null) {
					cellShipTo.setCellValue(quoteItem.getShipToCustomerName());
				}

				if (quoteItem.getEndCustomer() != null) {
					String customerName = DownloadUtil.getCustomerFullName(quoteItem.getEndCustomer());
					if (customerName != null)
						cellEndCustomer.setCellValue(customerName);
				} else if (quoteItem.getEndCustomerName() != null) {
					cellEndCustomer.setCellValue(quoteItem.getEndCustomerName());
				}

				if (quoteItem.getRemarks() != null)
					cellItemRemarks.setCellValue(quoteItem.getRemarks());

			}

			List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(
					termsAndConditions.entrySet());

			Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
				public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
					if (o1.getValue().toString().length() == o2.getValue().toString().length()) {
						return (o1.getValue()).toString().compareTo(o2.getValue());
					} else {
						return o1.getValue().toString().length() - o2.getValue().toString().length();
					}
				}
			});

			if (infoIds != null && infoIds.size() > 0) {
				String tc = "";
				for (int i = 0; i < infoIds.size(); i++) {
					Map.Entry<String, String> id = infoIds.get(i);
					tc += id.getValue() + ". " + id.getKey() + "\n";
				}

				if (tc.length() > 0) {
					HSSFRow row = sheet.getRow(termsIndex);
					HSSFCell cell = row.getCell(8);
					cell.setCellValue(tc);
				}
			}
		}
		return wb;
	}
*/
	public HSSFWorkbook getHSSFWorkbook(String bizUnitName) {
		FileInputStream fileIn;
		HSSFWorkbook wb = null;
		String fileInputStream = null;
		String templateName = null;
		try {
			// Defect #4
			templateName = sysMaintSB.getQuotationTemplate(bizUnitName);
			fileInputStream = DeploymentConfiguration.configPath + File.separator + templateName;

			/*
			 * //TODO try { String address =
			 * InetAddress.getLocalHost().getHostName().toString();
			 * if("cis2115vmts".equalsIgnoreCase(address)) { fileInputStream =
			 * "C:\\david\\sharefolder\\tempd"+File.separator+templateName; } }
			 * catch (Exception e) { e.printStackTrace(); }
			 */
			fileIn = new FileInputStream(fileInputStream);
			// fileIn = new
			// FileInputStream("C:/Users/046755/Desktop/excel/QuotationTemplate.xls");
			// fileIn = new FileInputStream("C:/excel/QuoteTemplate.xls");
			// fileIn = new FileInputStream("C:/excel/QuotationTemplate1.xls");
			wb = new HSSFWorkbook(fileIn);
		} catch (FileNotFoundException e) {
			LOGGER.log(Level.SEVERE, "exception in file not found for business unit : " + bizUnitName
					+ " , exception message : " + e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "exception in getting HSS work book found for business unit : " + bizUnitName
					+ " , exception message : " + e.getMessage(), e);
		}
		return wb;
	}

	public void setE3Value(Quote quote, HSSFCell cell) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		List<QuoteItem> quoteItems = quote.getQuoteItems();
		if (quoteItems != null) {
			Date submitDate = null;
			for (QuoteItem quoteItem : quoteItems) {
				if (quoteItem.getSubmissionDate() != null) {
					if (submitDate == null) {
						submitDate = quoteItem.getSubmissionDate();
					} else {
						if (submitDate.before(quoteItem.getSubmissionDate()))
							submitDate = quoteItem.getSubmissionDate();
					}
				}
			}
			if (submitDate != null)
				// cell.setCellValue(SDF.format(submitDate));
				cell.setCellValue(sdf.format(submitDate));
		}
	}

	public Date calculatePriceValidty(QuoteItem quoteItem) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		sdf.setTimeZone(OSTimeZone.getOsTimeZone());
		if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)
				&& quoteItem.getPriceValidity() != null) {
			Date validity = null;
			if (quoteItem.getPriceValidity().matches("[0-9]{1,}")) {
				int shift = Integer.parseInt(quoteItem.getPriceValidity());
				validity = QuoteUtil.shiftDate(quoteItem.getSentOutTime(), shift);
			} else {
				try {
					validity = sdf.parse(quoteItem.getPriceValidity());
				} catch (ParseException e) {
					LOGGER.log(Level.SEVERE, "EXception in parsing price validity date : "
							+ quoteItem.getPriceValidity() + ", exception message : " + e.getMessage(), e);
				}
			}
			if (validity != null) {
				Date quoteExpiryDate = null;
				if (quoteItem.getMaterialTypeId() != null
						&& quoteItem.getMaterialTypeId().equals(QuoteSBConstant.MATERIAL_TYPE_NORMAL)) {
					quoteExpiryDate = QuoteUtil.calculateQuoteExpiryDate(validity,
							QuoteSBConstant.MATERIAL_TYPE_NORMAL);
				} else if (quoteItem.getMaterialTypeId() != null
						&& quoteItem.getMaterialTypeId().equals(QuoteSBConstant.MATERIAL_TYPE_PROGRAM)) {
					quoteExpiryDate = QuoteUtil.calculateQuoteExpiryDate(validity,
							QuoteSBConstant.MATERIAL_TYPE_PROGRAM);
				} else {
					quoteExpiryDate = QuoteUtil.calculateQuoteExpiryDate(validity,
							QuoteSBConstant.MATERIAL_TYPE_NORMAL);
				}
				return quoteExpiryDate;
			}
		}
		return null;
	}

	/**
	 * Generate a report file.
	 * 
	 * @param resultList
	 * @param bzStr
	 * @return
	 */
	public HSSFWorkbook generateExcelFile(final HSSFWorkbook workBook, final String sheetName, final List resultList,
			final String bzStr) {
		LOGGER.fine("Generate excel file!");
		HSSFSheet sheet = workBook.createSheet(sheetName);
		int rowIndex = 0;
		sheet = com.avnet.emasia.webquote.web.quote.job.DownloadUtil.addSheetHeader(workBook, sheet, rowIndex,
				COLUMN_NAME_ARR);

		int rowCount = resultList.size();
		rowIndex = rowIndex + 1;
		if (rowCount > 0) {
			for (int index = 0; index < rowCount; index++) {
				DailyRITReportBean bean = (DailyRITReportBean) resultList.get(index);
				HSSFRow row = null;
				row = sheet.createRow(index + rowIndex);
				row = addRow(workBook, row, bean);
			}
		}
		return workBook;
	}

	/**
	 * Adds row values.
	 * 
	 * @param workbook
	 * @param row
	 * @param b
	 * @return
	 */
	private HSSFRow addRow(HSSFWorkbook workbook, HSSFRow row, DailyRITReportBean b) {
		int i = 0;
		try {
			HSSFCell cell = null;
			cell = row.createCell(i++);
			cell.setCellValue(b.getFormNo());
			cell = row.createCell(i++);
			cell.setCellValue(b.getQuoteNumber());
			cell = row.createCell(i++);
			cell.setCellValue(b.getPendingDay());
			cell = row.createCell(i++);
			cell.setCellValue(b.getMfr());
			cell = row.createCell(i++);
			cell.setCellValue(b.getQuoteType());
			cell = row.createCell(i++);
			cell.setCellValue(b.getSalesman());
			cell = row.createCell(i++);
			cell.setCellValue(b.getTeam());
			cell = row.createCell(i++);
			cell.setCellValue(b.getSoldToCustomerName());
			cell = row.createCell(i++);
			cell.setCellValue(b.getCustomerType());
			cell = row.createCell(i++);
			cell.setCellValue(b.getEndCustomer());
			cell = row.createCell(i++);
			cell.setCellValue(b.getRequestedPn());
			cell = row.createCell(i++);
			cell.setCellValue(b.getRequiredQty());
			cell = row.createCell(i++);
			cell.setCellValue(b.getEau());
			cell = row.createCell(i++);
			cell.setCellValue(b.getTargetResale());
			cell = row.createCell(i++);
			cell.setCellValue(b.getTargetMargin());
			cell = row.createCell(i++);
			cell.setCellValue(b.getAvtQuotedPn());
			cell = row.createCell(i++);
			cell.setCellValue(b.getQuoteMargin());
			cell = row.createCell(i++);
			cell.setCellValue(b.getAvnetQuotePrice());
			cell = row.createCell(i++);
			cell.setCellValue(b.getCost());
			cell = row.createCell(i++);
			cell.setCellValue(b.getCostIndicator());
			cell = row.createCell(i++);
			cell.setCellValue(b.getQtyIndicator());
			cell = row.createCell(i++);
			cell.setCellValue(b.getAvtQuotedQty());
			cell = row.createCell(i++);
			cell.setCellValue(b.getMultiUsage());
			cell = row.createCell(i++);
			cell.setCellValue(b.getAvnetInternalQCComment());
			cell = row.createCell(i++);
			cell.setCellValue(b.getLeadTime());
			cell = row.createCell(i++);
			cell.setCellValue(b.getMpq());
			cell = row.createCell(i++);
			cell.setCellValue(b.getMoq());
			cell = row.createCell(i++);
			cell.setCellValue(b.getMov());
			cell = row.createCell(i++);
			cell.setCellValue(b.getPriceValidity());
			cell = row.createCell(i++);
			cell.setCellValue(b.getShipmentValidity());
			cell = row.createCell(i++);
			cell.setCellValue(b.getMfrDebit());
			cell = row.createCell(i++);
			cell.setCellValue(b.getMfrQuote());
			cell = row.createCell(i++);
			cell.setCellValue(b.getMfrQuoteQty());
			cell = row.createCell(i++);
			cell.setCellValue(b.getReshedulingWindow());
			cell = row.createCell(i++);
			cell.setCellValue(b.getCancellWindow());
			cell = row.createCell(i++);
			cell.setCellValue(b.getQuotationTC());
			cell = row.createCell(i++);
			cell.setCellValue(b.getResaleIndicator());
			cell = row.createCell(i++);
			cell.setCellValue(b.getRfqItemAttachment());
			cell = row.createCell(i++);
			cell.setCellValue(b.getRfqFormAttachment());
			cell = row.createCell(i++);
			cell.setCellValue(b.getPmComment());
			cell = row.createCell(i++);
			cell.setCellValue(b.getAllocationPart());
			cell = row.createCell(i++);
			cell.setCellValue(b.getSpr());
			cell = row.createCell(i++);
			cell.setCellValue(b.getProjectName());
			cell = row.createCell(i++);
			cell.setCellValue(b.getApplication());
			cell = row.createCell(i++);
			cell.setCellValue(b.getMpSchedule());
			cell = row.createCell(i++);
			cell.setCellValue(b.getPpSchedule());
			cell = row.createCell(i++);
			cell.setCellValue(b.getDrmsProjectID());
			// Fixed defect 43
			cell = row.createCell(i++);
			cell.setCellValue(b.getDrmsAuthGp());
			cell = row.createCell(i++);
			cell.setCellValue(b.getReasonForAuthGpChange());
			cell = row.createCell(i++);
			cell.setCellValue(b.getMfrDr());
			cell = row.createCell(i++);
			cell.setCellValue(b.getDesignLocation());
			cell = row.createCell(i++);
			cell.setCellValue(b.getDesignInCat());
			cell = row.createCell(i++);
			cell.setCellValue(b.getLoa());
			cell = row.createCell(i++);
			cell.setCellValue(b.getQuoteType());
			cell = row.createCell(i++);
			cell.setCellValue(b.getProductGroup2());
			cell = row.createCell(i++);
			cell.setCellValue(b.getCompetitorInformation());
			cell = row.createCell(i++);
			cell.setCellValue(b.getBmtBiz());
			cell = row.createCell(i++);
			cell.setCellValue(b.getBusinessProgramType());
			cell = row.createCell(i++);
			cell.setCellValue(b.getRemarks());
			cell = row.createCell(i++);
			cell.setCellValue(b.getItemRemarks());
			cell = row.createCell(i++);
			cell.setCellValue(b.getReasonForRefresh());
			cell = row.createCell(i++);
			cell.setCellValue(b.getSegment());
			cell = row.createCell(i++);
			cell.setCellValue(b.getShipToParty());
			cell = row.createCell(i++);
			cell.setCellValue(b.getRequesterReference());
			cell = row.createCell(i++);
			cell.setCellValue(b.getRfqSubmissionDate());
			cell = row.createCell(i++);
			cell.setCellValue(b.getSoldToCode());
			cell = row.createCell(i++);
			cell.setCellValue(b.getShipToCode());
			cell = row.createCell(i++);
			cell.setCellValue(b.getEndCustomerCode());
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception in column number" + i + ", Exception message: " + e.getMessage(), e);
		}
		return row;
	}

	public List<DailyRITReportBean> convertBean(List<DailyRITReportBean> bLst, QuoteItem qi, DailyRITReportBean b) {
		if (qi.getRequestedMfr() != null && qi.getRequestedMfr().getName() != null
				&& !qi.getRequestedMfr().getName().equals("")) {
			b.setMfr(qi.getRequestedMfr().getName());
		}
		b.setQuoteType(qi.getQuoteType());
		if (qi.getQuote().getSales() != null) {
			b.setSalesman(qi.getQuote().getSales().getEmployeeId());
		}
		if (qi.getQuote().getTeam() != null) {
			b.setTeam(qi.getQuote().getTeam().getName());
		}
		if (qi.getSoldToCustomer() != null) {
			b.setSoldToCustomerName(qi.getSoldToCustomer().getCustomerFullName());
		}
		if (qi.getQuote() != null && qi.getQuote().getCustomerType() != null
				&& !qi.getQuote().getCustomerType().equals("")) {
			b.setCustomerType(qi.getQuote().getCustomerType());
		}
		b.setEndCustomer(qi.getEndCustomerName());
		b.setRequestedPn(qi.getRequestedPartNumber());
		if (qi.getRequestedQty() != null) {
			b.setRequiredQty(String.valueOf(qi.getRequestedQty()));
		}
		if (qi.getEau() != null) {
			b.setEau(String.valueOf(qi.getEau()));
		}
		if (qi.getTargetResale() != null) {
			b.setTargetResale(String.valueOf(qi.getTargetResale()));
		}
		b.setTargetMargin(convertDoubleToStrForMargin(qi.getTargetMargin()));
		if (qi.getQuotedPartNumber() != null) {
			b.setAvtQuotedPn(qi.getQuotedPartNumber());
		}
		b.setQuoteMargin(convertDoubleToStrForMargin(qi.getResaleMargin()));
		if (qi.getQuotedPrice() != null) {
			b.setAvnetQuotePrice(String.valueOf(qi.getQuotedPrice()));
		}
		if (qi.getCost() != null) {
			b.setCost(String.valueOf(qi.getCost()));
		}
		b.setCostIndicator(qi.getCostIndicator());
		b.setQtyIndicator(qi.getQtyIndicator());
		if (qi.getQuotedQty() != null) {
			b.setAvtQuotedQty(String.valueOf(qi.getQuotedQty()));
		}
		b.setMultiUsage(convertBooleanToStr(qi.getMultiUsageFlag()));
		b.setAvnetInternalQCComment(qi.getInternalComment());
		b.setLeadTime(qi.getLeadTime());
		if (qi.getMpq() != null) {
			b.setMpq(String.valueOf(qi.getMpq()));
		}
		if (qi.getMoq() != null) {
			b.setMoq(String.valueOf(qi.getMoq()));
		}
		if (qi.getMov() != null) {
			b.setMov(String.valueOf(qi.getMov()));
		}
		b.setPriceValidity(qi.getPriceValidity());
		b.setShipmentValidity(convertDateToStr(qi.getShipmentValidity()));
		b.setMfrDebit(qi.getVendorDebitNumber());
		b.setMfrQuote(qi.getVendorQuoteNumber());
		b.setMfrQuoteQty(qi.getQuotedQtyStr());
		b.setReshedulingWindow(qi.getRescheduleWindow() == null ? null : qi.getRescheduleWindow().toString());
		b.setCancellWindow(qi.getCancellationWindow() == null ? null : qi.getCancellationWindow().toString());
		b.setQuotationTC(qi.getTermsAndConditions());
		b.setResaleIndicator(qi.getResaleIndicator());
		b.setRfqItemAttachment(convertBooleanToStr(qi.getItemAttachmentFlag()));
		b.setRfqFormAttachment(convertBooleanToStr(qi.getItemAttachmentFlag()));
		b.setPmComment(qi.getInternalTransferComment());
		b.setAllocationPart(qi.getAllocationFlagStr());
		b.setSpr(qi.getSprFlagStr());
		b.setProjectName(qi.getProjectName());
		b.setApplication(qi.getApplication());
		b.setMpSchedule(qi.getMpSchedule());
		b.setPpSchedule(qi.getPpSchedule());
		b.setDesignLocation(qi.getDesignLocation());
		b.setDesignInCat(qi.getDesignInCat());
		b.setLoa(qi.getLoaFlagStr());
		if (qi.getQuote() != null) {
			b.setQuoteType(qi.getQuote().getQuoteType());
		}
		if (qi.getProductGroup2() != null) {
			b.setProductGroup2(qi.getProductGroup2().getName());
		}
		b.setCompetitorInformation(qi.getCompetitorInformation());
		// vo.quoteItem.id ==0 ? vo.quoteItem.quote.bmtCheckedFlag :
		// vo.quoteItem.bmtCheckedFlag) ? 'Yes':'No'}
		if (qi.getId() == 0) {
			b.setBmtBiz(convertBooleanToStr(qi.getQuote().getBmtCheckedFlag()));
		} else {
			b.setBmtBiz(qi.getBmtCheckedFlagStr());
		}
		if (qi.getProgramType() != null) {
			b.setBusinessProgramType(qi.getProgramType());
		}
		if (qi.getQuote() != null) {
			b.setRemarks(qi.getQuote().getRemarks());
		}
		b.setItemRemarks(qi.getRemarks());
		b.setReasonForRefresh(qi.getReasonForRefresh());
		// vo.quoteItem.id ==0 ? vo.quoteItem.quote.enquirySegment :
		// vo.quoteItem.enquirySegment}
		if (qi.getId() == 0) {
			b.setSegment(qi.getQuote().getEnquirySegment());
		} else {
			b.setSegment(qi.getEnquirySegment());
		}
		if (qi.getId() == 0) {
			if (qi.getQuote() != null && qi.getQuote().getShipToCustomer() != null) {
				if (!qi.getQuote().getShipToCustomerName().equals("")) {
					b.setShipToParty(qi.getQuote().getShipToCustomerName());
				} else {
					b.setShipToParty(qi.getQuote().getShipToCustomer().getName());
				}
			} else {
				if (qi.getShipToCustomer() != null) {
					if (!qi.getShipToCustomerName().equals("")) {
						b.setShipToParty(qi.getShipToCustomerName());
					} else {
						b.setShipToParty(qi.getShipToCustomer().getName());
					}
				}
			}
		}
		if (qi.getQuote() != null) {
			b.setRequesterReference(qi.getQuote().getYourReference());
		}
		if (qi.getQuote() != null) {
			b.setRfqSubmissionDate(convertDateToStr(qi.getSubmissionDate()));
		}
		if (qi.getSoldToCustomer() != null) {
			b.setSoldToCode(qi.getSoldToCustomer().getCustomerNumber());
		}
		if (qi.getShipToCustomer() != null) {
			b.setShipToCode(qi.getShipToCustomer().getCustomerNumber());
		}
		if (qi.getEndCustomer() != null) {
			b.setEndCustomerCode(qi.getEndCustomer().getCustomerNumber());
		}
		if (qi.getOrginalAuthGp() != null) {
			b.setDrmsAuthGp(DF2.format(qi.getOrginalAuthGp().doubleValue()) + "%");
		}
		if (qi.getAuthGpReasonDesc() != null) {
			b.setReasonForAuthGpChange(qi.getAuthGpReasonDesc());
		}
		bLst.add(b);
		return bLst;
	}

	private String convertDoubleToStrForMargin(Double doub) {
		if (doub == null || doub == 0d || doub.equals(new Double(0d))) {
			return "";
		} else {
			String str = "";
			if (doub != null) {
				str = DF2.format(doub) + PERCENT_SIGN;
			}
			return str;
		}
	}

	private String convertBooleanToStr(Boolean b) {
		String rltStr = "";
		if (b != null) {
			if (b) {
				rltStr = "Yes";
			} else {
				rltStr = "No";
			}
		} else {
			rltStr = "No";
		}

		return rltStr;
	}

	private String convertDateToStr(Date date) {
		if (date == null) {
			return "";
		} else {

			String returnStr = "";
			try {
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				returnStr = formatter.format(date);
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE,
						"Convert Date failed for Date: " + date.toString() + ", Exception message: " + e.getMessage(),
						e);
			}
			return returnStr;
		}
	}

	public void sendEmail(HSSFWorkbook workbook, String bzStr, String flowName, String subject) throws IOException {
		// OutstandingITReportTask.LOG.info("call outStanding it report
		// sendEmail");
		File xlsFile = null;
		File zipFile = null;
		FileOutputStream fileOut = null;
		String tempPath = sysConfigSB.getProperyValue(QuoteConstant.TEMP_DIR);
		try {
			MailInfoBean mib = new MailInfoBean();
			List<String> toList = new ArrayList<String>();
			try {
				List<User> userLst = null;
				if ("outstandingITReportTask".equals(flowName)) {
					userLst = dailyRITReportSB.findRecipients(QuoteConstant.OUTSTANDING_IT_REPORT, bzStr);
				}
				if ("dailyRITReportTask".equals(flowName)) {
					userLst = dailyRITReportSB.findRecipients(QuoteConstant.DAILY_RIT_REPORT, bzStr);
				}
				if (userLst != null) {
					for (User usr : userLst) {
						if (usr.getActive()) {// not nedd to send email to some
												// users which active = 0 added
												// by Lenon 2016-08-16
							toList.add(usr.getEmailAddress());
						}
					}
				}
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "get user email address failed ! for Bz: " + bzStr + ", Exception message: "
						+ MessageFormatorUtil.getParameterizedStringFromException(e), e);
			}
			if (toList.size() > 0) {
				mib.setMailSubject(subject);
				mib.setMailTo(toList);
				mib.setMailContent("");
				mib.setMailFrom(sysCodeMaintSB.getBuzinessProperty("OFFLINE_REPORT_FROM", bzStr));
				String zipFileName = tempPath + subject + ".zip";
				String xlsFileName = tempPath + subject + ".xls";
				xlsFile = new File(xlsFileName);
				fileOut = new FileOutputStream(xlsFile);
				workbook.write(fileOut);
				fileOut.close();
				zipFile = (File) QuoteUtil.doZipFile(zipFileName, xlsFileName);
				mib.setZipFile(zipFile);
				try {
					mailUtilsSB.sendAttachedMail(mib);
				} catch (Exception e) {
					LOGGER.log(Level.SEVERE, "Send email failed! , exception message : " + e.getMessage(), e);
				}

				LOGGER.info("call sendEmail end");
			} else {
				LOGGER.info("call sendEmail end, but hasn't any receiptor!");
			}
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e1) {
			throw e1;
		} finally {
			if (fileOut != null)
				fileOut = null;
			if (xlsFile != null) {
				if (xlsFile.exists())
					xlsFile.delete();
			}
			if (zipFile != null) {
				if (zipFile.exists())
					zipFile.delete();
			}
		}
	}

	public void deleteFilesInDir(String directory) {
		File dir = new File(directory);
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}
	}

	public boolean isRanOnThisServer(String serviceName) {
		boolean returnB = false;
		String hostName = null;
		try {
			InetAddress addr = InetAddress.getLocalHost();
			hostName = addr.getHostName().toString();
			LOGGER.info("hostName: " + hostName);
			if (serviceName != null && hostName != null && serviceName.contains(hostName)) {
				returnB = true;
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE,
					"Exception occured for service: " + serviceName + ", get host name exception : " + e.getMessage(),
					e);
			return false;
		}

		return returnB;

	}

	public void postProcessXLS(Object document, String[] columns, List<WorkingPlatformItemVO> quotationHistorys,
			String flowName) {
		HSSFWorkbook wb = (HSSFWorkbook) document;
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow header = sheet.getRow(0);
		int i = 0;
		for (String column : columns) {
			header.getCell(i++).setCellValue(column);
		}
		HSSFCellStyle lockedStyle = wb.createCellStyle();
		HSSFCellStyle mandatoryStyle = wb.createCellStyle();

		HSSFPalette palette1 = wb.getCustomPalette();
		// palette1.setColorAtIndex(HSSFColor.LIME.index, (byte) 251, (byte)
		// 236, (byte) 136);
		palette1.setColorAtIndex(HSSFColor.LIME.index, (byte) 191, (byte) 235, (byte) 201);
		mandatoryStyle.setFillForegroundColor(palette1.getColor(HSSFColor.LIME.index).getIndex());
		mandatoryStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

		HSSFCellStyle optionalStyle = wb.createCellStyle();
		HSSFPalette palette2 = wb.getCustomPalette();
		palette2.setColorAtIndex(HSSFColor.LAVENDER.index, (byte) 242, (byte) 220, (byte) 219);
		optionalStyle.setFillForegroundColor(palette2.getColor(HSSFColor.LAVENDER.index).getIndex());
		optionalStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

		for (i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
			HSSFRow row = sheet.getRow(i);
			String quoteNumber = row.getCell(3).getStringCellValue();
			WorkingPlatformItemVO foundVO = null;
			for (WorkingPlatformItemVO vo : quotationHistorys) {
				if (vo.getQuoteItem().getQuoteNumber().equals(quoteNumber)) {
					foundVO = vo;
					break;
				}
			}
			if (foundVO != null) {
				// fixed by DamonChen@20180521
				if (!"workingPlatformMB".equals(flowName)) {
					if (foundVO.getQuoteItem().getQuote().getFormAttachmentFlag() != null
							&& foundVO.getQuoteItem().getQuote().getFormAttachmentFlag() == true) {
						row.getCell(44).setCellValue("Yes");
					} else {
						row.getCell(44).setCellValue("No");
					}

					if (foundVO.getQuoteItem().getItemAttachmentFlag() != null
							&& foundVO.getQuoteItem().getItemAttachmentFlag() == true) {
						row.getCell(45).setCellValue("Yes");
					} else {
						row.getCell(45).setCellValue("No");
					}

					if (foundVO.getQuoteItem().getRefreshAttachmentFlag() != null
							&& foundVO.getQuoteItem().getRefreshAttachmentFlag() == true) {
						row.getCell(46).setCellValue("Yes");
					} else {
						row.getCell(46).setCellValue("No");
					}

					if (foundVO.getQuoteItem().getPmAttachmentFlag() != null
							&& foundVO.getQuoteItem().getPmAttachmentFlag() == true) {
						row.getCell(47).setCellValue("Yes");
					} else {
						row.getCell(47).setCellValue("No");
					}
				}
				if ("workingPlatformMB".equals(flowName)) {
					int offset = 6;
					if (foundVO.getQuoteItem().getQuote().getFormAttachmentFlag() != null
							&& foundVO.getQuoteItem().getQuote().getFormAttachmentFlag() == true) {
						row.getCell(49 + offset).setCellValue("Yes");
					} else {
						row.getCell(49 + offset).setCellValue("No");
					}

					if (foundVO.getQuoteItem().getItemAttachmentFlag() != null
							&& foundVO.getQuoteItem().getItemAttachmentFlag() == true) {
						row.getCell(50 + offset).setCellValue("Yes");
					} else {
						row.getCell(50 + offset).setCellValue("No");
					}

					if (foundVO.getQuoteItem().getRefreshAttachmentFlag() != null
							&& foundVO.getQuoteItem().getRefreshAttachmentFlag() == true) {
						row.getCell(51 + offset).setCellValue("Yes");
					} else {
						row.getCell(51 + offset).setCellValue("No");
					}

					if (foundVO.getQuoteItem().getPmAttachmentFlag() != null
							&& foundVO.getQuoteItem().getPmAttachmentFlag() == true) {
						row.getCell(52 + offset).setCellValue("Yes");
					} else {
						row.getCell(52 + offset).setCellValue("No");
					}

					if (foundVO.getQuoteItem().getQuoteItemDesign() != null) {
						row.getCell(85 + offset).setCellValue(
								foundVO.getQuoteItem().getQuoteItemDesign().getBmtFlag().getDescription());
					}

				}
			}
		}
		for (int j = 0; j < sheet.getPhysicalNumberOfRows(); j++) {
			HSSFRow row = sheet.getRow(j);
			for (i = 0; i < row.getLastCellNum(); i++) {
				HSSFCell cell = row.getCell(i);
				if (i == 4 || i == 6 || i == 12 || i == 18 || i == 19 || i == 26 || i == 27) {
					cell.setCellStyle(mandatoryStyle);
				} else if (i == 7 || i == 13) {
					cell.setCellStyle(optionalStyle);
				} else {
					cell.setCellStyle(lockedStyle);
				}
			}
		}

	}

	public void postProcessXLSForWorkingPlantPage(Object document, String[] columns, List<WorkingPlatformItemVO> quotationHistorys) {
		HSSFWorkbook wb = (HSSFWorkbook) document;
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow header = sheet.getRow(0);
		int i = 0;
		for (String column : columns) {
			header.getCell(i++).setCellValue(column);
		}
		HSSFCellStyle lockedStyle = wb.createCellStyle();
		HSSFCellStyle mandatoryStyle = wb.createCellStyle();

		HSSFPalette palette1 = wb.getCustomPalette();
		palette1.setColorAtIndex(HSSFColor.LIME.index, (byte) 191, (byte) 235, (byte) 201);
		mandatoryStyle.setFillForegroundColor(palette1.getColor(HSSFColor.LIME.index).getIndex());
		mandatoryStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

		HSSFCellStyle optionalStyle = wb.createCellStyle();
		HSSFPalette palette2 = wb.getCustomPalette();
		palette2.setColorAtIndex(HSSFColor.LAVENDER.index, (byte) 242, (byte) 220, (byte) 219);
		optionalStyle.setFillForegroundColor(palette2.getColor(HSSFColor.LAVENDER.index).getIndex());
		optionalStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

		for (i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
			HSSFRow row = sheet.getRow(i);
			String quoteNumber = row.getCell(3).getStringCellValue();
			WorkingPlatformItemVO foundVO = null;
			for (WorkingPlatformItemVO vo : quotationHistorys) {
				if (vo.getQuoteItem().getQuoteNumber().equals(quoteNumber)) {
					foundVO = vo;
					break;
				}
			}
			if (foundVO != null) {
				int offset = 6;
				if (foundVO.getQuoteItem().getQuote().getFormAttachmentFlag() != null
						&& foundVO.getQuoteItem().getQuote().getFormAttachmentFlag() == true) {
					row.getCell(49 + offset).setCellValue("Yes");
				} else {
					row.getCell(49 + offset).setCellValue("No");
				}

				if (foundVO.getQuoteItem().getItemAttachmentFlag() != null
						&& foundVO.getQuoteItem().getItemAttachmentFlag() == true) {
					row.getCell(50 + offset).setCellValue("Yes");
				} else {
					row.getCell(50 + offset).setCellValue("No");
				}

				if (foundVO.getQuoteItem().getRefreshAttachmentFlag() != null
						&& foundVO.getQuoteItem().getRefreshAttachmentFlag() == true) {
					row.getCell(51 + offset).setCellValue("Yes");
				} else {
					row.getCell(51 + offset).setCellValue("No");
				}

				if (foundVO.getQuoteItem().getPmAttachmentFlag() != null
						&& foundVO.getQuoteItem().getPmAttachmentFlag() == true) {
					row.getCell(52 + offset).setCellValue("Yes");
				} else {
					row.getCell(52 + offset).setCellValue("No");
				}

				if (foundVO.getQuoteItem().getQuoteItemDesign() != null) {
					row.getCell(85 + offset).setCellValue(
							foundVO.getQuoteItem().getQuoteItemDesign().getBmtFlag().getDescription());
				}
			}
		}
		for (int j = 0; j < sheet.getPhysicalNumberOfRows(); j++) {
			HSSFRow row = sheet.getRow(j);
			for (i = 0; i < row.getLastCellNum(); i++) {
				HSSFCell cell = row.getCell(i);
				if (i == 6 || i == 8 || i == 15 || i == 19 || i == 23 || i == 29 || i == 30
						|| i == 37 || i == 38) {
					cell.setCellStyle(mandatoryStyle);
				} else if (i == 9 || i == 16) {
					cell.setCellStyle(optionalStyle);
				} else {
					cell.setCellStyle(lockedStyle);
				}
			}
		}

	}
	
	/**
	 * 
	 * @param quoteItem
	 * @return
	 */
	public boolean isQuoteExpired(QuoteItem quoteItem) {

		boolean quoteExpired = false;

		Calendar now = Calendar.getInstance();
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);

		if (quoteItem.getQuoteExpiryDate() != null) {

			Date quoteExpiryDate = quoteItem.getQuoteExpiryDate();

			if ((quoteExpiryDate.getTime() - now.getTime().getTime()) < 0) {
				quoteExpired = true;
			} else {
				quoteExpired = false;
			}

		} else {

			Date sentOutTime = quoteItem.getSentOutTime();

			if (sentOutTime == null) {
				return false;
			}

			if (sentOutTime.before(now.getTime())) {
				quoteExpired = true;
			} else {
				quoteExpired = false;
			}

		}
		return quoteExpired;

	}

	public void proceedQuotationEmailSending(String strategyKey, List<QuotationEmail> emails, String flowName)
			throws WebQuoteException {
		if ("PostQuotationTask".equals(flowName) && emails == null) {
			emails = quoteSB.findQuotationEmail();
		}
		try {
			for (QuotationEmail email : emails) {
				QuotationEmailVO vo = new QuotationEmailVO();
				Quote quote = quoteSB.findQuoteByPK(Long.valueOf(email.getId().getFormId()));
				String creator = quote.getCreatedBy();
				User sales = quote.getSales();
				if (quote != null) {
					vo.setFormNumber(quote.getFormNumber());
					List<QuoteItem> sortQuoteItems = quote.getQuoteItems();
					Collections.sort(sortQuoteItems, QuoteItem.alternativeQuoteNumberComparator);
					String highlighted = email.getQuoteItemId();
					String[] highlightedQuoteItems = highlighted.split(",");
					for (QuoteItem qi : sortQuoteItems) {
						for (String id : highlightedQuoteItems) {
							if (id.length() > 0 && String.valueOf(qi.getId()).equals(id)) {
								qi.setHightlighted(true);
							}
						}
					}
					quote.setQuoteItems(sortQuoteItems);

					vo.setQuoteId(quote.getId());
					vo.setQuote(quote);
					vo.setSoldToCustomer(quote.getSoldToCustomer());
					vo.setSubmissionDateFromQuote(false);
					LOGGER.log(Level.INFO,
							"Quote " + quote.getFormNumber() + " with Quote Item " + quote.getQuoteItems().size());
					// vo.setHssfWorkbook(getQuoteTemplateBySoldTo(this,quote.getSoldToCustomer(),
					// quote));
					vo.setLink(getUrl() + "/RFQ/MyQuoteListForSales.jsf");
					vo.setRecipient(email.getToEmployeeName());

					User user = userSB.findByEmployeeIdLazily(email.getId().getFromEmployeeId());
					String bu = quote.getBizUnit().getName();
					List<String> bccEmails = new ArrayList<String>();

					if ("AsyncPostQuotationSB".equals(flowName)) {
						bccEmails.add(sysMaintSB.getEmailAddress(bu));
					}
					bccEmails.add(user.getEmailAddress());
					vo.setBccEmails(bccEmails);

					vo.setSender(sysMaintSB.getEmailSignName(bu) + "<br/>" + sysMaintSB.getEmailHotLine(bu) + "<br/>"
							+ "Email Box: " + sysMaintSB.getEmailSignContent(bu));
					vo.setFromEmail(user.getEmailAddress());
					vo.setFromEmailInName(user.getName());
					String subject = "Quotation - " + vo.getFormNumber();
					String fileName = "Quotation - " + vo.getFormNumber();

					String fullCustomerName = null;
					if ("PostQuotationTask".equals(flowName)) {
						fullCustomerName = quote.getSoldToCustomerName();
						if (fullCustomerName == null || fullCustomerName.isEmpty()) {
							fullCustomerName = DownloadUtil.getCustomerFullName(quote.getSoldToCustomer());
						}
					}
					fullCustomerName = DownloadUtil.getCustomerFullName(quote.getSoldToCustomer());
					if (fullCustomerName != null) {
						subject += " - " + fullCustomerName;
						if (fullCustomerName.length() > 40) {
							fileName += " - " + fullCustomerName.substring(0, 40);
						} else {
							fileName += " - " + fullCustomerName;
						}
					}
					if (quote != null) {
						String yourReference = "";
						if (!QuoteUtil.isEmpty(quote.getYourReference())) {
							if (quote.getYourReference().length() > 50)
								yourReference = quote.getYourReference().substring(0, 50);
							else
								yourReference = quote.getYourReference();
							subject += " - Reference :  " + yourReference;
						}
					}
					vo.setSubject(subject);
					vo.setFileName(fileName);
				}
				LOGGER.log(Level.INFO, "Send " + vo.getFormNumber() + " To : " + email.getToEmployeeId());
				if (email.getToEmployeeId() != null) {
					List<String> toEmails = new ArrayList<String>();
					String[] toEmailsArr = email.getToEmployeeId().split(",");
					for (String employeeId : toEmailsArr) {
						if (employeeId.length() > 0) {
							toEmails.add(employeeId);
						}
					}
					vo.setToEmails(toEmails);
				}
				LOGGER.log(Level.INFO, "Send " + vo.getFormNumber() + " Cc : " + email.getCcEmployeeId());
				if (email.getCcEmployeeId() != null) {
					List<String> ccEmails = new ArrayList<String>();
					String[] ccEmailsArr = email.getCcEmployeeId().split(",");
					for (String employeeId : ccEmailsArr) {
						if (employeeId.length() > 0) {
							ccEmails.add(employeeId);
						}
					}
					vo.setCcEmails(ccEmails);
				}
				// Enhancement for Defect 11(ASEAN/INDIA)
				if (creator != null && sales != null) {
					if (!creator.equalsIgnoreCase(sales.getEmployeeId())) {
						List<String> ccEmails = vo.getCcEmails();
						if (ccEmails == null) {
							ccEmails = new ArrayList<String>();
						}
						ccEmails.add(creator);
						vo.setCcEmails(ccEmails);
					}
				}
				vo.setRemark(email.getRemark());
				// sendQuotationEmail(vo);

/*				SendQuotationEmailStrategy strategy = (SendQuotationEmailStrategy) cacheUtil
						.getStrategy(SendQuotationEmailStrategy.class, strategyKey);*/
				
				SendQuotationEmailStrategy strategy =(SendQuotationEmailStrategy) StrategyFactory.getSingletonFactory()
						.getStrategy(SendQuotationEmailStrategy.class,strategyKey,
								this.getClass().getClassLoader());
				strategy.sendQuotationEmail(vo);

				/*
				 * SendQuotationEmailStrategy strategy = new
				 * SendQuotationEmailWithAttachStrategy();
				 * strategy.sendQuotationEmail(vo);
				 */
			}
			if ("PostQuotationTask".equals(flowName)) {
				final Date date = new Date();
				final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				quoteSB.removeQuotationEmail(sdf.format(date));
			}
		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, ex.getMessage());
			ex.printStackTrace();
		}
		LOGGER.info("end proceedQuotationEmailSending");
	}

	public List<WorkingPlatformItemVO> searchQuoteHistory(String quoteHistoryMfrSearch,
			String quoteHistoryPartNumberSearch, String quoteHistorySoldToCustomerNameSearch,
			List<WorkingPlatformItemVO> quotationHistorys, QuoteSB quoteSB, BizUnit bizUnit) {
		if (QuoteUtil.isEmpty(quoteHistoryMfrSearch))
			quoteHistoryMfrSearch = null;
		if (QuoteUtil.isEmpty(quoteHistoryPartNumberSearch))
			quoteHistoryPartNumberSearch = null;
		if (QuoteUtil.isEmpty(quoteHistorySoldToCustomerNameSearch))
			quoteHistorySoldToCustomerNameSearch = null;

		if (quoteHistoryMfrSearch == null && quoteHistoryPartNumberSearch == null
				&& quoteHistorySoldToCustomerNameSearch == null) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, QuoteConstant.MESSAGE_SEARCH_CRITERIA,
					ResourceMB.getText("wq.message.inputCriteria"));
			FacesContext.getCurrentInstance().addMessage("form_pendinglist:workingPlatformGrowl", msg);
		} else if (quoteHistoryPartNumberSearch != null && quoteHistoryPartNumberSearch.length() < 3) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, QuoteConstant.MESSAGE_INVALID_FORMAT,
					ResourceMB.getText("wq.message.minPN"));
			FacesContext.getCurrentInstance().addMessage("form_pendinglist:workingPlatformGrowl", msg);

		} else {
			quotationHistorys = new ArrayList<WorkingPlatformItemVO>();

			List<String> stage = new ArrayList<String>();
			stage.add(QuoteSBConstant.QUOTE_STAGE_FINISH);
			stage.add(QuoteSBConstant.QUOTE_STAGE_PENDING);
			stage.add(QuoteSBConstant.QUOTE_STAGE_INVALID);
			List<QuoteItem> quotationItemHistorys = quoteSB.findQuotesByMaterialsAndBizUnitAndStageAndStatusAndCustomer(
					quoteHistoryPartNumberSearch, quoteHistoryMfrSearch, bizUnit, stage, null,
					quoteHistorySoldToCustomerNameSearch);
			if (quotationItemHistorys != null) {
				for (QuoteItem quoteItem : quotationItemHistorys) {
					WorkingPlatformItemVO item = new WorkingPlatformItemVO();
					item.setQuoteItem(quoteItem, bizUnit);
					quotationHistorys.add(item);
				}
			}
		}

		return quotationHistorys;
	}

	public void changeAction(final String type, final List<String> selectedlist,
			final com.avnet.emasia.webquote.commodity.dto.PISearchBean pis, final CheckBoxSet checkBoxSet,
			final ProgramMaterialSB programMaterialSB, List<CheckBoxBean> programTypeList,
			List<CheckBoxBean> productGroupList, List<CheckBoxBean> mfrList) {
		pis.setActive(Constants.YES);
		pis.setMfrPart(null);
		checkBoxSet.setMfrPart(null);
		if (Constants.PARA_PROGRAM_CG.equalsIgnoreCase(type)) {
			checkBoxSet.setProgramType(checkBoxSet.updateCheckBox(checkBoxSet.getProgramType(), selectedlist));
			pis.setProgramTypeList(checkBoxSet.getProgramTypeStrList(false));
			// pis.setProductGroupList(checkBoxSet.getProductGroupStrList(true));
			pis.setMfrList(checkBoxSet.getMfrStrList(true));

			// programTypeList = programMaterialSB.getProgramTypeList(pis);
			// productGroupList = programMaterialSB.getProductGroupList(pis);
			// mfrList = programMaterialSB.getMfrList(pis);
			// checkBoxSet.setProductGroup(
			// checkBoxSet.updateCheckBoxBatch(checkBoxSet.getProductGroup(),
			// productGroupList, true));
			// checkBoxSet.setMfr(checkBoxSet.updateCheckBoxBatch(checkBoxSet.getMfr(),
			// mfrList, true));
		} else if (Constants.PARA_PRODUCT_GROUP_CG.equalsIgnoreCase(type)) {
			checkBoxSet.setProductGroup(checkBoxSet.updateCheckBox(checkBoxSet.getProductGroup(), selectedlist));
			pis.setProductGroupList(checkBoxSet.getProductGroupStrList(false));
			pis.setMfrList(checkBoxSet.getMfrStrList(true));
			mfrList = programMaterialSB.getMfrList(pis);
			checkBoxSet.setMfr(checkBoxSet.updateCheckBoxBatch(checkBoxSet.getMfr(), mfrList, true));
		} else if (Constants.PARA_MFR_CG.equalsIgnoreCase(type)) {
			checkBoxSet.setMfr(checkBoxSet.updateCheckBox(checkBoxSet.getMfr(), selectedlist));
			pis.setMfrList(checkBoxSet.getMfrStrList(false));
		}
		checkBoxSet.setSuccess(true);
		checkBoxSet.setActive(Constants.YES);
	}

	public static MailInfoBean sendUploadPricerEmail(final Date createOn, final String fileName, final User user,
			final ProgramItemUploadCounterBean countBean, final String fromEmailAddr, final MailInfoBean mib,
			String content, final String flowName) {
		if (countBean != null) {
			StringBuffer sb = new StringBuffer();
			if ("pricerUploadHelper".equals(flowName)) {
				sb.append("File Name: " + fileName + " Uploaded By: " + user.getName() + "  Uploaded by user at: "
						+ PricerUtils.convertFormat2Time(createOn) + " Proceeded by system at: "
						+ PricerUtils.convertFormat2Time(new Date()) + " <br/>");

			}
			sb.append("<table border=1><tr><th>No</th><th>Action</th><th>No Of Records</th></tr>");
			sb.append(" <tr><td>1</td> <td>New Part Created</td><td>" + countBean.getAddedPartCount() + "</td></tr>");
			sb.append(" <tr><td>2</td><td>Part Updated:</td><td>" + countBean.getUpdatedPartCount() + "</td></tr>");
			sb.append(" <tr><td>3</td><td>Part Removed</td><td>" + countBean.getRemovedPartCount() + "</td></tr>");
			sb.append(" <tr><td>4</td><td>Normal Pricer Added:</td><td>" + countBean.getAddedNormalCount()
					+ "</td></tr>");
			sb.append(" <tr><td>5</td> <td>Normal Pricer Updated:</td><td>" + countBean.getUpdatedNormalCount()
					+ "</td> </tr>");
			sb.append(" <tr><td>6</td><td>Normal Pricer Removed:</td><td>" + countBean.getRemovedNormalCount()
					+ "</td></tr>");
			sb.append(" <tr><td>7</td><td>Program Pricer Added:</td> <td>" + countBean.getAddedProgramCount()
					+ "</td> </tr>");
			sb.append(" <tr><td>8</td><td>Program Pricer Updated:</td> <td>" + countBean.getUpdatedProgramCount()
					+ "</td> </tr>");
			sb.append("	<tr><td>9</td><td>Program Pricer Removed:</td><td>" + countBean.getRemovedProgramCount()
					+ "</td> </tr>");
			sb.append("	<tr><td>10</td><td>Contract Pricer Added:</td><td>" + countBean.getAddedContractPriceCount()
					+ "</td></tr>");
			sb.append("	 <tr><td>11</td><td>Contract Pricer Updated:</td><td>"
					+ countBean.getUpdatedContractPriceCount() + "</td></tr>");
			sb.append("	  <tr><td>12</td><td>Contract Pricer Removed:</td><td>"
					+ countBean.getRemovedContractPriceCount() + "</td></tr>");

			sb.append("	<tr><td>13</td><td>Simple Pricer Added:</td><td>" + countBean.getAddedSimplePricerCount()
					+ "</td></tr>");
			sb.append("	<tr><td>14</td><td>Simple Pricer Updated:</td><td>" + countBean.getUpdatedSimplePricerCount()
					+ "</td></tr>");
			sb.append("	<tr><td>15</td><td>Simple Pricer Removed:</td><td>" + countBean.getRemovedSimplePricerCount()
					+ "</td></tr>");

			sb.append("	<tr><td>16</td><td>SalesCost Pricer Added:</td><td>" + countBean.getAddedSalesCostPricerCount()
					+ "</td></tr>");
			sb.append("	<tr><td>17</td><td>SalesCost Pricer Updated:</td><td>"
					+ countBean.getUpdatedSalesCostPricerCount() + "</td></tr>");
			sb.append("	<tr><td>18</td><td>SalesCost Pricer Removed:</td><td>"
					+ countBean.getRemovedSalesCostPricerCount() + "</td></tr>");

			sb.append("	</table>");
			content = sb.toString() + "<br/>";
			content += "Best Regards," + "<br/>";
			content += fromEmailAddr + "<br/>";
		}
		mib.setMailContent(content);
		mib.setMailFrom(fromEmailAddr);
		return mib;
	}

	public List<String> autoCompleteTeam(String key, TeamSB teamSB) {
		List<Team> teamLst = teamSB.findByName(key);
		List<String> resultLst = new ArrayList<String>();
		for (Team t : teamLst) {
			resultLst.add(t.getName());
		}

		return resultLst;
	}

	public List<String> autoCompleteSale(String key, UserSB userSB) {
		List<String> resultLst = new ArrayList<String>();
		List<User> userLst = userSB.findAutoCompleteUser(key);

		for (User usr : userLst) {
			resultLst.add(usr.getName());
		}
		return resultLst;
	}

	public List<String> autoCompleteFormNo(String key, QuoteSB quoteSB) {
		List<Quote> qLst = quoteSB.findQuoteByFormNumber(key);
		List<String> resultLst = new ArrayList<String>();

		for (Quote q : qLst) {
			resultLst.add(q.getFormNumber());
		}

		return resultLst;
	}

	public RFQBacklogReportVo constructCriteria(RFQBacklogReportVo reportCriteria, int from, Object object) {
		try {
			Date date = new Date();
			if ("6".equals(from)) {
				reportCriteria.setCreateFrom((getDate(Calendar.MONTH, -from)));
			} else {
				if ("7".equals(from)) {
					reportCriteria.setCreateFrom((getDate(Calendar.DATE, -from)));
				}
			}
			reportCriteria.setCreateTo(date);
			reportCriteria.setCurrentUser(getUser());
			if (object instanceof RFQBacklogReportMB) {
				reportCriteria.setStage("PENDING");
			}

			List<Role> roleLst = getUser().getRoles();
			String roleName = roleLst.get(0).getName();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE,
					"Initalization rfq backlog report failed! for user : " + getUser().getName() + e.getMessage(), e);
		}
		return reportCriteria;
	}

	public User getUser() {
		WQUserDetails userDetails = (WQUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		user = userDetails.getUser();
		return user;
	}

	public Date getDate(int field, int amount) {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(field, amount);

		return cal.getTime();
	}

	public boolean uploadFileSizeCheck(SystemCodeMaintenanceSB systemCodeMaintenanceSB, UploadedFile uploadFile,
			UploadStrategy uploadStrategy, long start) {
		long uploadfileSizeLimit = systemCodeMaintenanceSB.getUploadFileSizeLimit();
		boolean errorFound = false;
		if (uploadFile.getSize() > uploadfileSizeLimit) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, ResourceMB
							.getParameterizedString("wq.error.fileSizeError", Long.toString(uploadfileSizeLimit)) + ".",
							""));
			errorFound = true;
			return errorFound;
		}

		start = System.currentTimeMillis();
		boolean isUploadFile = uploadStrategy.isValidateUpload(uploadFile);
		if (!isUploadFile) {
			FacesContext.getCurrentInstance().addMessage("msgId", new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.message.pleaseSelectFile") + ".", ""));
			errorFound = true;
			return errorFound;
		}

		boolean isExcelFile = uploadStrategy.isExcelFile(uploadFile);
		if (!isExcelFile) {
			FacesContext.getCurrentInstance().addMessage("msgId", new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.message.selExcelFile") + ".", ""));
			errorFound = true;
			return errorFound;
		}
		return errorFound;
	}

	public void removeUser(final List<User> users) {
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> params = context.getExternalContext().getRequestParameterMap();
		long id = Long.parseLong(params.get("id"));
		for (int i = 0; i < users.size(); i++) {
			if (users.get(i).getId() == id) {
				users.remove(i);
			}
		}
	}

	public boolean removeAllAndUpload(final boolean isUploadFile, final boolean isExcelFile) {
		if (!isUploadFile) {
			FacesContext.getCurrentInstance().addMessage("msgId", new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.message.pleaseSelectFile") + ".", ""));
			return false;
		}
		if (!isExcelFile) {
			FacesContext.getCurrentInstance().addMessage("msgId", new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.message.selExcelFile") + ".", ""));
			return false;
		}

		return true;

	}

	/****************** added by punit ************/
	/**
	 * @param submitQuote
	 * @param vo
	 * @param customer
	 * @param user
	 * @param fullCustomerName
	 */
	public QuotationEmailVO commonLogicForDpRFQandRFQ(final Quote submitQuote, final QuotationEmailVO vo,
			final Customer customer, final User user, final String fullCustomerName) {
		String subject = "Quotation - " + submitQuote.getFormNumber();

		String fileName = "Quotation - " + submitQuote.getFormNumber();
		if (fullCustomerName != null) {
			subject += " - " + fullCustomerName;
			if (fullCustomerName.length() > 40) {
				fileName += " - " + fullCustomerName.substring(0, 40);
			} else {
				fileName += " - " + fullCustomerName;
			}
		}
		if (submitQuote != null) {
			String yourReference = "";
			if (!QuoteUtil.isEmpty(submitQuote.getYourReference())) {
				if (submitQuote.getYourReference().length() > 50)
					yourReference = submitQuote.getYourReference().substring(0, 50);
				else
					yourReference = submitQuote.getYourReference();
				subject += " - Reference :  " + yourReference;
			}
		}
		vo.setSubject(subject);
		vo.setFileName(fileName);

		List<String> toEmails = new ArrayList<String>();
		toEmails.add(submitQuote.getSales().getEmployeeId());
		vo.setToEmails(toEmails);
		List<String> ccEmails = new ArrayList<String>();
		if (submitQuote.getCopyToCS() != null) {
			String[] csIds = submitQuote.getCopyToCS().split(";");
			for (String csId : csIds) {
				if (!QuoteUtil.isEmpty(csId)) {
					ccEmails.add(csId);
				}
			}
		}
		// Enhancement for Defect 11(ASEAN/INDIA)
		if (!user.getEmployeeId().equalsIgnoreCase(submitQuote.getSales().getEmployeeId())) {
			ccEmails.add(user.getEmployeeId());
		}

		vo.setCcEmails(ccEmails);
		return vo;
	}

	public void createDynamicColumns(final List<ReportGroupVo> lst, final List<ColumnModelVo> columns,
			int grandColspanSize) {
		columns.clear();
		for (ReportGroupVo columnKey : lst) {
			String hearder = columnKey.getGroupByName();
			String property = columnKey.getGroupByColName();

			if (hearder.equalsIgnoreCase(ReportMB.HEADER_CUSTOMER)) {
				columns.add(new ColumnModelVo(ReportMB.HEADER_CUSTOMER_NUM, ReportMB.GRP_COL_CUSTOMER_NUM));
				columns.add(new ColumnModelVo(ReportMB.HEADER_CUSTOMER_NAME, ReportMB.GRP_COL_CUSTOMER_NAME));
			} else {
				columns.add(new ColumnModelVo(hearder, property));
			}
		}
		grandColspanSize = columns.size() + 1;
	}

	public void setSearchCriteria(ReportSearchCriteria criteria) {
		// to set the customer search information according to the raido button
		// of customer
		if ("0".equals(criteria.getSearchCustomerType())) {
			criteria.setCustomerNum(criteria.getCustomer());
			criteria.setCustomerName(null);
		}
		if ("1".equals(criteria.getSearchCustomerType())) {
			criteria.setCustomerNum(null);
			criteria.setCustomerName(criteria.getCustomer());
		}
		// to set the quote period format type
		if (criteria.isShowQuotePeriod()) {
			if (QUOTE_TYPE_YEAR.equals(criteria.getSearchQuoteType())) {
				criteria.setQuoteTypeFormat("yyyy");
			} else if (QUOTE_TYPE_QTR.equals(criteria.getSearchQuoteType())) {
				criteria.setQuoteTypeFormat("yyyy Q");
			} else {
				criteria.setQuoteTypeFormat("yy MON");
			}
		}

	}

	public ReportSearchCriteria resetReport(final ReportSearchCriteria criteria, final String defaultBuName,
			final String flowName) {
		// set the default period range is 12 month from today to before
		if ("orderOnHandReportMB".equals(flowName)) {
			criteria.setQuoteFrm(org.apache.commons.lang.time.DateUtils.addMonths(new Date(), -3));
		}
		criteria.setQuoteTo(new Date());
		criteria.setSalesMgr(null);
		criteria.setSalesTeam(null);
		criteria.setSalesUser(null);
		criteria.setCustomer(null);
		criteria.setCustomerNum(null);
		criteria.setMfr(null);
		criteria.setProductGrp1(null);
		criteria.setProductGrp2(null);
		criteria.setProductGrp3(null);
		criteria.setProductGrp4(null);
		criteria.setQuoteHandler(null);
		criteria.setBizUnit(null);
		criteria.setQuotedPartNumber(null);
		criteria.setCustomerName(null);
		criteria.setSearchCustomerType("0");
		criteria.setBizUnit(defaultBuName);
		criteria.setSearchQuoteType(QUOTE_TYPE_YEAR);
		criteria.setSearchResultType("0");
		return criteria;
	}

	public void onTransfer(final TransferEvent event, final String flowName) {
		String errMsg = null;
		for (Object item : event.getItems()) {
			if (HEADER_QUOTE_PERIOD.equals(((ReportGroupVo) item).getGroupByName())) {
				errMsg = ResourceMB.getText("wq.message.quotedPeriod");
				break;
			}
		}
		if (errMsg != null) {
			FacesContext fc = FacesContext.getCurrentInstance();
			if ("orderOnHandReportMB".equals(flowName)) {
				fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, errMsg, ""));
			} else {
				fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						ResourceMB.getText("wq.message.quotedPeriod"), ""));
			}
			fc.renderResponse();
			return;
		}
	}

	/*	*//**
			 * @param key
			 * @return
			 */

	/*
	 * // TODO TO REMOVE public List<String> autoCompleteSalesNumber(String key,
	 * RfqHeaderVO rfqHeader,BizUnit bizUnit,UserSB userSB){ List<User> users =
	 * null; if (getUser() != null && getUser().getRoles() != null) { for (Role
	 * r : getUser().getRoles()) { if
	 * (r.getName().equals(QuoteSBConstant.ROLE_CS) ||
	 * QuoteSBConstant.ROLE_CS_MANAGER.equals(r.getName())) {// modified // by
	 * // Lenon.Yang(043044) // 2016-07-12 // webquote // japan // fixed issue
	 * 1300 users = userSB.wFindAllSalesForCs(null, key, getUser(), false); if
	 * (getUser() != null && users.size() > 0) { return
	 * formatAutoCompleteUserCode(users,rfqHeader,bizUnit); } } else if
	 * (r.getName().equals(QuoteSBConstant.ROLE_INSIDE_SALES)) { // issue 1192
	 * users = userSB.wFindAllSalesForCsForInsideSales(null, key, getUser()); if
	 * (getUser() != null && users.size() > 0) { return
	 * formatAutoCompleteUserCode(users,rfqHeader,bizUnit); } } else if
	 * (r.getName().equals(QuoteSBConstant.ROLE_QC_OPERATION)
	 * ||r.getName().equals(QuoteSBConstant.ROLE_CENTRAL_PRICING)) { users =
	 * userSB.wFindAllSalesByBizUnit(null, key, new String[] {
	 * QuoteSBConstant.ROLE_SALES, QuoteSBConstant.ROLE_INSIDE_SALES,
	 * QuoteSBConstant.ROLE_SALES_MANAGER, QuoteSBConstant.ROLE_SALES_DIRECTOR,
	 * QuoteSBConstant.ROLE_SALES_GM }, bizUnit); if (getUser() != null &&
	 * users.size() > 0) { return
	 * formatAutoCompleteUserCode(users,rfqHeader,bizUnit); } } } } return
	 * Collections.emptyList(); }
	 * 
	 *//**
		 * @param users
		 * @return
		 *//*
		 * //TODO TO REMOVE public List<String>
		 * formatAutoCompleteUserCode(List<User> users, RfqHeaderVO
		 * rfqHeader,BizUnit bizUnit) { List<String> list = new
		 * ArrayList<String>(); for (User user : users) { //added by
		 * damon@20160117 if ((user.getDefaultBizUnit() != null) && (bizUnit !=
		 * null) &&
		 * (user.getDefaultBizUnit().getName().equals(bizUnit.getName()))) {
		 * String label = ""; label += user.getEmployeeId(); label +=
		 * QuoteConstant.AUTOCOMPLETE_SEPARATOR; label += user.getName();
		 * list.add(label); }
		 * 
		 * } if(users == null || users.isEmpty()){
		 * rfqHeader.setSalesBizUnit(null); } return list; }
		 */

	// TODO TOUSE TOCONFIRM
	public List<String> autoCompleteSalesNumber(String key, BizUnit bizUnit) {

		return formatAutoCompleteUserCode(getAssignedSales(null, key, bizUnit), bizUnit);
	}

	/**
	 * @param users
	 * @return
	 */
	// TODO TO USE
	public List<String> formatAutoCompleteUserCode(List<User> users, BizUnit bizUnit) {
		List<String> list = new ArrayList<String>();
		Set<User> set = new HashSet<User>(users);
		for (User user : set) {
			// added by damon@20160117
			if ((user.getDefaultBizUnit() != null) && (bizUnit != null)
					&& (user.getDefaultBizUnit().getName().equals(bizUnit.getName()))) {
				String label = "";
				label += user.getEmployeeId();
				label += QuoteConstant.AUTOCOMPLETE_SEPARATOR;
				label += user.getName();
				list.add(label);
			}

		}
		return list;
	}

	/**
	 * @param message
	 * @param errorMsg
	 * @param newProspectiveCustomerName1
	 * @param newProspectiveCustomerCountry
	 * @param newProspectiveCustomerCity
	 * @param newProspectiveCustomerSalesOrg
	 * @return
	 */
	public String validationMessage(String message, String errorMsg, String newProspectiveCustomerName1,
			String newProspectiveCustomerCountry, String newProspectiveCustomerCity,
			String newProspectiveCustomerSalesOrg) {
		if (QuoteUtil.isEmpty(newProspectiveCustomerName1)) {
			if (errorMsg.length() > 0) {
				errorMsg += ", ";
			}
			message = ResourceMB.getText("wq.label.custmrName");
			errorMsg += message;
		}
		if (QuoteUtil.isEmpty(newProspectiveCustomerCountry)) {
			if (errorMsg.length() > 0) {
				errorMsg += ", ";
			}
			message = ResourceMB.getText("wq.label.country");
			errorMsg += message;
		}
		if (QuoteUtil.isEmpty(newProspectiveCustomerCity)) {
			if (errorMsg.length() > 0) {
				errorMsg += ", ";
			}
			message = ResourceMB.getText("wq.label.city");
			errorMsg += message;
		}
		if (QuoteUtil.isEmpty(newProspectiveCustomerSalesOrg)) {
			if (errorMsg.length() > 0) {
				errorMsg += ", ";
			}
			message = ResourceMB.getText("wq.label.salsOrg");
			errorMsg += message;
		}

		return errorMsg;
	}

	/**
	 * @param duplicatedCustomerCode
	 * @param duplicatedCustomerName
	 * @param duplicatedCustomerType
	 * @param existingCustomer
	 */
	public String existingCustomer(String duplicatedCustomerCode, String duplicatedCustomerName,
			String duplicatedCustomerType, com.avnet.emasia.webquote.webservice.customer.ZwqCustomer existingCustomer) {

		duplicatedCustomerType = existingCustomer.getKtokd();
		if (!QuoteUtil.isEmpty(duplicatedCustomerType)) {
			String[] customerTypes = duplicatedCustomerType.split(",");
			duplicatedCustomerType = "";
			for (String customerType : customerTypes) {
				if (customerType.trim().equals(QuoteSBConstant.ACCOUNT_GROUP_SOLDTO)) {
					duplicatedCustomerType += QuoteConstant.CUSTOMER_SOLDTOCUSTOMER + ",";
				} else if (customerType.trim().equals(QuoteSBConstant.ACCOUNT_GROUP_SHIPTO)) {
					duplicatedCustomerType += QuoteConstant.CUSTOMER_SHIPTOCUSTOMER + ",";
				} else if (customerType.trim().equals(QuoteSBConstant.ACCOUNT_GROUP_ENDCUSTOMER)) {
					duplicatedCustomerType += QuoteConstant.CUSTOMER_ENDCUSTOMER + ",";
				} else if (customerType.trim().equals(QuoteSBConstant.ACCOUNT_GROUP_PROSPECTIVE_CUSTOMER)) {
					duplicatedCustomerType += QuoteConstant.CUSTOMER_PROSPECTIVECUSTOMER + ",";
				}
			}
			if (duplicatedCustomerType.length() > 0)
				duplicatedCustomerType = duplicatedCustomerType.substring(0, duplicatedCustomerType.length() - 1);
		}
		return duplicatedCustomerType;
	}

	/**
	 * @param customerAddresses
	 */
	public List<String> updateFormatAutoCompleteUserName(String key, BizUnit bizUnit, UserSB userSB) {
		return formatAutoCompleteUserName(getAssignedSales(key, null, bizUnit));
	}

	public List<User> getAssignedSales(String keyOfName, String keyOfNum, BizUnit bizUnit) {
		List<User> users = null;
		User user = getUser();
		if (user != null && !user.getRoles().isEmpty()) {
			if (user.isInRole(QuoteSBConstant.ROLE_CS)) {
				users = userSB.wFindAllSalesForCs(keyOfName, keyOfNum, user, false);
				if (users.size() > 0) {
					return users;
				}
			}
			if (user.isInRole(QuoteSBConstant.ROLE_INSIDE_SALES)) {
				users = userSB.wFindAllSalesForCsForInsideSales(keyOfName, keyOfNum, user);
				if (users.size() > 0) {
					return users;
				}
			}
			if (user.isInRole(QuoteSBConstant.ROLE_QC_OPERATION, QuoteSBConstant.ROLE_QC_PRICING,
					QuoteSBConstant.ROLE_CENTRAL_PRICING)) {
				users = userSB.wFindAllSalesByBizUnit(keyOfName, keyOfNum,
						new String[] { QuoteSBConstant.ROLE_SALES, QuoteSBConstant.ROLE_INSIDE_SALES,
								QuoteSBConstant.ROLE_SALES_MANAGER, QuoteSBConstant.ROLE_SALES_DIRECTOR,
								QuoteSBConstant.ROLE_SALES_GM },
						bizUnit);
				if (users.size() > 0) {
					return users;
				}
			}
		}
		return Collections.emptyList();

	}

	public List<String> formatAutoCompleteUserName(List<User> users) {
		List<String> list = new ArrayList<String>();
		Set<User> set = new HashSet<User>(users);
		for (User user : set) {
			String label = "";
			label += user.getName();
			label += QuoteConstant.AUTOCOMPLETE_SEPARATOR;
			label += user.getEmployeeId();
			list.add(label);
		}
		return list;
	}

	public List<String> populateRoleNameList() {
		List<String> roleNames = new ArrayList<String>();
		// CS, Sales, PM, CM & BMT
		roleNames.add(QuoteSBConstant.ROLE_SALES);
		roleNames.add(QuoteSBConstant.ROLE_QC_PRICING);
		roleNames.add(QuoteSBConstant.ROLE_PM);
		roleNames.add(QuoteSBConstant.ROLE_CS);
		roleNames.add(QuoteSBConstant.ROLE_QC_OPERATION);
		roleNames.add(QuoteSBConstant.ROLE_CM);
		roleNames.add(QuoteSBConstant.ROLE_INSIDE_SALES);
		roleNames.add(QuoteSBConstant.ROLE_CM_MANAGER);
		roleNames.add(QuoteSBConstant.ROLE_QCP_MANAGER);
		roleNames.add(QuoteSBConstant.ROLE_QCO_MANAGER);
		roleNames.add(QuoteSBConstant.ROLE_QC_DIRECTOR);
		roleNames.add(QuoteSBConstant.ROLE_SALES_MANAGER);
		roleNames.add(QuoteSBConstant.ROLE_SALES_DIRECTOR);
		roleNames.add(QuoteSBConstant.ROLE_SALES_GM);
		roleNames.add(QuoteSBConstant.ROLE_CS_MANAGER);
		roleNames.add(QuoteSBConstant.ROLE_PM_SPM);
		roleNames.add(QuoteSBConstant.ROLE_PM_BUM);
		roleNames.add(QuoteSBConstant.ROLE_PM_MD);
		return roleNames;
	}
}