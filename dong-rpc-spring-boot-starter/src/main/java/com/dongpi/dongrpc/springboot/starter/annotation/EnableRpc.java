package com.dongpi.dongrpc.springboot.starter.annotation;

import com.dongpi.dongrpc.springboot.starter.bootstrap.RpcConsumerBootstrap;
import com.dongpi.dongrpc.springboot.starter.bootstrap.RpcInitBootstrap;
import com.dongpi.dongrpc.springboot.starter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/08/06/10:34
 * @Description: 启用Rpc注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootstrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableRpc {

    /**
     * 是否需要服务端
     * 默认为true，表示需要服务端
     * 如果设置为false，则只启用客户端功能
     */
    boolean needServer() default true;
}
