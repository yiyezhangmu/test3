package com.coolcollege.intelligent.facade.dto.openApi.vo;

import lombok.Data;


/**
 * @author byd
 */
@Data
public class OpenUserVO {
    /**
     * 员工UserID
     */
    private String userId;
    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 第三方OA系统唯一标识
     */
    private String thirdOaUniqueFlag;
}
