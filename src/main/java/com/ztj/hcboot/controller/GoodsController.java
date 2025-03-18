package com.ztj.hcboot.controller;


import com.ztj.hcboot.service.IGoodsService;
import com.ztj.hcboot.util.SecurityUtils;
import com.ztj.hcboot.vo.GoodsVo;
import com.ztj.hcboot.vo.RespBean;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *  张是小
 * @author zsx
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private IGoodsService goodsService;

    /**
     * 商品列表
     * windows优化前QPS：1544.9/sec
     * @param request
     * @return
     */
    @GetMapping("/toList")
    public RespBean toList(HttpServletRequest request) {
        Long id = securityUtils.getCurrentUserId(request);
        System.out.println("当前用户id为:" + id);
        List<GoodsVo> goodsVoList = goodsService.findGoodsVo();


        return RespBean.success(goodsVoList);
    }

    /**
     * 商品详情
     * windows优化前QPS：1065.9/sec
     * @param request
     * @param goodsId
     * @return
     */
    @GetMapping("/detail")
    public RespBean toList(HttpServletRequest request, @RequestParam("id") Long goodsId) {
        Long id = securityUtils.getCurrentUserId(request);
        System.out.println("当前用户id为:" + id);
        List<GoodsVo> goodsVoList = goodsService.findGoodsVoDetail(goodsId);


        return RespBean.success(goodsVoList);
    }



}
