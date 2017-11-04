package com.choodon.rpc.base.protocol;

public class ProtocolHeader {

    /**
     * PASS通行证
     */
    public static final short PASS = (short) 0xcd;

    /**
     * Message Code: 0x01 ~ 0x0f
     * =========================================================================
     * ==========
     */
    public static final byte REQUEST = 0x01; // RPCRequest
    public static final byte RESPONSE = 0x02; // RPCResponse
    public static final byte HEARTBEAT_PING = 0x03; // Heartbeat Ping
    public static final byte HEARTBEAT_PONG = 0x04; // Heartbeat Pong
    private byte messageCode; // sign 低地址4位

    /**
     * Serializer Code: 0x01 ~ 0x0f
     * =========================================================================
     * =======
     */
    // 位数限制最多支持15种不同的序列化/反序列化方式
    // protostuff = 0x01
    // hessian = 0x02
    // kryo = 0x03
    // java = 0x04
    // ...
    // XX1 = 0x0e
    // XX2 = 0x0f
    private byte serializerCode; // sign 高地址4位


    private byte status; // 响应状态码
    private long id; // request.invokeId, 用于映射 <ID, RPCRequest, RPCResponse> 三元组
    private int bodyLength; // 消息体长度

    public void sign(byte sign) {
        // sign 低地址4位
        this.messageCode = (byte) (sign & 0x0f);
        // sign 高地址4位, 先转成无符号int再右移4位
        this.serializerCode = (byte) ((((int) sign) & 0xff) >> 4);
    }


    public byte getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(byte messageCode) {
        this.messageCode = messageCode;
    }

    public byte getSerializerCode() {
        return serializerCode;
    }

    public void setSerializerCode(byte serializerCode) {
        this.serializerCode = serializerCode;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public void setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }

}
