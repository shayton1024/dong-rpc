package com.dongpi.dongrpc.config;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/06/25/17:07
 * @Description: 框架全局配置
 */

@Data
public class RpcConfig {

    /**
     * 名称
     */
    private String name = "dong-rpc";

    /**
     * 版本号
     */
    private String version = "1.0";

    /**
     * 服务器主机名
     */
    private String serverHost = "localhost";

    /**
     * 端口号
     */
    private Integer serverPort = 8081;
}
