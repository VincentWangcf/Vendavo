package com.avnet.emasia.webquote.web.edi.mb;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.eclipse.jetty.util.log.Log;
import org.primefaces.model.StreamedContent;

import com.avnet.emasia.webquote.codeMaintenance.ejb.CodeMaintenanceSB;
import com.avnet.emasia.webquote.constants.EdiMesgType;
import com.avnet.emasia.webquote.edi.AMesgRefuse;
import com.avnet.emasia.webquote.edi.DMesgRefuse;
import com.avnet.emasia.webquote.edi.TIInfoExchangeSB;
import com.avnet.emasia.webquote.edi.utils.TISearchBean;
import com.avnet.emasia.webquote.entity.BalUnconsumedQty;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.web.edi.load.TILoadFileDownLoadStrategy;
import com.avnet.emasia.webquote.web.user.UserInfo;

@ManagedBean(name = "tILoadMB")
@SessionScoped
public class TILoadMB {

	
 	/**
	 * 
	 */
	private static final long serialVersionUID = -63377586495631333L;
	static Logger logger = Logger.getLogger(TILoadMB.class.getName());
	@EJB
    private  TIInfoExchangeSB tIExchangeSB;
	private transient TISearchBean tISearchBean;
	private transient StreamedContent downloadReport;
	//private String selectedMesg;

	private  SelectItem[] mesgs;
	private User user ;
	public TILoadMB() {
		
	}
	
	@PostConstruct
	public void init() {
		initMesgs();
		tISearchBean = new TISearchBean();
		tISearchBean.setMesgType((String)this.getMesgs()[0].getValue());
	}
	
	public void mesgTypeChange() {
		//logger.info(tISearchBean.getMesgType());
		if("3A1".equals(tISearchBean.getMesgType())) {
			this.tISearchBean.setQuoteNum(null);
		}
	}
	
	public StreamedContent getDownloadReport() {
		 
		 /*verify data from UI**/
		 /*ONE CHECK BLANK dATETO*/
		 if(tISearchBean.getCreateDateTo() == null) {
			 FacesContext.getCurrentInstance().addMessage(
						"msgId",new FacesMessage(FacesMessage.SEVERITY_ERROR, 
								ResourceMB.getText("wq.message.tidatetoblank"), ""));
			 return null;
		 }
		 /**[Date To] Should After [Date From]*/
		 if(tISearchBean.getCreateDateFrom()!=null && tISearchBean.getCreateDateTo()!=null &&
				 tISearchBean.getCreateDateTo().before(tISearchBean.getCreateDateFrom()) ) {
			 FacesContext.getCurrentInstance().addMessage(
						"msgId",new FacesMessage(FacesMessage.SEVERITY_ERROR,
								ResourceMB.getText("wq.message.tidateafterfrom"), ""));
			 return null;
		 }
		 /**special char*/
		 String specialChar1 = "*";
		 String specialChar2 = "#";
		 if(tISearchBean.getPoNum()!=null && (tISearchBean.getPoNum().contains(specialChar1)||
				 tISearchBean.getPoNum().contains(specialChar2))) {
			 FacesContext.getCurrentInstance().addMessage(
						"msgId",new FacesMessage(FacesMessage.SEVERITY_ERROR,
								ResourceMB.getText("wq.message.tisepcialchar"), ""));
			 return null;
		 }
		 if(tISearchBean.getQuoteNum()!=null && (tISearchBean.getQuoteNum().contains(specialChar1)||
				 tISearchBean.getQuoteNum().contains(specialChar2))) {
			 FacesContext.getCurrentInstance().addMessage(
						"msgId",new FacesMessage(FacesMessage.SEVERITY_ERROR,
								ResourceMB.getText("wq.message.tiqnumsepcialchar"), ""));
			 return null;
		 }
		 
		 user = UserInfo.getUser();
		 //tISearchBean.getMesgType();
		 TILoadFileDownLoadStrategy tILoadFileDownLoadStrategy = new TILoadFileDownLoadStrategy(tISearchBean.getMesgType());
		 try{
			 List<?> datas = tIExchangeSB.findMesg(tISearchBean); 
			 Log.info("getDownloadReport() datas size::" + datas.size());
			 if(datas.size()==0){
				 downloadReport = tILoadFileDownLoadStrategy.
						 getTemplateFile(tILoadFileDownLoadStrategy.getCurExchengCtx().getTemplateFileName());
			}else {
				downloadReport = tILoadFileDownLoadStrategy.getDownloadFile(user.getDefaultBizUnit().getName(), 
				 datas, tILoadFileDownLoadStrategy.getCurExchengCtx().getTemplateFileName());
			}
		 
			 if (downloadReport == null) {
					FacesContext.getCurrentInstance().addMessage(
							null,new FacesMessage(FacesMessage.SEVERITY_ERROR,
									ResourceMB.getText("wq.message.tidownloaderr"), ""));
				}
		 }catch(Exception ex) {
			 logger.info(user.getEmployeeId() +":TILoadMB:loadFile::: "+ex.getMessage());
		 }
		 return downloadReport;
	}
	
