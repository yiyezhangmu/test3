package com.coolcollege.intelligent.model.store.dto;

import lombok.Data;

import java.util.List;

/**
 * @ClassName UploadFileMessageDTO
 * @Description 用一句话描述什么
 */
@Data
public class UploadFileMessageDTO {
    private String fileName;

    private Integer successCount;

    private Integer failCount;

    private List<String> failReason;
}
