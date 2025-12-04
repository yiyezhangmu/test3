package com.coolcollege.intelligent.model.tbdisplay.param;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author wxp
 * @date 2021-3-8 15:45
 */
@Data
public class TbDisplayHandleParam {
    /**
     * 处理记录id
     */
    @NotNull(message = "处理记录id不能为空")
    private Long tableRecordId;

    /**
     * 处理图片列表
     */
    private List<TbDisplayHandlePhotoParam> handlePhotoList;

    private Integer tableProperty;

}
