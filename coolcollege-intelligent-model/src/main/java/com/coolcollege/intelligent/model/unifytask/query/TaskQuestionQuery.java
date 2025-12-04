package com.coolcollege.intelligent.model.unifytask.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * @author byd
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskQuestionQuery {


    /**
     * 门店id
     */
    private List<String> userIdList;

    /**
     *
     */
    private Integer pageNumber = 1;
    /**
     *
     */
    private Integer pageSize = 20;

    private Long beginTime;

    private Long endTime;

    private String dbName;
}
