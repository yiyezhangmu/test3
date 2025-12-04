package com.coolcollege.intelligent.model.storework.vo;

import com.coolcollege.intelligent.model.coolrelation.vo.CoolCourseVO;
import com.coolcollege.intelligent.model.sop.vo.TaskSopVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 店务定义项信息
 * @author wxp
 * @date 2022-09-08 10:48
 */
@ApiModel
@Data
public class StoreWorkColumnInfoVO {
    /**
     * 检查项id
     */
    @ApiModelProperty("检查项id")
    private Long metaColumnId;

    @ApiModelProperty("分类")
    private String categoryName;

    @ApiModelProperty("检查项名称")
    private String metaColumnName;

    @ApiModelProperty("执行标准")
    private String description;

    @ApiModelProperty("是否开启ai检查")
    private Integer isAiCheck;

    @ApiModelProperty(value = "执行要求")
    private List<Boolean> executeDemand;

    @ApiModelProperty("sop文档")
    private TaskSopVO taskSopVO;
    @ApiModelProperty("酷学院课程信息")
    private CoolCourseVO coolCourseVO;
    @ApiModelProperty("酷学院课程信息")
    private CoolCourseVO freeCourseVO;

}
