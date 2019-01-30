package com.avnet.emasia.webquote.web.quote;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.poi.ss.usermodel.Sheet;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.avnet.emasia.webquote.dp.EJBCommonSB;
import com.avnet.emasia.webquote.entity.BalUnconsumedQty;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.ejb.BalanceUnconsumedQtySB;
import com.avnet.emasia.webquote.quote.ejb.SystemCodeMaintenanceSB;
import com.avnet.emasia.webquote.quote.vo.BalUnconsumedQtyTemplateBean;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.web.maintenance.BalanceUnconsumedQtyDownLoadStrategy;
import com.avnet.emasia.webquote.web.maintenance.BalanceUnconsumedQtyUploadStrategy;
import com.avnet.emasia.webquote.web.user.UserInfo;

@ManagedBean
@SessionScoped
public class BalanceUnconsumedQtyMB implements Serializable {
	
	private static final long serialVersionUID = -5725924537821862139L;

	private static final Logger LOG =Logger.getLogger(BalanceUnconsumedQtyMB.class.getName());
	
	private transient UploadedFile uploadFile;
	
	private String uploadFileName;
	@EJB
	private SystemCodeMaintenanceSB systemCodeMaintenanceSB;
	@EJB
	private BalanceUnconsumedQtySB balanceUnconsumedQtySB;
	
	private User user = null;

	private transient StreamedContent  downloadReport;
	
	private final static String TEMPLATE_NAME = "Balance_Unconsumed_Qty.xlsx";
	
	private final static int COLUMN_OF_LENGTH = 5;

	private BalanceUnconsumedQtyDownLoadStrategy downloadSrategy = null;
	private BalanceUnconsumedQtyUploadStrategy uploadStrategy = null;
	private String bizUnitName = null;
	
	@EJB
	private EJBCommonSB ejbCommonSB;
	
	/** The resource MB. */
	@ManagedProperty(value="#{resourceMB}")
	/** To Inject ResourceMB instance  */
	private ResourceMB resourceMB;
	
	
	
	/**
	 * @param resourceMB the resourceMB to set
	 */
	public void setResourceMB(ResourceMB resourceMB) {
		this.resourceMB = resourceMB;
	}

	@PostConstruct
	public void postContruct() {
		user = UserInfo.getUser();
		bizUnitName = user.getDefaultBizUnit().getName();
		downloadSrategy = new BalanceUnconsumedQtyDownLoadStrategy();
		uploadStrategy = new BalanceUnconsumedQtyUploadStrategy();
		uploadStrategy.setUser(user);
	}
	
	public void uploadBalanceUnconsumedQty() {	
		LOG.info("begin upload Bal. Unconsumed Qty!"+uploadFile.getSize());
		long start =0;
		 boolean isErrorFound= ejbCommonSB.uploadFileSizeCheck(systemCodeMaintenanceSB, uploadFile, uploadStrategy, start);
		 if(isErrorFound){
			 return;
		 }
		
		uploadFileName = uploadFile.getFileName();
		Sheet sheet =  uploadStrategy.getSheet(uploadFile);
		boolean isRequiredFile =  uploadStrategy.isValidateFileColumn( sheet ,0,TEMPLATE_NAME, COLUMN_OF_LENGTH);
		if(!isRequiredFile){
			FacesContext.getCurrentInstance().addMessage(
					"msgId",new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceMB.getText("wq.message.excelFileFormat")+".",""));
			return;
		}
		List<BalUnconsumedQtyTemplateBean> beans = uploadStrategy.excel2Beans(sheet);
		String errMsg = uploadStrategy.verifyFields(beans);
		if (!errMsg.equals("")&&!errMsg.equals("[]")) {
			FacesContext.getCurrentInstance().addMessage(
					"msgId",new FacesMessage(FacesMessage.SEVERITY_ERROR, errMsg,""));
			return;
		}	
		String language = resourceMB.getDefaultLocaleAsString();
		user.setUserLocale(language);
		errMsg = balanceUnconsumedQtySB.verifyQuotedPartNumber(beans, new Locale(language)); 
		if (!errMsg.equals("")&&!errMsg.equals("[]")) {
			FacesContext.getCurrentInstance().addMessage(
					"msgId",new FacesMessage(FacesMessage.SEVERITY_ERROR, errMsg,""));
			return;
		}	
		Boolean isToDataBase = beansToDataBase(beans);
		if(isToDataBase){
			FacesContext.getCurrentInstance().addMessage("msgId",
					new FacesMessage(FacesMessage.SEVERITY_INFO, uploadFileName + ResourceMB.getText("wq.message.uploadSuccess")+".",""));
		}else{
			FacesContext.getCurrentInstance().addMessage("msgId",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, uploadFileName + ResourceMB.getText("wq.message.uploadError")+".",""));	
		}
		
		long end = System.currentTimeMillis();
		LOG.info("end upload Bal. Unconsumed Qty,takes " + (end - start) + " ms");	
	}

	public StreamedContent getDownloadReport() {
		List<BalUnconsumedQty> datas = balanceUnconsumedQtySB.findAllByBizUnits(bizUnitName);
		if(datas.size()==0){
			return downloadSrategy.getTemplateFile(TEMPLATE_NAME);
		}
		downloadReport = downloadSrategy.getDownloadFile(bizUnitName, datas,TEMPLATE_NAME);

		if (downloadReport == null) {
			FacesContext.getCurrentInstance().addMessage(
					null,new FacesMessage(FacesMessage.SEVERITY_ERROR," "+ResourceMB.getText("wq.message.downloadError")+"!"+"", ""));
			return null;
		}  

		return downloadReport;
	}	
	
	
	private Boolean beansToDataBase(List<BalUnconsumedQtyTemplateBean> beans) {
		Boolean bool = false;
		String bizUnit = user.getDefaultBizUnit().getName();
		if (bizUnit != null) {
			bool = balanceUnconsumedQtySB.batchPersist(beans, bizUnit);
		}
		return bool;
	}
	
	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public UploadedFile getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(UploadedFile uploadFile) {
		this.uploadFile = uploadFile;
	}

	
}
                    
