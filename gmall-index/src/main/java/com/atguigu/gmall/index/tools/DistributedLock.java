package com.atguigu.gmall.index.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

@Component
public class DistributedLock {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private Timer timer;

    public Boolean tryLock(String lockName,String uuid,Integer expire){
        String script = "if(redis.call('exists', KEYS[1]) == 0 or redis.call('hexists', KEYS[1], ARGV[1]) == 1)" +
                " then " +
                "   redis.call('hincrby', KEYS[1], ARGV[1], 1) " +
                "   redis.call('expire', KEYS[1], ARGV[2]) " +
                "   return 1 " +
                "else " +
                "   return 0 " +
                "end";
        //stringRedisTemplate.execute获取锁成功为true 失败为false
        if(!stringRedisTemplate.execute(new DefaultRedisScript<>(script,Boolean.class), Arrays.asList(lockName),uuid,expire.toString())){
            try {
                Thread.sleep(100);
                tryLock(lockName, uuid, expire);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else {
            renewExpire(lockName, uuid, expire);
        }
            return true;

    }

    public void unLock(String lockName,String uuid){
        String script = "if(redis.call('hexists', KEYS[1], ARGV[1]) == 0)" +
                " then" +
                "   return nil " +
                "elseif(redis.call('HINCRBY', KEYS[1], ARGV[1], -1) == 0)" +
                " then" +
                "   return redis.call('del', KEYS[1])" +
                " else" +
                "   return 0" +
                " end";
        Long flag = stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList(lockName), uuid);
        if (flag == null){
            throw new RuntimeException("你在尝试解除别人的锁或者你尝试解除不存在的锁");
        }else if(flag == 1){
            timer.cancel();
        }
    }

    private void renewExpire(String lockName,String uuid,Integer expire){
        String script = "if(redis.call('hexists', KEYS[1], ARGV[1]) == 1) then return redis.call('expire', KEYS[1], ARGV[2]) else return 0 end";
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                stringRedisTemplate.execute(new DefaultRedisScript<>(script,Boolean.class), Arrays.asList(lockName), uuid,expire.toString());
            }
        }, expire * 1000 /3,expire*1000/3);
    }

}
