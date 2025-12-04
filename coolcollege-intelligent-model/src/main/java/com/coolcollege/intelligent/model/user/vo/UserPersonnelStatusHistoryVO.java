package com.coolcollege.intelligent.model.user.vo;

import lombok.Data;

/**
 * @author ：xugangkun
 * @description：TODO
 * @date ：2022/3/3 11:06
 */
@Data
public class UserPersonnelStatusHistoryVO {
    /**
     * 状态名称
     */
    private String statusName;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 备注
     */
    private String remarks;
    /**
     * 状态有效时间,以天为最小单位
     */
    private String effectiveTime;
}
