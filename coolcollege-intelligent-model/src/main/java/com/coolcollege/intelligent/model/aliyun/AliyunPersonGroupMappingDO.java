package com.coolcollege.intelligent.model.aliyun;

import lombok.Data;

/**
 * describe:门店阿里云顾客表和标签分组映射表
 *
 * @author zhouyiping
 * @date 2020/08/24
 */
@Data
public class AliyunPersonGroupMappingDO {
    private Long id;
    private String customerId;
    private String personGroupId;
    private Long createTime;
    private String createId;
    private Long updateTime;
    private String updateId;


}
