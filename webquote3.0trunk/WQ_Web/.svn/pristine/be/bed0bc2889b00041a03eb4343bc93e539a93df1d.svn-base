package com.avnet.emasia.webquote.web.quote;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.avnet.emasia.webquote.entity.QuoteItemDesignHis;
import com.avnet.emasia.webquote.entity.QuoteItemHis;
import com.avnet.emasia.webquote.exception.WebQuoteException;
import com.avnet.emasia.webquote.quote.ejb.QuoteSB;
import com.avnet.emasia.webquote.quote.vo.AuditLogReportSearchCriteria;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilities.util.DateUtil;

@ManagedBean
@SessionScoped
public class AuditLogReportMB implements Serializable {


	private static final long serialVersionUID = 4947932561511214150L;

	private static final Logger LOG =Logger.getLogger(AuditLogReportMB.class.getName());

	@EJB
	QuoteSB quoteSB;

	private List<QuoteItemHis> auditQuoteItems;
	private List<QuoteItemDesignHis> auditQuoteItemDesigns;
	
	private List<QuoteItemHis> filteredAuditQuoteItemHis;
	
	private List<QuoteItemDesignHis> filteredAuditQuoteItemDesignHis;
	
	private AuditLogReportSearchCriteria criteria;
	

	public AuditLogReportMB(){

	}

	@PostConstruct
	public void postContruct(){
		criteria = new AuditLogReportSearchCriteria();
	}
	
	public void postProcessXLS(Object document)
	{
		String[] columns = {"Start Date & Time","End Date & Time","Used","Avnet Quote#","Quote Stage","RFQ Status","Allocation Flag","Alternative QuoteNumber",
				"Application","Aqcc","Ardc","AuthGp??","AuthGp?? Reason Code","AuthGp?? Reason Desc ","AuthGp?? Remark ","Bal Unconsumed Qty ",
				"Bmt Checked Flag","Bom Flag","Bom Number","Bom Remarks","Cancellation Window","Competitor Information","Cost","Cost Indicator",
				"Curr From","Curr To","Customer Type","Description","Design In Cat","Design Location","Design Region","Dr Expiry Date",
				"DR Neda Id","Dr Neda Line Number","Drms Number","Eau","End Customer","Enquiry Segment","Ex Rate From","Ex Rate To",
				"Final Quotation Price","First Rfq Code","Fis Mth","Fis Qtr","Fis Year","Handling","Internal Comment","IT Comment",
				"Invalid Mfr","Invalid Part Number","Item Seq","Last Bmt In Date","Last Bmt Out Date","Last It Update Time","Last Pm Updated On","Last Qc In Date",
				"Last Qc Out Date","Last Qc Updated On","Last Rit Update Time","Last Sq In Date","Last Sq Out Date","Last Sq Update Time","Last Updated By","Last Updated Name",
				"Last Updated On","Last Updated Pm","Last Updated Pm Name","Last Updated Qc","Last Updated Qc Name","Lead Time","Loa Flag","Material Type Id",
				"Min Sell Price","MOV","Mp Schedule","Mpq","Multi Usage Flag","New Customer Flag","Norm Sell Price","ORDER ON HAND FLAG",
				"Original AuthGp","Pmoq","Po Expiry Date","PP Schedule","Price List Remarks1","Price List Remarks2","Price List Remarks3","Price List Remarks4",
				"Price Validity","PRODUCT GROUP1","PRODUCT GROUP2","PRODUCT GROUP3","PRODUCT GROUP4","Program Type","Project Name","QC Comment",
				"Qty Indicator","Quotation Effective Date","Quote Expiry Date","Quote Number","Quote Type","Quoted Flag","Full Mfr Part Number","Quoted Mfr",
				"Quoted Part Number","Quoted Price","Quoted Qty","Reason For Refresh","Reference Margin","Remarks","Remarks","Requested Mfr",
				"Requested Part Number","Requested Qty","Resale Indicator","Resale Margin","Resale Max","Resale Min","Reschedule Window","Revert Version",
				"SAP Part Number","Sent Out Time","Ship To CustomerName","Ship To Customer Number","Shipment Validity","Sold To CustomerName","Sold To Customer Number","SPR Flag",
				"Submission Date","Supplier Dr Number","Target Margin ","Target Resale","Terms And Conditions","Valid Flag","Vat","Vendor Debit Number",
				"Vendor Quote Number","Vendor Quote Qty","Version"};
		HSSFWorkbook wb = (HSSFWorkbook) document;
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow header = sheet.getRow(0);
		int i = 0;
		for (String column : columns)
		{
			header.getCell(i++).setCellValue(column);
		}
	}

