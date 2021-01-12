package com.atguigu.gmall.auth;

import com.atguigu.gmall.common.utils.JwtUtils;
import com.atguigu.gmall.common.utils.RsaUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {

    // 别忘了创建D:\\project\rsa目录
    private static final String pubKeyPath = "E:\\abc\\rsa\\rsa.pub";
    private static final String priKeyPath = "E:\\abc\\rsa\\rsa.pri";


    private PublicKey publicKey;


    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    //    @BeforeEach
    @BeforeEach
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "11");
        map.put("username", "liuyan");
        // 生成token
        String token = JwtUtils.generateToken(map, privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6IjExIiwidXNlcm5hbWUiOiJsaXV5YW4iLCJleHAiOjE2MDk2NTg2NTl9.EsZmzMYYh6fjotqpYfmseKOjIORfsfRFOwkyzkweYHGlEAkrkx9fDKXw8gleEDHfZo0TE6SMLCatPlxCDi1h_YpO-pksDKF4o7N42IeBepNvZ2umKhsxoGIeDze61c0kWT__60jzVjlGXaS8_Pzt_-UJfiZlsLM70U6uhjL5O-A9cI7oUrc_93B0KrY-NX9vLXzDxJ5LW52QXodruD7CBt0PhTsxerr_rQmrXw-HQ8eIRbcq7nlgf2mxWVxs0Xw2JkyCYJTjfD0ICmwmI_aXLaPB6VMpZbSAr8SW19xQdzppKkJtTCMtvSTTr_qywjZaAb0hwEOdtWkp5vGKzr2UPA";

        // 解析token
        Map<String, Object> map = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + map.get("id"));
        System.out.println("userName: " + map.get("username"));
    }
}
