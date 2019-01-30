/** * @author  Damon。Chen
 * @date Created Date：2016年9月18日 上�?�9:13:42 *
 */

package com.avnet.emasia.webquote.web.quote;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.*;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.primefaces.model.UploadedFile;

import com.avnet.emasia.webquote.constants.ActionEnum;
import com.avnet.emasia.webquote.entity.DataAccess;
import com.avnet.emasia.webquote.entity.ExchangeRate;
import com.avnet.emasia.webquote.entity.Money;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.SalesCostType;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.exception.AppException;
import com.avnet.emasia.webquote.exception.CommonConstants;
import com.avnet.emasia.webquote.quote.ejb.QuoteSB;
import com.avnet.emasia.webquote.quote.ejb.SAPWebServiceSB;
import com.avnet.emasia.webquote.strategy.StrategyFactory;
import com.avnet.emasia.webquote.utilites.web.common.Constants;
import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil;
import com.avnet.emasia.webquote.utilities.DBResourceBundle;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.utilities.bean.MailInfoBean;
import com.avnet.emasia.webquote.web.quote.constant.QuoteConstant;
import com.avnet.emasia.webquote.web.quote.strategy.SendQuotationEmailStrategy;
import com.sap.document.sap.soap.functions.mc_style.TableOfZquoteMsg;
import com.sap.document.sap.soap.functions.mc_style.ZquoteMsg;

@Stateless
public class AsynReviseFinalQuotedPriceBean extends CommonBean {

	private static final long serialVersionUID = 1977637061836207036L;
	private static final Logger LOG = Logger.getLogger(AsynReviseFinalQuotedPriceBean.class.getName());
	private static final String SUCCESS_SUBJECT = "Mass Upload Results of Final Quotation Price ";
	private static final String SUCCESS_EMAILBODY = "Dear {salesperson_name}," + "<br/>" + "<br/>" + "<br/>"
			+ "The results of the Final Quotation Price upload is as followed." + "<br/>" + "~ Number of Quotes processed: {proccess_number}" + "<br/>"
			+ "~ Pass Quotes: {pass_number}" + "<br/>" + "~ Fail Quotes: {fail_number}" + "<br/>" +"<br/>" + "<br/>" + "<br/>" + "Best Regards," + "<br/>"
			+ "{region} Quote Centre";
	private static final String FAIL_SUBJECT = "Mass Upload Results of Final Quotation Price (Fail)";
	
//	private static final String FAIL_EMAILBODY = "Dear {salesperson_name}," + "<br/>" + "<br/>" + "<br/>"
//			+ResourceMB.getText("wq.message.massUploadUnsuccessful")+":" + "<br/>" +"<br/>" + "{application_errors}"+"<br/>"
//			+ResourceMB.getText("wq.message.reportHelpDeskMsg")+"." + "<br/>" + "<br/>" + "Best Regards," + "<br/>"
//			+ "{region} Quote Centre";
	

