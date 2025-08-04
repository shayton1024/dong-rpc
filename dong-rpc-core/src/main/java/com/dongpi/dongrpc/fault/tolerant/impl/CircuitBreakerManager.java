package com.dongpi.dongrpc.fault.tolerant.impl;

import com.dongpi.dongrpc.fault.tolerant.CircuitBreaker;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/08/04/14:36
 * @Description:
 */
public class CircuitBreakerManager {
    private static final CircuitBreaker INSTANCE = new CircuitBreaker(3, 10000); // 失败3次后打开，持续10秒

    public static CircuitBreaker getInstance() {
        return INSTANCE;
    }
}
