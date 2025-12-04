package com.coolcollege.intelligent.model.enterprise.dto;

import lombok.Data;

/**
 * 用户钉钉职位信息
 *
 * @ClassName: UserDingPositionDTO
 * @Author: xugangkun
 * @Date: 2021/3/26 14:21
 */
@Data
public class UserDingPositionDTO {
    /**
     * 用户职位映射表主键
     */
    private Long userRoleId;
    /**
     * 用户钉钉职位名称
     */
    private String userDingPosition;
}
