package com.coolcollege.intelligent.model.aliyun;

import lombok.Data;

/**
 * describe:门店阿里云人员标签分组表
 *
 * @author zhouyiping
 * @date 2020/08/24
 */
@Data
public class AliyunPersonGroupDO {

    private Long id;
    private String personGroupId;
    private String personGroupName;
    private Long createTime;
    private String createId;
    private Long updateTime;
    private String updateId;
    private String remark;
    private Integer isInternal;
    private String personGroupType;

}