	@EJB
	QuoteSB quoteSB;
	@EJB
	SAPWebServiceSB webServiceSB;


	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@TransactionTimeout(value = 36000, unit = TimeUnit.SECONDS)
	public Future<String>  massRevise(UploadedFile quoteExcel, User user,boolean isInsideSale,Locale locale) {

		LOG.info("begin massRevice..................");
		XSSFWorkbook workbook;
		try {
			InputStream file = (InputStream) quoteExcel.getInputstream();
			workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheetAt(0);
			XSSFSheet sheet1 = workbook.createSheet();
			int indexForCopyRow = 0;
			copyRows(sheet1, sheet.getRow(0), indexForCopyRow, "ERROR");
			Iterator<Row> rowIterator = sheet.iterator();
			List<String> errorMsgs = new ArrayList<String>();
			LinkedHashMap<Row, String> rowMap = new LinkedHashMap<Row, String>();
			List<HashMap<String,Row>> pendingRowList = new ArrayList<HashMap<String,Row>>();
			Double finalQuotationPrice = 0d;
			String quoteNumberStr =null;

			//DecimalFormat df = (DecimalFormat)DecimalFormat.getNumberInstance(locale);
			//df.setMaximumFractionDigits(5);
			DecimalFormat df = new DecimalFormat("#,###,###,##0.00000");
			int proccessNumber = 0;
			int successCount = 0;
			int failCount = 0;

			List<DataAccess> dataAccessList =userSB.findAllDataAccesses(user);
			
		    List<User> saleslist = new ArrayList<User>();
			if (isInsideSale) {
				saleslist = userSB.findAllSalesForCs(user);
			} else {
				saleslist = userSB.findAllSubordinates(user, 10);
			}
			saleslist.add(user);
			List<QuoteItem> pendingQuoteItems = new ArrayList<QuoteItem>();
			LOG.info("the Last Row Number is :"+sheet.getLastRowNum());
			int rownumber =sheet.getLastRowNum();
			if(rownumber>5001){
			LOG.log(Level.INFO, "the number of row is "+rownumber);
			String errorMsg =getLoclaizedText("wq.message.maxRecords",locale)+".";
			sendEmailForUnsuccessCase(errorMsg, user,locale);
			return new AsyncResult<String>(extractExceptionMsg(errorMsg,locale));
			}
			while (rowIterator.hasNext()) {
				QuoteItem item = null;
				finalQuotationPrice = 0d;
				Row row = rowIterator.next();
				if (row.getRowNum() == 0) {
					AsyncResult<String> resultCheck = this.checkHeaderRow(row, user, locale);
					if(resultCheck != null) return resultCheck;
					continue;
				}
				//count processcessNumber 
				proccessNumber++;
				
				Cell cellQuoteNumber = row.getCell(2);
				if (cellQuoteNumber == null) {
					rowMap.put(row, getLoclaizedText("wq.message.missingAvnetQuote",locale)+"#.");
					failCount++;
					continue;
				}
				quoteNumberStr = cellQuoteNumber.getStringCellValue().trim();

				// LOG.info(cellFinalPrice.getStringCellValue());
				// LOG.info("" + cellFinalPrice.getCellType());

				if (quoteNumberStr == null || "".equals(quoteNumberStr.trim())) {
					rowMap.put(row, getLoclaizedText("wq.message.missingAvnetQuote",locale)+"#.");
					failCount++;
					// logForRowMap(row, "is null");
					continue;
				} else {

					item = quoteSB.findQuoteItemByQuoteNumber(quoteNumberStr);
					if (item == null) {
						rowMap.put(row, MessageFormatorUtil.getParameterizedString(locale,"wq.message.avnetQuoteNotExist", quoteNumberStr));
						failCount++;
						// logForRowMap(row,quoteNumberStr);
						continue;
					} else {
						
						if (item.getStage() == null || !(item.getStage().trim().equals("FINISH"))) {
							rowMap.put(row,getLoclaizedText("wq.message.notFinshQuote",locale)+".");
							failCount++;
							// logForRowMap(row,item.getQuoteNumber());
							continue;
						}

						if (item.getQuote().isdLinkFlag() && (item.getRfqCurr().name() != item.getFinalCurr().name())) {
							rowMap.put(row,getLoclaizedText("wq.message.notAllowChildDLink_FinalQuote",locale));
							failCount++;
							LOG.info(getLoclaizedText("wq.message.notAllowChildDLink_FinalQuote",locale));
							// logForRowMap(row,item.getQuoteNumber());
							continue;
						}

						// begin validate Final Price
						Cell cellFinalPrice = row.getCell(29);	//201810 (whwong) change from 25 to 29 due to excel export had changed
						if (cellFinalPrice == null) {
							rowMap.put(row,getLoclaizedText("wq.message.missingFinalQuotationPrice",locale));
							failCount++;
							// logForRowMap(row,
							// "haven't check quoteNumber yet");
							continue;
						}
						if (cellFinalPrice.getCellType() == Cell.CELL_TYPE_NUMERIC) {
							finalQuotationPrice = cellFinalPrice.getNumericCellValue();
						} else if (cellFinalPrice.getCellType() == Cell.CELL_TYPE_STRING) {
							if (cellFinalPrice.getStringCellValue() != null && !"".equals(cellFinalPrice.getStringCellValue())) {

								if (!Pattern.compile("^(([1-9]+)|([0-9]+\\.[0-9]{1,10}))$").matcher(cellFinalPrice.getStringCellValue().replace(",", "")).matches()) {
									rowMap.put(row, getLoclaizedText("wq.message.quotationPriceNotNumeric",locale)+".");
									failCount++;
									// logForRowMap(row,
									// "haven't check quoteNumber yet");
									continue;
								}
								finalQuotationPrice = Double.valueOf(cellFinalPrice.getStringCellValue().replace(",", ""));
							}

						}

						if (finalQuotationPrice == null || finalQuotationPrice <= 0) {
							rowMap.put(row, getLoclaizedText("wq.message.missingFinalQuotationPrice",locale));
							failCount++;
							// logForRowMap(row,
							// "haven't check quoteNumber yet");
							continue;
						}
						// end validate Final Price

						if (!this.isHaveRightToRevise(item,saleslist,dataAccessList)) {
							rowMap.put(row,getLoclaizedText("wq.message.saleUnAuthorized",locale));
							failCount++;
							// logForRowMap(row,item.getQuoteNumber());
							continue;
						}
						
						if (item.isSalesCostFlag() == true && item.getSalesCostType() != null && (item.getSalesCost() != null)
								&& (!item.getSalesCostType().name().equals(SalesCostType.NonSC))) {

							if (new BigDecimal(finalQuotationPrice).compareTo(item.getSalesCost()) < 0) {
								rowMap.put(row, getLoclaizedText("wq.message.finalPriceGreaterEqualSC", locale));
								failCount++;
								continue;
							}
						} else {

							String resaleIndicator = item.getResaleIndicator();
							//Multi Currency Project 201810 (whwong)
							//Double quotedPrice = item.getQuotedPrice();
							//convert to RFQ
							Double quotedPrice = item.convertToRfqValueWithDouble(item.getQuotedPrice()).doubleValue();
							
							if (resaleIndicator == null) {
								resaleIndicator = "00AA";
							}

							if (resaleIndicator.trim().length() != 4) {
								rowMap.put(row,
										MessageFormatorUtil.getParameterizedString(locale, "wq.message.invalidResaleInd", item.getResaleIndicator()));
								failCount++;
								// logForRowMap(row,item.getQuoteNumber());
								continue;
							} else {

								try {
									String lowPart = resaleIndicator.substring(0, 2);
									String highPart = resaleIndicator.substring(2, 4);

									double highPrice = 0.0;
									double lowPrice = 0.0;
									lowPrice = quotedPrice * (1 - Double.parseDouble(lowPart) / 100);

									if (highPart.equalsIgnoreCase("AA")) {
										highPrice = 9999999999.0;
									} else {
										highPrice = quotedPrice * (1 + Double.parseDouble(highPart) / 100);
									}

									if (finalQuotationPrice < lowPrice || finalQuotationPrice > highPrice) {
										Object[] objArr = { String.valueOf(quotedPrice), df.format(lowPrice), df.format(highPrice), resaleIndicator };
										rowMap.put(row,
												MessageFormatorUtil.getParameterizedString(locale, "wq.message.invalidAvnetQuotePrice", objArr));
										failCount++;
										// logForRowMap(row,item.getQuoteNumber());
										continue;
									}
								} catch (Exception ex) {
									LOG.log(Level.INFO, ex.getMessage(), ex);
									throw new Exception(CommonConstants.WQ_EJB_MASTER_DATA_QUOTATION_PRICE_IS_BLANK);
								}

							}
						}
					}
				}

				item.setFinalQuotationPrice(finalQuotationPrice);
				//Multi Currency 201810 (whwong)
				if (item.getFinalCurr() == null) item.setFinalCurr(item.getRfqCurr());
				if (item.getFinalCurr().equals(item.getRfqCurr())) item.setExRateFnl(item.getExRateRfq());
				else if (item.getExRateFnl() == null) {
					ExchangeRate exRates[];
					exRates = Money.getExchangeRate(item.getBuyCurr(), item.getFinalCurr(), item.getQuote().getBizUnit(), item.getSoldToCustomer(), new Date());						
					
					if (exRates[0]!=null && exRates[1]!=null) item.setExRateFnl(exRates[1].getExRateTo());
				}

				item.setLastUpdatedBy(user.getEmployeeId());
				item.setLastUpdatedName(user.getName());
				item.setLastUpdatedOn(com.avnet.emasia.webquote.utilites.web.util.QuoteUtil.getCurrentTime());
				item.setAction(ActionEnum.MYQUOTE_MASS_REVISE_FINAL_QUOTE_PRICE.name());
				pendingQuoteItems.add(item);
				LOG.info("Add Quote Number to pendingQuoteItems:" + item.getQuoteNumber());
				HashMap<String, Row> hashMap = new HashMap<String, Row>();
				hashMap.put(quoteNumberStr, row);
				pendingRowList.add(hashMap);
				successCount++;

			}

			Map<Quote, List<QuoteItem>> quoteItemByFormNo = pendingQuoteItems.stream().collect(groupingBy(qi->qi.getQuote()));
			
			for (Map.Entry<Quote, List<QuoteItem>> entry : quoteItemByFormNo.entrySet()) {
/* with attachment for all region, so no need to get BizUnit
				String strategyKey=user.getDefaultBizUnit().getName();
				if(entry.getValue().get(0).getQuote().isdLinkFlag()){
					strategyKey=Constants.DEFAULT;
				}
*/				
				String strategyKey=Constants.DEFAULT;

				SendQuotationEmailStrategy strategy =(SendQuotationEmailStrategy) StrategyFactory.getSingletonFactory()
						.getStrategy(SendQuotationEmailStrategy.class, strategyKey,
								this.getClass().getClassLoader());
				strategy.sendQuotationEmail(strategy.genQuotationEmailVO(entry.getValue().get(0).getQuote(), user));
			}
			
			
			try {
				if (pendingQuoteItems.size() > 0) {
					LOG.info("begin save: " + pendingQuoteItems.size());
					quoteSB.saveQuoteItemsWithParamItem(pendingQuoteItems);
				}

			} catch (Exception e) {
				LOG.log(Level.WARNING, "Error occured when saving quote item for uploaded"
						+ "file:"+quoteExcel.getFileName()+", Exception message: "+MessageFormatorUtil.getParameterizedStringFromException(e));
				String errorMsg = e.getMessage() + "  " + e.getCause().getMessage();
				sendEmailForUnsuccessCase(errorMsg, user,locale);
				return new AsyncResult<String>(extractExceptionMsg(errorMsg,locale));
			}

			Iterator<Entry<Row, String>> it = rowMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Row, String> e = it.next();
				//LOG.info("key:" + e.getKey() + "===== value:" + e.getValue());
				this.copyRows(sheet1, e.getKey(), ++indexForCopyRow, e.getValue());
			}
            
			//send to SAP
			LinkedHashMap<String,String> msgMap =new  LinkedHashMap<String, String>();
			try {
				StringBuffer sb = new StringBuffer("");
				String se = "";
				String quoteN = null;
				String existsMsg = null;
				if (pendingQuoteItems.size() > 0) {

					TableOfZquoteMsg tableMsg = webServiceSB.createSAPQuote(pendingQuoteItems);
					List<ZquoteMsg> msgs = tableMsg.getItem();
					errorMsgs = new ArrayList<String>();
					for (ZquoteMsg msg : msgs) {
						// LOG.info("id:" + msg.getId() + "<br/>" + "type:" +
						// msg.getType() + "<br/>" + "number:" + msg.getNumber()
						// + "<br/>" + "message:"
						// + msg.getMessage());
						if (!msg.getType().equalsIgnoreCase("S")) {
							errorMsgs.add(msg.getMessage());
						}
					}

					if (errorMsgs.size() != 0) {

						for (String s : errorMsgs) {
							if (sb.toString().equals("")) {
								sb.append(s);
							} else {
								sb.append("</br>");
								sb.append(s);
							}

							quoteN = s.substring(s.indexOf("RFQ#")+4);
							//LOG.info(quoteN);
							if (quoteN != null) {
								existsMsg = msgMap.get(quoteN);
								if (existsMsg != null && !"".equals(existsMsg.trim())) {
									msgMap.put(quoteN, existsMsg + " ," + s);
								} else {
									msgMap.put(quoteN, "SAP: "+s);
								}
							}

							quoteN = null;
							existsMsg = null;
						}
						
						//move unsuccessful quote item to sheet
						///successCount=successCount-msgMap.size();
						//failCount+=msgMap.size();
						Iterator<Entry<String,String>> unIterator =msgMap.entrySet().iterator();
						while(unIterator.hasNext()){
						 Map.Entry<String, String>	e=unIterator.next();
						 LOG.info("get row for key:"+e.getKey());
						 for(HashMap<String,Row> m:pendingRowList){
								if (m.keySet().contains(e.getKey())) {
									//LOG.info("get row for key:"+e.getKey()+"check from pendingRowList");
									this.copyRows(sheet1, m.get(e.getKey()), sheet1.getLastRowNum() + 1, e.getValue());
									failCount++;
									successCount=successCount-1;
									//break; at 20161024
								}
						 }
							
						}
					}

				}

/*				String errMessage = "Final quotation price has been updated in WebQuote, but failed to update in SAP. Please check below error message.</br>"
						+ sb.toString();
				LOG.info(errMessage);
*/
				se = SUCCESS_EMAILBODY.replace("{salesperson_name}", user.getName()).replace("{fail_number}", String.valueOf(failCount))
						.replace("{pass_number}", String.valueOf(successCount)).replace("{region}", user.getDefaultBizUnit().getName())
						.replace("{proccess_number}", String.valueOf(proccessNumber));
				
				String RESULT_STR = getLoclaizedText("wq.message.resultFinalQuotation",locale)+"." + "<br/>" + "~ "+getLoclaizedText("wq.message.NumberOfQuotesProcessed",locale)+": {proccess_number}" + "<br/>"
						+ "~ "+getLoclaizedText("wq.message.passQuotes",locale)+": {pass_number}" + "<br/>" + "~ "+getLoclaizedText("wq.message.failQuotes",locale)+": {fail_number}" + "<br/>" +"<br/>";

				// delete the src sheet from workbook
				workbook.removeSheetAt(0);
                if(successCount ==proccessNumber){
                	sendResultEmail(workbook, SUCCESS_SUBJECT, se, user, "N");
                	String infoMsg=RESULT_STR.replace("{fail_number}", String.valueOf(failCount))
            				.replace("{pass_number}", String.valueOf(successCount)).replace("{proccess_number}", String.valueOf(proccessNumber));
                	return new AsyncResult<String>(infoMsg);
                }else{
                	sendResultEmail(workbook, SUCCESS_SUBJECT, se, user, "Y");
                	String infoMsg=RESULT_STR.replace("{fail_number}", String.valueOf(failCount))
            				.replace("{pass_number}", String.valueOf(successCount)).replace("{proccess_number}", String.valueOf(proccessNumber))+getLoclaizedText("wq.message.checkEmailFailQuote",locale)+ ".";
                	return new AsyncResult<String>(infoMsg);
                }
				
				
				


			} catch (AppException e) {
				LOG.log(Level.WARNING, "Error occured when when creating SAP quote for uploaded"
						+ "file:"+quoteExcel.getFileName()+", Exception message: "+MessageFormatorUtil.getParameterizedStringFromException(e));
				// Pagination : changes done for WQ - 983
				String errorMsg = MessageFormatorUtil.getParameterizedString(locale,CommonConstants.WQ_WEB_FINAL_QUOTATION_PRICE_HAS_BEEN_UPDATED, null)+ e.getCausedBy().getMessage();
				// Pagination : changes ends
				LOG.info(errorMsg);
				this.sendEmailForUnsuccessCase(errorMsg, user,locale);
				return new AsyncResult<String>(errorMsg);
			}

		} catch (Exception e)
		{
			this.sendEmailForUnsuccessCase(e.getMessage(), user,locale);
			LOG.log(Level.SEVERE, "Exception in mass Revise for user : "+user.getEmployeeId()+" , exception message : "+e.getMessage(), e);
			return new AsyncResult<String>(extractExceptionMsg(e.getMessage(),locale));
		}
	}

	private void sendEmailForUnsuccessCase(String errorMsg, User user,Locale locale) {
		if(errorMsg ==null || errorMsg.trim().equals("")){
			errorMsg="Catch Exception";
		}
		LOG.info("Begin Send Email For Unsuccess Case,The ErrorMsg is: "+errorMsg);
		this.sendResultEmail(null, FAIL_SUBJECT, getFailEmailBody(locale).replace("{salesperson_name}", user.getName()).replace("{application_errors}", errorMsg)
				.replace("{region}", user.getDefaultBizUnit().getName()), user, "N");

	}

	private String extractExceptionMsg(String msg, Locale locale){
		
		String EXCEPTION_STR=getLoclaizedText("wq.message.massUploadUnsuccessful",locale)+":" + "<br/>" +"<br/>" + "{application_errors}"+"<br/>"
				+ getLoclaizedText("wq.message.reportHelpDeskMsg",locale)+"." + "<br/>";
		
		if(msg ==null || msg.trim().equals("")){
			msg=getLoclaizedText("wq.message.catchExceptions",locale);
		}
		return EXCEPTION_STR.replace("{application_errors}", msg);
		
	}

	public String getLoclaizedText(String key, Locale locale) {

		ResourceBundle bundle = ResourceBundle.getBundle(DBResourceBundle.class.getName(),locale);
		return bundle.getString(key);

	}
	public  String getFailEmailBody(Locale locale){
		return "Dear {salesperson_name}," + "<br/>" + "<br/>" + "<br/>"+" The mass upload is unsuccessful. The error is as followed:" + "<br/>" +"<br/>" + "{application_errors}"+"<br/>"
				+"Please upload a valid report again. If it is application error, kindly inform helpdesk." + "<br/>" + "<br/>" + "Best Regards," + "<br/>"
				+ "{region} Quote Centre";
	}

	/** @author Damon。Chen
	 * @date Created Date：2016年9月18日 下�?�2:59:41
	   @Description: TODO
	   @param item
	   @param user
	   @return if has right to revise final price.
	 * @throws  
	 */
	private Boolean isHaveRightToRevise(QuoteItem item, List<User> saleslist, List<DataAccess> dataAccessList) {
		// TODO Auto-generated method stub
		//List<User> list = new ArrayList<User>();
		//List<DataAccess> dataAccessList =new ArrayList<DataAccess>();
		//Boolean isHave = false;
		Boolean flagForSalesUserId =false;
		Boolean flagForDataAccess =false;
		Long salesUserId = item.getQuote().getSales().getId();
			
			for(User luser : saleslist){
				if(luser.getId() ==salesUserId){
					flagForSalesUserId =true;
					break;
				}
			}
		
			if(flagForSalesUserId ==true){
				if(dataAccessList ==null ||dataAccessList.size()==0){
					 flagForDataAccess =true;
				}
				
				for(DataAccess dataAccess : dataAccessList){
					if((dataAccess.getManufacturer() == null || dataAccess.getManufacturer().getId() == item.getRequestedMfr().getId()) &&
		    				(dataAccess.getProductGroup() == null || item.getProductGroup2() == null ||dataAccess.getProductGroup().getId() == item.getProductGroup2().getId()) &&
		    				(dataAccess.getMaterialType() == null || item.getMaterialTypeId() == null ||dataAccess.getMaterialType().getName().equals(item.getMaterialTypeId())) &&
		    				(dataAccess.getProgramType() == null || item.getProgramType() == null || dataAccess.getProgramType().getName() == item.getProgramType()) &&
		    				(dataAccess.getTeam() == null || item.getQuote().getTeam() == null ||dataAccess.getTeam().getName().equals(item.getQuote().getTeam().getName()))){	
						 flagForDataAccess =true;
						break;
					}
					
					}
			}
	
      
		if(flagForDataAccess ==true && flagForSalesUserId==true){
			return true;
		}else{
			return false;
		}

	}

	private void copyRows(XSSFSheet sheet1, Row srcRow, int i, String exceptionStr) {
		try {
			Row destRow = sheet1.createRow(i);
			Iterator<Cell> cellIterator = srcRow.iterator();
			while (cellIterator.hasNext()) {
				Cell srcCell = cellIterator.next();
				if (srcCell == null) {
					continue;
				}

				Cell desCell = destRow.createCell(srcCell.getColumnIndex());
				desCell.setCellStyle(srcCell.getCellStyle());
				desCell.setCellType(srcCell.getCellType());
				switch (srcCell.getCellType()) {
				case Cell.CELL_TYPE_BOOLEAN:
					desCell.setCellValue(srcCell.getBooleanCellValue());
					break;
				case Cell.CELL_TYPE_ERROR:
					desCell.setCellValue(srcCell.getErrorCellValue());
					break;
				case Cell.CELL_TYPE_FORMULA:
					desCell.setCellValue(srcCell.getCellFormula());
					break;
				case Cell.CELL_TYPE_NUMERIC:
					desCell.setCellValue(srcCell.getNumericCellValue());
					break;
				case Cell.CELL_TYPE_STRING:
					desCell.setCellValue(srcCell.getStringCellValue());
					break;
				default:
					desCell.setCellValue(srcCell.getStringCellValue());
					break;

				}
			}
			//srcRow.getLastCellNum()
			// setting exception cell
			Cell exceptionCell;
			//fix by whwong (20181113): check the existing excel last column header whether is "ERROR" column, if yes, 
							//			then overwrite the exception in the same cell
			if (sheet1.getRow(0).getLastCellNum()>0){
				if ("ERROR".equals(sheet1.getRow(0).getCell(sheet1.getRow(0).getLastCellNum()-1).getStringCellValue())){
					if (destRow.getLastCellNum()>=sheet1.getRow(0).getLastCellNum())
						exceptionCell = destRow.getCell(sheet1.getRow(0).getLastCellNum()-1);
					else
						exceptionCell = destRow.createCell(srcRow.getLastCellNum());
				}
				else exceptionCell = destRow.createCell(srcRow.getLastCellNum());
			} else {
				exceptionCell = destRow.createCell(srcRow.getLastCellNum());
			}
			
			//Cell exceptionCell = destRow.createCell(1+srcRow.getLastCellNum());
			//exceptionCell.setCellStyle(destRow.getCell(1).getCellStyle());
			exceptionCell.setCellValue(exceptionStr);
		} catch (Exception e) {
			throw e;
		}
		//LOG.info("srcRow row number: " + srcRow.getRowNum());

	}
	//TODO
	private AsyncResult<String> checkHeaderRow(Row row, User user, Locale locale) {
		Boolean missQN = false;
		Boolean missFP = false;
		StringBuffer qsb = new StringBuffer();
		qsb.append(getLoclaizedText("wq.error.missingColQuoteReport", locale) + ".");
		Cell cellQuoteNumberHeader = row.getCell(2);
		if (cellQuoteNumberHeader == null) {
			missQN = true;
		} else {
			if (cellQuoteNumberHeader.getCellType() != Cell.CELL_TYPE_STRING) {
				missQN = true;
			} else {
				LOG.info(cellQuoteNumberHeader.getStringCellValue().replace(" ", "").toUpperCase());
				if (cellQuoteNumberHeader.getStringCellValue() == null || !"AVNETQUOTE#".equals(
						cellQuoteNumberHeader.getStringCellValue().replace(" ", "").toUpperCase())) {
					missQN = true;
				}
			}

		}

		if (missQN == true) {
			qsb.append("<br/>");
			LOG.info(qsb.toString());
			sendEmailForUnsuccessCase(qsb.toString(), user, locale);
			return new AsyncResult<String>(extractExceptionMsg(qsb.toString(), locale));
		}
		//201810 (whwong) change from 25 to 29 due to excel export had changed
		Cell cellQuoteFinalPriceHeader = row.getCell(29); 
		if (cellQuoteFinalPriceHeader == null) {
			missFP = true;
		} else {
			if (cellQuoteFinalPriceHeader.getCellType() != Cell.CELL_TYPE_STRING) {
				missFP = true;
			} else {
				LOG.info(cellQuoteFinalPriceHeader.getStringCellValue().replace(" ", "").toUpperCase());
				if (cellQuoteFinalPriceHeader.getStringCellValue() == null || !"FINALQUOTATIONPRICE".equals(
						cellQuoteFinalPriceHeader.getStringCellValue().replace(" ", "").toUpperCase())) {
					missFP = true;
				}
			}

		}
		if (missFP == true) {
			qsb.append("<br/>");
			// append("~ Final Quotation Price").append("<br/>");
			LOG.info(qsb.toString());
			sendEmailForUnsuccessCase(qsb.toString(), user, locale);
			return new AsyncResult<String>(extractExceptionMsg(qsb.toString(), locale));
		}
		return null;
	}
	/**
	 * @author Damon。Chen
	 * @date Created Date：2016年9月13日 上�?�10:50:46
	 * @Description: TODO
	 * @param quoteItem
	 * @return void
	 * @throws
	 */

	public String generateFileName() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
		Date date = new Date();
		return "FailQuoteReport_" + df.format(date);
	}

	public static void removeRow(HSSFSheet sheet, int rowIndex) {
		int lastRowNum = sheet.getLastRowNum();
		LOG.info("the last row num:" + sheet.getLastRowNum());
		if (rowIndex >= 0 && rowIndex < lastRowNum)
			sheet.shiftRows(rowIndex + 1, lastRowNum, -1);
		if (rowIndex == lastRowNum) {
			HSSFRow removingRow = sheet.getRow(rowIndex);
			if (removingRow != null)
				sheet.removeRow(removingRow);
		}
	}

	public void logForRowMap(Row row, String quoteNumber) {
		LOG.info("deleting row  num :" + row.getRowNum() + "and quote number: " + quoteNumber);
	}

	
	/** @author Damon。Chen
	 * @date Created Date：2016年9月18日 下�?�2:51:10
	   @Description: TODO
	 * @return  void
	   @param workbook
	   @param subject
	   @param emailbody
	   @param user
	   @param hasAttachment
	 * @throws  
	 */
	
	public void sendResultEmail(XSSFWorkbook workbook, String subject, String emailbody, User user, String hasAttachment) {
		try {
			LOG.info("begining set email, the value of hasAttachment is: "+hasAttachment);
			MailInfoBean mib = new MailInfoBean();
			List<String> toList = new ArrayList<String>();
			toList.add(user.getEmailAddress());
			mib.setMailSubject(subject);
			mib.setMailContent(emailbody);
			mib.setMailTo(toList);
			mib.setMailFrom("Webquote@Avnet.com");
			File xlsFile = null;
			File zipFile = null;
			FileOutputStream fileOut = null;
			// mib.setFileName();
			// mib.setMailAttachFileNames(workbook);
			// String tempPath =
			// sysConfigSB.getProperyValue(QuoteConstant.TEMP_DIR);
			// String zipFileName = generateFileName();
			// String xlsFileName = generateFileName();{}
			if (hasAttachment != null && hasAttachment.equals("Y")) {
				String tempPath = sysConfigSB.getProperyValue(QuoteConstant.TEMP_DIR);
				
				String fileName = generateFileName();
				String zipFileName = tempPath+fileName + ".zip";
				String xlsFileName = tempPath+fileName + ".xlsx"; 
				//String zipFileName = fileName + ".zip";
				//String xlsFileName = fileName + ".xls";
				xlsFile = new File(xlsFileName);
				fileOut = new FileOutputStream(xlsFile);
				workbook.write(fileOut);
				fileOut.flush();
				fileOut.close();
				zipFile = (File)QuoteUtil.doZipFile(zipFileName, xlsFileName);

				mib.setZipFile(zipFile);
				
				/*
				File f = new File(tempPath+generateFileName());
				FileOutputStream out = new FileOutputStream(f);
				workbook.write(out);
				out.flush();
				out.close();
				// File zipFile =
				// (File)com.avnet.emasia.webquote.utilites.web.util.QuoteUtil.doZipFile(zipFileName,xlsFileName);
				mib.setZipFile(f);*/
			}
			mailUtilsSB.sendAttachedMail(mib);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Exception occurred for email subject: "+subject+", Send email failed!" +e.getMessage(), e);
			
		}
	}

}
