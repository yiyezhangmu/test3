package com.coolcollege.intelligent.model.fsGroup.query;

import com.coolcollege.intelligent.model.page.PageBaseRequest;
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
public class FsGroupQuery extends PageBaseRequest {
    @ApiModelProperty("群名")
    private String name;

    @ApiModelProperty("群类型 store,region,other")
    private String type;

    @ApiModelProperty("绑定部门")
    private List<String> bindRegionIds;
}
