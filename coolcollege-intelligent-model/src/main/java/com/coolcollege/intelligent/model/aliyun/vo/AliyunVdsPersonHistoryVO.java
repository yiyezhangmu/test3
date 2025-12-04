package com.coolcollege.intelligent.model.aliyun.vo;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/01/20
 */
@Data
public class AliyunVdsPersonHistoryVO {
    private String customerId;
    private Long lastShotTime;
    private String targetPersonUrl;
    private String sourcePersonUrl;
    private Integer gender;
    private String picUrl;
    private String name;
    private Integer age;
    private String groupName;
    private String personalTag;
    private Integer historyCount;
    private String lastAppearStoreId;
    private String storeName;
    private String taskId;


}
