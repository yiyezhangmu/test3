package com.coolcollege.intelligent.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <p>
 * AI评语风格
 * </p>
 *
 * @author wangff
 * @since 2025/6/11
 */
@Getter
@RequiredArgsConstructor
public enum AICommentStyleEnum {

    DETAIL("detail", "详细"),

    NORMAL("normal", "正常"),

    BRIEF("brief", "简略"),
    ;
    private final String style;

    private final String msg;
}
