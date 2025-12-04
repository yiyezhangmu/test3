package com.coolcollege.intelligent.model.tbdisplay.vo;

import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableDataContentDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2021/9/27 10:57
 * @Version 1.0
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TbDisplayTableDataContentVO  extends TbDisplayTableDataContentDO {
    private String columnName;
    private String standardPic;
    private String description;
    /**
     * 排序
     */
    private Integer orderNum;

    /**
     * meta信息的score
     */
    private BigDecimal metaScore;


    @ApiModelProperty("审核记录列表")
    private List<TbDisplayHistoryColumnVO> historyColumnVOList;


    /**
     * 检查图片,0不强制1强制
     */
    private Integer mustPic;
}
