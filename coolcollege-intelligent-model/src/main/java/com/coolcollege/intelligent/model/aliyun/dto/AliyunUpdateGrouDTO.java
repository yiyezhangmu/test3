package com.coolcollege.intelligent.model.aliyun.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 邵凌志
 * @date 2020/8/29 20:51
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AliyunUpdateGrouDTO {

    /**
     * 阿里云分组id
     */
    private String corpId;
    private String storeId;
    /**
     * 所在区域的id
     */
    private String regionId;
    /**
     * 需要新增的分组id
     */
    private List<String> newGroupIds;
    /**
     * 需要删除的分组id
     */
    private List<String> oldGroupIds;
}
