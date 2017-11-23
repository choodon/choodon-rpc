package com.choodon.rpc.transport.netty.client;

import com.choodon.rpc.base.RPCCallback;
import com.choodon.rpc.base.RPCContext;
import com.choodon.rpc.base.RPCFuture;
import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.base.exception.RPCFrameworkException;
import com.choodon.rpc.base.extension.SpiMeta;
import com.choodon.rpc.base.log.LoggerUtil;
import com.choodon.rpc.base.protocol.RPCRequest;
import com.choodon.rpc.base.protocol.RPCResponse;
import com.choodon.rpc.base.protocol.Response;
import com.choodon.rpc.base.util.NetUtil;
import com.choodon.rpc.transport.netty.handler.TcpClientChannelInitializer;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

@SpiMeta(name = RPCConstants.NETTY_TCP)
public class TcpNettyClient extends AbstractNettyClient {


    @Override
    public void startup() {
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);
        bootstrap.handler(new TcpClientChannelInitializer());
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
        channel.writeAndFlush(request);
        Response response = RPCContext.syncGet();
        RPCContext.removeRequest();
        return (RPCResponse) response;
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
