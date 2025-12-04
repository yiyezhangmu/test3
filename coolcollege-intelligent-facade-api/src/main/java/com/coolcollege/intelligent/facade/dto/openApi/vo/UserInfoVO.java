package com.coolcollege.intelligent.facade.dto.openApi.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>
 * 用户信息VO
 * </p>
 *
 * @author wangff
 * @since 2025/3/14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVO {
    /**
     * 用户id
     */
    private String userId;

    /**
     * 名称
     */
    private String name;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 工号
     */
    private String jobnumber;

    /**
     * 所属区域列表
     */
    private List<String> regionIds;
}
