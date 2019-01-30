package com.avnet.emasia.webquote.utilites.web.util;

import java.util.List;

import com.avnet.emasia.webquote.quote.vo.QuoteBuilderSalesCostBean;
import com.avnet.emasia.webquote.utilities.bean.ExcelAliasBean;

public class SalesCostHeaderProcessSheet implements ProcessSheetInterface {
	private List<ExcelAliasBean> excelAliasTreeSet = POIUtils.getQuoteBuilderExcelAliasAnnotation(QuoteBuilderSalesCostBean.class);

	private ExcelAliasBean excelAliasBean = null;

	private QuoteBuilderSalesCostBean headerBean = new QuoteBuilderSalesCostBean();

	@Override
	public void processEachRecord(int column, int row, String value) {
		if (row == 2) {
			if (column < 30) {
				excelAliasBean = excelAliasTreeSet.get(column);
				POIUtils.setter(headerBean, excelAliasBean.getFieldName(), value, String.class);
			}
		}
	}

	public Object getHeaderBean() {
		return headerBean;
	}

	@Override
	public void rowCompleted() {

	}

	@Override
	public List getBeanList() {
		return null;
	}

	@Override
	public int getCountrows() {
		return -1;
	}
}
