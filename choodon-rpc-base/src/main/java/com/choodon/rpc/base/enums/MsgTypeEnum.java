package com.choodon.rpc.base.enums;

import com.choodon.rpc.base.protocol.*;
import com.choodon.rpc.base.util.NumberUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 消息类型
 *
 * @author michael
 * @since 2018/8/29
 */
public enum MsgTypeEnum {

    REQUEST(1, RPCRequest.class),
    RESPONSE(2, RPCResponse.class),
    HEARTBEAT_PING(3, HeartBeatPing.class),
    HEARTBEAT_PONG(4, HeartBeatPong.class);

    private Byte code;

    private Class msgClass;


    MsgTypeEnum(Integer code, Class msgClass) {
        this.code = code.byteValue();
        this.msgClass = msgClass;
    }

    public Byte getCode() {
        return code;
    }

    public void setCode(Byte code) {
        this.code = code;
    }

    public Class getMsgClass() {
        return msgClass;
    }

    public void setMsgClass(Class msgClass) {
        this.msgClass = msgClass;
    }

    public static MsgTypeEnum instance(byte code) {
        Optional<MsgTypeEnum> optional = Arrays.stream(MsgTypeEnum.values()).filter(msgTypeEnum -> NumberUtils.equals(code, msgTypeEnum.code)).findAny();
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new IllegalArgumentException("Illegal msg type code.");
        }
    }

    public static MsgTypeEnum instance(Class msgClass) {
        Optional<MsgTypeEnum> optional = Arrays.stream(MsgTypeEnum.values()).filter(msgTypeEnum -> Objects.equals(msgClass, msgTypeEnum.msgClass)).findAny();
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new IllegalArgumentException("Illegal msg class.");
        }
    }

}
