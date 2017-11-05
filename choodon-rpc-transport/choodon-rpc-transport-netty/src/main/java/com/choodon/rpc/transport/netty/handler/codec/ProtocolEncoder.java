package com.choodon.rpc.transport.netty.handler.codec;

import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.log.LoggerUtil;
import com.choodon.rpc.base.protocol.*;
import com.choodon.rpc.transport.netty.common.SerializationEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@Sharable
public class ProtocolEncoder extends MessageToByteEncoder<Holder> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Holder msg, ByteBuf out) throws Exception {
        if (msg instanceof RPCRequest) {
            doEncodeRequest((RPCRequest) msg, out);
        } else if (msg instanceof RPCResponse) {
            doEncodeResponse((RPCResponse) msg, out);
        } else if (msg instanceof HeartBeatPing) {
            doEncodeHeartBeatPing((HeartBeatPing) msg, out);
        } else if (msg instanceof HeartBeatPong) {
            doEncodeHeartBeatPong((HeartBeatPong) msg, out);
        } else {
            LoggerUtil.error(msg.getClass().getCanonicalName() + " can be found.");
        }
    }

    private void doEncodeRequest(RPCRequest request, ByteBuf out) {
        byte serializerCode = (byte) SerializationEnum.valueOf(request.getSerializer()).getValue();
        byte sign = (byte) ((serializerCode << 4) + ProtocolHeader.REQUEST);
        long invokeId = request.getId();
        byte[] bytes = request.getBytes();
        int length = bytes.length;

        out.writeShort(ProtocolHeader.PASS).writeByte(sign).writeByte(0x00).writeLong(invokeId).writeInt(length)
                .writeBytes(bytes);
    }

    private void doEncodeResponse(RPCResponse response, ByteBuf out) {
        byte serializerCode = (byte) SerializationEnum.valueOf(response.getSerializer()).getValue();
        byte sign = (byte) ((serializerCode << 4) + ProtocolHeader.RESPONSE);
        byte status = response.getStatus();
        long id = response.getId();
        byte[] bytes = response.getBytes();
        int length = bytes.length;
        out.writeShort(ProtocolHeader.PASS).writeByte(sign).writeByte(status).writeLong(id).writeInt(length)
                .writeBytes(bytes);
    }

    private void doEncodeHeartBeatPing(HeartBeatPing heartBeatPing, ByteBuf out) {
        byte serializerCode = (byte) SerializationEnum.valueOf(heartBeatPing.getSerializer()).getValue();
        byte sign = (byte) ((serializerCode << 4) + ProtocolHeader.HEARTBEAT_PING);
        long invokeId = heartBeatPing.getId();
        byte[] bytes = heartBeatPing.getBytes();
        int length = bytes.length;
        out.writeShort(ProtocolHeader.PASS).writeByte(sign).writeByte(0x00).writeLong(invokeId).writeInt(length).writeBytes(bytes);
    }

    private void doEncodeHeartBeatPong(HeartBeatPong heartBeatPong, ByteBuf out) {
        byte serializerCode = (byte) SerializationEnum.valueOf(heartBeatPong.getSerializer()).getValue();
        byte sign = (byte) ((serializerCode << 4) + ProtocolHeader.HEARTBEAT_PONG);
        long id = heartBeatPong.getId();
        byte[] bytes = heartBeatPong.getBytes();
        int length = bytes.length;
        out.writeShort(ProtocolHeader.PASS).writeByte(sign).writeByte(0x00).writeLong(id).writeInt(length).writeBytes(bytes);
    }
}