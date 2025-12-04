package com.coolcollege.intelligent.model.unifytask.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * 重新分配任务
 * @author byd
 */
@Data
public class ReallocateStoreTaskDTO {

    @NotNull(message = "门店任务id不能为空")
    private Long taskStoreId;


    private List<String> handerUserList;

    private List<String> approveUserList;

    /**
     * 复核人
     */
    private List<String> recheckUserList;

    /**
     * 三级审批人
     */
    private List<String> thirdApproveUserList;


    /**
     * 四级审批人
     */
    private List<String> fourApproveUserList;

    /**
     * 五级审批人
     */
    private List<String> fiveApproveUserList;

    @ApiModelProperty("修改当前节点的审批人")
    private List<String> currentNodeApproveUserList;
}
