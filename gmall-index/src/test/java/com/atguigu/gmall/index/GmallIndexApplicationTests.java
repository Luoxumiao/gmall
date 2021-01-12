package com.atguigu.gmall.index;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GmallIndexApplicationTests {

    @Autowired
    private RedissonClient redissonClient;

    @Test
    void contextLoads() {
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter("bloomFilter");
        bloomFilter.tryInit(10, 0.3);
        bloomFilter.add("1");
        bloomFilter.add("2");
        bloomFilter.add("3");
        bloomFilter.add("4");
        System.out.println(bloomFilter.contains("1"));

    }

    public static void main(String[] args) {
//        System.out.println(StringUtils.isNotBlank("null"));
//        System.out.println(StringUtils.isNotBlank("G"));
//        System.out.println(StringUtils.equals("null", null));

        BloomFilter<CharSequence> bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), 10, 0.3);
        bloomFilter.put("1");
        bloomFilter.put("2");
        bloomFilter.put("3");
        bloomFilter.put("4");
        System.out.println(bloomFilter.mightContain("1"));

    }

}
