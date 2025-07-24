package com.dongpi.dongrpc;

import com.dongpi.constant.RpcConstant;
import com.dongpi.dongrpc.config.RpcConfig;
import com.dongpi.dongrpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/06/26/18:12
 * @Description: RPC框架应用，存放项目全局用到的变量。双检锁单例模式实现
 */

@Slf4j
public class RpcApplication {

    private static volatile RpcConfig rpcConfig;

    /**
     * 框架初始化，支持传入自定义配置
     * @param newRpcConfig
     */
    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
    }

    /**
     * 初始化
     */
    public static void init() {
        RpcConfig newRpcConfig;
        try{
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            // 配置加载失败，使用默认值
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    public static RpcConfig getRpcConfig() {
        if(rpcConfig==null){
            synchronized (RpcApplication.class) {
                if(rpcConfig==null){
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
