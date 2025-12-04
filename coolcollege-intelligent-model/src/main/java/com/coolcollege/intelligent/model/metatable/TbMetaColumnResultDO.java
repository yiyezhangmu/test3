package com.coolcollege.intelligent.model.metatable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author yezhe
 * @date 2021-01-22 14:09
 */

/**
 * 标准检查项评价项配置表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TbMetaColumnResultDO implements Serializable {
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
     * 检查表ID
     */
    private Long metaTableId;

    /**
     * 检查项ID
     */
    private Long metaColumnId;

    /**
     * 创建者
     */
    private String createUserId;

    /**
     * 自定义名称
     */
    private String resultName;

    /**
     * 默认分值
     */
    private BigDecimal score;

    /**
     * 默认金额
     */
    private BigDecimal money;

    /**
     * 映射/关联结果
     */
    private String mappingResult;

    /**
     * 强制拍照,0不强制1强制
     */
    private Integer mustPic;

    /**
     * 排序
     */
    private Integer orderNum;

    /**
     * 是否删除:0:未删除，1.删除
     */
    private Integer deleted;

    /**
     * 描述信息,force：强制, ignore: 不强制
     */
    private String description;

    /**
     * 最高分
     */
    private BigDecimal maxScore;

    /**
     * 最低分
     */
    private BigDecimal minScore;

    /**
     * 分值加倍 0:不加倍，1:加倍
     */
    private Integer scoreIsDouble;
    /**
     * 奖罚加倍 0:不加倍，1:加倍
     */
    private Integer awardIsDouble;

    /**
     * 扩展信息
     */
    private String extendInfo;

    private static final long serialVersionUID = 1L;

}