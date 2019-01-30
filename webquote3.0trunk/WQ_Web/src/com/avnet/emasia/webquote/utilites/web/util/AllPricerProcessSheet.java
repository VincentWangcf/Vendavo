package com.avnet.emasia.webquote.utilites.web.util;

import java.util.LinkedList;
import java.util.List;

import com.avnet.emasia.webquote.commodity.bean.PricerUploadTemplateBean;
import com.avnet.emasia.webquote.commodity.constant.PricerConstant;
import com.avnet.emasia.webquote.commodity.util.PricerUtils;
import com.avnet.emasia.webquote.utilities.bean.ExcelAliasBean;

public class AllPricerProcessSheet implements ProcessSheetInterface {
	private List<PricerUploadTemplateBean> beans = new LinkedList<PricerUploadTemplateBean>();
	private List<ExcelAliasBean> excelAliasTreeSet = PricerUploadHelper.getPricerExcelAliasAnnotation();
	private PricerUploadTemplateBean bean = null;
	private ExcelAliasBean excelAliasBean = null;
	private int currentRow =-1;


	@Override
	public void processEachRecord(int column, int row, String value) {
		currentRow = row;
		//System.out.println("ROW==================" + row + ",Value ====" + value);
		if (row > PricerConstant.PRICER_TEMPLATE_HEADER_ROW_NUMBER - 1) {
			if(column<PricerConstant.COLUMN_OF_LENGTH){
				excelAliasBean = excelAliasTreeSet.get(column);
				//System.out.println("row:" + row + " column:" + column + " value:" + value);
				POIUtils.setter(bean, excelAliasBean.getFieldName(), value, String.class);
			}
		}
	}

	@Override
	public void rowCompleted() {
		if (currentRow >= PricerConstant.PRICER_TEMPLATE_HEADER_ROW_NUMBER - 1) {
			if (bean != null&&!PricerUtils.isNullRow(bean)) {
				beans.add(bean);
			}
			bean = new PricerUploadTemplateBean();
			bean.setLineSeq(currentRow+2);
		}
	}

	@Override
	public List getBeanList() {
		return beans;
	}

	@Override
	public Object getHeaderBean() {
		return null;
	}

	@Override
	public int getCountrows() {
		return currentRow;
	}
}
