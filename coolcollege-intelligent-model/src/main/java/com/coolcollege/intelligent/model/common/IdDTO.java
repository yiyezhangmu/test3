package com.coolcollege.intelligent.model.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: IdDTO
 * @Description:
 * @date 2021-09-17 17:34
 */
@ApiModel
@Data
public class IdDTO {

    @ApiModelProperty("id")
    @NotNull
    private Long id;

    private List<Long> unifyTaskIds;

}
