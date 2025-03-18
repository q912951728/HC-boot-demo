package com.ztj.hcboot.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
*  张是小
* @author zsx
*/
@Data
@EqualsAndHashCode
@TableName("t_order")
public class Order  implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * 订单id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 收货地址id
     */
    private Long deliveryAddrId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品数量
     */
    private Integer goodsCount;

    /**
     * 商品单价
     */
    private BigDecimal goodsPrice;

    /**
     * 订单渠道，1-pc，2-app, 3-ios
     */
    private Integer orderChannel;

    /**
     * 订单状态，0-新建未支付，1-已支付，2-已发货，3-已收获，4-已退款，5-已完成
     */
    private Integer status;

    /**
     * 订单创建时间
     */
    private Date createDate;

    /**
     * 订单支付时间
     */
    private Date payDate;


}
