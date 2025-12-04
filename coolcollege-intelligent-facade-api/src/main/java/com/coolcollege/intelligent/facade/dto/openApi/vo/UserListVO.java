package com.coolcollege.intelligent.facade.dto.openApi.vo;

import lombok.Data;

import java.util.List;

/**
 * <p>
 * 用户列表VO
 * </p>
 *
 * @author wangff
 * @since 2025/3/14
 */
@Data
public class UserListVO {

    /**
     * 用户信息列表
     */
    List<UserInfoVO> list;
}
