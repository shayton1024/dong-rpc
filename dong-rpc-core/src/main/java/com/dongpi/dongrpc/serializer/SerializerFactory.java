package com.dongpi.dongrpc.serializer;

import com.dongpi.dongrpc.spi.SpiLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/06/27/18:04
 * @Description:
 */
public class SerializerFactory {

    // 设置默认序列化器
    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    // 判断是否初始化
    private static volatile boolean initialized = false;

    // 似有构造函数，防止外部实例化
    private SerializerFactory() {
        // 私有构造函数
    }

    /**
     * 初始化方法，只有在首次使用时调用
     */
    private static void init() {
        if(!initialized) {
            synchronized (SerializerFactory.class) {
                if (!initialized) {
                    // 加载所有序列化器
                    SpiLoader.load(Serializer.class);
                    initialized = true;
                }
            }
        }
    }

    /**
     * 获取实例
     * @param key
     * @return
     */
    public static Serializer getInstance(String key) {
        init();
        return SpiLoader.getInstance(Serializer.class, key);
    }

    /**
     * 获取默认序列化器实例
     * @return 默认序列化器实例
     */
    public static Serializer getDefaultInstance() {
        return DEFAULT_SERIALIZER;
    }
}
