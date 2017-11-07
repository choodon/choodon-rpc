package com.choodon.rpc.transport.netty.client;

import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.base.exception.RPCFrameworkException;
import com.choodon.rpc.base.extension.ExtensionLoader;
import com.choodon.rpc.base.extension.SpiMeta;
import com.choodon.rpc.base.util.URLTools;
import com.choodon.rpc.transport.api.TransportClient;
import com.choodon.rpc.transport.api.TransportClientFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SpiMeta(name = RPCConstants.NETTY)
public class NettyClientFactory implements TransportClientFactory {
    private static final Map<String, TransportClient> clientContainer = new ConcurrentHashMap<>();

    @Override
    public TransportClient createClient(URL mergerURL) {
        String protocolKey = URLTools.getProtocolKey(mergerURL);
        if (clientContainer.containsKey(protocolKey)) {
            return clientContainer.get(protocolKey);
        } else {
            String transportProtocol = mergerURL.getParameter(URLParamType.transportProtocol.getName(), URLParamType.transportProtocol.getValue());
            if (transportProtocol.equalsIgnoreCase(RPCConstants.TCP)) {
                TransportClient client = ExtensionLoader.getExtensionLoader(TransportClient.class)
                        .getExtension(RPCConstants.NETTY_TCP);
                client = clientContainer.put(protocolKey, client);
                if (client == null) {
                    client = clientContainer.get(protocolKey);
                }
                client.init(mergerURL);
                client.startup();
                return clientContainer.get(protocolKey);
            } else if (transportProtocol.equalsIgnoreCase(RPCConstants.HTTP)) {

                TransportClient client = ExtensionLoader.getExtensionLoader(TransportClient.class)
                        .getExtension(RPCConstants.NETTY_TCP);
                client = clientContainer.put(protocolKey, client);
                if (client == null) {
                    client = clientContainer.get(protocolKey);
                }
                client.init(mergerURL);
                client.startup();
                return clientContainer.get(protocolKey);
            } else {
                throw new RPCFrameworkException("Error transport protocol type.");
            }

        }
    }

}
