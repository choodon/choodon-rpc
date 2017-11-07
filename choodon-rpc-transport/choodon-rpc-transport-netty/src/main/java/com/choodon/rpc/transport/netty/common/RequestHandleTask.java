package com.choodon.rpc.transport.netty.common;

import com.choodon.rpc.base.common.DataArea;
import com.choodon.rpc.base.extension.ExtensionLoader;
import com.choodon.rpc.base.log.LoggerUtil;
import com.choodon.rpc.base.protocol.*;
import com.choodon.rpc.base.serialization.Serializer;
import com.choodon.rpc.base.service.ServiceHandlerManager;
import com.choodon.rpc.base.util.NetUtil;
import io.netty.channel.ChannelHandlerContext;

public class RequestHandleTask implements RejectedRunnable {
    private Request request;
    private ChannelHandlerContext channelHandlerContext;

    public RequestHandleTask(Request request, ChannelHandlerContext ctx) {
        super();
        this.request = request;
        this.channelHandlerContext = ctx;
    }

    @Override
    public void run() {
        if (request instanceof HeartBeatPing) {
            String client_Server = NetUtil.getHostAndPortStr(channelHandlerContext.channel().remoteAddress()) + "-->" + NetUtil.getHostAndPortStr(channelHandlerContext.channel().localAddress());
            LoggerUtil.info(client_Server + " receive  heartbeat ping");
            channelHandlerContext.writeAndFlush(new HeartBeatPong(((HeartBeatPing) request).getId()));
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
        response.addParameter(dataArea.getHeader());
        response.setSerializer(serializationType);
        response.setBytes(serializer.writeObject(dataArea));
        response.setStatus((byte) 0);
        channelHandlerContext.writeAndFlush(response);
    }

    @Override
    public void rejected() {
        // TODO Auto-generated method stub

    }

}