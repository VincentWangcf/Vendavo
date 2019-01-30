package com.avnet.emasia.webquote.web.quote.job;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.DateFormat;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timer;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jboss.as.server.CurrentServiceContainer;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.logmanager.Level;
import org.jboss.msc.service.ServiceController;

import com.avnet.emasia.webquote.dp.EJBCommonSB;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.exception.CommonConstants;
import com.avnet.emasia.webquote.exception.WebQuoteRuntimeException;
import com.avnet.emasia.webquote.quote.ejb.SystemCodeMaintenanceSB;
import com.avnet.emasia.webquote.reports.ejb.DailyRITReportSB;
import com.avnet.emasia.webquote.user.ejb.BizUnitSB;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;
import com.avnet.emasia.webquote.utilities.ejb.MailUtilsSB;
import com.avnet.emasia.webquote.utilities.schedule.IScheduleTask;
import com.avnet.emasia.webquote.web.reports.vo.DailyRITReportBean;

/**
 * @author 914975
 * @createdOn 20131204
 */
@Stateless
// @ManagedBean
// @SessionScoped
public class DailyRITReportTask implements IScheduleTask {

	private static final Logger LOG = Logger.getLogger(DailyRITReportTask.class.getName());
	// Fixed defect 43 add two columns "DRMS Authorized GP%" "Reasons for
	// Authorized GP% Change"
	// private final static String[] columnNameArr =
	// {"Form No", "Avnet Quote#","Pending Day","MFR","Bid-To-Buy","Salesman
	// Name","Team","Customer Name(Sold To Party)","Customer Type","End
	// Customer","Requested P/N","Required Qty","EAU","Target Resale","Target
	// Margin %"
	// ,"Avnet Quoted P/N","Quoted Margin %","Avnet Quoted Price","Cost","Cost
	// Indicator","Qty Indicator","Avnet Quoted Qty","Multi-Usage","QC Comment
	// (Avnet Internal Only)","Lead Time (wks)","MPQ","MOQ","MOV","Price
	// Validity","Shipment Validity"
	// ,"MFR Debit #","MFR Quote #","MFR Quote Qty","Rescheduling
	// Window","Cancellation Window","Quotation T&C","Resale Indicator","RFQ
	// Item Attachment","RFQ Form Attachment","PM Comment","Allocation
	// Part","SPR","Project Name","Application","MP Schedule"
	// ,"PP Schedule","DRMS Project ID","MFR DR#","Design Location","Local
	// DR","LOA","Bid-To-Bid","Product Group 2","Competitor Information","BMT
	// Biz","Business Program Type","Remarks","Item Remarks","Reason For
	// Refresh","Segment"
	// ,"Ship To Party","Requester Reference","RFQ Submission Date","Sold To
	// Code","Ship To Code","End Customer Code"};
	@EJB
	private SystemCodeMaintenanceSB sysCodeMaintSB;
	@EJB
	private DailyRITReportSB dailyRITReportSB;
	@EJB
	private BizUnitSB bizutSB;
	@EJB
	private UserSB userSb;

	@EJB
	SysConfigSB sysConfigSB;

	@EJB
	private MailUtilsSB mailUtilsSB;
	
	@EJB
	private EJBCommonSB ejbCommonSB;

	@Override
	@Asynchronous
	@TransactionTimeout(value = 20000, unit = TimeUnit.SECONDS)
	public void executeTask(Timer timer) {
		LOG.info("Daily RIT DRMS report job beginning...");
		try {
					List<BizUnit> bizutLst = bizutSB.findAll();
					if (bizutLst != null) {
						for (BizUnit bz : bizutLst) {
							List<QuoteItem> dailyRITRptList = (List<QuoteItem>) dailyRITReportSB
									.findQuoteItem(bz.getName());
							if (dailyRITRptList != null && dailyRITRptList.size() > 0) {
								List<DailyRITReportBean> beanLst = convertBean(dailyRITRptList);
								HSSFWorkbook workbook = generateExcelFile(beanLst, bz.getName());
								sendEmail(workbook, bz.getName());
							}
						}

			} 
		} catch (Exception e)
		{
			LOG.log(Level.SEVERE, "Exception in executing timer task: "+timer.getInfo().toString()+", Exception message: "+MessageFormatorUtil.getParameterizedStringFromException(e), e);
		}

		LOG.info("Daily RIT DRMS report job ended");
	}

