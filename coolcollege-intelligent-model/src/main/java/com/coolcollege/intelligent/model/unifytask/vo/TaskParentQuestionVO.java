package com.coolcollege.intelligent.model.unifytask.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * @author byd
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskParentQuestionVO {

    /**
     * ID
     */
    @ApiModelProperty("父工单id")
    private Long id;
    /**
     * 任务名称
     */
    @ApiModelProperty("工单名称")
    private String questionName;

    /**
     * 工单详情列表
     */
    @ApiModelProperty("子工单列表")
    private List<UnifyTaskParentItemVO> itemList;
}
