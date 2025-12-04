package com.coolcollege.intelligent.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/04/29
 */
@Data
public class SerializationId  {
    @NotNull(message = "id不能为空")
    private Long id;
}
