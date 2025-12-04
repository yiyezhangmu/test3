package com.coolcollege.intelligent.model.unifytask.vo;

import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 任务人员信息
 * @author zhangnan
 * @date 2021-12-28 15:21
 */
@ApiModel(value = "任务人员信息")
@Data
public class TaskPersonVO {

    /**
     * 当前节点
     */
    @ApiModelProperty(value = "当前节点")
    private String nodeNo;

    /**
     * 处理人
     */
    @ApiModelProperty(value = "处理人")
    private List<PersonDTO> handleUser;

    /**
     * 审核人
     */
    @ApiModelProperty(value = "审核人")
    private List<PersonDTO> approveUser;

    /**
     * 二级审核人
     */
    @ApiModelProperty(value = "二级审核人")
    private List<PersonDTO> secondApproveUser;

    /**
     * 二级审核人
     */
    @ApiModelProperty(value = "三级审核人")
    private List<PersonDTO> thirdApproveUser;
}
