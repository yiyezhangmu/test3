package com.coolcollege.intelligent.model.patrolstore.dto;


import lombok.Data;

/**单表基础检查项名字实体类
 * @author shuchang.wei
 * @date 2020/12/21
 */
@Data
public class ColumnNameDTO {
    /**
     * 检查项名称
     */
    private String columnName;

    /**
     * 名称对应的code，给前端做dataIndex用
     */
    private String code;
}
