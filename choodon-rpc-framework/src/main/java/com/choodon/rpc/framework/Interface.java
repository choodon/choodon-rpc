package com.choodon.rpc.framework;

import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.framework.cluster.Cluster;
import com.choodon.rpc.framework.cluster.ClusterManager;
import com.choodon.rpc.framework.proxy.MultiProtocolMultiRegistryInvocationHandler;
import com.choodon.rpc.framework.proxy.SingleProtocolSingleRegistryInvocationHandler;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Interface {
    
    public static <T> T getRef(URL protocolurl, URL registryURL, URL interfaceURL, Class refInterface) {
        Cluster cluster = ClusterManager.createCluster(interfaceURL, protocolurl, registryURL);
        T ref = (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{refInterface}, new SingleProtocolSingleRegistryInvocationHandler(cluster));
        return ref;
    }

    public static <T> T getRef(List<URL> protocolURLs, List<URL> registryURLs,
            URL interfaceURL, Class refInterface) {
        CopyOnWriteArrayList clusters = new CopyOnWriteArrayList();
        Cluster cluster;
        if(interfaceURL==null){
            interfaceURL=URL.valueOf("choodon://0.0.0.0:0000");
            interfaceURL.setPath(refInterface.getName());
        }
        for (URL protocolURL : protocolURLs) {
            for (URL registryURL : registryURLs) {
                cluster = ClusterManager.createCluster(interfaceURL, protocolURL, registryURL);
                clusters.add(cluster);
            }
        }
        T ref = (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{refInterface}, new MultiProtocolMultiRegistryInvocationHandler(clusters));
        return ref;
    }

}
