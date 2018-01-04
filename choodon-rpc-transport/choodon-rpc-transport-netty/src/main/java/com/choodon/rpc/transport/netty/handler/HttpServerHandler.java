package com.choodon.rpc.transport.netty.handler;

import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.base.protocol.HeartBeatPing;
import com.choodon.rpc.base.protocol.RPCRequest;
import com.choodon.rpc.base.protocol.Request;
import com.choodon.rpc.transport.netty.common.RequestHandleTask;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

@ChannelHandler.Sharable
public class HttpServerHandler extends AbstractServerHandler {


    public HttpServerHandler(URL protocolURL) {
        super(protocolURL);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest httpRequest = (FullHttpRequest) msg;
            HttpHeaders httpHeaders = httpRequest.headers();
            String id = httpHeaders.get(RPCConstants.ID);
            String type = httpHeaders.get(RPCConstants.MESSAGE_TYPE);
            String serializationName = httpHeaders.get(RPCConstants.SERIALIZER);
            String protocolLength = httpHeaders.get(RPCConstants.PROTOCOL_LENGTH);
            Boolean keepAlive = HttpUtil.isKeepAlive(httpRequest);
            ByteBuf byteBuf = httpRequest.content();
            int length = byteBuf.readableBytes();
            Request request = null;
            if (type.equalsIgnoreCase(RPCConstants.MESSAGE_TYPE_REQUEST)) {
                request = new RPCRequest(Long.parseLong(id));
                request.setSerializer(serializationName);
                request.addParameter(URLParamType.transportProtocol.getName(), RPCConstants.HTTP);
                request.addParameter(RPCConstants.MESSAGE_TYPE, RPCConstants.MESSAGE_TYPE_REQUEST);
                request.addParameter(RPCConstants.HTTP_KEEP_ALIVE, keepAlive.toString());
                byte[] content = new byte[length];
                byteBuf.readBytes(content);
                request.setBytes(content);
            } else if (type.equalsIgnoreCase(RPCConstants.MESSAGE_TYPE_PING)) {
                request = new HeartBeatPing(Long.parseLong(id));
                request.addParameter(RPCConstants.HTTP_KEEP_ALIVE, keepAlive.toString());
                request.addParameter(URLParamType.transportProtocol.getName(), RPCConstants.HTTP);
                request.addParameter(RPCConstants.MESSAGE_TYPE, RPCConstants.MESSAGE_TYPE_PING);
            }
            if (length == Integer.parseInt(protocolLength)) {
                RequestHandleTask task = new RequestHandleTask(request, ctx);
                if (null == executorService) {
                    new Thread(task).start();
                } else {
                    executorService.execute(task);
                }
            } else {
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST, Unpooled.copiedBuffer("欢迎光临", CharsetUtil.UTF_8));
                response.headers().set("Content-Type", "text/html;charset=utf-8");
                response.headers().setInt("Content-Length", response.content().readableBytes());
                response.headers().set("message", "PROTOCOL LENGTH ERROR");
                if (!keepAlive) {
                    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    response.headers().set("Connection", "keep-alive");
                    ctx.write(response);
                }
            }
        }
        ReferenceCountUtil.safeRelease(msg);
    }

}
