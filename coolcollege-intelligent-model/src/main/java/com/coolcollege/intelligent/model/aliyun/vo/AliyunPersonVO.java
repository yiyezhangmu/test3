package com.coolcollege.intelligent.model.aliyun.vo;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/25
 */
@Data
public class AliyunPersonVO {

    private String customerId;
    private String storeId;
    private String faceId;
    private String aliPicUrl;
    private String picUrl;
    private String name;
    //首次进店时间
    private Long firstAppearTime;
    private Integer age;
    private String phone;
    private String birthday;
    private Long createTime;
    private String createId;
    private Long updateTime;
    private String updateId;
    private String remark;
    private String groupName;
    private String groupId;
    private String personalTag;
    private Long recentAppearTime;

    private Long storeVisitCount;

    private Long lastAppearTime;
    private String lastAppearPic;
    private String lastAppearTargetPic;
    private String lastAppearStoreId;
    private Integer gender;
    private String email;
    private String wechat;


}
