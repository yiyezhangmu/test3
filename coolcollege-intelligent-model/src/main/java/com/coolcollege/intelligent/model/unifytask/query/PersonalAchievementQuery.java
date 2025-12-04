package com.coolcollege.intelligent.model.unifytask.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;


/**
 * @author byd
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalAchievementQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "大类")
    private String mainClass;

    @ApiModelProperty(value = "职位id")
    private String positionId;

    @ApiModelProperty("品类")
    private String category;

    @ApiModelProperty("中类")
    private String middleClass;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("开始时间")
    private Date startDate;

    @ApiModelProperty("截止时间")
    private Date endDate;

    @ApiModelProperty("用户id")
    private String userId;

    private Integer pageNum=1;
    private Integer pageSize=10;
}
