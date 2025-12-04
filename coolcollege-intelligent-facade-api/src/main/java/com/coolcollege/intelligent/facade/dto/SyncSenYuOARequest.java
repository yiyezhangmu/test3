package com.coolcollege.intelligent.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 数据同步
 *
 * @author wxp
 * @since 2021/9/8
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SyncSenYuOARequest {

    /**
     * 企业id
     */
    private String enterpriseId;

    /**
     * 用户userId
     */
    String userId;

    /**
     * 第三方OA系统唯一标识
     */
    private String thirdOaUniqueFlag;

    /**
     * 森宇门店编码，用作权限同步
     */
    List<String> kehbmList;

    private String name ;//员工名称
    private String roleCode;//岗位编码
    private String roleName;//岗位描述
    private String mobile ;//手机号码
    private String yewhbdkhh1;//上级编码
    private String zhongxjzdj;//冻结标识
    private String kehzhz;//促销员标识
    /**
     * 是否已经激活, true表示已激活, false表示未激活
     */
    private Boolean active;
    //上级身份证
    private String parentThirdOaUniqueFlag;

}
