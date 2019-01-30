package com.avnet.emasia.webquote.web.maintenance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.vo.BalUnconsumedQtyTemplateBean;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.util.POIUtils;
import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil;
import com.avnet.emasia.webquote.utilities.bean.ExcelAliasBean;

public class BalanceUnconsumedQtyUploadStrategy extends UploadStrategy{

	private static final long serialVersionUID = -8750502887098654463L;

	@Override
	public String verifyFields(List<?> beans) {
		
		ArrayList<String> list = new ArrayList<String>();
		boolean mFlg = false;
		boolean lFlg = false;

		BalUnconsumedQtyTemplateBean bean = null;
		
		for (int i=0;i<beans.size();i++) {
			bean = (BalUnconsumedQtyTemplateBean) beans.get(i);
			String mfr = bean .getMfr();
			String supplierQuoteNumber = bean.getSupplierQuoteNumber();
			String quotedPartNumber = bean.getQuotedPartNumber();
			String quoteNumber = bean.getQuoteNumber();
			
			if (QuoteUtil.isEmpty(mfr)) {     
				list.add(ResourceMB.getParameterizedString("wq.message.mandatoryFieldMFR", bean.getLineSeq())+"\r\n");
				mFlg = true;
				bean.setErr(true); 
			}
			
			if (QuoteUtil.isEmpty(supplierQuoteNumber)) { 
				list.add(ResourceMB.getParameterizedString("wq.error.mandatoryMFRQuote", bean.getLineSeq())+" # \r\n");
				mFlg = true;
				bean.setErr(true);
			}
			
			if (bean.getBuQty() == null) { 
				list.add(ResourceMB.getText("wq.message.row")+" #<"+bean.getLineSeq()+"> :"+ResourceMB.getText("wq.error.mandatoryBalanceUnconsumed")+" \r\n");
				mFlg = true;
				bean.setErr(true);
			}
			
			if (quoteNumber!=null&&quoteNumber.length() >20) { 
				if (!mFlg) {
					list.add(ResourceMB.getText("wq.message.row")+" #<"+bean.getLineSeq()+"> : "+ResourceMB.getText("wq.error.quotedPartLengthError")+" ");	
					bean.setErr(true);
					lFlg = true;
				}
				
			}
			if (mfr!=null&&mfr.length() > 10) { 
				if (!mFlg) {
					list.add(ResourceMB.getText("wq.message.row")+" #<"+bean.getLineSeq()+"> : "+ResourceMB.getText("wq.error.mfrLengthError")+" ");	
					bean.setErr(true);
					lFlg = true;
				}
			}
			if (quotedPartNumber!=null&&quotedPartNumber.length() > 80) {
				if (!mFlg) { 
					list.add(ResourceMB.getText("wq.message.row")+" #<"+bean.getLineSeq()+"> : "+ResourceMB.getText("wq.error.avnetQuoteLengthError")+" ");	
					bean.setErr(true);
					lFlg = true;	
				}
			}
			if (supplierQuoteNumber!=null&&supplierQuoteNumber.length() > 50) {
				if (!mFlg) {  
					list.add(ResourceMB.getText("wq.message.row")+" #<"+bean.getLineSeq()+"> : "+ResourceMB.getText("wq.error.mfrQuoteLengthError")+" ");	
					bean.setErr(true);
					lFlg = true;
				}
			}
			
			if(!QuoteUtil.isInteger(bean.getBuQty())){		
				if (!mFlg&&!lFlg) {
					if(!bean.getBuQty().equals("0")){ 
						list.add(ResourceMB.getText("wq.message.row")+" #<"+bean.getLineSeq()+"> "+ResourceMB.getText("wq.error.balanceUnconsumedQty")+" ");	
						bean.setErr(true);
					}
				}
			}						
		}
		
		return list.toString();
	}

	@Override
	public List excel2Beans(Sheet sheet) {
		List<BalUnconsumedQtyTemplateBean> beans = new ArrayList<BalUnconsumedQtyTemplateBean>();
		FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
		User user = getUser();
		String bizUnitName = null;
		if(user!=null){
			bizUnitName = user.getDefaultBizUnit().getName();
		}
		Cell cell;
		int lineSeq = 0;
		TreeSet<ExcelAliasBean> excelAliasTreeSet = POIUtils.getExcelAliasAnnotation(BalUnconsumedQtyTemplateBean.class);

		Row row = null;
		ExcelAliasBean excelAliasBean = null;
		Iterator<ExcelAliasBean> iterator = null;
	//	for (Row row : sheet) {// Don't deal withheader
			for(int i=0;i<=sheet.getLastRowNum();i++){
			// skip first row
			if (lineSeq == 0) {
				lineSeq++;
				continue;
			}
			row = sheet.getRow(i);

			BalUnconsumedQtyTemplateBean bean = new BalUnconsumedQtyTemplateBean();
			bean.setLineSeq(lineSeq++);
			iterator = excelAliasTreeSet.iterator();
			while(iterator.hasNext()){
				excelAliasBean = iterator.next();
				cell = row.getCell(excelAliasBean.getCellNum()-1); 
				POIUtils.setter(bean,excelAliasBean.getFieldName(),POIUtils.getCellValueWithFormatter(cell,evaluator),String.class);
			}
		
			bean.setBizUnit(bizUnitName);
			beans.add(bean);
		}
		sheet = null;
		return beans;
	}

}
