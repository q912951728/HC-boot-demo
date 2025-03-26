package com.ztj.hcboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ztj.hcboot.mapper.SeckillOrderMapper;
import com.ztj.hcboot.pojo.SeckillGoods;
import com.ztj.hcboot.pojo.SeckillMessage;
import com.ztj.hcboot.pojo.SeckillOrder;
import com.ztj.hcboot.rabbitmq.MQSender;
import com.ztj.hcboot.service.ISeckillGoodsService;
import com.ztj.hcboot.service.ISeckillOrderService;
import com.ztj.hcboot.vo.RespBean;
import com.ztj.hcboot.vo.RespBeanEnum;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements ISeckillOrderService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private MQSender mqSender;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ISeckillGoodsService seckillGoodsService;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private RedisScript<Long> script;


    //内存标记，减少 Redis 访问
    private final Map<Long, Boolean> emptyStockMap = new HashMap<>();

    private Integer count = 0;

    @Override
    @Transactional
    public RespBean doSeckill(Long userId, Long goodsId) {
        //检查内存标记，减少 Redis 访问
        if (Boolean.TRUE.equals(emptyStockMap.get(goodsId))) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        //使用 Redis 原子操作预扣库存
        String stockKey = "seckill:stock:" + goodsId;
        //Long stock = redisTemplate.opsForValue().decrement(stockKey);
        Long stock = redisTemplate.execute(script, Collections.singletonList(stockKey));
        if(stock == null || stock < 0){
            emptyStockMap.put(goodsId, true);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //防止重复下单
        String userOrderKey = "seckill:order:" + userId + ":" + goodsId;
        Boolean isOrdered = redisTemplate.hasKey(userOrderKey);
        if (Boolean.TRUE.equals(isOrdered)) {
            // 已下单，回滚库存
            redisTemplate.opsForValue().increment(stockKey);
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }

        //发送消息队列处理后续订单逻辑
        try {
            String message = objectMapper.writeValueAsString(new SeckillMessage(userId, goodsId));
            mqSender.send(message);
            count++;
            System.out.println("countimpl:"+count);

        } catch (JsonProcessingException e) {
            log.error("消息转换异常：{}", e.getMessage());
            // MQ 发送失败，回滚库存 & 删除订单 key
            redisTemplate.opsForValue().increment(stockKey);
            redisTemplate.delete(userOrderKey);
            return RespBean.error(RespBeanEnum.MQ_SEND_ERROR);
        }

        return RespBean.success(0); // 0 代表排队中
    }

    /**
     * 获取秒杀结果
     * @param userId
     * @param goodsId
     * @return  orderId:成功 -1:失败  0:排队中
     */
    @Override
    public Long getSeckillResult(Long userId, Long goodsId) {
        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(new QueryWrapper<SeckillOrder>().eq("user_id", userId).eq("goods_id", goodsId));

        System.out.println("seckillOrder:"+seckillOrder);

        if(seckillOrder != null) {
            return seckillOrder.getOrderId();
        }else if (Boolean.TRUE.equals(redisTemplate.hasKey("seckill:order:" + userId + ":" + goodsId))) {
            return 0L;
        }else {
            return -1L;
        }

    }

    @Override
    public RespBean getSeckillPath(Long userId, Long goodsId) {
        UUID uuid = UUID.randomUUID();
        redisTemplate.opsForValue().set("seckillPath:" + userId + ":" + goodsId, uuid.toString(), 60, TimeUnit.MINUTES);

        return RespBean.success(uuid.toString());
    }

    /**
     * 校验秒杀路径
     * @param userId
     * @param path
     * @param goodsId
     * @return
     */
    @Override
    public boolean equalsSeckillPath(Long userId, String path, Long goodsId) {
        return Objects.equals(redisTemplate.opsForValue().get("seckillPath:" + userId + ":" + goodsId), path);
    }

    @PostConstruct
    public void preloadSeckillStock() {
        List<SeckillGoods> seckillGoodsList = seckillGoodsService.list(new QueryWrapper<>());
        for (SeckillGoods goods : seckillGoodsList) {
            String stockKey = "seckill:stock:" + goods.getGoodsId();
            redisTemplate.opsForValue().set(stockKey, String.valueOf(goods.getStockCount()));
            emptyStockMap.put(goods.getGoodsId(), false);
        }
        log.info("秒杀商品库存已预加载到 Redis: {}", seckillGoodsList);
    }
}
