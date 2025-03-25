package com.ztj.hcboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ztj.hcboot.pojo.SeckillOrder;
import com.ztj.hcboot.vo.RespBean;

public interface ISeckillOrderService extends IService<SeckillOrder> {

    RespBean doSeckill(Long userId, Long goodsId);

    Long getSeckillResult(Long userId, Long goodsId);

    RespBean getSeckillPath(Long userId, Long goodsId);

    boolean equalsSeckillPath(Long userId, String path, Long goodsId);
}
