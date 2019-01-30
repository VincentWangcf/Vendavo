package com.avnet.emasia.webquote.utilities.common;

import java.io.Serializable;
import java.util.Objects;


/**
 * 
 * @author 041863
 */
public class QueryBean implements Serializable  {

	private static final long serialVersionUID = 2206665514796984354L;
	private FilterMatchMode condition;
	private Object value;
	private String dataType;
	private String key;
	private boolean caseInsensitive = true;
	//added by damon@20160906
    private String  varParam;
	public QueryBean(FilterMatchMode condition, Object value, String key, String dataType) {
		super();
		this.condition = condition;
		this.value = value;
		this.key = key;
		this.dataType = dataType;
	}

	public QueryBean(FilterMatchMode condition, String key, Object value, String dataType,String param) {
		this.condition = condition;
		this.key = key;
		this.value = value;
		this.dataType = dataType;
		this.varParam =param;
	}
	
	public QueryBean(FilterMatchMode condition, String key, Object value) {
		this.condition = condition;
		this.key = key;
		this.value = value;
	}

	public boolean isCaseInsensitive() {
		return caseInsensitive;
	}

	public void setCaseInsensitive(boolean caseInsensitive) {
		this.caseInsensitive = caseInsensitive;
	}

	public String getKey() {
		return key;
	}

	public String getParameter() {
		return key.replace(".", "")+condition.name();
	}

	public void setKey(String key) {
		this.key = key;
	}

	public FilterMatchMode getCondition() {
		return condition;
	}

	public void setCondition(FilterMatchMode condation) {
		this.condition = condation;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 71 * hash + Objects.hashCode(this.value);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final QueryBean other = (QueryBean) obj;
		if (!Objects.equals(this.value, other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "QueryBean [condition=" + condition + ", value=" + value + ", dataType=" + dataType + ", key=" + key + ", varParam=" + varParam +"]";
	}

	public String getVarParam() {
		return varParam;
	}

	public void setVarParam(String varParam) {
		this.varParam = varParam;
	}

	
	
}
