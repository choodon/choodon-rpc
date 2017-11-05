package com.choodon.rpc.transport.netty.handler.codec;

import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.exception.RPCFrameworkException;
import com.choodon.rpc.base.protocol.*;
import com.choodon.rpc.transport.netty.common.SerializationEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.Signal;

import java.util.List;

public class ProtocolDecoder extends ReplayingDecoder<ProtocolDecoder.State> {

    // 协议体最大限制, 默认5M
    private static final int MAX_BODY_SIZE = 1024 * 1024 * 5;

    private static final boolean USE_COMPOSITE_BUF = true;

    public ProtocolDecoder() {
        super(State.HEADER_PASS);
        if (USE_COMPOSITE_BUF) {
            setCumulator(COMPOSITE_CUMULATOR);
        }
    }

    // 协议头
    private final ProtocolHeader header = new ProtocolHeader();

    private static void checkMagic(short magic) throws Signal {
        if (magic != ProtocolHeader.PASS) {
            throw new RPCFrameworkException("ILLEGAL_MAGIC");
        }
    }

    private static int checkBodyLength(int size) throws Signal {
        if (size > MAX_BODY_SIZE) {
            throw new RPCFrameworkException("BODY_TOO_LARGE");
        }
        return size;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        switch (state()) {
            case HEADER_PASS:
                checkMagic(in.readShort()); // MAGIC
                checkpoint(State.HEADER_SIGN);
            case HEADER_SIGN:
                header.sign(in.readByte()); // 消息标志位
                checkpoint(State.HEADER_STATUS);
            case HEADER_STATUS:
                header.setStatus(in.readByte()); // 状态位
                checkpoint(State.HEADER_ID);
            case HEADER_ID:
                header.setId(in.readLong()); // 消息id
                checkpoint(State.HEADER_BODY_LENGTH);
            case HEADER_BODY_LENGTH:
                header.setBodyLength(in.readInt()); // 消息体长度
                checkpoint(State.BODY);
            case BODY: {
                switch (header.getMessageCode()) {
                    case ProtocolHeader.HEARTBEAT_PING: {
                        int length = checkBodyLength(header.getBodyLength());
                        byte[] bytes = new byte[length];
                        in.readBytes(bytes);
                        HeartBeatPing ping = new HeartBeatPing(header.getId());
                        String serializationType = SerializationEnum.valueOf("serialization" + header.getSerializerCode()).getValue()
                                .toString();
                        ping.setSerializer(serializationType);
                        ping.setBytes(bytes);
                        out.add(ping);
                        break;
                    }
                    case ProtocolHeader.HEARTBEAT_PONG: {
                        int length = checkBodyLength(header.getBodyLength());
                        byte[] bytes = new byte[length];
                        in.readBytes(bytes);
                        HeartBeatPong pong = new HeartBeatPong(header.getId());
                        String serializationType = SerializationEnum.valueOf("serialization" + header.getSerializerCode()).getValue()
                                .toString();
                        pong.setSerializer(serializationType);
                        out.add(pong);
                        break;
                    }
                    case ProtocolHeader.REQUEST: {
                        int length = checkBodyLength(header.getBodyLength());
                        byte[] bytes = new byte[length];
                        in.readBytes(bytes);
                        RPCRequest request = new RPCRequest(header.getId());
                        String serializationType = SerializationEnum.valueOf("serialization" + header.getSerializerCode()).getValue()
                                .toString();
                        request.setSerializer(serializationType);
                        request.setBytes(bytes);
                        out.add(request);
                        break;
                    }
                    case ProtocolHeader.RESPONSE: {
                        int length = checkBodyLength(header.getBodyLength());
                        byte[] bytes = new byte[length];
                        in.readBytes(bytes);

                        RPCResponse response = new RPCResponse(header.getId());
                        response.setStatus(header.getStatus());
                        // todo待处理，根据状态码不同进行后续的反序列处理
                        String serializationType = SerializationEnum.valueOf("serialization" + header.getSerializerCode()).getValue()
                                .toString();
                        response.setSerializer(serializationType);
                        response.setBytes(bytes);
                        out.add(response);
                        break;
                    }
                    default:
                        throw new RPCFrameworkException("ILLEGAL_MSGTYPE");
                }
            }
            checkpoint(State.HEADER_PASS);
            break;
            default:  throw new RPCFrameworkException("ILLEGAL PACKET");
        }
    }

    enum State {
        HEADER_PASS, HEADER_SIGN, HEADER_STATUS, HEADER_ID, HEADER_BODY_LENGTH, BODY
    }
}
