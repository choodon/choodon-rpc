package com.choodon.rpc.transport.netty.common;

import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.base.enums.StatusEnum;
import com.choodon.rpc.base.exception.RPCFrameworkException;
import com.choodon.rpc.base.extension.ExtensionLoader;
import com.choodon.rpc.base.log.LoggerUtil;
import com.choodon.rpc.base.protocol.RPCRequest;
import com.choodon.rpc.base.protocol.RPCResponse;
import com.choodon.rpc.base.serialization.Serializer;
import com.choodon.rpc.base.service.ServiceHandlerManager;
import com.choodon.rpc.base.service.ServiceManager;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RPCRequestHandleTask implements RejectedRunnable {

    private RPCRequest rpcRequest;

    private ChannelHandlerContext channelHandlerContext;

    public RPCRequestHandleTask(RPCRequest rpcRequest, ChannelHandlerContext channelHandlerContext) {
        this.rpcRequest = rpcRequest;
        this.channelHandlerContext = channelHandlerContext;
    }

    @Override
    public void rejected() {
        RPCResponse rpcResponse = new RPCResponse(rpcRequest.getId());
        rpcResponse.addParameter(URLParamType.status.getName(), StatusEnum.BUSY.getCode());
        channelHandlerContext.writeAndFlush(rpcResponse);
    }

    @Override
    public void run() {
        RPCResponse rpcResponse = new RPCResponse(rpcRequest.getId());
        try {
            String serialization = rpcRequest.getParameterValue(URLParamType.serialize.getName());
            Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(serialization);
            Map<String, String> header = serializer.deserialize(rpcRequest.getHeaderBytes(), HashMap.class);
            String methodId = header.get(URLParamType.methodId.getName());
            Method method = ServiceManager.getMethod(methodId);
            Class returnType = method.getReturnType();
            boolean isVoid = Objects.equals(returnType, Void.TYPE);
            int paramCount = method.getParameterCount();
            Object value = null;
            switch (paramCount) {
                case 0: {
                    if (isVoid) {
                        ServiceHandlerManager.get(methodId).doVoidHandler();
                    } else {
                        value = ServiceHandlerManager.get(methodId).doHandler();
                    }
                    break;
                }
                case 1: {
                    Object parameter = serializer.deserialize(rpcRequest.getBodyBytes(), method.getParameterTypes()[0]);
                    if (isVoid) {
                        ServiceHandlerManager.get(methodId).doHandler(parameter);
                    } else {
                        value = ServiceHandlerManager.get(methodId).doHandler(parameter);
                    }
                    break;
                }
                default: {
                    Object[] parameters = serializer.deserializeMulti(rpcRequest.getBodyBytes(), method.getParameterTypes());
                    if (isVoid) {
                        ServiceHandlerManager.get(methodId).doHandler(parameters);
                    } else {
                        value = ServiceHandlerManager.get(methodId).doHandler(parameters);
                    }
                }

            }
            rpcResponse.setData(value);
        } catch (Exception e) {
            LoggerUtil.error("request:{} , exception:{}", rpcRequest.getId(), e.getMessage());
            rpcResponse.addParameter(URLParamType.status.getName(), StatusEnum.ERROR.getCode());
            rpcResponse.setData(new RPCFrameworkException("request:" + rpcRequest.getId() + " , exception:" + e.getMessage()));
        }
        channelHandlerContext.writeAndFlush(rpcResponse);

    }

}