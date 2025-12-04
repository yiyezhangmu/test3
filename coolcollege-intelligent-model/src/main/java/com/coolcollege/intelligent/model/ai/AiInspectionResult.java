package com.coolcollege.intelligent.model.ai;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author byd
 * @date 2025-10-27 14:39
 */
@AllArgsConstructor
@Data
public class AiInspectionResult {

    /**
     * 检测结果（合格/不合格）
     */
    private String result;

    /**
     * 不合格说明（当result为"不合格"时有效）
     */
    private String message;

    /**
     * AI检测内容
     */
    private String aiContent;
}
