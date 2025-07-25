package com.dongpi.dongrpc.registry;

import ch.qos.logback.core.net.server.Client;
import com.dongpi.dongrpc.config.RegistryConfig;
import com.dongpi.dongrpc.model.ServiceMetaInfo;
import io.etcd.jetcd.KV;

import java.util.List;

public class EtcdRegistry implements Registry{

    private Client client;

    private KV kv;

    /**
     * ETCD根路径
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";

    @Override
    public void init(RegistryConfig registryConfig) {

    }
    @Override
    public void registry(ServiceMetaInfo serviceMetaInfo) throws Exception {

    }

    @Override
    public void unRegistry(ServiceMetaInfo serviceMetaInfo) {

    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceName) {
        return null;
    }

    @Override
    public void destroy() {

    }
}
