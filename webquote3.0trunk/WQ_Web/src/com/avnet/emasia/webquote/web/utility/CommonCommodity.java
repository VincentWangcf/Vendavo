package com.avnet.emasia.webquote.web.utility;

import java.util.List;
import java.util.logging.Logger;

import com.avnet.emasia.webquote.commodity.dto.CheckBoxBean;
import com.avnet.emasia.webquote.commodity.dto.CheckBoxSet;

public class CommonCommodity {
	
	public CheckBoxSet checkBoxSet;
	public List<CheckBoxBean> mfrList ;
    public List<CheckBoxBean> productGroupList ;
    public List<CheckBoxBean> programTypeList ;
	
	public List<String> selectedMfrList ;
    public List<String> selectedProductGroupList ;
    public List<String> selectedProgramTypeList ;
    
    public List<String> selectedMfrListTemp ;
    public List<String> selectedProductGroupListTemp ;
    public List<String> selectedProgramTypeListTemp ;
    
    private static final Logger LOGGER = Logger.getLogger(CommonCommodity.class.getName());

	
	public void updateFilterPanel()
    {
    	LOGGER.fine("call |  updateFilterPanel");
    	if(checkBoxSet!=null)
    	{
    	    //selectedProgramTypeList = checkBoxSet.displayFilter(checkBoxSet.getProgramType(),checkBoxSet.getProgramTypeStrList(false));
    	    selectedProgramTypeListTemp = selectedProgramTypeList;
    	    setSelectedProgramTypeList(selectedProgramTypeList);
    		
    	   /* selectedProductGroupList = checkBoxSet.displayFilter(checkBoxSet.getProductGroup(),checkBoxSet.getProductGroupStrList(false));
    	    selectedProductGroupListTemp= selectedProductGroupList;
    	    setSelectedProductGroupList(selectedProductGroupList);*/
    	    
    	   // selectedMfrList = checkBoxSet.displayFilter(checkBoxSet.getMfr(),checkBoxSet.getMfrStrList(false));
    		selectedMfrListTemp=selectedMfrList;
    		setSelectedMfrList(selectedMfrList);
    			
    	}
    }


	/**
	 * @return the checkBoxSet
	 */
	public CheckBoxSet getCheckBoxSet() {
		return checkBoxSet;
	}


	/**
	 * @param checkBoxSet the checkBoxSet to set
	 */
	public void setCheckBoxSet(CheckBoxSet checkBoxSet) {
		this.checkBoxSet = checkBoxSet;
	}


	/**
	 * @return the mfrList
	 */
	public List<CheckBoxBean> getMfrList() {
		return mfrList;
	}


	/**
	 * @param mfrList the mfrList to set
	 */
	public void setMfrList(List<CheckBoxBean> mfrList) {
		this.mfrList = mfrList;
	}


	/**
	 * @return the productGroupList
	 */
	public List<CheckBoxBean> getProductGroupList() {
		return productGroupList;
	}


	/**
	 * @param productGroupList the productGroupList to set
	 */
	public void setProductGroupList(List<CheckBoxBean> productGroupList) {
		this.productGroupList = productGroupList;
	}


	/**
	 * @return the programTypeList
	 */
	public List<CheckBoxBean> getProgramTypeList() {
		return programTypeList;
	}


	/**
	 * @param programTypeList the programTypeList to set
	 */
	public void setProgramTypeList(List<CheckBoxBean> programTypeList) {
		this.programTypeList = programTypeList;
	}


	/**
	 * @return the selectedMfrList
	 */
	public List<String> getSelectedMfrList() {
		return selectedMfrList;
	}


	/**
	 * @param selectedMfrList the selectedMfrList to set
	 */
	public void setSelectedMfrList(List<String> selectedMfrList) {
		this.selectedMfrList = selectedMfrList;
	}


	/**
	 * @return the selectedProductGroupList
	 */
	public List<String> getSelectedProductGroupList() {
		return selectedProductGroupList;
	}


	/**
	 * @param selectedProductGroupList the selectedProductGroupList to set
	 */
	public void setSelectedProductGroupList(List<String> selectedProductGroupList) {
		this.selectedProductGroupList = selectedProductGroupList;
	}


	/**
	 * @return the selectedProgramTypeList
	 */
	public List<String> getSelectedProgramTypeList() {
		return selectedProgramTypeList;
	}


	/**
	 * @param selectedProgramTypeList the selectedProgramTypeList to set
	 */
	public void setSelectedProgramTypeList(List<String> selectedProgramTypeList) {
		this.selectedProgramTypeList = selectedProgramTypeList;
	}


	/**
	 * @return the selectedMfrListTemp
	 */
	public List<String> getSelectedMfrListTemp() {
		return selectedMfrListTemp;
	}


	/**
	 * @param selectedMfrListTemp the selectedMfrListTemp to set
	 */
	public void setSelectedMfrListTemp(List<String> selectedMfrListTemp) {
		this.selectedMfrListTemp = selectedMfrListTemp;
	}


	/**
	 * @return the selectedProductGroupListTemp
	 */
	public List<String> getSelectedProductGroupListTemp() {
		return selectedProductGroupListTemp;
	}


	/**
	 * @param selectedProductGroupListTemp the selectedProductGroupListTemp to set
	 */
	public void setSelectedProductGroupListTemp(List<String> selectedProductGroupListTemp) {
		this.selectedProductGroupListTemp = selectedProductGroupListTemp;
	}


	/**
	 * @return the selectedProgramTypeListTemp
	 */
	public List<String> getSelectedProgramTypeListTemp() {
		return selectedProgramTypeListTemp;
	}


	/**
	 * @param selectedProgramTypeListTemp the selectedProgramTypeListTemp to set
	 */
	public void setSelectedProgramTypeListTemp(List<String> selectedProgramTypeListTemp) {
		this.selectedProgramTypeListTemp = selectedProgramTypeListTemp;
	}
	
	
}
