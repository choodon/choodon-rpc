package com.choodon.rpc.registry.zookeeper;

import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.extension.ExtensionLoader;
import com.choodon.rpc.base.extension.SpiMeta;
import com.choodon.rpc.base.util.URLTools;
import com.choodon.rpc.registry.api.RegistryFactory;
import com.choodon.rpc.registry.api.RegistryService;

import java.util.concurrent.ConcurrentHashMap;

@SpiMeta(name = RPCConstants.ZOOKEEPER)
public class ZookeeperRegistryFactory implements RegistryFactory {
    private static final ConcurrentHashMap<String, RegistryService> registryServiceContainer = new ConcurrentHashMap<>();

    @Override
    public RegistryService getRegistryService(URL registryURL) {
        String registyKey = URLTools.getRegistryKey(registryURL);
        if (registryServiceContainer.containsKey(registyKey)) {
            return registryServiceContainer.get(registyKey);
        }
        RegistryService registryService = ExtensionLoader.getExtensionLoader(RegistryService.class).getExtension(registryURL.getProtocol());
        registryService.init(registryURL);
        registryService.connnect();
        registryServiceContainer.put(registyKey, registryService);
        return registryService;
    }

}
