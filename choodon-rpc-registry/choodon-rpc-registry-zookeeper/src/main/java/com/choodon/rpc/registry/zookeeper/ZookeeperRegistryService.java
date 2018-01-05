package com.choodon.rpc.registry.zookeeper;

import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.base.exception.ErrorMsgConstant;
import com.choodon.rpc.base.exception.RPCCheckException;
import com.choodon.rpc.base.extension.SpiMeta;
import com.choodon.rpc.registry.api.AbtractRegistryService;
import com.choodon.rpc.registry.api.EventTypeEnum;
import com.choodon.rpc.registry.api.NotifyListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryForever;
import org.apache.zookeeper.CreateMode;
import org.jboss.netty.util.internal.ConcurrentHashMap;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

@SpiMeta(name = RPCConstants.ZOOKEEPER)
public class ZookeeperRegistryService extends AbtractRegistryService {
    private CuratorFramework client = null;
    private Map<URL, PathChildrenCache> pathChildrenCacheContainer = new ConcurrentHashMap<>();

    @Override
    public void connnect() {
        Builder builder = CuratorFrameworkFactory.builder();
        builder.connectString(registryURL.getHostPortStr());
        builder.connectionTimeoutMs(registryURL.getIntParameter(URLParamType.registryTimeOut.getName(), URLParamType.registryTimeOut.getIntValue()));
        builder.sessionTimeoutMs(registryURL.getIntParameter(URLParamType.registrySessionTimeOut.getName(), URLParamType.registrySessionTimeOut.getIntValue()));
        builder.retryPolicy(new RetryForever(registryURL.getIntParameter(URLParamType.registryRetryInterval.getName(), URLParamType.registryRetryInterval.getIntValue())));
        builder.namespace("choodon");
        client = builder.build();
        client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                switch (newState) {
                    case CONNECTED:
                        break;
                    case SUSPENDED:
                        break;
                    case RECONNECTED:
                        recover();
                        break;
                    case LOST:
                        connnect();
                        break;
                    default:
                        break;
                }

            }
        });
        client.start();

    }

    private void recover() {
        reRegister();
        resubscribe();
    }

    private void reRegister() {
        for (URL url : new HashSet<>(registryURLs)) {
            register(url);
        }

    }

    private void resubscribe() {
        for (Entry<URL, NotifyListener> entry : listenerContianer.entrySet()) {
            subscribe(entry.getKey(), entry.getValue());
        }
    }

    @Override
    protected void destroy() throws IOException {
        for (PathChildrenCache pathChildrenCache : pathChildrenCacheContainer.values()) {
            pathChildrenCache.close();
        }
        client.close();

    }


    @Override
    protected void doRegister(URL url) throws Exception {
        String catagray = url.getParameter(RPCConstants.CATAGRAY);
        if (StringUtils.isBlank(catagray)) {
            throw new RPCCheckException("registry catagray is whitespace, empty (\"\") or null",
                    ErrorMsgConstant.PARAMETER_CHECK__EXCEPTION);
        }
        switch (catagray) {
            case RPCConstants.PROVIDER_CATAGRAY:
                String path = RPCConstants.SEPARATOR + url.getPath() +
                        RPCConstants.SEPARATOR + RPCConstants.PROVIDER_CATAGRAY + RPCConstants.SEPARATOR + url.getParameter(URLParamType.group.getName(), URLParamType.group.getValue()) +
                        RPCConstants.SEPARATOR + url.getParameter(URLParamType.version.getName(), URLParamType.version.getValue()) + RPCConstants.SEPARATOR
                        + url.getHostPortStr() + RPCConstants.SEPARATOR + url.getParameter(URLParamType.transportProtocol.getName(), URLParamType.transportProtocol.getValue());
                if (client.checkExists().forPath(path) == null) {
                    client.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
                    client.setData().forPath(path, url.toFullStr().getBytes(Charset.defaultCharset()));
                } else if (client.checkExists().forPath(path) != null) {
                    client.setData().forPath(path,
                            url.toFullStr().getBytes(Charset.defaultCharset()));
                }
                break;
            default:
                break;
        }

    }

    @Override
    protected void doUnRegister(URL url) throws Exception {
        String catagray = url.getParameter(RPCConstants.CATAGRAY);
        if (StringUtils.isBlank(catagray)) {
            throw new RPCCheckException("registry catagray is whitespace, empty (\"\") or null",
                    ErrorMsgConstant.PARAMETER_CHECK__EXCEPTION);
        }
        switch (catagray) {
            case RPCConstants.PROVIDER_CATAGRAY:
                String path = RPCConstants.SEPARATOR + url.getPath() +
                        RPCConstants.SEPARATOR + RPCConstants.PROVIDER_CATAGRAY + RPCConstants.SEPARATOR + url.getParameter(URLParamType.group.getName(), URLParamType.group.getValue()) +
                        RPCConstants.SEPARATOR + url.getParameter(URLParamType.version.getName(), URLParamType.version.getValue()) + RPCConstants.SEPARATOR
                        + url.getHostPortStr() + RPCConstants.SEPARATOR + url.getParameter(URLParamType.transportProtocol.getName(), URLParamType.transportProtocol.getValue());
                if (client.checkExists().forPath(path) == null) {
                    return;
                }
                client.delete().guaranteed().forPath(path);
                break;
            default:
                break;
        }
    }

    @Override
    protected void doSubscribe(final URL url, NotifyListener listener) throws Exception {
        String catagray = url.getParameter(RPCConstants.CATAGRAY);
        if (StringUtils.isBlank(catagray)) {
            throw new RPCCheckException("registry catagray is whitespace, empty (\"\") or null",
                    ErrorMsgConstant.PARAMETER_CHECK__EXCEPTION);
        }
        switch (catagray) {
            case RPCConstants.CONSUMER_CATAGRAY:
                String path = RPCConstants.SEPARATOR + url.getPath() +
                        RPCConstants.SEPARATOR + RPCConstants.PROVIDER_CATAGRAY + RPCConstants.SEPARATOR + url.getParameter(URLParamType.group.getName(), URLParamType.group.getValue()) +
                        RPCConstants.SEPARATOR + url.getParameter(URLParamType.version.getName(), URLParamType.version.getValue());
                if (client.checkExists().forPath(path) == null) {
                    return;
                }
                PathChildrenCache cache = pathChildrenCacheContainer.get(url);
                if (null != cache) {
                    return;
                }
                cache = new PathChildrenCache(client, path, false);
                pathChildrenCacheContainer.put(url, cache);
                cache.getListenable().addListener(new PathChildrenCacheListener() {
                    @Override
                    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                        switch (event.getType()) {
                            case CHILD_ADDED: {
                                URL registryURL = pareseURL(event);
                                ZookeeperRegistryService.this.notify(url, registryURL, EventTypeEnum.ADD);
                                break;
                            }

                            case CHILD_UPDATED: {
                                URL registryURL = pareseURL(event);
                                ZookeeperRegistryService.this.notify(url, registryURL, EventTypeEnum.UPDATE);
                                break;
                            }

                            case CHILD_REMOVED: {
                                URL registryURL = pareseURL(event);
                                ZookeeperRegistryService.this.notify(url, registryURL, EventTypeEnum.REMOVE);
                                break;
                            }
                            default:
                                break;
                        }
                    }
                });
                cache.start();
                break;

            default:
                break;
        }
    }

    @Override
    protected void doUnSubscribe(URL url, NotifyListener listener) throws IOException {
        pathChildrenCacheContainer.get(url).close();

    }

    @SuppressWarnings("unchecked")
    private URL pareseURL(PathChildrenCacheEvent event) throws Exception {
    	String path=event.getData().getPath();
        byte[] data = client.getData().forPath(path+RPCConstants.SEPARATOR+client.getChildren().forPath(path).get(0));
        return URL.valueOf(new String(data, Charset.defaultCharset()));
    }

}
