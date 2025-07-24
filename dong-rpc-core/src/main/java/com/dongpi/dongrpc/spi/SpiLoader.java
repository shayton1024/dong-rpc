package com.dongpi.dongrpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.dongpi.dongrpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SpiLoader {
    /**
     * 存储已加载的类：接口名 =>（key => 实现类）
     */
    private static final Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();

    /**
     * 对象实例缓存（避免重复 new），类路径 => 对象实例，单例模式
     */
    private static final Map<String, Object> instanceCache = new ConcurrentHashMap<>();

    /**
     * 系统 SPI 目录
     */
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";

    /**
     * 用户自定义 SPI 目录
     */
    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/custom/";

    /**
     * 扫描路径
     */
    private static final String[] SCAN_DIRS = new String[]{RPC_SYSTEM_SPI_DIR, RPC_CUSTOM_SPI_DIR};

    /**
     * 动态加载的类列表
     */
    private static final List<Class<?>> LOAD_CLASS_LIST = Arrays.asList(Serializer.class);

    /**
     * 私有构造函数防止实例化
     */
    private SpiLoader() {
    }

    /**
     * 加载所有类型
     */
    public static synchronized void loadAll() {
        log.info("加载所有 SPI");
        for (Class<?> aClass : LOAD_CLASS_LIST) {
            load(aClass);
        }
    }

    /**
     * 获取某个接口的实例（线程安全）
     */
    public static <T> T getInstance(Class<?> tClass, String key) {
        String tClassName = tClass.getName();
        Map<String, Class<?>> keyClassMap = loaderMap.get(tClassName);

        if (keyClassMap == null) {
            // 懒加载：如果未加载，先尝试加载
            keyClassMap = load(tClass);
            if (keyClassMap == null || keyClassMap.isEmpty()) {
                throw new RuntimeException(String.format("SpiLoader 未加载 %s 类型", tClassName));
            }
        }

        if (!keyClassMap.containsKey(key)) {
            throw new RuntimeException(String.format("SpiLoader 的 %s 不存在 key=%s 的类型", tClassName, key));
        }

        // 获取要加载的实现类
        Class<?> implClass = keyClassMap.get(key);
        String implClassName = implClass.getName();

        // 使用 computeIfAbsent 保证线程安全地创建实例
        return (T) instanceCache.computeIfAbsent(implClassName, k -> {
            try {
                return implClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                String errorMsg = String.format("%s 类实例化失败", implClassName);
                throw new RuntimeException(errorMsg, e);
            }
        });
    }

    /**
     * 加载某个类型（线程安全）
     */
    public static Map<String, Class<?>> load(Class<?> loadClass) {
        String className = loadClass.getName();

        // 检查是否已加载
        Map<String, Class<?>> existingMap = loaderMap.get(className);
        if (existingMap != null) {
            return existingMap;
        }

        // 双重检查锁定模式
        synchronized (SpiLoader.class) {
            existingMap = loaderMap.get(className);
            if (existingMap != null) {
                return existingMap;
            }

            log.info("加载类型为 {} 的 SPI", className);
            Map<String, Class<?>> keyClassMap = new HashMap<>();

            // 扫描路径，用户自定义的 SPI 优先级高于系统 SPI
            for (String scanDir : SCAN_DIRS) {
                List<URL> resources = ResourceUtil.getResources(scanDir + className);
                // 读取每个资源文件
                for (URL resource : resources) {
                    try (
                        InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
                    ) {
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            String[] strArray = line.split("=");
                            if (strArray.length > 1) {
                                String key = strArray[0];
                                String classToLoad = strArray[1];
                                keyClassMap.put(key, Class.forName(classToLoad));
                            }
                        }
                    } catch (Exception e) {
                        log.error("SPI 资源加载错误", e);
                    }
                }
            }

            // 放入缓存
            loaderMap.put(className, keyClassMap);
            return keyClassMap;
        }
    }
}