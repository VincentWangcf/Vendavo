package com.avnet.emasia.webquote.quote.strategy.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.quote.strategy.interfaces.DrmsValidationUpdateStrategy;
import com.avnet.emasia.webquote.quote.vo.QuoteItemVo;

public class NoBlockDrmsValidationUpdateStrategy implements DrmsValidationUpdateStrategy {

	@Override
	public String compareQGPAndAGP(QuoteItem item) {
		return null;
	}

	@Override
	public QuoteItem getUpdateSapReasonCodeItem(QuoteItem item) {
		item.setAuthGpReasonCode("14");
		item.setAuthGpReasonDesc("HIGHER GP%");
		return item;
	}

	@Override
	public boolean isValidationAGP() {
		return false;
	}

	@Override
	public void updateAuthGpReasonInEntity(List<QuoteItemVo> selectedQuoteItemVos) {
		for(QuoteItemVo vo : selectedQuoteItemVos){
			QuoteItem item = vo.getQuoteItem();
			String reasoncode = "01";
			String reasondesc = "COPIED FROM TARGET GP%";
			if(StringUtils.isNotBlank(vo.getAuthGpReasonCode()) || StringUtils.isNotBlank(vo.getAuthGpReasonDesc())) {
				reasoncode = vo.getAuthGpReasonCode();
				reasondesc = vo.getAuthGpReasonDesc();
			}
			item.setAuthGpReasonCode(reasoncode);
			item.setAuthGpReasonDesc(reasondesc);
		}
	}

	@Override
	public String getInternalComment(QuoteItem item) {
		return item.getInternalComment();
	}

}