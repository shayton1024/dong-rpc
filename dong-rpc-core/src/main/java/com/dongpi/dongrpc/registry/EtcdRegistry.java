package com.dongpi.dongrpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.dongpi.dongrpc.config.RegistryConfig;
import com.dongpi.dongrpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.options.WatchOption;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class EtcdRegistry implements Registry{

    private Client client;

    private KV kvClient;

    /**
     * ETCD根路径
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";

    /**
     * 本机注册的节点key集合
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    /**
     * 注册中心服务缓存
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    /**
     * 正在监听的key集合
     */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();

    @Override
    public void init(RegistryConfig registryConfig) {
        // 根据配置初始化ETCD客户端
        client = Client.builder()
                .endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient = client.getKVClient();
        // 开启心跳检测
        heartBeat();
    }
    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        // 创建Lease和KV客户端
        Lease leaseClient = client.getLeaseClient();

        // 创建一个30秒的租约
        long leaseId = leaseClient.grant(30).get().getID();

        // 设置要存储的键值对
        String registryKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registryKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        // 将键值对和租约关联起来，设置过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption).get();

        // 添加节点信息到本地缓存
        localRegisterNodeKeySet.add(registryKey);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        kvClient.delete(ByteSequence.from(ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey(), StandardCharsets.UTF_8));
        localRegisterNodeKeySet.remove(serviceMetaInfo.getServiceNodeKey());
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // 优先从本地缓存中读取服务
        List<ServiceMetaInfo> serviceMetaInfoList = registryServiceCache.readCache(serviceKey);
        if(CollUtil.isNotEmpty(serviceMetaInfoList)){
            return serviceMetaInfoList;
        }

        // 前缀搜索
        String searchPrefix = ETCD_ROOT_PATH + serviceKey;

        try {
            // 第一次访问服务才进行监听
            if(!registryServiceCache.containsKey(serviceKey)) {
                watch(serviceKey, null);
            }

            // 前缀查询
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8), getOption)
                    .get()
                    .getKvs();

            // 解析服务信息
            return keyValues.stream()
                    .map(keyValue -> {
                        String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(keyValue.getValue().toString(StandardCharsets.UTF_8), ServiceMetaInfo.class);
                        registryServiceCache.writeCache(serviceKey, key, serviceMetaInfo);
                        return serviceMetaInfo;
                    }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败", e);
        }
    }

    @Override
    public void destroy() {
        log.info("当前节点已经下线");
        // 下线节点
        // 遍历本节点所有的key，删除ETCD中的注册信息
        for(String nodeKey : localRegisterNodeKeySet) {
            try {
                kvClient.delete(ByteSequence.from(nodeKey, StandardCharsets.UTF_8)).get();
                log.info("节点 {} 已经下线", nodeKey);
            } catch (Exception e) {
                throw new RuntimeException(nodeKey + "节点下线失败");
            }
        }
        // 释放资源
        if(kvClient != null) {
            kvClient.close();
        }
        if(client != null) {
            client.close();
        }
    }

    @Override
    public void heartBeat() {
        // 每10秒续签一次
        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {
                // 遍历本节点所有的key
                if (localRegisterNodeKeySet.isEmpty()) {
                    log.warn("没有注册的节点，无法发送心跳");
                    return;
                }
                for (String nodeKey : localRegisterNodeKeySet) {
                    try {
                        List<KeyValue> keyValues = kvClient.get(ByteSequence.from(nodeKey, StandardCharsets.UTF_8))
                                .get().getKvs();// 发送心跳，实际是查询节点是否存在
                        // 节点已经过期（重启节点重新注册）
                        if(CollUtil.isEmpty(keyValues)) {
                            continue;
                        }
                        // 节点未过期，重新注册
                        KeyValue keyValue = keyValues.get(0);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        register(serviceMetaInfo);

                    } catch (Exception e) {
                        log.error("心跳发送失败，节点：{}", nodeKey, e);
                    }
                }
            }
        });

        // 支持秒级别定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String serviceKey, String serviceNodeKey) {
        Watch watchClient = client.getWatchClient();
        // 如果之前未监听，开启监听
        boolean added = watchingKeySet.add(serviceKey);

        // 前缀监听整个服务
        if(added) {
            WatchOption watchOption = WatchOption.builder().isPrefix(true).build();
            watchClient.watch(ByteSequence.from(ETCD_ROOT_PATH + serviceKey, StandardCharsets.UTF_8), watchOption, response -> {
                for (WatchEvent event : response.getEvents()) {
                    KeyValue keyValue = event.getKeyValue();
                    String watchServiceNodeKey = keyValue.getKey().toString(StandardCharsets.UTF_8);
                    switch (event.getEventType()) {
                        case PUT:
                            // 排除心跳检测
                            if(registryServiceCache.containsCache(serviceKey, watchServiceNodeKey)) {
                                log.debug("服务 {} 已存在，跳过注册", watchServiceNodeKey);
                                break;
                            }
                            ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(keyValue.getValue().toString(StandardCharsets.UTF_8), ServiceMetaInfo.class);
                            registryServiceCache.writeCache(serviceKey, watchServiceNodeKey, serviceMetaInfo);
                        case DELETE:
                            // 服务注销
                            registryServiceCache.clearCache(serviceKey, watchServiceNodeKey);
                        default:
                            break;
                    }
                }
            });
        }
    }
}
