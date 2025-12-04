package com.coolcollege.intelligent.model.selectcomponent;

import lombok.Data;

/**
 * @desc: 选人组件中人员岗位信息的返回
 * @author: xuanfeng
 * @date: 2021-10-27 15:07
 */
@Data
public class SelectComponentUserRoleVO {
    /**
     * 用户id
     */
    private String userId;

    /**
     * 岗位id
     */
    private Long positionId;

    /**
     * 岗位名
     */
    private String positionName;

    private String source;
}
