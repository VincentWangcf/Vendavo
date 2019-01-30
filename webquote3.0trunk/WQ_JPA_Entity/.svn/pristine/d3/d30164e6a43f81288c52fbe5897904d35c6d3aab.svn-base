package com.avnet.emasia.webquote.utilities;



public class ResourceMessageKey {
	
	private String localeId;
	private String messageCode;

	public String getLocaleId() {
		return localeId;
	}

	public void setLocaleId(String localeId) {
		this.localeId = localeId;
	}

	public String getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((localeId == null) ? 0 : localeId.hashCode());
		result = prime * result + ((messageCode == null) ? 0 : messageCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResourceMessageKey other = (ResourceMessageKey) obj;
		if (localeId == null) {
			if (other.localeId != null)
				return false;
		} else if (!localeId.equals(other.localeId))
			return false;
		if (messageCode == null) {
			if (other.messageCode != null)
				return false;
		} else if (!messageCode.equals(other.messageCode))
			return false;
		return true;
	}

	
}
