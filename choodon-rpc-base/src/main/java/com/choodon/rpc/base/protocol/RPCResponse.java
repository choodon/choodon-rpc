package com.choodon.rpc.base.protocol;

import com.choodon.rpc.base.common.RPCConstants;

public class RPCResponse extends Response {
    public RPCResponse(long id) {
        headers.put(RPCConstants.ID, String.valueOf(id));
    }

    private byte status;

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

}