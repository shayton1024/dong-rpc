package com.dongpi.dongrpc.fault.retry;

import com.dongpi.dongrpc.fault.retry.impl.NoRetryStrategy;
import com.dongpi.dongrpc.spi.SpiLoader;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/08/01/17:04
 * @Description: 重试策略工厂
 */
public class RetryStrategyFactory {

    static {
        SpiLoader.load(RetryStrategy.class);
    }

    /**
     * 默认重试器
     */
    public static final RetryStrategy DEFAULT_RETRY_STRATEGY = new NoRetryStrategy();

    /**
     * 获取重试策略实例
     *
     * @param key 重试策略的键
     * @return 重试策略实例
     */
    public static RetryStrategy getInstance(String key) {
        return SpiLoader.getInstance(RetryStrategy.class, key);
    }

}
