package com.coolcollege.intelligent.model.store.dto;

import lombok.Data;

import java.util.List;

/**
 * @author 邵凌志
 * @date 2020/7/8 21:22
 */
@Data
public class UploadExceptionDTO extends Throwable {
    private String fileName;

    private Integer successCount;

    private Integer failCount;

    private List<String> failReason;
}
