package com.coolcollege.intelligent.model.fsGroup.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SceneGroupIdRequest {
    private List<Long> ids;


    @ApiModelProperty("群场景ids,sceneId")
    private List<Long> sceneIds;

    @ApiModelProperty("chatIds")
    private List<String> chatIds;
}
