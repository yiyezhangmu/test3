package com.coolcollege.intelligent.model.coolcollege;

import lombok.Data;

/**
 * @author: xuanfeng
 * @date: 2022-04-22 14:59
 */
@Data
public class OpenCoolCollegeDTO {
    /**
     * 企业cropId
     */
    private String corp_id;
    /**
     * 企业名称
     */
    private String corp_name;
    /**
     * 标记开通来源是门店
     */
    private String source;
}
