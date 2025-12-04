package com.coolcollege.intelligent.model.store.dto;

import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * @ClassName StoreSupervisorMappingDTO
 * @Description 用一句话描述什么
 */
@Data
public class StoreSupervisorMappingDTO {
    /**
     * 流水主键
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
     * 电话
     */
    private String mobile;

    /**
     * 锁定定位:operator/运营,shopowner/店长,clerk/店员(positionId)
     */
    private String type;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 门店名称
     */
    private String storeName;
    /**
     * 岗位id集合
     */
    private List<String> pisitionIds;
    /**
     * 创建时间
     */
    private Long createTime;

    private String  source;

    private Integer isValid;

    private String storeArea;

    private String isDelete;

    @Override
    public boolean equals(Object obj){
        if(this==obj){
            return true;
        }
        if(obj == null || this.getClass()!=obj.getClass()){
            return false;
        }
       StoreSupervisorMappingDTO that= (StoreSupervisorMappingDTO)obj;
        return Objects.equals(storeId,that.getStoreId())&&Objects.equals
                (userId,that.getUserId())&& Objects.equals(type,that.getType());
    }
    @Override
    public int hashCode(){
        return super.hashCode();
    }

}
