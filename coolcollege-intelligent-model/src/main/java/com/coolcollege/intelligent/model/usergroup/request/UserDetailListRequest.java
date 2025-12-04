package com.coolcollege.intelligent.model.usergroup.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.List;


/**
 * @author wxp
 */
@ApiModel(value = "")
@Data
public class UserDetailListRequest {

    @ApiModelProperty(value = "所选人员范围[{type:person,value:}{type:position,value:}]", required = true)
    private String usePersonInfo;

    @ApiModelProperty(value = "使用人范围：self-仅自己，all-全部人员，part-部分人员", required = true)
    private String useRange;

    @ApiModelProperty(value = "创建人id", required = false)
    private String createUserId;
}
