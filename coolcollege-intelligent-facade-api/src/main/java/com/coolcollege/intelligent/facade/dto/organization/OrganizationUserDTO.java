package com.coolcollege.intelligent.facade.dto.organization;

import lombok.Data;

import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: OrganizationUserDTO
 * @Description:
 * @date 2022-11-28 10:22
 */
@Data
public class OrganizationUserDTO {

    private List<String> userIds;

    public OrganizationUserDTO(List<String> userIds) {
        this.userIds = userIds;
    }
}
