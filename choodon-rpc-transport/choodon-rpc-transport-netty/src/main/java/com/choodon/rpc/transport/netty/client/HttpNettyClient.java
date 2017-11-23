package com.choodon.rpc.transport.netty.client;

import com.choodon.rpc.base.RPCCallback;
import com.choodon.rpc.base.RPCContext;
import com.choodon.rpc.base.RPCFuture;
import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.base.extension.SpiMeta;
import com.choodon.rpc.base.protocol.RPCRequest;
import com.choodon.rpc.base.protocol.RPCResponse;
import com.choodon.rpc.base.protocol.Response;
import com.choodon.rpc.transport.netty.handler.Http1ClientChannelInitializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

@SpiMeta(name = RPCConstants.NETTY_HTTP)
public class HttpNettyClient extends AbstractNettyClient {

    @Override
    public void startup() {
//        bootstrap.option(ChannelOption.TCP_NODELAY, true);
//        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
//        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);
        bootstrap.handler(new Http1ClientChannelInitializer());
        int channelNum = mergeURL.getIntParameter(URLParamType.channelNum.getName(), URLParamType.channelNum.getIntValue());
        Channel channel = null;
        for (int i = 0; i < channelNum; i++) {
            channel = connect();
            if (null != channel && channel.isActive()) {
                channels.add(channel);
            }
        }
    }

    @Override
    public RPCResponse send4SyncTypeCall(RPCRequest request) throws Exception {
        Channel channel = selectChannel();
        RPCContext.setRequest(request);
        ByteBuf byteBuf = Unpooled.copiedBuffer(request.getBytes());
        DefaultFullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/", Unpooled.copiedBuffer(request.getBytes()));
        httpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpRequest.content().readableBytes());
        httpRequest.headers().set(RPCConstants.PROTOCOL_LENGTH, httpRequest.content().readableBytes());
        httpRequest.headers().set(RPCConstants.ID, request.getId());
        httpRequest.headers().set(RPCConstants.MESSAGE_TYPE, RPCConstants.MESSAGE_TYPE_REQUEST);
        httpRequest.headers().set(RPCConstants.SERIALIZER, request.getSerializer());
        channel.writeAndFlush(httpRequest);
        Response response = RPCContext.syncGet();
        RPCContext.removeRequest();
        return (RPCResponse) response;
    }

    @Override
    public RPCFuture send4FutureTypeCall(RPCRequest request) throws Exception {
        Channel channel = selectChannel();
        RPCContext.setRequest(request);
        DefaultFullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/", Unpooled.copiedBuffer(request.getBytes()));
        httpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpRequest.content().readableBytes());
        httpRequest.headers().set(RPCConstants.PROTOCOL_LENGTH, httpRequest.content().readableBytes());
        httpRequest.headers().set(RPCConstants.ID, request.getId());
        httpRequest.headers().set(RPCConstants.MESSAGE_TYPE, RPCConstants.MESSAGE_TYPE_REQUEST);
        httpRequest.headers().set(RPCConstants.SERIALIZER, request.getSerializer());
        channel.writeAndFlush(httpRequest);
        return RPCFuture.create();
    }

    @Override
    public void send4CallbackTypeCall(RPCRequest request, RPCCallback callback) throws Exception {
        Channel channel = selectChannel();
        RPCContext.setRequest(request);
        DefaultFullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/", Unpooled.copiedBuffer(request.getBytes()));
        httpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpRequest.content().readableBytes());
        httpRequest.headers().set(RPCConstants.PROTOCOL_LENGTH, httpRequest.content().readableBytes());
        httpRequest.headers().set(RPCConstants.ID, request.getId());
        httpRequest.headers().set(RPCConstants.MESSAGE_TYPE, RPCConstants.MESSAGE_TYPE_REQUEST);
        httpRequest.headers().set(RPCConstants.SERIALIZER, request.getSerializer());
        channel.writeAndFlush(httpRequest);
        RPCCallback.callbackContainer.put(request.getId(), callback);
    }
}
