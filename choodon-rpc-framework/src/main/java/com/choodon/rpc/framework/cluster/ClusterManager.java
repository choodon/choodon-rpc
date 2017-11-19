package com.choodon.rpc.framework.cluster;

import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.base.extension.ExtensionLoader;
import com.choodon.rpc.base.util.URLTools;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ClusterManager {
    private static Map<String, Cluster> clusterContainer = new ConcurrentHashMap();
    private static final ReentrantReadWriteLock clusterLock = new ReentrantReadWriteLock();

    public static Cluster createCluster(URL interfaceURL, URL protocolURL, URL registryURL) {
        String clusterKey = URLTools.getClusterKey(interfaceURL, protocolURL, registryURL);
        if (!clusterContainer.containsKey(clusterKey)) {
            clusterLock.writeLock().lock();
            if (!clusterContainer.containsKey(clusterKey)) {
                String clusterName = interfaceURL.getParameter(URLParamType.cluster.getName(), URLParamType.cluster.getValue());
                Cluster cluster = ExtensionLoader.getExtensionLoader(Cluster.class).getExtension(clusterName);
                cluster.init(interfaceURL, protocolURL, registryURL);
                clusterContainer.put(clusterKey, cluster);
            }
            clusterLock.writeLock().unlock();
        }
        return clusterContainer.get(clusterKey);
    }
}
