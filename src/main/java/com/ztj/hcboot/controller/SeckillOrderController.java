package com.ztj.hcboot.controller;


import com.ztj.hcboot.rabbitmq.MQSender;
import com.ztj.hcboot.service.ISeckillOrderService;
import com.ztj.hcboot.util.SecurityUtils;
import com.ztj.hcboot.vo.RespBean;
import com.ztj.hcboot.vo.RespBeanEnum;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/seckill")
public class SeckillOrderController {
    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private ISeckillOrderService seckillOrderService;

    @Autowired
    private MQSender mqSender;


    /**
     * 秒杀订单接口
     * 吞吐量 876
     * @param request
     * @param goodsId
     * @return
     */
    @PostMapping("/doSeckill")
    public RespBean doSeckill(HttpServletRequest request, @RequestParam String path, @RequestBody Long goodsId) {

        Long userId = securityUtils.getCurrentUserId(request);
        log.info("秒杀:userId:{},goodsId:{}",userId,goodsId);
        if(userId == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        //匹配redis中的path
        if(!seckillOrderService.equalsSeckillPath(userId, path, goodsId)){
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }

        return seckillOrderService.doSeckill(userId,goodsId);
    }


    /**
     * 获取秒杀结果
     * @param request
     * @param goodsId
     * @return
     */
    @GetMapping("/result")
    public RespBean getSeckillResult(HttpServletRequest request,@RequestParam("goodsId") Long goodsId) {
        Long userId = securityUtils.getCurrentUserId(request);
        if(userId == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        log.info("获取抢购结果:userId:{},goodsId:{}",userId,goodsId);

        Long orderId = seckillOrderService.getSeckillResult(userId, goodsId);

        return RespBean.success(orderId);
    }


    @GetMapping("/path")
    public RespBean getSeckillPath(HttpServletRequest request,@RequestParam("goodsId") Long goodsId) {
        Long userId = securityUtils.getCurrentUserId(request);
        if(userId == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        log.info("获取秒杀地址:userId:{},goodsId:{}",userId,goodsId);

        return seckillOrderService.getSeckillPath(userId, goodsId);
    }







    @GetMapping("/mq")
    public void mq() {
        mqSender.send("hello");
        return;
    }




}
