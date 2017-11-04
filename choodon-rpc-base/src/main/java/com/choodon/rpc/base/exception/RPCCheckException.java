package com.choodon.rpc.base.exception;

public class RPCCheckException extends AbstractException {

	private static final long serialVersionUID = -1802386379834236739L;

	public RPCCheckException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RPCCheckException(ErrorMsg errorMsg) {
		super(errorMsg);
		// TODO Auto-generated constructor stub
	}

	public RPCCheckException(String message, ErrorMsg errorMsg) {
		super(message, errorMsg);
		// TODO Auto-generated constructor stub
	}

	public RPCCheckException(String message, Throwable cause, ErrorMsg errorMsg) {
		super(message, cause, errorMsg);
		// TODO Auto-generated constructor stub
	}

	public RPCCheckException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public RPCCheckException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public RPCCheckException(Throwable cause, ErrorMsg errorMsg) {
		super(cause, errorMsg);
		// TODO Auto-generated constructor stub
	}

	public RPCCheckException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}



}
