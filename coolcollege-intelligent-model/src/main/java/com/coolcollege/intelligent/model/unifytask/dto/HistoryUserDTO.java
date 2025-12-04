package com.coolcollege.intelligent.model.unifytask.dto;

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
 * @date ：Created in 2020/11/27 18:05
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistoryUserDTO {
    private String nodeNo;
    private String userId;
    private String userName;
    /**
     * 头像
     */
    private String avatar;
    private String action;
}
