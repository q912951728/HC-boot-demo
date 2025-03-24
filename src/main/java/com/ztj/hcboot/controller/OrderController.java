package com.ztj.hcboot.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ztj.hcboot.pojo.Order;
import com.ztj.hcboot.pojo.SeckillOrder;
import com.ztj.hcboot.service.IOrderService;
import com.ztj.hcboot.service.ISeckillOrderService;
import com.ztj.hcboot.util.SecurityUtils;
import com.ztj.hcboot.vo.GoodsVo;
import com.ztj.hcboot.vo.RespBean;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/order")
public class OrderController {


    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private IOrderService orderService;


    @GetMapping("/detail")
    public RespBean detail(HttpServletRequest request, @RequestParam("orderId") Long orderId) {
        Long id = securityUtils.getCurrentUserId(request);
        System.out.println("订单detail接口，用户id为:" + id);
        System.out.println("订单detail接口，订单id为:" + orderId);
        Order order = orderService.getOne(new QueryWrapper<Order>().eq("id", orderId));
        System.out.println(order);
        return RespBean.success(order);
    }



}
