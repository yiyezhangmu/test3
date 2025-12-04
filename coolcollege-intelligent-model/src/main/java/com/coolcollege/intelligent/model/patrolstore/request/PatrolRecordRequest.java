package com.coolcollege.intelligent.model.patrolstore.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author byd
 */
@ApiModel
@Data
public class PatrolRecordRequest {
    /**
     * 开始时间
     */
    private String beginTime;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * 巡店类型
     */
    private String patrolType;
    /**
     * 巡店类型列表
     */
    private List<String> patrolTypeList;

    /**
     * 巡店方式 0 自主巡店 1 任务巡店  2-门店自检
     */
    private Integer patrolMode;

    /**
     * 是否逾期 0 未逾期 1 已逾期
     */
    private Boolean patrolOverdue;

    /**
     * 用户id
     */
    private List<String> userIdList;


    @NotNull
    private Integer pageSize = 10;
    @NotNull
    private Integer pageNum =1;

    /**
     * 近几天
     */
    private Integer recentDay;

    /**
     * 门店id
     */
    private List<String> storeIdList;

    /**
     * 巡店记录状态
     0 进行中 1 已完成 2 未开始
     */
    private Integer status;

    /**
     * 登录人userId
     */
    private String userId;

    /**
     * 登录人userName
     */
    private String userName;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 职位ids/角色ids
     */
    private List<Long> roleIdList;


    private String dbName;

    /**
     * 标准检查表id
     */
    private Long metaTableId;

    private String regionId;

    private String taskName;
    /**
     * 提交标识 存入库中的是0-7 的整数，前端吧这个整数转成二进制，比如7->111，代表检查表，总结，签名都提交
     */
    private Integer submitStatus;
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
     * 巡店ID
     */
    private String businessId;
    /**
     * 巡店人签名
     */
    private String supervisorSignature;

    private Integer saveOrSubmit;

    private Boolean getDirectStore = false;

    private Boolean userCreateTimeFilterDate = false ;

    /**
     * 创建起始时间
     */
    private String createBeginTime;

    /**
     * 创建结束时间
     */
    private String createEndTime;

    private String supervisorId;

    /**
     * 数据表id
     */
    private Long dataTableId;


    /**
     * 过滤逾期不可执行
     */
    private Boolean overdueTaskContinue;

    @ApiModelProperty("复审类型 0:可复审 1:已复审")
    private Integer recheckStatus;


    @ApiModelProperty(value = "复巡店检查: PATROL_STORE 巡店复审 PATROL_RECHECK", hidden = true)
    private String businessCheckType;

    /**
     * 复审人员id
     */
    @ApiModelProperty(value = "复审人员列表")
    private List<String> recheckUserIdList;
}
