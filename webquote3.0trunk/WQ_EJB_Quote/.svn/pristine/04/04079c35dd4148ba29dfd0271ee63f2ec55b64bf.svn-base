package com.avnet.emasia.webquote.quote.ejb.constant;

public enum FilterMatchMode {

	STARTS_WITH("startsWith", "LIKE"), 
	ENDS_WITH("endsWith", "LIKE"), 
	CONTAINS("contains", "LIKE"), 
	EXACT("exact", "="), 
	IN("in", "IN"), 
	LT("lt", "<"), 
	GT("gt", ">"),
	GT_EQ("gteq", ">="), 
	LT_EQ("lteq", "<="); 

	/**
	 * Value of p:column's filterMatchMode attribute which corresponds to this
	 * math mode
	 */
	private final String uiParam;
	private final String sqlKey;

	FilterMatchMode(String uiParam, String sqlKey) {
		this.uiParam = uiParam;
		this.sqlKey = sqlKey;
	}
	
	public String sqlKey() {
		return sqlKey;
	}
	
	/**
	 * @param uiParam
	 *            value of p:column's filterMatchMode attribute
	 * @return MatchMode which corresponds to given UI parameter
	 * @throws IllegalArgumentException
	 *             if no MatchMode is corresponding to given UI parameter
	 */
	public static FilterMatchMode fromUiParam(String uiParam) {
		for (FilterMatchMode matchMode : values()) {
			if (matchMode.uiParam.equals(uiParam)) {
				return matchMode;
			}
		}
		return FilterMatchMode.CONTAINS;
	}

}
