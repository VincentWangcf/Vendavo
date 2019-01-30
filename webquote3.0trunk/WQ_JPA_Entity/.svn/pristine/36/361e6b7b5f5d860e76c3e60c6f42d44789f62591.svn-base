/*
 * 
 */
package com.avnet.emasia.webquote.exception;

/**
 * The Class BaseException.
 */
public abstract class AbstractBaseRuntimeException extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The error. */
	private String errorCode;

	/** The caused by. */
	private Throwable causedBy;

	private Object[] parameters;
	
	/**
	 * Instantiates a new base exception.
	 */
	public AbstractBaseRuntimeException() {
		super();
	}

	public AbstractBaseRuntimeException(String errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
	}

	public AbstractBaseRuntimeException(String errorCode, Object[] parameters) {
		super();
		this.errorCode = errorCode;
		this.parameters = parameters;
	}

	public AbstractBaseRuntimeException(String errorCode, Throwable causedBy, Object[] parameters) {
		super();
		this.errorCode = errorCode;
		this.causedBy = causedBy;
		this.parameters = parameters;
	}

	public AbstractBaseRuntimeException(Throwable causedBy) {
		super();
		this.causedBy = causedBy;
	}

	/**
	 * Instantiates a new base exception.
	 *
	 * @param errorCode
	 *            the error
	 * @param causedBy
	 *            the caused by
	 */
	public AbstractBaseRuntimeException(final String errorCode, final Throwable causedBy) {
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
	 * @param errorCode the new error code
	 */
	public void setErrorCode(final String errorCode) {
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
	 * @param parameters the parameters to set
	 */
	public void setParameters(Object[] parameters) {
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
