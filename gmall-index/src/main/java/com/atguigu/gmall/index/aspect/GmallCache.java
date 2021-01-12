package com.atguigu.gmall.index.aspect;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface GmallCache {

    //缓存key的前缀
    //key:prefix+":"+方法参数
    String prefix() default "";

    //缓存默认时间5分钟，单位是分钟，指定缓存时间
    int timeout() default 5;

    //为了防止缓存雪崩，让注解使用人员指定时间随机值范围，默认5分钟
    int random() default 5;

    //为了防止缓存击穿，需要加入分布式锁,需要指定不同的key，默认是lock
    //让注解使用人员指定分布式锁的key的前缀 key的格式 lock +":"+ 方法参数
    String lock() default "lock";
}
