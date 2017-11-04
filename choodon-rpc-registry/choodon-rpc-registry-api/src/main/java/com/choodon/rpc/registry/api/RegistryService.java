package com.choodon.rpc.registry.api;

import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.extension.Scope;
import com.choodon.rpc.base.extension.Spi;

import java.util.List;

@Spi(scope = Scope.PROTOTYPE)
public interface RegistryService {

    void init(URL url);

    void connnect();

    void register(URL url);

    void unregister(URL url);

    void subscribe(URL url, NotifyListener listener);

    void unSubscribe(URL url, NotifyListener listener);

    List<URL> lookup(URL url);

    boolean isShutdown();

    void shutdownGracefully();

}
