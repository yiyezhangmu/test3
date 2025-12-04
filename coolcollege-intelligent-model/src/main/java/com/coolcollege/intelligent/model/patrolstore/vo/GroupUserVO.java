package com.coolcollege.intelligent.model.patrolstore.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: huhu
 * @Date: 2024/9/11 16:17
 * @Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupUserVO {

    private String userId;

    private String userName;
}
