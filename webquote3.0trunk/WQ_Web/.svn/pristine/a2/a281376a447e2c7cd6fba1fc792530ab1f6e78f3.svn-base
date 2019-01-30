package com.avnet.emasia.webquote.utilites.web.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.avnet.emasia.webquote.constants.StageEnum;
import com.avnet.emasia.webquote.constants.StatusEnum;
import com.avnet.emasia.webquote.quote.ejb.SystemCodeMaintenanceSB;
import com.avnet.emasia.webquote.web.quote.FacesUtil;

@ManagedBean
@ApplicationScoped
public class SelectItemMB implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(SelectItemMB.class.getName());
	private List<SelectItem> booleanItem;
	private List<SelectItem> rfqStatusItem;
	private List<SelectItem> rfqStatusItemHasSelect;
	private List<SelectItem> quoteStageBMTHisItemHasSelect;
	private List<SelectItem> quoteStageBMTHisItem;
	private List<SelectItem> bmtFlagItemsForFilter = new ArrayList<SelectItem>();
	private List<SelectItem> bmtFlagItems = new ArrayList<SelectItem>();
	
	@EJB
	private SystemCodeMaintenanceSB codeMaintenanceSB;
	
	//Bryan Start
	@EJB
	private CacheUtil cacheUtil;
	//Bryan End
	
	public SelectItemMB() {
	}

	@PostConstruct
	public void postContruct() {
		init();
	} 
	
	public void init() {
		List<String> statusItem = Arrays.asList(
						 StatusEnum.IT.name()
						,StatusEnum.QC.name()
						,StatusEnum.RIT.name()
						,StatusEnum.SQ.name()
						,StatusEnum.BQ.name()
						,StatusEnum.RBQ.name()
						,StatusEnum.BIT.name()
						,StatusEnum.RBIT.name()
						);
		rfqStatusItem = FacesUtil.selectItemsProvider(statusItem, false);
		rfqStatusItemHasSelect = FacesUtil.selectItemsProvider(statusItem, true);
		
		Map<String, String> quoteStageBMTHisItems = new HashMap<String, String>();
		quoteStageBMTHisItems.put(StageEnum.FINISH.name(),"Finish");
		quoteStageBMTHisItems.put(StageEnum.PENDING.name(),"Pending");
		quoteStageBMTHisItems.put(StageEnum.INVALID.name(),"Invalid");
		quoteStageBMTHisItem = FacesUtil.selectItemsProvider(quoteStageBMTHisItems, false);
		quoteStageBMTHisItemHasSelect = FacesUtil.selectItemsProvider(quoteStageBMTHisItems, true);

		Map<String, String> items = new HashMap<String, String>();
		items.put("1", "Yes");
		items.put("0", "No");
		booleanItem = FacesUtil.selectItemsProvider(items, true);
		
		bmtFlagItemsForFilter.add(new SelectItem("", "ALL"));
		bmtFlagItemsForFilter.add(new SelectItem("A", "Non-BMT DR"));
		bmtFlagItemsForFilter.add(new SelectItem("B", "BMT DR"));
		bmtFlagItemsForFilter.add(new SelectItem("C", "MRS"));
		bmtFlagItemsForFilter.add(new SelectItem("D", "BMT-DR Incomplete"));
		bmtFlagItemsForFilter.add(new SelectItem("99", "<EMPTY>"));
		
		bmtFlagItems.add(new SelectItem("99", "<EMPTY>"));
		bmtFlagItems.add(new SelectItem("A", "Non-BMT DR"));
		bmtFlagItems.add(new SelectItem("B", "BMT DR"));
		bmtFlagItems.add(new SelectItem("C", "MRS"));
		bmtFlagItems.add(new SelectItem("D", "BMT-DR Incomplete"));
		
		LOG.info("init select item data end");
	}
	
	public List<String> completeMethodDesignRegionList(String input){
		FacesContext context = FacesContext.getCurrentInstance();
	    String region =  (String)UIComponent.getCurrentComponent(context).getAttributes().get("region");
	    
	    //Bryan Start
	    //List<String> outcome =  DesignLocationCacheManager.getLocationByRegion(region);
	    List<String> outcome =  cacheUtil.getLocationByRegion(region);
	    //Bryan End
	    if(!org.apache.commons.lang.StringUtils.isEmpty(input)){
	    	List<String> machResult = new ArrayList<String>();
	    	for(String str:outcome){
	    		if(str.toUpperCase().contains(input.toUpperCase())){
	    			machResult.add(str);
	    		}
	    	}
	    	return machResult;
	    }

	    return outcome;
	}

	public List<String> completeMethodBMTCurrencyList(String input){
		List<String> currencyLst = codeMaintenanceSB.findCurrency();
		if(!org.apache.commons.lang.StringUtils.isEmpty(input)){
			List<String> machResult = new ArrayList<String>();
			for(String str:currencyLst){
				if(str.toUpperCase().contains(input.toUpperCase())){
					machResult.add(str);
				}
			}
			return machResult;
		}
		
		return currencyLst;
	}
	
	public List<SelectItem> getRfqStatusItemHasSelect() {
		return rfqStatusItemHasSelect;
	}
	public List<SelectItem> getQuoteStageBMTHisItemHasSelect() {
		return quoteStageBMTHisItemHasSelect;
	}
	
	public List<SelectItem> getQuoteStageBMTHisItem() {
		return quoteStageBMTHisItem;
	}
	public List<SelectItem> getRfqStatusItem() {
		return rfqStatusItem;
	}
	
	public List<SelectItem> getBooleanItem() {
		return booleanItem;
	}

	public List<SelectItem> quoteTypeItem(boolean hasSelectOption) {
		//Bryan Start
		//List<String> quoteTypes = QuoteTypeCacheManager.getQuoteTypeList();
		List<String> quoteTypes = cacheUtil.getQuoteTypeList();
		//Bryan End
		return FacesUtil.selectItemsProvider(quoteTypes, hasSelectOption);
	}

	public List<SelectItem> enquirySegmentItem(boolean hasSelectOption) {
		//Bryan Start
		//List<String> enquirySegmentCodes = EnquirySegmentCacheManager.getEnquirySegmentList();
		List<String> enquirySegmentCodes = cacheUtil.getEnquirySegmentList();
		//Bryan End
		return FacesUtil.selectItemsProvider(enquirySegmentCodes, hasSelectOption);
	}

	public List<SelectItem> designRegionItem(boolean hasSelectOption) {
		//Bryan Start
		//List<String> designRegionLists = DesignLocationCacheManager.getDesignRegionList();
		List<String> designRegionLists = cacheUtil.getDesignRegionList();
		//Bryan End
		return FacesUtil.selectItemsProvider(designRegionLists, hasSelectOption);
	}

	public List<SelectItem> designLocaltionItem(boolean hasSelectOption) {
		//Bryan Start
		//Map<String, String> designLocaltionLists = DesignLocationCacheManager.getDesignLocationMap();
		Map<String, String> designLocaltionLists = cacheUtil.getDesignLocationMap();
		//Bryan End
		return FacesUtil.selectItemsProvider(designLocaltionLists, hasSelectOption);
	}

	public List<SelectItem> designInCatItem(boolean hasSelectOption) {
		//Bryan Start
		//List<String> designInCats = DesignInCatCacheManager.getDesignInCatList();
		List<String> designInCats = cacheUtil.getDesignInCatList();
		//Bryan End
		return FacesUtil.selectItemsProvider(designInCats, hasSelectOption);
	}

	public List<SelectItem> bmtFlagItemForFilter() {
		return bmtFlagItemsForFilter;
	}
	
	public List<SelectItem> bmtFlagItem() {
		return bmtFlagItems;
	}

}