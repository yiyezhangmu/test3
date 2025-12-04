package com.coolcollege.intelligent.model.metatable;

import com.coolcollege.intelligent.model.enums.LevelRuleEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author 
 * 
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbMetaTableDO implements Serializable {
    /**
     * ID
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date editTime;

    /**
     * 检查表名称
     */
    private String tableName;

    /**
     *  描述信息
     */
    private String description;

    /**
     * 创建者id
     */
    private String createUserId;

    /**
     * 创建用户名
     */
    private String createUserName;

    /**
     * 是否支持分值
     */
    private Integer supportScore;

    /**
     * 是否锁定，当发布新任务后表锁定，增加复制表的功能
     */
    private Integer locked;

    /**
     * 是否有效
     */
    private Integer active;

    /**
     * 巡检标准表 || 巡检自定义表|| 陈列表
     */
    private String tableType;

    /**
     * 共享定义，预留字段
     */
    private String shareGroup;

    /**
     * 删除标记  0:正常 1:删除
     */
    private Integer deleted;

    /**
     * 更新用户id
     */
    private String editUserId;

    /**
     * 更新用户名称
     */
    private String editUserName;

    /**
     * 可见人名称
     */
    private String shareGroupName;

    /**
     * 结果可见人
     */
    private String resultShareGroup;

    /**
     * 结果可见人名称
     */
    private String resultShareGroupName;

    /**
     * 置顶时间
     */
    private Date topTime;

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

    /**
     * 是否开启高级检查项
     */

    /**
     * 门店场景ID
     */
    private Long storeSceneId;

    /***
     *  0:默认不选中, 1:默认选中合格, 2:默认选中不合格
     */
    private Integer defaultResultColumn;

    /**
     * 0:不计入总项数，1:计入总项数
     */
    private Boolean noApplicableRule;

    /**
     * 分类名称集合
     */
    private String categoryNameList;

    /**
     * 排序
     */
    private Integer orderNum;

    /**
     * 归档状态 0未归档 1已归档
     */
    private Integer status;
    /**
     * 检查表总分
     */
    private BigDecimal totalScore;
    /**
     * 表属性 0:普通表 1:高级表 2:加分表 3:权重表 4:扣分表 5:AI检查表 6:自定义表
     */
    private Integer tableProperty;

    /**
     * 原始选取的使用人[{type:person,value:}{type:position,value:}]
     */
    private String usePersonInfo;

    /**
     * 使用人范围：self-仅自己，all-全部人员，part-部分人员
     */
    private String useRange;

    /**
     * 选取的结果查看人[{type:person,value:}{type:position,value:}]
     */
    private String resultViewPersonInfo;

    /**
     * 结果可见范围：self-仅自己，all-全部人员，part-部分人员
     */
    private String resultViewRange;

    /**
     * 共同编辑人userId集合（前后逗号分隔） 删除
     */
    private String commonEditUserids;

    private List<String> editUserIdList;


    @ApiModelProperty("共同编辑人[{type:person,value:}{type:position,value:}]")
    private String commonEditPersonInfo;

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("复制后的新检查表中是否可以修改已有的检查项 1：可以 0：不可以")
    private Boolean copyModify;

    /**
     * 是否支持负分 0：不支持 1：支持
     */
    private Integer isSupportNegativeScore;

    /**
     * child：表节点   leaf：叶子节点
     */
    private String sopType;

    private String sopPath;

    private Long pid;

    /**
     * 扩展信息
     */
    private String extendInfo;

    /**
     * AI结果处理方式，0仅作为参考、1作为检查结果
     */
    private Integer aiResultMethod;

    /**
     * 是否开启AI检查
     */
    private Integer isAiCheck;

    public TbMetaTableDO(Date createTime, Date editTime, String tableName, String description, String createUserId,
                         String createUserName, Integer supportScore, Integer locked, Integer active, String tableType,
                         Integer deleted, String editUserId, String editUserName,Integer tableProperty) {
        this.createTime = createTime;
        this.editTime = editTime;
        this.tableName = tableName;
        this.description = description;
        this.createUserId = createUserId;
        this.createUserName = createUserName;
        this.supportScore = supportScore;
        this.locked = locked;
        this.active = active;
        this.tableType = tableType;
        this.deleted = deleted;
        this.editUserId = editUserId;
        this.editUserName = editUserName;
        this.tableProperty = tableProperty;
    }
}