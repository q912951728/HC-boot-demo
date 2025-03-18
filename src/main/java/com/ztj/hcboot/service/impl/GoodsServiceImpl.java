package com.ztj.hcboot.service.impl;

import com.ztj.hcboot.mapper.GoodsMapper;
import com.ztj.hcboot.service.IGoodsService;
import com.ztj.hcboot.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
*  张是小
* @author zsx
*/
@Service
public class GoodsServiceImpl implements IGoodsService {


    @Autowired
    private GoodsMapper goodsMapper;


    /**
     * 查询商品列表
     * @return
     */
    @Override
    public List<GoodsVo> findGoodsVo() {
        return goodsMapper.findGoodsVo();
    }

    @Override
    public List<GoodsVo> findGoodsVoDetail(Long goodsId) {
        return goodsMapper.findGoodsVoDetail(goodsId);
    }


}
