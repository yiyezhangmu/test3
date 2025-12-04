package com.coolcollege.intelligent.facade.dto.openApi.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2022/7/12 14:03
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisplayTableDataContentVO {
    /**
     * 主键id自增
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 删除标识
     */
    private Integer deleted;

    /**
     * 陈列记录id
     */
    private Long recordId;

    /**
     * 父任务id
     */
    private Long unifyTaskId;

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 循环轮次
     */
    private Long loopCount;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 区域ID
     */
    private Long regionId;

    /**
     * 区域路径
     */
    private String regionPath;

    /**
     * 检查表id
     */
    private Long metaTableId;

    /**
     * 检查内容id
     */
    private Long metaContentId;

    /**
     * 得分
     */
    private BigDecimal score;

    /**
     * 图片数组,[{"handleUrl":"url1","finalUrl":"url2"},{"handleUrl":"url1","finalUrl":"url2"}]
     */
    private String photoArray;

    private Long dataTableId;

    private String remark;

    private String taskName;

    private String columnName;
    private String standardPic;
    private String description;
    /**
     * 排序
     */
    private Integer orderNum;

    /**
     * meta信息的score
     */
    private BigDecimal metaScore;
}
