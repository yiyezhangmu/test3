package com.coolcollege.intelligent.model.supervision.request;

import com.coolcollege.intelligent.common.enums.supervison.SupervisionTaskPriorityEnum;
import com.coolcollege.intelligent.model.enums.SupervisionParentStatusEnum;
import com.coolcollege.intelligent.model.page.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/4/13 11:03
 * @Version 1.0
 */
@Data
@ApiModel
public class SupervisionTaskParentRequest extends PageRequest {

    @ApiModelProperty("关键字")
    private String keywords;

    @ApiModelProperty("开始时间")
    private Long startTime;

    @ApiModelProperty("结束时间")
    private Long endTime;

    @ApiModelProperty("完成状态")
    private  List<SupervisionParentStatusEnum> statusEnumList;

    @ApiModelProperty("优先级")
    private  List<SupervisionTaskPriorityEnum> supervisionTaskPriorityEnumList;

    @ApiModelProperty("任务分组")
    private List<String> taskGroupingList;

    @ApiModelProperty("标签")
    private List<String> tags;
    
    
    
    
    
    
    
    
    
}
