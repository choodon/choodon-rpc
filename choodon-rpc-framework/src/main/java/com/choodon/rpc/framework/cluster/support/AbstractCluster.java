package com.choodon.rpc.framework.cluster.support;

import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.framework.cluster.Cluster;
import com.choodon.rpc.framework.cluster.ha.HaStrategy;
import com.choodon.rpc.framework.cluster.loadbalance.LoadBalance;
import com.choodon.rpc.framework.referer.Referer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractCluster implements Cluster {

    protected URL mergerURL = null;
    protected List<Referer> referers = new CopyOnWriteArrayList<>();
    protected LoadBalance loadBalance = null;
    protected HaStrategy haStrategy = null;

    @Override
    public URL getMergerURL() {
        return mergerURL;
    }
}
