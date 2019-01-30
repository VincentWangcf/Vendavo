package com.avnet.emasia.webquote.commodity.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avnet.emasia.webquote.commodity.dto.CheckBoxBean;
import com.avnet.emasia.webquote.utilities.DateUtils;

public class CommodifyHelper {

	/**
	 * @param args
	 */
	 public static Map<String, List<CheckBoxBean>> getCheckBoxBeanMap(List<String> aList)
	 {
	    	
		 Map<String, List<CheckBoxBean>> map =new HashMap<String, List<CheckBoxBean>>();
    	 List<CheckBoxBean> returnList = new ArrayList<CheckBoxBean>();
    	 CheckBoxBean cbb;
    	 for(String tempStr: aList )
    	 { 
    		cbb = new CheckBoxBean();
    		cbb.setChecked(true);
    		cbb.setEnable(true);
    		cbb.setName(tempStr);
    		returnList.add(cbb);	
    	 }
    	 return map;
	 }
	 
	 
	 public static  List<CheckBoxBean> getCheckBoxBeanList(List<String> aList)
	 {
	    	
		 List<CheckBoxBean> returnList = new ArrayList<CheckBoxBean>();
    	 CheckBoxBean cbb;
    	 for(String tempStr: aList )
    	 { 
    		cbb = new CheckBoxBean();
    		cbb.setChecked(true);
    		cbb.setEnable(true);
    		cbb.setName(tempStr);
    		returnList.add(cbb);	
    	 }
    	 return returnList;
	 }
	    

}