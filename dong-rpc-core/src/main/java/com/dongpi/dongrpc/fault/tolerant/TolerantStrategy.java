package com.dongpi.dongrpc.fault.tolerant;

import com.dongpi.dongrpc.model.RpcResponse;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/08/04/11:34
 * @Description: 通用容错策略接口
 */
public interface TolerantStrategy {

    /**
     * 执行容错逻辑
     *
     * @param context 上下文信息
     * @param e 异常信息
     * @return RpcResponse 响应结果
     */
    RpcResponse doTolerant(Map<String, Object> context, Exception e);
}
