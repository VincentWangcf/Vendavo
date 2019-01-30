package com.avnet.emasia.webquote.web.quote;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.Currency;
import com.avnet.emasia.webquote.entity.ExcCurrencyDefault;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.ejb.ExchangeRateSB;
import com.avnet.emasia.webquote.user.ejb.BizUnitSB;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil;
import com.avnet.emasia.webquote.web.user.UserInfo;

/* 
 * Passwords of the protected workbook and worksheet are both "ok" 
 */


@ManagedBean
@SessionScoped
public class ExchangeCurrencyMB implements Serializable {
	
	
	@EJB
	private ExchangeRateSB exchangeRateSB;
	@EJB
	private BizUnitSB bizUnitSB;

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(ExchangeCurrencyMB.class.getName());
	
	
	private SelectItem[] exCurrencyList;
	
	private ExcCurrencyDefault defaultCurrency;
	private User user;
	

	@PostConstruct
	public void initialize() {
		user = UserInfo.getUser();
		BizUnit bizUnit = user.getDefaultBizUnit();
		Set<String> allowCurrencies = bizUnit.getAllowCurrencies();
		//List<String> currencyLst =exchangeRateSB.findAllExCurrencyByBu(bizUnit.getName());
		List<String> currencyLst =  new ArrayList(allowCurrencies);
		exCurrencyList =  QuoteUtil.createFilterOptions(currencyLst.toArray(new String[currencyLst.size()]),currencyLst.toArray(new String[currencyLst.size()]),  false, false);
		
		defaultCurrency=exchangeRateSB.findDefaultCurrencyByBu(bizUnit.getName());
		if(null==defaultCurrency) {
			defaultCurrency = new ExcCurrencyDefault();
			defaultCurrency.setBizUnit(bizUnit.getName());
			defaultCurrency.setUpdatedBy(String.valueOf(user.getId()));
			defaultCurrency.setUpdatedOn(new Date());
		}
	}

	
	public void updateDefaultCurrency(){
		defaultCurrency.setUpdatedBy(String.valueOf(user.getId()));
		defaultCurrency.setUpdatedOn(new Date());
		LOG.info("updateDefaultCurrency selected currency is ===>> " + defaultCurrency.getCurrency());
		exchangeRateSB.updateDefaultCurrency(defaultCurrency);
		BizUnit bizUnit = bizUnitSB.findByPk(user.getDefaultBizUnit().getName());
		bizUnit.setDefaultCurrency(Currency.valueOf(defaultCurrency.getCurrency()));
		bizUnitSB.updateCurrencybyRegion(bizUnit);
		
		FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,	ResourceMB.getText("wq.message.currSettingSuccess")+".",ResourceMB.getText("wq.message.currSettingFinish")));
	}



	public SelectItem[] getExCurrencyList() {
		return exCurrencyList;
	}


	public void setExCurrencyList(SelectItem[] exCurrencyList) {
		this.exCurrencyList = exCurrencyList;
	}


	public ExcCurrencyDefault getDefaultCurrency() {
		return defaultCurrency;
	}


	public void setDefaultCurrency(ExcCurrencyDefault defaultCurrency) {
		this.defaultCurrency = defaultCurrency;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}





	
	
	


}
