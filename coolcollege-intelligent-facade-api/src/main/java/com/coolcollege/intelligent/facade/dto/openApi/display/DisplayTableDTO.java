package com.coolcollege.intelligent.facade.dto.openApi.display;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * describe: 陈列检查表/SOP请求DTO
 *
 * @author wangff
 * @date 2024/10/25
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DisplayTableDTO extends DisplayBaseDTO {

    /**
     * 表单/SOP文档名称
     */
    private String name;
}
