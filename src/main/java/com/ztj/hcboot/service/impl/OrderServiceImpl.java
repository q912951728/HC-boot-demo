package com.ztj.hcboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ztj.hcboot.mapper.GoodsMapper;
import com.ztj.hcboot.mapper.OrderMapper;
import com.ztj.hcboot.mapper.SeckillGoodsMapper;
import com.ztj.hcboot.pojo.Order;
import com.ztj.hcboot.pojo.SeckillGoods;
import com.ztj.hcboot.pojo.SeckillOrder;
import com.ztj.hcboot.service.IOrderService;
import com.ztj.hcboot.service.ISeckillGoodsService;
import com.ztj.hcboot.service.ISeckillOrderService;
import com.ztj.hcboot.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
*  张是小
* @author zsx
*/
@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private ISeckillOrderService seckillOrderService;

    @Autowired
    private ISeckillGoodsService seckillGoodsService;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private Integer count = 0;

    @Override
    @Transactional
    public void seckill(Long userId, Long goodsId) {

        //从数据库中再次确认没有该用户的秒杀订单
        boolean exists = seckillOrderService.count(new QueryWrapper<SeckillOrder>()
                .eq("user_id", userId).eq("goods_id", goodsId))>0;

        if (exists) {
            log.info("用户id:{} 已经秒杀过该 商品id:{}",userId,goodsId);
            return ;
        }

        //减少数据库中的库存
        SeckillGoods seckillGoods = seckillGoodsService.getById(goodsId);
        if (seckillGoods != null && seckillGoods.getStockCount() > 0) {
            // 使用乐观锁减少库存，确保高并发时库存一致
            int rows = seckillGoodsMapper.updateStock(goodsId);

            if (rows == 0) {
                log.warn("库存减少失败,回滚 Redis 库存");
                String stockKey = "seckill:stock:" + goodsId;
                redisTemplate.opsForValue().increment(stockKey); // 回滚 Redis 库存
                return ;
            }
        }


        //生成订单
        GoodsVo goods = goodsMapper.findGoodsVoDetail(goodsId);  // 获取商品详情
        if (goods == null) {
            return ;
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

        //生成秒杀订单
        SeckillOrder newSeckillOrder = new SeckillOrder();
        newSeckillOrder.setUserId(userId);
        newSeckillOrder.setOrderId(order.getId());
        newSeckillOrder.setGoodsId(goodsId);
        seckillOrderService.save(newSeckillOrder);

        //在redis标记该用户已秒杀
        String userOrderKey = "seckill:order:" + userId + ":" + goodsId;
        redisTemplate.opsForValue().set(userOrderKey, "1");

        System.out.println("count:" + count++);
    }
}
