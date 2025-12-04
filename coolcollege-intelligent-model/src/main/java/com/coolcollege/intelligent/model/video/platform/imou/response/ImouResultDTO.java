package com.coolcollege.intelligent.model.video.platform.imou.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2022/04/25
 */
@NoArgsConstructor
@Data
public class ImouResultDTO<T> {

    private String msg;
    private String code;
    private T data;


}
