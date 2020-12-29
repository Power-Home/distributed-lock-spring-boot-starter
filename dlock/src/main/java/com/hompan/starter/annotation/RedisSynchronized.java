package com.hompan.starter.annotation;

import java.lang.annotation.*;
import java.lang.reflect.Method;

/**
 * Usage:
 * Marks a method if you want to make the method executing serializable in the distributed system.
 *
 * {@link #value()} define the value which is a part of the key of redis,
 * @see .autoproxy.RedisLockAutoProxyCreator.MyHandler getKey(Object, Method, Object[]) .
 *
 * {@link #fallbackMethod()} identify which fallback method should go in if current lock method
 * failed,
 * @see Fallback#value() ,throw {@link .exception.LockFailedException} by default.
 *
 */

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisSynchronized {
    String value() default "";
    String fallbackMethod() default "";
}
