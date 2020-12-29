package com.hompan.starter;

import com.hompan.starter.annotation.Fallback;
import com.hompan.starter.annotation.FallbackHandler;

/**
 * Usage:
 * test global fallback handle
 * Description:
 */
@FallbackHandler
public class TestFallbackHandler {
    @Fallback(value = "shutup")
    private void hah(RedisLockJoinPoint redisLockJoinPoint){
        System.out.println("finally you can't speak!!!");
    }
}
