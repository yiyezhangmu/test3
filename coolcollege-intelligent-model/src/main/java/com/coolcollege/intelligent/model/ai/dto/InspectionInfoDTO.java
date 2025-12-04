package com.coolcollege.intelligent.model.ai.dto;

import lombok.Data;

import java.util.List;

/**
 *
 * @author byd
 * @date 2025-10-28 14:21
 */
@Data
public class InspectionInfoDTO {

    /**
     * 图片列表（可能存储图片URL或路径）
     */
    private List<String> imageList;


    /**
     * 业务ID（如工单ID、合同ID等）
     */
    private String businessId;

    /**
     * 标准图
     */
    private String standardPic;


    /**
     * ai模型编码
     */
    private String modelCode;
}
