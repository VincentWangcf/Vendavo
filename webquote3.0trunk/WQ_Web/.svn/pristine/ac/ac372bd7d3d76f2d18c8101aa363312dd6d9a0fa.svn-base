package com.avnet.emasia.webquote.utilites.web.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;

import com.avnet.emasia.webquote.quote.vo.QuoteBuilderNonSalesCostBean;
import com.avnet.emasia.webquote.quote.vo.QuoteBuilderSalesCostBean;
import com.avnet.emasia.webquote.utilities.bean.ExcelAliasBean;
import com.avnet.emasia.webquote.web.quote.constant.QuoteConstant;

public class SalesCostQuoteBuilderProcessSheet implements ProcessSheetInterface {
	private List<QuoteBuilderSalesCostBean> beans = new ArrayList<QuoteBuilderSalesCostBean>();
	private List<ExcelAliasBean> excelAliasTreeSet = POIUtils.getQuoteBuilderExcelAliasAnnotation(QuoteBuilderSalesCostBean.class);
	private QuoteBuilderSalesCostBean bean = null;
	private ExcelAliasBean excelAliasBean = null;
	private int currentRow = -1;

	@Override
	public void processEachRecord(int column, int row, String value) {
		currentRow = row;
		if (row > 2) {
			if (column < 30) {
				excelAliasBean = excelAliasTreeSet.get(column);
				POIUtils.setter(bean, excelAliasBean.getFieldName(), value, String.class);
			}
		}
	}

	@Override
	public void rowCompleted() {
		if (currentRow >= 2 ) {
			if (bean != null && !isNullBean(bean)) {
				beans.add(bean);
			}
			bean = new QuoteBuilderSalesCostBean();
			bean.setLineSeq(currentRow + 2);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean isNullBean(QuoteBuilderSalesCostBean bean) {
		boolean isNullBean = true;
		try {
			if (bean != null) {
				List<ExcelAliasBean> excelAliasTreeSet = POIUtils
						.getQuoteBuilderExcelAliasAnnotation(QuoteBuilderSalesCostBean.class);
				Class clazz = bean.getClass();
				for (ExcelAliasBean excelAliasBean : excelAliasTreeSet) {
					String fieldName = excelAliasBean.getFieldName();
					fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1, fieldName.length());
					String methodName = "get" + fieldName;
					methodName = methodName.trim();
					Method m = clazz.getDeclaredMethod(methodName, new Class[] {});
					String value = (String) m.invoke(bean, new Object[] {});
					if (StringUtils.isNotBlank(value)) {
						isNullBean = false;
						break;
					}
				}
			}
		} catch (Exception e) {
		}
		
		return isNullBean;
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
