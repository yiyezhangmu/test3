package com.coolcollege.intelligent.model.aliyun.dto;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/25
 */
@Data
public class AliyunPersonDTO {

    private String customerId;
    private String storeId;
    private String faceId;
    private String aliPicUrl;
    private String picUrl;
    private String name;
    private Long firstAppearTime;
    private Integer age;
    private String phone;
    private String birthday;
    private Long createTime;
    private String createId;
    private Long updateTime;
    private String updateId;
    private String remark;
    private String groupId;
    private String groupName;
    private String personalTag;
    private Integer gender;
    private String email;
    private String wechat;

}
