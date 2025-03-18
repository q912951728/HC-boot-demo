package com.ztj.hcboot.pojo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * 
 * </p>
*  张是小
* @author zsx
*/
@Data
public class Goods{


    /**
     * 商品id
     */
    private Long id;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品标题
     */
    private String goodsTitle;

    /**
     * 商品图片
     */
    private String goodsImg;

    /**
     * 商品详情
     */
    private String goodsDetail;

    /**
     * 商品价格
     */
    private BigDecimal goodsPrice;

    /**
     * 商品库存，-1表示没有限制
     */
    private Integer goodsStock;


}
