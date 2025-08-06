package com.dongpi.examplespringbootconsumer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/08/06/16:37
 * @Description:
 */
@SpringBootTest
public class ExampleServiceImplTest {
    @Resource
    private ExampleServiceImpl exampleService;

    @Test
    public void test() {
        exampleService.test();
    }
}
