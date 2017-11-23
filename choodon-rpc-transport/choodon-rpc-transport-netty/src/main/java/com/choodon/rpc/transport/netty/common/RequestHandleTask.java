package com.choodon.rpc.transport.netty.common;

import com.choodon.rpc.base.common.DataArea;
import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.base.extension.ExtensionLoader;
import com.choodon.rpc.base.log.LoggerUtil;
import com.choodon.rpc.base.protocol.*;
import com.choodon.rpc.base.serialization.Serializer;
import com.choodon.rpc.base.service.ServiceHandlerManager;
import com.choodon.rpc.base.util.NetUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

public class RequestHandleTask implements RejectedRunnable {
    private Request request;
    private ChannelHandlerContext channelHandlerContext;

    public RequestHandleTask(Request request, ChannelHandlerContext ctx) {
        this.request = request;
        this.channelHandlerContext = ctx;
    }

    @Override
    public void run() {
        if (request instanceof HeartBeatPing) {
            String client_Server = NetUtil.getHostAndPortStr(channelHandlerContext.channel().remoteAddress()) + "-->" + NetUtil.getHostAndPortStr(channelHandlerContext.channel().localAddress());
            LoggerUtil.info(client_Server + " receive  heartbeat ping");
            write(request, new HeartBeatPong(((HeartBeatPing) request).getId()));
            LoggerUtil.info(client_Server + " send  heartbeat pong");
            return;
        }
        String serializationType = request.getSerializer();
        Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(serializationType);
        DataArea dataArea = serializer.readObject(request.getBytes(), DataArea.class);
        String handlerId = dataArea.getHandlerId();
        Object data = ServiceHandlerManager.get(handlerId).doHandler(dataArea.getArgs());
        dataArea.setArgs(new Object[]{data});
        RPCResponse response = new RPCResponse(request.getId());
        response.addParameters(dataArea.getHeader());
        response.setSerializer(serializationType);
        response.setBytes(serializer.writeObject(dataArea));
        response.setStatus((byte) 0);
        write(request, response);
    }

    private void write(Request request, Response response) {
        if (request.getParameterValue(URLParamType.transportProtocol.getName()).equalsIgnoreCase(RPCConstants.TCP)) {
            channelHandlerContext.writeAndFlush(response);
        } else if (request.getParameterValue(URLParamType.transportProtocol.getName()).equalsIgnoreCase(RPCConstants.HTTP)) {
            boolean keepAlive = request.getParameterBooleanValue(RPCConstants.HTTP_KEEP_ALIVE);
            FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(response.getBytes()));
            httpResponse.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());
            httpResponse.headers().setInt(RPCConstants.PROTOCOL_LENGTH, httpResponse.content().readableBytes());
            httpResponse.headers().set(RPCConstants.ID, response.getId());
            httpResponse.headers().set(RPCConstants.SERIALIZER, response.getSerializer());
            if (request.getParameterValue(RPCConstants.MESSAGE_TYPE).equalsIgnoreCase(RPCConstants.MESSAGE_TYPE_REQUEST)) {
                httpResponse.headers().set(RPCConstants.MESSAGE_TYPE, RPCConstants.MESSAGE_TYPE_RESPONSE);
            } else if (request.getParameterValue(RPCConstants.MESSAGE_TYPE).equalsIgnoreCase(RPCConstants.MESSAGE_TYPE_PING)) {
                httpResponse.headers().set(RPCConstants.MESSAGE_TYPE, RPCConstants.MESSAGE_TYPE_PONG);
            }
            if (!keepAlive) {
                channelHandlerContext.writeAndFlush(httpResponse).addListener(ChannelFutureListener.CLOSE);
            } else {
                httpResponse.headers().set(HttpHeaderNames.CONNECTION, "keep-alive");
                channelHandlerContext.writeAndFlush(httpResponse);
            }
        }
    }

    @Override
    public void rejected() {
        // TODO Auto-generated method stub

    }

}