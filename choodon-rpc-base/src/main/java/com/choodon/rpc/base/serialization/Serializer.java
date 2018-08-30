package com.choodon.rpc.base.serialization;


import com.choodon.rpc.base.extension.Scope;
import com.choodon.rpc.base.extension.Spi;

import java.io.IOException;

@Spi(scope = Scope.SINGLETON)
public interface Serializer {

    byte[] serialize(Object obj) throws IOException;

    <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException;

    byte[] serializeMulti(Object[] data) throws IOException;

    Object[] deserializeMulti(byte[] data, Class<?>[] classes) throws IOException;
}
