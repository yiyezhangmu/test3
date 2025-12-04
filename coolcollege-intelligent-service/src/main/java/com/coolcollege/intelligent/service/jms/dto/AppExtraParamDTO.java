package com.coolcollege.intelligent.service.jms.dto;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/03/01
 */
@Data
public class AppExtraParamDTO {
    private String messageId;
    private String messageUrl;
    //1不跳转  2 跳转Url
    private Integer messageType;
}
