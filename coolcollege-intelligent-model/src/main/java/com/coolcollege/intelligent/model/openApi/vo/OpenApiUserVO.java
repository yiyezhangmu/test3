package com.coolcollege.intelligent.model.openApi.vo;

import com.coolcollege.intelligent.model.enterprise.dto.EntUserRoleDTO;
import lombok.Data;

import java.util.List;

/**
 * @author chenyupeng
 * @since 2022/4/27
 */
@Data
public class OpenApiUserVO {

    private String userId;
    private String userName;
    private String jobnumber;
    private List<EntUserRoleDTO> userRoles;
}