	public void search(){

		try {
			
			if (StringUtils.isBlank(criteria.getsQuoteNumber())) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.message.quoteBlankNotAllowed"),"");
				FacesContext.getCurrentInstance().addMessage("messages", msg);
				return;
			}
			
			criteria.setupUIInCriteria();
//		User user = UserInfo.getUser();
//
//		criteria.setBizUnit(user.getDefaultBizUnit());
			auditQuoteItems = quoteSB.findQuoteItemHis(criteria);
			if(auditQuoteItems != null && auditQuoteItems.size() > 0) {
				
				auditQuoteItems = setHighLightForQuoteItemHis(auditQuoteItems);
				
				List<Long> quoteItemIds = new ArrayList<Long>();
				for(QuoteItemHis itemHis : auditQuoteItems) {
					quoteItemIds.add(itemHis.getId().getQuoteItemId());
				}
				auditQuoteItemDesigns = quoteSB.findQuoteItemDesignHis(quoteItemIds);
				
				auditQuoteItemDesigns = setHighLightForQuoteItemDesignHis(auditQuoteItemDesigns);
			}

			LOG.log(Level.INFO, auditQuoteItems.size() + " records is found for audit Log Quote");
		} catch (Exception e) {
		
			LOG.log(Level.SEVERE, "Exception in serahing quote for audit : "+criteria.getsQuoteNumber()+", exception message : "+e.getMessage(), e);
		}

	}
	
	
	
	private List<QuoteItemHis> setHighLightForQuoteItemHis(List<QuoteItemHis> quoteItemHisList ) throws WebQuoteException {
		List<QuoteItemHis> newList = new ArrayList<QuoteItemHis>();
		QuoteItemHis beforeBean = quoteItemHisList.get(0);
		newList.add(beforeBean);
		for(int i = 1; i < quoteItemHisList.size(); i++) {
			QuoteItemHis currrentBean = quoteItemHisList.get(i);
			Map<String,Boolean> highLightMap = currrentBean.getHighLightMap();
			Field[] fields = currrentBean.getClass().getDeclaredFields(); 
			for(Field file : fields) {
				String fieldName = file.getName();
				if(!"highLightMap".equals(fieldName)) {
					if("id".equals(fieldName)) {
						highLightMap.put("startDate", false);
					} else {
						highLightMap.put(fieldName, false);
					}
					
				}
			}
			if(StringUtils.equals(beforeBean.getQuoteNumber(), currrentBean.getQuoteNumber())) {
				//setHighLightForQuoteItemDesignHis(beforeBean, currrentBean, highLightMap);
				highLightMap.put("startDate", true);//startDate change
				for(Iterator<String> it = highLightMap.keySet().iterator();it.hasNext();) {
					String propertyName = it.next();
					if(!StringUtils.equals("highLightMap", propertyName) 
							&& !StringUtils.equals(propertyName, "startDate")
							&& !propertyName.contains("_")
							&& !StringUtils.equals(propertyName, "serialVersionUID")) {
						Object beforeBeanValue = null;
						Object currentBeanValue = null;
						try {
							beforeBeanValue = convertObject(PropertyUtils.getProperty(beforeBean, propertyName));
						currentBeanValue = convertObject(PropertyUtils.getProperty(currrentBean, propertyName));
						} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
							throw new WebQuoteException(e);
						}
						if(!StringUtils.equals(beforeBeanValue == null ? "" : beforeBeanValue.toString().trim(),
								currentBeanValue == null ? "" : currentBeanValue.toString().trim())) {
							highLightMap.put(propertyName, true);//value change
						}
					}
				}
				currrentBean.setHighLightMap(highLightMap);
				
			} 
			beforeBean = currrentBean;
			newList.add(currrentBean);
			
		}
		return newList;
	}
	
	private List<QuoteItemDesignHis> setHighLightForQuoteItemDesignHis(List<QuoteItemDesignHis> quoteItemDesignHisList) throws WebQuoteException {
		List<QuoteItemDesignHis> newList = new ArrayList<QuoteItemDesignHis>();
		QuoteItemDesignHis beforeBean = quoteItemDesignHisList.get(0);
		newList.add(beforeBean);
		for (int i = 1; i < quoteItemDesignHisList.size(); i++) {
			QuoteItemDesignHis currrentBean = quoteItemDesignHisList.get(i);
			Map<String, Boolean> highLightMap = currrentBean.getHighLightMap();
			Field[] fields = currrentBean.getClass().getDeclaredFields();
			for (Field file : fields) {
				String fieldName = file.getName();
				if (!"highLightMap".equals(fieldName)) {
					if ("id".equals(fieldName)) {
						highLightMap.put("startDate", false);
					} else {
						highLightMap.put(fieldName, false);
					}

				}
			}
			if (StringUtils.equals(beforeBean.getId().getQuoteItemId() + "",
					currrentBean.getId().getQuoteItemId() + "")) {
				setHighLightForQuoteItemDesignHis(beforeBean, currrentBean, highLightMap);

			} 
			beforeBean = currrentBean;
			newList.add(currrentBean);

		}
		return newList;
	}

	private void setHighLightForQuoteItemDesignHis(QuoteItemDesignHis beforeBean, QuoteItemDesignHis currrentBean,
			Map<String, Boolean> highLightMap    ) throws WebQuoteException {
		highLightMap.put("startDate", true);// startDate change
		for (Iterator<String> it = highLightMap.keySet().iterator(); it
				.hasNext();) {
			String propertyName = it.next();
			if (!StringUtils.equals("highLightMap", propertyName)
					&& !StringUtils.equals(propertyName, "startDate")
					&& !propertyName.contains("_")
					&& !StringUtils.equals(propertyName,
							"serialVersionUID")) {
				Object beforeBeanValue = null;
				Object currentBeanValue = null;
				try {
					beforeBeanValue = convertObject(PropertyUtils
								.getProperty(beforeBean, propertyName));
					currentBeanValue = convertObject(PropertyUtils.getProperty(currrentBean, propertyName));
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					e.printStackTrace();
				}
				if(!StringUtils.equals(beforeBeanValue == null ? "" : beforeBeanValue.toString().trim(),
						currentBeanValue == null ? "" : currentBeanValue.toString().trim())) {
					highLightMap.put(propertyName, true);//value change
				}
			}
		}
		currrentBean.setHighLightMap(highLightMap);
	}
	
	public void reset(){
		if(criteria.getQuoteNumber() != null && criteria.getQuoteNumber().size() > 0) {
			criteria.getQuoteNumber().clear();
		}
		criteria.setsQuoteNumber("");
	}

	
	private Object convertObject(Object obj) {
		if(obj != null) {
			if(obj instanceof java.util.Date) {
				obj = DateUtil.dateToString((Date)obj, "yyyy-MM-dd HH:mm:ss");
			} 
		}
		return obj;
	}
	


	public List<QuoteItemHis> getAuditQuoteItems() {
		return auditQuoteItems;
	}

	public void setAuditQuoteItems(List<QuoteItemHis> auditQuoteItems) {
		this.auditQuoteItems = auditQuoteItems;
	}

	public List<QuoteItemDesignHis> getAuditQuoteItemDesigns() {
		return auditQuoteItemDesigns;
	}

	public void setAuditQuoteItemDesigns(
			List<QuoteItemDesignHis> auditQuoteItemDesigns) {
		this.auditQuoteItemDesigns = auditQuoteItemDesigns;
	}

	public AuditLogReportSearchCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(AuditLogReportSearchCriteria criteria) {
		this.criteria = criteria;
	}

	public List<QuoteItemHis> getFilteredAuditQuoteItemHis() {
		return filteredAuditQuoteItemHis;
	}

	public void setFilteredAuditQuoteItemHis(
			List<QuoteItemHis> filteredAuditQuoteItemHis) {
		this.filteredAuditQuoteItemHis = filteredAuditQuoteItemHis;
	}

	public List<QuoteItemDesignHis> getFilteredAuditQuoteItemDesignHis() {
		return filteredAuditQuoteItemDesignHis;
	}

	public void setFilteredAuditQuoteItemDesignHis(
			List<QuoteItemDesignHis> filteredAuditQuoteItemDesignHis) {
		this.filteredAuditQuoteItemDesignHis = filteredAuditQuoteItemDesignHis;
	}

	

}