package com.choodon.rpc.transport.netty.server;

import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.base.exception.RPCFrameworkException;
import com.choodon.rpc.base.extension.ExtensionLoader;
import com.choodon.rpc.base.extension.SpiMeta;
import com.choodon.rpc.transport.api.TransportServer;
import com.choodon.rpc.transport.api.TransportServerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SpiMeta(name = RPCConstants.NETTY)
public class NettyServerFactory implements TransportServerFactory {
    private final Map<URL, TransportServer> serverContainer = new ConcurrentHashMap<>();

    @Override
    public TransportServer createServer(URL protocolURL) {
        if (serverContainer.containsKey(protocolURL)) {
            return serverContainer.get(protocolURL);
        } else {
            String transportProtocol = protocolURL.getParameter(URLParamType.transportProtocol.getName(), URLParamType.transportProtocol.getValue());
            if (transportProtocol.equalsIgnoreCase(RPCConstants.TCP)) {
                TransportServer server = ExtensionLoader.getExtensionLoader(TransportServer.class)
                        .getExtension(RPCConstants.NETTY_TCP);
                server.init(protocolURL);
                serverContainer.put(protocolURL, server);
                return server;
            } else if (transportProtocol.equalsIgnoreCase(RPCConstants.HTTP)) {

                TransportServer server = ExtensionLoader.getExtensionLoader(TransportServer.class)
                        .getExtension(RPCConstants.NETTY_TCP);
                server.init(protocolURL);
                serverContainer.put(protocolURL, server);
                return server;
            } else {
                throw new RPCFrameworkException("Error transport protocol type.");
            }

        }
    }

}
