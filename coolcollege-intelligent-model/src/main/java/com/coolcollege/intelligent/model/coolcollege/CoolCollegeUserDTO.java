package com.coolcollege.intelligent.model.coolcollege;

import lombok.Data;

import java.util.List;

/**
 * @author: xuanfeng
 * @date: 2022-03-31 11:29
 * 人员数据的实体封装
 */
@Data
public class CoolCollegeUserDTO {
    /**
     * 激活状态, 默认是true
     */
    private Boolean active;
    /**
     * 为部门负责人的 部门ID
     */
    private List<String> charge_department_ids;
    /**
     * 创建时间 13位时间戳
     */
    private Long create_time;
    /**
     * 所属部门id
     */
    private List<String> department_ids;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 人员信息扩展属性
     */
    private List<UserExpandColumnDto> extend_column;
    /**
     * 删除状态; 1:删除，0:未删除
     */
    private Integer is_delete;
    /**
     * 工号
     */
    private String jobnumber;
    /**
     * 最后更新时间 13位时间戳
     */
    private Long last_update_time;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 所属岗位id
     */
    private List<String> post_ids;
    /**
     * 所属岗位名称,postIds 为空时生效,不调用岗位事件接口时使用该字段
     */
    private List<String> post_names;
    /**
     * 用户Id
     */
    private String user_id;
    /**
     * 用户名称
     */
    private String user_name;
    /**
     * 是否是管理员
     */
    private Boolean isAdmin;

    private Boolean third_admin;
}
