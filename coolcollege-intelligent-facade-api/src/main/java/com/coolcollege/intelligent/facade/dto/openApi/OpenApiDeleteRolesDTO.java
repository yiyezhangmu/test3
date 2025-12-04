package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @Author: hu hu
 * @Date: 2025/1/8 16:10
 * @Description:
 */
@Data
public class OpenApiDeleteRolesDTO {

    private List<String> thirdUniqueIds;

    public boolean check() {
        return !CollectionUtils.isEmpty(thirdUniqueIds);
    }
}
