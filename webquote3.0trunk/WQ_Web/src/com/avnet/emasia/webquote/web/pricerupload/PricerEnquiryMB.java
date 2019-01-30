package com.avnet.emasia.webquote.web.pricerupload;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.jms.JMSException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.StreamedContent;

import com.avnet.emasia.webquote.commodity.bean.PricerUploadTemplateBean;
import com.avnet.emasia.webquote.commodity.constant.PRICER_TYPE;
import com.avnet.emasia.webquote.commodity.constant.PricerConstant;
import com.avnet.emasia.webquote.commodity.constant.UPLOAD_ACTION;
import com.avnet.emasia.webquote.commodity.ejb.ContractPriceUploadSB;
//import com.avnet.emasia.webquote.commodity.ejb.PricerEnquiryLazySB;
import com.avnet.emasia.webquote.commodity.ejb.PricerEnquirySB;
import com.avnet.emasia.webquote.commodity.ejb.PricerUploadVerifySB;
import com.avnet.emasia.webquote.commodity.util.PricerUtils;
import com.avnet.emasia.webquote.constants.RemoteEjbClassEnum;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.ejb.ManufacturerSB;
import com.avnet.emasia.webquote.quote.ejb.SystemCodeMaintenanceSB;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.reports.ejb.OfflineReportSB;
import com.avnet.emasia.webquote.user.ejb.BizUnitSB;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.common.CacheUtil;
import com.avnet.emasia.webquote.utilites.web.util.PricerUploadHelper;
import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;
import com.avnet.emasia.webquote.utilities.ejb.MailUtilsSB;
import com.avnet.emasia.webquote.vo.OfflineReportParam;
import com.avnet.emasia.webquote.web.datatable.SelectableLazyDataModel;
import com.avnet.emasia.webquote.web.quote.FacesUtil;
import com.avnet.emasia.webquote.web.quote.cache.CostIndicatorCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.MfrCacheManager;
import com.avnet.emasia.webquote.web.quote.constant.QuoteConstant;
import com.avnet.emasia.webquote.web.user.UserInfo;

@ManagedBean
@SessionScoped
// NEC Pagination Changes: Class modified for implementing lazy loading in
// pagination
public class PricerEnquiryMB implements Serializable {
	private SelectItem[] mfrSelectList;
	private SelectItem[] costIndicatorSelectList;
	private SelectItem[] salesCostFlagList;
	private SelectItem[] regionList;
	private SelectItem[] regionCurrencyList; 
	private transient StreamedContent downloadAll;
	private transient StreamedContent downloadSelected;
	private String bizUnitName = null;
	private boolean recordsExceedMaxAllowed = false;

	private PricerUploadTemplateBean criteria;
	private SelectItem[] typeSelectList;

	//Bryan Start
	@EJB
	private CacheUtil cacheUtil;
	//Bryan End
	@EJB
	private SysConfigSB sysConfigSB;
	@EJB
	private BizUnitSB bizUnitSB;

	@EJB
	PricerEnquirySB pricerEnquirySB;
	@EJB
	PricerEnquiryOffline pricerEnquiryOffline;
	@EJB
	private ManufacturerSB manufacturerSB;
	@EJB
	private PricerUploadVerifySB pricerUploadVerifySB;
	@EJB
	private UserSB userSB;
	@EJB
	private ContractPriceUploadSB contractPriceUploadSB;
	@EJB
	private PricerEnquirySB peSB;
	@EJB
	protected MailUtilsSB mailUtilsSB;

	@EJB
	private SystemCodeMaintenanceSB sysCodeMaintSB;

	@EJB
	OfflineReportSB offlineReprtSB;
	/** The resource MB. */
	@ManagedProperty(value = "#{resourceMB}")
	/** To Inject ResourceMB instance */
	private ResourceMB resourceMB;

	private PricerDownLoadStrategy downloadSrategy = null;

	private static final long serialVersionUID = -7299394000861327426L;
	private static final Logger LOG = Logger.getLogger(PricerEnquiryMB.class.getName());
	private String pricerType = null;

	private boolean isNormalMaterial = true;
	private boolean isProgramMaterial = true;
	private boolean isContractPricer = true;
	private boolean isSalesCostPricer = true;
	private boolean isSimplePricer = true;

	private User user = null;
	private transient Method lastSearchMethod;

	// NEC Pagination changes: initialize the Lazy data model for supporting the
	// lazy loading in pagination
	LazyDataModelForPricerEnquiry beans = new LazyDataModelForPricerEnquiry();

	/**
	 * @param resourceMB
	 *            the resourceMB to set
	 */
	public void setResourceMB(ResourceMB resourceMB) {
		this.resourceMB = resourceMB;
	}

