package com.choodon.rpc.transport.netty.handler.codec;

import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.base.enums.MsgTypeEnum;
import com.choodon.rpc.base.log.LoggerUtil;
import com.choodon.rpc.base.protocol.*;
import com.choodon.rpc.transport.netty.common.SerializationEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

@Sharable
public class TcpProtocolEncoder extends MessageToByteEncoder<Holder> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Holder msg, ByteBuf out) throws Exception {
        MsgTypeEnum msgTypeEnum = MsgTypeEnum.instance(msg.getClass());
        switch (msgTypeEnum) {
            case REQUEST: {
                doEncodeRequest((RPCRequest) msg, out);
                break;
            }
            case RESPONSE: {
                doEncodeResponse((RPCResponse) msg, out);
                break;
            }
            case HEARTBEAT_PING: {
                doEncodeHeartBeatPing((HeartBeatPing) msg, out);
                break;
            }
            case HEARTBEAT_PONG: {
                doEncodeHeartBeatPong((HeartBeatPong) msg, out);
                break;
            }
            default:
                LoggerUtil.error("Msg type class [{}] is error.", msg.getClass().getCanonicalName());
                break;
        }
    }

    private void doEncodeRequest(RPCRequest request, ByteBuf out) {
        byte serializerCode = SerializationEnum.instance(request.getParameterValue(URLParamType.serialize.getName(), URLParamType.serialize.getName())).getCode();
        out.writeShort(Protocol.PASS);
        out.writeByte(MsgTypeEnum.REQUEST.getCode());
        out.writeCharSequence(request.getParameterValue(URLParamType.id.getName()), Charset.defaultCharset());
        out.writeByte(serializerCode);
        out.writeInt(request.getParameterIntValue(URLParamType.headerLength.getName()));
        out.writeBytes(request.getHeaderBytes());
        out.writeInt(request.getParameterIntValue(URLParamType.bodyLength.getName()));
        out.writeBytes(request.getBodyBytes());
    }

    private void doEncodeResponse(RPCResponse response, ByteBuf out) {
        byte serializerCode = SerializationEnum.instance(response.getParameterValue(URLParamType.serialize.getName(), URLParamType.serialize.getName())).getCode();
        out.writeShort(Protocol.PASS);
        out.writeByte(MsgTypeEnum.RESPONSE.getCode());
        out.writeCharSequence(response.getParameterValue(URLParamType.id.getName()), Charset.defaultCharset());
        out.writeByte(response.getParameterByteValue(URLParamType.status.getName()));
        out.writeByte(serializerCode);
        out.writeInt(response.getParameterIntValue(URLParamType.headerLength.getName()));
        out.writeBytes(response.getHeaderBytes());
        out.writeInt(response.getParameterIntValue(URLParamType.bodyLength.getName()));
        out.writeBytes(response.getBodyBytes());
    }

    private void doEncodeHeartBeatPing(HeartBeatPing heartBeatPing, ByteBuf out) {
        out.writeShort(Protocol.PASS);
        out.writeByte(MsgTypeEnum.HEARTBEAT_PING.getCode());
        out.writeCharSequence(heartBeatPing.getParameterValue(URLParamType.id.getName()), Charset.defaultCharset());
    }

    private void doEncodeHeartBeatPong(HeartBeatPong heartBeatPong, ByteBuf out) {
        out.writeShort(Protocol.PASS);
        out.writeByte(MsgTypeEnum.HEARTBEAT_PONG.getCode());
        out.writeCharSequence(heartBeatPong.getParameterValue(URLParamType.id.getName()), Charset.defaultCharset());
    }
}