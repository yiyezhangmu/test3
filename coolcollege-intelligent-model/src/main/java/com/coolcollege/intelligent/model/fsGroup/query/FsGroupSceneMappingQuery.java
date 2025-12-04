package com.coolcollege.intelligent.model.fsGroup.query;

import com.coolcollege.intelligent.model.page.PageBaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * (FsGroupScene)实体类
 *
 * @author CFJ
 * @since 2024-04-26 18:37:14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FsGroupSceneMappingQuery extends PageBaseRequest {

    @ApiModelProperty("场景id")
    private String sceneId;

    @ApiModelProperty("群名")
    private String groupName;
}

