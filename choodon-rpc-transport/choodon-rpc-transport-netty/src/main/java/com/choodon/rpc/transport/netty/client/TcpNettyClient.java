package com.choodon.rpc.transport.netty.client;

import com.choodon.rpc.base.RPCCallback;
import com.choodon.rpc.base.RPCContext;
import com.choodon.rpc.base.RPCFuture;
import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.base.extension.SpiMeta;
import com.choodon.rpc.base.protocol.HeartBeatPing;
import com.choodon.rpc.base.protocol.HeartBeatPong;
import com.choodon.rpc.base.protocol.RPCRequest;
import com.choodon.rpc.base.protocol.RPCResponse;
import com.choodon.rpc.transport.netty.handler.TcpClientChannelInitializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;

@SpiMeta(name = RPCConstants.NETTY_TCP)
public class TcpNettyClient extends AbstractNettyClient {


    @Override
    public void startup() {
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);
        bootstrap.handler(new TcpClientChannelInitializer());
        int channelNum = mergeURL.getIntParameter(URLParamType.channelNum.getName(), URLParamType.channelNum.getIntValue());
        Channel channel;
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
        channel.writeAndFlush(request);
        RPCResponse response = RPCContext.syncGet();
        RPCContext.removeRequest();
        return response;
    }

    @Override
    public HeartBeatPong send4SyncTypeCall(HeartBeatPing heartBeatPing) throws Exception {
        return null;
    }

    @Override
    public RPCFuture send4FutureTypeCall(RPCRequest request) throws Exception {
        Channel channel = selectChannel();
        RPCContext.setRequest(request);
        channel.writeAndFlush(request);
        return RPCFuture.create();
    }

    @Override
    public void send4CallbackTypeCall(RPCRequest request, RPCCallback callback) throws Exception {
        Channel channel = selectChannel();
        RPCContext.setRequest(request);
        channel.writeAndFlush(request);
        RPCCallback.callbackContainer.put(request.getId(), callback);
    }

}
