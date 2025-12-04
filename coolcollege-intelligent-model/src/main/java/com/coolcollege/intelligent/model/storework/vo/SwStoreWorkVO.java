package com.coolcollege.intelligent.model.storework.vo;

import com.coolcollege.intelligent.model.storework.dto.StoreWorkCommonDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @author   wxp
 * @date   2022-09-15 11:31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwStoreWorkVO implements Serializable {

    @ApiModelProperty("店务id")
    private Long id;

    @ApiModelProperty("店务名称")
    private String workName;
    /**
     * 门店范围
     */
    @ApiModelProperty("门店范围")
    private List<StoreWorkCommonDTO> storeRange;

    /**
     * 每天都完成日清的门店计为1，有1天没有完成则不计算在内。
     */
    @ApiModelProperty("已完成门店数")
    private Integer finishNum;

    /**
     * 指选择的门店范围内的门店数。（不包含闭店、未开业的门店）
     */
    @ApiModelProperty("总门店数")
    private Integer totalNum;

    @ApiModelProperty("店务有效期开始时间")
    private Date beginTime;

    @ApiModelProperty("店务有效期结束时间")
    private Date endTime;

    @ApiModelProperty("创建人")
    private String createUserName;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新人")
    private String updateUserName;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("编辑权限")
    private Boolean editFlag;

    @ApiModelProperty("是否逾期")
    private Boolean overdue;

    @ApiModelProperty("逾期是否允许继续执行，0：否，1：是")
    private Integer overdueContinue;

    @ApiModelProperty("店务状态 进行中ongoing 停止stop")
    private String status;

}
