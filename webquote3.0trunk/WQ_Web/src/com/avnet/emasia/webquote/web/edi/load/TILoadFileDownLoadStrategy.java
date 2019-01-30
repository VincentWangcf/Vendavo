package com.avnet.emasia.webquote.web.edi.load;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.MessageFormat;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.jetty.util.log.Log;

import com.avnet.emasia.webquote.constants.EdiMesgType;
import com.avnet.emasia.webquote.edi.AMesgApprove;
import com.avnet.emasia.webquote.edi.AMesgRefuse;
import com.avnet.emasia.webquote.edi.DMesgApprove;
import com.avnet.emasia.webquote.edi.DMesgRefuse;
import com.avnet.emasia.webquote.edi.ThreeAMesgOne;
import com.avnet.emasia.webquote.edi.utils.TIEntityFactory;
import com.avnet.emasia.webquote.edi.utils.TIExchangeContext;
import com.avnet.emasia.webquote.edi.utils.TIExchangeContextFactory;
import com.avnet.emasia.webquote.web.maintenance.DownLoadStrategy;

public class TILoadFileDownLoadStrategy extends DownLoadStrategy{
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1826810634921438079L;
	private String mesgType;
	
	
	
	public String getMesgType() {
		return mesgType;
	}
	
	public TILoadFileDownLoadStrategy(String mesgType) {
		this.mesgType = mesgType;
	}
	

