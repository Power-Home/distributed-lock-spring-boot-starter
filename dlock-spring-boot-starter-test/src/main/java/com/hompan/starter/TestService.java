package com.hompan.starter;


import com.hompan.starter.annotation.Fallback;
import com.hompan.starter.annotation.RedisSynchronized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestService {
    @Autowired
    private RetryTask retryTask;

    @GetMapping("/a")
    @RedisSynchronized(value = "talk",fallbackMethod = "shutup")
    public String myTurn(@RequestParam String speak,@RequestParam int count){
        return speak;
    }

    @RequestMapping("/b")
    @Fallback(value = "shutup")
    private void notYourTurn(RedisLockJoinPoint redisLockJoinPoint){
        Object[] arguments = redisLockJoinPoint.getArguments();
        int count = (int) arguments[1];
        arguments[1]=++count;
        if(count <5){//失败后重试4次
            retryTask.retry(redisLockJoinPoint);
        }
    }

    @Fallback(value = "shutup2",replaceReturn = true)
    private String notYourTurn2(RedisLockJoinPoint redisLockJoinPoint){
        return "silence";
    }
}
