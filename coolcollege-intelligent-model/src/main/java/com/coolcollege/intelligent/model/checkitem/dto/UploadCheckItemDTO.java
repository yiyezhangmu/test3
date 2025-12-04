package com.coolcollege.intelligent.model.checkitem.dto;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/11/02
 */
@Data
public class UploadCheckItemDTO {
    /**
     * 检查项名称
     */
    private String checkItemName;
    /**
     * 检查项描述
     */
    private String description;
    /**
     * 检查项分类CId
     */
    private String checkItemTypeCid;
}
