package com.avnet.emasia.webquote.rowdata;

public interface Bean {
	
	int getSeq();
	void setSeq(int seq);
	String getconvertAndValidationError();
	String putConvertError(String field, String convertError);
	String getConvertError(String field);
	String getConvertErrors();
	
	
	String getValidateViolationMessage();
	void setValidateViolationMessage(String message);
	
	boolean isConvertSuccessful();
	boolean isValid();
	
}
