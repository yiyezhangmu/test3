package com.coolcollege.intelligent.model.sms;

import com.coolcollege.intelligent.model.enums.SmsCodeTypeEnum;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: SendSmsCodeDTO
 * @Description:发送验证码
 * @date 2021-07-21 11:20
 */
@Data
public class SendSmsCodeDTO {

    private String mobile;

    private SmsCodeTypeEnum codeType;
}
