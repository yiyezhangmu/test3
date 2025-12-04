package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/11/20 16:58
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonBasicDTO {
    /**
     * 用户id
     */
    private String userId;
    /**
     * 用户名字
     */
    private String userName;
    /**
     * 头像
     */
    private String avatar;
}
