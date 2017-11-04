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
        String clusterName = interfaceURL.getParameter(URLParamType.cluster.getName(),
                URLParamType.cluster.getValue());
        Cluster cluster = ClusterManager.createCluster(interfaceURL, protocolurl, registryURL);
        T ref = (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{refInterface}, new SingleProtocolSingleRegistryInvocationHandler(cluster));
        return ref;
    }

    public static <T> T getRef(List<URL> protocolurls, List<URL> registryURLs,
            URL interfaceURL, Class refInterface) {
        CopyOnWriteArrayList clusters = new CopyOnWriteArrayList();
        Cluster cluster = null;
        for (URL protocolurl : protocolurls) {
            for (URL registryURL : registryURLs) {
                cluster = ClusterManager.createCluster(interfaceURL, protocolurl, registryURL);
                clusters.add(cluster);
            }
        }
        T ref = (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{refInterface}, new MultiProtocolMultiRegistryInvocationHandler(clusters));
        return ref;
    }

}
