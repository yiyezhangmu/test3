package com.coolcollege.intelligent.model.patrolstore.param;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 巡店父任务初始化参数
 * 
 * @author yezhe
 * @date 2020-12-08 19:25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreDelParam {
    /**
     * 子任务ids
     */
    @NotNull(message = "子任务id列表不能为空")
    private List<Long> subTaskIds;
}
