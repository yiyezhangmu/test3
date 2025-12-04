package com.coolcollege.intelligent.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 数据同步
 *
 * @author wxp
 * @since 2021/9/8
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SyncMyjUserRequest {

    /**
     * 企业id
     */
    private String enterpriseId;

    /**
     * 用户userId
     */
    private String userId;


    /**
     * 门店区域id可别
     */
    private List<String> regionList;


    /**
     * 职位
     */
    private String position;
}
