package com.coolcollege.intelligent.model.user;

import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: UpdateUserCenterDTO
 * @Description: 用户中心
 * @date 2021-07-21 17:12
 */
@Data
public class UpdateUserCenterDTO {

    private String name;

    private String email;

    private String avatar;

    /**
     * 工号
     */
    private String jobnumber;
    /**
     * 第三方OA系统唯一标识
     */
    private String thirdOaUniqueFlag;

}
