package com.choodon.rpc.base.serialization.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.extension.SpiMeta;
import com.choodon.rpc.base.serialization.Serializer;

@SpiMeta(name = RPCConstants.DEFAULT_SERIALIZATION)
public class FastjsonSerializer implements Serializer {

	@Override
	public <T> byte[] writeObject(T obj) {
		return JSON.toJSONBytes(obj, SerializerFeature.SortField);
	}

	@Override
	public <T> T readObject(byte[] bytes, Class<T> clazz) {
		return JSON.parseObject(bytes, clazz, Feature.SortFeidFastMatch);
	}

}
