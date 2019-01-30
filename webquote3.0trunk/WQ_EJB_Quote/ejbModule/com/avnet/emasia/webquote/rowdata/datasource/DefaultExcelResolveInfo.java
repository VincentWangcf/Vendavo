package com.avnet.emasia.webquote.rowdata.datasource;

public class DefaultExcelResolveInfo implements ResolveInfo {
	/*the first column to resolve**/
	public int getColStartIndex() {
		return 0;
	}
	
	public int getSheetNo() {
		return 0;
	}
	
	public int getHeadCount() {
		return 1;
	}
}
