package com.coolcollege.intelligent.model.patrolstore.dto;

import com.coolcollege.intelligent.model.ai.AIResolveDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 线下巡店AI结果DTO
 * </p>
 *
 * @author wxp
 * @since 2025/7/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatrolStoreAIResolveDTO {

    /**
     * 项id
     */
    private Long columnId;

    /**
     * AI状态
     */
    private Integer aiStatus;

    /**
     * AI失败原因
     */
    private String aiFailReason;

    /**
     * True/False 2中取值：
     * -True：审核通过
     * -False：审核不通过
     */
    private Boolean aiCheckStatus;

    /**
     * 审核结果描述：
     * 如果check_status为True，check_msg为空字符串；
     * 如果check_status为False，check_msg会给出审核不通
     * 过的描述，如“未检测到有效期，效期卡可能存在手写
     * 体、污渍、遮挡、弯曲折叠等情况”
     */
    private String aiCheckMsg;

    /**
     * 原图上叠加审核不通过的效期卡检测框结果图，
     * Base64编码
     */
    private String imageBase64;

    /**
     * AI结果
     */
    private AIResolveDTO aiResolveDTO;

}
