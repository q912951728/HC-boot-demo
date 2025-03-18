package com.ztj.hcboot.pojo;

import lombok.Data;


/**
 * <p>
 * 
 * </p>
*  张是小
* @author zsx
*/
@Data
public class SeckillOrder {


    /**
     * 秒杀订单id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 商品id
     */
    private Long goodsId;


}
