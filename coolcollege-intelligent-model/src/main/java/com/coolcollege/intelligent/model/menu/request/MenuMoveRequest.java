package com.coolcollege.intelligent.model.menu.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/09/23
 */
@Data
public class MenuMoveRequest {

    @NotNull(message = "id不能为空")
    private Long id;

    @JsonProperty("parent_id")
    @NotNull(message = "父idId不能为空")
    private Long parentId;



}
