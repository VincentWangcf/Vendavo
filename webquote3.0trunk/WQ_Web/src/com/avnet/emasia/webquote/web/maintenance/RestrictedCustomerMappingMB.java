package com.avnet.emasia.webquote.web.maintenance;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
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

import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.RestrictedCustomerMapping;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.ejb.CustomerSB;
import com.avnet.emasia.webquote.quote.ejb.RestrictedCustomerMappingSB;
import com.avnet.emasia.webquote.quote.vo.RestrictedCustomerMappingSearchCriteria;
import com.avnet.emasia.webquote.quote.vo.RestrictedCustomerMappingVo;
import com.avnet.emasia.webquote.rowdata.DefaultRowDataConverter;
import com.avnet.emasia.webquote.rowdata.FileType;
import com.avnet.emasia.webquote.rowdata.datasource.DefaultExcelResolveInfo;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.util.PricerUploadHelper;
import com.avnet.emasia.webquote.utilities.DBResourceBundle;
import com.avnet.emasia.webquote.web.user.UserInfo;

@ManagedBean
@SessionScoped
public class RestrictedCustomerMappingMB implements Serializable {

	private static final long serialVersionUID = -790892198734878556L;
	private static final Logger LOG = Logger.getLogger(RestrictedCustomerMappingMB.class.getName());
	private static final String TEMPLATE_NAME = "Restricted_Customer_Mapping_Template.xlsx";
	private RestrictedCustomerMappingSearchCriteria criteria;
	private boolean recordsExceedMaxAllowed = false;
	public List<RestrictedCustomerMappingVo> restrictedCustomerMappingVos = null;
	private transient UploadedFile uploadFile;
	private transient StreamedContent  downloadReport;
	private User user;
	private final static int COLUMN_OF_LENGTH = 8;

	private String myPage = "";
	private RestrictedCustomerMappingDownLoadStrategy downLoadStrategy = null; 
	private RestrictedCustomerMappingUploadStrategy uploadStrategy = null;
	private String bizUnitName = null;
	private InputStream tmpInputStream;
	private List<RestrictedCustomerMappingVo> ConverterRestrictedCustomerMappingVos =null;
	
	private ResourceBundle bundle = ResourceBundle.getBundle(DBResourceBundle.class.getName(), Locale.getDefault());
	

	@EJB
	private RestrictedCustomerMappingSB restrictedCustomerMappingSB;
	@EJB
	CustomerSB customerSB;
	@PostConstruct
	public void postContruct() {
		criteria = new RestrictedCustomerMappingSearchCriteria();
		user = UserInfo.getUser();
		bizUnitName = user.getDefaultBizUnit().getName();
		criteria.setBizUnit(bizUnitName);
		downLoadStrategy = new RestrictedCustomerMappingDownLoadStrategy();
		uploadStrategy = new RestrictedCustomerMappingUploadStrategy();
		uploadStrategy.setUser(user); 
	}

	public void search(){
		long start = System.currentTimeMillis();
		//LOG.finer(criteria.toString());

		restrictedCustomerMappingVos = bean2Vo(restrictedCustomerMappingSB.search(criteria));

		int count = restrictedCustomerMappingVos.size();

		if(count > 500 ){
			setRecordsExceedMaxAllowed(true);
			for (int i = restrictedCustomerMappingVos.size() - 1; i >= 500; i--) {
				restrictedCustomerMappingVos.remove(i);
			}

		}else{
			setRecordsExceedMaxAllowed(false);
		}

		long end = System.currentTimeMillis();
		LOG.info("size: " + count + ", takes " + (end - start) + " ms");
	}
	
	
	public void setMyPage(String myPage) {
		this.myPage = myPage;
	}

	public String getMyPage() {		
		return "RestrictedCustomerControl.jsf" + "?faces-redirect=true";
	}
	
