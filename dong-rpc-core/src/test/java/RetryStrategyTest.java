import com.dongpi.dongrpc.fault.retry.RetryStrategy;
import com.dongpi.dongrpc.fault.retry.impl.FixedIntervalRetryStrategy;
import com.dongpi.dongrpc.fault.retry.impl.NoRetryStrategy;
import com.dongpi.dongrpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/08/01/16:48
 * @Description: 测试重试策略
 */
@Slf4j
public class RetryStrategyTest {

//    RetryStrategy retryStrategy = new NoRetryStrategy();
    RetryStrategy retryStrategy = new FixedIntervalRetryStrategy();

    @Test
    public void doRetry() {
        try {
            RpcResponse rpcResponse = retryStrategy.doRetry(() -> {
                log.info("执行重试逻辑");
                // 模拟抛出异常
                throw new RuntimeException("模拟异常");
            });
            log.info("重试结果: {}", rpcResponse);
        } catch (Exception e) {
            System.out.println("重试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
