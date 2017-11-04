package com.choodon.rpc.base.protocol;

import com.choodon.rpc.base.common.URLParamType;

public class Response extends Holder {

    public String getSerializer() {
        return getParameterValue(URLParamType.serialize.getName(), URLParamType.serialize.getValue());
    }

    public void setSerializer(String serializer) {
        headers.put(URLParamType.serialize.getName(), serializer);
    }

}
