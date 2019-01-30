package com.avnet.emasia.webquote.quote.ejb;

import java.io.Serializable;

import com.avnet.emasia.webquote.quote.ejb.constant.FilterMatchMode;
import java.util.Objects;

/**
 * 
 * @author 041863
 */
public class QueryBean implements Serializable {

	private static final long serialVersionUID = 2206665514796984354L;
	private FilterMatchMode condation;
	private Object value;

	public QueryBean(FilterMatchMode condation, Object value) {
		this.condation = condation;
		this.value = value;
	}
 

	public FilterMatchMode getCondation() {
		return condation;
	}

	public void setCondation(FilterMatchMode condation) {
		this.condation = condation;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
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
		return "QueryBean{condation=" + condation + ", value=" + value + '}';
	}

}
