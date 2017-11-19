package com.choodon.rpc.framework.cluster;

import com.choodon.rpc.base.RPCCallback;
import com.choodon.rpc.base.RPCFuture;
import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.extension.Scope;
import com.choodon.rpc.base.extension.Spi;
import com.choodon.rpc.base.protocol.RPCRequest;
import com.choodon.rpc.base.protocol.RPCResponse;
import com.choodon.rpc.registry.api.NotifyListener;

@Spi(scope = Scope.PROTOTYPE)
public interface Cluster extends NotifyListener {
    void init(URL interfaceURL, URL protocolURL, URL registryURL);

    URL getMergerURL();

    boolean isAvailable();

    RPCResponse syncCall(RPCRequest request) throws Exception;

    RPCFuture asyncCall(RPCRequest request) throws Exception;

    void callback(RPCRequest request, RPCCallback callBack) throws Exception;
}
