package com.coolcollege.intelligent.model.inspection.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author byd
 * @date 2025-10-16 17:17
 */
@Data
public class AiInspectionPhotoVO {

    private Date captureDate;
    private Date weekDay;
    private String storeId;
    private String picture;

}
