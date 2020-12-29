package com.hompan.starter.autoproxy;

import com.hompan.starter.RedisLock;
import com.hompan.starter.RedisLockJoinPoint;
import com.hompan.starter.ReflectiveMethodInvocation;
import com.hompan.starter.annotation.Fallback;
import com.hompan.starter.annotation.FallbackHandler;
import com.hompan.starter.annotation.RedisSynchronized;
import com.hompan.starter.exception.IllegalReturnException;
import com.hompan.starter.exception.LockFailedException;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Usage:
 * An auto proxy creator that builds proxies for specific beans
 * based on detected Advisors for each bean.
 */

public class RedisLockAutoProxyCreator implements BeanPostProcessor,ApplicationContextAware{
    private String prefix = "";
    public String getPrefix() {
        return prefix;
    }
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    private RedisLock redisLock;

    public void setRedisLock(RedisLock redisLock) {
        this.redisLock = redisLock;
    }

    private Map<String,ReflectiveMethodInvocation> fallbackInvocations = new HashMap<String,ReflectiveMethodInvocation>();

    private Map<String,ReflectiveMethodInvocation> fallbackHandlerInvocations = new HashMap<String,ReflectiveMethodInvocation>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        fillInvocationMap(bean,AopUtils.getTargetClass(bean).getName()+"",fallbackInvocations);
        return bean;
    }

    private void fillInvocationMap(Object bean,String keyPrefix,Map<String,ReflectiveMethodInvocation> map){
        Class<?> aClass = AopUtils.getTargetClass(bean);
        for (Method method : aClass.getDeclaredMethods()) {
            if(method.isAnnotationPresent(Fallback.class)){
                Fallback annotation = method.getAnnotation(Fallback.class);
                String key = keyPrefix+annotation.value();
                method.setAccessible(true);
                ReflectiveMethodInvocation reflectiveMethodInvocation = new ReflectiveMethodInvocation(bean, method, null,annotation.replaceReturn());
                map.put(key,reflectiveMethodInvocation);
            }
        }
    }
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> aClass =  AopUtils.getTargetClass(bean);
        for (Method method : aClass.getDeclaredMethods()) {
            if(method.isAnnotationPresent(RedisSynchronized.class)){
                //创建动态代理
                Enhancer enhancer = new Enhancer();
                enhancer.setSuperclass(aClass);
                enhancer.setCallback(new MyHandler(bean));
                return enhancer.create();
            }
        }
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(FallbackHandler.class);
        for (Object bean :
                beansWithAnnotation.values()) {
            fillInvocationMap(bean,"",fallbackHandlerInvocations);

        }
    }

    class MyHandler implements InvocationHandler {
        private Object o;
        MyHandler(Object o){
            this.o=o;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if(!method.isAnnotationPresent(RedisSynchronized.class)){
                return method.invoke(o, args);
            }
            String key = getKey(o,method,args);
            boolean locked = redisLock.lock(key);
            if(locked){
                try{
                    return method.invoke(o, args);
                }finally {
                    redisLock.unlock(key);
                }
            }

            RedisSynchronized annotation = method.getAnnotation(RedisSynchronized.class);
            String fallbackMethod = annotation.fallbackMethod();
            if(!"".equals(fallbackMethod)){
                ReflectiveMethodInvocation invocation = fallbackInvocations.get(AopUtils.getTargetClass(o).getName()+fallbackMethod);

                if(null == invocation){
                    invocation = fallbackHandlerInvocations.get(fallbackMethod);
                    if(null == invocation)
                        throw new NoSuchMethodException("no method named \"" + fallbackMethod +"\"");
                }
                RedisLockJoinPoint redisLockJoinPoint = new RedisLockJoinPoint(proxy, method, args);

                if(invocation.isReplaceReturn()) {
                    if(!method.getReturnType().isAssignableFrom(invocation.getMethod().getReturnType())){
                        throw new IllegalReturnException("return type illegal,require:"+method.getReturnType()
                                +",but returned:"+invocation.getMethod().getReturnType());
                    }
                    return invocation.proceed(redisLockJoinPoint);
                }else try {
                    invocation.proceed(redisLockJoinPoint);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            throw new LockFailedException("lock failed");
        }

        private String getKey(Object o, Method method, Object[] args) {
            RedisSynchronized annotation = method.getAnnotation(RedisSynchronized.class);
            String key = annotation.value();
            if("".equals(key)){
                key = method.toGenericString();
            }
            return prefix+key;
        }
    }
}
