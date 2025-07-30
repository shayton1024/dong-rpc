package com.dongpi.dongrpc.protocol;

import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/07/30/17:56
 * @Description: 协议消息的状态枚举
 */

@Getter
public enum ProtocolMessageStatusEnum {

    OK("ok",20),
    BAD_REQUEST("badRequest", 400),
    BAD_RESPONSE("badResponse", 500);

    private final String text;

    private final int value;

    ProtocolMessageStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    public static ProtocolMessageStatusEnum getEnumByValue(int value) {
        for(ProtocolMessageStatusEnum statusEnum : ProtocolMessageStatusEnum.values()) {
            if(statusEnum.value == value)
                return statusEnum;
        }

        return null;
    }
}
