package com.choodon.rpc.transport.netty.common;

import com.choodon.rpc.base.common.DataArea;
import com.choodon.rpc.base.extension.ExtensionLoader;
import com.choodon.rpc.base.protocol.RPCRequest;
import com.choodon.rpc.base.protocol.RPCResponse;
import com.choodon.rpc.base.serialization.Serializer;
import com.choodon.rpc.base.service.ServiceHandlerManager;
import io.netty.channel.ChannelHandlerContext;

public class RequestHandleTask implements RejectedRunnable {
    private RPCRequest request;
    private ChannelHandlerContext channelHandlerContext;

    public RequestHandleTask(RPCRequest request, ChannelHandlerContext ctx) {
        super();
        this.request = request;
        this.channelHandlerContext = ctx;
    }

    @Override
    public void run() {
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