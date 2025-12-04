package com.coolcollege.intelligent.model.supervision.request;

import com.coolcollege.intelligent.model.supervision.dto.SupervisionDefDataDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/3/3 18:41
 * @Version 1.0
 */
@Data
@ApiModel
public class SupervisionDefDataRequest {

    @ApiModelProperty("检查项数据列表")
    List<SupervisionDefDataDTO> supervisionDefDataDTOList;

    @ApiModelProperty("父任务ID")
    private Long taskParentId;

    @ApiModelProperty("按人任务ID")
    private Long taskId;

    @ApiModelProperty("是否提交")
    private Boolean submit;

    private String type;

}
