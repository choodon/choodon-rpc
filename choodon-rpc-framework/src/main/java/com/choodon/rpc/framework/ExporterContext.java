package com.choodon.rpc.framework;

import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.log.LoggerUtil;
import com.choodon.rpc.base.util.NetUtil;
import com.choodon.rpc.base.util.URLTools;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;


public class ExporterContext {
    private static Map<String, Exporter> exporterContainer = new ConcurrentHashMap<>();
    private static Map<String, Boolean> packageNameExportContainer = new ConcurrentHashMap<>();

    private static Exporter createExporter(URL protocolURL, URL registryURl) {
        protocolURL.setHost(NetUtil.getLocalAddress());
        String exporterKey = URLTools.getExporterKey(protocolURL, registryURl);
        if (exporterContainer.containsKey(exporterKey)) {
            return exporterContainer.get(exporterKey);
        } else {
            Exporter exporter = new Exporter(protocolURL, registryURl);
            exporterContainer.put(exporterKey, exporter);
            return exporterContainer.get(exporterKey);
        }

    }


    public static void export(String[] packagePaths, URL protocolURL, URL registryURL) {
        Exporter exporter = createExporter(protocolURL, registryURL);
        exporter.export(packagePaths);

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
