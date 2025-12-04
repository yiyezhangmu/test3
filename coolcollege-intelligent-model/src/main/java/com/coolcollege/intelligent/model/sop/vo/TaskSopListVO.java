package com.coolcollege.intelligent.model.sop.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ：xugangkun
 * @description：TODO
 * @date ：2021/9/17 15:47
 */
@ApiModel
@Data
public class TaskSopListVO {

    /**
     * 文档列表
     */
    private List<TaskSopVO> sopList;

    /**
     * 可见用户
     */
    @ApiModelProperty("可见用户")
    private String visibleUser;

    /**
     * 可见角色
     */
    @ApiModelProperty("可见角色")
    private String visibleRole;
    /**
     * 可见人名称
     */
    @ApiModelProperty("可见人名称")
    private String visibleUserName;
    /**
     * 可见角色名称
     */
    @ApiModelProperty("可见角色名称")
    private String visibleRoleName;

    /**
     * 业务类型TB_DISPLAY_TASK PATROL_STORE
     */
    @ApiModelProperty("业务类型:陈列 TB_DISPLAY_TASK, 巡店 PATROL_STORE, 督导注释 SUPERVISION")
    private String businessType;

    @ApiModelProperty("原始选取的使用人[{type:person,value:}{type:position,value:}]")
    private String usePersonInfo;

    @ApiModelProperty("使用人范围：self-仅自己，all-全部人员，part-部分人员  督导助手使用self")
    private String useRange;

    /**
     * 使用人userId集合（前后逗号分隔）
     */
    @ApiModelProperty("使用人userId集合（前后逗号分隔）")
    private String useUserids;
}
