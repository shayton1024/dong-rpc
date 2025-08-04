package com.dongpi.dongrpc.fault.tolerant.impl;

import com.dongpi.dongrpc.fault.tolerant.TolerantStrategy;
import com.dongpi.dongrpc.model.RpcResponse;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/08/04/11:36
 * @Description: 快速失败容错策略实现类
 */
public class FailFastTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        throw new RuntimeException("服务报错", e);
    }
}
