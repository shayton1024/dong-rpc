package com.dongpi.dongrpc.fault.tolerant;

import com.dongpi.dongrpc.RpcApplication;
import com.dongpi.dongrpc.fault.retry.RetryStrategy;
import com.dongpi.dongrpc.fault.retry.RetryStrategyFactory;
import com.dongpi.dongrpc.fault.tolerant.request.FailRequest;
import com.dongpi.dongrpc.model.RpcRequest;
import com.dongpi.dongrpc.model.RpcResponse;
import com.dongpi.dongrpc.model.ServiceMetaInfo;
import com.dongpi.dongrpc.server.tcp.VertxTcpClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/08/04/15:30
 * @Description:
 */
public class RetryWorker implements Runnable {
    private final BlockingQueue<FailRequest> queue;

    public RetryWorker(BlockingQueue<FailRequest> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while(true) {
            try {
                // 从队列中获取失败请求
                FailRequest failRequest = queue.take();

                RpcRequest rpcRequest = failRequest.getRpcRequest();
                ServiceMetaInfo serviceMetaInfo = failRequest.getServiceMetaInfo();
                List<ServiceMetaInfo> serviceMetaInfoList = failRequest.getServiceMetaInfoList();

                System.out.println("[FailBack] 异步补偿重试：" + serviceMetaInfo.getServiceName());

                // 使用策略重试机制
                RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(RpcApplication.getRpcConfig().getRetryStrategy());
                retryStrategy.doRetry(() -> VertxTcpClient.doRequest(rpcRequest, serviceMetaInfo));
            } catch (Exception e) {
                System.err.println("[FailBack] 重试失败：" + e.getMessage());
                // 可选：写日志、警告、再次入队
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException exception) {}
        }
    }
}
