package com.coolcollege.intelligent.model.enterprise.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2021/7/30 18:42
 * @Version 1.0
 */
@Data
public class ApproveDTO {
    /**
     * 用户id
     */
    private String userId;
    /**
     * 用户名字
     */
    private String userName;
    /**
     * 操作时间
     */
    private Date ApproveTime;
}
