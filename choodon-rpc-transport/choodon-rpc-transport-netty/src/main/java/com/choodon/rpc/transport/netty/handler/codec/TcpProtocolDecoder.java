package com.choodon.rpc.transport.netty.handler.codec;

import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.base.enums.MsgTypeEnum;
import com.choodon.rpc.base.exception.RPCFrameworkException;
import com.choodon.rpc.base.protocol.*;
import com.choodon.rpc.transport.netty.common.SerializationEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

@Sharable
public class TcpProtocolDecoder extends ByteToMessageDecoder {


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        short pass = in.readShort();
        ProtocolHeader.checkPass(pass);
        byte msgTypeCode = in.readByte();
        MsgTypeEnum msgTypeEnum = MsgTypeEnum.instance(msgTypeCode);
        switch (msgTypeEnum) {
            case REQUEST: {
                CharSequence id = in.readCharSequence(36, Charset.defaultCharset());
                byte serializationCode = in.readByte();
                SerializationEnum serializationEnum = SerializationEnum.instance(serializationCode);
                Integer headerLength = in.readInt();
                byte[] headBytes = new byte[headerLength];
                in.readBytes(headBytes);
                Integer bodyLength = in.readInt();
                byte[] bodyBytes = new byte[headerLength];
                in.readBytes(bodyBytes);
                RPCRequest rpcRequest = new RPCRequest(id.toString());
                rpcRequest.addParameter(URLParamType.serialize.getName(), serializationEnum.getValue());
                rpcRequest.addParameter(URLParamType.headerLength.getName(), headerLength.toString());
                rpcRequest.addParameter(URLParamType.bodyLength.getName(), bodyLength.toString());
                rpcRequest.setHeaderBytes(headBytes);
                rpcRequest.setBodyBytes(bodyBytes);
                out.add(rpcRequest);
                break;
            }
            case RESPONSE: {
                CharSequence id = in.readCharSequence(36, Charset.defaultCharset());
                Byte status = in.readByte();
                byte serializationCode = in.readByte();
                SerializationEnum serializationEnum = SerializationEnum.instance(serializationCode);
                Integer headerLength = in.readInt();
                byte[] headBytes = new byte[headerLength];
                in.readBytes(headBytes);
                Integer bodyLength = in.readInt();
                byte[] bodyBytes = new byte[headerLength];
                in.readBytes(bodyBytes);
                RPCResponse rpcResponse = new RPCResponse(id.toString());
                rpcResponse.addParameter(URLParamType.status.getName(), status.toString());
                rpcResponse.addParameter(URLParamType.serialize.getName(), serializationEnum.getValue());
                rpcResponse.addParameter(URLParamType.headerLength.getName(), headerLength.toString());
                rpcResponse.addParameter(URLParamType.bodyLength.getName(), bodyLength.toString());
                rpcResponse.setHeaderBytes(headBytes);
                rpcResponse.setBodyBytes(bodyBytes);
                out.add(rpcResponse);
                break;
            }
            case HEARTBEAT_PING: {
                CharSequence id = in.readCharSequence(36, Charset.defaultCharset());
                HeartBeatPing heartBeatPing = new HeartBeatPing(id.toString());
                out.add(heartBeatPing);
                break;
            }
            case HEARTBEAT_PONG: {
                CharSequence id = in.readCharSequence(36, Charset.defaultCharset());
                HeartBeatPong heartBeatPong = new HeartBeatPong(id.toString());
                out.add(heartBeatPong);
                break;
            }
            default:
                throw new RPCFrameworkException("Illegal msg type");
        }
    }
}
