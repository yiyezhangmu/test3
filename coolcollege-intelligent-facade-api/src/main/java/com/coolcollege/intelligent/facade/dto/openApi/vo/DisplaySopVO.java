package com.coolcollege.intelligent.facade.dto.openApi.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * describe: 陈列SOP文档VO
 *
 * @author wangff
 * @date 2024/10/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisplaySopVO {
    /**
     * 文档ID
     */
    private Long id;
    
    /**
     * 文档名称
     */
    private String name;
}
