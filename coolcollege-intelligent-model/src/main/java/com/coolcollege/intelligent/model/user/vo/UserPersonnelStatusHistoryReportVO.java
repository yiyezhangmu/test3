package com.coolcollege.intelligent.model.user.vo;

import lombok.Data;

import java.util.List;

/**
 * @author ：xugangkun
 * @date ：2022/3/4 10:32
 */
@Data
public class UserPersonnelStatusHistoryReportVO {
    /**
     * 用户id
     */
    private String userId;
    /**
     * 用户名称
     */
    private String userName;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 用户状态历史
     */
    private List<UserPersonnelStatusHistoryVO> statusHistoryList;
}
