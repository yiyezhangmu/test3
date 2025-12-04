package com.coolcollege.intelligent.model.newstore.vo;

import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseDictBaseVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhangnan
 * @date 2022-03-04 17:04
 */
@Data
public class NsStoreTypeVO extends EnterpriseDictBaseVO {

    @ApiModelProperty("新店类型")
    private String newStoreType;
}
