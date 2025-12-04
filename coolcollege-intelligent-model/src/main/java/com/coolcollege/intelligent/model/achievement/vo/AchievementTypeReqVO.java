package com.coolcollege.intelligent.model.achievement.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description: 业绩类型请求VO
 * @Author: mao
 * @CreateDate: 2021/5/21 10:54
 */
@Data
public class AchievementTypeReqVO {
    /**
     * id
     */
    @NotNull(message = "业绩类型id不能为空")
    private Long id;
    /**
     * 业绩名称
     */
    private String name;
}
