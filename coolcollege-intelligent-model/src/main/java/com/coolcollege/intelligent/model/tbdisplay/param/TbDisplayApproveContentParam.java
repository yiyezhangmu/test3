package com.coolcollege.intelligent.model.tbdisplay.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author suzhuhong
 * @Date 2021/9/28 16:14
 * @Version 1.0
 */
@Data
public class TbDisplayApproveContentParam {
    /**
     * 检查表数据项id
     */
    @NotNull(message = "检查表内容id不能为空")
    private Long dataContentId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 照片
     */
    private String photo;

    /**
     * 图片地址
     */
    @NotNull(message = "图片地址不能为空")
    private String photoArray;

    /**
     * 子任务id
     */
    private Long subTaskId;
}
