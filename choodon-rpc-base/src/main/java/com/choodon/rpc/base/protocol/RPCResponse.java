package com.choodon.rpc.base.protocol;

import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.base.enums.StatusEnum;

public class RPCResponse extends Holder {
    public RPCResponse(String id) {
        headers.put(URLParamType.id.getName(), id);
        headers.put(URLParamType.status.getName(), StatusEnum.SUCCEESS.getCode().toString());
    }
}