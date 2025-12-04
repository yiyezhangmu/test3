package com.coolcollege.intelligent.model.region.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @author byd
 * @ClassName RegionDO
 * @Description 区域
 */
@ApiModel("区域全路径返回实体")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionPathNameVO implements Serializable {
    /**
     * 区域全路径名称
     */
    @ApiModelProperty("区域全路径名称")
    private String allRegionName;

    private List<String> regionNameList;

    private static final long serialVersionUID = 1L;

}
