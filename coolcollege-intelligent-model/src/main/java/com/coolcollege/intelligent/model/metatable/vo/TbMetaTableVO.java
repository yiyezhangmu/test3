package com.coolcollege.intelligent.model.metatable.vo;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author
 * 
 */
@ApiModel("检查表返回实体类")
@Data
public class TbMetaTableVO implements Serializable {
    /**
     * ID
     */
    @ApiModelProperty("检查表id")
    private Long id;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    private Date createTime;

    @ApiModelProperty("修改人id")
    private String updateUserId;

    @ApiModelProperty("修改人名称")
    private String updateUserName;

    @ApiModelProperty("可见范围人")
    private String shareGroup;

    @ApiModelProperty("可见范围名称")
    private String shareGroupName;

    /**
     * 检查表名称
     */
    @ApiModelProperty("检查表名称")
    private String tableName;


    /**
     * 巡检标准表 || 巡检自定义表|| 陈列表
     */
    @ApiModelProperty("巡检标准表(STANDARD) || 巡检自定义表(DEFINE )|| 陈列表(TB_DISPLAY)")
    private String tableType;


    /**
     * 是否锁定，当发布新任务后表锁定，增加复制表的功能
     */
    @ApiModelProperty("是否锁定，当发布新任务后表锁定，增加复制表的功能 0:未锁定 1：已锁定")
    private Integer locked;

    /**
     * 是否有效
     */
    @ApiModelProperty("是否有效")
    private Integer active;

    /**
     * 是否提交
     */
    @ApiModelProperty("是否提交")
    private Integer isSubmit;

    /**
     * 数据表id
     */
    @ApiModelProperty("数据表id")
    private Long dataTableId;

    /**
     * 巡店记录id
     */
    @ApiModelProperty("巡店记录id")
    private Long businessId;

    /**
     * 检查项数
     */
    @ApiModelProperty("检查项数")
    private Integer columnCount;

    /**
     * 检查项分类数
     */
    @ApiModelProperty("检查项分类数")
    private Integer columnCategoryCount;

    /**
     * 检查表结果可见人id
     */
    @ApiModelProperty("检查表结果可见人id")
    private String resultShareGroup;

    /**
     * 检查表结果可见人名称
     */
    @ApiModelProperty("检查表结果可见人名称")
    private String resultShareGroupName;

    /**
     * 创建人id
     */
    @ApiModelProperty("创建人id")
    private String createUserId;

    /**
     * 创建人是否激活
     */
    private Boolean creatorIsActive;

    /**
     * 创建人名称
     */
    @ApiModelProperty("创建人名称")
    private String createUserName;

    /**
     * 表属性 0:普通表 1:高级表 2:加分表 3:权重表 4:扣分表 5:AI检查表 6:自定义表
     */
    @ApiModelProperty("表属性")
    private Integer tableProperty;

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
     * 置顶时间
     */
    private Date topTime;

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
    @ApiModelProperty("选取的结果查看人[{type:person,value:}{type:position,value:}]")
    private String resultViewPersonInfo;

    /**
     * 结果可见范围：self-仅自己，all-全部人员，part-部分人员
     */
    @ApiModelProperty("结果可见范围：self-仅自己，all-全部人员，part-部分人员")
    private String resultViewRange;

    /**
     * 是否可以编辑
     */
    private Boolean editFlag;

    @ApiModelProperty("共同编辑人[{type:person,value:}{type:position,value:}]")
    private String commonEditPersonInfo;

    /**
     * 是否开启AI检查
     */
    private Integer isAiCheck;


    private static final long serialVersionUID = 1L;

    public static TbMetaTableVO convertVO(TbMetaTableDO table) {
        TbMetaTableVO item = new TbMetaTableVO();
        item.setActive(table.getActive());
        item.setId(table.getId());
        item.setCreateTime(table.getCreateTime());
        item.setLocked(table.getLocked());
        item.setTableName(table.getTableName());
        item.setTableType(table.getTableType());
        item.setShareGroup(table.getShareGroup());
        item.setShareGroupName(table.getShareGroupName());
        item.setResultShareGroupName(table.getResultShareGroupName());
        item.setResultShareGroup(table.getResultShareGroup());
        item.setColumnCount(0);
        item.setColumnCategoryCount(Constants.INDEX_ZERO);
        item.setCreateUserId(table.getCreateUserId());
        item.setCreateUserName(table.getCreateUserName());
        item.setTableProperty(table.getTableProperty());
        item.setTotalScore(table.getTotalScore());
        item.setDefaultResultColumn(table.getDefaultResultColumn());
        item.setNoApplicableRule(table.getNoApplicableRule());
        item.setCategoryNameList(table.getCategoryNameList());
        item.setOrderNum(table.getOrderNum());
        item.setStatus(table.getStatus());
        item.setTopTime(table.getTopTime());
        item.setUsePersonInfo(table.getUsePersonInfo());
        item.setUseRange(table.getUseRange());
        item.setResultViewRange(table.getResultViewRange());
        item.setResultViewPersonInfo(table.getResultViewPersonInfo());
        item.setEditFlag(false);
        String updateUserName;
        String updateUserId;
        Date updateDateTime;
        if (StringUtils.isNotEmpty(table.getEditUserName())) {
            updateUserName = table.getEditUserName();
            updateDateTime = table.getEditTime();
            updateUserId = table.getEditUserId();
        } else {
            updateUserName = table.getCreateUserName();
            updateDateTime = table.getCreateTime();
            updateUserId = table.getCreateUserId();
        }
        item.setUpdateTime(updateDateTime);
        item.setUpdateUserName(updateUserName);
        item.setUpdateUserId(updateUserId);
        item.setCreateUserId(table.getCreateUserId());
        item.setCreateUserName(table.getCreateUserName());
        item.setIsAiCheck(table.getIsAiCheck());
        item.setCommonEditPersonInfo(table.getCommonEditPersonInfo());
        return item;
    }
}