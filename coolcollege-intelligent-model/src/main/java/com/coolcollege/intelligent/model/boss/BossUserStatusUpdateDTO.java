package com.coolcollege.intelligent.model.boss;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author zhangchenbiao
 * @FileName: BossUserStatusUpdateDTO
 * @Description:
 * @date 2021-09-17 17:30
 */
@Data
public class BossUserStatusUpdateDTO {

    @NotNull
    private Long id;

    @NotNull
    @Min(0)
    @Max(1)
    private Integer status;

}
