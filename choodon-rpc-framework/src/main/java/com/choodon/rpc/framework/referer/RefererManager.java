package com.choodon.rpc.framework.referer;

import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.util.URLTools;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RefererManager {
    private static final Map<String, Referer> refererContainer = new ConcurrentHashMap<>();
    private static final ReentrantReadWriteLock clusterLock = new ReentrantReadWriteLock();

    public static Referer create(URL mergerURL) {
        String refererKey = URLTools.getRefererKey(mergerURL);
        if (!refererContainer.containsKey(refererKey)) {
            clusterLock.writeLock().lock();
            if (!refererContainer.containsKey(refererKey)) {
                Referer referer = new Referer(mergerURL);
                refererContainer.put(refererKey, referer);
            }
            clusterLock.writeLock().unlock();
        }
        return refererContainer.get(refererKey);

    }
}
