package com.coolcollege.intelligent.facade.dto.openApi.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * describe: 陈列检查表VO
 *
 * @author wangff
 * @date 2024/10/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisplayTableVO {

    /**
     * 表单ID
     */
    private Long id;
    
    /**
     * 表单名称
     */
    private String name;
    
    /**
     * 表单类型，0：普通表、1：高级表
     */
    private Integer tableProperty;
}
