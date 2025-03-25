package com.ztj.hcboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ztj.hcboot.pojo.Order;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  服务类
 * </p>
*  张是小
* @author zsx
*/
public interface IOrderService extends IService<Order> {

    /**
     * 处理生成秒杀订单
     *
     * @param userId
     * @param goodsId
     */
    void seckill(@Param("userId") Long userId, @Param("goodsId") Long goodsId);
}
