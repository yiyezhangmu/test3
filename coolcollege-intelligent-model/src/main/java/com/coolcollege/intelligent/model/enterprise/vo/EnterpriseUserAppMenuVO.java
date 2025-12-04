package com.coolcollege.intelligent.model.enterprise.vo;

import lombok.Data;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/01/26
 */
@Data
public class EnterpriseUserAppMenuVO {
    private String userId;
    /**
     * 企业有权限的移动端菜单列表
     */
    private List<EnterpriseUserAppMenuInfoVO> menuInfoList;
    /**
     * 用户自定的菜单列表
     */
    private List<EnterpriseUserAppMenuInfoVO> userMenuInfoList;

}
