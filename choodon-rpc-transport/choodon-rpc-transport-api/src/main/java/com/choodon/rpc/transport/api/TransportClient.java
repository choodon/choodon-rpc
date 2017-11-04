package com.choodon.rpc.transport.api;

import com.choodon.rpc.base.RPCCallback;
import com.choodon.rpc.base.RPCFuture;
import com.choodon.rpc.base.extension.Scope;
import com.choodon.rpc.base.extension.Spi;
import com.choodon.rpc.base.protocol.RPCRequest;
import com.choodon.rpc.base.protocol.RPCResponse;

@Spi(scope = Scope.PROTOTYPE)
public interface TransportClient extends TransportService {

    RPCResponse send4SyncTypeCall(RPCRequest request) throws Exception;

    RPCFuture send4FutureTypeCall(RPCRequest request) throws Exception;

    void send4CallbackTypeCall(RPCRequest request, RPCCallback callback) throws Exception;

}
