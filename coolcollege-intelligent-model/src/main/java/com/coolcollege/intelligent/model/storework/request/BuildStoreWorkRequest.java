package com.coolcollege.intelligent.model.storework.request;

import com.coolcollege.intelligent.model.storework.dto.StoreWorkCommonDTO;
import com.coolcollege.intelligent.model.storework.dto.PersonInfoDTO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkDateRangeDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * @author wxp
 */
@ApiModel(value = "店务创建")
@Data
public class BuildStoreWorkRequest {

    @ApiModelProperty("店务id")
    private Long storeWorkId;

    @ApiModelProperty(value = "名称", required = true)
    @NotBlank(message = "名称不能为空")
    @Length(max = 100, message = "名称最多100个字")
    private String workName;

    @ApiModelProperty("店务周期 DAY:天 WEEK:周 MONTH:月")
    @NotBlank(message = "店务周期不能为空")
    private String workCycle;

    @ApiModelProperty(value = "有效期-开始时间")
    @NotNull(message = "开始时间不能为空")
    private Long beginTime;

    @ApiModelProperty(value = "有效期-结束时间")
    @NotNull(message = "结束时间不能为空")
    private Long endTime;

    @ApiModelProperty("店务说明")
    @Length(max = 500, message = "店务说明最多500个字")
    private String workDesc;

    @ApiModelProperty("逾期是否允许执行，0：否，1：是")
    @NotNull(message = "逾期是否允许执行不能为空")
    private Integer overdueContinue;
    /**
     * 门店id
     * type store:门店，region区域,group分组
     */
    @ApiModelProperty("门店范围")
    @NotEmpty(message = "门店范围不能为空")
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
    @NotEmpty(message = "执行任务信息不能为空")
    private List<StoreWorkDutyInfoRequest> dutyInfoList;


    @ApiModelProperty("临时id,做数据缓存key用")
    private Long tempCacheDataId;

    @ApiModelProperty("不生成店务情况")
    private StoreWorkDateRangeDTO notGenerateRange;

    @ApiModelProperty("使用AI日期")
    private StoreWorkDateRangeDTO useAiRange;

    @ApiModelProperty("使用AI门店")
    private List<StoreWorkCommonDTO> aiStoreRange;

    @ApiModelProperty("使用AI门店方式")
    private String aiStoreRangeMethod;
}
