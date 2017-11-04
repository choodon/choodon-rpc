package com.choodon.rpc.transport.netty.client;

import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.thread.NamedThreadFactory;
import com.choodon.rpc.base.util.NativeSupport;
import com.choodon.rpc.transport.api.TransportClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.HashedWheelTimer;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.SystemPropertyUtil;

import java.util.concurrent.ThreadFactory;

public abstract class AbstractNettyClient implements TransportClient {
    protected URL mergeURL;
    protected Bootstrap bootstrap;
    protected static EventLoopGroup group;
    protected final HashedWheelTimer timer = new HashedWheelTimer(new NamedThreadFactory("client.timer"));

    @Override
    public void init(URL mergeURL) {
        this.mergeURL = mergeURL;
        ThreadFactory workerFactory = new DefaultThreadFactory("rpc.client.worker", Thread.MAX_PRIORITY);
        group = initEventLoopGroup(Math.max(1, SystemPropertyUtil.getInt(
                "io.netty.eventLoopThreads", Runtime.getRuntime().availableProcessors() * 2)),
                workerFactory);
        bootstrap = new Bootstrap().group(group);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
    }


    private EventLoopGroup initEventLoopGroup(int nThreads, ThreadFactory tFactory) {
        return isNativeEt() ? new EpollEventLoopGroup(nThreads, tFactory) : new NioEventLoopGroup(nThreads, tFactory);
    }

    protected boolean isNativeEt() {
        return NativeSupport.isSupportNativeET();
    }

}
