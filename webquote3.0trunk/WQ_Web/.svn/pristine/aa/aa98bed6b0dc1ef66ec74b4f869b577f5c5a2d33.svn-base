package com.avnet.emasia.webquote.web.catalogsearch.helper;

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

import com.avnet.emasia.webquote.commodity.bean.PricerUploadTemplateBean;
import com.avnet.emasia.webquote.commodity.constant.CatalogSearchConstant;
import com.avnet.emasia.webquote.commodity.constant.PRICER_TYPE;
import com.avnet.emasia.webquote.commodity.constant.PricerConstant;
import com.avnet.emasia.webquote.commodity.constant.UPLOAD_ACTION;

import com.avnet.emasia.webquote.commodity.util.PricerUtils;
import com.avnet.emasia.webquote.entity.SalesCostType;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.quote.vo.CatalogSearchItemBean;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.util.POIUtils;
import com.avnet.emasia.webquote.utilites.web.util.PricerUploadHelper;
import com.avnet.emasia.webquote.utilities.bean.ExcelAliasBean;
import com.avnet.emasia.webquote.utilities.util.QuoteUtil;
import com.avnet.emasia.webquote.web.maintenance.UploadStrategy;
import com.avnet.emasia.webquote.web.quote.constant.QuoteConstant;

public class CatalogSearchUploadStrategy extends UploadStrategy {

	private static final long serialVersionUID = -43475458881197935L;
	private  static  List<ExcelAliasBean> excelAliasTreeSet = null ;
	
	@Override
	public List excel2Beans(Sheet sheet) {
		List<CatalogSearchItemBean> beans = new ArrayList<CatalogSearchItemBean>();
		FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
		User user = getUser();
		Cell cell;
		int lineSeq = 0;
		List<ExcelAliasBean> excelAliasTreeSet = PricerUploadHelper.getPricerExcelAliasAnnotation();

		Row row = null;
		int lastRownum = sheet.getLastRowNum();

		for (int i = 0; i <= lastRownum; i++) {
			 
			if (lineSeq < CatalogSearchConstant.CATALOG_SEARCH_TEMPLATE_HEADER_ROW_NUMBER) {
				lineSeq++;
				continue;
			}
			row = sheet.getRow(i);
			if (row == null) {
				continue;
			}
			CatalogSearchItemBean bean = new CatalogSearchItemBean();
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
		return this.isValidateFileColumn(this.getSheet(uploadFile), 0, CatalogSearchConstant.SEARCH_TEMPLATE_NAME, 
				CatalogSearchConstant.SEARCH_TEMPLATE_COLUMNS);
	}
	/**
	 *  verify
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String verifyFields(List<?> beans) {

		StringBuffer validateMsg = new StringBuffer();

		CatalogSearchItemBean bean = null;
		for (int i = 0; i < beans.size(); i++) {
			bean = (CatalogSearchItemBean) beans.get(i);
			
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
	
	public String validateFieldType(CatalogSearchItemBean bean){
		StringBuffer sb = new StringBuffer();
		if (!QuoteUtil.isEmpty(bean.getRequestQtyStr())&&
				!PricerUtils.isIntegerIncluleNegative(bean.getRequestQtyStr())) {	 	  	
			sb.append(ResourceMB.getParameterizedText("wq.error.reqQtyInteger", 
					String.valueOf(bean.getLineSeq()))+",<br/> ");	
		}	
		return sb.toString();
	}
		//wq.label.reqQty
	//add synchronized to avoid the many thread initial the source at the same time .
	private static synchronized List<ExcelAliasBean> getPricerExcelAliasAnnotation(){
		if(excelAliasTreeSet==null){
			excelAliasTreeSet  = POIUtils.getQuoteBuilderExcelAliasAnnotation(CatalogSearchItemBean.class);		
		}
		return excelAliasTreeSet;
	}
}
