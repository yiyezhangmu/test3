package com.coolcollege.intelligent.model.aliyun;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * describe:门店阿里云顾客表
 *
 * @author zhouyiping
 * @date 2020/08/24
 */
@Data
public class AliyunPersonDO {
    private Long id;
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
    private String personalTag;
    private String aliUserId;
    private String taskId;
    private Long lastAppearTime;
    private String lastAppearPic;
    private String lastAppearTargetPic;
    private String lastAppearStoreId;
    private Integer gender;
    private String email;
    private String wechat;




}
