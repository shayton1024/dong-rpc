package com.dongpi.dongrpc.fault.tolerant;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/08/04/11:45
 * @Description: 容错策略常量
 */
public interface TolerantStrategyKeys {

    /**
     * 故障恢复
     */
    String FAIL_BACK = "failBack";

    /**
     * 快速失败
     */
    String FAIL_FAST = "failFast";

    /**
     * 静默处理
     */
    String FAIL_SAFE = "failSafe";

    /**
     * 故障转移
     */
    String FAIL_OVER = "failOver";
}
