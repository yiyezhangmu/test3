package com.coolcollege.intelligent.model.sop.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author 邵凌志
 * @date 2021/2/20 16:49
 */
@Data
public class TaskSopClassifyDTO {

    private Long id;

    @NotBlank(message = "分类名称不能为空")
    private String classifyName;

    private String updateUserId;

    private String updateUser;
}
