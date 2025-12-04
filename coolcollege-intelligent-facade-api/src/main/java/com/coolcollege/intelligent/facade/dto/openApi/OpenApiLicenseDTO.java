package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;

import java.util.List;

/**
 * @author CFJ
 * @version 1.0
 * @date 2023/7/25 10:33
 */
@Data
public class OpenApiLicenseDTO {
    private String userId;
    List<OpenApiStoreLicenseRequestDTO> requests;
}
