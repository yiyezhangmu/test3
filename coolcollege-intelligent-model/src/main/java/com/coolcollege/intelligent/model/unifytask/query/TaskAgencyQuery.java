package com.coolcollege.intelligent.model.unifytask.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/12/10 21:18
 */
@Data
public class TaskAgencyQuery {

    /**
     * 查询类型
     * 页面分类
     * @see com.coolcollege.intelligent.model.enums.TaskQueryEnum
     */
    @NotNull(message = "查询类型不能为空")
    private String queryType;
    /**
     * 任务状态
     */
    private String status;
    /**
     * 任务状态
     */
    private String taskType;
    /**
     * 时间
     */
    private Long createBeginDate;
    /**
     * 时间
     */
    private Long createEndDate;
    /**
     *
     */
    private Integer pageNumber = 1;
    /**
     *
     */
    private Integer pageSize = 20;
    /**
     * 门店id
     */
    private String storeId;
    /**
     * 门店id
     */
    private List<String> storeIdList;

    // 我的-企业员工待办
    private String userId;

    private List<String> taskTypes;

}
