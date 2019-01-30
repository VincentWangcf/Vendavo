package com.avnet.emasia.webquote.rowdata.datasource;

import java.util.List;

public interface ProcessSheetInterface {
	public void processEachRecord( int column,int row, String value);

	public void rowCompleted();
	
	//public List getBeanList();
	

	//public Object getHeaderBean() ;

	//public int getCountrows();
}