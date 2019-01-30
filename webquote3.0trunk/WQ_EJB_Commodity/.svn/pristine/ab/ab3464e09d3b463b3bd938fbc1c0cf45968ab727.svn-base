package com.avnet.emasia.webquote.commodity.dto;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.avnet.emasia.webquote.entity.DataAccess;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.ProductGroup;
import com.avnet.emasia.webquote.entity.ProgramType;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-02-20
 * the checkbox model of the webpromo page.
 */

public class CheckBoxSet implements Serializable {

	private static final long serialVersionUID = 901691496637643642L;
	
	private static Logger logger = Logger.getLogger(CheckBoxSet.class.getName());
	
	private List<CheckBoxBean> programType = null;
	private List<CheckBoxBean> productGroup = null;
	private List<CheckBoxBean> mfr= null;
	private String mfrPart= null;
	private String exact= null;
	private String active = null;
	private boolean success;
	private boolean dataAccessCheckRequired = false;
	private List<DataAccess> dataAccesses ; 
	

	
	
	
	
	public List<DataAccess> getDataAccesses() {
		return dataAccesses;
	}
	public void setDataAccesses(List<DataAccess> dataAccesses) {
		this.dataAccesses = dataAccesses;
	}
	public boolean isDataAccessCheckRequired() {
		return dataAccessCheckRequired;
	}
	public void setDataAccessCheckRequired(boolean dataAccessCheckRequired) {
		this.dataAccessCheckRequired = dataAccessCheckRequired;
	}
	public List<CheckBoxBean> getProgramType() {
		return programType;
	}
	public void setProgramType(List<CheckBoxBean> programType) {
		this.programType = programType;
	}
	
	public void setProgramTypeObj(List<ProgramType> programTypeList) {
		
		 List<CheckBoxBean> returnList = new ArrayList<CheckBoxBean>();
    	 CheckBoxBean cbb;
    	 for(ProgramType obj: programTypeList )
    	 { 
    		cbb = new CheckBoxBean();
    		cbb.setChecked(true);
    		cbb.setEnable(true);
    		cbb.setName(obj.getName());
    		returnList.add(cbb);	
    	 }
		this.programType = returnList;
	}
	
	public List<CheckBoxBean> getProductGroup() {
		return productGroup;
	}
	public void setProductGroup(List<CheckBoxBean> productGroup) {
		this.productGroup = productGroup;
	}
	
	public void setProductGroupObj(List<ProductGroup> productGroupList) {
		
		 List<CheckBoxBean> returnList = new ArrayList<CheckBoxBean>();
	   	 CheckBoxBean cbb;
	   	 for(ProductGroup obj: productGroupList )
	   	 { 
	   		cbb = new CheckBoxBean();
	   		cbb.setChecked(true);
	   		cbb.setEnable(true);
	   		cbb.setName(obj.getName());
	   		returnList.add(cbb);	
	   	 }
		this.productGroup = returnList;
	}
	public List<CheckBoxBean> getMfr() {
		return mfr;
	}
	public void setMfr(List<CheckBoxBean> mfr) {
		this.mfr = mfr;
	}
	
