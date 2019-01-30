package com.avnet.emasia.webquote.quote.strategy.impl;

import java.text.NumberFormat;
import java.util.List;

import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.quote.strategy.interfaces.DrmsValidationUpdateStrategy;
import com.avnet.emasia.webquote.quote.vo.QuoteItemVo;
import com.avnet.emasia.webquote.utilities.util.QuoteUtil;

public class DefaultDrmsValidationUpdateStrategy implements DrmsValidationUpdateStrategy {

	@Override
	public String compareQGPAndAGP(QuoteItem item) {
		String msg = "";
		NumberFormat df = NumberFormat.getNumberInstance();
	    df.setMaximumFractionDigits(2);
		if(item.getResaleMargin() == null){
			if(item.getAuthGp() != null){
				msg = "Please proceed Internal Transfer because of Rounded Authorized GP " + df.format(item.getAuthGp()) + "% " + " > Rounded Quoted GP "; 
			}
		}else if(item.getResaleMargin() < 0){
			
		}else{
			if(item.getAuthGp() != null){
				if(com.avnet.emasia.webquote.utilities.util.QuoteUtil.compareQGPAndAGP(item.getResaleMargin(), item.getAuthGp()) < 0){
					msg = "Please proceed Internal Transfer because of Rounded Authorized GP " + df.format(item.getAuthGp()) + "% " + " > Rounded Quoted GP " + 
							df.format(item.getResaleMargin()) + "%";
				}
			}
		}
		return msg;
	}

	@Override
	public QuoteItem getUpdateSapReasonCodeItem(QuoteItem item) {
		item.setAuthGpReasonCode("14");
		item.setAuthGpReasonDesc("HIGHER GP%");
		return item;
	}

	@Override
	public boolean isValidationAGP() {
		return true;
	}

	@Override
	public void updateAuthGpReasonInEntity(List<QuoteItemVo> selectedQuoteItemVos) {
		for(QuoteItemVo vo : selectedQuoteItemVos){
			QuoteItem item = vo.getQuoteItem();
			item.setAuthGpReasonCode(vo.getAuthGpReasonCode());
			item.setAuthGpReasonDesc(vo.getAuthGpReasonDesc());
		}
	}

	@Override
	public String getInternalComment(QuoteItem item) {
		String comment = "";
		if(QuoteUtil.isEmpty(item.getInternalComment() )){
			comment = "A.GP is updated as Quote GP ("+item.getResaleMargin()+"%) by PM";
		}else{
			comment = item.getInternalComment() +"; A.GP is updated as Quote GP ("+item.getResaleMargin()+"%) by PM";
		}
		return comment;
	}
}