package com.coolcollege.intelligent.model.tbdisplay.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
    wxp
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TbDisplayHistoryUserVO {
    private String nodeNo;
    private String userId;
    private String userName;
    /**
     * 头像
     */
    private String avatar;
    private String action;
}
