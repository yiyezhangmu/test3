package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 付费订单状态
 * https://work.weixin.qq.com/api/doc/15219#%E9%80%80%E6%AC%BE%E9%80%9A%E7%9F%A5
 * 订单状态。0-未支付，1-已支付，2-已关闭， 3-未支付且已过期， 4-申请退款中， 5-申请退款成功， 6-退款被拒绝
 */
public enum OrderStatusEnum {

    NOTPAY(0, "未支付"),
    PAY(1, "已支付"),
    CLOSE(2, "已关闭"),
    NOTPAYOVERDUE(3, "未支付且已过期"),
    REFUNDING(4, "申请退款中"),
    REFUNDED(5, "申请退款成功"),
    REFUNDREFUSE(6, "退款被拒绝"),
            ;

    private static final Map<Integer, OrderStatusEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(OrderStatusEnum::getValue, Function.identity()));


    private Integer value;
    private String desc;

    OrderStatusEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public Integer getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static OrderStatusEnum getByCode(Integer value) {
        return map.get(value);
    }
}
