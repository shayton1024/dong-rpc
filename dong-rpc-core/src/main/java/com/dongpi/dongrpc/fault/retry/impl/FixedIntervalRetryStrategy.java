package com.dongpi.dongrpc.fault.retry.impl;

import com.dongpi.dongrpc.fault.retry.RetryStrategy;
import com.dongpi.dongrpc.model.RpcResponse;

import com.github.rholder.retry.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/08/01/16:30
 * @Description: 重试策略-固定时间间隔重试策略
 */
@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy {

    /**
     * 执行重试逻辑
     * @param callable
     * @return
     * @throws Exception
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws ExecutionException, RetryException{
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class) // 重试条件：如果抛出异常则重试
                .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS)) // 重试间隔：每次重试等待3秒
                .withStopStrategy(StopStrategies.stopAfterAttempt(3)) // 重试次数：最多重试3次
                .withRetryListener(new RetryListener(){;
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                       log.info("重试次数，{}", attempt.getAttemptNumber());
                    }
                })
                .build();
        return retryer.call(callable);
    }
}
