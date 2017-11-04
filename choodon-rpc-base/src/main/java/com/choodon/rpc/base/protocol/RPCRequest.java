package com.choodon.rpc.base.protocol;

import com.choodon.rpc.base.common.RPCConstants;

public class RPCRequest extends Request {

    public RPCRequest(long id) {
        headers.put(RPCConstants.ID, String.valueOf(id));
    }

    public RPCRequest() {
        headers.put(RPCConstants.ID, String.valueOf(id.getAndIncrement()));
    }

}