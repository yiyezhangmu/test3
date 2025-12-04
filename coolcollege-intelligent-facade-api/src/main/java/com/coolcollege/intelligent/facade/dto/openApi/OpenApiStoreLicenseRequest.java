package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;

import java.util.List;

/**
 * @author CFJ
 * @version 1.0
 * @date 2023/7/26 16:34
 */
@Data
public class OpenApiStoreLicenseRequest {
    private List<OpenApiStoreLicenseRequestDTO> requestDTOS;
}
