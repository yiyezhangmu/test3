package com.coolcollege.intelligent.model.patrolstore.request;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class RecordListRequest {
    /**
     * 开始时间
     */
    private Date beginTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 巡店类型
     */
    private String patrolType;
    /**
     * 用户id
     */
    private List<String> userIdList;

    /**
     * 职位ids/角色ids
     */
    private List<Long> roleIdList;

    @NotNull
    private Integer pageSize = 10;
    @NotNull
    private Integer pageNum =1;

    /**
     * 近几天
     */
    private Integer recentDay;

    /**
     * 门店id
     */
    private List<String> storeIdList;

    /**
     * 巡店记录状态
     */
    private Integer status;

    /**
     * 登录人userId
     */
    private String userId;

    /**
     * 登录人userName
     */
    private String userName;

    /**
     * 区域id
     */
    private List<String> regionIdList;
}