	/**
	 * Converts recipient to bean object.
	 * 
	 * @param qLst
	 * @return
	 */
	public List<DailyRITReportBean> convertBean(List<QuoteItem> qLst) {
		List<DailyRITReportBean> bLst = new ArrayList<DailyRITReportBean>();
		if (qLst != null) {
			for (QuoteItem qi : qLst) {
				DailyRITReportBean b = new DailyRITReportBean();
				b.setFormNo(qi.getQuote().getFormNumber());
				b.setQuoteNumber(qi.getQuoteNumber());
				b.setPendingDay(String.valueOf(qi.getPendingDay()));
				b.setDrmsProjectID(String.valueOf(qi.getDrmsNumber()));
				bLst = ejbCommonSB.convertBean(bLst, qi, b);
			}
		}

		return bLst;
	}
	
	private String generateExcelTitle(String bz) {
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		return "(" + bz + ")" + "Daily RIT Report of DRMS Quote " + df.format(date);
	}

	public Object reverse(byte[] rawData) {
		try {
			ByteArrayInputStream bi = new ByteArrayInputStream(rawData);
			ObjectInputStream oi = new ObjectInputStream(bi);
			return (oi.readObject());
		} catch (ClassNotFoundException|IOException e) {
			LOG.log(Level.SEVERE, "Exception in creating object from raw data"+e.getMessage(), e);
		}

		return null;

	}

	public void writeToDisc(HSSFWorkbook workbook, String fileName) {
		File file = new File(fileName);
		try {
			FileOutputStream stream = new FileOutputStream(file);
			workbook.write(stream);
		} catch (FileNotFoundException e) {
			LOG.log(Level.SEVERE, "Exception in writting file on disc, file name : "+fileName+" , exception message : "+e.getMessage(), e);
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Exception in writting file on disc, file name : "+fileName+" , exception message : "+e.getMessage(), e);
		}
	}

	public void sendEmail(HSSFWorkbook workbook, String bzStr) throws FileNotFoundException, IOException {
		final String subject = generateExcelTitle(bzStr);
		ejbCommonSB.sendEmail(workbook, bzStr, "outstandingITReportTask", subject);

	}
	/**
	 * Generate a report file.
	 * 
	 * @param resultList
	 * @param bzStr
	 * @return
	 */
	public HSSFWorkbook generateExcelFile(List resultList, String bzStr) {
		LOG.fine("Generate excel file!");
		HSSFWorkbook wb = new HSSFWorkbook();
		String sheetName = generateExcelTitle(bzStr);
		return ejbCommonSB.generateExcelFile(wb, sheetName,resultList, bzStr );
	}

	// todo
	public void testTask() {
		LOG.info("Daily RIT report job beginning...");
		try {
			List<BizUnit> bizutLst = bizutSB.findAll();
			if (bizutLst != null) {
				for (BizUnit bz : bizutLst) {
					List<QuoteItem> dailyRITRptList = (List<QuoteItem>) dailyRITReportSB.findQuoteItem(bz.getName());
					if (dailyRITRptList != null && dailyRITRptList.size() > 0) {
						List<DailyRITReportBean> beanLst = convertBean(dailyRITRptList);
						HSSFWorkbook workbook = generateExcelFile(beanLst, bz.getName());
						sendEmail(workbook, bz.getName());
					}
				}
			}
		} catch (Exception e)
		{
			LOG.log(Level.SEVERE, "Exception in Daily RIT report, Exception message: "+MessageFormatorUtil.getParameterizedStringFromException(e), e);;
		}
		LOG.info("Daily RIT report job ended.");
	}
	
}