	private static String convertToYYYYMMDD(Date date) {// convert m/d/yy to dd/MM/yyyy format
		 
		String returnStr ="";
		try {
			DateFormat format1 = new SimpleDateFormat("yyyy/MM/dd");
			returnStr = format1.format(date);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnStr;
	}
	 
	private static String convertBigDcToStr(BigDecimal bigDecimal) {
		
		if(bigDecimal == null) {
			return "";
		}
		return bigDecimal.toString();
		
	}
	private void ADR2ToExcel(XSSFWorkbook wb, List<?> data) {
		XSSFCell cell = null;
		XSSFSheet sheet = wb.getSheetAt(0);
		int HeaderRowCnt = this.getCurExchengCtx().getHeaderRowCount();
		if(data != null){
			for(int j=0; j<data.size(); j++){
				XSSFRow dataRow = sheet.createRow(j + HeaderRowCnt);
				Object[] objs = (Object[]) data.get(j);
				AMesgApprove aMesg = (AMesgApprove)objs[0];
				/*****PO No.	PO Item No.	Quote No.	TI Quote No	TI Quote Item No.	Start Date	End Date	Status Description	
				 * Qty	Status	TI Part No.	ZCPC	ZCPC Curr	Offer Price	Offer Currency	
				 * Resales Price	Resales Currency	Date Created	Valid Start Date	Valid End Date	
				 * TI Auth No.	Auth Qty	TI Part No.	TI Offer Price	TI Offer Currency	TI Comments	*****/
				/**PO No. */
				cell = dataRow.createCell(0);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(aMesg.getPoNum()!=null){
					cell.setCellValue(aMesg.getPoNum());
				}
				/**PO Item*/
				cell = dataRow.createCell(1);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(aMesg.getPoItemNum()!=null){
					cell.setCellValue(aMesg.getPoItemNum());
				}
				/**	Quote No. */
				cell = dataRow.createCell(2);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(aMesg.getQuoteNum()!=null){
					cell.setCellValue(aMesg.getQuoteNum());
				}
				/**TI Quote No */
				cell = dataRow.createCell(3);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(aMesg.getTiQuoteNum()!=null){
					cell.setCellValue(aMesg.getTiQuoteNum());
				}
				/**TI Quote Item No.*/
				cell = dataRow.createCell(4);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(aMesg.getTiQuoteItemNum()!=null){
					cell.setCellValue(aMesg.getTiQuoteItemNum());
				}
				/**Start Date*/
				cell = dataRow.createCell(5);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(aMesg.getStartDate()!=null){
					cell.setCellValue(convertToYYYYMMDD(aMesg.getStartDate()));
				}
				/**End Date*/
				cell = dataRow.createCell(6);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(aMesg.getEndDate()!=null){
					cell.setCellValue(convertToYYYYMMDD(aMesg.getEndDate()));
				}
				
				/**Status Description*/
				cell = dataRow.createCell(7);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(aMesg.getStatusDesc()!=null){
					cell.setCellValue(aMesg.getStatusDesc());
				}
				/**Qty**/
				cell = dataRow.createCell(8);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				//if(aMesg.getQty()>0){
				cell.setCellValue(aMesg.getQty());
				//}
				/**Status*/
				cell = dataRow.createCell(9);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(aMesg.getStatus()!=null){
					cell.setCellValue(aMesg.getStatus());
				}
				/**TI Part No.*/
				cell = dataRow.createCell(10);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(aMesg.getTiPartNum()!=null){
					cell.setCellValue(aMesg.getTiPartNum());
				}
				/**ZCPC*/
				cell = dataRow.createCell(11);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(aMesg.getZcpc()!=null){
					cell.setCellValue(convertBigDcToStr(aMesg.getZcpc()));
				}
				
				/**ZCPC Curr.*/
				cell = dataRow.createCell(12);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(aMesg.getZcpcCurr()!=null){
					cell.setCellValue(aMesg.getZcpcCurr());
				}
				/**Offer Price.*/
				cell = dataRow.createCell(13);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(aMesg.getOfferPrice()!=null){
					cell.setCellValue(convertBigDcToStr(aMesg.getOfferPrice()));
				}
				/**Offer Currency.*/
				cell = dataRow.createCell(14);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(aMesg.getOfferPriceCurr()!=null){
					cell.setCellValue(aMesg.getOfferPriceCurr());
				}
				/**Resales Price.*/
				cell = dataRow.createCell(15);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(aMesg.getResalesPrice()!=null){
					cell.setCellValue(convertBigDcToStr(aMesg.getResalesPrice()));
				}
				/**Resales Currency.*/
				cell = dataRow.createCell(16);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(aMesg.getResalesPriceCurr()!=null){
					cell.setCellValue(aMesg.getResalesPriceCurr());
				}
				/**Date Created.*/
				cell = dataRow.createCell(17);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(aMesg.getCreatedOn()!=null){
					cell.setCellValue(convertToYYYYMMDD(aMesg.getCreatedOn()));
				}
				/***begin resolve the second entity**/
				if(objs.length<2 ||objs[1] ==null) {
					continue;
				}
				DMesgApprove dMesg =  (DMesgApprove)objs[1];
				/**Valid Start Date.*/
				cell = dataRow.createCell(18);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(dMesg.getStartDate()!=null){
					cell.setCellValue(convertToYYYYMMDD(dMesg.getStartDate()));
				}
				/**Valid End Date.*/
				cell = dataRow.createCell(19);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(dMesg.getEndDate()!=null){
					cell.setCellValue(convertToYYYYMMDD(dMesg.getEndDate()));
				}
				/**TI Auth No.*/
				cell = dataRow.createCell(20);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				//if(dMesg.getTiAuthNum()!=null){
					cell.setCellValue(dMesg.getTiAuthNum());
				//}
				/**Auth Qty.*/
				cell = dataRow.createCell(21);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				//if(dMesg.getAuthQty()!=null){
					cell.setCellValue(dMesg.getAuthQty());
				//}
				/**TI Part No.*/
				cell = dataRow.createCell(22);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(dMesg.getTiPartNum()!=null){
					cell.setCellValue(dMesg.getTiPartNum());
				}
				/**TI Offer Price.*/
				cell = dataRow.createCell(23);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(dMesg.getOfferPricer()!=null){
					cell.setCellValue(convertBigDcToStr(dMesg.getOfferPricer()));
				}
				/**TI Offer Currency.*/
				cell = dataRow.createCell(24);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(dMesg.getOfferPricerCurr()!=null){
					cell.setCellValue(dMesg.getOfferPricerCurr());
				}
				/**TI Comments.*/
				cell = dataRow.createCell(25);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(dMesg.getTiComment()!=null){
					cell.setCellValue(dMesg.getTiComment());
				}
			}
		}
		/*XSSFCell cell = null;
		XSSFSheet sheet = wb.getSheetAt(0);

		List<ExcelAliasBean> excelAliasTreeSet = PricerUploadHelper.getPricerExcelAliasAnnotation();

		if(data != null){
			for(int j=0; j<data.size(); j++){
				XSSFRow row = sheet.createRow(PricerConstant.PRICER_TEMPLATE_HEADER_ROW_NUMBER+j);
				PricerUploadTemplateBean bean = (PricerUploadTemplateBean) data.get(j);
					
				for(ExcelAliasBean excelAliasBean:excelAliasTreeSet){					
					cell = row.createCell(excelAliasBean.getCellNum()-1);
					cell.setCellType(Cell.CELL_TYPE_STRING);
	
					if(cell!=null){
						cell.setCellValue(POIUtils.getter(bean,excelAliasBean.getFieldName()));
					}	
				}
			}
		}		*/
	}
	
	/**for AMesgRefuse **/
	private void AR1ToExcel(XSSFWorkbook wb, List<?> data) {
		/**PO Number	PO Item Number	Quote Number	Message Type	Status	Rejection Reason	Date_Created**/
		XSSFCell cell = null;
		XSSFSheet sheet = wb.getSheetAt(0);
		int HeaderRowCnt = this.getCurExchengCtx().getHeaderRowCount();
		if(data != null){
			for(int j=0; j<data.size(); j++){
				XSSFRow dataRow = sheet.createRow(j + HeaderRowCnt);
				AMesgRefuse aMesg = (AMesgRefuse)data.get(j);
				
				/**PO Number*/
				cell = dataRow.createCell(0);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(aMesg.getPoNum()!=null){
					cell.setCellValue(aMesg.getPoNum());
				}
				/**PO Item No*/
				cell = dataRow.createCell(1);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(aMesg.getPoItemNum()!=null){
					cell.setCellValue(aMesg.getPoItemNum());
				}
				/**Quote No*/
				cell = dataRow.createCell(2);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(aMesg.getQuoteNum()!=null){
					cell.setCellValue(aMesg.getQuoteNum());
				}
				/**TI Quote No. */
				cell = dataRow.createCell(3);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(aMesg.getTiQuoteNum()!=null){
					cell.setCellValue(aMesg.getTiQuoteNum());
				}
				/**TI Quote Item No. */
				cell = dataRow.createCell(4);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(aMesg.getTiQuoteItemNum()!=null){
					cell.setCellValue(aMesg.getTiQuoteItemNum());
				}
				/**TI Part No. */
				cell = dataRow.createCell(5);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(aMesg.getTiPartNum()!=null){
					cell.setCellValue(aMesg.getTiPartNum());
				}
				/**PO Number	PO Item Number	Quote Number	Message Type	Status	Rejection Reason	Date_Created**/
				/**Message Type */
				cell = dataRow.createCell(6);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(aMesg.getMesgType()!=null){
					cell.setCellValue(aMesg.getMesgType());
				}
				/**Status.*/
				cell = dataRow.createCell(7);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(aMesg.getStatus()!=null){
					cell.setCellValue(aMesg.getStatus());
				}
				/**Rejection Reason*/
				cell = dataRow.createCell(8);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(aMesg.getReason()!=null){
					cell.setCellValue(aMesg.getReason());
				}
				/**Date_Created*/
				cell = dataRow.createCell(9);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(aMesg.getCreatedOn()!=null){
					cell.setCellValue(convertToYYYYMMDD(aMesg.getCreatedOn()));
				}
			}
		}
	}
	
	/**for DMesgRefuse **/
	private void DR1ToExcel(XSSFWorkbook wb, List<?> data) {
		/**PO Number	PO Item Number	Quote Number	Message Type	Status	Rejection Reason	Date_Created**/
		XSSFCell cell = null;
		XSSFSheet sheet = wb.getSheetAt(0);
		int HeaderRowCnt = this.getCurExchengCtx().getHeaderRowCount();
		if(data != null){
			for(int j=0; j<data.size(); j++){
				XSSFRow dataRow = sheet.createRow(j + HeaderRowCnt);
				DMesgRefuse dMesg = (DMesgRefuse)data.get(j);
				/**PO Number*/
				cell = dataRow.createCell(0);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(dMesg.getPoNum()!=null){
					cell.setCellValue(dMesg.getPoNum());
				}
				/**PO Item*/
				cell = dataRow.createCell(1);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(dMesg.getPoItemNum()!=null){
					cell.setCellValue(dMesg.getPoItemNum());
				}
				/**Quote No. */
				cell = dataRow.createCell(2);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(dMesg.getQuoteNum()!=null){
					cell.setCellValue(dMesg.getQuoteNum());
				}
				/**TI Quote No. */
				cell = dataRow.createCell(3);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(dMesg.getTiQuoteNum()!=null){
					cell.setCellValue(dMesg.getTiQuoteNum());
				}
				/**TI Quote Item No. */
				cell = dataRow.createCell(4);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(dMesg.getTiQuoteItemNum()!=null){
					cell.setCellValue(dMesg.getTiQuoteItemNum());
				}
				/**TI Part No. */
				cell = dataRow.createCell(5);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(dMesg.getTiPartNum()!=null){
					cell.setCellValue(dMesg.getTiPartNum());
				}
				
				/**Message Type */
				cell = dataRow.createCell(6);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(dMesg.getMesgType()!=null){
					cell.setCellValue(dMesg.getMesgType());
				}
				/**Status.*/
				cell = dataRow.createCell(7);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(dMesg.getStatus()!=null){
					cell.setCellValue(dMesg.getStatus());
				}
				/**Rejection Reason*/
				cell = dataRow.createCell(8);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(dMesg.getReason()!=null){
					cell.setCellValue(dMesg.getReason());
				}
				/**Date_Created*/
				cell = dataRow.createCell(9);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(dMesg.getCreatedOn()!=null){
					cell.setCellValue(convertToYYYYMMDD(dMesg.getCreatedOn()));
				}
			}
		}
	}
	
	private void TAONEToExcel(XSSFWorkbook wb, List<?> data) {
		XSSFCell cell = null;
		XSSFSheet sheet = wb.getSheetAt(0);
		int HeaderRowCnt = this.getCurExchengCtx().getHeaderRowCount();
		Map<String, String> map = this.getCurExchengCtx().getEntityMapFile();
		List<Map.Entry<String, String>> entryList = new ArrayList<Map.Entry<String, String>>(map.entrySet());  
		//for sort
		Collections.sort(entryList,  new Comparator<Map.Entry<String, String>>() {  
			@Override
			public int compare(Entry<String, String> entry1, Entry<String, String> entry2) {
				return Integer.valueOf(entry1.getValue()).
						compareTo(Integer.valueOf(entry2.getValue()));
			}});  

		if(data != null){
			for(int j=0; j<data.size(); j++){
				XSSFRow dataRow = sheet.createRow(j + HeaderRowCnt);
				ThreeAMesgOne mesg = (ThreeAMesgOne)data.get(j);
				int offset =0;
				/**Date_Created*/
				cell = dataRow.createCell(offset++);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(mesg.getCreatedOn()!=null){
					cell.setCellValue(convertToYYYYMMDD(mesg.getCreatedOn()));
				}
				/**File_Name*/
				cell = dataRow.createCell(offset++);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(mesg.getFileName()!=null){
					cell.setCellValue(mesg.getFileName());
				}
				
				Iterator<Map.Entry<String, String>> iter = entryList.iterator();
				while(iter.hasNext()) {
					Map.Entry<String, String> entry = iter.next();
					String fieldName = entry.getKey();
					int index = Integer.valueOf(entry.getValue());
					Field field  = TIEntityFactory.FindFieldByName(fieldName, ThreeAMesgOne.class);
					if(field == null) {
						Log.info(MessageFormat.format("Can not find filed [{0}] in [{1}]", fieldName, ThreeAMesgOne.class.getName()));
					}
					String fieldType = field.getType().getSimpleName();
					Object obj = null;
					try {
						obj = field.get(mesg);
					} catch (Exception e) {
						e.printStackTrace();
					}  
					cell = dataRow.createCell(index + offset);
					cell.setCellType(Cell.CELL_TYPE_STRING);
					if(obj !=null) {
						if("String".equalsIgnoreCase(fieldType)) {
							cell.setCellValue(obj.toString());
						}else if("Date".equals(fieldType)) {
							cell.setCellValue(convertToYYYYMMDD((Date)obj));
						}else if ("Long".equals(fieldType)) {
							cell.setCellValue(obj.toString());
						} else if ("BigDecimal".equals(fieldType)) {
							cell.setCellValue(obj.toString());
						}
					}
					
				}
			}
		}
	}
	
	@Override
	public void setDateTosheet(XSSFWorkbook wb, List<?> data) {
		if(EdiMesgType.TARONE.getMesgTypeName().equals(this.getMesgType())) {
			AR1ToExcel(wb,data);
    	}else if(EdiMesgType.FDRONE.getMesgTypeName().equals(this.getMesgType())) {
    		DR1ToExcel(wb,data);
    	}else if(EdiMesgType.TARTWO.getMesgTypeName().equals(this.getMesgType())) {
    		ADR2ToExcel(wb,data);
    	}else if(EdiMesgType.TAONE.getMesgTypeName().equals(this.getMesgType())) {
    		TAONEToExcel(wb,data);
    	}
	}
	
	
	//private final static  Map<String,TIExchangeContext> MapExContext = GetInitMap();
	public TIExchangeContext getCurExchengCtx(){
		return TIExchangeContextFactory.GetMapExcontext().get(this.getMesgType());
	}
	
}
