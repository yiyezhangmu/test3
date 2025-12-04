package com.coolcollege.intelligent.model.patrolstore.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @author shuchang.wei
 * @date 2021/1/8 15:34
 */
@Data
@ToString
public class QuestionListRequest {
    private List<Long> roleIdList;
    private List<String> userIdList;

    private List<String> storeIdList;

    private String regionId;

    private List<Long> recordIdList;
    private Date beginDate;
    private Date endDate;
    private Integer pageNum;
    private Integer pageSize;

    private Boolean getDirectStore = false ;

    /**
     *  unify_task_store 工单状态
     *  1 待处理 2 待复审 endNode 已完成
     */
    private String nodeNo;

    @ApiModelProperty("工单来源")
    private String questionType;

    @ApiModelProperty(value = "检查表id")
    private Long metaTableId;

    @ApiModelProperty(value = "检查项ids,Get请求逗号分隔就可以")
    private List<Long> metaColumnIds;

    @ApiModelProperty(value = "是否超期")
    private Boolean isOverDue;
}
