package com.coolcollege.intelligent.model.coolrelation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 封装在redis中的酷学院用户信息
 * @author ：xugangkun
 * @date ：2021/5/17 9:37
 */
@Data
@AllArgsConstructor
public class CoolInfoDTO {
    /**
     * 酷学院token
     */
    private String actionToken;
    /**
     * 酷学院用户id
     */
    private String userId;
    /**
     * 酷学院企业id
     */
    private String enterpriseId;
}
