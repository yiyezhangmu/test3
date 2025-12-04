package com.coolcollege.intelligent.model.sop.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 邵凌志
 * @date 2021/2/20 16:37
 */
@ApiModel
@Data
public class TaskSopQuery {

    private String category;

    private String name;

    private Integer isDeleted;

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String userId;

    private List<String> roleIds;

    @ApiModelProperty("业务类型 陈列:TB_DISPLAY_TASK 巡店:PATROL_STORE")
    private String businessType;
}
