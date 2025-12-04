package com.coolcollege.intelligent.model.patrolstore.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author byd
 * @date 2024-09-05 15:54
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreBatchSubmitTableParam {

    @ApiModelProperty("多表提交参数")
    List<PatrolStoreSubmitTableParam> dataTableParamList;

}
