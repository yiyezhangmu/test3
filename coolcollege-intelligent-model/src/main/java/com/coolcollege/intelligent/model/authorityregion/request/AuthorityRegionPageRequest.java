package com.coolcollege.intelligent.model.authorityregion.request;

import com.coolcollege.intelligent.model.page.PageBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: hu hu
 * @Date: 2024/11/25 16:54
 * @Description:
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("授权区域分页查询条件")
public class AuthorityRegionPageRequest extends PageBaseRequest {

    @ApiModelProperty("查询关键词")
    private String keyword;
}
