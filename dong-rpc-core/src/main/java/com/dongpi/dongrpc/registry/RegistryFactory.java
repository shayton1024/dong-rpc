package com.dongpi.dongrpc.registry;

import com.dongpi.dongrpc.spi.SpiLoader;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/07/25/15:07
 * @Description: 注册工厂，用于获取注册中心对象
 */
public class RegistryFactory {

    static {
        SpiLoader.load(Registry.class);
    }

    /**
     * 默认注册中心
     */
    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

    /**
     * 获取实例
     * @param key
     * @return
     */
    public static Registry getInstance(String key) {
        return SpiLoader.getInstance(Registry.class, key);
    }
}
