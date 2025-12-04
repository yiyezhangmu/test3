package com.coolcollege.intelligent.model.qywx.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 企微读取成员接口
 * https://work.weixin.qq.com/api/doc/90001/90143/90332
 */
@NoArgsConstructor
@Data
public class QyWechatUserGetVo {

    @JsonProperty("errcode")
    private Integer errcode;
    @JsonProperty("errmsg")
    private String errmsg;
    @JsonProperty("userid")
    private String userid;
    @JsonProperty("name")
    private String name;
    // 职务信息；代开发自建应用需要管理员授权才返回；第三方仅通讯录应用可获取；对于非第三方创建的成员，第三方通讯录应用也不可获取
    @JsonProperty("position")
    private String position;
    @JsonProperty("mobile")
    private String mobile;
    // 头像url。 第三方仅通讯录应用可获取；对于非第三方创建的成员，第三方通讯录应用也不可获取
    @JsonProperty("avatar")
    private String avatar;

    // 成员所属部门id列表，仅返回该应用有查看权限的部门id；成员授权模式下，固定返回根部门id，即固定为1
    private List<Long> department;
    // 表示在所在的部门内是否为上级。；第三方仅通讯录应用可获取；对于非第三方创建的成员，第三方通讯录应用也不可获取
    private List<Long> is_leader_in_dept;


}
