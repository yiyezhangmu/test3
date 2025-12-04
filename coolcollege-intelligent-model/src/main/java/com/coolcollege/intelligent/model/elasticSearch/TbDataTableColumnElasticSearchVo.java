package com.coolcollege.intelligent.model.elasticSearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2021/8/16 9:54
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbDataTableColumnElasticSearchVo {

    /**
     * id
     */
    private String id ;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 子任务id
     */
    private Long subTaskId;

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 区域id
     */
    private Long regionId;

    /**
     * 区域路径
     */
    private String regionWay;

    /**
     * 区域路径
     */
    private String regionPath;

    /**
     * 记录id
     */
    private Long businessId;

    /**
     * 记录类型
     */
    private String businessType;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 数据表id
     */
    private Long dataTableId;

    /**
     * 检查表id
     */
    private Long metaTableId;

    /**
     * 属性 id
     */
    private Long metaColumnId;

    /**
     * 属性 名称
     */
    private String metaColumnName;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建人id
     */
    private String createUserId;

    /**
     * 处理人id
     */
    private String supervisorId;

    /**
     * 分类
     */
    private String categoryName;

    /**
     * 检查结果 '检查项结果:PASS,FAIL,INAPPLICABLE',
     */
    private String  check_result;

    /**
     * 检查项结果id
     */
    private Long check_result_id;

    /**
     * 检查结果名称
     */
    private String check_result_name;

    /**
     * 检查项上传的图片
     */
    private String check_pics;

    /**
     * 检查项的描述信息
     */
    private String check_text;

    /**
     * 检查项上传的视频
     */
    private String check_video;

    /**
     * 检查项分值
     */
    private String check_score;

    /**
     * 上报人id
     */
    private String handler_user_id;

    /**
     * 审核人id
     */
    private String check_user_id;

    /**
     * 复审人id
     */
    private String re_check_user_id;


    /**
     * 问题任务状态
     */
    private String  task_question_status;

    /**
     * 问题工单id
     */
    private Long task_question_id;

    /**
     * 检查项是否已经提交
     */
    private Integer submit_status;

    /**
     * 业务记录状态
     */
    private Integer business_status;

    /**
     * 删除标记
     */
    private Integer deleted;

    /**
     * 创建日期
     */
    private String create_date;

    /**
     * 罚款金额
     */
    private Double reward_penalt_money;

    /**
     * 值1
     */
    private String value1;

    /**
     * 值2
     */
    private String value2;

    /**
     * 数据资源类型 自定义 DEF  标准 STA
     */
    private String dataSourceType;

}
