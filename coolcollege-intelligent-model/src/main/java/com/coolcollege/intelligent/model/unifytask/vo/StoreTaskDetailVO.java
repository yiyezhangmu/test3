package com.coolcollege.intelligent.model.unifytask.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: StoreTaskClearVO
 * @Description: 门店日清任务
 * @date 2022-06-30 15:11
 */
@ApiModel
@Data
public class StoreTaskDetailVO {

    @ApiModelProperty("巡店")
    private List<StoreTaskListVO> taskList;

}
