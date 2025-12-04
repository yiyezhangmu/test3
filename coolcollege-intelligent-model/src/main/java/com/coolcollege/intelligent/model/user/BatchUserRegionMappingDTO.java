package com.coolcollege.intelligent.model.user;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/2/28 15:17
 * @Version 1.0
 */
@Data
public class BatchUserRegionMappingDTO {

    /**
     * unionid集合
     */
    @NotEmpty(message = "unionid 不能为空")
    private List<String> unionIds;

    /**
     * 用户所属部门
     */
    private List<String> regionIds;
}
