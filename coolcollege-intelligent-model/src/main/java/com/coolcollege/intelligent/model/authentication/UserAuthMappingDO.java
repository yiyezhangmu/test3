package com.coolcollege.intelligent.model.authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * describe:人员权限映射表
 *
 * @author zhouyiping
 * @date 2020/10/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAuthMappingDO {
    private Long id;
    private String userId;
    private String mappingId;
    private String type;
    private String source;
    private String createId;
    private Long createTime;
    private String updateId;
    private Long updateTime;

    public UserAuthMappingDO(String userId, String mappingId, String type, String source, String createId, Long createTime) {
        this.userId = userId;
        this.mappingId = mappingId;
        this.type = type;
        this.source = source;
        this.createId = createId;
        this.createTime = createTime;
    }

}
