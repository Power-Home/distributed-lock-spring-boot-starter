package com.hompan.starter;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;

import java.util.concurrent.TimeUnit;

/**
 * Usage:
 * customized redis template
 */

public class MyRedisTemplate{
    private RedisTemplate<String,String> redisTemplate;

    public void setRedisTemplate(RedisTemplate<String,String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Boolean setifAbsent(String key, String value, long timeoutMilis){
        Boolean execute = redisTemplate.execute(new MyRedisCallback(key,value,timeoutMilis));
        return execute;
    }

    class MyRedisCallback implements RedisCallback<Boolean>{
        private String key;
        private String value;
        private long timeoutMilis;

        public MyRedisCallback(String key, String value, long timeoutMilis) {
            this.key = key;
            this.value = value;
            this.timeoutMilis = timeoutMilis;
        }

        @Override
        public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
            RedisStringCommands redisStringCommands = connection.stringCommands();
            Boolean set = redisStringCommands.set(key.getBytes(), value.getBytes(), Expiration.milliseconds(timeoutMilis), RedisStringCommands.SetOption.SET_IF_ABSENT);
            return set;
        }
    }

    public Boolean expire(String key, long time) {
        return redisTemplate.expire(key,time,TimeUnit.MILLISECONDS);
    }

    public Boolean delete(String key) {
        //redisTemplate.execute("lua script");
        return redisTemplate.delete(key);
    }
}
