package com.atguigu.gmall.index.aspect;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class GmallCacheAspect {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RBloomFilter bloomFilter;

    //获取目标方法参数 joinPoint.getArgs()
    //获取目标对象类   joinPoint.getTarget().getClass()
    //获取目标方法签名，通过方法签名可以获取目标方法所有的信息 joinPoint.getSignature()
    @Around("@annotation(GmallCache)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
        //获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取连接点方法对象
        Method method = signature.getMethod();
        //获取方法上的注解对象
        GmallCache gmallCache = method.getAnnotation(GmallCache.class);
        //获取注解中的缓存前缀属性
        String prefix = gmallCache.prefix();

        //获取目标方法参数的列表
        List<Object> args = Arrays.asList(joinPoint.getArgs());

        //组装成缓存的key
        String key = prefix + ":" + args;

        //不存在返回false，直接返回null
        boolean flag = bloomFilter.contains(key);
        if (!flag){
            return null;
        }

        //1.查询缓存，判断缓存是否命中，命中的话直接返回
        String json = redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(json)){
            return JSON.parseObject(json, method.getReturnType());
        }
        //2.如果没有命中，要执行目标方法为了防止缓存击穿，添加分布式锁
        String lock = gmallCache.lock();
        RLock fairLock = redissonClient.getFairLock(lock + ":" + args);
        fairLock.lock();
        try {
            //3.查询缓存，判断缓存是否命中，命中的话直接返回
            String json2 = redisTemplate.opsForValue().get(key);
            if (StringUtils.isNotBlank(json2)){
                return JSON.parseObject(json2, method.getReturnType());
            }

            //4.执行目标方法
            Object result = joinPoint.proceed(joinPoint.getArgs());

            //5.把目标方法的返回数据放入缓存，并释放分布式锁
            if (result != null){
                int timeout = gmallCache.timeout() + new Random().nextInt(gmallCache.random());
                redisTemplate.opsForValue().set(key, JSON.toJSONString(result),timeout, TimeUnit.MINUTES);
            }

            return result;
        } finally {
            fairLock.unlock();
        }
    }

}
