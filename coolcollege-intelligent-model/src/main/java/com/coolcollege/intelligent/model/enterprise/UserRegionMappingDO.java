package com.coolcollege.intelligent.model.enterprise;

import com.coolcollege.intelligent.model.enums.UserAuthMappingSourceEnum;
import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/2/24 15:37
 * @Version 1.0
 */
@Data
public class UserRegionMappingDO {

    /**
     * id
     */
    private Integer id;
    /**
     * 映射主键 区域id
     */
    private String regionId;
    /**
     *  用户id
     */
    private String userId;
    /**
     * 创建人id
     */
    private String createId;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新人id
     */
    private String updateId;
    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 来源：create-门店创建  sync-同步
     */
    private String source;


    public static List<UserRegionMappingDO> convertList(String userId, List<String> regionIds, String operator){
        if(CollectionUtils.isEmpty(regionIds)){
            return Lists.newArrayList();
        }
        List<UserRegionMappingDO> userRegionList = new ArrayList<>();
        for (String regionId : regionIds) {
            UserRegionMappingDO result = new UserRegionMappingDO();
            result.setUserId(userId);
            result.setRegionId(regionId);
            result.setCreateId(operator);
            result.setUpdateId(operator);
            result.setCreateTime(System.currentTimeMillis());
            result.setUpdateTime(System.currentTimeMillis());
            result.setSource(UserAuthMappingSourceEnum.CREATE.getCode());
            userRegionList.add(result);
        }
        return userRegionList;
    }

    public static UserRegionMappingDO convertDO(String userId, String regionId, String operator){
        UserRegionMappingDO result = new UserRegionMappingDO();
        result.setUserId(userId);
        result.setRegionId(regionId);
        result.setCreateId(operator);
        result.setUpdateId(operator);
        result.setCreateTime(System.currentTimeMillis());
        result.setUpdateTime(System.currentTimeMillis());
        result.setSource(UserAuthMappingSourceEnum.CREATE.getCode());
        return result;
    }

}
