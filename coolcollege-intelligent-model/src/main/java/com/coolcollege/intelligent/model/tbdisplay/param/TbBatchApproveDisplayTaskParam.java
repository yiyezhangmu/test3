package com.coolcollege.intelligent.model.tbdisplay.param;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;


/**
 * @author byd
 */
@Data
public class TbBatchApproveDisplayTaskParam {

    /**
     * 处理图片列表
     */
    @Valid
    private List<TbDisplayApprovePhotoParam> approveItemList;

    private List<TbDisplayApproveContentParam> approveContentList;

}
