package com.atguigu.gmall.pms.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

import javax.annotation.PostConstruct;

@Configuration
@Slf4j
public class RabbitConfig {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init(){
        //不管消息是否有没有到达交换机，都会执行该回调
        rabbitTemplate.setConfirmCallback((@Nullable CorrelationData correlationData, boolean ack, @Nullable String cause) ->{
            if (!ack){
                log.error("消息没有到达交换机，原因：{}，",cause);
            }
        });
        //如果消息没有到达队列，才会执行
        rabbitTemplate.setReturnCallback((Message message, int replyCode, String replyText, String exchange, String routingkey)->{
            log.warn("消息没有到达交换机，交换机：{}，路由键：{}，消息内容：{}",exchange,routingkey,new String(message.getBody()));
        });
    }
}
