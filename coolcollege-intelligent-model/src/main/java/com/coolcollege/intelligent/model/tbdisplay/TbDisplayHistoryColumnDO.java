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
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TbDisplayHistoryColumnDO {
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
    private Boolean deleted;

    /**
     * 陈列记录id
     */
    private Long recordId;

    /**
     * 操作历史id
     */
    private Long historyId;

    /**
     * 模板检查项id
     */
    private Long metaColumnId;

    /**
     * 数据检查项id
     */
    private Long dataColumnId;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 操作人id
     */
    private String operateUserId;

    /**
     * 操作人姓名
     */
    private String operateUserName;

    /**
     * 图片
     */
    private String photo;

    /**
     * 图片数组,[{"handleUrl":"url1","finalUrl":"url2"},{"handleUrl":"url1","finalUrl":"url2"}]
     */
    private String photoArray;

    /**
     * 门店得分
     */
    private BigDecimal score;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否临时
     */
    private Integer isTemp;

    /**
     * 检查类型 0 检查项 1检查内容
     */
    private Integer checkType;
}