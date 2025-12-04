package com.coolcollege.intelligent.model.menu.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/09/24
 */
@Data
public class MenuSortRequest {
    @JsonProperty("id_list")
    @NotNull(message = "idList不能为空")
    List<Long> idList;
}
