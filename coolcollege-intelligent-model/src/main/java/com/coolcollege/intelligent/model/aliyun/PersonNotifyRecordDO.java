package com.coolcollege.intelligent.model.aliyun;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/01/12
 */
@Data
public class PersonNotifyRecordDO {
    private Long id;
    private String customerId;
    private String deviceId;
    private String sourcePicUrl;
    private String score;
    private Long shotTime;
    private Long createTime;
    private String targetPicUrl;
    private String storeId;
    private String personGroupName;

}
