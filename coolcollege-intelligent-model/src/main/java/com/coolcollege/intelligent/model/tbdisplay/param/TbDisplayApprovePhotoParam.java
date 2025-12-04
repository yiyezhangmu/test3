package com.coolcollege.intelligent.model.tbdisplay.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;


/**
 * @author byd
 */
@Data
public class TbDisplayApprovePhotoParam {
    /**
     * 检查表数据项id
     */
    @NotNull(message = "检查表快照id不能为空")
    private Long dataColumnId;

    /**
     * 分值
     */
    private BigDecimal score;

    /**
     * 分值
     */
    private BigDecimal totalScore;

    /**
     * 备注
     */
    private String remark;

    /**
     * 图片地址
     */
    private String photoArray;

    /**
     * 子任务id
     */
    private Long subTaskId;

}
