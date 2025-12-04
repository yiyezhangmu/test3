package com.coolcollege.intelligent.model.display.param;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author yezhe
 * @date 2020-11-17 15:45
 */
@Data
public class ApproveParam {
    /**
     * 子任务id
     */
    @NotNull(message = "子任务id不能为空")
    private Long subTaskId;

    /**
     * 审核类型
     */
    @NotNull(message = "审核类型不能为空")
    private String type;

    /**
     * 分值
     */
    private Integer score;

    /**
     * 备注
     */
    private String remark;

    /**
     * 审核编辑的图片列表
     */
    @Valid
    private List<DisplayPhotoParam> approvePhotoList;

    /**
     * 审批行为(reject,pass)
     */
    @NotNull(message = "审批行为不能为空")
    private String actionKey;

}
