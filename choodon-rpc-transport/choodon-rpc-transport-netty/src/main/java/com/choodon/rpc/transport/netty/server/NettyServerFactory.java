package com.choodon.rpc.transport.netty.server;

import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.base.exception.RPCFrameworkException;
import com.choodon.rpc.base.extension.ExtensionLoader;
import com.choodon.rpc.base.extension.SpiMeta;
import com.choodon.rpc.base.util.URLTools;
import com.choodon.rpc.transport.api.TransportServer;
import com.choodon.rpc.transport.api.TransportServerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SpiMeta(name = RPCConstants.NETTY)
public class NettyServerFactory implements TransportServerFactory {
    private static final Map<String, TransportServer> serverContainer = new ConcurrentHashMap<>();

    @Override
    public TransportServer createServer(URL protocolURL) {
        String protocolKey = URLTools.getProtocolKey(protocolURL);
        if (serverContainer.containsKey(protocolKey)) {
            return serverContainer.get(protocolURL);
        } else {
            String transportProtocol = protocolURL.getParameter(URLParamType.transportProtocol.getName(), URLParamType.transportProtocol.getValue());
            if (transportProtocol.equalsIgnoreCase(RPCConstants.TCP)) {
                TransportServer server = ExtensionLoader.getExtensionLoader(TransportServer.class)
                        .getExtension(RPCConstants.NETTY_TCP);
                server = serverContainer.put(protocolKey, server);
                if (server == null) {
                    server = serverContainer.get(protocolKey);
                }
                server.init(protocolURL);
                server.startup();
                return server;
            } else if (transportProtocol.equalsIgnoreCase(RPCConstants.HTTP)) {

                TransportServer server = ExtensionLoader.getExtensionLoader(TransportServer.class)
                        .getExtension(RPCConstants.NETTY_TCP);
                server = serverContainer.put(protocolKey, server);
                if (server == null) {
                    server = serverContainer.get(protocolKey);
                }
                server.init(protocolURL);
                server.startup();
                return server;
            } else {
                throw new RPCFrameworkException("Error transport protocol type.");
            }

        }
    }

}
