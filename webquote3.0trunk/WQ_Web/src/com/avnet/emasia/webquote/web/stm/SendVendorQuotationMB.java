package com.avnet.emasia.webquote.web.stm;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import com.avnet.emasia.webquote.dp.EJBCommonSB;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.MfrRequestInfo;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.ejb.QuoteSB;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.quote.vo.WorkingPlatformItemVO;
import com.avnet.emasia.webquote.stm.constant.B2bStatusEnum;
import com.avnet.emasia.webquote.stm.constant.StmConstant;
import com.avnet.emasia.webquote.stm.constant.StmQuoteTypeEnum;
import com.avnet.emasia.webquote.stm.dto.OutBoundVo;
import com.avnet.emasia.webquote.stm.dto.StmConfigInfo;
import com.avnet.emasia.webquote.stm.dto.VendorQuoteSearch;
import com.avnet.emasia.webquote.stm.ejb.BpmMaintainSB;
import com.avnet.emasia.webquote.stm.ejb.MfrRequestInfoSB;
import com.avnet.emasia.webquote.stm.ejb.VendorQuotationSB;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil;
import com.avnet.emasia.webquote.web.quote.CommonBean;
import com.avnet.emasia.webquote.web.stm.util.StmUtil;
import com.avnet.emasia.webquote.web.stm.util.StmXmlProcessTemplate;
import com.avnet.emasia.webquote.web.user.UserInfo;

@ManagedBean
@SessionScoped
public class SendVendorQuotationMB extends CommonBean implements Serializable {
	private static final Logger LOGGER = Logger.getLogger(SendVendorQuotationMB.class.getName());
	
	private static final long serialVersionUID = 1733760928249420856L;	
	
	private transient Method lastSearchMethod = null;
	
	private VendorQuoteSearch criteria = null;
	
	private boolean recordsExceedMaxAllowed = false;	
	
	private List<OutBoundVo> sendQuoteVos = new ArrayList<OutBoundVo>();
	
	private List<OutBoundVo> selectedVos = new ArrayList<OutBoundVo>();	
	
	private List<OutBoundVo> filteredVos;
	
	private User user = null;
	
	private StmConfigInfo stmConfigInfo = null;	
	private final static String REPLACEMENT_1="{1}";
	
	/*
	 * display quotation history
	 */
	private List<WorkingPlatformItemVO> quotationHistorys;
	private List<WorkingPlatformItemVO> filteredQuotationHistorys;
	private String quoteHistorySoldToCustomerNameSearch;
	private String quoteHistoryPartNumberSearch;
	private String quoteHistoryMfrSearch;
	
	
	@EJB
	private VendorQuotationSB vendorQuotationSB;
	
	@EJB
	private UserSB userSB;
	
	@EJB
	private QuoteSB quoteSB;
	
	@EJB
	private BpmMaintainSB bpmMaintainSB;
	
	@EJB
	private MfrRequestInfoSB mfrRequestInfoSB;
	
	@EJB
	protected EJBCommonSB ejbCommonSB;

	
	@PostConstruct
	public void postContruct() {
		user = UserInfo.getUser();
		criteria = new VendorQuoteSearch();
		criteria.setStage(QuoteSBConstant.QUOTE_STAGE_PENDING);
		stmConfigInfo = vendorQuotationSB.getStmSystemCode();
		this.setupMfrsInCriteria();
		this.setupDataAccessInCriteria();		
		this.setupBizUnit();
	}
	
	public void sendSearch() {		
		if(QuoteUtil.isEmpty(criteria.getBpmCode())){
			List<QuoteItem> searchResult = vendorQuotationSB.sendSearch(criteria);
			List<MfrRequestInfo> searchResult2 =mfrRequestInfoSB.findMfrRequestInfosbyQuoteItems(searchResult);
			sendQuoteVos = StmUtil.fillOutBoundVoForQuoteItem(stmConfigInfo,searchResult,searchResult2);
		}else{
			List<MfrRequestInfo> searchResult2 = vendorQuotationSB.searchMfrRequestInfo(criteria);
			sendQuoteVos = StmUtil.fillOutBoundVoForMfrRequestInfo(stmConfigInfo,searchResult2);
		}

		if (sendQuoteVos.size() > 500) {
			recordsExceedMaxAllowed = true;
			for (int i = sendQuoteVos.size() - 1; i >= 500; i--) {
				sendQuoteVos.remove(i);
			}
		} else {
			recordsExceedMaxAllowed = false;
		}
		selectedVos = null;		
		filteredVos = null;		
		lastSearchMethod = getlastSearchMethod();
	}
	
