package com.atguigu.gmall.auth.service;

import com.atguigu.gmall.auth.config.JwtProperties;
import com.atguigu.gmall.auth.feign.GmallUmsClient;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.common.exception.AuthException;
import com.atguigu.gmall.common.utils.CookieUtils;
import com.atguigu.gmall.common.utils.IpUtils;
import com.atguigu.gmall.common.utils.JwtUtils;
import com.atguigu.gmall.ums.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@EnableConfigurationProperties(JwtProperties.class)
@Service
public class AuthService {

    @Autowired
    private GmallUmsClient gmallUmsClient;

    @Autowired
    private  JwtProperties jwtProperties;

    public void login(String loginName, String password, HttpServletRequest request, HttpServletResponse response) {

        //1.远程调用ums的接口，查询用户信息
        ResponseVo<UserEntity> userEntityResponseVo =
                this.gmallUmsClient.queryUser(loginName, password);
        UserEntity userEntity = userEntityResponseVo.getData();

        //2.判断用户信息是否为空，如果为空，直接抛出异常
        if (userEntity == null){
            throw new AuthException("用户名或者密码错误！");
        }

        try {
            //3.组装载荷信息，为了防止被盗用，需要加入ip地址
            HashMap<String, Object> map = new HashMap<>();
            map.put("userId", userEntity.getId());
            map.put("username", userEntity.getUsername());
            String ip = IpUtils.getIpAddressAtService(request);
            map.put("ip", ip);

            //4.生成kwt类型的token
            String token = JwtUtils.generateToken(map, this.jwtProperties.getPrivateKey(), this.jwtProperties.getExpire());

            //5.放入cookie中
            CookieUtils.setCookie(request, response, this.jwtProperties.getCookieName(), token, this.jwtProperties.getExpire()*60);
            //6.把用户昵称放入cookie中
            CookieUtils.setCookie(request, response, this.jwtProperties.getUnick(), userEntity.getNickname() , this.jwtProperties.getExpire()*60);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
