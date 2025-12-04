package com.coolcollege.intelligent.service.syslog.impl;

import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author: hu hu
 * @Date: 2025/1/22 14:06
 * @Description:
 */
@Service
@Slf4j
public class SwPictureCenterResolve extends AbstractOpContentResolve{

    @Override
    public OpModuleEnum getOpModule() {
        return OpModuleEnum.SW_PICTURE_CENTER;
    }
}
