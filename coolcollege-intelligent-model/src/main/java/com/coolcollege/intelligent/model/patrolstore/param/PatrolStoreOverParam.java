package com.coolcollege.intelligent.model.patrolstore.param;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author yezhe
 * @date 2020-12-08 19:25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreOverParam {

    @NotNull(message = "巡店记录id不能为空")
    private Long businessId;

    /**
     * 群聊id（巡店结束后发送报告卡片）
     */
    private String openConversionId;

    /**
     * 稽核选择的签字人
     */
    private String signatureUser;
}
