package com.choodon.rpc.framework;

import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.log.LoggerUtil;
import com.choodon.rpc.base.util.NetUtil;
import com.choodon.rpc.base.util.URLTools;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class ExporterContext {
    private static final Map<String, Exporter> exporterContainer = new ConcurrentHashMap<>();
    private static final Map<String, Boolean> packageNameExportContainer = new ConcurrentHashMap<>();
    private static final ReentrantReadWriteLock exporterLock = new ReentrantReadWriteLock();

    private static Exporter createExporter(URL protocolURL, URL registryURl) {
        protocolURL.setHost(NetUtil.getLocalAddress());
        String exporterKey = URLTools.getExporterKey(protocolURL, registryURl);
        if (!exporterContainer.containsKey(exporterKey)) {
            exporterLock.writeLock().lock();
            if (!exporterContainer.containsKey(exporterKey)) {
                Exporter exporter = new Exporter(protocolURL, registryURl);
                exporterContainer.put(exporterKey, exporter);
            }
            exporterLock.writeLock().unlock();
        }
        return exporterContainer.get(exporterKey);

    }


    public static void export(String[] packageNames, URL protocolURL, URL registryURL) {
        Exporter exporter = createExporter(protocolURL, registryURL);
        exporter.export(packageNames);

    }

    public static void export(String[] packageNames, List<URL> protocolURLs, List<URL> registryURLs) {
        for (URL protocolURL : protocolURLs) {
            for (URL registryURL : registryURLs) {
                export(packageNames, protocolURL, registryURL);
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

    public static void export(URL protocolURL, URL registryURL, List<URL> serviceURLs) {
        Exporter exporter = createExporter(protocolURL, registryURL);
        exporter.export(serviceURLs);

    }
}
