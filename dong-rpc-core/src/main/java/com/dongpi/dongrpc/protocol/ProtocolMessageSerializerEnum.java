package com.dongpi.dongrpc.protocol;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/07/30/18:04
 * @Description: 协议消息序列化器枚举
 */
@Getter
public enum ProtocolMessageSerializerEnum {

    JDK(0, "jdk"),
    JSON(1, "json"),
    KRYO(2, "kryo"),
    HESSIAN(3, "hessian");

    private final int key;

    private final String value;

    ProtocolMessageSerializerEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * 获取序列化器枚举
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据key获取序列化器枚举
     * @param key
     * @return
     */
    public static ProtocolMessageSerializerEnum getEnumByKey(int key) {
        for (ProtocolMessageSerializerEnum serializer : ProtocolMessageSerializerEnum.values()) {
            if (serializer.key == key) {
                return serializer;
            }
        }
        return null;
    }

    /**
     * 根据value获取序列化器枚举
     * @param value
     * @return
     */
    public static ProtocolMessageSerializerEnum getEnumByValue(String value) {
        if(StrUtil.isBlank(value)){
            return null;
        }
        for (ProtocolMessageSerializerEnum serializer : ProtocolMessageSerializerEnum.values()) {
            if (serializer.value.equals(value)) {
                return serializer;
            }
        }
        return null;
    }
}
