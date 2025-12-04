package com.coolcollege.intelligent.model.patrolstore.vo;

import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.metatable.dto.TableColumnCountDTO;
import com.coolcollege.intelligent.model.patrolstore.entity.TbPatrolPlanDO;
import com.coolcollege.intelligent.model.patrolstore.entity.TbPatrolPlanDetailDO;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: PatrolPlanPageVO
 * @Description:
 * @date 2024-09-04 11:37
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolPlanDetailVO {

    @ApiModelProperty("自增ID")
    private Long planId;

    @ApiModelProperty("计划月份")
    private String planMonth;

    @ApiModelProperty("计划名称")
    private String planName;

    @ApiModelProperty("巡店人")
    private String supervisorId;

    @ApiModelProperty("巡店人姓名")
    private String supervisorUsername;

    @ApiModelProperty("审核人")
    private String auditUserId;

    @ApiModelProperty("审核人姓名")
    private String auditUsername;

    @ApiModelProperty("总门店数量")
    private Integer patrolTotalStoreNum;

    @ApiModelProperty("完成门店数量")
    private Integer patrolFinishStoreNum;

    @ApiModelProperty("审核状态 0待审批，1待处理，2已驳回，3已完成")
    private Integer auditStatus;

    @ApiModelProperty("多个检查表的ID")
    private List<MetaTableVO> metaTableList;

    @ApiModelProperty("巡店总结")
    private Boolean isOpenSummary;

    @ApiModelProperty("巡店签名")
    private Boolean isOpenAutograph;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("门店明细")
    private List<StorePlanDetailVO> storePlanList;

    @Data
    public static class MetaTableVO{

        @ApiModelProperty("表id")
        private Long metaTableId;

        @ApiModelProperty("表名称")
        private String tableName;

        @ApiModelProperty("表项数")
        private Integer columnCount;
    }

    public static List<MetaTableVO> getMetaTableList(List<TbMetaTableDO> metaTableList, List<TableColumnCountDTO> columnCount) {
        if(Objects.isNull(metaTableList) || Objects.isNull(columnCount)){
            return new ArrayList<>();
        }
        Map<Long, Integer> columnCountMap = columnCount.stream().collect(Collectors.toMap(k -> k.getMetaTableId(), v -> v.getColumnCount()));
        List<MetaTableVO> resultList = new ArrayList<>();
        for (TbMetaTableDO metaTable : metaTableList) {
            MetaTableVO result = new MetaTableVO();
            result.setMetaTableId(metaTable.getId());
            result.setTableName(metaTable.getTableName());
            result.setColumnCount(columnCountMap.get(metaTable.getId()));
            resultList.add(result);
        }
        return resultList;
    }

    public static PatrolPlanDetailVO convert(TbPatrolPlanDO tbPatrolPlanDO, List<StorePlanDetailVO> storePlanList, Map<String, String> userNameMap, List<PatrolPlanDetailVO.MetaTableVO> metaTableList) {
        if(Objects.isNull(userNameMap)){
            userNameMap = Maps.newHashMap();
        }
        return PatrolPlanDetailVO.builder().planId(tbPatrolPlanDO.getId())
                .planMonth(tbPatrolPlanDO.getPlanMonth()).planName(tbPatrolPlanDO.getPlanName())
                .supervisorId(tbPatrolPlanDO.getSupervisorId()).auditUserId(tbPatrolPlanDO.getAuditUserId())
                .patrolTotalStoreNum(tbPatrolPlanDO.getPatrolTotalStoreNum())
                .patrolFinishStoreNum(tbPatrolPlanDO.getPatrolFinishStoreNum())
                .auditStatus(tbPatrolPlanDO.getAuditStatus()).metaTableList(metaTableList)
                .isOpenSummary(tbPatrolPlanDO.getIsOpenSummary()).isOpenAutograph(tbPatrolPlanDO.getIsOpenAutograph())
                .remark(tbPatrolPlanDO.getRemark())
                .storePlanList(storePlanList).supervisorUsername(userNameMap.get(tbPatrolPlanDO.getSupervisorId())).auditUsername(userNameMap.get(tbPatrolPlanDO.getAuditUserId()))
                .createTime(tbPatrolPlanDO.getCreateTime())
                .build();
    }
}
