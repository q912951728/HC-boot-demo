package com.ztj.hcboot.controller;


import com.ztj.hcboot.service.ISeckillOrderService;
import com.ztj.hcboot.util.SecurityUtils;
import com.ztj.hcboot.vo.RespBean;
import com.ztj.hcboot.vo.RespBeanEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seckill")
public class SeckillOrderController {
    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private ISeckillOrderService seckillOrderService;

    /**
     * 秒杀订单接口
     * 吞吐量 876
     * @param request
     * @param goodsId
     * @return
     */
    @PostMapping("/doSeckill")
    public RespBean doSeckill(HttpServletRequest request,@RequestBody Long goodsId) {
        Long userId = securityUtils.getCurrentUserId(request);
        System.out.println("doSeckill接口，用户id为:" + userId);
        System.out.println("doSeckill接口，商品id为:" + goodsId);
        if(userId == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        return seckillOrderService.doSeckill(userId,goodsId);
    }

}
