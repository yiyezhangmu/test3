package com.coolcollege.intelligent.model.display.param;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author yezhe
 * @date 2020-11-17 16:02
 */
@Data
public class DisplayPhotoParam {
    /**
     * 检查表快照id
     */
    @NotNull(message = "检查表快照id不能为空")
    private Long templatePgId;

    /**
     * 检查项快照id
     */
    @NotNull(message = "检查项快照id不能为空")
    private Long checkItemPgId;

    /**
     * 图片地址
     */
    @NotNull(message = "图片地址不能为空")
    private String photoUrl;
}
