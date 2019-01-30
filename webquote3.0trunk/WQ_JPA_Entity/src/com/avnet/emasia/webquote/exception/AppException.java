package com.avnet.emasia.webquote.exception;

/**
 * @author Lin, Tough(901518)
 * Created on 2013-2-18
 */

public class AppException extends AbstractBaseException{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AppException(){
           super();
    }
    
    public AppException(String message){
           super(message);
    }
    
    public AppException(Throwable e) {
		super(e);
	}

	public AppException(String message, Throwable cause) {
		super(message, cause);
	}

	public AppException(String errorCode, Object[] parameters) {
		super(errorCode, parameters);
	}

	public AppException(String errorCode, Throwable causedBy, Object[] parameters) {
		super(errorCode, causedBy, parameters);
	}
	
	
}
