package com.coolcollege.intelligent.model.tbdisplay.param;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;


/**
 * @author byd
 */
@Data
public class TbApproveDisplayTaskParam {

    /**
     * 子任务id
     */
    private Long subTaskId;

    /**
     * 总分值
     */
    private BigDecimal score;

    /**
     * 备注
     */
    private String remark;


    /**
     * 审批行为(reject,pass)
     */
    @NotNull(message = "审批行为不能为空")
    private String actionKey;

    /**
     * 处理图片列表
     */
    @Valid
    private List<TbDisplayApprovePhotoParam> approveItemList;

    /**
     * 处理图片列表
     */
    @Valid
    private List<TbDisplayApproveContentParam> approveContentList;

    /**
     * 附件图片列表
     */
    @Size(min = 0, max = 3, message = "图片最多上传3张")
    private List<String> approveImageList;
}
