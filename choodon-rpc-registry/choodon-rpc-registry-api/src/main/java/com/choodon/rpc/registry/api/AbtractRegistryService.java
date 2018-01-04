package com.choodon.rpc.registry.api;

import com.choodon.rpc.base.common.ConcurrentSet;
import com.choodon.rpc.base.common.Pair;
import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.log.LoggerUtil;
import com.choodon.rpc.base.thread.NamedThreadFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbtractRegistryService implements RegistryService {
    protected URL registryURL = null;

    /**
     * 注册成功的服务
     */
    protected final ConcurrentSet<URL> registryURLs = new ConcurrentSet<>();
    /**
     * 注册失败的服务
     */
    protected final LinkedBlockingQueue<URL> failedRegisterURLQueue = new LinkedBlockingQueue<>();
    /**
     * 注册失败后，注册执行器会对注册失败的服务进行持续注册，直到注册成功。
     */
    protected final ExecutorService registryExecutor = Executors
            .newSingleThreadExecutor(new NamedThreadFactory("registry.executor"));

    /**
     * 取消注册失败的服务
     */
    protected final LinkedBlockingQueue<URL> failedUnRegisterURLQueue = new LinkedBlockingQueue<>();
    /**
     * 取消注册失败后，取消注册执行器会对取消注册失败的服务进行持续取消注册，直到取消注册成功。
     */
    protected final ExecutorService unRegistryExecutor = Executors
            .newSingleThreadExecutor(new NamedThreadFactory("unRegistry.executor"));

    /**
     * 订阅失败的服务
     */
    protected final LinkedBlockingQueue<URL> failedSubscribeURLQueue = new LinkedBlockingQueue<>();
    /**
     * 订阅失败后，订阅执行器会对订阅失败的服务进行持续订阅，直到订阅成功。
     */
    protected final ExecutorService subscriberExecutor = Executors
            .newSingleThreadExecutor(new NamedThreadFactory("subscriber.executor"));

    /**
     * 取消订阅失败的服务
     */
    protected final LinkedBlockingQueue<URL> failedUnSubscribeURLQueue = new LinkedBlockingQueue<>();
    /**
     * 订阅失败后，订阅执行器会对订阅失败的服务进行持续订阅，直到订阅成功。
     */
    protected final ExecutorService unSubscriberExecutor = Executors
            .newSingleThreadExecutor(new NamedThreadFactory("unSubscriber.executor"));
    /**
     * 通知队列
     */
    protected final Map<URL, LinkedBlockingQueue<Pair<URL, EventTypeEnum>>> subscribeNotifyContainer = new ConcurrentHashMap<>();
    /**
     * 通知队列
     */
    protected final LinkedBlockingQueue<Pair<URL, EventTypeEnum>> notifyQueue = new LinkedBlockingQueue<>();
    /**
     * 通知失败后，通知执行器会对通知失败的服务进行持续订阅，直到通知成功。
     */
    protected final ExecutorService notifyExecutor = Executors
            .newCachedThreadPool(new NamedThreadFactory("notify.executor"));
    /**
     * 注册中心服务状态
     */
    protected final AtomicBoolean isShutdown = new AtomicBoolean(true);
    /**
     * 服务订阅者
     */
    protected final ConcurrentHashMap<URL, NotifyListener> listenerContianer = new ConcurrentHashMap<>();
    /**
     * 服务订阅者-服务提供者
     */
    protected final ConcurrentHashMap<URL, Set<URL>> registryURLContainer = new ConcurrentHashMap<>();

    protected AbtractRegistryService() {
        registryExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while (!isShutdown.get()) {
                    URL url = null;
                    try {
                        url = failedRegisterURLQueue.take();
                        doRegister(url);
                        registryURLs.add(url);
                        registryURLs.remove(url);
                    } catch (Throwable t) {
                        if (url != null) {
                            LoggerUtil.warn("Register [{}] fail: {}, will try again...", url, t);
                            failedRegisterURLQueue.add(url);
                        }
                    }
                }
            }
        });

        unRegistryExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while (!isShutdown.get()) {
                    URL URL = null;
                    try {
                        URL = failedUnRegisterURLQueue.take();
                        doUnRegister(URL);
                    } catch (Throwable t) {
                        if (URL != null) {
                            LoggerUtil.warn("UnRegister [{}] fail: {}, will try again...", URL, t);
                            failedUnRegisterURLQueue.add(URL);
                        }
                    }
                }
            }
        });
        subscriberExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while (!isShutdown.get()) {
                    URL URL = null;
                    try {
                        URL = failedSubscribeURLQueue.take();
                        doSubscribe(URL, listenerContianer.get(URL));
                    } catch (Throwable t) {
                        if (URL != null) {
                            LoggerUtil.warn("Subscribe [{}] fail: {}, will try again...", URL, t);
                            failedSubscribeURLQueue.add(URL);
                        }
                    }
                }
            }
        });
        unSubscriberExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while (!isShutdown.get()) {
                    URL URL = null;
                    try {
                        URL = failedUnSubscribeURLQueue.take();
                        doUnSubscribe(URL, listenerContianer.get(URL));
                    } catch (Throwable t) {
                        if (URL != null) {
                            LoggerUtil.warn("UnSubscribe [{}] fail: {}, will try again...", URL, t);
                            failedUnSubscribeURLQueue.add(URL);
                        }
                    }
                }
            }
        });

    }

    @Override
    public void register(URL URL) {
        try {
            doRegister(URL);
            registryURLs.add(URL);
        } catch (Exception e) {
            failedRegisterURLQueue.add(URL);
        }

    }

    @Override
    public void unregister(URL URL) {
        try {
            doUnRegister(URL);
            registryURLs.remove(URL);
        } catch (Exception e) {
            failedUnRegisterURLQueue.add(URL);
        }
    }

    @Override
    public void subscribe(URL URL, NotifyListener listener) {
        try {
            listenerContianer.put(URL, listener);
            doSubscribe(URL, listener);
        } catch (Exception e) {
        	LoggerUtil.error("subscribe failure",e);
            failedSubscribeURLQueue.add(URL);
        }
    }

    @Override
    public void unSubscribe(URL URL, NotifyListener listener) {
        try {
            doUnSubscribe(URL, listener);
        } catch (Exception e) {
            failedUnSubscribeURLQueue.add(URL);
        }

    }

    public void notify(final URL subscriberURL, URL registryURL, EventTypeEnum eventType) {
        if (subscribeNotifyContainer.containsKey(subscriberURL)) {
            subscribeNotifyContainer.get(subscriberURL).add(Pair.bulid(registryURL, eventType));
            try {
                doNotify(subscriberURL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            LinkedBlockingQueue<Pair<URL, EventTypeEnum>> notifies = new LinkedBlockingQueue<>();
            subscribeNotifyContainer.put(subscriberURL, notifies);
            notifies.add(Pair.bulid(registryURL, eventType));
            try {
                doNotify(subscriberURL);
            } catch (InterruptedException e) {
                subscribeNotifyContainer.get(subscriberURL).clear();
                subscribe(subscriberURL, listenerContianer.get(subscriberURL));
                LoggerUtil.error("Notify Exception", e);
            }
        }
    }

    private void doNotify(URL subscriberURL) throws InterruptedException {
        Pair<URL, EventTypeEnum> notify = subscribeNotifyContainer.get(subscriberURL).take();
        listenerContianer.get(subscriberURL).notify(notify.getLeft(), notify.getRight());
        if (registryURLContainer.containsKey(subscriberURL)) {
            switch (notify.getRight()) {
                case ADD:
                    registryURLContainer.get(subscriberURL).add(notify.getLeft());
                    break;

                case REMOVE:
                    registryURLContainer.get(subscriberURL).remove(notify.getLeft());
                    break;

                default:
                    break;
            }
        } else {
            registryURLContainer.put(subscriberURL, new HashSet<URL>());
            switch (notify.getRight()) {
                case ADD:
                    registryURLContainer.get(subscriberURL).add(notify.getLeft());
                    break;
                case REMOVE:
                    registryURLContainer.get(subscriberURL).remove(notify.getLeft());
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public List<URL> lookup(URL URL) {
        return new ArrayList<>(registryURLContainer.get(URL));
    }

    @Override
    public boolean isShutdown() {
        return isShutdown.get();
    }

    public void setShutdown(boolean newValue) {
        isShutdown.set(newValue);

    }

    @Override
    public void shutdownGracefully() {
        if (isShutdown()) {
            try {
                destroy();
            } catch (IOException e) {
                LoggerUtil.warn("Registry service shut down  fail");
            }
        }

    }

    public void init(URL registryURL) {
        this.registryURL = registryURL;
    }

    protected abstract void destroy() throws IOException;

    protected abstract void doRegister(URL URL) throws Exception;

    protected abstract void doUnRegister(URL URL) throws Exception;

    protected abstract void doSubscribe(URL URL, NotifyListener listener) throws Exception;

    protected abstract void doUnSubscribe(URL URL, NotifyListener listener) throws IOException;

}
