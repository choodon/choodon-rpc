package com.choodon.rpc.base.serialization;


import com.choodon.rpc.base.extension.Scope;
import com.choodon.rpc.base.extension.Spi;

@Spi(scope = Scope.SINGLETON)
public interface Serializer {

	<T> byte[] writeObject(T obj);

	<T> T readObject(byte[] bytes, Class<T> clazz);
}
