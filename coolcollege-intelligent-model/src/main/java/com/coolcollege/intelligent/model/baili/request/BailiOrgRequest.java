package com.coolcollege.intelligent.model.baili.request;

import lombok.Data;

import java.util.Date;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/08/06
 */
@Data
public class BailiOrgRequest extends BailiBaseRequest {

    /**
     * 开始更新时间
     */
    private Date beginUpdateTime;

    /**
     *结束更新时间
     */
    private Date endUpdateTime;

    /**
     *店铺编码
     */
    private String storeCode;

    /**
     *页码
     */
    private Integer page;

    /**
     *分页大小
     */
    private Integer pageSize;

    /**
     * 组织状态 0-停用 1-启用
     */
    private Integer orgStatus;

}
