package com.coolcollege.intelligent.model.brand.request;

import com.coolcollege.intelligent.common.group.InsertGroup;
import com.coolcollege.intelligent.common.group.UpdateGroup;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * <p>
 * 品牌更新请求类
 * </p>
 *
 * @author wangff
 * @since 2025/3/6
 */
@Data
public class EnterpriseBrandUpdateRequest {
    @ApiModelProperty("主键")
    @NotNull(message = "id不能为空", groups = {UpdateGroup.class})
    private Long id;

    @ApiModelProperty("品牌名称")
    @NotEmpty(message = "品牌名称不能为空", groups = {InsertGroup.class, UpdateGroup.class})
    @Size(max = 50, message = "品牌名称长度为1-50个字符", groups = {InsertGroup.class, UpdateGroup.class})
    private String name;

    @ApiModelProperty("品牌code")
    @Pattern(regexp = "^[a-zA-Z0-9]{6}$", message = "品牌code仅限长度为6的大小写字母及数字", groups = {InsertGroup.class})
    private String code;

    @ApiModelProperty("扩展字段")
    private String extendInfo;
}
