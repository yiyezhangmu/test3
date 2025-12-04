package com.coolcollege.intelligent.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author CFJ
 * @version 1.0
 * @date 2023/7/23 16:26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserJurisdictionSubordinateDO implements Serializable {
    /**
     *
     */
    private Long id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 下属id
     */
    private String underlingId;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}
