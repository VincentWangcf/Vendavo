package com.avnet.emasia.webquote.web.quote;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;

import com.avnet.emasia.webquote.dp.EJBCommonSB;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.vo.ReportSearchCriteria;
import com.avnet.emasia.webquote.reports.ejb.ReportSB;
import com.avnet.emasia.webquote.reports.vo.ColumnModelVo;
import com.avnet.emasia.webquote.reports.vo.QuoteHitRateReportVo;
import com.avnet.emasia.webquote.reports.vo.ReportGroupVo;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil;
import com.avnet.emasia.webquote.web.user.UserInfo;


@ManagedBean
@SessionScoped
public class QuoteHitRateReportMB extends ReportMB implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7699135944386703126L;
	private static final Logger LOG = Logger
			.getLogger(QuoteHitRateReportMB.class.getName());

	@EJB
	ReportSB reportSB;
	private ReportSearchCriteria criteria;
	private List<QuoteHitRateReportVo> quoteHitRateReportVos;
	private List<QuoteHitRateReportVo> filteredQuoteHitRateReportVos;
	private QuoteHitRateReportVo grandTotal;
	private DualListModel<ReportGroupVo> groupCols;
	public List<ColumnModelVo> columns = new ArrayList<ColumnModelVo>();
	public int grandColspanSize;
	public String defaultBuName;
	
	@EJB
	protected EJBCommonSB ejbCommonSB;
	

	public QuoteHitRateReportMB() {
		initialMB();
		
	}

	public void initialMB() {
		// set the picking list to set the default Group By List 
		List<ReportGroupVo> source = getGroupsObj();
		List<ReportGroupVo>  target= new ArrayList<ReportGroupVo>();
		groupCols = new DualListModel<ReportGroupVo>(source, target);
		groupCols.getTarget().add(source.get(0));
		groupCols.getSource().remove(0);
		grandColspanSize = target.size();
	}

	@PostConstruct
	public void postContruct() {

		LOG.info("Quote Hit Rate Report.....");
	//	ejbCommonSBMB.constructCriteria(criteria, this.defaultBuName, bizUnitSelectList, quotePeriodSelectList, groupCols, "quoteHitRateReportMB");

		criteria = new ReportSearchCriteria();
		criteria.setSearchQuoteType(QUOTE_TYPE_YEAR); // search default search
		if ("orderOnHandReportMB".equals("quoteHitRateReportMB")) {
			// set the default period range is 12 month from today to before
			criteria.setQuoteFrm(org.apache.commons.lang.time.DateUtils.addMonths(new Date(), -3));
			criteria.setQuoteTo(new Date());
		}
		User user = UserInfo.getUser();
		defaultBuName = null != user.getDefaultBizUnit() ? user.getDefaultBizUnit().getName() : null;
		criteria.setBizUnit(defaultBuName);// set a default value
		// get bizUnit Information for this logic user
		List<BizUnit> bizUnitLst = user.getBizUnits();
		List<String> bus = new ArrayList<String>();
		if (bizUnitLst != null) {
			for (BizUnit bu : bizUnitLst)
				bus.add(bu.getName());
		}
		bizUnitSelectList = QuoteUtil.createFilterOptions(bus.toArray(new String[bus.size()]), true, false, true);
		// set quoted period type
		List<String> periods = new ArrayList<String>();
		periods.add(QUOTE_TYPE_YEAR);
		periods.add(QUOTE_TYPE_QTR);
		periods.add(QUOTE_TYPE_MONTH);
		quotePeriodSelectList = QuoteUtil.createFilterOptions(periods.toArray(new String[periods.size()]), true, false,
				true);
	
		//set pull down menu
		this.changePullDownMenu(defaultBuName);
		//set default show value
		this.setSearchGroupShowValue(groupCols.getTarget(), criteria);
		// set the  column display sequence
		this.setColumnsDisplaySequence();
		

	}

	public void search() {
		long fromDate = System.currentTimeMillis();
		setReportPeriod(criteria,12);
		List<ReportGroupVo> lst = groupCols.getTarget();
		boolean isValid = validationSearchCriteria(12,criteria,lst);

		if (isValid) {
			setSearchGroupShowValue(lst,criteria);
			
			this.setSearchCriteria();

			UIComponent table = FacesContext.getCurrentInstance().getViewRoot()
					.findComponent(":quoteHitRateForm:quotehitRateReport_dataTable");
			table.setValueExpression("sortBy", null);

			
			quoteHitRateReportVos = reportSB.findQuoteHitRateItem(criteria);
			grandTotal =  reportSB.findQuoteHitRateGrandTotal(criteria);
			
			this.setColumnsDisplaySequence();
			
			LOG.log(Level.INFO, "Search .....");
			LOG.log(Level.INFO, " quoteHitRateReportVos size is ==>> "
					+ quoteHitRateReportVos.size());
			long toDate = System.currentTimeMillis();
			
			long diff = toDate - fromDate ;

			long days = diff / (1000 * 60 * 60 * 24);
			long hour=diff/(1000*60*60);

			long m=(diff-hour*1000*60*60)/(60*1000);

			long s=(diff-hour*1000*60*60-m*60*1000)/1000;
			
			LOG.log(Level.INFO, "fromDate ==>> "+ fromDate);
			LOG.log(Level.INFO, "toDate ==>> "+ toDate);
			LOG.log(Level.INFO, "days difference ==>> "+ days);
			LOG.log(Level.INFO, "detail difference ==>>  hour is==>> "+ hour +" hour " + m+" minutes "+ s +" seconds ");
			    
		}

	}

	
	private void setColumnsDisplaySequence() {
		
		columns = new ArrayList<ColumnModelVo>();
		List<ReportGroupVo> lst = groupCols.getTarget();
		
		//set the display sequence
		createDynamicColumns(lst);
	}

	public void changeBizUnit(AjaxBehaviorEvent event) {

		String newBU = criteria.getBizUnit();
		LOG.log(Level.INFO, " new BU  ==>> " + newBU);
		this.changePullDownMenu(newBU);

	}
	public void changeTeam(AjaxBehaviorEvent event) {

		String bu = criteria.getBizUnit();
		
		String newTeam = criteria.getSalesTeam();
		LOG.log(Level.INFO, " new team  ==>> " + newTeam);
		this.changeSalesByTeamAndBu(bu,newTeam);
		

	}

	
	private void createDynamicColumns(List<ReportGroupVo> lst ) {
		ejbCommonSB.createDynamicColumns(lst, columns, grandColspanSize);
		if ("0".equals(criteria.getSearchResultType())) {
			columns.add(new ColumnModelVo("Quoted Item","quotedCnt"));
			columns.add(new ColumnModelVo("Hit Item","hitOrderCnt"));
			columns.add(new ColumnModelVo("Hit Order Rate","hitOrderRate"));
			columns.add(new ColumnModelVo("Met TP Item","metTpItemCnt"));
			columns.add(new ColumnModelVo("Met TP Hit Item","metTpHitItemCnt"));
		} else {
			columns.add(new ColumnModelVo("Quoted Amount","quotedAmt"));
			columns.add(new ColumnModelVo("Hit Amount","hitOrderAmt"));
			columns.add(new ColumnModelVo("Hit Amount Rate","hitOrderRate","%",1));
			columns.add(new ColumnModelVo("Met TP Hit Amount","metTpHitItemAmt"));
		}
	}

	public void sendReport() {
		// update the result from DB;
		long fromDate = System.currentTimeMillis();
		setReportPeriod(criteria,12);
		List<ReportGroupVo> lst = groupCols.getTarget();
		boolean isValid = validationSearchCriteria(12,criteria,lst);

		if (isValid) {
			
			setSearchGroupShowValue(lst,criteria);
			
			this.setSearchCriteria();
			quoteHitRateReportVos = reportSB.findQuoteHitRateItem(criteria);
			grandTotal =  reportSB.findQuoteHitRateGrandTotal(criteria);
			
			this.setColumnsDisplaySequence();
			
			if(quoteHitRateReportVos==null || quoteHitRateReportVos.size()==0 ) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, ResourceMB.getText("wq.label.norfqsearch")+".",
								""));
			} else {
				String attachedName = "QuoteHitRateReport";
				String reportTitle ="Quote Hit Rate Report";
				this.generateXlSAndSend(attachedName,columns,grandColspanSize,quoteHitRateReportVos,grandTotal,reportTitle,criteria.getBizUnit());
			}
			LOG.log(Level.INFO, "Search and Send.....");
			LOG.log(Level.INFO, " quoteHitRateReportVos size is ==>> "+ quoteHitRateReportVos.size());
			long toDate = System.currentTimeMillis();
			long diff = toDate - fromDate ;
			long hour=diff/(1000*60*60);
			long m=(diff-hour*1000*60*60)/(60*1000);
			long s=(diff-hour*1000*60*60-m*60*1000)/1000;		
			LOG.log(Level.INFO, "fromDate ==>> "+ fromDate);
			LOG.log(Level.INFO, "toDate ==>> "+ toDate);
			LOG.log(Level.INFO, "detail difference ==>>  hour is==>> "+ hour +" hour " + m+" minutes "+ s +" seconds ");

		} // validation

	}
	
	



	

	private void setSearchCriteria() {
		ejbCommonSB.setSearchCriteria(criteria);
	}

	public void reset() {
		criteria = ejbCommonSB.resetReport(criteria, defaultBuName, "quoteHitRateReportMB");
		// Group By List
		initialMB();
		this.setColumnsDisplaySequence();
	}

	public void onTransfer(TransferEvent event) {
		ejbCommonSB.onTransfer(event, "quoteHitRateReportMB");
	}
	
	public ReportSB getReportSB() {
		return reportSB;
	}

	public void setReportSB(ReportSB reportSB) {
		this.reportSB = reportSB;
	}

	public ReportSearchCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(ReportSearchCriteria criteria) {
		this.criteria = criteria;
	}

	public List<QuoteHitRateReportVo> getQuoteHitRateReportVos() {
		return quoteHitRateReportVos;
	}

	public void setQuoteHitRateReportVos(
			List<QuoteHitRateReportVo> quoteHitRateReportVos) {
		this.quoteHitRateReportVos = quoteHitRateReportVos;
	}

	public List<QuoteHitRateReportVo> getFilteredQuoteHitRateReportVos() {
		return filteredQuoteHitRateReportVos;
	}

	public void setFilteredQuoteHitRateReportVos(
			List<QuoteHitRateReportVo> filteredQuoteHitRateReportVos) {
		this.filteredQuoteHitRateReportVos = filteredQuoteHitRateReportVos;
	}

	public QuoteHitRateReportVo getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(QuoteHitRateReportVo grandTotal) {
		this.grandTotal = grandTotal;
	}

	public DualListModel<ReportGroupVo> getGroupCols() {
		return groupCols;
	}

	public void setGroupCols(DualListModel<ReportGroupVo> groupCols) {
		this.groupCols = groupCols;
	}

	public List<ColumnModelVo> getColumns() {
		return columns;
	}

	public void setColumns(List<ColumnModelVo> columns) {
		this.columns = columns;
	}

	public int getGrandColspanSize() {
		return grandColspanSize;
	}

	public void setGrandColspanSize(int grandColspanSize) {
		this.grandColspanSize = grandColspanSize;
	}

	

	

	
}
