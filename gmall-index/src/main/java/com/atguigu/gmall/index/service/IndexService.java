package com.atguigu.gmall.index.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.index.aspect.GmallCache;
import com.atguigu.gmall.index.feign.GmallPmsClient;
import com.atguigu.gmall.index.tools.DistributedLock;
import com.atguigu.gmall.pms.eneity.CategoryEntity;
import org.apache.commons.lang.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class IndexService {

    @Autowired
    private GmallPmsClient gmallPmsClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private DistributedLock distributedLock;

    @Autowired
    private RedissonClient redissonClient;

    private static final String KEY_PREFIX = "index:cates";

    public List<CategoryEntity> queryLvl1CategoriesByPid(){

//        String s = stringRedisTemplate.opsForValue().get(KEY_PREFIX + 0l);
//        if (StringUtils.isNotBlank(s)){
//            return JSON.parseArray(s, CategoryEntity.class);
//        }
        ResponseVo<List<CategoryEntity>> responseVo = gmallPmsClient.queryCatgoriesByPid(0l);

//      stringRedisTemplate.opsForValue().set(KEY_PREFIX + 0l, JSON.toJSONString(responseVo.getData()),60, TimeUnit.SECONDS);

        return responseVo.getData();

    }

    @GmallCache(prefix = KEY_PREFIX,timeout = 43200,random = 4320,lock = "index:lock")
    public List<CategoryEntity> querylvl2CategoriesWithSubByPid(Long pid) {

        ResponseVo<List<CategoryEntity>> listResponseVo = gmallPmsClient.queryCategoriesWithSubsByPid(pid);
        List<CategoryEntity> data = listResponseVo.getData();



        return data;
    }

    public List<CategoryEntity> querylvl2CategoriesWithSubByPid2(Long pid) {

        //1.先查询缓存
        String json = stringRedisTemplate.opsForValue().get(KEY_PREFIX + pid);
        //!StringUtils.equals("null", json)是因为后面存redis时null也会存进缓存
        if (StringUtils.isNotBlank(json) && !StringUtils.equals("null", json)){
            return JSON.parseArray(json, CategoryEntity.class);
        }else if(StringUtils.equals("null", json)) {
            return null;
        }

        RLock lock = redissonClient.getLock("index:lock:" + pid);
        lock.lock();

        //获取锁过程中可能有其他请求，已经提前获取到锁，并把数据放入缓存中
        String json2 = stringRedisTemplate.opsForValue().get(KEY_PREFIX + pid);
        //!StringUtils.equals("null", json)是因为后面存redis时null也会存进缓存
        if (StringUtils.isNotBlank(json2) && !StringUtils.equals("null", json2)){
            return JSON.parseArray(json2, CategoryEntity.class);
        }else if(StringUtils.equals("null", json2)) {
            return null;
        }

        ResponseVo<List<CategoryEntity>> listResponseVo = gmallPmsClient.queryCategoriesWithSubsByPid(pid);
        List<CategoryEntity> data = listResponseVo.getData();

        //2.放入缓存
        if (CollectionUtils.isEmpty(data)){
            //为了避免缓存穿透，空值也需要缓存进redis(应急方案)，最好的方法就是加布隆过滤器
            stringRedisTemplate.opsForValue().set(KEY_PREFIX + pid, JSON.toJSONString(data),3, TimeUnit.MINUTES);
        }else {
            //为了防止缓存雪崩，给缓存时间添加随机值
            stringRedisTemplate.opsForValue().set(KEY_PREFIX + pid, JSON.toJSONString(data),30 + new Random().nextInt(10), TimeUnit.DAYS);
        }

        return data;
    }

    public void testLock() {
        String uuid = UUID.randomUUID().toString();

        //获取锁
        RLock lock = redissonClient.getLock("lock");
        lock.lock();
        try {

            String number = stringRedisTemplate.opsForValue().get("number");
            if (StringUtils.isBlank(number)) {
                return;
            }
            int num = Integer.parseInt(number);
            stringRedisTemplate.opsForValue().set("number", String.valueOf(++num));
        } finally {

            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            lock.unlock();
        }

    }

    public /*synchronized*/ void testLock3() {
        String uuid = UUID.randomUUID().toString();

        //获取锁,不存在时获取锁相当于setnx,加过期时间防止死锁，设置uuid防止误删别的线程的锁
        Boolean flag = distributedLock.tryLock("lock", uuid, 120);
        if (flag){
            String number = stringRedisTemplate.opsForValue().get("number");
            if (StringUtils.isBlank(number)) {
                return;
            }
            int num = Integer.parseInt(number);
            stringRedisTemplate.opsForValue().set("number", String.valueOf(++num));
            //testSubLock(uuid);
            try {
                TimeUnit.SECONDS.sleep(90);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            distributedLock.unLock("lock", uuid);
        }
    }

    public void testSubLock(String uuid){
        distributedLock.tryLock("lock", uuid, 120);

        System.out.println("测试可重入锁");

        distributedLock.unLock("lock", uuid);
    }

    public /*synchronized*/ void testLock2() {

        //获取锁,不存在时获取锁相当于setnx,加过期时间防止死锁，设置uuid防止误删别的线程的锁
        String uuid = UUID.randomUUID().toString();
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid,3,TimeUnit.SECONDS);
        if (!flag){
            try {
                //获取锁失败，重试获取锁(自旋)
                Thread.sleep(200);
                testLock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else {
            String number = stringRedisTemplate.opsForValue().get("number");
            if (StringUtils.isBlank(number)) {
                return;
            }
            int num = Integer.parseInt(number);
            stringRedisTemplate.opsForValue().set("number", String.valueOf(++num));

            //释放锁，要保证删除和判断的原子性
            String script = "if(redis.call('get',KEYS[1]) == ARGV[1]) then return redis.call('del','lock') end";
            stringRedisTemplate.execute(new DefaultRedisScript<>(script,Boolean.class), Arrays.asList("lock"), uuid);
//            if (StringUtils.equals(uuid, stringRedisTemplate.opsForValue().get("lock"))){
//                stringRedisTemplate.delete("lock");
//            }

        }

    }

    public void testRead() {
        RReadWriteLock rwLock = redissonClient.getReadWriteLock("rwLock");
        rwLock.readLock().lock(10,TimeUnit.SECONDS);
    }

    public void testWrite() {
        RReadWriteLock rwLock = redissonClient.getReadWriteLock("rwLock");
        rwLock.writeLock().lock(10,TimeUnit.SECONDS);
    }

    public void testLatch() {
        RCountDownLatch latch = redissonClient.getCountDownLatch("latch");
        latch.trySetCount(6);

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void testCountDown() {
        RCountDownLatch latch = redissonClient.getCountDownLatch("latch");
        latch.countDown();
    }
}
