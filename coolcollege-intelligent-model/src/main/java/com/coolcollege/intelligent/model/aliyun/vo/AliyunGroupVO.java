package com.coolcollege.intelligent.model.aliyun.vo;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/25
 */
@Data
public class AliyunGroupVO {

    private String groupId;

    private String groupName;

    /**
     * 分组类型
     */
    private String  personGroupType;

    /**
     * 是否预制
     */
    private Integer  isInternal;

}
