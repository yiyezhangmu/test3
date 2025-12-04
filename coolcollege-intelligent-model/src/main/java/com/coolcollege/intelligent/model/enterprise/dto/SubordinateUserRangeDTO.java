package com.coolcollege.intelligent.model.enterprise.dto;

import com.coolcollege.intelligent.model.region.dto.MySubordinatesDTO;
import lombok.Data;

import java.util.List;

/**
 * @ClassName SubordinateUserRangeDTO
 * @Description 用户管辖范围
 * @author wxp
 */
@Data
public class SubordinateUserRangeDTO {

    private String userId;

    /**
     * 管辖用户范围：self-仅自己，all-全部人员，define-自定义
     */
    private String subordinateUserRange;

    /**
     * auto自动关联 select手动选择
     */
    private List<String> sourceList;

    /**
     * 我的下属集合
     */
    private List<MySubordinatesDTO> mySubordinates;




}
