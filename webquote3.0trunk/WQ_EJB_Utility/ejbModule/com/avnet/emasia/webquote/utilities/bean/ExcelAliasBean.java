package com.avnet.emasia.webquote.utilities.bean;

public class ExcelAliasBean  {

	private String fieldName;
	
	private String aliasName;

	private Integer cellNum;
	
	private Integer fieldLength;
	
	private boolean normalMandatory = false;
	
	private boolean programMandatory = false;
	
	private boolean contractMandatory = false;
	
	private boolean pastMasterMandatory = false;
	
	private boolean simplePricer = false;
	
	private boolean salesCostPricer = false;
	
	private String mandatory;
	
	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public Integer getCellNum() {
		return cellNum;
	}

	public void setCellNum(Integer cellNum) {
		this.cellNum = cellNum;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public boolean isNormalMandatory() {
		return normalMandatory;
	}

	public void setNormalMandatory(boolean normalMandatory) {
		this.normalMandatory = normalMandatory;
	}

	public Integer getFieldLength() {
		return fieldLength;
	}

	public void setFieldLength(Integer fieldLength) {
		this.fieldLength = fieldLength;
	}

	public boolean isProgramMandatory() {
		return programMandatory;
	}

	public void setProgramMandatory(boolean programMandatory) {
		this.programMandatory = programMandatory;
	}

	public boolean isContractMandatory() {
		return contractMandatory;
	}

	public void setContractMandatory(boolean contractMandatory) {
		this.contractMandatory = contractMandatory;
	}

	public boolean isPastMasterMandatory() {
		return pastMasterMandatory;
	}

	public void setPastMasterMandatory(boolean pastMasterMandatory) {
		this.pastMasterMandatory = pastMasterMandatory;
	}

	public boolean isSimplePricer() {
		return simplePricer;
	}

	public void setSimplePricer(boolean simplePricer) {
		this.simplePricer = simplePricer;
	}
	
	public boolean isSalesCostPricer() {
		return salesCostPricer;
	}

	public void setSalesCostPricer(boolean salesCostPricer) {
		this.salesCostPricer = salesCostPricer;
	}

	public String getMandatory() {
		return mandatory;
	}

	public void setMandatory(String mandatory) {
		this.mandatory = mandatory;
	}

}
