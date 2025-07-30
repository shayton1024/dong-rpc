package com.dongpi.dongrpc.protocol;

import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/07/30/18:01
 * @Description: 协议消息类型枚举
 */
@Getter
public enum ProtocolMessageTypeEnum {

    REQUEST(1),
    RESPONSE(2),
    HEARTBEAT(3),
    OTHERS(4);

    private final int key;

    ProtocolMessageTypeEnum(int key) {
        this.key = key;
    }

    public static ProtocolMessageTypeEnum getEnumByKey(int key) {
        for (ProtocolMessageTypeEnum typeEnum : ProtocolMessageTypeEnum.values()) {
            if (typeEnum.key == key) {
                return typeEnum;
            }
        }
        return null;
    }
}
