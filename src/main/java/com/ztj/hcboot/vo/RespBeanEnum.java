package com.ztj.hcboot.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 公共返回对象枚举
 */
@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {
    SUCCESS(200,"SUCCESS"),
    ERROR(500,"服务端异常"),
    LOGIN_ERROR(500210,"用户名或密码错误"),
    MOBILE_ERROR(500211,"手机号码格式错误"),
    BIND_ERROR(500212,"参数校验异常"),
    EMPTY_STOCK(500213,"库存不足"),
    REPEATE_ERROR(500214,"不能重复下单"),
    ORDER_NOT_EXIST(500215,"订单不存在"),
    SESSION_ERROR(500216,"会话已过期"),
    GOODS_NOT_EXIST(500217,"商品不存在")
    ;

    private final Integer code;
    private final String message;

    // 添加 getMessage 方法
    public String getMessage() {
        return message;
    }
}