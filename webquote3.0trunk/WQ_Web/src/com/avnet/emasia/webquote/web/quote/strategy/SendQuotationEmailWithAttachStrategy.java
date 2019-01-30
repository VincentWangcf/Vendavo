/**
 * 
 */
package com.avnet.emasia.webquote.web.quote.strategy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.mail.util.ByteArrayDataSource;
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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.jboss.logmanager.Level;

import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.ExchangeRate;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.ejb.SystemCodeMaintenanceSB;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.strategy.StrategyFactory;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilites.web.common.CacheUtil;
import com.avnet.emasia.webquote.utilites.web.util.DeploymentConfiguration;
import com.avnet.emasia.webquote.utilites.web.util.DownloadUtil;
import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil;
import com.avnet.emasia.webquote.utilities.DateUtils;
import com.avnet.emasia.webquote.utilities.bean.MailInfoBean;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;
import com.avnet.emasia.webquote.utilities.ejb.MailUtilsSB;
import com.avnet.emasia.webquote.web.quote.constant.QuoteConstant;
import com.avnet.emasia.webquote.web.quote.vo.QuotationEmailVO;
import com.avnet.emasia.webquote.constants.Constants;
import com.avnet.emasia.webquote.dp.EJBCommonSB;;

/**
 * @author 042659
 *
 */
public class SendQuotationEmailWithAttachStrategy implements SendQuotationEmailStrategy {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6147964591789764357L;
	private SysConfigSB sysConfigSB;
	private UserSB userSB;
	private MailUtilsSB mailUtilsSB;

	private SystemCodeMaintenanceSB systemCodeMaintenanceSB;

	private EJBCommonSB ejbCommonSB;

	private CacheUtil cacheUtil;

	private static final Logger LOGGER = Logger.getLogger(SendQuotationEmailWithAttachStrategy.class.getName());

	private final static DecimalFormat DF2 = new DecimalFormat("#.00");
	private final static DecimalFormat DF3 = new DecimalFormat("#");
	private final static DecimalFormat DF5 = new DecimalFormat("#");

	/**
	 * 
	 */
	public SendQuotationEmailWithAttachStrategy() {
		initSB();

	}

