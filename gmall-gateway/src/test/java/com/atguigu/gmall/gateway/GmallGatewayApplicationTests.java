package com.atguigu.gmall.gateway;

import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GmallGatewayApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void test(){
        String a = "";
//        System.out.println(StringUtils.isNotBlank(a));
        System.out.println(StringUtils.equals(null, a));
        System.out.println(StringUtils.equals(null, " "));
        System.out.println(StringUtils.equals(null, null));

    }

}
