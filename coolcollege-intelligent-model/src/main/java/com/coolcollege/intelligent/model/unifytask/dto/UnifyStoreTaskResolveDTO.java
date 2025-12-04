package com.coolcollege.intelligent.model.unifytask.dto;

import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: UnifyStoreTaskResolveDTO
 * @Description:
 * @date 2025-01-02 17:38
 */
@Data
public class UnifyStoreTaskResolveDTO {

    @ApiModelProperty("企业id")
    private String enterpriseId;

    @ApiModelProperty("门店任务do")
    private TaskStoreDO taskStore;

    @ApiModelProperty("是否刷新")
    private boolean refresh;

    public UnifyStoreTaskResolveDTO(String enterpriseId, TaskStoreDO taskStore, boolean refresh) {
        this.enterpriseId = enterpriseId;
        this.taskStore = taskStore;
        this.refresh = refresh;
    }
}
