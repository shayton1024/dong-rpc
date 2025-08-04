package com.dongpi.dongrpc.fault.tolerant.request;

import com.dongpi.dongrpc.fault.tolerant.RetryWorker;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/08/04/15:25
 * @Description:
 */
public class FailRequestQueue {
    private final BlockingQueue<FailRequest> queue = new LinkedBlockingQueue<>();
    private static FailRequestQueue INSTANCE = new FailRequestQueue();

    private FailRequestQueue() {
        // 私有构造函数，防止外部实例化
        new Thread(new RetryWorker(queue), "Failback Retry Worker").start();
    }

    public static FailRequestQueue getInstance() {
        return INSTANCE;
    }

    public void add(FailRequest failRequest) {
        queue.offer(failRequest);
    }
}
