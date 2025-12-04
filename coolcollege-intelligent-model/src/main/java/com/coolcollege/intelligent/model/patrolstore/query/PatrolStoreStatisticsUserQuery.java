package com.coolcollege.intelligent.model.patrolstore.query;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PatrolStoreStatisticsUserQuery extends PatrolStoreStatisticsBaseQuery {
    /**
     * 人员id
     */
    @ApiModelProperty("人员id列表")
    private List<String> userIdList;
}
