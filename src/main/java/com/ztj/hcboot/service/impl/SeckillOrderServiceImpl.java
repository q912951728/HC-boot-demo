package com.ztj.hcboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ztj.hcboot.mapper.GoodsMapper;
import com.ztj.hcboot.mapper.OrderMapper;
import com.ztj.hcboot.mapper.SeckillGoodsMapper;
import com.ztj.hcboot.mapper.SeckillOrderMapper;
import com.ztj.hcboot.pojo.Order;
import com.ztj.hcboot.pojo.SeckillGoods;
import com.ztj.hcboot.pojo.SeckillOrder;
import com.ztj.hcboot.service.ISeckillGoodsService;
import com.ztj.hcboot.service.ISeckillOrderService;
import com.ztj.hcboot.vo.GoodsVo;
import com.ztj.hcboot.vo.RespBean;
import com.ztj.hcboot.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements ISeckillOrderService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private ISeckillGoodsService seckillGoodsService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @Override
    @Transactional
    public RespBean doSeckill(Long userId, Long goodsId) {
//        //判断秒杀库存是否存在
//        Integer SeckillStock = seckillGoodsMapper.findSeckillGoodsStock(goodsId);
//
//
//        if (SeckillStock == null || SeckillStock <= 0) {
//            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
//        }
//
//        //判断是否重复抢购
//        SeckillOrder seckillOrder = this.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", userId).eq("goods_id", goodsId));
//
//
//        if(seckillOrder != null){
//            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
//        }
//
//        //减库存，下订单，写入秒杀订单
//        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goodsId));
//
//        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
//        seckillGoodsService.updateById(seckillGoods);
//
//        GoodsVo Goods = goodsMapper.findGoodsVoDetail(goodsId);
//        //生成订单
//        Order order = new Order();
//        order.setUserId(userId);
//        order.setGoodsId(goodsId);
//        order.setDeliveryAddrId(0L);
//        order.setGoodsName(Goods.getGoodsName());
//        order.setGoodsCount(1);
//        order.setGoodsPrice(Goods.getSeckillPrice());
//        order.setOrderChannel(1);
//        order.setStatus(0);
//        order.setCreateDate(new Date());
//        orderMapper.insert(order);
//        //生成秒杀订单
//        SeckillOrder newSeckillOrder = new SeckillOrder();
//        newSeckillOrder.setUserId(userId);
//        newSeckillOrder.setOrderId(order.getId());
//        newSeckillOrder.setGoodsId(goodsId);
//        this.save(newSeckillOrder);
//
//        return RespBean.success(order);


        //从 Redis 中获取库存
        String stockKey = "seckill:stock:" + goodsId;
        String seckillStock = redisTemplate.opsForValue().get(stockKey);
        if (seckillStock == null || Long.parseLong(seckillStock) <= 0) {
            // 如果库存不足，直接返回
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }


        //判断是否重复抢购
        String userOrderKey = "seckill:order:" + userId + ":" + goodsId;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(userOrderKey))) {
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }

        //预扣库存（减少 Redis 的库存）
        Long stock = redisTemplate.opsForValue().decrement(stockKey);
        if (stock == null || stock < 0) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        //同步减少数据库中的库存
        SeckillGoods seckillGoods = seckillGoodsMapper.selectById(goodsId);
        if (seckillGoods != null && seckillGoods.getStockCount() > 0) {
            // 使用乐观锁减少库存，确保高并发时库存一致
            int rows = seckillGoodsMapper.updateStock(goodsId);
            if (rows == 0) {
                // 如果更新失败，说明库存已经被其他线程扣减，返回库存不足
                redisTemplate.opsForValue().increment(stockKey); // 回滚 Redis 库存
                return RespBean.error(RespBeanEnum.EMPTY_STOCK);
            }
        }

        //生成订单
        GoodsVo goods = goodsMapper.findGoodsVoDetail(goodsId);  // 获取商品详情
        if (goods == null) {
            return RespBean.error(RespBeanEnum.GOODS_NOT_EXIST);
        }
        Order order = new Order();
        order.setUserId(userId);
        order.setGoodsId(goodsId);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsPrice(goods.getSeckillPrice());
        order.setGoodsCount(1);
        order.setDeliveryAddrId(0L);
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        order.setPayDate(null);
        orderMapper.insert(order);

        System.out.println("order::::" + order);

        //生成秒杀订单
        SeckillOrder newSeckillOrder = new SeckillOrder();
        newSeckillOrder.setUserId(userId);
        newSeckillOrder.setOrderId(order.getId());
        newSeckillOrder.setGoodsId(goodsId);
        this.save(newSeckillOrder);

        System.out.println("newSeckillOrder::::" + newSeckillOrder);

        //在redis标记该用户已秒杀
        redisTemplate.opsForValue().set(userOrderKey, "1");

        return RespBean.success(order);

    }

}
