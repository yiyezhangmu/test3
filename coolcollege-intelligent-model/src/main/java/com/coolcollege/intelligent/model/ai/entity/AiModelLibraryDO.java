package com.coolcollege.intelligent.model.ai.entity;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author   wangfff
 * @date   2025-08-01 09:28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiModelLibraryDO implements Serializable {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("编码")
    private String code;

    @ApiModelProperty("用于兼容的旧code")
    private String aliasCode;

    @ApiModelProperty("类型，platform平台/model模型")
    private String type;

    @ApiModelProperty("是否展示")
    private Boolean display;

    @ApiModelProperty("平台code")
    private String platformCode;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("是否同步返回结果")
    private Boolean syncGetResult;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("扩展信息")
    private String extendInfo;

    /**
     * 获取结果策略
     */
    public String getResultStrategy() {
        return getExtendInfo(Constants.AI_MODEL_LIBRARY.RESULT_STRATEGY_KEY, String.class);
    }

    /**
     * 是否支持自定义提示语
     */
    public boolean isSupportCustomPrompt() {
        return Boolean.TRUE.equals(getExtendInfo(Constants.AI_MODEL_LIBRARY.SUPPORT_CUSTOM_PROMPT_KEY, Boolean.class));
    }

    public <T> T getExtendInfo(String key, Class<T> clazz) {
        JSONObject extendInfo = JSONObject.parseObject(this.extendInfo);
        if (Objects.nonNull(extendInfo)) {
            return extendInfo.getObject(key, clazz);
        }
        return null;
    }
}