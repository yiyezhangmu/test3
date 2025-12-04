package com.coolcollege.intelligent.model.menu.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/01/26
 */
@Data
public class AppMenuDeleteRequest {
    @NotBlank(message = "ID不能为空")
    private Long id;
}
