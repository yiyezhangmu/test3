package com.coolcollege.intelligent.model.login;

import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: SwitchEnterpriseDO
 * @Description:企业切换登录
 * @date 2021-07-19 16:51
 */
@Data
public class SwitchEnterpriseDO {

    /**
     * 企业id
     */
    private String enterpriseId;
    /**
     * 企业id
     */
    private String loginWay;

}
