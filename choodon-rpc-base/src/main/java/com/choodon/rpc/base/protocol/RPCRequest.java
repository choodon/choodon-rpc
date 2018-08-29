package com.choodon.rpc.base.protocol;

import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.log.LoggerUtil;

public class RPCRequest extends Holder {

    public RPCRequest(long id) {
        headers.put(RPCConstants.ID, String.valueOf(id));
    }

    public RPCRequest(String id) {
        headers.put(RPCConstants.ID, id);
    }

}