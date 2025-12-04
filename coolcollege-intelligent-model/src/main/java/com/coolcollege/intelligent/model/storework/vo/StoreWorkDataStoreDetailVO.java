package com.coolcollege.intelligent.model.storework.vo;

import com.coolcollege.intelligent.model.homepage.vo.StoreWorkDataVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author byd
 * @date 2022-10-13 11:25
 */
@ApiModel
@Data
public class StoreWorkDataStoreDetailVO extends StoreWorkDataVO {

    @ApiModelProperty("闷蛋排名")
    private Long rank;
}
