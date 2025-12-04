package com.coolcollege.intelligent.model.unifytask.query;

import com.coolcollege.intelligent.model.page.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;


/**
 * @author byd
 */
@ApiModel
@Data
public class QuestionQuery extends PageRequest {

    /**
     * 查询类型
     * 页面分类
     * @see com.coolcollege.intelligent.model.enums.TaskQueryEnum
     */
    @ApiModelProperty(value = "查询工单来源: 待我处理:pending  抄送:cc  我创建的:create", required = true)
    @NotBlank(message = "查询类型不能为空")
    private String queryType;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("工单名称")
    private String taskName;
}
