package com.coolcollege.intelligent.model.workHandover.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 
 * @author   zhangchenbiao
 * @date   2022-11-16 11:39
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkHandoverDTO implements Serializable {

    /**
     * 企业id
     */
    private String eid;


    /**
     * 工作交接id
     */
    private Long workHandoverId;

}