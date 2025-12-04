package com.coolcollege.intelligent.model.user;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: BatchUserStatusDTO
 * @Description: 批量操作用户状态
 * @date 2021-07-21 10:00
 */
@Data
public class BatchUserStatusDTO {

    /**
     * unionid集合
     */
    @NotEmpty(message = "unionid 不能为空")
    private List<String> unionids;

    /**
     * 用户状态 0待审核 1正常 2冻结
     */
    @NotNull(message = "用户状态不能为空")
    @Min(0)
    @Max(2)
    private Integer userStatus;

}
