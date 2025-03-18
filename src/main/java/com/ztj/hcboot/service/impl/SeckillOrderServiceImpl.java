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
import org.springframework.stereotype.Service;

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


    @Override
    public RespBean doSeckill(Long userId, Long goodsId) {
        //判断秒杀库存是否存在
        Integer SeckillStock = seckillGoodsMapper.findSeckillGoodsStock(goodsId);


        if (SeckillStock == null || SeckillStock <= 0) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        //判断是否重复抢购
        SeckillOrder seckillOrder = this.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", userId).eq("goods_id", goodsId));


        if(seckillOrder != null){
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }

        //减库存，下订单，写入秒杀订单
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goodsId));

        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        seckillGoodsService.updateById(seckillGoods);

        GoodsVo Goods = goodsMapper.findGoodsVoDetail(goodsId);
        //生成订单
        Order order = new Order();
        order.setUserId(userId);
        order.setGoodsId(goodsId);
        order.setDeliveryAddrId(0L);
        order.setGoodsName(Goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(Goods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);
        //生成秒杀订单
        SeckillOrder newSeckillOrder = new SeckillOrder();
        newSeckillOrder.setUserId(userId);
        newSeckillOrder.setOrderId(order.getId());
        newSeckillOrder.setGoodsId(goodsId);
        this.save(newSeckillOrder);


        return RespBean.success(order);
    }

}
