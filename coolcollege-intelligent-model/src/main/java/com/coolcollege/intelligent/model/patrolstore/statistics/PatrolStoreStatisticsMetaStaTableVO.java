package com.coolcollege.intelligent.model.patrolstore.statistics;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.model.display.DisplayConstant;
import com.coolcollege.intelligent.model.video.SmallVideoDTO;
import com.coolcollege.intelligent.model.video.SmallVideoInfoDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author jeffrey
 * @date 2020/12/10
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreStatisticsMetaStaTableVO {

    private static final long serialVersionUID = 1L;

    /**
     * 得分率
     */
    private BigDecimal percent;

    private Long id;

    /**
     * 父任务id
     */
    private Long taskId;

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 门店名称
     */
    @Excel(name = "门店名称")
    private String storeName;

    @Excel(name = "门店编号")
    private String storeNum;

    /**
     * 区域ID
     */
    private Long regionId;


    /**
     * 巡店人id
     */
    private String supervisorId;

    /**
     * 巡店人姓名
     */
    @Excel(name = "巡店人")
    private String supervisorName;

    /**
     * 巡店开始时间
     */
    @Excel(name = "签到时间",  format = "yyyy.MM.dd HH:mm")
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm", timezone = "GMT+8")
    private Date signStartTime;

    /**
     * 创建时间
     */
    @Excel(name = "创建时间",  format = "yyyy.MM.dd HH:mm")
    private Date createTime;

    /**
     * 巡店结束时间
     */
    @Excel(name = "签退时间", format = "yyyy.MM.dd HH:mm")
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm", timezone = "GMT+8")
    private Date signEndTime;

    /**
     * 巡店开始地址
     */
    @Excel(name = "签到地址")
    private String signStartAddress;

    /**
     * 巡店结束地址
     */
    @Excel(name = "签退地址")
    private String signEndAddress;

    /**
     * 巡店开始定位经纬度
     */
    private String startLongitudeLatitude;

    /**
     * 巡店结束定位经纬度
     */
    private String endLongitudeLatitude;

    /**
     * 签到状态 1正常 2异常
     */
    @Excel(name = "签到状态")
    private Integer signInStatus;

    /**
     * 签退状态 1正常 2异常
     */
    @Excel(name = "签退状态")
    private Integer signOutStatus;

    /**
     * 巡店时长：毫秒
     */
    @Excel(name = "巡店时长（分钟）")
    private Long tourTime;

    /**
     * 巡店记录状态
     */
    private Integer status;

    /**
     * 巡店记录状态
     */
    @Excel(name = "任务状态")
    private String statusStr;


    /**
     * 巡店类型:offline,online,information,ai
     */
    @Excel(name = "类型")
    private String patrolType;

    /**
     * 表属性
     */
    private Integer tableProperty;


    /**
     * 检查表id
     */
    private Long metaTableId;

    /**
     * 检查表名称
     */
    @Excel(name = "任务内容")
    private String metaTableName;


    /**
     * 巡店检查表类型 DEFINE(自定义) STANDARD(标准检查表)
     */
    private String tableType;

    /**
     * 任务名称
     */
    @Excel(name = "任务名称")
    private String taskName;

    /**
     * 子任务审批链开始时间
     */
    private Date subBeginTime;

    /**
     * 子任务审批链结束时间
     */
    private Date subEndTime;

    /**
     * 总检查项数
     */
    @Excel(name = "总检查项数")
    private int totalColumnCount;

    /**
     * 合格项数
     */
    @Excel(name = "合格项数")
    private Integer passColumnCount;

    /**
     * 不合格项数
     */
    @Excel(name = "不合格项数")
    private Integer failColumnCount;

    /**
     * 不合格项数
     */
    @Excel(name = "不适用项数")
    private Integer inapplicableColumnCount;


    /**
     * 区域名称
     */
    private String regionName;

    @Excel(name = "所属区域")
    private String fullRegionName;

    /**
     * 区域名称列表
     */
    private List<String> regionNameList;

    /**
     * 任务类型
     */
    private String taskType;
    /**
     * 任务描述
     */
    @Excel(name = "任务说明")
    private String taskDesc;

    /**
     * 得分
     */
    @Excel(name = "得分")
    private BigDecimal score;

    /**
     * 总分
     */
    @Excel(name = "总分")
    private BigDecimal totalScore;

    /**
     * 巡店结果
     */
    @Excel(name = "巡店结果")
    private String checkResult;


    /**
     * 是否延期完成
     */
    @Excel(name = "是否逾期完成")
    private String isOverdue;

    /**
     * 奖罚金额
     */
    @Excel(name = "奖罚金额")
    private Double rewardPenaltMoney;


    /**
     * 巡店人姓名
     */
    private String createrUserName;
    /**
     * 检查项列表
     */
    @ExcelCollection(name = "数据列")
    private List<TbMetaStaColumnVO> staColumnList;

    private String signInStatusStr;

    private String signOutStatusStr;

    /**
     * 任务有效期
     */
    @Excel(name = "任务有效期")
    private String validTime;

    @Excel(name = "签到/签退方式")
    private String signWay;

    @Excel(name = "签到是否异常")
    private String signInRemark;

    @Excel(name = "签退是否异常")
    private String signOutRemark;



    /**
     * 审核人userId
     */
    private String auditUserId;

    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * 审核图片
     */
    private String auditPicture;

    /**
     * 审核意见
     */
    private String auditOpinion;

    /**
     * 审核意见
     */
    private String auditOpinionStr;

    /**
     * 审核人姓名
     */
    private String auditUserName;

    /**
     * 审核人备注
     */
    private String auditRemark;

    /**
     * 巡店总结
     */
    private String summary;

    /**
     * 巡店总结图片
     */
    private String summaryPicture;

    /**
     * 巡店总结视频
     */
    private String summaryVideo;

    /**
     * 巡店总结视频
     */
    private String summaryInfo;

    public String getSummaryInfo(){
        String summaryInfo = this.summaryPicture;
        if(StringUtils.isBlank(this.summaryVideo)){
            return summaryInfo;
        }
        SmallVideoInfoDTO  smallVideoInfoDTO = JSONObject.parseObject(this.summaryVideo, SmallVideoInfoDTO.class);
        if(CollectionUtils.isEmpty(smallVideoInfoDTO.getVideoList())){
            return summaryInfo;
        }
        StringBuilder videoSb = new StringBuilder();
        for (SmallVideoDTO smallVideoDTO : smallVideoInfoDTO.getVideoList()) {
            if(StringUtils.isNotBlank(smallVideoDTO.getVideoUrl())){
                videoSb.append(",").append(smallVideoDTO.getVideoUrl());
            }
        }
        summaryInfo += videoSb.toString();
        return summaryInfo;
    }

    /**
     * 巡店签名
     */
    private String supervisorSignature;

    public String getStatusStr() {
        if(this.status == 1){
            return "已完成";
        }
        if(this.status == 2){
            return "待审批";
        }
        if(this.status == 3){
            return "未开始";
        }
        return "待处理";
    }

    public String getAuditOpinionStr() {
        if(DisplayConstant.ActionKeyConstant.PASS.equals(this.auditOpinion)){
            return "通过";
        }
        if(DisplayConstant.ActionKeyConstant.REJECT.equals(this.auditOpinion)){
            return "拒绝";
        }
        return "";
    }
    /**
     * 是否逾期完成
     */
    private String overdue;

    /**
     * 格式化
     */
    private String signInOutStatusStr;

    /**
     * @修改字段：规则时间 (企业设置的时间)
     */
    private String tourTimeStr;

    /**
     * @新增字段：时间巡店时长 (实际巡店时间:签退时间-签到时间)
     * actualPatrolStoreDuration
     */
    private String actualPatrolStoreDuration;

    /**
     * @修改字段：规则时间 (企业设置的时间)
     */
    private String tourTimeStrBySeconds;

    /**
     * @新增字段：时间巡店时长 (实际巡店时间:签退时间-签到时间)
     * actualPatrolStoreDuration
     */
    private String actualPatrolStoreDurationBySeconds;

    /**
     * @新增字段：签到日期
     */
    @JsonFormat(pattern = "yyyy.MM.dd", timezone = "GMT+8")
    private Date signInDate;

    /**
     * @新增字段：门店地址
     */
    private String storeAddress;

    @ApiModelProperty("巡店内容列表")
    private List<PatrolDataTableVO> dataTableVOList;

    /**
     * 复审的巡店记录id
     */
    private Long recheckBusinessId;

    /**
     * 巡店检查类型  巡店检查: PATROL_STORE 巡店复审 PATROL_RECHECK
     */
    private String businessCheckType;

    /**
     * 复审人userId
     */
    private String recheckUserId;

    /**
     * 复审人名称
     */
    private String recheckUserName;

    /**
     * 复审时间
     */
    private Date recheckTime;


    /**
     * 巡店类型
     */
    private String recordPatrolType;

    /**
     * 签到图片
     */
    private String signInImg;

    /**
     * 签退图片
     */
    private String signOutImg;

//    public String getTourTimeStr() {
//        if (tourTime == null) {
//            return null;
//        }
//        long minute = tourTime / 1000 / 60;
//        long second = (tourTime - minute * 1000 * 60) / 1000;
//        return minute + "分" + second + "秒";
//    }
}
