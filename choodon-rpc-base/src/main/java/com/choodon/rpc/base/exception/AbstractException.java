package com.choodon.rpc.base.exception;

public class AbstractException extends RuntimeException {
	private static final long serialVersionUID = -1060962454373779730L;
	protected ErrorMsg defaut = ErrorMsgConstant.FRAMEWORK_DEFAULT_ERROR;
	protected String errorMsg = null;

	public AbstractException() {
		super();
	}

	public AbstractException(ErrorMsg errorMsg) {
		super();
		this.defaut = errorMsg;
	}

	public AbstractException(String message) {
		super(message);
		this.errorMsg = message;
	}

	public AbstractException(String message, ErrorMsg errorMsg) {
		super(message);
		this.defaut = errorMsg;
		this.errorMsg = message;
	}

	public AbstractException(String message, Throwable cause) {
		super(message, cause);
		this.errorMsg = message;
	}

	public AbstractException(String message, Throwable cause, ErrorMsg errorMsg) {
		super(message, cause);
		this.defaut = errorMsg;
		this.errorMsg = message;
	}

	public AbstractException(Throwable cause) {
		super(cause);
	}

	public AbstractException(Throwable cause, ErrorMsg errorMsg) {
		super(cause);
		this.defaut = errorMsg;
	}

	@Override
	public String getMessage() {
		if (defaut == null) {
			return super.getMessage();
		}

		String message;

		if (errorMsg != null && !"".equals(errorMsg)) {
			message = errorMsg;
		} else {
			message = defaut.getMessage();
		}

		return "error_message: " + message + ", status: " + defaut.getStatus() + ", error_code: "
				+ defaut.getErrorCode();
		// return "error_message: " + message + ", status: " +
		// defaut.getStatus() + ", error_code: "
		// + defaut.getErrorCode() + ",r=" +
		// RpcContext.getContext().getRequestId();
	}

	public int getStatus() {
		return defaut != null ? defaut.getStatus() : 0;
	}

	public int getErrorCode() {
		return defaut != null ? defaut.getErrorCode() : 0;
	}

	public ErrorMsg getMotan() {
		return defaut;
	}

}
