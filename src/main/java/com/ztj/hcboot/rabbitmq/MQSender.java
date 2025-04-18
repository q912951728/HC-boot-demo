package com.ztj.hcboot.rabbitmq;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * 发送秒杀消息
     * @param message
     */
    public void send(Object message) {
        log.info("send message: {}", message);
        rabbitTemplate.convertAndSend("seckillExchange","seckill.message", message);
    }
}
