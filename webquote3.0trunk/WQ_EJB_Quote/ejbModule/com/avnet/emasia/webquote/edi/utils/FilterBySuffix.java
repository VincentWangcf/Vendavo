package com.avnet.emasia.webquote.edi.utils;

import java.io.File;
import java.io.FilenameFilter;

public class FilterBySuffix implements FilenameFilter {
	public FilterBySuffix(String suffix) {
		super();
		this.suffix = suffix;
	}
	private String suffix;
	public boolean accept(File file, String name) {
		return name.endsWith(suffix);
	} 
}
