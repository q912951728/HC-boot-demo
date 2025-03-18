package com.ztj.hcboot.service;

import com.ztj.hcboot.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
*  张是小
* @author zsx
*/
public interface IGoodsService{

    /**
     * 查询商品列表
     * @return
     */
    List<GoodsVo> findGoodsVo();

    /**
     * 查询商品详情
     * @param goodsId
     * @return
     */
    List<GoodsVo> findGoodsVoDetail(Long goodsId);
}
