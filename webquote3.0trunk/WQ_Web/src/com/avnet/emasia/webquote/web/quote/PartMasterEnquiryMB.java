package com.avnet.emasia.webquote.web.quote;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.avnet.emasia.webquote.constants.RemoteEjbClassEnum;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.OfflineReport;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.ejb.MaterialSB;
import com.avnet.emasia.webquote.quote.ejb.QuoteSB;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.quote.vo.PartMasterCriteria;
import com.avnet.emasia.webquote.reports.ejb.OfflineReportSB;
import com.avnet.emasia.webquote.reports.util.ReportConvertor;
import com.avnet.emasia.webquote.user.ejb.BizUnitSB;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.common.CacheUtil;
import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil;
import com.avnet.emasia.webquote.utilities.DateUtils;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.vo.OfflineReportParam;
import com.avnet.emasia.webquote.vo.PartMasterResultBean;
import com.avnet.emasia.webquote.web.quote.cache.MfrCacheManager;
import com.avnet.emasia.webquote.web.quote.constant.QuoteConstant;
import com.avnet.emasia.webquote.web.user.UserInfo;


@ManagedBean
@SessionScoped
public class PartMasterEnquiryMB implements Serializable {
	
	private static final long serialVersionUID = 5566569584120997203L;
	private static final Logger LOG =Logger.getLogger(PartMasterEnquiryMB.class.getName());
	private static final int PAGE_SIZE=30000;
	
	@EJB
	QuoteSB quoteSB;
	@EJB
	MaterialSB materialSB;
	@EJB
	OfflineReportSB offlineReprtSB;
	@EJB
	private BizUnitSB bizUnitSB;
	
	@EJB
	private CacheUtil cacheUtil;
	
	private PartMasterCriteria criteria;
	private transient List<PartMasterResultBean> beans;
	private transient List<PartMasterResultBean> selectedBeans= null;
	
	private SelectItem[] mfrSelectList;
	private SelectItem[] salesCostFlagList;
	private SelectItem[] regionList;
	private User user = null;
	private String bizUnitName = null;
	private boolean recordsExceedMaxAllowed = false;
	String message= null ;
	
	private transient Method lastSearchMethod;
	
	public boolean isRecordsExceedMaxAllowed() {
		return recordsExceedMaxAllowed;
	}

	public void setRecordsExceedMaxAllowed(boolean recordsExceedMaxAllowed) {
		this.recordsExceedMaxAllowed = recordsExceedMaxAllowed;
	}

	@PostConstruct
	public void postContruct() {
		user = UserInfo.getUser();
		bizUnitName = user.getDefaultBizUnit().getName();
		this.changePullDownMenu(bizUnitName);
		criteria = new PartMasterCriteria();
		criteria.setRegion(bizUnitName);
		
	}
	
