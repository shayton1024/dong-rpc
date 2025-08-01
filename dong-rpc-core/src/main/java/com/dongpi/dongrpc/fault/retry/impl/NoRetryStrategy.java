package com.dongpi.dongrpc.fault.retry.impl;

import com.dongpi.dongrpc.fault.retry.RetryStrategy;
import com.dongpi.dongrpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/08/01/16:29
 * @Description: 重试策略：不重试
 */
public class NoRetryStrategy implements RetryStrategy {
    /**
     * 执行重试逻辑
     * @param callable
     * @return
     * @throws Exception
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        return callable.call();
    }
}
