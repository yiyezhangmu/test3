package com.coolcollege.intelligent.model.storework.vo;

import com.coolcollege.intelligent.model.storework.dto.StoreWorkCommonDTO;
import com.coolcollege.intelligent.model.storework.dto.PersonInfoDTO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkDateRangeDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 店务详情VO
 * @author   wxp
 * @date   2022-09-15 11:31
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SwStoreWorkDetailVO {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("店务名称")
    private String workName;

    @ApiModelProperty("店务周期 DAY:天 WEEK:周 MONTH:月")
    private String workCycle;

    @ApiModelProperty("店务有效期开始时间")
    private Date beginTime;

    @ApiModelProperty("店务有效期结束时间")
    private Date endTime;

    @ApiModelProperty("店务说明")
    private String workDesc;

    @ApiModelProperty("逾期是否允许继续执行，0：否，1：是")
    private Integer overdueContinue;

    @ApiModelProperty("店务状态 进行中ongoing 停止stop")
    private String status;
    /**
     * 门店id
     * type store:门店，region区域,group分组
     */
    @ApiModelProperty("门店范围")
    private List<StoreWorkCommonDTO> storeRangeList;
    /**
     * 点评人 协作人 信息
     */
    @ApiModelProperty("点评人、协作人信息")
    private PersonInfoDTO personInfo;
    /**
     * 执行人 及  检查表  时间阶段  关系
     */
    @ApiModelProperty("执行任务信息")
    private List<StoreWorkDutyGroupInfoVO> dutyInfoList;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("店务创建人id")
    private String createUserId;

    @ApiModelProperty("创建人名称")
    private String createUserName;

    @ApiModelProperty("不生产日清任务情况")
    private StoreWorkDateRangeDTO notGenerateRange;

    @ApiModelProperty("使用AI日期")
    private StoreWorkDateRangeDTO useAiRange;

    @ApiModelProperty("使用AI门店")
    private List<StoreWorkCommonDTO> aiStoreRange;

    @ApiModelProperty("使用AI门店方式")
    private String aiStoreRangeMethod;
}