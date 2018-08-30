package com.choodon.rpc.base.serialization.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.extension.SpiMeta;
import com.choodon.rpc.base.serialization.Serializer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

@SpiMeta(name = RPCConstants.DEFAULT_SERIALIZATION)
public class FastjsonSerializer implements Serializer {


    @Override
    public byte[] serialize(Object data) throws IOException {
        SerializeWriter out = new SerializeWriter();
        JSONSerializer serializer = new JSONSerializer(out);
        serializer.config(SerializerFeature.WriteEnumUsingToString, true);
        serializer.config(SerializerFeature.WriteClassName, true);
        serializer.write(data);
        return out.toBytes(Charset.defaultCharset());
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws IOException {
        return JSON.parseObject(new String(data), clazz);
    }

    @Override
    public byte[] serializeMulti(Object[] data) throws IOException {
        return serialize(data);
    }

    @Override
    public Object[] deserializeMulti(byte[] data, Class<?>[] classes) throws IOException {
        List<Object> list = JSON.parseArray(new String(data), classes);
        if (list != null) {
            return list.toArray();
        }
        return null;
    }

}