	public void setMfrObj(List<Manufacturer> mfrList) {
		
		 List<CheckBoxBean> returnList = new ArrayList<CheckBoxBean>();
	   	 CheckBoxBean cbb;
	   	 for(Manufacturer obj: mfrList )
	   	 { 
	   		cbb = new CheckBoxBean();
	   		cbb.setChecked(true);
	   		cbb.setEnable(true);
	   		cbb.setName(obj.getName());
	   		returnList.add(cbb);	
	   	 }
		this.mfr = returnList;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMfrPart() {
		return mfrPart;
	}
	public void setMfrPart(String mfrPart) {
		this.mfrPart = mfrPart;
		this.exact= null;
	}
	public String getExact() {
		return exact;
	}
	public void setExact(String exact) {
		this.exact = exact;
	}
	
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public List<String> getMfrStrList(boolean full)
	{
		List<String> returnList = new ArrayList<String>();
		if(null!=getMfr())
		{
			for(CheckBoxBean cbb : getMfr())
			{
				if(full)
				{
					returnList.add(cbb.getName());
				}
				else
				{
					if(cbb.isChecked())
					{
					   returnList.add(cbb.getName());
					}
				}
			}
			
			if(returnList.size()==0 && getMfr().size()!=0)
			{
				returnList = getMfrStrList(true);
			}
		}
		else 
			return null;
		return returnList;
	}
	public List<String> getProductGroupStrList(boolean full)
	{
		List<String> returnList = new ArrayList<String>();
		if(null!=getProductGroup())
		{
			for(CheckBoxBean cbb : getProductGroup())
			{
				if(full)
				{
					returnList.add(cbb.getName());
				}
				else
				{
					if(cbb.isChecked())
					{
					   returnList.add(cbb.getName());
					}
				}
		    }
			
			if(returnList.size()==0 && getProductGroup().size()!=0)
			{
				returnList = getProductGroupStrList(true);
			}
		}
		else 
			return null;
		
		return returnList;
	}
	
	public List<String> getProgramTypeStrList(boolean full)
	{
		List<String> returnList = new ArrayList<String>();
		if(null!=getProgramType())
		{
			for(CheckBoxBean cbb : getProgramType())
			{
				if(full)
				{
					returnList.add(cbb.getName());
				}
				else
				{
					if(cbb.isChecked())
					{
					   returnList.add(cbb.getName());
					}
				}
			}
			
			if(returnList.size()==0 && getProgramType().size()!=0)
			{
				returnList = getProgramTypeStrList(true);
			}
		}
		else 
			return null;
		
		return returnList;
	}
	
	public List<CheckBoxBean> updateCheckBoxBatch(List<CheckBoxBean> fullList, List<CheckBoxBean> newList, boolean enable)
	{
		Set<String> tempSet = new HashSet<String>();
		for(CheckBoxBean cbb: newList)
		{
			tempSet.add(cbb.getName());
		}
		for(CheckBoxBean cbb: fullList)
		{
			if(!tempSet.contains(cbb.getName()))
			{
				cbb.setChecked(false);
				if(enable)
				{
				  cbb.setEnable(false);
				}
			}
			else
			{
				if(enable)
				{
				  cbb.setEnable(true);
				}
				cbb.setChecked(true);
			}
		}
		return fullList;
	}
	
	public List<CheckBoxBean> updateCheckBox(List<CheckBoxBean> fullList, List<String> selectedItemList)
	{
		Set<String> tempSet = new HashSet<String>();
		for(String str: selectedItemList)
		{
			tempSet.add(str);
		}
		
		for(CheckBoxBean cbb: fullList)
		{
			if(tempSet.contains(cbb.getName()))
			{
				cbb.setChecked(true);
			}
			else
			{
				cbb.setChecked(false);
			}
		}
		return fullList;
	}
	public boolean isNew()
	{
		if(null== getProgramType() && null== getProductGroup() && null== getMfr() && null==getMfrPart())
		{
			return true;
		}
		else
		{
		    return false;	
		}
	}
    public PISearchBean getPiSearchBean(boolean emptyIsNull)
    {
    	if(isNew())
    	{
    		PISearchBean pis = new PISearchBean();
			pis.setDataAccessCheckRequired(isDataAccessCheckRequired());
			pis.setDataAccesses(getDataAccesses());
			return pis;
    	}
    	else
    	{
    		PISearchBean pis = new PISearchBean();
    		pis.setExact(getExact());
			pis.setProgramList(getProgramTypeStrList(false),emptyIsNull);
			pis.setProductGroupList(getProductGroupStrList(false),emptyIsNull);
			pis.setMfrList(getMfrStrList(false),emptyIsNull);
			pis.setMfrPart(getMfrPart());
			pis.setDataAccessCheckRequired(isDataAccessCheckRequired());
			pis.setDataAccesses(getDataAccesses());
			return pis;
    	}

    }
    public PISearchBean getPiSearchBean()
    {
    	return getPiSearchBean(false);
    }
    
    public void setProgram(List<CheckBoxBean> programType, boolean emptyIsNull) {
    	if(programType!=null)
    	{
    		if(programType.size()==0 && emptyIsNull)
    			programType = null;
    	}
		this.programType = programType;

	}
	public void setProductGroup(List<CheckBoxBean> productGroup, boolean emptyIsNull) {
		if(productGroup!=null)
    	{
    		if(productGroup.size()==0 && emptyIsNull)
    			productGroup = null;
    	}
		this.productGroup = productGroup;
	}
	public void setMfr(List<CheckBoxBean> mfr, boolean emptyIsNull) {
		if(mfr!=null)
    	{
    		if(mfr.size()==0 && emptyIsNull)
    			mfr = null;
    	}
		this.mfr = mfr;
	}
	
	public List<CheckBoxBean> specifiedOneMfr(List<CheckBoxBean> fullList, String mfr)
	{
		
		for(CheckBoxBean cbb: fullList)
		{
			if(cbb.getName().equalsIgnoreCase(mfr))
			{
				cbb.setChecked(true);
			}
			else
			{
				cbb.setChecked(false);
			}
		}
		return fullList;
	}
	
	public List<String> displayFilter(List<CheckBoxBean> fullList, List<String> selectedList)
	{
		logger.fine("call displayFilter");
		List<String> returnList = new ArrayList<String>();
		
		if(fullList!=null)
		{
			int size = 0; 
		    for(CheckBoxBean cbb : fullList)
		    {
		    	if(cbb.isEnable())
		    		size++;
		    }
			if(size==selectedList.size())
		    {
		    	return returnList;
		    }
		}
		return selectedList;		
	}
	
    public void refreshAll()
    {
    	
        for(CheckBoxBean ccb :  programType)
		{
        	ccb.setChecked(true);
        	ccb.setEnable(true);
		}
        for(CheckBoxBean ccb :  productGroup)
		{
        	ccb.setChecked(true);
        	ccb.setEnable(true);
		}
        for(CheckBoxBean ccb :  mfr)
		{
        	ccb.setChecked(true);
        	ccb.setEnable(true);
		}
    }
	
}
