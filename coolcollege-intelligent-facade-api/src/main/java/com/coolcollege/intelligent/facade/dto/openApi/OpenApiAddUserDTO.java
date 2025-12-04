package com.coolcollege.intelligent.facade.dto.openApi;

import com.aliyun.openservices.shade.org.apache.commons.lang3.StringUtils;
import lombok.Data;

import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: OpenApiAddUserDTO
 * @Description:
 * @date 2024-08-26 9:48
 */
@Data
public class OpenApiAddUserDTO {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 工号
     */
    private String jobnumber;

    /**
     * 是否是主管理员
     */
    private Boolean isAdmin;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 第三方唯一标识
     */
    private String thirdOaUniqueFlag;

    /**
     * 角色名称列表
     */
    private List<String> roleNameList;

    /**
     * 第三方部门id列表
     */
    private List<String> thirdDeptIdList;

    /**
     * 第三方部门列表
     */
    private List<String> authDeptIdList;

    /**
     * 用户状态 0待审核 1正常 2冻结
     */
    private Integer userStatus;

    public boolean check(){
        if(StringUtils.isBlank(userId)){
            return false;
        }
        if(StringUtils.isBlank(username)){
            return false;
        }
        return true;
    }

    public boolean checkMobile(){
        String regex = "^1[3-9]\\d{9}$";
        if(StringUtils.isNotBlank(mobile) && !mobile.matches(regex)){
            return false;
        }
        return true;
    }

}