	public String goToSendQuotePage(String quoteType){
		String message;
		if (selectedVos.size() == 0) {
			
			FacesContext.getCurrentInstance().addMessage("messages",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.message.noRecordSelected")+".", ""));
			return null;
		}

		if(selectedVos!=null&&selectedVos.size()>StmConstant.SELECTED_MAX_RESULT){
		
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					 ResourceMB.getParameterizedText("wq.message.selectedMaxResult", String.valueOf(StmConstant.SELECTED_MAX_RESULT))+".", ""));
			return null;
		}
		
		String redirectUrl =  "/stm/SendSgaQuote.xhtml?faces-redirect=true";
		String errorMessage = "";
		if(quoteType!=null&&quoteType.equalsIgnoreCase(StmQuoteTypeEnum.SGA.name())){
			errorMessage = ResourceMB.getText("wq.message.rfqSent")+".";
			redirectUrl =  "/stm/SendSgaQuote.xhtml?faces-redirect=true";
		}else{
			errorMessage = ResourceMB.getText("wq.message.rfqSent")+".";
			redirectUrl =  "/stm/SendSadaQuote.xhtml?faces-redirect=true";
		}
		
		for(OutBoundVo vo:selectedVos){			
			if(!(QuoteUtil.isEmpty(vo.getB2bStatus())||B2bStatusEnum.New.name().equalsIgnoreCase(vo.getB2bStatus())
					||B2bStatusEnum.Refreshed.name().equalsIgnoreCase(vo.getB2bStatus()))){
				FacesContext.getCurrentInstance().addMessage("messages",
						new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
				return null;
			}
		}
		
		for(int i=0;i<selectedVos.size();i++){
			OutBoundVo vo = selectedVos.get(i);
			
			StmUtil.fillTargetResaleCostDefaultValue(quoteType,vo);
			vo.setItemRemark(StmUtil.combineItemRemarks(quoteType,vo));
			if(QuoteUtil.isEmpty(vo.getReqQty())){
				StmUtil.fillReqQtyDefaultValue(quoteType,vo);
			}
			
			StmUtil.fillSeq2InOutBoundVo(vo,i+1);
			
			if(QuoteUtil.isEmpty(vo.getShipToCode())){
				vendorQuotationSB.autoGetShipToCode(quoteType,vo);
			}
		}

		return redirectUrl;
	}
	
	public void updateSelectedQuoteItem(SelectEvent event){
		OutBoundVo outBoundVo = (OutBoundVo)event.getObject();		
		quoteHistoryMfrSearch = outBoundVo.getMfr();//this.selectedQuoteItem.getQuotedMfr();
		quoteHistoryPartNumberSearch = outBoundVo.getFullMPN();
		quoteHistorySoldToCustomerNameSearch = outBoundVo.getQuoteItem().getQuote().getSoldToCustomerName();//.quoteItem.quote.soldToCustomerName;
		if(quotationHistorys!=null){
			quotationHistorys.clear();;	
		}
		RequestContext context = RequestContext.getCurrentInstance();	
		context.update("form_quotationHistory:quoteHistorySearch");
		
	}
	
	public void postProcessXLSForQuoteHistory(Object document){
		String[] columns = {"Quote Stage","RFQ Status","Form#","Avnet Quote#" ,"MFR","MFR P/N","Avnet Quoted Part","Customer","End Customer","Required Qty","EAU" ,"Quoted Margin %","Quoted Price","Cost","Cost Indicator","Target Resale","Salesman","Team","Price Validity","Shipment Validity","MFR Quote #","MFR Debit #","MFR Quote Qty","QC Internal Comment","QC External Comment","PMOQ" ,"MPQ" ,"MOQ" ,"MOV" ,"Lead Time","Multi Usage","Qty Indicator","RFQ Submission Date","Quote Release Date","Target Price Margin %","Quote Reference Margin %","Customer Type","Application" ,"Project Name","Design Location","Rescheduling Window","Cancellation Window","Quotation T&C","Quote Type","RFQ Form Attachment", "RFQ Item Attachment", "Refresh Attachment", "PM Attachment", "PM Comment","Remarks","Item Remarks","Competitor Information","Design-in Cat","DRMS Project ID","MFR DR#","DR Expiry Date","Authorized GP %","Reasons for Authorized GP% Change","Remarks of Reason","PP Schedule","MP Schedule","PO Expiry Date","Quote Expiry Date","Avnet Quoted Qty","Requester Reference","Salesman Code","LOA","Resale Indicator","Alloca- tion Part","Revert Version","First RFQ Code","Segment","BMT Biz","Reason For Refresh","Copy to CS","Product Group 2","Business Program Type","Sold To Code","Ship To Code","Ship To Party","End Customer Code","SAP Material Number","SPR","Quoted By","Sold To Party (Chinese)"};
		postProcessXLS(document, columns);
	}
	
