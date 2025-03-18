package com.ztj.hcboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ztj.hcboot.pojo.Goods;
import com.ztj.hcboot.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
*  张是小
* @author zsx
*/
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

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
    GoodsVo findGoodsVoDetail(Long goodsId);

    /**
     * 查询商品库存(非秒杀)
     * @param goodsId
     * @return
     */
    Integer findGoodsStock(Long goodsId);

}
