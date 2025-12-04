package com.coolcollege.intelligent.model.sop.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author 邵凌志
 * @date 2021/2/20 16:29
 */
@Data
public class TaskSopDTO {

    private Long id;

    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 文件地址
     */
    private String url;

    private String videoUrl;
    /**
     * 文件类型
     */
    private String type;
    /**
     * 文件分类
     */
    private String category;
    /**
     * 上传人id
     */
    private String createUserId;
    /**
     * 上传人
     */
    private String createUser;

    /**
     * 上传时间
     */
    private Date createTime;
    /**
     * 可见人
     */
    private String visibleUser;
    /**
     * 可见角色
     */
    private String visibleRole;
    /**
     * 可见人
     */
    private String visibleUserName;
    /**
     * 可见角色
     */
    private String visibleRoleName;


    /**
     * 原始选取的使用人[{type:person,value:}{type:position,value:}]
     */
    private String usePersonInfo;

    /**
     * 使用人范围：self-仅自己，all-全部人员，part-部分人员
     */
    private String useRange;

    /**
     * 使用人userId集合（前后逗号分隔）
     */
    private String useUserids;

    /**
     * 业务类型TB_DISPLAY_TASK PATROL_STORE
     */
    private String businessType;
}
