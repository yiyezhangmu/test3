package com.coolcollege.intelligent.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 数据同步
 *
 * @author chenyupeng
 * @since 2021/8/17
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SyncRequest {

    /**
     * 企业id
     */
    private String enterpriseId;

    /**
     * 百丽工号
     */
    String employeeCode;

    /**
     * 用户userId
     */
    String userId;

    /**
     * 组织Id，用作权限同步
     */
    List<Long> unitIdList;

    /**
     * 用户拥有的角色
     */
    List<String> roleNameList;

    /**
     * 第三方OA系统唯一标识
     */
    private String thirdOaUniqueFlag;

    /**
     * 森宇门店编码，用作权限同步
     */
    List<String> kehbmList;

    /**
     * 百丽用户所属组织  目前一个用户只有一个组织
     */
    List<Long> orgIds;

}
