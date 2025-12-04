package com.coolcollege.intelligent.model.ai;

import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 企业AI算法模型库
 * @author   zhangchenbiao
 * @date   2025-09-25 03:50
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseModelAlgorithmDTO implements Serializable {

    @ApiModelProperty("企业Id")
    private String enterpriseId;

    @ApiModelProperty("场景id")
    private Long sceneId;

    @ApiModelProperty("场景名称")
    private String sceneName;

    @ApiModelProperty("场景图片")
    private String scenePic;

    @ApiModelProperty("模型名称")
    private String modelName;

    @ApiModelProperty("模型编码")
    private String modelCode;

    @ApiModelProperty("系统prompt")
    private String systemPrompt;

    @ApiModelProperty("用户prompt")
    private String userPrompt;

    @ApiModelProperty("特殊prompt 如果有值需要跟用户promet拼接起来统一传值")
    private String specialPrompt;

    @ApiModelProperty("标准图")
    private String standardPic;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("启用状态 0: 未启用 1:已启用")
    private Integer algorithmStatus;

    @ApiModelProperty("分组id")
    private Long groupId;

    @ApiModelProperty("分组名称")
    private String groupName;

    @ApiModelProperty("场景介绍")
    private String standardDesc;

    @ApiModelProperty("是否支持编辑prompt")
    private Boolean supportCustomPrompt;

    @ApiModelProperty("默认userPrompt")
    private String defaultUserPrompt;

    @ApiModelProperty("是否支持编辑数字码力")
    private Boolean editFlag;

    /**
     * 节点信息
     */
    @ApiModelProperty("流程节点信息(处理人审批人抄送人 1103")
    private List<TaskProcessDTO> process;

    /**
     * 有效期设置策略 0-跟随全局配置 1-需要设置有效期
     */
    @ApiModelProperty("有效期设置策略 0-跟随全局配置 1-需要设置有效期 1103")
    private Integer expiryPolicy;

    /**
     * 问题工单有效期（单位小时）
     */
    @ApiModelProperty("问题工单有效期（单位小时）1103")
    private Integer expiryTimes;

}