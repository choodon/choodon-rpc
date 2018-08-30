package com.choodon.rpc.transport.api;

import com.choodon.rpc.base.RPCCallback;
import com.choodon.rpc.base.RPCFuture;
import com.choodon.rpc.base.extension.Scope;
import com.choodon.rpc.base.extension.Spi;
import com.choodon.rpc.base.protocol.HeartBeatPing;
import com.choodon.rpc.base.protocol.HeartBeatPong;
import com.choodon.rpc.base.protocol.RPCRequest;
import com.choodon.rpc.base.protocol.RPCResponse;

@Spi(scope = Scope.PROTOTYPE)
public interface TransportClient extends TransportService {

    RPCResponse send4SyncTypeCall(RPCRequest request) throws Exception;

    HeartBeatPong send4SyncTypeCall(HeartBeatPing heartBeatPing) throws Exception;

    RPCFuture send4FutureTypeCall(RPCRequest request) throws Exception;

    void send4CallbackTypeCall(RPCRequest request, RPCCallback callback) throws Exception;

}
