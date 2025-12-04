package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;

import java.util.List;

/**
 * @Author byd
 * @Date 2022/11/01 17:04
 * @Version 1.0
 */
@Data
public class OpenApiUserDTO {

    private String userId;

    private String thirdOaUniqueFlag;

    private List<String> roleList;
}
