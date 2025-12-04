package com.coolcollege.intelligent.model.patrolstore.dto;

import lombok.Data;

/**
 * @author shuchang.wei
 * @date 2020/12/21
 */
@Data
public class ColumnValueDTO {
    /**
     * 检查项值
     */
    private String value;

    /**
     * 对应name的code
     */
    private String code;
}
