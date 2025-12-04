package com.coolcollege.intelligent.model.tbdisplay.param;

import com.coolcollege.intelligent.model.tbdisplay.TbDisplayHistoryDO;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

/**
 * @author yezhe
 * @date 2020-11-17 15:45
 */
@Data
public class TbDisplayApproveParam {

    /**
     * 处理图片列表
     */
    @Valid
    private List<TbDisplayApprovePhotoParam> approveItemList;

    /**
     * 审核记录
     */
    private TbDisplayHistoryDO tbDisplayHistoryDO;

}
