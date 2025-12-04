package com.coolcollege.intelligent.model.metatable.request;

import com.coolcollege.intelligent.model.metatable.dto.CheckTableDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/4/25 12:25
 * @Version 1.0
 */
@Data
public class MoveSortRequest {
    @ApiModelProperty("移动之前集合，需要传递orderNum")
    List<CheckTableDTO> preMoveSortCheckTableList;
    @ApiModelProperty("移动之后集合，不需要传递orderNum")
    List<CheckTableDTO> afterMoveSortCheckTableList;

}