	public String goToUploadPage(){
		return "RestrictedCustomerMappingUpload.xhtml?faces-redirect=true";
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void upload() throws IOException{
		this.setTmpInputStream(uploadFile.getInputstream());
		boolean isUploadFile = uploadStrategy.isValidateUpload(uploadFile);
		String message;
		if(!isUploadFile){
			FacesContext.getCurrentInstance().addMessage("msgId",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.message.pleaseSelectFile")+".",""));
			return;	
		}
		
		boolean isExcelFile = uploadStrategy.isExcelFile(uploadFile);
		if(!isExcelFile){
			
			FacesContext.getCurrentInstance().addMessage(
					"msgId",new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceMB.getText("wq.message.selExcelFile")+".",""));
			return;
		}
		
		
		
		InputStream inputStream = null;
		
			 Boolean isToDataBase = true;
			try {
				inputStream = uploadFile.getInputstream();

				ByteArrayOutputStream baosOutputStream = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len;
				while ((len = inputStream.read(buffer)) > -1) {
					baosOutputStream.write(buffer, 0, len);
				}
				baosOutputStream.flush();

				ByteArrayInputStream stream1 = new ByteArrayInputStream(baosOutputStream.toByteArray());
				//ByteArrayInputStream stream2 = new ByteArrayInputStream(baosOutputStream.toByteArray());

				DefaultRowDataConverter Converter = new DefaultRowDataConverter();
				ConverterRestrictedCustomerMappingVos= Converter.convertAndValidate(stream1, FileType.EXCEL, new DefaultExcelResolveInfo(), RestrictedCustomerMappingVo.class);
				for(RestrictedCustomerMappingVo vo :ConverterRestrictedCustomerMappingVos){
					vo.setBizUnit(bizUnitName);
				}
				int row = 2;
				StringBuffer sb = new StringBuffer();
				for (RestrictedCustomerMappingVo restrictedCustomerMappingVo : ConverterRestrictedCustomerMappingVos) {
					StringBuffer sb1 = new StringBuffer();
					if(restrictedCustomerMappingVo.getconvertAndValidationError() != null){
						sb1.append(restrictedCustomerMappingVo.getconvertAndValidationError()+ "; ");
					}
					if (!sb1.toString().equals("")) {
						if (!sb1.toString().startsWith("Row")) {
							sb1.append(bundle.getString("wq.message.row") + " " + row + ": " + sb1.toString() + "<br/>");
						}
						// sb.append(bundle.getString("wq.message.row") + " " + row + ":
						// " + sb1.toString() + "<br/>");
						else {
							sb.append(sb1.toString() + "<br/>");
						}
					}
					row ++;
				}
				String errMsg =sb.toString();
				if (PricerUploadHelper.isHaveErrorMsg(errMsg)) {
					FacesContext.getCurrentInstance().addMessage(
							"msgId",new FacesMessage(FacesMessage.SEVERITY_ERROR, errMsg,""));
					return;
				}	
				// isToDataBase = beansToDataBase(ConverterRestrictedCustomerMappingVos);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		
		
		
		Sheet sheet =  uploadStrategy.getSheet(uploadFile);
		boolean isRequiredFile =  uploadStrategy.isValidateFileColumn( sheet ,0,TEMPLATE_NAME,COLUMN_OF_LENGTH );
		if(!isRequiredFile){
			
			FacesContext.getCurrentInstance().addMessage(
					"msgId",new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.message.excelFileFormat")+".",""));
			return;
		}
		
		//List<RestrictedCustomerMappingVo> beans = uploadStrategy.excel2Beans(sheet);
		
		String errMsg = uploadStrategy.verifyFields(ConverterRestrictedCustomerMappingVos);
		
	
		errMsg = errMsg + verifySoldToCodeInDb(ConverterRestrictedCustomerMappingVos);
		
		if (PricerUploadHelper.isHaveErrorMsg(errMsg)) {
			FacesContext.getCurrentInstance().addMessage(
					"msgId",new FacesMessage(FacesMessage.SEVERITY_ERROR, errMsg,""));
			return;
		}	
		 isToDataBase = beansToDataBase(ConverterRestrictedCustomerMappingVos);
			
			
		if(isToDataBase){
			
			FacesContext.getCurrentInstance().addMessage(
					"msgId",new FacesMessage(FacesMessage.SEVERITY_INFO,ResourceMB.getText("wq.message.uploadSuccess")+".",""));
		}else{
			
			FacesContext.getCurrentInstance().addMessage(
					"msgId",new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.message.uploadError")+".",""));
		}
	}
	
	public String verifySoldToCodeInDb(List<RestrictedCustomerMappingVo> beans){
		StringBuffer sb = new StringBuffer();
		int indexRow =2;
		for(RestrictedCustomerMappingVo vo :beans){
			if(vo.getSoldToCode()!=null){
				Customer customer = customerSB.findByPK(vo.getSoldToCode());
				if(customer==null){   
					sb.append(ResourceMB.getParameterizedText("wq.error.soldCodeDataError", String.valueOf(indexRow))+",<br/>");
				}
			}
			indexRow++;
		}
		return sb.toString();
	}
	

	public StreamedContent getDownloadReport() {
		List<RestrictedCustomerMappingVo> datas = bean2Vo(restrictedCustomerMappingSB.search(criteria));
		downloadReport = downLoadStrategy.getDownloadFile(bizUnitName, datas, TEMPLATE_NAME);

		if (downloadReport == null) {
	
			FacesContext.getCurrentInstance().addMessage(
					"growl",new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.message.downloadError")+"!", ""));
			return null;
		}

		return downloadReport;
	}
	
	private List<RestrictedCustomerMappingVo> bean2Vo(List<RestrictedCustomerMapping> searchResult) {
		List<RestrictedCustomerMappingVo> list = new ArrayList<RestrictedCustomerMappingVo>();
		RestrictedCustomerMappingVo vo = null;
		RestrictedCustomerMapping bean = null;
		for(int i=0;i<searchResult.size();i++){
			vo = new RestrictedCustomerMappingVo();
			bean = searchResult.get(i);
			vo.setLineSeq((i+1));
			vo.setBizUnit(bean.getBizUnit());
			//vo.setCostIndicator(bean.getCostIndicator());
			vo.setEndCustomerCode(bean.getEndCustomerCode());
			vo.setMfr(bean.getMfr());
			vo.setPartNumber(bean.getPartNumber());
			vo.setProductGroup1(bean.getProductGroup1());
			vo.setProductGroup2(bean.getProductGroup2());
			vo.setProductGroup3(bean.getProductGroup3());
			vo.setProductGroup4(bean.getProductGroup4());
			vo.setSoldToCode(bean.getSoldToCode());
			list.add(vo);
		}	
		return list;
	}
	
	private Boolean beansToDataBase(List<RestrictedCustomerMappingVo> beans) {
		Boolean bool = false;
		if (bizUnitName != null) {
			bool = restrictedCustomerMappingSB.batchPersist(beans, bizUnitName);
		}
		return bool;
	}
	
	public List<RestrictedCustomerMappingVo> getRestrictedCustomerMappingVos() {
		return restrictedCustomerMappingVos;
	}

	public void setRestrictedCustomerMappingVos(
			List<RestrictedCustomerMappingVo> restrictedCustomerMappingVos) {
		this.restrictedCustomerMappingVos = restrictedCustomerMappingVos;
	}

	public RestrictedCustomerMappingSearchCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(RestrictedCustomerMappingSearchCriteria criteria) {
		this.criteria = criteria;
	}

	public UploadedFile getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(UploadedFile uploadFile) {
		this.uploadFile = uploadFile;
	}
	
	public boolean isRecordsExceedMaxAllowed() {
		return recordsExceedMaxAllowed;
	}

	public void setRecordsExceedMaxAllowed(boolean recordsExceedMaxAllowed) {
		this.recordsExceedMaxAllowed = recordsExceedMaxAllowed;
	}
	public InputStream getTmpInputStream() {
		return tmpInputStream;
	}

	public void setTmpInputStream(InputStream tmpInputStream) {
		this.tmpInputStream = tmpInputStream;
	}
}