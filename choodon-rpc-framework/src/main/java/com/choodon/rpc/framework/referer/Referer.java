package com.choodon.rpc.framework.referer;


import com.choodon.rpc.base.RPCCallback;
import com.choodon.rpc.base.RPCFuture;
import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.base.extension.ExtensionLoader;
import com.choodon.rpc.base.protocol.RPCRequest;
import com.choodon.rpc.base.protocol.RPCResponse;
import com.choodon.rpc.transport.api.TransportClient;
import com.choodon.rpc.transport.api.TransportClientFactory;

public class Referer {

    private URL mergerURL;
    private TransportClient client;


    public Referer(URL mergerURL) {
        this.mergerURL = mergerURL;
        TransportClientFactory clientFactory = ExtensionLoader.getExtensionLoader(TransportClientFactory.class).getExtension(mergerURL.getParameter(URLParamType.transportTool.getName(), URLParamType.transportTool.getValue()));
        client = clientFactory.createClient(mergerURL);
    }

    public RPCResponse syncCall(RPCRequest request) throws Exception {
        return client.send4SyncTypeCall(request);
    }

    public RPCFuture asyncCall(RPCRequest request) throws Exception {
        return client.send4FutureTypeCall(request);
    }

    public void callback(RPCRequest request, RPCCallback callBack) throws Exception {
        client.send4CallbackTypeCall(request, callBack);

    }
}
