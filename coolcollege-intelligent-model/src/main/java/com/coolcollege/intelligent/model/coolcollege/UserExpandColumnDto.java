package com.coolcollege.intelligent.model.coolcollege;

import lombok.Data;

/**
 * @author: xuanfeng
 * @date: 2022-03-31 16:09
 * 用户拓展字段
 */
@Data
public class UserExpandColumnDto {
    /**
     * 属性名称
     */
    private String column_name;
    /**
     * 属性顺序值
     */
    private Integer column_order;
    /**
     * 属性值
     */
    private String column_value;
}