	public void JobTest() {
		StringBuilder strBuilder = new StringBuilder();
		try{
			tIExchangeSB.tiXmlToDB(strBuilder);
			tIExchangeSB.tiCsvToDB(strBuilder);
		}catch(Exception ex) {
			//ex.printStackTrace();
			/*FacesContext.getCurrentInstance().addMessage(
					"msgId",new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ""));*/
		}
		if(strBuilder.length()>0) {
			FacesContext.getCurrentInstance().addMessage(
					"msgId",new FacesMessage(FacesMessage.SEVERITY_ERROR, strBuilder.toString(), ""));
		}
	}
	
	public void deleteOverTimeData() {
		try{
			tIExchangeSB.deleteOverTimeData();
		}catch(Exception ex) {
			logger.severe(ex.toString());
		}
	}
	
	/*public StreamedContent getDownloadReport() {
		List<BalUnconsumedQty> datas = balanceUnconsumedQtySB.findAllByBizUnits(bizUnitName);
		if(datas.size()==0){
			return downloadSrategy.getTemplateFile(TEMPLATE_NAME);
		}
		downloadReport = downloadSrategy.getDownloadFile(bizUnitName, datas,TEMPLATE_NAME);

		if (downloadReport == null) {
			FacesContext.getCurrentInstance().addMessage(
					null,new FacesMessage(FacesMessage.SEVERITY_ERROR," download error!", ""));
			return null;
		}

		return downloadReport;
	}	*/
	

	public SelectItem[] getMesgs() {
		return mesgs;
	}
	
	private void initMesgs() {
		mesgs = new SelectItem[4];
		int i =0;
		mesgs[i++] = new SelectItem(EdiMesgType.TAONE.getMesgTypeName(),"3A1");
		mesgs[i++] = new SelectItem(EdiMesgType.TARONE.getMesgTypeName(),"3A1(R1)");
		mesgs[i++] = new SelectItem(EdiMesgType.FDRONE.getMesgTypeName(),"5D1(R1)");
		//NOTE
		mesgs[i++] = new SelectItem(EdiMesgType.TARTWO.getMesgTypeName(),"3A1(R2)+5D1(R2)");
		
	}
	
	
	public TISearchBean gettISearchBean() {
		return tISearchBean;
	}
	public void settISearchBean(TISearchBean tISearchBean) {
		this.tISearchBean = tISearchBean;
	}
	/*public String getSelectedMesg() {
		return selectedMesg;
	}
	public void setSelectedMesg(String selectedMesg) {
		this.selectedMesg = selectedMesg;
	}*/
	/* if(EdiMesgType.TARONE.getMesgTypeName().equals(selectedMesg)) {
	 List<AMesgRefuse> subList;
	 //do {
	 subList= tIExchangeSB.findAMesgRefuse(tISearchBean);
	 tILoadFile.AR1ToExcel(subList,tISearchBean.getStart());
		 //tISearchBean.setStart(++i * TISearchBean.getMax());
	 //}while(subList.size()==TISearchBean.getMax());
	 
}else if(EdiMesgType.FDRONE.getMesgTypeName().equals(selectedMesg)) {
	 List<DMesgRefuse> subList;
	 subList= tIExchangeSB.findDMesgRefuse(tISearchBean);
	 tILoadFile.DR1ToExcel(subList,tISearchBean.getStart());
}else if(EdiMesgType.TARTWO.getMesgTypeName().equals(selectedMesg)) {
	 List<Object[]> subList;
	 subList= tIExchangeSB.findAandDMesgApprove(tISearchBean);
	 tILoadFile.ADR2ToExcel(subList,tISearchBean.getStart());
}else {
	 logger.info(user.getEmployeeId() +":TILoadMB:loadFile can not find msType");
}
tISearchBean.setStart(0);*/
}