	/**
	 * Searchs pricer(normal,program, contractn, salescost, simple).
	 */
	public void pricerSearch() {
		LOG.info("pricerSearch begin...");
		long start = System.currentTimeMillis();
		try {
			pricerType = criteria.getPricerType();
			User user = UserInfo.getUser();
			criteria.setBizUnit(user.getDefaultBizUnit());
			if (QuoteUtil.isEmpty(criteria.getMfr())) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceMB.getText("wq.message.mrfField"), ""));
				return;
			}

			int minLenSearchKey4Others = 5;
			int minLenSearchKey4CustCoder = 3;

			boolean isMinLength = validateCriteriaFieldMinLength(criteria, minLenSearchKey4Others,
					minLenSearchKey4CustCoder);

			if (isMinLength) {
				Object[] obj = { minLenSearchKey4CustCoder, minLenSearchKey4Others };
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
						ResourceMB.getParameterizedString("wq.message.minLenghtSearchKey", obj), "");
				FacesContext.getCurrentInstance().addMessage("msgId", msg);
				return;
			}

			// NEC Pagination changes: Set datatable's first record value
			String tableId = "pcrEnquiryForm:pricr_datatable";
			if (isNormalMaterial()) {
				tableId = "pcrEnquiryForm:pricr_datatable";
			} else if (isProgramMaterial()) {
				tableId = "pcrEnquiryForm:datatable_prg";
			} else if (isContractPricer()) {
				tableId = "pcrEnquiryForm:ctrp_table";
			} else if (this.isSalesCostPricer()) {
				tableId = "pcrEnquiryForm:scost_table";
			} else if (this.isSimplePricer()) {
				tableId = "pcrEnquiryForm:simple_table";
			}
			// NEC Pagination Changes: Clear filter and sort field when reset is
			// clicked
			final DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent(tableId);
			int first = 0;
			if (null != d) {
				d.setFirst(first);
			}

			if (isNormalMaterial) {
				beans.setSelectionItems(beans.getSelectedNormals());
			} else if (isProgramMaterial) {
				beans.setSelectionItems(beans.getSelectedPrograms());
			} else if (this.isContractPricer()) {
				beans.setSelectionItems(beans.getSelectedContracts());
			} else if (this.isSalesCostPricer()) {
				beans.setSelectionItems(beans.getSelectedSalesCosts());
			} else if (this.isSimplePricer()) {
				beans.setSelectionItems(beans.getSelectedSimples());
			}
			beans.startPagination();
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Enquiry upload pricer failed! for pricer type : " + pricerType + ", business Unit : "
					+ bizUnitName + ", MFR " + criteria.getMfr() + ", message" + e.getMessage(), e);
		}

		try {
			lastSearchMethod = this.getClass().getDeclaredMethod("pricerSearch", null);
		} catch (Exception e) {
			LOG.log(Level.SEVERE,
					"Error occured when get search Method with reflection in PricerEnquiryMB for pricer type : "
							+ pricerType + ", business Unit : " + bizUnitName + ", MFR " + criteria.getMfr()
							+ ", message" + e.getMessage(),
					e);
		}
		long end = System.currentTimeMillis();
		LOG.info("pricerSearch end,takes " + (end - start) + " ms");
	}

	@PostConstruct
	public void postContruct() {
		user = UserInfo.getUser();
		bizUnitName = user.getDefaultBizUnit().getName();
		this.changePullDownMenu(bizUnitName);
		downloadSrategy = new PricerDownLoadStrategy();
		criteria = new PricerUploadTemplateBean();
		criteria.setPricerType(PRICER_TYPE.NORMAL.getName());
		this.pricerType = criteria.getPricerType();
		Date shipmentValidityFrom = PricerUtils.getTimesmorning(PricerUtils.getAfterMonth(-6));
		criteria.setShipmentValidityFrom(shipmentValidityFrom);
		Date quotationEffectiveDateFrom = PricerUtils.getTimesmorning(PricerUtils.getAfterMonth(-6));

		criteria.setQuotationEffectiveDateFrom(quotationEffectiveDateFrom);
		criteria.setRegion(bizUnitName);
		resetPricerType();
		setNormalMaterial(true);

		// if(beans==null){
		// NEC Pagination changes: initialize the Lazy data model for supporting
		// the pagination lazy loading
		beans = new LazyDataModelForPricerEnquiry();
		// }
	}

	public String getMyPage() {

		beans.setCacheSelectedItems(new HashSet<>());

		if (lastSearchMethod != null) {
			try {
				lastSearchMethod.invoke(this, null);
			} catch (Exception e) {
				LOG.log(Level.SEVERE,
						"Error occured when calling search method with reflection. on last serched method : "
								+ lastSearchMethod + ", Exception message: " + e.getMessage(),
						e);
			}
		}
		return "/RFQ/PricerEnquiry.jsf?faces-redirect=true";
	}

	public void removeSelectedPricer() {
		if ((PRICER_TYPE.CONTRACT.getName().equalsIgnoreCase(pricerType)
				&& (null == this.beans.getSelectedContracts() || 0 == this.beans.getSelectedContracts().size()))
				|| (PRICER_TYPE.NORMAL.getName().equalsIgnoreCase(pricerType)
						&& (null == this.beans.getSelectedNormals() || 0 == this.beans.getSelectedNormals().size()))
				|| (PRICER_TYPE.PROGRAM.getName().equalsIgnoreCase(pricerType)
						&& (null == this.beans.getSelectedPrograms() || 0 == this.beans.getSelectedPrograms().size()))
				|| (PRICER_TYPE.SALESCOST.getName().equalsIgnoreCase(pricerType)
						&& (null == this.beans.getSelectedSalesCosts()
								|| 0 == this.beans.getSelectedSalesCosts().size()))
				|| (PRICER_TYPE.SIMPLE.getName().equalsIgnoreCase(pricerType)
						&& (null == this.beans.getSelectedSimples() || 0 == this.beans.getSelectedSimples().size()))) {
			FacesContext.getCurrentInstance().addMessage("messages",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceMB.getText("wq.message.selectPricer"), ""));
			return;
		}

		// to confirm this function
		List<PricerUploadTemplateBean> selectBeans = new ArrayList<>(this.beans.getCacheSelectedItems());
		/********* edit check ******/
		PriceUploadStrategy uploadStrategy = new PriceUploadStrategy();
		/********* need to get materialRegional before edit check ******/
		for (PricerUploadTemplateBean bean : selectBeans) {
			long id = bean.getMetarialDetailId();
			bean.setMaterialRegional(pricerEnquirySB.getMRegionalByPricerID(id, user));
		}
		String errMsg = uploadStrategy.verifyAccessSCFlagInMRegional(pricerType, user, selectBeans,
				UPLOAD_ACTION.ADD_UPDATE);
		if (PricerUploadHelper.isHaveErrorMsg(errMsg)) {
			FacesContext.getCurrentInstance().addMessage("messages",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, errMsg, ""));
			return;
		}

		/****
		 * remove the v) Remove the necessity of uploading a dummy ‘A-Book Cost’
		 * requirement for Normal pricer. Also, remove the restriction of not
		 * allowing users to remove A-Book Cost pricer. (Depend on development
		 * effort. May not be able to implement in this project)
		 ****/
		/*
		 * String errorMsgOfAbookCost = null;
		 * if(PRICER_TYPE.NORMAL.getName().equalsIgnoreCase(pricerType)) {
		 * for(PricerUploadTemplateBean bean : beans.getSelectedNormals()){
		 * if(PricerConstant.A_COST_INDICATOR.equalsIgnoreCase(bean.
		 * getCostIndicator())) { errorMsgOfAbookCost =
		 * ResourceMB.getText("wq.message.bookcost"); } } }
		 * 
		 * if(PRICER_TYPE.CONTRACT.getName().equalsIgnoreCase(pricerType)) {
		 * for(PricerUploadTemplateBean bean : beans.getSelectedContracts()){
		 * if(PricerConstant.A_COST_INDICATOR.equalsIgnoreCase(bean.
		 * getCostIndicator())) { errorMsgOfAbookCost =
		 * ResourceMB.getText("wq.message.bookcost"); } } }
		 * 
		 * if(PRICER_TYPE.PROGRAM.getName().equalsIgnoreCase(pricerType)) {
		 * for(PricerUploadTemplateBean bean : beans.getSelectedPrograms()){
		 * if(PricerConstant.A_COST_INDICATOR.equalsIgnoreCase(bean.
		 * getCostIndicator())) { errorMsgOfAbookCost =
		 * ResourceMB.getText("wq.message.bookcost"); } } }
		 * if(!QuoteUtil.isEmpty(errorMsgOfAbookCost)) {
		 * FacesContext.getCurrentInstance().addMessage( "messages",new
		 * FacesMessage(FacesMessage.SEVERITY_ERROR, errorMsgOfAbookCost, ""));
		 * return; }
		 */

		boolean isSuccess = false;

		int[] count = new int[5];
		if (PRICER_TYPE.CONTRACT.getName().equalsIgnoreCase(pricerType)) {
			isSuccess = pricerEnquirySB.removePricer(pricerType, beans.getSelectedContracts(), user, count);
		} else if (PRICER_TYPE.NORMAL.getName().equalsIgnoreCase(pricerType)) {
			isSuccess = pricerEnquirySB.removePricer(pricerType, beans.getSelectedNormals(), user, count);
		} else if (PRICER_TYPE.PROGRAM.getName().equalsIgnoreCase(pricerType)) {
			isSuccess = pricerEnquirySB.removePricer(pricerType, beans.getSelectedPrograms(), user, count);
		} else if (PRICER_TYPE.SALESCOST.getName().equalsIgnoreCase(pricerType)) {
			isSuccess = pricerEnquirySB.removePricer(pricerType, beans.getSelectedSalesCosts(), user, count);
		} else if (PRICER_TYPE.SIMPLE.getName().equalsIgnoreCase(pricerType)) {
			isSuccess = pricerEnquirySB.removePricer(pricerType, beans.getSelectedSimples(), user, count);
		}

		if (!isSuccess) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceMB.getText("wq.error.removePrice"), ""));
		} else {
			StringBuffer message = new StringBuffer();
			message.append(ResourceMB.getText("wq.message.selPricerRecordsRemoved"));
			pricerSearch();
			FacesContext.getCurrentInstance().addMessage("removeAllPricerKey",
					new FacesMessage(FacesMessage.SEVERITY_INFO, message.toString(), ""));
		}

	}

	// private List<> getSelectBeanBy
	public void validateRemoveSelectedMFRPricer() {
		if (QuoteUtil.isEmpty(pricerType)) {
			FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.message.recordsSelected"), ""));
			return;
		}
		if (PRICER_TYPE.NORMAL.getName().equalsIgnoreCase(pricerType)) {
			if (beans.getSelectedNormals().size() != 1) {
				FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_ERROR,
						ResourceMB.getText("wq.message.recordsSelected"), ""));
				return;
			}
			beans.setSelected(beans.getSelectedNormals().get(0));

		} else if (PRICER_TYPE.PROGRAM.getName().equalsIgnoreCase(pricerType)) {
			if (beans.getSelectedPrograms().size() != 1) {
				FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_ERROR,
						ResourceMB.getText("wq.message.recordsSelected"), ""));
				return;
			}
			beans.setSelected(beans.getSelectedPrograms().get(0));

		} else if (PRICER_TYPE.CONTRACT.getName().equalsIgnoreCase(pricerType)) {
			if (beans.getSelectedContracts().size() != 1) {
				FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_ERROR,
						ResourceMB.getText("wq.message.recordsSelected"), ""));
				return;
			}
			beans.setSelected(beans.getSelectedContracts().get(0));

		} else if (PRICER_TYPE.SALESCOST.getName().equalsIgnoreCase(pricerType)) {
			if (beans.getSelectedSalesCosts().size() != 1) {
				FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_ERROR,
						ResourceMB.getText("wq.message.recordsSelected"), ""));
				return;
			}
			beans.setSelected(beans.getSelectedSalesCosts().get(0));
		} else if (PRICER_TYPE.SIMPLE.getName().equalsIgnoreCase(pricerType)) {
			if (beans.getSelectedSimples().size() != 1) {
				FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_ERROR,
						ResourceMB.getText("wq.message.recordsSelected"), ""));
				return;
			}
			beans.setSelected(beans.getSelectedSimples().get(0));
		}

		RequestContext.getCurrentInstance().execute("PF('removeAllPricerForSelectedMfrConfirmationDialog').show()");

	}

	// TODO
	public void removeSelectedMFRPricer() {

		boolean isSuccess = false;

		int[] count = new int[5];

		isSuccess = pricerEnquirySB.removeAllPricerForSelectedMfr(beans.getSelected().getMfr(), user, count);

		if (!isSuccess) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceMB.getText("wq.error.removePrice"), ""));
		} else {
			StringBuffer message = new StringBuffer();
			String msg = null;
			msg = ResourceMB.getText("wq.label.ALL");
			message.append(msg + "[");
			message.append(beans.getSelected().getMfr());
			msg = ResourceMB.getText("wq.message.Pricer");
			message.append("]" + msg + "[");
			message.append(count[0] + count[1] + count[2]);
			msg = ResourceMB.getText("wq.message.recordsRemovedNormalPricer");
			message.append("]" + msg + "[");
			message.append(count[0]);
			msg = ResourceMB.getText("wq.message.progPricer");
			message.append("]" + msg + "[");
			message.append(count[1]);
			msg = ResourceMB.getText("wq.message.contractPricer");
			message.append("]" + msg + "[");
			message.append(count[2]);
			message.append("]");
			msg = ResourceMB.getText("wq.message.salesCostPricer");
			message.append("]" + msg + "[");
			message.append(count[3]);
			message.append("]");
			msg = ResourceMB.getText("wq.message.simplePricer");
			message.append("]" + msg + "[");
			message.append(count[4]);
			message.append("]");
			pricerSearch();
			FacesContext.getCurrentInstance().addMessage("removeAllPricerKey",
					new FacesMessage(FacesMessage.SEVERITY_INFO, message.toString(), ""));
		}

	}

	public String goToEditPrice() {
		boolean isAllowEdit = true;
		List<PricerUploadTemplateBean> selectBeans = null;
		if (PRICER_TYPE.NORMAL.getName().equalsIgnoreCase(pricerType)) {
			if (beans.getSelectedNormals().size() == 0) {
				FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_ERROR,
						ResourceMB.getText("wq.message.noRecordSelected") + " !", ""));
				return null;
			}
			String message = PricerUploadHelper.isHaveSameKey(beans.getSelectedNormals());
			if (PricerUploadHelper.isHaveErrorMsg(message)) {
				FacesContext.getCurrentInstance().addMessage("messages",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, message, ""));
				return null;
			}
			if (beans.getSelectedNormals() != null && beans.getSelectedNormals().size() > 100) {
				isAllowEdit = false;
			}
			selectBeans = beans.getSelectedNormals();
		} else if (PRICER_TYPE.PROGRAM.getName().equalsIgnoreCase(pricerType)) {
			if (beans.getSelectedPrograms().size() == 0) {
				FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_ERROR,
						ResourceMB.getText("wq.message.noRecordSelected") + " !", ""));
				return null;
			}
			String message = PricerUploadHelper.isHaveSameKey(beans.getSelectedPrograms());
			if (PricerUploadHelper.isHaveErrorMsg(message)) {
				FacesContext.getCurrentInstance().addMessage("messages",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, message, ""));
				return null;
			}
			if (beans.getSelectedPrograms() != null && beans.getSelectedPrograms().size() > 100) {
				isAllowEdit = false;
			}
			selectBeans = beans.getSelectedPrograms();
		} else if (PRICER_TYPE.CONTRACT.getName().equalsIgnoreCase(pricerType)) {
			if (beans.getSelectedContracts().size() == 0) {
				FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_ERROR,
						ResourceMB.getText("wq.message.noRecordSelected") + " !", ""));
				return null;
			}
			String message = PricerUploadHelper.isHaveSameKey(beans.getSelectedContracts());
			if (PricerUploadHelper.isHaveErrorMsg(message)) {
				FacesContext.getCurrentInstance().addMessage("messages",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, message, ""));
				return null;
			}
			if (beans.getSelectedContracts() != null && beans.getSelectedContracts().size() > 100) {
				isAllowEdit = false;
			}
			selectBeans = beans.getSelectedContracts();
		} else if (PRICER_TYPE.SALESCOST.getName().equalsIgnoreCase(pricerType)) {
			if (beans.getSelectedSalesCosts().size() == 0) {
				FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_ERROR,
						ResourceMB.getText("wq.message.noRecordSelected") + " !", ""));
				return null;
			}
			String message = PricerUploadHelper.isHaveSameKey(beans.getSelectedSalesCosts());
			if (PricerUploadHelper.isHaveErrorMsg(message)) {
				FacesContext.getCurrentInstance().addMessage("messages",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, message, ""));
				return null;
			}
			if (beans.getSelectedSalesCosts() != null && beans.getSelectedSalesCosts().size() > 100) {
				isAllowEdit = false;
			}
			selectBeans = beans.getSelectedSalesCosts();
		} else if (PRICER_TYPE.SIMPLE.getName().equalsIgnoreCase(pricerType)) {
			if (beans.getSelectedSimples().size() == 0) {
				FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_ERROR,
						ResourceMB.getText("wq.message.noRecordSelected") + " !", ""));
				return null;
			}
			String message = PricerUploadHelper.isHaveSameKey(beans.getSelectedSimples());
			if (PricerUploadHelper.isHaveErrorMsg(message)) {
				FacesContext.getCurrentInstance().addMessage("messages",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, message, ""));
				return null;
			}
			if (beans.getSelectedSimples() != null && beans.getSelectedSimples().size() > 100) {
				isAllowEdit = false;
			}
			selectBeans = beans.getSelectedSimples();
		}
		if (!isAllowEdit) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceMB.getText("wq.message.editMax"), ""));
			return null;
		}
		/********* edit check ******/
		PriceUploadStrategy uploadStrategy = new PriceUploadStrategy();
		/********* need to get materialRegional before edit check ******/
		for (PricerUploadTemplateBean bean : selectBeans) {
			long id = bean.getMetarialDetailId();
			bean.setMaterialRegional(pricerEnquirySB.getMRegionalByPricerID(id, user));
		}
		String errMsg = uploadStrategy.verifyAccessSCFlagInMRegional(pricerType, user, selectBeans,
				UPLOAD_ACTION.ADD_UPDATE);
		if (PricerUploadHelper.isHaveErrorMsg(errMsg)) {
			FacesContext.getCurrentInstance().addMessage("messages",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, errMsg, ""));
			return null;
		}
		return "/PricerUpload/EditPrice.xhtml?faces-redirect=true";
	}

	// DONE
	public String editPrice() {
		boolean isSuccess = false;
		List<PricerUploadTemplateBean> beanList = new ArrayList<>(beans.getCacheSelectedItems());
		if (PRICER_TYPE.NORMAL.getName().equalsIgnoreCase(pricerType)) {
			String errorMsg = verifyInSelectList(beans.getSelectedNormals());
			if (!QuoteUtil.isEmpty(errorMsg)) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMsg, ""));
				return null;
			}
			// HashMap<?, ?> hashMapCache =
			// PricerUploadHelper.getPricerCache(user);
			// List<Manufacturer> mfrListCache = (List<Manufacturer>)
			// hashMapCache.get("mfr");
			List<Manufacturer> manufacturers = manufacturerSB.findManufacturerByBizUnit(user.getDefaultBizUnit());
			List<Manufacturer> mfrLst = PricerUtils.getMfrInUploadFile(beanList, manufacturers);
			errorMsg = pricerUploadVerifySB.batchVerifyEffectiveDateInDB(PRICER_TYPE.NORMAL.getName(),
					beans.getSelectedNormals(), user, mfrLst);
			if (!QuoteUtil.isEmpty(errorMsg)) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMsg, ""));
				return null;
			}
			isSuccess = pricerEnquirySB.updateNormalPricerMaterialDetail(beans.getSelectedNormals(), user);
		} else if (PRICER_TYPE.PROGRAM.getName().equalsIgnoreCase(pricerType)) {
			String errorMsg = verifyInSelectList(beans.getSelectedPrograms());
			if (!QuoteUtil.isEmpty(errorMsg)) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMsg, ""));
				return null;
			}
			// HashMap<?, ?> hashMapCache =
			// PricerUploadHelper.getPricerCache(user);
			// List<Manufacturer> mfrListCache = (List<Manufacturer>)
			// hashMapCache.get("mfr");
			List<Manufacturer> manufacturers = manufacturerSB.findManufacturerByBizUnit(user.getDefaultBizUnit());
			List<Manufacturer> mfrLst = PricerUtils.getMfrInUploadFile(beanList, manufacturers);
			errorMsg = pricerUploadVerifySB.batchVerifyEffectiveDateInDB(PRICER_TYPE.PROGRAM.getName(),
					beans.getSelectedPrograms(), user, mfrLst);
			if (!QuoteUtil.isEmpty(errorMsg)) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMsg, ""));
				return null;
			}
			isSuccess = pricerEnquirySB.updateProgramPricerMaterialDetail(beans.getSelectedPrograms(), user);
		} else if (PRICER_TYPE.CONTRACT.getName().equalsIgnoreCase(pricerType)) {
			String errorMsg = verifyInSelectList(beans.getSelectedContracts());
			if (!QuoteUtil.isEmpty(errorMsg)) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMsg, ""));
				return null;
			}

			List<Manufacturer> manufacturers = manufacturerSB.findManufacturerByBizUnit(user.getDefaultBizUnit());
			List<Manufacturer> mfrLst = PricerUtils.getMfrInUploadFile(beanList, manufacturers);
			errorMsg = pricerUploadVerifySB.verifyEffectiveDateForContract(beans.getSelectedContracts(), user, mfrLst);
			if (!QuoteUtil.isEmpty(errorMsg)) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMsg, ""));
				return null;
			}
			String language = resourceMB.getDefaultLocaleAsString();
			user.setUserLocale(language);
			errorMsg = pricerUploadVerifySB.checkCustomerNumber(beans.getSelectedContracts(), new Locale(language));
			if (!QuoteUtil.isEmpty(errorMsg)) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMsg, ""));
				return null;
			}
			isSuccess = pricerEnquirySB.updateContractPricer(beans.getSelectedContracts(), user);
		} else if (PRICER_TYPE.SALESCOST.getName().equalsIgnoreCase(pricerType)) {
			String errorMsg = verifyInSelectList(beans.getSelectedSalesCosts());
			if (!QuoteUtil.isEmpty(errorMsg)) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMsg, ""));
				return null;
			}

			errorMsg = pricerUploadVerifySB.verifyEffectiveDateForSalesCost(beans.getSelectedSalesCosts(), user);
			if (!QuoteUtil.isEmpty(errorMsg)) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMsg, ""));
				return null;
			}
			String language = resourceMB.getDefaultLocaleAsString();
			user.setUserLocale(language);

			isSuccess = pricerEnquirySB.updateSalesCostPricerMaterialDetail(beans.getSelectedSalesCosts(), user);
		} else if (PRICER_TYPE.SIMPLE.getName().equalsIgnoreCase(pricerType)) {
			String errorMsg = verifyInSelectList(beans.getSelectedSimples());
			if (!QuoteUtil.isEmpty(errorMsg)) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMsg, ""));
				return null;
			}
			String language = resourceMB.getDefaultLocaleAsString();
			user.setUserLocale(language);
			errorMsg = pricerUploadVerifySB.verifyEffectiveDateForSimplePricer(beans.getSelectedSimples(), user);
			if (!QuoteUtil.isEmpty(errorMsg)) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMsg, ""));
				return null;
			}
			isSuccess = pricerEnquirySB.updateSimplePricerMaterialDetail(beans.getSelectedSimples(), user);
		}

		if (!isSuccess) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceMB.getText("wq.error.editPrice"), ""));
			return null;
		}
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, ResourceMB.getText("wq.message.editPriceSuccess"), ""));
		return null;
	}

	// DONE
	public StreamedContent getDownloadAll() {
		List<PricerUploadTemplateBean> beans = pricerEnquirySB.pricerEnquiry(criteria, true);
		if (beans == null || beans.size() == 0) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceMB.getText("wq.message.noDataDownload"), ""));
			return null;
		}

		downloadAll = downloadSrategy.getDownloadFile(bizUnitName, beans, PricerConstant.PRICER_TEMPLATE_NAME);

		if (downloadAll == null) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.message.downloadError") + "!", ""));
			return null;
		}
		return downloadAll;
	}

	// DONE
	public StreamedContent getDownloadSelected() {
		if (PRICER_TYPE.NORMAL.getName().equalsIgnoreCase(pricerType)) {
			if (beans.getSelectedNormals() == null || beans.getSelectedNormals().size() == 0) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						ResourceMB.getText("wq.message.selectOneRecord"), ""));
				return null;
			}
			downloadSelected = downloadSrategy.getDownloadFile(bizUnitName, beans.getSelectedNormals(),
					PricerConstant.PRICER_TEMPLATE_NAME);

		} else if (PRICER_TYPE.PROGRAM.getName().equalsIgnoreCase(pricerType)) {
			if (beans.getSelectedPrograms() == null || beans.getSelectedPrograms().size() == 0) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						ResourceMB.getText("wq.message.selectOneRecord"), ""));
				return null;
			}
			downloadSelected = downloadSrategy.getDownloadFile(bizUnitName, beans.getSelectedPrograms(),
					PricerConstant.PRICER_TEMPLATE_NAME);

		} else if (PRICER_TYPE.CONTRACT.getName().equalsIgnoreCase(pricerType)) {
			if (beans.getSelectedContracts() == null || beans.getSelectedContracts().size() == 0) {

				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						ResourceMB.getText("wq.message.selectOneRecord"), ""));
				return null;
			}
			downloadSelected = downloadSrategy.getDownloadFile(bizUnitName, beans.getSelectedContracts(),
					PricerConstant.PRICER_TEMPLATE_NAME);

		} else if (PRICER_TYPE.SALESCOST.getName().equalsIgnoreCase(pricerType)) {
			if (beans.getSelectedSalesCosts() == null || beans.getSelectedSalesCosts().size() == 0) {

				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						ResourceMB.getText("wq.message.selectOneRecord"), ""));
				return null;
			}
			downloadSelected = downloadSrategy.getDownloadFile(bizUnitName, beans.getSelectedSalesCosts(),
					PricerConstant.PRICER_TEMPLATE_NAME);
		} else if (PRICER_TYPE.SIMPLE.getName().equalsIgnoreCase(pricerType)) {
			if (beans.getSelectedSimples() == null || beans.getSelectedSimples().size() == 0) {

				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						ResourceMB.getText("wq.message.selectOneRecord"), ""));
				return null;
			}
			downloadSelected = downloadSrategy.getDownloadFile(bizUnitName, beans.getSelectedSimples(),
					PricerConstant.PRICER_TEMPLATE_NAME);
		}

		if (downloadSelected == null) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.message.downloadError") + "!", ""));
			return null;
		}
		return downloadSelected;
	}
	
	public void createOfflineReportRep()
	{
		LOG.info("call createOfflineReportRep");
		//int count = pricerEnquirySB.count(criteria, true);
		if(pricerEnquirySB.count(criteria, true)>QuoteSBConstant.EXCEL_MAX_ROWS) {
			FacesContext.getCurrentInstance().addMessage("growl",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, 
							ResourceMB.getText("wq.error.excelmaxnumlimit") ,""));
			return ;
		}
		String message ="";
		OfflineReportParam param = new OfflineReportParam();
		param.setCriteriaBeanValue(criteria);
		param.setReportName(QuoteConstant.PRICER_ENQUIRY);
		param.setEmployeeId(String.valueOf(user.getEmployeeId()));
		param.setRemoteEjbClass(RemoteEjbClassEnum.PRICERENQUIRY_REMOTE_EJB.classSimpleName());
		offlineReprtSB.sendOffLineReportRemote(param);
        message = ResourceMB.getText("wq.message.reqSubmitted");
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, message ,"");
		
		FacesContext.getCurrentInstance().addMessage("growl", msg);
		return;
	}

	// DONE
	public void goDownloadOffline() throws NamingException, JMSException, InterruptedException {
		pricerType = criteria.getPricerType();
		User user = UserInfo.getUser();
		criteria.setBizUnit(user.getDefaultBizUnit());
		if (QuoteUtil.isEmpty(criteria.getMfr())) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceMB.getText("wq.message.mrfField"), ""));
			return;
		}

		/*
		 * boolean isMinLength = validateCriteriaFieldMinLength(criteria, 5);
		 * 
		 * if (isMinLength) { FacesMessage msg = new
		 * FacesMessage(FacesMessage.SEVERITY_ERROR,
		 * "Allowed minimum length of search keyword is 5", "");
		 * FacesContext.getCurrentInstance().addMessage("msgId", msg); return; }
		 */

		int minLenSearchKey4Others = 5;
		int minLenSearchKey4CustCoder = 3;

		boolean isMinLength = validateCriteriaFieldMinLength(criteria, minLenSearchKey4Others,
				minLenSearchKey4CustCoder);

		if (isMinLength) {
			Object[] obj = { minLenSearchKey4CustCoder, minLenSearchKey4Others };
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getParameterizedString("wq.message.minLenghtSearchKey", obj), "");
			FacesContext.getCurrentInstance().addMessage("msgId", msg);
			return;
		}

		FacesContext.getCurrentInstance().addMessage("msgId", new FacesMessage(FacesMessage.SEVERITY_INFO,
				ResourceMB.getText("wq.message.pricerDownloadOffline"), ""));

		pricerEnquiryOffline.downLoadOffline(criteria, user, downloadSrategy);

	}

	public InitialContext getInitialContext() throws NamingException {
		return new InitialContext();
	}

	public void changePricerType() {
		this.pricerType = criteria.getPricerType();
		// beans = null;
		// NEC Pagination changes: initialize the Lazy data model for supporting
		// the pagination lazy loading
		beans = new LazyDataModelForPricerEnquiry();
		recordsExceedMaxAllowed = false;
		this.resetPricerType();
		if (criteria.getPricerType().equalsIgnoreCase(PRICER_TYPE.NORMAL.getName())) {
			setNormalMaterial(true);
		} else if (criteria.getPricerType().equalsIgnoreCase(PRICER_TYPE.PROGRAM.getName())) {
			setProgramMaterial(true);
		} else if (criteria.getPricerType().equalsIgnoreCase(PRICER_TYPE.CONTRACT.getName())) {
			setContractPricer(true);
		} else if (criteria.getPricerType().equalsIgnoreCase(PRICER_TYPE.SALESCOST.getName())) {
			this.setSalesCostPricer(true);
		} else if (criteria.getPricerType().equalsIgnoreCase(PRICER_TYPE.SIMPLE.getName())) {
			this.setSimplePricer(true);
		}
	}

	private void resetPricerType() {
		this.setNormalMaterial(false);
		this.setProgramMaterial(false);
		this.setContractPricer(false);
		this.setSalesCostPricer(false);
		this.setSimplePricer(false);
	}

	public List<String> autoCompleteMfr(String key) {
		User user = UserInfo.getUser();
		List<Manufacturer> mfrs = manufacturerSB.mFindManufacturerByName(key, user.getBizUnits());
		List<String> result = new ArrayList<String>();

		for (Manufacturer mfr : mfrs) {
			result.add(mfr.getName());
		}

		return result;
	}

	public void changePullDownMenu(String bu) {
		
		this.mfrDownListInit(bu);
		this.typeSelectList = new SelectItem[5];
		this.typeSelectList[0] = new SelectItem(PRICER_TYPE.NORMAL.getName(), "Normal");
		this.typeSelectList[1] = new SelectItem(PRICER_TYPE.PROGRAM.getName(), "Program");
		this.typeSelectList[2] = new SelectItem(PRICER_TYPE.CONTRACT.getName(), "Contract");
		this.typeSelectList[3] = new SelectItem(PRICER_TYPE.SALESCOST.getName(), "SalesCost");
		this.typeSelectList[4] = new SelectItem(PRICER_TYPE.SIMPLE.getName(), "Simple");

		this.salesCostFlagList = new SelectItem[3];
		this.salesCostFlagList[0] = new SelectItem(QuoteSBConstant.OPTION_ALL, QuoteSBConstant.OPTION_ALL);
		this.salesCostFlagList[1] = new SelectItem(QuoteSBConstant.OPTION_YES, QuoteSBConstant.OPTION_YES);
		this.salesCostFlagList[2] = new SelectItem(QuoteSBConstant.OPTION_NO, QuoteSBConstant.OPTION_NO);

		/*********/
		this.regionList = QuoteUtil.getUserRegionDownListByOrder(user.getAllBizUnits(), bizUnitSB.getAllBizUnitsByOrder(), true);
		Set<String> allowCurrs = user.getDefaultBizUnit().getAllowCurrencies();
		this.regionCurrencyList = QuoteUtil.createFilterOptions(
				allowCurrs.toArray(new String[allowCurrs.size()]),
				allowCurrs.toArray(new String[allowCurrs.size()]), false, true, false);
	}

	private void mfrDownListInit(String region){
		//Bryan Start
		//List<Manufacturer> manufacturers = MfrCacheManager.getMfrListByBizUnit(region);
		List<Manufacturer> manufacturers = cacheUtil.getMfrListByBizUnit(region);
		//Bryan End
		List<String> mfrCodes = new ArrayList<String>();
		if (manufacturers != null) {
			for (Manufacturer manufacturer : manufacturers)
				mfrCodes.add(manufacturer.getName());
		}
		this.mfrSelectList = QuoteUtil.createFilterOptions(mfrCodes.toArray(new String[mfrCodes.size()]),
				mfrCodes.toArray(new String[mfrCodes.size()]), false, false);
	}
	
	public void regionChanged(){
		this.mfrDownListInit(this.criteria.getRegion());
	}
	
	public SelectItem[] convertCostIndicatorToSelectItem() {
		//Bryan Start
		//List<String> costIndicators = CostIndicatorCacheManager.getCostIndicator();
		List<String> costIndicators = cacheUtil.getCostIndicator();
		//Bryan End
		return QuoteUtil.createFilterOptions(costIndicators.toArray(new String[costIndicators.size()]),
				costIndicators.toArray(new String[costIndicators.size()]), false, false);
	}

	private boolean validateCriteriaFieldMinLength(PricerUploadTemplateBean bean, int minLenSearchKey4Others,
			int minLenSearchKey4CustCode) {
		Boolean isMinLength = (bean.getFullMfrPart().length() > 0
				&& criteria.getFullMfrPart().length() < minLenSearchKey4Others) ||
		/*
		 * (bean.getProductGroup1().length() > 0 &&
		 * bean.getProductGroup1().length() < minSearchLength) ||
		 * (bean.getProductGroup2().length() > 0 &&
		 * bean.getProductGroup2().length() < minSearchLength) ||
		 * (bean.getProductGroup3().length() > 0 &&
		 * bean.getProductGroup3().length() < minSearchLength) ||
		 * (bean.getProductGroup4().length() > 0 &&
		 * bean.getProductGroup4().length() < minSearchLength) ||
		 */
				(bean.getTermsAndConditions().length() > 0
						&& bean.getTermsAndConditions().length() < minLenSearchKey4Others)
				|| (bean.getPriceListRemarks().length() > 0
						&& bean.getPriceListRemarks().length() < minLenSearchKey4Others)
				|| (bean.getPriceListRemarks2().length() > 0
						&& bean.getPriceListRemarks2().length() < minLenSearchKey4Others)
				|| (bean.getPriceListRemarks3().length() > 0
						&& bean.getPriceListRemarks3().length() < minLenSearchKey4Others)
				|| (bean.getPriceListRemarks4().length() > 0
						&& bean.getPriceListRemarks4().length() < minLenSearchKey4Others)
				|| (bean.getSoldToCode().length() > 0 && bean.getSoldToCode().length() < minLenSearchKey4CustCode)
				|| (bean.getCustomerName().length() > 0 && bean.getCustomerName().length() < minLenSearchKey4Others)
				|| (bean.getEndCustomerName().length() > 0
						&& bean.getEndCustomerName().length() < minLenSearchKey4Others)
				|| (bean.getEndCustomerCode().length() > 0
						&& bean.getEndCustomerCode().length() < minLenSearchKey4CustCode)
				|| (bean.getProgram().length() > 0 && bean.getProgram().length() < minLenSearchKey4Others);
		return isMinLength;
	}

	/**
	 * verify field , lengthofFiled ,validity
	 * 
	 * @param beans
	 * @return
	 */
	private String verifyInSelectList(List<PricerUploadTemplateBean> beans) {
		StringBuffer sb = new StringBuffer();
		PricerUploadTemplateBean bean = null;

		for (int i = 0; i < beans.size(); i++) {
			bean = (PricerUploadTemplateBean) beans.get(i);
			sb.append(PricerUploadHelper.validateFieldType(bean));
			sb.append(PricerUploadHelper.validateLengthOfFields(bean));
			sb.append(PricerUploadHelper.validateValidityForEditPricer(bean));
			sb.append(PricerUploadHelper.validateMandatoryFields(bean));
			sb.append(PricerUploadHelper.validateMOQAndMOV(bean));
		}
		return sb.toString();
	}

	public boolean isProgramMaterial() {
		return isProgramMaterial;
	}

	public void setProgramMaterial(boolean isProgramMaterial) {
		this.isProgramMaterial = isProgramMaterial;
	}

	public boolean isContractPricer() {
		return isContractPricer;
	}

	public void setContractPricer(boolean isContractPricer) {
		this.isContractPricer = isContractPricer;
	}

	public boolean isSalesCostPricer() {
		return isSalesCostPricer;
	}

	public void setSalesCostPricer(boolean isSalesCostPricer) {
		this.isSalesCostPricer = isSalesCostPricer;
	}

	public boolean isSimplePricer() {
		return isSimplePricer;
	}

	public void setSimplePricer(boolean isSimplePricer) {
		this.isSimplePricer = isSimplePricer;
	}

	public SelectItem[] getTypeSelectList() {
		return typeSelectList;
	}

	public void setTypeSelectList(SelectItem[] typeSelectList) {
		this.typeSelectList = typeSelectList;
	}

	public PricerUploadTemplateBean getCriteria() {
		return criteria;
	}

	public void setCriteria(PricerUploadTemplateBean criteria) {
		this.criteria = criteria;
	}

	public boolean isNormalMaterial() {
		return isNormalMaterial;
	}

	public void setNormalMaterial(boolean isNormalMaterial) {
		this.isNormalMaterial = isNormalMaterial;
	}

	public SelectItem[] getMfrSelectList() {
		return mfrSelectList;
	}

	public void setMfrSelectList(SelectItem[] mfrSelectList) {
		this.mfrSelectList = mfrSelectList;
	}

	public SelectItem[] getCostIndicatorSelectList() {
		return costIndicatorSelectList;
	}

	public void setCostIndicatorSelectList(SelectItem[] costIndicatorSelectList) {
		this.costIndicatorSelectList = costIndicatorSelectList;
	}

	public SelectItem[] getSalesCostFlagList() {
		return salesCostFlagList;
	}

	public void setSalesCostFlagList(SelectItem[] salesCostFlagList) {
		this.salesCostFlagList = salesCostFlagList;
	}

	public SelectItem[] getRegionList() {
		return regionList;
	}

	public void setRegionList(SelectItem[] regionList) {
		this.regionList = regionList;
	}

	public SelectItem[] getRegionCurrencyList() {
		return regionCurrencyList;
	}

	public void setRegionCurrencyList(SelectItem[] regionCurrencyList) {
		this.regionCurrencyList = regionCurrencyList;
	}

	public boolean isRecordsExceedMaxAllowed() {
		return recordsExceedMaxAllowed;
	}

	public void setRecordsExceedMaxAllowed(boolean recordsExceedMaxAllowed) {
		this.recordsExceedMaxAllowed = recordsExceedMaxAllowed;
	}

	// NEC Pagination changes: it is getter for data table items
	public LazyDataModelForPricerEnquiry getBeans() {
		return beans;
	}

	// NEC Pagination changes: it is setter for data table items
	public void setBeans(LazyDataModelForPricerEnquiry beans) {
		this.beans = beans;
	}

	/*** return true means pass **/
	private boolean VerifyUserSalesCostFlag() {
		if (user.isSalsCostAccessFlag() != null) {
			boolean pass = (user.isSalsCostAccessFlag()
					&& QuoteSBConstant.OPTION_YES.equals(this.criteria.getSalesCostFlag()))
					|| (!user.isSalsCostAccessFlag()
							&& QuoteSBConstant.OPTION_NO.equals(this.criteria.getSalesCostFlag()));
			if (!pass) {
				FacesContext.getCurrentInstance().addMessage("msgId", new FacesMessage(FacesMessage.SEVERITY_ERROR,
						ResourceMB.getText("wq.error.noAccessToDataBySCflag"), ""));
			}
			return pass;
		}
		return true;
	}

	// NEC Pagination Changes: Inner Class created for implementing Pagination
	// with Lazy Loading
	public class LazyDataModelForPricerEnquiry extends SelectableLazyDataModel<PricerUploadTemplateBean> {

		private transient List<PricerUploadTemplateBean> selectedNormals = new ArrayList<>();
		private transient List<PricerUploadTemplateBean> selectedPrograms = new ArrayList<>();
		private transient List<PricerUploadTemplateBean> selectedContracts = new ArrayList<>();
		private transient List<PricerUploadTemplateBean> selectedSalesCosts = new ArrayList<>();
		private transient List<PricerUploadTemplateBean> selectedSimples = new ArrayList<>();

		private PricerUploadTemplateBean selected;

		public PricerUploadTemplateBean getSelected() {
			return selected;
		}

		public void setSelected(PricerUploadTemplateBean selected) {
			this.selected = selected;
		}

		public List<PricerUploadTemplateBean> getSelectedNormals() {
			return new ArrayList<>(getCacheSelectedItems());
		}

		public void setSelectedNormals(List<PricerUploadTemplateBean> selectedNormals) {
			this.selectedNormals = selectedNormals;
		}

		public List<PricerUploadTemplateBean> getSelectedPrograms() {
			return new ArrayList<>(getCacheSelectedItems());
		}

		public void setSelectedPrograms(List<PricerUploadTemplateBean> selectedPrograms) {
			this.selectedPrograms = selectedPrograms;
		}

		public List<PricerUploadTemplateBean> getSelectedContracts() {
			return new ArrayList<>(getCacheSelectedItems());
		}

		public void setSelectedContracts(List<PricerUploadTemplateBean> selectedContracts) {
			this.selectedContracts = selectedContracts;
		}

		public List<PricerUploadTemplateBean> getSelectedSalesCosts() {
			return new ArrayList<>(getCacheSelectedItems());
		}

		public void setSelectedSalesCosts(List<PricerUploadTemplateBean> selectedSalesCosts) {
			this.selectedSalesCosts = selectedSalesCosts;
		}

		public List<PricerUploadTemplateBean> getSelectedSimples() {
			return new ArrayList<>(getCacheSelectedItems());
		}

		public void setSelectedSimples(List<PricerUploadTemplateBean> selectedSimples) {
			this.selectedSimples = selectedSimples;
		}

		// NEC Pagination Changes: sets maximum no. of pages that can be stored
		// in cache at a time
		@Override
		public void startPagination() {
			super.startPagination();
			String cachePageSizeVal = sysConfigSB.getProperyValue(SelectableLazyDataModel.CACHE_PAGE_SIZE);
			try {
				setCachePageSize(Integer.parseInt(cachePageSizeVal));
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Error occured while setting size of cache, Exception message: " + e.getMessage(),
						e);
			}

		}

		// NEC Pagination Changes: finds count of records to be fetched
		@Override
		public int findLazyDataCount(String sortField, String sortOrder, Map<String, Object> filters) {
			return pricerEnquirySB.count(criteria, true);
		}

		// NEC Pagination Changes: finds data for current page when not present
		// in cache
		@Override
		public List<PricerUploadTemplateBean> findLazyData(int first, int pageSize, String sortField, String sortOrder,
				Map<String, Object> filters) {
			List<PricerUploadTemplateBean> lazyItems = pricerEnquirySB.pricerEnquiry(criteria, true, first, pageSize);
			// NEC Pagination Changes: Sets sequence number of records fetched
			// for current page
			for (int i = 1; i <= lazyItems.size(); i++) {
				if (null == lazyItems.get(i - 1) || lazyItems.get(i - 1).equals(""))
					break;
				else
					lazyItems.get(i - 1).setLineSeq(first + i);
			}
			return lazyItems;
		}

		// NEC Pagination Changes: finds data for previous page through
		// asynchronous call to database
		@Override
		public void findLazyPreviousPageData(int first, int pageSize, int pageNumber, String sortField, String sort,
				Map<String, Object> filters, ConcurrentHashMap<Integer, List<PricerUploadTemplateBean>> map,
				Queue<Integer> queue, int cachePageSize) {
			pricerEnquirySB.asynchSearch(criteria, true, first, pageSize, pageNumber, map, queue, cachePageSize);
		}

		// NEC Pagination Changes: finds data for next page through asynchronous
		// call to database
		@Override
		public void findLazyNextPageData(int first, int pageSize, int pageNumber, String sortField, String sort,
				Map<String, Object> filters, ConcurrentHashMap<Integer, List<PricerUploadTemplateBean>> map,
				Queue<Integer> queue, int cachePageSize) {
			pricerEnquirySB.asynchSearch(criteria, true, first, pageSize, pageNumber, map, queue, cachePageSize);
		}

		@Override
		// NEC Pagination changes: it is callback method
		public void cellChangeListener(String id) {
			beans.setCacheModifyData(id);
			String tableId = "pcrEnquiryForm:pricr_datatable";
			if (isNormalMaterial()) {
				tableId = "pcrEnquiryForm:pricr_datatable";
			} else if (isProgramMaterial()) {
				tableId = "pcrEnquiryForm:datatable_prg";
			} else if (isContractPricer()) {
				tableId = "pcrEnquiryForm:ctrp_table";
			} else if (isSalesCostPricer()) {
				tableId = "pcrEnquiryForm:scost_table";
			} else if (isSimplePricer()) {
				tableId = "pcrEnquiryForm:simple_table";
			}
			FacesUtil.updateUI(tableId);

		}

		@Override
		// NEC Pagination changes: return data for pagination
		protected SelectableLazyDataModel<PricerUploadTemplateBean> getLazyData() {
			return beans;
		}

		@Override
		// NEC Pagination changes: it is called on toggle of check box
		public void onToggleSelect(ToggleSelectEvent event) {

			if (isNormalMaterial()) {
				selectionItems = selectedNormals;
			} else if (isProgramMaterial()) {
				selectionItems = selectedPrograms;
			} else if (isContractPricer()) {
				selectionItems = selectedContracts;
			} else if (PricerEnquiryMB.this.isSalesCostPricer()) {
				selectionItems = this.selectedSalesCosts;
			} else if (PricerEnquiryMB.this.isSimplePricer()) {
				selectionItems = this.selectedSimples;
			}

			if (event.isSelected()) {
				this.getCacheSelectedItems().addAll(selectionItems);
			} else {
				this.getCacheSelectedItems().removeAll(getCurrentPageItems());
			}
		}

		@Override
		// NEC Pagination changes: it is called on on row select
		public void onRowSelect(SelectEvent event) {
			this.getCacheSelectedItems().removeAll(getCurrentPageItems());

			if (isNormalMaterial()) {
				selectionItems = selectedNormals;
			} else if (isProgramMaterial()) {
				selectionItems = selectedPrograms;
			} else if (isContractPricer()) {
				selectionItems = selectedContracts;
			} else if (PricerEnquiryMB.this.isSalesCostPricer()) {
				selectionItems = selectedSalesCosts;
			} else if (PricerEnquiryMB.this.isSimplePricer()) {
				selectionItems = selectedSimples;
			}

			this.getCacheSelectedItems().addAll(selectionItems);

		}

		@Override
		// NEC Pagination changes: it is called on on row select check box
		public void onRowSelectCheckbox(SelectEvent event) {
			// super.onRowSelectCheckbox(event);
			PricerUploadTemplateBean obj = (PricerUploadTemplateBean) event.getObject();
			this.getCacheSelectedItems().add(obj);
			onRowSelect(event);
		}

		@Override
		// NEC Pagination changes: it is called on on row unselect check box
		public void onRowUnselectCheckbox(UnselectEvent event) {
			PricerUploadTemplateBean obj = (PricerUploadTemplateBean) event.getObject();
			this.getCacheSelectedItems().remove(obj);

			if (isNormalMaterial()) {
				selectedNormals.remove(event.getObject());
			} else if (isProgramMaterial()) {
				selectedPrograms.remove(event.getObject());
			} else if (isContractPricer()) {
				selectedContracts.remove(event.getObject());
			} else if (isSalesCostPricer()) {
				this.selectedSalesCosts.remove(event.getObject());
			} else if (isSimplePricer()) {
				this.selectedSimples.remove(event.getObject());
			}
		}

		// NEC Pagination Changes: Retrieves data of the current row in data
		// table
		@Override
		public PricerUploadTemplateBean getRowData(String rowKey) {
			List<PricerUploadTemplateBean> list = (List<PricerUploadTemplateBean>) getWrappedData();
			if (!list.isEmpty()) {
				for (PricerUploadTemplateBean bean : list) {
					String key = (String.valueOf((bean).getLineSeq()));
					if (rowKey.equals(key))
						return bean;

				}
			}

			return null;
		}

	}
}
