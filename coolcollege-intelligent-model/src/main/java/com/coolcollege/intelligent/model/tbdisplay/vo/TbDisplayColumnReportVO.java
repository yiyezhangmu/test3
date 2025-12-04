package com.coolcollege.intelligent.model.tbdisplay.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wxp
 * @date 2021-3-15 20:07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbDisplayColumnReportVO {

    /**
     * 门店区域
     */
    @Excel(name = "门店区域")
    private String storeAreaName;

    /**
     * 门店名称
     */
    @Excel(name = "门店名称")
    private String storeName;

    /**
     * 所属任务
     */
    @Excel(name = "所属任务")
    private String taskName;

    /**
     * 检查表名称
     */
    @Excel(name = "检查表名称")
    private String tableName;

    /**
     * 检查方式
     */
    @Excel(name = "检查方式")
    private String checkType;

    /**
     * 检查项
     */
    @Excel(name = "检查项")
    private String columnName;

    /**
     * 标准图
     */
    @Excel(name = "标准图")
    private String standardPic;

    /**
     * 标准图描述
     */
    @Excel(name = "标准图描述")
    private String description;


    @Excel(name = "处理图片")
    private String handleUrl;

    /**
     * 门店图片
     */
    @Excel(name = "门店图片")
    private String avatar;



    /**
     * 处理人
     */
    @Excel(name = "处理人")
    private String handleUserName;

    /**
     * 审批人
     */
    @Excel(name = "审批人")
    private String approveUserName;

    /**
     * 复审人
     */
    @Excel(name = "复审人")
    private String recheckUserName;

}
