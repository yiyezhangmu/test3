package com.coolcollege.intelligent.model.patrolstore.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangchenbiao
 * @FileName: SendWXGroupMessageDTO
 * @Description:
 * @date 2024-09-12 16:11
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WXGroupMessageDTO {

    public static final String MARKDOWN_CONTENT = "**日期**: {0} {1}\n" +
            "**授权号**: {2}\n" +
            "**门店名称**: {3}\n" +
            "**到店时间**: {4}\n" +
            "**离店时间**: {5}\n"+
            "**巡店总结**: {6}";

    private String msgtype;

    private JSONObject markdown;

    private JSONObject image;
}
