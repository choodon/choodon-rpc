package com.choodon.rpc.base.exception;

public class ErrorMsgConstant {
    // service error status 503
    public static final int SERVICE_DEFAULT_ERROR_CODE = 10001;
    public static final int SERVICE_REJECT_ERROR_CODE = 10002;
    public static final int SERVICE_TIMEOUT_ERROR_CODE = 10003;
    public static final int SERVICE_TASK_CANCEL_ERROR_CODE = 10004;
    // service error status 404
    public static final int SERVICE_UNFOUND_ERROR_CODE = 10101;
    // service error status 403
    public static final int SERVICE_REQUEST_LENGTH_OUT_OF_LIMIT_ERROR_CODE = 10201;
    // framework error
    public static final int FRAMEWORK_DEFAULT_ERROR_CODE = 20001;
    public static final int FRAMEWORK_ENCODE_ERROR_CODE = 20002;
    public static final int FRAMEWORK_DECODE_ERROR_CODE = 20003;
    public static final int FRAMEWORK_INIT_ERROR_CODE = 20004;
    public static final int FRAMEWORK_EXPORT_ERROR_CODE = 20005;
    public static final int FRAMEWORK_SERVER_ERROR_CODE = 20006;
    public static final int FRAMEWORK_REFER_ERROR_CODE = 20007;
    public static final int FRAMEWORK_REGISTER_ERROR_CODE = 20008;
    // biz exception
    public static final int BIZ_DEFAULT_ERROR_CODE = 30001;

    public static final int PARAMETER_CHECK_ERROR_CODE = 40001;
    /**
     * service error start
     **/

    public static final ErrorMsg SERVICE_DEFAULT_ERROR = new ErrorMsg(503, SERVICE_DEFAULT_ERROR_CODE, "service error");
    public static final ErrorMsg SERVICE_REJECT = new ErrorMsg(503, SERVICE_REJECT_ERROR_CODE, "service reject");
    public static final ErrorMsg SERVICE_UNFOUND = new ErrorMsg(404, SERVICE_UNFOUND_ERROR_CODE, "service unfound");
    public static final ErrorMsg SERVICE_TIMEOUT = new ErrorMsg(503, SERVICE_TIMEOUT_ERROR_CODE,
            "service request timeout");
    public static final ErrorMsg SERVICE_TASK_CANCEL = new ErrorMsg(503, SERVICE_TASK_CANCEL_ERROR_CODE,
            "service task cancel");
    public static final ErrorMsg SERVICE_REQUEST_LENGTH_OUT_OF_LIMIT = new ErrorMsg(403,
            SERVICE_REQUEST_LENGTH_OUT_OF_LIMIT_ERROR_CODE, "servier requset data length over of limit");
    /**
     * framework error start
     **/
    public static final ErrorMsg FRAMEWORK_DEFAULT_ERROR = new ErrorMsg(503, FRAMEWORK_DEFAULT_ERROR_CODE,
            "framework default error");

    /**
     * service error end
     **/
    public static final ErrorMsg FRAMEWORK_ENCODE_ERROR = new ErrorMsg(503, FRAMEWORK_ENCODE_ERROR_CODE,
            "framework encode error");
    public static final ErrorMsg FRAMEWORK_DECODE_ERROR = new ErrorMsg(503, FRAMEWORK_DECODE_ERROR_CODE,
            "framework decode error");
    public static final ErrorMsg FRAMEWORK_INIT_ERROR = new ErrorMsg(500, FRAMEWORK_INIT_ERROR_CODE,
            "framework init error");
    public static final ErrorMsg FRAMEWORK_EXPORT_ERROR = new ErrorMsg(503, FRAMEWORK_EXPORT_ERROR_CODE,
            "framework export error");
    public static final ErrorMsg FRAMEWORK_REFER_ERROR = new ErrorMsg(503, FRAMEWORK_REFER_ERROR_CODE,
            "framework refer error");
    /**
     * biz error start
     **/
    public static final ErrorMsg BIZ_DEFAULT_EXCEPTION = new ErrorMsg(503, BIZ_DEFAULT_ERROR_CODE, "provider error");

    /** framework error end **/
    /**
     * 其他错误
     */
    public static final ErrorMsg PARAMETER_CHECK__EXCEPTION = new ErrorMsg(601, PARAMETER_CHECK_ERROR_CODE,
            "parameter error");

    private ErrorMsgConstant() {
    }
    /** biz error end **/
}
