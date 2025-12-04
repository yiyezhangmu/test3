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
public class HandleParam {
    /**
     * 子任务id
     */
    @NotNull(message = "子任务id不能为空")
    private Long subTaskId;

    /**
     * 处理图片列表
     */
    @Valid
    private List<DisplayPhotoParam> handlePhotoList;

}
