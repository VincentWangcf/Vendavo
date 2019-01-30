package com.avnet.emasia.webquote.web.mbt;

import com.avnet.emasia.webquote.constants.StageEnum;
import com.avnet.emasia.webquote.constants.StatusEnum;
import com.avnet.emasia.webquote.entity.QuoteItemDesign;
import com.avnet.emasia.webquote.quote.ejb.BmtQuoteSB;
import com.avnet.emasia.webquote.utilities.common.FilterMatchMode;
import com.avnet.emasia.webquote.utilities.common.QueryBean;
import com.avnet.emasia.webquote.utilities.util.QuoteUtil;
import com.avnet.emasia.webquote.web.datatable.BaseLazyDataMB;
import com.avnet.emasia.webquote.web.datatable.LazyEntityDataModel;
import com.avnet.emasia.webquote.web.user.UserInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.event.data.FilterEvent;

/**
 * 
 * @author 041863
 */
@ManagedBean
@SessionScoped
public class BmtQuotationHistoryMB extends BaseLazyDataMB<QuoteItemDesign> implements Serializable {

	private static final long serialVersionUID = 1713537803208637716L;
	@EJB
	private BmtQuoteSB bmtQuoteSB;
	private String mfr;
	private String pn;
	private String soldToName;
	private String ecName;
	private Date quoteReleaseDateFrom;
	private Date quoteReleaseDateTo;
	private List<String> stage;
	private List<String> status;
	private LazyEntityDataModel<QuoteItemDesign> quotationHistorys;

	@PostConstruct
	public void init() {
		quoteReleaseDateFrom = getDate(Calendar.MONTH, -3);
		quoteReleaseDateTo = new Date();
		setMfr("");
		setPn("");
		setSoldToName("");
		setEcName("");
		setStage(new ArrayList<>(Arrays.asList(StageEnum.FINISH.name(),StageEnum.PENDING.name())));
		setStatus(new ArrayList<>(Arrays.asList(StatusEnum.IT.name(), StatusEnum.QC.name(), StatusEnum.RIT.name(), StatusEnum.SQ.name(), StatusEnum.BQ.name(), StatusEnum.RBQ.name(),StatusEnum.BIT.name(),StatusEnum.RBIT.name())));
	}
	
	private Date getDate(int field, int amount) {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(field, amount);

		return cal.getTime();
	}

	public void searchHistory() {
		List<QueryBean> queryBeans = new ArrayList<QueryBean>();
		queryBeans.add(new QueryBean(FilterMatchMode.IN, "o.quoteItem.quote.bizUnit", UserInfo.getUser().getBizUnits()));
		if (!QuoteUtil.isEmpty(mfr)) {
			queryBeans.add(new QueryBean(FilterMatchMode.CONTAINS,"o.quoteItem.requestedMfr.name", mfr.toUpperCase()));
		}
		if (!QuoteUtil.isEmpty(pn)) {
			queryBeans.add(new QueryBean(FilterMatchMode.CONTAINS,"o.quoteItem.requestedPartNumber", pn.toUpperCase()));
		}
		if (!QuoteUtil.isEmpty(soldToName)) {
			queryBeans.add(new QueryBean(FilterMatchMode.CONTAINS,"concat(o.quoteItem.soldToCustomer.customerName1,' ',o.quoteItem.soldToCustomer.customerName2,' ',o.quoteItem.soldToCustomer.customerName3,' ',o.quoteItem.soldToCustomer.customerName4)",soldToName.toUpperCase(),null,"soldToCustomerNameParam"));
		}
		if (!QuoteUtil.isEmpty(ecName)) {
			queryBeans.add(new QueryBean(FilterMatchMode.CONTAINS,"o.quoteItem.endCustomerName", ecName.toUpperCase()));
		}
		if (!getStage().isEmpty()) {
			queryBeans.add(new QueryBean(FilterMatchMode.IN, "o.quoteItem.stage",stage));
		}
		if (!getStatus().isEmpty()) {
			queryBeans.add(new QueryBean(FilterMatchMode.IN,"o.quoteItem.status", status));
		}
		if (quoteReleaseDateFrom != null) {
			queryBeans.add(new QueryBean(FilterMatchMode.GT_EQ, "o.quoteItem.sentOutTime",quoteReleaseDateFrom));
		}
		if (quoteReleaseDateTo != null) {
			queryBeans.add(new QueryBean(FilterMatchMode.LT_EQ,"o.quoteItem.sentOutTime",quoteReleaseDateTo));
		}
		// TODO: other condition when pass test
		quotationHistorys = new LazyEntityDataModel<QuoteItemDesign>(bmtQuoteSB.getDataAccess(UserInfo.getUser()), bmtQuoteSB, queryBeans, "topForm:history_datatable");
	}

	public void clearSearchHistory() {
		init();
//		quotationHistorys = new LazyEntityDataModel<QuoteItemDesign>(QuoteItemDesign.class);
		searchHistory();
	}

	public String getMfr() {
		return mfr;
	}

	public void setMfr(String mfr) {
		this.mfr = mfr;
	}

	public String getPn() {
		return pn;
	}

	public void setPn(String pn) {
		this.pn = pn;
	}

	public String getSoldToName() {
		return soldToName;
	}

	public void setSoldToName(String soldToName) {
		this.soldToName = soldToName;
	}

	public String getEcName() {
		return ecName;
	}

	public void setEcName(String eCName) {
		this.ecName = eCName;
	}

	public Date getQuoteReleaseDateFrom() {
		return quoteReleaseDateFrom;
	}

	public void setQuoteReleaseDateFrom(Date quoteReleaseDateFrom) {
		this.quoteReleaseDateFrom = quoteReleaseDateFrom;
	}

	public Date getQuoteReleaseDateTo() {
		return quoteReleaseDateTo;
	}

	public void setQuoteReleaseDateTo(Date quoteReleaseDateTo) {
		this.quoteReleaseDateTo = quoteReleaseDateTo;
	}

	public List<String> getStage() {
		return stage;
	}

	public void setStage(List<String> stage) {
		this.stage = stage;
	}

	public List<String> getStatus() {
		return status;
	}

	public void setStatus(List<String> status) {
		this.status = status;
	}

	public LazyEntityDataModel<QuoteItemDesign> getQuotationHistorys() {
		return quotationHistorys;
	}

	public void setQuotationHistorys(LazyEntityDataModel<QuoteItemDesign> quotationHistorys) {
		this.quotationHistorys = quotationHistorys;
	}

	@Override
	protected LazyEntityDataModel<QuoteItemDesign> getLazyData() {
		return quotationHistorys;
	}

	@Override
	public void cellChangeListener(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFilter(FilterEvent event) {
		// TODO Auto-generated method stub
		
	}

}
