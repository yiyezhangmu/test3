package com.coolcollege.intelligent.service.requestBody.store;

import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreStatisticsBaseQuery;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @Description: 门店覆盖
 * @Author chenyupeng
 * @Date 2021/7/13
 * @Version 1.0
 */
@Data
public class StoreCoverRequestBody {
    /**
     * 区域id列表
     */
    List<String> regionIds;

    /**
     * 门店id列表
     */
    List<String> storeIds;

    /**
     * 检查表id
     */
    private Long metaTableId;

    private Integer pageSize;

    private Integer pageNum;

    /**
     * 时间范围起始值
     */
    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date beginDate;
    /**
     * 时间范围截至值
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "结束时间不能为空")
    private Date endDate;

    /**
     * 是否只获取直连门店数据
     */
    private Boolean getDirectStore = false;
    /**
     * 查询类型 check 已检查  unCheck 未检查
     */
    private String  queryType;
}