	/**
	 * @Description: init the dependency SBs @author 042659 @return void @throws
	 */
	private void initSB() {
		try {
			Map<String, Object> sBMap = getLookupSB();
			userSB = (UserSB) sBMap.get("userSB");
			sysConfigSB = (SysConfigSB) sBMap.get("sysConfigSB");
			mailUtilsSB = (MailUtilsSB) sBMap.get("mailUtilsSB");
			systemCodeMaintenanceSB = (SystemCodeMaintenanceSB) sBMap.get("systemCodeMaintenanceSB");
			sysConfigSB = (SysConfigSB) sBMap.get("sysConfigSB");
			cacheUtil = (CacheUtil) sBMap.get("cacheUtil");
			
			ejbCommonSB = (EJBCommonSB) sBMap.get("ejbCommonSB");

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception in initSB, exception message : " + e.getMessage(), e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.avnet.emasia.webquote.web.quote.strategy.SendQuotationEmailStrategy#
	 * sendQuotationEmail(com.avnet.emasia.webquote.web.quote.vo.
	 * QuotationEmailVO)
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
		content += "Quotation is attached. Good Selling!<br/><br/>";
		content += "RFQ Form: " + vo.getFormNumber() + "<br/>";
		content += "<a href=\"" + salesLink + "?quoteId=" + vo.getQuoteId() + "\">" + salesLink + "?quoteId=" + vo.getQuoteId() + "</a><br/><br/>";
		if (vo.getCcEmails() != null && vo.getCcEmails().size() > 0) {
			content += "Dear CS,<br/><br/>";
			content += "RFQ Form: " + vo.getFormNumber() + "<br/>";
			content += "<a href=\"" + csLink + "?quoteId=" + vo.getQuoteId() + "\">" + csLink + "?quoteId=" + vo.getQuoteId() + "</a><br/><br/>";
		}
		content += "Best Regards," + "<br/>";
		content += vo.getSender() + "<br/>";
		mailInfoBean.setMailContent(content);

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			getQuoteTemplateBySoldTo(vo.getSoldToCustomer(), vo.getQuote(), vo.isSubmissionDateFromQuote()).write(baos);
			byte[] bytes = baos.toByteArray();
			ByteArrayDataSource ds = new ByteArrayDataSource(bytes, "application/excel");
			mailInfoBean.setFileByteArray(ds);

			String fileName = vo.getFileName().replaceAll("\n", "") + " #" + DateUtils.getDefaultDateStrEmailTimeStamp() + "#.xls";
			mailInfoBean.setFileName(fileName);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception in setting message info for email sent from: " + mailInfoBean.getMailFrom() + ", Subject: "
					+ mailInfoBean.getMailSubject() + ", exception message : " + e.getMessage(), e);
		}
		try {
			mailUtilsSB.sendHtmlMail(mailInfoBean);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception for email sent from: " + mailInfoBean.getMailFrom() + ", Subject: " + mailInfoBean.getMailSubject()
					+ ", Email Sending Error : " + e.getMessage(), e);
		}

		LOGGER.info("sendQuotationEmail(QuotationEmailVO vosendQuotationEmail)  end");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.avnet.emasia.webquote.web.quote.strategy.SendQuotationEmailStrategy#
	 * getQuoteTemplateBySoldTo(com.avnet.emasia.webquote.entity.Customer,
	 * com.avnet.emasia.webquote.entity.Quote)
	 */
	@Override
	public HSSFWorkbook getQuoteTemplateBySoldTo(Customer soldToCustomer, Quote quote, boolean isSubmissionDateFromQuote) {
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
			fillVatToXLS(quote, sheet);
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
						if (isSubmissionDateFromQuote) {
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
					case "F9":
						String totalShipTo = "";
						if (items != null) {
							for (QuoteItem item : items) {
								if (item.getShipToCustomer() != null) {
									String shipToCustomerName = DownloadUtil.getCustomerFullName(item.getShipToCustomer());
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
			
			Collections.sort(quoteItems,  new Comparator<QuoteItem>() {
	            public int compare(QuoteItem e1, QuoteItem e2) {
	            	if (e1.getQuote().getId() == e2.getQuote().getId()) {
						return (int) (e2.getQuote().getId() - e1.getQuote().getId());
					} else {
						return (int) (e2.getId() - e1.getId());
					}
	            }
			});
			int termsIndex = QuoteConstant.QUOTE_ITEM_ROW_START + 3;
			if (quoteItems.size() > 1) {
				for (int i = 1; i < quoteItems.size(); i++) {
					QuoteUtil.copyRow(sheet, QuoteConstant.QUOTE_ITEM_ROW_START, QuoteConstant.QUOTE_ITEM_ROW_START + i);
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

				HSSFCell cellRfqCurr = row.getCell(j++);
				// sales cost
				HSSFCell cellSaleCost = row.getCell(j++);
				// suggested_resale
				HSSFCell cellSuggestedResale = row.getCell(j++);
				HSSFCell cellQuotedPrice = row.getCell(j++);
				HSSFCell cellTargetPrice = row.getCell(j++);

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

				HSSFCell cellQuotedExcRate = row.getCell(j++);
				HSSFCell cellFinalCurr = row.getCell(j++);

				// finalQuotationPrice
				HSSFCell cellFinalQuotationPrice = row.getCell(j++);
				HSSFCell cellFinalExcRate = row.getCell(j++);
				HSSFCell cellDollarLink = row.getCell(j++);
				HSSFCell cellRateRemaks = row.getCell(j++);

				if (quoteItem.isHightlighted()) {
					cellQuoteNumber.setCellStyle(normalFontBlueBackStyle);
					cellQuoteStage.setCellStyle(normalFontBlueBackStyle);
					// cellFirstRfqCode.setCellStyle(normalFontBlueBackStyle);
					cellMfr.setCellStyle(normalFontBlueBackStyle);
					cellMfrName.setCellStyle(normalFontBlueBackStyle);
					cellRequestedPartNumber.setCellStyle(normalFontBlueBackStyle);
					cellQuotedPartNumber.setCellStyle(normalFontBlueBackStyle);

					cellAqcc.setCellStyle(normalFontBlueBackStyle);
					cellRfqCurr.setCellStyle(normalFontBlueBackStyle);
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
					/*
					 * if (postQuotationTask instanceof PostQuotationTask ||
					 * postQuotationTask instanceof CommonBean) {
					 * cellEffectiveDate.setCellStyle(normalFontBlueBackStyle);
					 * } else if (postQuotationTask instanceof
					 * AsyncPostQuotationSB) {
					 * cellEffectiveDate.setCellStyle(normalFontBlueBackStyle);
					 * }
					 */
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

					cellQuotedExcRate.setCellStyle(normalFontBlueBackStyle);
					cellFinalCurr.setCellStyle(normalFontBlueBackStyle);
					cellFinalExcRate.setCellStyle(normalFontBlueBackStyle);
					cellDollarLink.setCellStyle(normalFontBlueBackStyle);
					cellRateRemaks.setCellStyle(normalFontBlueBackStyle);

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
					cellRfqCurr.setCellStyle(redFontPinkBackWrapStyle);
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
					cellQuotedExcRate.setCellStyle(normalFontPinkBackStyle);
					cellFinalCurr.setCellStyle(normalFontPinkBackStyle);
					cellFinalExcRate.setCellStyle(normalFontPinkBackStyle);
					cellDollarLink.setCellStyle(normalFontPinkBackStyle);
					cellRateRemaks.setCellStyle(normalFontPinkBackStyle);

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
					cellRfqCurr.setCellValue(QuoteConstant.DEFAULT_BLANK);
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

					cellQuotedExcRate.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cellFinalCurr.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cellFinalExcRate.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cellDollarLink.setCellValue(QuoteConstant.DEFAULT_BLANK);
					cellRateRemaks.setCellValue(QuoteConstant.DEFAULT_BLANK);

				}

				cellno.setCellValue("No." + (i + 1));
				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)) {
					cellQuoteNumber.setCellValue(quoteItem.getQuoteNumber());
				}

				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)) {
					cellQuoteStage.setCellValue(quoteItem.getStage());
				}

				/*
				 * if (quoteItem.getStage() != null &&
				 * quoteItem.getStage().equals(QuoteSBConstant.
				 * QUOTE_STAGE_FINISH)) {
				 * cellFirstRfqCode.setCellValue(quoteItem.getFirstRfqCode()); }
				 */

				if (quoteItem.getRequestedMfr() != null) {
					cellMfr.setCellValue(quoteItem.getRequestedMfr().getName());
					String mfrDescription = quoteItem.getRequestedMfr().getDescription();
					if (mfrDescription != null) {
						String sub = quoteItem.getRequestedMfr().getName() + " ";
						if (mfrDescription.startsWith(sub)) {
							cellMfrName.setCellValue(quoteItem.getRequestedMfr().getDescription().substring(sub.length()));
						}
					}
				}

				if (quoteItem.getRequestedPartNumber() != null) {
					cellRequestedPartNumber.setCellValue(quoteItem.getRequestedPartNumber());
				}

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

				if (quoteItem.getRfqCurr() != null) {

					cellRfqCurr.setCellValue(quoteItem.getRfqCurr().name());
				}

				// set values for sales cost
				if (quoteItem.getStage() != null && quoteItem.getSalesCost() != null && quoteItem.isSalesCostFlag() 
						&& quoteItem.convertToRfqValue(quoteItem.getSalesCost()) !=null) {
					cellSaleCost.setCellValue(DF5.format(quoteItem.convertToRfqValue(quoteItem.getSalesCost())));
				}

				// suggested_resale
				if (quoteItem.getStage() != null && quoteItem.isSalesCostFlag() && quoteItem.getSuggestedResale() != null 
						&& quoteItem.convertToRfqValue(quoteItem.getSuggestedResale())!=null) {
					cellSuggestedResale.setCellValue(DF5.format(quoteItem.convertToRfqValue(quoteItem.getSuggestedResale())));
				}
				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)
						&& quoteItem.getQuotedPrice() != null && quoteItem.convertToRfqValueWithDouble(quoteItem.getQuotedPrice())!=null)
					cellQuotedPrice.setCellValue(DF5.format(quoteItem.convertToRfqValueWithDouble(quoteItem.getQuotedPrice())));
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

				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH) && quoteItem.getPmoq() != null)
					cellPmoq.setCellValue(quoteItem.getPmoq());

				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH) && quoteItem.getMpq() != null)
					cellMpq.setCellValue(quoteItem.getMpq());

				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH) && quoteItem.getMoq() != null)
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

				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)
						&& quoteItem.getTermsAndConditions() != null) {
					String quoteItemTermsAndCondition = quoteItem.getTermsAndConditions().trim().replaceAll("[\\n\\r]+$", "");
					if (!quoteItemTermsAndCondition.equalsIgnoreCase("NO")) {
						String termsAndCondition = termsAndConditions.get(quoteItemTermsAndCondition);
						if (termsAndCondition == null) {
							termsAndConditions.put(quoteItemTermsAndCondition, "" + QuoteUtil.getTermId(terms));
							cellTermsAndConditions.setCellValue("Please refer to Special T&C " + QuoteUtil.getTermId(terms));
							terms++;
						} else {
							cellTermsAndConditions.setCellValue("Please refer to Special T&C " + termsAndConditions.get(quoteItemTermsAndCondition));
						}

					}

				}

				if (quoteItem.getStage() != null && quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH)
						&& quoteItem.getAllocationFlag() != null) {
					cellAllocationFlag
							.setCellValue(quoteItem.getAllocationFlag().booleanValue() ? QuoteConstant.OPTION_YES : QuoteConstant.OPTION_NO);
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

				if (quoteItem.getExRateRfq() != null && quoteItem.getExRateBuy() != null) {
					cellQuotedExcRate.setCellValue(quoteItem.getExRateRfq().multiply(quoteItem.getHandling())
							.divide(quoteItem.getExRateBuy(), Constants.SCALE, Constants.ROUNDING_MODE).doubleValue());

				}

				if (quoteItem.getFinalCurr() != null) {
					cellFinalCurr.setCellValue(quoteItem.getFinalCurr().name());

				}

				if (quoteItem.getExRateFnl() != null && quoteItem.getExRateBuy() != null) {
					cellFinalExcRate.setCellValue(quoteItem.getExRateFnl().multiply(quoteItem.getHandling())
							.divide(quoteItem.getExRateBuy(), Constants.SCALE, Constants.ROUNDING_MODE).doubleValue());
				}

				cellDollarLink.setCellValue(quoteItem.getQuote().isdLinkFlag() == true ? "Yes" : "No");

				if (!QuoteUtil.isEmpty(quoteItem.getRateRemarks())) {
					cellRateRemaks.setCellValue(quoteItem.getRateRemarks());
				}

			}

			List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(termsAndConditions.entrySet());

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

			/*FilterExcelColumnsStrategy filterExcelColumnsStrategy = (FilterExcelColumnsStrategy) cacheUtil
					.getStrategy(FilterExcelColumnsStrategy.class, quote.getBizUnit().getName());*/
			
			FilterExcelColumnsStrategy filterExcelColumnsStrategy =(FilterExcelColumnsStrategy) StrategyFactory.getSingletonFactory()
					.getStrategy(FilterExcelColumnsStrategy.class,quote.getBizUnit().getName(),
							this.getClass().getClassLoader());
			filterExcelColumnsStrategy.deleteColumns(sheet, new int[] { 36, 37 });

		}

		return wb;

	}

	/**
	 * @Description: TODO @author 042659 @param bizUnitName @return @return
	 * HSSFWorkbook @throws
	 */
	private HSSFWorkbook getHSSFWorkbook(String bizUnitName) {
		FileInputStream fileIn;
		HSSFWorkbook wb = null;
		String fileInputStream = null;
		String templateName = null;
		try {
			// Defect #4
			templateName = systemCodeMaintenanceSB.getQuotationTemplate(bizUnitName);
			fileInputStream = DeploymentConfiguration.configPath + File.separator + templateName;

			/*
			 * //TODO try { String address =
			 * InetAddress.getLocalHost().getHostName().toString();
			 * if("cis2115vmts".equalsIgnoreCase(address)) { fileInputStream =
			 * "C:\\david\\sharefolder\\tempd"+File.separator+templateName; } }
			 * catch (Exception e) { e.printStackTrace(); }
			 */
			//fileIn = new FileInputStream(fileInputStream);
			 fileIn = new
			 FileInputStream("C:/Users/046755/Desktop/excel/QuotationTemplate.xls");
			// fileIn = new FileInputStream("C:/excel/QuoteTemplate.xls");
			// fileIn = new
			// FileInputStream("C:/Damon/WQTemplate/QuotationTemplate.xls");
			wb = new HSSFWorkbook(fileIn);
		} catch (FileNotFoundException e) {
			LOGGER.log(Level.SEVERE, "exception in file not found for business unit : " + bizUnitName + " , exception message : " + e.getMessage(),
					e);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE,
					"exception in getting HSS work book found for business unit : " + bizUnitName + " , exception message : " + e.getMessage(), e);
		}
		return wb;
	}

	public String getUrl() {
		String url = sysConfigSB.getProperyValue(QuoteConstant.WEBQUOTE2_DOMAIN);
		return url;
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
				cell.setCellValue(sdf.format(submitDate));
		}
	}

	public void fillVatToXLS(Quote quote, HSSFSheet sheet) {
		String vat = null;
		if (quote.getQuoteItems().size() > 0) {

			QuoteItem tmpItem = null;
			for (QuoteItem item : quote.getQuoteItems()) {
				if (QuoteConstant.QUOTE_STAGE_FINISH.equals(item.getStage()) && item.getVat() != null) {
					if (tmpItem == null) {
						tmpItem = item;
					} else {
						if (tmpItem.getSentOutTime() != null && item.getSentOutTime() != null
								&& tmpItem.getSentOutTime().before(item.getSentOutTime())) {
							tmpItem = item;
						}
					}
				}

			}

			if (tmpItem != null && tmpItem.getVat().subtract(new BigDecimal(1)).multiply(new BigDecimal(100)).compareTo(new BigDecimal(0)) != 0) {
				vat = tmpItem.getVat().subtract(new BigDecimal(1)).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).toString();

			}
		}

		if (null != vat) {
			HSSFRow rowRate = sheet.getRow(21);
			HSSFCell cell = rowRate.getCell(3);

			vat = cell.getStringCellValue() + " " + vat + "%";
			cell.setCellValue(vat);
		} else {
			HSSFRow rowRate2 = sheet.getRow(21);
			HSSFCell cell2 = rowRate2.getCell(3);
			cell2.setCellValue("");

		}

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