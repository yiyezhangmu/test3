package com.coolcollege.intelligent.model.patrolstore.param;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 *
 * @author : yezhe
 * @date ：2021/01/06 16:18
 */
@Data
public class StoreTaskMapParam {

    @NotEmpty(message = "用户ids不能为空")
    private List<String> userIds;
    /**
     * 查询类型 other：通过人找本人关联门店再找巡店人 null或者其他：user数组里人的巡店其他
     */
    private String type;

    @NotNull(message = "开始时间不能为空")
    private Date startTime;

    @NotNull(message = "结束时间不能为空")
    private Date endTime;

    public static final String OTHER = "other";
}
