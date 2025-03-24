package com.ztj.hcboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ztj.hcboot.pojo.SeckillGoods;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
*  张是小
* @author zsx
*/
@Mapper
public interface SeckillGoodsMapper extends BaseMapper<SeckillGoods> {

    Integer findSeckillGoodsStock(Long goodsId);


    int updateStock(Long goodsId);
}
