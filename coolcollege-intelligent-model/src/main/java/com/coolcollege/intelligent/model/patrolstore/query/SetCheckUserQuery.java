package com.coolcollege.intelligent.model.patrolstore.query;

import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseStoreCheckRequestNewDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SetCheckUserQuery {

    private EnterpriseStoreCheckRequestNewDTO bigRegionUserIds;

    private EnterpriseStoreCheckRequestNewDTO warZoneUserIds;

    @ApiModelProperty("扩展字段")
    private String extendField;
}
