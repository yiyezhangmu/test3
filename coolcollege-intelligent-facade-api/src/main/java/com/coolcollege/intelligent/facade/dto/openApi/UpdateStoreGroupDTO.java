package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/4/20 16:11
 * @Version 1.0
 */
@Data
public class UpdateStoreGroupDTO {

    private String groupId;

    private List<String> dingDeptIds;

}
