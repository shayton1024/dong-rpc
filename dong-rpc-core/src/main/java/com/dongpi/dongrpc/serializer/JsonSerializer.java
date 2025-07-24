package com.dongpi.dongrpc.serializer;
import com.dongpi.dongrpc.model.RpcRequest;
import com.dongpi.dongrpc.model.RpcResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/06/27/17:12
 * @Description:
 */
public class JsonSerializer implements Serializer {

    /**
     * 为每个线程提供独立的 ObjectMapper 实例
     */
    private static final ThreadLocal<ObjectMapper> MAPPER_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        ObjectMapper mapper = new ObjectMapper();
        // 可以在这里配置 ObjectMapper
        return mapper;
    });


    /**
     * 获取当前线程的 ObjectMapper
     */
    private ObjectMapper getMapper() {
        return MAPPER_THREAD_LOCAL.get();
    }

    /**
     * 进行对象序列化
     * @param obj
     * @return
     * @param <T>
     * @throws IOException
     */
    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        return getMapper().writeValueAsBytes(obj);
    }

    /**
     * 进行对象反序列化
     * @param bytes
     * @param clazz
     * @return
     * @param <T>
     * @throws IOException
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        T obj = getMapper().readValue(bytes, clazz);
        // 参数类型处理
        if(obj instanceof RpcRequest) {
            return handleRequest((RpcRequest) obj, clazz);
        }
        if(obj instanceof RpcResponse) {
            return handleResponse((RpcResponse) obj, clazz);
        }

        return obj;
    }

    /**
     * 处理RpcRequest参数泛型
     * @param request
     * @param clazz
     * @return
     * @param <T>
     * @throws IOException
     */
    private <T> T handleRequest(RpcRequest request, Class<T> clazz) throws IOException {
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] args = request.getArgs();

        // 循环处理每一个参数类型
        for(int i = 0;i < parameterTypes.length;i++) {
            Class<?> parameterType = parameterTypes[i];
            if(parameterType.isAssignableFrom(RpcRequest.class)) {
                byte[] argBytes = getMapper().writeValueAsBytes(args[i]);
                args[i] = getMapper().readValue(argBytes, parameterType);
            }
        }
        return clazz.cast(request);
    }

    /**
     * 处理RpcResponse参数泛型
     * @param response
     * @param clazz
     * @return
     * @param <T>
     * @throws IOException
     */
    private <T> T handleResponse(RpcResponse response, Class<T> clazz) throws IOException {
        Object data = response.getData();
        Class<?> dataType = response.getDataType();

        if(data != null && dataType != null && !dataType.isAssignableFrom(data.getClass())) {
            try{
                ObjectMapper mapper = getMapper();
                // 将data转化为JSON字符串
                byte[] bytes = mapper.writeValueAsBytes(data);
                data = mapper.readValue(bytes, dataType);
                response.setData(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return clazz.cast(response);
    }
}
