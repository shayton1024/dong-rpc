package com.dongpi.dongrpc.fault.retry;

import com.dongpi.dongrpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/08/01/16:23
 * @Description: 通用重试策略接口
 */
public interface RetryStrategy {

    /**
     * 执行重试逻辑
     * @param callable
     * @return
     * @throws Exception
     */
    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}
