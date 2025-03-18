package com.ztj.hcboot.mapper;

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
public interface GoodsMapper {

    /**
     * 查询商品列表
     * @return
     */
    List<GoodsVo> findGoodsVo();

    List<GoodsVo> findGoodsVoDetail(Long goodsId);
}
