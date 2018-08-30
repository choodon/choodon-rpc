package com.choodon.rpc.transport.netty.handler;

import com.choodon.rpc.base.RPCContext;
import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.base.log.LoggerUtil;
import com.choodon.rpc.base.protocol.HeartBeatPing;
import com.choodon.rpc.base.protocol.RPCResponse;
import com.choodon.rpc.base.util.NetUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

@ChannelHandler.Sharable
public class HttpClientHandler extends AbstractClientHandler {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpResponse) {
            FullHttpResponse httpResponse = (FullHttpResponse) msg;
            HttpHeaders httpHeaders = httpResponse.headers();
            String serializationName = httpHeaders.get(RPCConstants.SERIALIZER);
            String protocolLength = httpHeaders.get(RPCConstants.PROTOCOL_LENGTH);
            String id = httpHeaders.get(RPCConstants.ID);
            String type = httpHeaders.get(RPCConstants.MESSAGE_TYPE);
            ByteBuf byteBuf = httpResponse.content();
            int length = byteBuf.readableBytes();
            RPCResponse response = null;
            if (type.equalsIgnoreCase(RPCConstants.MESSAGE_TYPE_RESPONSE)) {
                response = new RPCResponse(id);
                response.addParameter(URLParamType.serialize.getName(), serializationName);
                byte[] content = new byte[length];
                byteBuf.readBytes(content);
                response.setBytes(content);
            } else if (type.equalsIgnoreCase(RPCConstants.MESSAGE_TYPE_PONG)) {
                String client_Server = NetUtil.getHostAndPortStr(ctx.channel().localAddress()) + "-->" + NetUtil.getHostAndPortStr(ctx.channel().remoteAddress());
                COUNTER_CONTAINER.getAndDecrement(client_Server);
                LoggerUtil.info(client_Server + " receive  heartbeat pong");
            }
            if (length == Integer.parseInt(protocolLength)) {
                RPCContext.receiveResponse(response);
            } else {
                LoggerUtil.error("PROTOCOL LENGTH ERROR");
            }
        }
        ReferenceCountUtil.safeRelease(msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            HeartBeatPing request = new HeartBeatPing();
//            RPCContext.setRequest(request);
            DefaultFullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/", Unpooled.copiedBuffer("HeartBeat Ping", CharsetUtil.UTF_8));
            httpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpRequest.content().readableBytes());
            httpRequest.headers().set(RPCConstants.PROTOCOL_LENGTH, httpRequest.content().readableBytes());
            httpRequest.headers().set(RPCConstants.ID, request.getId());
            httpRequest.headers().set(RPCConstants.MESSAGE_TYPE, RPCConstants.MESSAGE_TYPE_PING);
            ctx.channel().writeAndFlush(httpRequest);
            String client_Server = NetUtil.getHostAndPortStr(ctx.channel().localAddress()) + "-->" + NetUtil.getHostAndPortStr(ctx.channel().remoteAddress());
            long counter = COUNTER_CONTAINER.getAndIncrement(client_Server);
            LoggerUtil.info(client_Server + " send  heartbeat ping");
            if (counter > 3) {
                ctx.channel().close().sync();
                LoggerUtil.error("Heartbeat Check failed 3 times,close " + client_Server + "channel. ");
            }


        }
    }
}
