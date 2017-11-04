package com.choodon.rpc.base.exception;

public class RPCFrameworkException extends AbstractException {
	private static final long serialVersionUID = -2382881641605947790L;

	public RPCFrameworkException() {
		super(ErrorMsgConstant.FRAMEWORK_DEFAULT_ERROR);
	}

	public RPCFrameworkException(ErrorMsg motanErrorMsg) {
		super(motanErrorMsg);
	}

	public RPCFrameworkException(String message) {
		super(message, ErrorMsgConstant.FRAMEWORK_DEFAULT_ERROR);
	}

	public RPCFrameworkException(String message, ErrorMsg motanErrorMsg) {
		super(message, motanErrorMsg);
	}

	public RPCFrameworkException(String message, Throwable cause) {
		super(message, cause, ErrorMsgConstant.FRAMEWORK_DEFAULT_ERROR);
	}

	public RPCFrameworkException(String message, Throwable cause, ErrorMsg motanErrorMsg) {
		super(message, cause, motanErrorMsg);
	}

	public RPCFrameworkException(Throwable cause) {
		super(cause, ErrorMsgConstant.FRAMEWORK_DEFAULT_ERROR);
	}

	public RPCFrameworkException(Throwable cause, ErrorMsg motanErrorMsg) {
		super(cause, motanErrorMsg);
	}

}
