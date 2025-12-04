package com.coolcollege.intelligent.model.patrolstore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 
 * @author   zhangchenbiao
 * @date   2023-07-11 01:57
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbPatrolStoreCountDTO implements Serializable {

    /**
     * 门店数量
     */
    private Long storeNum;

    /**
     * 巡店次数
     */
    private Long patrolStoreNum;


    private String storeId;


    private String supervisorId;
}