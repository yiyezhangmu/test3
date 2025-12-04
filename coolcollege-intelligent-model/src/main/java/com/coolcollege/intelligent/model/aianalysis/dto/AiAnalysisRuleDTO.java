package com.coolcollege.intelligent.model.aianalysis.dto;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.group.InsertGroup;
import com.coolcollege.intelligent.common.group.UpdateGroup;
import com.coolcollege.intelligent.model.aianalysis.AiAnalysisRuleDO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkCommonDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * <p>
 * AI分析规则新增编辑DTO
 * </p>
 *
 * @author wangff
 * @since 2025/6/30
 */
@Data
public class AiAnalysisRuleDTO {
    @ApiModelProperty("id")
    @NotNull(message = "id不能为空", groups = UpdateGroup.class)
    private Long id;

    @ApiModelProperty("规则名称")
    @NotBlank(message = "规则名称不能为空", groups = {InsertGroup.class, UpdateGroup.class})
    private String ruleName;

    @ApiModelProperty("有效期开始时间")
    @NotNull(message = "规则名称不能为空", groups = {InsertGroup.class, UpdateGroup.class})
    private LocalDate startTime;

    @ApiModelProperty("有效期结束时间")
    @NotNull(message = "规则名称不能为空", groups = {InsertGroup.class, UpdateGroup.class})
    private LocalDate endTime;

    @ApiModelProperty("抓拍设备，0所有设备、1指定场景设备")
    @NotNull(message = "规则名称不能为空", groups = {InsertGroup.class, UpdateGroup.class})
    private Integer captureDevice;

    @ApiModelProperty("抓拍设备场景，多选逗号隔开")
    private String captureDeviceScene;

    @ApiModelProperty("抓拍时间，时分，多选逗号隔开")
    @NotBlank(message = "抓拍时间不能为空", groups = {InsertGroup.class, UpdateGroup.class})
    private String captureTimes;

    @ApiModelProperty("AI分析模型id列表，多选逗号隔开")
    @NotBlank(message = "AI分析模型id列表不能为空", groups = {InsertGroup.class, UpdateGroup.class})
    private String models;

    @ApiModelProperty("报告推送时间")
    @NotNull(message = "报告推送时间不能为空", groups = {InsertGroup.class, UpdateGroup.class})
    private LocalTime reportPushTime;

    @ApiModelProperty("门店范围")
    @NotEmpty(message = "门店范围不能为空", groups = {InsertGroup.class, UpdateGroup.class})
    private List<StoreWorkCommonDTO> storeRange;

    @ApiModelProperty("报告推送人")
    @NotEmpty(message = "报告推送人不能为空", groups = {InsertGroup.class, UpdateGroup.class})
    private List<StoreWorkCommonDTO> reportPusher;

    public AiAnalysisRuleDO convertToDO(boolean isUpdate) {
        CurrentUser user = UserHolder.getUser();
        AiAnalysisRuleDO.AiAnalysisRuleDOBuilder builder = AiAnalysisRuleDO.builder()
                .id(id)
                .ruleName(ruleName)
                .startTime(startTime)
                .endTime(endTime)
                .captureDevice(captureDevice)
                .captureDeviceScene(captureDeviceScene)
                .captureTimes(captureTimes)
                .models(models)
                .reportPushTime(reportPushTime)
                .storeRange(JSONObject.toJSONString(storeRange))
                .reportPusher(JSONObject.toJSONString(reportPusher))
                .updateUserId(user.getUserId())
                .updateUserName(user.getName());
        if (!isUpdate) {
            builder.createUserId(user.getUserId())
                    .createUserName(user.getName());
        }
        return builder.build();
    }
}
