package com.avnet.emasia.webquote.utilites.web.util;

import java.util.List;

import com.avnet.emasia.webquote.commodity.bean.PricerUploadTemplateBean;

public interface ProcessSheetInterface {
	public void processEachRecord( int column,int row, String value);

	public void rowCompleted();
	
	public List getBeanList();
	

	public Object getHeaderBean() ;

	public int getCountrows();
}