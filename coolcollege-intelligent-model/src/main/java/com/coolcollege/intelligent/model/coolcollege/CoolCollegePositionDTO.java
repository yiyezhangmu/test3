package com.coolcollege.intelligent.model.coolcollege;

import lombok.Data;

/**
 * @author: xuanfeng
 * @date: 2022-03-31 11:29
 * 职位数据的实体封装
 */
@Data
public class CoolCollegePositionDTO {
    /**
     * 创建时间
     */
    private Long create_time;
    /**
     * 职位优先级
     */
    private Integer post_order;
    /**
     * 职位ID
     */
    private String post_id;
    /**
     * 删除状态; 1:删除，0:未删除
     */
    private Integer is_delete;
    /**
     * 最后更新时间
     */
    private Long last_update_time;
    /**
     * 职位名称
     */
    private String post_name;
}
