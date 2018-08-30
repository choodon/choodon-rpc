package com.choodon.rpc.base.enums;

/**
 * 状态枚举
 *
 * @author michael
 * @since 2018/8/30
 */
public enum StatusEnum {

    SUCCEESS(0, "success"),
    ERROR(1, "error"),
    BUSY(2, "busy");

    private Byte code;
    private String msg;

    StatusEnum(Integer code, String msg) {
        this.code = code.byteValue();
        this.msg = msg;
    }

    public Byte getCode() {
        return code;
    }

    public void setCode(Byte code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
