package com.coolcollege.intelligent.model.patrolstore.vo;

import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author suzhuhong
 * @Date 2021/11/17 19:24
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordByCheckColumnIdVO {

    private Integer patrolNum;

    private Integer passNum;

    private Integer failNum;

    private Integer inapplicableNum;

    PageInfo pageInfo;


}
