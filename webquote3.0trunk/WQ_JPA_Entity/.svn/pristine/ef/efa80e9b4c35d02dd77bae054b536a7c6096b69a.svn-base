package com.avnet.emasia.webquote.cache;

import java.util.Arrays;

public enum ActionIndicator {
	CLEARALL,
	CLEAREXRATES;
	public static boolean hasValue(String v) {
		if(v==null) return false;
		return Arrays.stream(ActionIndicator.values())
				.anyMatch(p->p.toString().equals(v)); 
		
	}
}
