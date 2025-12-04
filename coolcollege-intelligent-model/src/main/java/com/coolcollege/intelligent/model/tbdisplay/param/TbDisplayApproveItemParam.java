package com.coolcollege.intelligent.model.tbdisplay.param;

import com.coolcollege.intelligent.model.display.param.DisplayPhotoParam;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * @author byd
 */
@Data
public class TbDisplayApproveItemParam {
    /**
     * 处理记录id
     */
    @NotNull(message = "处理记录id")
    private Long recordId;

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
