package com.coolcollege.intelligent.model.base;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/09/23
 */
@Data
public class ClassIdBase {
    @NotNull(message = "id不能为空")
    private Long id;

}
