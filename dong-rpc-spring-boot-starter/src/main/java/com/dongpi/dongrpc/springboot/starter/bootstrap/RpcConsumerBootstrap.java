package com.dongpi.dongrpc.springboot.starter.bootstrap;

import com.dongpi.dongrpc.RpcApplication;
import com.dongpi.dongrpc.proxy.ServiceProxyFactory;
import com.dongpi.dongrpc.springboot.starter.annotation.RpcReference;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/08/06/16:03
 * @Description: 服务消费者启动类
 */
public class RpcConsumerBootstrap implements BeanPostProcessor {

    /**
     * 初始化消费者相关配置
     * 这里可以添加一些消费者的初始化逻辑，例如获取服务代理、注册监听等
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        // 遍历对象所有属性
        Field[] declaredFields = beanClass.getDeclaredFields();
        for(Field field : declaredFields) {
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);
            if(rpcReference != null) {
                // 为属性生成代理对象
                Class<?> interfaceClass = rpcReference.interfaceClass();
                if(interfaceClass == void.class) {
                    // 如果没有指定接口类，则使用当前类作为接口
                    interfaceClass = field.getType();
                }
                field.setAccessible(true);
                Object proxyObject = ServiceProxyFactory.getProxy(interfaceClass);
                try {
                    field.set(bean, proxyObject);
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("设置代理对象失败，可能是因为字段不可访问", e);
                }
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
