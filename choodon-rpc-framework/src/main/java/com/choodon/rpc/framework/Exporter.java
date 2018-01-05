package com.choodon.rpc.framework;

import com.choodon.rpc.base.common.ConcurrentHashSet;
import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.base.exception.RPCFrameworkException;
import com.choodon.rpc.base.extension.ExtensionLoader;
import com.choodon.rpc.base.log.LoggerUtil;
import com.choodon.rpc.base.service.ServiceHandlerManager;
import com.choodon.rpc.base.service.ServiceManager;
import com.choodon.rpc.base.util.URLTools;
import com.choodon.rpc.registry.api.RegistryFactory;
import com.choodon.rpc.registry.api.RegistryService;
import com.choodon.rpc.transport.api.TransportServerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class Exporter {

    private URL registryURL;
    private URL protocolURL;
    private Set<URL> services = new ConcurrentHashSet<>();
    private RegistryService registry;
    private AtomicBoolean isExport = new AtomicBoolean(false);
    private Set<String> packageNames = new ConcurrentHashSet<>();

    public Exporter(URL protocolURL, URL registryURL) {
        this.protocolURL = protocolURL;
        this.registryURL = registryURL;
    }

    public void exportALL() {
        export(services);
    }

    public void export(String[] packageNames) {
        for (String packageName : packageNames) {
            export(packageName);
        }
    }

    public void export(String packageName) {
        Set<URL> serviceURLs;
        if (packageNames.contains(packageName)) {
            LoggerUtil.warn("packageName :{} has exported in the exporter : {}.", packageName, URLTools.getExporterKey(protocolURL, registryURL));
            return;
        } else {
            synchronized (this) {
                if (packageNames.contains(packageName)) {
                    LoggerUtil.warn("packageName :{} has exported in the exporter : {}.", packageName, URLTools.getExporterKey(protocolURL, registryURL));
                    return;
                } else {
                    serviceURLs = new ConcurrentHashSet<>();
                    serviceURLs.addAll(ServiceManager.getIfNullInit(packageName));
                    packageNames.add(packageName);
                }
            }

        }
        export(serviceURLs);
    }

    private void export() {
        if (isExport.get()) {
            return;
        } else {
            synchronized (this) {
                if (!isExport.get()) {
                    RegistryFactory registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class)
                            .getExtension(registryURL.getProtocol());
                    registry = registryFactory.getRegistryService(registryURL);
                    TransportServerFactory serverFactory = ExtensionLoader.getExtensionLoader(TransportServerFactory.class)
                            .getExtension(protocolURL.getParameter(URLParamType.transportTool.getName(), URLParamType.transportTool.getValue()));
                    serverFactory.createServer(protocolURL);
                    isExport.set(true);
                }
            }
        }
    }

    public void export(Set<URL> serviceURLs) {
        for (URL serviceURL : serviceURLs) {
            export(serviceURL);
        }
    }

    public void export(URL serviceURL) {
        if (!isExport.get()) {
            export();
        }
        if (services.contains(serviceURL)) {
            LoggerUtil.warn("{} has exported in the exporter: {}", serviceURL, URLTools.getExporterKey(protocolURL, registryURL));
        } else {
            if (ServiceHandlerManager.isServiceHandled(serviceURL.getPath())) {
                services.add(serviceURL);
                registry.register(URLTools.buildRegistryProviderURL(protocolURL, serviceURL));
            } else {
                String className = serviceURL.getParameter(URLParamType.serviceImplClassName.getName(), serviceURL.getPath());
                Class clazz;
                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    LoggerUtil.error(className + "{} Class Not Found .", e);
                    throw new RPCFrameworkException(className + " Class Not Found .");
                }
                ServiceManager.handle(clazz);
                services.add(serviceURL);
                registry.register(URLTools.buildRegistryProviderURL(protocolURL, serviceURL));
            }
        }
        LoggerUtil.warn("{} exported successfully in the exporter: {}", serviceURL, URLTools.getExporterKey(protocolURL, registryURL));
    }

    public void unexport(String[] packageNames) {
        for (String packageName : packageNames) {
            unexport(packageName);
        }
    }

    public void unexport(String packageName) {
        List<URL> serviceURLs = ServiceManager.get(packageName);
        if (serviceURLs == null) {
            LoggerUtil.warn("packageName;{} has not exported.", packageName);
            return;
        } else {
            for (URL serviceURL : serviceURLs) {
                unexport(serviceURL);
            }
        }
        LoggerUtil.info("packageName :{} has unexported successfully.", packageName);
    }

    public void unexport(Set<URL> serviceURLs) {
        for (URL serviceURL : serviceURLs) {
            unexport(serviceURL);
        }
    }

    public void unexport(URL serviceURL) {
        registry.unregister(URLTools.buildRegistryProviderURL(protocolURL, serviceURL));
        LoggerUtil.warn("{} unexported successfully in the exporter: {}", serviceURL, URLTools.getExporterKey(protocolURL, registryURL));
    }

    public void unexport() {
        for (URL serviceURL : services) {
            unexport(serviceURL);
        }
        LoggerUtil.warn("All serviceURLs unexported  successfully in the exporter: {}", URLTools.getExporterKey(protocolURL, registryURL));
    }
}
