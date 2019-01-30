package com.avnet.emasia.webquote.web.shoppingcart.helper;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.primefaces.model.UploadedFile;
import com.avnet.emasia.webquote.commodity.constant.ShoppingCartConstant;
import com.avnet.emasia.webquote.commodity.util.PricerUtils;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.vo.ShoppingCartLoadItemBean;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.util.POIUtils;
import com.avnet.emasia.webquote.utilites.web.util.PricerUploadHelper;
import com.avnet.emasia.webquote.utilities.bean.ExcelAliasBean;
import com.avnet.emasia.webquote.utilities.util.QuoteUtil;
import com.avnet.emasia.webquote.web.maintenance.UploadStrategy;

public class ShoppingCartUploadStrategy extends UploadStrategy {

	private static final long serialVersionUID = -43475458881197935L;
	private  static  List<ExcelAliasBean> excelAliasTreeSet = null ;
	
	@Override
	public List excel2Beans(Sheet sheet) {
		List<ShoppingCartLoadItemBean> beans = new ArrayList<ShoppingCartLoadItemBean>();
		FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
		User user = getUser();
		Cell cell;
		int lineSeq = 0;
		List<ExcelAliasBean> excelAliasTreeSet = PricerUploadHelper.getPricerExcelAliasAnnotation();

		Row row = null;
		int lastRownum = sheet.getLastRowNum();

		for (int i = 0; i <= lastRownum; i++) {
			 
			if (lineSeq < ShoppingCartConstant.TEMPLATE_HEADER_ROW_NUMBER) {
				lineSeq++;
				continue;
			}
			row = sheet.getRow(i);
			if (row == null) {
				continue;
			}
			ShoppingCartLoadItemBean bean = new ShoppingCartLoadItemBean();
			bean.setLineSeq(++lineSeq);
			for (ExcelAliasBean excelAliasBean : excelAliasTreeSet) {
				cell = row.getCell(excelAliasBean.getCellNum() - 1);
				if (cell != null) {
					POIUtils.setter(bean, excelAliasBean.getFieldName(),
							POIUtils.getCellValueWithFormatter(cell, evaluator), String.class);
				}
			}

			//bean.setBizUnit(user.getDefaultBizUnit());
			beans.add(bean);
		}
		sheet = null;
		return beans;
		
	}
	
	//
	/*****/

	public Boolean isValidateFileColumn(UploadedFile uploadFile) {
		return this.isValidateFileColumn(this.getSheet(uploadFile), 0, ShoppingCartConstant.TEMPLATE_NAME, 
				ShoppingCartConstant.TEMPLATE_COLUMNS);
	}
	/**
	 *  verify
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String verifyFields(List<?> beans) {

		StringBuffer validateMsg = new StringBuffer();

		ShoppingCartLoadItemBean bean = null;
		for (int i = 0; i < beans.size(); i++) {
			bean = (ShoppingCartLoadItemBean) beans.get(i);
			
			if (PricerUtils.isAllowAppend(validateMsg.length())) {
				/** validate mandatory for all fields */
				validateMsg.append(this.validateMandatoryFields(bean, getPricerExcelAliasAnnotation()));
			} else {
				break;
			}
			
			if (PricerUtils.isAllowAppend(validateMsg.length())) {
				/** validate length for some fields */
				validateMsg.append(this.validateLengthOfFields(bean, getPricerExcelAliasAnnotation()));
			} else {
				break;
			}
			
			if (PricerUtils.isAllowAppend(validateMsg.length())) {
				
				validateMsg.append(this.validateFieldType(bean));
			} else {
				break;
			}
		}
		return validateMsg.toString();
	}
	
	public String validateFieldType(ShoppingCartLoadItemBean bean){
		StringBuffer sb = new StringBuffer();
		if (!QuoteUtil.isEmpty(bean.getRequestQtyStr())&&
				!PricerUtils.isIntegerIncluleNegative(bean.getRequestQtyStr())) {	 	  	
			sb.append(ResourceMB.getParameterizedText("wq.error.reqQtyInteger", 
					String.valueOf(bean.getLineSeq()))+",<br/> ");	
		}
		if (!QuoteUtil.isEmpty(bean.getTargetResale())&&
				!PricerUtils.isDecimal(bean.getTargetResale())) {	 	  	
			sb.append(ResourceMB.getParameterizedText("wq.error.targetResaleType", 
					String.valueOf(bean.getLineSeq()))+",<br/> ");	
		}
		if (!QuoteUtil.isEmpty(bean.getFinalPrice())&&
				!PricerUtils.isDecimal(bean.getFinalPrice())) {	 	  	
			sb.append(ResourceMB.getParameterizedText("wq.error.finalPriceType", 
					String.valueOf(bean.getLineSeq()))+",<br/> ");	
		}
		return sb.toString();
	}
		//wq.label.reqQty
	//add synchronized to avoid the many thread initial the source at the same time .
	private static synchronized List<ExcelAliasBean> getPricerExcelAliasAnnotation(){
		if(excelAliasTreeSet==null){
			excelAliasTreeSet  = POIUtils.getQuoteBuilderExcelAliasAnnotation(ShoppingCartLoadItemBean.class);		
		}
		return excelAliasTreeSet;
	}
}
