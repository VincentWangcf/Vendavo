package com.avnet.emasia.webquote.reports.ejb.job;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.UserTransaction;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jboss.ejb3.annotation.TransactionTimeout;

import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.OfflineReport;
import com.avnet.emasia.webquote.entity.Role;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.exception.WebQuoteException;
import com.avnet.emasia.webquote.quote.ejb.MaterialSB;
import com.avnet.emasia.webquote.quote.ejb.MyQuoteSearchSB;
import com.avnet.emasia.webquote.quote.ejb.SystemCodeMaintenanceSB;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.quote.vo.MyQuoteSearchCriteria;
import com.avnet.emasia.webquote.quote.vo.PartMasterCriteria;
import com.avnet.emasia.webquote.quote.vo.QuoteItemVo;
import com.avnet.emasia.webquote.reports.ejb.OfflineReportSB;
import com.avnet.emasia.webquote.reports.ejb.remote.OfflineRptRemote;
import com.avnet.emasia.webquote.reports.offlinereport.MyQuoteExcelFileGenerator;
import com.avnet.emasia.webquote.reports.offlinereport.OfflineReportConstants;
import com.avnet.emasia.webquote.reports.offlinereport.PartMasterExcelFileGenerator;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.utilities.bean.MailInfoBean;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;
import com.avnet.emasia.webquote.utilities.ejb.MailUtilsSB;
import com.avnet.emasia.webquote.utilities.schedule.HATimerService;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-01-23 Upload sequence: 3.
 */
@Stateless
@Remote(OfflineRptRemote.class)
@TransactionManagement(TransactionManagementType.BEAN)
public class OfflineRptJob implements OfflineRptRemote
{
	
	//there is transaction error when using container manageed transaction. real cause unknown, 
	//possibly due to multiple call to MyQuoteSearchSB and UserSB in the loop
    @Resource
    UserTransaction ut;

	private static Logger logger = Logger.getLogger(OfflineRptJob.class
			.getName());

	@EJB
	private transient OfflineReportSB offlineRptSB;
	@EJB
	private transient MyQuoteSearchSB myQuoteSearchSB;
	@EJB
	private transient UserSB userSb;
	@EJB
	private transient MailUtilsSB mailUtilsSB;
	@EJB
	private transient SystemCodeMaintenanceSB sysCodeMaintSB;
	@EJB
	private transient MaterialSB materialSB;
	@EJB
	SysConfigSB sysConfigSB;

	private static final String PROCESSING = "PROCESSING";
	private static final String FINISH = "FINISH";
	private static final String NEXT_LINE_SIGN = "\n";
	private static final String TEMP_FILE_LOCATION ="/app/eap62/webquote/temp/";

	// For control-M
	@TransactionTimeout(value = 36000, unit = TimeUnit.SECONDS)
	@Override
	public int executeTask(long reqestedId)
	{
		logger.info("call executeTask | requestId: "+reqestedId);
		int returnInt = 1;
		try
		{
			 OfflineReport oRpt = offlineRptSB.findById(reqestedId);
			 if(oRpt!=null)
			 {
				 processOfflineRptRequest(oRpt);
			 }
			 else
			 {
				 logger.info("Can 't found that offline report request | requestId: "+reqestedId);
			 }
		}
		catch (Exception e)
		{
			logger.log(Level.SEVERE, "Occurred exception during proceed the offline report request | requestId: "+reqestedId + " " + e.getMessage(),e);
			returnInt = 0;
		}
		return returnInt;
	}

