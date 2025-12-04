package com.coolcollege.intelligent.model.display;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yezhe
 * @date 2020-11-18 15:50
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisplayCommonDTO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 门店得分
     */
    private Integer score;

    /**
     * 子任务id
     */
    private Long subTaskId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 审核类型,approve/recheck
     */
    private String type;

    /**
     * 审核行为,reject/pass
     */
    private String actionKey;

    /**
     * 操作人id
     */
    private String userId;



}