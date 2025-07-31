package com.dongpi.dongrpc.protocol;

import com.dongpi.dongrpc.model.RpcRequest;
import com.dongpi.dongrpc.model.RpcResponse;
import com.dongpi.dongrpc.serializer.Serializer;
import com.dongpi.dongrpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/07/31/10:54
 * @Description: 协议消息解码器
 */
public class ProtocolMessageDecoder {
    public static ProtocolMessage<?> decode(Buffer buffer) throws IOException {
        // 从指定位置读取Buffer
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        byte magic = buffer.getByte(0);
        // 校验魔数
        System.out.println(ProtocolConstant.PROTOCOL_MAGIC);
        if(magic != ProtocolConstant.PROTOCOL_MAGIC) {
            throw new RuntimeException("invalid magic " + magic);
        }

        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer(buffer.getByte(2));
        header.setType(buffer.getByte(3));
        header.setStatus(buffer.getByte(4));
        header.setRequestId(buffer.getLong(5));
        header.setBodyLength(buffer.getInt(13));

        // 解决粘包问题，只读取指定的长度
        byte[] bufferBytes = buffer.getBytes(17, 17 + header.getBodyLength());
        // 解析消息体
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if(serializerEnum == null)
            throw new RuntimeException("invalid serializer " + header.getSerializer());
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        ProtocolMessageTypeEnum messageTypeEnum = ProtocolMessageTypeEnum.getEnumByKey(header.getType());
        if(messageTypeEnum == null)
            throw new RuntimeException("invalid message type " + header.getType());

        switch (messageTypeEnum) {
            case REQUEST:
                RpcRequest request = serializer.deserialize(bufferBytes, RpcRequest.class);
                return new ProtocolMessage<>(header, request);
            case RESPONSE:
                RpcResponse responseMessage = serializer.deserialize(bufferBytes, RpcResponse.class);
                return new ProtocolMessage<>(header, responseMessage);
            case HEARTBEAT:
            case OTHERS:
            default:
                throw new RuntimeException("unknown message type " + header.getType());
        }
    }
}
