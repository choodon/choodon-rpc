package com.choodon.rpc.transport.netty.client;

import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.exception.RPCFrameworkException;
import com.choodon.rpc.base.log.LoggerUtil;
import com.choodon.rpc.base.util.NetUtil;
import com.choodon.rpc.base.util.SystemUtil;
import com.choodon.rpc.transport.api.TransportClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractNettyClient implements TransportClient {
    protected URL mergeURL;
    protected Bootstrap bootstrap;
    protected static EventLoopGroup group;
    protected CopyOnWriteArrayList<Channel> channels = new CopyOnWriteArrayList<>();

    @Override
    public void init(URL mergeURL) {
        this.mergeURL = mergeURL;
        group = new NioEventLoopGroup(SystemUtil.getProcessorCoreSize() * 2 + 1);
        bootstrap = new Bootstrap().group(group);
        bootstrap.channel(NioSocketChannel.class);
    }



    protected Channel connect() {
        Channel channel = null;
        try {
            channel = bootstrap.connect(mergeURL.getHost(), mergeURL.getPort()).sync().channel();
            LoggerUtil.info(NetUtil.getHostAndPortStr(channel.localAddress()) + " successfully connected to " + mergeURL.getHostPortStr());
        } catch (Exception e) {
            LoggerUtil.error("connect " + mergeURL.getHostPortStr() + " exception", e);
        }
        return channel;
    }

    protected Channel selectChannel() throws Exception {
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
    public void shutdown() {
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
