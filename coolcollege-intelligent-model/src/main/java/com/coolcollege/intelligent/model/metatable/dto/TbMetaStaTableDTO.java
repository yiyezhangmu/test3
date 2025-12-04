package com.coolcollege.intelligent.model.metatable.dto;

import com.coolcollege.intelligent.model.enums.LevelRuleEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@ApiModel
@Data
public class TbMetaStaTableDTO {
    /**
     * ID
     */
    private Long id;
    /**
     * 检查表名称
     */
    private String tableName;

    /**
     *  描述信息
     */
    @ApiModelProperty("检查表描述")
    private String description;


    /**
     * 是否支持分值
     */
    private Integer supportScore;


    @ApiModelProperty("检查项列表")
    private List<TbMetaStaColumnDTO> staColumnDTOList;

    private List<Long> metaTableIds;

    private List<Long> columnIdList;

    private String tableType;

    private Boolean isAll;

    /**
     * 结果可见范围
     */
    private String resultShareGroup;
    /**
     * 结果项可见范围 person 人  position 职位
     */
    @ApiModelProperty("结果项可见范围_职位")
    private String resultShareGroupPosition;

    /**
     * 共享定义，预留字段
     */
    private String shareGroup;

    /**
     * 检查表查看人 person 人  position 职位
     */
    @ApiModelProperty("结果查看人_职位")
    private String shareGroupPosition;

    /**
     * @see LevelRuleEnum
     * @Description 等级规则
     */
    private String levelRule;

    /**
     * @see com.coolcollege.intelligent.model.enterprise.dto.EnterprisePatrolLevelDTO
     * @Description 巡店等级信息
     */
    private String levelInfo;

    private Long storeSceneId;

    @ApiModelProperty("'0:默认不选中, 1:默认选中合格, 2:默认选中不合格")
    private Integer defaultResultColumn;

    @ApiModelProperty("0:不计入总项数，1:计入总项数")
    private Boolean noApplicableRule;
    @ApiModelProperty("表属性 0:普通表 1:高级表 ...")
    private Integer tableProperty;

    private String tablePropertyStr;
    /**
     * 检查表总分
     */
    @ApiModelProperty("检查表总分")
    private BigDecimal totalScore;
    @ApiModelProperty("分类名称集合")
    private List<String> categoryNameList;
    @ApiModelProperty("归档状态 0未归档 1已归档")
    private Integer status = 0;

    /**
     * 原始选取的使用人[{type:person,value:}{type:position,value:}]
     */
    @ApiModelProperty("原始选取的使用人[{type:person,value:}{type:position,value:}]")
    private String usePersonInfo;

    /**
     * 使用人范围：self-仅自己，all-全部人员，part-部分人员
     */
    @ApiModelProperty("使用人范围：self-仅自己，all-全部人员，part-部分人员")
    private String useRange;


    /**
     * 选取的结果查看人[{type:person,value:}{type:position,value:}]
     */
    @ApiModelProperty("原始选取的使用人[{type:person,value:}{type:position,value:}]")
    private String resultViewPersonInfo;

    /**
     * 结果可见范围：self-仅自己，all-全部人员，part-部分人员
     */
    @ApiModelProperty("结果可见范围：self-仅自己，all-全部人员，part-部分人员")
    private String resultViewRange;

    @ApiModelProperty("共同编辑人userId集合")
    private List<String> commonEditUserIdList;

    @ApiModelProperty("共同编辑人[{type:person,value:}{type:position,value:}]")
    private String commonEditPersonInfo;

    /**
     * 共同编辑人userId集合（前后逗号分隔）
     */
    @ApiModelProperty("结果查看人是否包含使用人")
    private Boolean resultViewUserWithUserRang;

    @ApiModelProperty("复制后的新检查表中是否可以修改已有的检查项 1：可以 0：不可以")
    private Boolean copyModify = Boolean.TRUE;

    @ApiModelProperty("是否支持负分 1：支持 0：不支持")
    private Integer isSupportNegativeScore;

    private long pid;

    @ApiModelProperty("AI结果处理方式，0仅作为参考、1作为检查结果")
    private Integer aiResultMethod;

    @ApiModelProperty("是否开启AI检查")
    private Integer isAiCheck;
}
