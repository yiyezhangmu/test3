package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Builder;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: OpenApiAddUserDTO
 * @Description:
 * @date 2024-08-26 9:48
 */
@Data
@Builder
public class OpenApiGetUserAccessTokenDTO {

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 账号类型 1：手机号，2：userid， 3: third_oa_unique_flag(第三方系统OA标识)
     */
    private Integer accountType;

}
