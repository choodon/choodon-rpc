package com.choodon.rpc.base.protocol;

import com.choodon.rpc.base.common.AtomicPositiveLong;
import com.choodon.rpc.base.common.URLParamType;

public class Request extends Holder {
    protected static AtomicPositiveLong id = new AtomicPositiveLong(0);


    public String getSerializer() {
        return getParameterValue(URLParamType.serialize.getName(), URLParamType.serialize.getValue());
    }

    public void setSerializer(String serializer) {
        headers.put(URLParamType.serialize.getName(), serializer);
    }
}
