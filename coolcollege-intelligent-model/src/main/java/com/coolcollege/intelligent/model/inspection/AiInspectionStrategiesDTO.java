package com.coolcollege.intelligent.model.inspection;

import com.coolcollege.intelligent.model.page.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author byd
 * @date 2025-09-29 15:25
 */
@Data
public class AiInspectionStrategiesDTO extends PageRequest {

    @ApiModelProperty("场景id")
    private Long sceneId;

}
