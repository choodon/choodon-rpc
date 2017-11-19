package com.choodon.rpc.framework.cluster.support;

import com.choodon.rpc.base.RPCCallback;
import com.choodon.rpc.base.RPCFuture;
import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.base.extension.ExtensionLoader;
import com.choodon.rpc.base.extension.SpiMeta;
import com.choodon.rpc.base.log.LoggerUtil;
import com.choodon.rpc.base.protocol.RPCRequest;
import com.choodon.rpc.base.protocol.RPCResponse;
import com.choodon.rpc.base.util.URLTools;
import com.choodon.rpc.framework.cluster.ha.HaStrategy;
import com.choodon.rpc.framework.cluster.loadbalance.LoadBalance;
import com.choodon.rpc.framework.referer.Referer;
import com.choodon.rpc.framework.referer.RefererManager;
import com.choodon.rpc.registry.api.EventTypeEnum;
import com.choodon.rpc.registry.api.RegistryFactory;
import com.choodon.rpc.registry.api.RegistryService;

@SpiMeta(name = RPCConstants.DEFAULT)
public class DefaultCluster extends AbstractCluster {

    @Override
    public RPCResponse syncCall(RPCRequest request) throws Exception {
        if (referers.size() == 0) {
            LoggerUtil.warn(mergerURL.getParameter(RPCConstants.CLUSTER_KEY) + " has no providers.");
            return null;
        }
        return haStrategy.syncCall(request, referers, loadBalance);
    }

    @Override
    public RPCFuture asyncCall(RPCRequest request) throws Exception {
        if (referers.size() == 0) {
            LoggerUtil.warn(mergerURL.getParameter(RPCConstants.CLUSTER_KEY) + " has no providers.");
            return null;
        }
        return haStrategy.asyncCall(request, referers, loadBalance);
    }

    @Override
    public void callback(RPCRequest request, RPCCallback callBack) throws Exception {
        if (referers.size() == 0) {
            LoggerUtil.warn(mergerURL.getParameter(RPCConstants.CLUSTER_KEY) + " has no providers.");
            return;
        }
        haStrategy.callback(request, callBack, referers, loadBalance);
    }

    @Override
    public void notify(URL url, EventTypeEnum envetType) {
        switch (envetType) {
            case ADD:
                addNotify(url);
                break;
            case UPDATE:
                updateNotify(url);
                break;
            case REMOVE:
                removeNotify(url);
                break;
        }

    }

    private void addNotify(URL url) {
        String catagray = url.getParameter(RPCConstants.CATAGRAY);
        switch (catagray) {
            case RPCConstants.PROVIDER_CATAGRAY:
                Referer referer = RefererManager.create(url);
                referers.add(referer);
            default:
                LoggerUtil.error("unknown catagray,Notify URL: " + url);
        }
    }

    private void updateNotify(URL url) {
        String catagray = url.getParameter(RPCConstants.CATAGRAY);
        switch (catagray) {
            case RPCConstants.PROVIDER_CATAGRAY:
            default:
                LoggerUtil.error("unknown catagray,Notify URL: " + url);
        }
    }

    private void removeNotify(URL url) {
        String catagray = url.getParameter(RPCConstants.CATAGRAY);
        switch (catagray) {
            case RPCConstants.PROVIDER_CATAGRAY:
                Referer referer = RefererManager.create(url);
                referers.remove(referer);
            default:
                LoggerUtil.error("unknown catagray,Notify URL: " + url);
        }
    }


    @Override
    public void init(URL interfaceURL, URL protocolURL, URL registryURL) {
        mergerURL = URL.copy(interfaceURL);
        mergerURL.addParameter(RPCConstants.CLUSTER_KEY, URLTools.getClusterKey(interfaceURL, protocolURL, registryURL));
        mergerURL.addParameters(protocolURL.getParameters());
        String haStrategyName = interfaceURL.getParameter(URLParamType.haStrategy.getName(), URLParamType.haStrategy.getValue());
        haStrategy = ExtensionLoader.getExtensionLoader(HaStrategy.class).getExtension(haStrategyName);
        String loadBalanceName = interfaceURL.getParameter(URLParamType.loadBalance.getName(), URLParamType.loadBalance.getValue());
        loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(loadBalanceName);
        RegistryFactory registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getExtension(registryURL.getProtocol());
        RegistryService registryService = registryFactory.getRegistryService(registryURL);
        registryService.subscribe(URLTools.buildRegistryConsumerURL(protocolURL, interfaceURL), this);
    }
}
