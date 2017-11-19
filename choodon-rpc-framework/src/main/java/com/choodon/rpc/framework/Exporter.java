package com.choodon.rpc.framework;

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

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Exporter {

    private URL registryURL;
    private URL protocolURL;
    private List<URL> services = new CopyOnWriteArrayList<>();
    private RegistryService registry;
    private AtomicBoolean isExport = new AtomicBoolean(false);

    public Exporter(URL protocolURL, URL registryURL) {
        this.protocolURL = protocolURL;
        this.registryURL = registryURL;
    }

    public List<URL> getServices() {
        return services;
    }


    public void exportALL() {
        export(services);

    }

    public void export(String[] packagePaths) {
        List<URL> serviceURLs = new LinkedList<>();
        for (String packagePath : packagePaths) {
            serviceURLs.addAll(ServiceManager.get(packagePath));
        }
        export(serviceURLs);
    }

    public void export(String packageName) {
        List<URL> serviceURLs = new LinkedList<>();
        serviceURLs.addAll(ServiceManager.get(packageName));
        export(serviceURLs);
    }

    public void export(List<URL> serviceURLs) {
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
        for (URL serviceURL : serviceURLs) {
            if (services.contains(serviceURL)) {
                LoggerUtil.warn(serviceURL + " has exported in Exporter: " + URLTools.getExporterKey(protocolURL, registryURL));
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
                        LoggerUtil.error(className + " Class Not Found .", e);
                        throw new RPCFrameworkException(className + " Class Not Found .");
                    }
                    ServiceManager.handle(clazz);
                    services.add(serviceURL);
                    registry.register(URLTools.buildRegistryProviderURL(protocolURL, serviceURL));
                }
            }
        }
    }
}
