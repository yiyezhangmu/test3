package com.coolcollege.intelligent.model.enterprise;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.enterprise.SubordinateSourceEnum;
import lombok.Data;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2022/2/24 15:45
 * @Version 1.0
 */
@Data
public class SubordinateMappingDO {
    /**
     * id
     */
    private Integer id;
    /**
     *  用户id
     */
    private String userId;
    /**
     * 映射主键 区域id
     */
    private String regionId;
    /**
     * 人员id
     */
    private String personalId;
    /**
     * 类型 0 下属 ， 1 直属上级
     */
    private Integer type;
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
     * 管辖用户范围：self-仅自己，all-全部人员，define-自定义
     */
    private String userRange;

    /**
     * auto自动关联 select手动选择
     */
    private String source;


    public static SubordinateMappingDO convertDirect(String userId, String personalId, String operator){
        SubordinateMappingDO subordinateMapping = new SubordinateMappingDO();
        subordinateMapping.setUserId(userId);
        subordinateMapping.setPersonalId(personalId);
        subordinateMapping.setType(Constants.INDEX_ONE);
        subordinateMapping.setCreateId(operator);
        subordinateMapping.setCreateTime(System.currentTimeMillis());
        subordinateMapping.setUpdateTime(System.currentTimeMillis());
        subordinateMapping.setUpdateId(operator);
        subordinateMapping.setSource(SubordinateSourceEnum.SELECT.getCode());
        return subordinateMapping;
    }
}
