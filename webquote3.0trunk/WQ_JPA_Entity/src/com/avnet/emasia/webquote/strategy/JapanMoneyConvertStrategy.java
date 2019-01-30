package com.avnet.emasia.webquote.strategy;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.Currency;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.ExchangeRate;
import com.avnet.emasia.webquote.entity.QuoteItem;

public class JapanMoneyConvertStrategy extends MoneyConvertStrategy{
	@Override
	public ExchangeRate[] findExchangeRate(Currency fromCurrency, Currency toCurrency, BizUnit bizUnit, 
			Customer soldToCustomer, Date date, List<ExchangeRate> exchangeRates) {
		return super.findExchangeRate(fromCurrency, toCurrency, bizUnit, null, date, exchangeRates);
		
	}
	
	@Override
	public Date getExRateDateTime(QuoteItem quoteItem) {
		if(quoteItem == null) {
			throw new RuntimeException("quoteItem is null.");
		}
		return quoteItem.getSubmissionDate() == null ? new Date() : quoteItem.getSubmissionDate();
		 
	}
	//assertNotNull(quoteItem);
			
}