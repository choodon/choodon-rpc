package com.choodon.rpc.transport.netty.handler;

import com.choodon.rpc.base.RPCContext;
import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.base.exception.RPCTimeOutException;
import com.choodon.rpc.base.extension.ExtensionLoader;
import com.choodon.rpc.base.log.LoggerUtil;
import com.choodon.rpc.base.protocol.*;
import com.choodon.rpc.base.serialization.Serializer;
import com.choodon.rpc.base.util.NetUtil;
import com.google.common.util.concurrent.AtomicLongMap;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

@ChannelHandler.Sharable
public class ClientTCPHandler extends AbstractClientHandler {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof RPCResponse) {
            RPCContext.receviceResponse((Response) msg);
        } else if (msg instanceof HeartBeatPong) {
            String client_Server = NetUtil.getHostAndPortStr(ctx.channel().localAddress()) + "-->" + NetUtil.getHostAndPortStr(ctx.channel().remoteAddress());
            COUNTER_CONTAINER.getAndDecrement(client_Server);
            LoggerUtil.info(client_Server + " receive  heartbeat pong");
        } else {
            LoggerUtil.error(msg.getClass().getCanonicalName() + " is illegal response msg .");
        }
        ReferenceCountUtil.safeRelease(msg);

    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            HeartBeatPing request = new HeartBeatPing();
            RPCContext.setRequest(request);
            ctx.channel().writeAndFlush(request);
            String client_Server = NetUtil.getHostAndPortStr(ctx.channel().localAddress()) + "-->" + NetUtil.getHostAndPortStr(ctx.channel().remoteAddress());
            long counter = COUNTER_CONTAINER.getAndIncrement(client_Server);
            LoggerUtil.info(client_Server + " send  heartbeat ping");
            if (counter > 3) {
                ctx.channel().close().sync();
                LoggerUtil.error("Heartbeat Check failed 3 times,close " + client_Server + "channel. ");
            }


        }
    }

}
