package com.choodon.rpc.registry.api;

import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.extension.Scope;
import com.choodon.rpc.base.extension.Spi;

@Spi(scope = Scope.SINGLETON)
public interface RegistryFactory {
    RegistryService getRegistryService(URL registryURL);
}
