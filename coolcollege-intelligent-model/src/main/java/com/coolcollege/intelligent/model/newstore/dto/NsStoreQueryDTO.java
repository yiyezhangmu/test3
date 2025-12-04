package com.coolcollege.intelligent.model.newstore.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author wxp
 * @description: 新店DTO
 * @date 2022/3/8 13:52
 */
@Data
public class NsStoreQueryDTO {

    /**
     * 开始创建时间
     */
    private Date createTimeStart;
    /**
     * 结束创建时间
     */
    private Date createTimeEnd;
    /**
     * 区域id
     */
    private Long regionId;
    /**
     * 门店名称
     */
    private String name;
    /**
     * 门店类型
     */
    private List<String> typeList;
    /**
     * 新店状态：ongoing(进行中),completed(完成),failed(失败)
     */
    private List<String> statusList;
    /**
     * 负责人
     */
    private String directUserId;
    /**
     * 创建人
     */
    private String createUserId;
    /**
     * 经度
     */
    private String longitude;
    /**
     * 纬度
     */
    private String latitude;

}
