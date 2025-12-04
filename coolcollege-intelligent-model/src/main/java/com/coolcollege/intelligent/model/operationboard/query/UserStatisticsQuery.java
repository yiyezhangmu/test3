package com.coolcollege.intelligent.model.operationboard.query;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.ToString;

/**
 * @author shuchang.wei
 * @date 2021/1/8 11:48
 */
@Data
@ToString
public class UserStatisticsQuery extends BaseQuery {
    /**
     * 职位id
     */
    private List<Long> roleIdList = new ArrayList<>();
    /**
     * 执行人id
     */
    private List<String> userIdList = new ArrayList<>();
}
