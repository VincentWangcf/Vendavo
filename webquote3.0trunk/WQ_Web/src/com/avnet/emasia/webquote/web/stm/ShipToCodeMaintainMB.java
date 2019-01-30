package com.avnet.emasia.webquote.web.stm;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.poi.ss.usermodel.Sheet;
import org.primefaces.model.UploadedFile;

import com.avnet.emasia.webquote.dp.EJBCommonSB;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.ejb.SystemCodeMaintenanceSB;
import com.avnet.emasia.webquote.stm.dto.ShipToCodeMappingVo;
import com.avnet.emasia.webquote.stm.ejb.ShipToCodeSB;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.web.maintenance.UploadStrategy;
import com.avnet.emasia.webquote.web.user.UserInfo;

@ManagedBean
@SessionScoped
public class ShipToCodeMaintainMB implements Serializable{

	private static final long serialVersionUID = -7559014052207983346L;
	private static final Logger LOG = Logger.getLogger(ShipToCodeMaintainMB.class.getName());
	
	@EJB
	private ShipToCodeSB shipToCodeSB;
	@EJB
	private UserSB userSB;
	
	@EJB
	private SystemCodeMaintenanceSB systemCodeMaintenanceSB;

	private User user = null;
	
	@EJB
	protected EJBCommonSB ejbCommonSB;


	private transient UploadedFile uploadFile;
	private String uploadFileName;
	private UploadStrategy uploadStrategy = null;
	
	@PostConstruct
	public void postContruct() {
		user = UserInfo.getUser();

		uploadStrategy = new ShipToCodeUploadStrategy();
		uploadStrategy.setUser(user);
	}

	public void removeAllAndUpload() {
		LOG.info("begin Upload ........");
		
		if(null==uploadFile){
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, ResourceMB.getText("wq.message.pleaseSelectFile")+".", ""));
			return;
		}
		long start =0;
		boolean isErrorFound=false; 
		isErrorFound = ejbCommonSB.uploadFileSizeCheck(systemCodeMaintenanceSB, uploadFile, uploadStrategy, start);
		if(isErrorFound)
		{
			return;
		}
		uploadFileName = uploadFile.getFileName();
		Sheet sheet =  uploadStrategy.getSheet(uploadFile);

		List<ShipToCodeMappingVo> beans = uploadStrategy.excel2Beans(sheet);
		String errMsg = uploadStrategy.verifyFields(beans);
		if (!errMsg.equals("")&&!errMsg.equals("[]")) {
			FacesContext.getCurrentInstance().addMessage(
					"msgId",new FacesMessage(FacesMessage.SEVERITY_ERROR, errMsg.substring(1,errMsg.length()-1).replace(',', ' '),""));
			return;
		}
		errMsg =shipToCodeSB.verifyTeamExist(beans, UserInfo.getUser().getUserLocale());
			
		if (!errMsg.equals("")&&!errMsg.equals("[]")) {
			FacesContext.getCurrentInstance().addMessage(
				"msgId",new FacesMessage(FacesMessage.SEVERITY_ERROR, errMsg.substring(1,errMsg.length()-1).replace(',', ' '),""));
			return;
		}
		
		boolean saveSuccessful = shipToCodeSB.deleteAndSaveToDB(beans);
		
		if(saveSuccessful){
			FacesContext.getCurrentInstance().addMessage("msgId",
				new FacesMessage(FacesMessage.SEVERITY_INFO, uploadFileName + ResourceMB.getText("wq.message.uploadSuccess")+".",""));
		}else{
			FacesContext.getCurrentInstance().addMessage("msgId",
				new FacesMessage(FacesMessage.SEVERITY_ERROR, uploadFileName + ResourceMB.getText("wq.message.uploadError")+".",""));	
		}
		
		long end = System.currentTimeMillis();
		LOG.info("End upload BMP Customer,takes " + (end - start) + " ms");	
	
	}

	public UploadedFile getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(UploadedFile uploadFile) {
		this.uploadFile = uploadFile;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}
	
}
