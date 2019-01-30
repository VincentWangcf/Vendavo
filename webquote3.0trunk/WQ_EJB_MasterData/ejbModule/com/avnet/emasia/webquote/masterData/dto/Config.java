package com.avnet.emasia.webquote.masterData.dto;

import java.util.Arrays;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-01-25
 * 
 */
public class Config {
	
	private String filePath ;
	private String backupPath;
    private String dataFilePostfix;
    private String controlFilePostfix;
    private String mailServer;
	private String mailSendTo ;
	private String mailSendFrom ;
	private String backupFlag ;
	private String allowMultipleLoad;
	//Bryan Start
	private String jobTempOutputPath ;
	//Bryan End
	//whwong Start
	private long rowLimit=0 ;
	//whwong End

	private String fileName;
	private String className;
	public long getRowLimit() {
		return rowLimit;
	}

	public void setRowLimit(long rowLimit) {
		this.rowLimit = rowLimit;
	}

	private String[] methods;
	private String[] paramTypes;
	private String pkClass;
	private String[] pkClassParamTypes;
	private String[] pkClassMethods;
	private String fkClass;
	private String[] fkClassParamTypes;
	private String[] fkClassMethods;
	
	private String fk1Class;
	private String[] fk1ClassParamTypes;
	private String[] fk1ClassMethods;
	
	private String fk2Class;
	private String[] fk2ClassParamTypes;
	private String[] fk2ClassMethods;
	

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getBackupPath() {
		return backupPath;
	}

	public void setBackupPath(String backupPath) {
		this.backupPath = backupPath;
	}

	public String getDataFilePostfix() {
		return dataFilePostfix;
	}

	public void setDataFilePostfix(String dataFilePostfix) {
		this.dataFilePostfix = dataFilePostfix;
	}

	public String getControlFilePostfix() {
		return controlFilePostfix;
	}

	public void setControlFilePostfix(String controlFilePostfix) {
		this.controlFilePostfix = controlFilePostfix;
	}

	public String getMailServer() {
		return this.mailServer;
	}

	public void setMailServer(String mailServer) {
		this.mailServer = mailServer;
	}

	public String getMailSendTo() {
		return mailSendTo;
	}

	public void setMailSendTo(String mailSendTo) {
		this.mailSendTo = mailSendTo;
	}

	public String getMailSendFrom() {
		return mailSendFrom;
	}

	public void setMailSendFrom(String mailSendFrom) {
		this.mailSendFrom = mailSendFrom;
	}

	public boolean isBackupFlag() {
		if(backupFlag!=null && backupFlag.equalsIgnoreCase("Y"))
			return true;
		else 
			return false;
	}

	public boolean isAllowMultipleLoad() {
		if(allowMultipleLoad!=null && allowMultipleLoad.equalsIgnoreCase("Y"))
			return true;
		else 
			return false;
	}


	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getBackupFlag() {
		return backupFlag;
	}

	public void setBackupFlag(String backupFlag) {
		this.backupFlag = backupFlag;
	}

	public String getAllowMultipleLoad() {
		return allowMultipleLoad;
	}

	public void setAllowMultipleLoad(String allowMultipleLoad) {
		this.allowMultipleLoad = allowMultipleLoad;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String[] getMethods() {
		return methods;
	}

	public void setMethods(String[] methods) {
		this.methods = methods;
	}

	public String[] getParamTypes() {
		return paramTypes;
	}

	public void setParamTypes(String[] paramTypes) {
		this.paramTypes = paramTypes;
	}

	public String getPkClass() {
		return pkClass;
	}

	public void setPkClass(String pkClass) {
		this.pkClass = pkClass;
	}

	public String[] getPkClassParamTypes() {
		return pkClassParamTypes;
	}

	public void setPkClassParamTypes(String[] pkClassParamTypes) {
		this.pkClassParamTypes = pkClassParamTypes;
	}

	
	public String getFkClass() {
		return fkClass;
	}

	public void setFkClass(String fkClass) {
		this.fkClass = fkClass;
	}

	public String[] getFkClassParamTypes() {
		return fkClassParamTypes;
	}

	public void setFkClassParamTypes(String[] fkClassParamTypes) {
		this.fkClassParamTypes = fkClassParamTypes;
	}

	public String[] getPkClassMethods() {
		return pkClassMethods;
	}

	public void setPkClassMethods(String[] pkClassMethods) {
		this.pkClassMethods = pkClassMethods;
	}

	public String[] getFkClassMethods() {
		return fkClassMethods;
	}

	public void setFkClassMethods(String[] fkClassMethods) {
		this.fkClassMethods = fkClassMethods;
	}

	
	public String getFk1Class() {
		return fk1Class;
	}

	public void setFk1Class(String fk1Class) {
		this.fk1Class = fk1Class;
	}

	public String[] getFk1ClassParamTypes() {
		return fk1ClassParamTypes;
	}

	public void setFk1ClassParamTypes(String[] fk1ClassParamTypes) {
		this.fk1ClassParamTypes = fk1ClassParamTypes;
	}

	public String[] getFk1ClassMethods() {
		return fk1ClassMethods;
	}

	public void setFk1ClassMethods(String[] fk1ClassMethods) {
		this.fk1ClassMethods = fk1ClassMethods;
	}

	
	public String getFk2Class() {
		return fk2Class;
	}

	public void setFk2Class(String fk2Class) {
		this.fk2Class = fk2Class;
	}

	public String[] getFk2ClassParamTypes() {
		return fk2ClassParamTypes;
	}

	public void setFk2ClassParamTypes(String[] fk2ClassParamTypes) {
		this.fk2ClassParamTypes = fk2ClassParamTypes;
	}

	public String[] getFk2ClassMethods() {
		return fk2ClassMethods;
	}

	public void setFk2ClassMethods(String[] fk2ClassMethods) {
		this.fk2ClassMethods = fk2ClassMethods;
	}
	
	public String getJobTempOutputPath() {
		return jobTempOutputPath;
	}

	public void setJobTempOutputPath(String jobTempOutputPath) {
		this.jobTempOutputPath = jobTempOutputPath;
	}

	@Override
	public String toString() {
		return "Config [filePath=" + filePath + ", backupPath=" + backupPath
				+ ", dataFilePostfix=" + dataFilePostfix
				+ ", controlFilePostfix=" + controlFilePostfix
				+ ", MailServer=" + mailServer + ", mailSendTo=" + mailSendTo
				+ ", mailSendFrom=" + mailSendFrom + ", backupFlag="
				+ backupFlag + ", allowMultipleLoad=" + allowMultipleLoad
				+ ", fileName=" + fileName + ", className=" + className
				+ ", methods=" + Arrays.toString(methods) + ", paramTypes="
				+ Arrays.toString(paramTypes) + ", pkClass=" + pkClass
				+ ", pkClassParamTypes=" + Arrays.toString(pkClassParamTypes)
				+ ", pkClassMethods=" + Arrays.toString(pkClassMethods)
				+ ", fkClass=" + fkClass + ", fkClassParamTypes="
				+ Arrays.toString(fkClassParamTypes) + ", fkClassMethods="
				+ Arrays.toString(fkClassMethods) + "]";
	}





	
	

}
