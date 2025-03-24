package com.ztj.hcboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ztj.hcboot.mapper.SeckillGoodsMapper;
import com.ztj.hcboot.pojo.SeckillGoods;
import com.ztj.hcboot.service.ISeckillGoodsService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
*  张是小
* @author zsx
*/
@Service
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper, SeckillGoods> implements ISeckillGoodsService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostConstruct // Spring 容器启动时执行
    public void preloadSeckillStock() {
        //List<SeckillGoods> seckillGoodsList = seckillGoodsMapper.findAll(); // 查询所有秒杀商品
        List<SeckillGoods> seckillGoodsList =this.list(new QueryWrapper<SeckillGoods>());

        System.out.println("seckillGoodsList:"+seckillGoodsList);
        for (SeckillGoods goods : seckillGoodsList) {
            redisTemplate.opsForValue().set("seckill:stock:" + goods.getGoodsId(), String.valueOf(goods.getStockCount()));
        }
        System.out.println("秒杀商品库存已预加载到 Redis");
    }
}
