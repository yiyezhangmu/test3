package com.coolcollege.intelligent.model.patrolstore.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.coolcollege.intelligent.common.enums.patrol.PatrolPlanStatusEnum;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.model.patrolstore.entity.TbPatrolPlanDO;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhangchenbiao
 * @FileName: PatrolPlanPageVO
 * @Description:
 * @date 2024-09-04 11:37
 */
@Data
public class PatrolPlanPageVO {

    @ApiModelProperty("计划id")
    private Long planId;

    @ApiModelProperty("计划月份")
    private String planMonth;

    @ApiModelProperty("计划名称")
    @Excel(name = "计划名称", orderNum = "1", width = 30)
    private String planName;

    @ApiModelProperty("巡店人")
    @Excel(name = "巡店人", orderNum = "2", width = 20)
    private String supervisorUsername;

    @ApiModelProperty("审核人")
    @Excel(name = "审核人", orderNum = "3", width = 20)
    private String auditUsername;

    @ApiModelProperty("审核状态 0待审批，1待处理，2已驳回，3已完成")
    private Integer auditStatus;

    @Excel(name = "状态", orderNum = "4", width = 20)
    private String auditStatusName;

    @ApiModelProperty("总门店数量")
    @Excel(name = "巡店数量", orderNum = "5", width = 20)
    private Integer patrolTotalStoreNum;

    @ApiModelProperty("完成门店数量")
    @Excel(name = "已完成数量", orderNum = "6", width = 20)
    private Integer patrolFinishStoreNum;

    @ApiModelProperty("备注")
    @Excel(name = "备注", orderNum = "7", width = 20)
    private String remark;

    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间", orderNum = "8", width = 20, exportFormat = DateUtils.DATE_FORMAT_SEC_6)
    private Date createTime;

    @ApiModelProperty("任务类型")
    private String taskType;

    public static List<PatrolPlanPageVO> convertList( List<TbPatrolPlanDO> list, Map<String, String> userNameMap) {
        if(CollectionUtils.isEmpty(list)){
            return Lists.newArrayList();
        }
        List<PatrolPlanPageVO> result = new ArrayList<>(list.size());
        list.forEach(t -> {
            PatrolPlanPageVO patrolPlanPageVO = new PatrolPlanPageVO();
            BeanUtils.copyProperties(t, patrolPlanPageVO);
            patrolPlanPageVO.setPlanId(t.getId());
            patrolPlanPageVO.setSupervisorUsername(userNameMap.get(t.getSupervisorId()));
            patrolPlanPageVO.setAuditUsername(userNameMap.get(t.getAuditUserId()));
            patrolPlanPageVO.setTaskType("CALENDAR");
            patrolPlanPageVO.setAuditStatusName(PatrolPlanStatusEnum.MAP.get(patrolPlanPageVO.getAuditStatus()));
            result.add(patrolPlanPageVO);
        });
        return result;
    }
}
