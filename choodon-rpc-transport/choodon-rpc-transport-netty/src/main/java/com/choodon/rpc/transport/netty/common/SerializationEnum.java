package com.choodon.rpc.transport.netty.common;

import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.util.NumberUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public enum SerializationEnum {

    protostuff(1, RPCConstants.PROTOSTUFF),
    kryo(2, RPCConstants.KRYO),
    json(3, RPCConstants.FASTJSON);


    private Byte code;
    private String value;

    SerializationEnum(Integer code, String value) {
        this.code = code.byteValue();
        this.value = value;
    }

    public Byte getCode() {
        return code;
    }

    public void setCode(Byte code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static SerializationEnum instance(Byte code) {
        Optional<SerializationEnum> optional = Arrays.stream(SerializationEnum.values()).filter(serializationEnum -> NumberUtils.equals(serializationEnum.code, code)).findAny();
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new IllegalArgumentException("Illegal serialization code.");
        }
    }

    public static SerializationEnum instance(String value) {
        Optional<SerializationEnum> optional = Arrays.stream(SerializationEnum.values()).filter(serializationEnum -> Objects.equals(serializationEnum.value, value)).findAny();
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new IllegalArgumentException("Illegal serialization value.");
        }
    }
}
