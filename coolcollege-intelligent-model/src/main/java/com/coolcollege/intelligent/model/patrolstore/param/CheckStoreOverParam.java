package com.coolcollege.intelligent.model.patrolstore.param;

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
public class CheckStoreOverParam {

    @NotNull(message = "巡店记录id不能为空")
    private Long businessId;
}