	public void searchQuoteHistory(){
		bizUnit = user.getDefaultBizUnit();
		quotationHistorys = ejbCommonSB.searchQuoteHistory(quoteHistoryMfrSearch, quoteHistoryPartNumberSearch, quoteHistorySoldToCustomerNameSearch, quotationHistorys, quoteSB, bizUnit);
	}
	
	/**
	 *  a.	One or multiple records could be selected and sent out. 
		b.	After selection and clicking the button, a form is popped up for user to check fields available for SADA Quote only. 
			Except MFR, all other fields will be available for editing in the form, but the changes won��t  be saved to rfqs.
		c.	After clicking confirm to sending, system will do the mandatory validation based on above data list
		d.	After data passes validation, the data selected will be put in the XML file and sent to B2B side, 		
		e.	B2B status is updated to Bidding, RFQ status is updated to SQ
	 * @return
	 */
	public String sendQuote(String quoteType){	

		stmConfigInfo = stmConfigInfo==null?vendorQuotationSB.getStmSystemCode():stmConfigInfo;
		String message;
		if(stmConfigInfo==null){
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.message.getSTMConfigFailed"),""));
			return null;
		}
		
		StmXmlProcessTemplate stmXmlProcess = new StmXmlProcessTemplate();
		
		selectedVos = StmUtil.fillOutBoundVoForSendQuote(quoteType,selectedVos);
		
		StringBuffer sb = new StringBuffer();
		sb = stmXmlProcess.batchVerifySendObjNoAnnotationFieldLength(selectedVos);
		if(sb.length()>0){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sb.toString(), ""));
			return null;
		}
		
		
		sb = stmXmlProcess.batchVerifySendObjLength(selectedVos);		
		if(sb.length()>0){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sb.toString(), ""));
			return null;
		}

		sb = stmXmlProcess.batchVerifySendObjMandatory(quoteType,selectedVos);
		if(sb.length()>0){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sb.toString(), ""));
			return null;
		}

		sb = stmXmlProcess.batchVerifySendObjFieldType(selectedVos);
		if(sb.length()>0){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sb.toString(), ""));
			return null;
		}
		
		sb = stmXmlProcess.batchVerifyTargetResaleAndCost(selectedVos);
		if(sb.length()>0){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sb.toString(), ""));
			return null;
		}
		

		HashMap<OutBoundVo,String> hash = stmXmlProcess.batchGetSendXMLFileContent(selectedVos);	
		sb = stmXmlProcess.batchValidateOutBoundXMLByXSD(hash);
		if(sb.length()>0){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sb.toString(), ""));
			return null;
		}

		sb = stmXmlProcess.batchWriteOutboundFileToLocal(stmConfigInfo,hash);
		if(sb.length()>0){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sb.toString(), ""));
			return null;
		}
		sb = mfrRequestInfoSB.verifyQuoteItem(selectedVos, UserInfo.getUser().getUserLocale());
		if(sb.length()>0){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sb.toString(), ""));
			return null;
		}
		try{
			mfrRequestInfoSB.updateMfrRequestInfo(selectedVos, user);
		}catch(Exception e){
			
			LOGGER.log(Level.SEVERE, "Exception occured for quote type: "+quoteType+", Exception message: "+e.getMessage(), e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.message.sendQuoteFailed"),""));
			return null;			
		}	
		
		return "/stm/SendQuoteResult.jsf?faces-redirect=true";
	}

	public String getMyPage() {
		this.selectedVos = null;

		if (lastSearchMethod != null) {
			try {
				lastSearchMethod.invoke(this, null);
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE,"Error occured when calling search method with reflection. Exception message: "+e.getMessage(),e);
			}
		}
		return "/stm/VendorQuoteMaintenance.jsf?faces-redirect=true";
	}
	
	public List<String> autoCompleteBpmCode(String key){
		return bpmMaintainSB.autoCompleteBpmCode(key);
	}
	
	public List<String> autoCompleteBpmName(String key){
		return bpmMaintainSB.autoCompleteBpmName(key);
	}
	
	public void setBpmInfo(int itemNumber, String value) {
		if (value != null) {
			OutBoundVo vo = selectedVos.get(itemNumber - 1);
			String[] bpmCombineInfo = value.split(StmConstant.PART_SPLIT_SEPARATOR);
			if (bpmCombineInfo.length == 3) {
				vo.setBpmCode(bpmCombineInfo[0]);
				vo.setBpmName(bpmCombineInfo[1]);
				vo.setBpmCntCode(bpmCombineInfo[2]);
			}
		}
	}

	public void setEcBpmInfo(int itemNumber, String value) {
		if (value != null) {
			OutBoundVo vo = selectedVos.get(itemNumber - 1);
			String[] bpmCombineInfo = value.split(StmConstant.PART_SPLIT_SEPARATOR);
			if (bpmCombineInfo.length == 3) {
				vo.setEcBpmCode(bpmCombineInfo[0]);
				vo.setEcBPMName(bpmCombineInfo[1]);
				vo.setEcBPMCntCode(bpmCombineInfo[2]);
			}
		}
	}
	
	public VendorQuoteSearch getCriteria() {
		return criteria;
	}
	
	public void setCriteria(VendorQuoteSearch criteria) {
		this.criteria = criteria;
	}
	
	public boolean isRecordsExceedMaxAllowed() {
		return recordsExceedMaxAllowed;
	}

	public void setRecordsExceedMaxAllowed(boolean recordsExceedMaxAllowed) {
		this.recordsExceedMaxAllowed = recordsExceedMaxAllowed;
	}
	public List<OutBoundVo> getSendQuoteVos() {
		return sendQuoteVos;
	}
	public void setSendQuoteVos(List<OutBoundVo> sendQuoteVos) {
		this.sendQuoteVos = sendQuoteVos;
	}

	private void setupMfrsInCriteria() {
		List<String> mfrs = new ArrayList<String>();
		mfrs.add(StmConstant.STM_MFR);
		criteria.setMfr(mfrs);
	}
	
	private void setupBizUnit() {
		List<BizUnit> bizUnits = new ArrayList<BizUnit>();
		bizUnits.add(user.getDefaultBizUnit());
		criteria.setBizUnits(bizUnits);
	}

	private void setupDataAccessInCriteria() {
		User user = UserInfo.getUser();
		criteria.setDataAccesses(userSB.findAllDataAccesses(user));
	}
	
	private Method getlastSearchMethod(){
		Method method = null;
		try {
			method = this.getClass().getDeclaredMethod("sendSearch",null);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE,"Error occured when get search Method with reflection in SendReceiveQuoteMB with Avnet no: "+criteria.getRfqCode()+", "
					+ "MFR P/N: "+criteria.getQuotedPartNumber()+", BPM Code: "+criteria.getBpmCode()+", Exception message: "+e.getMessage(),e);
		}
		return method;
	}

	public List<OutBoundVo> getFilteredVos() {
		return filteredVos;
	}

	public void setFilteredVos(List<OutBoundVo> filteredVos) {
		this.filteredVos = filteredVos;
	}
	
	public List<OutBoundVo> getSelectedVos() {
		return selectedVos;
	}

	public void setSelectedVos(List<OutBoundVo> selectedVos) {
		this.selectedVos = selectedVos;
	}

	public List<WorkingPlatformItemVO> getQuotationHistorys() {
		return quotationHistorys;
	}

	public void setQuotationHistorys(List<WorkingPlatformItemVO> quotationHistorys) {
		this.quotationHistorys = quotationHistorys;
	}

	public List<WorkingPlatformItemVO> getFilteredQuotationHistorys() {
		return filteredQuotationHistorys;
	}

	public void setFilteredQuotationHistorys(
			List<WorkingPlatformItemVO> filteredQuotationHistorys) {
		this.filteredQuotationHistorys = filteredQuotationHistorys;
	}

	public String getQuoteHistorySoldToCustomerNameSearch() {
		return quoteHistorySoldToCustomerNameSearch;
	}

	public void setQuoteHistorySoldToCustomerNameSearch(
			String quoteHistorySoldToCustomerNameSearch) {
		this.quoteHistorySoldToCustomerNameSearch = quoteHistorySoldToCustomerNameSearch;
	}

	public String getQuoteHistoryPartNumberSearch() {
		return quoteHistoryPartNumberSearch;
	}

	public void setQuoteHistoryPartNumberSearch(String quoteHistoryPartNumberSearch) {
		this.quoteHistoryPartNumberSearch = quoteHistoryPartNumberSearch;
	}

	public String getQuoteHistoryMfrSearch() {
		return quoteHistoryMfrSearch;
	}

	public void setQuoteHistoryMfrSearch(String quoteHistoryMfrSearch) {
		this.quoteHistoryMfrSearch = quoteHistoryMfrSearch;
	}

	public void postProcessXLS(Object document, String[] columns){
		ejbCommonSB.postProcessXLS(document, columns, quotationHistorys,"sendVendorQuotationMB");
	}    
	
	
}
