package com.coolcollege.intelligent.model.store;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Objects;

/**
 * @ClassName StoreSupervisorMappingDO
 * @Description 用一句话描述什么
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreSupervisorMappingDO {
    /**
     * 自增ID
     */
    private Long id;

    /**
     * 门店ID
     */
    private String storeId;

    /**
     * 人员ID
     */
    private String userId;

    /**
     * 人员姓名
     */
    private String userName;

    /**
     * 人员类型:operator/运营,shopowner/店长,clerk/店员
     */
    private String type;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 创建人
     */
    private String createName;
    /**
     * 来源
     */
    private String source;
    /**
     * 是否可用
     */
    private Boolean  isValid;

    public StoreSupervisorMappingDO(String storeId, String userId, String userName, String type, Long createTime, String source, Boolean isValid) {
        this.storeId = storeId;
        this.userId = userId;
        this.userName = userName;
        this.type = type;
        this.createTime = createTime;
        this.source = source;
        this.isValid = isValid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoreSupervisorMappingDO that = (StoreSupervisorMappingDO) o;
        return storeId.equals(that.storeId) &&
                userId.equals(that.userId) &&
                type.equals(that.type) &&
                isValid.equals(that.isValid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storeId, userId, type, isValid);
    }
}