	public void processOfflineRptRequest(OfflineReport oRpt) throws Exception
	{
		logger.info("Start to proceed the offline request :"+ oRpt.getId());
		if (oRpt.getSendFlag() == null || !oRpt.getSendFlag())
		{
			logger.info("offline report request: "+oRpt.getId() +" have not been generated yet." );
			List<OfflineReport> batchList = null;
			try
			{
				logger.info("OfflineReport request: " + oRpt.toString());
				batchList = offlineRptSB.findBatchRequest(oRpt);
				int total = batchList.size();
				int part = 1;
				logger.info("bathcList size: " + total);
				for (OfflineReport rpt : batchList)
				{
					rpt.setType(PROCESSING);
					offlineRptSB.updateOfflineReport(rpt);
				}
				logger.info("update the offline report request status as processing: ");
				for (OfflineReport rpt : batchList)
				{
					List<QuoteItemVo> resultList = new ArrayList<QuoteItemVo>();
					try
					{
						ut.setTransactionTimeout(1000000);
						ut.begin();
						Object obj = reverse(rpt.getSearchBeanValue());
						
						if("com.avnet.emasia.webquote.quote.vo.PartMasterCriteria".equalsIgnoreCase(rpt.getSearchBeanClass()))
						{
							List<Material> resultList2 = new ArrayList<Material>();
							PartMasterCriteria criteria = (PartMasterCriteria) obj;
							resultList2 = materialSB.partMasterEnquiry(criteria, false);
							User user = userSb.findByEmployeeIdLazily(oRpt.getEmployeeId());
							ut.commit();
							List<Role> roles = user.getRoles();
							HSSFWorkbook workbook = null;
							PartMasterExcelFileGenerator myGe = new PartMasterExcelFileGenerator();
                            workbook = myGe.generateFileForQC(oRpt.getBizUnit(), resultList2);
							String fileName = generateFileName();
							sendEmail(rpt, fileName, workbook, total, part, "Part Master Enquiry OfflineReport");
							part++;
							rpt.setSendFlag(true);
							rpt.setType(FINISH);
							offlineRptSB.updateOfflineReport(rpt);
						}
						else
						{
							MyQuoteSearchCriteria criteria = (MyQuoteSearchCriteria) obj;
							resultList = myQuoteSearchSB.search(criteria,Locale.ENGLISH);
							User user = userSb.findByEmployeeIdLazily(oRpt.getEmployeeId());
							ut.commit();
							List<Role> roles = user.getRoles();
							boolean hide = false;
							for (Role role : roles)
							{
								String roleName = role.getName();
								if (roleName.contains("ROLE_CS")
										|| roleName.contains("ROLE_SALES")
										|| roleName.contains("ROLE_INSIDE_SALES"))
								{
									hide = true;
									break;
								}
							}

							HSSFWorkbook workbook = getWorkSheet(rpt, resultList);
							String fileName = generateFileName();

							sendEmail(rpt, fileName, workbook, total, part, "Myquote Search OfflineReport");
							part++;

							rpt.setSendFlag(true);
							rpt.setType(FINISH);
							offlineRptSB.updateOfflineReport(rpt);
						}
						
					}
					catch (Exception e)
					{
						/*logger.log(Level.SEVERE, e.getMessage(), e);*/
						throw e;
					}
					finally
					{
						if (resultList != null)
						{
							resultList.clear();
							resultList = null;
						}

					}

				}
			}
			catch (FileNotFoundException e)
			{
				sendErrorEmail(oRpt.getBizUnit() + NEXT_LINE_SIGN
						+ e.getMessage());
				throw e;
			}
			catch (IOException e1)
			{
				sendErrorEmail(oRpt.getBizUnit() + NEXT_LINE_SIGN
						+ e1.getMessage());
				throw e1;

			}
			catch (Exception e2)
			{
				sendErrorEmail(oRpt.getBizUnit() + NEXT_LINE_SIGN
						+ e2.getMessage());
				throw e2;
			}
			finally
			{
				if (batchList != null)
				{
					batchList.clear();
					batchList = null;
				}
			}

		}
		else
		{
			logger.info("it is a invalid offline report request: "+oRpt.getId()  );
		}
	}

