package com.choodon.rpc.framework;

import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.base.extension.ExtensionLoader;
import com.choodon.rpc.base.log.LoggerUtil;
import com.choodon.rpc.base.util.NetUtil;
import com.choodon.rpc.base.util.URLTools;
import com.choodon.rpc.transport.api.TransportServer;
import com.choodon.rpc.transport.api.TransportServerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class ExporterContext {
    private static final Map<String, Exporter> exporterContainer = new ConcurrentHashMap<>();
    private static final Map<String, Boolean> packageNameExportContainer = new ConcurrentHashMap<>();
    private static final Map<URL, Set<Exporter>> protocolURL2Exporter = new ConcurrentHashMap<>();

    private static Exporter createExporter(URL protocolURL, URL registryURL) {
        protocolURL.setHost(NetUtil.getLocalAddress());
        String exporterKey = URLTools.getExporterKey(protocolURL, registryURL);
        Exporter exporter;
        if (!exporterContainer.containsKey(exporterKey)) {
            Exporter exporter0 = new Exporter(protocolURL, registryURL);
            exporter = exporterContainer.putIfAbsent(exporterKey, exporter0);
            if (exporter == null) {
                exporter = exporter0;
            }
        } else {
            exporter = exporterContainer.get(exporterKey);
        }
        if (protocolURL2Exporter.containsKey(protocolURL)) {
            protocolURL2Exporter.get(protocolURL).add(exporter);
        } else {
            Set<Exporter> exporters;
            Set<Exporter> exporters0 = new HashSet<>();
            exporters = protocolURL2Exporter.putIfAbsent(protocolURL, exporters0);
            if (exporters == null) {
                exporters0.add(exporter);
            } else {
                exporters.add(exporter);
            }
        }
        return exporter;

    }


    public static void export(String[] packageNames, URL protocolURL, URL registryURL) {
        Exporter exporter = createExporter(protocolURL, registryURL);
        exporter.export(packageNames);
    }

    public static void export(String[] packageNames, List<URL> protocolURLs, List<URL> registryURLs) {
        for (URL protocolURL : protocolURLs) {
            for (URL registryURL : registryURLs) {
                unexport(packageNames, protocolURL, registryURL);
            }
        }

    }

    public static void export(String packageName, URL protocolURL, URL registryURL) {
        StringBuilder packageNameExportDes = new StringBuilder();
        packageNameExportDes.append(packageName).append("-").append(URLTools.getProtocolKey(protocolURL)).append("-").append(URLTools.getRegistryKey(registryURL));
        if (packageNameExportContainer.containsKey(packageNameExportDes.toString())) {
            boolean isExport = packageNameExportContainer.get(packageName);
            if (isExport) {
                LoggerUtil.error(packageNameExportDes + "has exported.");
                return;
            }
        } else {
            packageNameExportContainer.put(packageNameExportDes.toString(), Boolean.TRUE);
        }

        Exporter exporter = createExporter(protocolURL, registryURL);
        exporter.export(packageName);

    }

    public static void export(URL protocolURL, URL registryURL, Set<URL> serviceURLs) {
        Exporter exporter = createExporter(protocolURL, registryURL);
        exporter.export(serviceURLs);
    }

    public static void unexport(String[] packageNames, URL protocolURL, URL registryURL) {
        Exporter exporter = createExporter(protocolURL, registryURL);
        exporter.unexport(packageNames);
    }

    public static void unexport(String[] packageNames, List<URL> protocolURLs, List<URL> registryURLs) {
        for (URL protocolURL : protocolURLs) {
            for (URL registryURL : registryURLs) {
                unexport(packageNames, protocolURL, registryURL);
            }
        }

    }

    public static void unexport(String packageName, URL protocolURL, URL registryURL) {
        Exporter exporter = createExporter(protocolURL, registryURL);
        exporter.unexport(packageName);

    }

    public static void unexport(URL protocolURL, URL registryURL, Set<URL> serviceURLs) {
        Exporter exporter = createExporter(protocolURL, registryURL);
        exporter.unexport(serviceURLs);
    }

    public static void shutdownGracefully(URL protocolURL) {
        Set<Exporter> exporters = protocolURL2Exporter.get(protocolURL);
        for (Exporter exporter : exporters) {
            exporter.unexport();
        }
        TransportServerFactory serverFactory = ExtensionLoader.getExtensionLoader(TransportServerFactory.class)
                .getExtension(protocolURL.getParameter(URLParamType.transportTool.getName(), URLParamType.transportTool.getValue()));
        TransportServer server = serverFactory.createServer(protocolURL);
        server.shutdown();
        LoggerUtil.info("Server :{} shut down successfully.", protocolURL);
    }
}
