package com.avnet.emasia.webquote.utilities;

import java.util.function.Supplier;

public class StringBuilderDecorate {
	private final StringBuilder sb;
	private final int maxCapacity;
	public StringBuilderDecorate(int maxCapacity) {
		this.sb = new StringBuilder();
		this.maxCapacity = maxCapacity;
	}

	public StringBuilderDecorate append(String s) {
		if (s == null || s.trim().equals(""))
			return this;
		sb.append(s);
		if (sb.length() > maxCapacity) 
			throw new OverLimitException();
		return this;

	}
	
	public StringBuilderDecorate append(Supplier<String> s) {
		String r = null;
		if (s == null || (r=s.get())==null || r.trim().equals(""))
			return this;
		sb.append(r);
		if (sb.length() > maxCapacity) 
			throw new OverLimitException();
		return this;

	}
	
	@Override
	public String toString() {
		return sb.toString();
		
	}
	
	public static class OverLimitException extends RuntimeException {
		private static final long serialVersionUID = 7095909840617506328L;
		
	}
}