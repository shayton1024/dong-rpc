package com.dongpi.dongrpc.fault.retry;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/08/01/17:01
 * @Description: 重试策略常量
 */
public interface RetryStrategyKeys {

    /**
     * 不重试策略
     */
    String NO = "no";

    /**
     * 固定间隔重试策略
     */
    String FIXED_INTERVAL = "fixedInterval";
}
