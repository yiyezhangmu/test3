package com.coolcollege.intelligent.model.video;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 文件DTO
 * </p>
 *
 * @author wangff
 * @since 2025/7/2
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskFileDTO {
    /**
     * 抓图时间，yyyy-MM-dd HH:mm:ss
     */
    private String timePoint;

    /**
     * 文件地址
     */
    private String url;
}
