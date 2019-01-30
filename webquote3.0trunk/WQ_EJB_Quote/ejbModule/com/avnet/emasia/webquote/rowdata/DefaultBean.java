package com.avnet.emasia.webquote.rowdata;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.cxf.common.util.StringUtils;

public class DefaultBean implements Bean{
	@Transient
	private int seq;
	
	@Transient
	private Map<String, String> convertErrors = new HashMap<>();
	
	@Transient
	private String validateViolationMessage;
	

	@Override
 	public int getSeq() {
		return seq;
	}

	@Override
	public void setSeq(int seq) {
		this.seq = seq;
	}


	@Override
	public String putConvertError(String field, String convertError) {
		return convertErrors.put(field, convertError);
	}

	@Override
	public String getConvertError(String field) {
		return convertErrors.get(field);
	}

	
	@Override
	public String getConvertErrors() {
//		return "Row[" + getSeq() + "]: " + 
		String s = convertErrors.entrySet().stream().map(Entry::getValue).reduce("", 
				(s1, s2) -> {
					if (StringUtils.isEmpty(s1)){
						return s2;
					} else {
						return s1 + (StringUtils.isEmpty(s2) ?  "" : ("; " + s2) );
					}
		});
				return StringUtils.isEmpty(s) ? "": s;
				
	}

	@Override
	public boolean isConvertSuccessful() {
		return convertErrors.size() > 0 ? false : true;
	}

	@Override
	public boolean isValid() {
		return StringUtils.isEmpty(validateViolationMessage);
	}

	@Override
	public String getValidateViolationMessage() {
		
		return validateViolationMessage == null ? "" : validateViolationMessage;
	}

	@Override
	public void setValidateViolationMessage(String message) {
		validateViolationMessage = message;
	}

	/* (non-Javadoc)
	 * @see com.avnet.emasia.webquote.rowdata.Bean#getconvertAndValidationError()
	 */
	@Override
	public String getconvertAndValidationError() {
		String convertAndValidationError = null;

		convertAndValidationError = this.getValidateViolationMessage() + this.getConvertErrors();
		if (StringUtils.isEmpty(this.getValidateViolationMessage()) && StringUtils.isEmpty(this.getConvertErrors())) {
			return null;
		}

		return convertAndValidationError;
	}


}
 