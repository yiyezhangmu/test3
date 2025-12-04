package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/7/18 10:32
 * @Version 1.0
 */
@Data
public class OpenApiRemoveRegionAuthDTO {

    /**
     * 父任务ID
     */
    private String userId;

    /**
     * 用户权限列表
     */
    private List<String> mappingIdList;
}
