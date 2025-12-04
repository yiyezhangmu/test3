package com.coolcollege.intelligent.model.newstore.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author zhangnan
 * @date 2022-03-04 17:04
 */
@Data
public class NsStoreTypeAddOrUpdateRequest {

    @ApiModelProperty("类型id，更新必传")
    private Long id;

    @Length(max = 20, message = "门店类型名称不能超过20个字")
    @ApiModelProperty("新店类型")
    private String newStoreType;
}
