package com.avnet.emasia.webquote.rowdata.datasource;

public interface ResolveInfo {
	
	default int getHeadRowIndex() {
		return 0;
	}
	/**the start of data content , skip the head*/
	default int getDataContentStartRowIndex() {
		return 1;
	}
	
	default String getResolvedFileName() {
		return "";
	}
	
}
