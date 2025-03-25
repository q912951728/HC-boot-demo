package com.ztj.hcboot.rabbitmq;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ztj.hcboot.pojo.SeckillMessage;
import com.ztj.hcboot.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQReceiver {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @RabbitListener(queues = "seckillQueue")
    public void receive(String message) {
        log.info("receive message:{}", message);

        try {
            SeckillMessage seckillMessage = objectMapper.readValue(message, SeckillMessage.class);
            Long userId = seckillMessage.getUserId();
            Long goodsId = seckillMessage.getGoodsId();

            //判断是否重复抢购
            String userOrderKey = "seckill:order:" + userId + ":" + goodsId;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(userOrderKey))) {
                return ;
            }
            orderService.seckill(userId, goodsId);

        } catch (Exception e) {
            log.error("消息转换异常：{}", e.getMessage());
        }
    }
}
