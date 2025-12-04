package com.coolcollege.intelligent.model.tbdisplay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
* @Description:
* @Author:
* @CreateDate: 2021-03-02 17:24:31
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbDisplayTableDataColumnDO {
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
     * 检查项id
     */
    private Long metaColumnId;


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

    private String approveRemark;

    private String taskName;

    /**
     * 检查项上传的视频
     */
    private String checkVideo;

    /**
     * 是否需要ai检查
     */
    private Integer isAiCheck;

    /**
     * ai分数
     */
    private BigDecimal aiScore;

}