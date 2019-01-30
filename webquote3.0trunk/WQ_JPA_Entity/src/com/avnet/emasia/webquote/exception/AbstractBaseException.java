/*
 * 
 */
package com.avnet.emasia.webquote.exception;

/**
 * The Class BaseException.
 */
public abstract class AbstractBaseException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The error. */
	private String errorCode;

	/** The caused by. */
	private Throwable causedBy;

	private Object[] parameters;

	private String message;
	
	private String mainMessage;

	public String getMessage() {
		if (message == null)
			return super.getMessage();
		else
			return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	
	public String getMainMessage() {
		return mainMessage;
	}

	public void setMainMessage(String mainMessage) {
		this.mainMessage = mainMessage;
	}

	/**
	 * Instantiates a new base exception.
	 */
	public AbstractBaseException() {
		super();
	}

	public AbstractBaseException(String errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
	}

	public AbstractBaseException(String errorCode, Object[] parameters) {
		super();
		this.errorCode = errorCode;
		this.parameters = parameters;
	}

	public AbstractBaseException(String errorCode, Throwable causedBy, Object[] parameters) {
		super(errorCode,causedBy);
		this.errorCode = errorCode;
		this.causedBy = causedBy;
		this.parameters = parameters;
	}

	public AbstractBaseException(Throwable causedBy) {
		super();
		this.causedBy = causedBy;
	}

	
	public AbstractBaseException(String errorCode, String mainMessage,String message) {
		super();
		this.message = message;
		this.mainMessage=mainMessage;
	}
	/**
	 * Instantiates a new base exception.
	 *
	 * @param errorCode
	 *            the error
	 * @param causedBy
	 *            the caused by
	 */
	public AbstractBaseException(final String errorCode, final Throwable causedBy) {
		super();
		this.errorCode = errorCode;
		this.causedBy = causedBy;
	}

	/**
	 * Gets the error code.
	 *
	 * @return the error code
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Sets the error code.
	 *
	 * @param errorCode
	 *            the new error code
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * Gets the caused by.
	 *
	 * @return the causedBy
	 */
	public Throwable getCausedBy() {
		return causedBy;
	}
	
	

	/* (non-Javadoc)
	 * @see java.lang.Throwable#getCause()
	 */
	@Override
	public synchronized Throwable getCause() {
		// TODO Auto-generated method stub
		return super.getCause();
	}

	/**
	 * Sets the caused by.
	 *
	 * @param causedBy
	 *            the causedBy to set
	 */
	public void setCausedBy(final Throwable causedBy) {
		this.causedBy = causedBy;
	}

	/**
	 * @return the parameters
	 */
	public Object[] getParameters() {
		return parameters;
	}

	/**
	 * @param parameters
	 *            the parameters to set
	 */
	public void setParameters(final Object[] parameters) {
		this.parameters = parameters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder(20);
		builder.append(this.getClass().getSimpleName()).append(" [errorCode=").append(errorCode).append(", causedBy=")
				.append(causedBy).append(']');
		return builder.toString();
	}
}