	private String generateFileName()
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hhmmss");
		Date date = new Date();
		return "WebQuote Offline Report - #" + df.format(date) + "#";
	}

	public Object reverse(byte[] rawData)
	{
		try
		{
			ByteArrayInputStream bi = new ByteArrayInputStream(rawData);
			ObjectInputStream oi = new ObjectInputStream(bi);
			return (oi.readObject());
		}
		catch (ClassNotFoundException | IOException e1)
		{
			logger.log(Level.SEVERE, "Exception occoured while reading from inputstream in reverse Method"+e1.getMessage(),e1);
		}

		return null;

	}

	public void sendErrorEmail(String msg)
	{

		// Send error email
		String sender = sysConfigSB.getProperyValue(QuoteSBConstant.ERROR_EMAIL_FROM_ADDRESS);
   		String recipients = sysConfigSB.getProperyValue(QuoteSBConstant.ERROR_EMAIL_TO_ADDRESS);
   		List<String> to = new ArrayList<String>();
		if(null!=recipients && !recipients.isEmpty()) {
			String[] tos = recipients.split(";");
			for(String recipient:tos) {
				to.add(recipient);
			}
		}
		
		MailInfoBean mib = new MailInfoBean();
		String subject = "Offline report error";
		//add by Lenon.Yang(043044) 2016/05/23
		String jbossNodeName = System.getProperty(HATimerService.JBOSS_NODE_NAME);
		if(org.apache.commons.lang.StringUtils.isNotBlank(jbossNodeName)) {
			subject += "(Jboss Node:" + jbossNodeName + ")";
		}
		mib.setMailSubject(subject);
		mib.setMailContent(msg);
		mib.setMailTo(to);
		mib.setMailFrom(sender);
		try
		{
			mailUtilsSB.sendTextMail(mib);
		}
		catch (Exception e)
		{
			logger.log(Level.SEVERE, "Send email failed! : Exception message : "+MessageFormatorUtil.getParameterizedStringFromException(e),e);
		}
	}

	public HSSFWorkbook getWorkSheet(OfflineReport oRpt,
			List<QuoteItemVo> resultList) throws WebQuoteException
	{

		logger.fine("call getWorkSheet");
		HSSFWorkbook workbook = null;
		String methodName = oRpt.getServiceBeanMethod();
		MyQuoteExcelFileGenerator myGe = new MyQuoteExcelFileGenerator();

		switch (methodName)
			{
			case "csSearchOffline":
				workbook = myGe.generateFileForCS(oRpt.getBizUnit(), resultList);

				break;
			case "salesSearchOffline":
				workbook = myGe.generateFileForSale(oRpt.getBizUnit(),
						resultList);
				break;
			case "insideSalesSearchOffline":
				workbook = myGe.generateFileForSale(oRpt.getBizUnit(),resultList);
				break;
			case "qcPricingSearchOffline":
				workbook = myGe.generateFileForQC(oRpt.getBizUnit(), resultList);
				break;
			case "qcOperationSearchOffline":
				workbook = myGe.generateFileForQC(oRpt.getBizUnit(), resultList);
				break;
			case "mmSearchOffline":
				workbook = myGe
						.generateFileForMM(oRpt.getBizUnit(), resultList);
				break;
			case "pmSearchOffline":
				workbook = myGe.generateFileForPM(oRpt.getBizUnit(), resultList);
				break;
			case "bmtSearchOffline":
				workbook = myGe.generateFileForBMT(oRpt.getBizUnit(), resultList);
				break;

			}

		return workbook;
	}

	public void writeToDisc(HSSFWorkbook workbook, String fileName)
	{
		File file = new File(fileName);
		try
		{
			FileOutputStream stream = new FileOutputStream(file);
			workbook.write(stream);
		}
		catch (IOException e)
		{
			logger.log(Level.SEVERE, "Exception in writting file to disk, file name : "+fileName+" , EXception Message"+e.getMessage(),e);		}
		
	}

	public void deleteFilesInDir(String directory)
	{
		File dir = new File(directory);
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++)
		{
			files[i].delete();
		}
	}

	public void sendEmail(OfflineReport oRpt, String fileName,
			HSSFWorkbook workbook, int total, int part, String reportName)
			throws FileNotFoundException, IOException
	{
		logger.info("call offline report sendEmail");
		File xlsFile = null;
		File zipFile = null;
		FileOutputStream fileOut = null;
		String tempPath = sysConfigSB.getProperyValue(OfflineReportConstants.TEMP_DIR);
		try
		{
			MailInfoBean mib = new MailInfoBean();
			String createTime = oRpt.getCreatedOn().toString();
			User user = userSb.findByEmployeeIdLazily(oRpt.getEmployeeId());
			List<String> toList = new ArrayList<String>();
			toList.add(user.getEmailAddress());

			String subject = "WebQuote (" + oRpt.getBizUnit()
					+ ") Offline Report ( Requestd at: " + createTime + " )";
			if (total != 1)
			{
				subject = subject + ", Part " + part + " of " + total;
			}

			mib.setMailSubject(subject);
			mib.setMailContent(reportName);
			mib.setMailTo(toList);
			mib.setMailFrom(sysCodeMaintSB.getBuzinessProperty(
					"OFFLINE_REPORT_FROM", oRpt.getBizUnit()));

			String zipFileName = tempPath+fileName + ".zip";
			String xlsFileName = tempPath+fileName + ".xls";
			xlsFile = new File(xlsFileName);
			fileOut = new FileOutputStream(xlsFile);
			workbook.write(fileOut);
			fileOut.close();
			zipFile = (File)doZipFile(zipFileName, xlsFileName);

			mib.setZipFile(zipFile);

			try
			{
				logger.info("send mail subject :" + subject);
				mailUtilsSB.sendAttachedMail(mib);
			}
			catch (Exception e)
			{
				logger.log(Level.SEVERE,"Send email failed!, Exception message" + MessageFormatorUtil.getParameterizedStringFromException(e),e);
				logger.log(Level.WARNING,"Send email failed!" + e.getMessage());
			}

			logger.log(Level.WARNING,"call sendEmail end");
		}
		catch (IOException e)
		{
			logger.log(Level.SEVERE, "Exception occoured while sending email in sendEmail Method"+e.getMessage(),e);
			throw e;
		}
		finally
		{

			if (fileOut != null)
				fileOut = null;
			if (xlsFile != null)
			{
				if (xlsFile.exists())
					xlsFile.delete();
			}
			if (zipFile != null)
			{
				if (zipFile.exists())
					zipFile.delete();
			}
		}

	}

	private File doZipFile(String zipFileName, String fileNames)
			throws IOException
	{

		  byte[] buffer = new byte[1024];
		  File f = new File(zipFileName);
		  ZipOutputStream outStream = new ZipOutputStream(new FileOutputStream(f));
		  String[] dirs = null!=fileNames?fileNames.split("/"):null;
		  
		  int size = dirs.length;
		  String fileName = fileNames;
		  if(size>1) {
			  fileName = dirs[size-1];
		  }
		
		  FileInputStream in = new FileInputStream(fileNames);
		  outStream.putNextEntry(new ZipEntry(fileName));
		  int bytesRead;
		  //Each chunk of data read from the input stream
		  //is written to the output stream
		  while ((bytesRead = in.read(buffer)) > 0) {
		      outStream.write(buffer, 0, bytesRead);
		  }
		  //Close zip entry and file streams
		  outStream.closeEntry();
		  in.close();
		
		  outStream.close();
		  return f;

	}

}
