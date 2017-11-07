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
    private CopyOnWriteArrayList<Channel> channels = new CopyOnWriteArrayList<>();

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

    private Channel connect() {
        Channel channel = null;
        try {
            channel = bootstrap.connect(mergeURL.getHost(), mergeURL.getPort()).sync().channel();
            LoggerUtil.info(NetUtil.getHostAndPortStr(channel.localAddress()) + " successfully connected to " + mergeURL.getHostPortStr());
        } catch (Exception e) {
            LoggerUtil.error("connect " + mergeURL.getHostPortStr() + " exception", e);
        }
        return channel;
    }


    public RPCResponse send4SyncTypeCall(RPCRequest request) throws Exception {
        Channel channel = selectChannel();
        RPCContext.setRequest(request);
        channel.writeAndFlush(request);
        Response response = RPCContext.syncGet();
        RPCContext.removeRequest();
        return (RPCResponse) response;
    }

    public RPCFuture send4FutureTypeCall(RPCRequest request) throws Exception {
        Channel channel = selectChannel();
        RPCContext.setRequest(request);
        channel.writeAndFlush(request);
        return RPCFuture.create();
    }

    public void send4CallbackTypeCall(RPCRequest request, RPCCallback callback) throws Exception {
        Channel channel = selectChannel();
        RPCContext.setRequest(request);
        channel.writeAndFlush(request);
        RPCCallback.callbackContainer.put(request.getId(), callback);
    }

    private Channel selectChannel() throws Exception {
        int random = 0;
        Channel channel = null;
        while (channels.size() > 0) {
            random = ThreadLocalRandom.current().nextInt(0, channels.size());
            channel = channels.get(random);
            if (channel.isActive()) {
                return channel;
            } else if (!channel.isOpen()) {
                channel = channels.remove(random);
                if (channel.isOpen()) {
                    channel.close();
                }
            }

        }
        LoggerUtil.error("NO active channel connected to " + mergeURL.getHostPortStr());
        throw new RPCFrameworkException("NO active channel connected to " + mergeURL.getHostPortStr());
    }

    @Override
    public void shutdwon() {
        for (Channel channel : channels) {
            channel.close().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        LoggerUtil.info(NetUtil.getHostAndPortStr(future.channel().localAddress()) + " disconnect to " + NetUtil.getHostAndPortStr(future.channel().remoteAddress()));
                    } else {
                        LoggerUtil.info(NetUtil.getHostAndPortStr(future.channel().localAddress()) + " disconnect to " + NetUtil.getHostAndPortStr(future.channel().remoteAddress()) + "  excption", future.cause());
                    }
                }
            });
        }
    }

}
