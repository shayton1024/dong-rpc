package com.dongpi.dongrpc.registry;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.json.JSONUtil;
import com.dongpi.dongrpc.config.RegistryConfig;
import com.dongpi.dongrpc.model.ServiceMetaInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ZooKeeperRegistry implements Registry {

    private CuratorFramework client;

    private ServiceDiscovery<ServiceMetaInfo> serviceDiscovery;

    /**
     * 本机注册的节点 key 集合（用于维护续期）
     */
    private final Set<String> localRegisterNodeKeySet = new ConcurrentHashSet<>();

    /**
     * 注册中心服务缓存
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    /**
     * 正在监听的 key 集合
     */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();

    /**
     * 根节点
     */
    private static final String ZK_ROOT_PATH = "/rpc/zk";


    @Override
    public void init(RegistryConfig registryConfig) {
        // 构建 client 实例
        client = CuratorFrameworkFactory
                .builder()
                .connectString(registryConfig.getAddress())
                .retryPolicy(new ExponentialBackoffRetry(Math.toIntExact(registryConfig.getTimeout()), 3))
                .build();

        // 构建 serviceDiscovery 实例
        serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMetaInfo.class)
                .client(client)
                .basePath(ZK_ROOT_PATH)
                .serializer(new JsonInstanceSerializer<>(ServiceMetaInfo.class))
                .build();

        try {
            // 启动 client 和 serviceDiscovery
            client.start();
            serviceDiscovery.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        // 注册到 zk 里
        serviceDiscovery.registerService(buildServiceInstance(serviceMetaInfo));

        // 添加节点信息到本地缓存
        String registerKey = ZK_ROOT_PATH + "/" + serviceMetaInfo.getServiceNodeKey();
        localRegisterNodeKeySet.add(registerKey);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        try {
            serviceDiscovery.unregisterService(buildServiceInstance(serviceMetaInfo));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 从本地缓存移除
        String registerKey = ZK_ROOT_PATH + "/" + serviceMetaInfo.getServiceNodeKey();
        localRegisterNodeKeySet.remove(registerKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // 优先从缓存获取服务
        List<ServiceMetaInfo> cachedServiceMetaInfoList = registryServiceCache.readCache(serviceKey);
        if (cachedServiceMetaInfoList != null) {
            return cachedServiceMetaInfoList;
        }

        try {
            // 第一次访问服务才进行监听
            if(!registryServiceCache.containsKey(serviceKey)) {
                watch(serviceKey, null);
            }

            // 查询服务信息
            Collection<ServiceInstance<ServiceMetaInfo>> serviceInstanceList = serviceDiscovery.queryForInstances(serviceKey);

            // 解析服务信息
            List<ServiceMetaInfo> serviceMetaInfoList = serviceInstanceList.stream()
                    .map(ServiceInstance::getPayload)
                    .collect(Collectors.toList());

            serviceInstanceList.forEach(serviceInstance -> {
                String serviceNodeKey = ZK_ROOT_PATH + "/" + serviceInstance.getId();
                // 写入服务缓存
                registryServiceCache.writeCache(serviceKey, serviceNodeKey, serviceInstance.getPayload());
            });
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败", e);
        }
    }

    /**
     * 监听（消费端）
     *
     * @param serviceNodeKey 服务节点 key
     */
    @Override
    public void watch(String serviceKey, String serviceNodeKey) {
        // 监听路径：/rpc/zk/{serviceKey}
        String watchPath = ZK_ROOT_PATH + "/" + serviceKey;

        // 防止重复监听
        boolean added = watchingKeySet.add(serviceKey);
        if (!added) {
            return;
        }

        // 创建 PathChildrenCache 来监听子节点变化
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, watchPath, true);

        pathChildrenCache.getListenable().addListener((curatorFramework, event) -> {
            try {
                String fullPath = event.getData().getPath();  // /rpc/zk/{serviceKey}/{nodeId}
                String nodeData = new String(event.getData().getData(), StandardCharsets.UTF_8);
                String watchServiceNodeKey = fullPath;  // 直接用路径作为唯一标识

                switch (event.getType()) {
                    case CHILD_ADDED:
                        ServiceMetaInfo meta1 = JSONUtil.toBean(nodeData, ServiceMetaInfo.class);
                        registryServiceCache.writeCache(serviceKey, watchServiceNodeKey, meta1);
                        log.debug("ZK监听：服务节点上线 {}", watchServiceNodeKey);
                        break;
                    case CHILD_UPDATED:
                        ServiceMetaInfo meta2 = JSONUtil.toBean(nodeData, ServiceMetaInfo.class);
                        registryServiceCache.writeCache(serviceKey, watchServiceNodeKey, meta2);
                        log.debug("ZK监听：服务节点变更 {}", watchServiceNodeKey);
                        break;
                    case CHILD_REMOVED:
                        registryServiceCache.clearCache(serviceKey, watchServiceNodeKey);
                        log.debug("ZK监听：服务节点下线 {}", watchServiceNodeKey);
                        break;
                    default:
                        log.debug("ZK监听：忽略事件 {}", event.getType());
                        break;
                }
            } catch (Exception e) {
                log.error("ZK监听回调异常，serviceKey = {}", serviceKey, e);
            }
        });

        try {
            pathChildrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
            log.info("ZK开始监听服务：{}", watchPath);
        } catch (Exception e) {
            log.error("ZK监听启动失败：{}", watchPath, e);
            throw new RuntimeException("Zookeeper watch 启动失败", e);
        }
    }


    @Override
    public void destroy() {
        log.info("当前节点下线");
        // 下线节点（这一步可以不做，因为都是临时节点，服务下线，自然就被删掉了）
        for (String key : localRegisterNodeKeySet) {
            try {
                client.delete().guaranteed().forPath(key);
            } catch (Exception e) {
                throw new RuntimeException(key + "节点下线失败");
            }
        }

        // 释放资源
        if (client != null) {
            client.close();
        }
    }

    @Override
    public void heartBeat() {
        log.debug("ZooKeeper uses ephemeral nodes, no explicit heartbeat required.");
    }

    private ServiceInstance<ServiceMetaInfo> buildServiceInstance(ServiceMetaInfo serviceMetaInfo) {
        String serviceAddress = serviceMetaInfo.getServiceHost() + ":" + serviceMetaInfo.getServicePort();
        try {
            return ServiceInstance
                    .<ServiceMetaInfo>builder()
                    .id(serviceAddress)
                    .name(serviceMetaInfo.getServiceKey())
                    .address(serviceAddress)
                    .payload(serviceMetaInfo)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

