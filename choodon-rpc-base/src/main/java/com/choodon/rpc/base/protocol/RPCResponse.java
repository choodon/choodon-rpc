package com.choodon.rpc.base.protocol;

import com.choodon.rpc.base.common.RPCConstants;

public class RPCResponse extends Holder {
    public RPCResponse(String id) {
        headers.put(RPCConstants.ID, id);
    }
}