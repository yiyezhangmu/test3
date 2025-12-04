package com.coolcollege.intelligent.model.patrolstore.statistics;

import lombok.Data;

import java.util.Date;

/**
 * @author shuchang.wei
 * @date 2021/7/8 14:51
 */
@Data
public class ColumnQuestionTrendDTO {
    /**
     * 时间
     */
    private Date date;

    /**
     * 工单数
     */
    private int questionNum;

    public ColumnQuestionTrendDTO(){

    }

    public ColumnQuestionTrendDTO(Date date){
        this.date = date;
    }
}
