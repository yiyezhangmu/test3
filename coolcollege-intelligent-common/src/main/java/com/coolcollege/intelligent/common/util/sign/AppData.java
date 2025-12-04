package com.coolcollege.intelligent.common.util.sign;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author JiangDongZhao
 */
@Getter
@Setter
@EqualsAndHashCode
public class AppData {
    private String appId;
    private String appSecret;
}
