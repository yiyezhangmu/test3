package com.coolcollege.intelligent.model.spi;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/11
 */
@Data
public class BizInstanceDataDO {
    /**
     * 主键
     * 主键
     * isNullAble:0
     */
    private Long id;

    /**
     * 关联类型:createInstance/创建实例,renewInstance/续费实例,upgradeInstance/升级实例,expiredInstance/过期实例,releaseInstance/释放实例
     * isNullAble:1,defaultVal:createInstance
     */
    private String action;

    /**
     * 用户唯一标识
     * isNullAble:1
     */
    private String instanceId;

    /**
     * 云市场业务 ID
     * isNullAble:1
     */
    private String orderBizId;

    /**
     * 云市场订单 ID
     * isNullAble:1
     */
    private String orderId;

    /**
     * 云市场商品 code
     * isNullAble:1
     */
    private String productCode;

    /**
     * 商品规格标识
     * isNullAble:1
     */
    private String skuId;

    /**
     * 是否试用
     * isNullAble:1
     */
    private Boolean trial;

    /**
     * 过期时间
     * isNullAble:1
     */
    private java.time.LocalDateTime expiredOn;

    /**
     * 模板 ID，适用于模板类建站商品
     * isNullAble:1
     */
    private String template;

    /**
     * 创建时间
     * isNullAble:1
     */
    private java.time.LocalDateTime createTime;

    /**
     * 更新时间
     * isNullAble:1
     */
    private java.time.LocalDateTime updateTime;

}