	@SuppressWarnings("unchecked")
	public void parterMasterSearch(){
		LOG.info("parterMasterSearch begin...");
		long start = System.currentTimeMillis();
		try {
			if (QuoteUtil.isEmpty(criteria.getMfr())) {
				message = ResourceMB.getText("wq.message.mrfField");
				FacesContext.getCurrentInstance().addMessage(
						null,new FacesMessage(FacesMessage.SEVERITY_ERROR, message,"" ));
				return;
			}
		
			if(null!=beans)
				beans.clear();
			List<Material> list = materialSB.partMasterEnquiry(criteria,true);
			beans = ReportConvertor.convertPartMasterForQC(list, criteria.getRegion());
			int count = beans.size();

			if (count > 500) {
				recordsExceedMaxAllowed = true;
				for (int i = beans.size() - 1; i >= 500; i--) {
					beans.remove(i);
				}
			} else {
				recordsExceedMaxAllowed = false;
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Enquiry upload pricer failed! for searching part Number : "+criteria.getPartNumber()+" , in MFR"+criteria.getMfr()+" , Business Unit : "+bizUnitName +" , Reason :"+ e.getMessage(), e);
		}
		
		try {
			lastSearchMethod = this.getClass().getDeclaredMethod("parterMasterSearch",null);
		} catch (Exception e) {
			LOG.log(Level.SEVERE,
					"Error occured when get search Method with reflection in PartMasterEnquiryMB for searching part Number : "+criteria.getPartNumber()+" , in MFR"+criteria.getMfr()+" , Business Unit : "+bizUnitName +" , Reason :"+e.getMessage(),
					e);
		}
		long end = System.currentTimeMillis();
		LOG.info("parterMasterSearch end,takes " + (end - start) + " ms");	
	}
	
	public void changePullDownMenu(String bu) {
		beans = null;
		this.mfrDownListInit(bu);
		this.salesCostFlagList = new SelectItem[3];
		this.salesCostFlagList[0] = new SelectItem(QuoteSBConstant.OPTION_ALL, QuoteSBConstant.OPTION_ALL);
		this.salesCostFlagList[1] = new SelectItem(QuoteSBConstant.OPTION_YES, QuoteSBConstant.OPTION_YES);
		this.salesCostFlagList[2] = new SelectItem(QuoteSBConstant.OPTION_NO, QuoteSBConstant.OPTION_NO);

		/*********/
		this.regionList = QuoteUtil.getUserRegionDownListByOrder(user.getAllBizUnits(), bizUnitSB.getAllBizUnitsByOrder());
	}
	
	private void mfrDownListInit(String region) {
		//Bryan Start
		//List<Manufacturer> manufacturers = MfrCacheManager.getMfrListByBizUnit(region);
		List<Manufacturer> manufacturers = cacheUtil.getMfrListByBizUnit(region);
		//Bryan End
		List<String> mfrCodes = new ArrayList<String>();
		if (manufacturers != null) {
			for (Manufacturer manufacturer : manufacturers)
				mfrCodes.add(manufacturer.getName());
		}
		this.mfrSelectList = QuoteUtil.createFilterOptions(mfrCodes.toArray(new String[mfrCodes.size()]), mfrCodes.toArray(new String[mfrCodes.size()]), false, false);
	}

	public List<PartMasterResultBean> getBeans() {
		return beans;
	}

	public void setBeans(List<PartMasterResultBean> beans) {
		this.beans = beans;
	}

	public SelectItem[] getMfrSelectList() {
		return mfrSelectList;
	}

	public void setMfrSelectList(SelectItem[] mfrSelectList) {
		this.mfrSelectList = mfrSelectList;
	}

	public List<PartMasterResultBean> getSelectedBeans() {
		return selectedBeans;
	}

	public void setSelectedBeans(List<PartMasterResultBean> selectedBeans) {
		this.selectedBeans = selectedBeans;
	}


	
	public PartMasterCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(PartMasterCriteria criteria) {
		this.criteria = criteria;
	}

	public void createOfflineReportRep()
	{
		LOG.info("call createOfflineReportRep");
		if(materialSB.partMasterEnquiryCount(criteria)>QuoteSBConstant.EXCEL_MAX_ROWS) {
			FacesContext.getCurrentInstance().addMessage("growl",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, 
							ResourceMB.getText("wq.error.excelmaxnumlimit") ,""));
			return ;
		}
		if (QuoteUtil.isEmpty(criteria.getMfr())) {
			message = ResourceMB.getText("wq.message.mrfField");
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,message, ""));
			return;
		}
		OfflineReportParam param = new OfflineReportParam();
		param.setCriteriaBeanValue(criteria);
		param.setReportName(QuoteConstant.PART_MASTER);
		param.setEmployeeId(String.valueOf(user.getEmployeeId()));
		param.setRemoteEjbClass(RemoteEjbClassEnum.PARTMASTER_REMOTE_EJB.classSimpleName());
		offlineReprtSB.sendOffLineReportRemote(param);
		
		/*int totalRecords = materialSB.partMasterEnquiryCount(criteria);
		LOG.info("search result count:" + totalRecords);
		List<OfflineReport> reportBeanList = new ArrayList<OfflineReport>();
		Date curDate = DateUtils.getCurrentAsiaDateObj();
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat(
				"yyyy/MM/ddHH:mm:ss");
		String batchKey = dateTimeFormat.format(curDate);
		int count = totalRecords / PAGE_SIZE;
		LOG.info("count:" + count);
		for (int i = 0; i <= count; i++) 
		{
			criteria.setStart(i * PAGE_SIZE);
			if (PAGE_SIZE > (totalRecords - (i * PAGE_SIZE))) {
				criteria.setEnd(totalRecords - (i * PAGE_SIZE));
			} else {
				criteria.setEnd(PAGE_SIZE);
			}
			OfflineReport reportBean = new OfflineReport();
			reportBean.setServiceBeanId(batchKey);
			reportBean.setServiceBeanClass("com.avnet.emasia.webquote.quote.job.PartMasterExcelFileGenerator");
			reportBean.setServiceBeanMethod("QC");
			reportBean.setSearchBeanClass(PartMasterCriteria.class.getName());
			reportBean.setSearchBeanValue(transfer(criteria));
			reportBean.setCreatedOn(curDate);
			reportBean.setLastUpdatedOn(curDate);
			reportBean.setEmployeeId(String.valueOf(user.getEmployeeId()));
			reportBean.setEmployeeName(user.getName());
			reportBean.setSendFlag(false);
			reportBean.setBizUnit(bizUnitName);
			reportBeanList.add(reportBean);
		}
		// LOG.info("searchOffline end" );
		long returnRequestId = offlineReprtSB.createOfflineReportRequest(reportBeanList);
		LOG.info("searchOffline returnRequestId:" + returnRequestId	);
		sendQueue(returnRequestId);*/
        message = ResourceMB.getText("wq.message.reqSubmitted");
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, message ,"");
		
		FacesContext.getCurrentInstance().addMessage("growl", msg);
		return;
	}
	
	public void regionChanged(){
		this.mfrDownListInit(this.criteria.getRegion());
	}
	
	
	public void sendQueue(long requestId)
	{
		LOG.info("sendQueue start requestId:" + requestId	);
		InitialContext ini;
		try
		{
			ini = new InitialContext();
			final ConnectionFactory factory = (QueueConnectionFactory) ini.lookup("/ConnectionFactory");
			final Destination destination = (Destination) ini.lookup("/queue/offlineRptQueue");
	        final Connection connection = factory.createConnection();
	        final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	        MapMessage message = session.createMapMessage();
	        message.setLong("requestId", requestId);
	        final MessageProducer sender = session.createProducer(destination);
	        sender.send(message);
	        Thread.sleep(1000);
	        session.close();
	        connection.close();
	        LOG.info("sendQueue end");
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "Error is send offline for Request ID : "+requestId+"part Number : "+criteria.getPartNumber()+" , in MFR"+criteria.getMfr()+" , Business Unit : "+bizUnitName +" , Reason :"+MessageFormatorUtil.getParameterizedStringFromException(e), e);
		}
	}
	public byte[] transfer(Object obj) {
		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream oo = new ObjectOutputStream(bo);
			oo.writeObject(obj);
			return bo.toByteArray();
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Exeception in trasfer object for part Number : "+criteria.getPartNumber()+" , in MFR"+criteria.getMfr()+" , Business Unit : "+bizUnitName +" , Reason :"+MessageFormatorUtil.getParameterizedStringFromException(e), e);
		}
		return new byte[0];
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
}
                    
