package com.hompan.starter;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Usage:
 * test use the async to retry
 */
@Component
public class RetryTask {
    @Async
    public void retry(RedisLockJoinPoint jp){
        Object[] arguments = jp.getArguments();
        int count = (int) arguments[1];
        try {
            Thread.sleep(1000);
            System.out.println("retry:"+count);
            jp.proceed();
        } catch (Throwable throwable) {

            System.out.println("retry failed:"+count);
        }
    }
}
