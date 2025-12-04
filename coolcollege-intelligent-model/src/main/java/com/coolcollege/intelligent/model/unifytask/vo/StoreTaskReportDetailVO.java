package com.coolcollege.intelligent.model.unifytask.vo;

import com.coolcollege.intelligent.model.store.dto.StoreSignInfoDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: StoreTaskClearVO
 * @Description: 门店日清任务
 * @date 2022-06-30 15:11
 */
@ApiModel
@Data
public class StoreTaskReportDetailVO extends StoreTaskDetailVO{

    @ApiModelProperty("门店签到信息")
    private StoreSignInfoDTO storeSignInfoDTO;
}
