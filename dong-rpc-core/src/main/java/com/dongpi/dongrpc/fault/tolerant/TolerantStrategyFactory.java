package com.dongpi.dongrpc.fault.tolerant;

import com.dongpi.dongrpc.fault.tolerant.impl.FailFastTolerantStrategy;
import com.dongpi.dongrpc.spi.SpiLoader;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/08/04/11:47
 * @Description: 容错策略工厂类
 */
public class TolerantStrategyFactory {

    static{
        SpiLoader.load(TolerantStrategy.class);
    }

    /**
     * 默认容错策略
     */
    private static final TolerantStrategy DEFAULT_STRATEGY = new FailFastTolerantStrategy();

    /**
     * 获取容错策略
     *
     * @return TolerantStrategy 默认容错策略
     */
    public static TolerantStrategy getInstance(String key){
        return SpiLoader.getInstance(TolerantStrategy.class, key);
    }
}
